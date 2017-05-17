package com.universal.code.dto;


public class FilePropertiesDTO {

	//다운로드 파일명 인코딩
	public static String 	downloadFileNameCharset;
	
	public String getDownloadFileNameCharset() {
		return downloadFileNameCharset;
	}

	public void setDownloadFileNameCharset(String downloadFileNameCharset) {
		FilePropertiesDTO.downloadFileNameCharset = downloadFileNameCharset;
	}
}
