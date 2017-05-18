package com.universal.code.ast.java;

import japa.parser.ast.ImportDeclaration;

import java.util.List;
import java.util.Map;

public class ASTCodeDTO {

	String fileDir;
	String packages;
	String typeName;
	List<ImportDeclaration> imports;
	Map<String, Object> type;
	
	public String getFileDir() {
		return fileDir;
	}
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}
	public String getPackages() {
		return packages;
	}
	public void setPackages(String packages) {
		this.packages = packages;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public List<ImportDeclaration> getImports() {
		return imports;
	}
	public void setImports(List<ImportDeclaration> imports) {
		this.imports = imports;
	}
	public Map<String, Object> getType() {
		return type;
	}
	public void setType(Map<String, Object> type) {
		this.type = type;
	}


	
}
