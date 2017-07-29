package com.universal.code.marshaller.impl.convert;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.marshaller.impl.json.JSONMapConverter;
import com.universal.code.marshaller.impl.xml.XMLMapConverter;
import com.universal.code.messages.MessageHelper;
import com.universal.code.parameter.validation.ParamValidate;
import com.universal.code.parameter.validation.dto.ParamValidateDTO;
import com.universal.code.utils.thread.Local;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

/**
 * 
 * @author ksw
 * @description JSON 을 Map 으로 Map 을 JSON 로 변환해주는 마샬러 
 * JSON 의 Node 가 부모로부터 1개이 상의 배열일 경우 JSON 에 등록된 순서 대로 index 를 보장해주고
 * 부모로 부터 유일한 Node 일경우 JSON에 등록된 순서로의 index 를 보장하지 않는다. 
 * index가 보장되는 Node 의 경우 sample json [com/innorules/interfaces/utils/marshaller/sample/json_test_in.json] 기준  col 과 row 가 된다.
 */

@Component
public class ExchangeConverter{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private boolean marshalLog = false;
	
	public String jsonObjectToXML(String jsonStr, String rootName) {
		return jsonObjectToXML(jsonStr, Local.commonHeader().getEncoding(), rootName, null, null);
	}
	
	public String jsonObjectToXML(String jsonStr, String encoding, String rootName) {
		return jsonObjectToXML(jsonStr, encoding, rootName, null, null);
	}
	
	public String jsonObjectToXML(String jsonStr, String encoding, String rootName, String elementName) {
		return jsonObjectToXML(jsonStr, encoding, rootName, null, elementName, false);
	}
	
	public String jsonObjectToXML(String jsonStr, String encoding, String rootName, boolean typeHint) {
		return jsonObjectToXML(jsonStr, encoding, rootName, null, null, typeHint);
	}
	
	public String jsonObjectToXML(String jsonStr, String encoding, String rootName, Object[] expandElements) {
		return jsonObjectToXML(jsonStr, encoding, rootName, expandElements, null);
	}
	
	public String jsonObjectToXML(String jsonStr, String encoding, String rootName, Object[] expandElements, String elementName) {
		return jsonObjectToXML(jsonStr, encoding, rootName, expandElements, elementName, false);
	}
	
	public String jsonObjectToXML(String jsonStr, String encoding, String rootName, Object[] expandElements, boolean typeHint) {
		return jsonObjectToXML(jsonStr, encoding, rootName, expandElements, null, typeHint);
	}
	
	public String jsonObjectToXML(String jsonStr, String encoding, String rootName, Object[] expandElements, String elementName, boolean typeHint){
		String out = null;
		if(encoding == null) encoding = Local.commonHeader().getEncoding();

		ParamValidate validate = new ParamValidate();
		validate.addParam(new ParamValidateDTO((Object) jsonStr,  new String[]{"required"}, MessageHelper.message("ISNOT_EXIST_PARAMS", new Object[]{MessageHelper.message("MSG_JSON")})));
		validate.addParam(new ParamValidateDTO((Object) rootName, new String[]{"required"}, MessageHelper.message("ISNOT_EXIST_PARAMS", new Object[]{MessageHelper.message("MSG_ROOT_NAME")})));
		validate.addParam(new ParamValidateDTO((Object) encoding, new String[]{"required"}, MessageHelper.message("ISNOT_EXIST_PARAMS", new Object[]{MessageHelper.message("MSG_ENCODING")})));
		validate.execute();
		
		try {
			rootName = rootName.trim();
			jsonStr = jsonStr.trim();
			encoding = encoding.trim();
			
			JSON json = JSONSerializer.toJSON( jsonStr );

			XMLSerializer xmlSerializer = new XMLSerializer();
			//xmlSerializer.setNamespace("signature", "http://localhost"); //element namespace ( xmlns:signature="http://localhost" )
			
			xmlSerializer.setTypeHintsEnabled( typeHint ); //json type="string"
			if(typeHint) {
				xmlSerializer.setTypeHintsCompatibility( false );  //json json_type="boolean"
			}

			if(expandElements == null) {
				expandElements = IOperateCode.JSON_EXPAND_ELEMENTS;
			}
			setExpandChildElements(json, Arrays.asList(expandElements));
			
			xmlSerializer.setRootName( rootName );  
			if(elementName != null) {
				xmlSerializer.setElementName( elementName );
			}
			
			out = xmlSerializer.write( json, encoding );	
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ApplicationException(e);
		}

		return out;
	}

	
	private int setExpandChildElements(JSON json, List<Object> elementName){
		int out = 0;
		if(logger.isDebugEnabled() && marshalLog) {
			logger.debug(" CanonicalName : " + json.getClass().getCanonicalName());
		}
		
		Object jsonKey = null;
		Object jsonElement = null;
		if(JSONObject.class.isAssignableFrom(json.getClass())) {
			Iterator<?> keys = ((JSONObject) json).keys();

			while(keys.hasNext()){
				jsonKey = keys.next();
				jsonElement = ((JSONObject) json).get(jsonKey);
				
				if(JSON.class.isAssignableFrom(jsonElement.getClass())) {
					if(elementName.indexOf(jsonKey) > -1 && JSONArray.class.isAssignableFrom(jsonElement.getClass())) {
						if(logger.isDebugEnabled() && marshalLog) {
							logger.debug(" ---JSONArray key : " + jsonKey);
						}
						((JSONArray) jsonElement).setExpandElements( true );
					}
					else {
						if(logger.isDebugEnabled() && marshalLog) {
							logger.debug(" ---JSONObject key : " + jsonKey);
						}
					}
					setExpandChildElements((JSON) jsonElement, elementName);
				}
				else {
					if(logger.isDebugEnabled() && marshalLog) {
						logger.debug(" -key : " + jsonKey + ", value : " + jsonElement);
					}
				}
			}
		}
		else if(JSONArray.class.isAssignableFrom(json.getClass())) {
			for(int i = 0; i < ((JSONArray) json).size();i++){
				jsonElement = ((JSONArray) json).get(i);
				if(JSON.class.isAssignableFrom(jsonElement.getClass())) {
					setExpandChildElements((JSON) jsonElement, elementName);
				}
				else {
					if(logger.isDebugEnabled() && marshalLog) {
						logger.debug(" +array value : " + jsonElement);
					}
				}
			}
		}

		return out;
	}
	
	/**
	 * 현재 사용안함
	 * @param json
	 * @return
	 */
	private int setExpandElements(JSON json){
		int out = 0;
		JSONObject jsonObject = null;
		JSONArray  jsonArray = null;
		
		if(json != null && JSONObject.class.isAssignableFrom(json.getClass())) {
			
			jsonObject = ((JSONObject) json).getJSONObject( IOperateCode.META_COLUMN_INFO );
			if(jsonObject != null && jsonObject.size() > 0) {
				jsonArray = jsonObject.getJSONArray( IOperateCode.ELEMENT_COL );
				if(jsonArray != null && jsonArray.size() > 0) {
					jsonArray.setExpandElements( true ); //Use JSON to XML NODE name field name
				}
			}
			
			jsonObject = ((JSONObject) json).getJSONObject( IOperateCode.META_COLUMN_DATA );
			if(jsonObject != null && jsonObject.size() > 0) {
				jsonArray = jsonObject.getJSONArray( IOperateCode.ELEMENT_ROW );
				if(jsonArray != null && jsonArray.size() > 0) {
					jsonArray.setExpandElements( true ); //Use JSON to XML NODE name field name
				}
			}
		}
		
		return out;
	}
	
	
	public String xmlToJsonlib(String xmlStr){
		String out = null;

		XMLSerializer xmlSerializer = new XMLSerializer();  
		
		//xmlSerializer.setForceTopLevelObject(true);// create xml rootNode to first(top) json element
		
		JSON json = xmlSerializer.read( xmlStr );  
		out = json.toString(2);  
				
		return out;
	}
	
	
	public String xmlToJson(String xml, String rootName, String encoding){

		Map<String, Object> xmlMap = new XMLMapConverter().fromXML(xml, rootName, true);
		
		String out = new JSONMapConverter().toJSON(xmlMap, encoding);
		
		return out;
	} 
	
}
