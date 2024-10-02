package com.demo.tally_db_sync.controller;

import com.demo.tally_db_sync.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tally")
public class SyncController {

    @Autowired
    SyncService service;

    @GetMapping("/export-to-db")
    public String getExportTallyDataToDb() {
        try {
            return service.getExportTallyDataToDb();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Unable to fetch data from Tally\"}";
        }
    }

    @PostMapping("/import-to-tally")
    public String saveImportDataToTally(@RequestParam String xmlImportData) {
        try {
            return service.saveImportDataToTally(xmlImportData);
        } catch (Exception e){
            e.printStackTrace();
            return "{\"error\": \"Unable import data to Tally\"}";
        }
    }

/*
    To add primary stock item with quantity,rate & amount Eg given input below;
    String xmlImportData = """
        <ENVELOPE>
        	<HEADER>
        		<TALLYREQUEST>Import Data</TALLYREQUEST>
        	</HEADER>
        	<BODY>
        		<IMPORTDATA>
        			<REQUESTDESC>
        				<REPORTNAME>All Masters</REPORTNAME>
        			</REQUESTDESC>
        			<REQUESTDATA>
        				<TALLYMESSAGE
        					xmlns:UDF="TallyUDF">
        					<STOCKITEM NAME="Biscuit" ACTION="Alter">
        						<NAME.LIST>
        							<NAME>Biscuit</NAME>
        						</NAME.LIST>
        						<BASEUNITS>$</BASEUNITS>
        						<OPENINGBALANCE>3000.00</OPENINGBALANCE>
        						<RATE>10 $</RATE>
        						<OPENINGVALUE>-30000.00</OPENINGVALUE>
        					</STOCKITEM>
        				</TALLYMESSAGE>
        			</REQUESTDATA>
        		</IMPORTDATA>
        	</BODY>
        </ENVELOPE>
        """;
*/

/*  Purchase voucher tag for purchase stock item with quantity,rate & amount Eg given input below;
    String xmlImportData = """
            <ENVELOPE>
                <HEADER>
                    <TALLYREQUEST>Import Data</TALLYREQUEST>
                </HEADER>
                <BODY>
                    <IMPORTDATA>
                        <REQUESTDESC>
                            <REPORTNAME>All Masters</REPORTNAME>
                        </REQUESTDESC>
                        <REQUESTDATA>
                            <TALLYMESSAGE xmlns:UDF="TallyUDF">
                                <VOUCHER VCHTYPE="Purchase" ACTION="Create">
                                    <DATE>20240401</DATE>
                                    <VCHSTATUSDATE>20240401</VCHSTATUSDATE>
                                    <VOUCHERNUMBER>30</VOUCHERNUMBER>
                                    <VOUCHERTYPENAME>Purchase</VOUCHERTYPENAME>
                                    <PARTYLEDGERNAME>Party1</PARTYLEDGERNAME>
                                    <ISINVOICE>Yes</ISINVOICE>
                                    <LEDGERENTRIES.LIST>
                                        <LEDGERNAME>Party1</LEDGERNAME>
                                        <ISDEEMEDPOSITIVE>Yes</ISDEEMEDPOSITIVE>
                                        <AMOUNT>-10000.00</AMOUNT>
                                    </LEDGERENTRIES.LIST>
                                    <ALLINVENTORYENTRIES.LIST>
                                        <STOCKITEMNAME>Biscuit</STOCKITEMNAME>
                                        <GODOWNNAME>Chennai Hub</GODOWNNAME>
                                        <ACTUALQTY>-100.00</ACTUALQTY>
                                        <BILLEDQTY>-100.00</BILLEDQTY>
                                        <RATE>100.00</RATE>
                                        <AMOUNT>-10000.00</AMOUNT>
                                        <ACCOUNTINGALLOCATIONS.LIST>
                                            <LEDGERNAME>PurchaseMilk</LEDGERNAME>
                                            <ISDEEMEDPOSITIVE>No</ISDEEMEDPOSITIVE>
                                            <AMOUNT>10000.00</AMOUNT>
                                        </ACCOUNTINGALLOCATIONS.LIST>
                                        <BATCHALLOCATIONS.LIST>
                                            <BATCHNAME>Primary Batch</BATCHNAME>
                                            <GODOWNNAME>Chennai Hub</GODOWNNAME>
                                            <ACTUALQTY>100.00</ACTUALQTY>
                                            <BILLEDQTY>100.00</BILLEDQTY>
                                            <AMOUNT>10000.00</AMOUNT>
                                        </BATCHALLOCATIONS.LIST>
                                    </ALLINVENTORYENTRIES.LIST>
                                </VOUCHER>
                            </TALLYMESSAGE>
                        </REQUESTDATA>
                    </IMPORTDATA>
                </BODY>
            </ENVELOPE>
           """;
*/

/*    Sales voucher tag for sales stock item with quantity,rate & amount Eg given input below;
    String xmlImportData = """
           <ENVELOPE>
              <HEADER>
                  <TALLYREQUEST>Import Data</TALLYREQUEST>
              </HEADER>
              <BODY>
                  <IMPORTDATA>
                      <REQUESTDESC>
                          <REPORTNAME>All Masters</REPORTNAME>
                      </REQUESTDESC>
                      <REQUESTDATA>
                          <TALLYMESSAGE xmlns:UDF="TallyUDF">
                              <VOUCHER VCHTYPE="Sales" ACTION="Create">
                                  <DATE>20240401</DATE>
                                  <VCHSTATUSDATE>20240401</VCHSTATUSDATE>
                                  <VOUCHERNUMBER>24</VOUCHERNUMBER>
                                  <VOUCHERTYPENAME>Sales</VOUCHERTYPENAME>
                                  <PARTYLEDGERNAME>Party1</PARTYLEDGERNAME>
                                  <ISINVOICE>Yes</ISINVOICE>
                                  <LEDGERENTRIES.LIST>
                                      <LEDGERNAME>Party1</LEDGERNAME>
                                      <ISDEEMEDPOSITIVE>No</ISDEEMEDPOSITIVE>
                                      <AMOUNT>20000.00</AMOUNT>
                                  </LEDGERENTRIES.LIST>
                                  <ALLINVENTORYENTRIES.LIST>
                                      <STOCKITEMNAME>BaseBall</STOCKITEMNAME>
                                      <GODOWNNAME>Chennai Hub</GODOWNNAME>
                                      <ACTUALQTY>-100.00</ACTUALQTY>
                                      <BILLEDQTY>-100.00</BILLEDQTY>
                                      <RATE>200.00</RATE>
                                      <AMOUNT>-20000.00</AMOUNT>
                                      <ACCOUNTINGALLOCATIONS.LIST>
                                          <LEDGERNAME>Sales</LEDGERNAME>
                                          <ISDEEMEDPOSITIVE>Yes</ISDEEMEDPOSITIVE>
                                          <AMOUNT>-20000.00</AMOUNT>
                                      </ACCOUNTINGALLOCATIONS.LIST>
                                      <BATCHALLOCATIONS.LIST>
                                          <BATCHNAME>Primary Batch</BATCHNAME>
                                          <GODOWNNAME>Chennai Hub</GODOWNNAME>
                                          <ACTUALQTY>-100.00</ACTUALQTY>
                                          <BILLEDQTY>-100.00</BILLEDQTY>
                                          <AMOUNT>-20000.00</AMOUNT>
                                      </BATCHALLOCATIONS.LIST>
                                  </ALLINVENTORYENTRIES.LIST>
                              </VOUCHER>
                          </TALLYMESSAGE>
                      </REQUESTDATA>
                  </IMPORTDATA>
              </BODY>
          </ENVELOPE>
          """;
*/

}
