package com.universal.code.parameter.validation.dto;

import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.universal.code.annotation.Fields;
import com.universal.code.annotation.Model;
import com.universal.code.constants.IOperateCode;

@Component
@XStreamAlias("VerifiedInformation")
@Model(description = "유효성검증정보")
public class ParamValidateDTO {

	@Fields(required = false, description = "DTO")
	private Object model;
	
	@Fields(required = false, description = "필드명")
	private String fieldName;

	@Fields(required = false, description = "필드값")
	private Object fieldValue;
	
	@Fields(required = false, description = "메세지")
	private String message;
	
	@Fields(required = false, description = "정규식패턴")
	private String[] patterns;
	
	@Fields(required = false, description = "필수여부")
	private boolean required;

	@Fields(required = false, description = "유효성검사")
	private boolean validation;	
	
	@Fields(required = false, description = "유효성검사유형")
	private int validateType;	

	
	public ParamValidateDTO() {
		this.reset();
	}

	/**
	 * DTO 오브젝트에 있는 필드의 @Field 어노테이션을 기준으로 모든 필드 벨리데이션 설정
	 * @param model
	 */
	public ParamValidateDTO(Object model) {
		this.validateType = IOperateCode.VALIDATE_DTO_FULL_FIELD_ANNO;
		initialize(model, null, null, null, null);
	}
	
	/**
	 * DTO 오브젝트에 있는 fieldName의 @Field 어노테이션을 기준으로 단일 필드 벨리데이션 설정
	 * @param model
	 */
	public ParamValidateDTO(Object model, String fieldName) {
		this.validateType = IOperateCode.VALIDATE_DTO_SINGLE_FIELD_ANNO;
		initialize(model, fieldName, null, null, null);
	}
	
	/**
	 * fieldValue를 petterns 옵션으로 벨리데이션 설정
	 * @param fieldValue
	 * @param patterns
	 * @param message
	 */
	public ParamValidateDTO(Object fieldValue, String[] patterns, String message) {
		this.validateType = IOperateCode.VALIDATE_SINGLE_VALUE_PATTERN;
		initialize(null, null, fieldValue, patterns, message);
	}

	/**
	 * DTO 오브젝트의 fieldName의 fieldValue값을 petterns 옵션으로 벨리데이션 설정
	 * @param model
	 * @param fieldName
	 * @param fieldValue
	 * @param patterns
	 * @param message
	 */
	public ParamValidateDTO(Object model, String fieldName, Object fieldValue, String[] patterns, String message) {
		this.validateType = IOperateCode.VALIDATE_DTO_SINGLE_FIELD_PATTERN;
		initialize(model, fieldName, fieldValue, patterns, message);
	}
	
	/**
	 * 바인드된 값으로 벨리데이션 설정 DTO 초기화
	 * @Name : initialize 
	 * @Description : 
	 * @LastUpdated : 2015. 10. 17. 오후 11:12:01
	 * @param model
	 * @param fieldName
	 * @param fieldValue
	 * @param patterns
	 * @param message
	 */
	private void initialize(Object model, String fieldName, Object fieldValue, String[] patterns, String message){
		this.reset();
		this.model = model;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.patterns = patterns;
		this.message = message;
		
		if(this.patterns != null) {
			
			for(String pattern : this.patterns) {
				if( pattern instanceof String ) {
					if(patterns != null && pattern.toString().equalsIgnoreCase("required")) {
						this.required = true;
						break;
					}
				}
			}
		}
	}
	
	private void reset(){
		
		fieldName = "";
		fieldValue = null;
		message = "";
		patterns = null;
		required = false;
		validation = true;
	}
	
	
	public boolean isValidation() {
		return validation;
	}

	public void setValidation(boolean validation) {
		this.validation = validation;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String[] getPatterns() {
		return patterns;
	}

	public void setPatterns(String[] patterns) {
		this.patterns = patterns;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Object getModel() {
		return model;
	}

	public int getValidateType() {
		return validateType;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	public void setValidateType(int validateType) {
		this.validateType = validateType;
	}

}
