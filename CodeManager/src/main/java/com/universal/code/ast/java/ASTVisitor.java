package com.universal.code.ast.java;

import japa.parser.JavaParser;
import japa.parser.ParseException;
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
import japa.parser.ast.stmt.LabeledStmt;
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
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.exception.ValidateException;
import com.universal.code.utils.PropertyUtil;

@Component
public class ASTVisitor {
	
	private static final Logger logger = LoggerFactory.getLogger(ASTVisitor.class);
	
	public static final int VISIT_FULL_NODE = 0;
	public static final int VISIT_JAVADOC_NODE = 1;
	public static final int VISIT_PACKAGE_NODE = 2;
	public static final int VISIT_IMPORT_NODE = 3;
	public static final int VISIT_CLASS_OR_INTERFACE_NODE = 4;
	public static final int VISIT_CONSTRUCTOR_NODE = 5;
	public static final int VISIT_FIELD_NODE = 6;
	public static final int VISIT_METHOD_NODE = 7;
	public static final int VISIT_INIT_AND_DECLARAT_NODE = 8;
	
	@Autowired
	private PropertyUtil property;
	
	@Autowired
	private ASTConverter astHelper;
	
	private List<Node> extractNode = null;
	
	private boolean verbose = false;
	
	private List<Map<String, Object>> transferNode(){
		if(astHelper == null) {
			astHelper = new ASTConverter();
		}
		List<Map<String, Object>> transferMapList = astHelper.transferNode(extractNode, new ArrayList<Map<String, Object>>(), null, 0);
		extractNode.clear();
		return transferMapList;
	}
	
	public List<Map<String, Object>> execute(String javaSource, String encoding, int visitType) {
		return execute(javaSource, encoding, visitType, false);
	}
	
	public List<Map<String, Object>> execute(String javaSource, String encoding, int visitType, boolean verbose) {
		ByteArrayInputStream in = null;
	    CompilationUnit cu = null;
	    
	    try {
	    	if(encoding == null) {
	    		encoding = IOperateCode.DEFAULT_ENCODING;
	    	}
	    	in=new ByteArrayInputStream(javaSource.getBytes(encoding));
	        // parse the file
			cu = JavaParser.parse(in, encoding);

		} catch (ParseException e) {
			throw new ApplicationException("자바 분석장애 발생",e);
	    } catch (UnsupportedEncodingException e) {
	    	throw new ApplicationException("지원하지 않는 인코딩", e);
		} finally {
	        try {
	        	if(in != null) in.close();
			} catch (IOException e) {
				throw new ApplicationException("자바 파일스트림종료 장애 발생", e);
			}
	    }
	    
	    return execute(visitType, cu, null, verbose);
	}
	
	public List<Map<String, Object>> execute(String javaPath, int visitType) {
		return execute(javaPath, visitType, false);
	}
	
	public List<Map<String, Object>> execute(String javaPath, int visitType, boolean verbose) {
		FileInputStream in = null;
	    CompilationUnit cu = null;
	    
	    try {
	    	in = new FileInputStream(javaPath);
	        // parse the file
			cu = JavaParser.parse(in);
	    } catch (FileNotFoundException e) {
			throw new ValidateException("자바 파일이 존재하지 않습니다.", e);
		} catch (ParseException e) {
			throw new ApplicationException("자바 분석장애 발생.",e);
	    } finally {
	        try {
	        	if(in != null) in.close();
			} catch (IOException e) {
				throw new ApplicationException("자바 파일스트림종료 장애 발생.", e);
			}
	    }
	    
	    return execute(visitType, cu, null, verbose);
	}
	
	public List<Map<String, Object>> execute(int visitType, CompilationUnit cu, Object arg) {
		return execute(visitType, cu, arg, false);
	}
	
	public List<Map<String, Object>> execute(int visitType, CompilationUnit cu, Object arg, boolean verbose) {
		if(logger.isDebugEnabled()) {
			//logger.debug("[START] execute ");
		}
		if(property == null) {
			property = new PropertyUtil();
		}
		
		List<Map<String, Object>> out = null;
		this.verbose = verbose;
		
		try {
			extractNode = new ArrayList<Node>();
			switch(visitType) {
				case VISIT_FULL_NODE:
					new FullVisitor().visit(cu, arg);
					break;
				case VISIT_JAVADOC_NODE:
					new JavadocVisitor().visit(cu, arg);
					break;
				case VISIT_PACKAGE_NODE:
					new PackagesVisitor().visit(cu, arg);
					break;
				case VISIT_IMPORT_NODE:
					new ImportsVisitor().visit(cu, arg);
					break;
				case VISIT_CLASS_OR_INTERFACE_NODE:
					new ClassOrInterfaceVisitor().visit(cu, arg);
					break;
				case VISIT_FIELD_NODE:
					new FieldsVisitor().visit(cu, arg);
					break;
				case VISIT_METHOD_NODE:
					new MethodsVisitor().visit(cu, arg);
					break;
				case VISIT_CONSTRUCTOR_NODE:
					new ConstructorVisitor().visit(cu, arg);
					break;
				case VISIT_INIT_AND_DECLARAT_NODE:
					new InitAndDeclarationVisitor().visit(cu, arg);
					break;
				default :
					throw new ValidateException("잘못된 VisitType입니다. 바인드된 VisitType : ".concat(Integer.toString(visitType)));
			}
			
			if(logger.isDebugEnabled()) {
				//logger.debug("[START] transferNode" );
			}
			out = transferNode();
			if(logger.isDebugEnabled()) {
				//logger.debug("[END] transferNode" );
			}
		}
		catch(Exception e){
			throw new ApplicationException("자바 분석장애 발생", e);
		}
		
		if(logger.isDebugEnabled()) {
			//logger.debug("[END] execute ");
		}
		return out;
	}
	
	private void addExtractNode(Node node){
		extractNode.add(node);
	}
	
	private class JavadocVisitor extends VoidVisitorAdapter<Object> {
		
		@Override
		public void visit(CompilationUnit n, Object arg) {
			//if(logger.isDebugEnabled() && verbose) {
			//	logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			//}
			super.visit(n, arg);
		}
		
		public void visit(JavadocComment n, Object arg) {
			if(logger.isDebugEnabled() && verbose) {
				//logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) ); //confirm
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			addExtractNode(n);
			super.visit(n, arg);
		}
	}
	
	private class PackagesVisitor extends VoidVisitorAdapter<Object> {
		
		
		
		@Override
		public void visit(CompilationUnit n, Object arg) {
			//if(logger.isDebugEnabled() && verbose) {
			//	logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			//}
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(PackageDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				//logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) ); //confirm
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			addExtractNode(n);
			super.visit(n, arg);
		}
		
	}
	
	private class ImportsVisitor extends VoidVisitorAdapter<Object> {
		
		@Override
		public void visit(CompilationUnit n, Object arg) {
			//if(logger.isDebugEnabled() && verbose) {
			//	logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			//}
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ImportDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				//logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) ); //confirm
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			addExtractNode(n);
			super.visit(n, arg);
		}
	}
	
	private class ClassOrInterfaceVisitor extends VoidVisitorAdapter<Object> {
		
		
		
		@Override
		public void visit(CompilationUnit n, Object arg) {
			//if(logger.isDebugEnabled() && verbose) {
			//	logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			//}
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(TypeParameter n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(EnumDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(MarkerAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);	
		}

		public void visit(NormalAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MemberValuePair n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(StringLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(IntegerLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(BooleanLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(CharLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(DoubleLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(LongLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(NullLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(WildcardType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		
	}

	private class FieldsVisitor extends VoidVisitorAdapter<Object> {
		
		
		
		@Override
		public void visit(CompilationUnit n, Object arg) {
			//if(logger.isDebugEnabled() && verbose) {
			//	logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			//}
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(TypeParameter n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(EnumDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EnumConstantDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MarkerAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);	
		}

		public void visit(NormalAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MemberValuePair n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(StringLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(VariableDeclaratorId n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(VariableDeclarator n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ClassOrInterfaceType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ReferenceType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(FieldDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(PrimitiveType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(IntegerLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(BooleanLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(CharLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(DoubleLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(LongLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(NullLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(NameExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(WildcardType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ClassExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
	}
	
	
	/**
	 * 패키지, 임포트, 클래스, 생성자, 필드, 메소드 정보 간소화 비지터
	 */
	private class InitAndDeclarationVisitor extends VoidVisitorAdapter<Object> {
		
		@Override
		public void visit(CompilationUnit n, Object arg) {
			//if(logger.isDebugEnabled() && verbose) {
			//	logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			//}
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(PackageDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				//logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) ); //confirm
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ImportDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				//logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) ); //confirm
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		
		public void visit(EnumDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EnumConstantDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		
		public void visit(FieldDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		

		public void visit(AnnotationDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}


		public void visit(EmptyMemberDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}


		public void visit(EmptyTypeDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}


		public void visit(MethodDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}


		public void visit(InitializerDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ConstructorDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
	}
	
	
	private class ConstructorVisitor extends VoidVisitorAdapter<Object> {
		
		
		
		@Override
		public void visit(CompilationUnit n, Object arg) {
			//if(logger.isDebugEnabled() && verbose) {
			//	logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			//}
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(TypeParameter n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(EnumDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(MarkerAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);	
		}

		public void visit(NormalAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MemberValuePair n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(StringLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(IntegerLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(BooleanLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(CharLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(DoubleLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(LongLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(NullLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(WildcardType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ConstructorDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(Parameter n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		
	}
	
	private class MethodsVisitor extends VoidVisitorAdapter<Object> {
		
		
		
		@Override
		public void visit(CompilationUnit n, Object arg) {
			//if(logger.isDebugEnabled() && verbose) {
			//	logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			//}
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(AnnotationDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(AnnotationMemberDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ArrayAccessExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ArrayCreationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ArrayInitializerExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(AssertStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(AssignExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(BinaryExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(BlockComment n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(BlockStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(BooleanLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(BreakStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(CastExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(CatchClause n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(CharLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ClassExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(TypeParameter n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ClassOrInterfaceType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ConditionalExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ContinueStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(DoStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(DoubleLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EmptyMemberDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EmptyStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EmptyTypeDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EnclosedExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EnumDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ExpressionStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(FieldAccessExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ForeachStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ForStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(IfStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(InstanceOfExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(IntegerLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(IntegerLiteralMinValueExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(LabeledStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(LineComment n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(LongLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(LongLiteralMinValueExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MarkerAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MemberValuePair n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MethodCallExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MethodDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(NameExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(NormalAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(NullLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ObjectCreationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(Parameter n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MultiTypeParameter n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(PrimitiveType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(QualifiedNameExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ReferenceType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ReturnStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(SingleMemberAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(StringLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(SuperExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(SwitchEntryStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(SwitchStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(SynchronizedStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ThisExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ThrowStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(TryStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(TypeDeclarationStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(UnaryExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(VariableDeclarationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(VariableDeclarator n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(VariableDeclaratorId n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(VoidType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(WhileStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(WildcardType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
	}
	
	private class FullVisitor extends VoidVisitorAdapter<Object> {
		
		
		
		@Override
		public void visit(CompilationUnit n, Object arg) {
			//if(logger.isDebugEnabled() && verbose) {
			//	logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			//}
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(AnnotationDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(AnnotationMemberDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ArrayAccessExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ArrayCreationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ArrayInitializerExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(AssertStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(AssignExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(BinaryExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(BlockComment n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(BlockStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(BooleanLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(BreakStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(CastExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(CatchClause n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(CharLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ClassExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(TypeParameter n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}
		
		public void visit(ClassOrInterfaceType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ConditionalExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ConstructorDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ContinueStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(DoStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(DoubleLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EmptyMemberDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EmptyStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EmptyTypeDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EnclosedExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EnumConstantDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(EnumDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ExpressionStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(FieldAccessExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(FieldDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ForeachStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ForStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(IfStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ImportDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(InitializerDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(InstanceOfExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(IntegerLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(IntegerLiteralMinValueExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(JavadocComment n, Object arg) {
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(LabeledStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(LineComment n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(LongLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(LongLiteralMinValueExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MarkerAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MemberValuePair n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MethodCallExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MethodDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(NameExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(NormalAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(NullLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ObjectCreationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(PackageDeclaration n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(Parameter n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(MultiTypeParameter n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(PrimitiveType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(QualifiedNameExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ReferenceType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ReturnStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(SingleMemberAnnotationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(StringLiteralExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(SuperExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(SwitchEntryStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(SwitchStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(SynchronizedStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ThisExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(ThrowStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(TryStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(TypeDeclarationStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(UnaryExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(VariableDeclarationExpr n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(VariableDeclarator n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(VariableDeclaratorId n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(VoidType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(WhileStmt n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

		public void visit(WildcardType n, Object arg) {
			if(!extractNode.contains(n.getParentNode())) return;
			if(logger.isDebugEnabled() && verbose) {
				logger.debug("Check parent is : " + extractNode.contains(n.getParentNode()));
				logger.debug(n.getClass().getSimpleName() + ", extract : " + property.out(n, false) );
			}
			
			addExtractNode(n);
			super.visit(n, arg);
		}

	}
}
