package com.universal.runner.uiCode;

import java.io.File;
import java.io.UnsupportedEncodingException;
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
import com.universal.code.marshaller.XMLConverter;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.RegexUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;
import com.universal.code.utils.thread.Local;
import com.universal.code.xml.factory.DocumentFactory;
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
	static final List<String> PASS_FILE_PREFIX; 
	static final String T_TABLE_COLUMN = "table(column=";
	static final String TABLE_COLUMN = "column=";
	static final String VIEW_COLUMN = "column(";
	static final String VIEW_TEXT = "text(";
	static final String EXPORT_COMMENTS = "$PBExportComments$";
	static final Map<String, String> CONVERT_DATATABLE_TYPE;
	static final Map<String, String> CONVERT_REALGRID_TYPE;
	static final String BASE_PATH;
	static final String EXTRACT_ROOT_NAME;
	static final boolean MAKE_XML_FILE;
	
	static {
		
		PASS_FILE_PREFIX = new ArrayList<String>();
		//PASS_FILE_PREFIX.add("dr_"); // asis 레포트 파일 데이터셋 생성 제외
		
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
		EXTRACT_ORDER.add(T_TABLE_COLUMN);
		EXTRACT_ORDER.add(TABLE_COLUMN);
		EXTRACT_ORDER.add(VIEW_TEXT);
		EXTRACT_ORDER.add(VIEW_COLUMN);
		
		//create xena and realGrid dataSet target srd root directory 
		//srd 정상 및 오류코드 복원 및 xml정상 변환 완료 : hd, am, fm, fs, blank, bs, hr, kait
		//잔여  : , , reitsis, sm, swdc, tm
		// 사무실
		//BASE_PATH = "D:/Developer/AS-IS/KAIT_ERP/asisProject/kait-pbl-dump/pbl";
		// 집
		BASE_PATH = "D:/KimSangWoo/한자신백업/20170728/asisProject/kait-pbl-dump/pbl/hd";
		
		MAKE_XML_FILE = false;
	}
	
	public final static String SPECIAL_CHARACTER;
	public final static String PATTERN_STRING;
	public final static String INNER_SQL_STRING;
	
	static {
		SPECIAL_CHARACTER = "$*+|\\(\\)\\[\\]\\{\\}\\<\\>\\^\\-?:~!=@#%&\\_/\\\\,\\.\"'　`！＇，．／：；？＾＿｀｜￣、。·‥…¨〃―∥＼∼´～ˇ˘˝˚˙¸˛¡¿ː＂（）［］｛｝‘’“”〔〕〈〉《》「」『』【】＋－＜＝＞±×÷≠≤≥∞∴♂♀∠⊥⌒∂∇≡≒≪≫√∽∝∵∫∬∈∋⊆⊇⊂⊃∪∩∧∨￢⇒⇔∀∃∮∑∏＄％￦Ｆ′″℃Å￠￡￥¤℉‰€㎕㎖㎗ℓ㎘㏄㎣㎤㎥㎦㎙㎚㎛㎜㎝㎞㎟㎠㎡㎢㏊㎍㎎㎏㏏㎈㎉㏈㎧㎨㎰㎱㎲㎳㎴㎵㎶㎷㎸㎹㎀㎁㎂㎃㎄㎺㎻㎼㎽㎾㎿㎐㎑㎒㎓㎔Ω㏀㏁㎊㎋㎌㏖㏅㎭㎮㎯㏛㎩㎪㎫㎬㏝㏐㏓㏃㏉㏜㏆＃＆＊＠§※☆★○●◎◇◆□■△▲▽▼→←↑↓↔〓◁◀▷▶♤♠♡♥♧♣⊙◈▣◐◑▒▤▥▨▧▦▩♨☏☎☜☞¶†‡↕↗↙↖↘♭♩♪♬㉿㈜№㏇™㏂㏘℡®ªº─│┌┐┘└├┬┤┴┼━┃┏┓┛┗┣┳┫┻╋┠┯┨┷┿┝┰┥┸╂┒┑┚┙┖┕┎┍┞┟┡┢┦┧┩┪┭┮┱┲┵┶┹┺┽┾╀╁╃╄╅╆╇╈╉╊㉠㉡㉢㉣㉤㉥㉦㉧㉨㉩㉪㉫㉬㉭㉮㉯㉰㉱㉲㉳㉴㉵㉶㉷㉸㉹㉺㉻㈀㈁㈂㈃㈄㈅㈆㈇㈈㈉㈊㈋㈌㈍㈎㈏㈐㈑㈒㈓㈔㈕㈖㈗㈘㈙㈚㈛ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⒜⒝⒞⒟⒠⒡⒢⒣⒤⒥⒦⒧⒨⒩⒪⒫⒬⒭⒮⒯⒰⒱⒲⒳⒴⒵⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂０１２３４５６７８９ⅰⅱⅲⅳⅴⅵⅶⅷⅸⅹⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ½⅓⅔¼¾⅛⅜⅝⅞¹²³⁴ⁿ₁₂₃₄ㄱㄲㄳㄴㄵㄶㄷㄸㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅃㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣㅥㅦㅧㅨㅩㅪㅫㅬㅭㅮㅯㅰㅱㅲㅳㅴㅵㅶㅷㅸㅹㅺㅻㅼㅽㅾㅿㆀㆁㆂㆃㆄㆅㆆㆇㆈㆉㆊㆋㆌㆍㆎＡBCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩαβγδεζηθικλμνξοπρστυφχψωÆÐĦĲĿŁØŒÞŦŊæđðħıĳĸŀłøœßŧŋŉАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюВ";
		INNER_SQL_STRING = "a-zA-Zㄱ-ㅎ가-힣0-9ァ-ンあ-ん亜-穏下-懇左-損丼-他濃-那把-盆問-麻冶-翼拉-論和-腕";
		PATTERN_STRING = new StringBuilder().append("(vSql|vProc)([\\s]+)?=([\\s]+)?\"([").append(INNER_SQL_STRING).append(SPECIAL_CHARACTER).append("\\s]+)([\\s]+)?\";").toString();
		/*
		static final String T_TABLE_COLUMN = "table(column=";
		static final String TABLE_COLUMN = "column=";
		static final String VIEW_COLUMN = "column(";
		static final String VIEW_TEXT = "text(";
		static final String EXPORT_COMMENTS = "$PBExportComments$";
		*/
	}
	
	@Test
	public void run() throws UnsupportedEncodingException {
		Local.commonHeader();
		/*
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
		*/
		
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
		String newFileDir = null;
		String newFileName = null;
		String dataSetXml = null;
		try {
			int makeCnt = 0;
			for(int i = 0; i < fileList.size(); i++) {
				file = fileList.get(i);
				
				logger.debug("{}: {}", i, file);
				srdXml = getSrdDataDefinition(file.getPath(), srdEncoding);
				if(srdXml != null ) {
					tempXml = srdXml;
					logger.debug("#########################################");
					logger.debug("#	클라이언트 데이터 셋 생성을 시작합니다.	#");
					logger.debug("# 파일: {} ", file.getPath());
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
							//logger.debug("itemExpr : {}", itemExpr);
							itemExprNew = itemExpr.replace("=", "&#61;");
							itemExprNew = "=".concat(itemExprNew.substring("&#61;".length()));
							//logger.debug("itemExprNew : {}", itemExprNew);
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
					
					//asis 소스코드 오타 보완 ㅅ 이 속성 사이에 껴있음
					srdXml = srdXml.replace("name=\" c_code_\"ㅅ visible=", "name=\" c_code_\" visible=");
					
					logger.debug("[FINAL CONVERT]\n{}", srdXml);
					
					xmlMap = xmlConverter.xmlToMap(srdXml, EXTRACT_ROOT_NAME);

					dataSetXml = doGenerateDataSet(xmlMap, FileUtil.getFileName(file.getPath()));
					
					logger.debug("\n\n♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤♠♤\n\n");
					
					if(MAKE_XML_FILE) {
						
						// 1. file.getPath() 파일에서 확장자를  xml 로 변경하여 srd를 분석생성한 xml을 파일로 저장한다.
						// 2. xml데이터를 분석하여 xena datatables를 생성하고 .dt.xml 로 저장한다.
						newFileDir = FileUtil.getDirectory(file.getPath()).replace(File.separator.concat("kait-pbl-dump").concat(File.separator), File.separator.concat("kait-srd-xml").concat(File.separator));
						newFileName = FileUtil.getFileName(file.getPath());
						// srd convert xml 
						fileUtil.mkfile(newFileDir, newFileName.concat(".xml"), srdXml.toString(), "UTF-8", false);
						// xena dataSet xml
						fileUtil.mkfile(newFileDir, newFileName.concat(".dataSet").concat(".xml"), dataSetXml, "UTF-8", false);
					}
					
					makeCnt++;
				}
			}
			
			logger.debug("SRD 변환 및 데이터셋 생성성공 XML 갯수 : {} 건", (makeCnt * 2));
			
		}
		catch(Exception e) {
			logger.error("오리지날: \n{}", tempXml);
			logger.error("문제점치환: \n{}", srdXml);
			throw new ApplicationException(e);
		}
	}
	
	String doGenerateDataSet(Map<String, Object> xmlMap, String srdFileName) {
		logger.debug("#[doGenerateDataSet]#\n{}", xmlMap);
		String out = null;
		
		StringBuilder xgFinal = new StringBuilder();
		StringBuilder xgDatatable = new StringBuilder();
		StringBuilder xgRealGird = new StringBuilder();
		
		Map<String, Object> tableColumnMap = null; 
		Map<String, Object> viewTextMap = null;
		Map<String, Object> viewColumnMap = null;
		String srdComment = (String) xmlMap.get("comment");
		if(srdComment != null) {
			srdComment = srdComment.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "\\\"");
		}
		if(Map.class.isAssignableFrom(xmlMap.get("mergeTableColumn").getClass())) {
			tableColumnMap = (Map<String, Object>) xmlMap.get("mergeTableColumn");
		}
		if(Map.class.isAssignableFrom(xmlMap.get("mergeViewText").getClass())) {
			viewTextMap = (Map<String, Object>) xmlMap.get("mergeViewText");
		}
		if(Map.class.isAssignableFrom(xmlMap.get("mergeViewColumn").getClass())) {
			viewColumnMap = (Map<String, Object>) xmlMap.get("mergeViewColumn");
		}
		
		logger.debug("#tableColumnMap\n{}", tableColumnMap);
		logger.debug("#viewTextMap\n{}", viewTextMap);
		logger.debug("#viewColumnMap\n{}", viewColumnMap);
		
		if(tableColumnMap != null) {

			List<Map<String, Object>> tcAttributes = null;
			List<Map<String, Object>> vcAttributes = null;
			List<Map<String, Object>> vtAttributes = null;
			Map<String, Object> tcAttribute = null;
			Map<String, Object> vcAttribute = null;
			Map<String, Object> vtAttribute = null;
			String tcAttrValue = null;
			String vcAttrValue = null;
			String vtAttrValue = null;
			String tcName = null; 
			String tcType = null;
			String vcName = null;
			String vtName = null;
			String vtText = null;
			String vcFormat = null;
			boolean isFindViewColumn = false;
			boolean isFindViewText = false;
			
			String dtSize = null;
			String dtType = null;
			String dtDesc = null;
			String dtFormat = null;
			
			boolean isRealgrid = false;
			String dataSetId = stringUtil.getCamelCaseString(srdFileName.substring(0, srdFileName.indexOf(".")));
			
			if(srdFileName.startsWith("dq_")) {
				// dq_ 로시작하는 srd는 조회화면 srd
				isRealgrid = true;
			}
			
			xgFinal.append("<datatable component=\"xg-datatable\" id=\""+dataSetId+"\" bind=\"true\" div=\""+dataSetId+"\" realgrid=\""+isRealgrid+"\" description=\""+srdComment+"\">");
			xgFinal.append(SystemUtil.LINE_SEPARATOR);
			
			xgDatatable.append("	");
			xgDatatable.append("<format>");
			xgDatatable.append(SystemUtil.LINE_SEPARATOR);			
			
			if(isRealgrid) {
				// 리얼그리드 데이터셋 생성 코드 라인
				xgRealGird.append("	");
				xgRealGird.append("<gridColumn>");
				xgRealGird.append(SystemUtil.LINE_SEPARATOR);
			}
			

			//폼 또는 그리드 데이터 셋
			
			for(Entry<String, Object> tcEntry : tableColumnMap.entrySet()) {
				//logger.debug("entry: {}", entry.getValue());
				if(Map.class.isAssignableFrom(tcEntry.getValue().getClass())) {
					tcAttributes = new ArrayList<Map<String, Object>>();
					tcAttributes.add((Map<String, Object>) tcEntry.getValue());
				}
				else {
					tcAttributes = (List<Map<String, Object>>) tcEntry.getValue();	
				}					
				
				for(Map<String, Object> attrItem : tcAttributes) {
					tcAttribute = (Map<String, Object>) attrItem.get("@attribute");
					//logger.debug("attrItem: {}", attribute);
					
					if(false) {
						for(Entry<String, Object> attr : tcAttribute.entrySet()) {
							// 타겟 데이터
							tcAttrValue = ((String) attr.getValue()).trim();
							logger.debug("tableColumn : {} : {}", attr.getKey(), tcAttrValue);
						}
					}
					
					tcName = ((String) tcAttribute.get("name")).trim();
					tcType = ((String) tcAttribute.get("type")).trim();
					
					//logger.debug("tcName : {}", tcName);
					//logger.debug("tcType : {}", tcType);
					
					/**************
					 * ViewColumn
					 **************/
					if(viewColumnMap != null) {
						
						isFindViewColumn = false;
						for(Entry<String, Object> vcEntry : viewColumnMap.entrySet()) {
							//logger.debug("--viewColumn class: {}", vcEntry.getValue().getClass());
							if(Map.class.isAssignableFrom(vcEntry.getValue().getClass())) {
								vcAttributes = new ArrayList<Map<String, Object>>();
								vcAttributes.add((Map<String, Object>) vcEntry.getValue());
							}
							else {
								vcAttributes = (List<Map<String, Object>>) vcEntry.getValue();	
							}
							
							for(Map<String, Object> vcAttrItem : vcAttributes) {
								vcAttribute = (Map<String, Object>) vcAttrItem.get("@attribute");
								//logger.debug("attrItem: {}", attribute);
								vcName = ((String) vcAttribute.get("name")).trim();
								
								if(vcName.equals(tcName)) {
									//logger.debug("* Find tableColumn same name viewColumn: \n{}\n{}", tcAttribute, vcAttribute);
									vcFormat = (String) vcAttribute.get("format");
									if(vcFormat != null) {
										vcFormat = vcFormat.trim();
									}
									
									isFindViewColumn = true;
									break;
								}
							}
							
							if(isFindViewColumn) {
								break;
							}
							
						}
					}
					
					
					/**************
					 * ViewText
					 **************/
					if(viewTextMap != null) {
						
						isFindViewText = false;
						for(Entry<String, Object> vtEntry : viewTextMap.entrySet()) {
							//logger.debug("--ViewText class: {}", vtEntry.getValue().getClass());
							if(Map.class.isAssignableFrom(vtEntry.getValue().getClass())) {
								vtAttributes = new ArrayList<Map<String, Object>>();
								vtAttributes.add((Map<String, Object>) vtEntry.getValue());
							}
							else {
								vtAttributes = (List<Map<String, Object>>) vtEntry.getValue();	
							}
							
							for(Map<String, Object> vtAttrItem : vtAttributes) {
								vtAttribute = (Map<String, Object>) vtAttrItem.get("@attribute");
								//logger.debug("attrItem: {}", attribute);
								vtName = ((String) vtAttribute.get("name")).trim();

								//logger.debug("#.# {} : {}", vtName, tcName.concat("_t"));
								if(vtName.equals(tcName.concat("_t"))) {
									//logger.debug("* Find tableColumn same name ViewText: \n{}\n{}", tcAttribute, vtAttribute);
									vtText = (String) vtAttribute.get("text");
									if(vtText != null) {
										vtText = vtText.trim();
									}
									isFindViewText = true;
									break;
								}
							}
							
							if(isFindViewText) {
								break;
							}
							
						}
					}
					//////////////////////////////////////////////////////////
					
					if(tcType.contains("(") && tcType.contains(")")) {
						dtSize = tcType.substring(tcType.indexOf("(") + "(".length());
						dtSize = dtSize.substring(0, dtSize.lastIndexOf(")"));
					}
					else {
						dtSize = "0";
					}
					
					if(tcType.contains("(")) {
						dtType = tcType.substring(0, tcType.indexOf("("));
					}
					else {
						dtType = tcType;
					}
					
					dtDesc = null;
					dtFormat = null;
					
					if(isFindViewColumn) {
						//logger.debug("* Find tableColumn same name viewColumn: \n{}\n{}\n{}", tcAttribute, vcAttribute, vtAttribute);
						// 데이터셋 로직 구현 부분

						dtDesc = null;
						dtFormat = vcFormat;
						if(dtFormat != null && dtFormat.equals("[general]")) {
							dtFormat = null;
						}
						else if(dtFormat != null) {
							dtFormat = dtFormat.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "\\\"");
						}
						
						if(isFindViewText && vtText != null) {
							dtDesc = vtText;
						}
						else {
							dtDesc = "";	
						}
					}
					else {
						// ViewColumn이 존재하지 않는 tableColumn
						dtDesc = "";
					}
					
					String columnLien = "<column id=\""+stringUtil.getCamelCaseString(tcName)+"\" size=\""+dtSize+"\" type=\""+CONVERT_DATATABLE_TYPE.get(dtType) +"\""+ (dtFormat != null ? " format=\""+dtFormat+"\"" : "") + (dtDesc != null ? " description=\""+dtDesc+"\"" : "") +" column=\""+tcName+"\"></column>"; 
					//logger.debug(columnLien);
					
					xgDatatable.append("		");
					xgDatatable.append(columnLien);
					xgDatatable.append(SystemUtil.LINE_SEPARATOR);
				}
			}

			xgDatatable.append("	");
			xgDatatable.append("</format>");
			xgDatatable.append(SystemUtil.LINE_SEPARATOR);
			
			if(isRealgrid) {
				// 리얼그리드 데이터셋 생성 코드 라인
				xgRealGird.append("	");
				xgRealGird.append("</gridColumn>");
				xgRealGird.append(SystemUtil.LINE_SEPARATOR);
			}
			
			xgFinal.append(xgDatatable);
			xgFinal.append(xgRealGird);
			
			xgFinal.append("</datatable>");
			xgFinal.append(SystemUtil.LINE_SEPARATOR);
			
			out = xgFinal.toString();

			logger.debug("\n\n########### DATA SET ##############");
			logger.debug(out);
			
			/*
			if(isRealgrid) {
				throw new ApplicationException("TEST");
			}
			*/
		}
		else{
			throw new ApplicationException("srd에서 추출한 tableColumn 데이터가 존재하지 않습니다.");
		}
		

		return out;
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
		
		//asis 레포트파일은 데이터셋 생성에서 제외한다 (tobe에서 사용하지 않음)
		for(String filePrefix : PASS_FILE_PREFIX) {
			if(paths[(paths.length - 1)].toLowerCase().startsWith(filePrefix.toLowerCase())) {
				logger.debug("#데이터셋 생성 제외 파일: {}", path);
				return null;
			}
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
					
					if(line.length() > 2) last2Char = line.substring(line.length() - 2);
					if(line.length() > 3) last3Char = line.substring(line.length() - 3);					
					
					if(keyword.equals(T_TABLE_COLUMN) && line.startsWith(T_TABLE_COLUMN) || keyword.equals(TABLE_COLUMN) && line.startsWith(TABLE_COLUMN)) {
						data = line.substring(keyword.length());
												
						data = replaceSpecialCharacter(data);
						data = data.replaceFirst("\\(", "<tableColumn ");
						data = getChangeBracketChar(data);
						
						data = data.substring(0, data.lastIndexOf(")")).concat("/>");
						mergeTableColumn.append(data).append(SystemUtil.LINE_SEPARATOR);
						
						//logger.debug("{}: {}", keyword, data);
					}
					else if(keyword.equals(VIEW_COLUMN)) {
						
						if(line.startsWith(VIEW_COLUMN)) {
							data = line.substring(VIEW_COLUMN.length());
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
							//logger.debug("{}: {}", VIEW_COLUMN, data);
						}
						else if(isUseVColsLineSeparator) {
							data = line;
							data = getChangeBracketChar(data);
							data = replaceSpecialCharacter(data);
							
							
							logger.debug("#VCOLS 개행 후속라인 isUseVTextLineSeparator : {}, data : {}", isUseVColsLineSeparator, data);
							
							data = " [enter] ".concat(data);
							if(data.indexOf("\"") > -1 && ( data.endsWith(" )") || regexUtil.testPattern(data, "=([0-9a-zA-Zㄱ-ㅎ가-힣]+)\\)$") )) {
								data = data.substring(0, data.lastIndexOf(")")).concat("/>").concat(SystemUtil.LINE_SEPARATOR);
								isUseVColsLineSeparator = false;
							}
							mergeViewColumn.append(data);
						}
					}
					else if(keyword.equals(VIEW_TEXT)) {
						
						//logger.debug("#VIEW_TEXT Line : {}", line);
						
						if(line.startsWith(VIEW_TEXT)) {
							data = line.substring(VIEW_TEXT.length());
							//logger.debug("line data: {}", data);

							data = replaceSpecialCharacter(data);
							data = "<viewText ".concat(data);
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
							
							data = " [enter] ".concat(data);
							if(data.indexOf("\"") > -1 && ( data.endsWith(" )") || regexUtil.testPattern(data, "=([0-9a-zA-Zㄱ-ㅎ가-힣]+)\\)$") )) {
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
		
		mergeAsset.append("<comment>").append(comments).append("</comment>").append(SystemUtil.LINE_SEPARATOR);
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
				/*
				finalAsset.append("<!-- ");
				finalAsset.append(lineNo);
				finalAsset.append(" --> ");
				*/
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
		//logger.debug("out: {}", out);
		return out;
	}
	
}
