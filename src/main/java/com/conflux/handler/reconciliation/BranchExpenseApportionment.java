package com.conflux.handler.reconciliation;

import java.util.HashMap;

public class BranchExpenseApportionment {
	
	private String branchCode;
	private String costCode;
	private HashMap<ExpenseApportionmentType, Double> hm;
	
	public BranchExpenseApportionment(String branchCode, String costCode){
		this.branchCode = branchCode;
		this.costCode = costCode;
		hm = new HashMap<ExpenseApportionmentType, Double>();
	}
	
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getCostCode() {
		return costCode;
	}
	public void setCostCode(String costCode) {
		this.costCode = costCode;
	}
	
	public void setPercentageValueForExpenseType(ExpenseApportionmentType expenseApportionmentType, Double percentageValue){
		hm.put(expenseApportionmentType, percentageValue);
	}
	
	public Double getPercentageValueForExpenseType(ExpenseApportionmentType expenseApportionmentType){
		return hm.get(expenseApportionmentType);
	}
	
}
