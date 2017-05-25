package com.universal.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.body.ModifierSet;
import com.universal.code.ast.java.ASTVisitor;
import com.universal.code.bxm.BxmBeanGenerateUtil;
import com.universal.code.bxm.BxmDBIOGenerateUtil;
import com.universal.code.bxm.BxmDBIOmmGenerateUtil;
import com.universal.code.constants.IOperateCode;
import com.universal.code.excel.dto.WorkBookDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.DateUtil;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.StringUtil;


public class GenerateRun {

	private static final Logger logger = LoggerFactory.getLogger(GenerateRun.class);
	
	private static Properties props = new Properties();

	private static String SOURCE_ROOT;
	
	private static String EXCEL_PATH;
	
	private static String READ_EXCEL_NAME;
	
	private static String WRITE_EXCEL_NAME;
	
	private static String EXCEL_VERSION_DATE_FORMAT;
	
	private static Map<String, List<Map<String, Object>>> ANALYZED_MAP;
	
	private ASTVisitor visitor;
	
	private FileUtil fileUtil;
	
	private ExcelWriterUtil excelWriterUtil;
	
	public GenerateRun() {
		visitor = new ASTVisitor();
		fileUtil = new FileUtil();
		excelWriterUtil = new ExcelWriterUtil();
	}
	
	static {
		EXCEL_VERSION_DATE_FORMAT = "yyyyMMddHHmmsss";
		
		// 소스코드 생성대상 업무 DB접속정보
		props.setProperty("jdbc.driverClassName", "oracle.jdbc.driver.OracleDriver");
		props.setProperty("jdbc.url", "jdbc:oracle:thin:@localhost:1521:ora11g");
		props.setProperty("jdbc.username", "DESIGN");
		props.setProperty("jdbc.password", "DESIGN");
		props.setProperty("jdbc.initialSize", "5");
		props.setProperty("jdbc.maxActive", "100");
		props.setProperty("jdbc.maxIdle", "20");
		props.setProperty("jdbc.maxWait", "30000");
		props.setProperty("jdbc.poolPreparedStatements", "true");
		props.setProperty("jdbc.defaultAutoCommit", "false");
		props.setProperty("jdbc.validationQuery", "SELECT 1 FROM DUAL");
		
		// 소스 코드 생성 루트 경로
		SOURCE_ROOT = "C:/Developer/BXMWorkspace/HD-onl/src";
		
		// 프로그램 설계 엑셀 파일경로
		EXCEL_PATH = "N:/03.프로잭트/11.뱅크웨어/01.한국자산신탁/06.프로그램설계";
		
		READ_EXCEL_NAME = "한국자산신탁_분양임대_프로그램설계_ver.1.1.xlsx";
		
		WRITE_EXCEL_NAME = "한국자산신탁_분양임대_프로그램_자바메소드_ver.1.0.xlsx";
		
		ANALYZED_MAP = new LinkedHashMap<String, List<Map<String, Object>>>();
		ANALYZED_MAP.put("kait.hd.hda.onl.sc", null);
		ANALYZED_MAP.put("kait.hd.hda.onl.bc", null);
		ANALYZED_MAP.put("kait.hd.hda.onl.dao", null);
	}
	
	@Test
	public void doGenerate() throws Exception{
		
		//dbioOmmGenerate(); 
		
		//dbioGenerate();

		bxmBeanGenerate(); 
		
		//parseJavaMethodToExcel();
	}
	
	private void parseJavaMethodToExcel() throws Exception {

		for(Entry<String, List<Map<String, Object>>> entry : ANALYZED_MAP.entrySet()) {
			entry.setValue(parseJava(entry.getKey()));

			for(Map<String, Object> item : entry.getValue()) {
				//logger.debug(item.get("javaName") + "	" + item.get("name") + "	" + item.get("annotations"));
			}
			logger.debug("package: '{}' Number of Java methods analyzed: {}", entry.getKey(), entry.getValue().size());
		}
		
		logger.debug("Number of Java packages analyzed: {}", ANALYZED_MAP.size());
		
		// 자바 메소드 분석 결과를 저장할 파일 경로
		String writeExcelPath = EXCEL_PATH.concat("/").concat(WRITE_EXCEL_NAME);
		logger.debug("자바 메소드 분석결과 작성 대상 엑셀 파일 경로: {}", writeExcelPath);
		
		File excelFile = new File(writeExcelPath);
		if(excelFile.exists()) {
			// rename old excel file
			excelWriterUtil.renameExcelFile(writeExcelPath, excelFile, EXCEL_VERSION_DATE_FORMAT);
		}
		
		// create new excel worksheet
		WorkBookDTO workBookDTO = new WorkBookDTO();
		workBookDTO.setFileDir(EXCEL_PATH);
		workBookDTO.setFileName(WRITE_EXCEL_NAME);
		
		String newExcelPath = excelWriterUtil.createExcel(workBookDTO, ANALYZED_MAP);
		
		logger.debug("newExcelPath: {}", newExcelPath);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> parseJava(String packageStr) throws Exception {
		if(StringUtil.isEmpty(packageStr)) {
			throw new ApplicationException("검색 대상 패키지가 존재하지 않습니다.");
		}
				String javaPath = null;
		File[] files = FileUtil.getFiles(SOURCE_ROOT.concat("/").concat(packageStr.replace(".", "/")));
		Map<String, Object> descMap;
		Map<String, Object> extractMap;
		List<Map<String, Object>> elements = new ArrayList<Map<String, Object>>(); 
		String javaName = null;
		for(File file : files){ 
			javaPath = file.getCanonicalPath();
			
			if(javaPath.endsWith(IOperateCode.JAVA_EXTENSION)) {
				//logger.debug("javaPath: {}", javaPath);
				for(Map<String, Object> node : visitor.execute(javaPath, ASTVisitor.VISIT_METHOD_NODE, false)) {
					if(node.get("nodeType").equals("MethodDeclaration")) {
						javaName = file.getName().substring(0, file.getName().length() - IOperateCode.JAVA_EXTENSION.length()); 
						descMap = (Map<String, Object>) node.get("nodeDesc");
						logger.debug("-javaName: {}, descMap: {}", javaName, descMap);
	    				
						extractMap = new LinkedHashMap<String, Object>();
						extractMap.put("JavaName", javaName);
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
	
	public void dbioOmmGenerate(){ 
		
		BxmDBIOmmGenerateUtil ommGen = new BxmDBIOmmGenerateUtil();
		ommGen.setSourceRoot(SOURCE_ROOT);
		ommGen.setJavaPackage("kait.hd.hda.onl.dao.dto");
		ommGen.setDatabaseConfig(props);
		ommGen.setCreateFile(true);
		ommGen.setFileNamePrefix("D");
		ommGen.setFileNamePostfix("IO");
		ommGen.setInTables(null); // 특정 테이블의 OMM을 만들경우 설정한다 ex) '테이블01', '테이블02', '테이블03'
		ommGen.setFixedOmmTags(null); // 특정 필드를 모든 OMM에 적용할경우 ArrayList에 담아 설정한다 ex) 
									  // list.add("String txType<length=\"1\" description=\"트렌젝션 타입(CRUD)\"  >;")
									  // list.add("String recStat<length=\"1\" description=\"레코드 상태(O:정상,D:삭제)\"  >;")
		
		// create db table omm 
		ommGen.execute();
	}
	
	public void dbioGenerate(){ 
		
		BxmDBIOGenerateUtil dbioGen = new BxmDBIOGenerateUtil();
		dbioGen.setSourceRoot(SOURCE_ROOT);
		dbioGen.setBasePackage("kait.hd.hda.onl");
		dbioGen.setSubPackage("dao");
		dbioGen.setDatabaseConfig(props);
		dbioGen.setCreateFile(true);
		dbioGen.setDatasourceName("MainDS");
		dbioGen.setFileNamePrefix("D");
		
		// create db table dbio ( interface and mapper sql )
		dbioGen.execute();
	}

	public void bxmBeanGenerate() {
		logger.debug("[START] bxmBeanGenerate");
		BxmBeanGenerateUtil beanGen = new BxmBeanGenerateUtil();
		beanGen.setSourceRoot(SOURCE_ROOT);
		beanGen.setBasePackage("kait.hd.hda.onl");
		beanGen.setSubPackage("bc");
		beanGen.setCreateFile(true);
		beanGen.setFileNamePrefix("B");
		beanGen.setExcelPath(EXCEL_PATH.concat("/").concat(READ_EXCEL_NAME));
		
		// create bean with program design excel
		beanGen.execute();
		logger.debug("[END] bxmBeanGenerate");
	}
	
}
