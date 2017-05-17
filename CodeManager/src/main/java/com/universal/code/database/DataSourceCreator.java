package com.universal.code.database;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ValidateException;
import com.universal.code.utils.PropertyUtil;
import com.universal.code.utils.ResourceUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;

@Component
public class DataSourceCreator {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceCreator.class);
	
	@Autowired
	private ResourceUtil resource;
	
	@Autowired
	private PropertyUtil property;
	
	public DataSourceCreator(){
		if( resource == null ) {
			resource = new ResourceUtil();
		}
		if( property == null ) {
			property = new PropertyUtil(); 
		}
	}
	
	private Collection<String> getConnectionInitSqls(Properties properties){
		
		Collection<String> connectionInitSqls = null;
		String initSqls = properties.getProperty("connectionInitSqls");
		if(initSqls != null) {
			connectionInitSqls = new ArrayList<String>();
			for(String sql : initSqls.split("([,|;|\n|"+SystemUtil.LINE_SEPARATOR+"])")){
				if(StringUtil.isNotEmpty(sql.trim())) {
					connectionInitSqls.add(sql);
				}
			}
		}
		return connectionInitSqls;
	}
	
	public DataSource create(String propertiesPath, String keyPrefix){
		Properties properties = resource.load(propertiesPath);
		return create(properties, keyPrefix, getConnectionInitSqls(properties));
	}
	
	public DataSource create(File propertiesFile, String keyPrefix){
		Properties properties = resource.load(propertiesFile);
		return create(properties, keyPrefix, getConnectionInitSqls(properties));
	}
	
	public DataSource create(Properties properties, String keyPrefix){
		return create(properties, keyPrefix, getConnectionInitSqls(properties));
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
	 * maxActive > dbcp1, maxTotal > dbcp2
	 * maxIdle
	 * minIdle
	 * initialSize
	 * maxWait > dbcp1, maxWaitMills > dbcp2
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
	 * @param properties
	 * @param connectionInitSqlList
	 * @return
	 */
	public DataSource create(Properties properties, String keyPrefix, Collection<String> connectionInitSqlList){
		if(logger.isDebugEnabled()) {
			logger.debug("[START] create: {}", properties);
		}
		if(properties == null) {
			throw new ValidateException("DBCP설정 정보가 존재하지 않습니다.");
		}
		
		Properties connectionProperties = null;
		DataSource dbcp = null;
		
		String prefixKeyWord = StringUtil.NVL(keyPrefix, "");
		if(logger.isDebugEnabled()) {
			logger.debug("prefixKeyWord : [" + prefixKeyWord + "]");
		}
		
		String propertyName = null;
		connectionProperties = new Properties();
		for(Entry<Object, Object> entry : properties.entrySet()){
			propertyName = (String) entry.getKey(); 
			if(propertyName.startsWith(prefixKeyWord)) {
				propertyName = propertyName.substring(propertyName.lastIndexOf(IOperateCode.STR_DOT) + IOperateCode.STR_DOT.length());
				connectionProperties.setProperty(propertyName, (String) entry.getValue());
			}
		}
		
		if(connectionProperties.size() == 0) {
			throw new ValidateException("DBCP설정 정보가 존재하지 않습니다.");
		}
		else {
			if( logger.isDebugEnabled() ) {
				resource.out(connectionProperties);
			}
			
			try {
		        if( StringUtil.isEmpty(connectionProperties.getProperty("defaultAutoCommit")) ) {
		        	throw new ValidateException("Not found DefaultAutoCommit...");
		        }
		        if( StringUtil.isEmpty(connectionProperties.getProperty("driverClassName")) ) {
		        	throw new ValidateException("Not found DriverClassName...");
		        }
				if( StringUtil.isEmpty(connectionProperties.getProperty("url")) ) {
					throw new ValidateException("Not found URL...");
				}
				if( StringUtil.isEmpty(connectionProperties.getProperty("username")) ) {
					throw new ValidateException("Not found Username...");
				}
				if( StringUtil.isEmpty(connectionProperties.getProperty("password")) ) {
					throw new ValidateException("Not found Password...");
				}
				
				if(DataSourceConfigurer.isUseDBCP2()) {
					//dbcp2
					dbcp = new org.apache.commons.dbcp2.BasicDataSource();
					((org.apache.commons.dbcp2.BasicDataSource) dbcp).setConnectionInitSqls(connectionInitSqlList);
				}
				else {
					//dbcp1
					dbcp = new org.apache.commons.dbcp.BasicDataSource();
					((org.apache.commons.dbcp.BasicDataSource) dbcp).setConnectionInitSqls(connectionInitSqlList);
				}

				PropertyDescriptor[] writeProperties = PropertyUtils.getPropertyDescriptors(dbcp);
				for(Entry<Object, Object> entry : connectionProperties.entrySet()){
					propertyName = (String) entry.getKey();
					for (PropertyDescriptor writeBean : writeProperties) {
						if (propertyName.equals(writeBean.getName())) {
							property.setProperty(dbcp, propertyName, entry.getValue());
							break;
						}
					}
				}
			} catch (Exception e) {
				throw new ValidateException(e);
			} 
			finally {
				if( connectionProperties != null ) {
					connectionProperties.clear();			
				}
			}
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[END] create");
		}
		return dbcp;
	}

}
