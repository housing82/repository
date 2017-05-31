package com.universal.code.bxm;

import japa.parser.ast.body.Parameter;
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

public class BxmBeanGenerateUtil {

	private final static Logger logger = LoggerFactory.getLogger(BxmBeanGenerateUtil.class);
	
	private ExcelUtil excelUtil;
	private StringUtil stringUtil;
	private FileUtil fileUtil;
	private PropertyUtil propertyUtil;
	private GenerateHelper generateHelper;
	private ASTVisitor visitor;
	private TypeUtil typeUtil;
	
	private final static String EXCEL_START_FIRST_CELL;
	private final static String EXCEL_END_FIRST_CELL;

	//엑셀에서 참고하는 시트 이름
//	private final static List<String> EXTRACT_SHEET_NAMES;
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
	
	private static Map<String, Integer> METHOD_SEQ_MAP;
	
	static {
		EXCEL_START_FIRST_CELL = "[[START]]";
		EXCEL_END_FIRST_CELL = "[[END]]";
		
//		EXTRACT_SHEET_NAMES = new ArrayList<String>();
//		EXTRACT_SHEET_NAMES.add("2) DB테이블 DBIO");
//		EXTRACT_SHEET_NAMES.add("3) SC,BC 메소드설계");
		
		indexFieldMap = new LinkedHashMap<Long, String>();
		METHOD_SEQ_MAP = new HashMap<String, Integer>();
		
		templatePath = URLCoder.getInstance().getURLDecode(BxmDBIOGenerateUtil.class.getResource("/").getPath().concat("template").concat("/"), "");
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
	
	public BxmBeanGenerateUtil(){
		visitor = new ASTVisitor();
		excelUtil = new ExcelUtil();
		stringUtil = new StringUtil();
		fileUtil = new FileUtil();
		generateHelper = new GenerateHelper();
		propertyUtil = new PropertyUtil();
		typeUtil = new TypeUtil();
		
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
							if(programDesignDTO != null) {
								//logger.debug("programDesign[{}]: {}", i, programDesignDTO.toString());
								programDesignList.add(programDesignDTO);
							}
							
							if(cell.getCellValue().equalsIgnoreCase(EXCEL_END_FIRST_CELL)) {
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

						if(cell.getCellValue().equalsIgnoreCase(EXCEL_START_FIRST_CELL)) {
							
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
		
		// bxmBean file name
		String fileName = null;
		
		// bxmBeanTemplate
		String rvPackage = "#{rvPackage}";
		String rvImports = "#{rvImports}";
		String rvDate = "#{rvDate}";
		String rvLogicalName = "#{rvLogicalName}";
		String rvDescription = "#{rvDescription}";
		String rvClassName = "#{rvClassName}";
		String rvVariables = "#{rvVariables}";
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
		String rvBizCode = "#{rvBizCode}";
		
		// bxmBeanSaveMethodTemplate
		String rvInputVariableFirstUpper = "#{rvInputVariableFirstUpper}";

		// code data
		// bxmBeanTemplate
		String dsPackage = getJavaPackage();
		StringBuilder dsImports = null;
		Set<String> dsImportsSet = null;
		String dsDate = null;
		String dsLogicalName = null;
		String dsDescription = null;
		String dsClassName = null;
		StringBuilder dsVariables = null;
		String dsBody = null;
		
		// bxmBeanMethodTemplate
		String dsOutputType = null;
		String dsMethodName = null;
		String dsInputType = null;
		String dsInputVariable = null;
		String dsOutputVariable = null;
		StringBuilder dsDbioInit = null;
		String dsBcModf = null;
		String dsMethodLogicalName = null;
		String dsMethodDescription = null;
		StringBuilder dsBizCode = null;
		StringBuilder celleeInputVar = null;
		
		// bxmBeanSaveMethodTemplate
		String dsInputVariableFirstUpper = null;
		
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
		
		//현제 생성하려는 BC데이터
		ProgramDesignDTO currentDesign = null;
		ProgramDesignDTO designRow = null;
		String currentBasePackage = null;
		String bcOmmName = null;
		String javaPath = null;
		String compareClasStr = "";
		String compareMetdStr = "";
		String bcMetdNm = "";
		
		for(int i = 0; i < programDesignList.size(); i++) {
			designRow = programDesignList.get(i); 
			logger.debug(designRow.toString());
			if(designRow.getDataKind().equalsIgnoreCase("N")) {
				logger.debug("Not used data row: {}", designRow.getExcelRow());
				continue;
			}
			else if(designRow.getDataKind().equalsIgnoreCase("P")) {
				//package data
				currentBasePackage = designRow.getBasePack();
			}
			else if(!compareClasStr.equals(designRow.getBcNm()) 
				&& designRow.getDataKind().equalsIgnoreCase("M") 
				&& StringUtil.isNotEmpty(designRow.getBcNm())
				&& StringUtil.isNotEmpty(designRow.getBcMetdPref())
				&& StringUtil.isNotEmpty(designRow.getBcMetdBody())) {
				
				// 클래스 단위로 데이터를 추출한다.
				
				/****************
				 * 설계서에서취합된 자료를 기반으로 빈코드를 생성한다.
				 */
				if(currentDesign != null) {
					logger.debug("★★★★★★★★★★★★★ [START Method Element Setup] ★★★★★★★★★★★★★★");
					logger.debug(currentDesign.toString());
					//logger.debug("[dsImportsSet]\n{}", dsImportsSet);

					for(Entry<String, Map<String, Object>> entry : currentDesign.getCalleeMap().entrySet()) {
						String bcMethodName = entry.getKey();
						Map<String, Object> calleeMap = entry.getValue(); 
						
						ProgramDesignDTO bcMetdDesign = currentDesign.getMethodDesignMap(bcMethodName);
						bcOmmName = bcMetdDesign.getBcNm().concat(getMethodSeq(bcMetdDesign.getBcNm()));
						// BC 메소드 내용
						dsMethodLogicalName = new StringBuilder().append(bcMetdDesign.getBcMetdLogc()).append(" ").append(GenerateHelper.getMethodVerb(bcMetdDesign.getBcMetdPref())).toString();
						dsMethodDescription = dsMethodLogicalName;
						dsBcModf = bcMetdDesign.getBcModf();
						
						//output
						dsOutputType = bcOmmName.concat("Out");
						dsImportsSet.add(dsPackage.concat(".dto.").concat(dsOutputType)); // -> dsImportsSet.add
						logger.debug("bcOmmName: {} > {}", bcOmmName, dsPackage.concat(".dto.").concat(dsOutputType));
						dsOutputVariable = IOperateCode.ELEMENT_OUT;
						
						//method name
						dsMethodName = bcMetdDesign.getBcMetdPref().concat(stringUtil.getFirstCharUpperCase(bcMetdDesign.getBcMetdBody()));
						 
						//input 
						dsInputType = bcOmmName.concat("In");
						dsImportsSet.add(dsPackage.concat(".dto.").concat(dsInputType)); // -> dsImportsSet.add
						logger.debug("bcOmmName: {} > {}", bcOmmName, dsPackage.concat(".dto.").concat(dsInputType));
						dsInputVariable = stringUtil.getFirstCharLowerCase(dsInputType);
						
						//callee init code
						dsDbioInit = new StringBuilder();
						//callee biz code
						dsBizCode = new StringBuilder();
						//caller method input field
						celleeInputVar = new StringBuilder();
						
						logger.debug("-- dsMethodLogicalName: {}", dsMethodLogicalName);
						logger.debug("-- bcMethodName: {}", bcMethodName);
						
						for(Entry<String, Object> callee : calleeMap.entrySet()) {
							
							String calleeTypeFullName = callee.getKey();
							String calleeTypeName = calleeTypeFullName.substring(0, calleeTypeFullName.lastIndexOf(IOperateCode.STR_DOT));
							String calleeSimpleName = calleeTypeName.substring(calleeTypeName.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length()); 
							String calleeVarName = stringUtil.getFirstCharLowerCase(calleeSimpleName);
							
							String calleeMethodName = calleeTypeFullName.substring(calleeTypeFullName.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
							
							dsDbioInit
								.append("		")
								.append(calleeVarName)
								.append(" = DefaultApplicationContext.getBean(")
								.append(calleeVarName)
								.append(", ")
								.append(calleeSimpleName)
								.append(".class);")
								.append(SystemUtil.LINE_SEPARATOR);
							
							logger.debug("---- calleeTypeFullName: {}", calleeTypeFullName);
							logger.debug("  ----- calleeTypeName: {}", calleeTypeName);
							
							javaPath = new StringBuilder().append(getSourceRoot()).append("/").append(calleeTypeName.replace(IOperateCode.STR_DOT, "/")).append(".java").toString();
							logger.debug(" + + parse javaPath: {}", javaPath);
							List<Map<String, Object>> ast = visitor.execute(javaPath, ASTVisitor.VISIT_METHOD_NODE, false);
							boolean findMethod = false;
							for(Map<String, Object> method : ast) {
								if(method.get("nodeType").equals("MethodDeclaration")) {
									
									descMap = (Map<String, Object>) method.get("nodeDesc");
									if(descMap.get("name").equals(calleeMethodName)) {
										logger.debug("method descMap: {}", descMap);

										List<Parameter> parameters = (List<Parameter>) descMap.get("parameters");
										Type calleeReturnType = (Type) descMap.get("returnType");
										
										StringBuilder methodInputExpr = new StringBuilder();
										Map<String, Integer> methodVarMap = new HashMap<String, Integer>();
										
										for(Parameter parameter : parameters) {
											String inputTypeString = parameter.getType().toString();
											
											dsImportsSet.add(inputTypeString); // -> dsImportsSet.add
											logger.debug("parameter -> {}: {}", inputTypeString, parameter.getId().toString());
											
											String calleeInTypeSimpleName = inputTypeString.substring(inputTypeString.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
											celleeInputVar.append("		");
											celleeInputVar.append(calleeInTypeSimpleName);
											celleeInputVar.append(" ");
											
											//중복 체크
											String lowerInTypeSimpleName = IOperateCode.ELEMENT_IN.concat(stringUtil.getFirstCharUpperCase(calleeInTypeSimpleName));
											Integer varCnt = methodVarMap.get(lowerInTypeSimpleName);
											if(varCnt != null) {
												// plus
												varCnt = varCnt + 1;
												// make
												// 중복되는 메소드 지역변수 명은 시퀀스를 01 부터 붙인다. 
												lowerInTypeSimpleName = lowerInTypeSimpleName.concat(stringUtil.leftPad(Integer.toString(varCnt - 1), 2, "0")); 
											}
											else {
												// init
												varCnt = 1;
												// set
												methodVarMap.put(lowerInTypeSimpleName, varCnt);
												// make
												// lowerInTypeSimpleName = lowerInTypeSimpleName.concat(stringUtil.leftPad(Integer.toString(varCnt), 2, "0"));
												// 첫번째 메소드 지역변수 명은 시퀀스를 붙이지 않는다. 
											}
											
											//method inner variable name  
											celleeInputVar.append(lowerInTypeSimpleName);
											celleeInputVar.append(" = ");
											
											//OMM 분석
											String ifOmmPath = new StringBuilder().append(getSourceRoot()).append("/").append(inputTypeString.replace(IOperateCode.STR_DOT, "/")).append(".omm").toString();
											logger.debug("ifOmmPath: {}", ifOmmPath);
											File ommFile = new File(ifOmmPath); 
											if(ommFile.exists()) {
												// OMM 파일이 존재하면 분석 실행
												// omm일 경우
												celleeInputVar.append("new ");
												celleeInputVar.append(calleeInTypeSimpleName);
												celleeInputVar.append("();");
												
												// omm 분석 실행
												logger.debug("[Parse OMM Path]: {}", ifOmmPath);
												
												
												Object parseData = generateHelper.getOmmProperty(ommFile);
											}
											else if(inputTypeString.contains(".")){
												//omm 파일이 존재하지 않고 패키지가 존재할 경우
												
												logger.debug("★★★★★★★★★★★★★★★★★★★★★★★★★");
												logger.debug(inputTypeString);
												
												celleeInputVar.append(typeUtil.getPrimitiveWrapperDefaultValue(inputTypeString.substring(inputTypeString.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length())));
												celleeInputVar.append(";");
											}
											else {
												//패키지가 존재하지 않는 타입이거나 primitive 타입일경우
												
												logger.debug("※※※※※※※※※※※※※※※※※※※※※※※※※※");
												logger.debug(inputTypeString);
												
												celleeInputVar.append(typeUtil.getPrimitiveWrapperDefaultValue(inputTypeString.substring(inputTypeString.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length())));
												celleeInputVar.append(";");
											}
											
											celleeInputVar.append(SystemUtil.LINE_SEPARATOR);
											
											//메소드의 입력 표현식
											if(StringUtil.isNotEmpty(methodInputExpr.toString())) {
												methodInputExpr.append(", ");
											}
											methodInputExpr.append(lowerInTypeSimpleName);
										}
										
										logger.debug("celleeInputVar: {}", celleeInputVar.toString());
										logger.debug("methodInputExpr: {}", methodInputExpr.toString());
										logger.debug("calleeReturnType: {}", calleeReturnType.toString());
										
										String celleeOutType = calleeReturnType.toString();
										if(celleeOutType.contains(".")) {
											celleeOutType = celleeOutType.substring(celleeOutType.lastIndexOf(".") + ".".length());
										}
										String celleeOutVarName = stringUtil.getFirstCharLowerCase(celleeOutType);

										findMethod = true;
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
							}
							if(!findMethod) {
								throw new ApplicationException("설계서에 작성된 피호출자 메소드를 찾을수 없습니다. 자바: {}, 메소드: {}", calleeTypeName, calleeMethodName);
							}
						}
						
						logger.debug("[dsDbioInit]\n{}", dsDbioInit.toString());
						
						logger.debug("[celleeInputVar]\n{}", celleeInputVar.toString());
						
						logger.debug("[dsBizCode]\n{}", dsBizCode.toString());
						
					}
					
					dsImports = new StringBuilder();
					dsVariables = new StringBuilder();
					for(String imports : dsImportsSet) {
						dsImports.append("import ").append(imports).append(";").append(SystemUtil.LINE_SEPARATOR);
						String varTypeName = imports.substring(imports.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
						dsVariables.append("	private ").append(varTypeName).append(" ").append(stringUtil.getFirstCharLowerCase(varTypeName)).append(";").append(SystemUtil.LINE_SEPARATOR);
					}
					logger.debug("[dsImports]\n{}", dsImports.toString());
					logger.debug("[dsVariables]\n{}", dsVariables.toString());
					
					logger.debug("★★★★★★★★★★★★★ [END Method Element Setup] ★★★★★★★★★★★★★★");
				}
				
				logger.debug("★★★★★★★★★★★★★ [START Class Element Setup] ★★★★★★★★★★★★★★");
				
				if(StringUtil.isEmpty(currentBasePackage)) {
					throw new ApplicationException("베이스 패키지 정보가 존재하지 않습니다. 프로그램설계 문서(엑셀)을 확인하세요.");
				}
				
				designRow.setBcModf(StringUtil.NVL(designRow.getBcModf(), ""));
				logger.debug("[READY] {} {} {}{} : {}", designRow.getBcNm(), designRow.getBcModf(), designRow.getBcMetdPref(), designRow.getBcMetdBody(), designRow.getBcMetdLogc());
				
				// 생성 대상 클래스
				compareClasStr = designRow.getBcNm();
				// 생성 대상 클래스+메소드
				compareMetdStr = designRow.getBcMetdMergeStr();
				
				currentDesign = new ProgramDesignDTO(); 
				
				propertyUtil.copyProperty(designRow, currentDesign, copyTarget);
				currentDesign.setBasePack(currentBasePackage);
				
				dsDate = DateUtil.getFastDate(DateUtil.DEF_DATE_FORMAT);
				dsLogicalName = designRow.getLogc();
				dsDescription = designRow.getLogc();
				dsClassName = designRow.getBcNm();
				fileName = dsClassName.concat(".java");
				dsBcModf = StringUtil.NVL(designRow.getBcModf()).toLowerCase();
				dsPackage = currentDesign.getBasePack().concat(".").concat(getSubPackage());
				
				dsMethodName = new StringBuilder().append(designRow.getBcMetdPref()).append(stringUtil.getFirstCharUpperCase(designRow.getBcMetdBody())).toString();
				dsMethodLogicalName = new StringBuilder().append(rvLogicalName).append(" ").append(designRow.getBcMetdLogc()).toString();
				dsMethodDescription = dsMethodLogicalName;
				
				logger.debug("★★★★★★★★★★★★★ [END Class Element Setup] ★★★★★★★★★★★★★★");
				
				//첫번째 BC메소드
				bcMetdNm = designRow.getBcMetdPref().concat(designRow.getBcMetdBody());
				currentDesign.addCalleeMap(bcMetdNm, new LinkedHashMap<String, Object>());
				currentDesign.addMethodDesignMap(bcMetdNm, designRow);
				
				//variable init 
				//dsImports = new StringBuilder();
				dsImportsSet = new TreeSet<String>();
				//import code
				addImportCode(currentDesign.getCalleeMap(bcMetdNm), designRow, dsImportsSet, currentBasePackage);
				
				logger.debug("fileName: {}, dsPackage: {}, dsDate: {}", fileName, dsPackage, dsDate);
			}
			else if((StringUtil.isEmpty(designRow.getBcNm())
					&& StringUtil.isNotEmpty(designRow.getBcMetdPref())
					&& StringUtil.isNotEmpty(designRow.getBcMetdBody())
					) || compareClasStr.equals(designRow.getBcNm())) {
				// BC 메소드 부분이 빈 로우일경우 DBIO존재하면 해당 BC메소드가 사용하는 DBIO메소드임.
				// compareMetdStr 담긴 생성대상 BC메소드와 동일할경우 설정된 DBIO가 존재하면 해당 BC메소드가 사용하는 DBIO이다.
				logger.debug("BC클래스의 두번째 부터 나오는 BC메소드 추출");
				
				//BC클래스의 두번째 부터 나오는 BC메소드 추출
				bcMetdNm = designRow.getBcMetdPref().concat(designRow.getBcMetdBody());
				currentDesign.addCalleeMap(bcMetdNm, new LinkedHashMap<String, Object>());
				currentDesign.addMethodDesignMap(bcMetdNm, designRow);
				
				//import code
				addImportCode(currentDesign.getCalleeMap(bcMetdNm), designRow, dsImportsSet, currentBasePackage);
								
				logger.debug("[Bean Method] {}.{}{}", compareClasStr, designRow.getBcMetdPref(), designRow.getBcMetdBody());
			}
			else if((StringUtil.isEmpty(designRow.getBcNm())
					&& StringUtil.isEmpty(designRow.getBcMetdPref())
					&& StringUtil.isEmpty(designRow.getBcMetdBody())
					) || compareMetdStr.equals(designRow.getBcMetdMergeStr())) {
				// BC 메소드 부분이 빈 로우일경우 DBIO존재하면 해당 BC메소드가 사용하는 DBIO메소드임.
				// compareMetdStr 담긴 생성대상 BC메소드와 동일할경우 설정된 DBIO가 존재하면 해당 BC메소드가 사용하는 DBIO이다.
				
				logger.debug("BC메소드 내부에서 사용하는 callee");
	
				//import code
				addImportCode(currentDesign.getCalleeMap(bcMetdNm), designRow, dsImportsSet, currentBasePackage);
								
				logger.debug("[Bean use DBIO Method] {}.{}", designRow.getDbioNm(), designRow.getDbioMetdNm());
			}
			else {
				logger.debug("- this row({}) continue", i);
				continue;
			}
		}

		logger.debug("[END] createCode");
		return out;
	}
	
	
	private void addImportCode(Map<String, Object> bcCalleeMap, ProgramDesignDTO designRow, Set<String> dsImportsSet, String currentBasePackage) {
		//import code
		if(StringUtil.isNotEmpty(designRow.getDbioNm())) {
			
			if(designRow.getDbioNm().contains(IOperateCode.STR_DOT)) {
				
				dsImportsSet.add(designRow.getDbioNm());
				
				bcCalleeMap.put(new StringBuilder()
					.append(designRow.getDbioNm())
					.append(IOperateCode.STR_DOT)
					.append(designRow.getDbioMetdNm())
					.toString()
				, null);
					
			}
			else {
				String classPrefix = designRow.getDbioNm().substring(0, 1);
				
				dsImportsSet.add(new StringBuilder().append(currentBasePackage)
						.append(IOperateCode.STR_DOT)
						.append(GenerateHelper.JAVA_PREFIX.get(classPrefix))
						.append(IOperateCode.STR_DOT)
						.append(designRow.getDbioNm()).toString());
				
				bcCalleeMap.put(new StringBuilder()
						.append(currentBasePackage)
						.append(IOperateCode.STR_DOT)
						.append(GenerateHelper.JAVA_PREFIX.get(classPrefix))
						.append(IOperateCode.STR_DOT)
						.append(designRow.getDbioNm())
						.append(IOperateCode.STR_DOT)
						.append(designRow.getDbioMetdNm())
						.toString()
					, null);
			}
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
		BxmBeanGenerateUtil.templatePath = templatePath;
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
