package com.universal.code.bxm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.constants.JavaReservedWordConstants;
import com.universal.code.dto.OmmDTO;
import com.universal.code.dto.OmmFieldDTO;
import com.universal.code.excel.dto.ExcelDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;

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
				logger.debug("ommType: {}", ommType);
				out.setOmmType(ommType);
			}
			else if(line.startsWith("<description=\"")) {
				ommDesc = line.substring("<description=\"".length(), line.length() - 2);
				logger.debug("ommDesc: {}", ommDesc);
				out.setOmmDesc(ommDesc);
			}
			else if(line.equals("{") || line.equals("}")){
				continue;
			}
			else {
				//logger.debug("line field: {}", line);
				ommFieldDTO = new OmmFieldDTO();
			
				String type = line.substring(0, line.indexOf(" ")).trim();
				String name = line.substring(line.indexOf(" "), line.indexOf("<")).trim();
				String info = line.substring(line.indexOf("<") + "<".length(), line.lastIndexOf(">")).trim();
				String length = info.substring(info.indexOf("length=") + "length=".length(), info.indexOf(" ") + " ".length());
								
				ommFieldDTO.setType(type);
				ommFieldDTO.setName(name);
				ommFieldDTO.setLength(length);

				logger.debug(ommFieldDTO.toString());
				logger.debug("info: {}", info);
			}
		}
		
		return out;
	}
	
}
