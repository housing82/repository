DROP VIEW VT_SCHEMA;

/* Formatted on 2017-06-05 ¿ÀÈÄ 4:17:38 (QP5 v5.115.810.9015) */
CREATE OR REPLACE FORCE VIEW VT_SCHEMA 
AS

WITH USER_CONSTRAINT
       AS (  SELECT /*+ NO_MERGE MATERIALIZED */
                      --CONS.OWNER,
                      CONS.TABLE_NAME,
                      COLS.COLUMN_NAME,
                      SUBSTR (
                         XMLAGG (
                            XMLELEMENT (
                               X,
                               ',',
                                  CONS.CONSTRAINT_NAME
                               || '('
                               || CONS.CONSTRAINT_TYPE
                               || ')'
                            )
                               ORDER BY CONS.CONSTRAINT_NAME
                         ).EXTRACT ('//text()'),
                         2
                      )
                         CONSTRAINTS
               /* P : PRIMARY KEY, R : FOREIGN KEY, U : UNIQUE, C : CHECK OR NOT NULL */
               FROM   USER_CONSTRAINTS CONS, USER_CONS_COLUMNS COLS
              WHERE   CONS.CONSTRAINT_NAME = COLS.CONSTRAINT_NAME
                      AND CONS.OWNER = COLS.OWNER
           GROUP BY   
           		--CONS.OWNER,
            	CONS.TABLE_NAME, COLS.COLUMN_NAME),
    USER_INDEX
       AS (  SELECT   
       								--I.TABLE_OWNER,
                      C.TABLE_NAME,
                      C.COLUMN_NAME,
                      SUBSTR (
                         XMLAGG (
                            XMLELEMENT (
                               X,
                               ',',
                               C.INDEX_NAME || '(' || I.UNIQUENESS || ')'
                            )
                               ORDER BY C.COLUMN_POSITION
                         ).EXTRACT ('//text()'),
                         2
                      )
                         INDEXES
               FROM   USER_INDEXES I, USER_IND_COLUMNS C
              WHERE   I.INDEX_NAME = C.INDEX_NAME
                      AND I.TABLE_NAME = C.TABLE_NAME
           GROUP BY   
           		--I.TABLE_OWNER,
              C.TABLE_NAME, C.COLUMN_NAME)
  SELECT                                                          /*+ CACHE */
                                                                    --C.OWNER,
           C.TABLE_NAME,
           T.TABLE_TYPE,
           T.COMMENTS AS TABLE_COMMENTS,
           C.COLUMN_NAME,
           C.DATA_TYPE,
           C.DATA_LENGTH,
           NVL (C.DATA_DEFAULT, '') DATA_DEFAULT,
           --FN_DATA_DEF2CHAR (C.TABLE_NAME, C.COLUMN_NAME, 'USER_TAB_COLUMNS') DATA_DEFAULT_VAR2,
           C.NULLABLE,
           CT.CONSTRAINTS,
           UI.INDEXES,
           M.COMMENTS,
           C.DATA_TYPE || '('
           || DECODE (
                 C.DATA_TYPE,
                 'NUMBER',
                    DECODE (C.DATA_SCALE, NULL, '0', C.DATA_SCALE)
                 || ','
                 || C.DATA_LENGTH,
                 'INTEGER',
                    DECODE (C.DATA_SCALE, NULL, '0', C.DATA_SCALE)
                 || ','
                 || C.DATA_LENGTH,
                 'FLOAT',
                    DECODE (C.DATA_SCALE, NULL, '0', C.DATA_SCALE)
                 || ','
                 || C.DATA_LENGTH,
                 'DOUBLE',
                    DECODE (C.DATA_SCALE, NULL, '0', C.DATA_SCALE)
                 || ','
                 || C.DATA_LENGTH,
                 DATA_LENGTH
              )
           || ')'
              DATA_FULL_TYPE,
           DECODE (C.DATA_TYPE,
                   'NUMBER',
                   CASE WHEN C.DATA_LENGTH > 9 THEN 'Long' ELSE 'Integer' END,
                   'INTEGER',
                   'Integer',
                   'FLOAT',
                   'Float',
                   'DOUBLE',
                   'Double',
                   'NVARCHAR2',
                   'String',
                   'VARCHAR2',
                   'String',
                   'CHAR',
                   'String',
                   'LONG',
                   'String',
                   'CLOB',
                   'String',
                   'BLOB',
                   'BLOB',
                   'DATE',
                   'String',
                   'TIMESTAMP',
                   'String',
                   'String')
              JAVA_TYPE,
           C.DATA_SCALE,
           C.COLUMN_ID
    FROM   USER_TAB_COMMENTS T,
           USER_TAB_COLUMNS C,
           USER_COL_COMMENTS M,
           USER_CONSTRAINT CT,
           USER_INDEX UI
   WHERE       
   				--T.OWNER = C.OWNER
          --AND C.OWNER = M.OWNER AND
          T.TABLE_NAME = C.TABLE_NAME
          AND C.TABLE_NAME = M.TABLE_NAME
          AND C.COLUMN_NAME = M.COLUMN_NAME
          --AND C.OWNER = CT.OWNER(+)
          AND C.TABLE_NAME = CT.TABLE_NAME(+)
          AND C.COLUMN_NAME = CT.COLUMN_NAME(+)
          --AND C.OWNER = UI.TABLE_OWNER(+)
          AND C.TABLE_NAME = UI.TABLE_NAME(+)
          AND C.COLUMN_NAME = UI.COLUMN_NAME(+)
ORDER BY  
					--C.OWNER,
          C.TABLE_NAME, C.COLUMN_ID;