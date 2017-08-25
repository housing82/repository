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
	
	private static String MASTER_SYSCD;
	
	private static Map<String, List<Map<String, Object>>> ANALYZED_MAP;

	public GenerateRun() {
		// ...
	}
	
	static {
		EXCEL_VERSION_DATE_FORMAT = "yyyyMMddHHmmsss";
		
		MASTER_SYSCD = "hd";
		
		// 소스코드 생성대상 업무 DB접속정보
		/*
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
		*/
		
		props.setProperty("jdbc.driverClassName", "oracle.jdbc.driver.OracleDriver");
		props.setProperty("jdbc.url", "jdbc:oracle:thin:@192.168.5.12:1522:kait");
		props.setProperty("jdbc.username", "KAIT_TST");
		props.setProperty("jdbc.password", "KAIT_TST");
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
		ommGen.setMasterSyscd(MASTER_SYSCD);
		ommGen.setBasePackage("kait.{L2}.{L3}.onl");
		ommGen.setSubPackage("dao.dto");		
		ommGen.setDatabaseConfig(props);
		ommGen.setCreateFile(true);
		ommGen.setFileNamePrefix("D");
		ommGen.setFileNamePostfix("IO");
		ommGen.setInTables(HD_TABLE_IN_EXPR.toString()); // 특정 테이블의 OMM을 만들경우 설정한다 ex) '테이블01', '테이블02', '테이블03'
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
		dbioGen.setMasterSyscd(MASTER_SYSCD);
		dbioGen.setBasePackage("kait.{L2}.{L3}.onl");
		dbioGen.setSubPackage("dao");
		dbioGen.setDatabaseConfig(props);
		dbioGen.setCreateFile(true);
		dbioGen.setDatasourceName("MainDS");
		dbioGen.setFileNamePrefix("D");
		dbioGen.setTargetTables(HD_TABLE_IN_EXPR.toString());
		
		// create db table dbio ( interface and mapper sql )
		dbioGen.execute();
		logger.debug("[END] dbioGenerate");
	}

	private void bxmBeanGenerate() {
		logger.debug("[START] bxmBeanGenerate : {}", EXCEL_PATH.concat("/").concat(READ_EXCEL_NAME));
		
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
		logger.debug("[START] bxmServiceGenerate : {}", EXCEL_PATH.concat("/").concat(READ_EXCEL_NAME));
		
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
	
	private void testOmmParser() throws Exception {
		
		File ommFile = new File("C:/Developer/BXMWorkspace/HD-onl/src/kait/hd/hda/onl/dao/dto/DHdCodeSihang01IO.omm");
		new GenerateHelper().getOmmProperty(ommFile);
	}
	
	final static StringBuilder HD_TABLE_IN_EXPR; 
	
	static {
		//asis 분양/임대 사용하던 테이블 목록 (타업무 테이블 포함) 
		HD_TABLE_IN_EXPR = new StringBuilder();

		//분양/임대 업무 테이블
		HD_TABLE_IN_EXPR.append(" 'HD_ACMAST_E'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_ACNT'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_AGENCY'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_AGREE'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_COMM'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_COMM_HIST'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_COMPAMT'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_CUSTOMER'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_CUSTOMER_TXTUP'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_DEPOSIT'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_DEPT'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_DEPTMEMO'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_DEPT_HIST'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_GIFT'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_HOUSE'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_HOUSE_HIST'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_OPTION'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_SIHANG'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_SIHANG_HIST'");
		HD_TABLE_IN_EXPR.append(",'HD_CODE_SPECIAL'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_APPLY'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_BANKETC'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_COMPAMT'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_CONSULT'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_DAY_INCOME'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_DAY_INCOME_ADJUST'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_DAY_INCOME_CONV'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_DISK_TEMP'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_ETCAMT'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_INCOME'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_INCOME_ADJUST'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_INCOME_LOG'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_INCOME_REIN'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_INCOME_TEMP'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_INDEMINITY'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_INDEMINITY_CONV'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_JESE'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_LEND'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_LEND_TXTUP'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_MAGAM'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_OPTION'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_RATE_DELAY'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_RATE_DELAY_LOG'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_RATE_DISCOUNT'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_RATE_DISCOUNT_LOG'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_REMARK'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_RESERVE'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SEALHISTORY'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SELL'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SELLDETAIL'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SELLDETAIL_ADJUST'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SELLDETAIL_LOG'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SELLDETAIL_TEMP'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SELL_CONV'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SELL_LOG'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SELL_TXTUP'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SLIP_MAGAM'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SUBSCRIBE'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SUBSCRIBE_TXTUP'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SUPPLY'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SUPPLY_CHANGE'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SUPPLY_CONV'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_SUPPLY_HIST'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_UNION'");
		HD_TABLE_IN_EXPR.append(",'HD_HOUS_VIRTURE'");
		HD_TABLE_IN_EXPR.append(",'HD_LEASE_BONDSEIZURE'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_GIFT'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_HOLIDAY'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_INCOMEBILL'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_INCOMEDATE'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_INCOMEREMARK'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_KEY'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_MOVEIN'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_RATE_DELAY'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_RATE_DISCOUNT'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_SELLCREATE'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_SELLDETAIL'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_SELLDETAIL_CHANGE'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_SELLDETAIL_HIST'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_SQUARE'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_SQUAREDETAIL'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_SQUAREDETAIL_CNT'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_SQUAREDETAIL_CONV'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_SQUARE_CNT'");
		HD_TABLE_IN_EXPR.append(",'HD_REFER_SQUARE_CONV'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_CANCEL'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_DETAIL_RENT'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_DETAIL_TERM'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_DONGHO'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_ETC'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_FIXRATE'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_GIRO'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_GURT_AGREE'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_GURT_AGREE_ADJ'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_GURT_AGREE_TEMP'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_GURT_INCOME'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_GURT_INCOME_ADJ'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_GURT_INCOME_DAILY'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_GURT_INCOME_DAILY_ADJ'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_GURT_INCOME_TEMP'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_MANAGEMENT'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_MANAGEMENT_CONV'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_MASTER'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_RATE_DELAY'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_RATE_DISCOUNT'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_REMARK'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_RENT_AGREE'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_RENT_AGREE_ADJ'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_RENT_AGREE_TEMP'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_RENT_INCOME'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_RENT_INCOME_ADJ'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_RENT_INCOME_DAILY'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_RENT_INCOME_DAILY_ADJ'");
		HD_TABLE_IN_EXPR.append(",'HD_RENT_RENT_INCOME_TEMP'");
		HD_TABLE_IN_EXPR.append(",'HD_SALE_ETC'");
		HD_TABLE_IN_EXPR.append(",'HD_SALE_INCOME_UNKNOWN'");
		HD_TABLE_IN_EXPR.append(",'HD_SLIP_MAIN'");
		HD_TABLE_IN_EXPR.append(",'HD_SLIP_PARENT'");
		HD_TABLE_IN_EXPR.append(",'HD_SUPPLY_CONV'");
		HD_TABLE_IN_EXPR.append(",'HD_TAX_CALCULATE'");
		HD_TABLE_IN_EXPR.append(",'HD_TAX_CALCULATE_AGREE'");
		HD_TABLE_IN_EXPR.append(",'HD_TAX_CALCULATE_HIST'");
		HD_TABLE_IN_EXPR.append(",'HD_TAX_CALCULATE_REAL'");
		HD_TABLE_IN_EXPR.append(",'HD_TAX_CALCULATE_REAL_HIST'");
		HD_TABLE_IN_EXPR.append(",'HD_TAX_CALC_HISTORY'");
		HD_TABLE_IN_EXPR.append(",'HD_TEMP_VIRTURE_DEPOSIT'");
		HD_TABLE_IN_EXPR.append(",'HD_VIRTURE_ACCOUNT'");
		
		//타 업무 테이블
		HD_TABLE_IN_EXPR.append(",'CB3_CVS_DATA'");
		HD_TABLE_IN_EXPR.append(",'AM_CODE_VALIDATION'");
		HD_TABLE_IN_EXPR.append(",'HR_ORDE_MASTER'");
		HD_TABLE_IN_EXPR.append(",'HR_PERS_MASTER'");
		HD_TABLE_IN_EXPR.append(",'MM_COMM_CONTRACT'");
		HD_TABLE_IN_EXPR.append(",'MM_PROJ_ACNTCODE'");
		HD_TABLE_IN_EXPR.append(",'MM_PROJ_CODE_DETAIL'");
		HD_TABLE_IN_EXPR.append(",'SM_AUTH_PROJECT'");
		HD_TABLE_IN_EXPR.append(",'SM_AUTH_USER'");
		HD_TABLE_IN_EXPR.append(",'SM_CODE_BANK_HEAD'");
		HD_TABLE_IN_EXPR.append(",'SM_CODE_CALENDAR'");
		HD_TABLE_IN_EXPR.append(",'SM_CODE_COMPANY'");
		HD_TABLE_IN_EXPR.append(",'SM_CODE_CUST'");
		HD_TABLE_IN_EXPR.append(",'SM_CODE_DEPT'");
		HD_TABLE_IN_EXPR.append(",'SM_CODE_PROJECT'");
		HD_TABLE_IN_EXPR.append(",'SM_CODE_VALIDATION'");
		HD_TABLE_IN_EXPR.append(",'SM_TRANS_CB3_CVS_DATA'");
		HD_TABLE_IN_EXPR.append(",'SM_TRANS_CMS'");
		HD_TABLE_IN_EXPR.append(",'SM_TRANS_GIRO'");
		HD_TABLE_IN_EXPR.append(",'SM_TRANS_VIRTURE_DEPOSIT'");
		HD_TABLE_IN_EXPR.append(",'SM_CODE_ZIP_DATAIMP'");
		HD_TABLE_IN_EXPR.append(",'TM_CODE_ACNT'");
		HD_TABLE_IN_EXPR.append(",'TM_CODE_BANK'");
		HD_TABLE_IN_EXPR.append(",'TM_CODE_CUST_ACNT'");
		HD_TABLE_IN_EXPR.append(",'TM_CODE_DEPOSIT'");
		HD_TABLE_IN_EXPR.append(",'VW_HD_CODE_CUSTOMER'");
		HD_TABLE_IN_EXPR.append(",'VW_HD_HOUS_SELL'");

	}
}
