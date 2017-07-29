package com.universal.code.xml.factory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

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

import org.apache.commons.collections.FastHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.exception.ValidateException;
import com.universal.code.exception.XMLParsingException;
import com.universal.code.utils.CommonUtil;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;
import com.universal.code.xml.factory.dto.DocumentBuilderDTO;
import com.universal.code.xml.factory.dto.DocumentDTO;



@Component
@SuppressWarnings({ "unchecked", "restriction" })
public class DocumentFactory implements Serializable {

	private static final long serialVersionUID = -7833997168158866772L;

	private static final Logger logger = LoggerFactory.getLogger(DocumentFactory.class);
	
	@Autowired
	private FileUtil fileUtil;
	
	private static Map<String, DocumentDTO> documentXmlPool;
	
	private static List<String> nameXmlQue;
	
	private static int DEFAULT_DOCUMENT_CACHE_SIZE = 100;
	
	public static final int XML_FILE = 1;  
	
	public static final int XML_STRING = 2;
	
	static {
		documentXmlPool = new FastHashMap();
		((FastHashMap) documentXmlPool).setFast(true);
		nameXmlQue = new LinkedList<String>();
	}
	
	public DocumentFactory(){
		//test
		if(fileUtil == null) {
	    	fileUtil = new FileUtil();
	    }	
	}
    
    public DocumentDTO getDocumentPool(String xmlId){
    	return documentXmlPool.get(xmlId);
    }
    
	private synchronized void enter(String name, DocumentDTO document) {
		if(logger.isDebugEnabled()) {
			logger.debug(new StringBuilder().append("[XML save to the cache] [name : ").append(name).append(" document : ").append(document).append("]").toString());
		}
    	documentXmlPool.put(name, document);
        ((LinkedList<String>) nameXmlQue).addLast(name);
    }
    
	private void removeFirst() {
        if(documentXmlPool != null) {
        	if(logger.isDebugEnabled()) {
        		logger.debug("캐쉬 최대 개수를 초과하여 가장 첫번째 캐쉬된 XML문서를 삭제 합니다.");
        	}
        	documentXmlPool.remove(((LinkedList<String>) nameXmlQue).removeFirst());
        }
    }
	
    public static void clearAll() {
        if(nameXmlQue != null) {
        	nameXmlQue.clear();
        }
        if(documentXmlPool != null) {
        	documentXmlPool.clear();
        }
    }

    public Document getDocument(int dataFormat, String xmlId, String xmlSource) {
    	return getDocument(dataFormat, null, xmlId, xmlSource);
    }
    
    public Document getDocument(int dataFormat, DocumentBuilderDTO builderConfig, String xmlId, String xmlSource) {
    	if(logger.isDebugEnabled()) {
    		logger.debug("[START] getDocument : ".concat(xmlSource));
    	}
        
    	/** 캐쉬에서 문서 정보 획득 */
        DocumentDTO documentStore = getDocumentPool(xmlId);
        
    	Document document = null;
    	Object xmlObject = null;
    	long lastModified = -1;
    	
		switch (dataFormat) {
			case XML_FILE:
		        /** 파일객체 생성 */
				File docFile = new File(xmlSource);
		        /** 파일의 최종수정시간검사 */
		        lastModified = docFile.lastModified(); 
		        /** xml File 객체 */
		        xmlObject = docFile;
				break;
			case XML_STRING:
		        /** 문자XML 변경여부 검사 */
				if(documentStore == null || documentStore.getXmlText() == null) {
					lastModified = 1;
				}
				else if(documentStore.getXmlText().equals(xmlSource)) {
		        	lastModified = 1;	
		        }
		        else {
		        	lastModified = 0;
		        }
		        /** xml String 객체 */
		        xmlObject = xmlSource;
				break;
			default:
				throw new ApplicationException("XML데이터 형식지정이 잘못되었습니다.");
		}

        /** 문서가 이미 캐시에 존재하고, 동일한 것이라면 풀에서 리턴한다. */
        if (documentStore != null && documentStore.getLastModified() == lastModified) {
        	//캐쉬에서 문서 리턴
        	if(logger.isDebugEnabled()) {
        		logger.debug("- Returns documents from the cache");
        	}
        	document = documentStore.getDocument();
        }
        else {
        	//신규 캐쉬후 문서 리턴 
            if(logger.isDebugEnabled()) {
        		logger.debug("+ After returning generate new xml documents");
        	}
        	try {
        		document = buildNewDocument(builderConfig, xmlId, xmlObject, lastModified);
			} catch (XMLParsingException e) {
				throw new ApplicationException("신규 XML문서 생성 장애 발생", e);
			} 
        }
        if(logger.isDebugEnabled()) {
    		logger.debug("[END] getDocument");
    	}
        return document;
    }
    
    public Document buildNewDocument(String xmlId, Object xmlSource, long lastModified) throws XMLParsingException {
    	return buildNewDocument(null, xmlId, xmlSource, lastModified);
    }
    
    public Document buildNewDocument(DocumentBuilderDTO builderConfig, String xmlId, Object xmlSource, long lastModified) throws XMLParsingException {
    	if(logger.isDebugEnabled()) {
    		logger.debug("[START] buildNewDocument");
    	}

        Document documentObj = null;
        String xmlContents = null;
        try {
        	DocumentBuilder documentBuilder = getDocumentBuilder(builderConfig);
        	
        	if(File.class.isAssignableFrom(xmlSource.getClass())) {
            	if(logger.isDebugEnabled()) {
            		logger.debug("- Parse File xml");
            	}
            	xmlContents = fileUtil.getTextFileContent((File) xmlSource);
        		documentObj = documentBuilder.parse((File) xmlSource);
        	}
        	else if(String.class.isAssignableFrom(xmlSource.getClass())) {
        		if(logger.isDebugEnabled()) {
            		logger.debug("- Parse String xml");
            	}
        		xmlContents = (String) xmlSource;
        		documentObj = documentBuilder.parse(new ByteArrayInputStream(((String) xmlSource).getBytes()));
        	}
        	else {
        		throw new ValidateException("잘못된 XML형식입니다.");
        	}
        	
            /** 큐 사이즈가 최대허용 사이즈보다 크다면 캐쉬에서 제일 오래된 내용삭제*/
            if (nameXmlQue.size() > DEFAULT_DOCUMENT_CACHE_SIZE) {
            	removeFirst();
            }
            
            DocumentDTO documentStore = new DocumentDTO(documentObj, lastModified);
            documentStore.setXmlText(xmlContents);
            
            enter(xmlId, documentStore);
        } catch (SAXException e) {
            throw new XMLParsingException("XML문서 분석 장애 발생", e);
        } catch (IOException e) {
            throw new XMLParsingException("XML문서가 존재하지 않거나 잘못되었습니다.", e);
        } 
        
    	if(logger.isDebugEnabled()) {
    		logger.debug("[END] buildNewDocument");
    	}
    	return documentObj;
    }
    
    public DocumentBuilder getDocumentBuilder(){
    	return getDocumentBuilder(null);
    }
    
    public DocumentBuilder getDocumentBuilder(DocumentBuilderDTO documentbuilderConfig){
    	
    	DocumentBuilderDTO builderConfig = null;
    	if(documentbuilderConfig == null) {
    		builderConfig = new DocumentBuilderDTO();
    	}
    	else {
    		builderConfig = documentbuilderConfig;
    	}
    	
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        //파싱하기 전에 Valide검사를 할지 여부를 지정 주로 DTD문서가 존재하면 true 존재하지 않으면 false
        documentBuilderFactory.setValidating(builderConfig.isValidating()); 
        //Namespace 해석을 할 지 여부를 지정 즉, xml문서에 Namespace가 존재할 때 Namespace의 실제 여부를 판단할지(true) 실제 존재 여부는 판단하지 않고 단순히 하나의 태그로 볼지를 선택(false)
        documentBuilderFactory.setNamespaceAware(builderConfig.isNamespaceAware());
        //Whitespace(공백)은 정보에서 무시할지에 대한 설정
        documentBuilderFactory.setIgnoringElementContentWhitespace(builderConfig.isIgnoringElementContentWhitespace()); 
        
        if(builderConfig.getExpandEntityReferences() != null) {
        	documentBuilderFactory.setExpandEntityReferences(builderConfig.getExpandEntityReferences());
        }
        if(builderConfig.getCoalescing() != null) {
        	documentBuilderFactory.setCoalescing(builderConfig.getCoalescing());
        }
        if(builderConfig.getXIncludeAware() != null) {
        	documentBuilderFactory.setXIncludeAware(builderConfig.getXIncludeAware());
        }
        if(builderConfig.getIgnoringComments() != null) {
        	documentBuilderFactory.setIgnoringComments(builderConfig.getIgnoringComments());
        }
        
        if(builderConfig.getSchema() != null) {
        	documentBuilderFactory.setSchema(builderConfig.getSchema());
        }
        
        if(builderConfig.getAttributes() != null) {
        	for(Entry<String, Object> entry : builderConfig.getAttributes().entrySet()){
        		documentBuilderFactory.setAttribute(entry.getKey(), entry.getValue());
        	}
        }
        
        if(builderConfig.getFeature() != null) {
        	try {
	        	for(Map<String, Object> feature : builderConfig.getFeature()) {
					documentBuilderFactory.setFeature(builderConfig.getFeatureKey(feature), builderConfig.getFeatureValue(feature));
	        	}
        	} catch (ParserConfigurationException e) {
				throw new ApplicationException(e);
			}
        }
        
    	DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ApplicationException("문서 설정 장애 발생", e);
		}
		
    	return documentBuilder;
    }
    
    public Document getNormalizeDocument(int dataFormat, String xmlId, String docPath) {
    	return getNormalizeDocument(dataFormat, null, xmlId, docPath);
    }
    
	public Document getNormalizeDocument(int dataFormat, DocumentBuilderDTO builderConfig, String xmlId, String docPath) {
		/** filePath 의 XML의 Document 를 획득한다. */
		Document doc = getDocument(dataFormat, builderConfig, xmlId, docPath);
		/** DOM Tree 를 XML 문서의 구조대로 완성한다. */
		doc.getDocumentElement().normalize(); 
		return doc;
	}
		
	public boolean createNewXmlFile(String rootName, String docPath) {
		return createNewXmlFile(null, rootName, null, docPath, null, null);
	}
	
	public boolean createNewXmlFile(String rootName, String docPath, String encoding) {
		return createNewXmlFile(null, rootName, null, docPath, encoding, null);
	}
	
	public boolean createNewXmlFile(String rootName, Properties rootAttrs, String docPath) {
		return createNewXmlFile(null, rootName, rootAttrs, docPath, null, null);
	}
	
	public boolean createNewXmlFile(String rootName, Properties rootAttrs, String docPath, String encoding) {
		return createNewXmlFile(null, rootName, rootAttrs, docPath, encoding, null);
	}
	
	public boolean createNewXmlFile(String rootName, Properties rootAttrs, String docPath, String encoding, Properties outputProperties){
		return createNewXmlFile(null, rootName, rootAttrs, docPath, encoding, outputProperties);
	}
	
	public boolean createNewXmlFile(DocumentBuilderDTO builderConfig, String rootName, String docPath) {
		return createNewXmlFile(builderConfig, rootName, null, docPath, null, null);
	}
	
	public boolean createNewXmlFile(DocumentBuilderDTO builderConfig, String rootName, String docPath, String encoding) {
		return createNewXmlFile(builderConfig, rootName, null, docPath, encoding, null);
	}

	public boolean createNewXmlFile(String rootName, String docPath, String encoding, Properties outputProperties){
		return createNewXmlFile(null, rootName, null, docPath, encoding, outputProperties);
	}
	
	public boolean createNewXmlFile(DocumentBuilderDTO builderConfig, String rootName, String docPath, String encoding, Properties outputProperties){
		return createNewXmlFile(builderConfig, rootName, null, docPath, encoding, outputProperties);
	}
	
	public boolean createNewXmlFile(DocumentBuilderDTO builderConfig, String rootName, Properties rootAttrs, String docPath) {
		return createNewXmlFile(builderConfig, rootName, rootAttrs, docPath, null, null);
	}
	
	public boolean createNewXmlFile(DocumentBuilderDTO builderConfig, String rootName, Properties rootAttrs, String docPath, String encoding) {
		return createNewXmlFile(builderConfig, rootName, rootAttrs, docPath, encoding, null);
	}
		
	public boolean createNewXmlFile(DocumentBuilderDTO builderConfig, String rootName, Properties rootAttrs, String docPath, String encoding, Properties outputProperties){
		
		boolean out = false;
		if(rootName == null) {
			throw new ValidateException("XML RootName이 존재하지 않습니다.");
		}
		if(docPath == null) {
			throw new ValidateException("XML파일 경로가 존재하지 않습니다.");
		}		
		if(!FileUtil.getExt(docPath).equalsIgnoreCase("xml")) {
			throw new ApplicationException("Document(XML)파일의 확장자지정이 잘못되었습니다.");
		}
		
		if( !fileUtil.exists(docPath) ) {
			String fileDir = null;
			String fileName = null;
			if(docPath.indexOf(SystemUtil.FILE_SEPARATOR) > -1) {
				fileDir = docPath.substring(0, docPath.lastIndexOf(SystemUtil.FILE_SEPARATOR));
				fileName = docPath.substring(docPath.lastIndexOf(SystemUtil.FILE_SEPARATOR) + SystemUtil.FILE_SEPARATOR.length());
			}
			else {
				fileName = docPath;
			}

			if( fileUtil.mkfile(fileDir, fileName, null, encoding, false, true) != null ) {

				DocumentBuilder documentBuilder = getDocumentBuilder(builderConfig);
				Document document = documentBuilder.newDocument();
				Element root = document.createElement(rootName);
				if(rootAttrs != null) {
					for(Entry<Object, Object> entry : rootAttrs.entrySet()){
						if(CommonUtil.isNotEmptys(entry.getKey(), entry.getValue())) {
							root.setAttribute((String) entry.getKey(), (String) entry.getValue());
						}
					}
				}
				document.appendChild(root);
				out = documentTransformer(document, docPath, encoding, outputProperties);
			}
		}
		else {
			if(logger.isWarnEnabled()) {
				logger.warn("- 존재하는 문서(파일)입니다.");
			}
		}
		return out;
	}
	
	public boolean documentTransformer(Document document, String docPath) {
		return documentTransformer(document, docPath, "");
	}
	
	public boolean documentTransformer(Document document, String docPath, String encoding) {
		return documentTransformer(document, docPath, encoding, null);
	}
	
	public boolean documentTransformer(Document document, String docPath, Properties outputProperties) {
		return documentTransformer(document, docPath, null, outputProperties);
	}
	
	public synchronized boolean documentTransformer(Document document, String docPath, String encoding, Properties outputProperties) {

		boolean out = false;
		try {
			if(!FileUtil.getExt(docPath).equalsIgnoreCase("xml")) {
				throw new ApplicationException("Document(XML)파일의 확장자지정이 잘못되었습니다.");
			}
			Transformer transformer = getTransformer(encoding, outputProperties);
			
			/** 변경한 내용을 xml파일에 저장 */
			transformer.transform(new DOMSource(document), new StreamResult(docPath));
			if (logger.isDebugEnabled()) {
				logger.debug("documentTransformer.transformer saved");
			}
			out = true;
		} catch (TransformerConfigurationException e) {
			throw new ApplicationException(e);
		} catch (TransformerException e) {
			throw new ApplicationException(e);
		}
		return out;
	}
	
	public Transformer getTransformer(String encoding) {
		return getTransformer(encoding, null);
	}
	
	public Transformer getTransformer(String encoding, Properties outputProperties) {

		Transformer transformer = null;
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance(); // 변환기 공장 생성
			transformer = transformerFactory.newTransformer();
			
			/** 인코딩이 없을경우 기본인코딩 대입 */
			if(StringUtil.isEmpty(encoding)) {
				encoding = IOperateCode.DEFAULT_ENCODING;
			}
			/** 변환기 생성 */
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "yes");
			transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
			
			//transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"DTD 문서 경로 ex) /workhome/app/WEB-INF/dtds/schame.dtd");
			//transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			//transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			
			if(outputProperties != null && outputProperties.size() > 0) {
				for(Entry<Object, Object> entry : outputProperties.entrySet()){
					if(logger.isDebugEnabled()) {
						logger.debug("outputProperties : " + entry.getKey() + " : " + entry.getValue());
					}
					if(CommonUtil.isNotEmptys(entry.getKey(), entry.getValue())) {
						transformer.setOutputProperty(entry.getKey().toString(), entry.getValue().toString());
					}
				}
			}
			
		} catch (TransformerConfigurationException e) {
			throw new ApplicationException(e);
		}
		
		return transformer;
	}
}

