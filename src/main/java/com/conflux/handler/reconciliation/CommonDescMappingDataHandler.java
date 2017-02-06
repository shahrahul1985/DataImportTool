package com.conflux.handler.reconciliation;

import java.util.HashMap;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.handler.AbstractDataImportHandler;
import org.openmf.mifos.dataimport.handler.Result;

public class CommonDescMappingDataHandler extends AbstractDataImportHandler {
	
	private static final int DESC_COL = 0;
	private static final int GL_DESC_COL = 1;
	private static final int GL_ACCOUNT_COL = 2;
	private static final int COST_CODE_COL = 3;
	
	private final Workbook workbook;
	private HashMap<String, CommonDescMappingRecord> commonDescMappingRecordMap;
	
	public CommonDescMappingDataHandler(Workbook workbook) {
		this.workbook = workbook;
		commonDescMappingRecordMap = new HashMap<String, CommonDescMappingRecord>();
	}
	
	public void setCommonDescMappingRecord(String description, CommonDescMappingRecord commonDescMappingRecord) {
		commonDescMappingRecordMap.put(description, commonDescMappingRecord);
	}

	public CommonDescMappingRecord getCommonDescMappingRecord(String description) {
		return commonDescMappingRecordMap.get(description);
	}
	
	public Result parse() {
		// TODO Auto-generated method stub
		Result result = new Result();
		Sheet commonDescMappingSheet = workbook.getSheet("CommonDescMappingLogic");
		Integer noOfEntries = getNumberOfRows(commonDescMappingSheet, 0);
		for (int rowIndex = 2; rowIndex < noOfEntries; rowIndex++) {
			Row row;
			try {
				row = commonDescMappingSheet.getRow(rowIndex);
				parseCommonDescMappingSheet(row);
			} catch (Exception e) {
				result.addError("Row = " + rowIndex + " , " + e.getMessage());
			}
		}
		return null;
	}

	private void parseCommonDescMappingSheet(Row row) {
		// TODO Auto-generated method stub
		String desc = readAsString(DESC_COL, row);
		String costCode = readAsString(COST_CODE_COL, row);
		String glDesc = readAsString(GL_DESC_COL, row);
		String glAccount = readAsString(GL_ACCOUNT_COL, row);
		if (glAccount != null) {
			setCommonDescMappingRecord(desc.toLowerCase().trim(),
					new CommonDescMappingRecord(desc, glDesc, glAccount, costCode));
		}
	}

	@Override
	public Result upload() {
		// TODO Auto-generated method stub
		return null;
	}

}
