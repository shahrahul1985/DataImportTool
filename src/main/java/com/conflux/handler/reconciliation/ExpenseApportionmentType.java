package com.conflux.handler.reconciliation;


public enum ExpenseApportionmentType {
	
	MEDICAL_AID(0, "4600/011", "expenseApportionmentType.medicalAid"), //
    CO_PROVIDENT_FUND(1, "Co. Provident Fund", "expenseApportionmentType.coProvidentFund"), //
    PAYE_UIF(2, "PAYE & UIF", "expenseApportionmentType.payeUIF"), //
	STATIONERY(3, "2011/020", "expenseApportionmentType.stationery"),
	COURIER(4, "3400/000", "expenseApportionmentType.courier"),
	BANK_CHARGES(5, "2008/000", "expenseApportionmentType.bankCharges"),
	INSURANCE(6, "3850/000", "expenseApportionmentType.insurance");

	private final int columnId;
	private final String account;
	private final String code;
	
	public static ExpenseApportionmentType fromInt(final int columnId) {

    	ExpenseApportionmentType enumeration = ExpenseApportionmentType.MEDICAL_AID;
    	switch(columnId){
    	case 1: enumeration = ExpenseApportionmentType.CO_PROVIDENT_FUND;
    	break;
    	case 2: enumeration = ExpenseApportionmentType.PAYE_UIF;
    	break;
    	case 3: enumeration = ExpenseApportionmentType.STATIONERY;
    	break;
    	case 4: enumeration = ExpenseApportionmentType.COURIER;
    	break;
    	case 5: enumeration = ExpenseApportionmentType.BANK_CHARGES;
    	break;
    	case 6: enumeration = ExpenseApportionmentType.INSURANCE;
    	break;    		
    	}
    	        
        return enumeration;
    }
	
    public static ExpenseApportionmentType fromString(final String account) {

    	ExpenseApportionmentType enumeration = ExpenseApportionmentType.MEDICAL_AID;
    	if(account == null){
    		return null;
    	}
    	if(account.equals("4600/011")){ 
    		enumeration = ExpenseApportionmentType.MEDICAL_AID;
    	} else if(account.equals("2011/020")){
    		enumeration = ExpenseApportionmentType.STATIONERY;
    	} else if(account.equals("3400/000")){
    		enumeration = ExpenseApportionmentType.COURIER;
    	} else if(account.equals("2008/000")){
    		enumeration = ExpenseApportionmentType.BANK_CHARGES;
    	} else if(account.equals("3850/000")){
    		enumeration = ExpenseApportionmentType.INSURANCE;
    	}
        
        return enumeration;
    }
	
	private ExpenseApportionmentType(final int columnId, final String account, final String code) {
		this.columnId = columnId;
        this.account = account;
        this.code = code;
    }
	
	public String getValue() {
        return this.account;
    }

    public String getCode() {
        return this.code;
    }

	public int getColumnId() {
		return columnId;
	}

}
