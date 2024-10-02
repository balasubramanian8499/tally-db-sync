package com.demo.tally_db_sync.service;

import com.demo.tally_db_sync.model.TallyJson;
import com.demo.tally_db_sync.repo.TallyJsonRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);

    private static final String TALLY_URL = "http://localhost:9000";

    @Autowired
    TallyJsonRepository tallyJsonRepository;

    public String getExportTallyDataToDb() {

        String requestXml = createXmlRequest();
        String xmlResponse = sendRequestToTally(requestXml);
        logger.info("Xml Response: {}", xmlResponse);

        String jsonResponse = convertXmlToJson(xmlResponse);

        logger.info("JSON Response: {}", jsonResponse);
        saveJsonToMysqlDb(jsonResponse);
        return xmlResponse;
    }

    public String sendRequestToTally(String requestXml) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(requestXml, headers);
        ResponseEntity<String> response = restTemplate.exchange(TALLY_URL, HttpMethod.POST, request, String.class);
        return response.getBody();
    }

    private String createXmlRequest() {
        return """
                <ENVELOPE>
                  <HEADER>
                    <TALLYREQUEST>Export Data</TALLYREQUEST>
                  </HEADER>
                  <BODY>
                    <EXPORTDATA>
                      <REQUESTDESC>
                        <STATICVARIABLES>
                          <EXPLODEALLLEVELS>YES</EXPLODEALLLEVELS>
                          <EXPLODEFLAG>YES</EXPLODEFLAG>
                          <DSPSHOWALLACCOUNTS>Yes</DSPSHOWALLACCOUNTS>
                          <DSPSHOWOPENING>Yes</DSPSHOWOPENING>
                          <DSPSHOWINWARDS>YES</DSPSHOWINWARDS>
                          <DSPSHOWOUTWARDS>YES</DSPSHOWOUTWARDS>
                          <DSPSHOWCLOSING>Yes</DSPSHOWCLOSING>
                          <ISITEMWISE>No</ISITEMWISE>
                          <SVCURRENTCOMPANY>Susse</SVCURRENTCOMPANY>
                        </STATICVARIABLES>
                        <REPORTNAME>Stock Summary</REPORTNAME>
                      </REQUESTDESC>
                    </EXPORTDATA>
                  </BODY>
                </ENVELOPE>
                """;
    }

    public void saveJsonToMysqlDb(String jsonResponse) {
        try {
            logger.info("Saving JSON data");
            TallyJson tallyJson = new TallyJson();
            tallyJson.setJsonResponse(jsonResponse);
            tallyJsonRepository.save(tallyJson);
            logger.info("JSON data saved successfully.");
        } catch (Exception e) {
            logger.error("Error saving JSON data to Mysql: ", e);
        }
    }

    private String convertXmlToJson(String xml) {
        JSONObject xmlJSONObj = XML.toJSONObject(xml);

        JSONObject envelope = xmlJSONObj.getJSONObject("ENVELOPE");
        JSONArray dspStkInfoArray = envelope.getJSONArray("DSPSTKINFO");

        for (int i = 0; i < dspStkInfoArray.length(); i++) {
            JSONObject stockInfo = dspStkInfoArray.getJSONObject(i);

            JSONObject dspStkOut = stockInfo.getJSONObject("DSPSTKOUT");
            JSONObject renamedOut = new JSONObject();
            renamedOut.put("Outwards", dspStkOut);
            stockInfo.remove("DSPSTKOUT");
            stockInfo.put("DSPSTKOUT", renamedOut);

            JSONObject dspStkCl = stockInfo.getJSONObject("DSPSTKCL");
            JSONObject renamedCl = new JSONObject();
            renamedCl.put("Closing", dspStkCl);
            stockInfo.remove("DSPSTKCL");
            stockInfo.put("DSPSTKCL", renamedCl);

            JSONObject dspStkOp = stockInfo.getJSONObject("DSPSTKOP");
            JSONObject renamedOp = new JSONObject();
            renamedOp.put("Opening", dspStkOp);
            stockInfo.remove("DSPSTKOP");
            stockInfo.put("DSPSTKOP", renamedOp);

            JSONObject dspStkIn = stockInfo.getJSONObject("DSPSTKIN");
            JSONObject renamedIn = new JSONObject();
            renamedIn.put("Inwards", dspStkIn);
            stockInfo.remove("DSPSTKIN");
            stockInfo.put("DSPSTKIN", renamedIn);
        }
        return xmlJSONObj.toString(4);
    }

    public String saveImportDataToTally(String xmlImportData) {
        String xmlResponse = sendRequestToTally(xmlImportData);
        logger.info("Import JSON Response: {}", xmlResponse);
        return xmlResponse;
    }

}

