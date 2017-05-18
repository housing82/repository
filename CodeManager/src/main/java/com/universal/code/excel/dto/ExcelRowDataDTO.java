package com.universal.code.excel.dto;

import java.util.ArrayList;
import java.util.List;

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

public class ExcelRowDataDTO {
	
	private WorkBookDTO rootWorkBook;
	
	private int	rowIndex;
	
	private int	rowType;
	
	private List<ExcelCellDataDTO> excelCellDataList; 
	
	public ExcelRowDataDTO(WorkBookDTO rootWorkBook){
		if(rootWorkBook == null || rootWorkBook.getXSSFWorkbook() == null) {
			throw new ValidateException("엑셀 워크북이 존재하지 않습니다.");
		}
		
		this.rootWorkBook = rootWorkBook;
		this.reset();
	}

	private void reset(){
		rowType = -1;
		rowIndex = -1;
		excelCellDataList = new ArrayList<ExcelCellDataDTO>();
	}
	
	public XSSFWorkbook getXSSFWorkbook() {
		return rootWorkBook.getXSSFWorkbook();
	}

	public int getRowType() {
		return rowType;
	}

	public void setRowType(int rowType) {
		this.rowType = rowType;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public List<ExcelCellDataDTO> getExcelCellDataList() {
		return excelCellDataList;
	}

	public void setExcelCellDataList(List<ExcelCellDataDTO> excelCellDataList) {
		this.excelCellDataList = excelCellDataList;
	}

	public void addExcelCellDataList(ExcelCellDataDTO excelCellData) {
		this.excelCellDataList.add(excelCellData);
	}

}

