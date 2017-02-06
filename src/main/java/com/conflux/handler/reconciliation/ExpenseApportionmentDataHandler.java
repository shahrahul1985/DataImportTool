package com.conflux.handler.reconciliation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.client.Center;
import org.openmf.mifos.dataimport.handler.AbstractDataImportHandler;
import org.openmf.mifos.dataimport.handler.Result;
import com.conflux.bank.transactions.Records;

public class ExpenseApportionmentDataHandler extends AbstractDataImportHandler {

	private static final int BRANCH_CODE_COL = 0;
	private static final int COST_CODE_COL = 1;
	private static final int MEDICAL_AID_COL = 2;
	private static final int CO_PROVIDENT_FUND_COL = 3;
	private static final int PAYE_UIF_COL = 4;
	private static final int STATIONERY_COL = 5;
	private static final int COURIER_COL = 6;
	private static final int BANK_CHARGES_COL = 7;
	private static final int INSURANCE_COL = 8;

	private static final int DATE_COL = 1;
	private static final int DESC_COL = 2;
	private static final int AMOUNT_COL = 3;
	private static final int RECEIPT_COL = 8;
	private static final int SECOND_RECEIPT_COL = 9;
	private static final int PF_CODE_RECEIPT_COL = 12;
	private static final int ACCOUNT_COL = 17;
	private static final int PASTEL_CATEGORY_COL = 18;
	private static final int BANK_STMT_TYPE_COL = 19;
	private static final int GROUP_COL = 37;
	private static final int PF_CODE_COL = 38;

	private static final int OUT_TRANSACTION_ID_COL = 0;
	private static final int OUT_TRANSACTION_DATE_COL = 1;
	private static final int OUT_DESCRIPTION_COL = 2;
	private static final int OUT_AMOUNT_COL = 3;
	private static final int OUT_MOBILE_NUMBER_COL = 4;
	private static final int OUT_CLIENT_ACCOUNT_NO_COL = 5;
	private static final int OUT_LOAN_ACCOUNT_NO_COL = 6;
	private static final int OUT_GROUP_EXTERNAL_ID_COL = 7;
	private static final int OUT_BRANCH_EXTERNAL_ID_COL = 8;
	private static final int OUT_GL_CODE_COL = 9;
	private static final int OUT_ACCOUNTING_TYPE_COL = 10;
	private static final int OUT_TRANSACTION_TYPE_COL = 11;
	private static final int OUT_COMMENTS_COL = 12;

	private final Workbook workbook;
	private final ArrayList<BranchExpenseApportionment> branchesExpenseApportionment;
	private final ArrayList<Records> records;
	private HashMap<String, ExpenseApportionmentDetails> expenseApportionmentAccount;
	private double staffPayrollTotal = 0;
	private Date staffPayrollDate = null;
	private double bankChargesfees = 0;
	private double medical = 0;
	private double providentFund = 0;
	private double payeAndUif = 0;
	private double stationary = 0;
	private double courier = 0;
	private double bankCharges = 0;
	private double insurance = 0;

	public ExpenseApportionmentDataHandler(Workbook workbook) {
		this.workbook = workbook;
		branchesExpenseApportionment = new ArrayList<BranchExpenseApportionment>();
		records = new ArrayList<Records>();
		expenseApportionmentAccount = new HashMap<String, ExpenseApportionmentDetails>();
	}

	public ExpenseApportionmentDetails getExpenseApportionmentAccountAmount(String expenseApportionmentAccount) {
		return this.expenseApportionmentAccount.get(expenseApportionmentAccount);
	}

	public void addExpenseApportionmentAccountAmount(String expenseApportionmentAccount, Double amount, Date date) {
		ExpenseApportionmentDetails expenseApportionmentDetails = this.expenseApportionmentAccount
				.get(expenseApportionmentAccount);

		if (expenseApportionmentDetails != null) {
			Double existingAmount = expenseApportionmentDetails.getAmount();
			Date existingDate = expenseApportionmentDetails.getDate();
			if (existingDate.before(date)) {
				expenseApportionmentDetails.setDate(date);
			}
			expenseApportionmentDetails.setAmount(existingAmount.doubleValue() + amount.doubleValue());
			this.expenseApportionmentAccount.put(expenseApportionmentAccount, expenseApportionmentDetails);
		} else {
			this.expenseApportionmentAccount.put(expenseApportionmentAccount,
					new ExpenseApportionmentDetails(amount, date));
		}

	}

	public Result parseExpense() {
		// TODO Auto-generated method stub
		Result result = new Result();
		Sheet expenseApportionmentSheet = workbook.getSheet("ExpenseApportionment");
		Integer noOfEntries = getNumberOfRows(expenseApportionmentSheet, 0);
		for (int rowIndex = 2; rowIndex < noOfEntries; rowIndex++) {
			Row row;
			try {
				row = expenseApportionmentSheet.getRow(rowIndex);
				branchesExpenseApportionment.add(parseAsBranchExpenseApportionment(row));
			} catch (Exception e) {
				result.addError("Row = " + rowIndex + " , " + e.getMessage());
			}
		}
		return result;
	}

	public Result parseBankStmt(LookupSheetDataHandler lookupSheet, CommonDescMappingDataHandler commonDescMappingSheet) {
		Result result = new Result();
		Sheet bankStmtSheet = workbook.getSheet("BankStatement");
		Integer noOfEntries = getNumberOfRows(bankStmtSheet, 0);
		for (int rowIndex = 2; rowIndex < noOfEntries; rowIndex++) {
			Row row;
			try {
				row = bankStmtSheet.getRow(rowIndex);
				Records record = parseAsRecords(row, lookupSheet, commonDescMappingSheet);
				if (record != null) {
					record.setTransactionId(new Long(rowIndex));					
					records.add(record);
				}
			} catch (Exception e) {
				result.addError("Row = " + rowIndex + " , " + e.getMessage());
			}
		}

		populateSplitCostCodeRecords();
		records.add(new Records(null, staffPayrollDate, "Staff Payroll", staffPayrollTotal, null, null, null, null, null, null,
				null, BankReconciliationConstants.TRANSACTION_TYPE_ERROR, "Type Of Staff Payroll"));
		return result;
	}

	private Records parseAsRecords(Row row, LookupSheetDataHandler lookupSheet, CommonDescMappingDataHandler commonDescMappingSheet) {
		String date = readAsDate(DATE_COL, row, "dd-MMM-yy");
		DateFormat format = new SimpleDateFormat("dd-MMM-yy");
		if (date == null || "".equals(date)) {
			return null;
		}
		Date date1 = null;
		try {
			date1 = format.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String desc = readAsString(DESC_COL, row);
		Double amount = readAsDouble(AMOUNT_COL, row);
		String pfCodeReceipt = readAsString(PF_CODE_RECEIPT_COL, row);
		String account = readAsString(ACCOUNT_COL, row);
		String pastelCategory = readAsString(PASTEL_CATEGORY_COL, row);
		String bankStmtType = readAsString(BANK_STMT_TYPE_COL, row);
		String group = readAsString(GROUP_COL, row);
		String pfCode = readAsString(PF_CODE_COL, row);
		
		Records record = null;
		CommonDescMappingRecord commonDescMappingRecord = commonDescMappingSheet.getCommonDescMappingRecord(desc.toLowerCase().trim());
		if(commonDescMappingRecord != null){
			String type = getType(amount);
			if(commonDescMappingRecord.getCostCode().equals(BankReconciliationConstants.COST_CODE_HO)){
				return new Records(null, date1, desc, amount, null, null, null, null,
						BankReconciliationConstants.BRANCH_EXTERNAL_ID_HO, commonDescMappingRecord.getGlAccount(), type,
						BankReconciliationConstants.TRANSACTION_TYPE_OTHER, null);
			} 
		} else if (bankStmtType != null) {
			
			if (bankStmtType.trim().equals(BankReconciliationConstants.BANK_STMT_TYPE_CLIENT_DISBURSEMENT)) {
				record = handleClientDisbursements(date1, desc, amount);
			} else if (bankStmtType.trim().equals(BankReconciliationConstants.BANK_STMT_TYPE_CLIENT_DEPOSIT)) {
				record = handleClientDeposit(date1, desc, amount, pfCodeReceipt);
			} else if (bankStmtType.trim().equals(BankReconciliationConstants.BANK_STMT_TYPE_FEES)) {
				record = handleFees(date1, desc, amount, pfCodeReceipt, pastelCategory, bankStmtType, account,
						lookupSheet);
			} else if (bankStmtType.trim().equals(BankReconciliationConstants.BANK_STMT_TYPE_OTHER_EXPENSE)) {
				if (pastelCategory.trim().equals(BankReconciliationConstants.PASTEL_CATEGORY_RENT_HO)) {
					record = handleRentHO(date1, desc, amount, pfCodeReceipt, pastelCategory, bankStmtType, account);
				} else if (pastelCategory.trim().equals(BankReconciliationConstants.PASTEL_CATEGORY_RENT)) {
					record = handleRent(date1, desc, amount, pfCodeReceipt, pastelCategory, bankStmtType, account);
				} else {
					record = handleOtherExp(date1, desc, amount, pfCodeReceipt, pastelCategory, bankStmtType, account,
							lookupSheet);
				}
			} else if (bankStmtType.trim().equals(BankReconciliationConstants.BANK_STMT_TYPE_STAFF_PAYROLL)) {
				staffPayrollTotal += amount;
				if(staffPayrollDate == null){
					staffPayrollDate = date1;
				} else if(staffPayrollDate.before(date1)){	
					staffPayrollDate = date1;
				}
				/*
				 * record = new Records(null, date1, desc, amount, null, null,
				 * null, null, null, null, null,
				 * BankReconciliationConstants.TRANSACTION_TYPE_ERROR,
				 * "Type Of Staff Payroll");
				 */
			} else if (bankStmtType.trim().equals(BankReconciliationConstants.BANK_STMT_TYPE_OTHER_DEPOSIT)
					|| bankStmtType.trim().equals(BankReconciliationConstants.BANK_STMT_TYPE_TRANSFER_IN)
					|| bankStmtType.trim().equals(BankReconciliationConstants.BANK_STMT_TYPE_TRANSFER_OUT)) {
				
				record = handleClientDepositBasedGroupNameDesc(date1, desc, amount);
				if(record == null)
					record = handleOtherDep(date1, desc, amount, pfCodeReceipt, pastelCategory, bankStmtType, account,
						lookupSheet);

			} else {
				record = new Records(null, date1, desc, amount, null, null, null, null, null, null, null,
						BankReconciliationConstants.TRANSACTION_TYPE_ERROR, "No Logic for Matching");
			}
			//If the logic is not matching Check the description for GroupName Regex
			
		}
		return record;
	}

	private Records handleClientDisbursements(Date date, String desc, Double amount) {
		Matcher m = Pattern.compile(BankReconciliationConstants.CLIENT_DISBURSEMENT_GROUP_CODE_PATTERN).matcher(desc);
		if (m.matches()) {
			return new Records(null, date, desc, amount, null, null, null, m.group(1), null, null, null,
					BankReconciliationConstants.TRANSACTION_TYPE_DISBURSAL, null);
		}
		return new Records(null, date, desc, amount, null, null, null, null, null, null, null,
				BankReconciliationConstants.TRANSACTION_TYPE_ERROR,
				"Unable to Match GroupCode Pattern For disbursement");
	}

	private Records handleClientDeposit(Date date, String desc, Double amount, String pfCodeReceipt) {
		Matcher m = Pattern.compile(BankReconciliationConstants.CLIENT_DEPOSIT_GROUP_CODE_PATTERN)
				.matcher(pfCodeReceipt);
		if (m.matches()) {
			return new Records(null, date, desc, amount, null, null, null, m.group(1), null, null, null,
					BankReconciliationConstants.TRANSACTION_TYPE_CLIENT_PAYMENT, null);
		}
		Records record = handleClientDepositBasedGroupNameDesc(date, desc, amount);
		if(record == null){
			return new Records(null, date, desc, amount, null, null, null, null, null, null, null,
					BankReconciliationConstants.TRANSACTION_TYPE_CLIENT_PAYMENT,
					null);
		}
		return record;		
	}

	private String getType(Double amount) {
		String type = "CREDIT";
		if (amount < 0) {
			type = "DEBIT";
		}
		return type;
	}
	
	private Records handleClientDepositBasedGroupNameDesc(Date date, String desc, Double amount) {
		Matcher m = Pattern.compile(BankReconciliationConstants.CLIENT_DEPOSIT_GROUP_CODE_PATTERN_DESC).matcher(desc);
		if (m.matches()) {
			return new Records(null, date, desc, amount, null, null, null, m.group(2).replaceAll("\\s", "").replaceAll("-", ""), null, null, null,
					BankReconciliationConstants.TRANSACTION_TYPE_CLIENT_PAYMENT, null);
		}
		//For SAPO 
		Matcher mSapo = Pattern.compile(BankReconciliationConstants.CASH_DEPOSIT_WITHOUT_GROUP_CODE_PATTERN_DESC_SAPO).matcher(desc);
		if (mSapo.matches()) {
			return new Records(null, date, desc, amount, null, null, null, null, null, null, null,
					BankReconciliationConstants.TRANSACTION_TYPE_CLIENT_PAYMENT, null);
		}
		/*m = Pattern.compile(BankReconciliationConstants.CLIENT_DEPOSIT_GROUP_CODE_PATTERN_WITH_SPACE_DESC).matcher(desc);
		if (m.matches()) {
			return new Records(null, date, desc, amount, null, null, null, m.group(1), null, null, null,
					BankReconciliationConstants.TRANSACTION_TYPE_CLIENT_PAYMENT, null);
		}*/
		return null;
	}

	private Records handleRentHO(Date date, String desc, Double amount, String pfCodeReceipt, String pastelCategory,
			String bankStmtType, String account) {
		String accountGLCode = getAccountGLCode(account);
		return new Records(null, date, desc, amount, null, null, null, null,
				BankReconciliationConstants.BRANCH_EXTERNAL_ID_HO, accountGLCode, getType(amount),
				BankReconciliationConstants.TRANSACTION_TYPE_OTHER, null);
	}

	private Records handleRent(Date date, String desc, Double amount, String pfCodeReceipt, String pastelCategory,
			String bankStmtType, String account) {
		String branchExternalId = getBranchExternalIdForRentAccount(desc.toUpperCase());
		String accountGLCode = getAccountGLCode(account);
		String type = getType(amount);
		if (branchExternalId != null) {
			return new Records(null, date, desc, amount, null, null, null, null, branchExternalId, accountGLCode, type,
					BankReconciliationConstants.TRANSACTION_TYPE_OTHER, null);
		}
		return new Records(null, date, desc, amount, null, null, null, null, null, null, type,
				BankReconciliationConstants.TRANSACTION_TYPE_ERROR,
				"Unable to get the Branch External Id from the Desc for Rent");
	}

	private Records handleOtherDep(Date date, String desc, Double amount, String pfCodeReceipt, String pastelCategory,
			String bankStmtType, String account, LookupSheetDataHandler lookupSheet) {
		LookupRecord lookupRecord = lookupSheet.getLookupRecord(account);
		String type = getType(amount);
		if (lookupRecord != null && lookupRecord.getCostCode() != null) {
			if (lookupRecord.getCostCode().trim().equals(BankReconciliationConstants.COST_CODE_HO)) {
				String accountGLCode = getAccountGLCode(account);
				return new Records(null, date, desc, amount, null, null, null, null,
						BankReconciliationConstants.BRANCH_EXTERNAL_ID_HO, accountGLCode, type,
						BankReconciliationConstants.TRANSACTION_TYPE_OTHER, null);
			} else {
				return new Records(null, date, desc, amount, null, null, null, null, null, null, type,
						BankReconciliationConstants.TRANSACTION_TYPE_ERROR,
						"CostCode in the LookUp Sheet is[" + lookupRecord.getCostCode().trim() + "]");
			}
		}
		return new Records(null, date, desc, amount, null, null, null, null, null, null, type,
				BankReconciliationConstants.TRANSACTION_TYPE_ERROR,
				"Unable to find account[" + account + "] in LookUp Sheet");
	}

	private Records handleOtherExp(Date date, String desc, Double amount, String pfCodeReceipt, String pastelCategory,
			String bankStmtType, String account, LookupSheetDataHandler lookupSheet) {
		if(getBranchExternalIdForRentAccount(desc.toUpperCase()) != null){
			Records record = handleRent(date, desc, amount, pfCodeReceipt, pastelCategory, bankStmtType, account);
			if(BankReconciliationConstants.TRANSACTION_TYPE_OTHER.equals(record.getTransactionType())){
				return record;
			}
				
		}
		LookupRecord lookupRecord = lookupSheet.getLookupRecord(account);
		String type = getType(amount);
		if (lookupRecord != null && lookupRecord.getCostCode() != null) {
			if (lookupRecord.getCostCode().trim().equals(BankReconciliationConstants.COST_CODE_SPLIT_BRANCHES)) {
				addExpenseApportionmentAccountAmount(account, amount, date);
				return null;
			} else if (lookupRecord.getCostCode().trim().equals(BankReconciliationConstants.COST_CODE_HO)) {
				String accountGLCode = getAccountGLCode(account);
				return new Records(null, date, desc, amount, null, null, null, null,
						BankReconciliationConstants.BRANCH_EXTERNAL_ID_HO, accountGLCode, type,
						BankReconciliationConstants.TRANSACTION_TYPE_OTHER, null);
			} else {
				return new Records(null, date, desc, amount, null, null, null, null, null, null, type,
						BankReconciliationConstants.TRANSACTION_TYPE_ERROR,
						"CostCode in the LookUp Sheet is[" + lookupRecord.getCostCode().trim() + "]");
			}
		} else {
			return new Records(null, date, desc, amount, null, null, null, null, null, null, type,
					BankReconciliationConstants.TRANSACTION_TYPE_ERROR,
					"Unable to find account[" + account + "] in LookUp Sheet");
		}
	}

	private Records handleFees(Date date, String desc, Double amount, String pfCodeReceipt, String pastelCategory,
			String bankStmtType, String account, LookupSheetDataHandler lookupSheet) {
		LookupRecord lookupRecord = lookupSheet.getLookupRecord(account);
		String type = getType(amount);
		if (lookupRecord != null && lookupRecord.getCostCode() != null) {
			if (lookupRecord.getCostCode().trim().equals(BankReconciliationConstants.COST_CODE_SPLIT_BRANCHES)) {
				addExpenseApportionmentAccountAmount(account, amount, date);
			} else if (lookupRecord.getCostCode().trim().equals(BankReconciliationConstants.COST_CODE_HO)) {
				String accountGLCode = getAccountGLCode(account);
				return new Records(null, date, desc, amount, null, null, null, null,
						BankReconciliationConstants.BRANCH_EXTERNAL_ID_HO, accountGLCode, type,
						BankReconciliationConstants.TRANSACTION_TYPE_OTHER, null);
			} else {
				return new Records(null, date, desc, amount, null, null, null, null, null, null, type,
						BankReconciliationConstants.TRANSACTION_TYPE_ERROR,
						"CostCode in the LookUp Sheet is[" + lookupRecord.getCostCode().trim() + "]");
			}

		} else {
			return new Records(null, date, desc, amount, null, null, null, null, null, null, type,
					BankReconciliationConstants.TRANSACTION_TYPE_ERROR,
					"Unable to find account[" + account + "] in LookUp Sheet");
		}
		return null;
	}

	private String getAccountGLCode(String account) {
		Matcher m = Pattern.compile(BankReconciliationConstants.ACCOUNT_PATTERN).matcher(account);
		String accountGLCode = null;
		if (m.matches()) {
			accountGLCode = m.group(1);
		}
		return accountGLCode;
	}

	private String getBranchExternalIdForRentAccount(String desc) {
		Matcher m = Pattern.compile(BankReconciliationConstants.BRANCH_EXTERNAL_ID_PATTERN).matcher(desc);
		String branchExternalId = null;
		if (m.matches()) {
			branchExternalId = m.group(2);
		}
		return branchExternalId;
	}

	private BranchExpenseApportionment parseAsBranchExpenseApportionment(Row row) {
		String branchCode = readAsString(BRANCH_CODE_COL, row);
		String costCode = readAsString(COST_CODE_COL, row);
		Double medicalAid = readAsDouble(MEDICAL_AID_COL, row);
		Double coProvidentFund = readAsDouble(CO_PROVIDENT_FUND_COL, row);
		Double payeUIF = readAsDouble(PAYE_UIF_COL, row);
		Double stationery = readAsDouble(STATIONERY_COL, row);
		Double courier = readAsDouble(COURIER_COL, row);
		Double bankCharges = readAsDouble(BANK_CHARGES_COL, row);
		Double insurances = readAsDouble(INSURANCE_COL, row);
		BranchExpenseApportionment branchExpenseApportionment = new BranchExpenseApportionment(branchCode, costCode);
		branchExpenseApportionment
				.setPercentageValueForExpenseType(ExpenseApportionmentType.fromInt(MEDICAL_AID_COL - 2), medicalAid);
		branchExpenseApportionment.setPercentageValueForExpenseType(
				ExpenseApportionmentType.fromInt(CO_PROVIDENT_FUND_COL - 2), coProvidentFund);
		branchExpenseApportionment.setPercentageValueForExpenseType(ExpenseApportionmentType.fromInt(PAYE_UIF_COL - 2),
				payeUIF);
		branchExpenseApportionment
				.setPercentageValueForExpenseType(ExpenseApportionmentType.fromInt(STATIONERY_COL - 2), stationery);
		branchExpenseApportionment.setPercentageValueForExpenseType(ExpenseApportionmentType.fromInt(COURIER_COL - 2),
				courier);
		branchExpenseApportionment
				.setPercentageValueForExpenseType(ExpenseApportionmentType.fromInt(BANK_CHARGES_COL - 2), bankCharges);
		branchExpenseApportionment.setPercentageValueForExpenseType(ExpenseApportionmentType.fromInt(INSURANCE_COL - 2),
				insurances);
		return branchExpenseApportionment;
	}

	@Override
	public Result upload() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result parse() {
		// TODO Auto-generated method stub
		parseExpense();
		return null;
	}

	public Result parseBankRecon(LookupSheetDataHandler lookupSheet, CommonDescMappingDataHandler commonDescMappingSheet) {
		// TODO Auto-generated method stub
		parseExpense();
		parseBankStmt(lookupSheet, commonDescMappingSheet);
		return null;
	}

	public Result populate(Workbook workbook) {
		Result result = new Result();
		Sheet bankReconciliationSheet = workbook
				.createSheet(BankReconciliationConstants.BANK_RECONCILIATION_SHEET_NAME);
		setLayout(bankReconciliationSheet);
		populateBankReconciliationSheet(bankReconciliationSheet, workbook);
		return null;
	}

	private void populateBankReconciliationSheet(Sheet bankReconciliationSheet, Workbook workbook) {
		int rowIndex = 1;
		Row row = bankReconciliationSheet.createRow(rowIndex);
		for (Records record : records) {
			if (record.getAmount() == 0
					&& record.getTransactionType().equals(BankReconciliationConstants.TRANSACTION_TYPE_ERROR)) {
				continue;
			}
			populateLongColumn(OUT_TRANSACTION_ID_COL, row, record.getTransactionId());
			populateDateColumn(OUT_TRANSACTION_DATE_COL, row, record.getTransactionDate(), "M/d/yyyy", workbook);
			populateStringColumn(OUT_DESCRIPTION_COL, row, record.getDescription());		
			populateDoubleColumn(OUT_AMOUNT_COL, row, Math.abs(record.getAmount()));
			
			populateStringColumn(OUT_MOBILE_NUMBER_COL, row, record.getMobileNumber());
			populateLongColumn(OUT_CLIENT_ACCOUNT_NO_COL, row, record.getClientAccountNumber());
			populateLongColumn(OUT_LOAN_ACCOUNT_NO_COL, row, record.getLoanAccountNumber());
			populateStringColumn(OUT_GROUP_EXTERNAL_ID_COL, row, record.getGroupExternalId());
			populateStringColumn(OUT_BRANCH_EXTERNAL_ID_COL, row, record.getBranchExternalId());
			populateStringColumn(OUT_GL_CODE_COL, row, record.getGlCode());
			populateStringColumn(OUT_ACCOUNTING_TYPE_COL, row, record.getAccountingType());
			populateStringColumn(OUT_TRANSACTION_TYPE_COL, row, record.getTransactionType());
			populateStringColumn(OUT_COMMENTS_COL, row, record.getComments());

			row = bankReconciliationSheet.createRow(++rowIndex);
		}
	}

	private void populateSplitCostCodeRecords() {

		if (expenseApportionmentAccount != null && !expenseApportionmentAccount.isEmpty()) {
			Set<String> accounts = expenseApportionmentAccount.keySet();
			for (String account : accounts) {
				ExpenseApportionmentDetails expenseApportionmentDetails = expenseApportionmentAccount.get(account);
				Double totalAmount = expenseApportionmentDetails.getAmount();
				Date date = expenseApportionmentDetails.getDate();
				for (BranchExpenseApportionment branchExpenseApportionment : branchesExpenseApportionment) {
					Double branchPercentage = branchExpenseApportionment.getPercentageValueForExpenseType(
							ExpenseApportionmentType.fromString(getAccountGLCode(account)));
					Double branchAccountAmount = branchPercentage.doubleValue() * totalAmount.doubleValue();
					String type = "CREDIT";
					if (branchAccountAmount < 0) {
						type = "DEBIT";
					} else if (branchAccountAmount == 0) {
						continue;
					}
					String branchCode = branchExpenseApportionment.getBranchCode();
					if (branchCode.equals("HO")) {
						branchCode = BankReconciliationConstants.BRANCH_EXTERNAL_ID_HO;
					}
					String accountGLCode = getAccountGLCode(account);
					records.add(new Records(null, date, "SPLIT for account[" + account + "]",
							round(branchAccountAmount.toString(), 2), null, null, null, null, branchCode, accountGLCode,
							type, BankReconciliationConstants.TRANSACTION_TYPE_OTHER, null));

				}
			}
		}
	}

	public double round(String value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_EVEN);
		return bd.doubleValue();
	}

	private void populateStringColumn(int colIndex, Row row, String value) {
		if (value != null) {
			row.createCell(colIndex).setCellValue(value);
		}
	}

	private void populateLongColumn(int colIndex, Row row, Long value) {
		if (value != null) {
			row.createCell(colIndex).setCellValue(value);
		}
	}

	private void populateDoubleColumn(int colIndex, Row row, Double value) {
		if (value != null) {
			row.createCell(colIndex).setCellValue(value);
		}
	}

	private void populateDateColumn(int colIndex, Row row, Date value, String format, Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(format));
		if (value != null) {
			writeDate(colIndex, row, value, cellStyle);
		}
	}

	private void setLayout(Sheet worksheet) {
		Row rowHeader = worksheet.createRow(0);
		writeString(OUT_TRANSACTION_ID_COL, rowHeader, BankReconciliationConstants.TRANSACTION_ID_COL_NAME);
		writeString(OUT_TRANSACTION_DATE_COL, rowHeader, BankReconciliationConstants.TRANSACTION_DATE_COL_NAME);
		writeString(OUT_DESCRIPTION_COL, rowHeader, BankReconciliationConstants.DESCRIPTION_COL_NAME);
		writeString(OUT_AMOUNT_COL, rowHeader, BankReconciliationConstants.AMOUNT_COL_NAME);
		writeString(OUT_MOBILE_NUMBER_COL, rowHeader, BankReconciliationConstants.MOBILE_NUMBER_COL_NAME);
		writeString(OUT_CLIENT_ACCOUNT_NO_COL, rowHeader, BankReconciliationConstants.CLIENT_ACCOUNT_NO_COL_NAME);
		writeString(OUT_LOAN_ACCOUNT_NO_COL, rowHeader, BankReconciliationConstants.LOAN_ACCOUNT_NO_COL_NAME);
		writeString(OUT_GROUP_EXTERNAL_ID_COL, rowHeader, BankReconciliationConstants.GROUP_EXTERNAL_ID_COL_NAME);
		writeString(OUT_BRANCH_EXTERNAL_ID_COL, rowHeader, BankReconciliationConstants.BRANCH_EXTERNAL_ID_COL_NAME);
		writeString(OUT_GL_CODE_COL, rowHeader, BankReconciliationConstants.GL_CODE_COL_NAME);
		writeString(OUT_ACCOUNTING_TYPE_COL, rowHeader, BankReconciliationConstants.ACCOUNTING_TYPE_COL_NAME);
		writeString(OUT_TRANSACTION_TYPE_COL, rowHeader, BankReconciliationConstants.TRANSACTION_TYPE_COL_NAME);
		writeString(OUT_COMMENTS_COL, rowHeader, BankReconciliationConstants.COMMENTS_COL_NAME);

	}

}
