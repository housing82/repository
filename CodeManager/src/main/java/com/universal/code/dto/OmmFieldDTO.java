package com.universal.code.dto;

import java.io.Serializable;

import com.universal.code.extend.CommonObject;
import com.universal.code.extend.IDataTransferObject;


public class OmmFieldDTO extends CommonObject implements IDataTransferObject, Serializable {

	private String type;
	private String name;
	private String length;
	private String description;
	private String arrayReference;
	private String arrayReferenceType;
	
	public OmmFieldDTO() {
		this.reset();
	}
	
	private void reset() {
		
		type = null;
		name = null;
		length = "0";
		description = null;
		arrayReference = null;
		arrayReferenceType = null;
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

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getArrayReference() {
		return arrayReference;
	}

	public void setArrayReference(String arrayReference) {
		this.arrayReference = arrayReference;
	}

	public String getArrayReferenceType() {
		return arrayReferenceType;
	}

	public void setArrayReferenceType(String arrayReferenceType) {
		this.arrayReferenceType = arrayReferenceType;
	}

	
}