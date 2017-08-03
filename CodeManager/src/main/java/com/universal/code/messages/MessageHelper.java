package com.universal.code.messages;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.utils.CommonUtil;
import com.universal.code.utils.RegexUtil;
import com.universal.code.utils.ResourceUtil;
import com.universal.code.utils.thread.Local;

@Component
public class MessageHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageHelper.class);
	
	public static final String NO_MESSAGE = "There is no message in the message code";
	
	private static final String MESSAGE_ARRAY_FORMAT_PATTERN = "(\\{[0-9]?+\\})";
	
	@Autowired
	private ResourceUtil autoResource;
	
	private static ResourceUtil resource;
	
	@PostConstruct
	public void setResourceUtil() {
		MessageHelper.resource = autoResource;
	}
	
	@Autowired
	private RegexUtil autoRegexUtil;
	
	private static RegexUtil regex;
	
	@PostConstruct
	public void setRegexUtil() {
		MessageHelper.regex = autoRegexUtil;
	}
	
	public static String messageWithPropertiesPath(String propertiesPathParam, String messageCodeParam){
		return resourceMessage(propertiesPathParam, null, messageCodeParam, new Object[0]);
	}
	
	public static String messageWithPropertiesPath(String propertiesPathParam, String messageCodeParam, Object... bindMessagesParam){
		return resourceMessage(propertiesPathParam, null, messageCodeParam, bindMessagesParam);
	}
	
	public static String messageWithLocale(String localeParam, String messageCodeParam){
		return resourceMessage(null, localeParam, messageCodeParam, new Object[0]);
	}
	
	public static String messageWithLocale(String localeParam, String messageCodeParam, Object... bindMessagesParam){
		return resourceMessage(null, localeParam, messageCodeParam, bindMessagesParam);
	}
	
	public static String message(String messageCodeParam){
		//logger.debug("#Local.searchParam() : {}", Local.searchParam());
		//logger.debug("#Local.searchParam().getLocale() : {}", Local.searchParam().getLocale());
		//logger.debug("#messageCodeParam : {}", messageCodeParam);
		return resourceMessage(null, Local.searchParam().getLocale(), messageCodeParam, new Object[0]);
	}
	
	public static String message(String messageCodeParam, Object... bindMessagesParam){
		return resourceMessage(null, Local.searchParam().getLocale(), messageCodeParam, bindMessagesParam);
	}

	public static String resourceMessage(String propertiesPathParam, String localeParam, String messageCodeParam, Object... bindMessagesParam){
		/*
		if(logger.isDebugEnabled()) {
			logger.debug("[Start] Properties messageCode : " + messageCode + ", userLocale : " + userLocale);
		}
		*/
		if(MessageHelper.resource == null) {
			MessageHelper.resource = new ResourceUtil();
		}
		if(MessageHelper.regex == null) {
			MessageHelper.regex = new RegexUtil();
		}
		
		String propertiesPath = propertiesPathParam; 
		String userLocale = localeParam;
		String messageCode = messageCodeParam;
		Object[] bindMessages = bindMessagesParam;
		
		String out = resource.message(propertiesPath, userLocale, messageCode);

		String arrayMessage = null;
		if(bindMessages != null) {
			for(int i = 0; i < bindMessages.length; i++) {
				arrayMessage = resource.message(propertiesPath, userLocale, bindMessages[i].toString());
				if(arrayMessage != null) {
					bindMessages[i] = arrayMessage;
				}
			}
		}
		
		if(out == null) {
			out = NO_MESSAGE + " [" + messageCode + "]";
		}
		else if(bindMessages != null && bindMessages.length > 0) {
			// 메시지 out 내에  {0} {1} {2} 과 같은 패턴이 존재하면 bindMessages 에 있는 내용을 적용
			out = CommonUtil.formatMessage(out, bindMessages);
			/*
			Object formatData = null;
			List<String> bindFormats = regex.findPatternToList(out, MESSAGE_ARRAY_FORMAT_PATTERN);
			for(int i = 0; i < bindFormats.size(); i++) {
				if(bindMessages.length > i) {
					formatData = bindMessages[i];
				}
				else {
					formatData = "";
				}
				out = out.replace(bindFormats.get(i), (CharSequence) formatData);
			}
			*/
		}
		
		/*
		if(logger.isDebugEnabled()) {
			logger.debug(" - userLocale : " + userLocale + ", messageCode : " + messageCode + ", message : " + out + ", propertiesPath : " + propertiesPath);
		}
		*/
		return out.trim();
	}
}
