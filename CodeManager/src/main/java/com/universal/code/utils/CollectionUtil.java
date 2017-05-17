package com.universal.code.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CollectionUtil {

	private static final Logger logger = LoggerFactory.getLogger(CollectionUtil.class);
    
    public List<?> arrayToList(Object[] array){
    	if( array == null ) return null;
    	return Arrays.asList(array);
    }
    
    
    public Object[] listToArray(List<?> list){
    	if( list == null ) return null;
    	return list.toArray();
    }
    
    
	public List<?> toUniqueList(List<?> list){
    	if( list == null ) return null;
    	return new ArrayList<Object>(new LinkedHashSet<Object>(list));
    }
    
	
    public static Object first(List<Object> elements) {
    	return elements.get(0);
	}

    
    public static Object last(List<Object> elements) {
    	return elements.get(elements.size());
	}
    
    
    public static int index(List<Object> elements, Object value) {
    	return elements.indexOf(value);
	}
    
    
    public static Object value(List<Object> elements, int index) {
    	return elements.get(index);
	}
    
}
