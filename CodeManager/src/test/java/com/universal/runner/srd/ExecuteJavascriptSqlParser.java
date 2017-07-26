package com.universal.runner.srd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.sql.JSQLParser;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.JRegexUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;

public class ExecuteJavascriptSqlParser {

	private final static Logger logger = LoggerFactory.getLogger(ExecuteJavascriptSqlParser.class);
	
	private FileUtil fileUtil; 
	
	private JRegexUtil regexUtil; 
	
	private JSQLParser jsqlParser;
	
	private StringUtil stringUtil;
	
	private SqlParserHelper sqlParserHelper;
	
	private ExecutePowerbuilderSrdSqlParser executePowerbuilderSrdSqlParser;
	
	private final static String PBL_ROOT_PATH;
	private final static String PBL_PROJECT_PATH;
	
	private final static String NETJS_RESOURCE_PATH;
	private final static String TARGET_EXT;
	private final static String RECOVER_SEMI_COLON;
	private final static String RECOVER_SEMI_COLON_KEY;
	
	public final static String SPECIAL_CHARACTER;
	public final static String PATTERN_STRING;
	public final static String PATTERN_DELETE_DIV;
	public final static String PATTERN_UPDATE_DIV;
	public final static String INNER_SQL_STRING;
	
	public Map<String, Integer> hdAllTables;
	public Map<String, Integer> hdAllFunctions;
	public Map<String, Integer> hdAllProcedures;
	
	
	
	//카타가나 : ァ-ン
	//히라가나 : あ-ん
	//당용한자(간지) : 亜-穏下-懇左-損丼-他濃-那把-盆問-麻冶-翼拉-論和-腕
	// \r \n \t 공백 : \\s 
	static {
		PBL_ROOT_PATH = "D:/Developer/AS-IS/KAIT_ERP/asisProject/kait-src";
		PBL_PROJECT_PATH = "/";
		
		NETJS_RESOURCE_PATH = PBL_ROOT_PATH.concat(PBL_PROJECT_PATH);
		TARGET_EXT = ".js";
		RECOVER_SEMI_COLON = "___$SemiColon$___";
		// ぁあぃいぅうぇえぉおかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをんァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヰヱヲンヵヶ
		SPECIAL_CHARACTER = "$*+|\\(\\)\\[\\]\\{\\}\\<\\>\\^\\-?:~!=@#%&\\_/\\\\,\\.\"'　`！＇，．／：；？＾＿｀｜￣、。·‥…¨〃―∥＼∼´～ˇ˘˝˚˙¸˛¡¿ː＂（）［］｛｝‘’“”〔〕〈〉《》「」『』【】＋－＜＝＞±×÷≠≤≥∞∴♂♀∠⊥⌒∂∇≡≒≪≫√∽∝∵∫∬∈∋⊆⊇⊂⊃∪∩∧∨￢⇒⇔∀∃∮∑∏＄％￦Ｆ′″℃Å￠￡￥¤℉‰€㎕㎖㎗ℓ㎘㏄㎣㎤㎥㎦㎙㎚㎛㎜㎝㎞㎟㎠㎡㎢㏊㎍㎎㎏㏏㎈㎉㏈㎧㎨㎰㎱㎲㎳㎴㎵㎶㎷㎸㎹㎀㎁㎂㎃㎄㎺㎻㎼㎽㎾㎿㎐㎑㎒㎓㎔Ω㏀㏁㎊㎋㎌㏖㏅㎭㎮㎯㏛㎩㎪㎫㎬㏝㏐㏓㏃㏉㏜㏆＃＆＊＠§※☆★○●◎◇◆□■△▲▽▼→←↑↓↔〓◁◀▷▶♤♠♡♥♧♣⊙◈▣◐◑▒▤▥▨▧▦▩♨☏☎☜☞¶†‡↕↗↙↖↘♭♩♪♬㉿㈜№㏇™㏂㏘℡®ªº─│┌┐┘└├┬┤┴┼━┃┏┓┛┗┣┳┫┻╋┠┯┨┷┿┝┰┥┸╂┒┑┚┙┖┕┎┍┞┟┡┢┦┧┩┪┭┮┱┲┵┶┹┺┽┾╀╁╃╄╅╆╇╈╉╊㉠㉡㉢㉣㉤㉥㉦㉧㉨㉩㉪㉫㉬㉭㉮㉯㉰㉱㉲㉳㉴㉵㉶㉷㉸㉹㉺㉻㈀㈁㈂㈃㈄㈅㈆㈇㈈㈉㈊㈋㈌㈍㈎㈏㈐㈑㈒㈓㈔㈕㈖㈗㈘㈙㈚㈛ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂０１２３４５６７８９ⅰⅱⅲⅳⅴⅵⅶⅷⅸⅹⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ½⅓⅔¼¾⅛⅜⅝⅞¹²³⁴ⁿ₁₂₃₄ㄱㄲㄳㄴㄵㄶㄷㄸㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅃㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣㅥㅦㅧㅨㅩㅪㅫㅬㅭㅮㅯㅰㅱㅲㅳㅴㅵㅶㅷㅸㅹㅺㅻㅼㅽㅾㅿㆀㆁㆂㆃㆄㆅㆆㆇㆈㆉㆊㆋㆌㆍㆎＡBCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩαβγδεζηθικλμνξοπρστυφχψωÆÐĦĲĿŁØŒÞŦŊæđðħıĳĸŀłøœßŧŋŉАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюВ";
		INNER_SQL_STRING = "a-zA-Zㄱ-ㅎ가-힣0-9ァ-ンあ-ん亜-穏下-懇左-損丼-他濃-那把-盆問-麻冶-翼拉-論和-腕";
		PATTERN_STRING = new StringBuilder().append("(vSql|vProc)([\\s]+)?=([\\s]+)?\"([").append(INNER_SQL_STRING).append(SPECIAL_CHARACTER).append("\\s]+)([\\s]+)?\";").toString();
		RECOVER_SEMI_COLON_KEY = "!".concat(RECOVER_SEMI_COLON).concat("!");
		
		PATTERN_DELETE_DIV = new StringBuilder().append("(DELETE|TRUNCATE)([").append(INNER_SQL_STRING).append(SPECIAL_CHARACTER).append("\\s]+)([\\s]+)?';").toString();
		PATTERN_UPDATE_DIV = new StringBuilder().append("(UPDATE|INSERT)([").append(INNER_SQL_STRING).append(SPECIAL_CHARACTER).append("\\s]+)([\\s]+)?(\\)|');").toString();
		
	}
	
	
	
	public ExecuteJavascriptSqlParser() {
		fileUtil = new FileUtil(); 
		regexUtil = new JRegexUtil();
		jsqlParser = new JSQLParser();
		stringUtil = new StringUtil();
		executePowerbuilderSrdSqlParser = new ExecutePowerbuilderSrdSqlParser();
		sqlParserHelper = new SqlParserHelper();
		
		//임대분양 전채에서 사용하는 테이블,펑션,프로시저 목록
		hdAllTables = new LinkedHashMap<String, Integer>();
		hdAllFunctions = new LinkedHashMap<String, Integer>();
		hdAllProcedures = new LinkedHashMap<String, Integer>();
	}
	

	@Test
	public void execute() throws IOException {
		parseJavascript();
		
		//executePowerbuilderSrdSqlParser.parsePblDumpSql();
		
		String fileName = null;
		StringBuilder sqlStb = null;
		
		//tables
		fileName = "all_table_extract.sql";
		sqlStb = new StringBuilder();
		for(Entry<String, Integer> entry : hdAllTables.entrySet()) {
			sqlStb.append(entry.getKey());
			sqlStb.append(": ");
			sqlStb.append(entry.getValue());
			sqlStb.append(SystemUtil.LINE_SEPARATOR);
		}
		fileUtil.mkfile(NETJS_RESOURCE_PATH, fileName, sqlStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
		
		//functions
		fileName = "all_function_extract.sql";
		sqlStb = new StringBuilder();
		for(Entry<String, Integer> entry : hdAllFunctions.entrySet()) {
			sqlStb.append(entry.getKey());
			sqlStb.append(": ");
			sqlStb.append(entry.getValue());
			sqlStb.append(SystemUtil.LINE_SEPARATOR);
		}
		fileUtil.mkfile(NETJS_RESOURCE_PATH, fileName, sqlStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
		
		//functions
		fileName = "all_procedure_extract.sql";
		sqlStb = new StringBuilder();
		for(Entry<String, Integer> entry : hdAllProcedures.entrySet()) {
			sqlStb.append(entry.getKey());
			sqlStb.append(": ");
			sqlStb.append(entry.getValue());
			sqlStb.append(SystemUtil.LINE_SEPARATOR);
		}
		fileUtil.mkfile(NETJS_RESOURCE_PATH, fileName, sqlStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
		
	}
	
	public void parseJavascript() throws IOException {
		
		File basePath = new File(NETJS_RESOURCE_PATH);
		StringBuilder sqlStb = null;
		StringBuilder sqlAllStb = new StringBuilder();
		StringBuilder sqlProcStb = null;
		List<String> sqlList = null;
		List<String> extractSqlList = null;
		List<String> extractDivSqlList = null;
		String pblSql = null;
		
		if(basePath.exists() && basePath.isDirectory()) {
			
			List<File> fileList = new ArrayList<File>();
			
			fileUtil.addFileList(fileList, basePath, TARGET_EXT);
			
			sqlList = new ArrayList<String>(); 
			String fileContents = null;
			StringBuilder fileContentStb = null;
			File currentFile = null;
			String fileName = null;
			String filePath = null;
			String fileLine = null;
			String[] fileLines = null;
			String[] sqlLine = null;
			File file = null;
			String parseExt = null;
			String pblBasePath = null;
			File pblBaseFile = null;
			//스캔 결과 파일 loop
			for(int i = 0; i < fileList.size(); i++) {
				file = fileList.get(i); 
				
				if(currentFile == null || !currentFile.getPath().equals(file.getPath())) {
					//디렉토리가 변경되면 이전에 추출한 내용을 파일에 저장한다.
					//디렉토리 1개당 처리
					if(sqlStb != null && !sqlStb.toString().isEmpty()) {
						logger.debug("#parseExt: {}", parseExt);
						filePath = currentFile.getParentFile().getPath().concat(File.separator).concat("sql"); 
						fileName = currentFile.getPath().substring(currentFile.getPath().lastIndexOf(File.separator)).concat(parseExt);
						
						/** START##parse powerBuilder source sql */
						executePblParser(filePath, fileName, sqlStb);
						/** END##parse powerBuilder source sql */
						
						logger.debug("[★ 분석결과파일생성]");
						fileUtil.mkfile(filePath, fileName, sqlStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
						sqlAllStb.append(sqlStb.toString());
						sqlAllStb.append(SystemUtil.LINE_SEPARATOR);
						
						//throw new ApplicationException("STOP");
					}
					
					currentFile = file; //.getParentFile();
					sqlStb = new StringBuilder();
					sqlStb.append("◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆ < NETJS_SQL_START > ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆");
					sqlStb.append(SystemUtil.LINE_SEPARATOR);
					
					parseExt = ".extract.sql";
					logger.debug("* 디렉토리 변경: {}", currentFile.getPath());
				}
								
				fileContents = fileUtil.getTextFileContent(file, "EUC-KR");
				
//				fileLines = fileContents.split(SystemUtil.LINE_SEPARATOR);
//				fileContentStb = new StringBuilder();
//				
//				for(int j = 0; j < fileLines.length; j++) {
//					fileLine = fileLines[j];
//					
//					fileContentStb.append("/* ");
//					fileContentStb.append((j + 1));
//					fileContentStb.append(" */");
//					fileContentStb.append(fileLine);
//				}
				
				logger.debug("[START] 파일내부 주석 제거");
				fileContents = regexUtil.replaceAllPattern(fileContents, "(?://.*\n{0,1})|(?:/\\*(?:.|\\s)*?\\*/\n{0,1})", "");
				//fileContents = fileContents.replaceAll("(?://.*\n{0,1})|(?:/\\*(?:.|\\s)*?\\*/\n{0,1})", "");
				logger.debug("[END] 파일내부 주석 제거");
				
				if(fileContents.contains("vSql") || fileContents.contains("vProc")) {  
					int checkCnt = regexUtil.findPatternToList(fileContents, "(vSql|vProc)([\\s]+)?=([\\s]+)?\"").size();
					logger.debug("[checkCnt]: {}", checkCnt);
					fileLines = fileContents.split(SystemUtil.LINE_SEPARATOR);
					
					fileContentStb = new StringBuilder();
					fileLine = null;
					for(int j = 0; j < fileLines.length; j++) {
						fileLine = fileLines[j];
						
						fileLine = stringUtil.rtrim(fileLine);
						fileLine = regexUtil.replaceAllPattern(fileLine, "\"([\\s]+)?;", "\";");
						
						// SQL은 종료되었지만 마지막 종료문자가 "; 가 아닐경우
						// \"([\\s]+)?
						if(regexUtil.testPattern(fileLine, "'([\\s]+)?\"$")) {
							int nextIdx = j + 1; 
							if(nextIdx < fileLines.length) {
								String tempLine = fileLines[nextIdx].trim();
								if(tempLine.isEmpty()) {
									nextIdx = nextIdx + 1; 
									if(nextIdx < fileLines.length) {
										tempLine = fileLines[nextIdx];
									}
								}
								if(tempLine.contains("_ExecSql") || tempLine.contains("_RunSql")) {
									fileLine = fileLine.concat(";");
								}
							}
						}
						
						if(!fileLine.contains("\";") && fileLine.contains(";")) {
							
							//보존 문자열
							// ); " 
							// '; "
							if(regexUtil.testPattern(fileLine, ";([\\s]+)?\"$")) {
								if(!regexUtil.testPattern(fileLine, "\\);([\\s]+)?\"$") && !regexUtil.testPattern(fileLine, "';([\\s]+)?\"$")) {
									fileLine = fileLine.concat(";");
									fileLine = fileLine.replace("\";", RECOVER_SEMI_COLON_KEY);
								}
							}
							
							fileLine = fileLine.replace(";", RECOVER_SEMI_COLON);
						}
						else if(fileLine.contains("\";") && fileLine.contains(";")) {
							fileLine = fileLine.replace("\";", RECOVER_SEMI_COLON_KEY);
							fileLine = fileLine.replace(";", RECOVER_SEMI_COLON);
						}
						
						if(fileLine.contains(RECOVER_SEMI_COLON_KEY)) {
							fileLine = fileLine.replace(RECOVER_SEMI_COLON_KEY, "\";");
						}	
						
						//add contentsLine
						//fileContentStb.append("/* ");
						//fileContentStb.append((j + 1));
						//fileContentStb.append(" */");
						
						fileContentStb.append(fileLine);
						fileContentStb.append(SystemUtil.LINE_SEPARATOR);
					}
					fileContents = fileContentStb.toString();
					
					//retrieve 를 포함하는 파일 1개 처리
					logger.debug(" - Taget Path: {}", file.getPath());
					//logger.debug(" - fileContents:\n{}", fileContents);
					logger.debug(" [PATTERN_STRING]\n{}", PATTERN_STRING);
					
					sqlList = new ArrayList<String>();
					extractSqlList = regexUtil.findPatternToList(fileContents, PATTERN_STRING);
					logger.debug("[sqlList.size]: {}", extractSqlList.size());
					int sqlCnt = 1;
					boolean checkEqualKind = false;  //= '" +
					boolean sentenceExprCheck = false;
					
					for(String sql : extractSqlList) {
						//logger.debug("[sql]\n{}", sql);
						sql = sql.replace(RECOVER_SEMI_COLON, ";");
						sql = sql.trim();
						sql = sql.substring(sql.indexOf("\"") + "\"".length(), sql.length() - "\";".length()).trim();
						
						sqlLine = sql.split(SystemUtil.LINE_SEPARATOR);
						
						checkEqualKind = false;
						sqlProcStb = new StringBuilder();
						String contentsLine = null;
						for(String line : sqlLine) {
							line = line.trim();
							
							//extract contentsLine
							//if(line.startsWith("/*")) {
								//file contents line
							//	contentsLine = line.substring(0, line.indexOf("*/") + "*/".length());
							//	line = line.substring(contentsLine.length());
							//}
							//else {
							//	contentsLine = "";
							//}
							//logger.debug("contentsLine: {}", contentsLine);
							
							//여기
							if(regexUtil.testPattern(line, "=([\\s]+)?'([\\s]+)?\"([\\s]+)?[+]") && regexUtil.testPattern(line, "[+]([\\s]+)?\"([\\s]+)?'([\\s]+)?\"$")) {
								checkEqualKind = true;
								logger.debug("[checkEqualKind:true]");
							}
							
							sentenceExprCheck = (regexUtil.testPattern(line, "=([\\s]+)?'([\\s]+)?\"([\\s]+)?[+]") && regexUtil.testPattern(line, "[+]([\\s]+)?\"([\\s]+)?'([\\s]+)?\"$")); 

							if(checkEqualKind && !sentenceExprCheck && regexUtil.testPattern(line, "=([\\s]+)?\"([\\s]+)?[+]") && regexUtil.testPattern(line, "[+]([\\s]+)?\"([\\s]+)?\"$")) {
								logger.debug("[checkEqualKind:false]");
								line = regexUtil.replaceAllPattern(line, "=([\\s]+)?\"([\\s]+)?[+]", "='\"");
								line = regexUtil.replaceAllPattern(line, "[+]([\\s]+)?\"([\\s]+)?\"$", "+\"' \"");
							}

							if(line.endsWith("\"")) {
								line = line.substring(0, line.length() - "\"".length()).trim();
							}
							
							if(line.endsWith("+")) {
								if(line.contains("'\"")) {
									line = line.concat("\"'");
								}
								else if(line.contains("\"")) {
									line = line.concat("\"");
								}
							}

							//logger.debug("{}", line);
							
							if(!line.startsWith("//")) {
								
								if(line.startsWith("+")) {
									line = line.substring("+".length()).trim();
								}
								if(line.startsWith("\"")) {
									line = line.substring("\"".length());
								}
								
								//set contentsLine
								//line = contentsLine.concat(line);
								sqlProcStb.append(line).append(SystemUtil.LINE_SEPARATOR);
							}
							
							
						}
						sql = sqlProcStb.toString();
						//logger.debug("## sqlProcStb ##\n{}", sql);
						
						extractDivSqlList = regexUtil.findPatternToList(sql, PATTERN_DELETE_DIV);
						
						if(extractDivSqlList.size() == 0) {
							
							extractDivSqlList = regexUtil.findPatternToList(sql, PATTERN_UPDATE_DIV);
							
							if(extractDivSqlList.size() > 0) {
								//UPDATE DIV
								for(String divSql : extractDivSqlList) {
									//정제후 분할된 SQL을 담음
									sqlList.add(divSql);
									
									parseExt = executeSqlParse(sqlStb, divSql, file, sqlCnt);
								}
							}
							else {
								
								//정제된 SQL을 담음
								sqlList.add(sql);
								
								parseExt = executeSqlParse(sqlStb, sql, file, sqlCnt);
							}
						}
						else {
							//DELETE DIV
							for(String divSql : extractDivSqlList) {
								//정제후 분할된 SQL을 담음
								sqlList.add(divSql);
								
								parseExt = executeSqlParse(sqlStb, divSql, file, sqlCnt);
							}
						}
					}
					
					logger.debug("vSql checkCnt: {}, sqlList.size(): {}", checkCnt, sqlList.size());
					if(checkCnt > sqlList.size()) {
						
						// kait pbl sql 분석용 임시 주석
						// throw new ApplicationException("checkCnt: {}, sqlList.size(): {}", checkCnt, sqlList.size());
					}
					logger.debug("###################################################");
				}

			} //E. for fileList 
			
			//Last Line Processing
			if(currentFile != null /* || !currentFile.getPath().equals(file.getPath())*/) {
				//디렉토리가 변경되면 이전에 추출한 내용을 파일에 저장한다.
				//디렉토리 1개당 처리
				if(sqlStb != null) {
					filePath = currentFile.getParentFile().getPath().concat(File.separator).concat("sql");
					fileName = currentFile.getPath().substring(currentFile.getPath().lastIndexOf(File.separator)).concat(parseExt);
					
					/** START##parse powerBuilder source sql */
					executePblParser(filePath, fileName, sqlStb);
					/** END##parse powerBuilder source sql */
					
					logger.debug("[★ 분석결과파일생성]");
					fileUtil.mkfile(filePath, fileName, sqlStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
					sqlAllStb.append(sqlStb.toString());
					sqlAllStb.append(SystemUtil.LINE_SEPARATOR);
				}
				
				logger.debug("* 마지막 라인 처리: {}", currentFile.getPath());
			}
			
			fileUtil.mkfile(NETJS_RESOURCE_PATH, "all_sql_extract.sql", sqlAllStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
		}
	}
	
	
	private void executePblParser(String filePath, String fileName, StringBuilder sqlStb) {
		
		/** START##parse powerBuilder source sql */
		String pblBasePath = executePowerbuilderSrdSqlParser.PBL_RESOURCE_PATH;
		String pblSql = null;
		try {
			pblBasePath = filePath.replace(IOperateCode.STR_BACK_SLASH, IOperateCode.STR_SLASH).replace(NETJS_RESOURCE_PATH, pblBasePath);
			pblBasePath = pblBasePath.substring(0, pblBasePath.lastIndexOf(IOperateCode.STR_SLASH));
			pblBasePath = pblBasePath.concat(fileName.substring(0, fileName.indexOf(IOperateCode.STR_DOT))).replace(IOperateCode.STR_BACK_SLASH, IOperateCode.STR_SLASH);
			logger.debug("parse pblBasePath: {}", pblBasePath);
			
			if(new File(pblBasePath).exists()) {
				pblSql = executePowerbuilderSrdSqlParser.parsePblDumpSql(pblBasePath, hdAllTables, hdAllFunctions, hdAllProcedures);
			}
			else {
				pblSql = "pblDump is not exists";
			}
			
			sqlStb.append("◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆ < PLB_SQL_START > ◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆◆");
			sqlStb.append(SystemUtil.LINE_SEPARATOR);
			sqlStb.append(pblSql);
			sqlStb.append(SystemUtil.LINE_SEPARATOR);
			
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		/** END##parse powerBuilder source sql */
	}
	
	private String executeSqlParse(StringBuilder sqlStb, String sql, File file, int sqlCnt) {
		
		Map<String, Object> reseut = null;
		String ext = ".extract.sql";
		
		sqlStb.append("★ Sql From : ");
		sqlStb.append(getQualifiedName(file.getPath(), PBL_ROOT_PATH));
		sqlStb.append(SystemUtil.LINE_SEPARATOR);
		sqlStb.append(SystemUtil.LINE_SEPARATOR);
		sqlStb.append(sql);
		sqlStb.append(SystemUtil.LINE_SEPARATOR);


		logger.debug("■[START SQL]■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■ {}: {}", sqlCnt, file.getPath().substring(file.getPath().lastIndexOf(File.separator) + File.separator.length()));
		logger.debug("-sql:\n{}", sql);
		
		//if(false) {
		if(!sql.contains("DECLARE")) {
			sqlStb.append("■■■■■■■■■■■■ < PARSE IN/OUT COLUMN & TABLE > ■■■■■■■■■■■■");
			sqlStb.append(SystemUtil.LINE_SEPARATOR);
			
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
		}
		else {
//			logger.debug("#FIND DECLARE: CHECK COUNT MIMNUS --;");
//			checkCnt = checkCnt - 1;
			ext = ".extract.proc.sql";
			
			logger.debug("##### WITH CALL PROC");
			/***************************
			 * EXTRACT PROCEDURE NAME
			 */
			List<String> procedureNames = regexUtil.findPatternToList(sql, "PROC_([_a-zA-Z0-9]+)"); 
			for(String procedureName : procedureNames) {
				sqlParserHelper.setMapKeySequnceValue(hdAllProcedures, procedureName);	
			}
		}
		
		sqlStb.append("■■■■■■■■■■■■■■■■■■■■■■■■■■■ < SEPARATOR > ■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		sqlStb.append(SystemUtil.LINE_SEPARATOR);
		sqlStb.append(SystemUtil.LINE_SEPARATOR);
		sqlCnt++;
		
		return ext;
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
		
		sql.append("vSql=\"");
		sql.append(SystemUtil.LINE_SEPARATOR);
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
		sql.append(")	");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("\";");
		sql.append(SystemUtil.LINE_SEPARATOR);
		
		sql.append("vProc=\"");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("SELECT DC.*, 1 + $TEST|PIPE FROM (");
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
		sql.append("AND NVL(B.LISTTAG, '1') <> '3'	\n");		
		sql.append(")	\n");
		sql.append("\";");
		//sql = new StringBuilder();
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("테스트단어위");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("테스트단어아래");
		
		sql.append("vSql = \" UPDATE SM_CODE_CUST ").append(SystemUtil.LINE_SEPARATOR);
		sql.append("SET HD_YN = 'Y'").append(SystemUtil.LINE_SEPARATOR);
		sql.append("WHERE CUST_CODE = '\" + vCust + \"'\" ;").append(SystemUtil.LINE_SEPARATOR);
		sql.append("if (_ExecSql(\"UPDATE\", vSql))").append(SystemUtil.LINE_SEPARATOR);
		sql.append("{").append(SystemUtil.LINE_SEPARATOR);
		sql.append("alert(\"처리중 오류가 발생했습니다\");").append(SystemUtil.LINE_SEPARATOR);
		sql.append("return;").append(SystemUtil.LINE_SEPARATOR);
		sql.append("}").append(SystemUtil.LINE_SEPARATOR);
		sql.append("alert('분양고객으로 변경되었습니다.');").append(SystemUtil.LINE_SEPARATOR);
		sql.append("OnRetrieve();").append(SystemUtil.LINE_SEPARATOR);
	
		//, REFlags.MULTILINE
		/*
		String SPECIAL_CHARACTER = "$*+|\\(\\)\\[\\]\\{\\}\\<\\>\\^\\-?:~!=@#%&\\_/\\\\,\\.\"'　`！＇，．／：；？＾＿｀｜￣、。·‥…¨〃―∥＼∼´～ˇ˘˝˚˙¸˛¡¿ː＂（）［］｛｝‘’“”〔〕〈〉《》「」『』【】＋－＜＝＞±×÷≠≤≥∞∴♂♀∠⊥⌒∂∇≡≒≪≫√∽∝∵∫∬∈∋⊆⊇⊂⊃∪∩∧∨￢⇒⇔∀∃∮∑∏＄％￦Ｆ′″℃Å￠￡￥¤℉‰€㎕㎖㎗ℓ㎘㏄㎣㎤㎥㎦㎙㎚㎛㎜㎝㎞㎟㎠㎡㎢㏊㎍㎎㎏㏏㎈㎉㏈㎧㎨㎰㎱㎲㎳㎴㎵㎶㎷㎸㎹㎀㎁㎂㎃㎄㎺㎻㎼㎽㎾㎿㎐㎑㎒㎓㎔Ω㏀㏁㎊㎋㎌㏖㏅㎭㎮㎯㏛㎩㎪㎫㎬㏝㏐㏓㏃㏉㏜㏆＃＆＊＠§※☆★○●◎◇◆□■△▲▽▼→←↑↓↔〓◁◀▷▶♤♠♡♥♧♣⊙◈▣◐◑▒▤▥▨▧▦▩♨☏☎☜☞¶†‡↕↗↙↖↘♭♩♪♬㉿㈜№㏇™㏂㏘℡®ªº─│┌┐┘└├┬┤┴┼━┃┏┓┛┗┣┳┫┻╋┠┯┨┷┿┝┰┥┸╂┒┑┚┙┖┕┎┍┞┟┡┢┦┧┩┪┭┮┱┲┵┶┹┺┽┾╀╁╃╄╅╆╇╈╉╊㉠㉡㉢㉣㉤㉥㉦㉧㉨㉩㉪㉫㉬㉭㉮㉯㉰㉱㉲㉳㉴㉵㉶㉷㉸㉹㉺㉻㈀㈁㈂㈃㈄㈅㈆㈇㈈㈉㈊㈋㈌㈍㈎㈏㈐㈑㈒㈓㈔㈕㈖㈗㈘㈙㈚㈛ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂０１２３４５６７８９ⅰⅱⅲⅳⅴⅵⅶⅷⅸⅹⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ½⅓⅔¼¾⅛⅜⅝⅞¹²³⁴ⁿ₁₂₃₄ㄱㄲㄳㄴㄵㄶㄷㄸㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅃㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣㅥㅦㅧㅨㅩㅪㅫㅬㅭㅮㅯㅰㅱㅲㅳㅴㅵㅶㅷㅸㅹㅺㅻㅼㅽㅾㅿㆀㆁㆂㆃㆄㆅㆆㆇㆈㆉㆊㆋㆌㆍㆎＡBCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩαβγδεζηθικλμνξοπρστυφχψωÆÐĦĲĿŁØŒÞŦŊæđðħıĳĸŀłøœßŧŋŉАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюВ";
		String PATTERN_STRING = "(vSql|vProc)([\\s]+)?=([\\s]+)?\"(["+INNER_SQL_STRING+SPECIAL_CHARACTER+"\\s]+)([\\s]+)?\";";
		Pattern pa = new Pattern(PATTERN_STRING "^[ㄱ-ㅎ가-힣]*$", REFlags.MULTILINE);
		Matcher mt = pa.matcher(sql.toString());
		
		
		
		while(mt.find()){
  			
  			int count = 0;
  			for(String group : mt.groups()){
  				logger.debug(" group {}", group);
  	  			if(group != null && !group.isEmpty()){
  	  				if(logger.isDebugEnabled()) {
  					    //logger.debug(" Match Count [{}] {}", (count++), group);
  			    	}
  	  			}
  			}
  			
  			
  				logger.debug("==== {}", mt.toString());
  			
  		}
  		
		*/
		
		List<String> sqlList = regexUtil.findPatternToList(sql.toString(), PATTERN_STRING /*"^[ㄱ-ㅎ가-힣]*$"*/);
		
		//logger.debug(sql.toString());
		for(String sqls : sqlList) {
			logger.debug("############### START ################");
			logger.debug(sqls);
			logger.debug("############### END ################");
		}
		
		logger.debug("sqlList.size: {}", sqlList.size());
		
		/*
		if(true) return;
		
		
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
	
		*/
		
		StringBuilder testStb = new StringBuilder();
		testStb.append("+ \"   FROM (SELECT NVL(MAX(IN_SEQ), 0) + 1 AS A1, 0 AS A2").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"           FROM HD_RENT_GURT_INCOME_DAILY").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"          WHERE DEPT_CODE  = '\" + _GetItem(dwMain, 0, \"dept_code\") + \"'").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"            AND HOUSETAG   = '\" + _GetItem(dwMain, 0, \"housetag\")  + \"'").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"            AND IN_DATE    = '\" + _GetItem(dwInpt, 0, \"in_date\")   + \"'").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"          UNION ALL").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"         SELECT 0 AS A1, COUNT(*) AS A2").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"           FROM HD_RENT_GURT_INCOME").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"          WHERE CUST_CODE     = '\" + _GetItem(dwMain, 0, \"cust_code\") + \"'").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"            AND SEQ           =  \" + _GetItemNumber(dwMain, 0, \"sql\") + \"").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"            AND RECEIPT_DATE  > '\" + _GetItem(dwInpt, 0, \"in_date\")   + \"'").append(SystemUtil.LINE_SEPARATOR);
		testStb.append("+ \"        )").append(SystemUtil.LINE_SEPARATOR);
		
		logger.debug(testStb.toString());		
		boolean checkEqualKind = false;
		for(String line : testStb.toString().split(SystemUtil.LINE_SEPARATOR)) {
		
			if(regexUtil.testPattern(line, "=([\\s]+)?'([\\s]+)?\"([\\s]+)?[+]") && regexUtil.testPattern(line, "[+]([\\s]+)?\"([\\s]+)?'$")) {
				checkEqualKind = true;
				logger.debug("[checkEqualKind:true]");
			}
			boolean sentenceExprCheck = (regexUtil.testPattern(line, "=([\\s]+)?'([\\s]+)?\"([\\s]+)?[+]") && regexUtil.testPattern(line, "[+]([\\s]+)?\"([\\s]+)?'$")); 
			if(checkEqualKind && !sentenceExprCheck && regexUtil.testPattern(line, "=([\\s]+)?\"([\\s]+)?[+]") && regexUtil.testPattern(line, "[+]([\\s]+)?\"$")) {
				logger.debug("[checkEqualKind:false]");
				line = regexUtil.replaceAllPattern(line, "=([\\s]+)?\"([\\s]+)?[+]", "='\"");
				line = regexUtil.replaceAllPattern(line, "[+]([\\s]+)?\"$", "+\"'");
			}
			
			logger.debug(line);
		}
		
		StringBuilder insStb = new StringBuilder();
		insStb.append("INSERT INTO HD_HOUS_SEALHISTORY ");
		insStb.append("      ( CUST_CODE, SEQ,           SN ) VALUES ( ");
		insStb.append(" :CUST_CODE, :SEQ,           :SN  ");
		insStb.append(")");
		
		Map<String, Object> reseut = jsqlParser.parseSQL(insStb.toString());
		

		logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		
		if( reseut.get(JSQLParser.STATEMENT) != null ) {
			logger.debug("SQL Statement type : " + reseut.get(JSQLParser.STATEMENT));
		}
		
		logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		
		if( reseut.get(JSQLParser.INPUT_KEY) != null ) {
			for(Map<String, Object> item : (List<Map<String, Object>>) reseut.get(JSQLParser.TABLE_KEY) ){
				logger.debug("TABLE : " + item);
			}
		}
		
		logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		
		if( reseut.get(JSQLParser.INPUT_KEY) != null ) {
			for(Map<String, Object> item : (List<Map<String, Object>>) reseut.get(JSQLParser.INPUT_KEY) ){
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
