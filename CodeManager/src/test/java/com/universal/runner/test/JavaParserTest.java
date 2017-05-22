package com.universal.runner.test;

import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.annotation.Fields;
import com.universal.code.ast.java.ASTCodeDTO;
import com.universal.code.ast.java.ASTCodeGenerator;
import com.universal.code.ast.java.ASTCodeHelper;
import com.universal.code.ast.java.ASTConverter;
import com.universal.code.ast.java.ASTVisitor;
import com.universal.code.ast.java.MemberHelper;
import com.universal.code.constants.IOperateCode;
import com.universal.code.utils.CommonUtil;
import com.universal.code.utils.PropertyUtil;

public class JavaParserTest {

	private static final Logger logger = LoggerFactory.getLogger(JavaParserTest.class);
	
	private static PropertyUtil property = new PropertyUtil();
	
	private CommonUtil commonUtil = new CommonUtil();
	
	private ASTCodeHelper astCodeHelper = new ASTCodeHelper();
	
	private static void out(Object obj){
		System.out.println(obj);
	}
	
	static String path = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/test/java/com/universal/core/generic/GeneralTest2.java";
	static String path2 = "";

	static {
		path = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/test/java/com/universal/core/generic/GeneralTest2.java";
		//path = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/main/java/com/universal/core/database/mybatis/datasource/statics/TargetDataSourceType.java";
		//path ="D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/test/java/com/universal/core/ast/google/ExcelDTOTest.java";
		//path = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/main/java/com/universal/core/coder/MD5.java";
		//path = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/main/java/com/universal/core/marshaller/impl/json/JSONPixedBeanConverter.java";
		//path = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.system/src/main/java/com/universal/system/dto/common/CommonDTO.java";
		//path = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/main/java/com/universal/core/utils/classes/ClassScope.java";
		//path = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/main/java/com/universal/core/annotation/Fields.java";
		//path = "D:/02.Eclipse/workspace_home/mybatis-src/mybatis/src/test/java/org/apache/ibatis/plugin/PluginTest.java";
		//path = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/main/java/com/universal/core/ast/java/ASTVisitor.java";
		
		//path2 = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/main/java/com/universal/core/annotation/Fields.java";
		//path2 = "D:/02.Eclipse/workspace_home/mybatis-src/mybatis/src/test/java/org/apache/ibatis/plugin/PluginTest.java";
		path2 = "D:/02.Eclipse/workspace_home/com.universal-root/com.universal.core/src/main/java/com/universal/core/ast/java/ASTVisitor.java";
	}
	
	//@Test
	public void parse(){
		
		Object test = new String[]{"test","test"};
		System.out.println("test.getClass().isArray() : " + test.getClass().isArray());
		
		//Visitor
	    ASTVisitor visitor = new ASTVisitor();
	    List<Map<String, Object>> elements = visitor.execute(path, ASTVisitor.VISIT_FULL_NODE);
	    //List<Map<String, Object>> elements2 = visitor.execute(path2, ASTVisitor.VISIT_INIT_AND_DECLARAT_NODE);
	  
	    for(Map<String, Object> item : elements){
	    	//if(((Map<String, Object>)item.get(ASTConverter.NODE_DISPLAY_NAME)).get("modifiers") != null) {
	    		//out("isAbstract : " + ModifierSet.isAbstract( (Integer) ((Map<String, Object>)item.get(ASTConverter.NODE_DISPLAY_NAME)).get("modifiers")));
	    	//}
	    	out(getWhiteSpace((Integer)item.get("level")) + "["+(Integer)item.get("level")+"]"+ " (" + item.get(ASTConverter.NODE_DISPLAY_TYPE) + ") " + item.get(ASTConverter.NODE_DISPLAY_NAME) +  " >> " );
	    }
	    
	    //for(Map<String, Object> item : elements2){
	    //	out(getWhiteSpace((Integer)item.get("level")) + "["+(Integer)item.get("level")+"]"+ " (" + item.get(ASTConverter.NODE_DISPLAY_TYPE) + ") " + item.get(ASTConverter.NODE_DISPLAY_NAME));
	    //}
	    

	    
	}
	
	private String getWhiteSpace(int level){
		StringBuilder out = new StringBuilder();
		for(int i = 0; i < level; i++) {
			out.append("	");
		}
		return out.toString();
	}
	

	@Test
	public void create(){
	
	
		try {

		
		ASTCodeGenerator astCodeGenerator = new ASTCodeGenerator();
		
		//common
		List<MemberHelper> memberParameters = null;
		List<NameExpr> throwses = null;
		List<TypeParameter> typeParameters = null;
		List<TypeParameter> memberTypeParameters = null;
		List<AnnotationExpr> memberAnnotations = null; 
		List<ClassOrInterfaceType> typeBound = null;
		Map<String, Object> constr = null;
	    
	    List<Integer> subModifiers = null;
	    Map<String, Object> field = null;
	    Map<String, Object> method = null;
	    List<Expression> args = null;
	    List<VariableDeclarator> variables = null;
	    List<AnnotationExpr> annotations = null;
		    
		    // set imports
		    List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
		    imports.add(new ImportDeclaration(new NameExpr("java.sql.Connection"), false, false));
			imports.add(new ImportDeclaration(new NameExpr("java.sql.SQLException"), false, false));
			imports.add(new ImportDeclaration(new NameExpr("java.util.HashMap"), true, false));
			imports.add(new ImportDeclaration(new NameExpr("java.util.Map"), false, false));
			imports.add(new ImportDeclaration(new NameExpr("java.util.Properties"), false, false));
		
			//set type sub modifiers
			List<Integer> typeSubModifiers = astCodeHelper.createSubModifiers(ModifierSet.ABSTRACT, ModifierSet.STATIC);
			
		    // create annotation
		    List<AnnotationExpr> typeAnnotations = new ArrayList<AnnotationExpr>(); 
		    //Mark 
		    astCodeHelper.addAnnotation(typeAnnotations, "Service");
		    //Single
		    astCodeHelper.addAnnotation(typeAnnotations, "XStreamAlias", "CommonDTO");
		    //Normal
		    Map<String, Object> pairsMap = new LinkedHashMap<String, Object>();
		    pairsMap.put("init", null);
		    pairsMap.put("RefExpr", IOperateCode.STR_ASTERISK);
		    pairsMap.put("StringExpr", "isStringValue");
		    pairsMap.put("BooleanExpr", true);
		    pairsMap.put("IntergerExpr", 100);
		    pairsMap.put("LongExpr", 1000L);
		    pairsMap.put("DoubleExpr", 1.0001D);
		    pairsMap.put("CharExpr", "T".charAt(0));
		    pairsMap.put("ByteExpr", Byte.parseByte("0123"));
		    pairsMap.put("ShortExpr", Short.parseShort("25"));
		    pairsMap.put("FloatExpr", Float.parseFloat("2525"));
		    pairsMap.put("ClassExpr", Object.class);
		    pairsMap.put("AnnoExpr", Fields.class);
		    pairsMap.put("ByteArrayExpr", "Test".getBytes());
		    pairsMap.put("ArrayExpr", new Object[]{"test","abc", new NameExpr("IOperateCode.STR_ASTERISK")});
		    pairsMap.put("PrimitiveExpr", 1);
		    pairsMap.put("NameExpr1", new NameExpr("java.sql.Connection"));
		    pairsMap.put("NameExpr2", new NameExpr("IOperateCode.STR_ASTERISK"));
		    pairsMap.put("NameExpr3", new NameExpr("Fields.class"));
		    pairsMap.put("ListExpr", astCodeHelper.createNameExprs("Step1.class", "Step2.class"));
		    
		    astCodeHelper.addAnnotation(typeAnnotations, "DataTransferObject", pairsMap);
		    
		    //create type parameters
	    	typeParameters = astCodeHelper.createTypeParameters("K", "Map", "Object");
	    	typeParameters = astCodeHelper.createTypeParameters(typeParameters, "A", "Map", "Object");
	    	
		    // create extends
		    List<ClassOrInterfaceType> extendsList = astCodeHelper.createExtends("TestExtends1", "TestExtends2");
		    
		    // create implements
		    List<ClassOrInterfaceType> implementsList = astCodeHelper.createImplements("ImplementsType1", "ImplementsType2");
		    
		    // create constructor
		    List<Map<String, Object>> constructors = new ArrayList<Map<String, Object>>();
		    	
		    	// create constructor 1
			    constr = new HashMap<String, Object>();
			    
				    // constructor throws
				    throwses = astCodeHelper.createNameExprs("Exception", "ValidateException");
				    
				    // constructor parameters
				    memberParameters = new ArrayList<MemberHelper>();
				    memberParameters.add(new MemberHelper("String", 0, "argTest1"));
				    memberParameters.add(new MemberHelper("CommonDTO", 1, "argTest2"));
				    
				    // constructor typeParameters
				    memberTypeParameters = astCodeHelper.createTypeParameters("A", "Integer");
				    
				    // constructor annotations
				    memberAnnotations = new ArrayList<AnnotationExpr>();
				    astCodeHelper.addAnnotation(memberAnnotations, "Service"); //Mark

				    
				    
			    constr.put("modifiers", ModifierSet.PUBLIC);
			    constr.put("throws", throwses); //List<NameExpr>
			    constr.put("parameters", memberParameters); //List<MemberHelper>
			    constr.put("typeParameters", memberTypeParameters); //List<TypeParameter>
			    constr.put("annotations", memberAnnotations); //List<AnnotationExpr>
			    
			    constructors.add(constr);
		    
		    	// create constructor 2
			    constr = new HashMap<String, Object>();
			    
				    // constructor throws
				    throwses = new ArrayList<NameExpr>();
				    throwses.add(new NameExpr("SQLException"));
				    
				    // constructor parameters
				    memberParameters = new ArrayList<MemberHelper>();
				    memberParameters.add(new MemberHelper("String", 0, "argTest1"));
				    
				    // constructor typeParameters
				    memberTypeParameters = astCodeHelper.createTypeParameters("A", "Integer");
				    
				    // constructor annotations
				    memberAnnotations = new ArrayList<AnnotationExpr>();
				    astCodeHelper.addAnnotation(memberAnnotations, "DataTransferObject"); //Mark
				    
			    constr.put("modifiers", ModifierSet.PUBLIC);
			    constr.put("throws", throwses); //List<NameExpr>
			    constr.put("parameters", memberParameters); //List<MemberHelper>
			    constr.put("typeParameters", memberTypeParameters); //List<TypeParameter>
			    constr.put("annotations", memberAnnotations); //List<AnnotationExpr>
			    
			    constructors.add(constr);
			    

		    
		    // create field
		    List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
		    	// create field 1
	    		field = new HashMap<String, Object>();
			    
	    			field.put("type", "java.util.List<com.universal.system.dto.DataDTO>");
				    
				    variables = new ArrayList<VariableDeclarator>();
				    
				    variables.add(new VariableDeclarator(new VariableDeclaratorId("testList"), new NullLiteralExpr()));
				    
				    args = new ArrayList<Expression>();
				    args.add(new NameExpr("Arrays.asList(testArry)"));
				    
				    variables.add(new VariableDeclarator(
				    		new VariableDeclaratorId("stringList"), 
				    		new ObjectCreationExpr(null, new ClassOrInterfaceType("java.util.ArrayList<String>"), args)
				    	)
				    );
				    variables.add(new VariableDeclarator(
				    		new VariableDeclaratorId("stringArray"), 
				    		new ObjectCreationExpr(null, new ClassOrInterfaceType("java.util.ArrayList<String>"), args)
				    	)
				    );
				    field.put("variables", variables);
				    
				    field.put("modifiers", ModifierSet.PRIVATE);
				    
				    subModifiers = new ArrayList<Integer>();
				    subModifiers.add(ModifierSet.STATIC);
				    field.put("subModifiers", subModifiers);
			    
			    fields.add(field);

		    	// create field 2
				field = new HashMap<String, Object>();
			
					annotations = new ArrayList<AnnotationExpr>();
					astCodeHelper.addAnnotation(annotations, "Autowired"); //Mark
					
				    field.put("annotations", annotations);
				    
				    field.put("type", "Map<String, Object>");
				    
				    variables = new ArrayList<VariableDeclarator>();
				    //variables.add(new VariableDeclarator(new VariableDeclaratorId("testList"), new NullLiteralExpr()));
				    args = new ArrayList<Expression>();
				    args.add(new NameExpr("new HashMap<String, Object>()"));
				    
				    variables.add(new VariableDeclarator(
				    		new VariableDeclaratorId("intList"), 
				    		new ObjectCreationExpr(null, new ClassOrInterfaceType("java.util.HashMap<String, Object>"), args)
				    	)
				    );
				    field.put("variables", variables);
				    
				    field.put("modifiers", ModifierSet.PRIVATE);
				    			    
				    subModifiers = new ArrayList<Integer>();
				    subModifiers.add(ModifierSet.STATIC);
				    field.put("subModifiers", subModifiers);
			    
			    fields.add(field);
		    
		    	// create field 3
				field = new HashMap<String, Object>();
			
					annotations = new ArrayList<AnnotationExpr>();
					astCodeHelper.addAnnotation(annotations, "Autowired"); //Mark
					
				    field.put("annotations", annotations);
				    
				    field.put("type", "TestService");
				    
				    variables = new ArrayList<VariableDeclarator>();
				    
				    variables.add(new VariableDeclarator( new VariableDeclaratorId("testService") ));
				    
				    field.put("variables", variables);
				    
				    field.put("modifiers", ModifierSet.PRIVATE);
			    
			    fields.add(field);
			    
		    // create method
		    List<Map<String, Object>> methods = new ArrayList<Map<String, Object>>();
		    	// create method 1
		    	method = new HashMap<String, Object>();
			    
			    	annotations = new ArrayList<AnnotationExpr>();
			    	astCodeHelper.addAnnotation(annotations, "Transactional"); //Mark
				    method.put("annotations", annotations);
				    
			    	method.put("modifiers", ModifierSet.PUBLIC);
			    	
			    	subModifiers = astCodeHelper.createSubModifiers(ModifierSet.STATIC, ModifierSet.FINAL);
				    method.put("subModifiers", subModifiers);
				    
				    method.put("name", "removeAll");
				    
				    method.put("returnType", "void");
				    
				    memberParameters = new ArrayList<MemberHelper>();
				    memberParameters.add(new MemberHelper("boolean", 0, "findCheck"));
				    method.put("parameters", memberParameters);
				    
				    throwses = astCodeHelper.createNameExprs("SQLException");
				    method.put("throws", throwses);
				    
				    memberTypeParameters = astCodeHelper.createTypeParameters("K", "String");
				    
				    method.put("typeParameters", memberTypeParameters);
				    
		    	methods.add(method);
		    	
		    	// create method 2
		    	method = new HashMap<String, Object>();
			    
		    	annotations = new ArrayList<AnnotationExpr>();
		    	astCodeHelper.addAnnotation(annotations, "Transactional"); //Mark
			    
			    method.put("annotations", annotations);
			    
		    	method.put("modifiers", ModifierSet.PUBLIC);
		    	
		    	subModifiers = new ArrayList<Integer>();
			    subModifiers.add(ModifierSet.STATIC);
			    subModifiers.add(ModifierSet.FINAL);
		    	
			    method.put("subModifiers", subModifiers);
			    
			    method.put("name", "findTag");
			    
			    method.put("returnType", "String");
			    
			    memberParameters = new ArrayList<MemberHelper>();
			    memberParameters.add(new MemberHelper("List<String>", 0, "stringList"));
			    memberParameters.add(new MemberHelper("CommonDTO", 0, "common"));
			    memberParameters.add(new MemberHelper("boolean", 0, "findCheck"));
			    method.put("parameters", memberParameters);
			    
			    throwses = astCodeHelper.createNameExprs("SQLException"); 
			    method.put("throws", throwses);
			    
			    memberTypeParameters = astCodeHelper.createTypeParameters("V", "Object", "String");
			    
			    method.put("typeParameters", memberTypeParameters);
			    
	    	methods.add(method);
		    
			List<ASTCodeDTO> astCode = new ArrayList<ASTCodeDTO>();
			astCode.add(getChildren());
			
			
			Map<String, Object> type = new HashMap<String, Object>();
			type.put("modifiers", ModifierSet.PUBLIC);
			type.put("subModifiers", typeSubModifiers);
			type.put("annotations", typeAnnotations);
			type.put("extends", extendsList);
			type.put("implements", implementsList);
			type.put("typeParameters", typeParameters);
			type.put("constructors", constructors);
			type.put("fields", fields);
			type.put("methods", methods);
			type.put("innerTypes", astCode);
			
			
				ASTCodeDTO astCodeDTO = new ASTCodeDTO();
				astCodeDTO.setImports(imports);
				astCodeDTO.setFileDir("c:\\code\\generate\\ast\\");
				astCodeDTO.setTypeName("ASTCodeTest");
				astCodeDTO.setPackages("code.generate.ast.");
				astCodeDTO.setType(type);
				
			CompilationUnit cu =  astCodeGenerator.generate(astCodeDTO);
			out(cu.toString());
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private ASTCodeDTO getChildren() {
		
		//common
		MarkerAnnotationExpr markerAnno = null;
		SingleMemberAnnotationExpr singleAnno = null;
		NormalAnnotationExpr normalAnno = null;
		List<MemberHelper> memberParameters = null;
		List<NameExpr> throwses = null;
		List<TypeParameter> typeParameters = null;
		List<TypeParameter> memberTypeParameters = null;
		List<AnnotationExpr> memberAnnotations = null; 
		List<ClassOrInterfaceType> typeBound = null;
		Map<String, Object> constr = null;
				
			    List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
				    imports.add(new ImportDeclaration(new NameExpr("inner.java.sql.Connection"), false, false));
					imports.add(new ImportDeclaration(new NameExpr("inner.java.sql.SQLException"), false, false));
					imports.add(new ImportDeclaration(new NameExpr("inner.java.sql.SQLException"), false, false));
					imports.add(new ImportDeclaration(new NameExpr("inner.java.sql.SQLException"), false, false));
					imports.add(new ImportDeclaration(new NameExpr("inner.java.util.HashMap"), true, false));
					imports.add(new ImportDeclaration(new NameExpr("inner.java.util.Map"), false, false));
					imports.add(new ImportDeclaration(new NameExpr("inner.java.util.Properties"), false, false));
				
				List<Integer> typeSubModifiers = new ArrayList<Integer>();
				typeSubModifiers.add(ModifierSet.ABSTRACT);
				typeSubModifiers.add(ModifierSet.STATIC);
			    
			    // create annotation
			    List<AnnotationExpr> typeAnnotations = new ArrayList<AnnotationExpr>(); 
				    
				    markerAnno = new MarkerAnnotationExpr(new NameExpr("Service"));
			    typeAnnotations.add(markerAnno);
				    
				    singleAnno = new SingleMemberAnnotationExpr();
				    singleAnno.setName(new NameExpr("XStreamAlias"));
				    singleAnno.setMemberValue(new StringLiteralExpr("CommonDTO"));
			    typeAnnotations.add(singleAnno);

			    	normalAnno = new NormalAnnotationExpr();
				    normalAnno.setName(new NameExpr("DataTransferObject"));
				    List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
				    pairs.add(new MemberValuePair("description", new StringLiteralExpr("DTO 공통확장 클래스")));
				    pairs.add(new MemberValuePair("modelNames", new StringLiteralExpr("모델 타입")));
				    normalAnno.setPairs(pairs);
			    typeAnnotations.add(normalAnno);
			    
		    		typeParameters = new ArrayList<TypeParameter>();
		    	
				    typeBound = new ArrayList<ClassOrInterfaceType>();
				    typeBound.add(new ClassOrInterfaceType("Map"));
				    typeBound.add(new ClassOrInterfaceType("Object"));
			    typeParameters.add(new TypeParameter("K", typeBound));
			    
			    // create extends
			    List<ClassOrInterfaceType> extendsList = new ArrayList<ClassOrInterfaceType>();
				    extendsList.add(new ClassOrInterfaceType("TestExtends1"));
				    extendsList.add(new ClassOrInterfaceType("TestExtends2"));
			    
			    // create implements
			    List<ClassOrInterfaceType>  implementsList = new ArrayList<ClassOrInterfaceType>();
				    implementsList.add(new ClassOrInterfaceType("TestInterface1"));
				    implementsList.add(new ClassOrInterfaceType("TestInterface2"));
			    
			    // create constructor
			    List<Map<String, Object>> constructors = new ArrayList<Map<String, Object>>();
			    	
			    	// create constructor 1
				    constr = new HashMap<String, Object>();
				    
					    // constructor throws
					    throwses = new ArrayList<NameExpr>();
					    throwses.add(new NameExpr("Exception"));
					    throwses.add(new NameExpr("ValidateException"));
					    
					    // constructor parameters
					    memberParameters = new ArrayList<MemberHelper>();
					    memberParameters.add(new MemberHelper("String", 0, "argTest1"));
					    memberParameters.add(new MemberHelper("CommonDTO", 1, "argTest2"));
					    
					    // constructor typeParameters
					    memberTypeParameters = new ArrayList<TypeParameter>();
					    typeBound = new ArrayList<ClassOrInterfaceType>();
					    typeBound.add(new ClassOrInterfaceType("Integer"));
					    memberTypeParameters.add(new TypeParameter("A", typeBound));
					    
					    // constructor annotations
					    memberAnnotations = new ArrayList<AnnotationExpr>();
					    markerAnno = new MarkerAnnotationExpr(new NameExpr("Service"));
					    memberAnnotations.add(markerAnno);
				    
				    constr.put("modifiers", ModifierSet.PUBLIC);
				    constr.put("throws", throwses); //List<NameExpr>
				    constr.put("parameters", memberParameters); //List<MemberHelper>
				    constr.put("typeParameters", memberTypeParameters); //List<TypeParameter>
				    constr.put("annotations", memberAnnotations); //List<AnnotationExpr>
				    
				    constructors.add(constr);
			    
			    	// create constructor 2
				    constr = new HashMap<String, Object>();
				    
					    // constructor throws
					    throwses = new ArrayList<NameExpr>();
					    throwses.add(new NameExpr("SQLException"));
					    
					    // constructor parameters
					    memberParameters = new ArrayList<MemberHelper>();
					    memberParameters.add(new MemberHelper("String", 0, "argTest1"));
					    
					    // constructor typeParameters
					    memberTypeParameters = new ArrayList<TypeParameter>();
					    typeBound = new ArrayList<ClassOrInterfaceType>();
					    typeBound.add(new ClassOrInterfaceType("Integer"));
					    memberTypeParameters.add(new TypeParameter("A", typeBound));
					    
					    // constructor annotations
					    memberAnnotations = new ArrayList<AnnotationExpr>();
					    markerAnno = new MarkerAnnotationExpr(new NameExpr("DataTransferObject"));
					    memberAnnotations.add(markerAnno);
				    
				    constr.put("modifiers", ModifierSet.PUBLIC);
				    constr.put("throws", throwses); //List<NameExpr>
				    constr.put("parameters", memberParameters); //List<MemberHelper>
				    constr.put("typeParameters", memberTypeParameters); //List<TypeParameter>
				    constr.put("annotations", memberAnnotations); //List<AnnotationExpr>
				    
				    constructors.add(constr);
				    
			    //common
			    List<Integer> subModifiers = null;
			    Map<String, Object> field = null;
			    Map<String, Object> method = null;
			    List<Expression> args = null;
			    List<VariableDeclarator> variables = null;
			    List<AnnotationExpr> annotations = null;
			    
			    // create field
			    List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
			    	// create field 1
		    		field = new HashMap<String, Object>();
				    
		    			field.put("type", "List<String>");
					    
					    variables = new ArrayList<VariableDeclarator>();
					    
					    variables.add(new VariableDeclarator(new VariableDeclaratorId("testList"), new NullLiteralExpr()));
					    
					    args = new ArrayList<Expression>();
					    args.add(new NameExpr("Arrays.asList(testArry)"));
					    
					    variables.add(new VariableDeclarator(
					    		new VariableDeclaratorId("stringList"), 
					    		new ObjectCreationExpr(null, new ClassOrInterfaceType("java.util.ArrayList<String>"), args)
					    	)
					    );
					    variables.add(new VariableDeclarator(
					    		new VariableDeclaratorId("stringArray"), 
					    		new ObjectCreationExpr(null, new ClassOrInterfaceType("java.util.ArrayList<String>"), args)
					    	)
					    );
					    field.put("variables", variables);
					    
					    field.put("modifiers", ModifierSet.PRIVATE);
					    
					    subModifiers = new ArrayList<Integer>();
					    subModifiers.add(ModifierSet.STATIC);
					    field.put("subModifiers", subModifiers);
				    
				    fields.add(field);

			    	// create field 2
					field = new HashMap<String, Object>();
				
						annotations = new ArrayList<AnnotationExpr>();
					    markerAnno = new MarkerAnnotationExpr(new NameExpr("Autowired"));
					    annotations.add(markerAnno);
					    
					    field.put("annotations", annotations);
					    
					    field.put("type", "List<String>");
					    
					    variables = new ArrayList<VariableDeclarator>();
					    //variables.add(new VariableDeclarator(new VariableDeclaratorId("testList"), new NullLiteralExpr()));
					    args = new ArrayList<Expression>();
					    args.add(new NameExpr("Arrays.asList(testArry)"));
					    
					    variables.add(new VariableDeclarator(
					    		new VariableDeclaratorId("intList"), 
					    		new ObjectCreationExpr(null, new ClassOrInterfaceType("java.util.ArrayList<Integer>"), args)
					    	)
					    );
					    field.put("variables", variables);
					    
					    field.put("modifiers", ModifierSet.PRIVATE);
					    			    
					    subModifiers = new ArrayList<Integer>();
					    subModifiers.add(ModifierSet.STATIC);
					    field.put("subModifiers", subModifiers);
				    
				    fields.add(field);
			    
			    	// create field 3
					field = new HashMap<String, Object>();
				
						annotations = new ArrayList<AnnotationExpr>();
					    markerAnno = new MarkerAnnotationExpr(new NameExpr("Autowired"));
					    annotations.add(markerAnno);
					    
					    field.put("annotations", annotations);
					    
					    field.put("type", "TestService");
					    
					    variables = new ArrayList<VariableDeclarator>();
					    
					    variables.add(new VariableDeclarator( new VariableDeclaratorId("testService") ));
					    
					    field.put("variables", variables);
					    
					    field.put("modifiers", ModifierSet.PRIVATE);
				    
				    fields.add(field);
				    
			    // create method
			    List<Map<String, Object>> methods = new ArrayList<Map<String, Object>>();
			    	// create method 1
			    	method = new HashMap<String, Object>();
				    
				    	annotations = new ArrayList<AnnotationExpr>();
					    markerAnno = new MarkerAnnotationExpr(new NameExpr("Transactional"));
					    annotations.add(markerAnno);
					    
					    method.put("annotations", annotations);
					    
				    	method.put("modifiers", ModifierSet.PUBLIC);
				    	
				    	subModifiers = new ArrayList<Integer>();
					    subModifiers.add(ModifierSet.STATIC);
					    subModifiers.add(ModifierSet.FINAL);
				    	
					    method.put("subModifiers", subModifiers);
					    
					    method.put("name", "removeAll");
					    
					    method.put("returnType", "void");
					    
					    memberParameters = new ArrayList<MemberHelper>();
					    memberParameters.add(new MemberHelper("boolean", 0, "findCheck"));
					    method.put("parameters", memberParameters);
					    
					    throwses = new ArrayList<NameExpr>();
					    throwses.add(new NameExpr("SQLException"));
					    method.put("throws", throwses);
					    
					    memberTypeParameters = new ArrayList<TypeParameter>();
					    typeBound = new ArrayList<ClassOrInterfaceType>();
					    typeBound.add(new ClassOrInterfaceType("String"));
					    memberTypeParameters.add(new TypeParameter("K", typeBound));
					    
					    method.put("typeParameters", memberTypeParameters);
					    
			    	methods.add(method);
			    	
			    	// create method 2
			    	method = new HashMap<String, Object>();
				    
			    	annotations = new ArrayList<AnnotationExpr>();
				    markerAnno = new MarkerAnnotationExpr(new NameExpr("Transactional"));
				    annotations.add(markerAnno);
				    
				    method.put("annotations", annotations);
				    
			    	method.put("modifiers", ModifierSet.PUBLIC);
			    	
			    	subModifiers = new ArrayList<Integer>();
				    subModifiers.add(ModifierSet.STATIC);
				    subModifiers.add(ModifierSet.FINAL);
			    	
				    method.put("subModifiers", subModifiers);
				    
				    method.put("name", "findTag");
				    
				    method.put("returnType", "String");
				    
				    memberParameters = new ArrayList<MemberHelper>();
				    memberParameters.add(new MemberHelper("List<String>", 0, "stringList"));
				    memberParameters.add(new MemberHelper("CommonDTO", 0, "common"));
				    memberParameters.add(new MemberHelper("boolean", 0, "findCheck"));
				    method.put("parameters", memberParameters);
				    
				    throwses = new ArrayList<NameExpr>();
				    throwses.add(new NameExpr("SQLException"));
				    method.put("throws", throwses);
				    
				    memberTypeParameters = new ArrayList<TypeParameter>();
				    typeBound = new ArrayList<ClassOrInterfaceType>();
				    typeBound.add(new ClassOrInterfaceType("Object"));
				    typeBound.add(new ClassOrInterfaceType("String"));
				    memberTypeParameters.add(new TypeParameter("V", typeBound));
				    
				    method.put("typeParameters", memberTypeParameters);
				    
		    	methods.add(method);
			    
				Map<String, Object> type = new HashMap<String, Object>();
				type.put("modifiers", ModifierSet.PUBLIC);
				type.put("subModifiers", typeSubModifiers);
				type.put("annotations", typeAnnotations);
				type.put("extends", extendsList);
				type.put("implements", implementsList);
				type.put("typeParameters", typeParameters);
				type.put("constructors", constructors);
				type.put("fields", fields);
				type.put("methods", methods);
				
				
					ASTCodeDTO astCodeDTO = new ASTCodeDTO();
					astCodeDTO.setImports(imports);
					astCodeDTO.setFileDir("c:\\code\\generate\\ast\\");
					astCodeDTO.setTypeName("InnerASTCodeTest");
					astCodeDTO.setPackages("code.generate.ast.");
					astCodeDTO.setType(type);
					
		return astCodeDTO;
	}
	
	//@Test
	public void main() throws Exception {
	    // creates an input stream for the file to be parsed
 
	    if(true) {
		    	
			FileInputStream in = new FileInputStream(path);
	
		    CompilationUnit cu;
		    try {
		        // parse the file
		        cu = JavaParser.parse(in);
		    } finally {
		        in.close();
		    }
	
		    /*
		    //extract start
		    out("package : " + cu.getPackage().getName());
		    
		    for(ImportDeclaration imports : cu.getImports()) {
		    	out("import : " + imports.getName());	
		    }
		    
		    
		    ClassOrInterfaceDeclaration root = null;
		    List<TypeDeclaration> types = cu.getTypes();
		    for (TypeDeclaration type : types) {
		    	
		    	root = (ClassOrInterfaceDeclaration) type;
		    	out("***Root Type***");
		    	out("*getComment : " + root.getComment());
		    	for(AnnotationExpr annoExpr : root.getAnnotations()) {
		    		out("*getAnnotations : " + annoExpr.getChildrenNodes());
		    		
		    		for(Node node : annoExpr.getChildrenNodes()){
		    			out("*getAnnotations : " + node);
		    		}
		    		
		    	}
		    	
		    	out("*getModifiers : " + root.getModifiers());
		    	out("*getName : " + root.getName());
		    	out("*getNameExpr : " + root.getNameExpr());	  
		    	out("*getTypeParameters : " + root.getTypeParameters());
		    	out("*getExtends : " + root.getExtends());
		    	out("*getImplements : " + root.getImplements());
		    	
		        List<BodyDeclaration> members = type.getMembers();
		        for (BodyDeclaration member : members) {
		        	out("ClassName : " + member.getClass().getCanonicalName());
		        	out("Comment : " + member.getComment());
		        	out("Annotations : " + member.getAnnotations());
		        	out("Line : " + member.getBeginLine() + " ~ " + member.getEndLine());
		        	out("Data : " + member.getData());
	//	            if (member instanceof MethodDeclaration) {
	//	                MethodDeclaration method = (MethodDeclaration) member;
	//	                changeMethod(method);
	//	            }
		        }
		    }
		     */
	    
		    /*
			// visit and change the methods names and parameters
		    new MethodChangerVisitor().visit(cu, null);
		    System.out.println(" STEP 111111111111111111111 ");
			// out(cu.toString());
			new MethodVisitor().visit(cu, null);
			System.out.println(" STEP 222222222222222222222 ");
			*/
		    

	    }

	}


	
	private static class MethodVisitor extends VoidVisitorAdapter<Object> {

		@Override
		public void visit(MethodDeclaration n, Object arg) {
			// here you can access the attributes of the method.
			// this method will be called for all methods in this
			// CompilationUnit, including inner class methods
			out("MethodDeclaration :\n" + n);
			super.visit(n, arg);
		}
		
		@Override
		public void visit(Parameter n, Object arg) {
			// TODO Auto-generated method stub
			out("Parameter :" + n);
			super.visit(n, arg);
		}

		@Override
		public void visit(ExpressionStmt n, Object arg) {
			out("ExpressionStmt :" + n);
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodCallExpr n, Object arg) {
			out("MethodCallExpr :" + n);
			super.visit(n, arg);
		}


	}


	

	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes.
	 */
	private class MethodChangerVisitor extends VoidVisitorAdapter {

	    @Override
	    public void visit(MethodDeclaration n, Object arg) {
	        // change the name of the method to upper case
	        n.setName(n.getName().toUpperCase());

	        // create the new parameter
	        Parameter newArg = ASTHelper.createParameter(ASTHelper.INT_TYPE, "value");

	        // add the parameter to the method
	        ASTHelper.addParameter(n, newArg);
	    }

	}
	
	
	private void changeMethods(CompilationUnit cu) {
	    List<TypeDeclaration> types = cu.getTypes();
	    for (TypeDeclaration type : types) {
	        List<BodyDeclaration> members = type.getMembers();
	        for (BodyDeclaration member : members) {
	            if (member instanceof MethodDeclaration) {
	                MethodDeclaration method = (MethodDeclaration) member;
	                changeMethod(method);
	            }
	        }
	    }
	}

	private static void changeMethod(MethodDeclaration n) {
	    // change the name of the method to upper case
	    n.setName(n.getName().toUpperCase());

	    // create the new parameter
	    Parameter newArg = ASTHelper.createParameter(ASTHelper.INT_TYPE, "value");

	    // add the parameter to the method
	    ASTHelper.addParameter(n, newArg);
	}
	
	
	
	

	/**
	 * creates the compilation unit
	 */
	//@Test
	public void createCU() {
		ASTCodeGenerator astCodeGenerator = new ASTCodeGenerator();
		ASTCodeDTO astCodeDTO = new ASTCodeDTO();
		ASTCodeHelper astCodeHelper = new ASTCodeHelper();
		
		CompilationUnit cu = null;
		
		
		List<Integer> subModifiers = null;
		List<AnnotationExpr> annotations = null;
		List<ClassOrInterfaceType> extendsList = null;
		List<ClassOrInterfaceType> implementsList = null;
		List<VariableDeclarator> variables = null;
		List<MemberHelper> parameters = null;
		
		try {
			ASTCodeHelper astCodeMaker = new ASTCodeHelper();
			
		    List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
		    imports.add(new ImportDeclaration(new NameExpr("java.sql.Connection"), false, false));
			imports.add(new ImportDeclaration(new NameExpr("java.sql.SQLException"), false, false));
			imports.add(new ImportDeclaration(new NameExpr("java.util.HashMap"), false, false));
			imports.add(new ImportDeclaration(new NameExpr("java.util.Map"), false, false));
			imports.add(new ImportDeclaration(new NameExpr("java.util.Properties"), false, false));
			
		    cu = astCodeMaker.createCompilationUnit("com.universal.core.ast.google", imports);
		    
		    // create the type declaration
		    subModifiers = new ArrayList<Integer>();
		    subModifiers.add(ModifierSet.ABSTRACT);
		    subModifiers.add(ModifierSet.STATIC);
		    ClassOrInterfaceDeclaration classType = astCodeMaker.createClassOrInterfaceDeclaration(cu, ModifierSet.PUBLIC, subModifiers, false, "NewCreateJAVA");
		  
		    // create annotation
		    annotations = new ArrayList<AnnotationExpr>(); 
		    
		    MarkerAnnotationExpr markerAnno = new MarkerAnnotationExpr(new NameExpr("Service"));
		    annotations.add(markerAnno);
		    
		    SingleMemberAnnotationExpr singleAnno = new SingleMemberAnnotationExpr();
		    singleAnno.setName(new NameExpr("XStreamAlias"));
		    singleAnno.setMemberValue(new StringLiteralExpr("CommonDTO"));
		    annotations.add(singleAnno);

		    NormalAnnotationExpr normalAnno = new NormalAnnotationExpr();
		    normalAnno.setName(new NameExpr("DataTransferObject"));
		    List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
		    pairs.add(new MemberValuePair("description", new StringLiteralExpr("DTO 공통확장 클래스")));
		    pairs.add(new MemberValuePair("modelNames", new StringLiteralExpr("모델 타입")));
		    normalAnno.setPairs(pairs);
		    annotations.add(normalAnno);
		    
		    classType.setAnnotations(annotations);
		    
		    // create extends
		    extendsList = new ArrayList<ClassOrInterfaceType>();
		    extendsList.add(new ClassOrInterfaceType("TestExtends1"));
		    extendsList.add(new ClassOrInterfaceType("TestExtends2"));
		    classType.setExtends(extendsList);
		    
		    // create implements
		    implementsList = new ArrayList<ClassOrInterfaceType>();
		    implementsList.add(new ClassOrInterfaceType("TestInterface1"));
		    implementsList.add(new ClassOrInterfaceType("TestInterface2"));
		    classType.setImplements(implementsList);
		    
		    // create field
		    variables = new ArrayList<VariableDeclarator>();
		    //variables.add(new VariableDeclarator(new VariableDeclaratorId("testList"), new NullLiteralExpr()));
		    List<Expression> args = new ArrayList<Expression>();
		    args.add(new NameExpr("Arrays.asList(testArry)"));
		    variables.add(new VariableDeclarator(new VariableDeclaratorId("stringList"), new ObjectCreationExpr(null, new ClassOrInterfaceType("java.util.ArrayList<String>"), args)));
		    
		    subModifiers = new ArrayList<Integer>();
		    subModifiers.add(ModifierSet.STATIC);
		    FieldDeclaration filed = astCodeMaker.createFieldDeclaration(classType, ModifierSet.PUBLIC, subModifiers, new ClassOrInterfaceType("List<String>"), variables);

		    // create a method
		    subModifiers = new ArrayList<Integer>();
		    subModifiers.add(ModifierSet.STATIC);
		    subModifiers.add(ModifierSet.FINAL);
		    MethodDeclaration method = astCodeMaker.createMethodDeclaration(classType, ModifierSet.PUBLIC, subModifiers, new ClassOrInterfaceType("List"), "findData");
		    
		    // add a parameter to the method
		    parameters = new ArrayList<MemberHelper>();
		    parameters.add(new MemberHelper("List<String>", 0, "stringList"));
		    parameters.add(new MemberHelper("CommonDTO", 0, "common"));
		    parameters.add(new MemberHelper("boolean", 0, "findCheck"));
		    
		    astCodeMaker.addMethodParameter(method, parameters);
		    
		    // add a body to the method
		    BlockStmt block = astCodeMaker.createMethodBody(method);
		    
		    // add a statement do the method body
		    NameExpr clazz = new NameExpr("System");
		    FieldAccessExpr field = new FieldAccessExpr(clazz, "out");
		    MethodCallExpr call = new MethodCallExpr(field, "println");
		    
		    ASTHelper.addArgument(call, new StringLiteralExpr("Hello World!"));
		    ASTHelper.addArgument(call, new StringLiteralExpr("Goto Param"));
		    
		    ASTHelper.addStmt(block, call);

		    method = astCodeMaker.createMethodDeclaration(classType, ModifierSet.PUBLIC, subModifiers, ASTHelper.VOID_TYPE, "findDataII");
		    //logger.debug("cu : " + cu);
		    
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	    out(cu.toString());
	}
	
	
}
