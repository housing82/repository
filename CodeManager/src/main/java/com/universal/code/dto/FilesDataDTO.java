package com.universal.code.dto;

import java.io.Serializable;

import com.universal.code.extend.CommonObject;
import com.universal.code.extend.IDataTransferObject;


/**
* <p>Title: FilesDataDTO</p>
* <p>Description: RQMS DB Table : HCT_FILES_DAT DataTransferObject</p>
* <p>Copyright: Copyright (c) 2013</p>
* @since : 2013
* @author : Design criteria automatically generated DTO
* @version 1.0
*/


public class FilesDataDTO extends CommonObject implements IDataTransferObject, Serializable {

	private	long   	file_id;	//column name : FILE_ID, data type : NUMBER(0,22), comments : 첨부파일 ID
	private	String  file_nm;	//column name : FILE_NM, data type : VARCHAR2(500), comments : 파일 명
	private	long   	file_size;	//column name : FILE_SIZE, data type : NUMBER(0,22), comments : 파일 사이즈
	private	String  file_mime_type;	//column name : FILE_MIME, data type : VARCHAR2(100), comments : 파일 마임타입
	private	String	file_sys_path;	//column name : FILE_SAVE_PATH, data type : VARCHAR2(1000), comments : 파일 저장 경로
	private	String  actu_save_file_nm;	//column name : ACTU_SAVE_FILE_NM, data type : VARCHAR2(200), comments : 실제 저장 파일 명
	private long 	file_rels_id;
	
	private String 	file_ext;
	
	public FilesDataDTO(){
		this.reset();
	}

	private void reset(){
		file_id = 0;
		file_nm = "";
		file_size = 0;
		file_mime_type = "";
		file_sys_path = "";
		actu_save_file_nm = "";
		file_rels_id = 0;
		
		file_ext = "";
	}

	
	public String getFile_sys_path() {
		return file_sys_path;
	}

	public void setFile_sys_path(String file_sys_path) {
		this.file_sys_path = file_sys_path;
	}

	public String getFile_mime_type() {
		return file_mime_type;
	}

	public void setFile_mime_type(String file_mime_type) {
		this.file_mime_type = file_mime_type;
	}

	public void setFile_id(long file_id){
		this.file_id = file_id;
 	}

	public void setFile_nm(String file_nm){
		this.file_nm = file_nm;
 	}

	public void setFile_size(long file_size){
		this.file_size = file_size;
 	}

	public void setActu_save_file_nm(String actu_save_file_nm){
		this.actu_save_file_nm = actu_save_file_nm;
 	}

	public long getFile_id(){
		return file_id;
   	}

	public String getFile_nm(){
		return file_nm;
   	}

	public long getFile_size(){
		return file_size;
   	}

	public String getActu_save_file_nm(){
		return actu_save_file_nm;
   	}

	public long getFile_rels_id() {
		return file_rels_id;
	}

	public void setFile_rels_id(long file_rels_id) {
		this.file_rels_id = file_rels_id;
	}

	public String getFile_ext() {
		return file_ext;
	}

	public void setFile_ext(String file_ext) {
		this.file_ext = file_ext;
	}


}