package com.universal.code.database;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.ResourceUtil;
import com.universal.code.utils.StringUtil;

@Component
public class JDBCManager implements Serializable {

	private static final long serialVersionUID = -7661756513283903184L;

	private static final Logger logger = LoggerFactory.getLogger(JDBCManager.class);
	
	private final String DEF_PROPS_PATH = "WEB-INF/config/datasource/jdbc.properties"; //"WEB-INF/classes/".concat(this.getClass().getPackage().getName().replace(".","/").concat("/jdbc.properties"));
    
	@Autowired
	private ResourceUtil resource;

	@Autowired
	private DataSourceCreator dataSourceCreator;

	public Connection getConnection(Properties connectionProperties){
		return getConnection(connectionProperties, null);
	}

	public Connection getConnection(File connectionPropertiesFile){
		if( resource == null ) {
			resource = new ResourceUtil();
		}
		return getConnection(resource.load(connectionPropertiesFile), null);
	}
	
	public Connection getConnection(DataSourceConfigurer jdbcDataSourceConfigurer) {

		if(jdbcDataSourceConfigurer.getConnectionPropertiesFile() != null) {
			if( resource == null ) {
				resource = new ResourceUtil();
			}
			return getConnection(resource.load(jdbcDataSourceConfigurer.getConnectionPropertiesFile()), jdbcDataSourceConfigurer.getDataSourceKeyPrefix());
		}
		else if(jdbcDataSourceConfigurer.getConnectionProperties() != null) {
			return getConnection(jdbcDataSourceConfigurer.getConnectionProperties(), jdbcDataSourceConfigurer.getDataSourceKeyPrefix());
		}
		else {
			throw new ApplicationException("데이터베이스 연결정보가 잘못되었거나 존재하지않습니다.");
		}
	}
	
	/**
	 * [Datasource Properties Key Names]
	 * 
	 * defaultAutoCommit
	 * defaultReadOnly
	 * defaultTransactionIsolation
	 * defaultCatalog
	 * driverClassName
	 * driverClassLoader
	 * maxActive
	 * maxIdle
	 * minIdle
	 * initialSize
	 * maxWait
	 * poolPreparedStatements
	 * maxOpenPreparedStatements
	 * testOnBorrow
	 * testOnReturn
	 * timeBetweenEvictionRunsMillis
	 * numTestsPerEvictionRun
	 * minEvictableIdleTimeMillis
	 * testWhileIdle
	 * password
	 * url
	 * username
	 * validationQuery
	 * validationQueryTimeout
	 * accessToUnderlyingConnectionAllowed
	 * connectionProperties
	 * connectionInitSqls
	 * 
	 * @param connectionProperties
	 * @param dataSourceKeyPrefix : connectionProperties의 프로퍼티키의 시작구분문자 ex) localhost, orasystem
	 * @return
	 * 
	 * (connectionProperties configuration example)
	 * 
	 * > jdbc part
	 * props.setProperty("localhost.jdbc.driverClassName", "oracle.jdbc.driver.OracleDriver");
	 * props.setProperty("localhost.jdbc.url", "jdbc:oracle:thin:@localhost:1521:ora11g");
	 * props.setProperty("localhost.jdbc.username", "system");
	 * props.setProperty("localhost.jdbc.password", "oracle");
	 * props.setProperty("localhost.jdbc.initialSize", "5");
	 * props.setProperty("localhost.jdbc.maxActive", "100");
	 * props.setProperty("localhost.jdbc.maxIdle", "20");
	 * props.setProperty("localhost.jdbc.maxWait", "30000");
	 * props.setProperty("localhost.jdbc.poolPreparedStatements", "true");
	 * props.setProperty("localhost.jdbc.defaultAutoCommit", "false");
	 * props.setProperty("localhost.jdbc.validationQuery", "SELECT 1 FROM DUAL");
	 * 
	 * > clientInfo part
	 * props.setProperty("localhost.clientInfo.client_info", "ADMIN-SYSTEM");
	 * props.setProperty("localhost.clientInfo.username", "ADMIN-LOCALHOST"); 	 
	 * 
	 */
	public Connection getConnection(Properties connectionProperties, String dataSourceKeyPrefix) {
		
		Connection conn = null;
		Properties props = null;

		//use test start
		if( resource == null ) resource = new ResourceUtil();
		if( dataSourceCreator == null ) dataSourceCreator = new DataSourceCreator();
		//use test end
		
		if( connectionProperties == null ) {
			props = resource.load(DEF_PROPS_PATH);
		}
		else {
			props = connectionProperties;
		}
		
        if( props == null ) {
        	throw new ApplicationException(" connectionProperties is Null!! ");
        }
        
        String prefixWord = StringUtil.NVL(dataSourceKeyPrefix, ""); 
        if(!prefixWord.isEmpty()) {
        	prefixWord = prefixWord.concat(IOperateCode.STR_DOT);
        }

		try {
			if( logger.isDebugEnabled() ) {
				resource.out(props);
			}

			Properties clientInfo = new Properties();
			Iterator<Object> iter = connectionProperties.keySet().iterator();
			String keyName = null;
			String keyPrefix = null;
			
			while(iter.hasNext()){
				keyName = (String) iter.next();
				keyPrefix = prefixWord.concat("clientInfo.");
				if( keyName.startsWith(keyPrefix) ) {
					clientInfo.setProperty(keyName.substring(keyPrefix.length()), connectionProperties.getProperty(keyName));
				}
			}
			
			DataSource ds = dataSourceCreator.create(props, prefixWord.concat("jdbc."));

			conn = ds.getConnection();
			if( conn != null && clientInfo.size() > 0 ) {
				conn.setClientInfo(clientInfo);
			}
			
		} catch (SQLException e) {
			throw new ApplicationException(e);
		}
		
		
		if(logger.isDebugEnabled()) {
			if(conn != null){
				logger.debug(" - universial connection success : " + conn);
			}else{
				logger.debug(" - universial connection fail.....");
			}	
		}
		return conn;
	}
}
