package com.universal.code.dto;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import com.universal.code.extend.CommonObject;
import com.universal.code.extend.IDataTransferObject;


public class FilesystemDTO extends CommonObject implements IDataTransferObject, Serializable {
	
	private String type;
	private String name;
	private String ext;
	private String canonicalPath;
	private String relativePath;
	private long lastModified;
	private long size;
	private String sizeString;
	private long totalFile;
	private long totalDirectory;
	private Object realObject;
	private Annotation[] annotations;
	
	public FilesystemDTO(){
		this.reset();
	}
	
	private void reset(){
		type = "";
		name = "";
		ext = "";
		canonicalPath = "";
		relativePath = "";
		lastModified = 0L;
		size = 0L;
		sizeString = null;
		totalFile = 0L;
		totalDirectory = 0L;
		realObject = null;
		annotations = null;
	}

	

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCanonicalPath() {
		return canonicalPath;
	}

	public void setCanonicalPath(String canonicalPath) {
		this.canonicalPath = canonicalPath;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getSizeString() {
		return sizeString;
	}

	public void setSizeString(String sizeString) {
		this.sizeString = sizeString;
	}

	public long getTotalFile() {
		return totalFile;
	}

	public void setTotalFile(long totalFile) {
		this.totalFile = totalFile;
	}

	public long getTotalDirectory() {
		return totalDirectory;
	}

	public void setTotalDirectory(long totalDirectory) {
		this.totalDirectory = totalDirectory;
	}

	public Object getRealObject() {
		return realObject;
	}

	public void setRealObject(Object realObject) {
		this.realObject = realObject;
	}
}
