package com.universal.code.bxm;

import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.type.Type;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.ast.java.ASTVisitor;
import com.universal.code.coder.URLCoder;
import com.universal.code.constants.IOperateCode;
import com.universal.code.dto.OmmDTO;
import com.universal.code.dto.OmmFieldDTO;
import com.universal.code.dto.ProgramDesignDTO;
import com.universal.code.excel.ExcelUtil;
import com.universal.code.excel.dto.ExcelDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.DateUtil;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.PropertyUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;
import com.universal.code.utils.TypeUtil;

public class BxmServiceGenerateUtil {

	private final static Logger logger = LoggerFactory.getLogger(BxmServiceGenerateUtil.class);
	
	private ExcelUtil excelUtil;
	private StringUtil stringUtil;
	private FileUtil fileUtil;
	private PropertyUtil propertyUtil;
	private GenerateHelper generateHelper;
	private ASTVisitor visitor;
	private TypeUtil typeUtil;

	//엑셀에서 참고하는 시트 이름
	private static String templatePath;
	
	private String sourceRoot;
	private boolean createFile;
	private String fileNamePrefix;
	private String excelPath;
	
	private String basePackage;
	private String subPackage;
	private String bxmServiceMethodTemplate;
	private String bxmServiceSaveMethodTemplate;
	private String bxmServiceTemplate;
	
	private static Map<Long, String> indexFieldMap;
	
	private static Map<String, Integer> METHOD_SEQ_MAP;
	

	
	static {
		
		indexFieldMap = new LinkedHashMap<Long, String>();
		METHOD_SEQ_MAP = new HashMap<String, Integer>();
		
		templatePath = URLCoder.getInstance().getURLDecode(BxmDBIOGenerateUtil.class.getResource(IOperateCode.STR_SLASH).getPath().concat("template").concat(IOperateCode.STR_SLASH), "");
		if(templatePath.contains("test-classes")) {
			templatePath = templatePath.replace("test-classes", "classes");
		}
	}
	
	String getMethodSeq(String key) {
		Integer seq = METHOD_SEQ_MAP.get(key);
		if(seq == null) {
			seq = 1;
			METHOD_SEQ_MAP.put(key, 1);
		}
		else {
			seq = seq + 1;
			METHOD_SEQ_MAP.put(key, seq);
		}
		
		return stringUtil.leftPad(Integer.toString(seq), 2, "0");
	}
	
	public BxmServiceGenerateUtil(){
		visitor = new ASTVisitor();
		excelUtil = new ExcelUtil();
		stringUtil = new StringUtil();
		fileUtil = new FileUtil();
		generateHelper = new GenerateHelper();
		propertyUtil = new PropertyUtil();
		typeUtil = new TypeUtil();
		
		//service template
		bxmServiceMethodTemplate = fileUtil.getTextFileContent(templatePath.concat("bxmService.method.template"));
		bxmServiceSaveMethodTemplate = fileUtil.getTextFileContent(templatePath.concat("bxmService.saveMethod.template"));
		bxmServiceTemplate = fileUtil.getTextFileContent(templatePath.concat("bxmService.template"));
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
		// 분석한 엑셀 내용에 따른 BxmService 코드 생성
		
		List<ProgramDesignDTO> programDesignList = new ArrayList<ProgramDesignDTO>();
		ProgramDesignDTO programDesignDTO = null;
		
		List<ExcelDTO> designDataList = null;
		for(String sheetName : GenerateHelper.EXTRACT_SHEET_NAMES) {
			logger.debug("sheetName: {}", sheetName);
			
			if(!GenerateHelper.CPNT_DESIGN_SHEET_NAME.equals(sheetName)) {
				continue;
			}
			
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
						logger.debug("*startRow: {}", startRow);
						start = true;
					}
					
					if(start) {
						//logger.debug("{}:{} value: {}", cell.getRowIndex(), cell.getCellIndex(), cell.getCellValue());
						
						if(cell.getCellIndex() == 0) {
							
							cell.setCellValue(StringUtil.NVL(cell.getCellValue()));
							
							if(programDesignDTO != null) {
								//logger.debug("programDesign[{}]: {}", i, programDesignDTO.toString());
								programDesignList.add(programDesignDTO);
							}
														
							if(cell.getCellValue().equalsIgnoreCase(GenerateHelper.EXCEL_END_FIRST_CELL)) {
								logger.debug("*endRow: {}", cell.getRowIndex());
								break;
							}
							
							programDesignDTO = new ProgramDesignDTO();
							programDesignDTO.setExcelRow(cell.getRowIndex());
						}
						
						fieldName = getFieldName(cell.getCellIndex());
						fieldValue = StringUtil.NVL(cell.getCellValue());
						
						//logger.debug("-- real fieldName: {}, fieldValue: {}", fieldName, fieldValue);
						
						if(StringUtil.isNotEmpty(fieldName)) {
							kindIdx = fieldName.indexOf(IOperateCode.STR_PLUS);
							if(kindIdx > -1) {
								
								mergeCnt = Integer.parseInt(fieldName.substring(kindIdx + IOperateCode.STR_PLUS.length()).trim());
								fieldName = fieldName.substring(0, kindIdx);
								mergeFieldValue = new StringBuilder();
								
								//logger.debug("-i: {}, (i + mergeCnt): {}", i, (i + mergeCnt));
								for(int c = i; c <= (i + mergeCnt); c++) {
									mergeFieldValue.append(StringUtil.NVL(designDataList.get(c).getCellValue()));
								}
								fieldValue = mergeFieldValue.toString();
							}
							//logger.debug(" fieldName: {}, fieldValue: {}", fieldName, fieldValue);
							PropertyUtil.setProperty(programDesignDTO, fieldName, fieldValue);	
						}
					}
					
					if(cell.getCellIndex() == 0) {

						if(cell.getCellValue().equalsIgnoreCase(GenerateHelper.EXCEL_START_FIRST_CELL)) {
							
							extractField(designDataList, (currentRow - 1));
							logger.debug("EXCEL_START_FIRST_CELL: {}", cell.getRowIndex());
							check = true;
							startRow = cell.getRowIndex() + 1;
						}
						
						currentRow++;
					}
					
				}
			}
		}
		
		createCode(programDesignList);
		
		logger.debug("[END] execute");
	}

	private int createCode(List<ProgramDesignDTO> programDesignList) {
		logger.debug("[START] createCode");
		
		int out = 0;
		
		// bxmService file name
		String fileName = null;
		
		// bxmServiceTemplate
		String rvPackage = "#{rvPackage}";
		String rvImports = "#{rvImports}";
		String rvDate = "#{rvDate}";
		String rvLogicalName = "#{rvLogicalName}";
		String rvDescription = "#{rvDescription}";
		String rvClassName = "#{rvClassName}";
		String rvVariables = "#{rvVariables}";
		String rvBody = "#{rvBody}";
		
		// bxmServiceMethodTemplate
		String rvOutputType = "#{rvOutputType}";
		String rvTrxCode = "#{rvTrxCode}";
		String rvMethodName = "#{rvMethodName}";
		String rvInputType = "#{rvInputType}";
		String rvInputVariable = "#{rvInputVariable}";
		
		String rvDeleteInListFieldVar = "#{rvDeleteInListFieldVar}";
		String rvUpdateInListFieldVar = "#{rvUpdateInListFieldVar}";
		String rvInsertInListFieldVar = "#{rvInsertInListFieldVar}";
		
		String rvDeleteInListFieldType = "#{rvDeleteInListFieldType}";
		String rvUpdateInListFieldType = "#{rvUpdateInListFieldType}";
		String rvInsertInListFieldType = "#{rvInsertInListFieldType}";
		
		String rvValidate = "#{rvValidate}";
		
		String rvOutputVariable = "#{rvOutputVariable}";
		String rvCalleeInit = "#{rvCalleeInit}";
		String rvCelleeInputSetting = "#{rvCelleeInputSetting}";
		String rvCellerOutputSetting = "#{rvCellerOutputSetting}";
		
		// bxmServiceSaveMethodTemplate
		String rvDeleteExecuteCode = "#{rvDeleteExecuteCode}";
		String rvUpdateExecuteCode = "#{rvUpdateExecuteCode}";
		String rvInsertExecuteCode = "#{rvInsertExecuteCode}";

		String rvDeleteInFieldVar = "#{rvDeleteInFieldVar}";
		String rvUpdateInFieldVar = "#{rvUpdateInFieldVar}";
		String rvInsertInFieldVar = "#{rvInsertInFieldVar}";
		
		
		String rvScModf = "#{rvScModf}";
		String rvMethodLogicalName = "#{rvMethodLogicalName}";
		String rvMethodDescription = "#{rvMethodDescription}";
		String rvBizCode = "#{rvBizCode}";
		String rvMetdPref = "#{rvMetdPref}";
		
		// bxmServiceSaveMethodTemplate
		String rvInputVariableFirstUpper = "#{rvInputVariableFirstUpper}";

		// code data
		// bxmServiceTemplate
		String dsPackage = getJavaPackage();
		StringBuilder dsImports = null;
		Set<String> dsImportsSet = null;
		String dsDate = null;
		String dsLogicalName = null;
		String dsDescription = null;
		String dsClassName = null;
		StringBuilder dsVariables = null;
		StringBuilder dsBody = null;
		
		// bxmServiceMethodTemplate
		String dsOutputType = null;
		String dsTrxCode = null;
		String dsMethodName = null;
		String dsInputType = null;
		String dsInputVariable = null;
		
		String dsDeleteInListFieldVar = null;
		String dsUpdateInListFieldVar = null;
		String dsInsertInListFieldVar = null;

		String dsDeleteInListFieldType = null;
		String dsUpdateInListFieldType = null;
		String dsInsertInListFieldType = null;
		
		String dsOutputVariable = null;
		StringBuilder dsCalleeInit = null;
		String dsScModf = null;
		String dsMethodLogicalName = null;
		String dsMethodDescription = null;
		StringBuilder dsBizCode = null;
		StringBuilder dsCelleeInputSetting = null;
		
		StringBuilder dsValidate = null;
		
		StringBuilder dsDeleteExecuteCode = null;
		StringBuilder dsUpdateExecuteCode = null;
		StringBuilder dsInsertExecuteCode = null;

		String dsDeleteInFieldVar = null;
		String dsUpdateInFieldVar = null;
		String dsInsertInFieldVar = null;
		
		StringBuilder inOmmPropertySetGetter = null;
		StringBuilder dsCellerOutputSetting = null;
		// bxmServiceSaveMethodTemplate
		String dsInputVariableFirstUpper = null;
		String dsMetdPref = null;
		
		List<OmmDTO> scInOmmDTOList = null;
		OmmDTO scInOmmDTO = null;
		OmmDTO scInSubOmmDTO = null;
		List<OmmDTO> scOutOmmDTOList = null;
		OmmDTO scOutOmmDTO = null;
		OmmDTO scOutSubOmmDTO = null;
		
		String scInOmmType = null;
		String scInOmmFieldName = null;
		String scOutOmmType = null;
		String scOutOmmFieldName = null;
		OmmFieldDTO scInOmmField = null;
		OmmFieldDTO scOutOmmField = null;
		
		String finalBxmServiceTemplate = null;
		
		// copyTarget
		List<String> copyTarget = new ArrayList<String>();
		copyTarget.add("logc");
		copyTarget.add("asisTrxCd");
		copyTarget.add("grnt");
		copyTarget.add("progType");
		copyTarget.add("basePack");
		copyTarget.add("excelRow");
		
		//자바 메소드 분석 인포메이션 
		Map<String, Object> descMap;
		
		//현제 생성하려는 SC데이터
		ProgramDesignDTO currentDesign = null;
		ProgramDesignDTO designRow = null;
		String currentBasePackage = null;
		String scOmmName = null;
		String javaPath = null;
		String compareClasStr = "";
		String compareMetdStr = "";
		String scMetdNm = "";
		
		//1개의 메소드 안에서 In/Out 변수 중복제거 및 시퀀스 증가를 위한 맵
		Map<String, Integer> methodVarMap = null;
		Map<String, Integer> scSubOmmTypeMap = null;
		Map<String, Integer> calleeInitMap = null;
		Map<String, Map<String, Object>> outputSubMap = null;
		
		Map<String, Boolean> calleeOutTypeCheck = null;
		Map<String, Boolean> calleeOutVarCheck = null;
		Map<String, Boolean> calleeOutCallCheck = null;
		
		for(int i = 0; i < programDesignList.size(); i++) {
			designRow = programDesignList.get(i); 
			if(StringUtil.isEmpty(designRow.getDataKind()) && StringUtil.isEmpty(designRow.getBcMetdNm())) {
				continue;
			}
			
			logger.debug(designRow.toString());
			if(designRow.getDataKind().equalsIgnoreCase("N")) {
				logger.debug("Not used data row: {}", designRow.getExcelRow());
				continue;
			}
			else if(designRow.getDataKind().equalsIgnoreCase("P")) {
				//package data
				currentBasePackage = designRow.getBasePack();
			}
			else if(!compareClasStr.equals(designRow.getScNm()) 
				&& designRow.getDataKind().equalsIgnoreCase("M") 
				&& StringUtil.isNotEmpty(designRow.getScNm())
				&& StringUtil.isNotEmpty(designRow.getScMetdPref())
				&& StringUtil.isNotEmpty(designRow.getScMetdBody())) {
				
				// 클래스 단위로 데이터를 추출한다.
				
				/**********************************************
				 * 
				 * 설계서에서취합된 자료를 기반으로 SC코드를 생성한다.
				 * 
				 **********************************************/
				if(currentDesign != null) {
					logger.debug("★★★★★★★★★★★★★ [START Method Element Setup] ★★★★★★★★★★★★★★");
					logger.debug(currentDesign.toString());
					//logger.debug("[dsImportsSet]\n{}", dsImportsSet);

					//START Method Loop ######################
					
					/**********************************************
					 * 
					 * SC 메소드 1개당 호출하는 BC(callee) 메소드 loop 시작
					 * 
					 **********************************************/
					for(Entry<String, Map<String, Object>> entry : currentDesign.getCalleeMap().entrySet()) {
						/** sc method */
						String scMethodName = entry.getKey();
						/** sc method가 호출하는 bc(callee) method map */
						Map<String, Object> calleeMap = entry.getValue(); 
						
						// 현제 루프중인 SC 정보
						ProgramDesignDTO scMetdDesign = currentDesign.getMethodDesignMap(scMethodName);
						// sc 메소드의 I/O OMM이름
						scOmmName = scMetdDesign.getScNm().concat(getMethodSeq(scMetdDesign.getScNm()));

						logger.debug("★START scMethodName: {}.{}", scMetdDesign.getScNm(), scMethodName);
						logger.debug("★START dsMethodLogicalName: {}", dsMethodLogicalName);
						logger.debug("★START calleeMap: {}", calleeMap);
						// sc 메소드 로지컬 이름
						dsMethodLogicalName = new StringBuilder().append(scMetdDesign.getScMetdLogc()).append(" ").append(GenerateHelper.getMethodVerb(scMetdDesign.getScMetdPref())).toString();
						// sc 메소드 설명
						dsMethodDescription = dsMethodLogicalName;
						// sc 메소드의 modifiers
						dsScModf = scMetdDesign.getScModf();
						
						/********************************************
						 * 
						 * sc 메소드의 거래코드 (trx code)
						 * 
						 ********************************************/
						dsTrxCode = scMetdDesign.getScNm().concat(scMetdDesign.getTrxSeq());
						//method name
						dsMethodName = scMetdDesign.getScMetdPref().concat(stringUtil.getFirstCharUpperCase(scMetdDesign.getScMetdBody()));
						dsMetdPref = scMetdDesign.getScMetdPref();
						
						/********************************************
						 * 
						 * sc 메소드 output omm
						 * 
						 ********************************************/
						dsOutputType = scOmmName.concat(GenerateHelper.SIGNATURE_OUT);
						// sc 메소드 output omm full type
						String outputOmmPullType = dsPackage.concat(GenerateHelper.STR_PACKAGE_DOT_DTO).concat(dsOutputType);
						// outputOmmPullType import 구문 추가
						dsImportsSet.add(outputOmmPullType); // -> dsImportsSet.add
						// sc 메소드의 결과 변수명
						dsOutputVariable = IOperateCode.ELEMENT_OUT;
						logger.debug("[OUTPUT] SC Output Name: {} > return: {}, variable: {}", scOmmName, outputOmmPullType, dsOutputVariable);
						
						/********************************************
						 * 
						 * sc 메소드 input omm
						 * 
						 ********************************************/
						dsInputType = scOmmName.concat(GenerateHelper.SIGNATURE_IN);
						// sc 메소드 input omm full type
						String inputOmmPullType = dsPackage.concat(GenerateHelper.STR_PACKAGE_DOT_DTO).concat(dsInputType);
						// inputOmmPullType import 구문 추가
						dsImportsSet.add(inputOmmPullType); // -> dsImportsSet.add
						// sc 메소드 input omm 변수명
						dsInputVariable = stringUtil.getCharLowerCase(dsInputType, 1);
						logger.debug("[INPUT] SC Input Name: {} > parameter: {}, variable: {}", scOmmName, inputOmmPullType, dsInputVariable);
						
						/********************************************
						 * 
						 * sc 메소드 정보 생성용 변수 초기화
						 * 
						 ********************************************/						
						//callee init code
						dsCalleeInit = new StringBuilder();
						//caller method input field
						dsCelleeInputSetting = new StringBuilder();
						//callee biz code
						dsBizCode = new StringBuilder();
						//caller save logic delete code 
						dsDeleteExecuteCode = new StringBuilder();
						//caller save logic update code 
						dsUpdateExecuteCode = new StringBuilder();
						//caller save logic insert code 
						dsInsertExecuteCode = new StringBuilder();
						//caller method output field
						dsCellerOutputSetting = new StringBuilder();
						
						//output omm init SC 결과 초기화
						dsCellerOutputSetting.append("		");
						dsCellerOutputSetting.append(dsOutputVariable);
						dsCellerOutputSetting.append(" = ");
						dsCellerOutputSetting.append("new ");
						dsCellerOutputSetting.append(dsOutputType);
						dsCellerOutputSetting.append("();");
						dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
						
						/********************************************
						 * 
						 * sc 입력 OMM 객체 초기화
						 * 
						 ********************************************/	
						scInOmmDTO = new OmmDTO();
						scInOmmDTO.setSourceRoot(getSourceRoot());
						scInOmmDTO.setOmmType(inputOmmPullType);
						scInOmmDTO.setOmmDesc(dsMethodLogicalName.concat(" ").concat(GenerateHelper.SIGNATURE_IN));
						
						/********************************************
						 * 생성 하려는 입력 OMM과 입력OMM의 SUB OMM을 담을 List객체 초기화
						 ********************************************/
						scInOmmDTOList = new ArrayList<OmmDTO>();
						
						/********************************************
						 * 
						 * sc 결과 OMM 객체 초기화
						 * 
						 ********************************************/
						scOutOmmDTO = new OmmDTO();
						scOutOmmDTO.setSourceRoot(getSourceRoot());
						scOutOmmDTO.setOmmType(outputOmmPullType);
						scOutOmmDTO.setOmmDesc(dsMethodLogicalName.concat(" ").concat(GenerateHelper.SIGNATURE_OUT));
						
						/********************************************
						 * 생성 하려는 결과 OMM과 결과OMM의 SUB OMM을 담을 List객체 초기화
						 ********************************************/
						scOutOmmDTOList = new ArrayList<OmmDTO>();
						
						/********************************************
						 * 
						 * 1개의 메소드 안에서 In/Out 변수 중복제거 및 시퀀스 증가를 위한 맵
						 * 
						 ********************************************/
						// 메소드 변수 중복방지 맵
						methodVarMap = new LinkedHashMap<String, Integer>();
						// 피호출 메소드의 결과 변수 중복방지 맵
						outputSubMap = new LinkedHashMap<String, Map<String, Object>>();
						// 메소드에서 사용하는 I/O OMM에서 신규로 생성해야할 서브OMM의 객체명 방지 맵
						scSubOmmTypeMap = new LinkedHashMap<String, Integer>();
						// 동일한 callee가 여러번 셋팅되었을 경우 채크
						calleeInitMap = new LinkedHashMap<String, Integer>();
						// [미정리]
						calleeOutTypeCheck = new LinkedHashMap<String, Boolean>();
						calleeOutVarCheck = new LinkedHashMap<String, Boolean>();
						calleeOutCallCheck = new LinkedHashMap<String, Boolean>();	
						// SAVE SC 메소드에서 호출하는 피호출 BC메소드에게 넘겨줄 D/U/C 문자열 변수 초기화
						dsDeleteInListFieldVar = null;
						dsUpdateInListFieldVar = null;
						dsInsertInListFieldVar = null;
						
						/********************************************
						 * 
						 * SC 메소드에서 호출 하는 피호출(BC)메소드 loop 시작
						 * 
						 ********************************************/
						for(Entry<String, Object> callee : calleeMap.entrySet()) {
							
							/********************************************
							 * 
							 * 피호출 메소드에 바인드할 OMM의 setter/getter코드를 담을 변수 초기화 ( caller to callee inner getter/setter )
							 * 
							 ********************************************/
							inOmmPropertySetGetter = new StringBuilder();
							
							/** 피호출 클래스의 전체 타입 + 메소드 이름 */
							String calleeTypeFullName = callee.getKey();
							/** 피호출 클래스 전체 타입 */
							String calleeTypeName = calleeTypeFullName.substring(0, calleeTypeFullName.lastIndexOf(IOperateCode.STR_DOT));
							/** 피호출 클래스 간단이름 simpleName */
							String calleeSimpleName = calleeTypeName.substring(calleeTypeName.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
							/** 피호출 클레스 변수명 */
							String calleeVarName = stringUtil.getFirstCharLowerCase(calleeSimpleName);
							/** 피호출 메소드 명 */
							String calleeMethodName = calleeTypeFullName.substring(calleeTypeFullName.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
							
							logger.debug("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
							logger.debug("☆BCStart calleeTypeFullName: {}", calleeTypeFullName);
							logger.debug("☆BCStart calleeTypeName: {}", calleeTypeName);
							logger.debug("☆BCStart calleeSimpleName: {}", calleeSimpleName);
							logger.debug("☆BCStart calleeVarName: {}", calleeVarName);
							logger.debug("☆BCStart calleeMethodName: {}", calleeMethodName);
							logger.debug("☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
							
							if(calleeInitMap.get(calleeVarName) == null) {

								/********************************************
								 * 피호출 BC클래스 초기화 코드 
								 ********************************************/
								dsCalleeInit
									.append("		")
									.append(calleeVarName)
									.append(" = DefaultApplicationContext.getBean(")
									.append(calleeVarName)
									.append(", ")
									.append(calleeSimpleName)
									.append(".class);")
									.append(SystemUtil.LINE_SEPARATOR);
								
								/** 사용된 피호출 BC 변수명을 중복방지맵에 담음  */
								calleeInitMap.put(calleeVarName, 1);
							}
							else {
								/** BC 클래스 초기화는 1회만 수행함으로 코드를 생성하지 않는다. */
								Integer calleeInitCnt = calleeInitMap.get(calleeVarName); 
								calleeInitMap.put(calleeVarName, (calleeInitCnt + 1));
							}
							//bean 초기화코드 로그
							logger.debug("#dsCalleeInit:\n{}", dsCalleeInit.toString());
							
							/** 피호출 클래스(java)가 이미 생성되어있는지와 해당 클래스 자파일의 경로를 찾는다. */
							javaPath = generateHelper.findFilePath(getSourceRoot(), calleeTypeName, "java");
							logger.debug("#parse java path: {}", javaPath);
							
							/** 피호출 자바파일의 메소드정보 분석 (자바파일이 존재하지 않으면 예외를 발생시킴) */
							List<Map<String, Object>> ast = visitor.execute(javaPath, ASTVisitor.VISIT_METHOD_NODE, false);
							boolean findMethod = false;
							
							/** 분석된 피호출 자바의 메소드 갯수 만큼 loop시작 */
							for(Map<String, Object> method : ast) {
								if(method.get("nodeType").equals("MethodDeclaration")) {
									
									/** 메소드 정보 */
									descMap = (Map<String, Object>) method.get("nodeDesc");
									/** 피호출 메소드와 같은 메소드 명이 존재하면 아래 절차 실행 */
									if(descMap.get("name").equals(calleeMethodName)) {
										logger.debug("★callee bean method descMap: {}", descMap);
										/** 피호출 메소드의 입력 파라메터 정보 목록 */
										List<Parameter> parameters = (List<Parameter>) descMap.get("parameters");
										/** 피호출 메소드의 결과 타입 정보 */
										Type calleeReturnType = (Type) descMap.get("returnType");
										/** 피호출 메소드의 어노테이션 정보 목록 */
										List<AnnotationExpr> calleeAnnotations = (List<AnnotationExpr>) descMap.get("annotations");
										/** 피호출 메소드의 입력 표현식을 담을 변수 초기화 */
										StringBuilder methodInputExpr = new StringBuilder();
										OmmDTO parseOmm = null;
										// [미작성]
										List<String> saveInTypeSimpleName = new ArrayList<String>();
										
										/******************************************
										 * 
										 * 피호출 메소드의 입력 파라메터 목록 만큼 loop 시작
										 * 
										 ******************************************/
										for(Parameter parameter : parameters) {
											/** 입력 파라메터 타입 */
											String inputTypeString = parameter.getType().toString();
											logger.debug("#BCInput: {}", inputTypeString);
											/** 입력 파라메터 변수(id) */
											String inputVarString = parameter.getId().toString();
											
											/** 입력 파라메터가 배열타입이 아닐경우 */
											if(typeUtil.getPrimitiveConvertWrapper(inputTypeString).equals(inputTypeString)) {
												logger.debug("## inputType typeUtil.getPrimitiveConvertWrapper");
												
												/** 아래 if절에서 . 이 없는 경우는 inputTypeString 원본 정보에 . 를 포함하지 않은 simpleName정보가 들어있거나 Primitive타입인 경우이다. */
												if(!inputTypeString.contains(IOperateCode.STR_DOT)) {
													/** . 이 없는 simpleName일 경우 omm여부를 판단하기 위해 SourceRoot로 부터 해당 이름을 갖고있는 omm을 찾고있으면 풀타입을 셋팅한다. 없으면   */
													String path = generateHelper.findFilePath(getSourceRoot(), inputTypeString, "omm");
													
													if(path != null) {
														inputTypeString = generateHelper.getExtractPathToJavaType(getSourceRoot(), path);
													}
												}
												
												/** 패키지 정보가 존재하는 타입이면 import대상 코드에 추가한다. */
												if(inputTypeString.contains(IOperateCode.STR_DOT)) {
													dsImportsSet.add(inputTypeString); 
												}
											}
											/** 입력 파라메터가 List 타입일경우 */
											else if(inputTypeString.contains(List.class.getCanonicalName()) || inputTypeString.startsWith(List.class.getSimpleName())) {
												logger.debug("## inputType List");
												
												/** 입력 파라메터가 List 타입일경우 import대상 코드로 추가한다. */
												dsImportsSet.add(List.class.getCanonicalName()); 
												/** List의 ParameterizedType이 존재하는지 체크한다. */
												String listParamType = generateHelper.getTypeStringParameterizedType(inputTypeString);
												if(StringUtil.isNotEmpty(listParamType)) {
													if(!listParamType.equals("?") && typeUtil.getPrimitiveConvertWrapper(listParamType).equals(listParamType)) {
														logger.debug("◆import ParameterizedType: {}", listParamType);
														dsImportsSet.add(listParamType);	
													}
												}
											}
											logger.debug("$<Callee Method Input Parameter> {}: {}", inputTypeString, inputVarString);

											/** 입력 파라메터 타입의 simpleName */
											String calleeInTypeSimpleName = generateHelper.getTypeSimpleName(inputTypeString);
											
											/******************************************
											 * 
											 * 피호출 SAVE 메소드에게 바인드할 변수코드를 작성한다. ( callee input types )  
											 * 
											 ******************************************/
											dsCelleeInputSetting.append("		");
											if(scMetdDesign.getScMetdPref().toLowerCase().equals(IOperateCode.METHOD_PREF_SAVE)) {
												if(calleeOutTypeCheck.get(IOperateCode.METHOD_PREF_DELETE) == null && calleeMethodName.startsWith(IOperateCode.METHOD_PREF_DELETE)) {
													
													dsCelleeInputSetting.append(IOperateCode.CALLEE_VAR_POST_LIST);
													dsCelleeInputSetting.append("<");
													dsCelleeInputSetting.append(calleeInTypeSimpleName);
													dsCelleeInputSetting.append(">");
													
													dsDeleteInListFieldType = calleeInTypeSimpleName;
													
													calleeOutTypeCheck.put(IOperateCode.METHOD_PREF_DELETE, true);
												}
												else if(calleeOutTypeCheck.get(IOperateCode.METHOD_PREF_UPDATE) == null && calleeMethodName.startsWith(IOperateCode.METHOD_PREF_UPDATE)) {

													dsCelleeInputSetting.append(IOperateCode.CALLEE_VAR_POST_LIST);
													dsCelleeInputSetting.append("<");
													dsCelleeInputSetting.append(calleeInTypeSimpleName);
													dsCelleeInputSetting.append(">");
													
													dsUpdateInListFieldType = calleeInTypeSimpleName;
													
													calleeOutTypeCheck.put(IOperateCode.METHOD_PREF_UPDATE, true);
												}
												else if(calleeOutTypeCheck.get(IOperateCode.METHOD_PREF_INSERT) == null && calleeMethodName.startsWith(IOperateCode.METHOD_PREF_INSERT)) {

													dsCelleeInputSetting.append(IOperateCode.CALLEE_VAR_POST_LIST);
													dsCelleeInputSetting.append("<");
													dsCelleeInputSetting.append(calleeInTypeSimpleName);
													dsCelleeInputSetting.append(">");
													
													dsInsertInListFieldType = calleeInTypeSimpleName;
													
													calleeOutTypeCheck.put(IOperateCode.METHOD_PREF_INSERT, true);
												}
												else {
													dsCelleeInputSetting.append(calleeInTypeSimpleName);
												}
											}
											else {
												dsCelleeInputSetting.append(calleeInTypeSimpleName);
											}
											dsCelleeInputSetting.append(" ");
											
											
											String lowerInTypeSimpleName = null;
											String inArrayReferenceVar = null;
											String inArrayReferenceType = null;
											
											/******************************************************************************
											 * 
											 * CREATE SAVE METHOD INPUT PROPERTY
											 * 
											 ******************************************************************************/
											if(scMetdDesign.getScMetdPref().toLowerCase().equals(IOperateCode.METHOD_PREF_SAVE)) {
												logger.debug("#saveInputStep[1] dsCelleeInputSetting:\n{}", dsCelleeInputSetting.toString());
												
												// dsDeleteExecuteCode
												if(calleeOutVarCheck.get(IOperateCode.METHOD_PREF_DELETE) == null && calleeMethodName.startsWith(IOperateCode.METHOD_PREF_DELETE)) {
													// METHOD_PREF_DELETE
													if(inputVarString.equalsIgnoreCase(IOperateCode.ELEMENT_IN)) {
														inputVarString = stringUtil.getFirstCharUpperCase(calleeInTypeSimpleName);
													}
													inputVarString = IOperateCode.METHOD_PREF_DELETE.concat(stringUtil.getFirstCharUpperCase(inputVarString));
													
													lowerInTypeSimpleName = generateHelper.getLowerInTypeSimpleName(methodVarMap, inputVarString, calleeInTypeSimpleName);
													
													inArrayReferenceVar = lowerInTypeSimpleName.concat(IOperateCode.CALLEE_VAR_POST_COUNT);
													inArrayReferenceType = IOperateCode.WRAPPER_TYPE_INTEGER;
													
													lowerInTypeSimpleName = lowerInTypeSimpleName.concat(IOperateCode.CALLEE_VAR_POST_LIST);
													
													dsDeleteInListFieldVar = lowerInTypeSimpleName;
													
													saveInTypeSimpleName.add(lowerInTypeSimpleName);
													
													calleeOutVarCheck.put(IOperateCode.METHOD_PREF_DELETE, true);
												}
												// dsUpdateExecuteCode
												else if(calleeOutVarCheck.get(IOperateCode.METHOD_PREF_UPDATE) == null && calleeMethodName.startsWith(IOperateCode.METHOD_PREF_UPDATE)) {
													// METHOD_PREF_UPDATE
													if(inputVarString.equalsIgnoreCase(IOperateCode.ELEMENT_IN)) {
														inputVarString = stringUtil.getFirstCharUpperCase(calleeInTypeSimpleName);
													}
													inputVarString = IOperateCode.METHOD_PREF_UPDATE.concat(stringUtil.getFirstCharUpperCase(inputVarString));
													
													lowerInTypeSimpleName = generateHelper.getLowerInTypeSimpleName(methodVarMap, inputVarString, calleeInTypeSimpleName);
													
													inArrayReferenceVar = lowerInTypeSimpleName.concat(IOperateCode.CALLEE_VAR_POST_COUNT);
													inArrayReferenceType = IOperateCode.WRAPPER_TYPE_INTEGER;
													
													lowerInTypeSimpleName = lowerInTypeSimpleName.concat(IOperateCode.CALLEE_VAR_POST_LIST);
													
													dsUpdateInListFieldVar = lowerInTypeSimpleName;
													
													saveInTypeSimpleName.add(lowerInTypeSimpleName);
													
													calleeOutVarCheck.put(IOperateCode.METHOD_PREF_UPDATE, true);
												}
												// dsInsertExecuteCode
												else if(calleeOutVarCheck.get(IOperateCode.METHOD_PREF_INSERT) == null && calleeMethodName.startsWith(IOperateCode.METHOD_PREF_INSERT)) {
													// METHOD_PREF_INSERT
													if(inputVarString.equalsIgnoreCase(IOperateCode.ELEMENT_IN)) {
														inputVarString = stringUtil.getFirstCharUpperCase(calleeInTypeSimpleName);
													}
													inputVarString = IOperateCode.METHOD_PREF_INSERT.concat(stringUtil.getFirstCharUpperCase(inputVarString));
													
													lowerInTypeSimpleName = generateHelper.getLowerInTypeSimpleName(methodVarMap, inputVarString, calleeInTypeSimpleName);
													
													inArrayReferenceVar = lowerInTypeSimpleName.concat(IOperateCode.CALLEE_VAR_POST_COUNT);
													inArrayReferenceType = IOperateCode.WRAPPER_TYPE_INTEGER;
													
													lowerInTypeSimpleName = lowerInTypeSimpleName.concat(IOperateCode.CALLEE_VAR_POST_LIST);
													
													dsInsertInListFieldVar = lowerInTypeSimpleName;
													
													saveInTypeSimpleName.add(lowerInTypeSimpleName);
													
													calleeOutVarCheck.put(IOperateCode.METHOD_PREF_INSERT, true);
												}
												// not in (delete, update, insert)
												else {
													lowerInTypeSimpleName = generateHelper.getLowerInTypeSimpleName(methodVarMap, inputVarString, calleeInTypeSimpleName);
												}
											}
											else {
												lowerInTypeSimpleName = generateHelper.getLowerInTypeSimpleName(methodVarMap, inputVarString, calleeInTypeSimpleName);
											}
											
											
											logger.debug("#saveCudCalleeCount: {}\nType: {}\nVar: {}", dsMethodName, calleeOutTypeCheck, calleeOutVarCheck);
											
											//method inner variable name  
											
											//dsCelleeInputSetting.append("\n/*" + methodVarMap+"*/\n");
											dsCelleeInputSetting.append(lowerInTypeSimpleName);
											dsCelleeInputSetting.append(" = ");
											
											//OMM 분석
											//String ifInOmmPath = new StringBuilder().append(getSourceRoot()).append(IOperateCode.STR_SLASH).append(inputTypeString.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).append(".omm").toString();
											String ifInOmmPath = generateHelper.findFilePath(getSourceRoot(), inputTypeString, "omm");
											if(ifInOmmPath != null) {
												logger.debug("Exists InOmmPath: {}", ifInOmmPath);
												File ommFile = new File(ifInOmmPath);
												
												// OMM 파일이 존재하면 분석 실행
												parseOmm = generateHelper.getOmmProperty(ommFile);
												logger.debug("parseInOmm: \n{}", parseOmm.toString());
												
												scInOmmType = ifInOmmPath.substring(0, ifInOmmPath.lastIndexOf(IOperateCode.STR_DOT)).replace(getSourceRoot(), "").replace(IOperateCode.STR_SLASH, IOperateCode.STR_DOT);
												if(scInOmmType.startsWith(IOperateCode.STR_DOT)) {
													scInOmmType = scInOmmType.substring(IOperateCode.STR_DOT.length());
												}

												scInOmmFieldName = stringUtil.getFirstCharLowerCase(scInOmmType.substring(scInOmmType.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length()));
												
												//OMM Field ( SC가 사용하는 BC의 입력 OMM을 입력 필드로 삼는다. )
												/*
												scInOmmField = new OmmFieldDTO();
												
												if(scInOmmType.contains(GenerateHelper.STR_PACKAGE_DOT_DTO)) {
													//패키지를 현재 SC의 dto패키지로 변경하고 
													scInOmmType = scInOmmDTO.getOmmType().concat(GenerateHelper.OMM_SUB_POSTFIX);
												}
												
												scInOmmField.setType(scInOmmType);
												scInOmmField.setName(lowerInTypeSimpleName);
												scInOmmField.setLength("0");
												scInOmmField.setDescription(parseOmm.getOmmDesc());
												scInOmmField.setArrayReference(inArrayReferenceVar);
												scInOmmField.setArrayReferenceType(inArrayReferenceType);
												scInOmmField.setSourceRoot(getSourceRoot());
												scInOmmDTO.addOmmFields(scInOmmField);
												logger.debug("In SC OMM Field : {}", scInOmmField.toString());
												*/
												
												if(parseOmm.getOmmFields() != null && parseOmm.getOmmFields().size() > 0) {
													for(OmmFieldDTO calleeInOmmField : parseOmm.getOmmFields()) {
														
														String inBcFieldType = null;
														String inBcFieldVarName = null;
														
														/*
														 * if(scMetdDesign.getScMetdPref().toLowerCase().equals(IOperateCode.METHOD_PREF_SAVE)) {
														 * 
														 */
														//logger.debug("#InOmm Filed: {} / {} / {}", calleeInOmmField.getType(), calleeInOmmField.getName(), scInOmmDTO.getOmmType());
														/************************************
														 * 배열일 경우 List
														 ************************************/
														if(StringUtil.isNotEmpty(calleeInOmmField.getArrayReference())) {
															
															if(calleeInOmmField.getType().contains(GenerateHelper.STR_PACKAGE_DOT_DTO)) {
																// ParameterizedType이 OMM일 경우
																String newScOmmSubType = generateHelper.getSubOmmType(scSubOmmTypeMap, scInOmmDTO);
																
																//new sc subOmm input 
																scInSubOmmDTO = new OmmDTO();
																scInSubOmmDTO.setSourceRoot(getSourceRoot());
																scInSubOmmDTO.setOmmType(newScOmmSubType);
																scInSubOmmDTO.setOmmDesc(calleeInOmmField.getDescription());
																
																//add import target sub omm type
																dsImportsSet.add(newScOmmSubType); // sc input sub omm type 
																dsImportsSet.add(calleeInOmmField.getType()); // bc input sub omm type
																
																//SC In Sub OMM
																String inScFieldSimpleType = generateHelper.getTypeSimpleName(newScOmmSubType); //newScOmmSubType: sc field omm 타입명
																String inScFieldVarName = IOperateCode.ELEMENT_IN.concat(stringUtil.getFirstCharUpperCase(inScFieldSimpleType)); // service method 내부 변수명
																inScFieldVarName = generateHelper.getListFieldName(inScFieldVarName);
																
																inOmmPropertySetGetter.append("		List<");
																inOmmPropertySetGetter.append(inScFieldSimpleType); 
																inOmmPropertySetGetter.append("> ");
																inOmmPropertySetGetter.append(inScFieldVarName);  
																inOmmPropertySetGetter.append(" = ");
																inOmmPropertySetGetter.append(dsInputVariable);
																inOmmPropertySetGetter.append(".get");
																inOmmPropertySetGetter.append(stringUtil.getFirstCharUpperCase(inScFieldVarName)); //calleeInOmmField.getName(): sc field 변수명
																inOmmPropertySetGetter.append("();");
																inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																
																//BC In Sub OMM
																inBcFieldType = calleeInOmmField.getType().substring(calleeInOmmField.getType().lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
																inBcFieldVarName = generateHelper.getLowerInTypeSimpleName(methodVarMap, calleeInOmmField.getName(), calleeInOmmField.getType());
																inBcFieldVarName = generateHelper.getListFieldName(inBcFieldVarName);
																
																//test
																//inOmmPropertySetGetter.append("// ( "+ calleeInOmmField.getName() +" )" + methodVarMap);
																//inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																
																inOmmPropertySetGetter.append("		List<");
																inOmmPropertySetGetter.append(inBcFieldType);
																inOmmPropertySetGetter.append("> ");
																inOmmPropertySetGetter.append(inBcFieldVarName);
																inOmmPropertySetGetter.append(" = ");
																inOmmPropertySetGetter.append("new ArrayList<");
																inOmmPropertySetGetter.append(inBcFieldType);
																inOmmPropertySetGetter.append(">();");
																inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																
																String listParameterizedTypeName = generateHelper.removeEndsWithList(inBcFieldVarName);
																inOmmPropertySetGetter.append("		");
																inOmmPropertySetGetter.append(inBcFieldType); 
																inOmmPropertySetGetter.append(" ");
																inOmmPropertySetGetter.append(listParameterizedTypeName);  
																inOmmPropertySetGetter.append(" = null;");
																inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																
																// S. SC sub inOmm is null check
																inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																inOmmPropertySetGetter.append("		");
																inOmmPropertySetGetter.append("if( ").append(inScFieldVarName).append(" != null ) {");
																inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																// S. for 
																inOmmPropertySetGetter.append("			");
																inOmmPropertySetGetter.append("for( ").append(inScFieldSimpleType).append(" item : ").append(inScFieldVarName).append(" ) {");
																inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																
																// S. parameterizedType 초기화
																inOmmPropertySetGetter.append("				");
																inOmmPropertySetGetter.append(listParameterizedTypeName);
																inOmmPropertySetGetter.append(" = ");
																inOmmPropertySetGetter.append("new ");
																inOmmPropertySetGetter.append(inBcFieldType);
																inOmmPropertySetGetter.append("();");
																inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																
																//parse bc sub omm 
																File subOmmFile = new File(getSourceRoot().concat(IOperateCode.STR_SLASH).concat(calleeInOmmField.getType().replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).concat(".omm"));
																OmmDTO parseSubOmm = generateHelper.getOmmProperty(subOmmFile);
																
																// display real setter/getter 
																if(parseSubOmm != null && parseSubOmm.getOmmFields() != null && parseSubOmm.getOmmFields().size() > 0) {
																	for(OmmFieldDTO calleeInSubOmmField : parseSubOmm.getOmmFields()) {
																		inOmmPropertySetGetter.append("				");
																		inOmmPropertySetGetter.append(generateHelper.getSetterString(listParameterizedTypeName, calleeInSubOmmField, "item", calleeInSubOmmField, ";"));
																		inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																		
																		scInSubOmmDTO.addOmmFields(calleeInSubOmmField);	
																	}
																}
																
																// list add code
																inOmmPropertySetGetter.append("				");
																inOmmPropertySetGetter.append(inBcFieldVarName);
																inOmmPropertySetGetter.append(".add(");
																inOmmPropertySetGetter.append(listParameterizedTypeName);
																inOmmPropertySetGetter.append(");");
																inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																
																
																// E. for 
																inOmmPropertySetGetter.append("			");
																inOmmPropertySetGetter.append("}");
																inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																// E. SC sub inOmm is null check
																inOmmPropertySetGetter.append("		");
																inOmmPropertySetGetter.append("}");
																inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																
																//new sc method input omm fieldTypeName
																calleeInOmmField.setType(newScOmmSubType);
																//new sc method input omm getter fieldName Change 
																calleeInOmmField.setChangeName(inScFieldVarName);
																//input signature omm
																scInOmmDTOList.add(scInSubOmmDTO);
																
															}
															else {
																// ParameterizedType이 OMM을 제외한 타입일경우
															}
														}
														/************************************
														 * 일 반 DTO(OMM)일 경우
														 ************************************/														
														else if(calleeInOmmField.getType().contains(GenerateHelper.STR_PACKAGE_DOT_DTO)) {

															String newScOmmSubType = generateHelper.getSubOmmType(scSubOmmTypeMap, scInOmmDTO);
															
															//new sc subOmm input 
															scInSubOmmDTO = new OmmDTO();
															scInSubOmmDTO.setSourceRoot(getSourceRoot());
															scInSubOmmDTO.setOmmType(newScOmmSubType);
															scInSubOmmDTO.setOmmDesc(calleeInOmmField.getDescription());
															
															//add import target sub omm type
															dsImportsSet.add(newScOmmSubType); // sc input sub omm type 
															dsImportsSet.add(calleeInOmmField.getType()); // bc input sub omm type
															
															//SC In Sub OMM
															String inScFieldSimpleType = generateHelper.getTypeSimpleName(newScOmmSubType); //newScOmmSubType: sc field omm 타입명
															String inScFieldVarName = IOperateCode.ELEMENT_IN.concat(stringUtil.getFirstCharUpperCase(inScFieldSimpleType)); // service method 내부 변수명
														
															inOmmPropertySetGetter.append("		");
															inOmmPropertySetGetter.append(inScFieldSimpleType); 
															inOmmPropertySetGetter.append(" ");
															inOmmPropertySetGetter.append(inScFieldVarName);  
															inOmmPropertySetGetter.append(" = ");
															inOmmPropertySetGetter.append(dsInputVariable);
															inOmmPropertySetGetter.append(".get");
															inOmmPropertySetGetter.append(stringUtil.getFirstCharUpperCase(inScFieldVarName)); //calleeInOmmField.getName(): sc field 변수명
															inOmmPropertySetGetter.append("();");
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															
															//BC In Sub OMM
															inBcFieldType = calleeInOmmField.getType().substring(calleeInOmmField.getType().lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
															inBcFieldVarName = generateHelper.getLowerInTypeSimpleName(methodVarMap, calleeInOmmField.getName(), calleeInOmmField.getType());

															//test
															//inOmmPropertySetGetter.append("// ( "+ calleeInOmmField.getName() +" )" + methodVarMap);
															//inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															
															inOmmPropertySetGetter.append("		");
															inOmmPropertySetGetter.append(inBcFieldType);
															inOmmPropertySetGetter.append(" ");
															inOmmPropertySetGetter.append(inBcFieldVarName);
															inOmmPropertySetGetter.append(" = ");
															inOmmPropertySetGetter.append("new ");
															inOmmPropertySetGetter.append(inBcFieldType);
															inOmmPropertySetGetter.append("();");
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															
															// S. SC sub inOmm is null check
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															inOmmPropertySetGetter.append("		");
															inOmmPropertySetGetter.append("if( ").append(inScFieldVarName).append(" != null ) {");
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															
															//parse bc sub omm 
															File subOmmFile = new File(getSourceRoot().concat(IOperateCode.STR_SLASH).concat(calleeInOmmField.getType().replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).concat(".omm"));
															OmmDTO parseSubOmm = generateHelper.getOmmProperty(subOmmFile);
															
															// display real setter/getter 
															if(parseSubOmm != null && parseSubOmm.getOmmFields() != null && parseSubOmm.getOmmFields().size() > 0) {
																for(OmmFieldDTO calleeInSubOmmField : parseSubOmm.getOmmFields()) {
																	inOmmPropertySetGetter.append("			");
																	inOmmPropertySetGetter.append(generateHelper.getSetterString(inBcFieldVarName, calleeInSubOmmField, inScFieldVarName, calleeInSubOmmField, ";"));
																	inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																	
																	scInSubOmmDTO.addOmmFields(calleeInSubOmmField);	
																}
															}
															
															// E. SC sub inOmm is null check
															inOmmPropertySetGetter.append("		");
															inOmmPropertySetGetter.append("}");
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															
															//new sc method input omm fieldTypeName
															calleeInOmmField.setType(newScOmmSubType);
															//new sc method input omm getter fieldName Change 
															calleeInOmmField.setChangeName(inScFieldVarName);
															//input signature omm
															scInOmmDTOList.add(scInSubOmmDTO);
														}
														
														//in omm setter/getter
														if(!generateHelper.isArrayReference(parseOmm.getOmmFields(), calleeInOmmField.getName())) {
															inOmmPropertySetGetter.append("		");
															inOmmPropertySetGetter.append(generateHelper.getSetterString(lowerInTypeSimpleName, calleeInOmmField, inBcFieldVarName, ";"));
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);															
														}
														
														scInOmmDTO.addOmmFields(calleeInOmmField);
													}
												}
												
												/*
												dsCelleeInputSetting.append(dsInputVariable);
												dsCelleeInputSetting.append(".get");
												dsCelleeInputSetting.append(stringUtil.getFirstCharUpperCase(lowerInTypeSimpleName));
												dsCelleeInputSetting.append("();");
												if(parameters.size() > 1) {
													dsCelleeInputSetting.append(SystemUtil.LINE_SEPARATOR);
												}
												*/
												// omm일 경우 초기화
												dsCelleeInputSetting.append("new ");
												dsCelleeInputSetting.append(calleeInTypeSimpleName);
												dsCelleeInputSetting.append("();");
												dsCelleeInputSetting.append(SystemUtil.LINE_SEPARATOR);
											}
											else {
												//패키지가 존재하지 않는 타입이거나 primitive 타입일경우
												
												logger.debug("※※※※※※※※※※※※※※※※※※※※※※※※※※");
												logger.debug(inputTypeString);
												
												// omm이 아닐경우 초기화
												dsCelleeInputSetting.append(typeUtil.getPrimitiveWrapperDefaultValue(inputTypeString.substring(inputTypeString.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length())));
												dsCelleeInputSetting.append(";");
												if(parameters.size() > 1) {
													dsCelleeInputSetting.append(SystemUtil.LINE_SEPARATOR);
												}
											}
											
											//메소드의 입력 표현식
											if(StringUtil.isNotEmpty(methodInputExpr.toString())) {
												methodInputExpr.append(", ");
											}
											methodInputExpr.append(lowerInTypeSimpleName);
										}

										logger.debug("####methodName: {}", scMetdDesign.getScMetdPref().concat(scMetdDesign.getScMetdBody()));
										logger.debug("####dsCelleeInputSetting: {}", dsCelleeInputSetting.toString());
										logger.debug("####methodInputExpr: {}", methodInputExpr.toString());
										logger.debug("####calleeReturnType: {}", calleeReturnType.toString());
										logger.debug("#4###inOmmPropertySetGetter: \n{}", inOmmPropertySetGetter.toString());
										
										
										String celleeOutFullType = calleeReturnType.toString();
										boolean isOutListType = false;
										String outArrayReferenceVar = null;
										String outArrayReferenceType = null;
										String celleeOutType = null;
										
										
										// 존재하는 omm인지 채크
										String checkOutType = null;
										if(celleeOutFullType.contains(IOperateCode.STR_DOT)) {
											checkOutType = celleeOutFullType.substring(celleeOutFullType.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
										}
										else {
											checkOutType = celleeOutFullType;
										}
										String outTypeFullPath = generateHelper.findFilePath(getSourceRoot(), checkOutType, "omm");
										if(outTypeFullPath != null) {
											logger.debug("-outTypeFullPath: {}", outTypeFullPath);
											celleeOutFullType = generateHelper.getExtractPathToJavaType(getSourceRoot(), outTypeFullPath);
											// omm 이면 import setting
											dsImportsSet.add(celleeOutFullType);
										}
										
										logger.debug("#celleeOutFullType: {}", celleeOutFullType);
										
										if(celleeOutFullType.contains(List.class.getCanonicalName())) {
											//List일경우 설정 ##############
											String listParam = null;
											if(celleeOutFullType.contains("<") && celleeOutFullType.contains(">")) {
												listParam = celleeOutFullType.substring(celleeOutFullType.indexOf("<") + "<".length(), celleeOutFullType.lastIndexOf(">"));
												if(!listParam.equals("?") && typeUtil.getPrimitiveConvertWrapper(listParam).equals(listParam)) {
													listParam = listParam.substring(listParam.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
												}
												else {
													listParam = "?";
												}
											}
											
											celleeOutType = List.class.getSimpleName().concat("<").concat(listParam).concat(">"); 
											isOutListType = true;

										}
										else if(celleeOutFullType.contains(".")) {
											celleeOutType = celleeOutFullType.substring(celleeOutFullType.lastIndexOf(".") + ".".length());
										}
										else {
											celleeOutType = celleeOutFullType;
										}
										
										//Callee 결과 변수 중복 체크
										String celleeOutVarName = IOperateCode.ELEMENT_OUT.concat(stringUtil.getFirstCharUpperCase(celleeOutType));
										if(!celleeOutVarName.endsWith(GenerateHelper.SIGNATURE_IN) && !celleeOutVarName.endsWith(GenerateHelper.SIGNATURE_OUT) && !celleeOutVarName.endsWith(GenerateHelper.SIGNATURE_IO)) {
											celleeOutVarName = IOperateCode.ELEMENT_OUT.concat(stringUtil.getFirstCharUpperCase(calleeMethodName));
										}
										Integer varCnt = methodVarMap.get(celleeOutVarName);
										
										logger.debug("celleeOutVarName => {}: {}", celleeOutVarName, varCnt);
										if(varCnt != null) {
											// plus
											varCnt = varCnt + 1;
											// make
											// 중복되는 메소드 지역변수 명은 시퀀스를 01 부터 붙인다. 
											celleeOutVarName = celleeOutVarName.concat(stringUtil.leftPad(Integer.toString(varCnt - 1), 2, "0")); 
										}
										else {
											// init
											varCnt = 1;
											// set
											methodVarMap.put(celleeOutVarName, varCnt);
											// make
											// celleeOutVarName = celleeOutVarName.concat(stringUtil.leftPad(Integer.toString(varCnt), 2, "0"));
											// 첫번째 메소드 지역변수 명은 시퀀스를 붙이지 않는다. 
										}
										
										findMethod = true;
										
										if(isOutListType) {
											//타입이 List일경우 변수명 뒤에 List를 붙여줌
											outArrayReferenceVar = celleeOutVarName.concat(IOperateCode.CALLEE_VAR_POST_COUNT);
											outArrayReferenceType = IOperateCode.WRAPPER_TYPE_INTEGER;
											celleeOutVarName = celleeOutVarName.concat(IOperateCode.CALLEE_VAR_POST_LIST);
										}
										
										if(scMetdDesign.getScMetdPref().toLowerCase().equals(IOperateCode.METHOD_PREF_SAVE)) {
											// dsDeleteExecuteCode
											if(calleeOutCallCheck.get(IOperateCode.METHOD_PREF_DELETE) == null && calleeMethodName.startsWith(IOperateCode.METHOD_PREF_DELETE)) {
												
												// celleeOutVarName
												dsDeleteInFieldVar = stringUtil.getFirstCharUpperCase(saveInTypeSimpleName.get(0));
												celleeOutVarName = IOperateCode.CALLE_OUT_VAR_DELETE_COUNT;
												
												if(calleeMethodName.startsWith(IOperateCode.METHOD_PREF_SAVE)) {
													throw new ApplicationException("BXM Code생성기는 저장(SAVE) service메소드에서 다른 save메소드를 callee로 갖는것을 지원하지 않습니다.");
												}
												
												dsDeleteExecuteCode
													.append(calleeVarName)
													.append(IOperateCode.STR_DOT)
													.append(calleeMethodName)
													.append("(item);");
												
												calleeOutCallCheck.put(IOperateCode.METHOD_PREF_DELETE, true);
											}
											// dsUpdateExecuteCode
											else if(calleeOutCallCheck.get(IOperateCode.METHOD_PREF_UPDATE) == null && calleeMethodName.startsWith(IOperateCode.METHOD_PREF_UPDATE)) {
												
												dsUpdateInFieldVar = stringUtil.getFirstCharUpperCase(saveInTypeSimpleName.get(0));
												celleeOutVarName = IOperateCode.CALLE_OUT_VAR_UPDATE_COUNT;
												
												if(calleeMethodName.startsWith(IOperateCode.METHOD_PREF_SAVE)) {
													throw new ApplicationException("BXM Code생성기는 저장(SAVE) service메소드에서 다른 save메소드를 callee로 갖는것을 지원하지 않습니다.");
												}
												
												dsUpdateExecuteCode
													.append(calleeVarName)
													.append(IOperateCode.STR_DOT)
													.append(calleeMethodName)
													.append("(item);");
												
												calleeOutCallCheck.put(IOperateCode.METHOD_PREF_UPDATE, true);
											}
											// dsInsertExecuteCode
											else if(calleeOutCallCheck.get(IOperateCode.METHOD_PREF_INSERT) == null && calleeMethodName.startsWith(IOperateCode.METHOD_PREF_INSERT)) {
												
												dsInsertInFieldVar = stringUtil.getFirstCharUpperCase(saveInTypeSimpleName.get(0));
												celleeOutVarName = IOperateCode.CALLE_OUT_VAR_INSERT_COUNT;
												
												if(calleeMethodName.startsWith(IOperateCode.METHOD_PREF_SAVE)) {
													throw new ApplicationException("BXM Code생성기는 저장(SAVE) service메소드에서 다른 save메소드를 callee로 갖는것을 지원하지 않습니다.");
												}
												
												dsInsertExecuteCode
													.append(calleeVarName)
													.append(IOperateCode.STR_DOT)
													.append(calleeMethodName)
													.append("(item);");
												
												calleeOutCallCheck.put(IOperateCode.METHOD_PREF_INSERT, true);
											}
											// delete, update, insert
											else {

												// /** ### Execute Callee ### */
												if(dsBizCode.toString().length() == 0) {
													dsBizCode
														.append("		/** ### Execute Callee ### */")
														.append(SystemUtil.LINE_SEPARATOR);
												}
												
												dsBizCode
													.append("		")
													.append(celleeOutType)
													.append(" ")
													.append(celleeOutVarName)
													.append(" = ")
													.append(calleeVarName)
													.append(IOperateCode.STR_DOT)
													.append(calleeMethodName)
													.append("(")
													.append(methodInputExpr.toString())
													.append(");")
													.append(SystemUtil.LINE_SEPARATOR);
											}
											
										}
										else {
											
											dsBizCode
												.append("		")
												.append(celleeOutType)
												.append(" ")
												.append(celleeOutVarName)
												.append(" = ")
												.append(calleeVarName)
												.append(IOperateCode.STR_DOT)
												.append(calleeMethodName)
												.append("(")
												.append(methodInputExpr.toString())
												.append(");")
												.append(SystemUtil.LINE_SEPARATOR);
										}
										
										logger.debug("※※※※※※※※※※※※※※※※※※※※※※※※※# 679 : {}", celleeOutFullType);
										logger.debug("※※※※※※※※※※※※※※※※※※※※※※※※※# 680 : {}", celleeOutType);
										logger.debug("※※※※※※※※※※※※※※※※※※※※※※※※※# 681 : {}", celleeOutVarName);
										
										//Callee의 Output Type이 OMM일경우 omm 분석 아닐경우 sc의out 필드로 셋팅
										String ifOutOmmPath = generateHelper.findFilePath(getSourceRoot(), celleeOutFullType, "omm"); 
										//String ifOutOmmPath = new StringBuilder().append(getSourceRoot()).append(IOperateCode.STR_SLASH).append(celleeOutFullType.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).append(".omm").toString();
										 
										if(ifOutOmmPath != null) {
											Map<String, Object> outVarData = new HashMap<String, Object>();
											outVarData.put("kind", "omm");
											outVarData.put("type", ifOutOmmPath);
											outVarData.put("calleeAnnotations", calleeAnnotations);
											outputSubMap.put(celleeOutVarName, outVarData);

										}
										else {
											//패키지가 존재하지 않는 타입이거나 primitive 타입일경우
											
											Map<String, Object> outVarData = new HashMap<String, Object>();
											outVarData.put("kind", "general");
											outVarData.put("type", typeUtil.getPrimitiveConvertWrapper(celleeOutType));
											outVarData.put("calleeAnnotations", calleeAnnotations);
											outputSubMap.put(celleeOutVarName, outVarData);
											
										}
									}
								}
							}
							
							if(!findMethod) {
								logger.error("[ERROR] 설계서에 작성된 피호출자 메소드를 찾을수 없습니다. 자바: {}, 메소드: {}", calleeTypeName, calleeMethodName);
								throw new ApplicationException("설계서에 작성된 피호출자 메소드를 찾을수 없습니다. 자바: {}, 메소드: {}", calleeTypeName, calleeMethodName);
							}
							
							logger.debug("[inOmmPropertySetGetter]\n{}", inOmmPropertySetGetter.toString());
							
							dsCelleeInputSetting.append(inOmmPropertySetGetter.toString());
							dsCelleeInputSetting.append(SystemUtil.LINE_SEPARATOR);
							
							// 생성할 메인 입력  OMM정보를 담는다.
							//input signature omm ( sc method input )
							scInOmmDTOList.add(scInOmmDTO);
						}
						
						/*******************************************************
						 * 
						 * ## OUTPUT OMM START 
						 * 
						 *******************************************************/
						
						/*************************************
						 * 메소드 아웃풋 서브타입을 취합하여 SC의 Output OMM의 서브필드 및 서브 OMM을 정의한다.
						 * outputSubMap
						 * 
						 * CODE: dsCellerOutputSetting 
						 * 
						 * 메인아웃풋: scOutOmmDTO
						 * 메인아웃풋OMM의 서브 필드: scOutSubOmmDTO 
						 * 
						 * [add import target sub omm type]
						 * dsImportsSet.add(newScOmmSubType);
						 */
						
						dsCellerOutputSetting.append("	// [OUT-FIELD] size: " + outputSubMap.size());
						dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
						dsCellerOutputSetting.append("/**");
						dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
						
						OmmFieldDTO referenceField = null;
						//Set<String> arrayReferenceFilter = null;
						List<OmmFieldDTO> generalCalleeOutOmmFields = new ArrayList<OmmFieldDTO>();
						//List<OmmFieldDTO> generalOmmFields = null;
						for(Entry<String, Map<String, Object>> subField : outputSubMap.entrySet()) {
							Map<String, Object> rowMap = subField.getValue();
							logger.debug("[OUT-FIELD] {}: {}", subField.getKey(), rowMap);
							
							dsCellerOutputSetting.append(" [OUT-FIELD] ");
							dsCellerOutputSetting.append(subField.getKey());
							dsCellerOutputSetting.append(": ");
							dsCellerOutputSetting.append(subField.getValue());
							dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
							
							if(((String) rowMap.get("kind")).equalsIgnoreCase("omm")) {
								String ommPath = (String) rowMap.get("type");
								File ommFile = new File(ommPath);
								OmmDTO parseOmm = generateHelper.getOmmProperty(ommFile);
								String calleeOutputType = generateHelper.getPathToTypeName(getSourceRoot(), ommPath);
							
								dsCellerOutputSetting.append("calleeOutputType : ");
								dsCellerOutputSetting.append(calleeOutputType);
								dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
								
								//arrayReferenceFilter = new HashSet<String>();
								//generalOmmFields = new ArrayList<OmmFieldDTO>();
								
								// OMM 필드와 List<ParameterizedType> 필드만 골라낸다.
								for(OmmFieldDTO ommField : parseOmm.getOmmFields()) {
								
									if(ommField == null) continue;

									dsCellerOutputSetting.append("Type: ");
									dsCellerOutputSetting.append(ommField.getType());
									dsCellerOutputSetting.append(", ");
									dsCellerOutputSetting.append("Name: ");
									dsCellerOutputSetting.append(ommField.getName());
									dsCellerOutputSetting.append(", ");
									dsCellerOutputSetting.append("Length: ");
									dsCellerOutputSetting.append(ommField.getLength());
									dsCellerOutputSetting.append(", ");
									dsCellerOutputSetting.append("Description: ");
									dsCellerOutputSetting.append(ommField.getDescription());
									dsCellerOutputSetting.append(", ");
									dsCellerOutputSetting.append("ArrayReference: ");
									dsCellerOutputSetting.append(ommField.getArrayReference());
									dsCellerOutputSetting.append(", ");
									dsCellerOutputSetting.append("ArrayReferenceType: ");
									dsCellerOutputSetting.append(ommField.getArrayReferenceType());
									dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
									
									/****************************
									 * Array List<Object> Field
									 ****************************/
									if(StringUtil.isNotEmpty(ommField.getArrayReference())) {
										/**
										 * 배열 일경우 메인 OUT OMM의 필드로 삼음 (XENA Dataset Marshalling 문제로 인함)
										 */ 
										scOutOmmField = new OmmFieldDTO();
										/* Array Reference Type( ParameterizedType Name ) */
										String newScOmmSubType = null;
										//SC In Sub OMM
										String outScFieldSimpleType = null;
										String outScFieldVarName = null;
										boolean isOmmParameterizedType = false;
										if(ommField.getType().contains(GenerateHelper.STR_PACKAGE_DOT_DTO)) {
											newScOmmSubType = generateHelper.getSubOmmType(scSubOmmTypeMap, scOutOmmDTO);
											//SC In Sub OMM
											outScFieldSimpleType = generateHelper.getTypeSimpleName(newScOmmSubType); //newScOmmSubType: sc field omm 타입명
											outScFieldVarName = IOperateCode.ELEMENT_OUT.concat(stringUtil.getFirstCharUpperCase(outScFieldSimpleType)); // service method 내부 변수명
											isOmmParameterizedType = true;
										}
										else {
											newScOmmSubType = ommField.getType();
											outScFieldSimpleType = ommField.getType();
											outScFieldVarName = ommField.getName();
										}
										
										//여기부터 
										scOutOmmField.setType(newScOmmSubType); 
										scOutOmmField.setName(generateHelper.getListFieldName(outScFieldVarName)); 
										scOutOmmField.setLength("0");
										
										// Find ArrayReference
										referenceField = generateHelper.findOmmField(parseOmm.getOmmFields(), ommField.getArrayReference(), true);
										scOutOmmField.setArrayReferenceField(referenceField);
										if(referenceField != null) {
											logger.debug("#referenceField: {}", referenceField.toString());  
											scOutOmmField.setArrayReference(generateHelper.getCountFieldName(outScFieldVarName));
											scOutOmmField.setArrayReferenceType(referenceField.getType());
										}
										
										scOutOmmField.setDescription(ommField.getDescription());
										scOutOmmField.setSourceRoot(getSourceRoot());
										scOutOmmDTO.addOmmFields(scOutOmmField);
										
										if(isOmmParameterizedType) {
											
											//new sc output subOmm  
											scOutSubOmmDTO = generateHelper.getNewOmmProperty(getSourceRoot(), scOutOmmField, ommField);
											
											//임포트
											dsImportsSet.add(scOutSubOmmDTO.getOmmType());
											//결과 서브 OMM 생성 등록
											scOutOmmDTOList.add(scOutSubOmmDTO);
										}
										
										ommField = null;
									}
									/****************************
									 * Normal OMM Field
									 ****************************/
									else if(ommField.getType().contains(GenerateHelper.STR_PACKAGE_DOT_DTO)) {
										
										/**
										 * 일반 OMM일 경우 해당 타입을 메인 OUT OMM의 필드로 삼음 (XENA Dataset Marshalling 문제로 인함)
										 */
										String newScOmmSubType = generateHelper.getSubOmmType(scSubOmmTypeMap, scOutOmmDTO);
										//SC In Sub OMM
										String outScFieldSimpleType = generateHelper.getTypeSimpleName(newScOmmSubType); //newScOmmSubType: sc field omm 타입명
										String outScFieldVarName = IOperateCode.ELEMENT_OUT.concat(stringUtil.getFirstCharUpperCase(outScFieldSimpleType)); // service method 내부 변수명
									
										logger.debug("#newScOmmSubType: {}", newScOmmSubType);
										scOutOmmField = new OmmFieldDTO();
										scOutOmmField.setType(newScOmmSubType); 
										scOutOmmField.setName(outScFieldVarName); 
										scOutOmmField.setLength("0");
										// Find ArrayReference
										referenceField = generateHelper.findOmmField(parseOmm.getOmmFields(), ommField.getArrayReference(), true);
										scOutOmmField.setArrayReferenceField(referenceField);
										if(referenceField != null) {
											logger.debug("#referenceField: {}", referenceField.toString());
											scOutOmmField.setArrayReference(referenceField.getName());
											scOutOmmField.setArrayReferenceType(referenceField.getType());
										}
										
										scOutOmmField.setDescription(ommField.getDescription());
										scOutOmmField.setSourceRoot(getSourceRoot());

										scOutOmmDTO.addOmmFields(scOutOmmField);

										//new sc output subOmm  
										scOutSubOmmDTO = generateHelper.getNewOmmProperty(getSourceRoot(), scOutOmmField, ommField);
										
										//임포트
										dsImportsSet.add(scOutSubOmmDTO.getOmmType());
										//결과 서브 OMM 생성 등록
										scOutOmmDTOList.add(scOutSubOmmDTO);
										
										ommField = null;
										
									}
								}
								
								/**************************************
								 * 
								 * List 또는 OMM 타입을 제외한 나머지 필드만을 담을  OMM 필드 
								 * 
								 **************************************/
								String newScOmmSubType = generateHelper.getSubOmmType(scSubOmmTypeMap, scOutOmmDTO);
								//SC In Sub OMM
								String outScFieldSimpleType = generateHelper.getTypeSimpleName(newScOmmSubType); //newScOmmSubType: sc field omm 타입명
								String outScFieldVarName = IOperateCode.ELEMENT_OUT.concat(stringUtil.getFirstCharUpperCase(outScFieldSimpleType)); // service method 내부 변수명
															
								scOutOmmField = new OmmFieldDTO();
								scOutOmmField.setType(newScOmmSubType); 
								scOutOmmField.setName(outScFieldVarName);
								scOutOmmField.setLength("0");
								scOutOmmField.setArrayReference(null);
								scOutOmmField.setDescription(parseOmm.getOmmDesc());
								scOutOmmField.setSourceRoot(getSourceRoot());
								
								//new sc output subOmm  
								scOutSubOmmDTO = new OmmDTO();
								scOutSubOmmDTO.setSourceRoot(getSourceRoot());
								scOutSubOmmDTO.setOmmType(scOutOmmField.getType()); 
								scOutSubOmmDTO.setOmmDesc(scOutOmmField.getDescription());

								int subFieldCnt = 0;
								for(OmmFieldDTO ommField : parseOmm.getOmmFields()) {
									
									if(ommField == null) {
										continue;
									}
									else if(generateHelper.isArrayReference(parseOmm.getOmmFields(), ommField.getName())) {
										continue;
									}
									else if(StringUtil.isNotEmpty(ommField.getArrayReference())) {
										continue;
									}
									else if(ommField.getType().contains(GenerateHelper.STR_PACKAGE_DOT_DTO)) {
										continue;
									}
									
									//logger.debug("#Normal-ommField: {}", ommField);
									/*******************************
									 * List 또는 OMM 타입을 제외한 나머지 필드만을 담을  OMM 의 필드 셋팅 
									 *******************************/
									scOutSubOmmDTO.addOmmFields(ommField);
									subFieldCnt++;
								}
								
								if(subFieldCnt > 0) {
									//메인 아웃 OMM의 필드로 등록
									scOutOmmDTO.addOmmFields(scOutOmmField);
									//임포트
									dsImportsSet.add(scOutSubOmmDTO.getOmmType());
									//결과 서브 OMM 생성 등록
									scOutOmmDTOList.add(scOutSubOmmDTO);
								}
							}
							else {
								// general
								
								scOutOmmField = new OmmFieldDTO();
								scOutOmmField.setType((String) rowMap.get("type"));  
								scOutOmmField.setName((String) rowMap.get("name")); 
								scOutOmmField.setLength("0");
								scOutOmmField.setArrayReference(null);
								scOutOmmField.setDescription(generateHelper.getAstMethodAnnoValue((List<AnnotationExpr>) rowMap.get("calleeAnnotations"), "BxmCategory",  "logicalName"));
								scOutOmmField.setSourceRoot(getSourceRoot());
								
								generalCalleeOutOmmFields.add(scOutOmmField);
							}
							
							dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
						}
						
						if(generalCalleeOutOmmFields.size() > 0) {
							
							//new sc output subOmm  
							scOutSubOmmDTO = new OmmDTO();
							scOutSubOmmDTO.setSourceRoot(getSourceRoot());
							scOutSubOmmDTO.setOmmType(scOutOmmField.getType()); //타입 신규생성해야함 seq
							scOutSubOmmDTO.setOmmDesc(scOutOmmField.getDescription());
							
							for(OmmFieldDTO ommField : generalCalleeOutOmmFields) {
								
								// Find ArrayReference
								referenceField = generateHelper.findOmmField(generalCalleeOutOmmFields, ommField.getArrayReference(), true);
								ommField.setArrayReferenceField(referenceField);
								if(referenceField != null) {
									logger.debug("#referenceField: {}", referenceField.toString());
									ommField.setArrayReference(referenceField.getName());
									ommField.setArrayReferenceType(referenceField.getType());
								}
								
								scOutSubOmmDTO.addOmmFields(ommField);
							}
							
							scOutOmmDTOList.add(scOutSubOmmDTO);
						}
						
						dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
						dsCellerOutputSetting.append("*/");
						
						// 생성할 메인 결과 OMM정보를 담는다.
						//output signature omm ( sc method output )
						scOutOmmDTOList.add(scOutOmmDTO);
						
						/*******************************************************
						 * 
						 * ## OUTPUT OMM END 
						 * 
						 *******************************************************/
						
						logger.debug("[dsCalleeInit]\n{}", dsCalleeInit.toString()); // ok
						
						logger.debug("[dsCelleeInputSetting]\n{}", dsCelleeInputSetting.toString());
						
						logger.debug("[dsBizCode]\n{}", dsBizCode.toString());
						
						logger.debug("[dsCellerOutputSetting]\n{}", dsCellerOutputSetting.toString());
						
						/***************************
						 * 메소드의 In/Out OMM 생성
						 ***************************/
						// In
						for(OmmDTO inOmmDTO : scInOmmDTOList) {
							
							boolean inOmmCreated = generateHelper.createOmmFile(inOmmDTO, true);
							logger.debug("[Create scInOmmDTO] inOmmCreated: {}", inOmmCreated/*, scInOmmDTO.toString()*/);
						}
						
						// Out
						for(OmmDTO outOmmDTO : scOutOmmDTOList) {
							
							boolean outOmmCreated = generateHelper.createOmmFile(outOmmDTO, true);
							logger.debug("[Create scOutOmmDTO] outOmmCreated: {}", outOmmCreated/*, scOutOmmDTO.toString()*/);
						}
						
						String methodCode = null;
						
						if(false && scMetdDesign.getScMetdPref().toLowerCase().equals(IOperateCode.METHOD_PREF_SAVE)) {
							
							// stringUtil.getFirstCharUpperCase(dsInputVariable)
							
							if(dsDeleteInFieldVar == null) {
								throw new ApplicationException("프로그램설계서의 저장(SAVE)메소드의 callee 삭제메소드 설정이 존재하지 앖습니다.");
							}
							if(dsUpdateInFieldVar == null) {
								throw new ApplicationException("프로그램설계서의 저장(SAVE)메소드의 callee 수정메소드 설정이 존재하지 앖습니다.");
							}
							if(dsInsertInFieldVar == null) {
								throw new ApplicationException("프로그램설계서의 저장(SAVE)메소드의 callee 입력메소드 설정이 존재하지 앖습니다.");
							}

							logger.debug("#ScMetdMergeStr: {}", scMetdDesign.getScMetdMergeStr());
							
							if(dsDeleteInFieldVar == null || dsUpdateInFieldVar == null|| dsInsertInFieldVar == null 
								|| dsDeleteInListFieldVar == null || dsUpdateInListFieldVar == null|| dsInsertInListFieldVar == null
								|| dsDeleteInListFieldType == null || dsUpdateInListFieldType == null|| dsInsertInListFieldType == null
							) {
								throw new ApplicationException("저장 메소드 {}의 C/R/D callee정보가 존재하지 않습니다 설계서를 확인하세요", scMetdDesign.getScMetdMergeStr());
							}
							
							if(dsValidate == null) dsValidate = new StringBuilder();
							
							methodCode = bxmServiceSaveMethodTemplate
									.replace(rvMethodLogicalName, dsMethodLogicalName)
									.replace(rvMethodDescription, dsMethodDescription)
									.replace(rvScModf, dsScModf)
									.replace(rvOutputType, dsOutputType)
									.replace(rvTrxCode, dsTrxCode)
									.replace(rvMethodName, dsMethodName)
									.replace(rvMetdPref, dsMetdPref)
									.replace(rvInputType, dsInputType)
									.replace(rvInputVariable, dsInputVariable)
									.replace(rvOutputVariable, dsOutputVariable)
									.replace(rvCalleeInit, dsCalleeInit)
									.replace(rvCelleeInputSetting, dsCelleeInputSetting)
									.replace(rvBizCode, dsBizCode)
									.replace(rvCellerOutputSetting, dsCellerOutputSetting)
									.replace(rvDeleteExecuteCode, dsDeleteExecuteCode)
									.replace(rvUpdateExecuteCode, dsUpdateExecuteCode)
									.replace(rvInsertExecuteCode, dsInsertExecuteCode)
									.replace(rvDeleteInFieldVar, dsDeleteInFieldVar)
									.replace(rvUpdateInFieldVar, dsUpdateInFieldVar)
									.replace(rvInsertInFieldVar, dsInsertInFieldVar)
									.replace(rvDeleteInListFieldVar, dsDeleteInListFieldVar)
									.replace(rvUpdateInListFieldVar, dsUpdateInListFieldVar)
									.replace(rvInsertInListFieldVar, dsInsertInListFieldVar)
									.replace(rvDeleteInListFieldType, dsDeleteInListFieldType)
									.replace(rvUpdateInListFieldType, dsUpdateInListFieldType)
									.replace(rvInsertInListFieldType, dsInsertInListFieldType)
									.replace(rvValidate, dsValidate)
									;
						}
						else {
							if(dsValidate == null) dsValidate = new StringBuilder();
							
							methodCode = bxmServiceMethodTemplate
									.replace(rvMethodLogicalName, dsMethodLogicalName)
									.replace(rvMethodDescription, dsMethodDescription)
									.replace(rvScModf, dsScModf)
									.replace(rvOutputType, dsOutputType)
									.replace(rvTrxCode, dsTrxCode)
									.replace(rvMethodName, dsMethodName)
									.replace(rvMetdPref, dsMetdPref)
									.replace(rvInputType, dsInputType)
									.replace(rvInputVariable, dsInputVariable)
									.replace(rvOutputVariable, dsOutputVariable)
									.replace(rvCalleeInit, dsCalleeInit)
									.replace(rvCelleeInputSetting, dsCelleeInputSetting)
									.replace(rvBizCode, dsBizCode)
									.replace(rvCellerOutputSetting, dsCellerOutputSetting)
									.replace(rvValidate, dsValidate)
									;
						}
						
						logger.debug(">>[methodCode]\n\n{}", methodCode);
						
						
						dsBody.append(methodCode);
						dsBody.append(SystemUtil.LINE_SEPARATOR);
						dsBody.append(SystemUtil.LINE_SEPARATOR);
					}
					//END Method Loop ######################
					
					dsImports = new StringBuilder();
					dsVariables = new StringBuilder();
					
					String varTypeName = null;
					for(String imports : dsImportsSet) {
						dsImports.append("import ").append(imports).append(";").append(SystemUtil.LINE_SEPARATOR);
						
						if(!imports.contains(GenerateHelper.STR_PACKAGE_DOT_DTO)) {
							varTypeName = imports.substring(imports.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
							dsVariables.append("	private ").append(varTypeName).append(" ").append(stringUtil.getFirstCharLowerCase(varTypeName)).append(";").append(SystemUtil.LINE_SEPARATOR);
						}
					}
					logger.debug("[dsImports]\n{}", dsImports.toString());
					logger.debug("[dsVariables]\n{}", dsVariables.toString());
					logger.debug("[dsBody]\n{}", dsBody.toString());
					
					//설계서의 가장 마지막 라인 소스는 생성되지 않음으로 ( 가장 마지막라인 [[END]]이전 라인에는 임의의 정보를 입력하도록함 ) 추후 변경
					finalBxmServiceTemplate = bxmServiceTemplate
							.replace(rvPackage, dsPackage)
							.replace(rvImports, dsImports)
							.replace(rvDate, dsDate)
							.replace(rvLogicalName, dsLogicalName)
							.replace(rvDescription, dsDescription)
							.replace(rvClassName, dsClassName)
							.replace(rvVariables, dsVariables)
							.replace(rvBody, dsBody)
							;
					
					logger.debug("★★★★★★★★★★★★★ [END Method Element Setup] ★★★★★★★★★★★★★★");

					String finalSourceDir = new StringBuilder().append(getSourceRoot()).append(IOperateCode.STR_SLASH).append(dsPackage.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).toString();
					String finalSourceFile = new StringBuilder().append(dsClassName).append(".java").toString();
					
					logger.debug("#FinalSource: SC 자바 생성 ");
					logger.debug("#FinalSourcePath: {}/{}", finalSourceDir, finalSourceFile);
					logger.debug("#FinalSource: {}\n\n{}", dsClassName, finalBxmServiceTemplate);
					
					fileUtil.mkfile(
							new StringBuilder().append(getSourceRoot()).append(IOperateCode.STR_SLASH).append(dsPackage.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).toString()
							, finalSourceFile
							, finalBxmServiceTemplate
							, IOperateCode.DEFAULT_ENCODING
							, false
							, true);
 
				}
				
				
				logger.debug("★★★★★★★★★★★★★ [START Class Element Setup] ★★★★★★★★★★★★★★");
				
				if(StringUtil.isEmpty(currentBasePackage)) {
					throw new ApplicationException("베이스 패키지 정보가 존재하지 않습니다. 프로그램설계 문서(엑셀)을 확인하세요.");
				}
				
				designRow.setScModf(StringUtil.NVL(designRow.getScModf(), ""));
				logger.debug("[READY] {} {}", designRow.getScModf(), designRow.getScNm());
				
				// 생성 대상 클래스
				compareClasStr = designRow.getScNm();
				// 생성 대상 클래스+메소드
				compareMetdStr = designRow.getScMetdMergeStr();
				
				currentDesign = new ProgramDesignDTO(); 
				
				propertyUtil.copyProperty(designRow, currentDesign, copyTarget);
				currentDesign.setBasePack(currentBasePackage);
				
				dsDate = DateUtil.getFastDate(DateUtil.DEF_DATE_FORMAT);
				dsLogicalName = designRow.getLogc();
				dsDescription = designRow.getLogc();
				dsClassName = designRow.getScNm();
				fileName = dsClassName.concat(".java");
				dsScModf = StringUtil.NVL(designRow.getScModf()).toLowerCase();
				dsPackage = currentDesign.getBasePack().concat(".").concat(getSubPackage());
				
				dsMethodName = new StringBuilder().append(designRow.getScMetdPref()).append(stringUtil.getFirstCharUpperCase(designRow.getScMetdBody())).toString();
				dsMethodLogicalName = new StringBuilder().append(rvLogicalName).append(" ").append(designRow.getScMetdLogc()).toString();
				dsMethodDescription = dsMethodLogicalName;
				
				logger.debug("dsDate : {}", dsDate);
				logger.debug("dsPackage : {}", dsPackage);
				logger.debug("dsClassName : {}", dsClassName);
				logger.debug("dsLogicalName : {}", dsLogicalName);
				logger.debug("dsDescription : {}", dsDescription);
				logger.debug("fileName : {}", fileName);
				logger.debug("dsScModf : {}", dsScModf);
				
				
				logger.debug("★★★★★★★★★★★★★ [END Class Element Setup] ★★★★★★★★★★★★★★");
				if(StringUtil.isNotEmpty(designRow.getBcMetdNm())) {
					//첫번째 SC메소드
					scMetdNm = designRow.getScMetdPref().concat(designRow.getScMetdBody());
					currentDesign.addCalleeMap(scMetdNm, new LinkedHashMap<String, Object>());
					currentDesign.addMethodDesignMap(scMetdNm, designRow);
					
					//variable init 
					//dsImports = new StringBuilder();
					dsImportsSet = new LinkedHashSet<String>();
					//methods code init
					dsBody = new StringBuilder();
					//import code
					addImportCodeAndCalleeMapSetting(currentDesign.getCalleeMap(scMetdNm), designRow, dsImportsSet, currentBasePackage);
				}
				
				logger.debug("fileName: {}, dsPackage: {}, dsDate: {}", fileName, dsPackage, dsDate);
				
				logger.debug("[Service Method] {}.{}{}", compareClasStr, designRow.getScMetdPref(), designRow.getScMetdBody());
			}
			else if((StringUtil.isEmpty(designRow.getScNm())
					&& StringUtil.isNotEmpty(designRow.getScMetdPref())
					&& StringUtil.isNotEmpty(designRow.getScMetdBody())
					) || compareClasStr.equals(designRow.getScNm())) {
				
				if(StringUtil.isNotEmpty(designRow.getBcMetdNm())) {
					
					// SC 메소드 부분이 빈 로우일경우 DBIO존재하면 해당 SC메소드가 사용하는 DBIO메소드임.
					// compareMetdStr 담긴 생성대상 SC메소드와 동일할경우 설정된 DBIO가 존재하면 해당 SC메소드가 사용하는 DBIO이다.
					logger.debug("SC클래스의 두번째 부터 나오는 SC메소드 추출");
					
					//SC클래스의 두번째 부터 나오는 SC메소드 추출
					scMetdNm = designRow.getScMetdPref().concat(designRow.getScMetdBody());
					//logger.debug("currentDesign: {}", currentDesign);
					//logger.debug("designRow: {}", designRow);
					
					currentDesign.addCalleeMap(scMetdNm, new LinkedHashMap<String, Object>());
					if(StringUtil.isEmpty(designRow.getScNm())) {
						designRow.setScNm(compareClasStr);
					}
					currentDesign.addMethodDesignMap(scMetdNm, designRow);
					
					//import code
					addImportCodeAndCalleeMapSetting(currentDesign.getCalleeMap(scMetdNm), designRow, dsImportsSet, currentBasePackage);
					
					logger.debug("[Service Method] {}.{}{}", compareClasStr, designRow.getScMetdPref(), designRow.getScMetdBody());
				}
			}
			else if((StringUtil.isEmpty(designRow.getScNm())
					&& StringUtil.isEmpty(designRow.getScMetdPref())
					&& StringUtil.isEmpty(designRow.getScMetdBody())
					) || compareMetdStr.equals(designRow.getScMetdMergeStr())) {
				// SC 메소드 부분이 빈 로우일경우 DBIO존재하면 해당 SC메소드가 사용하는 DBIO메소드임.
				// compareMetdStr 담긴 생성대상 SC메소드와 동일할경우 설정된 DBIO가 존재하면 해당 SC메소드가 사용하는 DBIO이다.
				
				if(StringUtil.isNotEmpty(designRow.getBcMetdNm())) {
					logger.debug("SC메소드 내부에서 사용하는 callee");
				
					//import code
					addImportCodeAndCalleeMapSetting(currentDesign.getCalleeMap(scMetdNm), designRow, dsImportsSet, currentBasePackage);
				}
								
				logger.debug("[Service UseAs Callee Method] {}.{}", designRow.getCalleeNm(), designRow.getCalleeMetdNm());
			}
			else {
				logger.debug("- this row({}) continue\n{}", i, designRow.toString());
				continue;
			}
		}

		logger.debug("#FinalSource: 설계서의 가장 마지막 라인 소스는 생성되지 않음으로 ( 가장 마지막라인 [[END]]이전 라인에는 임의의 정보를 입력하도록함 ) 추후 변경");
		
		logger.debug("[END] createCode");
		return out;
	}
	
	
	private void addImportCodeAndCalleeMapSetting(Map<String, Object> scCalleeMap, ProgramDesignDTO designRow, Set<String> dsImportsSet, String currentBasePackage) {
		logger.debug("#scCalleeMap: {}", scCalleeMap);
		logger.debug("##designRow: {}", designRow);
		
		if(StringUtil.isEmpty(designRow.getBcMetdNm()) || designRow.getBcMetdNm().contains(" ")) {
			return;
		}
		
		//import code
		if(StringUtil.isNotEmpty(designRow.getBcNm())) {
			
			if(designRow.getBcNm().contains(IOperateCode.STR_DOT)) {
				
				dsImportsSet.add(designRow.getBcNm());
				/*
				logger.debug("#check: {}", new StringBuilder()
					.append(designRow.getBcNm())
					.append(IOperateCode.STR_DOT)
					.append(designRow.getCalleeMetdNm())
					.toString());
				*/
				scCalleeMap.put(new StringBuilder()
					.append(designRow.getBcNm())
					.append(IOperateCode.STR_DOT)
					.append(designRow.getBcMetdNm())
					.toString()
				, null);

			}
			else {
				String classPrefix = designRow.getBcNm().substring(0, 1);
				
				logger.debug("#designRow.getBcNm(): {}", designRow.getBcNm());
				String calleePath = generateHelper.findFilePath(getSourceRoot(), designRow.getBcNm(), "java");
//				logger.debug("#calleePath: {}", calleePath);
				calleePath = calleePath.substring(getSourceRoot().length() + File.separator.length());
//				logger.debug("#calleePath: {}", calleePath);
				
				String calleePackage = null;
				if(calleePath.contains(File.separator)) {
					calleePackage = calleePath.replace(File.separator, IOperateCode.STR_DOT);
				}
				else if(calleePath.contains(IOperateCode.STR_SLASH)) {
					calleePackage = calleePath.replace(IOperateCode.STR_SLASH, IOperateCode.STR_DOT);
				}
				else {
					calleePackage = calleePath;
				}
//				logger.debug("calleePackage: {}", calleePackage);
				
				if(calleePackage.endsWith(".java")) {
					calleePackage = calleePackage.substring(0, calleePackage.length() - ".java".length());
				}
				
				dsImportsSet.add(new StringBuilder().append(calleePackage).toString());
				
				scCalleeMap.put(new StringBuilder()
						.append(calleePackage)
						.append(IOperateCode.STR_DOT)
						.append(designRow.getBcMetdNm())
						.toString()
					, null);
			}
			
//			logger.debug(">scCalleeMap: {}", scCalleeMap);
		}
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
		BxmServiceGenerateUtil.templatePath = templatePath;
	}

	public String getJavaPackage() {
		if(basePackage == null) {
			throw new ApplicationException("베이스 패키지가 설정되지 않았습니다.");
		}
		if(subPackage == null) {
			throw new ApplicationException("서브 패키지가 설정되지 않았습니다.");
		}
		return new StringBuilder().append(basePackage).append(IOperateCode.STR_DOT).append(subPackage).toString(); 
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
