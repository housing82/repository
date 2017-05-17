package com.universal.code.utils;

import java.io.Serializable;
import java.rmi.dgc.VMID;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.universal.code.coder.MD5;

@Component
public class UniqueId4j  implements Serializable {
	
	protected static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

	/** serialVersionUID */
	private static final long serialVersionUID = -9200867117735180466L;
	
	private static final String FIXED_ID_STRING = "SEQ";
	
	private static final String FIXED_WHITE_SPACE = "0";

	private static final String DATE_TIME_FORMAT = "yyyyMMddHHmmssS"; 
	
	public static final String HOST_ADDRESS;
	static {
		HOST_ADDRESS = fixedLength(InetAddressUtil.getLocalHost().getHostAddress().replace(".",""), FIXED_WHITE_SPACE, 12);
	}

	
	private static final String FIXED_ID_FAIL_MSG = "체번의 앞단 예약 단어는 3자리 이어야만 합니다.";
	
	//발급한 시스템체번 임시저장맵
	private static Map<String, Integer> issuedID = new LinkedHashMap<String, Integer>();
	
	private static int limitedSeq = 0;
	
	private static String temporaryTime = null;
	
	private static boolean isUniqueIdLogging = false;

	private static int getLimitedSeq(){
		return getLimitedSeq(null, 1000, 9999);
	}

	private static int getLimitedSeq(int minSeq, int maxSeq){
		return getLimitedSeq(null, 1000, 9999);
	}
	
	private static int getLimitedSeq(String datetime){
		return getLimitedSeq(datetime, 1000, 9999);
	}
	
	//발급일 이 지나면 issuedID 를 clear함
	private static void clearIssuedID(){
		
		if(temporaryTime != null && temporaryTime.length() >= 8) {
			
			int year = Integer.parseInt(temporaryTime.substring(0,4));
			int month = Integer.parseInt(temporaryTime.substring(5,6));
			int day = Integer.parseInt(temporaryTime.substring(7,8));

			if(DateUtil.getPrevDateDiff(year, month, day) > 0) {
				if( issuedID != null ) {
					if(logger.isDebugEnabled()) {
						logger.debug("clear issuedID size : " + issuedID.size());
					}
					issuedID.clear();
				}
			}
		}
	}
	
	private static synchronized int getLimitedSeq(String datetimes, int minSeqx, int maxSeqx){

		clearIssuedID();

		String datetime = datetimes;
		int minSeq = minSeqx;
		int maxSeq = maxSeqx;
		
		if(datetime != null && (temporaryTime == null || !temporaryTime.equals(datetime))) {
			limitedSeq = minSeq;
			temporaryTime = datetime;
		}
		if(limitedSeq < minSeq || limitedSeq > maxSeq) {
			limitedSeq = minSeq;
		}
		
		return limitedSeq++;
	}
	
	private static String getSeqUUID(){
		String datetime = fixedLength(DateUtil.getFastDate(DATE_TIME_FORMAT), FIXED_WHITE_SPACE, 17);
		String seqUUID = getRandomUUID().replaceAll("-","") + getLimitedSeq(datetime);
		if(logger.isDebugEnabled() && isUniqueIdLogging) {
			logger.debug(CommonUtil.addString("seqUUID : " , seqUUID , " , seqUUID.length() : " , seqUUID.length()));
		}
		return seqUUID;
	}
	
	private static String getSeqTime(){
		String datetime = fixedLength(DateUtil.getFastDate(DATE_TIME_FORMAT), FIXED_WHITE_SPACE, 17);
		String seqTime = getLimitedSeq(datetime) + datetime;
		if(logger.isDebugEnabled() && isUniqueIdLogging) {
			logger.debug(CommonUtil.addString("seqTime : " , seqTime , " , seqTime.length() : " , seqTime.length()));
		}
		
		return seqTime;
	}
	
	private static String fixedLength(String originalString, String whiteSpaces, int fixedLengthx){
		String out = null;

		String original = originalString;
		String whiteSpace = whiteSpaces;
		int fixedLength = fixedLengthx;
		
		int originalLen = fixedLength - original.length();
		if(originalLen > 0) {
			for(int i = 0; i < originalLen; i++){
				original = original.concat(whiteSpace);
			}
		}
		else if(originalLen < 0) {
			original = original.substring(0, fixedLengthx);
		}

		out = original;
		return out;
	} 
	
	public static String getRandomUUID(){
		/** randomUUID 36 byte */
		return UUID.randomUUID().toString();
	}
	
	public static String getVMID(){
		/** VMID 43 byte */
		return new VMID().toString();
	}
	
	/**
	 * randomUUID + VMID
	 * @return : 32 byte
	 */
	public static String getId(){
		return getId("");
	}
	
	/**
	 * randomUUID + VMID + references
	 * @param references
	 * @return : 32 byte
	 */
	public static String getId(String references){
		String reference = references;
		
		/** reference null check */
		if(reference == null || reference.isEmpty()) {
			reference = "";
		}
		/** getRandomUUID 36 byte + rmi.VMID 43 byte + reference */
		String uuidMD5 = MD5.getInstance().getHashString(getRandomUUID().concat(getVMID()).concat(reference));
		
		if(logger.isDebugEnabled() && isUniqueIdLogging) {
			logger.debug(CommonUtil.addString("uuidMD5 : " , uuidMD5 , " , uuidMD5.length() : " , uuidMD5.length()));
		}
		
		Integer issueId = issuedID.get(uuidMD5);
		if(issueId == null) {
			issuedID.put(uuidMD5, 1);
		}
		else {
			if(logger.isDebugEnabled()) {
				logger.debug(CommonUtil.addString("- The re-issued ID is duplicated. is duplicate uuidMD5 : " , uuidMD5 ));
			}			
			issuedID.put(uuidMD5, ( issueId+1 ));
			uuidMD5 = getId(references);
		}
		return uuidMD5;
	}
	
	public static String getHostSeq(){
		return getHostSeq(null);
	}
	
	/**
	 * FIXED_ID_STRING + (getSeqTime: seq + datetime) + hostAddress
	 * @return : 36 byte 
	 */
	public static String getHostSeq(String prefix){
		String fixeds = (prefix != null ? prefix : FIXED_ID_STRING);
		
		if(fixeds.length() != 3) throw new RuntimeException(FIXED_ID_FAIL_MSG);
		
		String hostSeq = fixeds.concat(getSeqTime()).concat(HOST_ADDRESS);
		
		Integer issueId = issuedID.get(hostSeq);
		if(issueId == null) {
			//issuedID 에 존재하지 않으면 밀리세컨간의 중복방지를 위하여 발급된 아이디를 당일 보관한다.
			issuedID.put(hostSeq, 1);
		}
		else {
			if(logger.isDebugEnabled()) {
				logger.debug(CommonUtil.addString("- The re-issued ID is duplicated. is duplicate hostSeq : " , hostSeq ));
			}
			issuedID.put(hostSeq, ( issueId+1 ));
			hostSeq = getHostSeq(prefix);
		}
		return hostSeq;
	}
	
	
	
	public static String getHostUUID(){
		return getHostUUID(null);
	}
	
	/**
	 * FIXED_ID_STRING + (getSeqUUID: seq + randomUUID) + hostAddress
	 * @return : 51 byte
	 */
	private static String getHostUUID(String prefix){
		String fixeds = (prefix != null ? prefix : FIXED_ID_STRING);
		
		if(fixeds.length() != 3) throw new RuntimeException(FIXED_ID_FAIL_MSG);
		
		String hostUUID = getSeqUUID(); //fixeds.concat(getSeqUUID()); 
		
		Integer issueId = issuedID.get(hostUUID);
		if(issueId == null) {
			issuedID.put(hostUUID, 1);
		}
		else {
			if(logger.isDebugEnabled()) {
				logger.debug(CommonUtil.addString("- The re-issued ID is duplicated. is duplicate hostUUID : " , hostUUID ));
			}
			issuedID.put(hostUUID, ( issueId+1 ));
			hostUUID = getHostUUID(prefix);
		}
		return hostUUID;
	}
	
	
}
