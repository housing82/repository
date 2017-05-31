package com.universal.code.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.universal.code.extend.CommonObject;
import com.universal.code.extend.IDataTransferObject;

public class OmmDTO extends CommonObject implements IDataTransferObject, Serializable {

	private String ommType;
	private String ommDesc;
	private List<OmmFieldDTO> ommFields;
	
	public OmmDTO() {
		this.reset();
	}
	
	private void reset() {
		
		ommType = null;
		ommDesc = null;
		ommFields = new ArrayList<OmmFieldDTO>();
	}

	public String getOmmType() {
		return ommType;
	}

	public void setOmmType(String ommType) {
		this.ommType = ommType;
	}

	public String getOmmDesc() {
		return ommDesc;
	}

	public void setOmmDesc(String ommDesc) {
		this.ommDesc = ommDesc;
	}

	public List<OmmFieldDTO> getOmmFields() {
		return ommFields;
	}

	public void setOmmFields(List<OmmFieldDTO> ommFields) {
		this.ommFields = ommFields;
	}
	
	public void addOmmFields(OmmFieldDTO ommField) {
		if(ommFields == null) {
			this.ommFields = new ArrayList<OmmFieldDTO>();
		}
		this.ommFields.add(ommField);
	}
	
}