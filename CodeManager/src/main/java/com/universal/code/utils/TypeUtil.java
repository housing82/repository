package com.universal.code.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;

@Component
public class TypeUtil  {

	protected static final Logger logger = LoggerFactory.getLogger(TypeUtil.class);

	private CommonUtil commonUtil = new CommonUtil();
	
	private static Set<Class<?>> primitiveWrapper = null;
	
	private static Map<String, String> dataBaseType4j = new HashMap<String, String>();
	
	private static Map<String, Object> dataBaseDefault4j = new HashMap<String, Object>();
	
	static {
		primitiveWrapper = new HashSet<Class<?>>();
		primitiveWrapper.add(java.math.BigDecimal.class);
		primitiveWrapper.add(java.math.BigInteger.class);
		primitiveWrapper.add(Boolean.TYPE);
		primitiveWrapper.add(java.lang.Boolean.class);
		primitiveWrapper.add(Byte.TYPE);
		primitiveWrapper.add(java.lang.Byte.class);
		primitiveWrapper.add(Character.TYPE);
		primitiveWrapper.add(java.lang.Character.class);
		primitiveWrapper.add(java.lang.Class.class);
		primitiveWrapper.add(Double.TYPE);
		primitiveWrapper.add(java.lang.Double.class);
		primitiveWrapper.add(Float.TYPE);
		primitiveWrapper.add(java.lang.Float.class);
		primitiveWrapper.add(Integer.TYPE);
		primitiveWrapper.add(java.lang.Integer.class);
		primitiveWrapper.add(Long.TYPE);
		primitiveWrapper.add(java.lang.Long.class);
		primitiveWrapper.add(Short.TYPE);
		primitiveWrapper.add(java.lang.Short.class);
		primitiveWrapper.add(java.lang.String.class);
		primitiveWrapper.add(java.sql.Date.class);
		primitiveWrapper.add(java.sql.Time.class);
		primitiveWrapper.add(java.sql.Timestamp.class);
		//primitiveWrapper.add(java.io.File.class);
		//primitiveWrapper.add(java.net.URL.class);
	}
	
	static {
		dataBaseType4j.put("NUMBER", "long");
		dataBaseType4j.put("INTEGER", "int");
		dataBaseType4j.put("FLOAT", "float");
		dataBaseType4j.put("DOUBLE", "double");
		dataBaseType4j.put("NVARCHAR2", "String");
		dataBaseType4j.put("VARCHAR2", "String");
		dataBaseType4j.put("CHAR", "String");
		dataBaseType4j.put("LONG", "String");
		dataBaseType4j.put("CLOB", "String");
		dataBaseType4j.put("BLOB", "Blob");
		//dataBaseType4j.put("DATE", "Date");
		dataBaseType4j.put("DATE", "String"); //pms
	}
	
	static {
		dataBaseDefault4j.put("NUMBER", 0);
		dataBaseDefault4j.put("INTEGER", 0);
		dataBaseDefault4j.put("FLOAT", 0);
		dataBaseDefault4j.put("DOUBLE", 0);
		dataBaseDefault4j.put("NVARCHAR2", "\"\"");
		dataBaseDefault4j.put("VARCHAR2", "\"\"");
		dataBaseDefault4j.put("CHAR", "\"\"");
		dataBaseDefault4j.put("LONG", "\"\"");
		dataBaseDefault4j.put("CLOB", "\"\"");
		dataBaseDefault4j.put("BLOB", null);
		//dataBaseDefault4j.put("DATE", null);
		dataBaseDefault4j.put("DATE", "\"\""); //pms
	}
	
	
    /**
     * primitive or primitive wrapper class ( Contains an array ) true/false
     * @param propertyType
     * @return
     */
    public boolean isGeneralType(Class<?> propertyType){
    	return (propertyType.isPrimitive() || commonUtil.isConvertibleRequest(propertyType) || propertyType.isArray());
    }
    
    public boolean isPrimitiveWrapType(Class<?> propertyType) {
    	return primitiveWrapper.contains(propertyType);
    }
    

    
 	public String getDataBaseType4j(String dataBaseType){
 	
		String out = dataBaseType4j.get(dataBaseType);
		
 		return out;
 	}
    
 	public Object getDataBaseDefault4j(String dataBaseType){

 		Object out = dataBaseDefault4j.get(dataBaseType);
		
 		return out;
 	}
 	
    public boolean isSupportedReferenceType(String packages){
    	return isSupportedReferenceType(packages, null, null);
    }

    public boolean isSupportedIncludeReferenceType(String packages, String[] includePackages){
    	return isSupportedReferenceType(packages, includePackages, null);
    }
    
    public boolean isSupportedExcludeReferenceType(String packages, String[] excludePackages){
    	return isSupportedReferenceType(packages, null, excludePackages);
    }
    
    public boolean isSupportedReferenceType(String packages, String[] includePackages, String[] excludePackages){
    	
    	boolean out = true;
    	if(packages == null) return false;
    	
    	String[] excludes = null;
    	if( excludePackages == null || excludePackages.length == 0) {
    		excludes = IOperateCode.EXCLUDE_PACKAGE_STARTS; //{"java.","javax."}
    	}
    	else {
    		excludes = excludePackages;
    	}
    	
    	if(includePackages != null) {
    		for(String include : includePackages){
        		if(packages.startsWith(include)) {
        			out = true;
        			break;
        		}else {
        			out = false;
        		}
        	}
    	}
    	
    	for(String exclude : excludes){
    		if(packages.startsWith(exclude)) {
    			out = false;
    			break;
    		}
    	}
    	
    	if(!out) {
	    	if(logger.isDebugEnabled()) {
	    		logger.debug(" isSupportedReferenceType : " + out + ", type is : " + packages);
	    	}
    	}
    	return out;
    }
 


    public Class<?> getValidGenericeParameterType(Type parameterType, Class<?> defaultWildCardType){
    	
    	Class<?> out = null;
    	
    	if(parameterType instanceof java.lang.reflect.ParameterizedType) {
    		SystemUtil.err( " [WARN] ParameterizedType is Unsupported GeneticeParameterType : " + parameterType);
    		out = null;
    	}
		else if(parameterType instanceof java.lang.reflect.WildcardType) {
			SystemUtil.out( " - WildcardType is Converted to defaultWildCardType : " + defaultWildCardType);
			out = defaultWildCardType;
		}
		else {
			out = (Class<?>) parameterType;
		}
    	
    	return out;
    }
    
    
    public Collection<?> getCollectionType(Class<?> propertyType){
    	
    	Collection<?> coll = null;
    	
    	try {
			if(propertyType.isAssignableFrom(SortedSet.class)) {
				coll = new TreeSet<Object>();	
			}
			else if(propertyType.isAssignableFrom(Set.class)) {
				coll = new HashSet<Object>();
			}
			else if(propertyType.isAssignableFrom(List.class)
					|| propertyType.isAssignableFrom(Collection.class)) {
				coll = new ArrayList<Object>();
			}
			else {
				coll = (Collection<?>) propertyType.newInstance();
			}
    	} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    	
    	return coll;
    }
 
    public Map<?, ?> getMapType(Class<?> propertyType){
    	
    	Map<?, ?> map = null;
    	try {
	    
	    	if(propertyType.isAssignableFrom(SortedMap.class)) {
	    		map = new TreeMap<Object, Object>();	
	    	}
	    	else if(propertyType.isAssignableFrom(Map.class)) {
	    		map = new HashMap<Object, Object>();	
	    	}
	    	else {
				map = (Map<?, ?>) propertyType.newInstance();
	    	}
    	} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    	
    	return map;
    }
    
}
