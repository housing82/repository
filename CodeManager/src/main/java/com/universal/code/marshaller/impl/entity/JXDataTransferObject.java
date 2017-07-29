package com.universal.code.marshaller.impl.entity;

import java.util.ArrayList;
import java.util.List;

public class JXDataTransferObject {


	private String 	rootName;
	private long	size;
	private long 	startTimeMillis;
	private long 	endTimeMillis;
	private List<JXElementDTO> element;
	
	public JXDataTransferObject(){
		this.reset();
	}
	
	private void reset(){
		rootName = "";
		size = 0;
		startTimeMillis = 0;
		endTimeMillis = 0;
		element = null; 
	}


	public String getRootName() {
		return rootName;
	}


	public void setRootName(String rootName) {
		this.rootName = rootName;
	}


	public long getSize() {
		return size;
	}


	public void setSize(long size) {
		this.size = size;
	}


	public long getStartTimeMillis() {
		return startTimeMillis;
	}


	public void setStartTimeMillis(long startTimeMillis) {
		this.startTimeMillis = startTimeMillis;
	}


	public long getEndTimeMillis() {
		return endTimeMillis;
	}

	public void setEndTimeMillis(long endTimeMillis) {
		this.endTimeMillis = endTimeMillis;
	}

	public long getRunTimeMillis() {
		return (getEndTimeMillis() - getStartTimeMillis());
	}

	public List<JXElementDTO> getElement() {
		return element;
	}

	public void setElement(List<JXElementDTO> element) {
		this.element = element;
	}

	public void addElement(JXElementDTO element) {
		if(this.element == null) {
			this.element = new ArrayList<JXElementDTO>();
		}
		this.element.add(element);
	}
	
	public void clear(){
		
		if( element != null ) {
			element.clear();
		}
	}
}
