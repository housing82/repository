package com.universal.code.marshaller.impl.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JXElementDTO {

	private String 	nodeName;
	private String 	nodeValue;
	private int 	nodeLevel;
	private JXElementDTO 	nodeParent;
	private short	nodeType;
	private int		childLength;
	private List<JXElementDTO>	childElements;
	private Map<String, String> nodeAttrs;
	
	public JXElementDTO(){
		this.reset();
	}
	
	private void reset(){
		nodeName = "";
		nodeValue = "";
		nodeLevel = 0;
		nodeParent = null;
		nodeType = 0;
		childLength = 0;
		childElements = new ArrayList<JXElementDTO>();
		nodeAttrs = null;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeValue() {
		return nodeValue;
	}

	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
	}

	public int getNodeLevel() {
		return nodeLevel;
	}

	public void setNodeLevel(int nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public JXElementDTO getNodeParent() {
		return nodeParent;
	}

	public void setNodeParent(JXElementDTO nodeParent) {
		this.nodeParent = nodeParent;
	}

	public short getNodeType() {
		return nodeType;
	}

	public void setNodeType(short nodeType) {
		this.nodeType = nodeType;
	}

	public Map<String, String> getNodeAttrs() {
		return nodeAttrs;
	}

	public void setNodeAttrs(Map<String, String> nodeAttrs) {
		this.nodeAttrs = nodeAttrs;
	}

	public int getChildLength() {
		return childLength;
	}

	public void setChildLength(int childLength) {
		this.childLength = childLength;
	}

	public List<JXElementDTO> getChildElements() {
		return childElements;
	}

	public void setChildElements(List<JXElementDTO> childElements) {
		this.childElements = childElements;
	}
	
	public void addChildElements(JXElementDTO jxElementDTO) {
		if( childElements == null ) childElements = new ArrayList<JXElementDTO>();
 		this.childElements.add(jxElementDTO);
	}
	
}
