package com.conflux.handler.reconciliation;

import java.util.HashMap;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.handler.AbstractDataImportHandler;
import org.openmf.mifos.dataimport.handler.Result;

public class LookupSheetDataHandler extends AbstractDataImportHandler {

	private static final int SHORT_CODE_COL = 0;
	private static final int PASTEL_CATEGORY_COL = 1;
	private static final int BANK_STMT_TYPE_AND_PASTEL_CATEGORY_COL = 2;
	private static final int PASTEL_ACCOUNT_COL = 3;
	private static final int COST_CODE_COL = 4;

	private final Workbook workbook;
	private HashMap<String, LookupRecord> lookupRecordMap;

	public LookupSheetDataHandler(Workbook workbook) {
		this.workbook = workbook;
		lookupRecordMap = new HashMap<String, LookupRecord>();
	}

	public void setLookupRecord(String bankstmtTypeAndPastelCategory, LookupRecord lookupRecord) {
		lookupRecordMap.put(bankstmtTypeAndPastelCategory, lookupRecord);
	}

	public LookupRecord getLookupRecord(String bankstmtTypeAndPastelCategory) {
		return lookupRecordMap.get(bankstmtTypeAndPastelCategory);
	}

	@Override
	public Result parse() {
		// TODO Auto-generated method stub
		Result result = new Result();
		Sheet lookupSheet = workbook.getSheet("Lookup");
		Integer noOfEntries = getNumberOfRows(lookupSheet, 0);
		for (int rowIndex = 2; rowIndex < noOfEntries; rowIndex++) {
			Row row;
			try {
				row = lookupSheet.getRow(rowIndex);
				parseLookupSheet(row);
			} catch (Exception e) {
				result.addError("Row = " + rowIndex + " , " + e.getMessage());
			}
		}
		return null;
	}

	public void parseLookupSheet(Row row) {
		String shortCode = readAsString(SHORT_CODE_COL, row);
		String costCode = readAsString(COST_CODE_COL, row);
		String pastelCategory = readAsString(PASTEL_CATEGORY_COL, row);
		String bankStmtAndPastelCategoryCode = readAsString(BANK_STMT_TYPE_AND_PASTEL_CATEGORY_COL, row);
		String pastelAccount = readAsString(PASTEL_ACCOUNT_COL, row);
		if (pastelAccount != null) {
			setLookupRecord(pastelAccount,
					new LookupRecord(shortCode, bankStmtAndPastelCategoryCode, pastelCategory, pastelAccount, costCode));
		}

	}

	@Override
	public Result upload() {
		// TODO Auto-generated method stub
		return null;
	}

}
