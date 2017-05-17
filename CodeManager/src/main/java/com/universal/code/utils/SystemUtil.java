package com.universal.code.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.dto.CommonHeader;
import com.universal.code.dto.RuntimeHeader;
import com.universal.code.exception.ValidateException;
import com.universal.code.utils.thread.Local;

@Component
public class SystemUtil implements IOperateCode {

	private static Logger logger = LoggerFactory.getLogger(SystemUtil.class);
	
	private static StackTraceUtil stackTraceUtil = new StackTraceUtil();
	
	public final static String LINE_SEPARATOR = getProperty("line.separator");
	
	public final static String FILE_SEPARATOR = getProperty("file.separator");
	
	public final static String PATH_SEPARATOR = getProperty("path.separator");
	
	public final static String OS_NAME = getProperty("os.name");
	
	public final static String JAVA_VM_VERSION = getProperty("java.vm.version");
	
	public final static String JAVA_CLASS_VERSION = getProperty("java.class.version");
	
	public final static String USER_NAME = getProperty("user.name");
	
	public final static String OS_VERSION = getProperty("os.version");
	
	public final static String OS_ARCH = getProperty("os.arch");
	
	public final static String JAVA_VERSION = getProperty("java.version");
	
	public final static String JAVA_RUNTIME_VERSION = getProperty("java.runtime.version");
	
	public final static String JAVA_VM_SPECIFICATION_VERSION = getProperty("java.vm.specification.version");
	
	public final static String JAVA_HOME = getProperty("java.home");
	
	public final static String JAVA_VENDOR = getProperty("java.vendor");
	
	public final static String JAVA_SPECIFICATION_VERSION = getProperty("java.specification.version");
	
	/** ORACLE, IBM JDK 존재함, HP JDK 에서 존재하지 않음 */
	public final static String USER_COUNTRY = getProperty("user.country");
	
	public final static String USER_DIR = getProperty("user.dir");
	
	public final static String USER_LANGUAGE = getProperty("user.language");
	
	public final static String FILE_ENCODING = getProperty("file.encoding");
		
	//InetAddressUtil.getLocalHost().getHostAddress()은 초기화 시간을 지연시킴으로 호출시 캐쉬한다. 사용은 SystemUtil.getProperty("LOCALHOST_ADDRESS");
	//public final static String LOCALHOST_ADDRESS = getProperty("LOCALHOST_ADDRESS");
	
	public final static String SYSTEM_LOCALE = (USER_LANGUAGE != null && USER_COUNTRY != null) ? USER_LANGUAGE.concat("_").concat(USER_COUNTRY) : "ko_KR";
	
	private static Map<String, String> systemProperties = null;
	
	static {
		if( systemProperties == null) {
			if(logger.isDebugEnabled()) {
				logger.debug("Initializes the system properties");
			}
			systemProperties = new HashMap<String, String>();
			/** 필요시 추가하여 사용 */
			//systemProperties.put("EXTEND_PROP", "Data");
		}
	}
	
	public static String getExtendsProperty(String key){
		String out = null;
		if(key.equals("LOCALHOST_ADDRESS")) {
			//InetAddressUtil.getLocalHost().getHostAddress()은 오랜시간이 걸림으로 호출시 실행하여 셋팅한다.
			systemProperties.put(key, InetAddressUtil.getLocalHost().getHostAddress());
		}
		out = systemProperties.get(key);
	
		return out;
	}
	
	public static void printSystemProperties(){
		if(logger.isDebugEnabled()) {
			logger.debug("[printSystemProperties]");
		}
		Properties sysprops = System.getProperties();
		for(Enumeration<?> en = sysprops.propertyNames(); en.hasMoreElements();){
			String propsKey = (String) en.nextElement();
			logger.debug("[SYSPROP] " + propsKey + " : " + sysprops.getProperty(propsKey));
		}
		if(systemProperties != null) {
			for(Entry<String, String> entry : systemProperties.entrySet()) {
				logger.debug("[SYSPROP] " + entry.getKey() + " : " + entry.getValue());
			}
		} 
	}
	
	public static String getProperty(String key){
		
		String propsKey = key;
		String out = System.getProperty(propsKey);
		if( out == null ) {
			out = getExtendsProperty(propsKey);
		}
		
		if(logger.isTraceEnabled()) {
			logger.trace(CommonUtil.addString(key, STR_WHITE_SPACE, STR_COLON, STR_WHITE_SPACE, out));
		}
		return out;
	}
	
	public static String getEnvname(String key){
		
		String propsKey = key;
		String out = System.getenv(propsKey);
		
		if(logger.isTraceEnabled()) {
			logger.trace(CommonUtil.addString(key, STR_WHITE_SPACE, STR_COLON, STR_WHITE_SPACE, out));
		}
		return out;
	}
	
	public static long currentTimeMillis(){
    	return currentTimeMillis(null);
    }
	
	public static long currentTimeMillis(String localKey){
		if(localKey != null) {
			StackTraceElement beforeStack = CommonUtil.getCurrentStack();
			RuntimeHeader runtime = new RuntimeHeader();
			CommonHeader common = Local.commonHeader();
			long out = new Long(System.currentTimeMillis());
			runtime.setStartTimeMillies(out);
			runtime.setClassName(beforeStack.getClassName());
			runtime.setMethodName(beforeStack.getMethodName());
			common.addRuntimeHeader(localKey, runtime);
		}
    	return new Long(System.currentTimeMillis()); 
    }
	
	public static long durationCurrentTimeMillis(String localKey) {
		if(localKey != null) {
			throw new ValidateException("LocalKey is null...");
		}
		RuntimeHeader runtime = Local.commonHeader().getRuntimeHeader(localKey);
		if(runtime == null) {
			throw new ValidateException("There is no runtime information for the LocalKey....");
		}
		return durationCurrentTimeMillis(runtime.getStartTimeMillies());
	}
	
	public static long durationCurrentTimeMillis(long startCurrentTimeMillis) {
		long currentTimeMillis = startCurrentTimeMillis;
		long durationTime = currentTimeMillis() - currentTimeMillis;
		return durationTime;
	}
	
	public static double durationMillisecond(String localKey) {
		if(localKey != null) {
			throw new ValidateException("LocalKey is null...");
		}
		RuntimeHeader runtime = Local.commonHeader().getRuntimeHeader(localKey);
		if(runtime == null) {
			throw new ValidateException("There is no runtime information for the LocalKey....");
		}
		return durationMillisecond(runtime.getStartTimeMillies());
	}
	
	public static double durationMillisecond(long startCurrentTimeMillis) {
		long currentTimeMillis = startCurrentTimeMillis;
		double durationTime = (currentTimeMillis() - (currentTimeMillis)) / 1000.;
		return durationTime;
	}
	
	public static void out(Object message){
		out(message, new Throwable().getStackTrace()[ 1 ]);
	}
	
    public static void out(Object message, StackTraceElement stack) {
		
    	if(!CONFIRM_SYSTEMUTIL_SYSOUT) return;
    	
    	/*StackTraceElement[] stacks = new Throwable().getStackTrace();*/
    	StackTraceElement beforeStack = null;
    	if(stack != null) {
    		beforeStack = stack; 	
    	}
    	else {
    		beforeStack = new Throwable().getStackTrace()[ 1 ];
    	}
    	
    	StringBuffer stackBuffer = new StringBuffer();
    	stackBuffer.append(message);
    	stackBuffer.append(stackTraceUtil.getSystemOutFormat(beforeStack));
    	
    	
    	System.out.println(stackBuffer.toString());
    }
    
	public static void err(Object message){
		err(message, new Throwable().getStackTrace()[ 1 ]);
	}
    
    public static void err(Object message, StackTraceElement stack) {
		
    	if(!CONFIRM_SYSTEMUTIL_SYSERR) return;
    	
    	/*StackTraceElement[] stacks = new Throwable().getStackTrace();*/
    	StackTraceElement beforeStack = null;
    	if(stack != null) {
    		beforeStack = stack; 	
    	}
    	else {
    		beforeStack = new Throwable().getStackTrace()[ 1 ];
    	}
    	
    	StringBuffer stackBuffer = new StringBuffer();
    	stackBuffer.append(stackTraceUtil.getSystemOutFormat(beforeStack));
    	stackBuffer.append(message);
    	
    	System.err.println(stackBuffer.toString());
    }
    
}
