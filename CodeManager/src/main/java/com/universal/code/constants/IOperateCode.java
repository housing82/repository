package com.universal.code.constants;

import java.math.BigDecimal;
import java.util.List;

public interface IOperateCode extends IServiceCode {

	public final static String MAPPER_NAMESPACE_PREFIX = "codeManager";
	public final static String JAVA_EXTENSION = ".java";
	public final static String VERSION_TAG_V = "v";
	
	
	public final static String TX_TYPE_INSERT = "I";
	public final static String TX_TYPE_UPDATE = "U";
	public final static String TX_TYPE_REMOVE = "R";
	public final static String TX_TYPE_DELETE = "D";
	public final static String TX_TYPE_MERGE = "M";
	
	//REQUEST SERVLET SERVICE TYPE ( SPRING CONTROLLER or DWR CONTROLLER )
	public final static String SPRING_CONTROLLER = "SPRING";
	public final static String DWR_CONTROLLER = "DWR";
	public final static String DEFAULT_SERVLET = "DEFAULT_SERVLET";
	
	//Servlet init bean parameter 앞으로 유동변경가능하도록 변경될것
	public final static String DEFAULT_ENCODING = "UTF-8";
	public final static String ENCODING_MS949 = "MS949";
	public final static String ENCODING_EUCKR = "EUC-KR";
	public final static String ENCODING_UTF8 = "UTF-8";
	public final static String KR_STRING_CHARSET_NAME = "8859_1";
	public final static String ISO_STRING_CHARSET_NAME = "ISO-8859-1";
	public final static String STR_ROOT_RULE_PRNT = "ROOT";
	public final static String DEFAULT_DB_VENDOR = "ORACLE";
	public final static String DEFAULT_SESSION_NAME = "sessionDTO";

	//VALIDATION TYPE
	public final static int VALIDATE_DTO_FULL_FIELD_ANNO = 1;
	public final static int VALIDATE_DTO_SINGLE_FIELD_ANNO = 2;
	public final static int VALIDATE_SINGLE_VALUE_PATTERN = 3;
	public final static int VALIDATE_DTO_SINGLE_FIELD_PATTERN = 4;
	
	//CODE FIELD
	public final static char SPEC_TYPE_XML = 'X'; // XMl
	public final static char SPEC_TYPE_JSON = 'J'; // JSON
	
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
	
	public final static String METHOD_PREF_SAVE = "save";
	public final static String METHOD_PREF_INSERT = "insert";
	public final static String METHOD_PREF_UPDATE = "update";
	public final static String METHOD_PREF_DELETE = "delete";

	public final static String CALLE_OUT_VAR_INSERT_COUNT = "insCnt";
	public final static String CALLE_OUT_VAR_UPDATE_COUNT = "updCnt";
	public final static String CALLE_OUT_VAR_DELETE_COUNT = "delCnt";
	
	public final static String CALLEE_VAR_POST_LIST = List.class.getSimpleName();	
	public final static String CALLEE_VAR_POST_COUNT = "Cnt";
	
	public final static String WRAPPER_TYPE_INTEGER = Integer.class.getSimpleName();	
	
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
	
	public final static String SERVICE_CODE_SC = "SC";
	public final static String SERVICE_CODE_BC = "BC";
	public final static String SERVICE_CODE_DC = "DC";
	
	
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
	
	public final static String STR_PLUS = "+"; // IOperateCode.STR_PLUS
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
	
	
}
