package com.universal.code.excel.dto;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
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


public class ExcelDefaultStyleDTO {

	private WorkBookDTO rootWorkBook;
	
	private XSSFCellStyle defaultTitleCellStyle;
	private XSSFCellStyle defaultDataCellStyle;
	private XSSFCellStyle defaultDateCellStyle;
	
	private XSSFFont defaultTitleFont;
	private XSSFFont defaultDataFont;
	

	public ExcelDefaultStyleDTO(WorkBookDTO rootWorkBook){
		if(rootWorkBook == null || rootWorkBook.getXSSFWorkbook() == null) {
			throw new ValidateException("엑셀 워크북이 존재하지 않습니다.");
		}
		
		this.rootWorkBook = rootWorkBook;
		this.reset();
	}

	private void reset(){
		
		defaultTitleCellStyle = null;
		defaultDataCellStyle = null;
		defaultDateCellStyle = null;
		
		defaultTitleFont = null;
		defaultDataFont = null;
		
		this.initDefaultStyle();
	}

	private void initDefaultStyle(){
		//컬럼 타이틀(라벨)폰트 스타일
		defaultTitleFont = rootWorkBook.getXSSFWorkbook().createFont();
		defaultTitleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		
        //컬럼 타이틀(라벨)셀 스타일
        defaultTitleCellStyle = rootWorkBook.getXSSFWorkbook().createCellStyle();
        defaultTitleCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        defaultTitleCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        defaultTitleCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        defaultTitleCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        defaultTitleCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);  
        defaultTitleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        defaultTitleCellStyle.setFont(defaultTitleFont);
        defaultTitleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //defaultTitleCellStyle.setWrapText(true); 
        //defaultTitleCellStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);        
        
        //데이터 셀 스타일
        defaultDataCellStyle = rootWorkBook.getXSSFWorkbook().createCellStyle();
        defaultDataCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        defaultDataCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        defaultDataCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        defaultDataCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        
        //데이터 날짜(데이트)셀 스타일
        defaultDateCellStyle = rootWorkBook.getXSSFWorkbook().createCellStyle();
        defaultDateCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        defaultDateCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        defaultDateCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        defaultDateCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        defaultDateCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        defaultDateCellStyle.setDataFormat(rootWorkBook.getXSSFWorkbook().createDataFormat().getFormat("m/d/yy"));
	}
	
	public XSSFWorkbook getXSSFWorkbook() {
		return rootWorkBook.getXSSFWorkbook();
	}

	public XSSFCellStyle getDefaultTitleCellStyle() {
		return defaultTitleCellStyle;
	}

	public void setDefaultTitleCellStyle(XSSFCellStyle defaultTitleCellStyle) {
		this.defaultTitleCellStyle = defaultTitleCellStyle;
	}

	public XSSFCellStyle getDefaultDataCellStyle() {
		return defaultDataCellStyle;
	}

	public void setDefaultDataCellStyle(XSSFCellStyle defaultDataCellStyle) {
		this.defaultDataCellStyle = defaultDataCellStyle;
	}

	public XSSFCellStyle getDefaultDateCellStyle() {
		return defaultDateCellStyle;
	}

	public void setDefaultDateCellStyle(XSSFCellStyle defaultDateCellStyle) {
		this.defaultDateCellStyle = defaultDateCellStyle;
	}

	public XSSFFont getDefaultTitleFont() {
		return defaultTitleFont;
	}

	public void setDefaultTitleFont(XSSFFont defaultTitleFont) {
		this.defaultTitleFont = defaultTitleFont;
	}

	public XSSFFont getDefaultDataFont() {
		return defaultDataFont;
	}

	public void setDefaultDataFont(XSSFFont defaultDataFont) {
		this.defaultDataFont = defaultDataFont;
	}

}

