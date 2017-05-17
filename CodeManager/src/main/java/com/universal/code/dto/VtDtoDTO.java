package com.universal.code.dto;

/**
 * VT_DTO 데이터 DTO
 * @author Administrator
 *
 */
public class VtDtoDTO {


	   private String var;	   //column name : VAR, data type : VARCHAR2(49), comments : 	      var = "";
	   private String attr_doc;	   //column name : ATTR_DOC, data type : VARCHAR2(4000), comments : 	      attr_doc = "";
	   private String reset;	   //column name : RESET, data type : VARCHAR2(44), comments : 	      reset = "";
	   private String set_property;	   //column name : SET_PROPERTY, data type : VARCHAR2(266), comments : 	      set_property = "";
	   private String get_property;	   //column name : GET_PROPERTY, data type : VARCHAR2(205), comments : 	      get_property = "";
	   private String data_type;	   //column name : DATA_TYPE, data type : VARCHAR2(106), comments : 	      data_type = "";
	   private long data_length;	   //column name : DATA_LENGTH, data type : NUMBER(0,22), comments : 	      data_length = 0;
	   private String constraints;
	   private String constraint_type;
	   private String table_name;	   //column name : TABLE_NAME, data type : VARCHAR2(30), comments : 	      table_name = "";
	   private String table_comments;	   //column name : TABLE_COMMENTS, data type : VARCHAR2(4000), comments : 	      table_comments = "";
	   private String ascol;	   //column name : ASCOL, data type : VARCHAR2(62), comments : 	      ascol = "";
	   private String colcoma;	   //column name : COLCOMA, data type : VARCHAR2(31), comments : 	      colcoma = "";
	   private String colequal;	   //column name : COLEQUAL, data type : VARCHAR2(33), comments : 	      colequal = "";
	   private String mybcol;	   //column name : MYBCOL, data type : VARCHAR2(33), comments : 	      mybcol = "";
	   private String entcol;	   //column name : ENTCOL, data type : VARCHAR2(40), comments : 	      entcol = "";
	   private String column_name;
	   
	   public VtDtoDTO(){
		   this.reset();
	   }
	   
	   private void reset(){
		      var = "";
		      attr_doc = "";
		      reset = "";
		      set_property = "";
		      get_property = "";
		      data_type = "";
		      data_length = 0;
		      constraints = "";
		      constraint_type = "";
		      table_name = "";
		      table_comments = "";
		      ascol = "";
		      colcoma = "";
		      colequal = "";
		      mybcol = "";
		      entcol = "";
		      column_name="";

	   }

	public void setVar(String var) {
		this.var = var;
	}

	public String getVar() {
		return var;
	}

	public void setAttr_doc(String attr_doc) {
		this.attr_doc = attr_doc;
	}

	public String getAttr_doc() {
		return attr_doc;
	}

	public void setReset(String reset) {
		this.reset = reset;
	}

	public String getReset() {
		return reset;
	}

	public void setSet_property(String set_property) {
		this.set_property = set_property;
	}

	public String getSet_property() {
		return set_property;
	}

	public void setGet_property(String get_property) {
		this.get_property = get_property;
	}

	public String getGet_property() {
		return get_property;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_length(long data_length) {
		this.data_length = data_length;
	}

	public long getData_length() {
		return data_length;
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_comments(String table_comments) {
		this.table_comments = table_comments;
	}

	public String getTable_comments() {
		return table_comments;
	}

	public void setAscol(String ascol) {
		this.ascol = ascol;
	}

	public String getAscol() {
		return ascol;
	}

	public void setColcoma(String colcoma) {
		this.colcoma = colcoma;
	}

	public String getColcoma() {
		return colcoma;
	}

	public void setColequal(String colequal) {
		this.colequal = colequal;
	}

	public String getColequal() {
		return colequal;
	}

	public void setMybcol(String mybcol) {
		this.mybcol = mybcol;
	}

	public String getMybcol() {
		return mybcol;
	}

	public void setEntcol(String entcol) {
		this.entcol = entcol;
	}

	public String getEntcol() {
		return entcol;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}
	
}
