package com.universal.code.ast.java;

import japa.parser.ASTHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.exception.ValidateException;
import com.universal.code.utils.CollectionUtil;
import com.universal.code.utils.CommonUtil;
import com.universal.code.utils.StringUtil;

@Component
public class ASTCodeGenerator {

	public static final int CREATION_MAIN_TYPE = 1;
	public static final int CREATION_INNER_TYPE = 2;
	
	public static List<ImportDeclaration> defaultImports;
	static {
		defaultImports = new ArrayList<ImportDeclaration>();
		defaultImports.add(new ImportDeclaration(new NameExpr("org.slf4j.Logger"), false, false));
		defaultImports.add(new ImportDeclaration(new NameExpr("org.slf4j.LoggerFactory"), false, false));
	}
	
	public static List<ImportDeclaration> getDefaultImports() {
		return defaultImports;
	}

	public static void setDefaultImports(List<ImportDeclaration> defaultImports) {
		ASTCodeGenerator.defaultImports = defaultImports;
	}

	
	
	@Autowired
	private ASTCodeHelper astCodeHelper;
	
	@Autowired
	private CollectionUtil collectionUtil;
	
	public CompilationUnit generate( ASTCodeDTO astCodeDTO ) {
		return generate( astCodeDTO, null, null );
	}
	
	@SuppressWarnings("unchecked")
	private CompilationUnit generate( ASTCodeDTO astCodeDTO, CompilationUnit compUnit, ClassOrInterfaceDeclaration parentType ) {
		if(astCodeDTO == null) {
			throw new ValidateException("소스코드 정보가 존재하지 않습니다.");
		}
		
		if(astCodeHelper == null) {
			astCodeHelper = new ASTCodeHelper();
		}
		if(collectionUtil == null) {
			collectionUtil = new CollectionUtil();
		}
		
		CompilationUnit cu = null;
		Object unit = null;
		
		String fileDir = astCodeDTO.getFileDir();
		String packages = astCodeDTO.getPackages();
		List<ImportDeclaration> imports = astCodeDTO.getImports();
		List<ImportDeclaration> importTypes = null; 
		String typeName = astCodeDTO.getTypeName();
		Map<String, Object> type = astCodeDTO.getType();
		
		if(type == null) {
			throw new ValidateException("타입정보가 존재하지 않습니다.");
		}
		
		if(packages == null) {
			throw new ValidateException("패키지 정보가 존재하지 않습니다.");
		}
		else if(packages.endsWith(IOperateCode.STR_DOT)) {
			packages = packages.substring(0, packages.length() - IOperateCode.STR_DOT.length());
		}
		
		if(typeName == null) {
			throw new ValidateException("타입이름이 존재하지 않습니다.");
		}

		List<Integer> subModifiers = null;
		List<AnnotationExpr> annotations = null;
		List<VariableDeclarator> variables = null;
		List<MemberHelper> memberParameters = null;
		List<Parameter> parameters = null;
		List<TypeParameter> typeParameters = null;
		List<ClassOrInterfaceType> extendsList = null;
		List<ClassOrInterfaceType> implementsList = null;
		
		Integer modifiers = null;
		Integer arrayCount = null;
		Boolean isInterface = null;
		String typeStr = null;
		String returnType = null;
		String name = null;
		MethodDeclaration method = null;
		ConstructorDeclaration constructor = null;
		BlockStmt block = null;
		Type nodeType = null; 
		List<NameExpr> throwses = null;
		
		Object dataTransfer = null;
		
		try {
			if(fileDir != null) {
				if(fileDir.indexOf(IOperateCode.STR_BACK_SLASH) > -1) {
					fileDir = fileDir.replace(IOperateCode.STR_BACK_SLASH, IOperateCode.STR_SLASH);
					if(fileDir.endsWith(IOperateCode.STR_SLASH)) {
						fileDir = fileDir.substring(0, fileDir.length() - IOperateCode.STR_SLASH.length());
					}
				}
				
				if(!fileDir.endsWith(packages.replace(IOperateCode.STR_DOT, IOperateCode.STR_SLASH))) {
					throw new ValidateException("파일 생성 경로와 패키지정보가 일치하지 않습니다.");
				}
			}
			
			/** # create compilationUnit start */
			if(compUnit == null && parentType == null) {
				// default import setting
				if(imports != null && getDefaultImports() != null) {
					imports.addAll(ASTCodeGenerator.getDefaultImports());
				}
				else if(imports == null && getDefaultImports() != null) {
					imports = ASTCodeGenerator.getDefaultImports();
				}	
				
				// create compilationUnit with package and imports
				cu = astCodeHelper.createCompilationUnit(packages, imports);
				unit = cu;
			}
			else if(compUnit != null && parentType != null) {
				
				importTypes = compUnit.getImports();
				importTypes.addAll(imports);
				importTypes = (List<ImportDeclaration>) collectionUtil.toUniqueList(importTypes);
				
				cu = compUnit;
				astCodeHelper.setImports(cu, importTypes);
				unit = parentType;
			}
			else {
				throw new ApplicationException("컴파일유닛 또는 부모타입정보가 존재하지 않거나 잘못되었습니다.");
			}
			// set imports
			
			
		    /** # create compilationUnit end */
		    
		    /** # create type start */
		    
		    modifiers = CommonUtil.integerValue((Integer) type.get("modifiers"));
		    isInterface = CommonUtil.booleanValue((Boolean) type.get("isInterface"));
		    
		    // create the type declaration
		    dataTransfer = type.get("subModifiers");
		    if(dataTransfer != null) {
		    	if(List.class.isAssignableFrom(dataTransfer.getClass())) {
		    		subModifiers = (List<Integer>) dataTransfer;
		    	}
		    	else {
		    		throw new ValidateException("subModifiers는 List타입 이어야 합니다.");
		    	}
		    }
		    
		    // get annotation
		    dataTransfer = type.get("annotations");
		    if(dataTransfer != null) {
		    	if(List.class.isAssignableFrom(dataTransfer.getClass())) {
		    		// set typeAnnotations
		    		annotations = (List<AnnotationExpr>) dataTransfer;
		    	}
		    	else {
		    		throw new ValidateException("typeAnnotations는 List타입 이어야 합니다.");
		    	}
		    }
		    
		    // get typeParameters
		    dataTransfer = type.get("typeParameters");
		    if(dataTransfer != null) {
		    	if(List.class.isAssignableFrom(dataTransfer.getClass())) {
		    		// set typeParameters
		    		typeParameters = (List<TypeParameter>) dataTransfer;
		    	}
		    	else {
		    		throw new ValidateException("implements는 List타입 이어야 합니다.");
		    	}
		    }
		    
		    // get extends
		    dataTransfer = type.get("extends");
		    if(dataTransfer != null) {
		    	if(List.class.isAssignableFrom(dataTransfer.getClass())) {
		    		// set extends
		    		extendsList = (List<ClassOrInterfaceType>) dataTransfer;
		    	}
		    	else {
		    		throw new ValidateException("extends는 List타입 이어야 합니다.");
		    	}
		    }
		    
		    // get implements
		    dataTransfer = type.get("implements");
		    if(dataTransfer != null) {
		    	if(List.class.isAssignableFrom(dataTransfer.getClass())) {
		    		// set implements
		    		implementsList = (List<ClassOrInterfaceType>) dataTransfer;
		    	}
		    	else {
		    		throw new ValidateException("implements는 List타입 이어야 합니다.");
		    	}
		    }
		    
		    // create main type   parentType
		    ClassOrInterfaceDeclaration classType = astCodeHelper.createClassOrInterfaceDeclaration(unit, modifiers, subModifiers, annotations, isInterface, typeName, typeParameters, extendsList, implementsList, null);
		  
		    /** # create type end */
		    
		    /** # create constructor start */
		    
		    // create constructor
		    dataTransfer = type.get("constructors");
		    if(dataTransfer != null) {
		    	if(List.class.isAssignableFrom(dataTransfer.getClass())) {
		    		
		    		for(Map<String, Object> construct : (List<Map<String, Object>>) dataTransfer) {
		    			
		    			modifiers = CommonUtil.integerValue((Integer) construct.get("modifiers"));
		    			throwses = (List<NameExpr>) construct.get("throws");
		    			memberParameters = (List<MemberHelper>) construct.get("parameters");
		    			subModifiers = (List<Integer>) construct.get("subModifiers");
		    			typeParameters = (List<TypeParameter>) construct.get("typeParameters");
		    			annotations = (List<AnnotationExpr>) construct.get("annotations");
		    			
		    			// create constructor parameters
		    			parameters = astCodeHelper.getParameter(memberParameters);
		    			
			    		// create constructors
		    			constructor = astCodeHelper.createConstructorDeclaration(classType, modifiers, subModifiers, annotations, typeParameters, typeName, parameters, throwses, null);
		    			
			    		// add a body to the method
		    		    block = astCodeHelper.createConstructorBlock(constructor);
		    		    
		    		    // add a statement do the method body
		    		    /************************
		    		     * 남음 이분이 관건임 
		    		     */
		    		    NameExpr clazz = new NameExpr("System");
		    		    FieldAccessExpr fieldAccess = new FieldAccessExpr(clazz, "out");
		    		    MethodCallExpr callee = new MethodCallExpr(fieldAccess, "println");
		    		    
		    		    ASTHelper.addArgument(callee, new StringLiteralExpr("Hello World!"));
		    		    ASTHelper.addArgument(callee, new StringLiteralExpr("Goto Param"));
		    		    
		    		    ASTHelper.addStmt(block, callee);
		    		    
		    		}
		    	}
		    	else {
		    		throw new ValidateException("constructors는 List타입 이어야 합니다.");
		    	}
		    }
		    
		    /** # create constructor end */
		    
		    /** # create field start */
		    
		    // create field
		    dataTransfer = type.get("fields");
		    if(dataTransfer != null) {
		    	if(List.class.isAssignableFrom(dataTransfer.getClass())) {
		    		for(Map<String, Object> field : (List<Map<String, Object>>) dataTransfer){
		    			typeStr = (String) field.get("type");
		    			variables = (List<VariableDeclarator>) field.get("variables");
		    			modifiers = CommonUtil.integerValue((Integer) field.get("modifiers"));
		    			subModifiers = (List<Integer>) field.get("subModifiers");
		    			annotations = (List<AnnotationExpr>) field.get("annotations");
		    			
		    			if(StringUtil.isEmpty(typeStr)) {
		    				throw new ValidateException("필드 타입이 존재하지 않습니다. Config : " + field);
		    			}
		    			if(variables == null || variables.size() == 0) {
		    				throw new ValidateException("필드 명칭 정보가 존재하지 않습니다. Config : " + variables);
		    			}
		    			// create field
		    			astCodeHelper.createFieldDeclaration(classType, modifiers, subModifiers, annotations, new ClassOrInterfaceType(typeStr), variables);			
		    		}
		    	}
		    	else {
		    		throw new ValidateException("fields는 List타입 이어야 합니다.");
		    	}
		    }
		    
		    /** # create field end */
		    
		    /** # create method start */
		    
		    // create a method
		    dataTransfer = type.get("methods");
		    if(dataTransfer != null) {
		    	if(List.class.isAssignableFrom(dataTransfer.getClass())) {
		    		
		    		for(Map<String, Object> methods : (List<Map<String, Object>>) dataTransfer){
		    			returnType = (String) methods.get("returnType");
		    			name = (String) methods.get("name");
		    			memberParameters = (List<MemberHelper>) methods.get("parameters");
		    			modifiers = CommonUtil.integerValue((Integer) methods.get("modifiers"));
		    			subModifiers = (List<Integer>) methods.get("subModifiers");
		    			throwses = (List<NameExpr>) methods.get("throws");
		    			annotations = (List<AnnotationExpr>) methods.get("annotations");
		    			typeParameters = (List<TypeParameter>) methods.get("typeParameters");
		    			arrayCount = CommonUtil.integerValue((Integer) methods.get("arrayCount"));
		    			
		    			if(StringUtil.isEmpty(name)) {
		    				throw new ValidateException("메소드 명칭이 존재하지 않습니다. Config : " + methods);
		    			}
		    			
		    			if(returnType == null || returnType.equalsIgnoreCase("void")) {
		    				nodeType = ASTHelper.VOID_TYPE;
		    			}
		    			else {
		    				nodeType = new ClassOrInterfaceType(returnType);
		    			}
		    			// create a method
		    			method = astCodeHelper.createMethodDeclaration(classType, modifiers, subModifiers, annotations, typeParameters, nodeType, name, null, arrayCount, throwses, null);
		    			
		    			
		    			if(memberParameters != null && memberParameters.size() > 0) {
		    				 // add a parameter to the method
		    				astCodeHelper.addMethodParameter(method, memberParameters);
		    			}
		    			
		    		    // add a body to the method
		    		    block = astCodeHelper.createMethodBody(method);
		    		    
		    		    // add a statement do the method body
		    		    /************************
		    		     * 남음 이분이 관건임 
		    		     */
		    		    NameExpr clazz = new NameExpr("System");
		    		    FieldAccessExpr fieldAccess = new FieldAccessExpr(clazz, "out");
		    		    MethodCallExpr callee = new MethodCallExpr(fieldAccess, "println");
		    		    
		    		    ASTHelper.addArgument(callee, new StringLiteralExpr("Hello World!"));
		    		    ASTHelper.addArgument(callee, new StringLiteralExpr("Goto Param"));
		    		    
		    		    ASTHelper.addStmt(block, callee);
		    		}
		    	}
		    	else {
		    		throw new ValidateException("methods는 List타입 이어야 합니다.");
		    	}
		    }

		    /** # create field end */
		    
		    /** # create innerType start */
		    
		    dataTransfer = type.get("innerTypes");
		    if(dataTransfer != null) {
		    	if(List.class.isAssignableFrom(dataTransfer.getClass())) {
		    		for(ASTCodeDTO astCode : (List<ASTCodeDTO>) dataTransfer) {
		    			generate( astCode, cu, classType );
		    		}
		    	}
		    	else {
		    		throw new ValidateException("innerTypes는 List타입 이어야 합니다.");
		    	}
		    }
		    
		    /** # create innerType end */
		}
		catch(Exception e){
			throw new ApplicationException(e);
		}
		
	    return cu;
	}
	
}
