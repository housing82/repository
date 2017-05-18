package com.universal.code.excel.dto;

import java.util.ArrayList;
import java.util.List;

import com.universal.code.utils.DateUtil;
import com.universal.code.utils.FileUtil;

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

public class ExcelDTO {

	private int		sheetIndex;
	private String	sheetName;

	private String	fileName;
	private String	filePath;
	private String	mimeType;
	
	private long 	rowIndex;
	private long 	cellIndex;
	
	private int 	cellType;
	private String 	cellTypeName;
	private String 	cellValue;
	private String	cellFormula;
	
	private List<Object>	resultSet;

	private int targetSheetIndex;
	private int startRowIndex;
	private int endRowIndex;
	private List<Integer> readCellIndex;

	private boolean isExcel;
	private String 	extension;
	private String 	dateFormat;
	
	public ExcelDTO(){
		this.reset();
	}

	private void reset(){
		sheetIndex	=	0;
		sheetName = "";
		fileName = "";
		filePath = "";
		mimeType = "";
		rowIndex = 0;
		cellIndex = 0;
		cellType = -1;
		cellTypeName = "";
		cellValue = "";
		cellFormula = "";
		resultSet = new ArrayList<Object>();

		targetSheetIndex = -1; //Excel Read Setting var
		startRowIndex = -1; //Excel Read Setting var
		endRowIndex = -1; //Excel Read Setting var
		readCellIndex = new ArrayList<Integer>(); //Excel Read Setting var
		
		isExcel = false;
		extension = "";
		dateFormat = DateUtil.DEF_DAY_FORMAT;
	}

	
	
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public boolean isExcel() {
		return isExcel;
	}

	public void setExcel(boolean isExcel) {
		this.isExcel = isExcel;
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
		this.extension = FileUtil.getExt(filePath);
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public long getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(long rowIndex) {
		this.rowIndex = rowIndex;
	}

	public long getCellIndex() {
		return cellIndex;
	}

	public void setCellIndex(long cellIndex) {
		this.cellIndex = cellIndex;
	}


	public int getCellType() {
		return cellType;
	}

	public void setCellType(int cellType) {
		this.cellType = cellType;
	}

	public String getCellTypeName() {
		return cellTypeName;
	}

	public void setCellTypeName(String cellTypeName) {
		this.cellTypeName = cellTypeName;
	}

	public String getCellValue() {
		return (cellValue != null ? cellValue.trim() : null);
	}

	public void setCellValue(String cellValue) {
		this.cellValue = cellValue;
	}

	public String getCellFormula() {
		return cellFormula;
	}

	public void setCellFormula(String cellFormula) {
		this.cellFormula = cellFormula;
	}

	public List<Object> getResultSet() {
		return resultSet;
	}

	public void setResultSet(List<Object> resultSet) {
		this.resultSet = resultSet;
	}

	public int getTargetSheetIndex() {
		return targetSheetIndex;
	}

	public void setTargetSheetIndex(int targetSheetIndex) {
		this.targetSheetIndex = targetSheetIndex;
	}

	public int getStartRowIndex() {
		return startRowIndex;
	}

	public void setStartRowIndex(int startRowIndex) {
		this.startRowIndex = startRowIndex;
	}

	public int getEndRowIndex() {
		return endRowIndex;
	}

	public void setEndRowIndex(int endRowIndex) {
		this.endRowIndex = endRowIndex;
	}

	public List<Integer> getReadCellIndex() {
		return readCellIndex;
	}

	public void setReadCellIndex(List<Integer> readCellIndex) {
		this.readCellIndex = readCellIndex;
	}

	public void addReadCellIndex(int obj) {
		readCellIndex.add(obj);
	}

}

