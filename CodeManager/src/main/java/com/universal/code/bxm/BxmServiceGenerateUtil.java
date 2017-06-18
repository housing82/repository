package com.universal.code.bxm;

import japa.parser.ast.Node;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.type.Type;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

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
	
	public static String SC_SIGNATURE_IN;
	public static String SC_SIGNATURE_OUT;
	
	static {

		SC_SIGNATURE_IN = "In";
		SC_SIGNATURE_OUT = "Out";
		
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
		
		OmmDTO scInOmmDTO = null;
		OmmDTO scOutOmmDTO = null;

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
				
				/****************
				 * 설계서에서취합된 자료를 기반으로 빈코드를 생성한다.
				 */
				if(currentDesign != null) {
					logger.debug("★★★★★★★★★★★★★ [START Method Element Setup] ★★★★★★★★★★★★★★");
					logger.debug(currentDesign.toString());
					//logger.debug("[dsImportsSet]\n{}", dsImportsSet);

					//START Method Loop ######################
					for(Entry<String, Map<String, Object>> entry : currentDesign.getCalleeMap().entrySet()) {
						String scMethodName = entry.getKey();
						Map<String, Object> calleeMap = entry.getValue(); 
						
						ProgramDesignDTO scMetdDesign = currentDesign.getMethodDesignMap(scMethodName);
						scOmmName = scMetdDesign.getScNm().concat(getMethodSeq(scMetdDesign.getScNm()));

						logger.debug(">scMethodName: {}.{}", scMetdDesign.getScNm(), scMethodName);
						logger.debug(">calleeMap: {}", calleeMap);
						// SC 메소드 내용
						dsMethodLogicalName = new StringBuilder().append(scMetdDesign.getScMetdLogc()).append(" ").append(GenerateHelper.getMethodVerb(scMetdDesign.getScMetdPref())).toString();
						dsMethodDescription = dsMethodLogicalName;
						dsScModf = scMetdDesign.getScModf();
						
						//output
						dsOutputType = scOmmName.concat(SC_SIGNATURE_OUT);
						String outputOmmPullType = dsPackage.concat(".dto.").concat(dsOutputType);
						dsImportsSet.add(outputOmmPullType); // -> dsImportsSet.add
						dsOutputVariable = IOperateCode.ELEMENT_OUT;
						logger.debug("[OUTPUT] SC Output Name: {} > return: {}, variable: {}", scOmmName, outputOmmPullType, dsOutputVariable);

						
						//Output Type OMM은 생성대상 OMM임 ####################################
						String newScOutOmmPath = new StringBuilder().append(getSourceRoot()).append(IOperateCode.STR_SLASH).append(outputOmmPullType.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).append(".omm").toString();
						logger.debug("## NEW SC Output OMM Path: {}", newScOutOmmPath);
						
						
						//trx code
						dsTrxCode = scMetdDesign.getScNm().concat(scMetdDesign.getTrxSeq());
						//method name
						dsMethodName = scMetdDesign.getScMetdPref().concat(stringUtil.getFirstCharUpperCase(scMetdDesign.getScMetdBody()));
						 
						//input 
						dsInputType = scOmmName.concat(SC_SIGNATURE_IN);
						dsImportsSet.add(dsPackage.concat(".dto.").concat(dsInputType)); // -> dsImportsSet.add
						dsInputVariable = stringUtil.getCharLowerCase(dsInputType, 1);
						String inputOmmPullType = dsPackage.concat(".dto.").concat(dsInputType);
						logger.debug("[INPUT] SC Input Name: {} > parameter: {}, variable: {}", scOmmName, inputOmmPullType, dsInputVariable);
						
						//Input Type OMM은 생성대상 OMM임 ####################################
						String newScInOmmPath = new StringBuilder().append(getSourceRoot()).append(IOperateCode.STR_SLASH).append(inputOmmPullType.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).append(".omm").toString();
						logger.debug("## NEW SC Input OMM Path: {}", newScInOmmPath);
						
						//callee init code
						dsCalleeInit = new StringBuilder();
						//callee biz code
						dsBizCode = new StringBuilder();
						//caller method input field
						dsCelleeInputSetting = new StringBuilder();
						//caller method output field
						dsCellerOutputSetting = new StringBuilder();
						
						dsDeleteExecuteCode = new StringBuilder();
						dsUpdateExecuteCode = new StringBuilder();
						dsInsertExecuteCode = new StringBuilder();
						
						//output omm init
						dsCellerOutputSetting.append("		");
						dsCellerOutputSetting.append(dsOutputVariable);
						dsCellerOutputSetting.append(" = ");
						dsCellerOutputSetting.append("new ");
						dsCellerOutputSetting.append(dsOutputType);
						dsCellerOutputSetting.append("();");
						dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
						
						//new sc in omm
						scInOmmDTO = new OmmDTO();
						scInOmmDTO.setSourceRoot(getSourceRoot());
						scInOmmDTO.setOmmType(inputOmmPullType);
						scInOmmDTO.setOmmDesc(dsMethodLogicalName.concat(" ").concat(SC_SIGNATURE_IN));
						
						//new sc out omm
						scOutOmmDTO = new OmmDTO();
						scOutOmmDTO.setSourceRoot(getSourceRoot());
						scOutOmmDTO.setOmmType(outputOmmPullType);
						scOutOmmDTO.setOmmDesc(dsMethodLogicalName.concat(" ").concat(SC_SIGNATURE_OUT));
						
						logger.debug("-- dsMethodLogicalName: {}", dsMethodLogicalName);
						logger.debug("-- scMethodName: {}", scMethodName);
						
						
						//1개의 메소드 안에서 In/Out 변수 중복제거 및 시퀀스 증가를 위한 맵
						methodVarMap = new LinkedHashMap<String, Integer>();
						//동일한 callee가 여러번 셋팅되었을 경우 채크
						calleeInitMap = new LinkedHashMap<String, Integer>();
						scSubOmmTypeMap = new LinkedHashMap<String, Integer>();

						calleeOutTypeCheck = new LinkedHashMap<String, Boolean>();
						calleeOutVarCheck = new LinkedHashMap<String, Boolean>();
						calleeOutCallCheck = new LinkedHashMap<String, Boolean>();	
						
						
						dsDeleteInListFieldVar = null;
						dsUpdateInListFieldVar = null;
						dsInsertInListFieldVar = null;
						
						for(Entry<String, Object> callee : calleeMap.entrySet()) {
							
							String calleeTypeFullName = callee.getKey();
							String calleeTypeName = calleeTypeFullName.substring(0, calleeTypeFullName.lastIndexOf(IOperateCode.STR_DOT));
							String calleeSimpleName = calleeTypeName.substring(calleeTypeName.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length()); 
							String calleeVarName = stringUtil.getFirstCharLowerCase(calleeSimpleName);
							
							String calleeMethodName = calleeTypeFullName.substring(calleeTypeFullName.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());

							//caller to callee inner getter/setter
							inOmmPropertySetGetter = new StringBuilder();
							
							if(calleeInitMap.get(calleeVarName) == null) {

								//caller method input field
								dsCalleeInit
									.append("		")
									.append(calleeVarName)
									.append(" = DefaultApplicationContext.getBean(")
									.append(calleeVarName)
									.append(", ")
									.append(calleeSimpleName)
									.append(".class);")
									.append(SystemUtil.LINE_SEPARATOR);
								
								calleeInitMap.put(calleeVarName, 1);
							}
							else {
								Integer calleeInitCnt = calleeInitMap.get(calleeVarName); 
								calleeInitMap.put(calleeVarName, (calleeInitCnt + 1));
							}

							logger.debug("---- calleeTypeFullName: {}", calleeTypeFullName);
							logger.debug("  ----- calleeTypeName: {}", calleeTypeName);
							
							//javaPath = new StringBuilder().append(getSourceRoot()).append(IOperateCode.STR_SLASH).append(calleeTypeName.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).append(".java").toString();
							javaPath = generateHelper.findFilePath(getSourceRoot(), calleeTypeName, "java");
							
							logger.debug(" + + parse javaPath: {}", javaPath);
							//피호출 자바파일 분석
							List<Map<String, Object>> ast = visitor.execute(javaPath, ASTVisitor.VISIT_METHOD_NODE, false);
							boolean findMethod = false;
							

							
							//callee method 개수만큼 loop
							for(Map<String, Object> method : ast) {
								if(method.get("nodeType").equals("MethodDeclaration")) {
									
									descMap = (Map<String, Object>) method.get("nodeDesc");
									if(descMap.get("name").equals(calleeMethodName)) {
										logger.debug("method descMap: {}", descMap);
																				
										List<Parameter> parameters = (List<Parameter>) descMap.get("parameters");
										Type calleeReturnType = (Type) descMap.get("returnType");
										List<AnnotationExpr> calleeAnnotations = (List<AnnotationExpr>) descMap.get("annotations");
										
										StringBuilder methodInputExpr = new StringBuilder();
										OmmDTO parseOmm = null;
										
										List<String> saveInTypeSimpleName = new ArrayList<String>();

										
										for(Parameter parameter : parameters) {
											String inputTypeString = parameter.getType().toString();
											String inputVarString = parameter.getId().toString();
											
											//일반 타입일경우
											if(typeUtil.getPrimitiveConvertWrapper(inputTypeString).equals(inputTypeString)) {
												logger.debug("## inputType typeUtil.getPrimitiveConvertWrapper");
												
												if(!inputTypeString.contains(IOperateCode.STR_DOT)) {
													// 패키지가 존재하지 않는경우 패키지를 포함한 타입을 찾음
													String path = generateHelper.findFilePath(getSourceRoot(), inputTypeString, "omm");
													
													if(path != null) {
														path = path.replace(IOperateCode.STR_BACK_SLASH, IOperateCode.STR_SLASH).replace(getSourceRoot(), "");
														path = path.substring(0, path.lastIndexOf(IOperateCode.STR_DOT));
														path = path.replace(IOperateCode.STR_SLASH, IOperateCode.STR_DOT);
														if(path.startsWith(IOperateCode.STR_DOT)) {
															path = path.substring(IOperateCode.STR_DOT.length());
														}
														
														inputTypeString = path;
													}
												}
												
												dsImportsSet.add(inputTypeString); // -> dsImportsSet.add	
											}
											//List일 경우
											else if(inputTypeString.contains(List.class.getCanonicalName()) || inputTypeString.startsWith(List.class.getSimpleName())) {
												logger.debug("## inputType List");
												dsImportsSet.add(List.class.getCanonicalName());
												if(inputTypeString.contains("<") && inputTypeString.contains(">")) {
													String listParam = inputTypeString.substring(inputTypeString.indexOf("<") + "<".length(), inputTypeString.lastIndexOf(">"));
													if(!listParam.equals("?") && typeUtil.getPrimitiveConvertWrapper(listParam).equals(listParam)) {
														dsImportsSet.add(listParam);	
													}
												}
											}
											
											logger.debug("parameter -> {}: {}\n{}", inputTypeString, inputVarString, dsImportsSet);
											
											String calleeInTypeSimpleName = null;
											calleeInTypeSimpleName = inputTypeString.substring(inputTypeString.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
											
											// callee input types 
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
											
											if(scMetdDesign.getScMetdPref().toLowerCase().equals(IOperateCode.METHOD_PREF_SAVE)) {
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
											String ifInOmmPath = new StringBuilder().append(getSourceRoot()).append(IOperateCode.STR_SLASH).append(inputTypeString.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).append(".omm").toString();
											
											File ommFile = new File(ifInOmmPath); 
											if(ommFile.exists()) {
												logger.debug("Exists InOmmPath: {}", ifInOmmPath);
												
												// OMM 파일이 존재하면 분석 실행
												parseOmm = generateHelper.getOmmProperty(ommFile);
												logger.debug("parseInOmm: \n{}", parseOmm.toString());
												
												scInOmmType = ifInOmmPath.substring(0, ifInOmmPath.lastIndexOf(IOperateCode.STR_DOT)).replace(getSourceRoot(), "").replace(IOperateCode.STR_SLASH, IOperateCode.STR_DOT);
												if(scInOmmType.startsWith(IOperateCode.STR_DOT)) {
													scInOmmType = scInOmmType.substring(IOperateCode.STR_DOT.length());
												}
												 
												scInOmmFieldName = stringUtil.getFirstCharLowerCase(scInOmmType.substring(scInOmmType.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length()));
												
												
												
												//OMM Field ( SC가 사용하는 SC의 입력 OMM을 입력 필드로 삼는다. )
												/*
												scInOmmField = new OmmFieldDTO();
												
												if(scInOmmType.contains(".dto.")) {
													//패키지를 현재 SC의 dto패키지로 변경하고 
													scInOmmType = scInOmmDTO.getOmmType().concat("Sub");
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
														
														logger.debug("#InOmm Filed: {} / {} / {}", calleeInOmmField.getType(), calleeInOmmField.getName(), scInOmmDTO.getOmmType());
														if(calleeInOmmField.getType().contains(".dto.")) {
															//패키지를 현재 SC의 dto패키지로 변경하고
															String newSubOmmType = null;
															if(scInOmmDTO.getOmmType().endsWith("In") || scInOmmDTO.getOmmType().endsWith("IO")) {
																newSubOmmType = scInOmmDTO.getOmmType().substring(0, scInOmmDTO.getOmmType().length() - 2); 
															}
															newSubOmmType = generateHelper.getTypeNameNumbering(scSubOmmTypeMap, newSubOmmType.concat("Sub"));
															
															//add import target type
															dsImportsSet.add(newSubOmmType);
															dsImportsSet.add(calleeInOmmField.getType());
															
															//SC In Sub OMM
															String inScFieldSimpleType = generateHelper.getTypeSimpleName(newSubOmmType); //newSubOmmType: sc field omm 타입명
															String inScFieldVarName = "sc".concat(stringUtil.getFirstCharUpperCase(calleeInOmmField.getName()));
															inOmmPropertySetGetter.append("		");
															inOmmPropertySetGetter.append(inScFieldSimpleType); 
															inOmmPropertySetGetter.append(" ");
															inOmmPropertySetGetter.append(inScFieldVarName);  
															inOmmPropertySetGetter.append(" = ");
															inOmmPropertySetGetter.append(dsInputVariable);
															inOmmPropertySetGetter.append(".get");
															inOmmPropertySetGetter.append(stringUtil.getFirstCharUpperCase(calleeInOmmField.getName())); //calleeInOmmField.getName(): sc field 변수명
															inOmmPropertySetGetter.append("();");
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															
															//BC In Sub OMM
															String inBcFieldType = calleeInOmmField.getType().substring(calleeInOmmField.getType().lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());															
															inOmmPropertySetGetter.append("		");
															inOmmPropertySetGetter.append(inBcFieldType);
															inOmmPropertySetGetter.append(" ");
															inOmmPropertySetGetter.append(calleeInOmmField.getName());
															inOmmPropertySetGetter.append(" = ");
															inOmmPropertySetGetter.append("new ");
															inOmmPropertySetGetter.append(inBcFieldType);
															inOmmPropertySetGetter.append("();");
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															
															// SC in is null check
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															inOmmPropertySetGetter.append("		");
															inOmmPropertySetGetter.append("if( ");
															inOmmPropertySetGetter.append(inScFieldVarName);
															inOmmPropertySetGetter.append(" != null ) {");
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															
															//parse bc sub omm 
															File subOmmFile = new File(getSourceRoot().concat(IOperateCode.STR_SLASH).concat(calleeInOmmField.getType().replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).concat(".omm"));
															OmmDTO parseSubOmm = generateHelper.getOmmProperty(subOmmFile);
															
															// display real setter/getter 
															if(parseSubOmm != null && parseSubOmm.getOmmFields() != null && parseSubOmm.getOmmFields().size() > 0) {
																for(OmmFieldDTO calleeInSubOmmField : parseSubOmm.getOmmFields()) {
																	inOmmPropertySetGetter.append("			");
																	inOmmPropertySetGetter.append(generateHelper.getSetterString(calleeInOmmField.getName(), calleeInSubOmmField, inScFieldVarName, calleeInSubOmmField, ";"));
																	inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
																}
															}
															
															inOmmPropertySetGetter.append("		");
															inOmmPropertySetGetter.append("}");
															inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
															
															//new sc omm fieldTypeName
															calleeInOmmField.setType(newSubOmmType);
														}
														
														//in omm setter/getter
														inOmmPropertySetGetter.append("		");
														inOmmPropertySetGetter.append(generateHelper.getSetterString(lowerInTypeSimpleName, calleeInOmmField, null, calleeInOmmField, ";"));
														inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
														inOmmPropertySetGetter.append(SystemUtil.LINE_SEPARATOR);
														
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
											
											
											
											//dsCelleeInputSetting.append(SystemUtil.LINE_SEPARATOR);
											
											//메소드의 입력 표현식
											if(StringUtil.isNotEmpty(methodInputExpr.toString())) {
												methodInputExpr.append(", ");
											}
											methodInputExpr.append(lowerInTypeSimpleName);
										}
										
										//logger.debug("dsCelleeInputSetting: {}", dsCelleeInputSetting.toString());
										//logger.debug("methodInputExpr: {}", methodInputExpr.toString());
										//logger.debug("calleeReturnType: {}", calleeReturnType.toString());
										
										String celleeOutFullType = calleeReturnType.toString();
										boolean isOutListType = false;
										String outArrayReferenceVar = null;
										String outArrayReferenceType = null;
										String celleeOutType = null;
										
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
											/*
											OMM kait.hd.hdu.onl.sc.dto.BHDUCodeAccount01Out
											<description="현장 다건조회 Out">
											{
												Integer outInsertHdAcmastE01<length=9 description="HD_ACMAST_E 등록 결과">;
												kait.hd.hda.onl.dao.dto.DHdCodeAcnt01IO outDHdCodeAcnt01IO<length=0 description="HD_분양_전표_계정 ( HD_CODE_ACNT )">;
												Integer outSelectCountHdCodeAgency01<length=9 description="HD_코드-대행사 전채건수조회 결과">;
												Integer cnt<length=0 description="데이터건수">;
												kait.hd.hdu.onl.sc.dto.BHDUCodeAccount02In dataList<length=0 description="데이터목록" arrayReference="cnt">;
											}
											*/
										}
										else if(celleeOutFullType.contains(".")) {
											celleeOutType = celleeOutFullType.substring(celleeOutFullType.lastIndexOf(".") + ".".length());
										}
										else {
											celleeOutType = celleeOutFullType;
										}
										
										//Callee 결과 변수 중복 체크
										String celleeOutVarName = IOperateCode.ELEMENT_OUT.concat(stringUtil.getFirstCharUpperCase(celleeOutType));
										if(!celleeOutVarName.endsWith("In") && !celleeOutVarName.endsWith("Out") && !celleeOutVarName.endsWith("IO")) {
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
										String ifOutOmmPath = new StringBuilder().append(getSourceRoot()).append(IOperateCode.STR_SLASH).append(celleeOutFullType.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH)).append(".omm").toString();
										File ommFile = new File(ifOutOmmPath); 
										if(ommFile.exists()) {
											logger.debug("Exists OutOmmPath: {}", ifOutOmmPath);

											// Out OMM 파일이 존재하면 분석 실행
											parseOmm = generateHelper.getOmmProperty(ommFile);
											logger.debug("parseOutOmm: \n{}", parseOmm.toString());
											
											
											scOutOmmType = ifOutOmmPath.substring(0, ifOutOmmPath.lastIndexOf(IOperateCode.STR_DOT)).replace(getSourceRoot(), "").replace(IOperateCode.STR_SLASH, IOperateCode.STR_DOT);
											if(scOutOmmType.startsWith(IOperateCode.STR_DOT)) {
												scOutOmmType = scOutOmmType.substring(IOperateCode.STR_DOT.length());
											}
											//scOutOmmFieldName = stringUtil.getFirstCharLowerCase(scOutOmmType.substring(scOutOmmType.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length()));
											
											//OMM Field ( SC가 사용하는 DAO 또는 SC의 입력 OMM을 입력 필드로 삼는다. )
											scOutOmmField = new OmmFieldDTO();
											scOutOmmField.setType(scOutOmmType);
											scOutOmmField.setName(celleeOutVarName);
											scOutOmmField.setLength("0");
											scOutOmmField.setArrayReference(outArrayReferenceVar);
											scOutOmmField.setArrayReferenceType(outArrayReferenceType);
											scOutOmmField.setDescription(parseOmm.getOmmDesc());
											scOutOmmField.setSourceRoot(getSourceRoot());
											scOutOmmDTO.addOmmFields(scOutOmmField);

											if(parseOmm.getOmmFields() != null && parseOmm.getOmmFields().size() > 0) {
												for(OmmFieldDTO calleeOutOmmField : parseOmm.getOmmFields()) {
													dsCellerOutputSetting.append("		");
													dsCellerOutputSetting.append(generateHelper.getSetterString(dsOutputVariable, calleeOutOmmField, dsInputVariable, calleeOutOmmField, ";"));
													dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
													
													scOutOmmDTO.addOmmFields(calleeOutOmmField);
												}
											}
											
											/*
											dsCellerOutputSetting.append("		");
											dsCellerOutputSetting.append(generateHelper.getSetterString(dsOutputVariable, scOutOmmField, null, scOutOmmField, ";"));
											dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
											*/
										}
										else {
											//패키지가 존재하지 않는 타입이거나 primitive 타입일경우
											
											String outFieldDesc = celleeOutType.concat(" 결과");
											for(AnnotationExpr annoExpr : calleeAnnotations) {
												logger.debug("annoExpr.getName(): {}", annoExpr.getName().toString());
												if(annoExpr.getName().toString().equals("BxmCategory")) {

													for(Node node : annoExpr.getChildrenNodes()) {
														logger.debug("-Class: {}", node.getClass());
														if(MemberValuePair.class.isAssignableFrom(node.getClass())) {
															MemberValuePair item = (MemberValuePair) node;
															logger.debug("Anno Name: {}, Value: {}", item.getName(), item.getValue());
															if(item.getName().equals("logicalName")) {
																outFieldDesc = item.getValue().toString();
																if(!outFieldDesc.isEmpty() && outFieldDesc.length() >= 2) {
																	outFieldDesc = outFieldDesc.substring(1, outFieldDesc.length() - 1);
																	outFieldDesc = outFieldDesc.concat(" 결과");
																}
																break;
															}
														}
													}
													
													break;
												}
											}
											
											logger.debug("※※※※※※※※※※※※※※※※※※※※※※※※※# 749 : {}", celleeOutVarName);
											OmmFieldDTO calleeOutOmmField = new OmmFieldDTO();
											calleeOutOmmField.setType(typeUtil.getPrimitiveConvertWrapper(celleeOutType));
											calleeOutOmmField.setName(celleeOutVarName);
											calleeOutOmmField.setLength(typeUtil.getPrimitiveWrapperDefaultLengthMap(celleeOutType));
											calleeOutOmmField.setDescription(outFieldDesc);
											calleeOutOmmField.setArrayReference(outArrayReferenceVar);
											calleeOutOmmField.setArrayReferenceType(outArrayReferenceType);
											calleeOutOmmField.setSourceRoot(getSourceRoot());
											
											dsCellerOutputSetting.append("		");
											dsCellerOutputSetting.append(generateHelper.getSetterString(dsOutputVariable, calleeOutOmmField, null, calleeOutOmmField, ";"));
											dsCellerOutputSetting.append(SystemUtil.LINE_SEPARATOR);
											
											scOutOmmDTO.addOmmFields(calleeOutOmmField);
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
						}
						
						logger.debug("[dsCalleeInit]\n{}", dsCalleeInit.toString()); // ok
						
						logger.debug("[dsCelleeInputSetting]\n{}", dsCelleeInputSetting.toString());
						
						logger.debug("[dsBizCode]\n{}", dsBizCode.toString());
						
						logger.debug("[dsCellerOutputSetting]\n{}", dsCellerOutputSetting.toString());
						
						/***************************
						 * 메소드의 In/Out OMM 생성
						 * save 메소드일경우 Out OMM은 생성하지 않음
						 ***************************/
						boolean inOmmCreated = generateHelper.createOmmFile(scInOmmDTO, true);
						logger.debug("[Create scInOmmDTO] inOmmCreated: {}", inOmmCreated/*, scInOmmDTO.toString()*/);
						
						boolean outOmmCreated = generateHelper.createOmmFile(scOutOmmDTO, true);
						logger.debug("[Create scOutOmmDTO] outOmmCreated: {}", outOmmCreated/*, scOutOmmDTO.toString()*/);
						
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
						
						if(!imports.contains(".dto.")) {
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
					dsImportsSet = new TreeSet<String>();
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
