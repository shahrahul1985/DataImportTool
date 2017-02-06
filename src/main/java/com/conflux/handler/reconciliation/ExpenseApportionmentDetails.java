package com.conflux.handler.reconciliation;

import java.util.Date;

public class ExpenseApportionmentDetails {
	
	private Double amount;
	private Date date;
	
	public ExpenseApportionmentDetails(Double amount, Date date){
		this.amount = amount;
		this.date = date;
	}
	
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

}
