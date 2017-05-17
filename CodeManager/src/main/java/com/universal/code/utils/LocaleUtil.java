package com.universal.code.utils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;

@Component
public class LocaleUtil implements IOperateCode {

	private static final Logger logger = LoggerFactory.getLogger(LocaleUtil.class);

	private static final String DEFAULT_USER_LOCALE = SystemUtil.SYSTEM_LOCALE;
	
	private static Map<String, Locale> availableLocales = null;

	private final static boolean IS_LOCALE_LOG = false; 
	
	public static Locale getSystemLocale() {
		return parseStrict(null, false);
	}
	
	public static Locale parseStrict(String str) {
		return parseStrict(str, false);
	}
	
	public static Locale parseStrict(String str, boolean newLocale) {
		
		Locale out = null;
    	String localeStr = null;
    	String language = null;
        String country = null;
        String variant = null;
        
    	if(StringUtil.isNotEmpty(str)) {
    		localeStr = str;
    	} else {
    		localeStr = DEFAULT_USER_LOCALE;
    	}
    	
        int underBarIndex = localeStr.indexOf(STR_UNDERBAR);
        int dotIndex = localeStr.indexOf(STR_DOT);
    	int underbarLenght = STR_UNDERBAR.length();
    	int dotLenght = STR_DOT.length();
    	
        if(dotIndex > -1 && underBarIndex > dotIndex) {
        	if(logger.isDebugEnabled()) {
        		logger.debug(CommonUtil.addString(new Object[]{" underBarIndex : " , underBarIndex , ", dotIndex : " , dotIndex}) );
        	}
    		throw new RuntimeException(CommonUtil.addString(localeStr , " is Invalid locale pattern. "));
    	}
        else if(underBarIndex == -1 && dotIndex == -1) {
        	language = localeStr;
        	country = "";
        	variant = "";
        }
        else if(underBarIndex > -1 && dotIndex > -1) {
        	language = localeStr.substring(0, underBarIndex);
        	country = localeStr.substring(underBarIndex + underbarLenght, dotIndex);
        	variant = localeStr.substring(dotIndex + dotLenght);
        }
        else if(underBarIndex > -1) {
        	language = localeStr.substring(0, underBarIndex);
        	country = localeStr.substring(underBarIndex + underbarLenght);
        	variant = "";
        }
        else if(dotIndex > -1) {
        	language = localeStr.substring(0, dotIndex);
        	country = "";
        	variant = localeStr.substring(dotIndex + dotLenght);
        }
        
        if(logger.isDebugEnabled() && IS_LOCALE_LOG) {
        	logger.debug(CommonUtil.addString("language : " , language , ", country : " , country , ", variant : " , variant));
        }
       	
        if( newLocale ) {
        
        	if(getAvailableLocale(language) != null) {
            	out = new Locale(language, country, variant);
            }
        }
        else {
        	
        	if(StringUtil.isNotEmpty(language)) {
            	if(StringUtil.isNotEmpty(country)) {
            		language = language.concat(STR_UNDERBAR).concat(country);
            	}
            	
            	out = getAvailableLocale(language);
            }
        }
        
        if(out == null) {
        	throw new RuntimeException(CommonUtil.addString("사용자 언어가 잘못되거나 지원되지 않는 언어입니다. locale : ", localeStr));
        }
        
		return out;
	}
	
	/**
	 * ex) localeStr : ko_KR.eucKR
	 * @param localeStr
	 * @return
	 */
    public static Locale parse(String str) {
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug(CommonUtil.addString("[START] LOCALE PARSE "));
    	}
    	
    	Locale out = null;
    	String localeStr = null;
    	String language = null;
        String country = null;
        String variant = null;
        
    	if(StringUtil.isNotEmpty(str)) {
    		localeStr = str;
    	} else {
    		localeStr = DEFAULT_USER_LOCALE;
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug(CommonUtil.addString("localeStr : " , localeStr));
    	}
    	
        int localeLength = localeStr.length();
        int j = 0;
        int k = 0;
        
        boolean underBar = false;
        boolean dot = false;
        for(k = 0; k < localeLength; k++) {
        	
        	if(localeStr.charAt(k) == '_') underBar = true;
        	if(localeStr.charAt(k) == '.') dot = true;
        	
        	if(underBar || dot) {
        		language = localeStr.substring(0, k); 

        		if(underBar) {
                    j = ++k;
                    break;        			 
        		}
        		 
        		if(dot) {
        			out = new Locale(language, "", localeStr.substring(++k));
        			break;
        		}
        	}
        }

        if(out == null && language == null) {
        	out = new Locale(localeStr);
        }

        if(out == null) {
        	for(; k < localeLength; k++) {
            	
                if(localeStr.charAt(k) != '.') {
                	continue;
                }
                country = localeStr.substring(j, k);
                j = ++k;
                break;
            }
        }
        
        if(out == null) {
        	
        	if(country == null) {
        		country = localeStr.substring(j);
            	out = new Locale(language, country);
            } else {
            	variant = localeStr.substring(j);
            	out = new Locale(language, country, variant);
            }
        }
        
        return out;
    }
    
    
    /**
     * 캐쉬 해놓은 Available Locales Map 을 리턴하거나 캐쉬 정보가 없으면 캐쉬하여 데이터 를 리턴합니다.
     * @return
     */
    public static Map<String, Locale> getLocaleMap(){
    	
    	if(availableLocales != null) {
    		if(logger.isDebugEnabled()) {
    			if(IS_LOCALE_LOG) {
    				logger.debug(CommonUtil.addString("Available Locale Data load to cashed ... "));
    			}
    		}
    		return availableLocales;
    	}
    	else {
    		if(logger.isDebugEnabled()) {
    			logger.debug(CommonUtil.addString("Available Locale Data load to execute saveAvailableLocales... "));
    		}
   			return saveAvailableLocales();
    	}
    }
    
    /**
     * 지원 가능한 언어인지 여부를 JAVA의 Available Locales 에서 확인하여 줍니다.
     * @param language
     * @return
     */
    public static Locale getAvailableLocale(String language){

    	Locale out = getLocaleMap().get(language);
    	
    	return out;
    }
    
    /**
     * Java 에서 지원하는 모든 Locale 정보를 List<Locale> 로 리턴하여 줍니다.
     * @param sorting
     * @return
     */
    public List<Locale> getLocaleList() {
    	return getLocaleList(false, false);
    }
    
    /**
     * Java 에서 지원하는 모든 Locale 정보를 코드별로 정렬하여 List<Locale> 로 리턴하여 줍니다.
     * @param sorting : true/false
     * @return
     */
    public List<Locale> getLocaleList(boolean sorting) {
    	return getLocaleList(sorting, false);
    }
    
    /**
     * Java 에서 지원하는 모든 Locale 정보를 코드별로 정렬하여 List<Locale> 로 리턴하여 줍니다.
     * logging 여부에 따라 sysout 합니다.
     * @param sorting : true/false
     * @param logging : true/false
     * @return
     */
    public List<Locale> getLocaleList(boolean sorting, boolean logging) {
    	
    	List<Locale> out = new ArrayList<Locale>();
    	Locale[] list = DateFormat.getAvailableLocales();
    	
    	if(sorting) {
    		
    		List<String> aryLocale = new ArrayList<String>();
    		for (int i = 0; i < list.length; i++) {
        		aryLocale.add(list[i].toString());
    		}
    		
        	Collections.sort(aryLocale);
        	Locale locale = null;
        	String[] arrData = null;
        
        	for (String data : aryLocale) {
        		
        		if (data.indexOf(STR_UNDERBAR) > -1) {
        			arrData = data.split(STR_UNDERBAR);
        			locale = new Locale(arrData[0], arrData[1]);
        		} else {
        			locale = new Locale(data);
        		}
        		out.add(locale);
        	}
        	
    	}
    	else {
    		for (int i = 0; i < list.length; i++) {
    			out.add(list[i]);
    		}
    	}
    	
    	if(logging) {
    		
    		StringBuffer logSbf = new StringBuffer();
    		
    		logSbf.append(SystemUtil.LINE_SEPARATOR);
    		for(Locale locale : out){
        		
        		logSbf.append(CommonUtil.addString(
					"toString : " , locale.toString() , 
					", ", "getLanguage : " , locale.getLanguage() , 
					", ", "getCountry : " , locale.getCountry() , 
					", ", "getDisplayLanguage : " , locale.getDisplayLanguage() , 
					", ", "getDisplayCountry : " , locale.getDisplayCountry() , 
					", ", "getISO3Language : " , locale.getISO3Language() , 
					", ", "getISO3Country : " , locale.getISO3Country() , 
					", ", "getDisplayName : " , locale.getDisplayName() , 
					", ", "getVariant : " , locale.getVariant()
        		));
        		logSbf.append(SystemUtil.LINE_SEPARATOR);
        	}
        	
    		if(logger.isDebugEnabled()) {
    			logger.debug(CommonUtil.addString(logSbf.toString()));
    		}
    	}
    	
    	return out;
    }
    	
    
    /**
     * availableLocales 이 null 일경우 Java 에서 지원하는 모든 Locale 정보를 availableLocales (static map) 에 담아줍니다. 
     */
    public static Map<String, Locale> saveAvailableLocales() {
    	
    	try {
    		availableLocales = new HashMap<String, Locale>();

        	Locale[] list = DateFormat.getAvailableLocales();

    		for (int i = 0; i < list.length; i++) {
    			availableLocales.put(list[i].toString(), list[i]);
    			//if(logger.isDebugEnabled()) {
    			//	logger.debug("support locale : {}", list[i]);
    			//}
    		}	
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return availableLocales;
    }
    
    
    
}
