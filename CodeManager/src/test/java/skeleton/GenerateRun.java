package skeleton;

import java.util.Properties;

import org.junit.Test;

import com.universal.code.bxm.BxmBeanGenerateUtil;
import com.universal.code.bxm.BxmDBIOGenerateUtil;
import com.universal.code.bxm.BxmDBIOmmGenerateUtil;

public class GenerateRun {

	private static Properties props = new Properties();

	private static String sourceRoot;
	
	private static String programDesignExcelPath;
	
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
		sourceRoot = "C:/Developer/BXMWorkspace/HD-onl/src";
		
		// 프로그램 설계 엑셀 파일경로
		programDesignExcelPath = "N:/03.프로잭트/11.뱅크웨어/01.한국자산신탁/06.프로그램설계/한국자산신탁_분양임대_프로그램설계_ver.1.0.xlsx";
	}
	
	@Test
	public void doGenerate(){
		
		// dbioOmmGenerate(); 
		
		// dbioGenerate();
		
		bxmBeanGenerate(); 
	}
	
	
	public void dbioGenerate(){ 
		
		BxmDBIOGenerateUtil dbioGen = new BxmDBIOGenerateUtil();
		dbioGen.setSourceRoot(sourceRoot);
		dbioGen.setJavaPackage("kait.hd.hda.onl.dao");
		dbioGen.setDatabaseConfig(props);
		dbioGen.setCreateFile(true);
		dbioGen.setDatasourceName("MainDS");
		
		// create db table dbio ( interface and mapper sql )
		dbioGen.execute("D");
	}

	
	public void dbioOmmGenerate(){ 
		
		BxmDBIOmmGenerateUtil ommGen = new BxmDBIOmmGenerateUtil();
		ommGen.setSourceRoot(sourceRoot);
		ommGen.setJavaPackage("kait.hd.hda.onl.dao.dto");
		ommGen.setDatabaseConfig(props);
		ommGen.setCreateFile(true);
		
		// create db table omm 
		ommGen.execute("D", "IO", null, null);
	}
	
	
	public void bxmBeanGenerate() {
		
		BxmBeanGenerateUtil beanGen = new BxmBeanGenerateUtil();
		
		
		
		beanGen.execute("B", programDesignExcelPath);
	}
	
}
