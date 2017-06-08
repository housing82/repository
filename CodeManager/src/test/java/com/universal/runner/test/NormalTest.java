package com.universal.runner.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NormalTest {

	private static Logger logger = LoggerFactory.getLogger(NormalTest.class);
	
	@Test
	public void main() {
		
		try {
			//EUC-KR로 new String된 문자열을 다른 케릭터셋으로 변환
			String text = new String("* 한글문자열 스트림 변환 *".getBytes("EUC-KR"), "EUC-KR");
			
		    InputStream is = new ByteArrayInputStream(text.getBytes("EUC-KR"));
		    BufferedReader br = new BufferedReader(new InputStreamReader(is,"EUC-KR"));
		    write(br);
		} catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		}

		
		
		try {
			//ANSI 파일에서 읽은 문자열의 인코딩을 변환
			String currnetPath = URLDecoder.decode(this.getClass().getClassLoader().getResource(".").getPath().concat(this.getClass().getPackage().getName().replace(".", "/")), "UTF-8");
			logger.debug("currnetPath: {}", currnetPath);
			logger.debug("package: {}", this.getClass().getPackage().getName());
			
			File file = new File(currnetPath.concat("/ansi.txt"));
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,"EUC-KR"));
			write(br);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(BufferedReader br) {
		

		StringBuffer szBuffer = new StringBuffer();
		String strRead = null;

		try {
			while ((strRead = br.readLine()) != null)
				szBuffer.append(strRead + "\n");
	
			logger.debug("----------------------");
			logger.debug(szBuffer.toString()); // -- 변환 없이 출력
			logger.debug("----------------------");
			
			// EUC-KR 로 Decoding
			String str1 = new String(szBuffer.toString().getBytes("EUC-KR"), "EUC-KR"); 							
			logger.debug("EUC-KR:\n{}", str1);
	
			// EUC-KR --> UTF-8 로 Encoding 및 Decoding
			String str2 = new String(str1.getBytes("UTF-8"), "UTF-8"); 
			logger.debug("UTF-8:\n{}", str2);
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
