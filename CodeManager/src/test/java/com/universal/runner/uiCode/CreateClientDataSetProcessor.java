package com.universal.runner.uiCode;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.marshaller.XMLConverter;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.RegexUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;
import com.universal.code.utils.thread.Local;
import com.universal.code.xml.factory.DocumentFactory;
import com.universal.code.xml.factory.dto.DocumentBuilderDTO;
import com.universal.code.xml.process.DocumentReader;

public class CreateClientDataSetProcessor {

	static final Logger logger = LoggerFactory.getLogger(CreateClientDataSetProcessor.class);
	
	StringUtil stringUtil = new StringUtil();

	FileUtil fileUtil = new FileUtil();
	
	RegexUtil regexUtil = new RegexUtil();
	
	DocumentFactory documentFactory = new DocumentFactory();
	
	DocumentReader documentReader = new DocumentReader();
	
	XMLConverter xmlConverter = new XMLConverter();
	
	static final List<String> EXTRACT_ORDER;
	static final List<String> PASS_FILE;  // dr_hd_cont_allhouse.srd
	static final String T_COLUMN_EQUALS = "table(column=";
	static final String COLUMN_EQUALS = "column=";
	static final String COLUMN_RCASE = "column(";
	static final String TEXT_RCASE = "text(";
	static final String EXPORT_COMMENTS = "$PBExportComments$";
	static final Map<String, String> CONVERT_DATATABLE_TYPE;
	static final Map<String, String> CONVERT_REALGRID_TYPE;
	static final String BASE_PATH;
	static final String EXTRACT_ROOT_NAME;
	
	
	static {
		
		EXTRACT_ROOT_NAME = "mergeAsset";
		/**
		 * dr_hd_incr_inbetween.srd
		 * 
		 */
		PASS_FILE = new ArrayList<String>();
		//PASS_FILE.add(File.separator.concat("HDrContAllhouse").concat(File.separator).concat("dr_hd_cont_allhouse.srd"));	
		
		/**
		 * 제나 데이터 테이블 데이터타입
		 * array bit bool double int8 int16 int32 int64 pvoid string uint8 uint16 uint32 uint64 variant
		 * 쉬프트소프트 정성환 사원이 현재 datatable 데이터 타입은 string 밖에 적용안된다고함
		 */
		CONVERT_DATATABLE_TYPE = new LinkedHashMap<String, String>();
		CONVERT_DATATABLE_TYPE.put("number", "string");
		CONVERT_DATATABLE_TYPE.put("decimal", "string");
		CONVERT_DATATABLE_TYPE.put("integer", "string");
		CONVERT_DATATABLE_TYPE.put("float","string");
		CONVERT_DATATABLE_TYPE.put("double","string");
		CONVERT_DATATABLE_TYPE.put("nvarchar2","string");
		CONVERT_DATATABLE_TYPE.put("varchar2","string");
		CONVERT_DATATABLE_TYPE.put("char","string");
		CONVERT_DATATABLE_TYPE.put("long","string");
		CONVERT_DATATABLE_TYPE.put("clob","string");
		CONVERT_DATATABLE_TYPE.put("date","string");
		
		/**
		 * 리얼그리드의 데이터 타입 
		 * text number boolean datetime
		 */
		CONVERT_REALGRID_TYPE = new LinkedHashMap<String, String>();
		CONVERT_REALGRID_TYPE.put("number", "number");
		CONVERT_REALGRID_TYPE.put("decimal", "number");
		CONVERT_REALGRID_TYPE.put("integer", "number");
		CONVERT_REALGRID_TYPE.put("float","number");
		CONVERT_REALGRID_TYPE.put("double","number");
		CONVERT_REALGRID_TYPE.put("nvarchar2","text");
		CONVERT_REALGRID_TYPE.put("varchar2","text");
		CONVERT_REALGRID_TYPE.put("char","text");
		CONVERT_REALGRID_TYPE.put("long","text");
		CONVERT_REALGRID_TYPE.put("clob","text");
		CONVERT_REALGRID_TYPE.put("date","datetime");
		
		EXTRACT_ORDER = new ArrayList<String>();  
		EXTRACT_ORDER.add(EXPORT_COMMENTS);
		EXTRACT_ORDER.add(T_COLUMN_EQUALS);
		EXTRACT_ORDER.add(COLUMN_EQUALS);
		EXTRACT_ORDER.add(TEXT_RCASE);
		EXTRACT_ORDER.add(COLUMN_RCASE);
		
		//create xena and realGrid dataSet target srd root directory 
		//srd 정상 및 오류코드 복원 및 xml정상 변환 완료 : hd, am, fm, fs, blank, bs, hr, kait
		//잔여  : , , reitsis, sm, swdc, tm
		BASE_PATH = "D:/Developer/AS-IS/KAIT_ERP/asisProject/kait-pbl-dump/pbl/mm";
	}
	
	public final static String SPECIAL_CHARACTER;
	public final static String PATTERN_STRING;
	public final static String INNER_SQL_STRING;
	
	static {
		SPECIAL_CHARACTER = "$*+|\\(\\)\\[\\]\\{\\}\\<\\>\\^\\-?:~!=@#%&\\_/\\\\,\\.\"'　`！＇，．／：；？＾＿｀｜￣、。·‥…¨〃―∥＼∼´～ˇ˘˝˚˙¸˛¡¿ː＂（）［］｛｝‘’“”〔〕〈〉《》「」『』【】＋－＜＝＞±×÷≠≤≥∞∴♂♀∠⊥⌒∂∇≡≒≪≫√∽∝∵∫∬∈∋⊆⊇⊂⊃∪∩∧∨￢⇒⇔∀∃∮∑∏＄％￦Ｆ′″℃Å￠￡￥¤℉‰€㎕㎖㎗ℓ㎘㏄㎣㎤㎥㎦㎙㎚㎛㎜㎝㎞㎟㎠㎡㎢㏊㎍㎎㎏㏏㎈㎉㏈㎧㎨㎰㎱㎲㎳㎴㎵㎶㎷㎸㎹㎀㎁㎂㎃㎄㎺㎻㎼㎽㎾㎿㎐㎑㎒㎓㎔Ω㏀㏁㎊㎋㎌㏖㏅㎭㎮㎯㏛㎩㎪㎫㎬㏝㏐㏓㏃㏉㏜㏆＃＆＊＠§※☆★○●◎◇◆□■△▲▽▼→←↑↓↔〓◁◀▷▶♤♠♡♥♧♣⊙◈▣◐◑▒▤▥▨▧▦▩♨☏☎☜☞¶†‡↕↗↙↖↘♭♩♪♬㉿㈜№㏇™㏂㏘℡®ªº─│┌┐┘└├┬┤┴┼━┃┏┓┛┗┣┳┫┻╋┠┯┨┷┿┝┰┥┸╂┒┑┚┙┖┕┎┍┞┟┡┢┦┧┩┪┭┮┱┲┵┶┹┺┽┾╀╁╃╄╅╆╇╈╉╊㉠㉡㉢㉣㉤㉥㉦㉧㉨㉩㉪㉫㉬㉭㉮㉯㉰㉱㉲㉳㉴㉵㉶㉷㉸㉹㉺㉻㈀㈁㈂㈃㈄㈅㈆㈇㈈㈉㈊㈋㈌㈍㈎㈏㈐㈑㈒㈓㈔㈕㈖㈗㈘㈙㈚㈛ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂０１２３４５６７８９ⅰⅱⅲⅳⅴⅵⅶⅷⅸⅹⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ½⅓⅔¼¾⅛⅜⅝⅞¹²³⁴ⁿ₁₂₃₄ㄱㄲㄳㄴㄵㄶㄷㄸㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅃㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣㅥㅦㅧㅨㅩㅪㅫㅬㅭㅮㅯㅰㅱㅲㅳㅴㅵㅶㅷㅸㅹㅺㅻㅼㅽㅾㅿㆀㆁㆂㆃㆄㆅㆆㆇㆈㆉㆊㆋㆌㆍㆎＡBCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩαβγδεζηθικλμνξοπρστυφχψωÆÐĦĲĿŁØŒÞŦŊæđðħıĳĸŀłøœßŧŋŉАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюВ";
		INNER_SQL_STRING = "a-zA-Zㄱ-ㅎ가-힣0-9ァ-ンあ-ん亜-穏下-懇左-損丼-他濃-那把-盆問-麻冶-翼拉-論和-腕";
		PATTERN_STRING = new StringBuilder().append("(vSql|vProc)([\\s]+)?=([\\s]+)?\"([").append(INNER_SQL_STRING).append(SPECIAL_CHARACTER).append("\\s]+)([\\s]+)?\";").toString();
		/*
		static final String T_COLUMN_EQUALS = "table(column=";
		static final String COLUMN_EQUALS = "column=";
		static final String COLUMN_RCASE = "column(";
		static final String TEXT_RCASE = "text(";
		static final String EXPORT_COMMENTS = "$PBExportComments$";
		*/
	}
	
	@Test
	public void run() throws UnsupportedEncodingException {
		Local.commonHeader();
		
		String[] convertStrings = new String[]{
				 "HD_CODE_AGREE"
				,"HD_REFER_RATE_DISCOUNT"
				,"HD_REFER_RATE_DELAY"
				,"HD_REFER_HOLIDAY"
				,"HD_CODE_DEPOSIT"
				,"HD_CODE_SPECIAL"
				,"HD_CODE_OPTION"
				,"HD_CODE_AGENCY"
		};
		
		for(String convertString : convertStrings) {
			logger.debug("ds_{}", stringUtil.getCamelCaseString(convertString));
		}

		// asis pbl dump 루트 디렉토리에서 하위파일을 스켄하며 srd를 찾고 해당 파일을 input으로 createSrdDataSet을 실행한다. 
		
		List<File> fileList = fileUtil.getChildFileList(new ArrayList<File>(), new File(BASE_PATH), "srd");
		
		

		File file = null;
		String srdXml = null;
		// srd파일의 인코딩은 UTF-16LE
		String srdEncoding = "UTF-16LE";
		Map<String, Object> xmlMap = null;
		/*
		try {
			String xml = "<mergeAsset><viewColumn band=\"detail\" id=\"1\" alignment=\"2\" tabsequence=\"32766\" border=\"0\" color=\"0~tif (c01 = 'N', ntcolor, ytcolor)\" x=\"52\" y=\"80\" height=\"370\" width=\"793\" format=\"[general]\" html.valueishtml=\"0\"  name=\"h01\"  tag=\"No\" pointer=\"HyperLink!\" visible=\"1~tif (len(h01) &lt;&gt; 4, 0, 1)\" edit.limit=\"0\" edit.case=\"any\" edit.focusrectangle=\"no\" edit.autoselect=\"yes\" edit.autohscroll=\"yes\"  font.face=\"굴림체\" font.height=\"-9\" font.weight=\"400\"  font.family=\"2\" font.pitch=\"2\" font.charset=\"129\" font.underline=\"0~tif (c01 = 'N', 0, 1)\"  background.mode=\"0\" background.color=\"553648127~tif (c01 = 'N', nbcolor, ybcolor)\" /></mergeAsset>";
			xmlMap = xmlConverter.xmlToMap(xml, EXTRACT_ROOT_NAME);
			logger.debug("xmlMap: {}", xmlMap);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		String testStr = "=\"에서 정한 중도금을 ~\"갑~\"이 \" text=\"에서 정한 중도금을 ~\"갑~\"이 \"";
		logger.debug("testStr[1]: {}", testStr);
		logger.debug("testStr[2]: {}", testStr.replaceAll("([a-zA-Z0-9$_\\.])=\"([~a-zA-Z가-힣ㄱ-ㅎ \"]+)\"", "$1").replace("\"","'"));
		if(true) return;
		*/
		
		String tempXml = null;
		List<String> findPattern = null;
		try {
			
			for(int i = 0; i < fileList.size(); i++) {
				file = fileList.get(i);
				
				logger.debug("{}: {}", i, file);
				srdXml = getSrdDataDefinition(file.getPath(), srdEncoding);
				if(srdXml != null ) {
					tempXml = srdXml;
					logger.debug("#########################################");
					logger.debug("#	클라이언트 데이터 셋 생성을 시작합니다.	#");
					logger.debug("# 파일: {} #", file.getPath());
					logger.debug("#########################################");
					
					//srd 원본 코드 구성이 잘못된 부분에 대한 예외 방어코드
					//replace with string
					srdXml = srdXml.replace("=\"work_num\"","='work_num'");
					
					//replaceAll with regex
					srdXml = srdXml.replaceAll("~\"([0-9a-zA-Z,@\\.~\\)&#;])", "~'$1");  
					findPattern = regexUtil.findPatternToList(srdXml, "=\"([~a-zA-Zㄱ-ㅎ가-힣0-9=?:!#&;@/*%\\_+'\\.\\s\\(\\)\\[\\]\\-\\,]+)\" ");
					String itemExprNew = null;
					for(String itemExpr : findPattern) {
						if(itemExpr.substring("=".length()).contains("=")) {
							logger.debug("itemExpr : {}", itemExpr);
							itemExprNew = itemExpr.replace("=", "&#61;");
							itemExprNew = "=".concat(itemExprNew.substring("&#61;".length()));
							logger.debug("itemExprNew : {}", itemExprNew);
							srdXml = srdXml.replace(itemExpr, itemExprNew);
						}
					}
					srdXml = srdXml.replaceAll("(?i)([a-zA-Z0-9$_\\.]+)=([ㄱ-ㅎ가-힣a-zA-Z0-9&#;_\\(\\)\\.]+)", "$1=\"$2\""); // key=value를 key="value"로 치환
					srdXml = srdXml.replaceAll("\"([a-zA-Z0-9$_\\.])", "\" $1");
					//srdXml = srdXml.replaceAll("(?i)([a-zA-Zㄱ-ㅎ가-힣0-9~])=([a-zA-Zㄱ-ㅎ가-힣0-9~])", "$1  = $2");

					//replace with string
					// ( &#40; , ) &#41;
					srdXml = srdXml.replace("~\"", "~'");
					srdXml = srdXml.replace("=\" 0\",", "='0',");
					srdXml = srdXml.replace("~\" or", "~' or");
					srdXml = srdXml.replace("~\"&#41;", "~'&#41;");
					srdXml = srdXml.replace("=\"~~' ", "=\"~~\" ");
					srdXml = srdXml.replace("=\" A\"", "=' A'");
					srdXml = srdXml.replace("&#40;", "(").replace("&#41;", ")");
					srdXml = srdXml.replace("&", "&#38;");
					srdXml = srdXml.replace("v_taxsum=\" 1\" and", "v_taxsum&#61;' 1' and");	// ?
					srdXml = srdXml.replace("v_fundout=\" 1\",", "v_fundout&#61;' 1',");		// ?
					
					//asis 소스코드 오타 보완
					srdXml = srdXml.replace("name=\" c_code_\"ㅅ visible=", "name=\" c_code_\" visible=");
					
					
					//logger.debug("[FINAL CONVERT]\n{}", srdXml);
					
					xmlMap = xmlConverter.xmlToMap(srdXml, EXTRACT_ROOT_NAME);
					logger.debug("#[xmlMap]\n{}", xmlMap);
					logger.debug("\n\n♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤\n\n");
				}
			}
		}
		catch(Exception e) {
			logger.error("오리지날: \n{}", tempXml);
			logger.error("문제점치환: \n{}", srdXml);
			throw new ApplicationException(e);
		}
	}
	
	String replaceSpecialCharacter(String expr) {
		return expr.replace("<", "&lt;").replace(">", "&gt;");
	}
	
	/**
	 * srd파일을 분석하여 column, text 부의 정보를 추출하여 xml형식으로 리턴함
	 * 
	 *  asis SRD 정보
		1. dr_ 로 시작 :  레포트 기능정의 파일
		2. dddw_, dd_ 로 시작 :  콤보박스 기능정의 파일
		3.dq_ 로 시작: 조회화면
		4. dc_ 로 시작 : 프로그램의 조건을 입력하는 부분을 정의하는 화면이구요
		5. df_ 로 시작 : 찾기 기능을 팝업시켜주는 화면입니다.
		6. de_ 로 시작 : 등록화면
	 *
	 * @param filePath
	 * @param fileEncoding
	 * @return
	 */
	String getSrdDataDefinition(String filePath, String fileEncoding) {

		String path = filePath;
		
		String[] paths = null;
		String fileSeparator = IOperateCode.STR_SLASH;
		if(path.indexOf(IOperateCode.STR_BACK_SLASH) > -1) {
			fileSeparator = IOperateCode.STR_BACK_SLASH;
			paths = path.split(IOperateCode.STR_BACK_SLASH.concat(fileSeparator));
		}
		else {
			fileSeparator = IOperateCode.STR_SLASH;
			paths = path.split(fileSeparator);
		}
		
		int savedCnt = 0;
		String fileName = "";
		for(int i = (paths.length - 1); i > -1; i--) {
			if(savedCnt > 1) break;
			fileName = fileSeparator.concat(paths[i]).concat(fileName);
			savedCnt++;
		}
		logger.debug("fileName: {}", fileName);
		
		//String separator = "[[separator]]";
		String contents = fileUtil.getTextFileContent(path, fileEncoding);
		if(contents == null || StringUtil.isEmpty(contents)) {
			logger.debug("파일 내용이 존재하지 않습니다.");
			return null;
		}
		else if(PASS_FILE.contains(fileName)) {
			logger.debug("분석 제외 설정 파일 입니다. {}", path);
			return null;
		}
		String[] lines = contents.split(SystemUtil.LINE_SEPARATOR);
		
		StringBuilder mergeTableColumn = new StringBuilder().append("<mergeTableColumn>").append(SystemUtil.LINE_SEPARATOR);
		StringBuilder mergeViewText = new StringBuilder().append("<mergeViewText>").append(SystemUtil.LINE_SEPARATOR);
		StringBuilder mergeViewColumn = new StringBuilder().append("<mergeViewColumn>").append(SystemUtil.LINE_SEPARATOR);
		String comments = null;
		String data = null;
		
		
		String currentKeyWord = null;
		try {
			
			boolean isUseVTextLineSeparator = false;
			boolean isUseVColsLineSeparator = false;
			String last2Char = null;
			String last3Char = null;
			for(String keyword : EXTRACT_ORDER) {
				currentKeyWord = keyword; 
				isUseVTextLineSeparator = false;
				isUseVColsLineSeparator = false;
				for(String line : lines) {
					line = line.trim();
					//logger.debug("-line: {}", line);
					if(line.isEmpty()) continue;
					
					if(keyword.equals(T_COLUMN_EQUALS) && line.startsWith(T_COLUMN_EQUALS) || keyword.equals(COLUMN_EQUALS) && line.startsWith(COLUMN_EQUALS)) {
						data = line.substring(keyword.length());
												
						data = replaceSpecialCharacter(data);
						data = data.replaceFirst("\\(", "<tableColumn ");
						data = getChangeBracketChar(data);
						
						data = data.substring(0, data.lastIndexOf(")")).concat("/>");
						mergeTableColumn.append(data).append(SystemUtil.LINE_SEPARATOR);
						
						//logger.debug("{}: {}", keyword, data);
					}
					else if(keyword.equals(COLUMN_RCASE)) {
						
						if(line.startsWith(COLUMN_RCASE)) {
							data = line.substring(COLUMN_RCASE.length());
							data = replaceSpecialCharacter(data);
							data = "<viewColumn ".concat(data);
							data = getChangeBracketChar(data);
							
							//ViewColumn    
							last2Char = data.substring(data.length() - 2);
							last3Char = data.substring(data.length() - 3);
							//logger.debug("###{}: {}\nlast2Char: {}", keyword, data, last2Char);
							//logger.debug(">>test: {}", (")".concat(last2Char)).equals(") )"));
							if((( data.indexOf("color=\"") > -1 || data.indexOf("format=\"") > -1 || data.indexOf("pointer=\"") > -1 ) && !last2Char.equals(" )")) 
									|| (data.indexOf("format=\"") > -1 && last3Char.equals(") )")) ) {
								//개행 존재라인
								isUseVColsLineSeparator = true;
								mergeViewColumn.append(data).append(SystemUtil.STR_WHITE_SPACE);
								logger.debug("#VCOLS 개행 존재라인 isUseVTextLineSeparator : {}, data : {}", isUseVColsLineSeparator, data);
							}
							else {
								isUseVColsLineSeparator = false;
								data = data.substring(0, data.lastIndexOf(")")).concat("/>");
								mergeViewColumn.append(data).append(SystemUtil.LINE_SEPARATOR);
							}
							//logger.debug("{}: {}", COLUMN_RCASE, data);
						}
						else if(isUseVColsLineSeparator) {
							data = line;
							data = getChangeBracketChar(data);
							data = replaceSpecialCharacter(data);
							
							
							logger.debug("#VCOLS 개행 후속라인 isUseVTextLineSeparator : {}, data : {}", isUseVColsLineSeparator, data);
							
							if(data.indexOf("\"") > -1 && data.indexOf(")") > -1) {
								data = data.substring(0, data.lastIndexOf(")")).concat("/>").concat(SystemUtil.LINE_SEPARATOR);
								isUseVColsLineSeparator = false;
							}
							mergeViewColumn.append(data);
						}
					}
					else if(keyword.equals(TEXT_RCASE)) {
						
						//logger.debug("#TEXT_RCASE Line : {}", line);
						
						if(line.startsWith(TEXT_RCASE)) {
							data = line.substring(TEXT_RCASE.length());
								logger.debug("0: data: {}", data);
							data = replaceSpecialCharacter(data);
							data = "<viewText ".concat(data);
								logger.debug("1: data: {}", data);
							data = getChangeBracketChar(data);
							
							//ViewText
							last2Char = data.substring(data.length() - 2); 
							if((data.indexOf("text=\"") > -1 || data.indexOf("color=\"") > -1) && !last2Char.equals(" )") && !regexUtil.testPattern(data, "([a-zA-Z0-9\"])([ ])?\\)$")) {
								//개행 존재라인
								isUseVTextLineSeparator = true;
								mergeViewText.append(data).append(SystemUtil.STR_WHITE_SPACE);
								logger.debug("#VTEXT 개행 존재라인 isUseVTextLineSeparator : {}, data : {}", isUseVTextLineSeparator, data);
							}
							else {
								isUseVTextLineSeparator = false;
								data = data.substring(0, data.lastIndexOf(")")).concat("/>");
								mergeViewText.append(data).append(SystemUtil.LINE_SEPARATOR);
							}
						}
						else if(isUseVTextLineSeparator){
							data = line;
							data = getChangeBracketChar(data);
							data = replaceSpecialCharacter(data);
							logger.debug("#VTEXT 개행 후속라인 isUseVTextLineSeparator : {}, data : {}", isUseVTextLineSeparator, data);
							
							if(data.indexOf("\"") > -1 && data.indexOf(")") > -1) {
								data = data.substring(0, data.lastIndexOf(")")).concat("/>").concat(SystemUtil.LINE_SEPARATOR);
								isUseVTextLineSeparator = false;
							}
							
							mergeViewText.append(data);
						}						
					}
					else if(keyword.equals(EXPORT_COMMENTS) && line.startsWith(EXPORT_COMMENTS)) {
						data = line.substring(EXPORT_COMMENTS.length());
						data = replaceSpecialCharacter(data);
						comments = data;
						//logger.debug("{}: {}", EXPORT_COMMENTS, data);
					}

					
				}
			}

		}
		catch(Exception e) {
			logger.warn("※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※");
			logger.warn("");
			logger.warn("");
			logger.warn("");
			logger.warn("[WARN] 잘못된 srd코드 발생 \n경로: {}\n라인({}): {}\n내용: {}", path, currentKeyWord, data, contents);
			logger.warn("");
			logger.warn("");
			logger.warn("");
			logger.warn("※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※");
			e.printStackTrace();
			throw new ApplicationException("잘못된 srd코드 발생 \n경로: {}\n라인: {}\n내용: {}", new Object[] {path, data, contents}, e);
		}
		
		mergeTableColumn.append("</mergeTableColumn>");
		mergeViewText.append("</mergeViewText>");
		mergeViewColumn.append("</mergeViewColumn>");
		
		StringBuilder mergeAsset = new StringBuilder().append("<mergeAsset comment=\"").append(comments).append("\">").append(SystemUtil.LINE_SEPARATOR);
		mergeAsset.append(mergeTableColumn).append(SystemUtil.LINE_SEPARATOR);
		mergeAsset.append(mergeViewText).append(SystemUtil.LINE_SEPARATOR);
		mergeAsset.append(mergeViewColumn).append(SystemUtil.LINE_SEPARATOR);
		mergeAsset.append("</mergeAsset>");
		//logger.debug("[mergeAsset]\n{}", mergeAsset.toString());
		
		//개발 확인용 라인 셋팅
		StringBuilder finalAsset = new StringBuilder();
		if(mergeAsset != null) {
			int lineNo = 1;
			for(String expr : mergeAsset.toString().split(SystemUtil.LINE_SEPARATOR)) {
				finalAsset.append("<!-- ");
				finalAsset.append(lineNo);
				finalAsset.append(" --> ");
				finalAsset.append(expr).append(SystemUtil.LINE_SEPARATOR);
				lineNo++;
			}
			
		}
		
		
		mergeViewText = null;
		mergeViewColumn = null;
		mergeTableColumn = null;
		mergeAsset = null;
		
		return finalAsset.toString();
	}
	

	String getChangeBracketChar(String data) {
		String out = null;
		String last1Char = data.substring(data.length() - ")".length()); //
		if(regexUtil.testPattern(data, "\\) \\)$") 
			|| regexUtil.testPattern(data, "\\(([0-9a-zA-Zㄱ-ㅎ가-힣]+)\\)$")
			|| regexUtil.testPattern(data, "([ㄱ-ㅎ가-힣]+)\\)$")
			|| regexUtil.testPattern(data, "' \\)$")) { // srd 코드의 개행 라인
			
			data = data.replace("(", "&#40;").replace(")", "&#41;");
		}
		else if(last1Char.equals(")")) {
			data = data.substring(0, data.length() - ")".length());
			data = data.replace("(", "&#40;").replace(")", "&#41;");
			data = data.concat(")");
		}
		out = data;
		logger.debug("out: {}", out);
		return out;
	}
	
	void readXPathTest(String docPath){
		
		DocumentBuilderDTO documentBuilderDTO = new DocumentBuilderDTO();
		documentBuilderDTO.setIgnoringElementContentWhitespace(true);
		
		String xpathExpression = "//field[@id='A']/node()[1]";
		QName xpathQName = XPathConstants.NODE; 
		Object value = documentReader.getXpath(DocumentFactory.XML_FILE, docPath, docPath, xpathExpression, xpathQName);
		
		logger.debug(">> " + ((CDATASection) value).getTextContent());
		logger.debug("CanonicalName: " + value.getClass().getCanonicalName());
		
		//엘리먼트 Text or CDATASection내용을 문자열로 반환
		logger.debug("=> : " + documentReader.getText(DocumentFactory.XML_FILE, docPath, xpathExpression));
		
		NodeList nodeList = documentReader.getNodeList(DocumentFactory.XML_FILE, docPath, "//field");
		
		for(int i = 0; i < nodeList.getLength(); i++) {
			Element el = documentReader.getElementNode(nodeList, i);
			if(el != null) {
				logger.debug("> TextContent : " + el.getFirstChild().getTextContent());
				logger.debug("> AttributeValue : " + documentReader.getAttributeValue(el, "year"));
			}
		}
	} 
}
