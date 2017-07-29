package com.universal.code.marshaller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.marshaller.impl.json.JSONMapConverter;
import com.universal.code.marshaller.impl.json.JSONPixedBeanConverter;
import com.universal.code.utils.thread.Local;

@Component
public class JSONConverter {

	@Autowired
	private JSONMapConverter mapConverter;
	
	@Autowired
	private JSONPixedBeanConverter pixedBeanConverter;
	
	public JSONConverter(){
		if(mapConverter == null) {
			mapConverter = new JSONMapConverter();
		}
		if(pixedBeanConverter == null) {
			pixedBeanConverter = new JSONPixedBeanConverter();
		}
	}
	
	/**
	 * MapConverter (marshal)
	 * @param jsonMap
	 * @param jsonRoot
	 * @return
	 */
	public String mapToJson(Map<String, Object> jsonMap){
		return mapToJson(jsonMap, Local.commonHeader().getEncoding());
	}
	
	
	/**
	 * MapConverter (marshal)
	 * @param jsonMap
	 * @param jsonRoot
	 * @param encoding
	 * @return
	 */
	public String mapToJson(Map<String, Object> jsonMap, String encoding){
		
		String json = mapConverter.toJSON(jsonMap, encoding);
		
		return json;
	}
	
	
	/**
	 * MapConverter (unmarshal)
	 * @param jsonData
	 * @param jsonRoot
	 * @return
	 */
	public Map<String, Object> jsonToMap(String jsonData) {
		return jsonToMap(jsonData, Local.commonHeader().getEncoding());
	}
	
	/**
	 * MapConverter (unmarshal)
	 * @param jsonData
	 * @param jsonRoot
	 * @param encoding
	 * @return
	 */
	public Map<String, Object> jsonToMap(String jsonData, String encoding){
		
		Map<String, Object> map = null;
		
		try {
			map = mapConverter.fromJSON(jsonData, encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	
	/**
	 * PixedBeanConverter (marshal)
	 * @param bean
	 * @param rootTagName
	 * @return
	 */
	public String pixedBeanToJson(Object bean){
		return pixedBeanToJson(bean, Local.commonHeader().getEncoding());
	}

	/**
	 * PixedBeanConverter (marshal)
	 * @param bean
	 * @param rootTagName
	 * @param encoding
	 * @return
	 */
	public String pixedBeanToJson(Object bean, String encoding){
		
		String xml = pixedBeanConverter.toJSON(bean, encoding);
		
		return xml;
	}
	
}
