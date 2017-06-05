package com.universal.code.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;

public class MetaViewSQL {

	private final static Logger logger = LoggerFactory.getLogger(MetaViewSQL.class);
	
	public static String getVtSchema(String inTables) {
		StringBuffer schemaSql = new StringBuffer();
		
		schemaSql.append("WITH").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("USER_CONSTRAINT AS (").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("	SELECT /*+ NO_MERGE MATERIALIZED */").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("  	--CONS.OWNER,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	CONS.TABLE_NAME,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	COLS.COLUMN_NAME,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	SUBSTR (").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	   XMLAGG (").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	      XMLELEMENT (").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	         X,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	         ',',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	            CONS.CONSTRAINT_NAME").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	         || '('").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	         || CONS.CONSTRAINT_TYPE").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	         || ')'").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	      )").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	         ORDER BY CONS.CONSTRAINT_NAME").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	   ).EXTRACT ('//text()'),").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	   2").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("		) CONSTRAINTS").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  /* P : PRIMARY KEY, R : FOREIGN KEY, U : UNIQUE, C : CHECK OR NOT NULL */").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  FROM   USER_CONSTRAINTS CONS, USER_CONS_COLUMNS COLS").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  WHERE   CONS.CONSTRAINT_NAME = COLS.CONSTRAINT_NAME AND CONS.OWNER = COLS.OWNER").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  GROUP BY").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("  	--CONS.OWNER,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    CONS.TABLE_NAME, COLS.COLUMN_NAME),").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("USER_INDEX AS (").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("	SELECT").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("		--I.TABLE_OWNER,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	C.TABLE_NAME,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    C.COLUMN_NAME,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    SUBSTR (").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("       XMLAGG (").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          XMLELEMENT (").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("             X,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("             ',',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("             C.INDEX_NAME || '(' || I.UNIQUENESS || ')'").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          )").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("             ORDER BY C.COLUMN_POSITION").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("       ).EXTRACT ('//text()'),").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("       2").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    )INDEXES").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("	FROM   USER_INDEXES I, USER_IND_COLUMNS C").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  WHERE   I.INDEX_NAME = C.INDEX_NAME AND I.TABLE_NAME = C.TABLE_NAME").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  GROUP BY").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("     	--I.TABLE_OWNER,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	C.TABLE_NAME, C.COLUMN_NAME)").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  SELECT").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	/*+ CACHE */").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("    --C.OWNER,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    C.TABLE_NAME,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    T.TABLE_TYPE,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    T.COMMENTS AS TABLE_COMMENTS,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    C.COLUMN_NAME,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    C.DATA_TYPE,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    C.DATA_LENGTH,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    NVL (C.DATA_DEFAULT, '') DATA_DEFAULT,").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("    FN_DATA_DEF2CHAR(C.TABLE_NAME, C.COLUMN_NAME, 'USER_TAB_COLUMNS') DATA_DEFAULT_VAR2,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    C.NULLABLE,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    CT.CONSTRAINTS,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    UI.INDEXES,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    M.COMMENTS,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    C.DATA_TYPE || '('").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    || DECODE (").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          C.DATA_TYPE,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          'NUMBER',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("             DECODE (C.DATA_SCALE, NULL, '0', C.DATA_SCALE)").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          || ','").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          || C.DATA_LENGTH,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          'INTEGER',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("             DECODE (C.DATA_SCALE, NULL, '0', C.DATA_SCALE)").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          || ','").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          || C.DATA_LENGTH,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          'FLOAT',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("             DECODE (C.DATA_SCALE, NULL, '0', C.DATA_SCALE)").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          || ','").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          || C.DATA_LENGTH,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          'DOUBLE',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("             DECODE (C.DATA_SCALE, NULL, '0', C.DATA_SCALE)").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          || ','").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          || C.DATA_LENGTH,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("          DATA_LENGTH").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("       )").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    || ')'").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    DATA_FULL_TYPE,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    DECODE (C.DATA_TYPE,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'NUMBER',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            CASE WHEN C.DATA_LENGTH > 9 THEN 'BigDecimal' ELSE 'Integer' END,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'INTEGER',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'Integer',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'FLOAT',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'Float',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'DOUBLE',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'Double',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'NVARCHAR2',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'String',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'VARCHAR2',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'String',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'CHAR',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'String',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'LONG',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'String',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'CLOB',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'String',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'BLOB',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'BLOB',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'DATE',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'String',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'TIMESTAMP',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'String',").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("            'String')").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    JAVA_TYPE,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    C.DATA_SCALE,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    C.COLUMN_ID").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("	FROM").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("		USER_TAB_COMMENTS T,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    USER_TAB_COLUMNS C,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    USER_COL_COMMENTS M,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    USER_CONSTRAINT CT,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    USER_INDEX UI").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("	WHERE").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("		--T.OWNER = C.OWNER").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("    	--AND C.OWNER = M.OWNER AND").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    	T.TABLE_NAME = C.TABLE_NAME").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    	AND C.TABLE_NAME = M.TABLE_NAME").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    	AND C.COLUMN_NAME = M.COLUMN_NAME").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("    	--AND C.OWNER = CT.OWNER(+)").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    	AND C.TABLE_NAME = CT.TABLE_NAME(+)").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    	AND C.COLUMN_NAME = CT.COLUMN_NAME(+)").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("    	--AND C.OWNER = UI.TABLE_OWNER(+)").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    	AND C.TABLE_NAME = UI.TABLE_NAME(+)").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("    	AND C.COLUMN_NAME = UI.COLUMN_NAME(+)").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("	ORDER BY").append(SystemUtil.LINE_SEPARATOR);
		//schemaSql.append("		--C.OWNER,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	C.TABLE_NAME,").append(SystemUtil.LINE_SEPARATOR);
		schemaSql.append("  	C.COLUMN_ID;").append(SystemUtil.LINE_SEPARATOR);
		
		return schemaSql.toString();
	}
	
	public static String getTableListSQL(String inTables){
		
		StringBuffer schemaSql = new StringBuffer();
		
		schemaSql.append("SELECT * FROM ( \n");
		schemaSql.append("SELECT /*+ NO_MERGE */ \n"); 
		schemaSql.append("     TABLE_NAME \n");
		schemaSql.append("    ,TABLE_TYPE \n");
		schemaSql.append("    ,TABLE_COMMENTS \n");
		schemaSql.append("FROM \n"); 
		
		schemaSql.append("    ( \n");
		schemaSql.append(getVtSchema(null));
		schemaSql.append("    ) \n");
		
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
					+ "\n , max(column_id) column_id from ");
			schemaSql.append("    ( \n");
			schemaSql.append(getVtSchema(null));
			schemaSql.append("    ) \n");
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
			
			schemaSql.append("    ( \n");
			schemaSql.append(getVtSchema(null));
			schemaSql.append("    ) \n");
			
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
