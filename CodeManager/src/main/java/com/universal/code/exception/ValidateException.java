package com.universal.code.exception;

import com.universal.code.constants.IOperateCode;
import com.universal.code.utils.CommonUtil;
import com.universal.code.utils.StringUtil;


public class ValidateException extends RunTimeException {

	private static final long serialVersionUID = -8952033534298862944L;

	public static final String DEFAULT_MESSAGE = "유효성 검사에 실패하였습니다.";
    
    public ValidateException()
    {
        super(DEFAULT_MESSAGE);

        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_INVALID);
    }

    public ValidateException(String message, int resultCode)
    {
        super(message);

        super.init(); 
        setResultCode(resultCode);
    }
    
    public ValidateException(String message)
    {
        super(message);
        
        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_INVALID);
    }
    
    public ValidateException(Throwable cause)
    {
        super(DEFAULT_MESSAGE, cause);
        
        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_INVALID);
    }

    public ValidateException(String message, Object arguments[], int resultCode)
    {
        super(formatMessage(message, arguments));
        
        super.init(); 
        setResultCode(resultCode);
    }
    
    public ValidateException(String message, Object... arguments)
    {
        super(formatMessage(message, arguments));

        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_INVALID);
    }

    public ValidateException(String message, Throwable cause)
    {
        super(message, cause);

        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_INVALID);
    }

    public ValidateException(String message, Object arguments[], Throwable cause)
    {
        super(formatMessage(message, arguments), cause);

        super.init(); 
        setResultCode(IOperateCode.SERVICE_RESULT_CODE_INVALID);
    }
    


    private static String formatMessage(String message, Object... arguments)
    {
        return CommonUtil.formatMessage(StringUtil.NVL(message, DEFAULT_MESSAGE), arguments);
    }


}
