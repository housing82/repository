package com.universal.code.bxm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.constants.JavaReservedWordConstants;
import com.universal.code.utils.StringUtil;

public class GenerateHelper {

	private final static Logger logger = LoggerFactory.getLogger(GenerateHelper.class);
	
	private StringUtil stringUtil;
	
	private static GenerateHelper INSTANCE;
	
	static {
		INSTANCE = new GenerateHelper();
	}
	
	public GenerateHelper() {
		stringUtil = new StringUtil();
	}
	
	public static GenerateHelper getInstance() {
		return INSTANCE;
	}
	
	public String getCamelCaseFieldName(String str) {
		return JavaReservedWordConstants.get(stringUtil.getCamelCaseString(StringUtil.NVL(str)));
	}
	
}
