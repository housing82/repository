package com.universal.code.utils;

import java.util.ArrayList;
import java.util.List;

import jregex.Matcher;
import jregex.Pattern;
import jregex.REFlags;
import jregex.Replacer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.constants.ISQLInjectionPattern;
import com.universal.code.constants.IUniversalPattern;
import com.universal.code.constants.IXSSPattern;

@Component
public class JRegexUtil implements IUniversalPattern, IXSSPattern, ISQLInjectionPattern {

	protected static final Logger logger = LoggerFactory.getLogger(JRegexUtil.class);

	
	/**
	 * 주어진 패턴컴파일, Matcher 실행 및 Matcher 리턴
	 * @param contents
	 * @param pattern
	 * @return
	 */
	public Matcher match(String contents , String pattern) {
		String contentStr = StringUtil.NVL(contents);
		Pattern pa = pattern(pattern);
		
		return pa.matcher(contentStr);
	}

	public Pattern pattern(String pattern) {
		String patternStr = StringUtil.NVL(pattern);
		return new Pattern(patternStr, REFlags.MULTILINE);
	}
	
    /**
     * 대상문자(열) 에 patternStr(패턴) 에 해당하는 부분이 있는지 체크하여 줍니다.
     * @param objectStr : 대상 문자
     * @param patternStr : 정규패턴
     * @return
     */
	public boolean testPattern(String objectStr, String patternStr) {
		
		//if(logger.isDebugEnabled()) {
		//	logger.debug(CommonUtil.mergeObjectString(new Object[]{" *- matcherPatternFind -* objectStr :  " , objectStr , ", patternStr : " , patternStr}));
		//}
		
		boolean findMatcher = false;
		if(StringUtil.isNotEmptyStringArray(new String[]{objectStr, patternStr})){
			Matcher matcher = match(objectStr, patternStr);
			findMatcher = matcher.find();
		}
		return findMatcher;
	}

	/**
	 * 대상문자열중 패턴에 해당하는 문자열중 "" 이 아닌 값을 리스트에 담아 리턴하여줍니다.
	 * @param targetString : 대상문자열
	 * @param patternString : 패턴
	 * @return
	 */
	public List<String> findPatternToList(String targetString, String patternString){

		List<String> patternSet = new ArrayList<String>();

        String targetStr = targetString;
        String patternStr  = patternString;

        if(!StringUtil.isNotEmptyStringArray(new String[]{targetStr, patternStr})){
        	return patternSet;
        }

  		Matcher matcher = match(targetStr, patternStr);

  		while(matcher.find()){
  			patternSet.add(matcher.toString());
  		}
  		
  		return patternSet;
	}


	/**
	 * 대상문자열중 패턴에 해당하는 문자를 switchString 으로 모두 치환하여 리턴하여 줍니다.
	 * @param sentenceString
	 * @param patternString
	 * @param switchString
	 * @return
	 */
	public String replaceAllPattern(String sentenceString, String patternString, String switchString){

    	String sentenceStr = sentenceString;
    	String patternStr = patternString;
    	String switchStr = StringUtil.NVL(switchString);

        if(!StringUtil.isNotEmptyStringArray(new String[]{sentenceStr, patternStr})){
        	return sentenceStr;
        }else{
        	patternStr = "("+patternStr+")";
    		Matcher matcher = match(sentenceStr, patternStr);
    		
    		if(matcher.find()) {
    			Replacer replacer = pattern(patternStr).replacer(switchStr);
    			sentenceStr = replacer.replace(sentenceStr);
    		}
    	}

		return sentenceStr;
	}

	
	/**
	 * 대상문자열중 패턴에 해당하는 문자의 앞과 뒤를 postfixStr {patternString} prefixStr 형태로 모두 치환하여 리턴하여줍니다.
	 * @param sentenceStr
	 * @param patternStr
	 * @param postfixStr
	 * @param prefixStr
	 * @return
	 */
	public String replaceAllPatternWrap(String sentenceString, String patternString, String postfixString, String prefixString){

    	String sentenceStr = sentenceString;
    	String patternStr  = patternString;
    	String postfixStr  = postfixString;
    	String prefixStr   = prefixString;

        if(!StringUtil.isNotEmptyStringArray(new String[]{sentenceStr, patternStr, postfixStr, prefixStr})){
        	return sentenceStr;
        }else{
        	patternStr = "("+patternStr+")";
    		Matcher matcher = match(sentenceStr, patternStr);
    		if(matcher.find()) {
    			Replacer replacer = pattern(patternStr).replacer(postfixStr+"$1"+prefixStr);
    			sentenceStr = replacer.replace(sentenceStr);
    		}
    	}

		return sentenceStr;
	}

	
}
