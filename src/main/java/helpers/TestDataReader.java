package helpers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TestDataReader 
{
	private ArrayList<List<String>> testData;
	private String sheetName;
	private Config testConfig;

	public TestDataReader(Config testConfig, String sheetName, String path) 
	{
		this.testConfig = testConfig;
		this.sheetName = sheetName;
		readFile(sheetName, path);
	}
	
	/**
	 * This function is used to read the excel sheets of type .xls, .xlsx and .csv
	 * @param sheetName
	 * @param pathOfExcelFile
	 */
	private void readFile(String sheetName, String pathOfExcelFile)
	{
		String filename = pathOfExcelFile.trim();
		BufferedReader csvFile = null;
		FileInputStream fileInputStream = null;
		testData = new ArrayList<List<String>>();
		testConfig.logComment("Read:-'" + pathOfExcelFile + "', Sheet:- '" + sheetName + "'");
		
		try
		{
			fileInputStream = new FileInputStream(filename);
			if (filename.endsWith(".xls"))
			{
				HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
				HSSFSheet sheet = workbook.getSheet(sheetName);
				FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
				
				if (sheet == null)
					testConfig.logFail("No sheetName:- " + sheetName + " found.");
				
				Iterator<Row> rows = sheet.rowIterator();
				while (rows.hasNext())
				{
					HSSFRow row = (HSSFRow) rows.next();
					List<String> data = new ArrayList<String>();
					for (int z = 0; z < row.getLastCellNum(); z++)
					{
						String str = convertCellValueToString(row.getCell(z), evaluator);
						data.add(str);
					}
					testData.add(data);
				}
			}
			else if (filename.endsWith(".xlsx"))
			{
				XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
				XSSFSheet sheet = workbook.getSheet(sheetName);
				FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
				
				if (sheet == null)
					testConfig.logFail("No sheetName:- " + sheetName + " found.");
				
				Iterator<Row> rows = sheet.rowIterator();
				while (rows.hasNext())
				{
					XSSFRow row = (XSSFRow) rows.next();
					List<String> data = new ArrayList<String>();
					for (int z = 0; z < row.getLastCellNum(); z++)
					{
						String str = convertCellValueToString(row.getCell(z), evaluator);
						data.add(str);
					}
					testData.add(data);
				}
			}
			
			else if (filename.endsWith(".csv"))
			{
				csvFile = new BufferedReader(new FileReader(pathOfExcelFile));
				String dataRow = csvFile.readLine();

				while (dataRow != null)
				{
					String[] dataArray = dataRow.split(",");
					List<String> data = new ArrayList<String>();
					for (int z = 0; z < dataArray.length; z++)
					{
						String str = dataArray[z];
						data.add(str);
					}
					testData.add(data);
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (fileInputStream != null)
			{
				try
				{
					fileInputStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if(csvFile != null)
			{
				try
				{
					csvFile.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * This function is used to convert value of cell into string
	 * @param cell
	 * @param evaluator
	 * @return
	 */
	private String convertCellValueToString(Cell cell, FormulaEvaluator evaluator) 
	{
		String value = null;
		switch(cell.getCellType())
		{
		case Cell.CELL_TYPE_NUMERIC:
			value = Double.toString(cell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getRichStringCellValue().toString();
			break;
		case Cell.CELL_TYPE_FORMULA:
			HSSFDataFormatter formatter = new HSSFDataFormatter();
			value = formatter.formatCellValue(cell, evaluator); 
			break;
		case Cell.CELL_TYPE_ERROR:
			value = "";
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = Boolean.toString(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			value = "";
			break;
		}
		return value;
	}
	
	
	/**
	 * This function is used to fetch the data of a particular 'cell' of excel sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public String getData(int row, String column)
	{
		String data = "";
		List<String> headerRow = testData.get(0);
		List<String> dataRow = testData.get(row);
		
		for (int i = 0; i < headerRow.size(); i++)
		{
			if (headerRow.get(i).equalsIgnoreCase(column))
			{
				try
				{
					data = dataRow.get(i);
				}
				catch (IndexOutOfBoundsException e)
				{
					data = "";
				}
				break;
			}
		}
		
		if (data.equals(""))
		{
			data = "{skip}";
		}
		else
		{
			if (data.contains("{empty}"))
				data = data.replace("{empty}", "");
			if (data.contains("{space}"))
				data = data.replace("{space}", " ");
			
			while (data.contains("{random"))
			{
				int start = data.indexOf("Num:") + 4;
				int end = data.indexOf("}");
				int length = Integer.parseInt(data.substring(start, end));
				
				if (data.contains("{randomAlphaNum:" + length + "}"))
					data = data.replace("{randomAlphaNum:" + length + "}", Helper.generateRandomAlphaNumericString(length));
				if (data.contains("{randomAlphabetsNum:" + length + "}"))
					data = data.replace("{randomAlphabetsNum:" + length + "}", Helper.generateRandomString(length));
				if (data.contains("{randomNum:" + length + "}"))
					data = data.replace("{randomNum:" + length + "}", Long.toString(Helper.generateRandomNumber(length)));
			}
		}
		testConfig.logComment("Value of column '" + column + "' for row-" + row + ", of sheet '" + sheetName + "' is:- '" + data + "'");
		return data;
	}
}