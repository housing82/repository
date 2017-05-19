package skeleton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.ast.java.ASTVisitor;
import com.universal.code.bxm.BxmBeanGenerateUtil;
import com.universal.code.bxm.BxmDBIOGenerateUtil;
import com.universal.code.bxm.BxmDBIOmmGenerateUtil;
import com.universal.code.utils.FileUtil;


public class GenerateRun {

	private static final Logger logger = LoggerFactory.getLogger(GenerateRun.class);
	
	private static Properties props = new Properties();

	private static String SOURCE_ROOT;
	
	private static String EXCEL_PATH;
	
	private ASTVisitor visitor;
	
	private FileUtil fileUtil;
	
	public GenerateRun() {
		visitor = new ASTVisitor();
		fileUtil = new FileUtil();
	}
	
	static {
		// 소스코드 생성대상 업무 DB접속정보
		props.setProperty("jdbc.driverClassName", "oracle.jdbc.driver.OracleDriver");
		props.setProperty("jdbc.url", "jdbc:oracle:thin:@localhost:1521:ora11g");
		props.setProperty("jdbc.username", "DESIGN");
		props.setProperty("jdbc.password", "DESIGN");
		props.setProperty("jdbc.initialSize", "5");
		props.setProperty("jdbc.maxActive", "100");
		props.setProperty("jdbc.maxIdle", "20");
		props.setProperty("jdbc.maxWait", "30000");
		props.setProperty("jdbc.poolPreparedStatements", "true");
		props.setProperty("jdbc.defaultAutoCommit", "false");
		props.setProperty("jdbc.validationQuery", "SELECT 1 FROM DUAL");
		
		// 소스 코드 생성 루트 경로
		SOURCE_ROOT = "C:/Developer/BXMWorkspace/HD-onl/src";
		
		// 프로그램 설계 엑셀 파일경로
		EXCEL_PATH = "N:/03.프로잭트/11.뱅크웨어/01.한국자산신탁/06.프로그램설계/한국자산신탁_분양임대_프로그램설계_ver.1.0.xlsx";
	}
	
	@Test
	public void doGenerate() throws Exception{
		
		//dbioOmmGenerate(); 
		
		//dbioGenerate();
		
		//bxmBeanGenerate(); 
		
		parseJava();
	}
	
	private static String JAVA_EXTENSION = ".java";

	@SuppressWarnings("unchecked")
	public void parseJava() throws Exception {
		
		String javaPath = null;
		File[] files = FileUtil.getFiles(SOURCE_ROOT.concat("/").concat("kait.hd.hda.onl.dao".replace(".", "/")));
		Map<String, Object> descMap = new HashMap<String, Object>();
		List<Map<String, Object>> elements = new ArrayList<Map<String, Object>>(); 
		
		for(File file : files){ 
			javaPath = file.getCanonicalPath();
			
			if(javaPath.endsWith(JAVA_EXTENSION)) {
				//logger.debug("javaPath: {}", javaPath);
				for(Map<String, Object> node : visitor.execute(javaPath, ASTVisitor.VISIT_METHOD_NODE, false)) {
					if(node.get("nodeType").equals("MethodDeclaration")) {
						descMap = (Map<String, Object>) node.get("nodeDesc");
						node.put("javaName", file.getName().subSequence(0, file.getName().length() - JAVA_EXTENSION.length()));
						//logger.debug("javaName: {}, methodName: {}, parameters: {}, returnType: {}", node.get("javaName"), descMap.get("name"), descMap.get("parameters"), descMap.get("returnType"));
						logger.debug("{}	{}", node.get("javaName"), descMap.get("name"));
						//elements.add(node);
					}
				}
				
			}
		}
		
		
	}
	
	public void dbioOmmGenerate(){ 
		
		BxmDBIOmmGenerateUtil ommGen = new BxmDBIOmmGenerateUtil();
		ommGen.setSourceRoot(SOURCE_ROOT);
		ommGen.setJavaPackage("kait.hd.hda.onl.dao.dto");
		ommGen.setDatabaseConfig(props);
		ommGen.setCreateFile(true);
		ommGen.setFileNamePrefix("D");
		ommGen.setFileNamePostfix("IO");
		ommGen.setInTables(null); // 특정 테이블의 OMM을 만들경우 설정한다 ex) '테이블01', '테이블02', '테이블03'
		ommGen.setFixedOmmTags(null); // 특정 필드를 모든 OMM에 적용할경우 ArrayList에 담아 설정한다 ex) 
									  // list.add("String txType<length=\"1\" description=\"트렌젝션 타입(CRUD)\"  >;")
									  // list.add("String recStat<length=\"1\" description=\"레코드 상태(O:정상,D:삭제)\"  >;")
		
		// create db table omm 
		ommGen.execute();
	}
	
	public void dbioGenerate(){ 
		
		BxmDBIOGenerateUtil dbioGen = new BxmDBIOGenerateUtil();
		dbioGen.setSourceRoot(SOURCE_ROOT);
		dbioGen.setJavaPackage("kait.hd.hda.onl.dao");
		dbioGen.setDatabaseConfig(props);
		dbioGen.setCreateFile(true);
		dbioGen.setDatasourceName("MainDS");
		dbioGen.setFileNamePrefix("D");
		
		// create db table dbio ( interface and mapper sql )
		dbioGen.execute();
	}

	public void bxmBeanGenerate() {
		
		BxmBeanGenerateUtil beanGen = new BxmBeanGenerateUtil();
		beanGen.setSourceRoot(SOURCE_ROOT);
		beanGen.setCreateFile(true);
		beanGen.setFileNamePrefix("B");
		beanGen.setExcelPath(EXCEL_PATH);
		
		// create bean with program design excel
		beanGen.execute();
	}
	
}
