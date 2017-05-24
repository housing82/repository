package com.universal.code.dto;

import java.io.Serializable;

import com.universal.code.extend.CommonObject;
import com.universal.code.extend.IDataTransferObject;


public class FilePropertiesDTO extends CommonObject implements IDataTransferObject, Serializable {

	//다운로드 파일명 인코딩
	public static String downloadFileNameCharset;
	
	public String getDownloadFileNameCharset() {
		return downloadFileNameCharset;
	}

	public void setDownloadFileNameCharset(String downloadFileNameCharset) {
		FilePropertiesDTO.downloadFileNameCharset = downloadFileNameCharset;
	}
}
