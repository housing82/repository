package com.universal.code.dto;

import java.io.Serializable;

import com.universal.code.extend.CommonObject;
import com.universal.code.extend.IDataTransferObject;

/**
 * VT_SCHEMA 데이터 DTO
 * @author Administrator
 *
 */
public class VtSchemaDTO extends CommonObject implements IDataTransferObject, Serializable {


	 private String table_name;
	 private String table_comments;
	 private String table_type;
	 private String column_name;
	 private String	data_type;
	 private long   data_length;
	 private String data_default;
	 private String data_default_var2;
	 private String nullable;
	 private String constraints;
	 private String indexes;
	 private String constraint_type;
	 private String comments;
	 private String data_full_type;
	 private String java_type;
	 private long   data_scale;
	 private long   column_id;
	
	 public VtSchemaDTO(){
		 this.reset();
	 }
	 
	public void reset(){
		  table_name = "";
		  table_comments = "";
		  table_type = "";
		  column_name = "";
		  data_type = "";
		  data_length = 0;
		  data_default = "";
		  data_default_var2 = "";
		  nullable = "";
		  constraints = "";
		  indexes = "";
		  constraint_type = "";
		  comments = "";
		  data_full_type = "";
		  java_type = "";
		  data_scale = 0;
		  column_id = 0;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getTable_comments() {
		return table_comments;
	}

	public void setTable_comments(String table_comments) {
		this.table_comments = table_comments;
	}

	public String getTable_type() {
		return table_type;
	}

	public void setTable_type(String table_type) {
		this.table_type = table_type;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public long getData_length() {
		return data_length;
	}

	public void setData_length(long data_length) {
		this.data_length = data_length;
	}

	public String getData_default() {
		return data_default;
	}

	public void setData_default(String data_default) {
		this.data_default = data_default;
	}

	public String getData_default_var2() {
		return data_default_var2;
	}

	public void setData_default_var2(String data_default_var2) {
		this.data_default_var2 = data_default_var2;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getData_full_type() {
		return data_full_type;
	}

	public void setData_full_type(String data_full_type) {
		this.data_full_type = data_full_type;
	}

	public String getJava_type() {
		return java_type;
	}

	public void setJava_type(String java_type) {
		this.java_type = java_type;
	}

	public long getData_scale() {
		return data_scale;
	}

	public void setData_scale(long data_scale) {
		this.data_scale = data_scale;
	}

	public long getColumn_id() {
		return column_id;
	}

	public void setColumn_id(long column_id) {
		this.column_id = column_id;
	}

	public String getConstraint_type() {
		return constraint_type;
	}

	public void setConstraint_type(String constraint_type) {
		this.constraint_type = constraint_type;
	}

	public String getIndexes() {
		return indexes;
	}

	public void setIndexes(String indexes) {
		this.indexes = indexes;
	}
	
	
}
