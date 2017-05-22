package com.universal.code.excel.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.universal.code.excel.ExcelUtil;
import com.universal.code.exception.ValidateException;
import com.universal.code.utils.RegexUtil;
import com.universal.code.utils.StringUtil;

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

public class ExcelSheetDTO {
	
	public static final int ROW_TYPE_TITLE;
	
	public static final int ROW_TYPE_DATA;
	
	static {
		ROW_TYPE_TITLE = 0;
		
		ROW_TYPE_DATA = 1;
	} 
	
	private RegexUtil regexUtil;
	
	private WorkBookDTO rootWorkBook;
	
	private ExcelDefaultStyleDTO excelStyleDTO;
	
	private List<ExcelRowDataDTO> excelRowTitleList;
	
	private List<ExcelRowDataDTO> excelRowDataList;
	
	private int	sheetIndex;
	
	private String	sheetName;
	
	public ExcelSheetDTO(WorkBookDTO rootWorkBook){
		if(rootWorkBook == null || rootWorkBook.getXSSFWorkbook() == null) {
			throw new ValidateException("엑셀 워크북이 존재하지 않습니다.");
		}
		this.rootWorkBook = rootWorkBook;
		this.reset(rootWorkBook);
	}

	private void reset(WorkBookDTO workbook){
		regexUtil = new RegexUtil();
		
		sheetIndex = -1;
		excelStyleDTO = new ExcelDefaultStyleDTO(workbook);
		excelRowTitleList = new ArrayList<ExcelRowDataDTO>();
		excelRowDataList = new ArrayList<ExcelRowDataDTO>();
		
		sheetName = null;
	}

	public XSSFWorkbook getXSSFWorkbook() {
		return rootWorkBook.getXSSFWorkbook();
	}
	
	public int getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public ExcelDefaultStyleDTO getExcelStyleDTO() {
		return excelStyleDTO;
	}

	public void setExcelStyleDTO(ExcelDefaultStyleDTO excelStyleDTO) {
		this.excelStyleDTO = excelStyleDTO;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		if(StringUtil.isEmpty(sheetName)) {
			throw new ValidateException("null또는 0바이트 공백문자는 엑셀시트 이름으로 허용되지 않습니다.");
		}
		
		//작성불가능 특수문자  \ / ? * [ ] : 
		sheetName = regexUtil.replaceAllPattern(sheetName, ExcelUtil.EXCLUDE_SHEET_NAME_CHARACTER, "_");
		
		int duplicate = 0;
		String savedSheetName = null; 
		for(ExcelSheetDTO sheet : rootWorkBook.getExcelSheetList()) {
			savedSheetName = sheet.getSheetName();
			if(savedSheetName.indexOf(":") > -1) {
				savedSheetName = savedSheetName.substring(0, savedSheetName.lastIndexOf(":"));
			}
			if(savedSheetName.equals(sheetName)) {
				//throw new ValidateException("엑셀 시트이름은 동일하게 설정할수 없습니다.");
				duplicate++;
			}
		}
		
		if(duplicate > 0) {
			this.sheetName = sheetName.concat(":").concat(Integer.toString(duplicate));
		}
		else {
			this.sheetName = sheetName;
		}
	}

	public List<ExcelRowDataDTO> getExcelRowTitleList() {
		return excelRowTitleList;
	}

	public void setExcelRowTitleList(List<ExcelRowDataDTO> excelRowTitleList) {
		this.excelRowTitleList = excelRowTitleList;
	}

	public void addExcelRowTitleList(ExcelRowDataDTO excelRowTitleDTO) {
		this.excelRowTitleList.add(excelRowTitleDTO);
	}
	
	public List<ExcelRowDataDTO> getExcelRowDataList() {
		return excelRowDataList;
	}

	public void setExcelRowDataList(List<ExcelRowDataDTO> excelRowDataList) {
		this.excelRowDataList = excelRowDataList;
	}

	public void addExcelRowDataList(ExcelRowDataDTO excelRowDataDTO) {
		this.excelRowDataList.add(excelRowDataDTO);
	}
}

