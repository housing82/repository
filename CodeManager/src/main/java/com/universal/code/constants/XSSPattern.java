package com.universal.code.constants;
import java.util.regex.Pattern;

/**
* <p>Title: CrossSiteScriptingPattern</p>
* <p>Description:
* 보안 패턴 정규식 클래스
* </p>
* <p>Copyright: Copyright (c) 2011</p>
* <p>Company: mvc</p>
* @since 2011. 07. 04
* @author ksw
* @version 1.0
*/
public interface XSSPattern {

    /**
     * HTML_TAG 패턴용 스트링.
     */
    public static final String HTML_TAG = "\\<.*?\\>";

    /**
     * HTML_STYLE 패턴용 스트링.
     */
    public static final String HTML_STYLE = "\\<style[^>]*?>.*?\\<\\/style\\>";

    /**
     * HTML_SCRIPT 패턴용 스트링.
     */
    public static final String HTML_SCRIPT = "\\<(no)?script[^>]*?>.*?\\<\\/(no)?script\\>";

    /**
     * HTML_OPTION 패턴용 스트링.
     */
    public static final String HTML_OPTION = "\\<option[^>]*?>.*?\\<\\/option\\>";

    /**
     * HTML_HEAD 패턴용 스트링.
     */
    public static final String HTML_HEAD = "\\<head[^>]*?>.*?\\<\\/head\\>";

    /**
     * HTML_IFRAME 패턴용 스트링.
     */
    public static final String HTML_IFRAME = "\\<iframe[^>]*?>.*?\\<\\/iframe\\>";

    /**
     * HTML_FRAME 패턴용 스트링.
     */
    public static final String HTML_FRAME = "\\<frame[^>]*?>.*?\\<\\/frame\\>";

    /**
     * HTML_FRAMESET 패턴용 스트링.
     */
    public static final String HTML_FRAMESET = "\\<frameset[^>]*?>.*?\\<\\/frameset\\>";

    /**
     * HTML_SCRIPT_REPORT 패턴용 레포트 스트링.
     */
    public static final String HTML_SCRIPT_REPORT = "\\&lt;script[^>]*?&gt;.*?\\&lt;\\/script\\&gt;";

    /**
     * HTML_IFRAME_REPORT 패턴용 레포트 스트링.
     */
    public static final String HTML_IFRAME_REPORT = "\\&lt;iframe[^>]*?&gt;.*?\\&lt;\\/iframe\\&gt;";

    /**
     * HTML_FRAME_REPORT 패턴용 레포트 스트링.
     */
    public static final String HTML_FRAME_REPORT = "\\&lt;frame[^>]*?&gt;.*?\\&lt;\\/frame\\&gt;";

    /** test 필요 */
    public static final Pattern HTML_SCRIPT2 = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);

    /** test 필요 */
    public static final Pattern HTML_STYLE2 = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL);

    /** test 필요 */
    public static final Pattern HTML_TAG2 = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");



}