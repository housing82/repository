package com.universal.code.bxm;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.constants.JavaReservedWordConstants;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.StringUtil;

public class GenerateHelper {

	private final static Logger logger = LoggerFactory.getLogger(GenerateHelper.class);
	
	private StringUtil stringUtil;
	
	private static GenerateHelper INSTANCE;
	
	private static Map<String, Integer> javaDataMap;
	
	private static Map<String, Map<String, Integer>> javaMethodDataMap;
	
	static {
		INSTANCE = new GenerateHelper();
		javaDataMap = new HashMap<String, Integer>();
		javaMethodDataMap = new HashMap<String, Map<String, Integer>>();
	}
	
	public GenerateHelper() {
		stringUtil = new StringUtil();
	}
	
	//동일한이름의 자바가 생성이되었다면 자바 마지막의 순번을 채번함
	public String getJavaSeq(String javaName) {
		String out = null;
		String className = javaName.trim();
		if(className.toLowerCase().endsWith(".java")) {
			className = className.substring(0, className.length() - ".java".length()).trim();
		}
		Integer javaSeq = javaDataMap.get(className);
		if(javaSeq != null) {
			//이미존재할경우 순번 을 증가하여 리턴
			javaSeq = javaSeq + 1;
		}
		else {
			//존재하지않을경우 01 리턴
			javaSeq = 1;
		}
		javaDataMap.put(className, javaSeq);
		//신규 자바의 메소드맵 초기화
		Map<String, Integer> methodMap = new HashMap<String, Integer>();
		javaMethodDataMap.put(className, methodMap);
		
		out = stringUtil.leftPad(javaSeq.toString(), 2, "0");
		return out;
	}
	
	//특정자바 안에 동일한 메소드 생성 이력이있을경우 순번을 증가하여 리턴 없으면 01 리턴
	public String getJavaMethodSeq(String javaName, String methodName) {
		String out = null;
		String className = javaName.trim();
		String classMethod = methodName.trim();
		if(javaDataMap.get(className) == null) {
			throw new ApplicationException("아직생성되지 않은 클래스입니다.");
		}
		
		Map<String, Integer> methodMap = javaMethodDataMap.get(className);
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
		
		javaMethodDataMap.put(className, methodMap);
		
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
