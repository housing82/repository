package com.universal.code.bxm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.coder.URLCoder;
import com.universal.code.constants.IOperateCode;
import com.universal.code.dto.ProgramDesginDTO;
import com.universal.code.excel.ExcelUtil;
import com.universal.code.excel.dto.ExcelDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.DateUtil;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.PropertyUtil;
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
	private final static List<String> EXTRACT_SHEET_NAMES;
	private static String templatePath;
	
	private String sourceRoot;
	private boolean createFile;
	private String fileNamePrefix;
	private String excelPath;
	
	private String javaPackage;
	private String basePackage;
	private String subPackage;
	private String bxmBeanMethodTemplate;
	private String bxmBeanSaveMethodTemplate;
	private String bxmBeanTemplate;
	
	private static Map<Long, String> indexFieldMap;
	
	static {
		EXCEL_START_FIRST_CELL = "[[START]]";
		EXCEL_END_FIRST_CELL = "[[END]]";
		
		EXTRACT_SHEET_NAMES = new ArrayList<String>();
		EXTRACT_SHEET_NAMES.add("2) DB테이블 DBIO");
		EXTRACT_SHEET_NAMES.add("3) SC,BC 메소드설계");
		
		indexFieldMap = new LinkedHashMap<Long, String>();
		
		templatePath = URLCoder.getInstance().getURLDecode(BxmDBIOGenerateUtil.class.getResource("/").getPath().concat("template").concat("/"), "");
		if(templatePath.contains("test-classes")) {
			templatePath = templatePath.replace("test-classes", "classes");
		}
	}
	
	public BxmBeanGenerateUtil(){
		excelUtil = new ExcelUtil();
		stringUtil = new StringUtil();
		fileUtil = new FileUtil();
		generateHelper = new GenerateHelper();
		
		//bean template
		bxmBeanMethodTemplate = fileUtil.getTextFileContent(templatePath.concat("bxmBean.method.template"));
		bxmBeanSaveMethodTemplate = fileUtil.getTextFileContent(templatePath.concat("bxmBean.saveMethod.template"));
		bxmBeanTemplate = fileUtil.getTextFileContent(templatePath.concat("bxmBean.template"));
	}
	
	public void execute() {
		logger.debug("[START] execute");
		if(getExcelPath() == null) {
			throw new ApplicationException("프로그램 설계 엑셀파일 경로가 설정되지 않았습니다.");
		}
		
		// 이미 캐쉬된 문서인지 확인
		Map<String, List<ExcelDTO>> sheetData = GenerateHelper.getExcelData(getExcelPath());
		// 아닐경우 신규 분석
		if(sheetData == null) {
			//sheetData = new HashMap<String, List<ExcelDTO>>();
			// 설계 엑셀문서 파싱
			 sheetData = excelUtil.readExcel(getExcelPath(), false);
			// 파싱한 문서내용 저장
			GenerateHelper.setExcelData(getExcelPath(), sheetData);
		}
		// 분석한 엑셀 내용에 따른 BxmBean 코드 생성
		
		List<ProgramDesginDTO> programDesginList = new ArrayList<ProgramDesginDTO>();
		ProgramDesginDTO programDesginDTO = null;
		
		List<ExcelDTO> designDataList = null;
		for(String sheetName : EXTRACT_SHEET_NAMES) {
			logger.debug("sheetName: {}", sheetName);
			designDataList = sheetData.get(sheetName);
			
			if(designDataList != null && designDataList.size() > 0) {
				
				boolean start = false;
				boolean check = false;
				long startRow = -1;
				long currentRow = 0;
				ExcelDTO cell = null;
				String fieldName = null;
				String fieldValue = null;
				StringBuilder mergeFieldValue = null;
				int kindIdx = -1;
				int mergeCnt = -1;
				for(int i = 0; i < designDataList.size(); i++) {
					cell = designDataList.get(i);
					
					if(!start && check && cell.getRowIndex() == startRow) {
						start = true;
					}
					
					if(start) {
						//logger.debug("{}:{} value: {}", cell.getRowIndex(), cell.getCellIndex(), cell.getCellValue());
						
						if(cell.getCellIndex() == 0) {
							if(programDesginDTO != null) {
								//logger.debug("programDesgin[{}]: {}", i, programDesginDTO.toString());
								programDesginList.add(programDesginDTO);
							}
							
							if(cell.getCellValue().equalsIgnoreCase(EXCEL_END_FIRST_CELL)) {
								break;
							}
							
							programDesginDTO = new ProgramDesginDTO();
						}
						
						fieldName = getFieldName(cell.getCellIndex());
						fieldValue = cell.getCellValue();
						//logger.debug("-- real fieldName: {}, fieldValue: {}", fieldName, fieldValue);
						
						if(StringUtil.isNotEmpty(fieldName)) {
							kindIdx = fieldName.indexOf(IOperateCode.STR_PLUS);
							if(kindIdx > -1) {
								
								mergeCnt = Integer.parseInt(fieldName.substring(kindIdx + IOperateCode.STR_PLUS.length()).trim());
								fieldName = fieldName.substring(0, kindIdx);
								mergeFieldValue = new StringBuilder();
								
								//logger.debug("-i: {}, (i + mergeCnt): {}", i, (i + mergeCnt));
								for(int c = i; c <= (i + mergeCnt); c++) {
									mergeFieldValue.append(designDataList.get(c).getCellValue());
								}
								fieldValue = mergeFieldValue.toString();
							}
							//logger.debug(" fieldName: {}, fieldValue: {}", fieldName, fieldValue);
							PropertyUtil.setProperty(programDesginDTO, fieldName, fieldValue);	
						}
					}
					
					if(cell.getCellIndex() == 0) {

						if(cell.getCellValue().equalsIgnoreCase(EXCEL_START_FIRST_CELL)) {
							
							extractField(designDataList, (currentRow - 1));
							
							check = true;
							startRow = cell.getRowIndex() + 1;
						}
						
						currentRow++;
					}
					
				}
			}
		}
		
		createCode(programDesginList);
		
		logger.debug("[END] execute");
	}

	private int createCode(List<ProgramDesginDTO> programDesginList) {
		int out = 0;
		
		// bxmBean file name
		String fileName = null;
		
		// bxmBeanTemplate
		String rvPackage = "#{rvPackage}";
		String rvImports = "#{rvImports}";
		String rvDate = "#{rvDate}";
		String rvLogicalName = "#{rvLogicalName}";
		String rvDescription = "#{rvDescription}";
		String rvClassName = "#{rvClassName}";
		String rvBody = "#{rvBody}";
		
		// bxmBeanMethodTemplate
		String rvOutputType = "#{rvOutputType}";
		String rvMethodName = "#{rvMethodName}";
		String rvInputType = "#{rvInputType}";
		String rvInputVariable = "#{rvInputVariable}";
		String rvOutputVariable = "#{rvOutputVariable}";
		String rvDbioInit = "#{rvDbioInit}";
		String rvBcModf = "#{rvBcModf}";
		String rvMethodLogicalName = "#{rvMethodLogicalName}";
		String rvMethodDescription = "#{rvMethodDescription}";
		
		// bxmBeanSaveMethodTemplate
		String rvInputVariableFirstUpper = "#{rvInputVariableFirstUpper}";

		// code data
		// bxmBeanTemplate
		String dsPackage = getJavaPackage();
		String dsImports = null;
		String dsDate = null;
		String dsLogicalName = null;
		String dsDescription = null;
		String dsClassName = null;
		String dsBody = null;
		
		// bxmBeanMethodTemplate
		String dsOutputType = null;
		String dsMethodName = null;
		String dsInputType = null;
		String dsInputVariable = null;
		String dsOutputVariable = null;
		String dsDbioInit = null;
		String dsBcModf = null;
		String dsMethodLogicalName = null;
		String dsMethodDescription = null;
		
		// bxmBeanSaveMethodTemplate
		String dsInputVariableFirstUpper = null;
		
		for(ProgramDesginDTO desgin : programDesginList) { 
			logger.debug(desgin.toString());
			
			
			dsDate = DateUtil.getFastDate(DateUtil.DEF_DATE_FORMAT);
			rvLogicalName = desgin.getLogc();
			rvDescription = desgin.getLogc();
			rvClassName = desgin.getBcNm();
			fileName = rvClassName.concat(".java");
			dsBcModf = StringUtil.NVL(desgin.getBcModf(), "public").toLowerCase();
			
			
			dsMethodName = new StringBuilder().append(desgin.getBcMetdPref()).append(stringUtil.getFirstCharUpperCase(desgin.getBcMetdBody())).toString();
			dsMethodLogicalName = new StringBuilder().append(rvLogicalName).append(" ").append(desgin.getBcMetdLogc()).toString();
			dsMethodDescription = dsMethodLogicalName;
			
			logger.debug("fileName: {}, rvPackage: {}, dsDate: {}", fileName, dsPackage, dsDate);
			
			
		}
		

		
		
		
		return out;
	}
	
	private void extractField(List<ExcelDTO> designDataList, long currentRow) {
		boolean find = false;
		for(ExcelDTO excelDTO : designDataList) {
			if(currentRow == excelDTO.getRowIndex()) {
				setFieldName(excelDTO.getCellIndex(), excelDTO.getCellValue());
				find = true;
			}
			if(find && currentRow < excelDTO.getRowIndex()) {
				break;
			}
		}
	}
	
	void setFieldName(long key, String value) {
		indexFieldMap.put(key, value);
	}
	
	String getFieldName(long key) {
		return indexFieldMap.get(key);
	}
	
	public String getSourceRoot() {
		return sourceRoot;
	}

	public void setSourceRoot(String sourceRoot) {
		this.sourceRoot = sourceRoot;
	}
	
	public boolean isCreateFile() {
		return createFile;
	}

	public void setCreateFile(boolean createFile) {
		this.createFile = createFile;
	}

	public String getFileNamePrefix() {
		return (fileNamePrefix == null ? "" : fileNamePrefix);
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	public String getExcelPath() {
		return excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}


	public static String getTemplatePath() {
		return templatePath;
	}

	public static void setTemplatePath(String templatePath) {
		BxmBeanGenerateUtil.templatePath = templatePath;
	}

	public String getJavaPackage() {
		if(basePackage == null) {
			throw new ApplicationException("베이스 패키지가 설정되지 않았습니다.");
		}
		if(subPackage == null) {
			throw new ApplicationException("서브 패키지가 설정되지 않았습니다.");
		}
		return new StringBuilder().append(basePackage).append(".").append(subPackage).toString(); 
	}


	public String getSubPackage() {
		return subPackage;
	}

	public void setSubPackage(String subPackage) {
		this.subPackage = subPackage;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	
}
