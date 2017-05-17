package com.universal.code.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;

@Component
public class ResourceUtil implements IOperateCode {

	private static final Logger logger = LoggerFactory.getLogger(ResourceUtil.class);

	@Autowired
	private ResourceLoader resourceLoader;

	private final String RESOURCE_PATH = "WEB-INF/messages/messages*";
	
	private final String RESOURCE_EXT = ".properties";
	
	private static Map<String, Map<String, Object>> resourceMap = null; 
	
	@Autowired
	private FileUtil fileUtil;
	
	private static void initResourceMap(){
		if(resourceMap == null) {
			resourceMap = new HashMap<String, Map<String, Object>>();
		}
	}
	
	private static void setResourceMap(String key, Map<String, Object> value){
		initResourceMap();
		resourceMap.put(key, value);
	}
	
	private static Map<String, Object> getResourceMap(String key){
		initResourceMap();
		return resourceMap.get(key);
	}
	
	public void out(Properties properties){
		if(logger.isDebugEnabled()) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
        	StackTraceElement beforeStack = stacks[ 1 ];    
        	
        	logger.debug("[Properties Infomation] ".concat(beforeStack.getClassName()).concat("@").concat(beforeStack.getMethodName()).concat(":").concat(Integer.toString(beforeStack.getLineNumber())));
			for(Entry<?, ?> entry : properties.entrySet()){
				logger.debug(entry.getKey() + " : " + entry.getValue());
			}
		}
	}
	
	public String message(String userLocale, String key) {
		return message(null, userLocale, key);
	}

	public String message(String propertiesPath, String userLocale, String key) {
		//if(logger.isDebugEnabled()) {
		//	logger.debug("[Start] ResourceUtil messageCode : " + key + ", userLocale : " + userLocale + ", propertiesPath : " + propertiesPath );
		//}
		
		String out = null;
		String language = null;
		Properties props = null;

		if( StringUtil.isNotEmpty(userLocale) /*&& ( userLocale.indexOf(STR_DOT) > -1 || userLocale.indexOf(STR_UNDERBAR) > -1 )*/ ) {
			language = userLocale;
		}
		else {
			language = SystemUtil.SYSTEM_LOCALE;
		}
		
		Locale locale = null;
		List<String> locales = null;
		List<String> messageFiles = new ArrayList<String>();
		
		String propsKey = null; // multilanguage properties path
		if(propertiesPath != null) {
			propsKey = propertiesPath;
		}
		else {
			propsKey = RESOURCE_PATH;
		}
		
		if(!propsKey.endsWith(RESOURCE_EXT)) propsKey = propsKey.concat(RESOURCE_EXT); // += .properties

		if(propsKey.indexOf(STR_ASTERISK.concat(RESOURCE_EXT)) > -1) {

			locale = LocaleUtil.parseStrict(language, false);
			
			String localeLanguage = locale.getLanguage();
			String localeCountry = locale.getCountry();
			String localeToString = locale.toString();
			String localeVariant = locale.getVariant();
			
			locales = new ArrayList<String>();
			if(StringUtil.isNotEmpty(localeLanguage)) locales.add(localeLanguage.toLowerCase());
			if(StringUtil.isNotEmpty(localeCountry)) locales.add(localeCountry.toLowerCase());
			if(StringUtil.isNotEmpty(localeToString)) locales.add(localeToString);
			if(StringUtil.isNotEmpty(localeVariant)) locales.add(localeVariant.toLowerCase());
			
			for(String localeStr : locales) {
				messageFiles.add(propsKey.replace(STR_ASTERISK, STR_UNDERBAR.concat(localeStr)));
			}
		}
		else {
			messageFiles.add(propsKey);
		}
		
		for(String filePath : messageFiles) {

			props = load(filePath);
			if( props != null ) {
				out = (String) props.get(key);
				break;
			}
		}
		
		return out;
	}
	
	public Properties load(String filePath) {

		String propertyPath = filePath;
		
		if(StringUtil.isEmpty(propertyPath)) {
			throw new ApplicationException(" Property path isEmpty!! ");
		}
		
		String canonicalPath = null;
		
		if( fileUtil != null) {
			canonicalPath = fileUtil.getRealPath(propertyPath);
		}
		else {
			canonicalPath = propertyPath;
		}
		
		/*
		if(logger.isDebugEnabled()) {
			logger.debug(CommonUtil.addString(SystemUtil.LINE_SEPARATOR ,"- propertyPath : ", propertyPath, SystemUtil.LINE_SEPARATOR,"- canonicalPath : ", canonicalPath, SystemUtil.LINE_SEPARATOR));
		}
		*/		
		File resource = new File(canonicalPath);
		if(resource.exists()) {
			return load(resource, true);
		}
		else {
			return null;
		}
		
	}
	
	public Properties load(File propertiesFile) {
		return load(propertiesFile, false);
	}
	
	
	public Properties load(File propertiesFile, boolean isExists) {
		/*
		if(logger.isDebugEnabled()) {
			logger.debug(" [ResourceUtil.load] ");
		}
		*/
		
		Properties out = null;
		boolean returnCache = true;
		String canonicalPath = null;
		Map<String, Object> cache = null;
		File file = propertiesFile;
		
		try {
			canonicalPath = file.getCanonicalPath();
			
			if(!FileUtil.getExt(canonicalPath).equalsIgnoreCase("properties")) {
				throw new ApplicationException("프로퍼티 파일이 아닙니다.");
			}
			
			if(!isExists && !file.exists()) {
				if(logger.isErrorEnabled()) {
					logger.error(" 프로퍼티 파일이 존재하지 않습니다." + canonicalPath);
				}
				return null;
			}
			
			cache = getResourceMap(canonicalPath);
			
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
		//InputStream is = this.getClass().getClassLoader().getResourceAsStream(classPath);
		
		/*
		if(logger.isDebugEnabled()) {
			logger.debug(CommonUtil.addString("- resource cache size : ", resourceMap.size()));
		}
		*/
		
		long resourceLastmodified = -1;
		if(cache != null && cache.get(RESOURCE_LASTMODIFIED_KEY) != null) {
			resourceLastmodified = (Long) cache.get(RESOURCE_LASTMODIFIED_KEY);
		}
		
		//if(logger.isDebugEnabled()) {
		//	logger.debug(CommonUtil.addString(SystemUtil.LINE_SEPARATOR, " file : ", file, SystemUtil.LINE_SEPARATOR, " resourceLastmodified : ", resourceLastmodified));
		//}
		if(cache == null || file.lastModified() != resourceLastmodified) {
			
			BufferedInputStream bis = null;
			
			try {
				returnCache = false;
	        	bis = new BufferedInputStream(new FileInputStream(file));
	        	out = new Properties();
	        	out.load(bis);
				
	        	Map<String, Object> propsValues = new HashMap<String, Object>();
	        	propsValues.put(RESOURCE_LASTMODIFIED_KEY, file.lastModified());
	        	propsValues.put(RESOURCE_PROPERTIES_KEY, out);
	        	
	        	setResourceMap(canonicalPath, propsValues);
	        	
	        	if(logger.isDebugEnabled()) {
	        		logger.debug(CommonUtil.addString("new Loading properties file... ", file.getCanonicalPath(), ", resourceMap size : ", resourceMap.size()));
	        	}
	        	
	        } catch (Exception e) {
	            throw new ApplicationException(e);
	        } finally {
	        	if(bis != null) {
	        		try {
						bis.close();
					} catch (IOException e) {
						throw new ApplicationException(e);
					}
	        	}
	        }
		}
		
		if( returnCache ) {
			
			out = (Properties) cache.get(RESOURCE_PROPERTIES_KEY);
			/*
        	if(logger.isDebugEnabled()) {
        		logger.debug(CommonUtil.addString("Property loaded from the cache... ", canonicalPath));
        	}	
        	*/
		}
		
        return out;
	}

	/**
	 * spring io util  
	 * @param resourceURL : classpath:images/imagefile.bmp or file:///c:/test/file.txt
	 * @return
	 */
	public Resource getResource(String url) {
	    return resourceLoader.getResource(url);
	}
}
