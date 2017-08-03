package com.universal.code.parameter.validation;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.annotation.Fields;
import com.universal.code.annotation.Model;
import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ValidateException;
import com.universal.code.messages.MessageHelper;
import com.universal.code.parameter.validation.dto.ParamValidateDTO;
import com.universal.code.utils.CommonUtil;
import com.universal.code.utils.PropertyUtil;
import com.universal.code.utils.RegexUtil;
import com.universal.code.utils.StringUtil;
import com.universal.code.utils.SystemUtil;
import com.universal.code.utils.TypeUtil;

@Component
public class ParamValidate {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RegexUtil regexUtil;
	
	@Autowired
	private TypeUtil typeUtil;
	
	@Autowired
	private PropertyUtil propertyUtil;
	
	@Autowired
	private StringUtil stringUtil;
	
	private boolean isValidateLog = false;
	
	private List<ParamValidateDTO> params;
	
	private final String VP_REQUIRED = "required";
	
	private final String VP_REGEX = "regex:";
	
	private final String VP_TYPE = "type:";
	
	private final String VP_SIZE = "size:";
	
	private final String VP_LENGTH = "length:";
	
	private String encoding = IOperateCode.DEFAULT_ENCODING;
	
	public ParamValidate(){
		this.reset();
		//ParamValidate를 @Autowired하지 않고 new ParamValidate() 하였을경우
		if(regexUtil == null) this.regexUtil = new RegexUtil();
		if(typeUtil == null) this.typeUtil = new TypeUtil();
		if(propertyUtil == null) this.propertyUtil = new PropertyUtil();
		if(stringUtil == null) this.stringUtil = new StringUtil();
	}
	
	private void reset(){
		params = new ArrayList<ParamValidateDTO>();
	}

	public List<ParamValidateDTO> getParams() {
		return params;
	}

	public void setParams(List<ParamValidateDTO> params) {
		this.params = params;
	}

	public void addParam(ParamValidateDTO param) {
		if(this.params == null) this.params = new ArrayList<ParamValidateDTO>();
		this.params.add(param);
	}
	
	public ParamValidateDTO validation(){
		return validation(null);
	}
	
	public ParamValidateDTO validation(String encoding){
		
		if(this.params == null) return new ParamValidateDTO();
		
		if(encoding != null) {
			this.encoding = encoding;
		}
		
		ParamValidateDTO inValidate = execution();
		
		if(inValidate == null) inValidate = new ParamValidateDTO();
		
		return inValidate;
	}
	
	public ParamValidateDTO execute(){
		return execute(null, false);
	}
	
	public ParamValidateDTO execute(boolean returnInvalid){
		return execute(null, returnInvalid);
	}
	
	public ParamValidateDTO execute(String encoding, boolean returnInvalid){

		if(this.params == null) return null;
		
		if(encoding != null) {
			this.encoding = encoding;
		}
		
		ParamValidateDTO inValidate = execution();
		
		if(!returnInvalid && inValidate != null) {
			throw new ValidateException(inValidate.getMessage(), IOperateCode.SERVICE_RESULT_CODE_INVALID);
		}
		
		return inValidate;
	}
	
	private ParamValidateDTO execution(){
		
		ParamValidateDTO inValidate = null;
		try {
			int validateCnt = 1;
			for(ParamValidateDTO validate : this.params) {
				
		    	if(logger.isDebugEnabled() && isValidateLog) {
		    		logger.debug(CommonUtil.addString("#START Validate No.", validateCnt));
		    	}

		    	validate = this.run(validate);
		    	
	    		if(!validate.isValidation()) {
	    			inValidate = validate; 
	    			break;
	    		}
	    		else {
	    			if(logger.isDebugEnabled() && isValidateLog) {
	    	    		logger.debug(CommonUtil.addString("#PASS Validate No.", validateCnt, " END"));
	    	    	}	
	    		}
	    		validateCnt++;
			}
			
			if(this.params != null) {
				this.params.clear();
			}
		}
		catch(Exception e){
			throw new ValidateException(MessageHelper.message("EXCEPTION_VALIDATION"), e);
		}
		
		return inValidate;
	}
	
	/**
	 * 바인드된 ParamValidateDTO설정값 으로 벨리데이션 실행 
	 * @Name : run 
	 * @Description : 
	 * @LastUpdated : 2015. 10. 17. 오후 11:14:41
	 * @param paramValidate
	 * @return
	 */
    public ParamValidateDTO run(ParamValidateDTO paramValidate){
    	
    	if( logger.isDebugEnabled() && isValidateLog ) {
	    	logger.debug(CommonUtil.addString(SystemUtil.LINE_SEPARATOR, " * 유효성검증 필드 정보", SystemUtil.LINE_SEPARATOR, new PropertyUtil().out(paramValidate)));
	    }
    	
    	/**
    	 * paramValidate.getValidateType()으로 벨리데이션 유형을 판단한다. 
    	 */
    	
    	if(paramValidate.getValidateType() == IOperateCode.VALIDATE_DTO_FULL_FIELD_ANNO) {
    		//DTO 오브젝트에 있는 필드의 @Field 어노테이션을 기준으로 모든 필드 벨리데이션 설정
    		//@Model 어노테이션타입에만 해당
    		//@Fields 어노테이션필드에만 해당
    		return validateModel(paramValidate);
    	}
    	else if(paramValidate.getValidateType() == IOperateCode.VALIDATE_DTO_SINGLE_FIELD_ANNO) {
    		//DTO 오브젝트에 있는 fieldName의 @Field 어노테이션을 기준으로 단일 필드 벨리데이션 설정
    		//@Model 어노테이션타입에만 해당
    		//@Fields 어노테이션필드에만 해당
    		return validateModel(paramValidate);    		
    	}
    	else if(paramValidate.getValidateType() == IOperateCode.VALIDATE_SINGLE_VALUE_PATTERN) {
    		//fieldValue를 petterns 옵션으로 벨리데이션 설정
    		return validate(paramValidate);
    	}
		else if(paramValidate.getValidateType() == IOperateCode.VALIDATE_DTO_SINGLE_FIELD_PATTERN) {
			//DTO 오브젝트의 fieldName의 fieldValue값을 petterns 옵션으로 벨리데이션 설정 (추가작업필요함)
			return validate(paramValidate);
		}
		else {
			throw new ValidateException(MessageHelper.message("INVALID_PARAMS", "유효성검사유형이"));
		}
    }
    
    private boolean validateField(ParamValidateDTO paramValidate, Object model, Field field){
    	
    	Fields fieldAnno = field.getAnnotation(Fields.class);
		//@Fields 어노테이션이있는 필드만 벨리데이션한다. 없는 필드는 패스 한다. 
		if(fieldAnno != null && model != null && field != null) {
			// field.getType()
			try {
				boolean required = fieldAnno.required(); 
				int maxLength = fieldAnno.attributeLength();
				logger.debug(" #### propertyUtil : " + propertyUtil);
				Object value = propertyUtil.getProperty(model, field.getName());
				
				//필수 값 존재 여부채크
				if( required ) {
					if(value == null || (field.getType().isAssignableFrom(String.class) && StringUtil.isEmpty((String)value))) {
						paramValidate.setMessage(MessageHelper.message("ISNOT_EXIST_PARAMS", fieldAnno.description()));
						paramValidate.setValidation(false);
						return false;
					}
				}
				
				//필드 타입이 문자열일때 문자열의 byte(UTF-8기준 한글3바이트,나머지 한글2바이트) 최대길이를 넘어서는지 채크 
				if( field.getType().isAssignableFrom(String.class) && maxLength > 0 && value != null) {
					//((String)field)
					int byteLength = stringUtil.getBytesLength((String)value, this.encoding);
					if(byteLength > maxLength) {
						paramValidate.setMessage(MessageHelper.message("LENGTH_CAN_NOT_GREATHER_THAN", fieldAnno.description(), Integer.toString(maxLength)));
						paramValidate.setValidation(false);
						return false;	
					}
				}
			} 
			catch (Exception e){
				throw new ValidateException(MessageHelper.message("INVALID_PARAMS", "유효성검사대상 @필드{"+field.getName()+"}가"), e);
			}
		}
		return true;
    }
    
    private ParamValidateDTO validateModel(ParamValidateDTO paramValidate){
    	
    	Object model = paramValidate.getModel();

		
    	if(model != null) {

        	Class<?> modelClass = null;
    		Model modelAnno = null;
    		
    		if(Collection.class.isAssignableFrom(model.getClass()) && paramValidate.getValidateType() == IOperateCode.VALIDATE_DTO_FULL_FIELD_ANNO){
    			//DeclaredFields field validate
				for(Object items : (List<?>) model){
					
					modelClass = items.getClass();
		    		modelAnno = modelClass.getAnnotation(Model.class);
		    		
		    		//@Model 어노테이션이있는 DTO만 벨리데이션한다.
	        		if(modelAnno != null) {
	        			
	        			for(Field field : modelClass.getDeclaredFields()) {
	    					if(logger.isDebugEnabled()) {
	    						logger.debug("[VALIDATE] DeclaredFields Name : " + field.getName());
	    					}
	    					if(!validateField(paramValidate, items, field)) {
	    						return paramValidate;
	    					}
	        			}	
	        		}
	        		else {
	        			throw new ValidateException(MessageHelper.message("INVALID_PARAMS", "유효성검사대상 @클래스가", "@Model어노테이션 클래스만 가능합니다."));
	        		}
				}
    		}
    		else {
    			modelClass = model.getClass();
        		modelAnno = modelClass.getAnnotation(Model.class);
        		
        		//@Model 어노테이션이있는 DTO만 벨리데이션한다.
        		if(modelAnno != null) {
        			
        			if(paramValidate.getValidateType() == IOperateCode.VALIDATE_DTO_FULL_FIELD_ANNO) {
        				//DeclaredFields field validate
        				for(Field field : modelClass.getDeclaredFields()) {
        					if(logger.isDebugEnabled()) {
        						logger.debug("[VALIDATE] DeclaredFields Name : " + field.getName());
        					}
        					if(!validateField(paramValidate, model, field)){
        						return paramValidate;
        					}
            			}
        			}
        			else if(paramValidate.getValidateType() == IOperateCode.VALIDATE_DTO_SINGLE_FIELD_ANNO) {
        				//Single field validate
        				
        				//validate field 
        				String fieldName = paramValidate.getFieldName();
        				boolean isValidatedField = false;
        				if(logger.isDebugEnabled()) {
        					logger.debug("[VALIDATE] SuperClass Field Name : " + fieldName);
        				}
        				
        				if(StringUtil.isNotEmpty(fieldName)) {
    					    Class<?> modeClass = model.getClass();
    					    
    					    while (modeClass != null) {
    					        //fieldList에 벨리데이션 대상 필드가있는지 loop 하여 있으면 벨리데이션하고 없으면 superClass 의 DeclaredFields 에서 다시 찾는다. 
    					        //찾는 필드가 없으면 나올때까지 superClass 를 계속 탐색한다. (그래도 없으면 필드가 없는것임)
    					        isValidatedField = false;
    					        for(Field field : modeClass.getDeclaredFields()){
    					        	if(field.getName().equals(fieldName)) {
    					        		
    					        		if(logger.isDebugEnabled()) {
    					        			logger.debug("[VALIDATE] Find Field Name : " + fieldName);
    					        		}
    					        		if(!validateField(paramValidate, model, field)){
    					        			if(logger.isDebugEnabled()) {
    					        				logger.debug("[VALIDATE] Field validation Fail!! : " + fieldName);
    					        			}
    		        						return paramValidate;
    		        					}
    					        		if(logger.isDebugEnabled()) {
    					        			logger.debug("[VALIDATE] Field validation OK!! : " + fieldName);
    					        		}
    					        		
    					        		isValidatedField = true;
    					        		break;
    					        	}
    					        }
    					        
    					        if(isValidatedField) {
    					        	break;
    					        }
    					        modeClass = modeClass.getSuperclass();
    					    }
    					    
    					    if(!isValidatedField) {
    	    					//해당 필드가 없음
    	    					throw new ValidateException(MessageHelper.message("ISNOT_EXIST_PARAMS", "유효성검사대상 @필드{"+fieldName+"}가"));
    	    				}
        				}
        				else {
        					throw new ValidateException(MessageHelper.message("INVALID_PARAMS", "유효성검사대상 @필드명이"));
        				}
        				/*
        				if(StringUtil.isNotEmpty(fieldName)) {
            				try {
            					logger.debug("[VALIDATE] SuperClass Field Name : " + fieldName);
        						Field field = modelClass.getField(fieldName);
        						
        						if(!validateField(paramValidate, model, field)){
            						return paramValidate;
            					}
        					} catch (NoSuchFieldException | SecurityException e) {
        						throw new ValidateException(MessageHelper.message("ISNOT_EXIST_PARAMS", "유효성검사대상 @필드{"+fieldName+"}가"), e);
        					}
            			}
        				else {
        					throw new ValidateException(MessageHelper.message("INVALID_PARAMS", "유효성검사대상 @필드명이"));
        				}
        				*/
        			}
        		}
        		else {
        			throw new ValidateException(MessageHelper.message("INVALID_PARAMS", "유효성검사대상 @클래스가", "@Model어노테이션 클래스만 가능합니다."));
        		}
    		}
    	}
    	else{
    		throw new ValidateException(MessageHelper.message("ISNOT_EXIST_PARAMS", "유효성검사대상 DTO가"));
    	}
    	
    	return paramValidate;
    }
    
    
    private ParamValidateDTO validate(ParamValidateDTO paramValidate){
    	
    	Object values = paramValidate.getFieldValue();
    	
    	if( paramValidate.isRequired() && ( values == null || values.toString().isEmpty()) ) {
			//required
			if(logger.isDebugEnabled()) {
    			logger.debug(" - Required Validate");
    		}
			paramValidate.setValidation(false);
			return paramValidate;
    	}
    	
    	if(values != null) {

    		Class<?> valueClass = values.getClass();
        	String vPattern = null; 
        	long sizeLen = 0;
        	
    		//regex pattern test
    		for(String pattern : paramValidate.getPatterns()) {
    			
    			if( pattern.equalsIgnoreCase(VP_REQUIRED) ) {
    				continue;
    			}
    			else if( pattern.toLowerCase().startsWith(VP_REGEX) ) {
    				if(logger.isDebugEnabled()) {
    	    			logger.debug(" - Regex Validate");
    	    		}
    				//regex
    				vPattern = pattern.substring(pattern.toLowerCase().indexOf(VP_REGEX) + VP_REGEX.length()).trim();
					
					if(logger.isDebugEnabled()) {
		    			logger.debug(CommonUtil.addString( SystemUtil.LINE_SEPARATOR, " regexp : ", vPattern ,SystemUtil.LINE_SEPARATOR, " values : ", values.toString()));
		    		}

					if(!regexUtil.testPattern(values.toString(), vPattern)) {
						paramValidate.setValidation(false);
						return paramValidate;
					}
    			}
    			else if( pattern.toLowerCase().startsWith(VP_TYPE) ) {
    				if(logger.isDebugEnabled()) {
    	    			logger.debug(" - Type Validate");
    	    		}
    				//type
    				vPattern = pattern.substring(pattern.toLowerCase().indexOf(VP_TYPE) + VP_TYPE.length()).trim();
    				
    				if(logger.isDebugEnabled()) {
    	    			logger.debug(CommonUtil.addString( SystemUtil.LINE_SEPARATOR, " value type : ", valueClass.getCanonicalName() ,SystemUtil.LINE_SEPARATOR, " validate type : ", vPattern));
    	    		}
    				
    				if(!valueClass.getCanonicalName().equalsIgnoreCase(vPattern) && !valueClass.getSimpleName().equalsIgnoreCase(vPattern)) {
    					paramValidate.setValidation(false);
    					return paramValidate;
    				}
    			}
    			else if( pattern.toLowerCase().startsWith(VP_SIZE) ) {
    				if(logger.isDebugEnabled()) {
    	    			logger.debug(" - Size Validate");
    	    		}
    				//size
					sizeLen = Long.parseLong( pattern.substring(pattern.toLowerCase().indexOf(VP_SIZE) + VP_SIZE.length()).trim() );
					
    				if( typeUtil.getCollectionType(valueClass) != null ) {
    					//Collection
    					if(((Collection<?>) values).size() != sizeLen) {
    						paramValidate.setValidation(false);
    						return paramValidate;
    					}
    				}
    				else if( typeUtil.getMapType(valueClass) != null ) {
    					//Map
    					if(((Map<?,?>) values).size() != sizeLen) {
    						paramValidate.setValidation(false);
    						return paramValidate;
    					}
    				}
    				else {
    					throw new ValidateException(MessageHelper.message("INVALIDATE_SIZE_TYPE"));
    				}
    			}
    			else if( pattern.toLowerCase().startsWith(VP_LENGTH) ) {
    				if(logger.isDebugEnabled()) {
    	    			logger.debug(" - Length Validate");
    	    		}
    				//length
    				sizeLen = Long.parseLong( pattern.substring(pattern.toLowerCase().indexOf(VP_LENGTH) + VP_LENGTH.length()).trim() );
    				
    				if( valueClass.isAssignableFrom(Array.class) ) {
    					//Array
    					if(Array.getLength(values) != sizeLen) {
    						paramValidate.setValidation(false);
    						return paramValidate;
    					}
    				}
    				else {
    					//toString length
    					if(values.toString().length() != sizeLen) {
    						paramValidate.setValidation(false);
    						return paramValidate;
    					}
    				}
    			}
    			else {
    				//잘못된 유효성 검사 패턴
    				throw new ValidateException(MessageHelper.message("INVALIDATE_VALIDATE_PATTERN"));
    			}
    		}
    	}
    	
		return paramValidate;
    }
}
