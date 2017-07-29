package com.universal.code.xml.process.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

public class ElementDTO {

	Map<String, String> attributes;
	
	
	String nodeName;
	String nodeText;
	List<ElementDTO> childrens;
	String xpathExpr;
	QName xqname;
	
	List<String> removeAttributes;
	boolean removeNode;
	boolean removeNodeText;
	boolean removeNodeChildAll;
	boolean modified;
	
	String modifyNodeName;
	String modifyAttr;
	String modifyAttrValue;
	
	public ElementDTO(){
		attributes = null;
		removeAttributes = null;
		nodeName = null;
		nodeText = null;
		childrens = null;
		xpathExpr = null;
		xqname = null;
		removeNode = false;
		removeNodeText = false;
		removeNodeChildAll = false;
		modified = false;
		modifyNodeName = null;
		modifyAttr = null;
		modifyAttrValue = null;		
	}
	
	public String getModifyNodeName() {
		return modifyNodeName;
	}

	public void setModifyNodeName(String modifyNodeName) {
		this.modifyNodeName = modifyNodeName;
	}

	public String getModifyAttr() {
		return modifyAttr;
	}

	public void setModifyAttr(String modifyAttr) {
		this.modifyAttr = modifyAttr;
	}

	public String getModifyAttrValue() {
		return modifyAttrValue;
	}

	public void setModifyAttrValue(String modifyAttrValue) {
		this.modifyAttrValue = modifyAttrValue;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public boolean isRemoveNode() {
		return removeNode;
	}

	public void setRemoveNode(boolean removeNode) {
		this.removeNode = removeNode;
	}

	public boolean isRemoveNodeText() {
		return removeNodeText;
	}

	public void setRemoveNodeText(boolean removeNodeText) {
		this.removeNodeText = removeNodeText;
	}

	public boolean isRemoveNodeChildAll() {
		return removeNodeChildAll;
	}

	public void setRemoveNodeChildAll(boolean removeNodeChildAll) {
		this.removeNodeChildAll = removeNodeChildAll;
	}

	// 페어런트 노드 ?
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	public void addAttributes(String key, String value) {
		if( this.attributes == null ) {
			this.attributes = new LinkedHashMap<String, String>();
		}
		this.attributes.put(key, value);
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeText() {
		return nodeText;
	}
	public void setNodeText(String nodeText) {
		this.nodeText = nodeText;
	}
	public List<ElementDTO> getChildrens() {
		return childrens;
	}
	public void setChildrens(List<ElementDTO> childrens) {
		this.childrens = childrens;
	}
	public void addChildrens(ElementDTO value) {
		if( this.childrens == null ) {
			this.childrens = new ArrayList<ElementDTO>();
		}
		this.childrens.add(value);
	}
	
	public List<String> getRemoveAttributes() {
		return removeAttributes;
	}

	public void setRemoveAttributes(List<String> removeAttributes) {
		this.removeAttributes = removeAttributes;
	}

	public void addRemoveAttributes(String value) {
		if( this.removeAttributes == null ) {
			this.removeAttributes = new ArrayList<String>();
		}
		this.removeAttributes.add(value);
	}
	
	public String getXpathExpr() {
		return xpathExpr;
	}
	public void setXpathExpr(String xpathExpr) {
		this.xpathExpr = xpathExpr;
	}
	public QName getXqname() {
		return xqname;
	}
	public void setXqname(QName xqname) {
		this.xqname = xqname;
	}
}
