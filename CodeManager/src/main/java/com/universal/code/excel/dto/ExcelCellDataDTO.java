package com.universal.code.excel.dto;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.universal.code.exception.ValidateException;

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


public class ExcelCellDataDTO {

	public static final int CELL_TYPE_NUMERIC = Cell.CELL_TYPE_NUMERIC;
	public static final int CELL_TYPE_STRING = Cell.CELL_TYPE_STRING;
	public static final int CELL_TYPE_FORMULA = Cell.CELL_TYPE_FORMULA;
	public static final int CELL_TYPE_BLANK = Cell.CELL_TYPE_BLANK;
	public static final int CELL_TYPE_BOOLEAN = Cell.CELL_TYPE_BOOLEAN;
	//public static final int CELL_TYPE_ERROR = Cell.CELL_TYPE_ERROR;
	
	public static final int CELL_TYPE_RICH_STRING = 6;
	public static final int CELL_TYPE_CALENDAR = 7;
	public static final int CELL_TYPE_DATE = 8;
	
	private WorkBookDTO rootWorkBook;
	
	private int	cellIndex;
	private int	cellType;
	private Object cellValue;
	private Object errorValue;
	private XSSFComment cellComment;
	
	private XSSFHyperlink hyperlink;
	private XSSFCellStyle cellStyle;
	
	public ExcelCellDataDTO(WorkBookDTO rootWorkBook){
		if(rootWorkBook == null || rootWorkBook.getXSSFWorkbook() == null) {
			throw new ValidateException("엑셀 워크북이 존재하지 않습니다.");
		}
		
		this.rootWorkBook = rootWorkBook;
		this.reset();
	}

	private void reset(){
		cellIndex = -1;
		cellType = Cell.CELL_TYPE_STRING;
		cellValue = null;
		hyperlink = null;
		cellStyle = null;
		errorValue = null;
		cellComment = null;
	}

	public XSSFWorkbook getXSSFWorkbook() {
		return rootWorkBook.getXSSFWorkbook();
	}
	
	public int getCellIndex() {
		return cellIndex;
	}

	public void setCellIndex(int cellIndex) {
		this.cellIndex = cellIndex;
	}

	public Object getErrorValue() {
		return errorValue;
	}

	public void setErrorValue(Object errorValue) {
		if(!byte.class.isAssignableFrom(errorValue.getClass()) && !FormulaError.class.isAssignableFrom(errorValue.getClass())) {
			throw new ValidateException("ErrorValue는 byte 또는 FormulaError만 셋팅가능합니다.");
		}
		
		this.errorValue = errorValue;
	}

	public XSSFComment getCellComment() {
		return cellComment;
	}

	public void setCellComment(XSSFComment cellComment) {
		this.cellComment = cellComment;
	}

	public int getCellType() {
		return cellType;
	}

	public void setCellType(int cellType) {
		this.cellType = cellType;
	}

	public Object getCellValue() {
		return cellValue;
	}

	public void setCellValue(Object cellValue) {
		this.cellValue = cellValue;
	}

	public XSSFHyperlink getHyperlink() {
		return hyperlink;
	}

	public void setHyperlink(XSSFHyperlink hyperlink) {
		this.hyperlink = hyperlink;
	}

	public XSSFCellStyle getCellStyle() {
		return cellStyle;
	}

	public void createCellStyle() {
		this.cellStyle = rootWorkBook.getXSSFWorkbook().createCellStyle();
	}
}

