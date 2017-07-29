package com.universal.code.xml.process;

import java.io.StringWriter;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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

import com.sun.org.apache.xpath.internal.XPathAPI;
import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.exception.ValidateException;
import com.universal.code.utils.StringUtil;
import com.universal.code.xml.factory.DocumentFactory;
import com.universal.code.xml.factory.dto.DocumentBuilderDTO;

@Component
public class DocumentReader {

	private static final Logger logger = LoggerFactory.getLogger(DocumentReader.class);

	@Autowired
	private DocumentFactory documentFactory;

	public DocumentReader() {
		if (documentFactory == null) {
			documentFactory = new DocumentFactory();
		}
	}

	public NodeList getNodeList(int xmlDataFormat, String docPath, String xpathExpression) {
		return getNodeList(documentFactory.getDocument(xmlDataFormat, docPath, docPath), xpathExpression);
	}

	public NodeList getNodeList(int xmlDataFormat, DocumentBuilderDTO builderConfig, String docPath, String xpathExpression) {
		return getNodeList(documentFactory.getDocument(xmlDataFormat, builderConfig, docPath, docPath), xpathExpression);
	}
	
	public NodeList getNodeList(final Document document, String xpathExpression) {
		Node root = document.getDocumentElement();
		try {
			return XPathAPI.selectNodeList(root, xpathExpression);
		} catch (TransformerException e) {
			throw new ApplicationException("XPathAPI 표현식을 처리하는 과정에 오류가 발생하였습니다.", e);
		}
	}

	public String getText(int xmlDataFormat, String docPath, String xpathExpression) {
		return getText(documentFactory.getDocument(xmlDataFormat, docPath, docPath), xpathExpression);
	}
	
	public String getText(int xmlDataFormat, DocumentBuilderDTO builderConfig, String docPath, String xpathExpression) {
		return getText(documentFactory.getDocument(xmlDataFormat, builderConfig, docPath, docPath), xpathExpression);
	}
	
	/**
	 * XML Document에서 xpathExpression에 해당하는 내용을 String으로 리턴함
	 * @param doc
	 * @param xpathExpression
	 * @return
	 */
	public String getText(final Document document, String xpathExpression) {
		String out = null;
		Node root = document.getDocumentElement();
		try {
			out = XPathAPI.eval(root, xpathExpression).str().trim();
		} catch (TransformerException e) {
			throw new ApplicationException("XPathAPI 표현식을 처리하는 과정에 오류가 발생하였습니다.", e);
		}
		return out;
	}

	/**
	 * XML Element의 속성값을 반환합니다.
	 * @param element
	 * @param attrName
	 * @return
	 */
	public String getAttributeValue(Element element, String attrName) {
		return StringUtil.NVL(element.getAttribute(attrName));
	}

	/**
	 * XML NodeList에서 index번째 ElementNode를 반환합니다.
	 * @param nodeList
	 * @param index
	 * @return
	 */
	public Element getElementNode(final NodeList nodeList, int index) {
		Node node = nodeList.item(index);
		if(node.getNodeType() == Node.ELEMENT_NODE) {
			return (Element) nodeList.item(index);
		}
		return null;
	}
	
	/**
	 * XML NodeList에서 index번째 TextNode를 반환합니다.
	 * @param nodeList
	 * @param index
	 * @return
	 */
	public Text getTextNode(final NodeList nodeList, int index) {
		Node node = nodeList.item(index);
		if(node.getNodeType() == Node.TEXT_NODE) {
			return (Text) nodeList.item(index);
		}
		return null;
	}
	
	/**
	 * XML NodeList에서 index번째 CDATASectionNode를 반환합니다.
	 * @param nodeList
	 * @param index
	 * @return
	 */
	public CDATASection getCDATASectionNode(final NodeList nodeList, int index) {
		Node node = nodeList.item(index);
		if(node.getNodeType() == Node.CDATA_SECTION_NODE) {
			return (CDATASection) nodeList.item(index);
		}
		return null;
	}
	
	public String getXMLToString(String docPath) {
		return getXMLToString(docPath, IOperateCode.DEFAULT_ENCODING);
	}
		
	public String getXMLToString(Document document) {
		return getXMLToString(document, IOperateCode.DEFAULT_ENCODING);
	}
	
	public String getXMLToString(Document document, String encoding) {
		return getXMLToString(document, encoding, null);
	}

	public String getXMLToString(String docPath, String encoding) {
		return getXMLToString(null, docPath, encoding);
	}
	
	public String getXMLToString(String docPath, Properties outputProperties) {
		return getXMLToString(documentFactory.getDocument(DocumentFactory.XML_FILE, docPath, docPath), docPath, outputProperties);
	}
	
	public String getXMLToString(DocumentBuilderDTO builderConfig, String docPath, String encoding) {
		return getXMLToString(documentFactory.getDocument(DocumentFactory.XML_FILE, builderConfig, docPath, docPath), encoding, null);
	}
		
	public String getXMLToString(DocumentBuilderDTO builderConfig, String docPath) {
		return getXMLToString(documentFactory.getDocument(DocumentFactory.XML_FILE, builderConfig, docPath, docPath), docPath, null);
	}
	
	public String getXMLToString(DocumentBuilderDTO builderConfig, String docPath, Properties outputProperties) {
		return getXMLToString(documentFactory.getDocument(DocumentFactory.XML_FILE, builderConfig, docPath, docPath), IOperateCode.DEFAULT_ENCODING, outputProperties);
	}
	
	public String getXMLToString(Document document, Properties outputProperties) {
		return getXMLToString(document, IOperateCode.DEFAULT_ENCODING, outputProperties);
	}
	
	/**
	 * XML Document를 String으로 변환하여 반환합니다.
	 * @param document
	 * @param encoding
	 * @param outputProperties
	 * @return
	 */
	public String getXMLToString(final Document document, String encoding, Properties outputProperties) {

		String xml = null;
		try {

			Transformer transformer = documentFactory.getTransformer(encoding, outputProperties);
			// String 만들기
			StringWriter sw = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(sw));
			xml = sw.toString();

		} catch (TransformerConfigurationException e) {
			throw new ApplicationException(e);
		} catch (TransformerException e) {
			throw new ApplicationException(e);
		}

		return xml;
	}
	


	public Object getXpath(Document docment, String xpathExpression) {
		return getXpath(docment, xpathExpression, null);
	}
	
	public Object getXpath(int xmlDataFormat, String xmlId, String xmlSource, String xpathExpression) {
		return getXpath(documentFactory.getDocument(xmlDataFormat, xmlId, xmlSource), xpathExpression, null);
	}
	
	public Object getXpath(int xmlDataFormat, String xmlId, String xmlSource, String xpathExpression, QName xpathQName) {
		return getXpath(documentFactory.getDocument(xmlDataFormat, xmlId, xmlSource), xpathExpression, xpathQName);
	}
	
	public Object getXpath(int xmlDataFormat, DocumentBuilderDTO builderConfig, String xmlId, String xmlSource, String xpathExpression, QName xpathQName) {
		return getXpath(documentFactory.getDocument(xmlDataFormat, builderConfig, xmlId, xmlSource), xpathExpression, xpathQName);
	}

	/**
	 * XML Document에서 xpathExpression에 해당하는 document 오브젝트를 xpathQName으로 리턴하여 줍니다.
	 * @param docment
	 * @param xpathExpression
	 * @param xpathQName
	 * @return
	 */
	public Object getXpath(final Document docment, String xpathExpression, QName xpathQName) {
		if(docment == null) {
			throw new ValidateException("XML Document가 존재하지 않습니다.");
		}
		if(StringUtil.isEmpty(xpathExpression)) {
			throw new ValidateException("XPath Expression이 존재하지 않습니다.");
		}
		
		Object out = null;
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression xPathExpr = xPath.compile(xpathExpression);
			
			if( xpathQName != null ) {
				out = xPathExpr.evaluate(docment, xpathQName);
			}
			else {
				out = xPathExpr.evaluate(docment);
			}
			
		} catch (XPathExpressionException e) {
			throw new ApplicationException("XPath Expression 파싱장애 발생", e);
		}
		return out;
	}
}
