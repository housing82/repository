package com.universal.code.excel.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
* <p>Title: AnalyzerModel</p>
* <p>Description:
*
* </p>
* <p>Copyright: Copyright (c) 2012</p>
* <p>Company: mvc</p>
* @since
* @author ksw
* @version 1.0
*/

public class WorkBookDTO {

	private List<ExcelSheetDTO> excelSheetList;
	private XSSFWorkbook workbook;
	private String fileDir;
	private String fileName;
	
	public WorkBookDTO(){
		this.workbook = new XSSFWorkbook();
		this.reset();
	}

	private void reset(){
		excelSheetList = new ArrayList<ExcelSheetDTO>();
		fileDir = null;
		fileName = null;
	}
	
	public XSSFWorkbook getXSSFWorkbook() {
		return workbook;
	}

	public List<ExcelSheetDTO> getExcelSheetList() {
		return excelSheetList;
	}

	public void setExcelSheetList(List<ExcelSheetDTO> excelSheetList) {
		this.excelSheetList = excelSheetList;
	}

	public void addExcelSheet(ExcelSheetDTO excelSheet) {
		this.excelSheetList.add(excelSheet);
	}

	public String getFileDir() {
		return fileDir;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}

