package com.universal.code.bxm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.excel.ExcelUtil;
import com.universal.code.excel.dto.ExcelDTO;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.StringUtil;

public class BxmBeanGenerateUtil {

	private final static Logger logger = LoggerFactory.getLogger(BxmBeanGenerateUtil.class);
	
	private ExcelUtil excelUtil;
	private StringUtil stringUtil;
	private FileUtil fileUtil;
	private GenerateHelper generateHelper;
	
	private final static String EXCEL_START_FIRST_CELL;
	private final static String EXCEL_END_FIRST_CELL;
	
	//엑셀에서 참고하는 시트 이름
	private final static String EXCEL_SHEET_NAME01;
	private final static String EXCEL_SHEET_NAME02;
	private final static String EXCEL_SHEET_NAME03;
	private final static String EXCEL_SHEET_NAME04;
	private final static String EXCEL_SHEET_NAME05;
	private final static String EXCEL_SHEET_NAME06;
	
	
	static {
		EXCEL_START_FIRST_CELL = "[[START]]";
		EXCEL_END_FIRST_CELL = "[[END]]";
		
		EXCEL_SHEET_NAME01 = "3) 프로그램 파일";
		EXCEL_SHEET_NAME02 = "";
		EXCEL_SHEET_NAME03 = "";
		EXCEL_SHEET_NAME04 = "";
		EXCEL_SHEET_NAME05 = "";
		EXCEL_SHEET_NAME06 = "";
	}
	
	public BxmBeanGenerateUtil(){
		excelUtil = new ExcelUtil();
		stringUtil = new StringUtil();
		fileUtil = new FileUtil();
		generateHelper = new GenerateHelper();
	}
	
	public void execute(String javaPrefix, String excelPath) {
		
		
		// 이미 캐쉬된 문서인지 확인
		Map<String, List<ExcelDTO>> sheetData = GenerateHelper.getExcelData(excelPath);
		// 아닐경우 신규 분석
		if(sheetData == null) {
			//sheetData = new HashMap<String, List<ExcelDTO>>();
			// 설계 엑셀문서 파싱
			 sheetData = excelUtil.readExcel(excelPath, false);
			// 파싱한 문서내용 저장
			GenerateHelper.setExcelData(excelPath, sheetData);
		}
		// 분석한 엑셀 내용에 따른 BxmBean 코드 생성
		
		List<ExcelDTO> designDataList = sheetData.get(EXCEL_SHEET_NAME01);
		if(designDataList != null) {
			boolean start = false;
			boolean check = false;
			long startRow = -1;
			for(ExcelDTO cell : designDataList) {
				if(!start && check && cell.getRowIndex() == startRow) {
					start = true;
				}
				if(cell.getCellIndex() == 0 && cell.getCellValue().equalsIgnoreCase(EXCEL_END_FIRST_CELL)) {
					break;
				}
				if(start) {
					logger.debug("{}:{} value: {}", cell.getRowIndex(), cell.getCellIndex(), cell.getCellValue());
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
				}
				if(cell.getCellIndex() == 0 && cell.getCellValue().equalsIgnoreCase(EXCEL_START_FIRST_CELL)) {
					check = true;
					startRow = cell.getRowIndex() + 1;
				}
			}
		}
		
		
		
	}
}
