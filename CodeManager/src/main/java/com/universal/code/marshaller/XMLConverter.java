package com.universal.code.marshaller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.exception.ApplicationException;
import com.universal.code.marshaller.impl.xml.XMLMapConverter;
import com.universal.code.marshaller.impl.xml.XMLPixedBeanConverter;
import com.universal.code.utils.thread.Local;

@Component
public class XMLConverter {

	@Autowired
	private XMLMapConverter mapConverter;
	
	@Autowired
	private XMLPixedBeanConverter pixedBeanConverter;
	
	public XMLConverter(){
		if(mapConverter == null) {
			mapConverter = new XMLMapConverter();
		}
		if(pixedBeanConverter == null) {
			pixedBeanConverter = new XMLPixedBeanConverter();
		}
	}
	
	/**
	 * PixedBeanConverter (marshal)
	 * @param bean
	 * @param rootTagName
	 * @return
	 */
	public String pixedBeanToXml(Object bean, String rootTagName){
		return pixedBeanToXml(bean, rootTagName, Local.commonHeader().getEncoding());
	}

	/**
	 * PixedBeanConverter (marshal)
	 * @param bean
	 * @param rootTagName
	 * @param encoding
	 * @return
	 */
	public String pixedBeanToXml(Object bean, String rootTagName, String encoding){
		
		String xml = pixedBeanConverter.toXML(bean, rootTagName, encoding);
		
		return xml;
	}
	
	
	/**
	 * PixedBeanConverter (unmarshal)
	 * @param xml
	 * @param rootTagName
	 * @return
	 */
	public Object xmlToPixedBean(String xml, String rootTagName){
		return xmlToPixedBean(xml, rootTagName, Local.commonHeader().getEncoding());
	}
	
	/**
	 * PixedBeanConverter (unmarshal)
	 * @param xml
	 * @param rootTagName
	 * @param encoding
	 * @return
	 */
	public Object xmlToPixedBean(String xml, String rootTagName, String encoding){
		return xmlToPixedBean(xml, rootTagName, encoding, null);
	}
	
	
	public Object xmlToPixedBean(String xml, String rootTagName, Object beanType){
		return xmlToPixedBean(xml, rootTagName, null, beanType);
	}
	
	/**
	 * PixedBeanConverter (unmarshal)
	 * @param xml
	 * @param rootTagName
	 * @param encoding
	 * @param beanType
	 * @return
	 */
	public Object xmlToPixedBean(String xml, String rootTagName, String encoding, Object beanType){
		
		Object bean = pixedBeanConverter.fromXML(xml, rootTagName, encoding, beanType);
		
		return bean;
	}
	
	
	/**
	 * MapConverter (marshal)
	 * @param xmlMap
	 * @param xmlRoot
	 * @return
	 */
	public String mapToXml(LinkedHashMap<String, Object> xmlMap, String xmlRoot){
		return mapToXml(xmlMap, xmlRoot, null);
	}
	
	
	/**
	 * MapConverter (marshal)
	 * @param xmlMap
	 * @param xmlRoot
	 * @param encoding
	 * @return
	 */
	public String mapToXml(LinkedHashMap<String, Object> xmlMap, String xmlRoot, String encoding){
		
		String xml = mapConverter.toXML(xmlMap, xmlRoot, encoding);
		
		return xml;
	}
	
	
	/**
	 * MapConverter (unmarshal)
	 * @param xmlData
	 * @param xmlRoot
	 * @return
	 */
	public Map<String, Object> xmlToMap(String xmlData, String xmlRoot) {
		return xmlToMap(xmlData, xmlRoot, null);
	}
	
	/**
	 * MapConverter (unmarshal)
	 * @param xmlData
	 * @param xmlRoot
	 * @param encoding
	 * @return
	 */
	public Map<String, Object> xmlToMap(String xmlData, String xmlRoot, String encoding){
		
		Map<String, Object> map = null;
		
		try {
			map = mapConverter.fromXML(xmlData, xmlRoot, encoding);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		return map;
	}
	
}
