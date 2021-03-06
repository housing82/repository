package com.universal.code.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.constants.ISQLInjectionPattern;
import com.universal.code.constants.IUniversalPattern;
import com.universal.code.constants.IXSSPattern;

@Component
public class RegexUtil implements IUniversalPattern, IXSSPattern, ISQLInjectionPattern {

	protected static final Logger logger = LoggerFactory.getLogger(RegexUtil.class);

	public static final int DEFAULT_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL; 
	
	
	/**
	 * 주어진 패턴컴파일, Matcher 실행 및 Matcher 리턴
	 * @param contents
	 * @param pattern
	 * @return
	 */
	public Matcher match(String contents , String pattern, int flags) {
		String contentStr = StringUtil.NVL(contents);
		String patternStr = StringUtil.NVL(pattern);
		Pattern regex = null;
		if(flags == -1) {
			regex = Pattern.compile(patternStr);
		}
		else {
			regex = Pattern.compile(patternStr, flags);
		}
		return regex.matcher(contentStr);
	}

	public boolean testPattern(String objectStr, String patternStr) {
		return testPattern(objectStr, patternStr, DEFAULT_FLAGS);
	}
	
    /**
     * 대상문자(열) 에 patternStr(패턴) 에 해당하는 부분이 있는지 체크하여 줍니다.
     * @param objectStr : 대상 문자
     * @param patternStr : 정규패턴
     * @return
     */
	public boolean testPattern(String objectStr, String patternStr, int flags) {
		
		//if(logger.isDebugEnabled()) {
		//	logger.debug(CommonUtil.mergeObjectString(new Object[]{" *- matcherPatternFind -* objectStr :  " , objectStr , ", patternStr : " , patternStr}));
		//}
		
		boolean findMatcher = false;
		if(StringUtil.isNotEmptyStringArray(new String[]{objectStr, patternStr})){
			Matcher matcher = match(objectStr, patternStr, flags);
			findMatcher = matcher.find();
		}
		return findMatcher;
	}

	public List<String> findPatternToList(String targetString, String patternString){
		return findPatternToList(targetString, patternString, DEFAULT_FLAGS);
	}
	
	/**
	 * 대상문자열중 패턴에 해당하는 문자열중 "" 이 아닌 값을 리스트에 담아 리턴하여줍니다.
	 * @param targetString : 대상문자열
	 * @param patternString : 패턴
	 * @return
	 */
	public List<String> findPatternToList(String targetString, String patternString, int flags){

		List<String> patternSet = new ArrayList<String>();

        String targetStr = targetString;
        String patternStr  = patternString;

        if(!StringUtil.isNotEmptyStringArray(new String[]{targetStr, patternStr})){
        	return patternSet;
        }

  		Matcher matcher = match(targetStr, patternStr, flags);

  		int count = 0;
  		String matchStr = "";
  		while(matcher.find()){
  			matchStr = matcher.group().trim();
  			if(!matchStr.equals("")){
  				//if(logger.isDebugEnabled()) {
				//    logger.debug(CommonUtil.mergeObjectString(new Object[]{" Match Count [", (count++), "] ", matchStr}));
		    	//}
			    patternSet.add(matchStr);
  			}
  		}
  		return patternSet;
	}
	
	public String replaceAllPattern(String sentenceString, String patternString, String switchString){
		return replaceAllPattern(sentenceString, patternString, switchString, DEFAULT_FLAGS);
	}
	
	/**
	 * 대상문자열중 패턴에 해당하는 문자를 switchString 으로 모두 치환하여 리턴하여 줍니다.
	 * @param sentenceString
	 * @param patternString
	 * @param switchString
	 * @return
	 */
	public String replaceAllPattern(String sentenceString, String patternString, String switchString, int flags){

    	String sentenceStr = sentenceString;
    	String patternStr = patternString;
    	String switchStr = StringUtil.NVL(switchString);

        if(!StringUtil.isNotEmptyStringArray(new String[]{sentenceStr, patternStr})){
        	return sentenceStr;
        }else{
        	patternStr = "("+patternStr+")";
    		Matcher matcher = match(sentenceStr, patternStr, flags);
    		if(matcher.find()) {
    			sentenceStr = matcher.replaceAll(switchStr);
    		}
    	}

		return sentenceStr;
	}

	
	public String replaceAllPatternWrap(String sentenceString, String patternString, String postfixString, String prefixString){
		return replaceAllPatternWrap(sentenceString, patternString, postfixString, prefixString, DEFAULT_FLAGS);
	}
	
	/**
	 * 대상문자열중 패턴에 해당하는 문자의 앞과 뒤를 postfixStr {patternString} prefixStr 형태로 모두 치환하여 리턴하여줍니다.
	 * @param sentenceStr
	 * @param patternStr
	 * @param postfixStr
	 * @param prefixStr
	 * @return
	 */
	public String replaceAllPatternWrap(String sentenceString, String patternString, String postfixString, String prefixString, int flags){

    	String sentenceStr = sentenceString;
    	String patternStr  = patternString;
    	String postfixStr  = postfixString;
    	String prefixStr   = prefixString;

        if(!StringUtil.isNotEmptyStringArray(new String[]{sentenceStr, patternStr, postfixStr, prefixStr})){
        	return sentenceStr;
        }else{
        	patternStr = "("+patternStr+")";
    		Matcher matcher = match(sentenceStr, patternStr, flags);
    		if(matcher.find()) {
    			sentenceStr = matcher.replaceAll(postfixStr+"$1"+prefixStr);
    		}
    	}

		return sentenceStr;
	}

	
}
