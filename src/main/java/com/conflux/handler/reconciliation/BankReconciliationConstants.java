package com.conflux.handler.reconciliation;

public class BankReconciliationConstants {

	public static final String BANK_STMT_TYPE_CLIENT_DISBURSEMENT = "Client Disbursement";
	public static final String BANK_STMT_TYPE_BALANCE = "Balance";
	public static final String BANK_STMT_TYPE_CLIENT_DEPOSIT = "Client Deposit";
	public static final String BANK_STMT_TYPE_FEES = "Fees";
	public static final String BANK_STMT_TYPE_OTHER_DEPOSIT = "Other Dep";
	public static final String BANK_STMT_TYPE_OTHER_EXPENSE = "Other Exp";
	public static final String BANK_STMT_TYPE_STAFF_PAYROLL = "Staff Payroll";
	public static final String BANK_STMT_TYPE_TRANSFER_IN = "Transfer In";
	public static final String BANK_STMT_TYPE_TRANSFER_OUT = "Transfer Out";
	  

	
	public static final String PASTEL_CATEGORY_RENT = "Rent";
	public static final String PASTEL_CATEGORY_RENT_HO = "Rent HO";
	public static final String PASTEL_CATEGORY_STATIONARY = "Stationary";
	public static final String PASTEL_CATEGORY_COURIER = "Courier";
	
	public static final String CLIENT_DISBURSEMENT_GROUP_CODE_PATTERN = "(^[LMK][a-zA-Z]{3}[0-9]{3})(.*)";
	public static final String CLIENT_DEPOSIT_GROUP_CODE_PATTERN = "(^[LMK][a-zA-Z]{3}[0-9]{3})(.*)";
	public static final String ACCOUNT_PATTERN = "([0-9]{4}/[0-9]{3})(.*)";
	public static final String BRANCH_EXTERNAL_ID_PATTERN = "(.*)\\(([a-zA-Z]{2})\\sRENT\\)";
	
	public static final String CLIENT_DEPOSIT_GROUP_CODE_PATTERN_DESC = "(.*)([LMK][a-zA-Z]{3}[\\s]?[-]?[0-9]{3})(.*)";
	public static final String CASH_DEPOSIT_WITHOUT_GROUP_CODE_PATTERN_DESC_SAPO = "(.*)CASH DEPOSIT(.*)";
	//public static final String CLIENT_DEPOSIT_GROUP_CODE_PATTERN_WITH_SPACE_DESC = "([LMK][a-zA-Z]{3}[0-9]{3})$";
	
	public static final String TRANSACTION_TYPE_DISBURSAL = "DISBURSAL";
	public static final String TRANSACTION_TYPE_CLIENT_PAYMENT = "Client Payment";
	public static final String TRANSACTION_TYPE_OTHER = "other";
	public static final String TRANSACTION_TYPE_ERROR = "error";
	//public static final String TRANSACTION_TYPE_DISBURSAL = "DISBURSAL";
	
	public static final String BANK_RECONCILIATION_SHEET_NAME = "BankReconciliation";
	
	public static final String TRANSACTION_ID_COL_NAME = "Transaction Id";
	public static final String TRANSACTION_DATE_COL_NAME = "Transaction Date";
	public static final String DESCRIPTION_COL_NAME = "Description";
	public static final String AMOUNT_COL_NAME = "Amount";
	public static final String MOBILE_NUMBER_COL_NAME = "Mobile Number";
	public static final String CLIENT_ACCOUNT_NO_COL_NAME = "Client Account No";
	public static final String LOAN_ACCOUNT_NO_COL_NAME = "Loan Account No";
	public static final String GROUP_EXTERNAL_ID_COL_NAME = "Group External Id";
	public static final String BRANCH_EXTERNAL_ID_COL_NAME = "Branch External Id";
	public static final String GL_CODE_COL_NAME = "GL Code";
	public static final String ACCOUNTING_TYPE_COL_NAME = "Accounting Type";
	public static final String TRANSACTION_TYPE_COL_NAME = "Transaction Type";
	public static final String COMMENTS_COL_NAME = "Comments";
	
	public static final String COST_CODE_SPLIT_BRANCHES = "Branch cost codes";
	public static final String COST_CODE_HO = "99";
	
	public static final String BRANCH_EXTERNAL_ID_HO = "HO";
	
}
