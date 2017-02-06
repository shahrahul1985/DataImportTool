package com.conflux.handler.reconciliation;

import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Workbook;
import org.openmf.mifos.dataimport.dto.client.Client;
import org.openmf.mifos.dataimport.handler.AbstractDataImportHandler;
import org.openmf.mifos.dataimport.handler.Result;
import org.openmf.mifos.dataimport.http.RestClient;

import com.conflux.bank.transactions.Records;

public class BankReconcilationDataHandler extends AbstractDataImportHandler{
	
	private final Workbook workbook;
	private final ArrayList<Records> records;
	
    public BankReconcilationDataHandler(Workbook workbook) {
        this.workbook = workbook;
        records = new ArrayList<Records>();
    }

	@Override
	public Result parse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result upload() {
		// TODO Auto-generated method stub
		return null;
	}

}
