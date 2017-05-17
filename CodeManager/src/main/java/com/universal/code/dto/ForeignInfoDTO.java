package com.universal.code.dto;

public class ForeignInfoDTO {

	private String table_name= "";
	private String constraint_type= "";
	private String constraint_name= "";
	private String column_name= "";
	private String position= "";
	private String r_owner= "";
	private String r_constraint_name= "";
	private String fk_table_name= "";
	private String fk_column_name= "";
	
	
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getConstraint_type() {
		return constraint_type;
	}
	public void setConstraint_type(String constraint_type) {
		this.constraint_type = constraint_type;
	}
	public String getConstraint_name() {
		return constraint_name;
	}
	public void setConstraint_name(String constraint_name) {
		this.constraint_name = constraint_name;
	}
	public String getColumn_name() {
		return column_name;
	}
	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getR_owner() {
		return r_owner;
	}
	public void setR_owner(String r_owner) {
		this.r_owner = r_owner;
	}
	public String getR_constraint_name() {
		return r_constraint_name;
	}
	public void setR_constraint_name(String r_constraint_name) {
		this.r_constraint_name = r_constraint_name;
	}
	public String getFk_table_name() {
		return fk_table_name;
	}
	public void setFk_table_name(String fk_table_name) {
		this.fk_table_name = fk_table_name;
	}
	public String getFk_column_name() {
		return fk_column_name;
	}
	public void setFk_column_name(String fk_column_name) {
		this.fk_column_name = fk_column_name;
	}
}
