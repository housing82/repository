package com.universal.code.bxm;

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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.coder.URLCoder;
import com.universal.code.constants.IOperateCode;
import com.universal.code.database.JDBCManager;
import com.universal.code.dto.ForeignInfoDTO;
import com.universal.code.dto.TableDTO;
import com.universal.code.dto.VtSchemaDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.sql.MetaViewSQL;
import com.universal.code.utils.DateUtil;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.RegexUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;

public class BxmDBIOGenerateUtil {

	private final static Logger logger = LoggerFactory.getLogger(BxmDBIOGenerateUtil.class);
	
	// BXM ECLIPSE HD SOURCE ROOT
	private String sourceRoot;
	// ASIS DAO DTO BASE PACKAGE
	private String javaPackage; 
	private String basePackage; 
	private String subPackage;
	private String masterSyscd;
	
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
	private String fileNamePrefix;
	
	private JDBCManager jdbcManager;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet results;
	private StringUtil stringUtil;
	private FileUtil fileUtil;
	private RegexUtil regexUtil;
	private GenerateHelper generateHelper;
	
	private final String javaCrudTemplateDbio;
	private final String javaSelectListTemplateDbio;
	private final String javaTemplateDbio;
	private final String xmlDeleteTemplateDbio;
	private final String xmlInsertTemplateDbio;
	private final String xmlSelectTemplateDbio;
	private final String xmlSelectListTemplateDbio;
	private final String xmlTemplateDbio;
	private final String xmlUpdateMergeTemplateDbio;
	
	private static String templatePath;
	
	private static final String rvMethodDescription;	//인터페이스 메소드 주석 설명
	private static final String rvTestValues;			//인터페이스 메소드 주석 테스트 값
	private static final String rvLogicalName;			//인터페이스 메소드 어노테이션 로지컬 명
	private static final String rvDescription;			//인터페이스 메소드 어노테이션 설명
	private static final String rvOutputType; 			//인터페이스 메소드 결과 타입
	private static final String rvMethodName;			//인터페이스 메소드 명
	private static final String rvInputType; 			//인터페이스 메소드 입력 타입
	private static final String rvInputVariable;		//인터페이스 메소드 입력 타입 변수 명
	private static final String rvPackage;				//인터페이스 패키지
	private static final String rvMapper;				//인터페이스 어노테이션 mapper
	private static final String rvDatasource;			//인터페이스 어노테이션 datasource
	private static final String rvClassName;			//인터페이스 클래스 명
	private static final String rvBody;					//인터페이스 & XML 내용
	private static final String rvSql;					//XML 노드별 SQL 내용
	private static final String rvNamespace;			//XML 네임스페이스
	private static final String rvDate;					//생성일자
	
	private static Map<String, String> methodMap;
	
	
	
	private static String CONSTRAINT_CHECK;
	private static String CONSTRAINT_PRIMARY;
	private static String CONSTRAINT_UNIQUE;
	private static String CONSTRAINT_FOREIGN;
	private static String CONSTRAINT_CASE_PRIMARY;
	private static String CONSTRAINT_CASE_UNIQUE;
	private static String CONSTRAINT_CASE_FOREIGN;
	
	static {
		
		methodMap = new LinkedHashMap<String, String>();
		methodMap.put("insert", "등록");
		methodMap.put("select", "단건조회");
		methodMap.put("selectCount", "전채건수조회");
		methodMap.put("selectList", "목록조회");
		methodMap.put("update", "수정");
		methodMap.put("merge", "병합");
		methodMap.put("delete", "삭제");
		//템플릿 패스
		//templatePath = URLCoder.getInstance().getURLDecode(BxmDBIOGenerateUtil.class.getResource(".").getPath().concat("template").concat(File.separator), ""); 
		templatePath = URLCoder.getInstance().getURLDecode(BxmDBIOGenerateUtil.class.getResource("/").getPath().concat("template").concat("/"), "");
		if(templatePath.contains("test-classes")) {
			templatePath = templatePath.replace("test-classes", "classes");
		}
		
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
		
		rvMethodDescription = "#{rvMethodDescription}"; //인터페이스 메소드 주석 설명
		rvTestValues = "#{rvTestValues}";				//인터페이스 메소드 주석 테스트 값
		rvLogicalName = "#{rvLogicalName}";				//인터페이스 메소드 어노테이션 로지컬 명
		rvDescription = "#{rvDescription}";				//인터페이스 메소드 어노테이션 설명
		rvOutputType = "#{rvOutputType}"; 				//인터페이스 메소드 결과 타입
		rvMethodName = "#{rvMethodName}";				//인터페이스 메소드 명
		rvInputType = "#{rvInputType}"; 				//인터페이스 메소드 입력 타입
		rvInputVariable = "#{rvInputVariable}";			//인터페이스 메소드 입력 타입 변수 명
		rvPackage = "#{rvPackage}";						//인터페이스 패키지
		rvMapper = "#{rvMapper}";						//인터페이스 어노테이션 mapper
		rvDatasource = "#{rvDatasource}";				//인터페이스 어노테이션 datasource
		rvClassName = "#{rvClassName}";					//인터페이스 클래스 명
		rvBody = "#{rvBody}";							//인터페이스 & XML 내용
		rvSql = "#{rvSql}";								//XML 노드별 SQL 내용
		rvNamespace = "#{rvNamespace}";					//XML 네임스페이스
		rvDate = "#{rvDate}";							//생성일자
	}
	
	public BxmDBIOGenerateUtil() {
		jdbcManager = new JDBCManager();
		stringUtil = new StringUtil();
		fileUtil = new FileUtil();
		generateHelper = new GenerateHelper();
		regexUtil = new RegexUtil();
		
		// source template
		javaCrudTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("bxmDbio.java.crud.template"));
		javaSelectListTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("bxmDbio.java.selectList.template"));
		javaTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("bxmDbio.java.template"));
		xmlDeleteTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("bxmDbio.xml.delete.template"));
		xmlInsertTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("bxmDbio.xml.insert.template"));
		xmlSelectTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("bxmDbio.xml.select.template"));
		xmlSelectListTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("bxmDbio.xml.selectList.template"));
		xmlTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("bxmDbio.xml.template"));
		xmlUpdateMergeTemplateDbio = fileUtil.getTextFileContent(templatePath.concat("bxmDbio.xml.update.merge.template"));
	}
	
	public void execute() {
		logger.debug("[START] execute: {}", getDatabaseConfig());
		logger.debug("★ SourceRoot: {}", getSourceRoot());
		logger.debug("★ JavaPackage: {}", getJavaPackage());
		logger.debug("url: {}", templatePath);
		logger.debug("javaCrudTemplateDbio:\n {}", javaCrudTemplateDbio);
		logger.debug("javaSelectListTemplateDbio:\n {}", javaSelectListTemplateDbio);
		logger.debug("javaTemplateDbio:\n {}", javaTemplateDbio);
		logger.debug("xmlDeleteTemplateDbio:\n {}", xmlDeleteTemplateDbio);
		logger.debug("xmlInsertTemplateDbio:\n {}", xmlInsertTemplateDbio);
		logger.debug("xmlSelectTemplateDbio:\n {}", xmlSelectTemplateDbio);
		logger.debug("xmlSelectListTemplateDbio:\n {}", xmlSelectListTemplateDbio);
		logger.debug("xmlTemplateDbio:\n {}", xmlTemplateDbio);
		logger.debug("xmlUpdateMergeTemplateDbio:\n {}", xmlUpdateMergeTemplateDbio);

		List<TableDTO> tableList = null;
		List<VtSchemaDTO> columnList = null;
		List<ForeignInfoDTO> foreignColumnList = null;
		String fileName = null;
		String fieldName = null;
		
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
		
		//20170821
		String appOrgCode = null;
		String appCode = null;
		String tableBodyName = null;
		
		try {
			conn = jdbcManager.getConnection(getDatabaseConfig());
			logger.debug("conn: {}", conn);
			// SELECT DATABASE
			tableList = getTableList(conn, getTargetTables());
			// currentTableName테이블의 참조키 컬럼정보
			foreignColumnList = getForgeinColumn(conn);

			String currentTableName = null;
			//테이블 목록
			for(TableDTO table : tableList) {

				currentTableName = table.getTableName();

				// 20170821 수정 (타업무 테이블 DBIO생성시 타업무 대분류코드를 순번앞에 붙임
				appOrgCode = "";
				appCode = table.getTableName().substring(0, table.getTableName().indexOf(IOperateCode.STR_UNDERBAR));
				if(appCode == null) {
					throw new ApplicationException("테이블의 대분류 업무코드를 추출할수 없는 테이블 명명입니다. 테이블명: {}", table.getTableName());	
				}
				tableBodyName = table.getTableName().substring(table.getTableName().indexOf(IOperateCode.STR_UNDERBAR));
				
				if(!appCode.equalsIgnoreCase(getMasterSyscd())) {
					appOrgCode = appCode.toUpperCase();
					appCode = getMasterSyscd(); // 분양임대의 경우 MasterSyscd는 HD 
				}
				
				// dbio 파일명 명명규칙: ‘D’ + 테이블명 + 일련번호(2자리) -> DAO동사 + 약어명 + “01” ~ “99”
				//fileName = getFileNamePrefix().concat(stringUtil.getCharUpperCase(stringUtil.getCamelCaseString(currentTableName), 2));
				fileName = getFileNamePrefix().concat(stringUtil.getCharUpperCase(stringUtil.getCamelCaseString( appCode.concat(tableBodyName)  ), 2).concat(appOrgCode));
				
				// 일련번호 연결
				fileName = fileName.concat(generateHelper.getJavaSeq(fileName));
				
				logger.debug("#TableName(): -{}, appOrgCode: {}, appCode: {}, fileName: {}, tableBodyName: {}", table.getTableName(), appOrgCode, appCode, fileName, tableBodyName);
				
				// currentTableName테이블의 컬럼목록
				columnList = getColumnList(conn, currentTableName, false);
				
				/**********************
				 * START MYBATIS INTERFACE JAVA
				 */
				// 메소드 주석에 사용할 테이블 코멘트
				dsDescription = StringUtil.NVL(table.getTableComments(), currentTableName);
				dsPackage = getJavaPackage(table.getTableName().split(IOperateCode.STR_UNDERBAR));
				dsDate = DateUtil.getFastDate(DateUtil.DEF_DATE_FORMAT);
				dsMapper = new StringBuilder().append(getJavaPackage(table.getTableName().split(IOperateCode.STR_UNDERBAR)).replace(".", "/")).append(fileName.concat(".dbio")).toString();
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
						
				dsNamespace = getJavaPackage(table.getTableName().split(IOperateCode.STR_UNDERBAR)).concat(".").concat(dsClassName);
				
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
					// DAO 메소드 : DAO동사 + 약어명 + “01” ~ “99”
					dsMethodName = entry.getKey().concat(stringUtil.getFirstCharUpperCase(stringUtil.getCamelCaseString(currentTableName)));
					dsMethodName = dsMethodName.concat(generateHelper.getJavaSeq(dsMethodName));
					
					dsMethodDescription = dsDescription.concat(" ").concat(entry.getValue());
					dsLogicalName = dsMethodDescription; 
					dsOutputType = getJavaPackage(table.getTableName().split(IOperateCode.STR_UNDERBAR)).concat(".dto.").concat(fileName).concat("IO");
					dsInputType = getJavaPackage(table.getTableName().split(IOperateCode.STR_UNDERBAR)).concat(".dto.").concat(fileName).concat("IO");
					dsInputVariable = stringUtil.getFirstCharLowerCase(fileName.concat("IO"));
					
					if(entry.getKey().equals("selectList" )) {
						// selectList
						methodCode = javaSelectListTemplateDbio;
						
						dsInputVariable = "in";
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
						
						if(entry.getKey().equals("selectCount")) {
							dsOutputType = "java.lang.Integer";
						}
					}
					

					
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
					else if(entry.getKey().equals("selectCount")) {
						sqlCode = xmlSelectTemplateDbio;
						
						dsSql = selectCount(tableList, columnList, foreignColumnList, dsInputVariable); // <-- SQL
					}
					else if(entry.getKey().equals("selectList")) {
						sqlCode = xmlSelectListTemplateDbio;
						
						dsSql = selectList(tableList, columnList, foreignColumnList, dsInputVariable); // <-- SQL
					}
					else if(entry.getKey().equals("update")) {
						sqlCode = xmlUpdateMergeTemplateDbio;
						
						dsSql = update(columnList); // <-- SQL
					}
					else if(entry.getKey().equals("merge")) {
						sqlCode = xmlUpdateMergeTemplateDbio;
						
						dsSql = merge(columnList); // <-- SQL
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
	  
					//테이블에 pk or fk 가 없을경우 merge sql은 생성하지 않는다. 
					//이러할 경우 dsSql는 empty이다.
					if(StringUtil.isNotEmpty(dsSql)) {
						
						//MyBatis Interface Java Code
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
						
						//MyBatis Mapper XML Code
						xmlBd.append(sqlCode
								.replace(rvMethodName, dsMethodName)
								.replace(rvInputType, dsInputType)
								.replace(rvOutputType, dsOutputType)
								.replace(rvSql, dsSql));
						
						xmlBd.append(SystemUtil.LINE_SEPARATOR);						
					}
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
							new StringBuilder().append(sourceRoot).append("/").append(getJavaPackage(table.getTableName().split(IOperateCode.STR_UNDERBAR)).replace(".", "/")).toString()
							, fileName.concat(".java")
							, javaFinal
							, IOperateCode.DEFAULT_ENCODING
							, false
							, true); 
					
					fileUtil.mkfile(
							new StringBuilder().append(sourceRoot).append("/").append(getJavaPackage(table.getTableName().split(IOperateCode.STR_UNDERBAR)).replace(".", "/")).toString()
							, fileName.concat(".dbio")
							, xmlFinal
							, IOperateCode.DEFAULT_ENCODING
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
	
	private String insert(List<VtSchemaDTO> tableInfo){
		
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO "+tableInfo.get(0).getTable_name()+"(	/* "+ StringUtil.NVL(tableInfo.get(0).getTable_comments(), tableInfo.get(0).getTable_name()) +" */" ).append(SystemUtil.LINE_SEPARATOR);
		int i = 0;
		for(VtSchemaDTO schema : tableInfo){   
			sql.append("	" + (i > 0 ? ",":" ") + schema.getColumn_name() + "	/* "+StringUtil.NVL(schema.getComments(), schema.getColumn_name())+", "+schema.getData_full_type()+" */" ).append(SystemUtil.LINE_SEPARATOR);
			i++;
		}
		sql.append(") VALUES (" ).append(SystemUtil.LINE_SEPARATOR);
		
		i = 0;
		for(VtSchemaDTO schema : tableInfo){
			//TO_DATE("#{"+schema.getColumn_name()+"}", '" + IOperateCode.DEF_DATE_FORMAT + "')
			if(schema.getData_type().equals("DATE")) {
				sql.append("	" + (i > 0 ? ",":" ") + "TO_DATE(" + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null) + ", '" + DateUtil.DEF_DATE_FORMAT + "')" ).append(SystemUtil.LINE_SEPARATOR);					
			}
			else {
				sql.append("	" + (i > 0 ? ",":" ") + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null)).append(SystemUtil.LINE_SEPARATOR);
			}
			
			i++;
		}
		
		sql.append(")");
		
		logger.debug("\n" + sql.toString());

		return sql.toString().trim();
	}
	
	private String update(List<VtSchemaDTO> tableInfo){
		
		List<String> conditionCols = new ArrayList<String>();
		
		StringBuilder where = new StringBuilder();
		
		for(VtSchemaDTO schema : tableInfo){
			if(schema.getConstraints() != null && (
				   schema.getConstraints().indexOf(CONSTRAINT_PRIMARY) > -1 
				|| schema.getConstraints().indexOf(CONSTRAINT_FOREIGN) > -1
				|| schema.getConstraints().startsWith(CONSTRAINT_CASE_PRIMARY) 
				|| schema.getConstraints().startsWith(CONSTRAINT_CASE_FOREIGN) 
			)) {
				where.append("	AND " + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
				conditionCols.add(schema.getColumn_name());
			}
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE "+tableInfo.get(0).getTable_name()+" SET	/* "+ StringUtil.NVL(tableInfo.get(0).getTable_comments(), tableInfo.get(0).getTable_name()) +" */" ).append(SystemUtil.LINE_SEPARATOR);

		int i = 0;
		for(VtSchemaDTO schema : tableInfo){
			if(conditionCols.contains(schema.getColumn_name())) {
				continue;
			}
			
			//TO_DATE("#{"+schema.getColumn_name()+"}", '" + IOperateCode.DEF_DATE_FORMAT + "')
			if(schema.getData_type().equals("DATE")) {
				sql.append("	" + (i > 0 ? ",":" ") + schema.getColumn_name() + " = TO_DATE(" + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null) + ", '" + DateUtil.DEF_DATE_FORMAT + "')" + "	/* "+StringUtil.NVL(schema.getComments(), schema.getColumn_name())+", "+schema.getData_full_type()+" */" ).append(SystemUtil.LINE_SEPARATOR);					
			}
			else {
				sql.append("	" + (i > 0 ? ",":" ") + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null) + "	/* "+StringUtil.NVL(schema.getComments(), schema.getColumn_name())+", "+schema.getData_full_type()+" */" ).append(SystemUtil.LINE_SEPARATOR);
			}
			
			i++;
		}
		
		sql.append("WHERE 1=1" ).append(SystemUtil.LINE_SEPARATOR);
		
		sql.append(where.toString());

		logger.debug("\n" + sql.toString());
		
		return sql.toString().trim();
	}
/*	
	MERGE INTO emp
    USING DUAL
    ON (employee_id = 200911)
    WHEN MATCHED THEN
              UPDATE SET
                    first_name = 'John',
                    last_name = 'Petrucci',
                    email = 'dream@johnpetrucci.com',
                    ......
    WHEN NOT MATCHED THEN
              INSERT (first_name, last_name, email, ...... ) 
                         VALUES ('John', 'Petrucci', 'dream@johnpetrucci.com', ...... );
*/
	private String merge(List<VtSchemaDTO> tableInfo) {
		StringBuilder sql = new StringBuilder();
		boolean checkCondition = false;
		int i = 0;
		sql.append("MERGE INTO "+tableInfo.get(0).getTable_name() + " /* "+ StringUtil.NVL(tableInfo.get(0).getTable_comments(), tableInfo.get(0).getTable_name()) +" */" ).append(SystemUtil.LINE_SEPARATOR);
		sql.append("	USING DUAL ON (" ).append(SystemUtil.LINE_SEPARATOR);
		
		List<String> conditionCols = new ArrayList<String>();
		
		for(VtSchemaDTO schema : tableInfo){
			if(schema.getConstraints() != null && (
					   schema.getConstraints().indexOf(CONSTRAINT_PRIMARY) > -1 
					|| schema.getConstraints().indexOf(CONSTRAINT_FOREIGN) > -1
					|| schema.getConstraints().startsWith(CONSTRAINT_CASE_PRIMARY) 
					|| schema.getConstraints().startsWith(CONSTRAINT_CASE_FOREIGN) 
			)) {
				if(!checkCondition) {
					checkCondition = true;
				}
				sql.append("		" + (i > 0 ? "AND " : "") + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
				conditionCols.add(schema.getColumn_name());
				i++;
			}
		}

		sql.append("	)" ).append(SystemUtil.LINE_SEPARATOR);
		sql.append("	WHEN MATCHED THEN" ).append(SystemUtil.LINE_SEPARATOR);
		sql.append("		UPDATE SET" ).append(SystemUtil.LINE_SEPARATOR);

		i = 0;
		for(VtSchemaDTO schema : tableInfo){
			if(conditionCols.contains(schema.getColumn_name())) {
				continue;
			}
			//TO_DATE("#{"+schema.getColumn_name()+"}", '" + IOperateCode.DEF_DATE_FORMAT + "')
			if(schema.getData_type().equals("DATE")) {
				sql.append("			" + (i > 0 ? ",":" ") + schema.getColumn_name() + " = TO_DATE(" + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null) + ", '" + DateUtil.DEF_DATE_FORMAT + "')" + "	/* "+StringUtil.NVL(schema.getComments(), schema.getColumn_name())+", "+schema.getData_full_type()+" */" ).append(SystemUtil.LINE_SEPARATOR);					
			}
			else {
				sql.append("			" + (i > 0 ? ",":" ") + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null) + "	/* "+StringUtil.NVL(schema.getComments(), schema.getColumn_name())+", "+schema.getData_full_type()+" */" ).append(SystemUtil.LINE_SEPARATOR);
			}
			
			i++;
		}
		
		sql.append("	WHEN NOT MATCHED THEN" ).append(SystemUtil.LINE_SEPARATOR);
		sql.append("		INSERT (" ).append(SystemUtil.LINE_SEPARATOR);
		
		i = 0;
		for(VtSchemaDTO schema : tableInfo){   
			sql.append("			" + (i > 0 ? ",":" ") + schema.getColumn_name() + "	/* "+StringUtil.NVL(schema.getComments(), schema.getColumn_name())+", "+schema.getData_full_type()+" */" ).append(SystemUtil.LINE_SEPARATOR);
			i++;
		}
		
		sql.append("		) VALUES (" ).append(SystemUtil.LINE_SEPARATOR);

		i = 0;
		for(VtSchemaDTO schema : tableInfo){
			//TO_DATE("#{"+schema.getColumn_name()+"}", '" + IOperateCode.DEF_DATE_FORMAT + "')
			if(schema.getData_type().equals("DATE")) {
				sql.append("			" + (i > 0 ? ",":" ") + "TO_DATE(" + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null) + ", '" + DateUtil.DEF_DATE_FORMAT + "')" ).append(SystemUtil.LINE_SEPARATOR);					
			}
			else {
				sql.append("			" + (i > 0 ? ",":" ") + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null)).append(SystemUtil.LINE_SEPARATOR);
			}
			
			i++;
		}
		
		sql.append("		)" ).append(SystemUtil.LINE_SEPARATOR);
		
		if(!checkCondition) {
			//ON 안의 조건절이 존재하지 않으면 초기화 ( 조건절은 테이블에 PK, FK, UK 중 하나라도 존재하면 생성된다 )
			sql = new StringBuilder();
		}
		return sql.toString().trim();
	}
	

	private String delete(List<VtSchemaDTO> tableInfo){
		

		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM "+tableInfo.get(0).getTable_name() + " /* "+ StringUtil.NVL(tableInfo.get(0).getTable_comments(), tableInfo.get(0).getTable_name()) +" */" ).append(SystemUtil.LINE_SEPARATOR);
		sql.append("WHERE 1=1" ).append(SystemUtil.LINE_SEPARATOR);
		
		
		for(VtSchemaDTO schema : tableInfo){
			if(schema.getConstraints() != null && (
					   schema.getConstraints().indexOf(CONSTRAINT_PRIMARY) > -1 
					|| schema.getConstraints().indexOf(CONSTRAINT_FOREIGN) > -1
					|| schema.getConstraints().startsWith(CONSTRAINT_CASE_PRIMARY) 
					|| schema.getConstraints().startsWith(CONSTRAINT_CASE_FOREIGN) 
			)) {
				sql.append("	AND " + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
			}
		}
		
		logger.debug("\n" + sql.toString());
		
		return sql.toString().trim();
	}
	
	
	
	
	//single table & single row select
	private String select(List<TableDTO> tableList, List<VtSchemaDTO> tableInfo, List<ForeignInfoDTO> forgeintables){
	
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT" ).append(SystemUtil.LINE_SEPARATOR);
		int i = 0;
		for(VtSchemaDTO schema : tableInfo){
			//TO_DATE("#{"+schema.getColumn_name()+"}", '" + IOperateCode.DEF_DATE_FORMAT + "')
			if(schema.getData_type().equals("DATE")) {
				sql.append("	" + (i > 0 ? ",":" ") + "TO_CHAR(" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + ", '" + DateUtil.DEF_DAY_FORMAT + "') AS " + generateHelper.getCamelCaseFieldName(schema.getColumn_name()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */" ).append(SystemUtil.LINE_SEPARATOR);
			}
			else {
				sql.append("	" + (i > 0 ? ",":" ") + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " AS " + generateHelper.getCamelCaseFieldName(schema.getColumn_name()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */" ).append(SystemUtil.LINE_SEPARATOR);
			}
			
			i++;
		}
		
		//foregin 정보추출 시작
		List<ForeignInfoDTO> foreginTables = findFKTables(tableInfo.get(0).getTable_name(), forgeintables, new ArrayList<ForeignInfoDTO>(), 0);
		
		//FROM 절 시작 ----------------------------------------
		sql.append("FROM" ).append(SystemUtil.LINE_SEPARATOR);
		i = 0;
		for(String tableName : getFKTable(foreginTables)) {
			sql.append("		" + (i > 0 ? ",":" ") + tableName + "	/* "+getTableComments(tableList, tableName)+" */" ).append(SystemUtil.LINE_SEPARATOR);
			i++;
		}
		if(foreginTables.size() == 0) {
			sql.append("	" + tableInfo.get(0).getTable_name() + "	/* "+getTableComments(tableList, tableInfo.get(0).getTable_name())+" */" ).append(SystemUtil.LINE_SEPARATOR);
		}
		//FROM 절 종료 ----------------------------------------
		
		sql.append("WHERE 1=1" ).append(SystemUtil.LINE_SEPARATOR);
		
		//JOIN 절 시작 ----------------------------------------
		for(ForeignInfoDTO fkTable : getFKTableJoinWhereSentence(foreginTables)){
			sql.append("	AND " + fkTable.getTable_name() + "." + fkTable.getColumn_name() + " = " + fkTable.getFk_table_name() + "." + fkTable.getFk_column_name() ).append(SystemUtil.LINE_SEPARATOR);
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
				sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), null) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
				i++;
			}
			
			if(schema.getColumn_name().equals("REC_STAT")) {
				sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + "REC_STAT != 'D'"  ).append(SystemUtil.LINE_SEPARATOR);
			}
			
		}
		
		//JOIN TABLE REC_STAT ( 명칭이 일치하는 레코드 상태 정보 컬럼이존재할경우 )
		for(String tableName : getFKTable(foreginTables)) {
			if(!tableInfo.get(0).getTable_name().equals(tableName)) {
				sql.append("	AND " + tableName + "." + "REC_STAT != 'D'"  ).append(SystemUtil.LINE_SEPARATOR);
			}
		}
		
		
		logger.debug("\n" + sql.toString());
		
		return sql.toString().trim();
	}
	
	private String where(List<TableDTO> tableList, List<VtSchemaDTO> tableInfo, List<ForeignInfoDTO> forgeintables, String paramAlias, String whiteSpace) {
		
		StringBuilder sql = new StringBuilder();
		
		//foregin 정보추출 시작
		List<ForeignInfoDTO> foreginTables = findFKTables(tableInfo.get(0).getTable_name(), forgeintables, new ArrayList<ForeignInfoDTO>(), 0);
		
		/** ##################################
		 * MAKE WHERE EXPRESSION
		 * ################################## */
		sql.append(whiteSpace);
		sql.append("WHERE 1=1" ).append(SystemUtil.LINE_SEPARATOR);
		
		/* ###################### JOIN 절 시작 ###################### */
		for(ForeignInfoDTO fkTable : getFKTableJoinWhereSentence(foreginTables)){
			sql.append(whiteSpace);
			sql.append("	AND " + fkTable.getTable_name() + "." + fkTable.getColumn_name() + " = " + fkTable.getFk_table_name() + "." + fkTable.getFk_column_name() ).append(SystemUtil.LINE_SEPARATOR);  
		}
		/* ###################### JOIN 절 종료 ###################### */

		
		/* ###################### 검색 조건 시작 ###################### */
		int i = 0;
		for(VtSchemaDTO schema : tableInfo){ 
			if(schema.getIndexes() != null && !schema.getIndexes().isEmpty()) {
				if(schema.getData_type().equals("CHAR")) { //equals
					//myBatis sentence
					sql.append(whiteSpace);
					sql.append("	<if test=\""+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != null and "+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != ''\">" ).append(SystemUtil.LINE_SEPARATOR);
					sql.append(whiteSpace);
					sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
					sql.append(whiteSpace);
					sql.append("	</if>" ).append(SystemUtil.LINE_SEPARATOR);
					i++;
				}
				else if(schema.getData_type().equals("NUMBER")) { //equals
					//myBatis sentence
					sql.append(whiteSpace);
					sql.append("	<if test=\""+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != null\">" ).append(SystemUtil.LINE_SEPARATOR);
					sql.append(whiteSpace);
					sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
					sql.append(whiteSpace);
					sql.append("	</if>" ).append(SystemUtil.LINE_SEPARATOR);
					i++;
				}
				else if(schema.getData_type().equals("VARCHAR2")) { //like
					//myBatis sentence
					if( schema.getColumn_name().endsWith("NM") || schema.getColumn_name().endsWith("NAME") 
						|| schema.getColumn_name().endsWith("ADDR") || schema.getColumn_name().endsWith("DESC") 
						|| schema.getColumn_name().endsWith("IP") || schema.getColumn_name().endsWith("VAL") ) {
						
						sql.append(whiteSpace);
						sql.append("	<if test=\""+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != null and "+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != ''\">" ).append(SystemUtil.LINE_SEPARATOR);
						sql.append(whiteSpace);
						sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " LIKE '%' || " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias) + " || '%'	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
						sql.append(whiteSpace);
						sql.append("	</if>" ).append(SystemUtil.LINE_SEPARATOR);
					}
					else {
						sql.append(whiteSpace);
						sql.append("	<if test=\""+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != null and "+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != ''\">" ).append(SystemUtil.LINE_SEPARATOR);
						sql.append(whiteSpace);
						sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
						sql.append(whiteSpace);
						sql.append("	</if>" ).append(SystemUtil.LINE_SEPARATOR);
					}
					i++;
				}
				else if(schema.getData_type().equals("CLOB")) { //like
					//myBatis sentence
					sql.append(whiteSpace);
					sql.append("	<if test=\""+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != null and "+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != ''\">" ).append(SystemUtil.LINE_SEPARATOR);
					sql.append(whiteSpace);
					sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " LIKE '%' || " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias) + " || '%'	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
					sql.append(whiteSpace);
					sql.append("	</if>" ).append(SystemUtil.LINE_SEPARATOR);
					i++;
				}
				else if(schema.getData_type().equals("DATE")) { // TO_DATE equals
					//myBatis sentence
					sql.append(whiteSpace);
					sql.append("	<if test=\""+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != null and "+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != ''\">" ).append(SystemUtil.LINE_SEPARATOR);
					sql.append(whiteSpace);
					sql.append("	AND TO_CHAR(" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + ", '" + DateUtil.DEF_DAY_FORMAT + "') = " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
					sql.append(whiteSpace);
					sql.append("	</if>" ).append(SystemUtil.LINE_SEPARATOR);
					i++;
				}
			}
			else {
				if( ( schema.getData_type().equals("VARCHAR2") && schema.getData_length() > 200 ) || schema.getData_type().equals("CLOB")) { //like
					sql.append(whiteSpace);
					sql.append("	<if test=\""+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != null and "+getMyBatisExprValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias)+" != ''\">" ).append(SystemUtil.LINE_SEPARATOR);
					sql.append(whiteSpace);
					sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " LIKE '%' || " + getMyBatisTypeValue(schema.getColumn_name().toLowerCase(), schema.getData_type(), paramAlias) + " || '%'	/* "+schema.getComments()+", "+schema.getData_full_type()+" */"  ).append(SystemUtil.LINE_SEPARATOR);
					sql.append(whiteSpace);
					sql.append("	</if>" ).append(SystemUtil.LINE_SEPARATOR);
				}
			}

			if(schema.getColumn_name().equals("REC_STAT")) {
				sql.append(whiteSpace);
				sql.append("	AND " + tableInfo.get(0).getTable_name() + "." + "REC_STAT != 'D'"  ).append(SystemUtil.LINE_SEPARATOR);
			}

		}
		
		//JOIN TABLE REC_STAT 
		
		for(String tableName : getFKTable(foreginTables)) {
			if(!tableInfo.get(0).getTable_name().equals(tableName)) {
				sql.append(whiteSpace);
				sql.append("	AND " + tableName + "." + "REC_STAT != 'D'"  ).append(SystemUtil.LINE_SEPARATOR);
			}
		}
		
		/* ###################### 검색 조건 종료 ###################### */
	
		return sql.toString();
	}
	

	private String selectCount(List<TableDTO> tableList, List<VtSchemaDTO> tableInfo, List<ForeignInfoDTO> forgeintables, String paramAlias){

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " ).append(SystemUtil.LINE_SEPARATOR);
		sql.append("	COUNT(1)" ).append(SystemUtil.LINE_SEPARATOR);
		
		//foregin 정보추출 시작
		List<ForeignInfoDTO> foreginTables = findFKTables(tableInfo.get(0).getTable_name(), forgeintables, new ArrayList<ForeignInfoDTO>(), 0);
		
		/** ##################################
		 * MAKE FROM EXPRESSION
		 * ################################## */
		sql.append("FROM" ).append(SystemUtil.LINE_SEPARATOR);
		int i = 0;
		for(String tableName : getFKTable(foreginTables)) {
			sql.append("	" + (i > 0 ? ",":" ") + tableName + "	/* "+getTableComments(tableList, tableName)+" */" ).append(SystemUtil.LINE_SEPARATOR);
			i++;
		}
		if(foreginTables.size() == 0) {
			sql.append("	" + tableInfo.get(0).getTable_name() + "	/* "+getTableComments(tableList, tableInfo.get(0).getTable_name())+" */" ).append(SystemUtil.LINE_SEPARATOR);
		}

		/** ##################################
		 * MAKE WHERE EXPRESSION
		 * ################################## */
		
		sql.append(where(tableList, tableInfo, forgeintables, null, ""));
		
		logger.debug("\n" + sql.toString());
		
		return sql.toString().trim();
	}
	

	private String selectList(List<TableDTO> tableList, List<VtSchemaDTO> tableInfo, List<ForeignInfoDTO> forgeintables, String paramAlias){

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM (" ).append(SystemUtil.LINE_SEPARATOR);
		sql.append("	SELECT ROWNUM AS ROW__NUM, RECORDS.* FROM (" ).append(SystemUtil.LINE_SEPARATOR);
		sql.append("		/* #### SQL Body [[ ################# */");
		sql.append(SystemUtil.LINE_SEPARATOR);
		sql.append("		SELECT" ).append(SystemUtil.LINE_SEPARATOR);
		
		/** ##################################
		 * MAKE READ COLUMN : COLUMN_NAME AS ALIAS_NAME
		 * ################################## */
		int i = 0;
		for(VtSchemaDTO schema : tableInfo){
			//TO_DATE("#"+schema.getColumn_name()+"#", '" + DateUtil.DEF_DATE_FORMAT + "')
			if(schema.getData_type().equals("DATE")) {
				sql.append("			" + (i > 0 ? ",":" ") + "TO_CHAR(" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + ", '" + DateUtil.DEF_DAY_FORMAT + "') AS " + generateHelper.getCamelCaseFieldName(schema.getColumn_name()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */" ).append(SystemUtil.LINE_SEPARATOR);
			}
			else {
				sql.append("			" + (i > 0 ? ",":" ") + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " AS " + generateHelper.getCamelCaseFieldName(schema.getColumn_name()) + "	/* "+schema.getComments()+", "+schema.getData_full_type()+" */" ).append(SystemUtil.LINE_SEPARATOR);
			}
			
			i++;
		}
		
		
		//foregin 정보추출 시작
		List<ForeignInfoDTO> foreginTables = findFKTables(tableInfo.get(0).getTable_name(), forgeintables, new ArrayList<ForeignInfoDTO>(), 0);
		
		/** ##################################
		 * MAKE FROM EXPRESSION
		 * ################################## */
		sql.append("		FROM" ).append(SystemUtil.LINE_SEPARATOR);
		i = 0;
		for(String tableName : getFKTable(foreginTables)) {
			sql.append("			" + (i > 0 ? ",":" ") + tableName + "	/* "+getTableComments(tableList, tableName)+" */" ).append(SystemUtil.LINE_SEPARATOR);
			i++;
		}
		if(foreginTables.size() == 0) {
			sql.append("			" + tableInfo.get(0).getTable_name() + "	/* "+getTableComments(tableList, tableInfo.get(0).getTable_name())+" */" ).append(SystemUtil.LINE_SEPARATOR);
		}

		/** ##################################
		 * MAKE WHERE EXPRESSION
		 * ################################## */
		
		sql.append(where(tableList, tableInfo, forgeintables, paramAlias, "		"));
		
		/** ##################################
		 * MAKE ORDER BY EXPRESSION
		 * ################################## */
		sql.append("		ORDER BY" ).append(SystemUtil.LINE_SEPARATOR);
		boolean orderDate = false;
		
		for(VtSchemaDTO schema : tableInfo){
			if(schema.getColumn_name().equals("REG_DATE")) {
				sql.append("			" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " DESC" ).append(SystemUtil.LINE_SEPARATOR);
				orderDate = true;
				break;
			}
		}
		if(!orderDate) {
			for(VtSchemaDTO schema : tableInfo){
				if(schema.getData_type().endsWith("DATE") 
				  || schema.getData_type().startsWith("TIMESTAMP") 
				  || schema.getColumn_name().indexOf("DATE") > -1) {
					sql.append("			" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " DESC" ).append(SystemUtil.LINE_SEPARATOR);
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
					sql.append("			" + tableInfo.get(0).getTable_name() + "." + schema.getColumn_name() + " DESC" ).append(SystemUtil.LINE_SEPARATOR);
					break;
				}
			}	
		}
		
		sql.append("		/* #### SQL Body ]] ################# */" ).append(SystemUtil.LINE_SEPARATOR);
		sql.append("	) RECORDS" ).append(SystemUtil.LINE_SEPARATOR);
		sql.append("	WHERE ROWNUM &lt;= ((#{pageNum}*#{pageCount})+1) " ).append(SystemUtil.LINE_SEPARATOR);
		sql.append(")").append(SystemUtil.LINE_SEPARATOR);
		sql.append("WHERE ROW__NUM &gt; (#{pageNum}-1)*#{pageCount}");
		
		logger.debug("\n" + sql.toString());
		
		return sql.toString().trim();
	}
	

	
	

	private String getMyBatisExprValue(String valueName, String dataType, String paramAlias){
		String caller = new Throwable().getStackTrace()[1].getMethodName();
		logger.debug("-Call Method : " + caller);
		
		StringBuilder out = new StringBuilder();
		
		if(paramAlias != null) {
			out.append(paramAlias);
			out.append(".");
		}
		out.append(generateHelper.getCamelCaseFieldName(valueName));
		
		return out.toString();
	}
	

	private String getMyBatisTypeValue(String valueName, String dataType, String paramAlias){
		String caller = new Throwable().getStackTrace()[1].getMethodName();
		logger.debug("-Call Method : " + caller);
		
		String[] includeJdbcType = {"where", "insert","update","merge","delete", "select", "selectCount", "selectList"};
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
		
		out.append("#{");
		if(paramAlias != null) {
			out.append(paramAlias);
			out.append(".");
		}
		out.append(generateHelper.getCamelCaseFieldName(valueName));
		
		if(Arrays.asList(includeJdbcType).indexOf(caller) > -1 && dataType != null) {

			out.append(", jdbcType=");
			for(Entry<String, String> entry : jdbcType.entrySet()) {
				if(dataType.startsWith(entry.getKey())) {
					out.append(entry.getValue());
					break;
				}
			}
		}

		out.append("}");
		
		return out.toString();
	}

	
	private String getTableComments(List<TableDTO> tableList, String tableName){
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
	private List<ForeignInfoDTO> findFKTables(String currentTable, List<ForeignInfoDTO> forgeintables, List<ForeignInfoDTO> out, int parent){
		
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
	private List<String> getFKTable(List<ForeignInfoDTO> foreginTables){
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
	
	private List<ForeignInfoDTO> getFKTableJoinWhereSentence(List<ForeignInfoDTO> foreginTables){
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
	
	

	private List<ForeignInfoDTO> getForgeinColumn(Connection conn){
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

	public String getJavaPackage(String[] levelNames) {
		String out = getJavaPackage();

		List<String> levelPositions = regexUtil.findPatternToList(out, "\\{([a-zA-Z0-9_]+)\\}");

		logger.debug("levelNames.length: {}, levelPositions.size(): {}", levelNames.length, levelPositions.size());
		
		if(levelNames.length < levelPositions.size()) {
			throw new ApplicationException("패키지 래벨설정과 바인드된 레벨 파라메터 배열 개수가 일치하지 않습니다.");
		}
		
		String item = null;
		String levelName = null;
		for(int i = 0; i < levelPositions.size(); i++) {
			item = levelPositions.get(i);
			levelName = levelNames[i];
			if(i == 0 && getMasterSyscd() != null && !levelName.toLowerCase().equals(getMasterSyscd().toLowerCase())) {
				levelName = getMasterSyscd();
			}
			out = out.replace(item, levelName.toLowerCase());
		}
		return out;
	}
	
	public String getJavaPackage() {
		if(basePackage == null) {
			throw new ApplicationException("베이스 패키지가 설정되지 않았습니다.");
		}
		if(subPackage == null) {
			throw new ApplicationException("서브 패키지가 설정되지 않았습니다.");
		}
		return new StringBuilder().append(basePackage).append(".").append(subPackage).toString(); 
	}
	
	public String getSubPackage() {
		return subPackage;
	}

	public void setSubPackage(String subPackage) {
		this.subPackage = subPackage;
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

	public String getFileNamePrefix() {
		return (fileNamePrefix == null ? "" : fileNamePrefix);
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getMasterSyscd() {
		return masterSyscd;
	}

	public void setMasterSyscd(String masterSyscd) {
		this.masterSyscd = masterSyscd;
	}
	
}
