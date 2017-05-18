package skeleton;

import java.util.Properties;

import org.junit.Test;

import com.universal.code.bxm.BxmDBIOGenerateUtil;
import com.universal.code.bxm.BxmDBIOmmGenerateUtil;

public class GenerateRun {


	@Test
	public void dbioGenerate(){ 
		Properties props = new Properties();

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
		
		BxmDBIOGenerateUtil ommGen = new BxmDBIOGenerateUtil();
		ommGen.setSourceRoot("C:/Developer/BXMWorkspace/HD-onl/src");
		ommGen.setJavaPackage("kait.hd.hda.onl.dao");
		ommGen.setDatabaseConfig(props);
		ommGen.setCreateFile(true);
		ommGen.setDatasourceName("MainDS");
		ommGen.execute();
	}
		
	//@Test
	public void ommGenerate(){ 
		Properties props = new Properties();

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
		
		BxmDBIOmmGenerateUtil ommGen = new BxmDBIOmmGenerateUtil();
		ommGen.setSourceRoot("C:/Developer/BXMWorkspace/HD-onl/src");
		ommGen.setJavaPackage("kait.hd.hda.onl.dao.dto");
		ommGen.setDatabaseConfig(props);
		ommGen.setCreateFile(true);
		
		// create db table omm 
		ommGen.execute(null, null);
	}
	
}
