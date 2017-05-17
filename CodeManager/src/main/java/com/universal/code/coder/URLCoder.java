package com.universal.code.coder;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;

/**
* <p>Title: URL</p>
* <p>Description:
* java.net 의 URL En/De coder 를 이용해 URL의 인코딩/디코딩에 사용한다.
* </p>
* <p>Copyright: Copyright (c) 2011</p>
* <p>Company: mvc</p>
* @since 2011. 07. 04
* @author ksw
* @version 1.0
*/

@Component
public class URLCoder {

    private static final URLCoder INSTANCE = new URLCoder();

    public static URLCoder getInstance()
    {
        return INSTANCE;
    }

	private final String DEFAULT_ENC = IOperateCode.DEFAULT_ENCODING;

	/**
	 * 주어진 케릭터 인코딩 타입으로 URLDecoding 하여줍니다.
	 * @param str
	 * @param charater
	 * @return
	 */
	public String getURLDecode(String str, String enc){

		String decodeStr = "";
		try {
			decodeStr = URLDecoder.decode(str, (enc == null || enc.equals("") ? DEFAULT_ENC:enc));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decodeStr;
	}

	/**
	 * 주어진 케릭터 인코딩 타입으로 URLEncoding 하여줍니다.
	 * @param str
	 * @param encoding
	 * @return
	 */
	public String getURLEncode(String str, String enc){

		String encodeStr = "";
		try {
			encodeStr = URLEncoder.encode(str, (enc == null || enc.equals("") ? DEFAULT_ENC:enc));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodeStr;
	}

}
