package com.universal.code.exception;



import com.universal.code.utils.StackTraceUtil;
import com.universal.code.utils.thread.Local;


public class RunTimeException extends RuntimeException {

	static final long serialVersionUID = 5930391908933291621L;

    private Object arguments[];
    
    private int resultCode;
    
    protected StackTraceUtil stackTrace;
    
    protected void init(){
    	resultCode = -1;
    	stackTrace = new StackTraceUtil();
    }
    
    public RunTimeException(String message) {
        super(message);
    }

    public RunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RunTimeException(Throwable cause) {
        super(cause);
    }

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		Local.commonHeader().setResultCode(resultCode);
		this.resultCode = resultCode;
		
	}
}
