package com.universal.code.bxm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.constants.IOperateCode;
import com.universal.code.constants.JavaReservedWordConstants;
import com.universal.code.dto.OmmDTO;
import com.universal.code.dto.OmmFieldDTO;
import com.universal.code.excel.dto.ExcelDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;
import com.universal.code.utils.TypeUtil;

public class GenerateHelper {

	private final static Logger logger = LoggerFactory.getLogger(GenerateHelper.class);
	
	private StringUtil stringUtil;
	
	private FileUtil fileUtil;
	
	private TypeUtil typeUtil;
	
	private static GenerateHelper INSTANCE;
	
	private static Map<String, Integer> javaDataMap;
	
	private static Map<String, Map<String, Integer>> javaMethodDataMap;
	
	private static Map<String, Map<String, List<ExcelDTO>>> excelDataMap;
	
	final static List<String> EXTRACT_SHEET_NAMES;
	
	final static String CPNT_DESIGN_SHEET_NAME;
	
	final static String DBIO_DESIGN_SHEET_NAME;
	
	final static Map<String, String> JAVA_PREFIX;
	
	final static Map<String, String> METHOD_VERB;
	
	final static String OMM_EXT;
	
	final static String EXCEL_START_FIRST_CELL;
	final static String EXCEL_END_FIRST_CELL;

	
	static {
		EXCEL_START_FIRST_CELL = "[[START]]";
		EXCEL_END_FIRST_CELL = "[[END]]";
		
		JAVA_PREFIX = new HashMap<String, String>();
		
		JAVA_PREFIX.put("S", "sc");
		JAVA_PREFIX.put("B", "bc");
		JAVA_PREFIX.put("D", "dao");
		
		METHOD_VERB = new HashMap<String, String>();
		METHOD_VERB.put("get", "조회");
		METHOD_VERB.put("search", "다건조회");
		METHOD_VERB.put("save", "저장");
		METHOD_VERB.put("modify", "수정");
		METHOD_VERB.put("remove", "삭제");
		METHOD_VERB.put("delete", "완전삭제");
		
		OMM_EXT = ".omm";
		
		INSTANCE = new GenerateHelper();
		javaDataMap = new HashMap<String, Integer>();
		javaMethodDataMap = new HashMap<String, Map<String, Integer>>();
		excelDataMap = new HashMap<String, Map<String, List<ExcelDTO>>>();
		
		DBIO_DESIGN_SHEET_NAME = "2) DB테이블 DBIO";
		CPNT_DESIGN_SHEET_NAME = "3) SC,BC 메소드설계";
		
		
		EXTRACT_SHEET_NAMES = new ArrayList<String>();
		EXTRACT_SHEET_NAMES.add(DBIO_DESIGN_SHEET_NAME);
		EXTRACT_SHEET_NAMES.add(CPNT_DESIGN_SHEET_NAME);
	}
	
	static String getMethodVerb(String key) {
		return (METHOD_VERB.get(key) != null ? METHOD_VERB.get(key) : "");
	}
	
	public GenerateHelper() {
		stringUtil = new StringUtil();
		fileUtil = new FileUtil();
		typeUtil = new TypeUtil();
	}
	
	public static void setExcelData(String excelPath, Map<String, List<ExcelDTO>> sheetData) {
		
		GenerateHelper.excelDataMap.put(excelPath, sheetData);
	}
	
	public static Map<String, List<ExcelDTO>> getExcelData(String excelPath) {
		
		return GenerateHelper.excelDataMap.get(excelPath);
	}
	
	//동일한이름의 자바가 생성이되었다면 자바 마지막의 순번을 채번함
	public String getJavaSeq(String javaName) {
		String out = null;
		String className = javaName.trim();
		if(className.toLowerCase().endsWith(".java")) {
			className = className.substring(0, className.length() - ".java".length()).trim();
		}
		Integer javaSeq = GenerateHelper.javaDataMap.get(className);
		if(javaSeq != null) {
			//이미존재할경우 순번 을 증가하여 리턴
			javaSeq = javaSeq + 1;
		}
		else {
			//존재하지않을경우 01 리턴
			javaSeq = 1;
		}
		GenerateHelper.javaDataMap.put(className, javaSeq);
		//신규 자바의 메소드맵 초기화
		Map<String, Integer> methodMap = new HashMap<String, Integer>();
		GenerateHelper.javaMethodDataMap.put(className, methodMap);
		
		out = stringUtil.leftPad(javaSeq.toString(), 2, "0");
		return out;
	}
	
	//특정자바 안에 동일한 메소드 생성 이력이있을경우 순번을 증가하여 리턴 없으면 01 리턴
	public String getJavaMethodSeq(String javaName, String methodName) {
		String out = null;
		String className = javaName.trim();
		String classMethod = methodName.trim();
		if(GenerateHelper.javaDataMap.get(className) == null) {
			throw new ApplicationException("아직생성되지 않은 클래스입니다.");
		}
		
		Map<String, Integer> methodMap = GenerateHelper.javaMethodDataMap.get(className);
		//메소드 정보조회
		Integer methodSeq = methodMap.get(classMethod);

		if(methodSeq != null) {
			//이미존재할경우 순번 을 증가하여 리턴
			methodSeq = methodSeq + 1;
		}
		else {
			//존재하지않을경우 01 리턴
			methodSeq = 1;
		}

		methodMap.put(classMethod, methodSeq);
		
		GenerateHelper.javaMethodDataMap.put(className, methodMap);
		
		out = stringUtil.leftPad(methodSeq.toString(), 2, "0");
		return out;
	}
	
	
	public static GenerateHelper getInstance() {
		return INSTANCE;
	}
	
	public String getCamelCaseFieldName(String str) {
		return JavaReservedWordConstants.get(stringUtil.getCamelCaseString(StringUtil.NVL(str)));
	}
	
/*	
	OMM kait.hd.hda.onl.dao.dto.DHdCodeSihang01IO

	<description="HD_코드_시행사 ( HD_CODE_SIHANG )">
	{
		String deptCode<length=12 description="사업코드 [SYS_C0025340(C),SYS_C0025342(P) SYS_C0025342(UNIQUE)]"  >;
		Long seq<length=22 description="순번 [SYS_C0025341(C),SYS_C0025342(P) SYS_C0025342(UNIQUE)]"  >;
		String sihangVendor<length=20 description="시행사사업자코드"  >;
		String sihangDepyo<length=40 description="시행사대표자명"  >;
		String sihangUpte<length=40 description="시행사업태"  >;
		String sihangUpjong<length=40 description="시행사업종"  >;
		String sihangZip<length=6 description="시행사우편번호"  >;
		String sihangAddr1<length=100 description="시행사주소1"  >;
		String sihangAddr2<length=100 description="시행사주소2"  >;
		String inputDutyId<length=12 description="입력담당"  >;
		String inputDate<length=14 description="입력일시"  >;
		String chgDutyId<length=12 description="수정담당"  >;
		String sihangName<length=60 description="시행사상호명"  >;
		String chgDate<length=14 description="수정일시"  >;
		String sihangZipOrg<length=6 description=""  >;
		String sihangAddr1Org<length=100 description=""  >;
		String sihangAddr2Org<length=100 description=""  >;
		String sihangAddrTag<length=1 description=""  >;
	}
	*/
	public OmmDTO getOmmProperty(File ommFile) {
		OmmDTO out = null;
		if(ommFile.exists()) {
			out = new OmmDTO();
		}
		else {
			throw new ApplicationException("OMM 파일이 존재하지 않습니다. 파일: {}", ommFile.getPath());
		}
		
		FileUtil fileUtil = new FileUtil();
		
		String ommContents = fileUtil.getTextFileContent(ommFile); 
		
		StringTokenizer lines = new StringTokenizer(ommContents, SystemUtil.LINE_SEPARATOR);
		String line = null;
		
		String ommType = null;
		String ommDesc = null;
		OmmFieldDTO ommFieldDTO = null;
		while(lines.hasMoreElements()) {
			line = lines.nextElement().toString().trim();
			
			if(line.startsWith("OMM")) {
				//first line
				ommType = line.substring("OMM".length()).trim();
				//logger.debug("ommType: {}", ommType);
				out.setOmmType(ommType);
			}
			else if(line.startsWith("<description=\"")) {
				ommDesc = line.substring("<description=\"".length(), line.length() - 2);
				//logger.debug("ommDesc: {}", ommDesc);
				out.setOmmDesc(ommDesc);
			}
			else if(line.equals("{") || line.equals("}")){
				continue;
			}
			else {
				//logger.debug("\n\nline field: {}", line);
				ommFieldDTO = new OmmFieldDTO();
			
				String type = line.substring(0, line.indexOf(" ")).trim();
				String name = line.substring(line.indexOf(" "), line.indexOf("<")).trim();
				//logger.debug("#type: {}, name: {}", type, name);
				String info = line.substring(line.indexOf("<") + "<".length(), line.lastIndexOf(">"));
				//logger.debug("#info: {}", info);
				
				String length = null;
				String description = null;
				String arrayReference = null;
				if(info.indexOf("description=\"") > -1) {
					length = info.substring(info.indexOf("length=") + "length=".length(), info.indexOf(" ") + " ".length());
					//logger.debug("#length: {}", length);
					
					description = info.substring(info.indexOf("description=\"") + "description=\"".length());
					//logger.debug("#description(1): {}", description);
					description = description.substring(0, description.indexOf("\"")).trim();
					//logger.debug("#description(2): {}", description);
				}
				else if(info.contains("arrayReference=\"")) {
					
					arrayReference = info.substring(info.indexOf("arrayReference=\"") + "arrayReference=\"".length());
					arrayReference = arrayReference.substring(0, arrayReference.indexOf("\"")).trim();
				}
				else {
					length = info.substring(info.indexOf("length=") + "length=".length());
					//logger.debug("#length: {}", length);
				}
				
				ommFieldDTO.setType(type);
				ommFieldDTO.setName(name);
				ommFieldDTO.setLength(length);
				ommFieldDTO.setDescription(description);
				ommFieldDTO.setArrayReference(arrayReference);
				
				//logger.debug(ommFieldDTO.toString());
				
				out.addOmmFields(ommFieldDTO);
			}
		}
		
		return out;
	}
	
	String getSetterString(String setOmmVarName, OmmFieldDTO setOmmFieldDTO, String getOmmVarName, OmmFieldDTO getOmmFieldDTO, String closeCode) {
		StringBuilder out = new StringBuilder();
		
		out.append(setOmmVarName);
		out.append(".");
		out.append("set");
		out.append(stringUtil.getFirstCharUpperCase(setOmmFieldDTO.getName()));
		out.append("(");
		out.append(getGetterString(getOmmVarName, getOmmFieldDTO, ""));
		out.append(")");
		out.append(closeCode);
		
		
		return out.toString();
	}
	
	String getGetterString(String ommVarName, OmmFieldDTO ommFieldDTO, String closeCode) {
		StringBuilder out = new StringBuilder();
		
		if(StringUtil.isEmpty(ommVarName)) {
			out.append(ommFieldDTO.getName());
		}
		else {
			out.append(ommVarName);
			out.append(".");
			out.append("get");
			out.append(stringUtil.getFirstCharUpperCase(ommFieldDTO.getName()));
			out.append("()");	
		}
		out.append(closeCode);
		
		return out.toString();
	}

	boolean createOmmFile(OmmDTO ommDTO, boolean makeFile) {
		boolean out = false;
		logger.debug("[createOmmFile]\n{}", ommDTO);
		if(ommDTO.getOmmFields() == null || ommDTO.getOmmFields().size() == 0) {
			//throw new ApplicationException("OMM 필드가 설정되지 않았습니다.");
			return out;
		}
		
		String fileName = null;
		String ommPath = ommDTO.getSourceRoot().replace(IOperateCode.STR_BACK_SLASH, IOperateCode.STR_SLASH);
		if(!ommPath.endsWith(IOperateCode.STR_SLASH)) {
			ommPath = ommPath.concat(IOperateCode.STR_SLASH);
		}
		ommPath = ommPath.concat(ommDTO.getOmmType().replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH));
		fileName = ommPath.substring(ommPath.lastIndexOf(IOperateCode.STR_SLASH) + IOperateCode.STR_SLASH.length());
		ommPath = ommPath.substring(0, ommPath.length() - (fileName.length() + IOperateCode.STR_SLASH.length()));

		logger.debug("ommPath: {}, fileName: {}", ommPath, fileName);
		
		StringBuilder strbd = new StringBuilder();
		
		strbd = new StringBuilder();
		strbd.append("OMM ");
		strbd.append(ommDTO.getOmmType());
		strbd.append(SystemUtil.LINE_SEPARATOR);
		strbd.append(SystemUtil.LINE_SEPARATOR);
		
		strbd.append("<description=\"");
		strbd.append(ommDTO.getOmmDesc());
		strbd.append("\">");
		strbd.append(SystemUtil.LINE_SEPARATOR);
		strbd.append("{");
		strbd.append(SystemUtil.LINE_SEPARATOR);
		
		String fieldType = null;
		String ommFilePath = null;
		for(OmmFieldDTO ommField : ommDTO.getOmmFields()) {
			
			fieldType = ommField.getType(); 
			if((ommField.getType().startsWith(List.class.getSimpleName()) || ommField.getType().startsWith(List.class.getCanonicalName())) && StringUtil.isNotEmpty(ommField.getArrayReference())) {
				
				if(ommField.getType().contains("<") && ommField.getType().contains(">")) {
					fieldType = fieldType.substring(fieldType.indexOf("<") + "<".length(), fieldType.lastIndexOf(">"));
					
					if(fieldType.contains(".")) {
						fieldType = fieldType.substring(fieldType.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
					}
					
					// omm일경우
					ommFilePath = findFilePath(ommField.getSourceRoot(), fieldType, "omm");
					if(ommFilePath != null) {
						logger.debug("#createPath: {}", ommFilePath);
						// omm파일 						
						ommFilePath = ommFilePath.replace(IOperateCode.STR_BACK_SLASH, IOperateCode.STR_SLASH);
						ommFilePath = ommFilePath.replace(ommField.getSourceRoot(), "");
						ommFilePath = ommFilePath.substring(0, ommFilePath.length() - ".omm".length());
						ommFilePath = ommFilePath.replace(IOperateCode.STR_SLASH, IOperateCode.STR_DOT);
						if(ommFilePath.startsWith(IOperateCode.STR_DOT)) {
							ommFilePath = ommFilePath.substring(IOperateCode.STR_DOT.length());
						}
						fieldType = ommFilePath;
					}
					else {
						// omm이 아님
						fieldType = typeUtil.getPrimitiveConvertWrapper(fieldType);
					}
				}
				else {
					throw new ApplicationException("OMM 리스트필드의 ParameterizedType이 존재하지 않습니다.");
				}
			}
			
			if(StringUtil.isNotEmpty(ommField.getArrayReference())) {
				
				strbd.append("	");
				strbd.append(ommField.getArrayReferenceType());
				strbd.append(" ");
				strbd.append(ommField.getArrayReference());
				strbd.append("<");
				strbd.append("length=");
				if(ommField.getArrayReferenceType().equalsIgnoreCase(Integer.class.getSimpleName())) {
					strbd.append(9);
				}
				else {
					strbd.append(StringUtil.NVL(ommField.getArrayReferenceLength(), "0"));
				}
				
				strbd.append(" description=\"");
				strbd.append(ommField.getDescription());
				strbd.append(" 건수\"");
				strbd.append("  ");
				strbd.append("  >;");
				strbd.append(SystemUtil.LINE_SEPARATOR);
			}
			
			
			
			strbd.append("	");
			strbd.append(fieldType);
			strbd.append(" ");
			strbd.append(ommField.getName());
			strbd.append("<");
			strbd.append("length=");
			strbd.append(StringUtil.NVL(ommField.getLength(), "0"));
			
			if(StringUtil.isNotEmpty(ommField.getArrayReference())) {
				strbd.append(" arrayReference=\"");
				strbd.append(ommField.getArrayReference());
				strbd.append("\"");
			}
			
			strbd.append(" description=\"");
			strbd.append(ommField.getDescription());
			strbd.append("\"");
			strbd.append("  >;");
			strbd.append(SystemUtil.LINE_SEPARATOR);
		}
		strbd.append("}");
		
		if(makeFile)
			fileUtil.mkfile(ommPath, fileName.concat(OMM_EXT),
					strbd.toString(), IOperateCode.DEFAULT_ENCODING, 
					false, true);
		
		return out;
	}
	
	/**
	 * 주어진 rootPath 아래로 바인드된 fileName의 전채(일반)경로를 찾는다.
	 * 가장 먼저 검색된 fileName의 경로를 리턴한다.
	 * @param rootPath
	 * @param fileName
	 * @return
	 */
	public String findFilePath(String rootPath, String fileName, String fileExt) {
		String path = null;
		String resourceName = null;
		
		File root = new File(rootPath);
		if(!root.exists()) {
			throw new ApplicationException("루트 패스에 해당하는 디렉토리파일이 존재하지 않습니다. 루트: {}", rootPath);
		}
		if(!root.isDirectory()) {
			throw new ApplicationException("루트 패스는 디렉토리여야만 합니다. 루트패스파일: {}", rootPath);
		}
		if(StringUtil.isEmpty(fileName)) {
			throw new ApplicationException("파일명이 존재하지 않습니다. 파일명: {}", fileName);
		}
//		logger.debug("★rootPath: {}", rootPath);
//		logger.debug("★fileName: {}", fileName);
//		logger.debug("★fileExt: {}", fileExt);
		
		resourceName = fileName.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH).concat(IOperateCode.STR_DOT).concat(fileExt);
//		logger.debug("★resourceName: {}", resourceName);
		if(resourceName.contains(IOperateCode.STR_SLASH)) {
			path = rootPath.concat(IOperateCode.STR_SLASH).concat(resourceName);
			if(!new File(path).exists()) {
				throw new ApplicationException("루트 경로로 부터 설정된 패키지 + 파일명에 해당하는 파일이 존재하지 않습니다. 파일: {}", path);
			}
		}
		else {
			File[] fileList = root.listFiles();
			try {
				boolean findFile = false;
				for(File file : fileList) {
					if(file.isFile()) {
//						logger.debug("fileName: {}, file.getName: {}", fileName, file.getName());
						if(fileName.concat(IOperateCode.STR_DOT).concat(fileExt).equals(file.getName())) {
							path = file.getCanonicalPath();
							findFile = true;
							break;
						}
					}
				}
				if(!findFile) {
					for(File file : fileList) {
						if(file.isDirectory()) {
//							logger.debug("file.getCanonicalPath: {}, fileName: {}", file.getCanonicalPath(), fileName);
							path = findFilePath(file.getCanonicalPath(), fileName, fileExt);
							if(StringUtil.isNotEmpty(path)) {
								break;
							}
						}
					}
				}
			} catch (IOException e) {
				throw new ApplicationException(e);
			}
		}
		
		return path;
	}

	
	public String getLowerInTypeSimpleName(Map<String, Integer> methodVarMap, String inputVarString, String calleeInTypeSimpleName) {
		logger.debug("[IN-getLowerInTypeSimpleName] inputVarString: {}, calleeInTypeSimpleName: {}\n[methodVarMap]:\n{}", inputVarString, calleeInTypeSimpleName, methodVarMap);
		
		String lowerInTypeSimpleName = null;
		//중복 체크
		if(inputVarString.equalsIgnoreCase("in")) {
			lowerInTypeSimpleName = IOperateCode.ELEMENT_IN.concat(stringUtil.getFirstCharUpperCase(calleeInTypeSimpleName));
		}
		else {
			lowerInTypeSimpleName = IOperateCode.ELEMENT_IN.concat(stringUtil.getFirstCharUpperCase(inputVarString));	
		}
		logger.debug("[MD-getLowerInTypeSimpleName] : {}", lowerInTypeSimpleName);
		Integer varCnt = methodVarMap.get(lowerInTypeSimpleName);
		if(varCnt != null) {
			// plus
			varCnt = varCnt + 1;
			
			methodVarMap.put(lowerInTypeSimpleName, varCnt);
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
		logger.debug("[OUT-getLowerInTypeSimpleName] : {}\nmethodVarMap:\n{}", lowerInTypeSimpleName, methodVarMap);
		return lowerInTypeSimpleName;
	}
	
	
	public String getTypeNameNumbering(Map<String, Integer> methodVarMap, String typeCanonicalName) {
		logger.debug("[IN-getOmmTypeName] typeCanonicalName: {}\n[methodVarMap]:\n{}", typeCanonicalName, methodVarMap);
		
		//중복 체크
		logger.debug("[MD-getOmmTypeName] : {}", typeCanonicalName);
		Integer varCnt = methodVarMap.get(typeCanonicalName);
		if(varCnt != null) {
			// plus
			varCnt = varCnt + 1;
		}
		else {
			// init
			varCnt = 1;
		}
		// set
		methodVarMap.put(typeCanonicalName, varCnt);
		
		typeCanonicalName = typeCanonicalName.concat(stringUtil.leftPad(Integer.toString(varCnt), 2, "0"));

		logger.debug("[OUT-getOmmTypeName] : {}\nmethodVarMap:\n{}", typeCanonicalName, methodVarMap);
		return typeCanonicalName;
	}
	
	public String getTypeSimpleName(String canonicalType) {
		
		String out = null;
		
		if(canonicalType.endsWith(".omm")) {
			canonicalType = canonicalType.substring(0, canonicalType.length() - 4);
		}
		else if(canonicalType.endsWith(".java")) {
			canonicalType = canonicalType.substring(0, canonicalType.length() - 5);
		}
		
		if(canonicalType.contains(IOperateCode.STR_DOT)) {
			out = canonicalType.substring(canonicalType.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
		}
		else {
			out = canonicalType;
		}
		
		return out;
	}
	
}
