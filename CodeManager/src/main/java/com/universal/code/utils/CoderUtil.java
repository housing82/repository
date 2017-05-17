package com.universal.code.utils;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.coder.Base64Codec;
import com.universal.code.coder.MD5;
import com.universal.code.coder.URLCoder;
import com.universal.code.constants.IOperateCode;


/**
* <p>Title: CryptionUtil</p>
* <p>Description:
* 문자열의 인코딩/디코딩 및 암호화를 담당하는 Cryption 공통클래스
* </p>
* <p>Copyright: Copyright (c) 2011</p>
* <p>Company: mvc</p>
* @since 2011. 07. 04
* @author ksw
* @version 1.0
*/
@Component
public class CoderUtil {

	protected static final Logger logger = LoggerFactory.getLogger(CoderUtil.class);

	/**
	 * MD5 인스턴스
	 */
	private final MD5 md5 = MD5.getInstance();

	/**
	 * org.apache.commons.codec.binary.Base64 인스턴스
	 */
	private final Base64Codec base64Codec = Base64Codec.getInstance();

	/**
	 * URL En/De code 클래스 인스턴스
	 */
	private final URLCoder url = URLCoder.getInstance();

	/**
	 * 기본 인코딩 케릭터셋
	 */
	private final String defaultEnc = IOperateCode.DEFAULT_ENCODING;

    /**
     * 일반적인 md5 암호화 결과를 리턴합니다.
     * @param str
     * @return
     */
	public String hashStringMD5(String str){
		return md5.getHashString(str);
	}

    /**
     * 일반적인 md5 암호화 결과를 리턴합니다.
     * @param str
     * @return
     */
	public String encryptMD5(String str){
		return md5.getEncryptMD5(str);
	}
	
	/**
	 * 첫번째 String 과 getHashMD5 으로 암호화한 값의 일치여부를 리턴합니다.
	 * @param MD5Code
	 * @param NewStr
	 * @return
	 */
	public boolean compareHashStringMD5(String MD5Code, String NewStr){
		boolean compare = false;
		if(MD5Code.equals(hashStringMD5(NewStr))) compare = true;
		return compare;
	}
	
	/**
	 * 첫번째 String 과 getEncryptMD5 으로 암호화한 값의 일치여부를 리턴합니다.
	 * @param EncryptMD5Code
	 * @param NewStr
	 * @return
	 */
	public boolean compareEncryptMD5(String EncryptMD5Code, String NewStr){
		boolean compare = false;
		if(EncryptMD5Code.equals(md5.getEncryptMD5(NewStr))) {
			compare = true;
		}
		return compare;
	}

	/**
	 * String 을 base64로 인코딩하여 줍니다.
	 * @param str
	 * @return
	 */
	public String encodeBase64Codec(String str){
		return base64Codec.encode(str);
	}

	/**
	 * String 을 base64로 디코딩하여 줍니다.
	 * @param str
	 * @return
	 */
	public String decodeBase64Codec(String str){
		return base64Codec.decode(str);
	}

	/**
	 * URLEncoding 하여줍니다.
	 * @param str
	 * @return
	 */
	public String encodeURL(String str){
		return encodeURL(str, null);
	}

	/**
	 * 주어진 케릭터 인코딩 타입으로 URLEncoding 하여줍니다.
	 * @param str
	 * @param encoding
	 * @return
	 */
	public String encodeURL(String str, String type){
		return url.getURLEncode(str,  type);
	}

	/**
	 * URLDecoding 하여줍니다.
	 * @param str
	 * @return
	 */
	public String decodeURL(String str){
		return decodeURL(str, null);
	}

	/**
	 * 주어진 케릭터 인코딩 타입으로 URLDecoding 하여줍니다.
	 * @param str
	 * @param charater
	 * @return
	 */
	public String decodeURL(String str, String type){
		return url.getURLDecode(str, type);
	}

	/**
	 * 인코딩타입을 8859_1으로 복호화 하고 바이트 배열에 저장한값을
	 * 문자열로 리턴합니다.
	 * @param str
	 * @param character
	 * @return
	 */
	public String newString(String str){

		String value = "";
		try {
			value = new String(str.getBytes("8859_1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 인코딩타입을 8859_1으로 복호화 하고 바이트 배열에 저장한값을
	 * 두번째 파라메터 type 으로 변환하여줍니다.
	 * @param str
	 * @param character
	 * @return
	 */
	public String newString(String str, String type){

		String value = "";
		try {
			value = new String(str.getBytes("8859_1"), (type == null || type.equals("") ? defaultEnc:type));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return value;
	}

	//@Test
	public void base64() {
		String sentence = "베이스64 엔코딩디코딩 test class function 테스트";

		String _64Codec = encodeBase64Codec(sentence);
		SystemUtil.out(" encodeBase64Codec : " + _64Codec);
		SystemUtil.out(" decodeBase64Codec : " + decodeBase64Codec(_64Codec));

		SystemUtil.out("─────────────────────────");

		sentence = "#S00000002";
		String urlStr = encodeURL(sentence, "UTF-8");
		SystemUtil.out(" encodeURL : " + urlStr);
		SystemUtil.out(" decodeURL : " + decodeURL(urlStr));

		SystemUtil.out("─────────────────────────");

		SystemUtil.out(" hashStringMD5 : " + hashStringMD5(sentence));

	}

}
