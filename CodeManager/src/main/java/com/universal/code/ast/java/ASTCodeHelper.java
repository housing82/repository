package com.universal.code.ast.java;

import japa.parser.ASTHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.exception.ApplicationException;
import com.universal.code.exception.ValidateException;
import com.universal.code.utils.CommonUtil;

@Component
public class ASTCodeHelper {

	private static final Logger logger = LoggerFactory.getLogger(ASTCodeHelper.class);
	
	@Autowired
	private CommonUtil commonUtil;
	
	public ASTCodeHelper(){
		if(commonUtil == null) {
			commonUtil = new CommonUtil();
		}
	}
	
	public CompilationUnit createCompilationUnit(){
		return createCompilationUnit(null);
	}
	
	public CompilationUnit createCompilationUnit(String package_){
		return createCompilationUnit(package_, null);
	}
	
	public CompilationUnit createCompilationUnit(String package_, List<ImportDeclaration> imports){
		if(logger.isDebugEnabled()) {
			logger.debug("[START] createCompilationUnit ");
		}
		CompilationUnit cu = new CompilationUnit();
		// set the package
		setPackage(cu, package_);
		// set the import
		setImports(cu, imports);
		
		return cu;
	}
	
	public void setPackage(CompilationUnit cu, String package_){
		if(package_ != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("[START] setPackage");
			}
			// set the package
			cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(package_)));
		}
	}
	
	public void setImports(CompilationUnit cu, List<ImportDeclaration> imports){
		if(imports != null && imports.size() > 0) {
			if(logger.isDebugEnabled()) {
				logger.debug("[START] setImports");
			}
			// set the import
			cu.setImports(imports);
		}
	}
	
	public EnumDeclaration createEnumDeclaration(int modifiers, String name) {
		return createEnumDeclaration(modifiers, null, name, null, null, null);
	}
	
	public EnumDeclaration createEnumDeclaration(int modifiers, List<AnnotationExpr> annotations, String name, List<ClassOrInterfaceType> implementsList, List<EnumConstantDeclaration> entries, List<BodyDeclaration> members){
		if(logger.isDebugEnabled()) {
			logger.debug("[START] createEnumDeclaration");
		}
		EnumDeclaration enumType = new EnumDeclaration(modifiers, annotations, name, implementsList, entries, members);
		
		return enumType;
	}

	public ClassOrInterfaceDeclaration createClassOrInterfaceDeclaration(Object unit, int modifiers, boolean isInterface, String name) {
		return createClassOrInterfaceDeclaration(unit, modifiers, null, null, isInterface, name, null, null, null, null);
	}
	
	public ClassOrInterfaceDeclaration createClassOrInterfaceDeclaration(Object unit, int modifiers, List<Integer> subModifiers, boolean isInterface, String name) {
		return createClassOrInterfaceDeclaration(unit, modifiers, subModifiers, null, isInterface, name, null, null, null, null);
	}
	
	public ClassOrInterfaceDeclaration createClassOrInterfaceDeclaration(Object unit, int modifiers, List<Integer> subModifiers, List<AnnotationExpr> annotations, boolean isInterface, String name, List<TypeParameter> typeParameters, List<ClassOrInterfaceType> extendsList, List<ClassOrInterfaceType> implementsList, List<BodyDeclaration> members) {
		if(logger.isDebugEnabled()) {
			logger.debug("[START] createClassOrInterfaceDeclaration");
		}
		// create the type declaration		
		ClassOrInterfaceDeclaration type = new ClassOrInterfaceDeclaration(modifiers, annotations, isInterface, name, typeParameters, extendsList, implementsList, members);	
	    if(subModifiers != null) {
	    	for(Integer subModifier : subModifiers) {
	    		type.setModifiers(ModifierSet.addModifier(type.getModifiers(), subModifier));
	    	}
	    }
	    if(unit instanceof CompilationUnit) {
	    	ASTHelper.addTypeDeclaration((CompilationUnit) unit, type);	
	    }
	    else if(unit instanceof ClassOrInterfaceDeclaration) {
	    	ASTHelper.addMember((TypeDeclaration) unit, type);
	    }
	    else {
	    	throw new ApplicationException("잘못된 부모 타입 : ".concat(unit != null ? unit.getClass().getCanonicalName() : "null"));
	    }
		return type;
	}
	
	public ConstructorDeclaration createConstructorDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, String name) {
		return createConstructorDeclaration(classType, modifiers, null, null, null, name, null, null, null);
	}
	
	public ConstructorDeclaration createConstructorDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, List<Integer> subModifiers, String name) {
		return createConstructorDeclaration(classType, modifiers, subModifiers, null, null, name, null, null, null);
	}

	public ConstructorDeclaration createConstructorDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, List<AnnotationExpr> annotations, List<TypeParameter> typeParameters, String name, List<Parameter> parameters, List<NameExpr> throws_, BlockStmt block) {
		return createConstructorDeclaration(classType, modifiers, null, annotations, typeParameters, name, parameters, throws_, block);
	}
	
	public ConstructorDeclaration createConstructorDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, List<Integer> subModifiers, List<AnnotationExpr> annotations, List<TypeParameter> typeParameters, String name, List<Parameter> parameters, List<NameExpr> throws_, BlockStmt block) {
		if(logger.isDebugEnabled()) {
			logger.debug("[START] createConstructorDeclaration");
		}
		ConstructorDeclaration constructor = new ConstructorDeclaration(modifiers, annotations, typeParameters, name, parameters, throws_, block);
		if(subModifiers != null) {
	    	for(Integer subModifier : subModifiers) {
	    		constructor.setModifiers(ModifierSet.addModifier(constructor.getModifiers(), subModifier));
	    	}
	    }
		ASTHelper.addMember(classType, constructor);
		return constructor;
	}
	
	public FieldDeclaration createFieldDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, Type type, VariableDeclarator variable) {
		return createFieldDeclaration(classType, modifiers, null, null, type, null);
	}

	public FieldDeclaration createFieldDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, Type type, List<VariableDeclarator> variables) {
		return createFieldDeclaration(classType, modifiers, null, null, type, variables);
	}
	
	public FieldDeclaration createFieldDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, List<Integer> subModifiers, Type type, VariableDeclarator variable) {
		return createFieldDeclaration(classType, modifiers, subModifiers, null, type, null);
	}

	public FieldDeclaration createFieldDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, List<Integer> subModifiers, Type type, List<VariableDeclarator> variables) {
		return createFieldDeclaration(classType, modifiers, subModifiers, null, type, variables);
	}
	
	public FieldDeclaration createFieldDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, List<Integer> subModifiers, List<AnnotationExpr> annotations, Type type, List<VariableDeclarator> variables){
		if(logger.isDebugEnabled()) {
			logger.debug("[START] createFieldDeclaration");
		}
		FieldDeclaration field = new FieldDeclaration(modifiers, annotations, type, variables);
		logger.debug(" field : " + field);
	    if(subModifiers != null) {
	    	for(Integer subModifier : subModifiers) {
	    		field.setModifiers(ModifierSet.addModifier(field.getModifiers(), subModifier));
	    	}
	    }
	    
	    logger.debug(" addMember before ");
		ASTHelper.addMember(classType, field);
		logger.debug(" addMember after ");
		return field;
	}

	public MethodDeclaration createMethodDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, Type type, String name) {
		return createMethodDeclaration(classType, modifiers, null, null, null, type, name, null, null, null, null);
	}
 
	public MethodDeclaration createMethodDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, Type type, String name, List<Parameter> parameters) {
		return createMethodDeclaration(classType, modifiers, null, null, null, type, name, parameters, null, null, null);
	}
	
	public MethodDeclaration createMethodDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, List<Integer> subModifiers, Type type, String name) {
		return createMethodDeclaration(classType, modifiers, subModifiers, null, null, type, name, null, null, null, null);
	}
 
	public MethodDeclaration createMethodDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, List<Integer> subModifiers, Type type, String name, List<Parameter> parameters) {
		return createMethodDeclaration(classType, modifiers, subModifiers, null, null, type, name, parameters, null, null, null);
	}
	
	public MethodDeclaration createMethodDeclaration(ClassOrInterfaceDeclaration classType, int modifiers, List<Integer> subModifiers, List<AnnotationExpr> annotations, List<TypeParameter> typeParameters, Type type, String name, List<Parameter> parameters, Integer arrayCount, List<NameExpr> throws_, BlockStmt block){
		if(logger.isDebugEnabled()) {
			logger.debug("[START] createMethodDeclaration");
		}
		// create a method
		if(arrayCount == null) {
			arrayCount = 0;
		}
	    MethodDeclaration method = new MethodDeclaration(modifiers, annotations, typeParameters, type, name, parameters, arrayCount, throws_, block);
	    if(subModifiers != null) {
	    	for(Integer subModifier : subModifiers) {
	    		method.setModifiers(ModifierSet.addModifier(method.getModifiers(), subModifier));
	    	}
	    }
	    
	    ASTHelper.addMember(classType, method);
	    
	    return method;
	}
	
	public List<Parameter> getParameter(List<MemberHelper> parameters) {
		List<Parameter> out = null;
		if(parameters != null) {
			out = new ArrayList<Parameter>();
			// create parameter
			for(MemberHelper parameter : parameters) {
				Parameter param = ASTHelper.createParameter(ASTHelper.createReferenceType(parameter.getArgType(), parameter.getArrayCount()), parameter.getArgName());	
			    param.setVarArgs(parameter.isVarArgs());
			    out.add(param);
			}
		}
		return out;
	}

	
	public void addMethodParameter(MethodDeclaration method, List<MemberHelper> parameters){
		if(logger.isDebugEnabled()) {
			logger.debug("[START] addMethodParameter");
		}
		Parameter param = null;
		for(MemberHelper parameter : parameters) {
			// add a parameter to the method
			//if(parameter.isPrimitive()) {
			//	PrimitiveType primitive = new PrimitiveType();
			//	param = ASTHelper.createParameter(ASTHelper.createReferenceType(primitive, parameter.getArrayCount()), parameter.getArgName());				
			//}
			//else {
				param = ASTHelper.createParameter(ASTHelper.createReferenceType(parameter.getArgType(), parameter.getArrayCount()), parameter.getArgName());	
			//}
		    
		    param.setVarArgs(parameter.isVarArgs());
		    ASTHelper.addParameter(method, param);
		}

	}
	
	public BlockStmt createConstructorBlock(ConstructorDeclaration constructor){
		if(logger.isDebugEnabled()) {
			logger.debug("[START] createConstructorBlock");
		}
		// add a block to the constructor
	    BlockStmt block = new BlockStmt();
	    constructor.setBlock(block);
	    
	    return block;
	}
	
	public BlockStmt createMethodBody(MethodDeclaration method){
		if(logger.isDebugEnabled()) {
			logger.debug("[START] createMethodBody");
		}
		// add a body to the method
	    BlockStmt block = new BlockStmt();
	    method.setBody(block);
	    
	    return block;
	}
	
	public void setMethodStmt(BlockStmt block){
		if(logger.isDebugEnabled()) {
			logger.debug("[START] setMethodStmt");
		}
		// add a statement do the method body
	    NameExpr clazz = new NameExpr("System");
	    FieldAccessExpr field = new FieldAccessExpr(clazz, "out");
	    MethodCallExpr call = new MethodCallExpr(field, "println");
	    
	    ASTHelper.addArgument(call, new StringLiteralExpr("Hello World!"));
	    
	    ASTHelper.addStmt(block, call);
	}
	
	
	public void addAnnotation(List<AnnotationExpr> annotations, String nameExpr) {
		addAnnotation(annotations, nameExpr, null);
	}
	
	@SuppressWarnings("unchecked")
	public void addAnnotation(List<AnnotationExpr> annotations, String nameExpr, Object valueExpr) {
		if(valueExpr != null) {
			if(Map.class.isAssignableFrom(valueExpr.getClass())) {
				addNormalAnnotation(annotations, nameExpr, (Map<String, Object>) valueExpr);
			}
			else {
				addSingleMemberAnnotation(annotations, nameExpr, valueExpr);
			}
		}
		else {
			addMarkerAnnotation(annotations, nameExpr);
		}
	}

	private void addMarkerAnnotation(List<AnnotationExpr> annotations, String nameExpr){
		if(annotations == null) {
			throw new ValidateException("Repository List is null");
		}
		MarkerAnnotationExpr markerAnno = new MarkerAnnotationExpr(new NameExpr(nameExpr));
		annotations.add(markerAnno);
	}
	
	private void addSingleMemberAnnotation(List<AnnotationExpr> annotations, String nameExpr, Object valueExpr){
		if(annotations == null) {
			throw new ValidateException("Repository List is null");
		}
		
		SingleMemberAnnotationExpr singleAnno = new SingleMemberAnnotationExpr();
	    singleAnno.setName(new NameExpr(nameExpr));
	    singleAnno.setMemberValue(createSingleValueExpr(valueExpr));
		annotations.add(singleAnno);
	}
	
	private void addNormalAnnotation(List<AnnotationExpr> annotations, String nameExpr, Map<String, Object> pairsMap){
		if(annotations == null) {
			throw new ValidateException("Repository List is null");
		}
		
		NormalAnnotationExpr normalAnno = new NormalAnnotationExpr();
	    normalAnno.setName(new NameExpr(nameExpr));
	    Expression expr = null;
	    if(pairsMap != null && pairsMap.size() > 0) {
		    List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
		    
		    for(Entry<String, Object> entry : pairsMap.entrySet()){
		    	expr = createSingleValueExpr(entry.getValue());	
		    	if(expr != null) {
		    		pairs.add(new MemberValuePair(entry.getKey(), expr));
		    	}
		    }
		    if(pairs.size() > 0) {
		    	normalAnno.setPairs(pairs);
		    }
	    }
	    annotations.add(normalAnno);
	}
		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Expression createSingleValueExpr(Object value){
		
		Expression out = null;
		if(value == null) {
			out = new NullLiteralExpr();
		}
		else if(String.class.isAssignableFrom(value.getClass())) {
    		out = new StringLiteralExpr((String) value);	
    	}
    	else if(Boolean.class.isAssignableFrom(value.getClass())) {
    		out = new BooleanLiteralExpr((Boolean) value);	
    	}
    	else if(Integer.class.isAssignableFrom(value.getClass())) {
    		out = new IntegerLiteralExpr(Integer.toString((Integer) value));	
    	}
    	else if(Long.class.isAssignableFrom(value.getClass())) {
    		out = new LongLiteralExpr(Long.toString((Long) value));	
    	}
    	else if(Double.class.isAssignableFrom(value.getClass())) {
    		out = new DoubleLiteralExpr(Double.toString((Double) value));	
    	}
    	else if(Character.class.isAssignableFrom(value.getClass())) {
    		out = new CharLiteralExpr(Character.toString((Character) value));	
    	}
    	else if(Byte.class.isAssignableFrom(value.getClass())) {
    		out = new IntegerLiteralExpr(Byte.toString((Byte) value));	
    	}
    	else if(Short.class.isAssignableFrom(value.getClass())) {
    		out = new IntegerLiteralExpr(Short.toString((Short) value));	
    	}
    	else if(Float.class.isAssignableFrom(value.getClass())) {
    		out = new DoubleLiteralExpr(Float.toString((Float) value));
    	}
    	else if(Class.class.isAssignableFrom(value.getClass())) {
    		out = new ClassExpr(new ReferenceType(new ClassOrInterfaceType(value.toString().substring(value.toString().indexOf(" ")+1))));	
    	}
    	else if(value.getClass().isArray()) { 
    		List<Expression> arrayValues = new ArrayList<Expression>();
    		Object[] items = commonUtil.convertToObjectArray(value);
    		Expression expr = null;
            for(Object item : items) {
            	expr = createSingleValueExpr(item);
            	if(expr != null) {
            		arrayValues.add(expr);
            	}
            }
            out = new ArrayInitializerExpr(arrayValues);
    	}
    	else if(List.class.isAssignableFrom(value.getClass())) {
    		if(((List<?>) value).size() > 0 && Expression.class.isAssignableFrom(((List<?>) value).get(0).getClass())) {
    			out = new ArrayInitializerExpr((List<Expression>) value);
    		}
    		else {
    			out = createSingleValueExpr(((List) value).toArray());
    		}
    	}
    	else if(NameExpr.class.isAssignableFrom(value.getClass())) {
    		out = (NameExpr) value;
    	}
    	else {
    		out = new NameExpr(String.valueOf(value));
    	}
		
		return out;
	}

	
	public List<TypeParameter> createTypeParameters(String name, String... extendsType) {
		return createTypeParameters(new ArrayList<TypeParameter>(), name, extendsType);
	}
	
	public List<TypeParameter> createTypeParameters(List<TypeParameter> typeParameters, String name, String... extendsType) {
		
		List<ClassOrInterfaceType> typeBound = new ArrayList<ClassOrInterfaceType>();
		if(extendsType != null && extendsType.length > 0) {
			for(String extendsName : extendsType){
				typeBound.add(new ClassOrInterfaceType(extendsName));
			}
		}
	    typeParameters.add(new TypeParameter(name, typeBound));
	    return typeParameters;
	}
	
	public List<ClassOrInterfaceType> createExtends(String... extendsType){
		return createExtends(new ArrayList<ClassOrInterfaceType>(), extendsType);
	}
	
	public List<ClassOrInterfaceType> createExtends(List<ClassOrInterfaceType> extendsList, String... extendsType){
		
		if(extendsType != null && extendsType.length > 0) {
			for(String extendsName : extendsType){
				extendsList.add(new ClassOrInterfaceType(extendsName));
			}
		}
		return extendsList;
	}
	
	public List<ClassOrInterfaceType> createImplements(String... implementsType){
		return createImplements(new ArrayList<ClassOrInterfaceType>(), implementsType);
	}
	
	public List<ClassOrInterfaceType> createImplements(List<ClassOrInterfaceType> implementsList, String... implementsType){
		
		if(implementsType != null && implementsType.length > 0) {
			for(String implementsName : implementsType){
				implementsList.add(new ClassOrInterfaceType(implementsName));
			}
		}
		return implementsList;
	}
	
	public List<NameExpr> createNameExprs(String... names){
		return createNameExprs(new ArrayList<NameExpr>(), names);
	}
	
	public List<NameExpr> createNameExprs(List<NameExpr> nameExprList, String... names){
		
		if(names != null && names.length > 0) {
			for(String name : names){
				nameExprList.add(new NameExpr(name));
			}
		}
		return nameExprList;
	}
	
	
	public List<Integer> createSubModifiers(Integer... modifiers){
		return createSubModifiers(new ArrayList<Integer>(), modifiers);
	}
	
	public List<Integer> createSubModifiers(List<Integer> subModifiersList, Integer... modifiers){
		
		if(modifiers != null && modifiers.length > 0) {
			for(Integer modfrs : modifiers){
				subModifiersList.add(modfrs);
			}
		}
		return subModifiersList;
	}
	
	
	/**
	 * ArrayAccessExpr(Expression name, Expression index) 
	 * ArrayCreationExpr(Type type, int arrayCount, ArrayInitializerExpr initializer)
	 * AssignExpr(Expression target, Expression value, Operator op)
	 * BinaryExpr(Expression left, Expression right, Operator op)
	 * CastExpr(Type type, Expression expr)
	 * ConditionalExpr(Expression condition, Expression thenExpr, Expression elseExpr)
	 * 		EnclosedExpr( Expression inner)
	 * FieldAccessExpr(Expression scope, String field)
	 * InstanceOfExpr(Expression expr, Type type)
	 * MethodCallExpr(Expression scope, String name, List<Expression> args)
	 * ObjectCreationExpr(Expression scope, ClassOrInterfaceType type, List<Expression> args)
	 * QualifiedNameExpr(NameExpr scope, String name)
	 * 		SuperExpr( Expression classExpr)
	 * 		ThisExpr( Expression classExpr)
	 * UnaryExpr(Expression expr, Operator op)
	 * VariableDeclarationExpr(int modifiers, Type type, List<VariableDeclarator> vars)
	 */
	
	public Expression createMultiValueExpr(Map<?, ?> exprMap){
		Expression out = null;
		if(exprMap == null) {
			out = new NullLiteralExpr();
		}
		
		
		return out;
	}
	
	
}
