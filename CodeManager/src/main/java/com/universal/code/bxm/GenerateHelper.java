package com.universal.code.bxm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.constants.JavaReservedWordConstants;
import com.universal.code.excel.dto.ExcelDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.StringUtil;

public class GenerateHelper {

	private final static Logger logger = LoggerFactory.getLogger(GenerateHelper.class);
	
	private StringUtil stringUtil;
	
	private static GenerateHelper INSTANCE;
	
	private static Map<String, Integer> javaDataMap;
	
	private static Map<String, Map<String, Integer>> javaMethodDataMap;
	
	private static Map<String, Map<String, List<ExcelDTO>>> excelDataMap;
	
	final static List<String> EXTRACT_SHEET_NAMES;
	
	final static String CPNT_DESIGN_SHEET_NAME;
	
	final static String DBIO_DESIGN_SHEET_NAME;
	
	final static Map<String, String> JAVA_PREFIX;
	
	final static Map<String, String> METHOD_VERB;
	
	static {
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
	
}
