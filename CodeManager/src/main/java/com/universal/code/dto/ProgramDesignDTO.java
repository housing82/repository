package com.universal.code.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import com.universal.code.extend.CommonObject;
import com.universal.code.extend.IDataTransferObject;

public class ProgramDesignDTO extends CommonObject implements IDataTransferObject {

	private String logc;
	private String asisTrxCd;
	private String grnt;
	private String progType;
	private String dataKind;
	private String basePack;
	private String scNm;
	private String trxSeq;
	private String scModf;
	private String scMetdPref;
	private String scMetdBody;
	private String scMetdLogc;
	private String bcNm;
	private String bcModf;
	private String bcMetdPref;
	private String bcMetdBody;
	private String bcMetdLogc;
	private String calleeNm;
	private String calleeMetdNm;
	private long excelRow;
	private Map<String, Map<String, Object>> calleeMap;
	private Map<String, ProgramDesignDTO> methodDesignMap;
	
	public ProgramDesignDTO() {
		this.reset();
	}
	
	private void reset() {
		logc = "";
		asisTrxCd = "";
		grnt = "";
		progType = "";
		dataKind = "";
		basePack = "";
		scNm = "";
		trxSeq = "";
		scModf = "";
		scMetdPref = "";
		scMetdBody = "";
		scMetdLogc = "";
		bcNm = "";
		bcModf = "";
		bcMetdPref = "";
		bcMetdBody = "";
		bcMetdLogc = "";
		calleeNm = "";
		calleeMetdNm = "";
		excelRow = -1;
		calleeMap = null;
		methodDesignMap = null;
	}

	public String getLogc() {
		return logc;
	}

	public void setLogc(String logc) {
		this.logc = logc;
	}

	public String getAsisTrxCd() {
		return asisTrxCd;
	}

	public void setAsisTrxCd(String asisTrxCd) {
		this.asisTrxCd = asisTrxCd;
	}

	public String getGrnt() {
		return grnt;
	}

	public void setGrnt(String grnt) {
		this.grnt = grnt;
	}

	public String getProgType() {
		return progType;
	}

	public void setProgType(String progType) {
		this.progType = progType;
	}

	public String getDataKind() {
		return dataKind;
	}

	public void setDataKind(String dataKind) {
		this.dataKind = dataKind;
	}

	public String getBasePack() {
		return basePack;
	}

	public void setBasePack(String basePack) {
		this.basePack = basePack;
	}

	public String getScNm() {
		return scNm;
	}

	public void setScNm(String scNm) {
		this.scNm = scNm;
	}

	public String getTrxSeq() {
		return trxSeq;
	}

	public void setTrxSeq(String trxSeq) {
		this.trxSeq = trxSeq;
	}

	public String getScModf() {
		return scModf;
	}

	public void setScModf(String scModf) {
		this.scModf = scModf;
	}

	public String getScMetdPref() {
		return scMetdPref;
	}

	public void setScMetdPref(String scMetdPref) {
		this.scMetdPref = scMetdPref;
	}

	public String getScMetdBody() {
		return scMetdBody;
	}

	public void setScMetdBody(String scMetdBody) {
		this.scMetdBody = scMetdBody;
	}

	public String getScMetdLogc() {
		return scMetdLogc;
	}

	public void setScMetdLogc(String scMetdLogc) {
		this.scMetdLogc = scMetdLogc;
	}

	public String getBcNm() {
		return bcNm;
	}

	public void setBcNm(String bcNm) {
		this.bcNm = bcNm;
	}

	public String getBcModf() {
		return bcModf;
	}

	public void setBcModf(String bcModf) {
		this.bcModf = bcModf;
	}

	public String getBcMetdPref() {
		return bcMetdPref;
	}

	public void setBcMetdPref(String bcMetdPref) {
		this.bcMetdPref = bcMetdPref;
	}

	public String getBcMetdBody() {
		return bcMetdBody;
	}

	public void setBcMetdBody(String bcMetdBody) {
		this.bcMetdBody = bcMetdBody;
	}

	public String getBcMetdLogc() {
		return bcMetdLogc;
	}

	public void setBcMetdLogc(String bcMetdLogc) {
		this.bcMetdLogc = bcMetdLogc;
	}

	public String getCalleeNm() {
		return calleeNm;
	}

	public void setCalleeNm(String calleeNm) {
		this.calleeNm = calleeNm;
	}

	public String getCalleeMetdNm() {
		return calleeMetdNm;
	}

	public void setCalleeMetdNm(String calleeMetdNm) {
		this.calleeMetdNm = calleeMetdNm;
	}

	public Map<String, Map<String, Object>> getCalleeMap() {
		return calleeMap;
	}

	public Map<String, Object> getCalleeMap(String key) {
		if(calleeMap == null) {
			calleeMap = new LinkedHashMap<String, Map<String, Object>>();
		}
		return calleeMap.get(key);
	}
	
	public void setCalleeMap(Map<String, Map<String, Object>> calleeMap) {
		this.calleeMap = calleeMap;
	}

	public void addCalleeMap(String key, Map<String, Object> value) {
		if(calleeMap == null) {
			calleeMap = new LinkedHashMap<String, Map<String, Object>>();
		}
		calleeMap.put(key, value);
	}

	
	
	
	public Map<String, ProgramDesignDTO> getMethodDesignMap() {
		return methodDesignMap;
	}

	public ProgramDesignDTO getMethodDesignMap(String key) {
		if(methodDesignMap == null) {
			methodDesignMap = new LinkedHashMap<String, ProgramDesignDTO>();
		}
		return methodDesignMap.get(key);
	}
	
	public void setMethodDesignMap(Map<String, ProgramDesignDTO> methodDesignMap) {
		this.methodDesignMap = methodDesignMap;
	}
	
	public void addMethodDesignMap(String key, ProgramDesignDTO value) {
		if(methodDesignMap == null) {
			methodDesignMap = new LinkedHashMap<String, ProgramDesignDTO>();
		}
		this.methodDesignMap.put(key, value);
	}

	public String getBcMetdMergeStr() {
		return new StringBuilder().append(getBcNm()).append(getBcMetdPref()).append(getBcMetdBody()).toString();
	}

	public String getScMetdMergeStr() {
		return new StringBuilder().append(getScNm()).append(getScMetdPref()).append(getScMetdBody()).toString();
	}
	
	public long getExcelRow() {
		return excelRow;
	}

	public void setExcelRow(long excelRow) {
		this.excelRow = excelRow;
	}
	
}
