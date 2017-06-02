package com.universal.code.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.exception.ValidateException;



/**
* <p>Title: PopulateUtil</p>
* <p>Description:
* Object 들의 Populate 유틸 클래스
* </p>
* <p>Copyright: Copyright (c) 2011</p>
* <p>Company: mvc</p>
* @since 2011. 07. 04
* @author ksw
* @version 1.0
*/
@Component
public class PropertyUtil {

	private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

	@Autowired
	private CollectionUtil collectionUtil;
	
	@Autowired
	private ReflectUtil reflectUtil;

	@Autowired
	private TypeUtil typeUtil;
	
	public PropertyUtil(){
		//PropertyUtil를 @Autowired하지 않고 new PropertyUtil() 하였을경우 
		if(collectionUtil == null) this.collectionUtil = new CollectionUtil();
		if(reflectUtil == null) this.reflectUtil = new ReflectUtil();
		if(typeUtil == null) this.typeUtil = new TypeUtil();
	}
	
	/**
	 * 주어진 bean 에 propertyName 이 존재한다면 propertyName 의 value 를 반환
	 * 업다면 null을 반환합니다.
	 * @param bean
	 * @param propertyName
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
    public static  Object getProperty(Object bean, String propertyName) {
        if(bean == null || StringUtil.isEmpty(propertyName)) {
        	throw new ApplicationException(" The parameter was null or invalid. ");
        }
        /*
        PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(bean, propertyName);
        if(propertyDescriptor != null) {
        	propertyDescriptor.getValue(propertyName);
        }
        */
        
        try {
			if( PropertyUtils.getPropertyDescriptor(bean, propertyName) != null ) {
				return BeanUtils.getProperty(bean, propertyName);
			}
		} catch (IllegalAccessException e) {
			throw new ApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new ApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new ApplicationException(e);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
        
        if(logger.isWarnEnabled()) {
        	logger.warn(CommonUtil.addString("[WARN] The property does not exist [bean : ", bean.getClass().getCanonicalName(), ", property : ", propertyName, "]"));
        }
        return null;
    }

    /**
     * 주어진 bean 에 propertyName 이 존재한다면 propertyName 에 result 를 setting
     * 결과 true/false 를 반환합니다.
     * @param bean
     * @param propertyName
     * @param result
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static boolean setProperty(Object bean, String propertyName, Object result) {
        if(bean == null || StringUtil.isEmpty(propertyName)) {
        	throw new ApplicationException(" The parameter was null or invalid. ");
        }
        /*
        PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(bean, propertyName);
        if(propertyDescriptor != null) {
        	propertyDescriptor.setValue(propertyName, result);
        }
        */
        
        try {
			if( PropertyUtils.getPropertyDescriptor(bean, propertyName) != null ) {
				BeanUtils.setProperty(bean, propertyName, result);
				return true;
			}
		} catch (IllegalAccessException e) {
			throw new ApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new ApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new ApplicationException(e);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
        
        if(logger.isWarnEnabled()) {
        	logger.warn(CommonUtil.addString("[WARN] The property does not exist [bean : ", bean.getClass().getCanonicalName(), ", property : ", propertyName, "]"));
        }
        return false;
    }

    /**
     * 바인드된 copyTarget 에 담겨있는 propertyName 이
     * readBean 에 존재시 데이터를  writeBean 의 동일한 propertyName 에 setting
     * @param readBean
     * @param writeBean
     * @param copyTarget
     * @return
     */
    public boolean  copyProperty(Object readBean, Object writeBean, List<String> copyTarget){
    	boolean result = false;
    	String propertyName = "";
    	
    	for(int i=0; i < copyTarget.size(); i++) {
    		propertyName = copyTarget.get(i);
			setProperty(writeBean, propertyName, getProperty(readBean , propertyName));
    	}
    	return result;
    }


    public boolean copySameProperty(Object readBean, Object writeBean) {
    	return copySameProperty(readBean, writeBean, false);
    }
    
    /**
     * readBean의 프로퍼티 명과 프로퍼티 타입이 일치하는 writeBean의 프로퍼티에 값을 담아줌. 
     * @param readBean
     * @param writeBean
     * @param copyTarget
     * @return
     */
    public boolean copySameProperty(Object readBean, Object writeBean, boolean isLogging){
    	boolean result = false;
    	String readName = null;
    	Class<?> readType = null;
    	Object readValue = null;
    	try {

    		PropertyDescriptor[] readProperties  = PropertyUtils.getPropertyDescriptors(readBean);
    		PropertyDescriptor[] writeProperties = PropertyUtils.getPropertyDescriptors(writeBean);
    		
    		for (PropertyDescriptor read : readProperties) {
    			readName = read.getName();
    			readType = read.getPropertyType();
    			
    			for (PropertyDescriptor write : writeProperties) {
    			
    				if(readName.equals(write.getName()) && readType.isAssignableFrom(write.getPropertyType())) {
    					
    					readValue = BeanUtils.getProperty(readBean, readName);
    					//readValue = getProperty(readBean, readName);
    					
    					if(isLogging && logger.isDebugEnabled()) {
	    					logger.debug(new StringBuilder().append("\n [Copy Property Infomation]")
	    						.append("\n readName : ").append(readName)
	    						.append("\n readValue : ").append(readValue)
	    						.append("\n readType : ").append(readType.getCanonicalName()) 
	    						.append("\n isInterface : ").append(readType.isInterface())
	    						.append("\n isAnnotation : ").append(readType.isAnnotation())
	    						.append("\n isArray : ").append(readType.isArray())
	    						.append("\n isEnum : ").append(readType.isEnum())
	    						.append("\n").toString()
	    					);
    					}
    					
    					if( !readType.isAssignableFrom(Map.class) /* && readValue != null */ ) {
    						BeanUtils.setProperty(writeBean, readName, readValue);
    						//setProperty(writeBean, readName, readValue);
    					}
    					break;
    				}
    			}
    		}
		} catch (IllegalAccessException e) {
			throw new ApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new ApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new ApplicationException(e);
		} 

    	return result;
    }
    
    /**
     * 추출에 제외할 메소드명을 반환
     * @param excludeMethods
     * @return
     */
    public List<String> getExcludeNames(String[] excludeMethods){
    	List<String> excludeNames = new ArrayList<String>();
    	@SuppressWarnings("unchecked")
		List<String> customNames = (List<String>) collectionUtil.arrayToList(excludeMethods);
    	if(customNames != null) excludeNames.addAll(customNames); 
    	if(excludeNames.indexOf("class") == -1) excludeNames.add("class");
    	if(excludeNames.indexOf("jspPropertyGroups") == -1) excludeNames.add("jspPropertyGroups");
    	return excludeNames;
    }
    
    public Object getObjectList(Object bean, Class<?> extractType, String[] excludeMethods, boolean isLogging) {
    	return out(bean, extractType, excludeMethods, isLogging, new Throwable().getStackTrace() , 0, true);
    }
    
    public Object out(Object bean) {
    	return out(bean, false, true);
    }
    
    public Object out(Object bean, boolean childExtract) {
    	return out(bean, false, childExtract);
    }
    
    public Object out(Object bean, boolean childExtract, String... excludeMethods) {
    	return out(bean, excludeMethods, false, childExtract);
    }
    
    public Object out(Object bean, boolean isLogging, boolean childExtract) {
    	return out(bean, null, isLogging, childExtract, new Throwable().getStackTrace());
    }
    
    public Object out(Object bean, String[] excludeMethods, boolean isLogging, boolean childExtract) {
    	return out(bean, excludeMethods, isLogging, childExtract, new Throwable().getStackTrace());
    }
    
    public Object out(Object bean, String[] excludeMethods, boolean isLogging, boolean childExtract, StackTraceElement[] stacks) {
    	return out(bean, null, excludeMethods, isLogging, (stacks == null ? new Throwable().getStackTrace() : stacks) , 0, childExtract);
    }
    

    
    /**
     * 바인딩된 bean 의 내부 내용을 log4j 로 출력하여줍니다.
     * @param bean
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private Object out(Object bean, Class<?> extractType, String[] excludeMethods, boolean isLogging, StackTraceElement[] stacks, int level, boolean childExtract) {

    	StringBuffer message = new StringBuffer();
    	
    	List<Object> extractList = new ArrayList<Object>();
    	
    	List<String> excludeNames = getExcludeNames(excludeMethods);
    	    	
    	if(bean != null) {
    		
        	//StackTraceElement[] stacksTrace = new Throwable().getStackTrace();
        	if(stacks == null) stacks = new Throwable().getStackTrace();
        	StackTraceElement beforeStack = getCaller(stacks);
        	
    		String propertyName = "";
    		String propertyType = "";
    		Object propertyValue = null;
    		PropertyDescriptor property = null;
            PropertyDescriptor propertyDescriptors[] = PropertyUtils.getPropertyDescriptors(bean);
            
            String whiteSpace = "";
        	for(int j=0;j < level;j++) {
        		whiteSpace = whiteSpace.concat(IOperateCode.STR_TAB);
        	}
        	
        	message.append(SystemUtil.LINE_SEPARATOR);
        	message.append(whiteSpace);
        	if(level == 0) message.append(" ■■■■■■■■■■■■■■■■■■■ ");
        	else message.append(" → ");
        	message.append("[Printout level : " + level + " Start] class : " + bean.getClass().getCanonicalName());
        	
        	if(level == 0) message.append(" ■■■■■■■■■■■■■■■■■■■ ");
            message.append(SystemUtil.LINE_SEPARATOR);
            
            if(level == 0) {
	            message.append(whiteSpace);                
	            message.append(" [ caller is = ");
	            message.append(beforeStack.getClassName());
	            message.append(IOperateCode.STR_DOT);
	            message.append(beforeStack.getMethodName());
	            message.append(" ] ");
	            message.append(SystemUtil.LINE_SEPARATOR);
            }
            message.append(SystemUtil.LINE_SEPARATOR);
            
            for(int i=0;i<propertyDescriptors.length;i++){
            	
            	property = propertyDescriptors[i];

            	propertyName = property.getName();
            	
            	if( property.getPropertyType() == null ) {
            		if(isLogging && logger.isDebugEnabled()) {
						logger.debug(CommonUtil.addString(
							new Object[]{
								 "\n  X [ property name ] " , propertyName , " PropertyType is NULL"
							})
						);
            		}
            		continue;
            	}
            	
            	propertyType = property.getPropertyType().getCanonicalName();
            	
            	if(excludeNames != null && excludeNames.indexOf(propertyName) > -1) continue;

            	try {
            		if( property.getReadMethod() != null) {
            			propertyValue = PropertyUtils.getProperty(bean, propertyName);
            		}
            		else {
            			propertyValue = " ※ There is no read method. ";
            		}
				} catch (IllegalAccessException e) {
					throw new ApplicationException(" propertyName : ".concat(propertyName), e);
				} catch (InvocationTargetException e) {
					throw new ApplicationException(" propertyName : ".concat(propertyName), e);
				} catch (NoSuchMethodException e) {
					throw new ApplicationException(" propertyName : ".concat(propertyName), e);
				}
            	
            	if(isLogging && property.getWriteMethod() != null && property.getReadMethod() != null && propertyValue != null) {
            		if(logger.isDebugEnabled()) {
						logger.debug(CommonUtil.addString(
							new Object[]{
								 "\n  ↘ [ value property ] " , propertyType 
								,"\n *- property.getPropertyType : ", property.getPropertyType()
								,"\n *- propertyValue.getClass : ", propertyValue.getClass()
								,"\n *- property getDisplayName : " , property.getDisplayName() 
								,"\n *- getReadMethod getReturnType : " , property.getReadMethod().getReturnType() 
								,"\n *- getReadMethod getGenericReturnType : " , property.getReadMethod().getGenericReturnType() 
								,"\n *- getReadMethod getDeclaringClass : " , property.getReadMethod().getDeclaringClass() 
								,"\n *- getWriteMethod getParameterTypes : " , property.getWriteMethod().getParameterTypes() 
								,"\n *- getWriteMethod getGenericParameterTypes : " , property.getReadMethod().getGenericParameterTypes() 
								,"\n *- getWriteMethod getDeclaringClass : " , property.getReadMethod().getDeclaringClass() 
								,"\n *- propertyValue getComponentType : " , propertyValue.getClass().getComponentType() 
								,"\n *- propertyValue getSigners : " , propertyValue.getClass().getSigners() 
								,"\n *- propertyValue getCanonicalName : " , propertyValue.getClass().getCanonicalName() 
								,"\n *- propertyValue getEnclosingClass : " , propertyValue.getClass().getEnclosingClass() 
								,"\n *- propertyValue getInterfaces : " , propertyValue.getClass().getInterfaces() 
								,"\n *- propertyValue getSuperclass : " , propertyValue.getClass().getSuperclass() 
								,"\n *- propertyValue getTypeParameters : " , (propertyValue.getClass().getTypeParameters().length > 0 ? propertyValue.getClass().getTypeParameters()[0] : null) 
								,"\n *- getWriteMethod getGenericParameterType[0] : " , (reflectUtil.getGenericParameterType(property.getWriteMethod()).size() > 0 && reflectUtil.getGenericParameterType(property.getWriteMethod()).get(0).size() > 0 ? reflectUtil.getGenericParameterType(property.getWriteMethod()).get(0).get(0) : null )
							})
						);
            		}
            	}

            	message.append(whiteSpace);
                message.append(whiteSpace);
            	message.append("[");
            	message.append(i);
            	message.append("] ");
            	message.append(propertyType);
            	message.append(" ");
            	message.append(propertyName);
            	message.append(" : ");
            	message.append(propertyValue);
            	message.append(SystemUtil.LINE_SEPARATOR);
            	
            	if(extractType != null) {
                	if(extractType.isAssignableFrom(property.getPropertyType())) {
                		extractList.add(propertyValue);            		
                	}
            	}

            	
            	if(childExtract) {
	            	if(propertyValue != null && Collection.class.isAssignableFrom(property.getPropertyType()) && Collection.class.isAssignableFrom(propertyValue.getClass())) {
	    				if(logger.isDebugEnabled()) {
	    					//logger.debug(CommonUtil.addString("propertyValue is collection ParameterizedType!! "));
	    				}
						
						if(extractType != null) {
	                		extractList.addAll((Collection<? extends Object>) getDataTransferCollectionObjectContents(propertyValue, extractType, excludeMethods, isLogging, stacks, level ));
	                	}
	                	else {
	                		message.append(getDataTransferCollectionObjectContents(propertyValue, extractType, excludeMethods, isLogging, stacks, level ));
	                	}
	            	}
	            	else if(!typeUtil.isGeneralType(property.getPropertyType())) {
	            		if(logger.isDebugEnabled()) {
	    					//logger.debug(CommonUtil.addString("propertyValue is not GeneralType!! "));
	    				}
	            		
	                	if(extractType != null) {
	                		extractList.addAll((Collection<? extends Object>) getDataTransferObjectContents(property, extractType, propertyValue, excludeMethods, isLogging, stacks, level ));
	                	}
	                	else {
	                		message.append(getDataTransferObjectContents(property, extractType, propertyValue, excludeMethods, isLogging, stacks, level ));
	                	}
	            		
	            	}
            	}
            }
            
            message.append(SystemUtil.LINE_SEPARATOR);
        	message.append(whiteSpace);
        	if(level == 0) message.append(" ■■■■■■■■■■■■■■■■■■■ ");
        	else message.append(" ← ");
        	message.append("[Printout level : " + level + " End]");
        	if(level == 0) if(level == 0) message.append(" ■■■■■■■■■■■■■■■■■■■ ");
        	message.append(SystemUtil.LINE_SEPARATOR);
        	message.append(SystemUtil.LINE_SEPARATOR);
        	
            if( level == 0  && isLogging ) {
                if(logger.isDebugEnabled()) {
                	logger.debug(message.toString());
                }
            }
            
    	}
    	else {
    		message.append(" input signature is null");
    		
    		if( level == 0  && isLogging ) {
    			if(logger.isDebugEnabled()) {
    				logger.debug(message.toString());
    			}
    		}
    	}
    	
    	if(extractType != null) {
    		return extractList;
    	}
    	else {
    		return message.toString();
    	}
    }
    
    private Object getDataTransferObjectContents(PropertyDescriptor property, Class<?> extractType, Object propertyValue, String[] excludeMethods, boolean isLogging, StackTraceElement[] stacks, int level  ) {
    	
    	StringBuffer message = new StringBuffer();
    	
    	List<Object> extractList = new ArrayList<Object>();
    	
    	if( propertyValue != null && typeUtil.isSupportedReferenceType(propertyValue.getClass().getCanonicalName()) ) {
    		
        	if(extractType != null) {
        		extractList.addAll((Collection<? extends Object>) out(propertyValue, extractType, excludeMethods, isLogging, stacks, level + 1, true));
        	}
        	else {
        		message.append(SystemUtil.LINE_SEPARATOR);
        		message.append(out(propertyValue, extractType, excludeMethods, isLogging, stacks, level + 1, true));
        	}
    	}
    	
    	if(extractType != null) {
    		return extractList;
    	}
    	else {
    		return message.toString();
    	}
    }
    
    
    private Object getDataTransferCollectionObjectContents(Object propertyValue, Class<?> extractType, String[] excludeMethods, boolean isLogging, StackTraceElement[] stacks, int level ) {
    	
    	StringBuffer message = new StringBuffer();
    	
    	List<Object> extractList = new ArrayList<Object>();
    	
		for(Object collectionValues : (Collection<?>) propertyValue) {
			
			if( collectionValues instanceof ParameterizedType ) {
				if(logger.isDebugEnabled()) {
					//logger.debug(CommonUtil.addString("collectionValues is ParameterizedType!! < recall getDataTransferCollectionObjectContents [ ", collectionValues.getClass().getCanonicalName(), " ] >"));
				}
				
		    	if(extractType != null) {
		    		extractList.addAll((Collection<? extends Object>) getDataTransferCollectionObjectContents(collectionValues, extractType, excludeMethods, isLogging, stacks, level + 1));
		    	}
		    	else {
		    		message.append(getDataTransferCollectionObjectContents(collectionValues, extractType, excludeMethods, isLogging, stacks, level + 1));
		    	}
			}
			else if( /*typeUtil.isSupportedReferenceType(collectionValues.getClass().getCanonicalName()) &&*/
				!(collectionValues instanceof WildcardType) 
				&& !collectionValues.getClass().isPrimitive()
				&& !typeUtil.isPrimitiveWrapType(collectionValues.getClass())
			) {
				if(logger.isDebugEnabled()) {
					//logger.debug(CommonUtil.addString("collectionValues is self object < recall out [ ", collectionValues.getClass().getCanonicalName(), " ] >"));
				}
				
				// user self dto 이면 제귀 호출
		    	if(extractType != null) {
		    		extractList.addAll((Collection<? extends Object>) out(collectionValues, extractType, excludeMethods, isLogging, stacks, level + 1, true));
		    	}
		    	else {
		    		message.append(SystemUtil.LINE_SEPARATOR);
		    		message.append(out(collectionValues, extractType, excludeMethods, isLogging, stacks, level + 1, true));
		    	}
		    	

			}
		}
		
    	if(extractType != null) {
    		return extractList;
    	}
    	else {
    		return message.toString();
    	}
    }

    /**
     * map의 value을 bean에 넣어주는 메소드
     * @param readMap
     * @param writeBean
     */
	public Object mapToBean(Map<String, ? extends Object> readMap, Object writeBean) {
		if (readMap == null) {
			throw new ValidateException("Map데이터가 존재하지 않습니다.");
		}
		if (writeBean == null) {
			throw new ValidateException("정보를 저장할 Bean이 존재하지 않습니다.");
		}

		try {
			BeanUtils.populate(writeBean, readMap);
		} catch (IllegalAccessException e) {
			throw new ApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new ApplicationException(e);
		}
		
		return writeBean;
	}

	/**
	 * bean의 value을 map에 넣어주는 메소드
	 * @param readBean
	 * @param writeMap
	 */
	public void beanToMap(Object readBean, Map<String, Object> writeMap) {
		if (readBean == null) {
			throw new ValidateException("Bean데이터가 존재하지 않습니다.");
		}
		if (writeMap == null) {
			throw new ValidateException("정보를 저장할 Map이 존재하지 않습니다.");
		}
		
		try {
			Map<String, Object> map = PropertyUtils.describe(readBean);
			writeMap.putAll(map);
		} catch (IllegalAccessException e) {
			throw new ApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new ApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new ApplicationException(e);
		}

	}

    private StackTraceElement getCaller(StackTraceElement[] stacks){
    	
    	for(StackTraceElement caller : stacks){
    		if(!this.getClass().getCanonicalName().equals(caller.getClassName())) {
    			return caller;
    		}
    	}
    	return stacks[0];
    }
}
