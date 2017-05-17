package com.universal.code.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StackTraceUtil {

	protected static final Logger logger = LoggerFactory.getLogger(StackTraceUtil.class);
	
    public String getSystemOutFormat(StackTraceElement stackTraceElement) {
    	
    	StackTraceElement beforeStack = stackTraceElement;
    	
    	if( beforeStack == null) {
    		throw new RuntimeException( " StackTraceElement is Null !!" );
    	}
    	
    	StringBuffer message = new StringBuffer();
    	
    	//message.append("sysout");
    	message.append(" [");    	
    	message.append( DateUtil.getFastDate("HH:mm:ss") );
    	message.append("] [");
    	message.append(beforeStack.getClassName());
    	message.append(".");
    	message.append(beforeStack.getMethodName());
    	message.append(":");
    	message.append(beforeStack.getLineNumber());
    	message.append("]");
    	
    	return message.toString();
    }
    
    public String getStackTrace(Throwable cause){

    	StackTraceElement[] stackTrace = cause.getStackTrace();
    	
    	if( stackTrace == null ) {
    		throw new RuntimeException( " StackTraceElement[] is Null !!" );
    	}
    	
    	StringBuffer message = new StringBuffer();
    	if(cause.getMessage() == null) {
    		message.append(cause.toString());
    		message.append(SystemUtil.LINE_SEPARATOR);
    	}
    	
    	if(cause.getMessage() != null) {
    		message.append("Message : ");
    		message.append(cause.getMessage());
    		message.append(SystemUtil.LINE_SEPARATOR);
    	} 
    	    	
    	if(stackTrace.length > 0) { 
    		message.append("Trace : ");
	    	for(StackTraceElement stack : stackTrace){
	    		message.append(stack.toString());
	    		message.append(SystemUtil.LINE_SEPARATOR);
	    	}
    	}
    	
    	if(cause.getCause() != null && cause.getCause().getStackTrace().length > 0) {
    		message.append("Caused by : ");
	    	for(StackTraceElement stack : cause.getCause().getStackTrace()){
	    		message.append(stack.toString());
	    		message.append(SystemUtil.LINE_SEPARATOR);
	    	}
    	}
    	
    	if(!message.toString().endsWith(SystemUtil.LINE_SEPARATOR) && !message.toString().endsWith("\n")) {
    		message.append(SystemUtil.LINE_SEPARATOR);
    	}
    	
    	return message.toString();
    }
    
    
}
