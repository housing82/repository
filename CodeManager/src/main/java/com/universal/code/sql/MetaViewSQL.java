package com.universal.code.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.utils.StringUtil;

public class MetaViewSQL {

	private final static Logger logger = LoggerFactory.getLogger(MetaViewSQL.class);
	
	public static String getTableListSQL(String inTables){
		
		StringBuffer schemaSql = new StringBuffer();
		
		schemaSql.append("SELECT * FROM ( \n");
		schemaSql.append("SELECT /*+ NO_MERGE */ \n"); 
		schemaSql.append("     TABLE_NAME \n");
		schemaSql.append("    ,TABLE_TYPE \n");
		schemaSql.append("    ,TABLE_COMMENTS \n");
		schemaSql.append("FROM \n"); 
		schemaSql.append("    VT_SCHEMA \n");
		schemaSql.append("WHERE 1=1 \n");
		schemaSql.append("	AND TABLE_TYPE = 'TABLE' \n");
		if(inTables != null && inTables.length() > 0) {
			schemaSql.append("	AND TABLE_NAME in ( "+inTables+" ) \n");
		}		
		schemaSql.append("GROUP BY \n"); 
		schemaSql.append("    TABLE_NAME, TABLE_TYPE, TABLE_COMMENTS \n");

		schemaSql.append(")");
		schemaSql.append("ORDER BY \n"); 
		schemaSql.append("    TABLE_NAME \n");
		
		logger.debug("\n"+schemaSql.toString());
		return schemaSql.toString();
	}
	
	public static String getForgeinColumn() {
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT \n");
		sql.append("	AA.TABLE_NAME, \n");
		sql.append("	AA.CONSTRAINT_TYPE, \n");
		sql.append("	AA.CONSTRAINT_NAME, \n");
		sql.append("	AA.COLUMN_NAME, \n");
		sql.append("	AA.POSITION, \n");
		sql.append("	AA.R_OWNER, \n");
		sql.append("	AA.R_CONSTRAINT_NAME, \n");
		sql.append("	BB.TABLE_NAME AS FK_TABLE_NAME , \n");
		sql.append("	BB.COLUMN_NAME AS FK_COLUMN_NAME \n");
		sql.append("FROM \n");
		sql.append("	(SELECT  \n");
		sql.append("		A.TABLE_NAME , \n");
		sql.append("		A.CONSTRAINT_TYPE , \n");
		sql.append("		A.CONSTRAINT_NAME , \n");
		sql.append("		B.COLUMN_NAME , \n");
		sql.append("		B.POSITION , \n");
		sql.append("		A.R_OWNER , \n");
		sql.append("		A.R_CONSTRAINT_NAME \n");
		sql.append("	FROM  \n");
		sql.append("		USER_CONSTRAINTS A , \n");
		sql.append("		USER_CONS_COLUMNS B \n");
		sql.append("	WHERE 1=1 \n");
		//sql.append("		A.OWNER IN ('PMS') \n");
		sql.append("		AND A.CONSTRAINT_TYPE IN ('R') \n");
		sql.append("		AND A.OWNER = B.OWNER \n");
		sql.append("		AND A.CONSTRAINT_NAME = B.CONSTRAINT_NAME \n");
		sql.append("	)AA \n");
		sql.append("	,(SELECT  \n");
		sql.append("		A.TABLE_NAME , \n");
		sql.append("		A.CONSTRAINT_NAME , \n");
		sql.append("		B.COLUMN_NAME \n");
		sql.append("	FROM  \n");
		sql.append("		USER_CONSTRAINTS A , \n");
		sql.append("		USER_CONS_COLUMNS B \n");
		sql.append("	WHERE 1=1 \n");
		//sql.append("		A.OWNER IN ('PMS') \n");
		sql.append("		AND A.OWNER = B.OWNER \n");
		sql.append("		AND A.CONSTRAINT_NAME = B.CONSTRAINT_NAME \n");
		sql.append("	)BB \n");
		sql.append("WHERE AA.R_CONSTRAINT_NAME = BB.CONSTRAINT_NAME AND AA.COLUMN_NAME = BB.COLUMN_NAME \n");
		sql.append("ORDER BY AA.TABLE_NAME, AA.CONSTRAINT_NAME, AA.POSITION \n");
		
		logger.debug(sql.toString());
		
		return sql.toString();
	}

	public static String getColumnListSQL(String inTables, boolean mergeTable){

		StringBuffer schemaSql = new StringBuffer();
		
		if(inTables != null && inTables.equals("PrimaryKey")) {
		
			schemaSql.append("\n SELECT max(table_name)   table_name, max(table_comments)  table_comments"
					+ "\n , max(table_type)  table_type, column_name, max(data_type)  data_type"
					+ "\n , max(data_length)  data_length, ''  data_default, max(nullable)  nullable"
					+ "\n , max(constraints)  constraints, max(comments)  comments"
					+ "\n , max(data_full_type)  data_full_type, max(java_type)  java_type"
					+ "\n , max(data_scale)  data_scale"
					+ "\n , max(column_id) column_id from VT_SCHEMA "); 
			schemaSql.append("\n WHERE constraints LIKE '%' || ? || '%' GROUP BY column_name");
		}
		else if(StringUtil.isNotEmpty(inTables)) {
			schemaSql.append("SELECT \n"); 
			schemaSql.append("       table_name  \n");
			schemaSql.append("     , table_comments  \n");
			schemaSql.append("     , table_type  \n");
			schemaSql.append("     , column_name  \n");
			schemaSql.append("     , data_type  \n");
			schemaSql.append("     , data_length  \n");
			schemaSql.append("     , data_default  \n");
			schemaSql.append("     , nullable  \n");
			schemaSql.append("     , constraints  \n");
			schemaSql.append("     , indexes  \n");
			schemaSql.append("     , comments  \n");
			schemaSql.append("     , data_full_type  \n");
			schemaSql.append("     , java_type  \n");
			schemaSql.append("     , data_scale  \n");
			schemaSql.append("     , column_id  \n");
			schemaSql.append("FROM \n"); 
			schemaSql.append("    VT_SCHEMA \n");			
			schemaSql.append("WHERE \n"); 
			if(mergeTable) {
				schemaSql.append("	  table_name in ( "+inTables+" ) \n");
			}
			else {
				schemaSql.append("    table_name = ? \n");
			}
			//schemaSql.append("    AND ((constraints NOT LIKE '%' || ? || '%' AND constraints NOT LIKE '%' || ? || '%') \n");
			//schemaSql.append("    OR constraints is null) \n");
		}
		
		logger.debug(schemaSql.toString());
		return schemaSql.toString();
	}
	
}
