package com.universal.code.constants;

import java.math.BigDecimal;

public interface IOperateCode extends IServiceCode {

	public final static String MAPPER_NAMESPACE_PREFIX = "ubms";
	
	public final static String TX_TYPE_INSERT = "I";
	public final static String TX_TYPE_UPDATE = "U";
	public final static String TX_TYPE_REMOVE = "R";
	public final static String TX_TYPE_DELETE = "D";
	
	//REQUEST SERVLET SERVICE TYPE ( SPRING CONTROLLER or DWR CONTROLLER )
	public final static String SPRING_CONTROLLER = "SPRING";
	public final static String DWR_CONTROLLER = "DWR";
	public final static String DEFAULT_SERVLET = "DEFAULT_SERVLET";
	
	//Servlet init bean parameter 앞으로 유동변경가능하도록 변경될것
	public final static String DEFAULT_ENCODING = "UTF-8";
	public final static String DEFAULT_ENCODING_MS949 = "MS949";
	public final static String DEFAULT_ENCODING_EUCKR = "EUC-KR";
	public final static String DEFAULT_ENCODING_UTF8 = "UTF-8";
	public final static String KR_STRING_CHARSET_NAME = "8859_1";
	public final static String ISO_STRING_CHARSET_NAME = "ISO-8859-1";
	public final static String STR_ROOT_RULE_PRNT = "ROOT";
	public final static String DEFAULT_DB_VENDOR = "ORACLE";
	public final static String DEFAULT_SESSION_NAME = "sessionDTO";
	
	//기본 Calendar format 
	public final static String DEFAULT_CALENDAR_DATE_FORMAT = "yyyy-MM-dd"; 
	//고정 플래그값
	public final static String META_REC_STAT_ORIGINAL = "O";
	public final static String META_REC_STAT_HIDDEN = "H";
	public final static String META_REC_STAT_REMOVE = "D";
	public final static String META_CODE_USE_TYPE_FORM = "F";
	public final static String META_CODE_USE_TYPE_GRID = "G";
	public final static String META_CODE_USE_TYPE_SERVER = "S";
	public final static String META_CODE_USE_TYPE_ALL = "A";
	public final static String META_CODE_YES = "Y";
	public final static String META_CODE_NO  = "N";
	
	
	public final static String META_MAIL_TYPE_RISK_FACT  = "RISK_FACT"; //(위험 요인)
	public final static String META_MAIL_TYPE_PROG_DELY = "PROG_DELY"; //(진척 지연)
	public final static String META_MAIL_TYPE_PFMC_REG_DELY  = "PFMC_REG_DELY"; //(실적 등록 지연)
	
	//COMMON CODE REFERENCE VALUE NAME
	public final static String REF_VAL_ITEM  = "ITEM";
	
	//TREEGRID PAGE VIEW URI
	public final static String TREEGRID_PAGE_VIEW = "common/treegrid/body";
	
	//VALIDATION TYPE
	public final static int VALIDATE_DTO_FULL_FIELD_ANNO = 1;
	public final static int VALIDATE_DTO_SINGLE_FIELD_ANNO = 2;
	public final static int VALIDATE_SINGLE_VALUE_PATTERN = 3;
	public final static int VALIDATE_DTO_SINGLE_FIELD_PATTERN = 4;
	
	
	//IMPOERTANT CODE MASTER KEY
	public final static String CODE_MASTER_USR_TECH_CLAS = "CD_USR_TECH_CLAS";
	public final static String CODE_MASTER_ORG_DIV_INSD = "ORG_DIV_INSD";
	public final static String CODE_MASTER_ORG_DIV_OUTS = "ORG_DIV_OUTS";
	public final static String CODE_MASTER_COST_TYPE_OUTLAY = "COST_TYPE_02";
	
	//TREEGRID TYPE
	public final static String TREEGRID_LAYOUT = "Layout"; //그리드 레이아웃					IOperateCode.TREEGRID_LAYOUT
	public final static String TREEGRID_DATA = "Data"; //그리드 페이지 Body (비어있는 페이지)<B></B>	IOperateCode.TREEGRID_DATA
	public final static String TREEGRID_PAGE = "Page"; //그리드 데이타						IOperateCode.TREEGRID_PAGE
	
	public final static String PARAM_TREEGRID_ID = "GridID"; //그리드 아이디 파라메터명					IOperateCode.PARAM_TREEGRID_ID
	public final static String POSTFIX_TREEGRID_LAYOUT = "_Layout"; //그리드 레이아웃 스킨 파일 뒷단어			IOperateCode.POSTFIX_TREEGRID_LAYOUT
	public final static String POSTFIX_TREEGRID_PAGEDATA = "_PageData"; //그리드 페이지데이터 스킨 파일 뒷단어	IOperateCode.POSTFIX_TREEGRID_PAGEDATA
	
	//INDEX METHOD TYPE
	public final static String INDEX_LOGIN = "login"; 		// IOperateCode.INDEX_LOGIN
	public final static String INDEX_MAIN = "main"; 		// IOperateCode.INDEX_MAIN
	public final static String INDEX_BUSINESS = "business";	// IOperateCode.INDEX_BUSINESS
	public final static String INDEX_POPUP = "popup"; 		// IOperateCode.INDEX_POPUP

	//CODE MASTER META FIELD
	public final static String META_GLOBAL_CODE_MASTER_PROJECT = "COM"; // XMl
	
	//CODE FIELD
	public final static char SPECTYPE_XML = 'X'; // XMl
	public final static char SPECTYPE_JSON = 'J'; // JSON
	
	//SERVER SEQ PREFIX
	public final static String META_SEQ_FREFIX_FILE_RELS_CD = "FKS";
	public final static String META_SEQ_FREFIX_FILE_CD = "FSQ";
	
	//계획/실적 타입코드
	public final static String META_PARAM_PLAN = "plan";
	public final static String META_PARAM_RES = "res";
	
	//FILE ATCH 
	public final static String META_FILE_ATCH_DIV_DEFAULT = "STND";
	public final static String META_CD_FILE_WORK_STAT = "FILE_WORK_STAT_CPLT";
	
	//SearchParam setup data key name
	public final static String SEARCH_KEY_MENU_MODU_CD_MST_LIST = "MenuModuCdMstList";
	public final static String SEARCH_KEY_CD_MST = "cd_mst";
	public final static String SEARCH_KEY_CD_MST_PRJ = "cd_mst_prj";
	public final static String SEARCH_KEY_PRJ_CD = "prj_cd";
	public final static String SEARCH_KEY_SELT_PRJ_CD = "selt_prj_cd";
	public final static String SEARCH_KEY_PRJ_CD_ADM = "prj_cd_adm";
	public final static String SEARCH_KEY_CD = "cd";
	public final static String SEARCH_KEY_MENU_CD = "menu_cd";
	public final static String SEARCH_KEY_CD_MST_MENU_CD = "cdmst_menu_cd";
	public final static String SEARCH_KEY_USR_ID = "usr_id";
	public final static String SEARCH_KEY_ORG_CD = "org_cd";
	public final static String SEARCH_KEY_VIEW_TYPE = "viewType";
	public final static String SEARCH_KEY_VIEW_TYPE_POPUP = "POPUP";
	public final static String SEARCH_KEY_FILE_RELS_CD = "file_rels_cd";
	public final static String SEARCH_KEY_FILE_CD = "file_cd";
	public final static String SEARCH_KEY_FILE = "file";
	
	
	public final static String SEARCH_TYPE = "search_type";
	public final static String SEARCH_KEY = "search_key";
	
	
	
	
	//Login Status Type
	public final static String LOGN_STAT_SUCCESS = "success";
	public final static String LOGN_STAT_INVALID_PWD = "invalidPwd";
	public final static String LOGN_STAT_NOT_FOUND = "notfound";
	
	//method type
	public final static String METHOD_TYPE_CONTROLLER_INDEX = "index";
	public final static String METHOD_TYPE_CONTROLLER_GRID = "treegrid";
	public final static String METHOD_TYPE_COMPONENT = "component";
	
	//data type
	public final static String DS_TYPE_XML = "xml";
	public final static String DS_TYPE_JSON = "json";
	public final static String DS_TYPE_SERIALIZE = "serialize";
	
	
	//ELEMENT META FIELD
	public final static String XML_NODE_VAL = "#text";
	public final static String XML_NODE_ATTR = "@attribute";
	public final static String DEFAULT_ELEMENT_ROOT = "signature";
	public final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"$XmlEncoding\" standalone=\"$Standalone\"?>";
	public final static String XML_HEADER_ENCODING = "$XmlEncoding";
	public final static String XML_HEADER_STANDALONE = "$Standalone";
	public final static String DEFAULT_XML_STANDALONE = "yes";

	//META ATTR
	public final static String META_ATTR_ID = "id";				// IOperateCode.META_ATTR_ID
	public final static String META_ATTR_ALIAS = "alias";		// IOperateCode.META_ATTR_ALIAS
	public final static String META_ATTR_ARRAY = "array";		// IOperateCode.META_ATTR_ARRAY
	public final static String META_ATTR_DESC = "description";	// IOperateCode.META_ATTR_DESC
	public final static String META_ATTR_NAME = "name";			// IOperateCode.META_ATTR_NAME
	public final static String META_ATTR_TYPE = "type";			// IOperateCode.META_ATTR_TYPE
	public final static String META_ATTR_LENGTH = "length";		// IOperateCode.META_ATTR_LENGTH
	
	public final static String META_COMM_HEADER = "commonHeader";
	public final static String META_RULE_INFO = "ruleInfomation";
	public final static String META_COLUMN_INFO = "columnInfo";
	public final static String META_COLUMN_DATA = "columnData";
	public final static String META_ADMIN_CLAS = "ADMIN";
	public final static String META_PERM_CONTROL = "CONTROL";
	public final static String META_PERM_READ = "READ";
	public final static String META_PERM_NONE = "NONE";
	public final static String DEF_ARRAY_VALUE_NAME = "value";
	public final static String ELEMENT_COL = "cols";
	public final static String ELEMENT_ROW = "rows";
	public final static String ELEMENT_IN = "in";
	public final static String ELEMENT_OUT = "out";
	public final static Object[] JSON_EXPAND_ELEMENTS = {"value","cols","rows"};
	public final static String DEF_KEY_FILE_NAME = "file";
	
	//PARAM META FIELD
	public final static String PARAM_META_APP_CONTENTS = "appContents";
	public final static String PARAM_META_ELEMENT_ROOT = "elementRoot";
	public final static String PARAM_META_PAGINATION = "$pagination";
	public final static String PARAM_META_ENCODING = "$encoding";
	public final static String PAGINATION_TYPE_ROWBOUNDS = "rowBounds";
	public final static String PARAM_META_SEARCH_PARAM = "searchParam";
	public final static String PARAM_META_OPERATE_CODE = "operateCode";
	
	public final static String PARAM_META_MENU = "menu";
	public final static String PARAM_META_POPUP_MENU = "popupMenu";
	public final static String PARAM_META_CODES = "codes";
	
	//MARSHAL ANNOTATION VALUE FIELD
	public final static int	META_INPUT_FIELD = 1;
	public final static int META_OUTPUT_FIELD = 2;
	public final static int META_INOUT_FIELD = 3;
	
	//DEFAULT IR META FIELD
	public final static String ATTR_IRULE_CODE_TYPE = "iRuleCodeType";	
	public final static String META_RULE_CODE = "ruleCode";		// IOperateCode.META_RULE_CODE
	public final static String META_RULE_ID = "ruleId";			// IOperateCode.META_RULE_ID
	public final static String META_RULE_VALIDATE = "ruleValidate";			// IOperateCode.META_RULE_VALIDATE
	public final static String META_RULE_NAME = "ruleName";		// IOperateCode.META_RULE_NAME
	public final static String META_RULE_ALIAS = "ruleAlias";	// IOperateCode.META_RULE_ALIAS
	public final static String META_MODIFY_DATE = "modifyDate";	// IOperateCode.META_RULE_ALIAS
	
	public final static String STR_DELETE = "delete";
	public final static String STR_CONFIRM = "confirm";
	public final static String STR_PASS = "pass";
	public final static String STR_INPUT_CONTRACTION = "I";
	
	//DEFAULT SETTING VALUE FIELD
	public final static boolean CONFIRM_SYSTEMUTIL_SYSOUT = true;
	public final static boolean CONFIRM_SYSTEMUTIL_SYSERR = true;
	public final static boolean	BOOLEAN_DEF_VALUE = false;
	public final static Class<?> DEF_CONVERTER_TYPE = java.lang.String.class;

	public final static String MESSAGE_ISNULL = "message is null.";
	public final static String HTML_BR = "<br/>";
	public final static String HTML_NBSP = "&nbsp;";
	public final static String STR_ASTERISK = "*";
	public final static String STR_UNDERBAR = "_";
	public final static String STR_HYPHEN = "-";
	public final static String STR_EQUAL = "=";
	public final static String STR_SHARP = "#";
	public final static String STR_SLASH = "/";
	public final static String STR_TAB = "	";
	public final static String STR_BACK_SLASH = "\\";
	public final static String STR_COLON = ":";
	public final static String STR_SEMICOLON = ";";
	public final static String STR_DO = "do";
	public final static String STR_PIPE = "|";
	public final static String STR_BRACKET_START = "[";
	public final static String STR_BRACKET_END  = "]";
	
	public final static String STR_PAREN_START = "(";
	public final static String STR_PAREN_END  = ")";

	public final static String STR_BRACE_START = "{";
	public final static String STR_BRACE_END  = "}";
	
	public final static String STR_DOT = ".";
	public final static String STR_COMA = ",";
	public final static String STR_DOUBLE_QUOTATION = "\"";
	public final static String REQUEST_EXTENSION = ".mvc";
	public final static String STR_SUB_FIELD_SEPARATOR = ".";
	public final static String SPLIT_SUB_FIELD_SEPARATOR = STR_BACK_SLASH.concat(STR_SUB_FIELD_SEPARATOR);
	public final static String STR_SUB_GROUP_SEPARATOR = "$";
	public final static String STR_RESOLVER = "Resolver";
	public final static String STR_CLASS = "class";	
	public final static String STR_JUNIT = "junit";
	public final static String STR_INVOKE_TARGET = "InvokeTarget";
	public final static String STR_BLANK = "";
	public final static String EXT_DELIMETER = ".";
	public final static String STR_WHITE_SPACE = " ";
	public final static String RESOURCE_LASTMODIFIED_KEY = "lastModified";
	public final static String RESOURCE_PROPERTIES_KEY = "properties";
	public final static String STR_HTML_LINE_SEPARATOR = "<br>";
	
	
	public final static String STR_STRING = "String";
	public final static String STR_NUMBER = "Number";
	public final static String STR_BOOLEAN = "Boolean";	
	public final static String[] EXCLUDE_PACKAGE_STARTS = {"java.","javax."};
	public final static int		RULE_COLUMN_SOURCE_RESULT_ONLY = 0;
	public final static int		RULE_COLUMN_SOURCE_BOTH = 1;
	public final static int		RULE_COLUMN_SOURCE_DB_ONLY = 2;
	public final static double 	DOUBLE_ZERO_VALUE = 0.0D;
	public final static float 	FLOAT_ZERO_VALUE = 0.0F;
	public final static long 	LONG_ZERO_VALUE = 0L;
	public final static int 	INTEGER_ZERO_VALUE = 0;
	public final static short 	SHORT_ZERO_VALUE = 0;
	public final static byte 	BYTE_ZERO_VALUE = 0;
	public final static char 	CHAR_DEF_VALUE = ' ';
	public final static BigDecimal 	BIGDECIMAL_ZERO_VALUE = BigDecimal.ZERO;	
	 
	public final static String TEST_FIELD = "TEST_FIELD";
	
}
