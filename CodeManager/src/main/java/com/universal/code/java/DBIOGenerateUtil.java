package com.universal.code.java;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.coder.URLCoder;
import com.universal.code.constants.IOperateCode;
import com.universal.code.database.JDBCManager;
import com.universal.code.dto.ForeignInfoDTO;
import com.universal.code.dto.TableDTO;
import com.universal.code.dto.VtSchemaDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.java.sql.MetaViewSQL;
import com.universal.code.utils.DateUtil;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;

public class DBIOGenerateUtil {

	private final static Logger logger = LoggerFactory.getLogger(DBIOGenerateUtil.class);
	
	// BXM ECLIPSE HD SOURCE ROOT
	private String sourceRoot;
	// ASIS DAO DTO BASE PACKAGE
	private String javaPackage; 
	// Database Connection infomation
	private Properties databaseConfig;
	//datasourceName
	private String datasourceName;
	//default datasource name
	private final static String DEFAULT_DATASOURCE_NAME;
	// target tables to sql in expression 
	// 특정 지정 테이블 없을경우 셋팅안함
	// ex) "TABLE1", "TABLE2", "TABLE3"
	private String targetTables;
	// Create OMM File 
	private boolean createFile;
	
	private JDBCManager jdbcManager;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet results;
	private StringUtil stringUtil;
	private FileUtil fileUtil;
	private GenerateHelper generateHelper;
	
	public DBIOGenerateUtil() {
		jdbcManager = new JDBCManager();
		stringUtil = new StringUtil();
		fileUtil = new FileUtil();
		generateHelper = new GenerateHelper();
	}
	
	private static final String rvMethodDescription = "#{rvMethodDescription}"; //인터페이스 메소드 주석 설명
	private static final String rvTestValues = "#{rvTestValues}";				//인터페이스 메소드 주석 테스트 값
	private static final String rvLogicalName = "#{rvLogicalName}";				//인터페이스 메소드 어노테이션 로지컬 명
	private static final String rvDescription = "#{rvDescription}";				//인터페이스 메소드 어노테이션 설명
	private static final String rvOutputType = "#{rvOutputType}"; 				//인터페이스 메소드 결과 타입
	private static final String rvMethodName = "#{rvMethodName}";				//인터페이스 메소드 명
	private static final String rvInputType = "#{rvInputType}"; 				//인터페이스 메소드 입력 타입
	private static final String rvInputVariable = "#{rvInputVariable}";			//인터페이스 메소드 입력 타입 변수 명
	private static final String rvPackage = "#{rvPackage}";						//인터페이스 패키지
	private static final String rvMapper = "#{rvMapper}";						//인터페이스 어노테이션 mapper
	private static final String rvDatasource = "#{rvDatasource}";				//인터페이스 어노테이션 datasource
	private static final String rvClassName = "#{rvClassName}";					//인터페이스 클래스 명
	private static final String rvBody = "#{rvBody}";							//인터페이스 & XML 내용
	private static final String rvSql = "#{rvSql}";								//XML 노드별 SQL 내용
	private static final String rvNamespace = "#{rvNamespace}";					//XML 네임스페이스
	private static final String rvDate = "#{rvDate}";							//생성일자
	
	private static Map<String, String> methodMap = new LinkedHashMap<String, String>();
	
	private static String templatePath;
	
	
	private static String CONSTRAINT_CHECK;
	private static String CONSTRAINT_PRIMARY;
	private static String CONSTRAINT_UNIQUE;
	private static String CONSTRAINT_FOREIGN;
	private static String CONSTRAINT_CASE_PRIMARY;
	private static String CONSTRAINT_CASE_UNIQUE;
	private static String CONSTRAINT_CASE_FOREIGN;
	
	static {
		methodMap.put("insert", "등록");
		methodMap.put("select", "단건조회");
		methodMap.put("selectList", "목록조회");
		methodMap.put("update", "수정");
		methodMap.put("merge", "병합");
		methodMap.put("delete", "삭제");
		//템플릿 패스
		templatePath = URLCoder.getInstance().getURLDecode(DBIOGenerateUtil.class.getResource(".").getPath().concat("template").concat(File.separator), ""); 
		//기본 데이터소스명
		DEFAULT_DATASOURCE_NAME = "MainDS";
		
		/*
			C (CHECK) 
			P (PRIMARY KEY) 
			U (UNIQUE) 
			R (FOREIGN KEY) 
		 */
		
		CONSTRAINT_CHECK = "(C)";
		CONSTRAINT_PRIMARY = "(P)";
		CONSTRAINT_UNIQUE = "(U)";
		CONSTRAINT_FOREIGN = "(R)";
		CONSTRAINT_CASE_PRIMARY = "PK_";
		CONSTRAINT_CASE_UNIQUE = "UK_";
		CONSTRAINT_CASE_FOREIGN = "FK_";
	}
	
	
	
	
	public void execute() {
		logger.debug("[START] execute: {}", getDatabaseConfig());
		logger.debug("★ SourceRoot: {}", getSourceRoot());
		logger.debug("★ JavaPackage: {}", getJavaPackage());
		
		
		logger.debug("url: {}", templatePath);
		
		String javaCrudTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("java.crud.template.dbio"));
		String javaSelectListTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("java.selectList.template.dbio"));
		String javaTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("java.template.dbio"));
		String xmlDeleteTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("xml.delete.template.dbio"));
		String xmlInsertTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("xml.insert.template.dbio"));
		String xmlSelectTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("xml.select.template.dbio"));
		String xmlSelectListTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("xml.selectList.template.dbio"));
		String xmlTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("xml.template.dbio"));
		String xmlUpdateMergeTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("xml.update.merge.template.dbio"));
		
		
		logger.debug("javaCrudTemplateDbio:\n {}", javaCrudTemplateDbio);
		logger.debug("javaSelectListTemplateDbio:\n {}", javaSelectListTemplateDbio);
		logger.debug("javaTemplateDbio:\n {}", javaTemplateDbio);
		logger.debug("xmlDeleteTemplateDbio:\n {}", xmlDeleteTemplateDbio);
		logger.debug("xmlInsertTemplateDbio:\n {}", xmlInsertTemplateDbio);
		logger.debug("xmlSelectTemplateDbio:\n {}", xmlSelectTemplateDbio);
		logger.debug("xmlSelectListTemplateDbio:\n {}", xmlSelectListTemplateDbio);
		logger.debug("xmlTemplateDbio:\n {}", xmlTemplateDbio);
		logger.debug("xmlUpdateMergeTemplateDbio:\n {}", xmlUpdateMergeTemplateDbio);
		
		/*
		private static final String rvMethodDescription = "#{rvMethodDescription}"; //인터페이스 메소드 주석 설명
		private static final String rvTestValues = "#{rvTestValues}";				//인터페이스 메소드 주석 테스트 값
		private static final String rvLogicalName = "#{rvLogicalName}";				//인터페이스 메소드 어노테이션 로지컬 명
		private static final String rvDescription = "#{rvDescription}";				//인터페이스 메소드 어노테이션 설명
		private static final String rvOutputType = "#{rvOutputType}"; 				//인터페이스 메소드 결과 타입
		private static final String rvMethodName = "#{rvMethodName}";				//인터페이스 메소드 명
		private static final String rvInputType = "#{rvInputType}"; 				//인터페이스 메소드 입력 타입
		private static final String rvInputVariable = "#{rvInputVariable}";			//인터페이스 메소드 입력 타입 변수 명
		private static final String rvPackage = "#{rvPackage}";						//인터페이스 패키지
		private static final String rvMapper = "#{rvMapper}";						//인터페이스 어노테이션 mapper
		private static final String rvDatasource = "#{rvDatasource}";				//인터페이스 어노테이션 datasource
		private static final String rvClassName = "#{rvClassName}";					//인터페이스 클래스 명
		private static final String rvBody = "#{rvBody}";							//인터페이스 & XML 내용
		private static final String rvSql = "#{rvSql}";								//XML 노드별 SQL 내용
		private static final String rvNamespace = "#{rvNamespace}";					//XML 네임스페이스
		*/
		
		List<TableDTO> tableList = null;
		List<VtSchemaDTO> columnList = null;
		List<ForeignInfoDTO> foreignColumnList = null;
		StringBuilder javaStrbd = null;
		StringBuilder xmlStrbd = null;
		String fileName = null;
		String fieldName = null;
		
		String packages = getJavaPackage();
		String mapper = packages.replace(".", "/");
		String datasource = getDatasourceName(); // MainDS
		String logicalName = null;
		String dbioFileName = null;
		
		
		String dsMethodDescription = null; //인터페이스 메소드 주석 설명
		String dsTestValues = null;				//인터페이스 메소드 주석 테스트 값
		String dsLogicalName = null;				//인터페이스 메소드 어노테이션 로지컬 명
		String dsDescription = null;				//인터페이스 메소드 어노테이션 설명
		String dsOutputType = null; 				//인터페이스 메소드 결과 타입
		String dsMethodName = null;				//인터페이스 메소드 명
		String dsInputType = null; 				//인터페이스 메소드 입력 타입
		String dsInputVariable = null;			//인터페이스 메소드 입력 타입 변수 명
		String dsPackage = null;						//인터페이스 패키지
		String dsMapper = null;						//인터페이스 어노테이션 mapper
		String dsDatasource = null;				//인터페이스 어노테이션 datasource
		String dsClassName = null;					//인터페이스 클래스 명
		String dsBody = null;							//인터페이스 & XML 내용
		String dsSql = null;								//XML 노드별 SQL 내용
		String dsNamespace = null;					//XML 네임스페이스
		String dsDate = null;				// 생성 일시
		
		String methodCode = null;
		String sqlCode = null;
		
		StringBuilder testValues = null;
		
		StringBuilder javaBd = null;
		StringBuilder xmlBd = null;
		String javaFinal = null;
		String xmlFinal = null;
		
		try {
			conn = jdbcManager.getConnection(getDatabaseConfig());
			logger.debug("conn: {}", conn);
			// SELECT DATABASE
			tableList = getTableList(conn, getTargetTables());
			// currentTableName테이블의 참조키 컬럼정보
			foreignColumnList = getForgeinColumn(conn);
			int fileSeq = 0;
			String currentTableName = null;
			//테이블 목록
			for(TableDTO table : tableList) {
				
				if(currentTableName != null && !table.getTableName().equals(currentTableName)) {
					fileSeq = 1;
				}
				else {
					fileSeq++;
				}
				currentTableName = table.getTableName();

				// dbio 파일명 명명규칙: ‘D’ + 테이블명 + 일련번호(2자리) -> DAO동사 + 약어명 + “01” ~ “99”
				fileName = "D".concat(stringUtil.getFirstCharUpperCase(stringUtil.getCamelCaseString(currentTableName))).concat(stringUtil.leftPad(Integer.toString(fileSeq), 2, "0"));
				// currentTableName테이블의 컬럼목록
				columnList = getColumnList(conn, currentTableName, false);
				
				/**********************
				 * START MYBATIS INTERFACE JAVA
				 */
				// 메소드 주석에 사용할 테이블 코멘트
				dsDescription = StringUtil.NVL(table.getTableComments(), currentTableName);
				dsPackage = getJavaPackage();
				dsDate = DateUtil.getFastDate(DateUtil.DEF_DATE_FORMAT);
				dsMapper = new StringBuilder().append(getJavaPackage().replace(".", "/")).append(fileName.concat(".dbio")).toString();
				dsDatasource = getDatasourceName();
				dsLogicalName = dsDescription;
				dsClassName = fileName;
				
				//마이바티스 인터페이스 자바 메인템플릿
				javaFinal = javaTemplateDbio
						.replace(rvDate, dsDate)
						.replace(rvPackage, dsPackage)
						.replace(rvMapper, dsMapper)
						.replace(rvDatasource, dsDatasource)
						.replace(rvLogicalName, dsLogicalName)
						.replace(rvDescription, dsDescription)
						.replace(rvClassName, dsClassName);
						
				dsNamespace = getJavaPackage().concat(".").concat(fileName);
				
				//마이바티스 인터페이스 메퍼XML 메인템플릿
				xmlFinal = xmlTemplateDbio
						.replace(rvNamespace, dsNamespace)
						.replace(rvDate, dsDate);
				
				logger.debug("DAO File Name : {}, comment : {}", fileName, dsDescription);
				logger.debug("TableName : {}, TableComment : {}", currentTableName, table.getTableComments());
				// logger.debug("[javaFinal]\n{}", javaFinal);
				
				//메소드 주석의 테스트코드라인
				testValues = new StringBuilder();
				//컬럼목록
				for(VtSchemaDTO vtSchema : columnList) {
					fieldName = generateHelper.getCamelCaseFieldName(vtSchema.getColumn_name());
					testValues.append(fieldName).append("=; ");
				}
				dsTestValues = testValues.toString();
				
				//자바 인터페이스
				javaBd = new StringBuilder();
				//마이바티스 XML
				xmlBd = new StringBuilder();
				
				// 메소드 코드
				for(Entry<String, String> entry : methodMap.entrySet()) {
					
					//마이바티스 인터페이스 (자바)
					dsMethodName = entry.getKey().concat(stringUtil.getFirstCharUpperCase(stringUtil.getCamelCaseString(currentTableName)));
					dsMethodDescription = dsDescription.concat(" ").concat(entry.getValue());
					dsLogicalName = dsMethodDescription; 
					dsOutputType = getJavaPackage().concat(".dto.").concat(fileName).concat("IO");
					dsInputType = getJavaPackage().concat(".dto.").concat(fileName).concat("IO");
					dsInputVariable = stringUtil.getFirstCharLowerCase(fileName.concat("IO"));
					
					if(entry.getKey().equals("selectList" )) {
						// selectList
						methodCode = javaSelectListTemplateDbio;
					}
					else {
						// crud & merge
						methodCode = javaCrudTemplateDbio;
						
						if( entry.getKey().equals("insert") 
							|| entry.getKey().equals("update") 
							|| entry.getKey().equals("merge")
						 	|| entry.getKey().equals("delete")
						 ) {
							dsOutputType = "int";
						}
					}
					
					javaBd.append(methodCode
						.replace(rvMethodName, dsMethodName)
						.replace(rvMethodDescription, dsMethodDescription)
						.replace(rvLogicalName, dsLogicalName)
						.replace(rvDescription, dsLogicalName)
						.replace(rvOutputType, dsOutputType)
						.replace(rvInputType, dsInputType)
						.replace(rvInputVariable, dsInputVariable)
						.replace(rvTestValues, dsTestValues)
					);
					
					javaBd.append(SystemUtil.LINE_SEPARATOR);
					
					//마이바티스 SQL (XML)
					
					/*
					methodMap.put("insert", "등록");
					methodMap.put("select", "단건조회");
					methodMap.put("selectList", "목록조회");
					methodMap.put("update", "수정");
					methodMap.put("merge", "병합");
					methodMap.put("delete", "삭제");
					
					
					xmlDeleteTemplateDbio
					xmlInsertTemplateDbio
					xmlSelectTemplateDbio
					xmlSelectListTemplateDbio
					xmlTemplateDbio
					xmlUpdateMergeTemplateDbio

					*/
					if(entry.getKey().equals("insert")) {
						sqlCode = xmlInsertTemplateDbio;
						
						dsSql = insert(columnList); // <-- SQL
					}
					else if(entry.getKey().equals("select")) {
						sqlCode = xmlSelectTemplateDbio;
						
						dsSql = select(tableList, columnList, foreignColumnList); // <-- SQL
					}
					else if(entry.getKey().equals("selectList")) {
						sqlCode = xmlSelectListTemplateDbio;
						
						dsSql = selectList(tableList, columnList, foreignColumnList); // <-- SQL
					}
					else if(entry.getKey().equals("update")) {
						sqlCode = xmlUpdateMergeTemplateDbio;
						
						dsSql = update(columnList); // <-- SQL
					}
					else if(entry.getKey().equals("merge")) {
						sqlCode = xmlUpdateMergeTemplateDbio;
						
						dsSql = ""; // <-- SQL
					}
					else if(entry.getKey().equals("delete")) {
						sqlCode = xmlDeleteTemplateDbio;
						
						dsSql = delete(columnList); // <-- SQL
					}
					else {
						throw new ApplicationException("스테이트먼트가 잘못되었거나 설정된 스테이트먼트가 아닙니다.");
					}
					
					/***********************************
					 * 테이블당 스테이트먼트에 맞는 SQL생성
					 */

					
					xmlBd.append(sqlCode
						.replace(rvMethodName, dsMethodName)
						.replace(rvInputType, dsInputType)
						.replace(rvOutputType, dsOutputType)
						.replace(rvSql, dsSql)
					);
					 
					xmlBd.append(SystemUtil.LINE_SEPARATOR);
				}
				// 인터페이스 최종 코드
				javaFinal = javaFinal.replace(rvBody, javaBd.toString());
				// 메퍼 최종 코드
				xmlFinal = xmlFinal.replace(rvBody, xmlBd.toString());
				
				
				logger.debug("[javaFinal]\n{}", javaFinal);
				logger.debug("[xmlFinal]\n{}", xmlFinal);
				logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
				
				if(isCreateFile()) {
					
					fileUtil.mkfile(
							new StringBuilder().append(sourceRoot).append("/").append(getJavaPackage().replace(".", "/")).toString()
							, fileName.concat(".java")
							, javaFinal
							, IOperateCode.DEFAULT_ENCODING_UTF8
							, false
							, true); 
					
					fileUtil.mkfile(
							new StringBuilder().append(sourceRoot).append("/").append(getJavaPackage().replace(".", "/")).toString()
							, fileName.concat(".dbio")
							, xmlFinal
							, IOperateCode.DEFAULT_ENCODING_UTF8
							, false
							, true); 					
				}
				
				logger.debug("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■ {}", SystemUtil.LINE_SEPARATOR);
			}
			
		}
		catch(Exception e) {
			throw new ApplicationException(e);
		}
		finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					throw new ApplicationException(e);
				}
			}
		}
	
		
		logger.debug("[END] execute");
	}
	
	public String insert(List<VtSchemaDTO> tableInfo){
		
		StringBuilder sql = new StringBuilder();
		sql.append("	INSERT INTO "+tableInfo.get(0).getTable_name()+"(	/* "+ StringUtil.NVL(tableInfo.get(0).getTable_comments(), tableInfo.get(0).getTable_name()) +" */" +"\n");
		int i = 0;
		for(VtSchemaDTO schema : tableInfo){   
			sql.append("		" + (i > 0 ? ",":" ") + schema.getColumn_name() + "	/* "+StringUtil.NVL(schema.getComments(), schema.getColumn_name())+", "+schema.getData_full_type()+" */" +"\n");
			i++;
		}
		sql.append("	) VALUES (" + "\n");
		
		i = 0;
		for(VtSchemaDTO schema : tableInfo){
			//TO_DATE("#{"+schema.getColumn_name()+"}", 'yyyy-MM-dd hh24:mi:ss')
			if(schema.getData_type().equals("DATE")) {
				sql.append("		" + (i > 0 ? ",":" ") + "TO_DATE(" + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + ", 'yyyy-MM-dd hh24:mi:ss')" +"\n");					
			}
			else {
				sql.append("		" + (i > 0 ? ",":" ") + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) +"\n");
			}
			
			i++;
		}
		
		sql.append("	)\n");
		
		logger.debug("\n" + sql.toString());

		return sql.toString();
	}
	
	public String update(List<VtSchemaDTO> tableInfo){
		
		StringBuilder sql = new StringBuilder();
		sql.append("	UPDATE "+tableInfo.get(0).getTable_name()+" SET	/* "+ StringUtil.NVL(tableInfo.get(0).getTable_comments(), tableInfo.get(0).getTable_name()) +" */" +"\n");

		int i = 0;
		for(VtSchemaDTO schema : tableInfo){

			//TO_DATE("#{"+schema.getColumn_name()+"}", 'yyyy-MM-dd hh24:mi:ss')
			if(schema.getData_type().equals("DATE")) {
				sql.append("		" + (i > 0 ? ",":" ") + schema.getColumn_name() + " = TO_DATE(" + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + ", 'yyyy-MM-dd hh24:mi:ss')" + "	/* "+StringUtil.NVL(schema.getComments(), schema.getColumn_name())+", "+schema.getData_full_type()+" */" +"\n");					
			}
			else {
				sql.append("		" + (i > 0 ? ",":" ") + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + "	/* "+StringUtil.NVL(schema.getComments(), schema.getColumn_name())+", "+schema.getData_full_type()+" */" +"\n");
			}
			
			i++;
		}
		
		sql.append("	WHERE 1=1" +"\n");
		
	
		for(VtSchemaDTO schema : tableInfo){
			if(schema.getConstraints() != null && (
				   schema.getConstraints().indexOf(CONSTRAINT_PRIMARY) > -1 
				|| schema.getConstraints().indexOf(CONSTRAINT_FOREIGN) > -1
				|| schema.getConstraints().startsWith(CONSTRAINT_CASE_PRIMARY) 
				|| schema.getConstraints().startsWith(CONSTRAINT_CASE_FOREIGN) 
			)) {
				sql.append("		AND " + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  + "\n");
			}
		}

		logger.debug("\n" + sql.toString());
		
		return sql.toString();
	}
	
	

	public String delete(List<VtSchemaDTO> tableInfo){
		

		StringBuilder sql = new StringBuilder();
		sql.append("	DELETE FROM "+tableInfo.get(0).getTable_name() + " /* "+ StringUtil.NVL(tableInfo.get(0).getTable_comments(), tableInfo.get(0).getTable_name()) +" */" +"\n");
		sql.append("	WHERE 1=1" +"\n");
		
		
		for(VtSchemaDTO schema : tableInfo){
			if(schema.getConstraints() != null && (
					   schema.getConstraints().indexOf(CONSTRAINT_PRIMARY) > -1 
					|| schema.getConstraints().indexOf(CONSTRAINT_FOREIGN) > -1
					|| schema.getConstraints().startsWith(CONSTRAINT_CASE_PRIMARY) 
					|| schema.getConstraints().startsWith(CONSTRAINT_CASE_FOREIGN) 
			)) {
				sql.append("		AND " + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  + "\n");
			}
		}
		
		logger.debug("\n" + sql.toString());
		
		return sql.toString();
	}
	
	
	//single table & single row select
	public String select(List<TableDTO> tableList, List<VtSchemaDTO> tableInfo, List<ForeignInfoDTO> forgeintables){
	
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT" +"\n");
		int i = 0;
		for(VtSchemaDTO schema : tableInfo){
			//TO_DATE("#{"+schema.getColumn_name()+"}", 'yyyy-MM-dd hh24:mi:ss')
			if(schema.getData_type().equals("DATE")) {
				sql.append("		" + (i > 0 ? ",":" ") + "TO_CHAR(" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + ", 'yyyy-MM-dd') AS " + generateHelper.getCamelCaseFieldName(schema.getColumn_name()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */" +"\n");
			}
			else {
				sql.append("		" + (i > 0 ? ",":" ") + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " AS " + generateHelper.getCamelCaseFieldName(schema.getColumn_name()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */" +"\n");
			}
			
			i++;
		}
		
		//foregin 정보추출 시작
		List<ForeignInfoDTO> foreginTables = findFKTables(tableInfo.get(0).getTable_name(), forgeintables, new ArrayList<ForeignInfoDTO>(), 0);
		
		//FROM 절 시작 ----------------------------------------
		sql.append("FROM" +"\n");
		i = 0;
		for(String tableName : getFKTable(foreginTables)) {
			sql.append("		" + (i > 0 ? ",":" ") + tableName + "	/* "+getTableComments(tableList, tableName)+" */" + "\n");
			i++;
		}
		if(foreginTables.size() == 0) {
			sql.append("		" + tableInfo.get(0).getTable_name() + "	/* "+getTableComments(tableList, tableInfo.get(0).getTable_name())+" */" + "\n");
		}
		//FROM 절 종료 ----------------------------------------
		
		sql.append("WHERE 1=1" +"\n");
		
		//JOIN 절 시작 ----------------------------------------
		for(ForeignInfoDTO fkTable : getFKTableJoinWhereSentence(foreginTables)){
			sql.append("	AND " + fkTable.getTable_name() + "." + fkTable.getColumn_name() + " = " + fkTable.getFk_table_name() + "." + fkTable.getFk_column_name() + "\n");
		}
		//JOIN 절 종료 ----------------------------------------
		
		i = 0;
		for(VtSchemaDTO schema : tableInfo){
			if(schema.getConstraints() != null && (
					   schema.getConstraints().indexOf(CONSTRAINT_PRIMARY) > -1 
					|| schema.getConstraints().indexOf(CONSTRAINT_FOREIGN) > -1
					|| schema.getConstraints().startsWith(CONSTRAINT_CASE_PRIMARY) 
					|| schema.getConstraints().startsWith(CONSTRAINT_CASE_FOREIGN) 
			)) {
				sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  + "\n");
				i++;
			}
			/*
			if(schema.getColumn_name().equals("REC_STAT")) {
				sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + "REC_STAT != 'D'"  + "\n");
			}
			*/
		}
		
		//JOIN TABLE REC_STAT 
		/*
		for(String tableName : getFKTable(foreginTables)) {
			if(!tableInfo.get(0).getTable_name().equals(tableName) && isTableIsRecStat(tableName)) {
				sql.append("		AND " + tableName + "." + "REC_STAT != 'D'"  + "\n");
			}
		}
		*/
		
		logger.debug("\n" + sql.toString());
		
		return sql.toString();
	}
	
	

	public String selectList(List<TableDTO> tableList, List<VtSchemaDTO> tableInfo, List<ForeignInfoDTO> forgeintables){
		
	
//		SELECT * FROM 
//		    ( SELECT ROWNUM AS ROW__NUM, A.* FROM 
//		        (
//		
//		/* #### Original SQL [[ ################# */
//		SELECT
//						 HD_CODE_ACNT.DEPT_CODE AS deptCode	/* 현장코드, VARCHAR2(12) */
//						,HD_CODE_ACNT.JCODE AS jcode	/* 내부코드, VARCHAR2(2) */
//						,HD_CODE_ACNT.JACNTCODE AS jacntcode	/* 계정코드, VARCHAR2(10) */
//						,HD_CODE_ACNT.JACNTNAME AS jacntname	/* 계정명칭, VARCHAR2(40) */
//						,HD_CODE_ACNT.DETAILCODE AS detailcode	/* 수지계정, VARCHAR2(10) */
//						,HD_CODE_ACNT.INPUT_DUTY_ID AS inputDutyId	/* 입력담당, VARCHAR2(12) */
//						,HD_CODE_ACNT.INPUT_DATE AS inputDate	/* 입력일시, VARCHAR2(14) */
//						,HD_CODE_ACNT.CHG_DUTY_ID AS chgDutyId	/* 변경담당, VARCHAR2(12) */
//						,HD_CODE_ACNT.CHG_DATE AS chgDate	/* 변경일시, VARCHAR2(14) */
//					FROM
//						HD_CODE_ACNT	/* HD_분양_전표_계정 */
//					WHERE 1=1
//						<if test="dHdCodeAcnt01IO.deptCode != null and dHdCodeAcnt01IO.deptCode != &quot;&quot;">
//						AND HD_CODE_ACNT.DEPT_CODE = :dHdCodeAcnt01IO___deptCode	/* 현장코드, VARCHAR2(12) */
//						</if>
//						<if test="dHdCodeAcnt01IO.jcode != null and dHdCodeAcnt01IO.jcode != &quot;&quot;">
//						AND HD_CODE_ACNT.JCODE = :dHdCodeAcnt01IO___jcode	/* 내부코드, VARCHAR2(2) */
//						</if>
//					ORDER BY
//						HD_CODE_ACNT.INPUT_DATE DESC
//		/* #### Original SQL ]] ################# */
//		
//		        ) A 
//		    WHERE ROWNUM <= ((:pageNum*:pageCount)+1) 
//		    ) 
//		WHERE ROW__NUM > (:pageNum-1)*:pageCount 


		StringBuilder sql = new StringBuilder();
		sql.append("	SELECT * FROM (" +"\n");
		sql.append("		SELECT ROWNUM AS RNUM, RECORDS.* FROM (" +"\n");
		sql.append("			SELECT" +"\n");
		int i = 0;
		for(VtSchemaDTO schema : tableInfo){
			//TO_DATE("#"+schema.getColumn_name()+"#", 'yyyy-MM-dd hh24:mi:ss')
			if(schema.getData_type().equals("DATE")) {
				sql.append("				" + (i > 0 ? ",":" ") + "TO_CHAR(" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + ", 'yyyy-MM-dd') AS " + generateHelper.getCamelCaseFieldName(schema.getColumn_name()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */" +"\n");
			}
			else {
				sql.append("				" + (i > 0 ? ",":" ") + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " AS " + generateHelper.getCamelCaseFieldName(schema.getColumn_name()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */" +"\n");
			}
			
			i++;
		}
		
		
		//foregin 정보추출 시작
		List<ForeignInfoDTO> foreginTables = findFKTables(tableInfo.get(0).getTable_name(), forgeintables, new ArrayList<ForeignInfoDTO>(), 0);
		
		//FROM 절 시작 ----------------------------------------
		sql.append("			FROM" +"\n");
		i = 0;
		for(String tableName : getFKTable(foreginTables)) {
			sql.append("				" + (i > 0 ? ",":" ") + tableName + "	/* "+getTableComments(tableList, tableName)+" */" + "\n");
			i++;
		}
		if(foreginTables.size() == 0) {
			sql.append("				" + tableInfo.get(0).getTable_name() + "	/* "+getTableComments(tableList, tableInfo.get(0).getTable_name())+" */" + "\n");
		}
		//FROM 절 종료 ----------------------------------------
		
		sql.append("			WHERE 1=1" +"\n");
		
		//JOIN 절 시작 ----------------------------------------
		for(ForeignInfoDTO fkTable : getFKTableJoinWhereSentence(foreginTables)){
			sql.append("				AND " + fkTable.getTable_name() + "." + fkTable.getColumn_name() + " = " + fkTable.getFk_table_name() + "." + fkTable.getFk_column_name() + "\n");  
		}
		//JOIN 절 종료 ----------------------------------------

		
		i = 0;
		for(VtSchemaDTO schema : tableInfo){ 
			if(schema.getIndexes() != null && !schema.getIndexes().isEmpty()) {
				if(schema.getData_type().equals("CHAR")) { //equals
					//myBatis sentence
					sql.append("				<if test='"+schema.getColumn_name().toLowerCase()+" != null and "+schema.getColumn_name().toLowerCase()+" != \"\"'>" + "\n");
					sql.append("				AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  + "\n");
					sql.append("				</if>" + "\n");
					i++;
				}
				else if(schema.getData_type().equals("NUMBER")) { //equals
					//myBatis sentence
					sql.append("				<if test='"+schema.getColumn_name().toLowerCase()+" != null'>" + "\n");
					sql.append("				AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  + "\n");
					sql.append("				</if>" + "\n");
					i++;
				}
				else if(schema.getData_type().equals("VARCHAR2")) { //like
					//myBatis sentence
					if( schema.getColumn_name().endsWith("_NM") || schema.getColumn_name().endsWith("_ADDR") || schema.getColumn_name().endsWith("_DESC") || schema.getColumn_name().endsWith("_IP") || schema.getColumn_name().endsWith("_VAL") ) {
						sql.append("				<if test='"+schema.getColumn_name().toLowerCase()+" != null and "+schema.getColumn_name().toLowerCase()+" != \"\"'>" + "\n");
						sql.append("				AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " LIKE '%' || " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + " || '%'	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  + "\n");
						sql.append("				</if>" + "\n");
					}
					else {
						sql.append("				<if test='"+schema.getColumn_name().toLowerCase()+" != null and "+schema.getColumn_name().toLowerCase()+" != \"\"'>" + "\n");
						sql.append("				AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  + "\n");
						sql.append("				</if>" + "\n");
					}
					i++;
				}
				else if(schema.getData_type().equals("CLOB")) { //like
					//myBatis sentence
					sql.append("				<if test='"+schema.getColumn_name().toLowerCase()+" != null and "+schema.getColumn_name().toLowerCase()+" != \"\"'>" + "\n");
					sql.append("				AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " LIKE '%' || " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + " || '%'	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  + "\n");
					sql.append("				</if>" + "\n");
					i++;
				}
				else if(schema.getData_type().equals("DATE")) { // TO_DATE equals
					//myBatis sentence
					sql.append("				<if test='"+schema.getColumn_name().toLowerCase()+" != null and "+schema.getColumn_name().toLowerCase()+" != \"\"'>" + "\n");
					sql.append("				AND TO_CHAR(" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + ", 'yyyy-MM-dd') = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  + "\n");
					sql.append("				</if>" + "\n");
					i++;
				}
			}
			else {
				if( ( schema.getData_type().equals("VARCHAR2") && schema.getData_length() > 200 ) || schema.getData_type().equals("CLOB")) { //like
					sql.append("				<if test='"+schema.getColumn_name().toLowerCase()+" != null and "+schema.getColumn_name().toLowerCase()+" != \"\"'>" + "\n");
					sql.append("				AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " LIKE '%' || " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type()) + " || '%'	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  + "\n");
					sql.append("				</if>" + "\n");
				}
			}
			/*
			if(schema.getColumn_name().equals("REC_STAT")) {
				sql.append("				AND " + tableInfo.get(0).getTable_name() + "." + "REC_STAT != 'D'"  + "\n");
			}
			*/
		}
		
		//JOIN TABLE REC_STAT 
		/*
		for(String tableName : getFKTable(foreginTables)) {
			if(!tableInfo.get(0).getTable_name().equals(tableName) && isTableIsRecStat(tableName)) {
				sql.append("				AND " + tableName + "." + "REC_STAT != 'D'"  + "\n");
			}
		}
		*/
		
		sql.append("			ORDER BY" +"\n");
		boolean orderDate = false;
		for(VtSchemaDTO schema : tableInfo){
			if(schema.getColumn_name().equals("REG_DATE")) {
				sql.append("				" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " DESC" +"\n");
				orderDate = true;
				break;
			}
		}
		if(!orderDate) {
			for(VtSchemaDTO schema : tableInfo){
				if(schema.getColumn_name().endsWith("_DATE")) {
					sql.append("				" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " DESC" +"\n");
					orderDate = true;
					break;
				}
			}	
		}
		if(!orderDate) {
			for(VtSchemaDTO schema : tableInfo){
				if(schema.getConstraints() != null && (
						   schema.getConstraints().indexOf(CONSTRAINT_PRIMARY) > -1 
						|| schema.getConstraints().indexOf(CONSTRAINT_FOREIGN) > -1
						|| schema.getConstraints().startsWith(CONSTRAINT_CASE_PRIMARY) 
						|| schema.getConstraints().startsWith(CONSTRAINT_CASE_FOREIGN) 
				)) {	
					sql.append("				" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " DESC" +"\n");
					break;
				}
			}	
		}
		
		sql.append("		) RECORDS" +"\n");
		sql.append("	)" +"\n");
		sql.append("	WHERE RNUM BETWEEN #{startRowNum} AND #{endRowNum}");
		
		logger.debug("\n" + sql.toString());
		
		return sql.toString();
	}
	
	
	
	public String getMyBatisTypeValue(String valueName, String dataType){
		String caller = new Throwable().getStackTrace()[1].getMethodName();
		logger.debug("-Call Method : " + caller);
		
		String[] includeJdbcType = {"insert","update","merge","delete", "select", "selectList"};
		// ,jdbcType=VARCHAR

		Map<String,String> jdbcType = new HashMap<String, String>();
		jdbcType.put("LONG", "LONGVARCHAR");
		jdbcType.put("CHAR", "CHAR");
		jdbcType.put("NUMBER", "NUMERIC");
		jdbcType.put("DOUBLE", "DOUBLE");
		jdbcType.put("FLOAT", "FLOAT");
		jdbcType.put("CLOB", "CLOB");
		jdbcType.put("DATE", "DATE");
		jdbcType.put("TIMESTAMP", "TIMESTAMP");
		jdbcType.put("VARCHAR2", "VARCHAR");
		

		StringBuilder out = new StringBuilder();
		
		if(Arrays.asList(includeJdbcType).indexOf(caller) > -1 && dataType != null) {
			
			out.append("#{");
			out.append(generateHelper.getCamelCaseFieldName(valueName));
			out.append(", jdbcType=");
			for(Entry<String, String> entry : jdbcType.entrySet()) {
				if(dataType.startsWith(entry.getKey())) {
					out.append(entry.getValue());
					break;
				}
			}

			out.append("}");
		}
		else {

			out.append("#{");
			out.append(generateHelper.getCamelCaseFieldName(valueName));
			out.append("}");
		}
		
		return out.toString();
	}

	public String getTableComments(List<TableDTO> tableList, String tableName){
		String out = null;
		for(TableDTO table : tableList){
			if(table.getTableName().equals(tableName)) {
				out = StringUtil.NVL(table.getTableComments(), table.getTableName());
			}
		}
		return out;
	}
	
	/*
	public boolean isTableIsRecStat(List<TableDTO> tableList, String tableName){
		boolean out = false;
		for(TableDTO table : tableList){
			if(table.getTableName().equals(tableName)) {
				if(!table.getRecStat().equals("")) {
					out = true;
					break;
				}
			}
		}
		return out;
	}
	*/
	
	/**
	 * 관계테이블 조인 관계를 몇개까지 할것인가의 정의 ( 허나 모두 함 )
	 * @param currentTable : 현제 테이블명
	 * @param forgeintables
	 * @param out
	 * @param parent : 레벨 ( 0 : 전부다 , 1 : 1개만(현재테이블로부터 위로 1개) ) 
	 * @return
	 */
	public List<ForeignInfoDTO> findFKTables(String currentTable, List<ForeignInfoDTO> forgeintables, List<ForeignInfoDTO> out, int parent){
		
		for(ForeignInfoDTO fks : forgeintables) {
			if(fks.getTable_name().equals(currentTable)) {
				//System.out.println("###### findFKTables ####### \n" + propertyUtil.out(fks));		
				out.add(fks);
				if(parent == 0 || parent == 1) {
					if(parent != 0) parent++;
					findFKTables(fks.getFk_table_name(), forgeintables, out, parent);
				}
			}
		}

		return out;
	}
	
	// 조인 from 테이블 목록 중복 필터 목록 
	public List<String> getFKTable(List<ForeignInfoDTO> foreginTables){
		List<String> out = new ArrayList<String>();
		

		for(ForeignInfoDTO ForeignInfoDTO : foreginTables) {
			if(out.indexOf(ForeignInfoDTO.getTable_name()) == -1) {
				out.add(ForeignInfoDTO.getTable_name());
			}
			if(out.indexOf(ForeignInfoDTO.getFk_table_name()) == -1) {
				out.add(ForeignInfoDTO.getFk_table_name());
			}
		}	

		
		return out;
	}
	
	public List<ForeignInfoDTO> getFKTableJoinWhereSentence(List<ForeignInfoDTO> foreginTables){
		List<ForeignInfoDTO> out = new ArrayList<ForeignInfoDTO>();
		
		for(ForeignInfoDTO ForeignInfoDTO : foreginTables) {
			if(out.indexOf(ForeignInfoDTO) == -1) {
				out.add(ForeignInfoDTO);
			}
		}	

		return out;
	}
	
	private List<TableDTO> getTableList(Connection conn, String inTables) {
		List<TableDTO> schemaList = new ArrayList<TableDTO>();
		TableDTO dataDto = null;


		try {
			String query = MetaViewSQL.getTableListSQL(inTables);
			// logger.debug(query);
			pstmt = conn.prepareStatement(query); // create a prepareStatement
			results = pstmt.executeQuery(); // execute select statement

			while (results.next()) {

				dataDto = new TableDTO();
				dataDto.setTableName(StringUtil.NVL(results.getString("table_name"), ""));
				dataDto.setTableType(StringUtil.NVL(results.getString("table_type"), ""));
				dataDto.setTableComments(StringUtil.NVL(results.getString("table_comments"), ""));

				schemaList.add(dataDto);
			}

		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			try {
				if (results != null)
					results.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				throw new ApplicationException(e);
			}
		}

		logger.debug(" - schemaList.size() : " + schemaList.size());

		return schemaList;
	}
	
	

	

	
	private List<VtSchemaDTO> getColumnList(Connection conn, String inTables, boolean multiTable) {
		List<VtSchemaDTO> columnList = new ArrayList<VtSchemaDTO>();
		VtSchemaDTO dataDto = null;
		
		try {
			String query = MetaViewSQL.getColumnListSQL(inTables, multiTable);
			// logger.debug(query);
			pstmt = conn.prepareStatement(query); // create a prepareStatement
			
			if(inTables != null && inTables.equals("PrimaryKey")) {
				pstmt.setString(1, "PK_");
			}
			else if(!StringUtil.NVL(inTables).equals("")) {
				if(multiTable) {
					//pstmt.setString(1, "PK_");
					//pstmt.setString(2, "FK_");
				}
				else {
					pstmt.setString(1, inTables);
					//pstmt.setString(2, "PK_");
					//pstmt.setString(3, "FK_");	
				}
			}
			
			results = pstmt.executeQuery(); // execute select statement

			while (results.next()) {

				dataDto = new VtSchemaDTO();
				if(inTables != null && inTables.equals("PrimaryKey")) {
					dataDto.setTable_name(inTables);
				}
				else {
					dataDto.setTable_name(results.getString("table_name"));
				}
				
				dataDto.setTable_comments(StringUtil.NVL(results.getString("table_comments")));
				dataDto.setTable_type(results.getString("table_type"));
				dataDto.setColumn_name(StringUtil.NVL(results.getString("column_name")));
				dataDto.setData_type(results.getString("data_type"));
				dataDto.setData_length(results.getLong("data_length"));
				dataDto.setData_default(StringUtil.NVL(results.getString("data_default")));
				dataDto.setNullable(StringUtil.NVL(results.getString("nullable")));
				dataDto.setConstraints(StringUtil.NVL(results.getString("constraints")));
				dataDto.setIndexes(StringUtil.NVL(results.getString("indexes")));
				if(dataDto.getConstraints().equalsIgnoreCase("NULL")) dataDto.setConstraints(""); 
				dataDto.setComments(StringUtil.NVL(results.getString("comments")));
				dataDto.setData_full_type(StringUtil.NVL(results.getString("data_full_type")));
				dataDto.setJava_type(StringUtil.NVL(results.getString("java_type")));
				dataDto.setData_scale(results.getLong("data_scale"));
				dataDto.setColumn_id(results.getLong("column_id"));
				
				columnList.add(dataDto);
			}

		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			try {
				if (results != null)
					results.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				throw new ApplicationException(e);
			}
		}
		
		
		return columnList;
	}
	
	

	public List<ForeignInfoDTO> getForgeinColumn(Connection conn){
		List<ForeignInfoDTO> fKTableList = new ArrayList<ForeignInfoDTO>();
		ForeignInfoDTO dataDto = null;

		PreparedStatement pstmt = null;
		ResultSet results = null;

		try {

			String query = MetaViewSQL.getForgeinColumn();
			pstmt = conn.prepareStatement(query); // create a prepareStatement
			results = pstmt.executeQuery();       // execute select statement
			
			try {
				while(results.next()){
					dataDto = new ForeignInfoDTO();
					
					dataDto.setTable_name(results.getString("table_name"));
					dataDto.setConstraint_type(results.getString("constraint_type"));
					dataDto.setConstraint_name(results.getString("constraint_name"));
					dataDto.setColumn_name(results.getString("column_name"));
					dataDto.setPosition(results.getString("position"));
					dataDto.setR_owner(results.getString("r_owner"));
					dataDto.setR_constraint_name(results.getString("r_constraint_name"));
					dataDto.setFk_table_name(results.getString("fk_table_name"));
					dataDto.setFk_column_name(results.getString("fk_column_name"));
					
					fKTableList.add(dataDto);
					
				}
			} catch (SQLException e) {
				throw new ApplicationException(e);
			}
			
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			logger.debug(" - fKTableList.size() : " + fKTableList.size());
			try {
				if(results != null) results.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException e) {
				throw new ApplicationException(e);
			}
		}

		return fKTableList;
		
	}
	
	public String getSourceRoot() {
		return sourceRoot;
	}

	public void setSourceRoot(String sourceRoot) {
		this.sourceRoot = sourceRoot;
	}

	public String getJavaPackage() {
		return javaPackage;
	}

	public void setJavaPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}
	
	public Properties getDatabaseConfig() {
		return databaseConfig;
	}

	public void setDatabaseConfig(Properties databaseConfig) {
		this.databaseConfig = databaseConfig;
	}

	public boolean isCreateFile() {
		return createFile;
	}

	public void setCreateFile(boolean createFile) {
		this.createFile = createFile;
	}

	public String getDatasourceName() {
		
		return (datasourceName != null ? datasourceName : DEFAULT_DATASOURCE_NAME);
	}

	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}

	public String getTargetTables() {
		return targetTables;
	}

	public void setTargetTables(String targetTables) {
		this.targetTables = targetTables;
	}


}
