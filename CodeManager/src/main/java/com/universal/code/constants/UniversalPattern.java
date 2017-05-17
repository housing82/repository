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
public interface UniversalPattern {

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