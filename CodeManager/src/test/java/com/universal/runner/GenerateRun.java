package com.universal.runner;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.bxm.BxmBeanGenerateUtil;
import com.universal.code.bxm.BxmDBIOGenerateUtil;
import com.universal.code.bxm.BxmDBIOmmGenerateUtil;
import com.universal.code.bxm.BxmServiceGenerateUtil;
import com.universal.code.bxm.GenerateHelper;
import com.universal.code.coder.URLCoder;
import com.universal.code.utils.ASTMethodToExcelUtil;


public class GenerateRun {

	private static final Logger logger = LoggerFactory.getLogger(GenerateRun.class);
	
	private static Properties props = new Properties();

	private static String SOURCE_ROOT;
	
	private static String EXCEL_PATH;
	
	private static String READ_EXCEL_NAME;
	
	private static String WRITE_EXCEL_NAME; 
	
	private static String EXCEL_VERSION_DATE_FORMAT;
	
	private static String BASE_PACKAGE;
	
	private static Map<String, List<Map<String, Object>>> ANALYZED_MAP;

	public GenerateRun() {
		// ...
	}
	
	static {
		EXCEL_VERSION_DATE_FORMAT = "yyyyMMddHHmmsss";
		
		// 소스코드 생성대상 업무 DB접속정보
		props.setProperty("jdbc.driverClassName", "oracle.jdbc.driver.OracleDriver");
		props.setProperty("jdbc.url", "jdbc:oracle:thin:@localhost:1521:ora11g");
		props.setProperty("jdbc.username", "KAIT_LOCAL");
		props.setProperty("jdbc.password", "kait_local");
		props.setProperty("jdbc.initialSize", "5");
		props.setProperty("jdbc.maxActive", "100");
		props.setProperty("jdbc.maxIdle", "20");
		props.setProperty("jdbc.maxWait", "30000");
		props.setProperty("jdbc.poolPreparedStatements", "true");
		props.setProperty("jdbc.defaultAutoCommit", "false");
		props.setProperty("jdbc.validationQuery", "SELECT 1 FROM DUAL");
		
		// 소스 코드 생성 루트 경로
		SOURCE_ROOT = "D:/Developer/BXMWorkspace/HD-onl/src";

		// 프로그램 설계 엑셀 파일경로
		//EXCEL_PATH = "N:/03.프로잭트/11.뱅크웨어/01.한국자산신탁/06.프로그램설계";
		
		// 클라우드 접속이 안되었을경우 로컬에 있는 파일을 봄
		//if(!new File(EXCEL_PATH).exists()) {
			String currenPath = GenerateRun.class.getClassLoader().getResource(".").getPath();
			EXCEL_PATH = new StringBuilder().append(URLCoder.getInstance().getURLDecode(currenPath, "UTF-8")).append("../../document").toString();
		//}
		logger.debug("EXCEL_PATH: {}", EXCEL_PATH);
		
		READ_EXCEL_NAME = "한국자산신탁_분양임대_프로그램설계_ver.1.1.xlsx";
		
		WRITE_EXCEL_NAME = "한국자산신탁_분양임대_프로그램_자바메소드_ver.1.0.xlsx";
		
		BASE_PACKAGE = "kait.hd.hda.onl";
		
		ANALYZED_MAP = new LinkedHashMap<String, List<Map<String, Object>>>();
		ANALYZED_MAP.put("kait.hd", null);
	}
	
	@Test
	public void doGenerate() throws Exception{
		
		//dbioOmmGenerate(); 

		//dbioGenerate();

		//bxmBeanGenerate(); 
		
		bxmServiceGenerate();
		
		//parseJavaMethodToExcel();
		
		//testOmmParser();
		
	}
	
	private void testOmmParser() throws Exception {
		
		File ommFile = new File("C:/Developer/BXMWorkspace/HD-onl/src/kait/hd/hda/onl/dao/dto/DHdCodeSihang01IO.omm");
		new GenerateHelper().getOmmProperty(ommFile);
	}
	
	private void parseJavaMethodToExcel() throws Exception {
		logger.debug("[START] parseJavaMethodToExcel");
		
		ASTMethodToExcelUtil methodToExcel = new ASTMethodToExcelUtil(); 
		
		methodToExcel.setAnalyzedMap(ANALYZED_MAP);
		methodToExcel.setExcelPath(EXCEL_PATH);
		methodToExcel.setExcelVersionDateFormat(EXCEL_VERSION_DATE_FORMAT);
		methodToExcel.setSourceRoot(SOURCE_ROOT);
		methodToExcel.setWriteExcelName(WRITE_EXCEL_NAME);
		
		methodToExcel.execute();
		logger.debug("[END] parseJavaMethodToExcel");
	}
	
	
	private void dbioOmmGenerate(){ 
		logger.debug("[START] dbioOmmGenerate");
		
		BxmDBIOmmGenerateUtil ommGen = new BxmDBIOmmGenerateUtil();
		ommGen.setSourceRoot(SOURCE_ROOT);
		ommGen.setBasePackage("kait.{L2}.{L3}.onl");
		ommGen.setSubPackage("dao.dto");		
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
		logger.debug("[END] dbioOmmGenerate");
	}
	
	private void dbioGenerate(){ 
		logger.debug("[START] dbioGenerate");
		
		BxmDBIOGenerateUtil dbioGen = new BxmDBIOGenerateUtil();
		dbioGen.setSourceRoot(SOURCE_ROOT);
		dbioGen.setBasePackage("kait.{L2}.{L3}.onl");
		dbioGen.setSubPackage("dao");
		dbioGen.setDatabaseConfig(props);
		dbioGen.setCreateFile(true);
		dbioGen.setDatasourceName("MainDS");
		dbioGen.setFileNamePrefix("D");

		// create db table dbio ( interface and mapper sql )
		dbioGen.execute();
		logger.debug("[END] dbioGenerate");
	}

	private void bxmBeanGenerate() {
		logger.debug("[START] bxmBeanGenerate");
		
		BxmBeanGenerateUtil beanGen = new BxmBeanGenerateUtil();
		beanGen.setSourceRoot(SOURCE_ROOT);
		beanGen.setBasePackage(BASE_PACKAGE);
		beanGen.setSubPackage("bc");
		beanGen.setCreateFile(true);
		beanGen.setFileNamePrefix("B");
		beanGen.setExcelPath(EXCEL_PATH.concat("/").concat(READ_EXCEL_NAME));
		
		// create bean with program design excel
		beanGen.execute();
		logger.debug("[END] bxmBeanGenerate");
	}

	
	private void bxmServiceGenerate() {
		logger.debug("[START] bxmServiceGenerate");
		
		BxmServiceGenerateUtil serviceGen = new BxmServiceGenerateUtil();
		serviceGen.setSourceRoot(SOURCE_ROOT);
		serviceGen.setBasePackage(BASE_PACKAGE);
		serviceGen.setSubPackage("sc");
		serviceGen.setCreateFile(true);
		serviceGen.setFileNamePrefix("S");
		serviceGen.setExcelPath(EXCEL_PATH.concat("/").concat(READ_EXCEL_NAME));
		
		// create bean with program design excel
		serviceGen.execute();
		logger.debug("[END] bxmServiceGenerate");
	}
	
}
