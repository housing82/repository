package com.universal.code.dto;

import java.util.Collection;
import java.util.List;

import com.universal.code.extend.CommonObject;
import com.universal.code.extend.IDataTransferObject;

public class ProgramDesginDTO extends CommonObject implements IDataTransferObject {

	private String logc;
	private String asisTrxCd;
	private String grnt;
	private String progType;
	private String userYn;
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
	private String dbioNm;
	private String dbioMetdNm;
	private List<String> strList;
	private Collection strCollection;
	private TableDTO tableDTO;
	
	public ProgramDesginDTO() {
		this.reset();
	}
	
	private void reset() {
		logc = "";
		asisTrxCd = "";
		grnt = "";
		progType = "";
		userYn = "";
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
		dbioNm = "";
		dbioMetdNm = "";
		strList = null;
		strCollection= null;
		tableDTO = null;
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

	public String getUserYn() {
		return userYn;
	}

	public void setUserYn(String userYn) {
		this.userYn = userYn;
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

	public String getDbioNm() {
		return dbioNm;
	}

	public void setDbioNm(String dbioNm) {
		this.dbioNm = dbioNm;
	}

	public String getDbioMetdNm() {
		return dbioMetdNm;
	}

	public void setDbioMetdNm(String dbioMetdNm) {
		this.dbioMetdNm = dbioMetdNm;
	}

	public List<String> getStrList() {
		return strList;
	}

	public void setStrList(List<String> strList) {
		this.strList = strList;
	}

	public Collection getStrCollection() {
		return strCollection;
	}

	public void setStrCollection(Collection strCollection) {
		this.strCollection = strCollection;
	}

	public TableDTO getTableDTO() {
		return tableDTO;
	}

	public void setTableDTO(TableDTO tableDTO) {
		this.tableDTO = tableDTO;
	}
	
	
	
}
