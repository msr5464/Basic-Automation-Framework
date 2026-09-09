package TestAutomation.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Failure evidence for the QA-Agent-Network agents.
 *
 * <p>The agents diagnose a failed test from artifacts rather than by replaying
 * the journey — signing in again and reconstructing the test data is slow and
 * frequently impossible. Playwright records these natively; Selenium records
 * neither, so this class produces them. Without it the agents fall back to
 * guessing from an exception message, which is exactly the guessing they exist
 * to replace.
 *
 * <p>Two artifacts, written under {@code Config.resultsDirectory}:
 *
 * <ul>
 *   <li>{@code dom/<method>_<timestamp>.html} — the page as it actually was at
 *       the moment of failure, carrying the header the agents parse for the URL
 *       and capture time.</li>
 *   <li>{@code telemetry/<method>_<timestamp>.jsonl} — one JSON object per
 *       action, so the agent can see which selectors worked before the one that
 *       did not. This is the Selenium equivalent of a Playwright trace.</li>
 * </ul>
 *
 * <p>Every method here swallows its own failures. Telemetry that breaks a test
 * run is worse than no telemetry: these run inside a listener that is already
 * handling a failure, and a second exception thrown there would mask the first.
 */
public final class AgentTelemetry {

    /** Matches the header shared/dom_snapshot.py parses. Keep the spelling exact. */
    private static final String HEADER =
            "<!-- qa-agent-network:dom-snapshot test=\"%s\" url=\"%s\" capturedAt=\"%s\" -->%n";

    private static final SimpleDateFormat FILE_STAMP = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat ISO_STAMP = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private AgentTelemetry() {
    }

    private static Path resultsDir() {
        String configured = Config.resultsDirectory;
        if (configured == null || configured.trim().isEmpty()) {
            configured = System.getProperty("user.dir") + File.separator + "test-output";
        }
        return Paths.get(configured);
    }

    /**
     * Write the failure-time DOM. Called from TestListener.onTestFailure.
     *
     * <p>outerHTML rather than driver.getPageSource(): getPageSource returns the
     * document as originally served, so on any single-page app it shows markup
     * that has not existed since load — which is precisely the wrong evidence
     * for diagnosing a locator that stopped matching.
     */
    public static void captureDomSnapshot(Config testConfig, String methodName) {
        WebDriver driver = testConfig == null ? null : testConfig.driver;
        if (driver == null || methodName == null || methodName.isEmpty()) {
            return;
        }
        try {
            String html = String.valueOf(((JavascriptExecutor) driver)
                    .executeScript("return document.documentElement.outerHTML;"));
            if (html == null || html.isEmpty() || "null".equals(html)) {
                return;
            }
            String url = safeUrl(driver);
            Date now = new Date();
            Path target = resultsDir().resolve("dom")
                    .resolve(methodName + "_" + FILE_STAMP.format(now) + ".html");
            Files.createDirectories(target.getParent());
            String content = String.format(HEADER, escape(methodName), escape(url),
                    ISO_STAMP.format(now)) + html;
            Files.write(target, content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("[agent-telemetry] DOM snapshot skipped: " + e.getMessage());
        }
    }

    /**
     * Append one action to this test's timeline.
     *
     * <p>Call from wherever the framework already funnels its interactions —
     * {@code Element.click}, {@code Element.sendKeys}, {@code Browser.goToUrl}
     * and friends — or from a WebDriverListener wrapped around the driver via
     * {@code new EventFiringDecorator<>(listener).decorate(driver)}.
     *
     * <p>Field names match what SeleniumTelemetryParser reads. Passing null for
     * {@code error} records a successful action: the actions that SUCCEEDED are
     * as diagnostic as the one that failed, because they establish how far the
     * journey actually got.
     */
    public static void recordAction(Config testConfig, String methodName, String action,
                                    String selector, String value, Throwable error) {
        if (methodName == null || methodName.isEmpty()) {
            return;
        }
        try {
            Path target = resultsDir().resolve("telemetry")
                    .resolve(methodName + "_" + stampFor(testConfig, methodName) + ".jsonl");
            Files.createDirectories(target.getParent());

            StringBuilder line = new StringBuilder(256);
            line.append("{\"action\":\"").append(escapeJson(action)).append('"');
            if (selector != null && !selector.isEmpty()) {
                line.append(",\"selector\":\"").append(escapeJson(selector)).append('"');
            }
            if (value != null && !value.isEmpty()) {
                // Truncated, and never a credential: this file is read by an LLM.
                String trimmed = value.length() > 60 ? value.substring(0, 60) : value;
                line.append(",\"value\":\"").append(escapeJson(trimmed)).append('"');
            }
            String url = testConfig == null ? null : safeUrl(testConfig.driver);
            if (url != null && !url.isEmpty()) {
                line.append(",\"url\":\"").append(escapeJson(url)).append('"');
            }
            if (error != null) {
                String message = error.getClass().getSimpleName() + ": "
                        + String.valueOf(error.getMessage());
                line.append(",\"error\":\"").append(escapeJson(firstLine(message))).append('"');
            }
            line.append("}").append(System.lineSeparator());

            Files.write(target, line.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException | RuntimeException e) {
            System.out.println("[agent-telemetry] action not recorded: " + e.getMessage());
        }
    }

    /**
     * Per-thread fallback for the stamp, used when no Config is available.
     * TestNG runs tests in parallel threads, so this must not be a plain static
     * or two concurrent tests would interleave into one file.
     */
    private static final ThreadLocal<java.util.Map<String, String>> THREAD_STAMPS =
            ThreadLocal.withInitial(java.util.HashMap::new);

    /**
     * One timestamp per test method, so every action in a test appends to the
     * same file. A fresh stamp per call would scatter a single timeline across
     * hundreds of one-line files and destroy the ordering that makes it useful
     * — and the ordering is the entire value, since what the agent needs is
     * which selectors worked before the one that did not.
     *
     * <p>The Config-backed path is preferred because it survives anything that
     * hands work to another thread; the thread-local is the fallback for calls
     * made without a Config, which would otherwise hit exactly the scattering
     * this method exists to prevent.
     */
    private static String stampFor(Config testConfig, String methodName) {
        String key = "AgentTelemetryStamp_" + methodName;
        if (testConfig != null) {
            try {
                String existing = testConfig.getRunTimeProperty(key);
                if (existing != null && !existing.isEmpty()) {
                    return existing;
                }
                String stamp = FILE_STAMP.format(new Date());
                testConfig.putRunTimeProperty(key, stamp);
                return stamp;
            } catch (RuntimeException e) {
                // Config not fully initialised — fall through to the thread-local.
            }
        }
        return THREAD_STAMPS.get().computeIfAbsent(
                methodName, unused -> FILE_STAMP.format(new Date()));
    }

    private static String safeUrl(WebDriver driver) {
        try {
            return driver == null ? "" : String.valueOf(driver.getCurrentUrl());
        } catch (Exception e) {
            return "";     // the session may already be gone by failure time
        }
    }

    private static String firstLine(String text) {
        if (text == null) {
            return "";
        }
        int newline = text.indexOf('\n');
        return newline < 0 ? text : text.substring(0, newline);
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("\"", "&quot;").replace("--", "&#45;&#45;");
    }

    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(text.length() + 16);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':  out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n");  break;
                case '\r': out.append("\\r");  break;
                case '\t': out.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }
}
