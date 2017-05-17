package com.universal.code.parameter;

import java.util.List;
import java.util.Properties;


public interface PagingParameterHandler {

	public static final int DEFAULT_PAGE_GROUP = 10;

	public static final int DEFAULT_PAGE_SIZE  = 40;
	
	public static final String DEFAULT_VENDOR = "ORACLE";
	
	public static final String VENDOR_ORACLE = "ORACLE";
	public static final String VENDOR_MSSQL = "MSSQL";
	public static final String VENDOR_MYSQL = "MYSQL";
	public static final String VENDOR_INFORMIX = "INFORMIX";
	public static final String VENDOR_DB2 = "DB2";
	public static final String VENDOR_CUBRID = "CUBRID";
	
	public static final String DEF_PAGE = "page";
	public static final String DEF_PAGE_NUMBER = "pageNumber";
	public static final String DEF_PAGE_SIZE = "pageSize";
	public static final String DEF_PAGE_GROUP = "pageGroup";
	public static final String DEF_TOTAL_COUNT = "totalCount";
	public static final String DEF_NOTICE_COUNT = "noticeCount";
	public static final String DEF_START_ROW_NUMBER = "startRowNumber";
	public static final String DEF_END_ROW_NUMBER = "endRowNumber";
	public static final String DEF_CURRENT_SIZE = "currentSize";
	public static final String DEF_PAGE_REAL_SIZE = "pageRealSize";
	public static final String DEF_TOTAL_PAGE = "totalPage";
	
	public static final String DEF_START_PAGE_NUMBER = "startPageNumber";
	public static final String DEF_END_PAGE_NUMBER = "endPageNumber";
	
	public static final String DEF_ORDERS = "orders";
	public static final String DEF_MOVE_NUMBER = "moveNumber";
	public static final String DEF_NEXT_START_ROW_NUMBER = "nextStartRowNumber";
	public final static String DEF_LOCALE_PARAM = "locale";
	
	//PAGING PARAMETER NAMES
	public static final String ATTR_PAGE = "pageAttr";
	public static final String ATTR_PAGE_NUMBER = "pageNumberAttr";
	public static final String ATTR_PAGE_SIZE = "pageSizeAttr";
	public static final String ATTR_PAGE_GROUP = "pageGroupAttr";
	public static final String ATTR_TOTAL_COUNT = "totalCountAttr";
	public static final String ATTR_NOTICE_COUNT = "noticeCountAttr";
	public static final String ATTR_START_ROW_NUMBER = "startRowNumAttr";
	public static final String ATTR_END_ROW_NUMBER = "endRowNumAttr";
	public static final String ATTR_CURRENT_SIZE = "currentSizeAttr";
	public static final String ATTR_PAGE_REAL_SIZE = "pageRealSizeAttr";
	public static final String ATTR_TOTAL_PAGE = "totalPageAttr";
	
	public static final String ATTR_START_PAGE_NUMBER = "startPageNumberAttr";
	public static final String ATTR_END_PAGE_NUMBER = "endPageNumberAttr";
	
	public static final String ATTR_ORDERS = "ordersAttr";
	public static final String ATTR_MOVE_NUMBER = "moveNumberAttr";
	public static final String ATTR_NEXT_START_ROW_NUMBER = "nextstartRowNumAttr";
    
	public void setPageWithCalculate(long totalCount);
    
    public void setPageWithCalculate(String dbVendor, long totalCount);

    public void setPageWithCalculate(long totalCount, Properties customParamNames);
    
    public void setPageWithCalculate(String dbVendor, long totalCount, Properties customParamNames);

    public void setPageWithCalculate(long totalCount, Properties customParamNames, boolean withTopRow);
    
    public void setPageWithCalculate(String dbVendor, long totalCount, Properties customParamNames, boolean withTopRow);
    
    public void calculate(String vendor, boolean pageWithNoticeRow);

    public void doTotalPage();

    public void setPage(List<?> list);
    
    public void setPage(String key, List<?> list);
    
    public List<?> getPage(String key);
    
    public long getTotalCount(String key);

    public int getNoticeCount(String key);
    
    public String getOrders(String key);
    
    public void doStartPage();

    public void doEndPage();
    
}
