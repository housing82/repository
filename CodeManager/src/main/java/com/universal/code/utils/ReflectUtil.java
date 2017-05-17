package com.universal.code.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.dto.ClassDTO;
import com.universal.code.exception.ApplicationException;

/**
 * <p>
 * Title: ReflectClassMethodUtil
 * </p>
 * <p>
 * Description: REFLECT METHOD CALL UTIL
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @since 2012
 * @author ksw
 * @version 1.0
 */

@Component
public class ReflectUtil implements IOperateCode {

	private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

	public Object invokeMethod(Object clazz, Object method) {
		return invokeMethod(clazz, method, null, null);
	}


	public Object invokeMethod(Object clazz, Object method, Class<?>[] signatureTypes, Object[] signatureValues){
		
		Class<?> typeClass = null;
		Method classMethod = null;
		Class<?>[] inputTypes = null;
		Object[] inputValues = null;
		
		//typeClass
		if(Class.class.isAssignableFrom(clazz.getClass())) {
			typeClass = (Class<?>) clazz;
		}
		else if(String.class.isAssignableFrom(clazz.getClass())){
			typeClass = findClass((String) clazz);
		}
		else {
			throw new ApplicationException("클래스 아규먼트가 잘못되었습니다.");
		}
		
		//inputTypes
		if(signatureTypes == null) {
			inputTypes = new Class[0];
		}
		else {
			inputTypes = signatureTypes;
		}
		
		//classMethod
		if(Method.class.isAssignableFrom(method.getClass())) {
			classMethod = (Method) method;
		}
		else if(String.class.isAssignableFrom(method.getClass())){
			classMethod = findMethod(typeClass, (String) method, inputTypes);
		}
		else {
			throw new ApplicationException("메소드 아규먼트가 잘못되었습니다.");
		}
		
		//inputValues
		if(signatureValues == null) {
			inputValues = new Object[0];
		}
		else {
			inputValues = signatureValues;
		}
		
		return invokeMethod(typeClass, classMethod, inputValues);
	}
	
	
	/**
	 * classNm 의 method 를 실행후 결과를 리턴하여줍니다.
	 * 
	 * @param target
	 * @param method
	 * @return
	 */
	private Object invokeMethod(Class<?> clazz, Method method) {
		return invokeMethod(clazz, method, new Object[0]);
	}

	/**
	 * classNm 의 method(argument)를 실행후 결과를 리턴하여줍니다.
	 * 
	 * @param classNm
	 * @param isMethod
	 * @param argument
	 * @return
	 */
	private Object invokeMethod(Class<?> clazz, Method method, Object[] arguments) {

		Object result = null;

		StringBuffer invokeMessage = null;

		if(logger.isDebugEnabled()) {
			invokeMessage = new StringBuffer();
			invokeMessage.append("\n □□□□□□□□□□□□□□□□□□□ [invoke method infomation] □□□□□□□□□□□□□□□□□□□");
			invokeMessage.append("\n class : ".concat(clazz.getCanonicalName()));
			invokeMessage.append("\n method : ".concat(method.getName()));
			for (int i = 0; i < method.getParameterTypes().length; i++) {
				invokeMessage.append("\n  parameterTypes[" + i+ "] : ".concat(method.getParameterTypes()[i].toString()));
			}
			invokeMessage.append("\n returnTypes : ".concat(method.getReturnType().toString()));
			invokeMessage.append("\n □□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□");
			logger.debug(invokeMessage.toString());
		}

		try {
			/** execute class method result object */
			result = method.invoke(clazz.newInstance(), arguments);
			
			if(logger.isDebugEnabled()) {
				invokeMessage = new StringBuffer();
				invokeMessage.append("\n □□□□□□□□□□□□□□□□□□□ [invoke method return value] □□□□□□□□□□□□□□□□□□□");
				invokeMessage.append("\n " + result);
				invokeMessage.append("\n □□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□");
				logger.debug(invokeMessage.toString());
			}

		} catch (IllegalArgumentException e) {
			throw new ApplicationException(e);
		} catch (IllegalAccessException e) {
			throw new ApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new ApplicationException(e);
		} catch (InstantiationException e) {
			throw new ApplicationException(e);
		}

		return result;
	}

	public static boolean existsClass(String... classType) {
		boolean out = false;
		try {
			String[] clazzTypes = classType;
			if( clazzTypes != null ) {
				for(String clazzType : clazzTypes) {
					Class.forName(clazzType);
				}
				out = true;
			}
		} catch (ClassNotFoundException e) {
			if(logger.isDebugEnabled()) {
				logger.debug("ClassNotFound : ".concat(e.getMessage()));
			}
		}
		
		return out;
	}
	
	public ClassDTO[] getMethosFromClassNames(String[] classNames) {

		if (classNames == null) {
			return new ClassDTO[0];
		}

		Class<?>[] classes = new Class[classNames.length];

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		for (int i = 0; i < classNames.length; i++) {
			try {
				classes[i] = classLoader.loadClass(classNames[i]);
			} catch (ClassNotFoundException e) {
				classes[i] = ClassNotFoundException.class;
				e.printStackTrace();
			}
		}

		return getMethosFromClasses(classes);
	}

	public ClassDTO[] getMethosFromClasses(Class<?>[] classes) {

		if (classes == null) {
			return new ClassDTO[0];	
		}	

		ClassDTO[] ClassDTOArr = new ClassDTO[classes.length];
		for (int i = 0; i < classes.length; i++) {
			ClassDTOArr[i] = new ClassDTO();
			ClassDTOArr[i].setClassName(classes[i].getName());
			ClassDTOArr[i].setMethods(classes[i].getDeclaredMethods());
		}

		return ClassDTOArr;
	}

	/**
	 * class 의 존재여부를 채크하고 존재하면 해당 클래스를 리턴하여 줍니다.
	 * 
	 * @param className : ex) qas.rqms.component.common.util.global.StringUtil
	 * @return
	 */
	public Class<?> findClass(String classType) {

		Class<?> clazz = null;
		String clazzType = classType;
		if (StringUtil.isEmpty(clazzType))
			return clazz;

		URL classUrl = null;
		String classPath = clazzType.replace(STR_DOT, STR_SLASH).trim();
		classPath = new StringBuilder().append(STR_SLASH).append(classPath).append(STR_DOT).append(STR_CLASS).toString();
		classUrl = this.getClass().getResource(classPath);

		if (logger.isDebugEnabled()) {
			logger.debug("classUrl: {} ", classUrl);
		}

		if (classUrl != null) {
			try {
				clazz = Class.forName(clazzType);
			} catch (ClassNotFoundException e) {
				throw new ApplicationException(e);
			}
			if (logger.isDebugEnabled()) {
				logger.debug(CommonUtil.addString(clazzType, " : [", classUrl.getFile(), "]" ));
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(" - Class Not Found ");
			}
		}

		return clazz;
	}

	class DynaClassLoader extends ClassLoader {

		public Class<?> defineClass(String name, byte[] b) {
			return defineClass(name, b, 0, b.length);
		}
	}

	public Class<?> findDynaClass(String name, byte[] b) {
		return new DynaClassLoader().defineClass(name, b);
	}

	public Method findMethod(String classType, String methodName, Class<?>[] signature) {

		Method out = null;
		if (!StringUtil.isNotEmptyStringArray(new String[] { classType, methodName })) {
			return out;
		}

		out = findMethod(findClass(classType), methodName, signature);

		return out;
	}

	public Method findMethod(Class<?> classes, String methodName, Class<?>[] signature) {

		Class<?> clazz = classes;
		Method out = null;

		if (!StringUtil.isNotEmptyStringArray(new String[] { methodName })) {
			return out;
		}

		if (clazz != null) {
			try {
				out = clazz.getMethod(methodName, signature);
			} catch (SecurityException e) {
				throw new ApplicationException(e);
			} catch (NoSuchMethodException e) {
				throw new ApplicationException(e);
			}
		}

		return out;
	}

	
	public Method searchMethod(Class<?> clazz, String name) {
		return searchMethod(clazz, name, new Class[0]);
	}

	public Method searchMethod(Class<?> clazz, String name, Class<?>[] paramTypes) {

		if (clazz == null) {
			throw new ApplicationException("Class must not be null");
		}
		if (name == null) {
			throw new ApplicationException("Either name of the field must be specified");
		}

		for (Class<?> searchType = clazz; searchType != null; searchType = searchType.getSuperclass()) {

			Method methods[] = searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods();
			Method amethod[];
			int j = (amethod = methods).length;

			for (int i = 0; i < j; i++) {
				Method method = amethod[i];
				if (name.equals(method.getName()) && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes())))
					return method;
			}
		}

		return null;
	}

	public Method[] findMethods(Class<?> classes) {

		Method[] out = null;
		Class<?> clazz = classes;

		if (clazz != null) {
			out = clazz.getMethods();
		}

		return out;
	}

	public Method[] findMethods(String classType) {

		Method[] out = null;
		String clazzName = classType;

		if (StringUtil.isNotEmpty(clazzName)) {
			Class<?> clazz = findClass(classType);

			if (clazz != null) {
				out = findMethods(clazz);
			}
		}

		return out;
	}

	public Method findDeclaredMethod(String classType, String methodName,
			Class<?>[] signature) {

		Method out = null;
		if (!StringUtil.isNotEmptyStringArray(new String[] { classType, methodName })) {
			return out;
		}

		out = findDeclaredMethod(findClass(classType), methodName, signature);

		return out;
	}

	public Method findDeclaredMethod(Class<?> classes, String methodName,
			Class<?>[] signature) {

		Class<?> clazz = classes;
		Method out = null;

		if (!StringUtil.isNotEmptyStringArray(new String[] { methodName })) {
			return out;
		}

		if (clazz != null) {
			try {
				out = clazz.getDeclaredMethod(methodName, signature);
			} catch (SecurityException e) {
				throw new ApplicationException(e);
			} catch (NoSuchMethodException e) {
				throw new ApplicationException(e);
			}
		}

		return out;
	}

	public Method[] findDeclaredMethods(Class<?> classes) {

		Method[] out = null;
		Class<?> clazz = classes;

		if (clazz != null) {
			out = clazz.getDeclaredMethods();
		}

		return out;
	}

	public Method[] findDeclaredMethods(String classType) {

		Method[] out = null;
		String clazzName = classType;

		if (StringUtil.isNotEmpty(clazzName)) {
			Class<?> clazz = findClass(classType);

			if (clazz != null) {
				out = findDeclaredMethods(clazz);
			}

		}

		return out;
	}

	public Field[] getDeclaredFields(Class<?> clazz) {

		Field[] out = null;

		if (clazz != null) {
			out = clazz.getDeclaredFields();
		}

		return out;
	}

	public Field[] getDeclaredFields(String classType) {

		Field[] out = null;
		Class<?> clazz = findClass(classType);
		if (clazz != null) {
			out = getDeclaredFields(clazz);
		}

		return out;
	}

	public Field[] getFields(Class<?> clazz) {

		Field[] out = null;

		if (clazz != null) {
			out = clazz.getFields();
		}

		return out;
	}

	public Field[] getFields(String classType) {

		Field[] out = null;
		Class<?> clazz = findClass(classType);
		if (clazz != null) {
			out = getFields(clazz);
		}

		return out;
	}

	public Annotation[] getDeclaredAnnotations(Class<?> clazz) {

		Annotation[] out = null;

		if (clazz != null) {
			out = clazz.getDeclaredAnnotations();
		}

		return out;
	}

	public Annotation[] getDeclaredAnnotations(String classType) {

		Annotation[] out = null;
		Class<?> clazz = findClass(classType);
		if (clazz != null) {
			out = getDeclaredAnnotations(clazz);
		}

		return out;
	}

	public Annotation[] getAnnotations(Class<?> clazz) {

		Annotation[] out = null;

		if (clazz != null) {
			out = clazz.getAnnotations();
		}

		return out;
	}

	public Annotation[] getAnnotations(String classType) {

		Annotation[] out = null;
		Class<?> clazz = findClass(classType);
		if (clazz != null) {
			out = getAnnotations(clazz);
		}

		return out;
	}

	/**
	 * methodCall
	 * 
	 * @param classNm
	 * @param methodNm
	 * @param methodParameter
	 * @return
	 */
	public Object methodCall(String classNm, String methodNm,
			Map<Integer, Object[]> methodParameter) {

		Object result = null;
		boolean validate = true;
		String message = "methodCall Finished Result";

		if (StringUtil.isNotEmptyStringArray(new String[] { classNm, methodNm })) {

			if (methodParameter != null) {

				List<String> bindParamTypes = new ArrayList<String>();
				List<Object> bindParamValues = new ArrayList<Object>();
				Object[] paramObj = null;
				boolean parameterChk = true;

				for (int i = 0; i < methodParameter.size(); i++) {

					paramObj = methodParameter.get(i);
					if (paramObj.length == 2 && paramObj[0] != null
							&& paramObj[0] instanceof String) {
						bindParamTypes.add(paramObj[0].toString());
						bindParamValues.add(paramObj[1]);
					} else {
						parameterChk = false;
						break;
					}
				}

				if (parameterChk) {
					result = methodCall(classNm, methodNm, bindParamTypes,
							bindParamValues);
				} else {
					message = " argument miss match ";
					validate = false;
				}

			} else {
				message = " methodParameter is null ";
				validate = false;
			}
		} else {
			message = " className or methodName is null ";
			validate = false;
		}

		methodParameter.clear();

		if (validate) {
			if(logger.isDebugEnabled()) {
				logger.debug("\n *- " + message + " : " + result + "\n");
			}
		} else {
			throw new ApplicationException(" *- methodCall Parameter ERROR [" + message + "]");
		}

		return result;
	}

	/**
	 * call method
	 * 
	 * @param classNm
	 * @param methodNm
	 * @param paramTypes
	 * @param paramValues
	 * @return
	 */
	private Object methodCall(String classNm, String methodNm, List<String> bindParamTypes, List<?> bindParamValues) {

		Object result = null;
		String classNames = classNm;
		Class<?> clazz = findClass(classNames);

		if (clazz == null) {
			return result;
		}
		Method[] methods = findMethods(clazz);

		if (methods == null || methods.length == 0 || (bindParamTypes.size() != bindParamValues.size())) {
			if(logger.isDebugEnabled()) {
				logger.debug(" - methods is null or parameter problem... ");
			}
			return result;
		}

		String methodName = "";

		Class<?>[] methodParamTypes = null;
		Class<?>[] paramTypes = null;
		Class<?> methodParamType = null;
		Method isMethod = null;
		Object[] argument = null;

		boolean methodParamChecker = true;

		for (Method method : methods) {
			methodName = method.getName();
			methodParamTypes = method.getParameterTypes();

			/** search call method */
			if (methodName.equals(methodNm)) {

				// logger.debug("params.size() :"+ bindParamTypes.size() +
				// " = " + methodParamType.length +
				// " : methodParamType.length");
				/** parameterType size check */
				if (bindParamTypes.size() != methodParamTypes.length) {
					continue;
				}

				methodParamChecker = true;
				paramTypes = new Class[bindParamTypes.size()];
				for (int i = 0; i < methodParamTypes.length; ++i) {
					methodParamType = methodParamTypes[i];
					/** parameter type check */
					/**
					 * 페키지 경로가 다른 같은 오브젝트 명을 사용하는 파라메터타입이 있을수있음으로 CanonicalName
					 * 으로 비교한다.
					 */
					if (!bindParamTypes.get(i).equals(
							methodParamType.getCanonicalName())) {
						methodParamChecker = false;
					} else {
						paramTypes[i] = methodParamType;
					}
				}

				if (methodParamChecker) {
					isMethod = method;
					argument = new Object[bindParamValues.size()];
					
					int i = 0;
					for (Object arguments : bindParamValues) {
						argument[i] = arguments;
						if(logger.isDebugEnabled()) {
							logger.debug(" - Method Param[{}] Type: {}, Data Type: {}, Value: {}", i, paramTypes[i].getCanonicalName(), argument[i].getClass().getCanonicalName(), argument[i]);
						}
						i++;
					}
					
					if(logger.isDebugEnabled()) {
						logger.debug(" - find method : " + isMethod);	
					}
					break;
				} else {
					paramTypes = null;
				}
			}
		}

		if (isMethod != null) {
			/** execute class method result object */
			result = invokeMethod(clazz, isMethod, argument);
		} else {
			if(logger.isDebugEnabled()) {
				logger.debug(" - Not Found Method ");
			}
		}

		return result;
	}

	public List<Type> getParameterizedTypeList(Type parameterType) {

		List<Type> out = new ArrayList<Type>();
		if (parameterType == null) {
			if(logger.isDebugEnabled()) {
				logger.debug(" parameterType is null ");
			}
			return out;
		}

		Type[] parameterArgTypes = null;
		if (parameterType instanceof ParameterizedType) {

			parameterArgTypes = ((ParameterizedType) parameterType).getActualTypeArguments();
			for (Type parameterArgType : parameterArgTypes) {
				out.add(parameterArgType);
			}
		} else {
			if(logger.isDebugEnabled()) {
				logger.debug(" parameterType [" + parameterType+ "] is not ParameterizedType ");
			}
		}

		return out;
	}

	public Map<Integer, List<Type>> getGenericParameterType(Method method) {

		Map<Integer, List<Type>> out = new HashMap<Integer, List<Type>>();
		if (method == null) {
			if(logger.isDebugEnabled()) {
				logger.debug(" method is null ");
			}
			return out;
		}

		List<Type> types = null;
		Type[] parameterTypes = method.getParameterTypes();
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		Type genericParameterType = null;

		for (int i = 0; i < parameterTypes.length; i++) {
			genericParameterType = genericParameterTypes[i];
			types = new ArrayList<Type>();
			types.addAll(getParameterizedTypeList(genericParameterType));
			out.put(i, types);
		}

		return out;
	}

	public Map<Integer,Map<Type, List<Type>>> getGenericParameterTypes(Method method) {
		
		Map<Integer,Map<Type, List<Type>>> out = new HashMap<Integer,Map<Type, List<Type>>>();
		if(method == null) {
			if(logger.isDebugEnabled()) logger.debug(" method is null ");
			return out;
		}
		
		
		Type[] parameterTypes = method.getParameterTypes();
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		Map<Type, List<Type>> signature = null;
		
		for(int i = 0; i < parameterTypes.length; i++) {
			signature = new HashMap<Type, List<Type>>();
			signature.put(parameterTypes[i], getParameterizedTypeList(genericParameterTypes[i]));			
			out.put(i, signature);
		}
		
		return out;
	}
	
	/**
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with
	 * the supplied {@code name}. Searches all superclasses up to {@link Object}
	 * .
	 * 
	 * @param clazz
	 *            the class to introspect
	 * @param name
	 *            the name of the field
	 * @return the corresponding Field object, or {@code null} if not found
	 */
	public Field findField(Class<?> clazz, String name) {
		return findField(clazz, name, null);
	}

	/**
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with
	 * the supplied {@code name} and/or {@link Class type}. Searches all
	 * superclasses up to {@link Object}.
	 * 
	 * @param clazz
	 *            the class to introspect
	 * @param name
	 *            the name of the field (may be {@code null} if type is
	 *            specified)
	 * @param type
	 *            the type of the field (may be {@code null} if name is
	 *            specified)
	 * @return the corresponding Field object, or {@code null} if not found
	 */
	public Field findField(Class<?> clazz, String name, Class<?> type) {

		if (clazz == null)
			throw new ApplicationException("Class must not be null");
		if (name == null)
			throw new ApplicationException("Either name of the field must be specified");

		Class<?> searchType = clazz;
		while (!Object.class.equals(searchType) && searchType != null) {
			Field[] fields = searchType.getDeclaredFields();
			for (Field field : fields) {
				if ((name == null || name.equals(field.getName()))&& (type == null || type.equals(field.getType()))) {
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	public void setFieldValue(Object bean, Field field, Object value) {
		try {
			if (!isPassField(field)) {
				field.set(bean, value);
			}
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException("Unexpected reflection exception - "+ ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	public Object getFieldValue(Field field, Object target) {
		Object out = null;
		try {
			if (!isPassField(field)) {
				out = field.get(target);
			}
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException("Unexpected reflection exception - "+ ex.getClass().getName() + ": " + ex.getMessage());
		}
		return out;
	}

	/**
	 * use populate pass field
	 * 
	 * @param field
	 * @return
	 */
	public boolean isPassField(Field field) {
		boolean out = false;

		if (!Modifier.isPublic(field.getModifiers())
				|| Modifier.isTransient(field.getModifiers())
				|| Modifier.isFinal(field.getModifiers())
				|| Modifier.isStatic(field.getModifiers())) {
			if (logger.isDebugEnabled()) {
				logger.debug("[ field name : " + field.getName()+ " ] modifiers \"" + field.getModifiers()+ "\" is pass");
			}
			out = true;
		}

		return out;
	}

}
