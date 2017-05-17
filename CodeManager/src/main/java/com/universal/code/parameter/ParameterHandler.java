package com.universal.code.parameter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface ParameterHandler {

	public static final String DEFAULT_XML_ROOT = "parameter";
	
    public Object set(String key, Object val);

    public void setRequestMap(Map<String, String[]> map);
    
    public void setParamMap(Map<String, Object> map);

    public void setRequest(HttpServletRequest request);

    public Object getObject(String key);

    public Object getObject(String key, Object def);

    public String getString(String key);

    public String getString(String key, String def);

    public String getString(String key, boolean trim);

    public String getString(String key, boolean trim, String def);
    
    public Integer getInteger(String key);

    public Integer getInteger(String key, Integer def);

    public Long getLong(String key);

    public Long getLong(String key, Long def);

    public Double getDouble(String key);

    public Double getDouble(String key, Double def);
    
    public Float getFloat(String key);

    public Float getFloat(String key, Float def);
    
    public BigDecimal getBigDecimal(String key);

    public BigDecimal getBigDecimal(String key, BigDecimal def);
    
    public Map<?, ?> getMap(String key);

    public Map<?, ?> getMap(String key, Map<?, ?> def);
    
    public List<?> getList(String key);

    public List<?> getList(String key, List<?> def);

    public Object[] getArray(String key);

    public Object[] getArray(String key, Object[] def);
    
    public Boolean getBoolean(String key);
    
    public Boolean getBoolean(String key, boolean def);
    
    public Character getCharacter(String key);
    
    public Character getCharacter(String key, Character def);
    
    public Character getCharacter(String key, int index);
    
    public Character getCharacter(String key, int index, Character def);
    
    public Date getDate(String key);
    
    public Date getDate(String key, Date def);
    
    public Timestamp getTimestamp(String key);
    
    public Timestamp getTimestamp(String key, Timestamp def);
    
    public void doParamToBean(Object bean);
    /*
    public String getJSON();

    public String getJSON(String encoding);
    
    public String getXML();

    public String getXML(String rootName);
    
    public String getXML(String rootName, String encoding);
    */
    public String toString();
}
