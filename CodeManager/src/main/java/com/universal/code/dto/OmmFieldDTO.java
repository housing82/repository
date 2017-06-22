package com.universal.code.dto;

import java.io.Serializable;

import com.universal.code.extend.CommonObject;
import com.universal.code.extend.IDataTransferObject;


public class OmmFieldDTO extends CommonObject implements IDataTransferObject, Serializable {

	private String type;
	private String name;
	private String changeName;
	private String changeSetterName;
	private String changeGetterName;
	private String length;
	private String description;
	private String arrayReference;
	private String arrayReferenceType;
	private String arrayReferenceLength;
	private String sourceRoot;
	
	public OmmFieldDTO() {
		this.reset();
	}
	
	private void reset() {
		
		type = null;
		name = null;
		changeName = null;
		changeSetterName = null;
		changeGetterName = null;
		length = "0";
		description = null;
		arrayReference = null;
		arrayReferenceType = null;
		arrayReferenceLength = null;
		sourceRoot = null;
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

	public String getChangeName() {
		return changeName;
	}

	public void setChangeName(String changeName) {
		this.changeName = changeName;
	}

	public String getChangeSetterName() {
		return changeSetterName;
	}

	public void setChangeSetterName(String changeSetterName) {
		this.changeSetterName = changeSetterName;
	}

	public String getChangeGetterName() {
		return changeGetterName;
	}

	public void setChangeGetterName(String changeGetterName) {
		this.changeGetterName = changeGetterName;
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

	public String getSourceRoot() {
		return sourceRoot;
	}

	public void setSourceRoot(String sourceRoot) {
		this.sourceRoot = sourceRoot;
	}

	public String getArrayReferenceLength() {
		return arrayReferenceLength;
	}

	public void setArrayReferenceLength(String arrayReferenceLength) {
		this.arrayReferenceLength = arrayReferenceLength;
	}

	
}