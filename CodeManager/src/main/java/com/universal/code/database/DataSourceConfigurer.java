package com.universal.code.database;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Driver;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.ReflectUtil;
import com.universal.code.utils.SystemUtil;


public class DataSourceConfigurer {
	//DataSourceConfigurer.DBCP1_TYPE
	public static final String DBCP1_TYPE = "org.apache.commons.dbcp.BasicDataSource";
	public static final String DBCP2_TYPE = "org.apache.commons.dbcp2.BasicDataSource";
	
    private static boolean USE_DBCP2 = false;
    static {
    	if( Integer.parseInt(SystemUtil.JAVA_VM_SPECIFICATION_VERSION.replace(IOperateCode.STR_DOT, "")) > 16 
    		&& ReflectUtil.existsClass("org.apache.commons.dbcp2.BasicDataSource", "org.apache.commons.pool2.ObjectPool")) {
    		DataSourceConfigurer.USE_DBCP2 = true; 
    	}
    }
    
    public static boolean isUseDBCP2(){
    	return USE_DBCP2;
    }
    
    private String name;
    private String factory;
    private boolean isDefault;

    public String initialContext;
    private String dataSourceName;
	private File connectionPropertiesFile;
	private Properties clientInfo;
	private String dataSourceKeyPrefix;

	//BasicDataSource 1 and 2 Property
	private volatile Boolean defaultAutoCommit; //type miss
	private volatile List<?> connectionInitSqls; //type miss
	
	private transient Boolean defaultReadOnly;
	private volatile int defaultTransactionIsolation;
	private volatile String defaultCatalog;
	private boolean cacheState;
	private Driver driver;
	private String driverClassName;
	private ClassLoader driverClassLoader;
	private boolean lifo;
	private int maxIdle;
	private int minIdle;
	private int initialSize;
	private boolean poolPreparedStatements;
	private int maxOpenPreparedStatements;
	private boolean testOnCreate;
	private boolean testOnBorrow;
	private boolean testOnReturn;
	private long timeBetweenEvictionRunsMillis; //유효 커넥션 검사 주기
	private int numTestsPerEvictionRun;
	private long minEvictableIdleTimeMillis;
	private long softMinEvictableIdleTimeMillis;
	private String evictionPolicyClassName;
	private boolean testWhileIdle; //커넥션이 유효한지 검사 유무 
	private volatile String password;
	private String url;
	private String username;
	private volatile String validationQuery;
	private volatile int validationQueryTimeout;
	private boolean accessToUnderlyingConnectionAllowed;
	private long maxConnLifetimeMillis;
	private String jmxName;
	private boolean enableAutoCommitOnReturn;
	private boolean rollbackOnReturn;
	private Properties connectionProperties;
	private volatile DataSource dataSource;
	private volatile PrintWriter logWriter;
	private boolean closed;

	//dbcp1 only
	private int maxActive;
	private long maxWait; 
	private volatile boolean restartNeeded;
	
	//dbcp2 only
	private int maxTotal;
	private long maxWaitMillis;
	private Integer defaultQueryTimeout;
	
	
	public DataSourceConfigurer(){
		this.defaultCatalog = "PUBLIC";
		
		this.defaultAutoCommit = true; //dbcp1 default : true, dbcp2 default : null

		this.defaultReadOnly = null; //dbcp1 & dbcp2 default : null 

		this.defaultTransactionIsolation = -1;

		this.defaultQueryTimeout = null;

		this.cacheState = true;

		this.driver = null;

		this.driverClassName = null;

		this.driverClassLoader = null;

		this.lifo = true;

		this.maxActive = 8; //dbcp1
		
		this.maxTotal = 8; //dbcp2

		this.maxIdle = 8;

		this.minIdle = 0;

		this.initialSize = 0;

		this.maxWait = -1L; //dbcp1
		
		this.maxWaitMillis = -1L; //dbcp2

		this.poolPreparedStatements = false;

		this.maxOpenPreparedStatements = -1;

		this.testOnCreate = false;

		this.testOnBorrow = true;

		this.testOnReturn = false;

		this.timeBetweenEvictionRunsMillis = -1L;

		this.numTestsPerEvictionRun = 3;

		this.minEvictableIdleTimeMillis = 1800000L;

		this.softMinEvictableIdleTimeMillis = -1L;

		this.evictionPolicyClassName = "org.apache.commons.pool2.impl.DefaultEvictionPolicy";

		this.testWhileIdle = false; 

		this.password = null;

		this.url = null;

		this.username = null;

		this.validationQuery = null;

		this.validationQueryTimeout = -1;

		this.accessToUnderlyingConnectionAllowed = false;

		this.maxConnLifetimeMillis = -1L;

		this.jmxName = null;

		this.enableAutoCommitOnReturn = true;

		this.rollbackOnReturn = true;

		this.connectionProperties = new Properties();

		this.dataSource = null;

		try {
			this.logWriter = new PrintWriter(new OutputStreamWriter(System.out, IOperateCode.DEFAULT_ENCODING_UTF8));
		} catch (UnsupportedEncodingException e) {
			throw new ApplicationException("DataSourceConfigurer LogWriter 초기화 장애발생", e);
			
		}
	
		this.restartNeeded = false;
	}
	
	/**
	 * DataSourceConfigurer init
	 * @param name
	 * @param factory
	 * @param isDefault
	 * @param initialContext
	 * @param dataSourceName
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 * @param initialSize
	 * @param maxActive
	 * @param minIdle
	 * @param maxIdle
	 * @param validationQuery
	 * @param validationQueryTimeout
	 */
	public DataSourceConfigurer(String name, String factory, boolean isDefault,
			String initialContext, String dataSourceName,
			String driverClassName, String url, String username,
			String password, int initialSize, int maxActive, int minIdle,
			int maxIdle, String validationQuery, int validationQueryTimeout) {
		this.name = name;
		this.factory = factory;
		this.isDefault = isDefault;
		this.initialContext = initialContext;
		this.dataSourceName = dataSourceName;
		this.driverClassName = driverClassName;
		this.url = url;
		this.username = username;
		this.password = password;
		this.initialSize = initialSize;
		this.maxActive = maxActive;
		this.minIdle = minIdle;
		this.maxIdle = maxIdle;
		this.validationQuery = validationQuery;
		this.validationQueryTimeout = validationQueryTimeout;
	}

	public DataSourceConfigurer(Properties connectionProperties){
		this.connectionProperties = connectionProperties;
	}
	
	public DataSourceConfigurer(File connectionPropertiesFile){
		this.connectionPropertiesFile = connectionPropertiesFile;
	}

	public DataSourceConfigurer(Properties connectionProperties, String dataSourceKeyPrefix){
		this.connectionProperties = connectionProperties;
		this.dataSourceKeyPrefix = dataSourceKeyPrefix;
	}
	
	public DataSourceConfigurer(File connectionPropertiesFile, String dataSourceKeyPrefix){
		this.connectionPropertiesFile = connectionPropertiesFile;
		this.dataSourceKeyPrefix = dataSourceKeyPrefix;
	}	

	public DataSourceConfigurer(Properties connectionProperties, Properties clientInfo){
		this.connectionProperties = connectionProperties;
		this.clientInfo = clientInfo;
	}
	
	public DataSourceConfigurer(File connectionPropertiesFile, Properties clientInfo){
		this.connectionPropertiesFile = connectionPropertiesFile;
		this.clientInfo = clientInfo;
	}

	public DataSourceConfigurer(Properties connectionProperties, Properties clientInfo, String dataSourceKeyPrefix){
		this.connectionProperties = connectionProperties;
		this.clientInfo = clientInfo;
		this.dataSourceKeyPrefix = dataSourceKeyPrefix;
	}
	
	public DataSourceConfigurer(File connectionPropertiesFile, Properties clientInfo, String dataSourceKeyPrefix){
		this.connectionPropertiesFile = connectionPropertiesFile;
		this.clientInfo = clientInfo;
		this.dataSourceKeyPrefix = dataSourceKeyPrefix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getInitialContext() {
		return initialContext;
	}

	public void setInitialContext(String initialContext) {
		this.initialContext = initialContext;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public File getConnectionPropertiesFile() {
		return connectionPropertiesFile;
	}

	public void setConnectionPropertiesFile(File connectionPropertiesFile) {
		this.connectionPropertiesFile = connectionPropertiesFile;
	}

	public Properties getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(Properties clientInfo) {
		this.clientInfo = clientInfo;
	}

	public String getDataSourceKeyPrefix() {
		return dataSourceKeyPrefix;
	}

	public void setDataSourceKeyPrefix(String dataSourceKeyPrefix) {
		this.dataSourceKeyPrefix = dataSourceKeyPrefix;
	}

	public Boolean getDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}

	public Boolean getDefaultReadOnly() {
		return defaultReadOnly;
	}

	public void setDefaultReadOnly(Boolean defaultReadOnly) {
		this.defaultReadOnly = defaultReadOnly;
	}

	public int getDefaultTransactionIsolation() {
		return defaultTransactionIsolation;
	}

	public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
		this.defaultTransactionIsolation = defaultTransactionIsolation;
	}

	public Integer getDefaultQueryTimeout() {
		return defaultQueryTimeout;
	}

	public void setDefaultQueryTimeout(Integer defaultQueryTimeout) {
		this.defaultQueryTimeout = defaultQueryTimeout;
	}

	public String getDefaultCatalog() {
		return defaultCatalog;
	}

	public void setDefaultCatalog(String defaultCatalog) {
		this.defaultCatalog = defaultCatalog;
	}

	public boolean isCacheState() {
		return cacheState;
	}

	public void setCacheState(boolean cacheState) {
		this.cacheState = cacheState;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public ClassLoader getDriverClassLoader() {
		return driverClassLoader;
	}

	public void setDriverClassLoader(ClassLoader driverClassLoader) {
		this.driverClassLoader = driverClassLoader;
	}

	public boolean isLifo() {
		return lifo;
	}

	public void setLifo(boolean lifo) {
		this.lifo = lifo;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxTotal() {
		if(maxTotal == -1 && maxActive > -1) {
			maxTotal = maxActive;
		}
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	public long getMaxWaitMillis() {
		if(maxWaitMillis == -1 && maxWait > -1) {
			maxWaitMillis = maxWait;
		}
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public boolean isPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	public void setPoolPreparedStatements(boolean poolPreparedStatements) {
		this.poolPreparedStatements = poolPreparedStatements;
	}

	public int getMaxOpenPreparedStatements() {
		return maxOpenPreparedStatements;
	}

	public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
		this.maxOpenPreparedStatements = maxOpenPreparedStatements;
	}

	public boolean isTestOnCreate() {
		return testOnCreate;
	}

	public void setTestOnCreate(boolean testOnCreate) {
		this.testOnCreate = testOnCreate;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public int getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public long getSoftMinEvictableIdleTimeMillis() {
		return softMinEvictableIdleTimeMillis;
	}

	public void setSoftMinEvictableIdleTimeMillis(
			long softMinEvictableIdleTimeMillis) {
		this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
	}

	public String getEvictionPolicyClassName() {
		return evictionPolicyClassName;
	}

	public void setEvictionPolicyClassName(String evictionPolicyClassName) {
		this.evictionPolicyClassName = evictionPolicyClassName;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public int getValidationQueryTimeout() {
		return validationQueryTimeout;
	}

	public void setValidationQueryTimeout(int validationQueryTimeout) {
		this.validationQueryTimeout = validationQueryTimeout;
	}

	public List<?> getConnectionInitSqls() {
		return connectionInitSqls;
	}

	public void setConnectionInitSqls(List<?> connectionInitSqls) {
		this.connectionInitSqls = connectionInitSqls;
	}

	public boolean isAccessToUnderlyingConnectionAllowed() {
		return accessToUnderlyingConnectionAllowed;
	}

	public void setAccessToUnderlyingConnectionAllowed(
			boolean accessToUnderlyingConnectionAllowed) {
		this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
	}

	public long getMaxConnLifetimeMillis() {
		return maxConnLifetimeMillis;
	}

	public void setMaxConnLifetimeMillis(long maxConnLifetimeMillis) {
		this.maxConnLifetimeMillis = maxConnLifetimeMillis;
	}

	public String getJmxName() {
		return jmxName;
	}

	public void setJmxName(String jmxName) {
		this.jmxName = jmxName;
	}

	public boolean isEnableAutoCommitOnReturn() {
		return enableAutoCommitOnReturn;
	}

	public void setEnableAutoCommitOnReturn(boolean enableAutoCommitOnReturn) {
		this.enableAutoCommitOnReturn = enableAutoCommitOnReturn;
	}

	public boolean isRollbackOnReturn() {
		return rollbackOnReturn;
	}

	public void setRollbackOnReturn(boolean rollbackOnReturn) {
		this.rollbackOnReturn = rollbackOnReturn;
	}

	public Properties getConnectionProperties() {
		return connectionProperties;
	}

	public void setConnectionProperties(Properties connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public PrintWriter getLogWriter() {
		return logWriter;
	}

	public void setLogWriter(PrintWriter logWriter) {
		this.logWriter = logWriter;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public boolean isRestartNeeded() {
		return restartNeeded;
	}

	public void setRestartNeeded(boolean restartNeeded) {
		this.restartNeeded = restartNeeded;
	}	

}
