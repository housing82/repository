package com.universal.code.constants;
import java.util.regex.Pattern;

/**
* <p>Title: SQLInjectionPattern</p>
* <p>Description:
* 보안 패턴 정규식 클래스
* </p>
* <p>Copyright: Copyright (c) 2011</p>
* <p>Company: mvc</p>
* @since 2011. 07. 04
* @author ksw
* @version 1.0
*/
public interface SQLInjectionPattern {

    /**
     * 한글 패턴용 스트링.
     */
    public static final String singleQuotation  = "[']+";

    /**
     * 한글 패턴용 스트링.
     */
    public static final String doubleHyphen  = "[--]+";

}