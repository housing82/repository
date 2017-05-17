package com.universal.code.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ValidateException;
import com.universal.code.utils.SystemUtil;

@Component
public class SearchParam extends ParameterProvider implements PagingParameterHandler {

	private static final long serialVersionUID = 2630827212117482383L;

	private static final Properties DEFAULT_PAGE_PARAMETER_NAMES;
	
	public static final List<String> REQUIRED_PAGING_PARAM;
	
	private Properties customPageParameterNames;
	
	static {
		REQUIRED_PAGING_PARAM = new ArrayList<String>(); 
		REQUIRED_PAGING_PARAM.add(ATTR_PAGE_NUMBER);
		REQUIRED_PAGING_PARAM.add(ATTR_PAGE_SIZE);
		REQUIRED_PAGING_PARAM.add(ATTR_TOTAL_COUNT);
		REQUIRED_PAGING_PARAM.add(ATTR_CURRENT_SIZE);
		REQUIRED_PAGING_PARAM.add(ATTR_START_ROW_NUMBER);
		REQUIRED_PAGING_PARAM.add(ATTR_END_ROW_NUMBER);
		REQUIRED_PAGING_PARAM.add(ATTR_TOTAL_PAGE);
		REQUIRED_PAGING_PARAM.add(ATTR_END_PAGE_NUMBER);
		REQUIRED_PAGING_PARAM.add(ATTR_START_PAGE_NUMBER);
		REQUIRED_PAGING_PARAM.add(ATTR_ORDERS);
		REQUIRED_PAGING_PARAM.add(ATTR_MOVE_NUMBER);
		
		DEFAULT_PAGE_PARAMETER_NAMES = new Properties();
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_PAGE, DEF_PAGE);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_PAGE_NUMBER, DEF_PAGE_NUMBER);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_PAGE_SIZE, DEF_PAGE_SIZE);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_TOTAL_COUNT, DEF_TOTAL_COUNT);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_NOTICE_COUNT, DEF_NOTICE_COUNT);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_START_ROW_NUMBER, DEF_START_ROW_NUMBER);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_END_ROW_NUMBER, DEF_END_ROW_NUMBER);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_CURRENT_SIZE, DEF_CURRENT_SIZE);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_PAGE_REAL_SIZE, DEF_PAGE_REAL_SIZE);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_TOTAL_PAGE, DEF_TOTAL_PAGE);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_END_PAGE_NUMBER, DEF_END_PAGE_NUMBER);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_START_PAGE_NUMBER, DEF_START_PAGE_NUMBER);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_ORDERS, DEF_ORDERS);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_MOVE_NUMBER, DEF_MOVE_NUMBER);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_PAGE_GROUP, DEF_PAGE_GROUP);
		DEFAULT_PAGE_PARAMETER_NAMES.setProperty(ATTR_NEXT_START_ROW_NUMBER, DEF_NEXT_START_ROW_NUMBER);
	}
	
	
	public SearchParam() {

	}
	
	
	public SearchParam(Properties customPageParameterNames) {
		setCustomPageParameterNames(customPageParameterNames);
	}
	
	
	public SearchParam(HttpServletRequest request) {
		initialize(request);
	}


	public SearchParam(HttpServletRequest request, Properties customPageParameterNames) {
		setCustomPageParameterNames(customPageParameterNames);
		initialize(request);
	}
	
	
	@SuppressWarnings("unchecked")
	public SearchParam(Map<String, ? extends Object> map, Class<?> valueType) {
		if( Arrays.class.isAssignableFrom(valueType) || valueType.isArray() ) {
			setRequestMap((Map<String, String[]>) map);
		}
		else {
			setParamMap((Map<String, Object>) map);
		}
	}
		
	
	@SuppressWarnings("unchecked")
	public SearchParam(Map<String, ? extends Object> map, Class<?> valueType, Properties customPageParameterNames) {
		setCustomPageParameterNames(customPageParameterNames);
		if( Arrays.class.isAssignableFrom(valueType) || valueType.isArray() ) {
			setRequestMap((Map<String, String[]>) map);
		}
		else {
			setParamMap((Map<String, Object>) map);
		}
	}
	
	
	public Properties getCustomPageParameterNames() {
		return customPageParameterNames;
	}
	

	public void setCustomPageParameterNames(Properties customPageParameterNames) {
		if(customPageParameterNames != null && customPageParameterNames.size() > 0) {
			for(String key : REQUIRED_PAGING_PARAM) {
				if(customPageParameterNames.getProperty(key) == null) {
					throw new ValidateException("누락된 커스텀 페이징 파라메터 '".concat(key).concat("' 필수 설정 파라메터 입니다.\n필수설정 파라메터 목록 : ").concat(getRequiredParamNames()));
				}
			}
		}
		this.customPageParameterNames = customPageParameterNames;
	}

	public void clearCustomPageParameterNames() {
		customPageParameterNames.clear();
	}
	

	private String getRequiredParamNames() {
		StringBuilder strb = new StringBuilder();
		for(String keyLog : REQUIRED_PAGING_PARAM) {
			if(!strb.toString().equals("")) {
				strb.append(" ");
				strb.append(IOperateCode.STR_COMA);
			}
			strb.append(keyLog);
		}
		return strb.toString();
	}
	
	
	public String getParamName(String attrName){
		String out = null;
		//request page parameter name 
		if( out == null ) {
			out = getString(attrName);
		}

		if( out == null ) {
			if( customPageParameterNames != null && customPageParameterNames.size() > 0) {
				//custom properties page parameter name
				out = customPageParameterNames.getProperty(attrName);
			}
			else {
				//default properties page parameter name 
				out = DEFAULT_PAGE_PARAMETER_NAMES.getProperty(attrName);	
			}
		}
		
		/*
		if(logger.isDebugEnabled()) {
			logger.debug(" getParamName : " + out);
		}
		*/
		return out;
	}


	/**
     * 페이징 속성 을 ParamMap 에서 제거합니다.
     */
	public void removeAllPageAttribute(){
		for(Entry<?, ?> entry : DEFAULT_PAGE_PARAMETER_NAMES.entrySet()) {
			remove(entry.getValue());
		}
		
		if(customPageParameterNames != null) {
			for(Entry<?, ?> entry : customPageParameterNames.entrySet()) {
				remove(entry.getValue());
			}
		}
    }


	@Override
	public void setPageWithCalculate(long totalCount) {
		
		setPageWithCalculate(null, totalCount, null, false);
	}
	
	
	@Override
	public void setPageWithCalculate(String dbVendor, long totalCount) {
		
		setPageWithCalculate(dbVendor, totalCount, null, false);
	}


	@Override
	public void setPageWithCalculate(long totalCount, Properties customParamNames) {
		
		setPageWithCalculate(null, totalCount, customParamNames, false);
	}


	@Override
	public void setPageWithCalculate(String dbVendor, long totalCount, Properties customParamNames) {
		
		setPageWithCalculate(dbVendor, totalCount, customParamNames, false);
	}


	@Override
	public void setPageWithCalculate(long totalCount, Properties customParamNames, boolean withTopRow) {
		
		setPageWithCalculate(null, totalCount, null, withTopRow);
	}


	@Override
	public void setPageWithCalculate(String dbVendor, long totalCount, Properties customParamNames, boolean withTopRow) {
		
    	if(dbVendor == null || dbVendor.isEmpty()) {
    		dbVendor = DEFAULT_VENDOR;
    	}

    	if(customParamNames != null) {
    		setCustomPageParameterNames(customParamNames);
    	}
    	
    	String TOTAL_COUNT = getParamName(ATTR_TOTAL_COUNT);
    	String PAGE_GROUP = getParamName(ATTR_PAGE_GROUP);
    	String PAGE_SIZE = getParamName(ATTR_PAGE_SIZE);
    	String PAGE_NUMBER = getParamName(ATTR_PAGE_NUMBER);
    	
    	/** 데이터 전채 갯수 설정 */
    	set(TOTAL_COUNT, totalCount);
    	
		/** 페이지 그룹 갯수 설정 */
    	int pageGroup = getInteger(PAGE_GROUP, -1);
		if(pageGroup == -1) {
			set(PAGE_GROUP, DEFAULT_PAGE_GROUP);
		}
		
    	/** 페이지당 레코드 갯수 설정 */
		int pageSize = getInteger(PAGE_SIZE, -1);
		if(pageSize == -1) {
			set(PAGE_SIZE, DEFAULT_PAGE_SIZE);
		}
		
    	/** 조회 페이지 번호 설정 */
		int pageNumber = getInteger(PAGE_NUMBER, -1);
		if(pageNumber == -1) {
			set(PAGE_NUMBER, IOperateCode.INTEGER_ZERO_VALUE);
		}
		
    	calculate(dbVendor, withTopRow);
	}


	@Override
	public void calculate(String vendor, boolean pageWithNoticeRow) {
		if(vendor == null) {
			throw new ValidateException("It is null database vendor for paging...");
		}
		
		if(vendor.equalsIgnoreCase(VENDOR_ORACLE)) {
    		calculateOracle(pageWithNoticeRow);
    	}
		else if(vendor.equalsIgnoreCase(VENDOR_MSSQL)) {
    		calculateMSsql(pageWithNoticeRow);
    	}
		else if(vendor.equalsIgnoreCase(VENDOR_MYSQL)) {
    		calculateMysql(pageWithNoticeRow);
    	}
		else if(vendor.equalsIgnoreCase(VENDOR_INFORMIX)) {
    		calculateInformix(pageWithNoticeRow);
    	}
		else if(vendor.equalsIgnoreCase(VENDOR_DB2)) {
    		calculateDB2(pageWithNoticeRow);
    	}
		else if(vendor.equalsIgnoreCase(VENDOR_CUBRID)) {
    		calculateCubrid(pageWithNoticeRow);
    	}
		else {
			throw new ValidateException("This database vendors set up for paging incorrect.");
		}
		
		/*
    	if(logger.isDebugEnabled()) {
    		logger.debug(" :: Paging Calculate end :: ");
    	}
    	*/
    	doTotalPage();
    	/*
    	if(logger.isDebugEnabled()) {
    		logger.debug(" :: do TotalPage end :: ");
    	}
    	*/
    	doStartPage();
    	/*
    	if(logger.isDebugEnabled()) {
    		logger.debug(" :: do StartPage end :: ");
    	}
    	*/
    	doEndPage();
    	/*
    	if(logger.isDebugEnabled()) {
    		logger.debug(" :: do EndPage end :: ");
    	}
    	*/
    	calculateLogger();
	}

	
	@Override
	public void doTotalPage() {

		String TOTAL_PAGE = getParamName(ATTR_TOTAL_PAGE);
		String TOTAL_COUNT = getParamName(ATTR_TOTAL_COUNT);
		String PAGE_SIZE = getParamName(ATTR_PAGE_SIZE);
		set(TOTAL_PAGE, ((getLong(TOTAL_COUNT) - 1) / getInteger(PAGE_SIZE) + 1));
	}

	public void getTotalPage() {

		String TOTAL_PAGE = getParamName(ATTR_TOTAL_PAGE);
		getInteger(TOTAL_PAGE);
	}
	
	@Override
	public void setPage(List<?> list) {

		set(DEF_PAGE, list);
	}


	@Override
	public void setPage(String key, List<?> list) {

		set(key, list);
	}
	
	
	public List<?> getPage() {

		return getPage(DEF_PAGE);
	}


	@Override
	public List<?> getPage(String key) {

		return (List<?>) get(key);
	}
	
	
    public String getLocale() {
    	return getLocale(DEF_LOCALE_PARAM);
    }
    
    
    public String getLocale(String param) {
    	return getString(param);
    }
    
	
	public long getTotalCount() {

		return getTotalCount(DEF_TOTAL_COUNT);
	}


	@Override
	public long getTotalCount(String key) {

		return getLong(key);
	}
	
	
	public int getNoticeCount() {

		return getNoticeCount(DEF_TOTAL_COUNT);
	}


	@Override
	public int getNoticeCount(String key) {

		return getInteger(key);
	}
	
	
	public String getOrders() {
		
		return getOrders(DEF_ORDERS);
	}
	
	@Override
	public String getOrders(String key) {
		
		return getString(key);
	}
	

	private void calculateOracle(boolean pageWithNoticeRow) {
		
		String START_ROW_NUMBER = getParamName(ATTR_START_ROW_NUMBER);
		String PAGE_NUMBER = getParamName(ATTR_PAGE_NUMBER);
		String PAGE_SIZE = getParamName(ATTR_PAGE_SIZE);
		String END_ROW_NUMBER = getParamName(ATTR_END_ROW_NUMBER);
		String CURRENT_SIZE = getParamName(ATTR_CURRENT_SIZE);
		String PAGE_REAL_SIZE = getParamName(ATTR_PAGE_REAL_SIZE);
		String NOTICE_COUNT = getParamName(ATTR_NOTICE_COUNT);
		
		set(START_ROW_NUMBER, getInteger(PAGE_NUMBER) * getInteger(PAGE_SIZE) + 1);
    	set(END_ROW_NUMBER, getLong(START_ROW_NUMBER) + getInteger(PAGE_SIZE) - 1);
		set(CURRENT_SIZE, getInteger(PAGE_NUMBER) * getInteger(PAGE_SIZE));
		if(pageWithNoticeRow) {
			set(PAGE_REAL_SIZE, getInteger(PAGE_SIZE) + getInteger(NOTICE_COUNT));
		}
	}



	private void calculateMysql(boolean pageWithNoticeRow) {
		
		String START_ROW_NUMBER = getParamName(ATTR_START_ROW_NUMBER);
		String PAGE_NUMBER = getParamName(ATTR_PAGE_NUMBER);
		String PAGE_SIZE = getParamName(ATTR_PAGE_SIZE);
		String END_ROW_NUMBER = getParamName(ATTR_END_ROW_NUMBER);
		String CURRENT_SIZE = getParamName(ATTR_CURRENT_SIZE);
		String PAGE_REAL_SIZE = getParamName(ATTR_PAGE_REAL_SIZE);
		String NOTICE_COUNT = getParamName(ATTR_NOTICE_COUNT);
		
    	set(START_ROW_NUMBER,getInteger(PAGE_NUMBER) * getInteger(PAGE_SIZE));
    	set(END_ROW_NUMBER,getLong(START_ROW_NUMBER) + getInteger(PAGE_SIZE));
    	set(CURRENT_SIZE,getInteger(PAGE_NUMBER) * getInteger(PAGE_SIZE));
    	if(pageWithNoticeRow) { 
    		set(PAGE_REAL_SIZE, getInteger(PAGE_SIZE) + getInteger(NOTICE_COUNT));
    	}
	}



	private void calculateMSsql(boolean pageWithNoticeRow) {
		
		String START_ROW_NUMBER = getParamName(ATTR_START_ROW_NUMBER);
		String PAGE_NUMBER = getParamName(ATTR_PAGE_NUMBER);
		String PAGE_SIZE = getParamName(ATTR_PAGE_SIZE);
		String END_ROW_NUMBER = getParamName(ATTR_END_ROW_NUMBER);
		String CURRENT_SIZE = getParamName(ATTR_CURRENT_SIZE);
		String PAGE_REAL_SIZE = getParamName(ATTR_PAGE_REAL_SIZE);
		String NOTICE_COUNT = getParamName(ATTR_NOTICE_COUNT);
		
    	set(START_ROW_NUMBER, getInteger(PAGE_NUMBER) * getInteger(PAGE_SIZE) + 1);
    	set(END_ROW_NUMBER, getLong(START_ROW_NUMBER) + getInteger(PAGE_SIZE) - 1);
    	set(CURRENT_SIZE, (getInteger(PAGE_NUMBER) + 1) * getInteger(PAGE_SIZE));
    	if(pageWithNoticeRow) {
    		set(PAGE_REAL_SIZE, getInteger(PAGE_SIZE) + getInteger(NOTICE_COUNT));
    	}
	}



	private void calculateInformix(boolean pageWithNoticeRow) {
		
		String START_ROW_NUMBER = getParamName(ATTR_START_ROW_NUMBER);
		String PAGE_NUMBER = getParamName(ATTR_PAGE_NUMBER);
		String PAGE_SIZE = getParamName(ATTR_PAGE_SIZE);
		String END_ROW_NUMBER = getParamName(ATTR_END_ROW_NUMBER);
		String CURRENT_SIZE = getParamName(ATTR_CURRENT_SIZE);
		String PAGE_REAL_SIZE = getParamName(ATTR_PAGE_REAL_SIZE);
		String NOTICE_COUNT = getParamName(ATTR_NOTICE_COUNT);
		String NEXT_START_ROW_NUMBER = getParamName(ATTR_NEXT_START_ROW_NUMBER);
		String TOTAL_COUNT = getParamName(ATTR_TOTAL_COUNT);
		
    	set(START_ROW_NUMBER, getInteger(PAGE_NUMBER) * getInteger(PAGE_SIZE) + 1);
    	set(END_ROW_NUMBER, getLong(START_ROW_NUMBER) + getInteger(PAGE_SIZE) - 1);
    	set(CURRENT_SIZE, getInteger(PAGE_NUMBER) * getInteger(PAGE_SIZE));
    	set(NEXT_START_ROW_NUMBER, getLong(TOTAL_COUNT) - (((getInteger(PAGE_NUMBER) + 1) * getInteger(PAGE_SIZE)) - getInteger(PAGE_SIZE)));
    	if(pageWithNoticeRow) {
    		set(PAGE_REAL_SIZE, getInteger(PAGE_SIZE) + getInteger(NOTICE_COUNT));
    	}
	}



	private void calculateDB2(boolean pageWithNoticeRow) {
		
		calculateOracle(pageWithNoticeRow);
	}



	private void calculateCubrid(boolean pageWithNoticeRow) {
		
		calculateOracle(pageWithNoticeRow);
	}


	@Override
	public void doStartPage() {
		
		String PAGE_NUMBER = getParamName(ATTR_PAGE_NUMBER);
		String PAGE_GROUP = getParamName(ATTR_PAGE_GROUP);
		String START_PAGE_NUMBER = getParamName(ATTR_START_PAGE_NUMBER);
		
		int prev = ((((getInteger(PAGE_NUMBER) + 1) % getInteger(PAGE_GROUP) == 0 ? 
			(getInteger(PAGE_NUMBER) + 1) / getInteger(PAGE_GROUP) : 
			((getInteger(PAGE_NUMBER) + 1) / getInteger(PAGE_GROUP))+1) - 1) * getInteger(PAGE_GROUP)) + 1;
		
		set(START_PAGE_NUMBER, prev);
	}


	@Override
	public void doEndPage() {
		
		String PAGE_NUMBER = getParamName(ATTR_PAGE_NUMBER);
		String PAGE_GROUP = getParamName(ATTR_PAGE_GROUP);
		String TOTAL_PAGE = getParamName(ATTR_TOTAL_PAGE);
		String END_PAGE_NUMBER = getParamName(ATTR_END_PAGE_NUMBER);
		
		int next = ((((getInteger(PAGE_NUMBER) + 1) % getInteger(PAGE_GROUP) == 0 ? 
			(getInteger(PAGE_NUMBER) + 1) / getInteger(PAGE_GROUP) : 
			((getInteger(PAGE_NUMBER) + 1) / getInteger(PAGE_GROUP)) + 1) * getInteger(PAGE_GROUP)) + 1) - 1;
		
		if(next > getInteger(TOTAL_PAGE)) next = getInteger(TOTAL_PAGE);
		
		set(END_PAGE_NUMBER, next);
	}
	
    public void calculateLogger() {
    	
    	if(logger.isDebugEnabled()) {
    		
    		StringBuilder strbld = new StringBuilder();
    		Properties savedProp = null;
    		
    		if( customPageParameterNames != null && customPageParameterNames.size() > 0) {
    			//custom properties page parameter name
    			savedProp = customPageParameterNames; 
			}
			else {
				//default properties page parameter name
				savedProp = DEFAULT_PAGE_PARAMETER_NAMES;
			}

    		strbld.append("[ Paging Calculate Result ]");
			for(Entry<?, ?> entry : savedProp.entrySet()) {
				strbld.append(SystemUtil.LINE_SEPARATOR);
				strbld.append(" - Attr : "); 
				strbld.append(entry.getKey());
				strbld.append(", Name : ");
				strbld.append(entry.getValue());
				strbld.append(", Value = ");
				strbld.append(get(entry.getValue()));
			}
			
			strbld.append(SystemUtil.LINE_SEPARATOR);
			logger.debug(strbld.toString());
		}
    }
}
