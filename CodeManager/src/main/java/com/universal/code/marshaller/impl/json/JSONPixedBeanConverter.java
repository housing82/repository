package com.universal.code.marshaller.impl.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.marshaller.impl.convert.ExchangeConverter;
import com.universal.code.marshaller.impl.entity.JXDataTransferObject;
import com.universal.code.marshaller.impl.xml.XMLPixedBeanConverter;
import com.universal.code.utils.thread.Local;

@Component
public class JSONPixedBeanConverter {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private boolean marshalLog = false;

	@Autowired
	private XMLPixedBeanConverter pixedConverter;
	
	@Autowired
	private ExchangeConverter exchangeConverter;
	
	public JSONPixedBeanConverter(){
		if(pixedConverter == null) {
			pixedConverter = new XMLPixedBeanConverter();
		}
		if(exchangeConverter == null) {
			exchangeConverter = new ExchangeConverter();
		}
	}
	
	
	/**
	 * unmarshal
	 * @param xml
	 * @return
	 */
	public Object fromJSON(String json) {
		return fromJSON(json, Local.commonHeader().getEncoding());
	}
	
	/**
	 * unmarshal
	 * @param xml
	 * @param rootTagName
	 * @return
	 */
	public Object fromJSON(String json, String encoding) {
		return fromJSON(json, encoding, new JXDataTransferObject());
	}
	
	/**
	 * unmarshal
	 * @param xml
	 * @param rootTagName
	 * @param bean
	 * @return
	 */
	public Object fromJSON(String json, String encoding, Object bean){
		if(encoding == null) encoding = Local.commonHeader().getEncoding();

		String xml = exchangeConverter.jsonObjectToXML(json, encoding, IOperateCode.DEFAULT_ELEMENT_ROOT);
		if(logger.isDebugEnabled() && marshalLog) {
			logger.debug(" -jsonObjectToXML :\n" + xml);
		}
		
		Object out = pixedConverter.fromXML(xml, IOperateCode.DEFAULT_ELEMENT_ROOT, encoding, bean);
		if(logger.isDebugEnabled() && marshalLog) {
			logger.debug(" -pixedBean :\n " + out);
		}
		
		return out;
	}
	
	/**
	 * marshal
	 * @param bean
	 * @param encoding
	 * @return
	 */
	public String toJSON(Object bean){
		
		return toJSON(bean, Local.commonHeader().getEncoding());
	}
	
	/**
	 * marshal
	 * @param bean
	 * @param xml
	 * @return
	 */
	public String toJSON(Object bean, String encoding){
		if(encoding == null) encoding = Local.commonHeader().getEncoding();
		String xml = pixedConverter.toXML(bean, IOperateCode.DEFAULT_ELEMENT_ROOT, encoding);
		if(logger.isDebugEnabled() && marshalLog) {
			logger.debug(" -toXML :\n" + xml);
		}
		
		String out = exchangeConverter.xmlToJson(xml, IOperateCode.DEFAULT_ELEMENT_ROOT, encoding);
		if(logger.isDebugEnabled() && marshalLog) {
			logger.debug(" -xmlToJson :\n" + out);
		}
		
		return out;
	}
	
		
	
}
