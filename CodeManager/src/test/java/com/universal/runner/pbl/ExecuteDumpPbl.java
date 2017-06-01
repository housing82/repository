package com.universal.runner.pbl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.universal.code.constants.IOperateCode;
import com.universal.code.exception.ApplicationException;
import com.universal.code.utils.DateUtil;
import com.universal.code.utils.FileUtil;
import com.universal.code.utils.SystemUtil;


public class ExecuteDumpPbl {
	private final static Logger logger = LoggerFactory.getLogger(ExecuteDumpPbl.class);
	
	private FileUtil fileUtil; 
	
	private final static String PBL_ROOT_PATH;
	private final static String TARGET_EXT;
	private final static String PBL_DUMP;
	private final static String BASE_DUMP_FILE_TEMP;
	
	static {
		PBL_ROOT_PATH = "C:/Developer/AS-IS/KAIT_ERP/asisProject/kait-pbl-dump/pbl";
		PBL_DUMP = "C:/Developer/Git Repository/repository/CodeManager/pbldump-1.3.1stable/PblDump";
		TARGET_EXT = ".pbl";
		BASE_DUMP_FILE_TEMP = "C:/Developer/Git Repository/repository/CodeManager";
	}
	
	public ExecuteDumpPbl() {
		fileUtil = new FileUtil(); 
	}
	
	
	@Test
	public void execute() {
		
		File basePath = new File(PBL_ROOT_PATH);

		if(basePath.exists() && basePath.isDirectory()) {
			List<File> fileList = new ArrayList<File>();
			List<String> errorFileList = new ArrayList<String>();
			List<String> successFiles = new ArrayList<String>();
			
			addFileList(fileList, basePath);
			
			execDump(fileList, 0, successFiles, errorFileList);
			
			StringBuilder successStb = new StringBuilder();
			for(String successFile : successFiles) {
				logger.debug("successFile: {}", successFile);
				successStb.append(successFile).append(SystemUtil.LINE_SEPARATOR);
			}
			
			StringBuilder errStb = new StringBuilder();
			for(String errorFile : errorFileList) {
				logger.debug("errorFile: {}", errorFile);
				errStb.append(errorFile).append(SystemUtil.LINE_SEPARATOR);
			}
			
			String dumpResult = new StringBuilder().append("success = ").append(successFiles.size()).append(", error = ").append(errorFileList.size()).toString();
			
			logger.debug("dumpResult: {}", dumpResult);
			
			fileUtil.mkfile(PBL_ROOT_PATH.concat("/dump_status"), "dump_error_list.".concat(DateUtil.getFastDate("yyyyMMddHHmmss")).concat(".txt"), errStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
			fileUtil.mkfile(PBL_ROOT_PATH.concat("/dump_status"), "dump_success_list.".concat(DateUtil.getFastDate("yyyyMMddHHmmss")).concat(".txt"), successStb.toString(), IOperateCode.ENCODING_UTF8, false, true);
			fileUtil.mkfile(PBL_ROOT_PATH.concat("/dump_status"), dumpResult.concat(" ").concat(DateUtil.getFastDate("yyyyMMddHHmmss")), dumpResult, IOperateCode.ENCODING_UTF8, false, true);
			
			/*
			String dumpdir = null;
			String fileName = null;
			String tempFile = null;
			
			for(File file : fileList) {
				
				dumpdir = file.getCanonicalPath();
				dumpdir = dumpdir.substring(0, dumpdir.lastIndexOf("."));
				fileName = file.getCanonicalPath().substring(file.getCanonicalPath().lastIndexOf(File.separator) + File.separator.length());
				tempFile =  dumpdir.concat(File.separator).concat(fileName);
				logger.debug("dumpdir: {}", dumpdir);
				//파일의 덤프를 생성할 디렉토리 생성
				fileUtil.mkdir(dumpdir, true);
				//파일 복사
				fileUtil.copyFile(file.getCanonicalPath(), tempFile);
				
				execDump("C:/Developer/pbldump-1.3.1stable/PblDump -esu ".concat(tempFile).concat(" *.*"), tempFile);
			}
			*/
		}
		else {
			throw new ApplicationException("파워빌더 루트 디렉토리가 존재하지 않거나 디렉토리가 아닙니다.");
		}

	}
	
		
	public void execDump(List<File> fileList, int i, List<String> successFiles, List<String> errorFiles)  {
		File file = fileList.get(i);
		
		String dumpdir = null;
		String tempFile = null;
		String fileName = null;
		String command = null;
	    DefaultExecutor executor = null;
	    
	    ByteArrayOutputStream baos = null;
	    PumpStreamHandler streamHandler = null;
	    
		try {
			executor = new DefaultExecutor();
			baos = new ByteArrayOutputStream();
			streamHandler = new PumpStreamHandler(baos);
			
			executor.setStreamHandler(streamHandler);
			 
			dumpdir = file.getCanonicalPath();
		    dumpdir = dumpdir.substring(0, dumpdir.lastIndexOf("."));
			fileName = file.getCanonicalPath().substring(file.getCanonicalPath().lastIndexOf(File.separator) + File.separator.length());
			tempFile =  dumpdir.concat(File.separator).concat(fileName);
			command = PBL_DUMP.concat(" -esu ".concat(tempFile).concat(" *.*"));
			logger.debug("dumpdir: {}", dumpdir);
			
			//파일의 덤프를 생성할 디렉토리 생성
			File dumpdirFile = new File(dumpdir);
			if(dumpdirFile.exists()) {
				for(File oldFile : dumpdirFile.listFiles()) {
					oldFile.delete();
				}
			} else {
				fileUtil.mkdir(dumpdir, true);	
			}
			
			//파일 복사
			fileUtil.copyFile(file.getCanonicalPath(), tempFile);
			
		    CommandLine cmdLine = CommandLine.parse(command);
		    int exitValue = executor.execute(cmdLine);
		    logger.debug("exitValue: {}", exitValue);
		    
		    String returnStr = baos.toString();
		    List<String> dumpFiles =  new ArrayList<String>();
		    
		    
		    if(exitValue == 0) {
		    	logger.debug("returnStr : {}", returnStr);
		    	String[] stz = returnStr.split(SystemUtil.LINE_SEPARATOR);
		    	boolean start = false;
		    	int renameCnt = 0;
		    	for(String line : stz) {
		    		
		    		if(start && line.equals("--")) {
		    			logger.debug("END line: {}", line);
		    			break;
		    		}
		    		
		    		//logger.debug("start: {}, line: {}", start, line);	    		
		    		if(start) {
		    			line = line.substring(line.indexOf(" ")).trim();
		    			logger.debug("DATA line: {}", line);
		    			dumpFiles.add(line);

				    	
				    	File pblDumpFile = null;
				    	File renameDumpFile = null;
				    	
				    	if(line.contains(".")) {
				    		
				    		String path = BASE_DUMP_FILE_TEMP.concat(IOperateCode.STR_SLASH).concat(line);
				    		logger.debug("Origin dump file: {}", path);
				    		pblDumpFile = new File(path);
				    		if(pblDumpFile.exists()) {
				    			// 이도옹
				    			renameDumpFile = new File(dumpdir.concat(IOperateCode.STR_SLASH).concat(line));
				    			if(renameDumpFile.exists()) {
				    				renameDumpFile.delete();
				    			}
				    			boolean move = pblDumpFile.renameTo(renameDumpFile);
				    			logger.debug("Move: {}, RenameDumpFile: {}", move, renameDumpFile);
				    			if(move) {
				    				renameCnt++;
				    			}
				    			else {
				    				throw new ApplicationException("renameTo 실패 원본파일: {}", pblDumpFile.getPath());
				    			}
				    		}
				    	}
		    		}

		    		if(line.equals("--")) {
		    			logger.debug("START line: {}", line);
		    			start = true;
		    		}
		    	}
		    	
		    	logger.debug("renameCnt: {}", renameCnt);
		    }
		    
		    successFiles.add(tempFile);
		
		} catch (IOException e) {
			
			errorFiles.add(tempFile);
			e.printStackTrace();
			logger.error("ERROR File: {}", tempFile);
		}
		finally {
			new File(tempFile).delete();
			
		    if(fileList.size() - 1 > i ) {
		    	execDump(fileList, i+1, successFiles, errorFiles);
		    }
		    else {
		    	logger.debug("Dump END");
		    }
		}
	}

	private void addFileList(List<File> fileList, File file) {
		
		File[] files = file.listFiles();
		
		for(File item : files) {
			if(item.isFile() && (item.getPath().endsWith(TARGET_EXT) || item.getPath().endsWith(TARGET_EXT.toUpperCase()))) {
				fileList.add(item);
			}
			else if(item.isDirectory()) {
				addFileList(fileList, item);
			}
		}
	}
	
}
