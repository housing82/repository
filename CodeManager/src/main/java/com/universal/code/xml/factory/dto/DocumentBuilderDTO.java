package com.universal.code.xml.factory.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.validation.Schema;

public class DocumentBuilderDTO {

	private boolean validating;
	private boolean namespaceAware;
	private boolean ignoringElementContentWhitespace;
    private Boolean expandEntityReferences;
    private Boolean coalescing;
    private Boolean XIncludeAware;
    private Boolean ignoringComments;
    private Schema schema;
    private Hashtable<String, Object> attributes;
    private List<Map<String, Object>> feature;
    
    
	public DocumentBuilderDTO(){
		this.reset();
	}
	
	private void reset(){
		validating = false;
		namespaceAware = false;
		ignoringElementContentWhitespace = false;
		
		expandEntityReferences = null;
		coalescing = null;
		XIncludeAware = null;
		ignoringComments = null;
		schema = null;
		attributes = null;
		feature = null;
	}

	public boolean isValidating() {
		return validating;
	}

	public void setValidating(boolean validating) {
		this.validating = validating;
	}

	public boolean isNamespaceAware() {
		return namespaceAware;
	}

	public void setNamespaceAware(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
	}

	public boolean isIgnoringElementContentWhitespace() {
		return ignoringElementContentWhitespace;
	}

	public void setIgnoringElementContentWhitespace(
			boolean ignoringElementContentWhitespace) {
		this.ignoringElementContentWhitespace = ignoringElementContentWhitespace;
	}

	public Boolean getExpandEntityReferences() {
		return expandEntityReferences;
	}

	public void setExpandEntityReferences(Boolean expandEntityReferences) {
		this.expandEntityReferences = expandEntityReferences;
	}

	public Boolean getCoalescing() {
		return coalescing;
	}

	public void setCoalescing(Boolean coalescing) {
		this.coalescing = coalescing;
	}

	public Boolean getXIncludeAware() {
		return XIncludeAware;
	}

	public void setXIncludeAware(Boolean xIncludeAware) {
		XIncludeAware = xIncludeAware;
	}

	public Boolean getIgnoringComments() {
		return ignoringComments;
	}

	public void setIgnoringComments(Boolean ignoringComments) {
		this.ignoringComments = ignoringComments;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public Hashtable<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Hashtable<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void addAttributes(String key, Object value) {
		if(attributes == null) this.attributes = new Hashtable<String, Object>();
		this.attributes.put(key, value);
	}

	public List<Map<String, Object>> getFeature() {
		return feature;
	}
	
    public void setFeature(String key, Object value){
    	if(feature == null) {
    		feature = new ArrayList<Map<String, Object>>();
    	}
    	Map<String, Object> featureData = new HashMap<String, Object>();
    	featureData.put("key", key);
    	featureData.put("value", value);
    	feature.add(featureData);
    }
    
    public String getFeatureKey(Map<String, Object> feature){
    	if(feature == null) return null;
    	return (String) feature.get("key");
    }
    
    public boolean getFeatureValue(Map<String, Object> feature){
    	if(feature == null) return false;
    	return (Boolean) feature.get("value");
    }
	
	
}