package com.universal.code.marshaller.impl.helper;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.constants.IOperateCode;
import com.universal.code.messages.MessageHelper;
import com.universal.code.utils.SystemUtil;

@Component
public class ConverterHelper {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public final static Map<String, String> unsupportEncodeConvertMap = new HashMap<String, String>();
	
	static {
		unsupportEncodeConvertMap.put("MS949", "EUC-KR");
	}
	
	public void writeXmlHeader(Writer writer, String encoding) {
		writeXmlHeader(writer, encoding, IOperateCode.DEFAULT_XML_STANDALONE);
	}
	
	private String getXMLEncoding(String encoding){
		String headerEncoding = encoding;
		if(headerEncoding == null) headerEncoding = IOperateCode.DEFAULT_ENCODING;
		
		String encodeCoversion = unsupportEncodeConvertMap.get(headerEncoding.toUpperCase());
		if(encodeCoversion != null) {
			if(logger.isDebugEnabled()) { 
				logger.debug(new StringBuilder()
					.append(MessageHelper.message("UNSUPPORT_ENCODE_CONVERSION", new Object[]{headerEncoding, encodeCoversion})).toString()
				);
			}
			headerEncoding = encodeCoversion;
		}
		return headerEncoding;
	}
	
	public void writeXmlHeader(Writer writer, String encoding, String standalone){
		
		try {
			String headerEncoding = getXMLEncoding(encoding);
			
			writer.write(IOperateCode.XML_HEADER.replace(IOperateCode.XML_HEADER_ENCODING, headerEncoding).replace(IOperateCode.XML_HEADER_STANDALONE, standalone));
			writer.write(SystemUtil.LINE_SEPARATOR);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
