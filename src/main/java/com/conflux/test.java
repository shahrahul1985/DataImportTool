package com.conflux;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmf.mifos.dataimport.handler.Result;

import com.conflux.handler.reconciliation.BankReconciliationConstants;

public class test {
	
	public static final String CLIENT_DEPOSIT_GROUP_CODE_PATTERN_DESC = "(.*)([LMK][a-zA-Z]{3}[\\s]?[-]?[0-9]{3})(.*)";
	public static final String FEE_CODE_PATTERN_DESC = "^CASH DEPOSIT FEE$";
	public static final String ACCOUNT_CODE_PATTERN_DESC = "^ACCOUNT STATEMENT FEE \\(ON REQUEST\\)$";
	public static final String CASH_DEPOSIT_PATTERN_DESC = "(.*)CASH DEPOSIT(.*)";
	
	public static void main(String[] args){
		String file = "F:/notToBeDeleted/Phakamani/BankStmtReconcilation/SAPOTest.xlsx";
		try {
			FileInputStream fis = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(fis);
			Result result = new Result();
			Sheet expenseApportionmentSheet = workbook.getSheet("Sheet1");
			int counter = 0;
			int errorCounter = 0;
			int feesCounter = 0;
			int accountCounter = 0;
			int cashDepositCounter = 0;
			Integer noOfEntries = getNumberOfRows(expenseApportionmentSheet, 0);
			for (int rowIndex = 2; rowIndex < noOfEntries; rowIndex++) {
				Row row;
				try {
					row = expenseApportionmentSheet.getRow(rowIndex);
					String branchCode = readAsString(1, row);
					Matcher m = Pattern.compile(CLIENT_DEPOSIT_GROUP_CODE_PATTERN_DESC).matcher(branchCode.replaceAll("\\s", ""));
					Matcher mFees = Pattern.compile(FEE_CODE_PATTERN_DESC).matcher(branchCode);
					Matcher mAccountFees = Pattern.compile(ACCOUNT_CODE_PATTERN_DESC).matcher(branchCode);
					Matcher mCashDeposit = Pattern.compile(CASH_DEPOSIT_PATTERN_DESC).matcher(branchCode.trim());
					
					String branchExternalId = null;
					if (m.matches()) {
						branchExternalId = m.group(2);
						//System.out.println(branchExternalId);
						counter++;
					}else if (mFees.matches()) {
						feesCounter++;
					} else if(mAccountFees.matches()){
						accountCounter++;
					} else if(mCashDeposit.matches()){
						cashDepositCounter++ ;
					} else {
						System.out.println(branchCode);
						errorCounter++;
					}
					
				} catch (Exception e) {
					result.addError("Row = " + rowIndex + " , " + e.getMessage());
				}
			}
			System.out.println("Counter[" + counter + "], ErrorCounter[" + errorCounter + "], "
					+ "feesCounter[" + feesCounter + "], AccountCounter[" + accountCounter + "], "
							+ "CashDepositCounter[" + cashDepositCounter + "]");
			workbook.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public static Integer getNumberOfRows(Sheet sheet, int primaryColumn) {
	        Integer noOfEntries = 1;
	        // getLastRowNum and getPhysicalNumberOfRows showing false values
	        // sometimes
	           while (sheet.getRow(noOfEntries) !=null && sheet.getRow(noOfEntries).getCell(primaryColumn) != null) {
	               noOfEntries++;
	           }
	        	
	        return noOfEntries;
	    }
	 
	 
	 public static String readAsString(int colIndex, Row row) {
	        try {
	        	Cell c = row.getCell(colIndex);
	        	if (c == null || c.getCellType() == Cell.CELL_TYPE_BLANK )
	        		return "";
	        	String res = null;
				if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
					switch (c.getCachedFormulaResultType()) {
					case Cell.CELL_TYPE_ERROR:
						return "";
					case Cell.CELL_TYPE_NUMERIC:
						res = NumberToTextConverter.toText(c.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						res = c.getRichStringCellValue().getString();
			             break;
	        		 }
	        	}else{
	        		switch(c.getCellType()){
	        		case Cell.CELL_TYPE_ERROR:
						return "";
					case Cell.CELL_TYPE_NUMERIC:
						res = NumberToTextConverter.toText(c.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						res = c.getRichStringCellValue().getString();
			             break;
	        		}
	        	}
	        	//res = trimEmptyDecimalPortion(c.getStringCellValue().trim());        	        	
	            return res.trim();
	        } catch (Exception e) {
	        	e.printStackTrace();
	            return ((Double)row.getCell(colIndex).getNumericCellValue()).intValue() + "";
	        }
	    }

}
