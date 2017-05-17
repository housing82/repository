package com.universal.code.dto;

import com.universal.code.constants.IOperateCode;

public class RuntimeHeader {

	private long startTimeMillies;
	
	private long endTimeMillies;
	
	private long durationMillies;
	
	private String className;
	
	private String methodName;
	
	public RuntimeHeader() {
		startTimeMillies = IOperateCode.LONG_ZERO_VALUE;
		endTimeMillies = IOperateCode.LONG_ZERO_VALUE;
		durationMillies = IOperateCode.LONG_ZERO_VALUE;
		className = null;
		methodName = null;	
	}

	public long getStartTimeMillies() {
		return startTimeMillies;
	}

	public void setStartTimeMillies(long startTimeMillies) {
		this.startTimeMillies = startTimeMillies;
	}

	public long getEndTimeMillies() {
		return endTimeMillies;
	}

	public void setEndTimeMillies(long endTimeMillies) {
		this.endTimeMillies = endTimeMillies;
	}

	public long getDurationMillies() {
		return durationMillies;
	}

	public void setDurationMillies(long durationMillies) {
		this.durationMillies = durationMillies;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	
}
