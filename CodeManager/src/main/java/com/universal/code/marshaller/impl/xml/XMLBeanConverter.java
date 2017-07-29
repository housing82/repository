package com.universal.code.marshaller.impl.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.tomcat.util.http.mapper.Mapper;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.universal.code.utils.thread.Local;

@Component
public class XMLBeanConverter {

	
	public String toXML(Object bean, String root) {
		return this.toXML(bean, root, Local.commonHeader().getEncoding());
	}
	
	
	/**
	 * 바인드된 빈을 XML String 으로 변환
	 * @param bean
	 * @param root
	 * @param encoding
	 * @return
	 */
	public String toXML(Object bean, String root, String encoding) {

		String out = null;
		XStream xStream = null;
		Mapper mapper = null;
		
		try {
			
			if(encoding == null || encoding.isEmpty()) {
				encoding = Local.commonHeader().getEncoding();
			}
			
			xStream = new XStream(new DomDriver(encoding, new XmlFriendlyNameCoder("__", "_")));
			xStream.alias(root, bean.getClass());
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Writer writer = new OutputStreamWriter(outputStream, encoding);
			xStream.toXML(bean, writer);
			
			out = outputStream.toString(encoding);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return out;
	}

	
	public Object fromXML(String xmlString, Object bean, String root) {
		return this.fromXML(xmlString, root, bean, Local.commonHeader().getEncoding());
	}
	
	
	/**
	 * XML String 데이터를 바인된 bean 으로 변환
	 * @param xmlString
	 * @param root
	 * @param bean
	 * @param encoding
	 * @return
	 */
	public Object fromXML(String xmlString, String root, Object bean, String encoding) {
		
		if(encoding == null || encoding.isEmpty()) {
			encoding = Local.commonHeader().getEncoding();
		}
		
		Object out = null;
		XStream xStream = new XStream(new DomDriver(encoding, new XmlFriendlyNameCoder("__", "_")));
		xStream.alias(root, bean.getClass());
		
		out = xStream.fromXML(xmlString);
		
		return out;
	}
	
	
}
