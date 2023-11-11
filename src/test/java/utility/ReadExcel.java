package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import report.JIRAReport;
import stepDefinitions.BasicDefinition;

public class ReadExcel {

	public IPPMethods ippMethods = new IPPMethods(); 
	public FileInputStream fis;
	public FileOutputStream fos;
	public XSSFWorkbook wb;
	public static File excel;
	public XSSFCell cell;
	public XSSFRow row;
	public static ArrayList<String> colHeader;
	public static ArrayList<String> rowHeader;
	public static Logger logger = LogManager.getLogger(ReadExcel.class);
		
	// For Reading multi sheets in Excel

	/**
	 * Method to Read the Entire Workbook and store it in Map with respect to Work Sheets
	 * 
	 * @param sheetname - Name of the Excel Worksheet
	 * @param wb - Workbook
	 * @return
	 * @throws IOException
	 */
@SuppressWarnings("deprecation")
	public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> readsheet(XSSFWorkbook wb) throws IOException{
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> sheets = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>>();
		LinkedHashMap<String, LinkedHashMap<String, String>> rows = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> cols;
		int sheetCount = wb.getNumberOfSheets();
		for(int s=0;s<sheetCount;s++) {
//			System.out.println(wb.getSheetName(s));
		XSSFSheet sh = wb.getSheetAt(s);
		int rowCount = sh.getLastRowNum();
		int colCount = sh.getRow(0).getLastCellNum();
		XSSFRow excelHeader = sh.getRow(0);
		Cell currentcell = sh.getRow(0).getCell(0);
		
		for(int i=1;i<=rowCount;i++) {
			cols = new LinkedHashMap<String, String>();
//			System.out.println("row = "+ i);
			for(int j=1; j<colCount;j++) {
//				System.out.println("column = "+ j);	
				if(sh.getRow(i).getCell(j)!=null) {
					currentcell =sh.getRow(i).getCell(j); 
				
				switch(currentcell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					if(!sh.getRow(i).getCell(j).getStringCellValue().equals("")){
					cols.put(excelHeader.getCell(j).getStringCellValue(), sh.getRow(i).getCell(j).getStringCellValue());
					}
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if(!String.valueOf(sh.getRow(i).getCell(j).getNumericCellValue()).equals("")){
					cols.put(excelHeader.getCell(j).getStringCellValue(), String.valueOf(sh.getRow(i).getCell(j).getNumericCellValue()));
					}
					break;
				case Cell.CELL_TYPE_BLANK:
					cols.put(excelHeader.getCell(j).getStringCellValue(), "");
					break;
				case Cell.CELL_TYPE_FORMULA:
					switch(currentcell.getCachedFormulaResultType()) {
					case Cell.CELL_TYPE_STRING:
						cols.put(excelHeader.getCell(j).getStringCellValue(), sh.getRow(i).getCell(j).getStringCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						cols.put(excelHeader.getCell(j).getStringCellValue(), String.valueOf(sh.getRow(i).getCell(j).getNumericCellValue()));
						break;
						}
					}
				}
			}
			rows.put(sh.getRow(i).getCell(0).getStringCellValue(), cols);
		}
		sheets.put(wb.getSheetName(s), rows);
		rows = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		}
		
		return sheets;
		
	}

/**
 	 * Method to read the cell value according the sheetName and Test Case ID
 * 
 * @param sheetName
 * @param rowHeader
 * @param columnHeader
 * @return
 * @throws IOException
 */

	public String getExcelValue(String sheetName, String rowHeader, String columnHeader) {
	String cellValue = "";
	try {
		if(BasicDefinition.testDataMap.containsKey(sheetName) 
				&& BasicDefinition.testDataMap.get(sheetName).containsKey(rowHeader) && 
				BasicDefinition.testDataMap.get(sheetName).get(rowHeader).containsKey(columnHeader)){
			cellValue = BasicDefinition.testDataMap.get(sheetName).get(rowHeader).get(columnHeader);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return cellValue;
}

	/**
	 * Method to identify the SheetName with the Key value of Row ID and Column Header
	 * 
	 * @param columnHeader
	 * @return
	 */
	public String getExcelSheetName(String columnHeader) {
	String sheetName = "" , record = ""; 
	for (Entry<String, LinkedHashMap<String, LinkedHashMap<String, String>>> entireWorkbook : BasicDefinition.testDataMap.entrySet()) {
			LinkedHashMap<String, LinkedHashMap<String, String>> sheetSpecific = entireWorkbook.getValue();
			if (sheetSpecific.containsKey(BasicDefinition.TC_id)) {
				for (Entry<String, LinkedHashMap<String, String>> rowSpecific : sheetSpecific.entrySet()) {
					if (rowSpecific.getKey().equals(BasicDefinition.TC_id)) {
						LinkedHashMap<String, String> columnName = rowSpecific.getValue();
						for (Entry<String, String> sheetNamerecord :columnName.entrySet()){
							if(sheetNamerecord.getValue().contains("{")){
								record = sheetNamerecord.getValue().replaceAll("[{}]", "");
								if(record.contains(columnHeader)){
									sheetName = sheetNamerecord.getKey();
									break;
								}}
						}
						

					}
				}
			}
		}

		return sheetName;
	}

	/**
	 * Merge the Test Data with Input files such as MT103, MT103 STP, MT103 R, MT202 e.t.c
	 */
	public void merge_TestData_and_InputData(){
		BasicDefinition.testDataMap.forEach((key, value) -> {
		if(!(key.equalsIgnoreCase("TestData"))){
		value.forEach((tckey, tcvalue) -> {
				if(BasicDefinition.testDataMap.get("TestData").keySet().contains(tckey)){
//		    System.out.println("TC_id "+tckey +" value "+tcvalue);
					BasicDefinition.testDataMap.get("TestData").get(tckey).put(key, tcvalue.toString());
			}
		});
		}
	});
}

public String readexcelContent(String painheader, String mt103header, String swiftheader ) {
	String xlValue = "", fieldType = this.getExcelValue("TestData", BasicDefinition.TC_id, "FileType");
	switch(fieldType.toLowerCase().trim()) {
	case "pain001":
		xlValue = this.getExcelValue("Pain001", BasicDefinition.TC_id, painheader);
		break;
	case "mt103":
	case "mt202":
	case "sterci":
		if(getExcelValue("TestData", BasicDefinition.TC_id, "FLOW_TYPE").equalsIgnoreCase("MT202") || getExcelValue("TestData", BasicDefinition.TC_id, "FLOW_TYPE").equalsIgnoreCase("INBOUND")){
			xlValue="TEST"+getExcelValue("MT202", BasicDefinition.TC_id, "32A").substring(0, 2);
		}else {
			xlValue = this.getExcelValue("MT103", BasicDefinition.TC_id, mt103header);
		}
		break;
	case "swiftmt":
		if(this.getExcelValue("TestData", BasicDefinition.TC_id, "FLOW_TYPE").equalsIgnoreCase("MT103"))
			xlValue = this.getExcelValue("SWIFTMT", BasicDefinition.TC_id, swiftheader);
		else
			xlValue = this.getExcelValue("MT202", BasicDefinition.TC_id, swiftheader);
		break;	
	}
	return xlValue;
}

public String readexcelContent(String header) {
	String xlValue = "", fieldType = this.getExcelValue("TestData", BasicDefinition.TC_id, "FileType");
	switch(fieldType.toLowerCase().trim()) {
	case "pain001":
		xlValue = this.getExcelValue(fieldType, BasicDefinition.TC_id, header);
		break;
	case "mt103":
	case "sterci":	
		xlValue = this.getExcelValue("MT103", BasicDefinition.TC_id, header);
		break;
	case "swiftmt":
		if(this.getExcelValue("TestData", BasicDefinition.TC_id, "FLOW_TYPE").equalsIgnoreCase("MT103"))
			xlValue = this.getExcelValue("SWIFTMT", BasicDefinition.TC_id, header);
		else
			xlValue = this.getExcelValue("MT202", BasicDefinition.TC_id, header);
		break;		
	}
	return xlValue;
}
}
