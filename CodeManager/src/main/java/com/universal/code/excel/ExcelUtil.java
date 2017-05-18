package com.universal.code.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.excel.dto.ExcelCellDataDTO;
import com.universal.code.excel.dto.ExcelDTO;
import com.universal.code.excel.dto.ExcelDefaultStyleDTO;
import com.universal.code.excel.dto.ExcelRowDataDTO;
import com.universal.code.excel.dto.ExcelSheetDTO;
import com.universal.code.excel.dto.WorkBookDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.exception.ValidateException;
import com.universal.code.utils.DateUtil;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.PropertyUtil;
import com.universal.code.utils.RegexUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;
import com.universal.code.utils.UniqueId4j;

/**
 * <p>
 * Title: CommonUtil
 * </p>
 * <p>
 * Description: 엑셀 핸들링 유틸
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: mvc
 * </p>
 * 
 * @since 2012
 * @author ksw
 * @version 1.0
 */
@Component
public class ExcelUtil {

	private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	private final boolean logging = true;

	public static final String EXCLUDE_SHEET_NAME_CHARACTER = "([/\\\\\\?\\*\\[\\]:]+)";
	
	public static final String DUPLICATE_SHEET_NUMBER = ":([0-9]+)";
	
	public final static String EXCEL_EXTENSION_XLS = "xls";

	public final static String EXCEL_EXTENSION_XLSX = "xlsx";

	public final static String EXCEL_TEMP_REPOSITORY = "WEB-INF/temporary/";
	
	@Autowired
	private FileUtil fileUtil;

	@Autowired
	private StringUtil stringUtil;
	
	@Autowired
	private PropertyUtil property;
	
	@Autowired
	private RegexUtil regex;
	
	public ExcelUtil(){
		if(fileUtil == null) {
			fileUtil = new FileUtil();
		}
		if(stringUtil == null) {
			stringUtil = new StringUtil();
		}
		if(property == null) {
			property = new PropertyUtil();
		}
		if(regex == null) {
			regex = new RegexUtil();
		}
	}
	
	private static String getCellTypeName(int cellTypeCode) {
		switch (cellTypeCode) {
			case 0:
				return "numeric";
			case 1:
				return "text";
			case 2:
				return "formula";
			case 3:
				return "blank";
			case 4:
				return "boolean";
			case 5:
				return "error";
		}
		return "#unknown cell type (" + cellTypeCode + ")#";
	}
	
	public Map<String, List<ExcelDTO>> readExcel(String filePath) {
		return readExcel(filePath, false);
	}

	public Map<String, List<ExcelDTO>> readExcel(String filePath, boolean logging) {
		ExcelDTO excelDTO = new ExcelDTO();
		excelDTO.setFilePath(filePath);
		return readExcel(excelDTO, logging);
	}
	
	public Map<String, List<ExcelDTO>> readExcel(ExcelDTO excelDTO) {
		return readExcel(excelDTO, false);
	}
	
	public Map<String, List<ExcelDTO>> readExcel(ExcelDTO excelDTO, boolean logging) {

		if (excelDTO == null) {
			throw new ApplicationException("엑셀 정보가 존재하지 않습니다.");
		}

		if (logger.isDebugEnabled()) {
			logger.debug(new StringBuilder()
				.append("\n-filePath : " + excelDTO.getFilePath())
				.append("\n-extension : " + excelDTO.getExtension())
				.append("\n")
				.toString());
		}

		String excelPath = excelDTO.getFilePath();

		if (StringUtil.isEmpty(excelPath) || !fileUtil.exists(excelPath)) {
			throw new ApplicationException("파일이 존재하지 않거나 경로가 잘못되었습니다.");
		}
		
		if (StringUtil.isEmpty(excelDTO.getExtension())) {
			throw new ApplicationException("파일이 확장자가 존재하지 않거나 엑셀파일명이 잘못되었습니다.");
		}

		// 메소드 호출 앞단에서 엑셀여부를 판단하고 엑셀파일의 확장자를 excelDTO에 바인드하여 넘길경우
		// IE를 제외 한 브라우저마임타입 : application/vnd.ms-excel
		// IE8은 ms-excel 로 마입타입을 전달하지 못한다. application/octet-stream
		// 마입타입 존재여부와 확장자로만 엑셀을 구분함.
		if (excelDTO.getExtension().equalsIgnoreCase(EXCEL_EXTENSION_XLS)) {
			// 엑셀 2003 버전 or 이전
			return readXls(excelDTO, logging);
		} else if (excelDTO.getExtension().equalsIgnoreCase(EXCEL_EXTENSION_XLSX)) {
			// 엑셀 2007 버전 or 이상
			return readXlsx(excelDTO, logging);
		} else {
			throw new ApplicationException("Is Not Excel File!");
		}
	}

	public static boolean isExcelFile(String fileExtension) {
		boolean out = false;
		String extension = fileExtension;
		if (extension.equalsIgnoreCase(EXCEL_EXTENSION_XLS) || extension.equalsIgnoreCase(EXCEL_EXTENSION_XLSX)) {
			// xls : 엑셀 2003 버전 or 이전, xlsx : //엑셀 2007 버전 or 이상
			out = true;
		}

		return out;
	}

	/**
	 * 엑셀 2003 포멧이하 .xls Read Function
	 * 
	 * @param filePath
	 * @param excelDTO
	 * @return
	 */
	private Map<String, List<ExcelDTO>> readXls(ExcelDTO excelDTO, boolean logging) {
		if (logger.isDebugEnabled()) {
			logger.debug("[START] readXls");
		}
		
		Map<String, List<ExcelDTO>> excelSheet = new LinkedHashMap<String, List<ExcelDTO>>();

		try {
			List<ExcelDTO> resultSet = null;
			HSSFSheet sheet = null;
			HSSFRow row = null;
			HSSFCell cell = null;
			HSSFWorkbook workBook = new HSSFWorkbook(new FileInputStream(new File(excelDTO.getFilePath())));
			FormulaEvaluator evaluator = workBook.getCreationHelper().createFormulaEvaluator();
			DecimalFormat df = new DecimalFormat();
			
			if (logger.isDebugEnabled()) {
				logger.debug(new StringBuilder().append("+- readXls end read excel file").toString());
			}
			
			int targetSheetIndex = -1;
			int startRowIndex = -1;
			int endRowIndex = -1;
			List<Integer> readCellIndex = new ArrayList<Integer>();

			String dateFormat = DateUtil.DEF_DAY_FORMAT;
			if (excelDTO != null) {
				targetSheetIndex = excelDTO.getTargetSheetIndex();
				startRowIndex = excelDTO.getStartRowIndex();
				endRowIndex = excelDTO.getEndRowIndex();
				readCellIndex = excelDTO.getReadCellIndex();
				dateFormat = excelDTO.getDateFormat();
			}

			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

			int sheetNum = workBook.getNumberOfSheets();
			if (logger.isDebugEnabled() && logging) {
				logger.debug(new StringBuilder().append(" sheetNum : ").append(sheetNum).toString());
			}
			/** start sheet */
			for (int k = 0; k < sheetNum; k++) { // sheet
				/** 시트 전체 이거나 설정된 시트이면 읽음 */
				if (targetSheetIndex != -1 && !confirmSheet(k, targetSheetIndex)) {
					continue;
				}// sheet confirm end
				
				sheet = workBook.getSheetAt(k);
				if (logger.isDebugEnabled()) {
					logger.debug(" - Parse Sheet("+k+") " + workBook.getSheetName(k) + " ( PhysicalNumberOfRows:" + sheet.getPhysicalNumberOfRows() + " / LastRowNum:" + sheet.getLastRowNum() + " ) ::::::::::::::::::::::: ");
				}
				
				// int rows = sheet.getPhysicalNumberOfRows(); //실제 값이 존재하는 row
				// 갯수
				int rows = sheet.getLastRowNum();
				// logger.debug( " rows : " + rows);
				// sheet.getLastRowNum();

				/** start row */
				resultSet = new ArrayList<ExcelDTO>();
				for (int r = 0; r <= rows; r++) {
					/** 로우 전체 이거나 설정된 로우이면 읽음 */
					if ((startRowIndex > -1 || endRowIndex > -1) && !confirmReadRow(r, startRowIndex, endRowIndex)) {
						continue;
					}// row confirm end

					row = sheet.getRow(r);
					if(row == null) {
						if (logger.isWarnEnabled() && logging) {
							logger.warn(" > 엑셀 ".concat(Integer.toString(r)).concat("번째 Row(행)이 존재하지 않음"));
						}
						continue;
					}
					
					try {
						// int cells = row.getPhysicalNumberOfCells(); //실제 값이
						// 존재하는 cell 갯수 (셀이 없으면 null point ex)
						int cells = row.getLastCellNum();
						// logger.debug( " cells : " + cells);

						/** start cell */
						for (int c = 0; c < cells; c++) { // after short is deprecation
							try {
								/** 셀 전체 이거나 설정된 셀이면 읽음 */
								if (readCellIndex.size() > 0 && !confirmReadCellIndex(c, readCellIndex)) {
									if (logger.isDebugEnabled() && logging) {
										logger.debug(new StringBuilder().append(" cell confirm fail!! not read excel cell column : ").append(c).toString());
									}
									continue;
								}// cell confirm end
	
								cell = row.getCell(c); // after getCell(short) is deprecation
								
								ExcelDTO dto = new ExcelDTO();
								dto.setSheetIndex(k);
								dto.setRowIndex(r);
								dto.setCellIndex(c);
	
								StringBuilder strb = null;
								if (logger.isDebugEnabled() && logging) {
									strb = new StringBuilder()
										.append(" sheetName : ")
										.append(sheet.getSheetName())
										.append(", sheetNum : ")
										.append(k)
										.append(", row index : ")
										.append(r)
										.append("==cells:")
										.append(c);
								}
								
								if (cell == null) {
									if (logger.isDebugEnabled() && logging) {
										strb.append(", data : cell is null ");
									}
									// continue;
									dto.setCellValue(null);
								} else {
									if (logger.isDebugEnabled() && logging) {
										strb.append(", cellType : ").append(cell.getCellType());
									}
									
									dto.setCellType(cell.getCellType());
									dto.setCellTypeName(getCellTypeName(cell.getCellType()));
									
									switch (cell.getCellType()) {
										case HSSFCell.CELL_TYPE_NUMERIC:
											if (HSSFDateUtil.isCellDateFormatted(cell)) {
												if (logger.isDebugEnabled() && logging) {
													strb.append(", cellValue : ").append(cell.getDateCellValue());
												}
												// logger.debug(cell.getStringCellValue());
												dto.setCellValue(formatter.format(cell.getDateCellValue()));
												break;
											} else {
												if (logger.isDebugEnabled() && logging) {
													strb.append(", cellValue : ").append(cell.getNumericCellValue());
												}
												// logger.debug(cell.getNumericCellValue());
												dto.setCellValue(Double.toString(cell.getNumericCellValue()));
												break;
											}
		
										case HSSFCell.CELL_TYPE_STRING:
											if (logger.isDebugEnabled() && logging) {
												strb.append(", cellValue : ").append(cell.getStringCellValue());
											}
											// logger.debug(cell.getStringCellValue());
											dto.setCellValue(cell.getStringCellValue());
											break;
										case HSSFCell.CELL_TYPE_BOOLEAN:
											if (logger.isDebugEnabled() && logging) {
												strb.append(", cellValue : ").append(cell.getBooleanCellValue());
											}
											
											dto.setCellValue(String.valueOf(cell.getBooleanCellValue()));
											break;
										case HSSFCell.CELL_TYPE_FORMULA:
											if(!(cell.toString()=="") ) {
												switch (evaluator.evaluateFormulaCell(cell)) {
													case HSSFCell.CELL_TYPE_NUMERIC:
														dto.setCellValue(df.format(cell.getNumericCellValue()));
													break;
													case HSSFCell.CELL_TYPE_STRING:
														dto.setCellValue(cell.getStringCellValue());
													break;
													case HSSFCell.CELL_TYPE_BOOLEAN:
														dto.setCellValue(String.valueOf(cell.getBooleanCellValue()));
													break;
												}
											}
											dto.setCellFormula(cell.getCellFormula());
	
											if (logger.isDebugEnabled() && logging) {
												strb.append(", cellValue : ").append(dto.getCellValue())
													.append(", cellFormula : ").append(dto.getCellFormula());
											}
											break;
										case HSSFCell.CELL_TYPE_ERROR:
											if (logger.isDebugEnabled() && logging) {
												strb.append(", cellValue : ").append(cell.getErrorCellValue());
											}
	
											dto.setCellValue(stringUtil.byteToBinaryString(cell.getErrorCellValue()));
											break;
										default:
											/**
											 * CELL_TYPE_BLANK = 3; 
											 */
											if (logger.isDebugEnabled() && logging) {
												strb.append(", cellValue : _blank ");
											}
											dto.setCellValue("");
											// logger.debug("");
											break;
										}
									}
	
								if (logger.isDebugEnabled() && logging) {
									logger.debug(strb.toString());
								}
								
								resultSet.add(dto);
							} catch (Exception e) {
								throw new ApplicationException(new StringBuilder().append("장애 발생 [ cell index : ").append(c).append(" ]").toString() , e);
							}
						}// cell
					} catch (Exception e) {
						throw new ApplicationException(new StringBuilder().append("장애 발생 [ row index : ").append(r).append(" ]").toString() , e);
					}
				}// row

				excelSheet.put(sheet.getSheetName(), resultSet);
			}// sheet

		} catch (Exception e) {
			throw new ApplicationException("엑셀 분석 장애 발생", e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("[END] readXls");
		}
		return excelSheet;
	}
	
	/**
	 * 엑셀 2007 포멧이상 .xlsx Read Function
	 * 
	 * @param filePath
	 * @param excelDTO
	 * @return
	 */
	private Map<String, List<ExcelDTO>> readXlsx(ExcelDTO excelDTO, boolean logging) {
		if (logger.isDebugEnabled()) {
			logger.debug("[START] readXlsx");
		}
		
		Map<String, List<ExcelDTO>> excelSheet = new LinkedHashMap<String, List<ExcelDTO>>();

		try {
			List<ExcelDTO> resultSet = null;
			XSSFSheet sheet = null;
			XSSFRow row = null;
			XSSFCell cell = null;
			XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(new File(excelDTO.getFilePath())));
			FormulaEvaluator evaluator = workBook.getCreationHelper().createFormulaEvaluator();
			
			if (logger.isDebugEnabled()) {
				logger.debug(new StringBuilder().append("+- readXlsx end read excel file").toString());
			}
			
			int targetSheetIndex = -1;
			int startRowIndex = -1;
			int endRowIndex = -1;
			List<Integer> readCellIndex = new ArrayList<Integer>();

			String dateFormat = DateUtil.DEF_DAY_FORMAT;
			if (excelDTO != null) {
				targetSheetIndex = excelDTO.getTargetSheetIndex();
				startRowIndex = excelDTO.getStartRowIndex();
				endRowIndex = excelDTO.getEndRowIndex();
				readCellIndex = excelDTO.getReadCellIndex();
				dateFormat = excelDTO.getDateFormat();
			}

			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

			// new PopulateUtil().printout(excelDTO);

			int sheetNum = workBook.getNumberOfSheets();
			if (logger.isDebugEnabled() && logging) {
				logger.debug(new StringBuilder().append(" sheetNum : ").append(sheetNum).toString());
			}
			/** start sheet */
			for (int k = 0; k < sheetNum; k++) { // sheet
				/** 시트 전체 이거나 설정된 시트이면 읽음 */
				if (targetSheetIndex != -1 && !confirmSheet(k, targetSheetIndex)) {
					if (logger.isDebugEnabled() && logging) {
						logger.debug(new StringBuilder().append(":::::::::::::: pass sheet number : ").append(k).toString());
					}
					continue;
				} // sheet confirm end
				
				sheet = workBook.getSheetAt(k);
				if (logger.isDebugEnabled()) {
					logger.debug(" - Parse Sheet("+k+") " + workBook.getSheetName(k) + " ( PhysicalNumberOfRows:" + sheet.getPhysicalNumberOfRows() + " / LastRowNum:" + sheet.getLastRowNum() + " ) ::::::::::::::::::::::: ");
				}
				
				// int rows = sheet.getPhysicalNumberOfRows(); //실제 값이 존재하는 row
				// 갯수
				int rows = sheet.getLastRowNum();
				// logger.debug( " rows : " + rows);
				// sheet.getLastRowNum();
				/** start row */
				resultSet = new ArrayList<ExcelDTO>();
				for (int r = 0; r <= rows; r++) {
					/** 로우 전체 이거나 설정된 로우이면 읽음 */
					if ((startRowIndex > -1 || endRowIndex > -1) && !confirmReadRow(r, startRowIndex, endRowIndex)) {
						continue;
					}// row confirm end

					row = sheet.getRow(r);
					if(row == null) {
						if (logger.isWarnEnabled() && logging) {
							logger.warn(" > 엑셀 ".concat(Integer.toString(r)).concat("번째 Row(행)이 존재하지 않음"));
						}
						continue;
					}
					
					try {
						// int cells = row.getPhysicalNumberOfCells(); //실제 값이
						// 존재하는 cell 갯수 (셀이 없으면 null point ex)
						int cells = row.getLastCellNum();
						// logger.debug( " cells : " + cells);

						/** start cell */
						for (int c = 0; c < cells; c++) {
							try {
								/** 셀 전체 이거나 설정된 셀이면 읽음 */
								if (readCellIndex.size() > 0 && !confirmReadCellIndex(c, readCellIndex)) {
									continue;
								}// cell confirm end
	
								cell = row.getCell(c);
	
								ExcelDTO dto = new ExcelDTO();
								dto.setSheetIndex(k);
								dto.setRowIndex(r);
								dto.setCellIndex(c);
								
								StringBuilder strb = null;
								if (logger.isDebugEnabled() && logging) {
									strb = new StringBuilder()
										.append(" sheetName : ")
										.append(sheet.getSheetName())
										.append(", sheetNum : ")
										.append(k)
										.append(", row index : ")
										.append(r)
										.append("==cells:")
										.append(c);
								}
								
								if (cell == null) {
									if (logger.isDebugEnabled() && logging) {
										strb.append(", data : cell is null ");
									}
									// continue;
									dto.setCellValue(null);
								} else {
									if (logger.isDebugEnabled() && logging) {
										strb.append(", cellType : ").append(cell.getCellType());
									}
									
									dto.setCellType(cell.getCellType());
									dto.setCellTypeName(getCellTypeName(cell.getCellType()));
									
									switch (cell.getCellType()) {
										case XSSFCell.CELL_TYPE_NUMERIC:
											if (HSSFDateUtil.isCellDateFormatted(cell)) {
												if (logger.isDebugEnabled() && logging) {
													strb.append(", cellValue : ").append(cell.getDateCellValue());
												}
												// logger.debug(cell.getStringCellValue());
												dto.setCellValue(formatter.format(cell.getDateCellValue()));
												break;
											} else {
												if (logger.isDebugEnabled() && logging) {
													strb.append(", cellValue : ").append(cell.getNumericCellValue());
												}
												// logger.debug(cell.getNumericCellValue());
												dto.setCellValue(Double.toString(cell.getNumericCellValue()));
												break;
											}
		
										case XSSFCell.CELL_TYPE_STRING:
											if (logger.isDebugEnabled() && logging) {
												strb.append(", cellValue : ").append(cell.getStringCellValue());
											}
											// logger.debug(cell.getStringCellValue());
											dto.setCellValue(cell.getStringCellValue());
											break;
										case XSSFCell.CELL_TYPE_BOOLEAN:
											if (logger.isDebugEnabled() && logging) {
												strb.append(", cellValue : ").append(cell.getBooleanCellValue());
											}
											
											dto.setCellValue(String.valueOf(cell.getBooleanCellValue()));
											break;										
										case XSSFCell.CELL_TYPE_FORMULA:
											if(!(cell.toString()=="") ) {
												switch (evaluator.evaluateFormulaCell(cell)) {
													case XSSFCell.CELL_TYPE_NUMERIC:
														dto.setCellValue(String.valueOf(cell.getNumericCellValue()));
													break;
													case XSSFCell.CELL_TYPE_STRING:
														dto.setCellValue(cell.getStringCellValue());
													break;
													case XSSFCell.CELL_TYPE_BOOLEAN:
														dto.setCellValue(String.valueOf(cell.getBooleanCellValue()));
													break;
												}
											}
	    									dto.setCellFormula(cell.getCellFormula());
	
											if (logger.isDebugEnabled() && logging) {
												strb.append(", cellValue : ").append(dto.getCellValue())
													.append(", cellFormula : ").append(dto.getCellFormula())
													.append(", getRawValue : ").append(cell.getRawValue());
											}
	
											break;
										case XSSFCell.CELL_TYPE_ERROR:
											if (logger.isDebugEnabled() && logging) {
												strb.append(", cellValue : ").append(cell.getErrorCellValue());
											}
											
											dto.setCellValue(stringUtil.byteToBinaryString(cell.getErrorCellValue()));
											break;
										default:
											/**
											 * CELL_TYPE_BLANK = 3; 
											 * CELL_TYPE_ERROR = 5;
											 */
											if (logger.isDebugEnabled() && logging) {
												strb.append(", cellValue : _blank ");
											}
											dto.setCellValue("");
											// logger.debug("");
											break;
										}
									}
	
								if (logger.isDebugEnabled() && logging) {
									logger.debug(strb.toString());
								}
								
								resultSet.add(dto);
							} catch (Exception e) {
								throw new ApplicationException(new StringBuilder().append("장애 발생 [ cell index : ").append(c).append(" ]").toString() , e);
							}
						}// cell
					} catch (Exception e) {
						throw new ApplicationException(new StringBuilder().append("장애 발생 [ row index :").append(r).append("]").toString() , e);
					}
				}// row

				excelSheet.put(sheet.getSheetName(), resultSet);
			}// sheet

		} catch (Exception e) {
			throw new ApplicationException("엑셀 분석 장애 발생", e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("[END] readXlsx");
		}
		return excelSheet;
	}

	
	public String wrtieXlsx(WorkBookDTO workBookDTO){
		if (logger.isDebugEnabled()) {
			logger.debug("[START] wrtieXlsx");
		}
		String excelFilePath = null;
		
		XSSFWorkbook workbook = workBookDTO.getXSSFWorkbook();
        XSSFSheet sheet = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        ExcelCellDataDTO excelCellDataDTO = null;
        
        int cellTotalCount = 0;
        //셋팅된 시트 개수만큼 loop
        for(ExcelSheetDTO excelSheet : workBookDTO.getExcelSheetList()) {
        	//sheet 생성
        	if(logger.isDebugEnabled()) {
    			logger.debug("excelSheet.getSheetName() : " + excelSheet.getSheetName());
    		}
        	if(excelSheet.getSheetName() != null) {
        		sheet = workbook.createSheet( regex.replaceAllPatternWrap(excelSheet.getSheetName(), ExcelUtil.DUPLICATE_SHEET_NUMBER, IOperateCode.STR_PAREN_START, IOperateCode.STR_PAREN_END).replace(IOperateCode.STR_COLON, IOperateCode.STR_BLANK) );
        	}
        	else {
        		sheet = workbook.createSheet();
        	}
        	
        	StringBuilder indexSB = null;
        	if(logger.isDebugEnabled()) {
        		indexSB = new StringBuilder();
        		indexSB.append("[Create Excel Index]\n");
        	}
        	//Title
        	int r = 0;
        	for(ExcelRowDataDTO rowData : excelSheet.getExcelRowTitleList()){
        		row = sheet.createRow(r);
        		if(logger.isDebugEnabled()) {
        			indexSB.append(r);
        			indexSB.append(" : ");
        		}
        		
        		for(int c = 0; c < rowData.getExcelCellDataList().size(); c++){
        			excelCellDataDTO = rowData.getExcelCellDataList().get(c);
        			cell = row.createCell(c);
        			if(logger.isDebugEnabled()) {
        				if(c > 0) indexSB.append(", ");
        				else indexSB.append("TITLE : ");
        				indexSB.append(c);
            		}
        			setCellData(ExcelSheetDTO.ROW_TYPE_TITLE, excelSheet.getExcelStyleDTO(), cell, excelCellDataDTO, r, c);
        			cellTotalCount++;
        		}
        		r++;
        		
    			if(logger.isDebugEnabled()) {
    				indexSB.append("\n");
        		}
        	}
        	
        	//Column
        	r = excelSheet.getExcelRowTitleList().size();
        	for(ExcelRowDataDTO rowData : excelSheet.getExcelRowDataList()){
        		row = sheet.createRow(r);
        		if(logger.isDebugEnabled()) {
        			indexSB.append(r);
        			indexSB.append(" : ");
        		}
        		for(int c = 0; c < rowData.getExcelCellDataList().size(); c++){
        			excelCellDataDTO = rowData.getExcelCellDataList().get(c);
        			cell = row.createCell(c);
        			if(logger.isDebugEnabled()) {
        				if(c > 0) indexSB.append(", ");
        				else indexSB.append("EDATA : ");
        				indexSB.append(c);
            		}
        			setCellData(ExcelSheetDTO.ROW_TYPE_DATA, excelSheet.getExcelStyleDTO(), cell, excelCellDataDTO, r, c);
        			cellTotalCount++;
        		}
        		r++;
        		
        		if(logger.isDebugEnabled()) {
    				indexSB.append("\n");
        		}
        	}
        	
        	if(logger.isDebugEnabled()) {
        		logger.debug(indexSB.toString());
        	}
        }
        
        if(cellTotalCount > 0) {
        	excelFilePath = wrtieExcel(workbook, workBookDTO);
        }
        

		if (logger.isDebugEnabled()) {
			logger.debug("[END] wrtieXlsx new msexcel path : " + excelFilePath);
		}
		return excelFilePath;
	}
	
	private void setCellData(int rowType, ExcelDefaultStyleDTO defaultStyleDTO, XSSFCell cell, ExcelCellDataDTO cellDTO, int r, int c) {
		
		switch(cellDTO.getCellType()) {
			case ExcelCellDataDTO.CELL_TYPE_BLANK:
				cell.setCellValue((String) cellDTO.getCellValue());
				break;
			case ExcelCellDataDTO.CELL_TYPE_BOOLEAN:
				cell.setCellValue((Boolean) cellDTO.getCellValue());
				break;
			case ExcelCellDataDTO.CELL_TYPE_CALENDAR:
				cell.setCellValue((Calendar) cellDTO.getCellValue());
				break;
			case ExcelCellDataDTO.CELL_TYPE_DATE:
				cell.setCellValue((Date) cellDTO.getCellValue());
				break;
			case ExcelCellDataDTO.CELL_TYPE_FORMULA:
				cell.setCellFormula((String) cellDTO.getCellValue());
				break;
			case ExcelCellDataDTO.CELL_TYPE_NUMERIC:
				cell.setCellValue((Double) cellDTO.getCellValue());
				break;
			case ExcelCellDataDTO.CELL_TYPE_RICH_STRING:
				cell.setCellValue((RichTextString) cellDTO.getCellValue());
				break;
			case ExcelCellDataDTO.CELL_TYPE_STRING:
				cell.setCellValue((String) cellDTO.getCellValue());
				break;
			default :
				throw new ValidateException(new StringBuilder().append((r+1)).append("행, ").append((c+1)).append("열 셀타입 설정이 잘못되었습니다. ").toString());
		}
		        	
		//CellStyle
		if(cellDTO.getCellStyle() != null) {
			cell.setCellStyle(cellDTO.getCellStyle());	
		}
		else {
			if(rowType == ExcelSheetDTO.ROW_TYPE_TITLE) {
				cell.setCellStyle(defaultStyleDTO.getDefaultTitleCellStyle());
			}
			else { 
				if(cellDTO.getCellType() == ExcelCellDataDTO.CELL_TYPE_DATE) {
					cell.setCellStyle(defaultStyleDTO.getDefaultDateCellStyle());
				}
				else {
					cell.setCellStyle(defaultStyleDTO.getDefaultDataCellStyle());
				}
			}
		}
		
		//ErrorValue
		if(cellDTO.getErrorValue() != null) {
			if(byte.class.isAssignableFrom(cellDTO.getErrorValue().getClass())) {
				cell.setCellErrorValue((Byte) cellDTO.getErrorValue());
			}
			else if(FormulaError.class.isAssignableFrom(cellDTO.getErrorValue().getClass())) {
				cell.setCellErrorValue((FormulaError) cellDTO.getErrorValue());
			}	
		}
	
		//CellComment
		if(cellDTO.getCellComment() != null) {
			cell.setCellComment(cellDTO.getCellComment());	
		}
		
		//Hyperlink
		if(cellDTO.getHyperlink() != null) {
			cell.setHyperlink(cellDTO.getHyperlink());	
		}

	}
	
	private String wrtieExcel(XSSFWorkbook workbook, WorkBookDTO workBookDTO) {
		String filePath = null;

		try {
	    	//파일생성
	    	String fixedXSSFExt = ".xlsx";
	    	String fixedHSSFExt = ".xls";
	    	String fileDir = null;
	    	String fileName = null;
	    	if(workBookDTO.getFileDir() != null) {
	    		fileDir = fileUtil.getRealPath(workBookDTO.getFileDir());
	    	}
	    	else {
	    		fileDir = fileUtil.getContextPath().concat(EXCEL_TEMP_REPOSITORY);
	    	}

			if(!fileDir.endsWith(SystemUtil.FILE_SEPARATOR)) {
				fileDir = fileDir.concat(SystemUtil.FILE_SEPARATOR);
			}

			File temporaryDir = new File(fileDir);
	        if(!temporaryDir.exists()) {
	        	temporaryDir.mkdirs();
	        }
	        
	    	if(workBookDTO.getFileName() != null) {
	    		fileName = workBookDTO.getFileName();
	    		if(fileName.toLowerCase().endsWith(fixedHSSFExt)) {
	    			fileName = fileName.substring(0, fileName.lastIndexOf("."));
	    		}
	    		
	    		if(!fileName.toLowerCase().endsWith(fixedXSSFExt)) {
	    			fileName = fileName.concat(fixedXSSFExt);
	    		}
	    		
	    	}
	    	else {
	    		fileName = DateUtil.getFastDate("yyyyMMddHHmmss").concat(IOperateCode.STR_UNDERBAR).concat(UniqueId4j.getHostSeq()).concat(fixedXSSFExt);
	    	}
			
	        filePath = fileDir.concat(fileName); 
			if (logger.isDebugEnabled()) {
				logger.debug(" wrtieExcel new msexcel file path : " + filePath);
			}
			
	        FileOutputStream outFile = null;
			outFile = new FileOutputStream(filePath);
			if(outFile != null) {
				//엑셀 write
				workbook.write(outFile);
				//stream 닫음
				outFile.close();
			}
		} catch (FileNotFoundException e) {
			throw new ApplicationException("작성하려는 엑셀 파일이 존재하지 않습니다.", e);
		} catch (IOException e) {
			throw new ApplicationException("엑셀 내용작성에 실패하였거나 파일종료가 잘못되었습니다.", e);
		} 
		
		return filePath;
	}
	

	public boolean confirmReadCellIndex(int cell, List<Integer> readCellIndex){
		boolean confirm = false;
		if(readCellIndex.size() > 0) {
			for(int i=0; i<readCellIndex.size(); i++) {
				if(readCellIndex.get(i) == cell) {
					confirm = true;
					break;
				}
			}
		}
		return confirm;
	}

	public boolean confirmReadRow(int row, int startRow, int endRow){
		boolean confirm = false;
		if((endRow <= -1 && startRow > -1) && (startRow <= row)) { //startRow만 셋팅
			confirm = true;
		}
		else if((endRow > -1 && startRow <= -1) && (row <= endRow)) { //endRow만 셋팅
			confirm = true;
		}
		else if((startRow > -1 && endRow > -1) && (startRow <= row && row <= endRow)) { //startRow endRow둘다 셋팅
			confirm = true;
		}
		return confirm;
	}

	public boolean confirmSheet(int sheet, int targetSheet){
		boolean confirm = false;
		if(sheet  == targetSheet) {
			confirm = true;
		}
		return confirm;
	}
	
}
