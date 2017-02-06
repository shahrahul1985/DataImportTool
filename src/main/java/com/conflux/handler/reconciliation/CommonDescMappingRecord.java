package com.conflux.handler.reconciliation;

public class CommonDescMappingRecord {
	
	
	private String desc;
	private String glDesc;
	private String glAccount;
	private String costCode;
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getGlDesc() {
		return glDesc;
	}

	public void setGlDesc(String glDesc) {
		this.glDesc = glDesc;
	}

	public String getGlAccount() {
		return glAccount;
	}

	public void setGlAccount(String glAccount) {
		this.glAccount = glAccount;
	}

	public String getCostCode() {
		return costCode;
	}

	public void setCostCode(String costCode) {
		this.costCode = costCode;
	}

	public CommonDescMappingRecord(String desc, String glDesc, String glAccount, String costCode){
		this.desc = desc;
		this.glDesc = glDesc;
		this.glAccount = glAccount;
		this.costCode = costCode;
	}
	

}
