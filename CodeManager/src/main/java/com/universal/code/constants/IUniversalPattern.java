package com.universal.code.constants;

/**
* <p>Title: UniversalPattern</p>
* <p>Description:
* 보안 패턴 정규식 클래스
* </p>
* <p>Copyright: Copyright (c) 2011</p>
* <p>Company: mvc</p>
* @since 2011. 07. 04
* @author ksw
* @version 1.0
*/
public interface IUniversalPattern {

    /**
     * 한글 패턴용 스트링.
     */
    public static final String HANGUL  = "^[가-힣]+";

    /**
     * 특수문자를 제외한 한영숫자의 일반적인 패턴용 스트링.
     */
    public static final String CUSTOM  = "^[0-9a-zA-Z가-힣]+";

    /**
     * 숫자 패턴용 스트링.
     */
    public static final String NUMBER  = "^[-+0-9]+";

    /**
     * 영문 패턴용 스트링.
     */
    public static final String ENGLISH = "^[A-Za-z]+";

    /**
     * 영숫자 패턴용 스트링.
     */
    public static final String NUMERIC = "^[A-Za-z0-9]+";

    /**
     * 주민등록번호 페턴
     */
    public static final String REGISTER_NUMBER_TYPE_01 = "(\\d{6})([- 　\\t\\n\\x0B\\f\\r])[1234]\\d{6}";

    public static final String REGISTER_NUMBER_TYPE_02 = "(\\d{6})\\d{7}";

    public static final String REGISTER_NUMBER_TYPE_03 = "(\\d{6})([ 　\\t\\n\\x0B\\f\\r])[1234]\\d{6}";

    public static final String REGISTER_NUMBER = "(\\d{6})([- 　\\t\\n\\x0B\\f\\r]+)?[1234]\\d{6}";

    public static final String NUMBER_PATTERN01 = "^[1-9][0-9]*$"; 			// 는 자연수를 표시할수 있다. 
    public static final String NUMBER_PATTERN02 = "^(0|[1-9][0-9]*)$"; 		//  는 0을 포함하는 자연수 
	public static final String NUMBER_PATTERN03 = "^(0|-?[1-9][0-9]*)$"; 	//  정수표시 
	public static final String NUMBER_PATTERN04 = "^[0-9]+(.[0-9]+)?$"; 	//  소숫점 표시 
	public static final String NUMBER_PATTERN05 = "^[0-9]+(.[0-9])?$"; 		//  소수점 둘째자리 까지 
	public static final String NUMBER_PATTERN06 = "^[0-9]+(.[0-9]{1,2})?$"; //  소수점 둘째자리나 첫째자리 
	public static final String NUMBER_PATTERN07 = "^[0-9]{1,3}(,[0-9])*(.[0-9]{1,2})?$"; //  돈의 표시... 
    		
    /**
     * 파일명에 들어갈수 없는 특수문자
     */
    public static final String PTN_FILE_NAME_NOT_SPECIAL_CHAR = "([/:*?\"\\<\\>\\|\\\\]+)";
    
    /**
     * 휴대전화 페턴
     */
    public static final String MOBILE_PATTERN = "(01[0|1|7|8|9])([- 　\\t\\n\\x0B\\f\\r]*)(\\d{3,4})([- 　\\t\\n\\x0B\\f\\r]*)\\d{4}";

    /**
     * 이메일 페턴
     */
    public static final String EMAIL_PATTERN = "([_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+)";

    /**
     * html 의 mailto 제거용 패턴
     */
    public static final String HTML_MAILTO = "(\\<a href=\"mailto:[^>]*?>)(.*?)(\\<\\/a\\>)";

    
    public static final String PTN_STARTS_WITH_WINDOW_PATH = "^[a-zA-Z]:";
    
    public static final String PTN_WINDOW_ROOT_PATH = "^([a-zA-Z]:[\\\\|/]+)$";
    
}