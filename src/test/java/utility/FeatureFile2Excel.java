package utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FeatureFile2Excel {

	public static void main(String[] args) {
		LineIterator iterator = null;
		StringBuffer scenario = new StringBuffer();
		ArrayList<String> listofScenarios = new ArrayList<String>();
		XSSFWorkbook book = new XSSFWorkbook();
		XSSFSheet xmlsheet = book.createSheet("TestCaseUpload");
		XSSFCellStyle style = book.createCellStyle();
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		
		int rowcount = 0, i = 0;
		writeRMAHeader(book, xmlsheet);
		try {
			iterator = FileUtils.lineIterator(new File(
					"C:\\Users\\mahabu\\Downloads\\5219.feature"),
					"UTF-8");
			while (iterator.hasNext()) {
				String line = iterator.nextLine();
				if (line.startsWith("@") || line.startsWith("#") || line.length() == 0)
					continue;

				if (line.startsWith("Scenario:")) {
					listofScenarios.add(scenario.toString());
					scenario.delete(0, scenario.length() - 1);
				}
				scenario.append(line.trim() + System.lineSeparator());
			}
			listofScenarios.add(scenario.toString());
			for (String string : listofScenarios) {

				if (string.startsWith("Scenario:")) {
					rowcount = xmlsheet.getLastRowNum() - xmlsheet.getFirstRowNum();
					Row newrow = xmlsheet.createRow(rowcount + 1);
					i++;
					for (int j = 0; j < xmlsheet.getRow(0).getLastCellNum(); j++) {
						Cell cell = newrow.createCell(j);
						cell.setCellStyle(style);
						switch (j) {
						case 0:
							style.setVerticalAlignment(VerticalAlignment.TOP);
							cell.setCellValue(i);
							
							break;
						case 5:
							style.setVerticalAlignment(VerticalAlignment.TOP);
							cell.setCellValue(StringUtils.substringBetween(string, "Scenario:", "Given").replaceAll("['&-/]", ""));
							break;
						case 6:
							CellStyle styles = book.createCellStyle();
							styles.setWrapText(true);
							cell.setCellStyle(styles);
							styles.setVerticalAlignment(VerticalAlignment.TOP);
							cell.setCellValue("Given" + StringUtils.substringBetween(string, "Given", "When").replaceAll("['&-/]", ""));
							break;
						case 7:
							style.setVerticalAlignment(VerticalAlignment.TOP);
							cell.setCellValue("Step 1" );
							break;
						case 8:
							CellStyle style1 = book.createCellStyle();
							style1.setWrapText(true);
							cell.setCellStyle(style1);
							style1.setVerticalAlignment(VerticalAlignment.TOP);
							cell.setCellValue("When" + StringUtils.substringBetween(string, "When", "Then").replaceAll("['&-/]", ""));
							break;
						case 9:
							CellStyle style2 = book.createCellStyle();
							style2.setWrapText(true);
							cell.setCellStyle(style2);
							style2.setVerticalAlignment(VerticalAlignment.TOP);
							cell.setCellValue(string.substring(string.indexOf("Then")).replaceAll("['&-/]", ""));
							break;
						case 23:
							style.setVerticalAlignment(VerticalAlignment.TOP);
							cell.setCellValue("3-Medium");
							break;
						case 24:
							style.setVerticalAlignment(VerticalAlignment.TOP);
							cell.setCellValue("3-Medium");
							break;
						case 25:
							style.setVerticalAlignment(VerticalAlignment.TOP);
							cell.setCellValue("Ready");
							break;
						case 26:
							style.setVerticalAlignment(VerticalAlignment.TOP);
							style.setBorderRight(BorderStyle.MEDIUM);
							cell.setCellValue("Automation");
							break;

						}

					}

				}
			}

			FileOutputStream out = new FileOutputStream(
					System.getProperty("user.home") + File.separator + "ALM" + ".xlsx");
			book.write(out);
			out.close();
			System.out.println("Feature file content are upload in ALM Format");
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	private static void writeRMAHeader(XSSFWorkbook wb, XSSFSheet sheet) {

		Row headerRow = sheet.createRow(0);
		CellStyle style = wb.createCellStyle();
		XSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setBold(true);
		font.setItalic(false);

		style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFont(font);

		Cell headerCell = headerRow.createCell(0);
		headerCell.setCellValue("S.No");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(1);
		headerCell.setCellValue("To Be Uploaded(No\\No)");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(2);
		headerCell.setCellValue("Subject");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(3);
		headerCell.setCellValue("Foldername");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(4);
		headerCell.setCellValue("Subfoldername");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(5);
		headerCell.setCellValue("Test Name");
		headerCell.setCellStyle(style);
		sheet.setColumnWidth(5, 12000);


		headerCell = headerRow.createCell(6);
		headerCell.setCellValue("Test Description");
		headerCell.setCellStyle(style);
		sheet.setColumnWidth(6, 12000);

		headerCell = headerRow.createCell(7);
		headerCell.setCellValue("Step Name");
		headerCell.setCellStyle(style);
		
		headerCell = headerRow.createCell(8);
		headerCell.setCellValue("Step Description");
		headerCell.setCellStyle(style);
		sheet.setColumnWidth(8, 12000);
		
		headerCell = headerRow.createCell(9);
		headerCell.setCellValue("Expected Results");
		headerCell.setCellStyle(style);
		sheet.setColumnWidth(9, 12000);

		headerCell = headerRow.createCell(10);
		headerCell.setCellValue("Test Configuration");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(11);
		headerCell.setCellValue("Test ConfigurationDescription");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(12);
		headerCell.setCellValue("No of Test Configurations");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(13);
		headerCell.setCellValue("Parameter Name 1");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(14);
		headerCell.setCellValue("Parameter Name 2");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(15);
		headerCell.setCellValue("Parameter Name 3");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(16);
		headerCell.setCellValue("Parameter Name 4");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(17);
		headerCell.setCellValue("Parameter Name 5");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(18);
		headerCell.setCellValue("Actual Value 1");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(19);
		headerCell.setCellValue("Actual Value 2");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(20);
		headerCell.setCellValue("Actual Value 3");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(21);
		headerCell.setCellValue("Actual Value 4");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(22);
		headerCell.setCellValue("Actual Value 5");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(23);
		headerCell.setCellValue("Priority");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(24);
		headerCell.setCellValue("Complexity");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(25);
		headerCell.setCellValue("Status");
		headerCell.setCellStyle(style);

		headerCell = headerRow.createCell(26);
		headerCell.setCellValue("Type");
		headerCell.setCellStyle(style);
	}
}
