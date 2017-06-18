package com.universal.code.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hibernate.pretty.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.exception.ApplicationException;

@Component
public class StringUtil {

	private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);
	
	 /**
     * <p>
     * The maximum size to which the padding constant(s) can expand.
     * </p>
     */
    private static final int PAD_LIMIT = 8192;

    /**
     * <p>
     * An array of <code>String</code> s used for padding.
     * </p>
     *
     * <p>
     * Used for efficient space padding. The length of each String expands as
     * needed.
     * </p>
     */
    private static final String[] PADDING = new String[Character.MAX_VALUE];

    static {
        // space padding is most common, start with 64 chars
        PADDING[32] = "                                                                ";
    }
    
    /**
     * DEFAULT EMPTY STRING
     */
    public final static String EMPTY = "";
    
    
	public static boolean hasText(String str){
		return org.springframework.util.StringUtils.hasText(str);
	}
	
	/**
	 * str의 isEmpty 여부를 체크하 고 false 라면 ""을 반환합니다.
	 * @param str
	 * @return
	 */
	public static String NVL(String str){
		return NVL(str, null);
	}
	
	/**
	 * str의 isEmpty 여부를 체크하 고 false 라면 defaultStr을 반환합니다.
	 * @param str
	 * @param defaultStr
	 * @return
	 */
    public static String NVL(String str, String defs) {

    	String out = str;
    	String defaults = defs;
    	
        if (isEmpty(out)) {
        	
        	if(isEmpty(defaults)) {
        		out = "";
        	}
        	else {
        		out = defaults;
        	}
        }
        return out;
    }
    
	public char[] getCharArray(String str) {
		char[] out = str.toCharArray(); // Char[]로 변환
		return out;
	}

	public char getCharacter(String str, int index) {
		char[] charArray = str.toCharArray(); // Char[]로 변환

		if(charArray.length <= index) {
			throw new RuntimeException(" 바인드된 케릭터 인덱스가 케릭터 길이와 같거나 클수없습니다. 케릭터 길이 : " + charArray.length + ", 바인드된 케릭터 인덱스 : " + index);
		}
		return charArray[index];
	}
	
	
    /**
     * str의 byte 길이를 리턴하여줍니다. (한글2바이트)
     * @param str
     * @return
     */
	public int getCharLength(String str) {
		return getCharLength(str, 2);
	}
	
    /**
     * str의 byte 길이를 리턴하여줍니다. (한글은 바인드된 korByte로 계산)
     * @param str
     * @return
     */
    public int getCharLength(String str, int korByte) {
    	
    	int out = 0;
    	String strs = str;
		char tempChar[] = new char[strs.length()];

		for (int i = 0; i < tempChar.length; i++) {
			tempChar[i] = strs.charAt(i);
			if (tempChar[i] < 128) {
				out++;
			} else {
				out += korByte;
			}
		}

		return out;
    }
    
    /**
     * 문자열의 바이트 계산
     * @param str
     * @return int
     */
    public int getBytesLength(String str) {
    	return getBytesLength(str, null);
    }
    
    /**
     * 문자열의 바이트 계산(인코딩설정가능)
     * @param str
     * @param encoding : 기본 시스템 파일 인코딩
     * @return int
     */
    public int getBytesLength(String str, String encoding) {
    	
    	int out = 0;
    	byte[] byteString = null;
    	String strEncoding = null;
    	if( StringUtils.isNotEmpty(encoding) ) {
    		strEncoding = encoding;
    	}
    	else {
    		strEncoding = SystemUtil.FILE_ENCODING;
    	}
    	
    	try {
    		byteString = str.getBytes(strEncoding);
    		out = byteString.length;
    	} catch (UnsupportedEncodingException e) {
			throw new ApplicationException("문자열의 바이트 계산 장애발생.", e);
		}
    	
    	return out;
    }
 
    /**
     * 반각문자로 변경한다
     * @param src 변경할값
     * @return String 변경된값
     */
    public String toHalfChar(String str)
    {
    	if(NVL(str).equals("")) return "";
    	String target = str.trim();

        StringBuffer strBuf = new StringBuffer();

        char c = 0;
        int nSrcLength = target.length();
        for (int i = 0; i < nSrcLength; i++) {
            c = target.charAt(i);
            //영문이거나 특수 문자 일경우.
            if (c >= '！' && c <= '～') {
                c -= 0xfee0;
            }
            else if (c == '　') {
                c = 0x20;
            }
            // 문자열 버퍼에 변환된 문자를 쌓는다
            strBuf.append(c);
        }
        return strBuf.toString();
    }

    /**
     * 전각문자로 변경한다.
     * @param src 변경할값
     * @return String 변경된값
     */
    public String toFullChar(String str)
    {
    	if(NVL(str).equals("")) return "";
    	String target = str.trim();

        // 변환된 문자들을 쌓아놓을 StringBuffer 를 마련한다
        StringBuffer strBuf = new StringBuffer();
        char c = 0;
        int nSrcLength = target.length();
        for (int i = 0; i < nSrcLength; i++) {
            c = target.charAt(i);
            //영문이거나 특수 문자 일경우.
            if (c >= 0x21 && c <= 0x7e) {
                c += 0xfee0;
            }
            //공백일경우
            else if (c == 0x20) {
                c = 0x3000;
            }
            // 문자열 버퍼에 변환된 문자를 쌓는다
            strBuf.append(c);
        }
        return strBuf.toString();
    }


    public String replaceAllIgnoreCase(String str_sentence, String str_from, String str_to){
    	String result = "";
    	String sentence = str_sentence;
    	String from = str_from;
    	String to = str_to;
    	if(sentence == null || from == null || to == null) {
    		return result;
    	}else{
    		result = sentence.replaceAll("(?i)"+from, to);
    	}

    	return result;
    }

	public String getFirstCharUpperCase(String strWord) {
		return getCharUpperCase(strWord, 1);
	}
	
	public String getCharUpperCase(String strWord, int upperCaseCount) {
		
		String out = null;
		if(upperCaseCount < 1) {
			upperCaseCount = 1;
		}
		
		String word = NVL(strWord, "").trim();
		if(word.length() > upperCaseCount) {
			out = word.substring(0,upperCaseCount).toUpperCase() + word.substring(upperCaseCount);
		}
		else {
			out = word.toUpperCase();
		}
		
		return out;
	}
	
	public String getFirstCharLowerCase(String strWord) {
		
		String out = null;
		
		String word = NVL(strWord, "").trim();
		if(word.length() > 1) {
			out = word.substring(0,1).toLowerCase() + word.substring(1);
		}
		else {
			out = word.toLowerCase();
		}
		
		return out;
	}
	
	public String getCharLowerCase(String strWord, int lowerCaseCount) {
		
		String out = null;
		if(lowerCaseCount < 1) {
			lowerCaseCount = 1;
		}
		
		String word = NVL(strWord, "").trim();
		if(word.length() > lowerCaseCount) {
			out = word.substring(0,lowerCaseCount).toLowerCase() + word.substring(lowerCaseCount);
		}
		else {
			out = word.toLowerCase();
		}
		
		return out;
	}
	
	public String getCamelCaseString(String str){
		
		StringBuffer out = new StringBuffer();
		String originalStr = (str.isEmpty() ? "" : str);
		String tempStr = originalStr.toLowerCase();

		if(tempStr.indexOf("_") > -1) {
			
			for(String metaName : tempStr.split("_")){
				out.append(getFirstCharUpperCase(metaName));
			}
			
		}
		else {
			out.append(getFirstCharUpperCase(tempStr));
		}
		
		return getFirstCharLowerCase(out.toString());
	}
	
	public String getJavaFieldNaming(String str){
		
		StringBuffer out = new StringBuffer();
		String originalStr = (str.isEmpty() ? "" : str);
		String tempStr = originalStr.toLowerCase();

		if(tempStr.indexOf("_") > -1) {
			int idx = 0;
			for(String metaName : tempStr.split("_")){
				if(idx == 0) {
					out.append(getFirstCharLowerCase(metaName));	
				}
				else {
					out.append(getFirstCharUpperCase(metaName));
				}
				idx++;
			}
			
		}
		else {
			out.append(getFirstCharLowerCase(tempStr));
		}
		
		return out.toString();
	}
	
	
    public String replaceFirstIgnoreCase(String str_sentence, String str_from, String str_to){
    	String result = "";
    	String sentence = str_sentence;
    	String from = str_from;
    	String to = str_to;
    	if(sentence == null || from == null || to == null) {
    		return result;
    	}else{
    		result = sentence.replaceFirst("(?i)"+from, to);
    	}

    	return result;
    }

    
	public String replaceAllCaseMaintain(String str_sentence, String str_pattern, String str_start, String str_end){

    	String result = "";
    	String sentence = str_sentence;
    	String pattern = str_pattern;
    	String to_start = str_start;
    	String to_end = str_end;
    	if(sentence == null || pattern == null || to_start == null || to_end == null) {
    		return result;
    	}else{
    		pattern = findSpecialCharacter(pattern);

    		pattern = "("+pattern+")";
    		Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    		Matcher matcher = regex.matcher(sentence);
    		result = matcher.replaceAll(to_start+"$1"+to_end);
    	}


		return result;
	}
	
	
	public String findSpecialCharacter(String str){
		return findSpecialCharacter(str, null, null, null, null);
	}
	
	public String findSpecialCharacter(String str, String headPattern, String bodyPattern, String endPattern, String splitPattern){

		String out = str;
		if(out == null) return "";

		String[] relationPart = null;
		String pattern = "([\\*\\(\\)\\[\\{\\_\\.\\\"\\+\\/\\|\\\\])";
		Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = null;
		String temp = "";
		
		if(headPattern != null && bodyPattern != null && endPattern != null && splitPattern != null) {

			temp = out.replaceAll(bodyPattern,"");
			if(logger.isDebugEnabled()) {
				logger.debug(CommonUtil.addString("- findSpc step 1 : " , temp));
			}
			temp = temp.replaceAll(headPattern,"");
			if(logger.isDebugEnabled()) {
				logger.debug(CommonUtil.addString("- findSpc step 2 : " , temp));
			}
			temp = temp.replaceAll(endPattern,"");
			if(logger.isDebugEnabled()) {
				logger.debug(CommonUtil.addString("- findSpc step 3 : " , temp));
			}
			relationPart = temp.split(splitPattern);
			if(logger.isDebugEnabled()) {
				logger.debug(CommonUtil.addString("- relationPart.length : " , relationPart.length));
			}
			
			for(int i = 0; i < relationPart.length;i++){
				if(!relationPart[i].trim().equals("")) {
					if(logger.isDebugEnabled()) {
						logger.debug(CommonUtil.addString("relationPart[", i, "] : " , relationPart[i]));
					}
					matcher = regex.matcher(relationPart[i]);
					if(i == (relationPart.length-1)) relationPart[i] = matcher.replaceAll("\\\\$1")+endPattern;
					else relationPart[i] = matcher.replaceAll("\\\\$1")+bodyPattern;
				}
			}

			out = headPattern+(StringUtils.join(relationPart, ""));
		}else{
			matcher = regex.matcher(out);
			out = matcher.replaceAll("\\\\$1");
		}

		return out;
	}
	
	/**
	 * 문자열의 오른쪽 공백제거 
	 * @param str
	 * @return
	 */
	public String rtrim(String str) {
		
		String out = str;
		
		char[] val = out.toCharArray();
		int st = 0;
		int len = out.length();

		while (st < len && val[len-1] <= ' ') {
			len--;
		}
		
		out = out.substring(0, len);
		
		return out;
	}


	/**
	 * 문자열의 왼쪽 공백제거
	 * @param str
	 * @return
	 */
	public String ltrim(String str) {

		String out = str;
		char[] val = out.toCharArray();
		int st  = 0;
		int len = out.length();

		while (st < len && val[st] <= ' ') {
			st++;
		}
		
		out = out.substring(st, len);
		return out;
	}
	
	
    /**
     * str의 영문 도는 한글 여부를 리턴합니다.
     * @param TargetStr
     * @return
     */
    public boolean isKoreanString(String str) {
    	
        boolean out = false; //영어
        int idx = 0;
        String target = str;
        int hashCode = 0;
        
        if (target != null) {
            for (idx = 0; idx < target.length(); idx++) {
                Character code = new Character(target.charAt(idx));
                hashCode = code.hashCode();
                if ((hashCode > 255) || (hashCode < 0)) {
                    out = true; // 한글
                }
            }
        }
        
        return out;
    }
    
    
    /**
     * str의 모든 공백을 제거하여 리턴합니다.
     * @param src
     * @return
     */
    public static String removeWhiteSpace(String str) {
    	
    	String out = str;
    	
        if(StringUtils.isNotEmpty(out)) {
        	out = str.replaceAll("\\s", "");
        }
        
        return out;    
    }
    
    
    /**
     * 문자열을 integer 형으로 변환합니다.
     * @param input
     * @return
     */
    public int parseInt(String str) {
        return parseInt(str, 0);
    }

    /**
     * 문자열이 null 이면 integer output 을 반환합니다.
     * @param input
     * @param output
     * @return
     */
    public int parseInt(String str, int defs) {
    	
    	String out = str;
    	int defaults = defs;
    	
        if (out == null) {
            return defaults;
        }
        return Integer.parseInt(out);
    }

    /**
     * 문자열을 Long 형으로 반환합니다.
     * @param input
     * @return
     */
    public long parseLong(String str) {
        return parseLong(str, 0);
    }

    /**
     * 문자열이 null 이면  long type 의 output 을 반환합니다.
     * @param input
     * @param output
     * @return
     */
    public long parseLong(String str, long defs) {
    	
    	String out = str;
    	long defaults = defs;
    	
        if (out == null) {
            return defaults;
        }
        return Long.parseLong(out);
    }

    /**
     * 문자열을 더블형으로 형변환하거나 null 일경우 0.000 을 반환합니다
     * @param input
     * @return
     */
    public double parseDouble(String str) {
        return parseDouble(str, 0);
    }

    /**
     * 문자열이 null 이면 output 을 반환하고 null이 아니면 더블형으로 형변환하여 반환합니다.
     * @param input
     * @param output
     * @return
     */
    public double parseDouble(String str, double defs) {
    	
    	String out = str;
    	double defaults = defs;
    	
        if (out == null) {
            return defaults;
        }
        return Double.parseDouble(out);
    }
    
    /**
     * 문자열을 플롯형으로 형변환하거나 null 일경우 0.000 을 반환합니다
     * @param input
     * @return
     */
    public Float parseFloat(String str) {
        return parseFloat(str, 0);
    }

    /**
     * 문자열이 null 이면 output 을 반환하고 null이 아니면 플롯형으로 형변환하여 반환합니다.
     * @param input
     * @param output
     * @return
     */
    public Float parseFloat(String str, float defs) {
    	
    	String out = str;
    	float defaults = defs;
    	
        if (out == null) {
            return defaults;
        }
        return Float.parseFloat(out);
    }
    
    

	/**
	 * 바인드된 문자배열을 배열값의 문자열길이를 기준으로 순차정렬 하여줍니다.
	 * @param arr
	 * @return
	 */
	public String[] arrayOrdered(String[] arr){
		return arrayOrdered(arr, "asc");
	}

	/**
	 * 바인드된 문자배열을 배열값의 문자열길이를 기준으로 order 순으로 정렬하여줍니다.
	 * @param arr
	 * @return
	 */
	public String[] arrayOrdered(String[] arr, String sending){

		if(arr == null) return null;

		String[] array = arr;
		final String orderBy = StringUtils.isEmpty(sending) ? "asc" : sending.toLowerCase();

        Arrays.sort(array, new Comparator<Object>(){
	    		String frontStr = "";
	    		String backStr = "";
	    		int position = 0;

				public int compare(final Object front, final Object backend)
	            {
	                frontStr = (String) front;
	                backStr = (String) backend;
            		if(orderBy.equalsIgnoreCase("desc")){
            			position = backStr.length() - frontStr.length();
            		}else{
            			position = frontStr.length() - backStr.length();
            		}

	                return position;
	            }
	        }
	    );

        return array;
	}


	/**
	 * 바인드된 리스트를 값의 문자열길이를 기준으로 순차정렬 하여줍니다.
	 * @param arr
	 * @return
	 */
	public List<?> listOrdered(List<?> arrays){
		return listOrdered(arrays, "asc");
	}

	/**
	 * 바인드된 리스트를 값의 문자열길이를 기준으로 order 순으로 정렬하여줍니다.
	 * @param arr
	 * @return
	 */
	public List<?> listOrdered(List<?> arrays, String sending){

		if(arrays == null) return null;

		List<?> array = arrays;
		final String orderBy = StringUtils.isEmpty(sending) ? "asc" : sending.toLowerCase();

        Collections.sort(array, new Comparator<Object>(){
	    		String frontStr = "";
	    		String backStr = "";
	    		int position = 0;

				public int compare(final Object front, final Object backend)
	            {
	                frontStr = (String) front;
	                backStr = (String) backend;
            		if(orderBy.equalsIgnoreCase("desc")){
            			position = backStr.length() - frontStr.length();
            		}else{
            			position = frontStr.length() - backStr.length();
            		}

	                return position;
	            }
	        }
	    );

        return array;
	}

	/**
	 * 리스트에 담긴 문자목록을 HashCode (아스키) 를 기준으로 sending 방향대로 정렬하여 줍니다.
	 * @param arrays
	 * @param sending
	 * @return
	 */
	public List<?> listHashCodeOrdered(List<?> arrays, String sending){

		if(arrays == null) return null;

		List<?> array = arrays;
		final String orderBy = sending;

        Collections.sort(array, new Comparator<Object>(){
        	int frontStr = 0;
        	int backStr = 0;
	    		int position = 0;

				public int compare(final Object front, final Object backend )
	            {
	            	frontStr = front.toString().hashCode();
	            	backStr = backend.toString().hashCode();
            		if(orderBy.equalsIgnoreCase("desc")){
            			position = backStr - frontStr;
            		}else{
            			position = frontStr - backStr;
            		}

	                return position;
	            }
	        }
	    );

        return array;
	}
	
    /**
     * 배열에 담긴내용이 모두 NotEmpty 이면 true 아니면 false 를 리턴하여 줍니다.
     * @param arrayStr
     * @return
     */
	public static boolean isNotEmptyStringArray(String... arrayString) {
		
		boolean out = false;
		String[] strAry = arrayString;
		/*
		if(logger.isDebugEnabled()) {
			logger.debug(CommonUtil.addString("*- isNotEmptyStringArray : " , strAry));
		}
		*/
		if(strAry != null && strAry.length > 0) {
			out = true;
			
			for(String str : strAry){
				if(StringUtils.isEmpty(str)) {
					out = false;
					break;
				}
			}
		}
		return out;
	}

	public static boolean isNotEmpty(String str) {
		String sentence = str; 
		return StringUtils.isNotEmpty(sentence);
	}

	public static boolean isEmpty(String str) {
		String sentence = str; 
		return StringUtils.isEmpty(sentence);
	}
	
	public boolean strBoolean(String value){
		boolean out = false;
		String str = (value != null ? value.toUpperCase() : "");
		if(str.equals("Y") || str.equals("YES") || 
		   str.equals("T") || str.equals("TRUE") || str.equals("1")) {
			out = true;
		}
		return out;
	}
	
	public String escapeJson(String string) {
        if (string == null || string.length() == 0) {
            return "";
        }

        char	c = 0;	
        int	i;
        int	len = string.length();
        StringBuilder sb = new StringBuilder(len);
        String	t;
        
        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
	            case '\\': 
	            case '"':
	                sb.append('\\');
	                sb.append(c);
	                break;
	            case '/':
	            	sb.append('\\');
	                sb.append(c);
	                break;
	            case '\b':
	                sb.append("\\b");
	                break;
	            case '\t':
	                sb.append("\\t");
	                break;
	            case '\n':
	                sb.append("\\n");
	                break;
	            case '\f':
	                sb.append("\\f");
	                break;
	            case '\r':
	               sb.append("\\r");
	               break;
	            default:
	                if (c < ' ') {
	                    t = "000" + Integer.toHexString(c);
	                    sb.append("\\u" + t.substring(t.length() - 4));
	                } else {
	                    sb.append(c);
	                }
            }
        }
        
        return sb.toString();
    }
	
    // Padding
    //-----------------------------------------------------------------------
    /**
     * <p>
     * Repeat a String <code>repeat</code> times to form a new String.
     * </p>
     *
     * <pre>
     *
     *  StringUtils.repeat(null, 2) = null
     *  StringUtils.repeat(&quot;&quot;, 0)   = &quot;&quot;
     *  StringUtils.repeat(&quot;&quot;, 2)   = &quot;&quot;
     *  StringUtils.repeat(&quot;a&quot;, 3)  = &quot;aaa&quot;
     *  StringUtils.repeat(&quot;ab&quot;, 2) = &quot;abab&quot;
     *  StringUtils.repeat(&quot;a&quot;, -2) = &quot;&quot;
     *
     * </pre>
     *
     * @param str
     *            the String to repeat, may be null
     * @param repeat
     *            number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated,
     *         <code>null</code> if null String input
     */
    public String repeat(String str, int repeat) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY;
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return padding(repeat, str.charAt(0));
        }

        int outputLength = inputLength * repeat;
        switch (inputLength) {
        case 1:
            char ch = str.charAt(0);
            char[] output1 = new char[outputLength];
            for (int i = repeat - 1; i >= 0; i--) {
                output1[i] = ch;
            }
            return new String(output1);
        case 2:
            char ch0 = str.charAt(0);
            char ch1 = str.charAt(1);
            char[] output2 = new char[outputLength];
            for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                output2[i] = ch0;
                output2[i + 1] = ch1;
            }
            return new String(output2);
        default:
            StringBuffer buf = new StringBuffer(outputLength);
            for (int i = 0; i < repeat; i++) {
                buf.append(str);
            }
            return buf.toString();
        }
    }

    /**
     * <p>
     * Returns padding using the specified delimiter repeated to a given length.
     * </p>
     *
     * <pre>
     *
     *  StringUtils.padding(0, 'e')  = &quot;&quot;
     *  StringUtils.padding(3, 'e')  = &quot;eee&quot;
     *  StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     *
     * </pre>
     *
     * @param repeat
     *            number of times to repeat delim
     * @param padChar
     *            character to repeat
     * @return String with repeated character
     * @throws IndexOutOfBoundsException
     *             if <code>repeat &lt; 0</code>
     */
    private static String padding(int repeat, char padChar) {
        // be careful of synchronization in this method
        // we are assuming that get and set from an array index is atomic
        String pad = PADDING[padChar];
        if (pad == null) {
            pad = String.valueOf(padChar);
        }
        while (pad.length() < repeat) {
            pad = pad.concat(pad);
        }
        PADDING[padChar] = pad;
        return pad.substring(0, repeat);
    }

    /**
     * <p>
     * Right pad a String with spaces (' ').
     * </p>
     *
     * <p>
     * The String is padded to the size of <code>size</code>.
     * </p>
     *
     * <pre>
     *
     *  StringUtils.rightPad(null, *)   = null
     *  StringUtils.rightPad(&quot;&quot;, 3)     = &quot;   &quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 3)  = &quot;bat&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 5)  = &quot;bat  &quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 1)  = &quot;bat&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, -1) = &quot;bat&quot;
     *
     * </pre>
     *
     * @param str
     *            the String to pad out, may be null
     * @param size
     *            the size to pad to
     * @return right padded String or original String if no padding is
     *         necessary, <code>null</code> if null String input
     */
    public String rightPad(String str, int size) {
        return rightPad(str, size, ' ');
    }

    /**
     * <p>
     * Right pad a String with a specified character.
     * </p>
     *
     * <p>
     * The String is padded to the size of <code>size</code>.
     * </p>
     *
     * <pre>
     *
     *  StringUtils.rightPad(null, *, *)     = null
     *  StringUtils.rightPad(&quot;&quot;, 3, 'z')     = &quot;zzz&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 3, 'z')  = &quot;bat&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 5, 'z')  = &quot;batzz&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 1, 'z')  = &quot;bat&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, -1, 'z') = &quot;bat&quot;
     *
     * </pre>
     *
     * @param str
     *            the String to pad out, may be null
     * @param size
     *            the size to pad to
     * @param padChar
     *            the character to pad with
     * @return right padded String or original String if no padding is
     *         necessary, <code>null</code> if null String input
     * @since 2.0
     */
    public String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(padding(pads, padChar));
    }

    /**
     * <p>
     * Right pad a String with a specified String.
     * </p>
     *
     * <p>
     * The String is padded to the size of <code>size</code>.
     * </p>
     *
     * <pre>
     *
     *  StringUtils.rightPad(null, *, *)      = null
     *  StringUtils.rightPad(&quot;&quot;, 3, &quot;z&quot;)      = &quot;zzz&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 3, &quot;yz&quot;)  = &quot;bat&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 5, &quot;yz&quot;)  = &quot;batyz&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 8, &quot;yz&quot;)  = &quot;batyzyzy&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 1, &quot;yz&quot;)  = &quot;bat&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, -1, &quot;yz&quot;) = &quot;bat&quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 5, null)  = &quot;bat  &quot;
     *  StringUtils.rightPad(&quot;bat&quot;, 5, &quot;&quot;)    = &quot;bat  &quot;
     *
     * </pre>
     *
     * @param str
     *            the String to pad out, may be null
     * @param size
     *            the size to pad to
     * @param padStr
     *            the String to pad with, null or empty treated as single space
     * @return right padded String or original String if no padding is
     *         necessary, <code>null</code> if null String input
     */
    public String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (padStr == null || padStr.length() == 0) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * <p>
     * Left pad a String with spaces (' ').
     * </p>
     *
     * <p>
     * The String is padded to the size of <code>size<code>.</p>
     *
     * <pre>
     *
     *  StringUtils.leftPad(null, *)   = null
     *  StringUtils.leftPad(&quot;&quot;, 3)     = &quot;   &quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 3)  = &quot;bat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 5)  = &quot;  bat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 1)  = &quot;bat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, -1) = &quot;bat&quot;
     *
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @return left padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     */
    public String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * <p>
     * Left pad a String with a specified character.
     * </p>
     *
     * <p>
     * Pad to a size of <code>size</code>.
     * </p>
     *
     * <pre>
     *
     *  StringUtils.leftPad(null, *, *)     = null
     *  StringUtils.leftPad(&quot;&quot;, 3, 'z')     = &quot;zzz&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 3, 'z')  = &quot;bat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 5, 'z')  = &quot;zzbat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 1, 'z')  = &quot;bat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, -1, 'z') = &quot;bat&quot;
     *
     * </pre>
     *
     * @param str
     *            the String to pad out, may be null
     * @param size
     *            the size to pad to
     * @param padChar
     *            the character to pad with
     * @return left padded String or original String if no padding is necessary,
     *         <code>null</code> if null String input
     * @since 2.0
     */
    public String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return padding(pads, padChar).concat(str);
    }

    /**
     * <p>
     * Left pad a String with a specified String.
     * </p>
     *
     * <p>
     * Pad to a size of <code>size</code>.
     * </p>
     *
     * <pre>
     *
     *  StringUtils.leftPad(null, *, *)      = null
     *  StringUtils.leftPad(&quot;&quot;, 3, &quot;z&quot;)      = &quot;zzz&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 3, &quot;yz&quot;)  = &quot;bat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 5, &quot;yz&quot;)  = &quot;yzbat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 8, &quot;yz&quot;)  = &quot;yzyzybat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 1, &quot;yz&quot;)  = &quot;bat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, -1, &quot;yz&quot;) = &quot;bat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 5, null)  = &quot;  bat&quot;
     *  StringUtils.leftPad(&quot;bat&quot;, 5, &quot;&quot;)    = &quot;  bat&quot;
     *
     * </pre>
     *
     * @param str
     *            the String to pad out, may be null
     * @param size
     *            the size to pad to
     * @param padStr
     *            the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary,
     *         <code>null</code> if null String input
     */
    public String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (padStr == null || padStr.length() == 0) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    // Centering
    //-----------------------------------------------------------------------
    /**
     * <p>
     * Centers a String in a larger String of size <code>size</code> using the
     * space character (' ').
     * <p>
     *
     * <p>
     * If the size is less than the String length, the String is returned. A
     * <code>null</code> String returns <code>null</code>. A negative size
     * is treated as zero.
     * </p>
     *
     * <p>
     * Equivalent to <code>center(str, size, " ")</code>.
     * </p>
     *
     * <pre>
     *
     *  StringUtils.center(null, *)   = null
     *  StringUtils.center(&quot;&quot;, 4)     = &quot;    &quot;
     *  StringUtils.center(&quot;ab&quot;, -1)  = &quot;ab&quot;
     *  StringUtils.center(&quot;ab&quot;, 4)   = &quot; ab &quot;
     *  StringUtils.center(&quot;abcd&quot;, 2) = &quot;abcd&quot;
     *  StringUtils.center(&quot;a&quot;, 4)    = &quot; a  &quot;
     *
     * </pre>
     *
     * @param str
     *            the String to center, may be null
     * @param size
     *            the int size of new String, negative treated as zero
     * @return centered String, <code>null</code> if null String input
     */
    public String center(String str, int size) {
        return center(str, size, ' ');
    }

    /**
     * <p>
     * Centers a String in a larger String of size <code>size</code>. Uses a
     * supplied character as the value to pad the String with.
     * </p>
     *
     * <p>
     * If the size is less than the String length, the String is returned. A
     * <code>null</code> String returns <code>null</code>. A negative size
     * is treated as zero.
     * </p>
     *
     * <pre>
     *
     *  StringUtils.center(null, *, *)     = null
     *  StringUtils.center(&quot;&quot;, 4, ' ')     = &quot;    &quot;
     *  StringUtils.center(&quot;ab&quot;, -1, ' ')  = &quot;ab&quot;
     *  StringUtils.center(&quot;ab&quot;, 4, ' ')   = &quot; ab&quot;
     *  StringUtils.center(&quot;abcd&quot;, 2, ' ') = &quot;abcd&quot;
     *  StringUtils.center(&quot;a&quot;, 4, ' ')    = &quot; a  &quot;
     *  StringUtils.center(&quot;a&quot;, 4, 'y')    = &quot;yayy&quot;
     *
     * </pre>
     *
     * @param str
     *            the String to center, may be null
     * @param size
     *            the int size of new String, negative treated as zero
     * @param padChar
     *            the character to pad the new String with
     * @return centered String, <code>null</code> if null String input
     * @since 2.0
     */
    public String center(String str, int size, char padChar) {
        if (str == null || size <= 0) {
            return str;
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }

    /**
     * <p>
     * Centers a String in a larger String of size <code>size</code>. Uses a
     * supplied String as the value to pad the String with.
     * </p>
     *
     * <p>
     * If the size is less than the String length, the String is returned. A
     * <code>null</code> String returns <code>null</code>. A negative size
     * is treated as zero.
     * </p>
     *
     * <pre>
     *
     *  StringUtils.center(null, *, *)     = null
     *  StringUtils.center(&quot;&quot;, 4, &quot; &quot;)     = &quot;    &quot;
     *  StringUtils.center(&quot;ab&quot;, -1, &quot; &quot;)  = &quot;ab&quot;
     *  StringUtils.center(&quot;ab&quot;, 4, &quot; &quot;)   = &quot; ab&quot;
     *  StringUtils.center(&quot;abcd&quot;, 2, &quot; &quot;) = &quot;abcd&quot;
     *  StringUtils.center(&quot;a&quot;, 4, &quot; &quot;)    = &quot; a  &quot;
     *  StringUtils.center(&quot;a&quot;, 4, &quot;yz&quot;)   = &quot;yayz&quot;
     *  StringUtils.center(&quot;abc&quot;, 7, null) = &quot;  abc  &quot;
     *  StringUtils.center(&quot;abc&quot;, 7, &quot;&quot;)   = &quot;  abc  &quot;
     *
     * </pre>
     *
     * @param str
     *            the String to center, may be null
     * @param size
     *            the int size of new String, negative treated as zero
     * @param padStr
     *            the String to pad the new String with, must not be null or
     *            empty
     * @return centered String, <code>null</code> if null String input
     * @throws IllegalArgumentException
     *             if padStr is <code>null</code> or empty
     */
    public String center(String str, int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (padStr == null || padStr.length() == 0) {
            padStr = " ";
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }
    
    public String bytesToNewString(byte[] bytes){
    	return bytesToNewString(bytes, null);
    }
    
    public String bytesToNewString(byte[] bytes, String encoding){
    	String out = null;
    	if(bytes != null) {
    		if(encoding != null) {
    			try {
					out = new String(bytes, encoding);
				} catch (UnsupportedEncodingException e) {
					throw new ApplicationException("Method : byteToString", e);
				}
    		}
    		else {
    			out = new String(bytes);
    		}
    	}
    	return out;
    }

    
    public String byteArrayToBinaryString(byte[] b){
        StringBuilder sb=new StringBuilder();
        for(int i=0; i<b.length; ++i){
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    public String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    public byte[] binaryStringToByteArray(String s){
        int count=s.length()/8;
        byte[] b=new byte[count];
        for(int i=1; i<count; ++i){
            String t=s.substring((i-1)*8, i*8);
            b[i-1]=binaryStringToByte(t);
        }
        return b;
    }

	public byte binaryStringToByte(String s) {
		byte ret = 0, total = 0;
		for (int i = 0; i < 8; ++i) {
			ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
			total = (byte) (ret | total);
		}
		return total;
	}
    

    
}
