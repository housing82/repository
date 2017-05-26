package com.universal.code.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.constants.IOperateCode;
import com.universal.code.excel.dto.ExcelCellDataDTO;
import com.universal.code.excel.dto.ExcelDefaultStyleDTO;
import com.universal.code.excel.dto.ExcelRowDataDTO;
import com.universal.code.excel.dto.ExcelSheetDTO;
import com.universal.code.excel.dto.WorkBookDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.DateUtil;

public class ExcelWriterUtil {
	private static final Logger logger = LoggerFactory.getLogger(ExcelWriterUtil.class);
	
	private ExcelUtil excelUtil;
	
	public ExcelWriterUtil() {
		excelUtil = new ExcelUtil();
	}

	public String createExcel (WorkBookDTO workBookDTO, Map<String, List<Map<String, Object>>> analyzedMap) {
		
		List<ExcelSheetDTO> excelSheetList = new ArrayList<ExcelSheetDTO>();
		ExcelSheetDTO excelSheetDTO = null;
		ExcelRowDataDTO excelRowTitleDTO = null;
		ExcelRowDataDTO excelRowDataDTO = null;
		ExcelDefaultStyleDTO excelDefaultStyleDTO = null;
		ExcelCellDataDTO excelCellDataDTO = null;
		List<Map<String, Object>> methodList = null;
		Map<String, Object> methodMap = null;
		StringBuilder cellStrbd = null;
		
		int sheetIdx = 0;
		int columnIdx = 0;
		int rowIdx = 0;
		for(Entry<String, List<Map<String, Object>>> entry : analyzedMap.entrySet()) {
			methodList = entry.getValue();
			if(methodList == null || methodList.size() == 0) {
				continue;
			}
			
        	excelSheetDTO = new ExcelSheetDTO(workBookDTO);
        	excelSheetDTO.setSheetIndex(sheetIdx);	
        	excelSheetDTO.setSheetName(entry.getKey());
        	
        	//엑셀 디폴트 스타일
        	excelDefaultStyleDTO = new ExcelDefaultStyleDTO(workBookDTO);
        	//set default style
        	excelSheetDTO.setExcelStyleDTO(excelDefaultStyleDTO);
        	
        	//시트별 최상단 컬럼 타이틀
        	excelRowTitleDTO = new ExcelRowDataDTO(workBookDTO);
        	methodMap = methodList.get(0);
        	
        	columnIdx = 0;
        	for(Entry<String, Object> rowTitle : methodMap.entrySet()) {
        		//logger.debug("method: {}", rowTitle);
        		
        		//타이틀 컬럼
            	excelCellDataDTO = new ExcelCellDataDTO(workBookDTO);
        		excelCellDataDTO.setCellIndex(columnIdx);
        		excelCellDataDTO.setCellType(ExcelCellDataDTO.CELL_TYPE_STRING);
        		excelCellDataDTO.setCellValue(rowTitle.getKey());
        		
        		//타이틀 로우에 컬럼 셋팅
        		excelRowTitleDTO.setRowIndex(rowIdx);
        		excelRowTitleDTO.setRowType(ExcelSheetDTO.ROW_TYPE_TITLE); 
        		excelRowTitleDTO.addExcelCellDataList(excelCellDataDTO);
        		
        		columnIdx++;
        	}
        	//add
        	excelSheetDTO.addExcelRowTitleList(excelRowTitleDTO);
        	rowIdx++;
        	

        	
        	for(Map<String, Object> rowData : methodList) {
            	//시트별 로우 데이터
            	excelRowDataDTO = new ExcelRowDataDTO(workBookDTO);
            	
            	columnIdx = 0;
            	for(Entry<String, Object> cellData : rowData.entrySet()) {
            		//logger.debug("method: {}", cellData);
            		cellStrbd = new StringBuilder();
            		if(cellData.getValue() != null) {
            			//logger.debug("cellDataClass: {}", cellData.getValue().getClass());
            			if(List.class.isAssignableFrom(cellData.getValue().getClass())) {
            				
            				for(Object cells : (List) cellData.getValue()) {
            					if(!cellStrbd.toString().isEmpty()) {
            						cellStrbd.append(", ");
            					}
            					cellStrbd.append(cells);
            				}
            			}
            			else {
            				cellStrbd.append(cellData.getValue());
            			}
            		}
            		
            		//데이터 컬럼
                	excelCellDataDTO = new ExcelCellDataDTO(workBookDTO);
            		excelCellDataDTO.setCellIndex(columnIdx);
            		excelCellDataDTO.setCellType(ExcelCellDataDTO.CELL_TYPE_STRING);
            		excelCellDataDTO.setCellValue(cellStrbd.toString());
            		
            		//데이터 로우에 컬럼 셋팅
            		excelRowDataDTO.setRowIndex(rowIdx);
            		excelRowDataDTO.setRowType(ExcelSheetDTO.ROW_TYPE_DATA); 
            		excelRowDataDTO.addExcelCellDataList(excelCellDataDTO);
            		
            		columnIdx++;
            	}
            	
            	//add
            	excelSheetDTO.addExcelRowDataList(excelRowDataDTO);
            	rowIdx++;
        	}
        	        	
        	excelSheetList.add(excelSheetDTO);
        	
			sheetIdx++;
		}

        
        //엑셀 시트 목록 셋팅
        workBookDTO.setExcelSheetList(excelSheetList);
        
        //설정한 데이터로 엑셀 생성
        String path = excelUtil.wrtieXlsx(workBookDTO);

        //생성 성공한 엑셀 경로 리턴
		return path;
	}
	
	/**
	 * 파일명의 확장자 앞에 v년월일시분밀리초 를 붙여 rename(백업)합니다.
	 * @param writeExcelPath
	 * @param excelFile
	 * @return
	 */
	public boolean renameExcelFile(String writeExcelPath, File excelFile, String excelVersionDateFormat) {
		
		String renameExt = getExcelExt(writeExcelPath);
		String renameDate = IOperateCode.VERSION_TAG_V.concat(DateUtil.getFastDate(excelVersionDateFormat));
		String renameFilePath = writeExcelPath.substring(0, writeExcelPath.length() - renameExt.length());
		renameFilePath = renameFilePath.concat(".").concat(renameDate).concat(renameExt);
		logger.debug("동일한 이름의 엑셀파일이 존재함으로 이미 존재하는 파일명의 이름을 변경함\nfrom: {}\nto: {}", writeExcelPath, renameFilePath);
		
		File renameExcelFile = new File(renameFilePath);
		boolean out = excelFile.renameTo(renameExcelFile);
		
		logger.debug("renameTo: {}", out);
		return out;
	}
	
	

	/**
	 * 주어진 경로의 엑셀 확장자를 리턴합니다.
	 * 엑셀이 아닐경우 익셉션
	 * @param excelPath
	 * @return
	 */
	private String getExcelExt(String excelPath) {
		String out = null;
		if(excelPath.endsWith(".xlsx")) {
			out = ".xlsx";
		}
		else if(excelPath.endsWith(".xls")) {
			out = ".xls";
		}
		else {
			throw new ApplicationException("파일 확장자가 엑셀 형식이 아닙니다.");
		}
		return out;
	}

	
	
}
