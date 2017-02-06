package com.conflux.handler.reconciliation;

public class LookupRecord {
	
	private String shortCode;
	private String pastelCategory;
	private String pastelAccount;
	private String costCode;
	private String bankStmtAndPastelCategory;
	
	public LookupRecord(String shortCode, String bankStmtAndPastelCategory, String pastelCategory, String pastelAccount, String costCode){
		this.shortCode = shortCode;
		this.bankStmtAndPastelCategory = bankStmtAndPastelCategory;
		this.pastelCategory = pastelCategory;
		this.pastelAccount = pastelAccount;
		this.costCode = costCode;		
	}
	
	public String getBankStmtAndPastelCategory() {
		return bankStmtAndPastelCategory;
	}

	public void setBankStmtAndPastelCategory(String bankStmtAndPastelCategory) {
		this.bankStmtAndPastelCategory = bankStmtAndPastelCategory;
	}

	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public String getPastelCategory() {
		return pastelCategory;
	}
	public void setPastelCategory(String pastelCategory) {
		this.pastelCategory = pastelCategory;
	}
	public String getPastelAccount() {
		return pastelAccount;
	}
	public void setPastelAccount(String pastelAccount) {
		this.pastelAccount = pastelAccount;
	}
	public String getCostCode() {
		return costCode;
	}
	public void setCostCode(String costCode) {
		this.costCode = costCode;
	}

}
