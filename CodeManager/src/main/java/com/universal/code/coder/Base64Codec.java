package com.universal.code.coder;

import org.springframework.stereotype.Component;
import org.apache.commons.codec.binary.Base64;

/**
* <p>Title: Base64Codec</p>
* <p>Description:
* Apache codec 의 base64 를 이용하여 문자열의 base64 인코딩,디코딩을 담당한다.
* </p>
* <p>Copyright: Copyright (c) 2011</p>
* <p>Company: mvc</p>
* @since 2011. 07. 04
* @author ksw
* @version 1.0
*/
@Component
public class Base64Codec {

	private static final Base64Codec INSTANCE = new Base64Codec();

    public static Base64Codec getInstance()
    {
        return INSTANCE;
    }

    public String encode(String value)
    {
        String result = "";
        try
        {
            byte plainText[] = null;
            plainText = value.getBytes();
            result = Base64.encodeBase64URLSafeString(plainText);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public String decode(String value)
    {
        String result = "";
        try
        {
        	Base64 decoder = new Base64();
            byte plainText[] = null;
            plainText = decoder.decode(value);
            result = new String(plainText);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }




}
