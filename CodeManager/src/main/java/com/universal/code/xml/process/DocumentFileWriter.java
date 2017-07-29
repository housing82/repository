package com.universal.code.xml.process;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.xpath.XPathConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.CoderUtil;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.PropertyUtil;
import com.universal.code.utils.RegexUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.xml.factory.DocumentFactory;
import com.universal.code.xml.process.dto.ElementDTO;

@Component
public class DocumentFileWriter {

	private static final Logger logger = LoggerFactory.getLogger(DocumentFileWriter.class);
	
	@Autowired
	private DocumentFactory documentFactory;
	
	@Autowired
	private DocumentReader documentReader;
	
	@Autowired
	private FileUtil fileUtil;
	
	@Autowired
	private CoderUtil coderUtil;
	
	@Autowired
	private PropertyUtil propertyUtil;
	
	@Autowired
	private RegexUtil regexUtil;
	
	private Document document;
	
	private String documentPath;

	private final String PTN_WHITESPACE = "\\S"; //(^[	|\r\n|\n|&#10;|".concat(SystemUtil.LINE_SEPARATOR).concat("]+$)");
	
	public DocumentFileWriter() {}
	
	public DocumentFileWriter(String documentPath) {
		
		if (documentFactory == null) {
			documentFactory = new DocumentFactory();
		}
		if (documentReader == null) {
			documentReader = new DocumentReader();
		}
		if (fileUtil == null) {
			fileUtil = new FileUtil();
		}
		if (propertyUtil == null) {
			propertyUtil = new PropertyUtil();
		}
		if (coderUtil == null) {
			coderUtil = new CoderUtil();
		}
		if (regexUtil == null) {
			regexUtil = new RegexUtil();
		}
		//XML문서 경로
		this.documentPath = documentPath;
		//XML문서
		this.document = documentFactory.getNormalizeDocument(DocumentFactory.XML_FILE, this.documentPath, this.documentPath);
	}

	/**
	 * XML의 특정위치에 새로운 엘리먼트 생성
	 * @param docPath
	 * @param readElement
	 * @param writeElement
	 * @return
	 */
	public Document writeElement(ElementDTO readElement, ElementDTO writeElement ) {
		if(readElement == null) {
			//기준 엘리먼트가없으면 첫번째 최상위 엘리먼트로 셋팅
			readElement = new ElementDTO();
			readElement.setXqname(XPathConstants.NODE);
			readElement.setXpathExpr("/*"); 
		}

		Document out = null;
		if(  this.document != null ) {
			out = writeElement(this.document, readElement, writeElement );
		}
		else {
			throw new ApplicationException("XML Document가 존재하지 않거나 잘못되었습니다.");
		}

		return out;
	}
	
	/**
	 * XML의 특정엘리먼트 내용 변경
	 * @param docPath
	 * @param readElement
	 * @param writeElement
	 * @return
	 */
	public Document modifyElement(ElementDTO readElement, ElementDTO modifyElement ) {
		if(readElement == null) {
			throw new ApplicationException("변경대상 XML 엘리먼트정보가 존재하지 않습니다.");
		}

		Document out = null;
		if(  this.document != null ) {
			out = modifyElement(this.document, readElement, modifyElement );
		}
		else {
			throw new ApplicationException("XML Document가 존재하지 않거나 잘못되었습니다.");
		}		

		return out;
	}
	
	/**
	 * XML의 특정 엘리먼트 삭제
	 * @param docPath
	 * @param readElement
	 * @param writeElement
	 * @return
	 */
	public Document removeElement(ElementDTO readElement ) {
		if(readElement == null) {
			throw new ApplicationException("삭제대상 XML 엘리먼트정보가 존재하지 않습니다.");
		}
		
		Document out = null;
		if(  this.document != null ) {
			out = removeElement(this.document, readElement );
		}
		else {
			throw new ApplicationException("XML Document가 존재하지 않거나 잘못되었습니다.");
		}			

		return out;
	}
	
	public Document documentTransformer() {
		return documentTransformer(null, null);
	}
	
	public Document documentTransformer(String encoding) {
		return documentTransformer(encoding, null);
	}
	
	public Document documentTransformer(Properties outputProperties) {
		return documentTransformer(IOperateCode.DEFAULT_ENCODING, outputProperties);
	}
			
	public Document documentTransformer(String encoding, Properties outputProperties) {
		if(this.document == null) {
			throw new ApplicationException("내용을 저장할 XML Document가 존재하지 않습니다.");
		}
		if(documentFactory.documentTransformer(this.document, this.documentPath, encoding, outputProperties)) {
			return this.document;
		}
		else {
			return null;
		}
	}
	
	private Document removeElement(Document document, ElementDTO readElement ) {
		
		Object xPathEL = documentReader.getXpath(document, readElement.getXpathExpr(), readElement.getXqname());
		if( xPathEL == null ) {
			logger.warn("\n삭제대상 XpathExpression에 해당하는 엘리먼트가 존재하지 않습니다.\n".concat(readElement.getXpathExpr()).concat("\n\n"));
			return document;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("xPathEL : " + xPathEL.getClass().getSimpleName());
		}
		
		if(readElement.getXqname() == XPathConstants.NODE) {
			Element element = (Element) xPathEL;
			removeElement(element, readElement);
		}
		else if(readElement.getXqname() == XPathConstants.NODESET) {
			NodeList nodeList = (NodeList) xPathEL;
			for (int i = 0; i < nodeList.getLength(); i++) {
				removeElement((Element) nodeList.item(i), readElement);
			}
		}
		
		return document;
	}
	
	
	private void removeElement(Element element, ElementDTO readElement){
		
		//remove element
		if(readElement.isRemoveNode()) {
			element.getParentNode().removeChild(element);
		}
		else {
			NodeList childList = null;
			Node childNode = null;
			
			//remove attribute
			if(readElement.getRemoveAttributes() != null) {
				removeAttributes(element, readElement);
			}
			
			//remove child element all or text node all
			if(readElement.isRemoveNodeChildAll() || readElement.isRemoveNodeText()) {
				childList = element.getChildNodes();
				for(int i = (childList.getLength() - 1); i >= 0; i--) {
					childNode = childList.item(i);
					if(logger.isDebugEnabled()) {
						logger.debug("childNode.getNodeType() : " + childNode.getNodeType());
					}
					//remove text node all
					if(readElement.isRemoveNodeText() && (childNode.getNodeType() == Node.TEXT_NODE || childNode.getNodeType() == Node.CDATA_SECTION_NODE)) {
						element.removeChild(childNode);
					}
					//remove child element all
					if(readElement.isRemoveNodeChildAll() && childNode.getNodeType() == Node.ELEMENT_NODE) {
						element.removeChild(childNode);
					}
				}	
			}
		}
	}
	
	public void removeAttributes(Element element, ElementDTO elementDTO){
		for(String attr : elementDTO.getRemoveAttributes()){
			element.removeAttribute(attr);
		}
	}
	
	private Document writeElement(Document document, ElementDTO readElement, ElementDTO writeElement ) {

		Object xPathEL = documentReader.getXpath(document, readElement.getXpathExpr(), readElement.getXqname());
		if( xPathEL == null ) {
			logger.warn("\n쓰기기준 XpathExpression에 해당하는 엘리먼트가 존재하지 않습니다.\n".concat(readElement.getXpathExpr()).concat("\n\n"));
			return document;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("xPathEL : " + xPathEL.getClass().getSimpleName());
		}

		//입력 대상 엘리먼트
		if(readElement.getXqname() == XPathConstants.NODE) {
			Element element = (Element) xPathEL;
			createElement(document, element, writeElement);
		}
		else if(readElement.getXqname() == XPathConstants.NODESET) {
			NodeList nodeList = (NodeList) xPathEL;
			for (int i = 0; i < nodeList.getLength(); i++) {
				createElement(document, (Element) nodeList.item(i), writeElement);
			}
		}

		return document;
	}
	
	private void createElement(Document document, Element parentElement, ElementDTO writeElement) {
		
		if(writeElement == null) {
			throw new ApplicationException("XML에 작성할 엘리먼트정보가 존재하지 않습니다.");
		}
		else if(writeElement.getNodeName() == null) {
			throw new ApplicationException("XML에 작성할 엘리먼트명이 존재하지 않습니다.");
		}
		
		Element newElement = createElement(document, parentElement, writeElement.getNodeName());
		
		//element attributes
		if(writeElement.getAttributes() != null) {
			setAttributes(newElement, writeElement.getAttributes());
		}
		
		//element children
		if(writeElement.getChildrens() != null) {
			for(ElementDTO writeNode : writeElement.getChildrens()){
				createElement(document, newElement, writeNode);
			}
		}
		
		//element text value
		if(writeElement.getNodeText() != null) {
			createCDATASection(document, newElement, writeElement.getNodeText());
		}
	}

	public Element createElement(Document document, Element parent, String tagName) {
		Element newElement = document.createElement(tagName);
		parent.appendChild(newElement);
		return newElement;
	}
	
	public void createTextNode(Document document, Element element, String content) {
		Text contentText = document.createTextNode(content);
		//element.appendChild(contentText);
		element.insertBefore(contentText, element.getFirstChild());
	}
	
	public void createCDATASection(Document document, Element element, String content) {
		CDATASection cdata = document.createCDATASection(content);
		//element.appendChild(cdata);
		element.insertBefore(cdata, element.getFirstChild());
	}
	
	public Element setAttributes(Element newElement, Map<String, String> attributes) {
		if(attributes != null) {
			for(Entry<String, String> enty : attributes.entrySet()){
				newElement.setAttribute(enty.getKey(), enty.getValue());
			}		
		}
		return newElement;
	}
	
	private Document modifyElement(Document document, ElementDTO readElement, ElementDTO modifyElement ) {

		Object xPathEL = documentReader.getXpath(document, readElement.getXpathExpr(), readElement.getXqname());
		if( xPathEL == null ) {
			logger.warn("\n수정대상 XpathExpression에 해당하는 엘리먼트가 존재하지 않습니다.\n".concat(readElement.getXpathExpr()).concat("\n\n"));
			return document;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("xPathEL : " + xPathEL.getClass().getSimpleName());
		}

		//입력 대상 엘리먼트
		if(readElement.getXqname() == XPathConstants.NODE) {
			if(logger.isDebugEnabled()) {
				logger.debug("NodeType : " + ((Node) xPathEL).getNodeType());
			}
			if( ((Node) xPathEL).getNodeType() == Node.ELEMENT_NODE ) {
				Element element = (Element) xPathEL;
				modifyElement(document, element, modifyElement);				
			}
		}
		else if(readElement.getXqname() == XPathConstants.NODESET) {
			NodeList nodeList = (NodeList) xPathEL;
			for (int i = 0; i < nodeList.getLength(); i++) {
				if(logger.isDebugEnabled()) {
					logger.debug("NodeType : " + nodeList.item(i).getNodeType());
				}
				if( nodeList.item(i).getNodeType() == Node.ELEMENT_NODE ) {
					modifyElement(document, (Element) nodeList.item(i), modifyElement);
				}
			}
		}

		return document;
	}
	
	private void modifyElement(Document document, Element element, ElementDTO modifyElement) {
		
		if(modifyElement == null) {
			throw new ApplicationException("XML에 작성할 엘리먼트정보가 존재하지 않습니다.");
		}
		
		Element modElement = element;
		
		//element text value
		if(modifyElement.getNodeText() != null && modifyElement.getChildrens() == null) {
			modifyTextNode(document, modElement, modifyElement.getNodeText());
		}
		
		//element attribute remove
		if(modifyElement.getRemoveAttributes() != null) {
			removeAttributes(element, modifyElement);
		}
		
		//element attributes set
		if(modifyElement.getAttributes() != null) {
			setAttributes(modElement, modifyElement.getAttributes());
		}
	
		//element children
		if(modifyElement.getChildrens() != null && modElement.getChildNodes().getLength() > 0) {
			NodeList childNodes = null;
			Node childNode = null;
			boolean attrPriority = false;
			
			childNodes = modElement.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {

				childNode = childNodes.item(i);
				
				//실제 NODE만 취급
				if(childNode.getNodeType() == Node.ELEMENT_NODE) { 
					if(logger.isDebugEnabled()) {
						//logger.debug(propertyUtil.out(childNode, "textContent","schemaTypeInfo"));
					}
					
					attrPriority = false;
					//변경 속성을 셋팅한 엘리먼트 모두 우선 변경
					for (ElementDTO modifyNode : modifyElement.getChildrens()) {
						// 같은nodeName을 기준으로 순차변경한다.
						if (!modifyNode.isModified()) {
							if(StringUtil.isNotEmptyStringArray(modifyNode.getModifyAttr(), modifyNode.getModifyAttrValue()) && ((Element) childNode).getAttribute(modifyNode.getModifyAttr()).equals(modifyNode.getModifyAttrValue()) ) {
								if(logger.isDebugEnabled()) {
									logger.debug("[Changing elements that match a child node ATTRIBUTE Val] attributeName : " + modifyNode.getModifyAttr() + ", attributeValue" + modifyNode.getModifyAttrValue());
								}
								modifyElement(document, (Element) childNode, modifyNode);
								modifyNode.setModified(true);
								attrPriority = true;
								break;			
							}
						}
					}
					
					//변경 속성 우선 변경이 되지 않았으면 실행
					if(!attrPriority) {
						for (ElementDTO modifyNode : modifyElement.getChildrens()) {
							// 같은nodeName을 기준으로 순차변경한다.
							if (!modifyNode.isModified()) {
								if (modifyNode.getNodeName().equals(childNode.getNodeName())) {
									if(logger.isDebugEnabled()) {
										logger.debug("[Sub-node name change order] childIndex : " + i + ", nodeName : " + modifyNode.getNodeName());
									}
									modifyElement(document, (Element) childNode, modifyNode);
									modifyNode.setModified(true);
									break;
								}
							}
						}						
					}
				}
			}
		}
	}

	/**
	 * 바인드된 엘리먼트의 모든 자식 텍스트/CDATA 노드의 내용변경
	 * @param document
	 * @param element
	 * @param content
	 */
	public void modifyTextNode(Document document, Element element, String content) {
		if(logger.isDebugEnabled()) {
			logger.debug("[START] modifyTextNode content : ".concat(content));
		}
		
		Node childNode = null;
		NodeList childList = element.getChildNodes();
		Text text = null;
		
		for(int i = (childList.getLength() - 1); i >= 0; i--) {
			childNode = childList.item(i);
			if(logger.isDebugEnabled()) {
				logger.debug("modifyTextNode getNodeType() : " + childNode.getNodeType());
			}
			if(childNode.getNodeType() == Node.CDATA_SECTION_NODE) {
				element.removeChild(childNode);
			}
			else if(childNode.getNodeType() == Node.TEXT_NODE) {
				text = (Text) childNode;
				if(regexUtil.testPattern(text.getData(), PTN_WHITESPACE)) {
					element.removeChild(childNode);
				}
			}
		}
		
		createCDATASection(document, element, content);
	}
	
	@Deprecated
	private void modifyTextNodeOLD(Document document, Element element, String content) {
		if(logger.isDebugEnabled()) {
			logger.debug("[START] modifyTextNode content : ".concat(content));
		}
		
		Node childNode = null;
		NodeList childList = element.getChildNodes();
		boolean changed = false;
		CDATASection cdata = null;
		Text text = null;
		for(int i = 0; i < childList.getLength(); i++) {
			childNode = childList.item(i);
			if(logger.isDebugEnabled()) {
				logger.debug("modifyTextNode getNodeType() : " + childNode.getNodeType());
			}
			if(childNode.getNodeType() == Node.CDATA_SECTION_NODE) {
				cdata = (CDATASection) childNode;
				if(StringUtil.isNotEmpty(cdata.getData())) {
					cdata.setData(content);
					if(!changed) changed = true;
					break;
				}
			}
			else if(childNode.getNodeType() == Node.TEXT_NODE) {
				text = (Text) childNode;
				if(logger.isDebugEnabled()) {
					logger.debug("text.getData() : " + text.getData());
					logger.debug("test : " + regexUtil.testPattern(text.getData(), PTN_WHITESPACE));
				}
				
				if(regexUtil.testPattern(text.getData(), PTN_WHITESPACE)) {
					if(StringUtil.isNotEmpty(text.getData())) {
						//정규식으로 엔터와 탭만있을때는 변경하지 않는것으로
						
						text.setData(content);
						if(!changed) changed = true;
						logger.debug("Changed text.getData() : " + content);
						break;
					}
				}
			}
		}
		
		if(!changed) {
			text = document.createTextNode(content);
			element.appendChild(text);
		}
	}
	
}
