package com.universal.code.utils;

import java.lang.reflect.Array;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;

@Component
public class CommonUtil {

	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	
	@Autowired
	private TypeUtil typeUtil;
	
	@Autowired
	private RegexUtil regex;
	
	private ConvertUtilsBean coverter = BeanUtilsBean.getInstance().getConvertUtils();
	
	private static final String DELIM_STR = "{}";

	private static final String DELIM_IN_NUMBER = "(\\{[0-9]?+\\})"; 
	
	/**
	 * 개발용유틸 : Map 데이터를 sysout 하여줌
	 * @param map
	 */
	public static void outMap(Map<?, ?> map) {
		
		Map<?, ?> maps = map;
		if(logger.isDebugEnabled()) {
			for(Entry<?, ?> ent : maps.entrySet()) {
				logger.debug(ent.getKey() + " : " + ent.getValue());
			}
		}
    }
	
	/**
	 * 바인드된 클래스가 beanutils 의 ConvertUtils 가 converting 가능한 primitive reference type 인지 체크하여줌
	 * @param clazz
	 * @return 
	 */
    public boolean isConvertibleRequest(Class<?> clazz) {
    	
    	if(clazz == null || clazz.isAssignableFrom(Class.class)) {
    		return false;
    	}
    	
    	return isConvertibleReference(clazz);
    }
    
    
    public boolean isConvertibleReference(Class<?> clazz) {
    	return (coverter.lookup(clazz) != null);
    }
    
    
    public String printHttpServletHeader(HttpServletRequest request, HttpServletResponse response, boolean logging) {
    	String out = null;
    	if(request == null) {
    		return out;
    	}
    	
		@SuppressWarnings("unchecked")
		Enumeration<String> headerNames = request.getHeaderNames();
		if(headerNames == null) {
    		return out;
    	}
		
		String headerName = null;
		StringBuilder headerBuilder = new StringBuilder();
		while(headerNames.hasMoreElements()) {
			headerName = headerNames.nextElement();
			headerBuilder.append("- Http Header ");
			headerBuilder.append(headerName.concat(" : ").concat(request.getHeader(headerName)));
			headerBuilder.append(SystemUtil.LINE_SEPARATOR);
		}
		
		headerBuilder.append("- Request Character Encoding : ");
		headerBuilder.append(request.getCharacterEncoding());
		headerBuilder.append(SystemUtil.LINE_SEPARATOR);

		headerBuilder.append("- Response Character Encoding : ");
		headerBuilder.append(response.getCharacterEncoding());
		headerBuilder.append(SystemUtil.LINE_SEPARATOR);
		
		out = headerBuilder.toString();
		
		if(logging && logger.isDebugEnabled()) {
			logger.debug(SystemUtil.LINE_SEPARATOR.concat(out));
		}
		
		return out;
    }
    
 	public String getDataBaseType4j(String dataBaseType) {
 		if( typeUtil == null ) typeUtil = new TypeUtil(); //code gen temp
 		return typeUtil.getDataBaseType4j(dataBaseType);
 	}
    
 	public Object getDataBaseDefault4j(String dataBaseType) {
 		if( typeUtil == null ) typeUtil = new TypeUtil(); //code gen temp
 		return typeUtil.getDataBaseDefault4j(dataBaseType);
 	}
 	
	/**
	 * 주어진 List 의 중복값을 제거하여 줍니다. 
	 * List 의 순서를 유지합니다.
	 * @param items
	 * @return
	 */
	public List<String> getDeduplicationList(List<String> items) {

		List<String> uniqueItems = new ArrayList<String>();
		boolean duplication = false;
		for(String item : items) {
			duplication = false;
			for(String uniqueStr : uniqueItems) {
				if(uniqueStr.equals(item)) {
					duplication = true;
					break;
				}
			}
			if(!duplication) {
				uniqueItems.add(item);
			}
		}
		
		return uniqueItems;
	}
	
	
    public static String addString(Object... arrays) {
    	
    	StringBuilder out = new StringBuilder();
    	Object[] merges = arrays;
    	if( merges != null ) {
	    	for(Object sentence : merges) {
	    		out.append(sentence);
	    	}	
    	}
    	
    	return out.toString();
    }
    
	public URL[] mergeURLObjectArrays(URL[] rootObject, URL[] newObject) {

		URL[] outUrl = null; 
		
		List<URL> holder = new ArrayList<URL>();

		Collections.addAll(holder, rootObject);
		Collections.addAll(holder, newObject);

		outUrl = holder.toArray(new URL[holder.size()]);
		
		return outUrl;
	}

	public List<Object> arrayToList(Object... arrays) {
		return Arrays.asList(arrays);
	}
	
	public boolean isArrayInObject(Object[] arrays, Object[] compares) {
		List<?> lists = arrayToList(arrays);
		for(Object compare : compares) {
			if(lists.indexOf(compare) > -1) {
				return true;
			}
		}
		return false;
	}
	
	public String getClientAddress(HttpServletRequest request) {
		
		String clientAddress  = request.getHeader("X-FORWARDED-FOR"); 
		if(clientAddress == null) 
		{ 
			clientAddress = request.getRemoteAddr(); 
		} 
		
		return clientAddress;
	}
	
	public static double runtime(long runTime) {
		double runtime = (System.currentTimeMillis() - (runTime)) / 1000.0;
		if(logger.isDebugEnabled()) {
			logger.debug(" -# runtime : " + runtime + " sec");
		}
		return runtime;
	}

	
	public static Integer getRandomNumber(Integer maxValue, Integer minValue) {
		Random random = new Random();
	
		return (random.nextInt(maxValue) + minValue);
	}

	public static String getRandomUpperAlphabat() {
		// 대문자 A-Z 랜덤 알파벳 생성
	    return String.valueOf((char) ((Math.random() * 26) + 65));
	}
	
	public static String getRandomLowerAlphabat() {
	    // 소문자 a-z 랜덤 알파벳 생성
	    return String.valueOf((char) ((Math.random() * 26) + 97));
	}

	public static boolean isNotEmptys(Object... objects) {
		
		boolean out = false;
		Object[] objectAry = objects;
		if(objectAry != null && objectAry.length > 0) {
			out = true;
			
			for(Object object : objectAry) {
				if(object == null) {
					out = false;
					break;
				}
			}
		}
		return out;
	}
	
	public static boolean isEmptys(Object... objects) {
		
		boolean out = true;
		Object[] objectAry = objects;
		if(objectAry != null && objectAry.length > 0) {
			
			for(Object object : objectAry) {
				if(object != null) {
					out = false;
					break;
				}
			}
		}
		return out;
	}
	
	public static boolean booleanValue(Boolean value) {
		if(value == null) {
			return false;
		}
		return value;
	}
	
	public static Integer integerValue(Integer value) {
		if(value== null) {
			return 0;
		}
		return value;
	}
	
	public Object[] convertToObjectArray(Object arrays) {
	    Class<?> ofArray = arrays.getClass().getComponentType();
	    if (ofArray.isPrimitive()) {
	        List<Object> array = new ArrayList<Object>();
	        for (int i = 0; i < Array.getLength(arrays); i++) {
	        	array.add(Array.get(arrays, i));
	        }
	        return array.toArray();
	    }
	    else {
	        return (Object[]) arrays;
	    }
	}
	
	public static StackTraceElement getCurrentStack() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        int point = 0;
        for(StackTraceElement stack : stacktrace) {
        	if(CommonUtil.class.getCanonicalName().equals(stack.getClassName())) {
    			return stacktrace[point+1];
    		}
        	point++;
        }
        
        return null;
	}
	
	public static String getCurrentClassMethod() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        int point = 0;
        for(StackTraceElement stack : stacktrace) {
        	if(CommonUtil.class.getCanonicalName().equals(stack.getClassName())) {
        		StackTraceElement current = stacktrace[point+1];
        		String className = current.getClassName();
        		if(className.indexOf(IOperateCode.STR_DOT) > -1) {
        			className = className.substring(className.lastIndexOf(IOperateCode.STR_DOT) + 1);
        		}
    			return className.concat(IOperateCode.STR_DOT).concat(current.getMethodName()).concat(IOperateCode.STR_COLON).concat(Integer.toString(current.getLineNumber()));
    		}
        	point++;
        }
        
        return null;
	}
	
    public static String formatMessage(String message, Object... arguments) {
    	
    	String out = null;
    	String pattern = message;
    	Object[] argument = arguments;
    	if(pattern != null) {

    		try {
		    	if(pattern.indexOf(DELIM_STR) > -1) {
		    		out = MessageFormatter.arrayFormat(pattern, argument).getMessage().replaceAll(DELIM_IN_NUMBER, IOperateCode.STR_BLANK);
		    	}
		    	else {
		    		out = MessageFormat.format(pattern, argument);
		    	}
    		}
    		catch(IllegalArgumentException e){
    			throw new ApplicationException("Invalid message pattern : {}", new Object[]{pattern}, e);
    		}
    	}
        return out;
    }

	
    
}
