package com.universal.code.ast.java;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class MemberHelper {

	private static Set<String> primitiveTypes = new HashSet<String>();
	static {
		primitiveTypes.add("byte");
		primitiveTypes.add("short");
		primitiveTypes.add("int");
		primitiveTypes.add("long");
		primitiveTypes.add("float");
		primitiveTypes.add("double");
		primitiveTypes.add("char");
		primitiveTypes.add("boolean");
	}
	
	public MemberHelper(){ }
	
	public MemberHelper(String argType, int	arrayCount, String argName){
		this.argType = argType;
		this.arrayCount = arrayCount;
		this.argName = argName;
	}
	
	public MemberHelper(String argType, int	arrayCount, String argName, boolean varArgs){
		this.argType = argType;
		this.arrayCount = arrayCount;
		this.argName = argName;
		this.varArgs = varArgs;
	}
	
	private String argType;
	private int	arrayCount;
	private String argName;
	private boolean varArgs;
	
	public boolean isPrimitive(){
		if(argType != null) {
			return primitiveTypes.contains(argType);
		}
		else {
			return false;
		}
	}
	
	public String getArgType() {
		return argType;
	}
	public void setArgType(String argType) {
		this.argType = argType;
	}
	public int getArrayCount() {
		return arrayCount;
	}
	public void setArrayCount(int arrayCount) {
		this.arrayCount = arrayCount;
	}
	public String getArgName() {
		return argName;
	}
	public void setArgName(String argName) {
		this.argName = argName;
	}
	public boolean isVarArgs() {
		return varArgs;
	}
	public void setVarArgs(boolean varArgs) {
		this.varArgs = varArgs;
	}
	
	
	
}
