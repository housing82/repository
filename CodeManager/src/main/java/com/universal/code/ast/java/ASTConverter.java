package com.universal.code.ast.java;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EmptyMemberDeclaration;
import japa.parser.ast.body.EmptyTypeDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.MultiTypeParameter;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.comments.BlockComment;
import japa.parser.ast.comments.JavadocComment;
import japa.parser.ast.comments.LineComment;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.exception.ApplicationException;

@Component
public class ASTConverter {
	
	private static final Logger logger = LoggerFactory.getLogger(ASTConverter.class);
	
	public static final String NODE_DISPLAY_NAME = "nodeDesc";
	public static final String NODE_DISPLAY_TYPE = "nodeType";


	List<Map<String, Object>> transferNode(List<Node> extractNode, List<Map<String, Object>> finalList, Node parentNode, int level){
		
    	try {

    		Map<String, Object> beans = null;
    		Map<String, Object> describe = null;
    		Map<String, Object> displayAttr = null;
    		
    		for(Node node : extractNode) {
    			if((level == 0 && (node.getParentNode() != null && node.getParentNode().getClass() == CompilationUnit.class))
    				|| (node.getParentNode() != null && node.getParentNode() == parentNode) ) {
    				
    				beans = new LinkedHashMap<String, Object>();
	    			displayAttr = new LinkedHashMap<String, Object>();
	    			

	    			
	    			
	    			/**
	    			 * Total 78 visit information convert
	    			 */
	    			if(ContinueStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "continue");
	    				displayAttr.put("id", ((ContinueStmt) node).getId());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(BreakStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "break");
	    				displayAttr.put("id", ((BreakStmt) node).getId());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(SwitchEntryStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((SwitchEntryStmt) node).getLabel());
	    				displayAttr.put("stmts", ((SwitchEntryStmt) node).getStmts());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);	    				
	    			}
	    			else if(SwitchStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "switch");
	    				displayAttr.put("selector", ((SwitchStmt) node).getSelector());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(AnnotationMemberDeclaration.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("modifiers", ((AnnotationMemberDeclaration) node).getModifiers());
	    				displayAttr.put("type", ((AnnotationMemberDeclaration) node).getType());
	    				displayAttr.put("name", ((AnnotationMemberDeclaration) node).getName());
	    				displayAttr.put("annotations", ((AnnotationMemberDeclaration) node).getAnnotations());
	    				displayAttr.put("defaultValue", ((AnnotationMemberDeclaration) node).getDefaultValue());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(AnnotationDeclaration.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("modifiers", ((AnnotationDeclaration) node).getModifiers());
	    				displayAttr.put("name", ((AnnotationDeclaration) node).getNameExpr());
	    				displayAttr.put("annotations", ((AnnotationDeclaration) node).getAnnotations());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(SuperExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "super");
	    				displayAttr.put("classExpr", ((SuperExpr) node).getClassExpr());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(SynchronizedStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "synchronized");
	    				displayAttr.put("expr", ((SynchronizedStmt) node).getExpr());
	    				//block 내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ConditionalExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((ConditionalExpr) node).getCondition());
	    				displayAttr.put("thenExpr", ((ConditionalExpr) node).getThenExpr());
	    				displayAttr.put("elseExpr", ((ConditionalExpr) node).getElseExpr());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(WildcardType.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "wildcard");
	    				displayAttr.put("extends", ((WildcardType) node).getExtends());
	    				displayAttr.put("super", ((WildcardType) node).getSuper());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ClassExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((ClassExpr) node).getType());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(SingleMemberAnnotationExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((SingleMemberAnnotationExpr) node).getName());
	    				displayAttr.put("memberValue", ((SingleMemberAnnotationExpr) node).getMemberValue());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ArrayInitializerExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((ArrayInitializerExpr) node).getValues());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ArrayAccessExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("index", ((ArrayAccessExpr) node).getIndex());
	    				displayAttr.put("name", ((ArrayAccessExpr) node).getName());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ForStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "for");
	    				displayAttr.put("update", ((ForStmt) node).getUpdate());
	    				displayAttr.put("compare", ((ForStmt) node).getCompare());
	    				displayAttr.put("init", ((ForStmt) node).getInit());
	    				//body ( 내용 ) 
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(WhileStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((WhileStmt) node).getCondition());
	    				//body ( 내용 ) 
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ArrayCreationExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((ArrayCreationExpr) node).getType());
	    				displayAttr.put("arrayCount", ((ArrayCreationExpr) node).getArrayCount());
	    				displayAttr.put("dimensions", ((ArrayCreationExpr) node).getDimensions());
	    				displayAttr.put("initializer", ((ArrayCreationExpr) node).getInitializer());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(BooleanLiteralExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((BooleanLiteralExpr) node).getValue());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(UnaryExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((UnaryExpr) node).getOperator());
	    				displayAttr.put("expr", ((UnaryExpr) node).getExpr());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);   				
	    			}
	    			else if(EnclosedExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((EnclosedExpr) node).getInner());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(CastExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("type", ((CastExpr) node).getType());
	    				displayAttr.put("name", ((CastExpr) node).getExpr());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(BinaryExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((BinaryExpr) node).getOperator());
	    				displayAttr.put("left", ((BinaryExpr) node).getLeft());
	    				displayAttr.put("right", ((BinaryExpr) node).getRight());
	
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(IfStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((IfStmt) node).getCondition().toString());
	    				//thenStmt ( 내용 )
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ForeachStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((ForeachStmt) node).getIterable().toString());
	    				displayAttr.put("variable", ((ForeachStmt) node).getVariable().toString());
	    				// body 내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(NullLiteralExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "null");
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(VoidType.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "void");
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ReturnStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((ReturnStmt) node).getExpr());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ObjectCreationExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("scope", ((ObjectCreationExpr) node).getScope());
	    				displayAttr.put("name", ((ObjectCreationExpr) node).getType());
	    				displayAttr.put("args", ((ObjectCreationExpr) node).getArgs());
	    				//anonymousClassBody ( 익명 클래스 바디 )
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ThrowStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((ThrowStmt) node).getExpr());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(MultiTypeParameter.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("annotations", ((MultiTypeParameter) node).getAnnotations());
	    				displayAttr.put("modifiers", ((MultiTypeParameter) node).getModifiers());
	    				displayAttr.put("types", ((MultiTypeParameter) node).getTypes());
	    				displayAttr.put("name", ((MultiTypeParameter) node).getId());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(CatchClause.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((CatchClause) node).getExcept());
	    				// catchBlock 익셉션처리내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(TryStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "try");
	    				// tryBlock try안의 내용 
	    				// catchs 익셉션처리내용
	    				// finallyBlock 파이날내용
	    				// resources ( 자바 7부터 ) catch 또는 finally 에서 다른 method등의  resource를 이용하여 종결또는 처리하는 행위안의 resources내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(VariableDeclarationExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("annotations", ((VariableDeclarationExpr) node).getAnnotations());
	    				displayAttr.put("modifiers", ((VariableDeclarationExpr) node).getModifiers());
	    				displayAttr.put("type", ((VariableDeclarationExpr) node).getType().toString());
	    				displayAttr.put("name", ((VariableDeclarationExpr) node).getVars());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(PrimitiveType.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((PrimitiveType) node).getType().toString());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ThisExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "this");
	    				displayAttr.put("classExpr", ((ThisExpr) node).getClassExpr());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(AssignExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((AssignExpr) node).getTarget());
	    				displayAttr.put("value", ((AssignExpr) node).getValue());
	    				displayAttr.put("operator", ((AssignExpr) node).getOperator());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(BlockStmt.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", "block");
	    				//stmts 내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(FieldAccessExpr.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("scope", ((FieldAccessExpr) node).getScope());
	    				displayAttr.put("name", ((FieldAccessExpr) node).getField());
	    				displayAttr.put("typeArgs", ((FieldAccessExpr) node).getTypeArgs());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(VariableDeclaratorId.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("name", ((VariableDeclaratorId) node).getName().toString());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(ClassOrInterfaceType.class.isAssignableFrom(node.getClass())) {
	    				displayAttr.put("scope", ((ClassOrInterfaceType) node).getScope());
	    				displayAttr.put("name", ((ClassOrInterfaceType) node).getName().toString());
	    				displayAttr.put("typeArgs", ((ClassOrInterfaceType) node).getTypeArgs());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if(TypeParameter.class.isAssignableFrom(node.getClass())){
	    				displayAttr.put("name", ((TypeParameter) node).getName().toString());
	    				displayAttr.put("typeBound", ((TypeParameter) node).getTypeBound());
	    				
						beans.put(NODE_DISPLAY_NAME, displayAttr);
	    			}
	    			else if (PackageDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((PackageDeclaration) node).getName().toString());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					} 
					else if (ImportDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("static", ((ImportDeclaration) node).isStatic());
						displayAttr.put("name", ((ImportDeclaration) node).getName().toString());
						displayAttr.put("asterisk", ((ImportDeclaration) node).isAsterisk());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					} 
					else if (ClassOrInterfaceDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("annotations", ((ClassOrInterfaceDeclaration) node).getAnnotations());
						displayAttr.put("modifiers", ((ClassOrInterfaceDeclaration) node).getModifiers());
						displayAttr.put("interface", ((ClassOrInterfaceDeclaration) node).isInterface());
						displayAttr.put("name", ((ClassOrInterfaceDeclaration) node).getName().toString());
						displayAttr.put("typeParameters", ((ClassOrInterfaceDeclaration) node).getTypeParameters());
						displayAttr.put("extends", ((ClassOrInterfaceDeclaration) node).getExtends());
						displayAttr.put("implements", ((ClassOrInterfaceDeclaration) node).getImplements());
						// comment 주석(javadoc)
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					} 
					else if (ConstructorDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("annotations", ((ConstructorDeclaration) node).getAnnotations());
						displayAttr.put("modifiers", ((ConstructorDeclaration) node).getModifiers());
						displayAttr.put("name", ((ConstructorDeclaration) node).getName().toString());
						displayAttr.put("typeParameters", ((ConstructorDeclaration) node).getTypeParameters());
						displayAttr.put("parameters", ((ConstructorDeclaration) node).getParameters());
						displayAttr.put("throws", ((ConstructorDeclaration) node).getThrows());
						// block 내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					} 
					else if (FieldDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("annotations", ((FieldDeclaration) node).getAnnotations());
						displayAttr.put("modifiers", ((FieldDeclaration) node).getModifiers());
						displayAttr.put("type", ((FieldDeclaration) node).getType());
						displayAttr.put("name", ((FieldDeclaration) node).getVariables());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					} 
					else if (InitializerDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("annotations", ((InitializerDeclaration) node).getAnnotations());
						displayAttr.put("static", ((InitializerDeclaration) node).isStatic());
						displayAttr.put("name", "initializer");
						// block 초기화코드 내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					} 
					else if (MethodDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("annotations", ((MethodDeclaration) node).getAnnotations());
						displayAttr.put("modifiers", ((MethodDeclaration) node).getModifiers());
						displayAttr.put("returnType", ((MethodDeclaration) node).getType());
						displayAttr.put("name", ((MethodDeclaration) node).getName().toString());
						displayAttr.put("typeParameters", ((MethodDeclaration) node).getTypeParameters());
						displayAttr.put("parameters", ((MethodDeclaration) node).getParameters());
						displayAttr.put("throws", ((MethodDeclaration) node).getThrows());
						// body 내용
						// comment 주석 (jd)
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					} 
					else if(VariableDeclarator.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((VariableDeclarator) node).getId());
						displayAttr.put("init", ((VariableDeclarator) node).getInit());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(ReferenceType.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((ReferenceType) node).getType());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(Parameter.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("type", ((Parameter) node).getType());
						displayAttr.put("name", ((Parameter) node).getId());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(ExpressionStmt.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((ExpressionStmt) node).getExpression());
						// comment 주석(jd)
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(MethodCallExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("scope", ((MethodCallExpr) node).getScope());
						displayAttr.put("name", ((MethodCallExpr) node).getNameExpr());
						displayAttr.put("args", ((MethodCallExpr) node).getArgs());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(QualifiedNameExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("qualifier", ((QualifiedNameExpr) node).getQualifier());
						displayAttr.put("name", ((QualifiedNameExpr) node).getName());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(NameExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((NameExpr) node).getName());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(MarkerAnnotationExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((MarkerAnnotationExpr) node).getName());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(NormalAnnotationExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((NormalAnnotationExpr) node).getName());
						displayAttr.put("pairs", ((NormalAnnotationExpr) node).getPairs());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(MemberValuePair.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((MemberValuePair) node).getName());
						displayAttr.put("value", ((MemberValuePair) node).getValue());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}	

					else if(DoubleLiteralExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((DoubleLiteralExpr) node).getValue());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(CharLiteralExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((CharLiteralExpr) node).getValue());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(IntegerLiteralExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((IntegerLiteralExpr) node).getValue());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(IntegerLiteralMinValueExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((IntegerLiteralMinValueExpr) node).getValue());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(LongLiteralExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((LongLiteralExpr) node).getValue());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(LongLiteralMinValueExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((LongLiteralMinValueExpr) node).getValue());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(StringLiteralExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((StringLiteralExpr) node).getValue());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(EnumDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("annotations", ((EnumDeclaration) node).getAnnotations());
						displayAttr.put("modifiers", ((EnumDeclaration) node).getModifiers());
						//displayAttr.put("nameExpr", ((EnumDeclaration) node).getNameExpr());
						displayAttr.put("name", ((EnumDeclaration) node).getName().toString());
						displayAttr.put("implements", ((EnumDeclaration) node).getImplements());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(EnumConstantDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("annotations", ((EnumConstantDeclaration) node).getAnnotations());
						displayAttr.put("name", ((EnumConstantDeclaration) node).getName());
						displayAttr.put("args", ((EnumConstantDeclaration) node).getArgs());
						//classBody 내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(JavadocComment.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", "javadoc");
						//Content 내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(LineComment.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", "lineComment");
						//Content 내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(BlockComment.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", "blockComment");
						//Content 내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(AssertStmt.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((AssertStmt) node).getCheck());
						displayAttr.put("message", ((AssertStmt) node).getMessage());

						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(DoStmt.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", "do");
						//body 내용
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(EmptyStmt.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", "empty");
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(ExplicitConstructorInvocationStmt.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("this", ((ExplicitConstructorInvocationStmt) node).isThis());
						displayAttr.put("name", ((ExplicitConstructorInvocationStmt) node).getExpr());
						displayAttr.put("args", ((ExplicitConstructorInvocationStmt) node).getArgs());
						displayAttr.put("typeArgs", ((ExplicitConstructorInvocationStmt) node).getTypeArgs());

						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(EmptyMemberDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", "emptyMember");
						displayAttr.put("annotations", ((EmptyMemberDeclaration) node).getAnnotations());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(EmptyTypeDeclaration.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("annotations", ((EmptyTypeDeclaration) node).getAnnotations());
						displayAttr.put("modifiers", ((EmptyTypeDeclaration) node).getModifiers());
						displayAttr.put("name", ((EmptyTypeDeclaration) node).getName());
						//displayAttr.put("nameExpr", ((EmptyTypeDeclaration) node).getNameExpr());

						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(InstanceOfExpr.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", ((InstanceOfExpr) node).getExpr());
						displayAttr.put("type", ((InstanceOfExpr) node).getType());

						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else if(TypeDeclarationStmt.class.isAssignableFrom(node.getClass())) {
						displayAttr.put("name", "typeDeclarationStmt");

						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					else {
						displayAttr.put("name", node.getClass().getSimpleName());
						
						beans.put(NODE_DISPLAY_NAME, displayAttr);
					}
					
	    			if(displayAttr.get("modifiers") != null) {
	    				int modifiers = (Integer) displayAttr.get("modifiers");
	    				displayAttr.put("abstract", ModifierSet.isAbstract(modifiers));
	    				displayAttr.put("final", ModifierSet.isFinal(modifiers));
	    				displayAttr.put("native", ModifierSet.isNative(modifiers));
	    				displayAttr.put("private", ModifierSet.isPrivate(modifiers));
	    				displayAttr.put("protected", ModifierSet.isProtected(modifiers));
	    				displayAttr.put("public", ModifierSet.isPublic(modifiers));
	    				displayAttr.put("static", ModifierSet.isStatic(modifiers));
	    				displayAttr.put("strictfp", ModifierSet.isStrictfp(modifiers));
	    				displayAttr.put("synchronized", ModifierSet.isSynchronized(modifiers));
	    				displayAttr.put("transient", ModifierSet.isTransient(modifiers));
	    				displayAttr.put("volatile", ModifierSet.isVolatile(modifiers));
	    			}
	    			
	    			beans.put(NODE_DISPLAY_TYPE, node.getClass().getSimpleName());
	    			beans.put("level", level);
	    			
	    			displayAttr.put("beginLine", node.getBeginLine());
	    			displayAttr.put("endLine", node.getEndLine());
	    			displayAttr.put("beginColumn", node.getBeginColumn());
	    			displayAttr.put("endColumn", node.getEndColumn());
	    			beans.put("childNodeSize", node.getChildrenNodes().size());
	    			
	    			/*
	    			describe = new LinkedHashMap<String, Object>(BeanUtils.describe(node));
	    			describe.remove("class");
	    			describe.remove("parentNode");
	    			describe.remove("childrenNodes");
	    			describe.remove("allContainedComments");
	    			beans.put("node", describe);
	    			*/	
	    			beans.put("node", node);
	    			
	    			finalList.add(beans);
	    			
	    			if(node.getChildrenNodes().size() > 0) {
	    				transferNode(extractNode, finalList, node, (level + 1));
	    			}
    			}
    		}
//		} catch (IllegalAccessException e) {
//			throw new RuntimeApplicationException(e);
//		} catch (InvocationTargetException e) {
//			throw new RuntimeApplicationException(e);
//		} catch (NoSuchMethodException e) {
//			throw new RuntimeApplicationException(e);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	    
	    return finalList;
	}
	
}
