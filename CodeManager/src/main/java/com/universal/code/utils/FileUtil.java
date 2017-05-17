package com.universal.code.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import com.universal.code.constants.IOperateCode;
import com.universal.code.constants.UniversalPattern;
import com.universal.code.dto.FilePropertiesDTO;
import com.universal.code.dto.FilesDataDTO;
import com.universal.code.dto.FilesystemDTO;
import com.universal.code.exception.ApplicationException;
import com.universal.code.exception.ValidateException;

@Component
public class FileUtil implements ApplicationContextAware, IOperateCode {
	
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	@Autowired
	private RegexUtil regexUtil;
	
	@Autowired
	private CoderUtil coderUtil;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private UniqueId4j uniqueId4j;
	
	public FileUtil() {
		if(regexUtil == null) {
			regexUtil = new RegexUtil();
		}
	}
	
	/**
	 * WebApplicationContext 선언
	 */
	@Autowired
	private WebApplicationContext context;

	/**
	 * ApplicationContext 을 WebApplicationContext 로 캐스팅하여 context 에 담습니다.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = (WebApplicationContext) applicationContext;
    }
	
    /**
     * WebApplicationContext 의 ContextPath 에 문자열 Path 를 연결하여 반환합니다.
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    private String getContextRealPath(String currentPath) {
    	
    	String out = null;
    	
    	try {
    		String current = StringUtil.NVL(currentPath, STR_BLANK).trim();
        	current = (current.equals(STR_SLASH) ? STR_BLANK : current);
        	boolean isCanonicalPath = false;
        	
        	if(SystemUtil.FILE_SEPARATOR.equals(STR_SLASH) && current.startsWith(STR_SLASH)) {
        		//linux, unix ( SystemUtil.FILE_SEPARATOR is / )
        		isCanonicalPath = new File(currentPath).exists();
        		if(logger.isDebugEnabled()) {
    	    		logger.debug(" Linux, Unix path : " + isCanonicalPath);
    	    	}
        	}
        	else {
        		//window NT ( SystemUtil.FILE_SEPARATOR is (a-zA-Z):\\ ) 
        		if( regexUtil.testPattern(currentPath, RegexUtil.PTN_STARTS_WITH_WINDOW_PATH) ) {
        			isCanonicalPath = true;
        	    	if(logger.isDebugEnabled()) {
        	    		logger.debug(" Window NT path : " + isCanonicalPath);
        	    	}
        		}
        	}
        	
    		if(!isCanonicalPath) {
    			//WebUtils.getRealPath
    			out = WebUtils.getRealPath(context.getServletContext(), STR_SLASH.concat(current));	
    		}
    		else {
    			//isCanonicalPath
    			out = currentPath;
    		}
			
		} catch (FileNotFoundException e) {
			throw new ApplicationException(e);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
    	
    	return out; 
    }
    
    /**
     * WebApplicationContext 의 ContextPath 를 구합니다.
     * @return
     * @throws FileNotFoundException
     */
    public String getContextPath() {
    	return getContextRealPath(STR_BLANK);
    }

	/**
	 * 컨텍스트 패스 아래 파일에 대한 실제 경로를 얻기위한 명령은 
	 * getRealPath 만을 사용하도록한다.
	 * @param filePath
	 * @return
	 */
    public String getRealPath(String filePath) {
    	if(context == null && logger.isErrorEnabled()) {
    		logger.error(" WebApplicationContext is null...");
    	}
    	return (context != null ? getContextRealPath(filePath) : filePath);
    }

    
	public File getRealFile(String filePath){
		
		File out = null; 
	
		String canonicalPath = getRealPath(filePath);
		
		if(logger.isDebugEnabled()) {
			logger.debug(CommonUtil.addString("canonicalPath : ", canonicalPath));
		}
	
		out = new File(canonicalPath);
	
		if(!out.exists()) {  
			throw new RuntimeException(CommonUtil.addString(STR_DOUBLE_QUOTATION, canonicalPath, "\" file path does not exist."));
		}

		return out;
	}
	
    
    public long getLastModified(File file){
    	
    	long out = 0;
    	
    	if(file != null && file.exists()) {
    		out = file.lastModified();
    	}
    	else {  
    		throw new RuntimeException(CommonUtil.addString(STR_DOUBLE_QUOTATION, file, "\" file does not exist."));
    	}
    	
    	return out;
    }
 
    public long getLastModified(String filePath){
    	
    	long out = 0;
    	String realPath = getRealPath(filePath);
    	File file = new File(realPath);
    	out = getLastModified(file);
    	
    	return out;
    }

    
	/**
	 * 주어진 path 에 있는 파일의 존재여부를 반환합니다.
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
    public boolean exists(String filePath) {
    	
    	boolean out = false;
    	if(StringUtil.isEmpty(filePath)) return out;
    	if(!(new File(filePath).exists())) {
    		out = (new File(getRealPath(filePath)).exists());
    	}
    	else {
    		out = true;
    	}
    	
    	return out;
    }
    
    /**
     * http, https 으로 접근할수있는 원격지 URL의 파일 존재 여부를 반환합니다.
     * @param pathUrl
     * @return
     */
    public boolean existsFileURL(String pathUrl) {

    	URL url = null;
    	URLConnection con;
    	boolean result = false;

		try {
			url = new URL(pathUrl);
			con = url.openConnection();
	    	HttpURLConnection exitCode = (HttpURLConnection) con;
	    	if(exitCode.getResponseCode() == 200){
	    		result = true;
	    	}
		} catch (UnknownHostException e){
			//ignore
			if(logger.isErrorEnabled()) {
				logger.error(CommonUtil.addString(e.getMessage()));
			}			
		} catch (MalformedURLException e) {
			//ignore
			if(logger.isErrorEnabled()) {
				logger.error(CommonUtil.addString(e.getMessage()));
			}
		} catch (IOException e) {
			//ignore
			if(logger.isErrorEnabled()) {
				logger.error(CommonUtil.addString(e.getMessage()));
			}
		}
		
		return result;
    }
    
    public boolean directoryConfirmAndMake(String targetDir) {
    	return directoryConfirmAndMake(targetDir, false);
    }
    
    /**
     * FS의 디렉토리 존재여부를 판단하고 존재하지 않는다면 생성합니다.
     * @param targetDir
     */
    public boolean directoryConfirmAndMake(String targetDir, boolean verbose){

    	boolean result = true;
    	
    	try {
    		String directory = getRealPath(targetDir);
    		
    		if(logger.isDebugEnabled() && verbose) {
    			logger.debug(CommonUtil.addString("..디렉토리 : " , directory));
    		}
	        File dir = new File(directory);
	        
	        if(logger.isDebugEnabled() && verbose) {
	        	logger.debug(CommonUtil.addString("..디렉토리 존재여부 : " , dir.isDirectory()));
	        }
	        
	        if(!dir.isDirectory()){
	        	
	        	if(logger.isDebugEnabled() && verbose) {
	        		logger.debug(CommonUtil.addString("..디렉토리가 존재하지 않음으로 디렉토리를 생성합니다."));
	        	}
	            if(!dir.mkdirs()){
	            	
	            	if(logger.isDebugEnabled() && verbose) {
	            		logger.debug(CommonUtil.addString("..디렉토리 생성 실패."));
	            	}
	                result = false;
	            }else{
	            	result = true;
	            }
	        }
		} catch (Exception e) {
			result = false;
			throw new ApplicationException(e);
		}

        return result;
    }

    /**
     * 파일명을 제외한 파일 확장자를 반환
     * @param fileName
     * @return
     */
    public static String getExt(String canonicalPath) {
        int idx;
        String fileName = canonicalPath;
        if (fileName == null || (idx = fileName.lastIndexOf(EXT_DELIMETER)) == -1)
            return "";
        else
            return fileName.substring(idx + 1);
    }

    public static String getDirectory(String canonicalPath) {
    	int idx;
        String fileName = canonicalPath;
        if (fileName == null || (idx = fileName.lastIndexOf(File.separator)) == -1)
            return "";
        else
            return fileName.substring(0, idx + File.separator.length());
    }
    
    /**
     * 확장자를 제외한 파일명을 반환
     * @param fileName
     * @return
     */
    public static String getNameBody(String filePath) {
    	String fileName = filePath;
    	if(fileName == null) {
    		return fileName;
    	}
    	
    	int start = 0;
    	int dot = fileName.lastIndexOf(STR_DOT);
    	int slash = fileName.lastIndexOf(STR_SLASH);
    	int backslash = fileName.lastIndexOf(STR_BACK_SLASH);
    	
    	if(dot == -1 && slash == -1 && backslash == -1) {
    		return fileName;
    	}
    	else if(slash >= backslash) {
    		start = slash + STR_SLASH.length();
    	}
    	else if(backslash > -1){
    		start = backslash + STR_BACK_SLASH.length();
    	}
    	
    	if(dot == -1) {
    		return fileName.substring(start);
    	}
    	else {
    		return fileName.substring(start, dot);
    	}
    }

    public static String getFileName(String filePath) {
    	String fileName = filePath;
    	if(fileName == null) {
    		return fileName;
    	}
    	
    	int start = 0;
    	int slash = fileName.lastIndexOf(STR_SLASH);
    	int backslash = fileName.lastIndexOf(STR_BACK_SLASH);
    	
    	if(slash == -1 && backslash == -1) {
    		return fileName;	
    	}
    	else if(slash >= backslash) {
    		start = slash + STR_SLASH.length();
    	}
    	else if(backslash > -1){
    		start = backslash + STR_BACK_SLASH.length();
    	}
    	return fileName.substring(start);
   
    }
    
    public File mkdir(final String dir, final boolean verbose)  {
    	return mkdir(new File(dir), verbose);
    }
    
	public File mkdir(final File dir, final boolean verbose)  {

		final File f = dir;

		try {
			if (f.exists() && ( !f.isDirectory())) {
				throw new IllegalArgumentException(CommonUtil.addString("..make directory '" , f.getPath() , "' already exists and it's not a directory."));
			}

			if (f.exists()) {
				if (verbose && logger.isDebugEnabled()) {
					logger.debug(CommonUtil.addString("..make directory '" , f.getPath() , "' already present"));
				}
				return f;
			}
			
			f.mkdirs();
			if (verbose && logger.isDebugEnabled()) {
				logger.debug(CommonUtil.addString("..made dir '" , f.getPath() , "'"));
			}
		}catch(Exception e){
			throw new ApplicationException("[Error] Make Directory Fail...", e);
		}
		return f;
	}


    /**
     * 파일을 생성하여 파일내용을 작성한다.
     * @param filrName : 생성할파일 이름을 포함한 전체경로
     * @param contents : 생성한 파일에 쓰기할 내용
     * @param inheritfile : 파일존재시 내용을 파일내용으로 이어쓸것인지의 여부
     * @return : 생성한 파일을 리턴
     * @throws Exception
     */
	@Deprecated
    public File makefile(String fileDir, String fileName, String contents, boolean inheritfile) {

    	if(logger.isDebugEnabled()) {
    		logger.debug(CommonUtil.addString("mkfile fileDir : " , fileDir , "\nmkfile fileName : " , fileName , "\nmkfile fileName : " , contents));
    	}

    	String mkfileName = "";	// 파일 이름

    	if(fileDir != null) {
    		directoryConfirmAndMake(fileDir);	// 디렉토리를 채크하고 없으면 생성한다.
    		mkfileName = fileDir.concat(STR_SLASH).concat(fileName);	// 경로를 포함한 파일 이름
    	}else{
    		mkfileName = fileName;	// 경로를 포함한 파일 이름
    	}

    	File file = null;
    	OutputStream out = null;
    	String fileContent = null;
    	
    	try {
            file = new File(mkfileName);	// 파일 생성
            out = new FileOutputStream(file, inheritfile);	// 파일에 문자를 적을 스트림 생성
            fileContent = StringUtil.NVL(contents);
            out.write(fileContent.getBytes()); // 파일에 쓰기
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(out != null) {
				try {
					// 파일 쓰기 스트림 닫기
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
			}
		}
        return file;
    }
	
	
	public File mkfile(String fileDir, String fileName, String contents, boolean inheritfile) {
		return mkfile(fileDir, fileName, contents, null, inheritfile);
	}

	public File mkfile(String fileDir, String fileName, String contents, String encoding, boolean inheritfile) {
		return mkfile(fileDir, fileName, contents, encoding, inheritfile, false);
	}

	/**
	 * 파일 생성
	 * @param fileDir	: 생성할파일 이름을 포함한 전체경로
	 * @param fileName	: 생성할파일 이름을 포함한 전체경로
	 * @param contents	: 생성한 파일에 쓰기할 내용
	 * @param encoding	: 파일인코딩
	 * @param inheritfile	: 파일이 존재할 경우 오버라이트 할것인지 파일의 내용을 추가할것인지 여부
	 * @param verbose	: 파일 생성 로깅을 할것인지 여부
	 * @return
	 */
    public File mkfile(String fileDir, String fileName, String contents, String encoding, boolean inheritfile, boolean verbose) {

    	if (logger.isDebugEnabled() && verbose) {
    		logger.debug(CommonUtil.addString("[START] mkfile\n mkfile fileDir : " , fileDir , "\n mkfile fileName : " , fileName , "\n mkfile fileName : " , contents));
    		logger.debug(CommonUtil.addString("..wget() mkfile start.. "));
    	}

    	String mkfileName = "";		// 파일 이름

    	if(fileDir != null) {
    		//mkdir(fileDir, verbose);
    		directoryConfirmAndMake(fileDir, verbose);	// 디렉토리를 채크하고 없으면 생성한다.
    		mkfileName = fileDir.concat(STR_SLASH).concat(fileName);	// 경로를 포함한 파일 이름
    	}else{
    		mkfileName = fileName;	// 경로를 포함한 파일 이름
    	}

        File file = new File(mkfileName);	// 파일 생성
        OutputStreamWriter out = null;
        String fileContent = StringUtil.NVL(contents);

        if (logger.isDebugEnabled() && verbose) {
        	logger.debug(CommonUtil.addString("..new file() will save to ", mkfileName));
        }
		try {
			out = new OutputStreamWriter(new FileOutputStream(file, inheritfile), encoding); // 파일에 문자를 적을 스트림 생성
			if (logger.isDebugEnabled() && verbose) {
				logger.debug ("..new file() make stream ");
			}
			
			out.write(fileContent); // 파일에 쓰기
			if (logger.isDebugEnabled() && verbose) {
				logger.debug ("..new file() write contents. ");
			}
			
			out.flush(); // 파일 에 문자열전달
			if (logger.isDebugEnabled() && verbose) {
				logger.debug ("..new file() flush ");
			}
		}
		catch (UnsupportedEncodingException e) {
			throw new ApplicationException(e);
		}
		catch (FileNotFoundException e) {
			throw new ApplicationException(e);
		}
		catch (IOException e) {
			throw new ApplicationException(e);
		}
		finally {
	        try {
				if(out != null) {
					out.close(); // 스트림 닫기
					if (logger.isDebugEnabled() && verbose) {
						logger.debug ("..new file() close ");
					}
				}
			} catch (IOException e) {
				throw new ApplicationException(e);
			}
		}
		if (logger.isDebugEnabled() && verbose) {
			logger.debug ("..wget() end ");
		}
		
        return file;
    }

    
	/**
	 * 파일 다운로드 유틸
	 * @param req
	 * @param res
	 * @param file
	 * @param filename
	 * @param mime
	 * @return
	 * @throws java.lang.Exception
	 */
    public boolean doSendFile(HttpServletRequest req, HttpServletResponse res, File file, String filename, String mime) {
    	if(logger.isDebugEnabled()) {
			logger.debug("[START] FileUtil.doSendFile filename : " + filename);
		}
    	
		String newFileName = filename;
		String fileNameEncoding = StringUtil.NVL(FilePropertiesDTO.downloadFileNameCharset);
		
		try {
			//한글 파일명 파일의 정상적 파일명 다운로드를 위한 new String
			
			/*
			if( fileNameEncoding.equals(IOperateCode.DEFAULT_ENCODING_MS949) || fileNameEncoding.equals(IOperateCode.DEFAULT_ENCODING_EUCKR) ) {
				newFileName = new String(newFileName.getBytes(fileNameEncoding), IOperateCode.KR_STRING_CHARSET_NAME);
			}
			else if( !fileNameEncoding.equals(IOperateCode.DEFAULT_ENCODING) ){ //UTF-8
				newFileName = new String(newFileName.getBytes(fileNameEncoding));
			}
			*/
			
			if(req.getHeader("User-Agent").contains("Mozilla")) {
				
				if( StringUtil.isNotEmpty(fileNameEncoding) ) {
					
					if( fileNameEncoding.equals(IOperateCode.DEFAULT_ENCODING_MS949) || fileNameEncoding.equals(IOperateCode.DEFAULT_ENCODING_EUCKR) ) {
						newFileName = new String(newFileName.getBytes(fileNameEncoding), IOperateCode.KR_STRING_CHARSET_NAME);
					}
					else if( !fileNameEncoding.equals(IOperateCode.DEFAULT_ENCODING) ){ //UTF-8
						newFileName = new String(newFileName.getBytes(fileNameEncoding), IOperateCode.ISO_STRING_CHARSET_NAME);
					}
				}
			}
			else {
				if( StringUtil.isNotEmpty(fileNameEncoding) ) {
					newFileName = URLEncoder.encode(newFileName, fileNameEncoding);
				}
			}
			
		} catch (UnsupportedEncodingException ue) {
			throw new ApplicationException(ue);
		}
		
		if (file.exists() && StringUtil.isNotEmpty(newFileName)) {
			
			res.reset();
			if (mime == null) {
				mime = "application/octet-stream";
			}
			
			//파일명에 들어갈수 없는 특수문자 " "공백처리
			newFileName = regexUtil.replaceAllPattern(newFileName, UniversalPattern.PTN_FILE_NAME_NOT_SPECIAL_CHAR, " ");
	    	if(logger.isDebugEnabled()) {
	    		logger.debug(" +- file name encoding : " + fileNameEncoding);
	    		logger.debug(" +- download file name : " + newFileName);
			}
	    	
			res.setContentType(mime.concat(";charset=").concat(fileNameEncoding)); //euc-kr
			//res.setHeader("Content-Transfer-Encoding", "7bit"); //before
			res.setHeader("Content-Transfer-Encoding", "binary"); //add
			res.setHeader("Connection", "close"); //add
			
			if (req.getHeader("User-Agent").indexOf("MSIE 5.5") > -1) {
				res.setHeader("Content-Disposition", "filename=".concat(newFileName).concat(";"));
			} 
			else {
				res.setHeader("Content-Disposition", "attachment; filename=".concat(newFileName).concat(";"));
			}
			res.setHeader("Content-Length", Long.toString(file.length()));

			byte b[] = new byte[4096];
			BufferedInputStream fin = null;
			BufferedOutputStream outs = null;
			
			try {
				fin = new BufferedInputStream(new FileInputStream(file));

				outs = new BufferedOutputStream(res.getOutputStream());
			
				int read = 0;
				while ((read = fin.read(b)) != -1) {
					outs.write(b, 0, read);
				}
			}
			catch (FileNotFoundException fne) {
				//ignore
				if(logger.isErrorEnabled()) {
					logger.error(CommonUtil.addString(fne.getMessage()));
				}
			}
			catch(Exception e){
				//ignore
				if(logger.isErrorEnabled()) {
					logger.error(CommonUtil.addString(e.getMessage()));
				}
			}
			finally {
				try {
					if( outs != null) {
						outs.close();
					}
					if( fin != null) {
						fin.close();
					}
				} catch (IOException ioe) {
					throw new ApplicationException(ioe);
				}
			}
		} else {
			return false;
		}

		return true;
    }

    public List<FilesDataDTO> sendMultipart(String path , String child, HttpServletRequest request){
    	return sendMultipart(null, null, path , child, request);
    }
    
    public List<FilesDataDTO> sendMultipart(String path , String child, HttpServletRequest request, String attachHome){
    	return sendMultipart(null, attachHome, path , child, request);
    }
    /**
     * 멀티파트로 업로드된 파일을 바인딩된 path 와 child 경로로 디렉토리와 파일명을 암호화하여  복제합니다.
     * upload base directory 는 어플리케이션의 baseDoc 입니다.
     * @param path
     * @param child
     * @param request
     * @return
     */
    public List<FilesDataDTO> sendMultipart(String fileParamName, String path , String child, HttpServletRequest request){
    	return sendMultipart(fileParamName, null, path , child, request);
    }
    
    /**
     * 멀티파트로 업로드된 파일을 바인딩된 path 와 child 경로로 디렉토리와 파일명을 암호화하여  복제합니다.
     * attachHome 의 존재 여부( != null ) 에 따라 기본경로 혹은 주어진 path 가 upload base directory 가 됩니다.
     * @param attachHome
     * @param path
     * @param child
     * @param request
     * @return
     */
	public List<FilesDataDTO> sendMultipart(String fileParamName, String attachHome, String filePath , String child, HttpServletRequest request){

		if(!(request instanceof MultipartHttpServletRequest)) return null;
		if(StringUtil.NVL(filePath).equals("") || StringUtil.NVL(child).equals("")) return null;
		if(fileParamName == null) fileParamName = IOperateCode.DEF_KEY_FILE_NAME;
		
		String path = filePath.concat(STR_SLASH).concat(child); 
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Iterator<?> filesIter = multipartRequest.getFileNames();
		
		List<MultipartFile> mpFiles = multipartRequest.getFiles(fileParamName);
		
		if(logger.isDebugEnabled()) {
			logger.debug(" [MULTIPART UPDATE COUNT] : " + mpFiles.size());
		}
		
		int temp = 0;
		while (filesIter.hasNext()) {
			filesIter.next();
			temp++;
		}
		logger.debug(" [MULTIPART UPDATE COUNT FILE_NAMES] : " + temp);
		
		List<FilesDataDTO> resultSet = null;
		FilesDataDTO entity = null;
		File newFile = null, uploadFile = null;
		long fileSize = 0;
		int i = 0;
		MultipartFile mpFile = null;
		String fname = null;
		String originalName = null;
		String fsname = null;
		String uploadPath = null;
		String baseDir = null;
		
		String uploadDir = "attach".concat(SystemUtil.FILE_SEPARATOR).concat("files").concat(SystemUtil.FILE_SEPARATOR);
		
		//upload base directory
		if(attachHome != null && !attachHome.isEmpty()) {
			baseDir = attachHome;
		}
		else {
			baseDir = getContextPath();
		}
		
		if(!baseDir.endsWith(SystemUtil.FILE_SEPARATOR)) {
			baseDir = baseDir.concat(SystemUtil.FILE_SEPARATOR);
		}
		
		if(logger.isInfoEnabled()) {
			logger.info(CommonUtil.addString("- baseDir : " , baseDir));
			logger.info(CommonUtil.addString("- uploadDir : " , uploadDir));
		}
		
		try {
			resultSet = new ArrayList<FilesDataDTO>();

			for(MultipartFile attachFile : mpFiles) {

				mpFile = attachFile;
				if(logger.isInfoEnabled()) {
					logger.info(CommonUtil.addString("- ["+i+"] mpFile : " , mpFile));
				}
				
				fileSize = mpFile.getSize();
				if(logger.isInfoEnabled()) {
					logger.info(CommonUtil.addString("- ["+i+"] fileSize : " , fileSize));
				}
				
				originalName = StringUtil.NVL(mpFile.getOriginalFilename()).trim();
				if(logger.isInfoEnabled()) {
					logger.info(CommonUtil.addString("- ["+i+"] originalName : " , originalName));
				}
				
				
				if(StringUtil.isNotEmpty(originalName)) {

					if(originalName.indexOf(STR_SLASH) != -1) {
						fname = originalName.substring(originalName.lastIndexOf(STR_SLASH) + 1);
					}
		            else {
		            	fname = originalName.substring(originalName.lastIndexOf(STR_BACK_SLASH) + 1);
		            }

		            //업로드 디렉토리 체크및 생성
					uploadPath = baseDir.concat(uploadDir).concat(path);
					if(logger.isInfoEnabled()) {
						logger.info(CommonUtil.addString("- ["+i+"] uploadPath : " , uploadPath));
					}
					
		            uploadFile = new File(uploadPath);
		            if(!uploadFile.exists()) {
		            	uploadFile.mkdirs();
		            }

					fsname = uploadPath
							.concat(SystemUtil.FILE_SEPARATOR)
							.concat(Long.toString(System.currentTimeMillis()))
							.concat(STR_UNDERBAR)
							.concat(coderUtil.hashStringMD5(fname));
					
					newFile = new File(fsname);
					if(logger.isInfoEnabled()) {
						logger.info(CommonUtil.addString("- [", i, "] newFile : " , fsname));
					}
					
					//업로드된 파일을 newFile 으로 복제
					mpFile.transferTo(newFile);
					if(logger.isInfoEnabled()) {
						logger.info(CommonUtil.addString("- [", i, "] mpFile.transferTo(newFile) "));
					}
					
					entity = new FilesDataDTO();
					
					entity.setFile_nm(fname);	//오리지널 파일명
					if(logger.isInfoEnabled()) {
						logger.info(CommonUtil.addString("- [", i, "] setFile_nm : ", entity.getFile_nm()));
					}
					
					entity.setFile_ext(getExt(fname));
					if(logger.isInfoEnabled()) {
						logger.info(CommonUtil.addString("- [", i, "] setFile_ext : ", entity.getFile_ext()));
					}
					
					entity.setFile_sys_path(newFile.getAbsolutePath());	//파일시스템에 쓰여질 신규 파일이름 (전체경로)
					if(logger.isInfoEnabled()) {
						logger.info(CommonUtil.addString("- [", i, "] setFile_sys_path : ", entity.getFile_sys_path()));
					}
					
					entity.setFile_mime_type(mpFile.getContentType());	//파일 마임타입
					if(logger.isInfoEnabled()) {
						logger.info(CommonUtil.addString("- [", i, "] setFile_mime_type : ", entity.getFile_mime_type()));
					}
					
					entity.setFile_size(fileSize);	//파일 사이즈
					if(logger.isInfoEnabled()) {
						logger.info(CommonUtil.addString("- [", i, "] setFile_size : ", entity.getFile_size()));
					}

					resultSet.add(entity);

					if(logger.isInfoEnabled()) {
						logger.info(CommonUtil.addString("- [", i, "] File End "));
					}
				}
				
				
				i++;
			}
		} catch (IllegalStateException e) {
			throw new ApplicationException(e);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}

		if(logger.isInfoEnabled()) {
			logger.info(CommonUtil.addString("sendMultipart End Files resultSet.size " , resultSet.size()));
		}
		return resultSet;
	}

    

    public static synchronized void copy(InputStream in, OutputStream out) throws IOException {
    	
        byte buffer[] = new byte[256];
        do {
            int bytesRead = in.read(buffer);
            if(bytesRead != -1)
                out.write(buffer, 0, bytesRead);
            else
                return;
        } while(true);
    }

    public static synchronized void copy(FileInputStream in, FileOutputStream out)  {
    	
        FileChannel inc = null;
        FileChannel outc = null;
        
        try {
        	
        	inc = in.getChannel();
        	outc = out.getChannel();
            
        	for(ByteBuffer buffer = ByteBuffer.allocate(in.available()); inc.read(buffer) != -1; buffer.clear())
            {
                buffer.flip();
                outc.write(buffer);
            }
		
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
        finally {
            try {
				if(in != null) {
					in.close();
				}
				if(out != null) {
					out.close();
				}
				if(inc != null) {
					inc.close();
				}
				if(outc != null) {
					outc.close();
				}
            } catch (IOException e) {
				throw new ApplicationException(e);
			}
        }
    }

    public static void copy(String orginal, String copy) throws IOException {
        FileInputStream fin = new FileInputStream(orginal);
        FileOutputStream fout = new FileOutputStream(copy);
        copy(fin, fout);
    }

    public static synchronized boolean delete(String file) {
        File delFile = new File(file);
        if(!delFile.exists())
            return false;
        else
            return delFile.delete();
    }

    public static synchronized void deleteOnExit(String file) {
        File delFile = new File(file);
        if(!delFile.exists())
        {
            return;
        } 
        else
        {
            delFile.deleteOnExit();
            return;
        }
    }

    public String getTextFileContent(String path) {
        return getTextFileContent(new File(getRealPath(path)));
    }

    public String getTextFileContent(File files) {

        StringBuffer contents = new StringBuffer();
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        File fileStore = files;
        
        try {
            if(fileStore == null || !fileStore.exists()) {
            	throw new FileNotFoundException(CommonUtil.addString(fileStore, " 파일이 존재하지 않습니다."));
            }

            fileReader = new FileReader(fileStore);
            bufferedReader = new BufferedReader(fileReader);

            String read;
            while((read = bufferedReader.readLine()) != null) {
            	contents.append((new StringBuilder(String.valueOf(read))).append(SystemUtil.LINE_SEPARATOR).toString());
            }
        }
        catch(IOException io) {
            throw new ApplicationException(io);
        }
        catch(Exception e) {
            throw new ApplicationException(e);
        }
        finally {
        	if(fileReader != null) {
        		try {
					fileReader.close();
				} catch (IOException e) {
					throw new ApplicationException(e);
				}
        	}
        	if(bufferedReader != null) {
        		try {
        			bufferedReader.close();
				} catch (IOException e) {
					throw new ApplicationException(e);
				}
        	}
        }
        
        return contents.toString();
    }
    
	    
	/**
	 * request의 멀티파트 파일 처리
	 * @param request
	 * @param file
	 * @param path
	 * @return
	 */
    public FilesDataDTO multipartFileUpload(HttpServletRequest request, String file, String path, boolean verbose) {
    	FilesDataDTO filesDataDTO = new FilesDataDTO();

		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile(file);
		String today = DateFormatUtils.format(new Date(System.currentTimeMillis()), "yyyyMMdd");
		String absPath = path.concat(SystemUtil.FILE_SEPARATOR).concat(today);
		
		directoryConfirmAndMake(absPath, verbose);
		absPath =  getRealPath(absPath);
		
		filesDataDTO.setFile_nm(multipartFile.getOriginalFilename());
		filesDataDTO.setFile_ext(getExt(filesDataDTO.getFile_nm()));
		filesDataDTO.setActu_save_file_nm(UniqueId4j.getId().concat(STR_DOT).concat(filesDataDTO.getFile_ext()));
		filesDataDTO.setFile_sys_path(absPath.concat(SystemUtil.FILE_SEPARATOR).concat(filesDataDTO.getActu_save_file_nm()));
		
		File saveFile = new File(filesDataDTO.getFile_sys_path());
		
		byte[] bytes = null;
		ByteArrayOutputStream byteStream = null;
		OutputStream outStream = null;
		try{
			bytes = multipartFile.getBytes();
			byteStream = new ByteArrayOutputStream();
			byteStream.write(bytes);
			
			outStream = new FileOutputStream(saveFile);
			byteStream.writeTo(outStream);
			
			outStream.flush();
		}catch(IOException e){
			throw new RuntimeException(e);
		}finally{
			
			try {
				if( byteStream != null) {
					byteStream.close();
				}
				if( outStream != null ) {
					outStream.close();
				}
			} catch (IOException e) {
				throw new ApplicationException(e);
			}
		}
		
    	return filesDataDTO;
    }

    
    /**
     * 바인드된 패스가 directory 이면 해당 패스아래있는 파일목록을 리턴합니다.
     * @param path
     * @return
     */
    public static File[] getFiles(String path){
    	File[] files = null;
    	File directory = new File(path);
    	if(directory.isDirectory()) {
    		files = directory.listFiles();
    	}
    	else {
    		throw new RuntimeException(" 패스["+path+"]는 디렉토리가 아닙니다.");
    	}
    	return files;
    }
    
    /**
     * 바인드된 sourceFilePath 의 파일을 
     * 패스 targetFilePath 으로 복사합니다.
     * @param sourceFilePath
     * @param targetFilePath
     * @throws IOException
     */
    public static void copyFile(String sourceFilePath, String targetFilePath) throws IOException {
    	
    	DataInputStream in = null;
    	DataOutputStream out = null;
    	
        try {
        	String dir = targetFilePath.replace(STR_SLASH, STR_BACK_SLASH);
        	File d = new File(dir.substring(0, dir.lastIndexOf(STR_BACK_SLASH) + STR_BACK_SLASH.length()));
        	boolean isConfirm = false;
	        if(!d.isDirectory()){
	        	//대상 디렉토리 없음
	            if(d.mkdirs()){
	            	//디렉토리 생성 성공
	            	isConfirm = true;
	            }
	            else {
	            	// 디렉토리 생성 실패
	            	isConfirm = false;
	            }
	        }
	        else {
	        	//대상 디렉토리 존재
	        	isConfirm = true;
	        }
	        
	        if(isConfirm) {
	        	in = new DataInputStream(new FileInputStream(sourceFilePath));
	            out = new DataOutputStream(new FileOutputStream(targetFilePath));
	            byte[] buff = new byte[8*1024];
	            int size;
	            while((size = in.read(buff))> -1) {
	                out.write(buff, 0, size);
	            }	
	        }
        }
        catch(IOException ioe) {
            throw ioe;
        }
        finally {
            
        	if(out != null) {
            	out.close();
            }
            
            if(in != null) {
            	in.close();
            }
            
        }
    }
    
    /**
     * 파일 내부 문자열을 스캔하여 바인드된 문자열 scanWord의 존재여부를 리턴함
     * @param filePath
     * @param scanWord
     * @return
     */
    public boolean fileScan(String filePath, String... scanWords) {
    	return fileScan(filePath, null, scanWords);
    }
    
    /**
     * 파일 내부 문자열을 스캔하여 
     * useDelimiter으로 파일의 문자 내용을 구분하고
     * 바인드된 문자열 scanWord의 존재여부를 리턴함
     * @param filePath
     * @param useDelimiter
     * @param scanWord
     * @return
     */
    public boolean fileScan(String filePath, String useDelimiter, String... scanWords) {
    	
    	if( logger.isDebugEnabled() ) {
			logger.debug(new StringBuilder().append(SystemUtil.LINE_SEPARATOR)
				.append(" [START FILE SCAN]").append(SystemUtil.LINE_SEPARATOR)
				.append(" filePath : ").append(filePath).append(SystemUtil.LINE_SEPARATOR)
				.append(" useDelimiter : ").append(useDelimiter).append(SystemUtil.LINE_SEPARATOR)
				.append(" scanWords : ").append(Arrays.toString(scanWords)).append(SystemUtil.LINE_SEPARATOR)
				.toString()
			);
		}

    	if( filePath == null ) {
    		throw new ValidateException("파일경로가 존재하지 않습니다.");
    	}
    	if( scanWords == null ) {
    		throw new ValidateException("검색 단어가 존재하지 않습니다.");
    	}
    	
    	boolean out = false;
    	File target = new File(filePath);
    	Scanner scanner = null;
    	
    	try {
    		
			scanner = new Scanner(target);
			if( useDelimiter != null ) {
				scanner.useDelimiter(useDelimiter);
			}
			
			String contents = null;
			int order = 0;
			while (scanner.hasNext()) {
				contents = scanner.next();
				if( logger.isDebugEnabled() ) {
					logger.debug("- ["+order+"] File Scan : " + contents);
				}
				for(String word : scanWords) {
					if(contents.indexOf(word) > -1) {
						if( logger.isDebugEnabled() ) {
							logger.debug("Checked scanWords : " + word);
						}
						out = true;
						break;
					}	
				}
				if(out) break;
				order++;
			}
		} catch (FileNotFoundException e) {
			throw new ApplicationException("파일이 존재하지 않습니다.", e);
		}
    	finally {
    		if(scanner != null) {
    			scanner.close();
    		}
    	}
    	
    	return out; 
    }

    public String getLastModifiedDate(String filePath) {
    	return getFileModifyDate(filePath, null, null, null);
    }
    
    public String getLastModifiedDate(String filePath, String dateFormat) {
    	return getFileModifyDate(filePath, dateFormat, null, null);
    }
    
    /**
     * 주어진 경로(파일 또는 디렉토리)의 수정일을 정해진 날짜포맷으로 변환
     * @param filePath
     * @param dateFormat
     * @param timeZone
     * @param locale
     * @return
     */
    public String getFileModifyDate(String filePath, String dateFormat, TimeZone timeZone, Locale locale){
    	
    	File file = null;
    	if( StringUtil.isEmpty(filePath) ) {
    		throw new ApplicationException("파일경로가 존재하지 않습니다.");
    	}
    	
    	file = new File(filePath);
    	if( !file.exists() ) {
    		throw new ApplicationException("파일이 존재하지 않습니다.");
    	}
    	
    	if( dateFormat == null ) {
    		dateFormat = DateUtil.DEF_DATE_FORMAT;
    	}
    	//ex)
    	//timeZone : TimeZone.getTimeZone("Asia/Seoul")
    	//locale : Locale.KOREA
    	
    	return FastDateFormat.getInstance(dateFormat, timeZone, locale).format(file.lastModified());
    }
    
    
    public String getFileSize(File file) {
    	return getFileSize(file, null, 0);
    }
    
    public String getFileSize(File file, String format) {
    	return getFileSize(file, format, 0);
    }
    
    public String getFileSize(File file, double baseSize) {
    	return getFileSize(file, null, baseSize);
    }
    
    /**
     * 바인드된 더블 값을 bytes,KB,MB,GB,TB,PB중 가장 큰단위로 변환
     * @param file : 사이즈조회 대상 파일
     * @param decimalFormat : 변환한 값의 데이터포맷 ( #,###.## )
     * @param baseSize : 기본 바이트 단위 ( 1024 )
     * @return
     */
    public String getFileSize(File file, String decimalFormat, double baseSize) {
    	
    	String out = null;
    	if( file == null || !file.exists() ) {
    		throw new ApplicationException("파일이 존재하지 않습니다.");
    	}

    	double size = ((Long) file.length()).doubleValue();
    	
    	out = byteConvert(size, decimalFormat, baseSize);
    	

        return out;
    }

    /**
     * 바인드된 더블 값을 bytes,KB,MB,GB,TB,PB중 가장 큰단위로 변환
     * @param size : 변환대상 값
     * @param decimalFormat : 변환한 값의 데이터포맷 ( #,###.## )
     * @param baseSize : 기본 바이트 단위 ( 1024 )
     * @return
     */
    public String byteConvert(double size, String decimalFormat, double baseSize){
    	//if(logger.isDebugEnabled()) {
		//	logger.debug("[START] byteConvert size : " + size);
		//} 
    	String out = null;
    	String format = null;
    	
    	if( decimalFormat == null ) {
    		format = "#,###.##";
    	}
    	else {
    		format = decimalFormat;
    	}
    	
    	double BASE = 1024;
    	
    	if( baseSize > 0) BASE = baseSize; 
    	
    	double KB = BASE, MB = KB*BASE, GB = MB*BASE, TB = GB*BASE, PB = TB*BASE;
    	
    	DecimalFormat df = new DecimalFormat(format);
    	
    	if(size >= PB) {
    		out = df.format(size/PB) + "PB";
        }
    	else if(size >= TB) {
    		out = df.format(size/TB) + "TB";
        }
    	else if(size >= GB) {
    		out = df.format(size/GB) + "GB";
        }
    	else if(size >= MB) {
    		out = df.format(size/MB) + "MB";
        }
    	else if(size >= KB) {
    		out = df.format(size/KB) + "KB";
        }
    	else {
    		out = "" + (int)size + "Bytes";
    	}
		
    	//if(logger.isDebugEnabled()) {
		//	logger.debug("[E N D] byteConvert size : " + out);
		//} 
    	return out;
    }
    
    /**
     * 디렉토리의 사이즈(bytes & 단위변환)와 파일개수, 디렉토리개수를 반환
     * @param storagePath
     * @return
     */
    public FilesystemDTO directoryInfo(String storagePath) {
    	return directoryInfo(storagePath, null, 0);
    }
    
    /**
     * 디렉토리의 사이즈(bytes & 단위변환)와 파일개수, 디렉토리개수를 반환
     * @param storagePath
     * @param format
     * @param baseSize
     * @return
     */
    public FilesystemDTO directoryInfo(String storagePath, String format, double baseSize) {
    	
    	File storage = new File(storagePath);
    	if( storage == null || !storage.isDirectory() ) {
    		throw new ApplicationException("디렉토리가 아니거나 존재하지 않습니다.[PathParameter:"+storagePath+"]");
    	}
    	
    	FilesystemDTO files = directoryInfo(storage, new FilesystemDTO());
    	files.setSizeString(byteConvert(files.getSize(), format, baseSize));
    	return files;
    }
    
    /**
     * 디렉토리의 사이즈(bytes & 단위변환)와 파일개수, 디렉토리개수를 반환
     * @param storageFolder
     * @param files
     * @return
     */
	private FilesystemDTO directoryInfo(File storageFolder, FilesystemDTO files) {

		try {
			File[] listFile = storageFolder.listFiles();

			if( listFile != null ) {
				for (int i = 0; i < listFile.length; i++) {
					
					if (listFile[i].isFile()) {

						files.setSize(files.getSize() + listFile[i].length());
						files.setTotalFile(files.getTotalFile() + 1);
					} else {

						files.setTotalDirectory(files.getTotalDirectory() + 1);
						files = directoryInfo(listFile[i], files);
					}
				}
			}
		} catch (Exception e) {
			throw new ApplicationException("디렉토리 사이즈 채크 장에발생", e);
		}
		return files;
	}
    
	/**
	 * directoryInfo 보다 느림
	 * 디렉토리의 사이즈만 반환(bytes)
	 * @param storagePath
	 * @return
	 */
	public long sizeOfDirectory(String storagePath) {
    	File storage = new File(storagePath);
    	if( storage == null || !storage.isDirectory() ) {
    		throw new ApplicationException("디렉토리가 아니거나 존재하지 않습니다.[PathParameter:"+storagePath+"]");
    	}
		long size = FileUtils.sizeOfDirectory(storage);
		return size;
	}

	public byte[] getFileByes(String filePath){
		if(filePath == null) {
			throw new ApplicationException("파일경로가 존재하지 않습니다. File : {}", filePath);
		}
		File file = new File(filePath);
		if(!file.exists() || !file.canRead()) {
			throw new ApplicationException("파일이 존재하지 않거나 읽을수 없는 파일입니다. File : {}", filePath);
		}
		
		byte[] bytes = null;
		
		try {
			bytes = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
		return bytes;
	}
	
	public String getPathSeparator(String path){
		String out = IOperateCode.STR_SLASH;
		if(path != null && path.indexOf(IOperateCode.STR_BACK_SLASH) > -1) {
			out = IOperateCode.STR_BACK_SLASH;
		}
    	return out;
    }
	
	public String getDirFilePath(String dirPath, String fileName){
    	String out = null;
    	if(dirPath != null && fileName != null) {
    		String separator = getPathSeparator(dirPath);
    		if(separator != null) {
    			if(!dirPath.endsWith(separator)) {
        			dirPath = dirPath.concat(separator);
        		}
    		}
    		else {
      			dirPath = dirPath.concat(IOperateCode.STR_SLASH);
    		}
    		
    		out = dirPath.concat(fileName);
    		if(out.indexOf(IOperateCode.STR_BACK_SLASH) > -1) {
    			out = out.replace(IOperateCode.STR_BACK_SLASH, IOperateCode.STR_SLASH);
    		}
    	}
    	return out;
    }
	
	public boolean isNotRootPath(File... files) {
		boolean out = true;
		for(File file : files) { 
			if(logger.isDebugEnabled()) {
				logger.debug("- check isNotRootPath: {}", file.getParent());
			}
			if(file.getParent() == null) {
				out = false;
				break;
			}
		}
		return out;
	}
	
	public boolean isNotRootPath(String... paths) {
		boolean out = true;
		for(String path : paths) { 
			if(logger.isDebugEnabled()) {
				logger.debug("- check isNotRootPath: {}", path);
			}
			if(path.equals(IOperateCode.STR_SLASH) || regexUtil.testPattern(path, RegexUtil.PTN_WINDOW_ROOT_PATH)) {
				out = false;
				break;
			}
		}
		return out;
	}
	
}
