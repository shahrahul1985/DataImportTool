package com.conflux.bank.transactions;

import java.util.Date;

public class Records {

	private Long transactionId;
	private Date transactionDate;
	private String description;
	private Double amount;
	private String mobileNumber;
	private Long clientAccountNumber;
	private Long loanAccountNumber;
	private String groupExternalId;
	private String branchExternalId;
	private String glCode;
	private String accountingType;
	private String transactionType;
	private String comments;

	public Records(Long transactionId, Date transactionDate, String desc, Double amount, String mobileNumber,
			Long clientAccountNumber, Long loanAccountNumber, String groupExternalId, String branchExternalId,
			String glCode, String accountType, String transactionType, String comments) {
		if(transactionId != null){
			this.transactionId = transactionId;
		} else {
			this.transactionId = new Long(1);
		}
		this.transactionDate = transactionDate;
		this.description = desc;
		this.amount = amount;
		this.mobileNumber = mobileNumber;
		this.clientAccountNumber = clientAccountNumber;
		this.loanAccountNumber = loanAccountNumber;
		this.groupExternalId = groupExternalId;
		this.branchExternalId = branchExternalId;
		this.glCode = glCode;
		this.accountingType = accountType;
		this.transactionType = transactionType;
		this.setComments(comments);
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Long getClientAccountNumber() {
		return clientAccountNumber;
	}

	public void setClientAccountNumber(Long clientAccountNumber) {
		this.clientAccountNumber = clientAccountNumber;
	}

	public Long getLoanAccountNumber() {
		return loanAccountNumber;
	}

	public void setLoanAccountNumber(Long loanAccountNumber) {
		this.loanAccountNumber = loanAccountNumber;
	}

	public String getGroupExternalId() {
		return groupExternalId;
	}

	public void setGroupExternalId(String groupExternalId) {
		this.groupExternalId = groupExternalId;
	}

	public String getBranchExternalId() {
		return branchExternalId;
	}

	public void setBranchExternalId(String branchExternalId) {
		this.branchExternalId = branchExternalId;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public String getAccountingType() {
		return accountingType;
	}

	public void setAccountingType(String accountingType) {
		this.accountingType = accountingType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
