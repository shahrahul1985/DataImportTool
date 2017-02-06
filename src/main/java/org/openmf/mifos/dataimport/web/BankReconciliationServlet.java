package org.openmf.mifos.dataimport.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmf.mifos.dataimport.handler.DataImportHandler;
import org.openmf.mifos.dataimport.handler.ImportFormatType;
import org.openmf.mifos.dataimport.handler.ImportHandlerFactory;
import org.openmf.mifos.dataimport.handler.Result;

import com.conflux.handler.reconciliation.CommonDescMappingDataHandler;
import com.conflux.handler.reconciliation.ExpenseApportionmentDataHandler;
import com.conflux.handler.reconciliation.LookupSheetDataHandler;

@WebServlet(name = "BankReconciliationServlet", urlPatterns = {"/reconciliation"})
@MultipartConfig(maxFileSize=52428800, fileSizeThreshold=52428800)
public class BankReconciliationServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String filename = "";
        try {
            Part part = request.getPart("file");
            filename = readFileName(part);
            System.out.println("Picked Up the File");
            ImportFormatType.of(part.getContentType());
            InputStream content = part.getInputStream();
            Workbook workbook = new XSSFWorkbook(content);
            LookupSheetDataHandler lookupSheet = null;
            CommonDescMappingDataHandler commonDescMappingSheet = null;
            if(workbook.getSheetIndex("Lookup") >= 0){
            	lookupSheet = new LookupSheetDataHandler(workbook);
            	lookupSheet.parse();
            }
            if(workbook.getSheetIndex("CommonDescMappingLogic") >= 0){
            	commonDescMappingSheet = new CommonDescMappingDataHandler(workbook);
            	commonDescMappingSheet.parse();
            }
            
            DataImportHandler handler = ImportHandlerFactory.createImportHandler(workbook);
            if(handler instanceof ExpenseApportionmentDataHandler){
            	Result result =((ExpenseApportionmentDataHandler) handler).parseBankRecon(lookupSheet, commonDescMappingSheet);
            	Workbook outWorkbook = new XSSFWorkbook();
            	String fileName = "bankReconciliation.xlsx";
            	((ExpenseApportionmentDataHandler) handler).populate(outWorkbook);
            	writeToStream(outWorkbook, result, response, fileName);            	
            }
            
           /* if(result.isSuccess()){
            	
            }*/
            //writeResult(workbook, result, response);
        } catch (IOException e) {
            throw new ServletException("Cannot import request. " + filename, e);
        }

    }
	
	private Result parseExpense(DataImportHandler handler, LookupSheetDataHandler lookupSheet) throws IOException {
        Result result = handler.parse();
        return result;
    }
	 
    private String readFileName(Part part) {
        String filename = null;
        for (String s : part.getHeader("content-disposition").split(";")) {
            if (s.trim().startsWith("filename")) {
                filename = s.split("=")[1].replaceAll("\"", "");
            }
        }
        return filename;
    }
    
    void writeToStream(Workbook workbook, Result result, HttpServletResponse response, String fileName) throws IOException {
		 OutputStream stream = response.getOutputStream();
		// if(result.isSuccess()) {
			     response.setContentType("application/vnd.ms-excel");
				 response.setHeader("Content-Disposition", "attachment;filename="+fileName);
	             workbook.write(stream);
	             stream.flush();
	 	         stream.close();
		/* }
		 else {
			 OutputStreamWriter out = new OutputStreamWriter(stream,"UTF-8");
			 for(String e : result.getErrors()) {
		            out.write(e);
		        }
			 out.flush();
			 out.close();
		 } */
	  }
}
