package com.universal.code.utils.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.dto.CommonHeader;
import com.universal.code.parameter.SearchParam;
import com.universal.code.utils.SystemUtil;
import com.universal.code.utils.UniqueId4j;

@Component
public class Local {
	
	private static final Logger logger = LoggerFactory.getLogger(Local.class);
	
	
	
	/**
	 * Thread local variable containing each thread's CommonHeader
	 */
    private static final ThreadLocal<CommonHeader> threadLocalHeader = new ThreadLocal<CommonHeader>() {
            @Override protected CommonHeader initialValue() {
            	
            	CommonHeader header = new CommonHeader();
            	header.setGuid(UniqueId4j.getId());
            	header.setStartTimeMillis(SystemUtil.currentTimeMillis());
            	
            	if(logger.isDebugEnabled()) {
	            	logger.debug(new StringBuilder()
	            		.append(SystemUtil.LINE_SEPARATOR)
	            		.append(" ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■ ")
	            		.append(SystemUtil.LINE_SEPARATOR)
	            		.append(" ■■ [START]    ")
	            		.append(SystemUtil.LINE_SEPARATOR)
	            		.append(" ■■ initialValue    ")
	            		.append(SystemUtil.LINE_SEPARATOR)
	            		.append(" ■■ guid : " + header.getGuid() )
	            		.append(SystemUtil.LINE_SEPARATOR)
	            		.append(" ■■ startTimeMillis : " + header.getStartTimeMillis() )
	            		.append(SystemUtil.LINE_SEPARATOR)
	            		.append(" ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■ ")
	            		.append(SystemUtil.LINE_SEPARATOR)
	            		.toString()
	            	);
            	}
            	
                return header;
        } 
    }; 
    
    /**
     * Local.commonHeader()
     * @return
     */
    public static CommonHeader commonHeader() {
        return threadLocalHeader.get();
    }
    
    /**
     * Local.searchParam()
     * @return
     */
    public static SearchParam searchParam() {
    	return threadLocalHeader.get().getSearchParam();
    }


    /**
     * @Name : getId 
     * @Description : Returns the current thread's unique ID, assigning it if necessary
     * @LastUpdated : 2014. 9. 13. 오후 7:06:14
     * @return
     */
    public static String getId() {
        return threadLocalHeader.get().getGuid();
    }
     
    /**
     * @Name : setId 
     * @Description : ThreadLocal threadLocal id set
     * @LastUpdated : 2014. 9. 13. 오후 7:06:21
     * @param id
     */
    public static void setId(String id) {
    	threadLocalHeader.get().setGuid(id);
    }
     
    /**
     * @Name : remove 
     * @Description : ThreadLocal threadLocal data remove
     * @LastUpdated : 2014. 9. 13. 오후 7:06:50
     */
    public static void remove() {
    	
    	if(logger.isDebugEnabled()) {
	    	logger.debug(new StringBuilder()
	    		.append(SystemUtil.LINE_SEPARATOR)
	    		.append(" ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■ ")
	    		.append(SystemUtil.LINE_SEPARATOR)
	            .append(" ■■ [END]    ")
	    		.append(SystemUtil.LINE_SEPARATOR)
        		.append(" ■■ guid : " + commonHeader().getGuid() )	    		
        		.append(SystemUtil.LINE_SEPARATOR)
        		.append(" ■■ RunTime runTimeMillis : ")
				.append(commonHeader().getRunTimeMillis())
				.append("(ms)")
	    		.append(SystemUtil.LINE_SEPARATOR)
				.append(" ■■ StartTimeMillis : ")
				.append(commonHeader().getStartTimeMillis())
	    		.append(SystemUtil.LINE_SEPARATOR)
				.append(" ■■ EndTimeMillis : ")
				.append(commonHeader().getEndTimeMillis())
	    		.append(SystemUtil.LINE_SEPARATOR)
	    		.append(" ■■ ThreadLocal END ")
	    		.append(SystemUtil.LINE_SEPARATOR)
	    		.append(" ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■ ")
	    		.append(SystemUtil.LINE_SEPARATOR)
	    		.toString()
			);
    	}

    	threadLocalHeader.remove();
    }
    
}
