package com.universal.code.bxm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.constants.IOperateCode;
import com.universal.code.database.JDBCManager;
import com.universal.code.dto.TableDTO;
import com.universal.code.dto.VtSchemaDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.sql.MetaViewSQL;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;

public class BxmOmmGenerateUtil {

	private final static Logger logger = LoggerFactory.getLogger(BxmOmmGenerateUtil.class);
	
	// BXM ECLIPSE HD SOURCE ROOT
	private String sourceRoot;
	// ASIS DAO DTO BASE PACKAGE
	private String javaPackage; 
	// Database Connection infomation
	private Properties databaseConfig;
	// Create OMM File 
	private boolean createFile = false;
	private JDBCManager jdbcManager;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet results;
	private StringUtil stringUtil;
	private FileUtil fileUtil;
	private GenerateHelper generateHelper;
	
	public BxmOmmGenerateUtil() {
		jdbcManager = new JDBCManager();
		stringUtil = new StringUtil();
		fileUtil = new FileUtil();
		generateHelper = new GenerateHelper();
	}
	
	public void execute(String inTables, List<String> fixedOmmTags) {
		logger.debug("[START] execute: {}", getDatabaseConfig());
		logger.debug("★ SourceRoot: {}", getSourceRoot());
		logger.debug("★ JavaPackage: {}", getJavaPackage());
		
		List<TableDTO> tableList = null;
		List<VtSchemaDTO> columnList = null;
		StringBuilder strbd = null;
		String fileName = null;
		String fieldName = null;
		String description = null;
		
		try {
			conn = jdbcManager.getConnection(getDatabaseConfig());
			logger.debug("conn: {}", conn);
			// SELECT DATABASE
			tableList = getTableList(conn, inTables);
			int fileSeq = 0;
			String currentTableName = null;
			
			for(TableDTO table : tableList) {
				if(currentTableName != null && !table.getTableName().equals(currentTableName)) {
					fileSeq = 1;
				}
				else {
					fileSeq++;
				}
				currentTableName = table.getTableName();
				/*				
					클래스명 50자리
					- SC, BC, DAO 의 정의 된 50자리 이내 클래스명
					일련번호 2자리 : 01~99
					입출력유형 3자리
					- In : 입력, Out : 출력, IO : 입.출력
					- 배치는 IO로 표현함
					- Sub + 일련번호[2자리] : DTO 정의 시 DTO 포함 할 때
					Default 생성 DTO명(Table당 생성)
					- Default DTO의 순번은 ‘00’으로 함
				*/
				fileName = "D".concat(stringUtil.getFirstCharUpperCase(stringUtil.getCamelCaseString(table.getTableName()))).concat(stringUtil.leftPad(Integer.toString(fileSeq), 2, "0")).concat("IO");
				description = StringUtil.NVL(table.getTableComments(), table.getTableName());
				if(!description.equals(table.getTableName())) {
					description = description.concat(" ( ").concat(table.getTableName()).concat(" )");
				}
				
				strbd = new StringBuilder();
				strbd.append("OMM ");
				strbd.append(getJavaPackage());
				strbd.append(".");
				strbd.append(fileName);
				strbd.append(SystemUtil.LINE_SEPARATOR);
				strbd.append(SystemUtil.LINE_SEPARATOR);
				
				strbd.append("<description=\"");
				strbd.append(description);
				strbd.append("\">");
				strbd.append(SystemUtil.LINE_SEPARATOR);
				strbd.append("{");
				strbd.append(SystemUtil.LINE_SEPARATOR);
				
				logger.debug("{}: {}", table.getTableName(), table.getTableComments());
				// SELECT DATABASE
				columnList = getColumnList(conn, table.getTableName(), false);
				
				for(VtSchemaDTO vtSchema : columnList) {
					fieldName = generateHelper.getCamelCaseFieldName(vtSchema.getColumn_name());
					
					strbd.append("	");
					strbd.append(vtSchema.getJava_type());
					strbd.append(" ");
					strbd.append(fieldName);
					strbd.append("<");
					strbd.append("length=");
					strbd.append(vtSchema.getData_length());
					strbd.append(" description=\"");
					strbd.append(vtSchema.getComments());

					if (!vtSchema.getConstraints().isEmpty() || !vtSchema.getIndexes().isEmpty()) {
						strbd.append(" [");
						strbd.append(vtSchema.getConstraints());
						if(!vtSchema.getConstraints().isEmpty() && !vtSchema.getIndexes().isEmpty()) strbd.append(" ");
						strbd.append(vtSchema.getIndexes());
						strbd.append("]");
					}
					strbd.append("\"  >;");
					strbd.append(SystemUtil.LINE_SEPARATOR);
				}
				
				if(fixedOmmTags != null) {
					for(String ommTag : fixedOmmTags) {
						strbd.append("	");
						strbd.append(ommTag);
						strbd.append(SystemUtil.LINE_SEPARATOR);
					}
				}
				
				
				
				strbd.append("}");
				
				// 테이블별 OMM 생성
				logger.debug(strbd.toString());
				
				if(isCreateFile())
				fileUtil.mkfile(
						new StringBuilder().append(sourceRoot).append("/").append(getJavaPackage().replace(".", "/")).toString()
						, fileName.concat(".omm")
						, strbd.toString()
						, IOperateCode.DEFAULT_ENCODING
						, false
						, true); 
				
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
			e.printStackTrace();
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


}
