package com.universal.runner.etc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.coder.URLCoder;
import com.universal.code.constants.IOperateCode;
import com.universal.code.database.JDBCManager;
import com.universal.code.dto.TableDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;


public class CreateModify2ByteColumnSql {

	private static final Logger logger = LoggerFactory.getLogger(CreateModify2ByteColumnSql.class);
	
	private static Properties props = new Properties();

	private static String SOURCE_ROOT;
	
	private static String RESOURCE_PATH;
	
	private static String READ_EXCEL_NAME;
	
	private static String WRITE_EXCEL_NAME; 
	
	private static String EXCEL_VERSION_DATE_FORMAT;
	
	private static String BASE_PACKAGE;
	
	private static String MASTER_SYSCD;
	
	private static Map<String, List<Map<String, Object>>> ANALYZED_MAP;
	
	private FileUtil fileUtil;
	
	private JDBCManager jdbcManager;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet results;
	
	public CreateModify2ByteColumnSql() {
		// ...
		fileUtil = new FileUtil();
		jdbcManager = new JDBCManager();
	}
	
	static {
		EXCEL_VERSION_DATE_FORMAT = "yyyyMMddHHmmsss";

		props.setProperty("jdbc.driverClassName", "oracle.jdbc.driver.OracleDriver");
		props.setProperty("jdbc.url", "jdbc:oracle:thin:@192.168.5.12:1522:kait");
		props.setProperty("jdbc.username", "KAIT_TST");
		props.setProperty("jdbc.password", "KAIT_TST");
		props.setProperty("jdbc.initialSize", "5");
		//props.setProperty("jdbc.maxActive", "100");
		props.setProperty("jdbc.maxIdle", "20");
		props.setProperty("jdbc.maxWait", "30000");
		props.setProperty("jdbc.poolPreparedStatements", "true");
		props.setProperty("jdbc.defaultAutoCommit", "false");
		//props.setProperty("jdbc.validationQuery", "SELECT 1 FROM DUAL");
		// 클라우드 접속이 안되었을경우 로컬에 있는 파일을 봄
		String currenPath = CreateModify2ByteColumnSql.class.getClassLoader().getResource(".").getPath();
		RESOURCE_PATH = new StringBuilder().append(URLCoder.getInstance().getURLDecode(currenPath, "UTF-8")).append("../../document/sql").toString();
		logger.debug("RESOURCE_PATH: {}", RESOURCE_PATH);
		
		
		
	}
	

	
	@Test
	public void run() {
		
		String reservedKey = "#{select}";
		String modifyAlterSqlBody = fileUtil.getTextFileContent(RESOURCE_PATH.concat("/").concat("_alter.sql"));
		String selectSqlBody = fileUtil.getTextFileContent(RESOURCE_PATH.concat("/").concat("_select.sql"));
		
		String[] selectArray = selectSqlBody.split(SystemUtil.LINE_SEPARATOR);
		
		int row = 1;
		int addCount = 0;
		String modifySql = null;
		StringBuilder s = new StringBuilder();
		for(String select : selectArray) {
			
			if(row > 1) {
				s.append("UNION ALL");
				s.append(SystemUtil.LINE_SEPARATOR);
			}
			s.append(select);
			s.append(SystemUtil.LINE_SEPARATOR);
			
			if(row == 2000) {
				logger.debug(modifyAlterSqlBody.replace(reservedKey, s.toString()));
				
				modifySql = addOn(modifyAlterSqlBody.replace(reservedKey, s.toString()));
				row = 0;
				s = new StringBuilder();
				fileUtil.mkfile(RESOURCE_PATH, "_resize_ddl.sql", modifySql, IOperateCode.DEFAULT_ENCODING, true, false);
				
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					throw new ApplicationException(e);
				}
				
				addCount++;
			}
			row++;
		}
		
		logger.debug("#addCount: {}", addCount);
	}


	
	public String addOn(String query) {
		String modifySql = null;
		StringBuilder modifySqls = new StringBuilder();
		
		try {
			conn = jdbcManager.getConnection(props);
			conn.setReadOnly(true);
			logger.debug("#getConnection: {}", conn);					

			pstmt = conn.prepareStatement(query); // create a prepareStatement
			results = pstmt.executeQuery(); // execute select statement
			
			while (results.next()) {
				
				modifySql = StringUtil.NVL(results.getString("MODIFY_SQL"), "").concat(SystemUtil.LINE_SEPARATOR);
				logger.debug("modifySql : {}", modifySql);
				modifySqls.append(modifySql);
			}
		
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			close(conn, pstmt, results);
		}
		
		return modifySqls.toString();
	}
	
	
	void close(Connection conn, PreparedStatement pstmt, ResultSet results) {
		try {
			
 			if (results != null && !results.isClosed()) {
				results.close();
				logger.debug("$results.close");
			}
			if (pstmt != null && !pstmt.isClosed()) {
				pstmt.close();
				logger.debug("$pstmt.close");
			}
			
			if (conn != null && !conn.isClosed()) {
				conn.close();
				logger.debug("#conn.close : {}", conn.isClosed());
			}
			
			logger.debug("@conn : {}", conn);
			
		} catch (SQLException e) {
			throw new ApplicationException(e);
		}
	}
}
