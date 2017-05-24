package com.universal.code.dto;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.universal.code.extend.CommonObject;
import com.universal.code.extend.IDataTransferObject;


public class ClassDTO extends CommonObject implements IDataTransferObject, Serializable {
	
    private String packageString;
    private String className;
    private Method[] methods;
    
    public String getPackageString() {
        return packageString;
    }
    public void setPackageString(String packageString) {
        this.packageString = packageString;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public Method[] getMethods() {
        return methods;
    }
    public void setMethods(Method[] methods) {
        this.methods = methods;
    }
    
}
