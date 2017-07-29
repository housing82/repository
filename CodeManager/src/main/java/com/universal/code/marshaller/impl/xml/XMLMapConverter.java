package com.universal.code.marshaller.impl.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.mapper.Mapper;
import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.marshaller.impl.helper.ConverterHelper;
import com.universal.code.messages.MessageHelper;
import com.universal.code.parameter.validation.ParamValidate;
import com.universal.code.parameter.validation.dto.ParamValidateDTO;
import com.universal.code.utils.thread.Local;

/**
 * 
 * @author ksw
 * @description XML 을 Map 으로 Map 을 XML 로 변환해주는 마샬러 
 * XML 의 Node 가 부모로부터 1개이 상의 배열일 경우 XML 에 등록된 순서 대로 index 를 보장해주고
 * 부모로 부터 유일한 Node 일경우 XML에 등록된 순서로의 index 를 보장하지 않는다. 
 * index가 보장되는 Node 의 경우 sample xml [com/innorules/interfaces/utils/marshaller/sample/xml_test_in.xml] 기준  col 과 row 가 된다.
 */

@Component
public class XMLMapConverter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private boolean marshalLog = false;
	
	@Autowired
	private ConverterHelper converterHelper;
	
	public XMLMapConverter(){
		if(converterHelper == null) {
			converterHelper = new ConverterHelper();
		}
	}
	
	public Map<String, Object> fromXML(String xmlData, String root) {
		return fromXML(xmlData, root, Local.commonHeader().getEncoding());
	}
	
	public Map<String, Object> fromXML(String xmlData, String root, String encoding) {
		return fromXML(xmlData, root, encoding, false);
	}
	
	public Map<String, Object> fromXML(String xmlData, String root, boolean attrToField) {
		return fromXML(xmlData, root, Local.commonHeader().getEncoding(), attrToField);
	}
	
	/**
	 * unmarshal
	 * @param xmlData
	 * @param root
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> fromXML(String xmlData, String root, String encoding, boolean attrToField) {
		if(encoding == null) encoding = Local.commonHeader().getEncoding();

		ParamValidate validate = new ParamValidate();
		validate.addParam(new ParamValidateDTO((Object) xmlData, new String[]{"required"}, MessageHelper.message("ISNOT_EXIST_PARAMS", new Object[]{MessageHelper.message("MSG_XML")})));
		validate.addParam(new ParamValidateDTO((Object) root, 	 new String[]{"required"}, MessageHelper.message("ISNOT_EXIST_PARAMS", new Object[]{MessageHelper.message("MSG_ROOT_NAME")})));
		validate.addParam(new ParamValidateDTO((Object) encoding,new String[]{"required"}, MessageHelper.message("ISNOT_EXIST_PARAMS", new Object[]{MessageHelper.message("MSG_ENCODING")})));
		validate.execute();
		
		Map<String, Object> out = null;
		try {
			root = root.trim();
			xmlData = xmlData.trim();
			encoding = encoding.trim();
			
			XStream xStream = new XStream(new DomDriver(encoding, new XmlFriendlyNameCoder("__", "_")));
			Mapper mapper = xStream.getMapper();
			xStream.alias(root, Map.class);
			xStream.registerConverter(new Marshaller(mapper, attrToField));
			out = (Map<String, Object>) xStream.fromXML(xmlData); 
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ApplicationException(e);
		}
		
		
		return out;
	}

	
	public String toXML(Map<String, Object> xmlMap, String root) throws ApplicationException {
		return toXML(xmlMap, root, Local.commonHeader().getEncoding());
	}
	
	/**
	 * marshal
	 * @param xmlMap
	 * @param root
	 * @return
	 * @throws ApplicationException
	 */
	public String toXML(Map<String, Object> xmlMap, String root, String encoding) {
		if(encoding == null) encoding = Local.commonHeader().getEncoding();
		
		String out = null;
		XStream xStream = null;
		Mapper mapper = null;
		
		try {
			
			xStream = new XStream(new DomDriver(encoding, new XmlFriendlyNameCoder("__", "_")));  // does not require XPP3 library
			//XStream xStream = new XStream(new StaxDriver()); // does not require XPP3 library starting with Java 6
			
			mapper = xStream.getMapper();
			xStream.alias(root, Map.class);
			xStream.registerConverter(new Marshaller(mapper));
			
			/**
			//DomDriver bind a writer created the xml header */
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Writer writer = new OutputStreamWriter(outputStream, encoding);
			converterHelper.writeXmlHeader(writer, encoding);
			xStream.toXML(xmlMap, writer);
		 	
			out = outputStream.toString(encoding);
			
			/**
			//StaxDriver (java 6) with PrettyPrintWriter
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Writer writer = new OutputStreamWriter(outputStream, encoding);
			HierarchicalStreamWriter xmlWriter = new PrettyPrintWriter(writer);
			writer.write(XML_HEADER.replace(XML_HEADER_ENCODING, encoding));
			writer.write(SystemUtil.LINE_SEPARATOR);
			xStream.marshal(xmlMap, xmlWriter);
			out = new String(outputStream.toByteArray(), encoding);
			*/
	
			/**
			//DomDriver (append to xml header string)
			StringBuffer xmlBuffer = new StringBuffer();
			xmlBuffer.append(XML_HEADER.replace(XML_HEADER_ENCODING, encoding));
			xmlBuffer.append(SystemUtil.LINE_SEPARATOR);
			xmlBuffer.append(xStream.toXML(xmlMap).toString());
			out = xmlBuffer.toString(); 
			*/
		
		} catch (UnsupportedEncodingException e) {
			throw new ApplicationException(e);
		}
		
		
		return out;
	}
	
	private class Marshaller extends AbstractCollectionConverter implements Converter {

		private boolean attributeToField = false;
		
		public Marshaller(Mapper mapper) {
			super(mapper);
		}

		public Marshaller(Mapper mapper, boolean attrToField) {
			super(mapper);
			this.attributeToField = attrToField;
		}
		
		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class type) {
			
			return type.equals(java.util.HashMap.class)
					|| type.equals(java.util.Hashtable.class)
					|| (type.getName().equals("java.util.LinkedHashMap") || type.getName().equals("sun.font.AttributeMap"));
		}

		/**
		 * toXML
		 */
		@SuppressWarnings("rawtypes")
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			
			Map map = (Map) value;
			Entry entry = null;
			String key = "";
			Object values = null;
			
			for (Object obj : map.entrySet()) {
				entry = (Entry) obj;
				key = entry.getKey().toString();
				values = entry.getValue();
				
				if(values == null) continue; 
				
				//values is Collection
				if(Collection.class.isAssignableFrom(values.getClass())) {
					
					for(Object childValues : ((List) values)) {
						
						if(childValues == null) continue; 
						
						//childValues is Map
						if(childValues.getClass().isAssignableFrom(HashMap.class)) {
							
							if(logger.isDebugEnabled() && marshalLog) {
								logger.debug(" [inner] hashMap key = " + key);
							}

							if( !key.equals(IOperateCode.XML_NODE_ATTR) ) {

								writeXmlNodeMapValues(writer, key, childValues, context);
							}
						}
						else {
							if( !key.equals(IOperateCode.XML_NODE_VAL) ) writer.startNode(key);
							
							//childValues is Array
							if(childValues.getClass().isAssignableFrom(ArrayList.class)) {
								
								for(Object subArray : ((List) childValues)) {
									marshal(subArray, writer, context);
								}
							}
							//childValues is Text
							else {
								writeXmlNodeTextValue(writer, childValues.toString());
							}
							
							if( !key.equals(IOperateCode.XML_NODE_VAL) ) writer.endNode();
						}
					}
				}
				//values is Map
				else if(HashMap.class.isAssignableFrom(values.getClass())) {
					
					if(logger.isDebugEnabled() && marshalLog) {
						logger.debug(" [outer] hashMap key : " + key);
					}
					
					if( !key.equals(IOperateCode.XML_NODE_ATTR) ) {
						
						writeXmlNodeMapValues(writer, key, values, context);
					}
				}
				//values is Text
				else {
					
					if( !key.equals(IOperateCode.XML_NODE_VAL) ) writer.startNode(key);
					
					writeXmlNodeTextValue(writer, values.toString());
					if( !key.equals(IOperateCode.XML_NODE_VAL) ) writer.endNode();
				}
			}
		}

		/**
		 * marshal sub-functions
		 * @param writer
		 * @param key
		 * @param nodeValues
		 * @param context
		 */
		public void writeXmlNodeMapValues(HierarchicalStreamWriter writer, String key, Object nodeValues, MarshallingContext context){
			
			writer.startNode(key);
			
			if( ((Map<?, ?>) nodeValues).get(IOperateCode.XML_NODE_ATTR) != null ) {
				
				if(logger.isDebugEnabled() && marshalLog) {
					logger.debug( " +- attribute write " + IOperateCode.XML_NODE_ATTR);
				}
				for( Object attr : ((Map<?, ?>) ((Map<?, ?>) nodeValues).get(IOperateCode.XML_NODE_ATTR)).entrySet() ){
					//write node attributes
					Entry<?, ?> attrEty = (Entry<?, ?>) attr;
					writer.addAttribute(attrEty.getKey().toString(), attrEty.getValue().toString());							
				}
			}
			
			if(logger.isDebugEnabled() && marshalLog) {
				logger.debug( " +- hashMap recall marshal");
			}
			marshal(nodeValues, writer, context);
			
			writer.endNode();
		}
		
		/**
		 * marshal sub-functions
		 * @param writer
		 * @param value
		 */
		public void writeXmlNodeTextValue(HierarchicalStreamWriter writer, String value){
			writer.setValue(value);
		}
		
		
		/**
		 * fromXML
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			
			Map<String, Object> map = new LinkedHashMap<String, Object>();

			List<Object> nodeValueList = null;
			
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				
				if (reader.hasMoreChildren()) {
					setNodeValues(reader, context, map, nodeValueList, unmarshal(reader, context));
					reader.moveUp();
					continue;
				}
				
				setNodeValues(reader, context, map, nodeValueList);
				reader.moveUp();
			}
			
			/*
			//another loop type ( sample )
			for (; reader.hasMoreChildren(); reader.moveUp()) {
				reader.moveDown();
				
				//... code ...
			}
			*/
			
			return map;
		}
		
		
		/**
		 * unmarshal sub-functions
		 * @param reader
		 * @param context
		 * @param map
		 * @param nodeValueList
		 */
		private void setNodeValues(HierarchicalStreamReader reader, UnmarshallingContext context, Map<String, Object> map, List<Object> nodeValueList){
			setNodeValues(reader, context, map, nodeValueList, null);
		}
		
		
		/**
		 * unmarshal sub-functions
		 * @param reader
		 * @param context
		 * @param map
		 * @param nodeValueList
		 * @param childMap
		 */
		@SuppressWarnings("unchecked")
		private void setNodeValues(HierarchicalStreamReader reader, UnmarshallingContext context, Map<String, Object> map, List<Object> nodeValueList, Object childMap){
			
			Object savedValue = map.get(reader.getNodeName());
			Object nodeValues = null;
			if( childMap != null ) {
				//nodeValues = childMap;
				nodeValues = getNodeValues(reader, context, childMap);
			}
			else {
				nodeValues = getNodeValues(reader, context);
			}

			if(logger.isDebugEnabled() && marshalLog) {
				logger.debug(reader.getNodeName() + " : " + nodeValues + ", childMap : [" + childMap + "]");
			}
			
			if(savedValue != null && !savedValue.getClass().isAssignableFrom(ArrayList.class)) {
				
				nodeValueList = new ArrayList<Object>();
				nodeValueList.add(savedValue);
				nodeValueList.add(nodeValues);
				map.put(reader.getNodeName(), nodeValueList);
			}
			else if(savedValue != null && savedValue.getClass().isAssignableFrom(ArrayList.class)) {
				
				((List<Object>) savedValue).add(nodeValues);
				map.put(reader.getNodeName(), savedValue);
			}
			else {
				
				map.put(reader.getNodeName(), nodeValues);
			}
			
		}
		
		
		/**
		 * unmarshal sub-functions
		 * @param reader
		 * @param context
		 * @return
		 */
		private Object getNodeValues(HierarchicalStreamReader reader, UnmarshallingContext context){
			return getNodeValues(reader, context, null);
		}
		
		
		/**
		 * unmarshal sub-functions
		 * @param reader
		 * @param context
		 * @param childMap
		 * @return
		 */
		@SuppressWarnings("unchecked")
		private Object getNodeValues(HierarchicalStreamReader reader, UnmarshallingContext context, Object childMap){
			
			Object nodeValue = null;
			//attribute 가 있으면 attribute 만을 추출하여 셋팅한다.
			if(reader.getAttributeCount() > 0) { // reader.getValue().isEmpty() 
				// set the node attributes map<String, Object>
				nodeValue = getNodeAttributes(reader, context, childMap);
				if(logger.isDebugEnabled() && marshalLog) {
					logger.debug(" getNodeValues reader.getAttributeCount() > 0 : " + nodeValue);
				}
			}
			else if( childMap != null ) { 
				// reader.getAttributeCount() is zero(0)
				// This node attributes does not only exist childMap
				nodeValue = childMap;
				if(logger.isDebugEnabled() && marshalLog) {
					logger.debug(" getNodeValues childMap != null : " + nodeValue);
				}
			}
			else {
				nodeValue = getNodeTextValue(reader.getValue());
				if(logger.isDebugEnabled() && marshalLog) {
					logger.debug(" getNodeValues nodeValue text : " + nodeValue);
				}
			}

			if( nodeValue.getClass().isAssignableFrom(HashMap.class) && getNodeTextValue(reader.getValue()).length() > 0 ) {
				if(logger.isDebugEnabled() && marshalLog) {
					logger.debug(" getNodeValues (Map) nodeValue put XmlNodeValue : " + nodeValue);
				}
				((Map<String, Object>) nodeValue).put(IOperateCode.XML_NODE_VAL, getNodeTextValue(reader.getValue()));
			}
			
			if(logger.isDebugEnabled() && marshalLog) {
				logger.debug(" + nodeValue : " + nodeValue);
			}
						
			return nodeValue;
		}
		
		/**
		 * unmarshal sub-functions
		 * @param nodeTextValue
		 * @return
		 */
		private String getNodeTextValue(String nodeTextValue){
			String value = "";
			if( nodeTextValue != null && !nodeTextValue.isEmpty() && nodeTextValue.trim().length() > 0) {
				value = nodeTextValue.trim();
			}
			
			return value;
		}
		
		/**
		 * unmarshal sub-functions
		 * @param reader
		 * @param context
		 * @param childMap
		 * @return
		 */
		@SuppressWarnings("unchecked")
		private Map<String, Object> getNodeAttributes(HierarchicalStreamReader reader, UnmarshallingContext context, Object childMap){
			Map<String, Object> elements = null;
			String attributeName = null;
			
			if( childMap != null && childMap.getClass().isAssignableFrom(HashMap.class)) {
				elements = (Map<String, Object>) childMap;
			}
			
			if(reader.getAttributeCount() > 0) {
				
				if( elements == null) {
					elements = new HashMap<String, Object>();
				}
				
				Map<String, Object> attributes = new HashMap<String, Object>();

				@SuppressWarnings("unchecked")
				Iterator<String> names = reader.getAttributeNames();
				
				while(names.hasNext()) {
					attributeName = names.next();
					attributes.put(attributeName, reader.getAttribute(attributeName));
				}
				
				//자식 정보가 없고 Node Text Value가 있으면
				if(childMap == null && !reader.hasMoreChildren() && !reader.getValue().isEmpty()){
					elements.put(IOperateCode.XML_NODE_VAL, getNodeTextValue(reader.getValue()));
				}
				
				if(this.attributeToField) {
					for(Entry<String, Object> entry : attributes.entrySet()) {
						elements.put("@"+entry.getKey(), entry.getValue());
					}	
				}
				else {
					elements.put(IOperateCode.XML_NODE_ATTR, attributes);
				}
				
			} 

			return elements;
		}
		
	}

}
