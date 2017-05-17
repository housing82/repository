package com.universal.code.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InetAddressUtil {

	protected static final Logger logger = LoggerFactory.getLogger(InetAddressUtil.class);

	public final static String LOCAL_HOST_ADDRESS = getLocalHost().getHostAddress(); 
	public final static String LOCAL_HOST_NAME = getLocalHost().getHostName();
	public final static String LOCAL_CANONICAL_HOST_NAME = getLocalHost().getCanonicalHostName();
	
	public static InetAddress getLocalHost() {
		
		InetAddress local = null;

		try {
			local = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 

		return local;
	}
	
	public static InetAddress getRemoteHost(String hostName) {
		
		InetAddress host = null;

		try {
			//ex) hostName : www.naver.com
			host = InetAddress.getByName(hostName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		
		return host;
	}
	
	
}
