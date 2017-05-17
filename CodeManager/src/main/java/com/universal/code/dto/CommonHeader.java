package com.universal.code.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.annotation.Fields;
import com.universal.code.constants.IOperateCode;
import com.universal.code.parameter.SearchParam;
import com.universal.code.utils.SystemUtil;


@Component
public class CommonHeader implements Serializable {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 144157635035030490L;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, RuntimeHeader> runtimeHeader;
	
	@Fields(description = "헤더 어노테이션 맵")
	private Map<String, Object> annotationMap;
	
	@Fields(description = "헤더 오브젝트 맵")
	private Map<String, Object> objectMap;
	
	@Fields(description = "헤더 프로퍼티")
	private Properties	properties;
	
	@Fields(description = "거래코드", groupName = IOperateCode.META_COMM_HEADER, isMarshalField = true)
	private String 	guid;
	
	@Fields(description = "결과코드", groupName = IOperateCode.META_COMM_HEADER, isMarshalField = true)
	private int		resultCode;
	
	@Fields(description = "메시지", groupName = IOperateCode.META_COMM_HEADER, isMarshalField = true)
	private String	message;

	@Fields(description = "원인", groupName = IOperateCode.META_COMM_HEADER, isMarshalField = true)
	private String	cause;
	
	@Fields(description = "원인추적", groupName = IOperateCode.META_COMM_HEADER, isMarshalField = true)
	private String	stackTrace;

	@Fields(description = "실행시간", groupName = IOperateCode.META_COMM_HEADER, isMarshalField = true)
	private long	runTimeMillis;

	private String[]	arrayMessages;

	private List<String>	traceMessages;

	private long	startTimeMillis;
	
	private long	endTimeMillis;
	
	private String	receiveFormat;
	
	private String	encoding;
	
	private boolean	ruleValidate;
	
	private String elementRoot;
	
	private String appContents;
	
	private String pagination;
	
	private String clientAddress;
	
	private String controllerType;
	
	private boolean runController;
	
	private boolean exec;
	
	private Map<String, String[]> requestMap;
	
	private SearchParam searchParam; 
		
	private HttpServletRequest request;

	private HttpServletResponse response;
	
	public CommonHeader(){
		this.reset();
	}
	
	private void reset(){
		annotationMap = null;
		objectMap = null;
		properties = null;
		
		guid = "";
		startTimeMillis = 0;
		endTimeMillis = -1;
		runTimeMillis = -1;
		resultCode = IOperateCode.SERVICE_RESULT_CODE_SUCCESS;
		message = "";
		cause = "";
		stackTrace = "";
		arrayMessages = null;
		traceMessages = null;
		receiveFormat = null;
		encoding = IOperateCode.DEFAULT_ENCODING;
		ruleValidate = true;
		elementRoot = null;
		appContents = null;
		pagination = null;
		clientAddress = null;
		exec = false;
		searchParam = null;
		controllerType = IOperateCode.SPRING_CONTROLLER; // ( SPRING or DWR )
		requestMap = new LinkedHashMap<String, String[]>();
		request = null;
		response = null;
		
		runtimeHeader = null;
	}
	
	public RuntimeHeader getRuntimeHeader(String key) {
		return runtimeHeader.get(key);
	}
	
	
	public Map<String, RuntimeHeader> getRuntimeHeader() {
		return runtimeHeader;
	}

	public void setRuntimeHeader(LinkedHashMap<String, RuntimeHeader> runtimeHeader) {
		this.runtimeHeader = runtimeHeader;
	}

	public void addRuntimeHeader(String key, RuntimeHeader header) {
		if(runtimeHeader == null) {
			this.runtimeHeader = new LinkedHashMap<String, RuntimeHeader>();
		}
		runtimeHeader.put(key, header);
	}
	
	public Map<String, Object> getAnnotationMap() {
		return annotationMap;
	}

	public void setAnnotationMap(Map<String, Object> annotationMap) {
		this.annotationMap = annotationMap;
	}

	public void addAnnotation(String key, Object clazz) {
		if(annotationMap == null) {
			this.annotationMap = new HashMap<String, Object>();
		}
		annotationMap.put(key, clazz);
	}

	public Object getAnnotation(String key) {
		if(annotationMap != null) {
			return annotationMap.get(key);
		}
		return null;
	}

	public void setObject(String key, Object clazz) {
		if(objectMap == null) {
			this.objectMap = new HashMap<String, Object>();
		}
		objectMap.put(key, clazz);
	}

	public Object getObject(String key) {
		if(objectMap != null) {
			return objectMap.get(key);
		}
		return null;
	}
	
	public Map<String, Object> getObjectMap() {
		return objectMap;
	}

	public void setObjectMap(Map<String, Object> objectMap) {
		this.objectMap = objectMap;
	}

	public void setProperties(String key, String value) {
		if(properties == null) {
			this.properties = new Properties();
		}
		properties.setProperty(key, value);
	}

	public String getProperties(String key) {
		return getProperties(key, null);
	}

	public String getProperties(String key, String defaultValue) {
		if(properties != null) {
			return properties.getProperty(key, defaultValue);
		}
		return null;
	}
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public long getStartTimeMillis() {
		return startTimeMillis;
	}

	public void setStartTimeMillis(long startTimeMillis) {
		this.startTimeMillis = startTimeMillis;
	}

	public long getEndTimeMillis() {
		if(this.resultCode != IOperateCode.SERVICE_RESULT_CODE_SUCCESS || endTimeMillis == -1) {
			setEndTimeMillis(SystemUtil.currentTimeMillis());
		}
		return endTimeMillis;
	}

	public void setEndTimeMillis(long endTimeMillis) {
		this.endTimeMillis = endTimeMillis;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		if(logger.isDebugEnabled()) {
			logger.debug(" [SET COMMON HEADER MESSAGE] : " + message);
		}
		this.message = message;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void addProperties(String key, String value) {
		if( properties == null ) properties = new Properties();
		this.properties.setProperty(key, (value == null ? IOperateCode.MESSAGE_ISNULL : value));
	} 
	
	
	public void setRunTimeMillis(long runTimeMillis) {
		this.runTimeMillis = runTimeMillis;
	}

	public long getRunTimeMillis() {
		if(runTimeMillis == -1) runTimeMillis = getEndTimeMillis() - startTimeMillis;
		return runTimeMillis;
	}

	public String[] getArrayMessages() {
		return arrayMessages;
	}

	public void setArrayMessages(String[] arrayMessages) {
		this.arrayMessages = arrayMessages;
	}

	public List<String> getTraceMessages() {
		return traceMessages;
	}

	public void setTraceMessages(List<String> traceMessages) {
		this.traceMessages = traceMessages;
	}

	public void addTraceMessages(String traceMessages) {
		if( this.traceMessages == null ) this.traceMessages = new ArrayList<String>();
		this.traceMessages.add(traceMessages);
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getReceiveFormat() {
		return receiveFormat;
	}

	public void setReceiveFormat(String receiveFormat) {
		this.receiveFormat = receiveFormat;
	}


	public boolean isRuleValidate() {
		return ruleValidate;
	}


	public void setRuleValidate(boolean ruleValidate) {
		this.ruleValidate = ruleValidate;
	}


	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		if(encoding != null && !encoding.isEmpty()) {
			this.encoding = encoding;
		}
	}

	public String getElementRoot() {
		return elementRoot;
	}


	public void setElementRoot(String elementRoot) {
		this.elementRoot = elementRoot;
	}

	public String getAppContents() {
		return appContents;
	}
	
	public void setAppContents(String appContents) {
		this.appContents = appContents;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getControllerType() {
		return controllerType;
	}

	public void setControllerType(String controllerType) {
		this.controllerType = controllerType;
	}

	public boolean isRunController() {
		return runController;
	}

	public void setRunController(boolean runController) {
		this.runController = runController;
	}

	public Map<String, String[]> getRequestMap() {
		return requestMap;
	}

	public void setRequestMap(Map<String, String[]> requestMap) {
		this.requestMap = requestMap;
	}

	public SearchParam getSearchParam() {
		return searchParam;
	}

	public void initSearchParam(HttpServletRequest request) {
		searchParam = new SearchParam(request);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getPagination() {
		return pagination;
	}

	public void setPagination(String pagination) {
		this.pagination = pagination;
	}

	public boolean isExec() {
		return exec;
	}

	public void setExec(boolean exec) {
		this.exec = exec;
	}

	
}
