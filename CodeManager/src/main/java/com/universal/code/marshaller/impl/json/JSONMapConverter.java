package com.universal.code.marshaller.impl.json;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * 
 * @author ksw
 * @description JSON 을 Map 으로 Map 을 JSON 로 변환해주는 마샬러 
 * JSON 의 Node 가 부모로부터 1개이 상의 배열일 경우 JSON 에 등록된 순서 대로 index 를 보장해주고
 * 부모로 부터 유일한 Node 일경우 JSON에 등록된 순서로의 index 를 보장하지 않는다. 
 * index가 보장되는 Node 의 경우 sample json [com/innorules/interfaces/utils/marshaller/sample/json_test_in.json] 기준  col 과 row 가 된다.
 */

@Component
public class JSONMapConverter implements IOperateCode {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private boolean marshalLog = false;
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> fromJSON(String jsonContents, String encoding){
		
		Map<String, Object> out = null;
		
		if(jsonContents != null) {
			
			//JSONObject jsonObject = JSONObject.fromObject( jsonContents );
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON( jsonContents );  
			
			if(jsonObject != null && Map.class.isAssignableFrom(jsonObject.getClass())) {
				
				out = (Map<String, Object>) jsonObject;
			}	
		}
		
		if(logger.isDebugEnabled() && marshalLog) {
			logger.debug(" out : " + out);
		}
		
 		return out;
	}

	
	public String toJSON(Map<String, Object> jsonMap, String encoding){
		
		String out = null;
		
		if(jsonMap != null) {
			
			//JSONObject jsonObject = JSONObject.fromObject( jsonMap );
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON( jsonMap );  
			
			if(jsonObject != null) {
				out = jsonObject.toString();
			}	
		}
		
		if(logger.isDebugEnabled() && marshalLog) {
			logger.debug(" out : " + out);
		}
		
		return out;
	}
	
}
