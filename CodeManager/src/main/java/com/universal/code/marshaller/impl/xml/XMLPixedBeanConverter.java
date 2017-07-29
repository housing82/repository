package com.universal.code.marshaller.impl.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import com.universal.code.exception.ApplicationException;
import com.universal.code.marshaller.impl.entity.JXDataTransferObject;
import com.universal.code.marshaller.impl.entity.JXElementDTO;
import com.universal.code.marshaller.impl.helper.ConverterHelper;
import com.universal.code.utils.SystemUtil;
import com.universal.code.utils.thread.Local;

@Component
public class XMLPixedBeanConverter {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private boolean marshalLog = false;
	private StringBuffer nodeLogs = null;
	
	@Autowired
	private ConverterHelper converterHelper;
	
	public XMLPixedBeanConverter(){
		if(converterHelper == null) {
			converterHelper = new ConverterHelper();
		}
	}
	
	/**
	 * unmarshal
	 * @param xml
	 * @return
	 */
	public Object fromXML(String xml, String rootTagName) {
		return fromXML(xml, rootTagName, Local.commonHeader().getEncoding());
	}
	
	/**
	 * unmarshal
	 * @param xml
	 * @param rootTagName
	 * @return
	 */
	public Object fromXML(String xml, String rootTagName, String encoding) {
		return fromXML(xml, rootTagName, encoding, new JXDataTransferObject());
	}
	
	/**
	 * unmarshal
	 * @param xml
	 * @param rootTagName
	 * @param bean
	 * @return
	 */
	public Object fromXML(String xml, String rootTagName, String encoding, Object bean){
		if(encoding == null) encoding = Local.commonHeader().getEncoding();
		
		Object out = unmarshal(xml, rootTagName, bean);
		
		return out;
	}
	
	/**
	 * marshal
	 * @param bean
	 * @param encoding
	 * @return
	 */
	public String toXML(Object bean, String rootTagName){
		
		return toXML(bean, rootTagName, Local.commonHeader().getEncoding());
	}
	
	/**
	 * marshal
	 * @param bean
	 * @param xml
	 * @return
	 */
	public String toXML(Object bean, String rootTagName, String encoding){
		if(encoding == null) encoding = Local.commonHeader().getEncoding();
		
		String out = marshal(bean, rootTagName, encoding);
				
		return out;
	}
	
	
	/**
	 * toXML
	 * @param bean
	 * @param xml
	 * @return
	 */
	private String marshal(Object bean, String rootTagName, String encoding){
		
		String out = null;
		
		
		Document document = extractBean(bean, rootTagName, null, null);
		
		try {
			// transform the DOM Object to an XML
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			//transformerFactory.setAttribute("indent-number", 4);
			
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //omit-xml-declaration (not use automic xml header) 
			//transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			
			DOMSource domSource = new DOMSource(document);
			
			Writer writer = new StringWriter();
			converterHelper.writeXmlHeader(writer, encoding);
			
			StreamResult result = new StreamResult(writer);
			
			transformer.transform(domSource, result);
			
			out = writer.toString();
			
		} catch (TransformerConfigurationException e) {
			throw new ApplicationException(e);
		} catch (TransformerException e) {
			throw new ApplicationException(e);
		} 
		
		return out;
	}
	
	/**
	 * marshal sub-functions
	 * @param bean
	 * @param xml
	 * @param rootTagName
	 * @return
	 */
	private Document extractBean(Object bean, String rootTagName, Document parnetDocument, Element parentElement){
	
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = parnetDocument;
		
		boolean isBeanPixedDTO = false;
		if(JXDataTransferObject.class.isAssignableFrom(bean.getClass()) || JXElementDTO.class.isAssignableFrom(bean.getClass())) {
			isBeanPixedDTO = true;
		}
		
		Element element = null;
		Attr attr = null;
		Entry<?, ?> entry = null;
		String key = "";
		String values = null;
		
		try {
			
			if(isBeanPixedDTO) {
								
				if(JXDataTransferObject.class.isAssignableFrom(bean.getClass())) {
					
					factory = DocumentBuilderFactory.newInstance();
					builder = factory.newDocumentBuilder();
					document = builder.newDocument();
					
					JXDataTransferObject rootBean = (JXDataTransferObject) bean;
					
					// root element
					if(rootTagName != null && !rootTagName.isEmpty()) {
						element = document.createElement(rootTagName);
					}
					else {
						element = document.createElement(rootBean.getRootName());
					}
					
					document.appendChild(element);
					
					if(logger.isDebugEnabled() && marshalLog) {
						nodeLogs = new StringBuffer();
						nodeLogs.append(" getRootName : " + rootBean.getRootName());
						nodeLogs.append(" getStartTimeMillis : " + rootBean.getStartTimeMillis());
						nodeLogs.append(" getEndTimeMillis : " + rootBean.getEndTimeMillis());
						nodeLogs.append(" getRunTimeMillis : " + rootBean.getRunTimeMillis());
						nodeLogs.append(" getElement().size() : " + rootBean.getElement().size());
						nodeLogs.append("──────────────────────────────");
						logger.debug(nodeLogs.toString());
					}
					
					for(JXElementDTO xmlElement : rootBean.getElement()) {
						extractBean(xmlElement, rootTagName, document, element);
					}
					
					if(logger.isDebugEnabled() && marshalLog) {
						logger.debug("──────────────────────────────");
					}
				}
				else {
					
					// XMLElementDTO
					JXElementDTO elementsBean = (JXElementDTO) bean;
					
					element = document.createElement(elementsBean.getNodeName());

					if(elementsBean.getNodeValue() != null && !elementsBean.getNodeValue().isEmpty()) {
						element.appendChild(document.createTextNode(elementsBean.getNodeValue()));
					}

					if( elementsBean.getNodeAttrs() != null ) {
						for (Object obj : elementsBean.getNodeAttrs().entrySet()) {
							entry = (Entry<?, ?>) obj;
							key = entry.getKey().toString();
							values = entry.getValue().toString();
							
							attr = document.createAttribute(key);
							attr.setValue(values);
							element.setAttributeNode(attr);
						}
					}
					
					if(parentElement != null) {
						parentElement.appendChild(element);
					}
					
					
					String white = "";
					if(logger.isDebugEnabled() && marshalLog) {
						
						for(int j = 0; j < elementsBean.getNodeLevel(); j++){
							white += "   ";
						}
						
						nodeLogs = new StringBuffer();
						nodeLogs.append(white + elementsBean.getNodeName() + " START ");
						nodeLogs.append(", getNodeName : " + elementsBean.getNodeName());
						if(elementsBean.getNodeParent() != null) {
							nodeLogs.append(", Parent NodeName : " + elementsBean.getNodeParent().getNodeName());
						}
						nodeLogs.append(", getNodeValue : " + elementsBean.getNodeValue());
						nodeLogs.append(", getNodeAttrs : " + elementsBean.getNodeAttrs());
						nodeLogs.append(", getNodeLevel : " + elementsBean.getNodeLevel());
						logger.debug(nodeLogs.toString());
					}
					
					for(JXElementDTO child : elementsBean.getChildElements()) {
						extractBean(child, rootTagName, document, element);
					}
					
					if(logger.isDebugEnabled() && marshalLog) {
						logger.debug(white + elementsBean.getNodeName() + " END ");
					}
				}
				
				
			}
			else {
				//User bean is not supported ( example )
				
				String xmlFilePath = "xmlfile.xml";

				try {
					DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
					document = documentBuilder.newDocument();

					// root element
					Element root = document.createElement("company");
					document.appendChild(root);

					// employee element
					Element employee = document.createElement("employee");
					root.appendChild(employee);

					// set an attribute to staff element
					attr = document.createAttribute("id");
					attr.setValue("10");
					employee.setAttributeNode(attr);

					// you can also use staff.setAttribute("id", "1") for this
					// firstname element
					Element firstName = document.createElement("firstname");
					firstName.appendChild(document.createTextNode("James"));
					employee.appendChild(firstName);

					// lastname element
					Element lastname = document.createElement("lastname");
					lastname.appendChild(document.createTextNode("Harley"));
					employee.appendChild(lastname);

					// email element
					Element email = document.createElement("email");
					email.appendChild(document.createTextNode("james@example.org"));
					employee.appendChild(email);

					// department elements
					Element department = document.createElement("department");
					department.appendChild(document.createTextNode("Human Resources"));
					employee.appendChild(department);

					// create the xml file
					// transform the DOM Object to an XML File
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource domSource = new DOMSource(document);
					StreamResult streamResult = new StreamResult(new File(xmlFilePath));

					// If you use
					// StreamResult result = new StreamResult(System.out);
					// the output will be pushed to the standard output ...
					// You can use that for debugging
					transformer.transform(domSource, streamResult);

					if(logger.isDebugEnabled()) {
						logger.debug("Done creating XML File");
					}

				} catch (ParserConfigurationException pce) {
					throw new ApplicationException(pce);
				} catch (TransformerException tfe) {
					throw new ApplicationException(tfe);
				}
			}
			
		} catch (ParserConfigurationException pce) {
			throw new ApplicationException(pce);
		} 

		return document;
	}
	

	/**
	 * fromXML
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object unmarshal(String xml, String rootTagName, Object bean){
		
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Object out = bean;
		InputSource is = null;
		Document document = null;
		Element xmlRoot = null;
		NodeList nodes = null;
		
		
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			//xml 소스 분석
			document = builder.parse(is);
			//xml 문서 일반화
			document.normalize();
			
			String rootName = "";
			if(rootTagName != null && !rootTagName.isEmpty()) {
				nodes = document.getElementsByTagName(rootTagName);
				if(nodes != null && nodes.getLength() == 1) {
					nodes = nodes.item(0).getChildNodes();
				}
				rootName = rootTagName;
			}
			else {
				//document 에서 xml root element 획득 
				xmlRoot = document.getDocumentElement();
				nodes = xmlRoot.getChildNodes();
				rootName = xmlRoot.getNodeName();
			}
			
			if(out == null || out.getClass().isAssignableFrom(JXDataTransferObject.class)) {
				if(out == null) out = new JXDataTransferObject();

				((JXDataTransferObject) out).setRootName(rootName);
				((JXDataTransferObject) out).setStartTimeMillis(SystemUtil.currentTimeMillis());
				((JXDataTransferObject) out).setElement((List<JXElementDTO>) parse(nodes, new ArrayList<JXElementDTO>(), true));
				((JXDataTransferObject) out).setEndTimeMillis(SystemUtil.currentTimeMillis());
			}
			else {
				out = parse(nodes, out, false);
			}

		} catch (ParserConfigurationException e) {
			throw new ApplicationException(e);
		} catch (SAXException e) {
			throw new ApplicationException(e);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
		
		return out;
	}

	
	/**
	 * unmarshal sub-functions
	 * @param nodes
	 * @param bean
	 * @param isBeanPixedDTO
	 * @return
	 */
	private Object parse(NodeList nodes, Object bean, boolean isBeanPixedDTO) {
		return parse(nodes, bean, null, isBeanPixedDTO, 1);
	}
	
	/**
	 * unmarshal sub-functions
	 * @param nodes
	 * @param bean
	 * @param parentBean
	 * @param isBeanPixedDTO
	 * @param level
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object parse(NodeList nodes, Object bean, Object parentBean, boolean isBeanPixedDTO, int level){
		
		
		Node node = null;
		JXElementDTO elements = null;
		
		for(int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			
			if(node.getNodeType() != Node.ELEMENT_NODE) continue;
			
			if(logger.isDebugEnabled() && marshalLog) {
				nodeLogs = new StringBuffer();
				nodeLogs.append(" - getNodeName : " + node.getNodeName());
				nodeLogs.append(", getNodeType : " + node.getNodeType());
				nodeLogs.append(", getChildNodes length : " + node.getChildNodes().getLength());
				nodeLogs.append(", getNodeValue : " + getNodeTextValue(node) );
				nodeLogs.append(", node attributes :" + getNodeAttributes(node));
				logger.debug(nodeLogs.toString());
			}
			
			if(isBeanPixedDTO) {
				 
				List<JXElementDTO> elementList = (List<JXElementDTO>) bean;
				elements = new JXElementDTO();
				
				elements.setNodeName(node.getNodeName());
				elements.setNodeType(node.getNodeType());
				elements.setNodeLevel(level);
				elements.setChildLength(node.getChildNodes().getLength());
				elements.setNodeValue(getNodeTextValue(node));
				elements.setNodeAttrs(getNodeAttributes(node));
				
				if(getChildElementNodeCount(node) > 0) {
					level++;
					elements.setChildElements((List<JXElementDTO>) parse(node.getChildNodes(), new ArrayList<JXElementDTO>(), elements, isBeanPixedDTO, level));
					level--;
				}
				
				if(parentBean != null && JXElementDTO.class.isAssignableFrom(parentBean.getClass())) {
					elements.setNodeParent((JXElementDTO) parentBean);
				}
				
				elementList.add(elements);
				//ChildElements
			}
			else {
				if(node.getChildNodes().getLength() > 0) {
					level++;
					parse(node.getChildNodes(), bean, bean, isBeanPixedDTO, level);
				}
			}
		}
		
		return bean;
	}
	
	/**
	 * unmarshal sub-functions
	 * @param node
	 * @return
	 */
	private int getChildElementNodeCount(Node node){
		int count = 0;
		if(node.getChildNodes().getLength() > 0) {
			for(int i = 0; i <node.getChildNodes().getLength(); i++){
				if(node.getNodeType() == Node.ELEMENT_NODE) count++;
			}
		}
		return count;
	}
	
	/**
	 * unmarshal sub-functions
	 * @param node
	 * @return
	 */
	private Map<String, String> getNodeAttributes(Node node){
		Map<String, String> out = null;
		
		if( node.getAttributes() != null && node.getAttributes().getLength() > 0) { 
			out = new HashMap<String, String>();
			for(int j =0; j < node.getAttributes().getLength(); j++) {
				
				Node attr = node.getAttributes().item(j);
				if(attr.getNodeType() != Node.ATTRIBUTE_NODE) continue;
				
				out.put(attr.getNodeName(), attr.getNodeValue());
			}
		}
		
		return out;
	}
	
	/**
	 * unmarshal sub-functions
	 * @param parnetNode
	 * @return
	 */
	private String getNodeTextValue(Node parnetNode){
		Node node = null;
		StringBuffer out = null;
		if(parnetNode.getChildNodes().getLength() > 0) {
			out = new StringBuffer();
			for(int y = 0; y < parnetNode.getChildNodes().getLength(); y++) {
				node = parnetNode.getChildNodes().item(y);
				
				if(node.getNodeType() == Node.TEXT_NODE && node.getNodeValue() != null) {
					out.append(node.getNodeValue());
				}
				else if(node.getNodeType() == Node.CDATA_SECTION_NODE && ((CharacterData) node).getData() != null) {
					out.append(((CharacterData) node).getData());
				}
			}
			
			if(!out.toString().trim().isEmpty()) {
				return out.toString().trim();
			}
				
		}
		return null;
	}
	
	private String getNodeTypeString(Node node)
    {
        String nodeType = "UNKNOWN_NODE_TYPE";
 
        switch(node.getNodeType()) {
        case Node.ELEMENT_NODE:
            nodeType = "ELEMENT_NODE";
            break;
        case Node.ATTRIBUTE_NODE:
            nodeType = "ATTRIBUTE_NODE";
            break;
        case Node.TEXT_NODE:
            nodeType = "TEXT_NODE";
            break;
        case Node.CDATA_SECTION_NODE:
            nodeType = "CDATA_SECTION_NODE";
            break;
        case Node.ENTITY_REFERENCE_NODE:
            nodeType = "ENTITY_REFERENCE_NODE";
            break;
        case Node.ENTITY_NODE:
            nodeType = "ENTITY_NODE";
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            nodeType = "PROCESSING_INSTRUCTION_NODE";
            break;
        case Node.COMMENT_NODE:
            nodeType = "COMMENT_NODE";
            break;
        case Node.DOCUMENT_NODE:
            nodeType = "DOCUMENT_NODE";
            break;
        case Node.DOCUMENT_TYPE_NODE:
            nodeType = "DOCUMENT_TYPE_NODE";
            break;
        case Node.DOCUMENT_FRAGMENT_NODE:
            nodeType = "DOCUMENT_FRAGMENT_NODE";
            break;
        case Node.NOTATION_NODE:
            nodeType = "NOTATION_NODE";
            break;
        }
 
        return nodeType;
    }
	
	
}
