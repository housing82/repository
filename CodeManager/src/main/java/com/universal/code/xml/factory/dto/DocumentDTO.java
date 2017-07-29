package com.universal.code.xml.factory.dto;

import java.io.Serializable;

import org.w3c.dom.Document;

public class DocumentDTO  implements Serializable {

	private static final long serialVersionUID = 906762060492016605L;

	private Document document;

	private String xmlText;
	
	private long lastModified;

    public DocumentDTO(Document document, long lastModified) {
        this.document = document;
        this.lastModified = lastModified;
    }

    public Document getDocument() {
        return document;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setDocument(Document document) {
    	this.document = document;
    }

    public void setLastModified(long l) {
        lastModified = l;
    }

	public String getXmlText() {
		return xmlText;
	}

	public void setXmlText(String xmlText) {
		this.xmlText = xmlText;
	}
	
}
