package com.universal.code.exception;

import com.universal.code.constants.IOperateCode;
import com.universal.code.utils.CommonUtil;
import com.universal.code.utils.StringUtil;


public class ApplicationException extends RunTimeException {

	private static final long serialVersionUID = 8537976232349035843L;

	public static final String DEFAULT_MESSAGE = "어플리케이션에서 오류가 발생하였습니다.";
    
    public ApplicationException()
    {
        super(DEFAULT_MESSAGE);

        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_ERROR);
    }

    public ApplicationException(String message, int resultCode)
    {
        super(message);

        super.init(); 
        setResultCode(resultCode);
    }
    
    public ApplicationException(String message)
    {
        super(message);
        
        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_ERROR);
    }
    
    public ApplicationException(Throwable cause)
    {
        super(DEFAULT_MESSAGE, cause);
        
        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_ERROR);
    }

    public ApplicationException(String message, Object arguments[], int resultCode)
    {
        super(formatMessage(message, arguments));
        
        super.init(); 
        setResultCode(resultCode);
    }
    
    public ApplicationException(String message, Object... arguments)
    {
        super(formatMessage(message, arguments));
        
        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_ERROR);
    }

    public ApplicationException(String message, Throwable cause)
    {
        super(message, cause);
        
        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_ERROR);
    }

    public ApplicationException(String message, Object arguments[], Throwable cause)
    {
        super(formatMessage(message, arguments), cause);
   
        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_ERROR);
    }
    
    
    private static String formatMessage(String message, Object... arguments)
    {
        return CommonUtil.formatMessage(StringUtil.NVL(message, DEFAULT_MESSAGE), arguments);
    }


}
