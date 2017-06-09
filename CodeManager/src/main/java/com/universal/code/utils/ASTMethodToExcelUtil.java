package com.universal.code.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.ast.java.ASTVisitor;
import com.universal.code.constants.IOperateCode;
import com.universal.code.excel.ExcelWriterUtil;
import com.universal.code.excel.dto.WorkBookDTO;
import com.universal.code.exception.ApplicationException;

public class ASTMethodToExcelUtil {

	private static final Logger logger = LoggerFactory.getLogger(ASTMethodToExcelUtil.class);
	
	private ExcelWriterUtil excelWriterUtil;

	private ASTVisitor visitor;
	
	private Map<String, List<Map<String, Object>>> analyzedMap;
	
	private String excelPath;
	
	private String writeExcelName; 
	
	private String sourceRoot;
	
	private String excelVersionDateFormat;
	
	public ASTMethodToExcelUtil() {
		visitor = new ASTVisitor();
		excelWriterUtil = new ExcelWriterUtil();
	}
	
	public void execute() throws Exception {

		List<Map<String, Object>> value = null;
		for(Entry<String, List<Map<String, Object>>> entry : analyzedMap.entrySet()) {
			value = parseJava(entry.getKey());
			if(value != null) {
				entry.setValue(value);
				/*
				for(Map<String, Object> item : entry.getValue()) {
					logger.debug(item.get("javaName") + "	" + item.get("name") + "	" + item.get("annotations"));
				}
				*/
			}
			logger.debug("package: '{}' Number of Java methods analyzed: {}", entry.getKey(), (value != null ? value.size() : 0));
		}
		
		logger.debug("Number of Java packages analyzed: {}", analyzedMap.size());
		
		// 자바 메소드 분석 결과를 저장할 파일 경로
		String writeExcelPath = getExcelPath().concat("/").concat(getWriteExcelName());
		logger.debug("자바 메소드 분석결과 작성 대상 엑셀 파일 경로: {}", writeExcelPath);
		
		File excelFile = new File(writeExcelPath);
		if(excelFile.exists()) {
			// rename old excel file
			excelWriterUtil.renameExcelFile(writeExcelPath, excelFile, getExcelVersionDateFormat());
		}
		
		// create new excel worksheet
		WorkBookDTO workBookDTO = new WorkBookDTO();
		workBookDTO.setFileDir(getExcelPath());
		workBookDTO.setFileName(getWriteExcelName());
		
		String newExcelPath = excelWriterUtil.createExcel(workBookDTO, analyzedMap);
		
		logger.debug("newExcelPath: {}", newExcelPath);
		
	}
	

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> parseJava(String packageStr) throws Exception {
		if(StringUtil.isEmpty(packageStr)) {
			throw new ApplicationException("검색 대상 패키지가 존재하지 않습니다.");
		}
		String directory = getSourceRoot().concat("/").concat(packageStr.replace(".", "/"));
		if(!new File(directory).exists()) {
			return null;
		}
		String javaPath = null;
		File[] files = FileUtil.getFiles(directory);
		Map<String, Object> descMap;
		Map<String, Object> extractMap;
		List<Map<String, Object>> elements = new ArrayList<Map<String, Object>>(); 
		String javaName = null;
		for(File file : files){ 
			javaPath = file.getCanonicalPath();
			
			if(file.isDirectory()) {
				elements.addAll(parseJava(packageStr.concat(IOperateCode.STR_DOT).concat(file.getName())));
			}
			else if(file.isFile() && javaPath.endsWith(IOperateCode.JAVA_EXTENSION)) {
				//logger.debug("javaPath: {}", javaPath);
				for(Map<String, Object> node : visitor.execute(javaPath, ASTVisitor.VISIT_METHOD_NODE, false)) {
					if(node.get("nodeType").equals("MethodDeclaration")) {
						javaName = file.getName().substring(0, file.getName().length() - IOperateCode.JAVA_EXTENSION.length()); 
						descMap = (Map<String, Object>) node.get("nodeDesc");
						logger.debug("-javaName: {}, descMap: {}", javaName, descMap);
	    				
						extractMap = new LinkedHashMap<String, Object>();
						extractMap.put("JavaName", packageStr.concat(IOperateCode.STR_DOT).concat(javaName));
						extractMap.put("MethodName", descMap.get("name"));
						extractMap.put("TypeParameters", descMap.get("typeParameters"));
						extractMap.put("InputTypes", descMap.get("parameters"));
						extractMap.put("ReturnType", descMap.get("returnType"));
						extractMap.put("Throws", descMap.get("throws"));
						extractMap.put("Modifiers", descMap.get("modifiers"));
						extractMap.put("Final", descMap.get("final"));
						extractMap.put("Static", descMap.get("static"));
						extractMap.put("Public", descMap.get("public"));
						extractMap.put("Private", descMap.get("private"));
						extractMap.put("Protected", descMap.get("protected"));
						extractMap.put("Abstract", descMap.get("abstract"));
						extractMap.put("Synchronized", descMap.get("synchronized"));
						extractMap.put("Transient", descMap.get("transient"));
						extractMap.put("Native", descMap.get("native"));
						extractMap.put("Strictfp", descMap.get("strictfp"));
						extractMap.put("Volatile", descMap.get("volatile"));
								
						elements.add(extractMap);
					}
				}
			}
		}
		
		// elements 을 이용하여 분석결과를 엑셀에 저장한다.
		logger.debug("elements.size: {}", elements.size());
		return elements;
	}

	public Map<String, List<Map<String, Object>>> getAnalyzedMap() {
		return analyzedMap;
	}

	public void setAnalyzedMap(Map<String, List<Map<String, Object>>> analyzedMap) {
		this.analyzedMap = analyzedMap;
	}

	public String getExcelPath() {
		return excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	public String getWriteExcelName() {
		return writeExcelName;
	}

	public void setWriteExcelName(String writeExcelName) {
		this.writeExcelName = writeExcelName;
	}

	public String getSourceRoot() {
		return sourceRoot;
	}

	public void setSourceRoot(String sourceRoot) {
		this.sourceRoot = sourceRoot;
	}

	public String getExcelVersionDateFormat() {
		return excelVersionDateFormat;
	}

	public void setExcelVersionDateFormat(String excelVersionDateFormat) {
		this.excelVersionDateFormat = excelVersionDateFormat;
	}
	
	
}
