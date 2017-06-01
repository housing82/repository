package com.universal.runner.srd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.utils.FileUtil;

public class ExecutePowerbuilderSrdSqlParser {

	private final static Logger logger = LoggerFactory.getLogger(ExecutePowerbuilderSrdSqlParser.class);
	
	private FileUtil fileUtil; 
	
	private final static String PBL_ROOT_PATH;
	private final static String TARGET_EXT;
	
	static {
		PBL_ROOT_PATH = "C:/Developer/AS-IS/KAIT_ERP/asisProject/kait-pbl-dump/pbl";
		TARGET_EXT = ".srd";
	}
	
	public ExecutePowerbuilderSrdSqlParser() {
		fileUtil = new FileUtil(); 
	}
	
	@Test
	public void execute() throws IOException {
		
		File basePath = new File(PBL_ROOT_PATH);

		if(basePath.exists() && basePath.isDirectory()) {
			
			List<File> fileList = new ArrayList<File>();
			
			fileUtil.addFileList(fileList, basePath, TARGET_EXT);
			
			String fileContents = null;
			for(File file : fileList) {
				logger.debug(" - Taget Path: {}", file.getPath());
				
				fileContents = fileUtil.getTextFileContent(file, "UTF-16LE");
				
				logger.debug("[fileContents]\n{}", fileContents);
				
				//TextFileReader oFile = new TextFileReader(file.getPath(), "UTF-8");
				//logger.debug("line : {}", oFile.readLine());
				
				
				
			} //E. for fileList 
		}
	}
	
	

}
