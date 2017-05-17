package com.universal.code.parameter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ValidateException;
import com.universal.code.utils.DateUtil;
import com.universal.code.utils.PropertyUtil;
import com.universal.code.utils.StringUtil;

@Component
public abstract class ParameterProvider implements Serializable, Map<String, Object>, ParameterHandler {

	protected static final Logger logger = LoggerFactory.getLogger(ParameterProvider.class);
	
	private static final long serialVersionUID = 1457540417890029190L;

	private Map<String, Object> paramMap;

	@Autowired
	private PropertyUtil property;
	
	@Autowired
	private StringUtil string;
	
	//@Autowired
	//private XMLConverter xmlConverter;
	
	//@Autowired
	//private JSONConverter jsonConverter;
	
	public ParameterProvider() {
		
		this.paramMap = new LinkedHashMap<String, Object>();
	}

    public void initialize(HttpServletRequest request){

    	this.setRequest(request);
    }
    
    
	@SuppressWarnings("unchecked")
	public void initialize(Map<String, ? extends Object> map, Class<?> valueType) {
		if( Arrays.class.isAssignableFrom(valueType) || valueType.isArray() ) {
			setRequestMap((Map<String, String[]>) map);
		}
		else {
			setParamMap((Map<String, Object>) map);
		}
	}
	
	
    @Deprecated
    @SuppressWarnings("unchecked")
	public void initialize(Map<String, ? extends Object> map) {

    	if(map != null && map.size() > 0) {
    		
    		for(Object val : map.values()) {
    			if(val.getClass().isArray()) {
    				this.setRequestMap((Map<String, String[]>) map);
    			}
    			else {
    				this.setParamMap((Map<String, Object>) map);
    			}
    			break;
    		}
    	}
    }
	
	
	@Override
	public Object set(String key, Object val) {
		
		return put(key, val);
	}

	/**
	 * Map value is Array String or Single String
	 */
	@Override
	public void setRequestMap(Map<String, String[]> map) {
		if(map == null) return;
		
		Map<String, String[]> maps = map;
		
    	String[] value = null;
    	for(Entry<String, String[]> entry : maps.entrySet()) {
    		value = entry.getValue();
    		if(value != null ) {
    			this.paramMap.put(entry.getKey(), (value.length == 1 ? value[0] : value));
    		}
    	}
	}

	/**
	 * Normal Map set
	 */
	@Override
	public void setParamMap(Map<String, Object> map) {
		this.paramMap = map;
	}

	/**
	 * HttpRequest set
	 */
	@Override
	public void setRequest(HttpServletRequest request) {
		if(request == null) return;
		
		Enumeration<?> enumeration = request.getParameterNames();
		
        String key = null;
        String[] values = null;
        while(enumeration.hasMoreElements()){
            key = (String) enumeration.nextElement();
            values = request.getParameterValues(key);
            if(values != null){
            	this.paramMap.put(key, (values.length > 1) ? values:values[0]);
            }
        }
	}

	public Map<String, Object> getParamMap(){
		return paramMap;
	}
	/*
	@Override
    public String getJSON() {
    	
		return getJSON(IOperateCode.DEFAULT_ENCODING);
    }

	@Override
    public String getJSON(String encoding) {
    	
		return jsonConverter.mapToJson(paramMap, encoding);
    }
	
	@Override
    public String getXML() {
    	
    	return getXML(DEFAULT_XML_ROOT, IOperateCode.DEFAULT_ENCODING);
    }
	
	@Override
    public String getXML(String rootName) {
    	
    	return getXML(rootName, IOperateCode.DEFAULT_ENCODING);
    }
	
    @Override
    public String getXML(String rootName, String encoding) {

    	return xmlConverter.mapToXml(paramMap, rootName, encoding);
    }
    */
	@Override
	public Object getObject(String key) {
		
		return getObject(key, null);
	}

	
	@Override
	public Object getObject(String key, Object def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		return (value != null ? value : def);
	}
	
	@Override
	public String getString(String key) {

		return getString(key, false, null);
	}

	@Override
	public String getString(String key, boolean trim) {

		return getString(key, trim, null);
	}
	
	@Override
	public String getString(String key, String def) {

		return getString(key, false, def);
	}
	
	@Override
	public String getString(String key, boolean trim, String def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(String.class.isAssignableFrom(value.getClass())) {
				if(trim) {
					return ((String) value).trim();
				}
				else {
					return (String) value;
				}
				
			}
			else {
				throw new ValidateException("String is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}

	@Override
	public Integer getInteger(String key) {
		
		return getInteger(key, IOperateCode.INTEGER_ZERO_VALUE);
	}

	@Override
	public Integer getInteger(String key, Integer def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
            if(Integer.class.isAssignableFrom(value.getClass())) {
				return ((Integer) value).intValue();
			}
			else if(Number.class.isAssignableFrom(value.getClass())) {
				return ((Number) value).intValue();
			}
			else if(String.class.isAssignableFrom(value.getClass())) {
				String text = value.toString();
	            if(!StringUtil.hasText(text)) {
	            	return def;
	            }
	            else {
	            	return Integer.parseInt(text);
	            }
			}
			else {
				throw new ValidateException("Integer is not a formal parameter. parameter : ".concat(key));
			}
            
            
		}
		else {
			return def;
		}
	}

	@Override
	public Long getLong(String key) {
		
		return getLong(key, IOperateCode.LONG_ZERO_VALUE);
	}

	@Override
	public Long getLong(String key, Long def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(Long.class.isAssignableFrom(value.getClass())) {
				return ((Long) value).longValue();
			}
			else if(Number.class.isAssignableFrom(value.getClass())) {
				return ((Number) value).longValue();
			}
			else if(String.class.isAssignableFrom(value.getClass())) {
				String text = value.toString();
	            if(!StringUtil.hasText(text)) {
	            	return def;
	            }
	            else {
	            	return Long.parseLong(text);
	            }
			}
			else {
				throw new ValidateException("Long is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}

	@Override
	public Double getDouble(String key) {
		
		return getDouble(key, IOperateCode.DOUBLE_ZERO_VALUE);
	}

	@Override
	public Double getDouble(String key, Double def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(Double.class.isAssignableFrom(value.getClass())) {
				return ((Double) value).doubleValue();
			}
			else if(Number.class.isAssignableFrom(value.getClass())) {
				return ((Number) value).doubleValue();
			}
			else if(String.class.isAssignableFrom(value.getClass())) {
				String text = value.toString();
	            if(!StringUtil.hasText(text)) {
	            	return def;
	            }
	            else {
	            	return Double.parseDouble(text);
	            }
			}
			else {
				throw new ValidateException("Double is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}

	@Override
	public Float getFloat(String key) {
		
		return getFloat(key, IOperateCode.FLOAT_ZERO_VALUE);
	}

	@Override
	public Float getFloat(String key, Float def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(Float.class.isAssignableFrom(value.getClass())) {
				return ((Float) value).floatValue();
			}
			else if(Number.class.isAssignableFrom(value.getClass())) {
				return ((Number) value).floatValue();
			}
			else if(String.class.isAssignableFrom(value.getClass())) {
				String text = value.toString();
	            if(!StringUtil.hasText(text)) {
	            	return def;
	            }
	            else {
	            	return Float.parseFloat(text);
	            }
			}
			else {
				throw new ValidateException("Float is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}

	@Override
	public BigDecimal getBigDecimal(String key) {
		
		return getBigDecimal(key, null);
	}

	@Override
	public BigDecimal getBigDecimal(String key, BigDecimal def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(BigDecimal.class.isAssignableFrom(value.getClass())) {
				return new BigDecimal((String) value);
			}
			else if(String.class.isAssignableFrom(value.getClass())) {
				String text = value.toString();
	            if(!StringUtil.hasText(text)) {
	            	return def;
	            }
	            else {
	            	return new BigDecimal(text);
	            }
			}
			else {
				throw new ValidateException("BigDecimal is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}

	@Override
	public Map<?, ?> getMap(String key) {
		
		return getMap(key, null);
	}

	@Override
	public Map<?, ?> getMap(String key, Map<?, ?> def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(Map.class.isAssignableFrom(value.getClass())) {
				return (Map<?, ?>) value;
			}
			else {
				throw new ValidateException("Map is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}

	@Override
	public List<?> getList(String key) {
		
		return getList(key, null);
	}

	@Override
	public List<?> getList(String key, List<?> def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(Collection.class.isAssignableFrom(value.getClass())) {
				return (List<?>) value;
			}
			else if(value.getClass().isArray()) {
				return Arrays.asList(value);
			}
			else {
				throw new ValidateException("List is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}


	@Override
	public Object[] getArray(String key) {
		
		return getArray(key, null);
	}

	@Override
	public Object[] getArray(String key, Object[] def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(value.getClass().isArray()) {
				return (Object[]) value;
			}
			else if(Collection.class.isAssignableFrom(value.getClass())) {
				return ((Collection<?>) value).toArray();
			}
			else {
				throw new ValidateException("Array is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}
	
	
	@Override
	public Boolean getBoolean(String key) {
		
		return getBoolean(key, false);
	}

	@Override
	public Boolean getBoolean(String key, boolean def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(Boolean.class.isAssignableFrom(value.getClass())) {
				return ((Boolean)value).booleanValue();
			}
			else if(String.class.isAssignableFrom(value.getClass())) {
				return string.strBoolean((String) value);
			}
			else if(Integer.class.isAssignableFrom(value.getClass())) {
				return (1 == ((Integer) value).intValue());
			}
			else {
				throw new ValidateException("Boolean is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}

	@Override
	public Character getCharacter(String key) {
		
		return getCharacter(key, -1, null);
	}

	@Override
	public Character getCharacter(String key, Character def) {
		
		return getCharacter(key, -1, def);
	}

	@Override
	public Character getCharacter(String key, int index) {
		
		return getCharacter(key, index, null);
	}

	@Override
	public Character getCharacter(String key, int index, Character def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(Character.class.isAssignableFrom(value.getClass())) {
            	return ((Character) value).charValue();
			} 
			else if(String.class.isAssignableFrom(value.getClass())) {
            	char[] charArray = value.toString().toCharArray();
        		if(charArray.length <= index) {
        			throw new ValidateException(" 바인드된 케릭터 인덱스가 케릭터 길이와 같거나 클수없습니다. 케릭터 길이 : " + charArray.length + ", 바인드된 케릭터 인덱스 : " + index);
        		}
        		return charArray[index];
            }
			else {
				throw new ValidateException("Character is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}

	@Override
	public Date getDate(String key) {
		
		return getDate(key, null);
	}

	@Override
	public Date getDate(String key, Date def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(Date.class.isAssignableFrom(value.getClass())) {
				return (Date) value;
			}
			else if(String.class.isAssignableFrom(value.getClass())) {
				return DateUtil.getStringToDate(value.toString());
			}
			else {
				throw new ValidateException("Date is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}

	@Override
	public Timestamp getTimestamp(String key) {
		
		return getTimestamp(key, null);
	}

	@Override
	public Timestamp getTimestamp(String key, Timestamp def) {
		if(key == null) {
			return def;
		}
		
		Object value = paramMap.get(key);
		if(value != null) {
			if(Timestamp.class.isAssignableFrom(value.getClass())) {
				return (Timestamp) value;
			}
			else if(Date.class.isAssignableFrom(value.getClass())) {
				Date date = (Date) value;
	            return new Timestamp(date.getTime());
			}
			else {
				throw new ValidateException("Timestamp is not a formal parameter. parameter : ".concat(key));
			}
		}
		else {
			return def;
		}
	}

	
	@Override
	public void doParamToBean(Object bean) {
		if(!paramMap.isEmpty()) {
			for(Entry<?, ?> entry : this.entrySet()){
				if(String.class.isAssignableFrom(entry.getKey().getClass())) {
					property.setProperty(bean, entry.getKey().toString(), entry.getValue());
				}
			}
		}
	}

	@Override
	public int size() {
		return paramMap.size();
	}

	@Override
	public boolean isEmpty() {

		return (paramMap != null ? paramMap.isEmpty() : true);
	}

	@Override
	public boolean containsKey(Object key) {
		
		return paramMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		
		return paramMap.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		
		return paramMap.get(key);
	}


	@Override
	public Object remove(Object key) {
		
		return paramMap.remove(key);
	}

	@Override
	public void clear() {
		
		paramMap.clear();
	}


	@Override
	public Collection<Object> values() {
		
		return paramMap.values();
	}


	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		
		this.paramMap.putAll(m);
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		
		return paramMap.entrySet();
	}

	@Override
	public Object put(String key, Object value) {
		
		return this.paramMap.put(key, value);
	}

	@Override
	public Set<String> keySet() {
		
		return paramMap.keySet();
	}

	@Override
	public String toString() {
        return (new StringBuilder()).append(" count : ").append(paramMap.size()).append(", params : ").append(paramMap).toString();
    }
	
}
