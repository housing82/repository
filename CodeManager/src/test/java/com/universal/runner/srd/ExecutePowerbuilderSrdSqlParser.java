package com.universal.runner.srd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.sql.JSQLParser;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.RegexUtil;
import com.universal.code.utils.SystemUtil;

public class ExecutePowerbuilderSrdSqlParser {

	private final static Logger logger = LoggerFactory.getLogger(ExecutePowerbuilderSrdSqlParser.class);
	
	private FileUtil fileUtil; 
	
	private RegexUtil regexUtil; 
	
	private JSQLParser jsqlParser;
	
	private SqlParserHelper sqlParserHelper;
	
	private final static String PBL_ROOT_PATH;
	private final static String PBL_PROJECT_PATH;
	
	public final static String PBL_RESOURCE_PATH;
	private final static String TARGET_EXT;
	public final static String SPECIAL_CHARACTER;
	public final static String PATTERN_STRING;
	
	//카타가나 : ァ-ン
	//히라가나 : あ-ん
	//당용한자(간지) : 亜-穏下-懇左-損丼-他濃-那把-盆問-麻冶-翼拉-論和-腕
	
	static {
		PBL_ROOT_PATH = "C:/Developer/AS-IS/KAIT_ERP/asisProject/kait-pbl-dump/pbl";
		PBL_PROJECT_PATH = "/hd";
		
		PBL_RESOURCE_PATH = PBL_ROOT_PATH.concat(PBL_PROJECT_PATH);
		TARGET_EXT = ".srd";
		
		SPECIAL_CHARACTER = "[$][*][+][|]\\(\\)\\[\\]\\{\\}\\<\\>\\^\\-?;:~!=@#%&\\_/\\\\,\\.　`'！＇，．／：；？＾＿｀｜￣、。·‥…¨〃―∥＼∼´～ˇ˘˝˚˙¸˛¡¿ː＂（）［］｛｝‘’“”〔〕〈〉《》「」『』【】＋－＜＝＞±×÷≠≤≥∞∴♂♀∠⊥⌒∂∇≡≒≪≫√∽∝∵∫∬∈∋⊆⊇⊂⊃∪∩∧∨￢⇒⇔∀∃∮∑∏＄％￦Ｆ′″℃Å￠￡￥¤℉‰€㎕㎖㎗ℓ㎘㏄㎣㎤㎥㎦㎙㎚㎛㎜㎝㎞㎟㎠㎡㎢㏊㎍㎎㎏㏏㎈㎉㏈㎧㎨㎰㎱㎲㎳㎴㎵㎶㎷㎸㎹㎀㎁㎂㎃㎄㎺㎻㎼㎽㎾㎿㎐㎑㎒㎓㎔Ω㏀㏁㎊㎋㎌㏖㏅㎭㎮㎯㏛㎩㎪㎫㎬㏝㏐㏓㏃㏉㏜㏆＃＆＊＠§※☆★○●◎◇◆□■△▲▽▼→←↑↓↔〓◁◀▷▶♤♠♡♥♧♣⊙◈▣◐◑▒▤▥▨▧▦▩♨☏☎☜☞¶†‡↕↗↙↖↘♭♩♪♬㉿㈜№㏇™㏂㏘℡®ªº─│┌┐┘└├┬┤┴┼━┃┏┓┛┗┣┳┫┻╋┠┯┨┷┿┝┰┥┸╂┒┑┚┙┖┕┎┍┞┟┡┢┦┧┩┪┭┮┱┲┵┶┹┺┽┾╀╁╃╄╅╆╇╈╉╊㉠㉡㉢㉣㉤㉥㉦㉧㉨㉩㉪㉫㉬㉭㉮㉯㉰㉱㉲㉳㉴㉵㉶㉷㉸㉹㉺㉻㈀㈁㈂㈃㈄㈅㈆㈇㈈㈉㈊㈋㈌㈍㈎㈏㈐㈑㈒㈓㈔㈕㈖㈗㈘㈙㈚㈛ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂０１２３４５６７８９ⅰⅱⅲⅳⅴⅵⅶⅷⅸⅹⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ½⅓⅔¼¾⅛⅜⅝⅞¹²³⁴ⁿ₁₂₃₄ㄱㄲㄳㄴㄵㄶㄷㄸㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅃㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣㅥㅦㅧㅨㅩㅪㅫㅬㅭㅮㅯㅰㅱㅲㅳㅴㅵㅶㅷㅸㅹㅺㅻㅼㅽㅾㅿㆀㆁㆂㆃㆄㆅㆆㆇㆈㆉㆊㆋㆌㆍㆎＡBCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩαβγδεζηθικλμνξοπρστυφχψωÆÐĦĲĿŁØŒÞŦŊæđðħıĳĸŀłøœßŧŋŉぁあぃいぅうぇえぉおかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをんァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヰヱヲンヵヶАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюВ";
		PATTERN_STRING = "retrieve=\"([a-zA-Zㄱ-ㅎ가-힣0-9ァ-ンあ-ん亜-穏下-懇左-損丼-他濃-那把-盆問-麻冶-翼拉-論和-腕"+SPECIAL_CHARACTER+" 	\r\n\t]+)\"";
		
	}
	
	public ExecutePowerbuilderSrdSqlParser() {
		fileUtil = new FileUtil(); 
		regexUtil = new RegexUtil();
		jsqlParser = new JSQLParser();
		sqlParserHelper = new SqlParserHelper();
	}
	
	@Test
	public void parsePblDumpSql() throws IOException {
		parsePblDumpSql(null, null, null, null);
	}
	
	
	
	public String parsePblDumpSql(String baseDir, Map<String, Integer> hdAllTables,	Map<String, Integer> hdAllFunctions, Map<String, Integer> hdAllProcedures) throws IOException {
		
		File basePath = new File(PBL_RESOURCE_PATH);
		if(baseDir != null) {
			basePath = new File(baseDir);
		}
		else {
			basePath = new File(PBL_RESOURCE_PATH);
		}
		
		StringBuilder sqlStb = null;
		StringBuilder sqlAllStb = new StringBuilder();
		List<String> sqlList = null;
		Map<String, Object> reseut = null;
		
		if(basePath.exists() && basePath.isDirectory()) {
			
			List<File> fileList = new ArrayList<File>();
			
			fileUtil.addFileList(fileList, basePath, TARGET_EXT);
			
			sqlList = new ArrayList<String>(); 
			String fileContents = null;
			File currentDir = null;
			String fileName = null;
			for(File file : fileList) {
				 
				if(currentDir == null || !currentDir.getPath().equals(file.getParentFile().getPath())) {
					//디렉토리가 변경되면 이전에 추출한 내용을 파일에 저장한다.
					//디렉토리 1개당 처리					
					if(sqlStb != null) {
						logger.debug(sqlStb.toString());
						fileName = currentDir.getPath().substring(currentDir.getPath().lastIndexOf(File.separator)).concat(".extract.sql");
						fileUtil.mkfile(currentDir.getPath(), fileName, sqlStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
						
						sqlAllStb.append(sqlStb.toString());
						sqlAllStb.append(SystemUtil.LINE_SEPARATOR);
					}
					
					currentDir = file.getParentFile();
					sqlStb = new StringBuilder();
					logger.debug("* 디렉토리 변경: {}", currentDir.getPath());
				}
								
				fileContents = fileUtil.getTextFileContent(file, "UTF-16LE");
				
				if(fileContents.contains("retrieve=")) {
					int checkCnt = regexUtil.findPatternToList(fileContents, "(^retrieve=| retrieve=|	retrieve=)").size();
					
					//retrieve 를 포함하는 파일 1개 처리
					logger.debug(" - Taget Path: {}", file.getPath());
					//logger.debug("[fileContents]\n{}", fileContents);
	
					if(fileContents.contains("~\"")) {
						fileContents = fileContents.replace("~\"", "");
					}
					
					
					sqlList = regexUtil.findPatternToList(fileContents, PATTERN_STRING);
					
					for(String sql : sqlList) {
						//logger.debug("[sql]\n{}", sql);
						sql = sql.trim();
						sql = sql.substring("retrieve=\"".length(), sql.length() - "\"".length());
						
						sqlStb.append("★ Sql From : ");
						sqlStb.append(getQualifiedName(file.getPath(), PBL_ROOT_PATH));
						sqlStb.append(SystemUtil.LINE_SEPARATOR);
						sqlStb.append(SystemUtil.LINE_SEPARATOR);
						sqlStb.append(sql);
						sqlStb.append(SystemUtil.LINE_SEPARATOR);
						sqlStb.append("■■■■■■■■■■■■ < PARSE IN/OUT COLUMN & TABLE > ■■■■■■■■■■■■");
						sqlStb.append(SystemUtil.LINE_SEPARATOR);

						logger.debug("■[START SQL]■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
						logger.debug("-sql:\n{}", sql);
						
						reseut = jsqlParser.parseSQL(sql);
						
						logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
						
						if( reseut.get(JSQLParser.STATEMENT) != null ) {
							logger.debug("SQL Statement type: {}", reseut.get(JSQLParser.STATEMENT));
							
							sqlStb.append("#SQL Statement type: ");
							sqlStb.append(reseut.get(JSQLParser.STATEMENT));
							sqlStb.append(SystemUtil.LINE_SEPARATOR);
						}
						sqlStb.append(SystemUtil.LINE_SEPARATOR);
						
						logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
						
						if( reseut.get(JSQLParser.TABLE_KEY) != null ) {
							for(Map<String, Object> item : (List<Map<String, Object>>) reseut.get(JSQLParser.TABLE_KEY) ){
								logger.debug("TABLE : {}", item);
								
								sqlStb.append("TABLE : ");
								sqlStb.append(item);
								sqlStb.append(SystemUtil.LINE_SEPARATOR);
								
								if((String) item.get("TABLE_NAME") != null && !((String) item.get("TABLE_NAME")).startsWith("(")) {
									sqlParserHelper.setMapKeySequnceValue(hdAllTables, (String) item.get("TABLE_NAME"));
								}
							}
						}
						sqlStb.append(SystemUtil.LINE_SEPARATOR);
						
						logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
						
						if( reseut.get(JSQLParser.INPUT_KEY) != null ) {
							for(Map<String, Object> item : (List<Map<String, Object>>) reseut.get(JSQLParser.INPUT_KEY) ){
								logger.debug("INPUT Parameter: {}", item);
								
								sqlStb.append("INPUT Parameter: ");
								sqlStb.append(item);
								sqlStb.append(SystemUtil.LINE_SEPARATOR);
								
								if((String) item.get("TYPE") != null && ((String) item.get("TYPE")).equals("Function")) {
									sqlParserHelper.setMapKeySequnceValue(hdAllFunctions, (String) item.get("FUNCTION_NAME"));
								}
							}
						}						
						sqlStb.append(SystemUtil.LINE_SEPARATOR);
						logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
						
						
						if( reseut.get(JSQLParser.OUTPUT_KEY) != null ) {
							for(Map<String, Object> item : (List<Map<String, Object>>) reseut.get(JSQLParser.OUTPUT_KEY) ){
								logger.debug("OUTPUT ColumnInfo: {}", item);
								
								sqlStb.append("OUTPUT ColumnInfo: ");
								sqlStb.append(item);
								sqlStb.append(SystemUtil.LINE_SEPARATOR);
								
								if((String) item.get("TYPE") != null && ((String) item.get("TYPE")).equals("Function")) {
									sqlParserHelper.setMapKeySequnceValue(hdAllFunctions, (String) item.get("FUNCTION_NAME"));
								}
							}
						}
						sqlStb.append(SystemUtil.LINE_SEPARATOR);
						logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
						
						sqlStb.append("■■■■■■■■■■■■■■■■■■■■■■■■■■■ < SEPARATOR > ■■■■■■■■■■■■■■■■■■■■■■■■■■■");
						sqlStb.append(SystemUtil.LINE_SEPARATOR);
						sqlStb.append(SystemUtil.LINE_SEPARATOR);
					}
					
					logger.debug("checkCnt: {}, sqlList.size(): {}", checkCnt, sqlList.size());
					if(checkCnt != sqlList.size()) {
						throw new ApplicationException("checkCnt: {}, sqlList.size(): {}", checkCnt, sqlList.size());
					}
					logger.debug("###################################################");
				}

			} //E. for fileList 
			
			if(currentDir != null /*|| !currentDir.getPath().equals(file.getParentFile().getPath())*/) {
				//디렉토리가 변경되면 이전에 추출한 내용을 파일에 저장한다.
				//디렉토리 1개당 처리					
				if(sqlStb != null) {
					logger.debug(sqlStb.toString());
					fileName = currentDir.getPath().substring(currentDir.getPath().lastIndexOf(File.separator)).concat(".extract.sql");
					fileUtil.mkfile(currentDir.getPath(), fileName, sqlStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
					sqlAllStb.append(sqlStb.toString());
					sqlAllStb.append(SystemUtil.LINE_SEPARATOR);
				}

				logger.debug("* 마지막 라인 처리: {}", currentDir.getPath());
			}
			
			if(baseDir == null) {
				fileUtil.mkfile(PBL_RESOURCE_PATH, "all_sql_extract.sql", sqlAllStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
			}
		}
		else {
			throw new ApplicationException("파일이 존재하지 않거나 디렉토리가 아닙니다. 경로: {}", basePath.getPath());
		}
		
		return sqlAllStb.toString();
	}
	
	
	private String getQualifiedName(String resourcePath, String removeString) {
		
		String removeStr = removeString.replace("/", ".").replace("\\", ".");
		String resource = resourcePath.replace("/", ".").replace("\\", ".").replace(removeStr, "").trim();
		if(resource.startsWith(".")) {
			resource = resource.substring(".".length());
		}
		
		return resource;
	}
	
	//@Test
	public void extractTest() {
		StringBuilder sql = new StringBuilder();
		
		sql.append(" retrieve=\"SELECT a.loan_kind_code || a.bank_code AS Acode,");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("	    RPAD( a.loan_kind_name, 13, '　') || RPAD(  a.bank_name , 21, '　') ||");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		 LPAD(TRIM(TO_CHAR(a.loan_amt, '9,999,999,999,999')), 12, '　')  AS Aname,");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("	    TO_CHAR(a.contents_int_rate,'9,999.00') AS bcode, ");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		 TO_CHAR(a.contents_int_rate,'9,999.00') AS bname,");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		 a.amt");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("FROM ");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("(");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append(" 	    SELECT b.loan_kind_code, MAX(c.VALID_VALUE_NAME) AS loan_kind_name,  ");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("				    b.bank_code, MAX(d.bank_name) AS bank_name,");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("					 sum(b.loan_amt) loan_amt,");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("					 b.contents_int_rate,");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("                SUM(a.dr_amt) AS amt");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		  FROM AM_SLIP_DETAIL a,");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		       AM_LOAN_MASTER b,");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("				 AM_CODE_VALIDATION c,");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("				 AM_CODE_BANK d");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		 WHERE a.loan_no = b.loan_no ");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		   AND c.VALID_VALUE_CODE = b.loan_kind_code");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("  		   AND a.company_code          = :as_company ");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		  AND SUBSTR(a.slip_date,1,6)   BETWEEN :as_fyyyy AND :as_tyyyy");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		 	AND c.VALIDATION_CODE = '62'");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("			AND d.bank_code = b.bank_code");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("	      and a.acnt_code in (select calc.acnt_code ");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("													from am_code_calc_acnt calc");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("													where calc.calc_kind_code = '000'");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("														and calc.calc_list_code = '99990702' /*차입금이자 계정*/ )");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("	 GROUP BY b.loan_kind_code, b.bank_code, b.contents_int_rate");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		HAVING  SUM(a.dr_amt) <> 0) a");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("\" arguments=((\"as_company\", string),(\"as_fyyyy\", string),(\"as_tyyyy\", string))  sort=\"acode A bcode A \" )");
		sql.append(SystemUtil.LINE_SEPARATOR);
		 
		logger.debug(PATTERN_STRING);
		
		for(String item : regexUtil.findPatternToList(sql.toString(), PATTERN_STRING)) {
			logger.debug("[sql]\n{}", item);
		}
	}
	

	//@Test
	public void parser() {
		
		StringBuilder sql = new StringBuilder();
		
		
		sql.append("SELECT * FROM (");
		sql.append("SELECT A.DEPT_CODE,	\n");
		sql.append("	B.DEPT_NAME,	\n");
		sql.append("	A.HOUSETAG, C.NM,	\n");
		sql.append("	(SELECT COLUMN_K FROM TEST_TABLE WHERE A = 1) SDATA	\n");
		sql.append("FROM HD_CODE_HOUSE A,	\n");
		sql.append("	HD_CODE_DEPT B,	\n");
		sql.append("	HD_CODE_COMM C	\n");
		sql.append("WHERE A.DEPT_CODE = B.DEPT_CODE	\n");
		sql.append("	AND A.HOUSETAG = C.CODE	\n");
		sql.append("	AND C.GUBUN    = '03'	\n");
		sql.append("	AND EXISTS (SELECT E.PROJ_CODE	\n");
		sql.append("FROM SM_AUTH_USER D,	\n");
		sql.append("      SM_AUTH_PROJECT E	\n");
		sql.append("WHERE D.GROUP_CODE   = E.GROUP_CODE	\n");
		sql.append("  AND D.COMPANY_CODE = E.COMPANY_CODE	\n");
		sql.append("  AND E.PROJ_CODE    = A.DEPT_CODE	\n");
		sql.append("  AND D.USER_ID      = :as_emp	\n");
		sql.append(")	\n");
		sql.append("AND NVL(B.LISTTAG, '1') <> '2'	\n");		
		sql.append(")	\n");
		
		
		Map<String, Object> reseut = jsqlParser.parseSQL(sql.toString());
		
		logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		
		if( reseut.get(JSQLParser.STATEMENT) != null ) {
			logger.debug("SQL Statement type : " + reseut.get(JSQLParser.STATEMENT));
		}
		logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		
		if( reseut.get(JSQLParser.INPUT_KEY) != null ) {
			for(String item : (List<String>) reseut.get(JSQLParser.INPUT_KEY) ){
				logger.debug("INPUT Col : " + item);
			}
		}
		logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		
		if( reseut.get(JSQLParser.OUTPUT_KEY) != null ) {
			for(Map<String, Object> item : (List<Map<String, Object>>) reseut.get(JSQLParser.OUTPUT_KEY) ){
				logger.debug("OUTPUT Col : " + item);
			}
		}
		logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		
	}
	
}
