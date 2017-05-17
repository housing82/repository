package com.universal.code.dto;

import java.util.ArrayList;
import java.util.List;

public class TableDTO {


	private String	tableName;
	private String	tableType;
	private String	tableComments;
	private String	dtoName;
	private String	dtoPackage;
	private String	inTables;
	private String 	extendClass;
	private List<String> extendImportType;
	private List<String> addField;
	private boolean addFieldOnly;
	private boolean mergeMake;
	
	public TableDTO(){
		this.reset();
	}
	
	private void reset(){
		tableName="";
		tableType="";
		tableComments="";
		dtoName="";
		dtoPackage="";
		inTables="";
		extendClass="";
		extendImportType=new ArrayList<String>();
		addField=new ArrayList<String>();
		addFieldOnly=false;
		mergeMake=false;
	}
	

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getTableComments() {
		return tableComments;
	}

	public void setTableComments(String tableComments) {
		this.tableComments = tableComments;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDtoName() {
		return dtoName;
	}
	public void setDtoName(String dtoName) {
		this.dtoName = dtoName;
	}
	public String getDtoPackage() {
		return dtoPackage;
	}
	public void setDtoPackage(String dtoPackage) {
		this.dtoPackage = dtoPackage;
	}

	public String getInTables() {
		return inTables;
	}

	public void setInTables(String inTables) {
		this.inTables = inTables;
	}

	public String getExtendClass() {
		return extendClass;
	}

	public void setExtendClass(String extendClass) {
		this.extendClass = extendClass;
	}



	public List<String> getExtendImportType() {
		return extendImportType;
	}

	public void setExtendImportType(List<String> extendImportType) {
		this.extendImportType = extendImportType;
	}

	public List<String> getAddField() {
		return addField;
	}

	public void setAddField(List<String> addField) {
		this.addField = addField;
	}

	public boolean isAddFieldOnly() {
		return addFieldOnly;
	}

	public void setAddFieldOnly(boolean addFieldOnly) {
		this.addFieldOnly = addFieldOnly;
	}

	public boolean isMergeMake() {
		return mergeMake;
	}

	public void setMergeMake(boolean mergeMake) {
		this.mergeMake = mergeMake;
	}
}
