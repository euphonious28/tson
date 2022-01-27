package com.euph28.tson.restclientinterface.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.interpreter.Statement;
import com.euph28.tson.reporter.TSONReporter;
import com.euph28.tson.reporter.report.Report;
import com.euph28.tson.reporter.report.ReportType;
import com.euph28.tson.restclientinterface.TSONRestClient;

/**
 * Rest Client Keyword: Send request with specified body
 * <p>
 * Sends a request using the Rest Client with the provided {@code value} as the {@code requestBody}
 */
public class KeywordSend extends KeywordBase {

    /* ----- CONSTRUCTOR ------------------------------ */
    public KeywordSend(TSONRestClient tsonRestClient) {
        super(tsonRestClient);
    }

    /* ----- OVERRIDE: KeywordBase ------------------------------ */
    @Override
    public String getCode() {
        return "SEND";
    }

    @Override
    public String getLspDescriptionShort() {
        return "JSON request";
    }

    @Override
    public String getLspDescriptionLong() {
        return "Path to JSON body to be sent";
    }

    @Override
    public KeywordType getKeywordType() {
        return KeywordType.ACTION;
    }

    @Override
    public boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, Statement statement) {
        // Send request
        tsonRestClient.setRequestBody(statement.getValue(), true);
        tsonRestClient.transformRequestBody(tsonContext::resolveContent);
        tsonRestClient.send();

        // Report
        Report report = tsonReporter.getReport();
        report.setReportType(ReportType.INFO);
        report.setReportFallbackTitle("Send " + statement.getValue());
        report.setReportStep(String.format("Send %s to %s", statement.getValue(), tsonRestClient.getRequestData().getRequestUrl()));
        report.addAttachment("request.json", tsonRestClient.getRequestData().getRequestBody());
        report.addAttachment("response.json", tsonRestClient.getResponseData().getResponseBody());
        report.addAttachment("time_start", String.valueOf(tsonRestClient.getResponseData().getTimeStart()));
        report.addAttachment("time_connect", String.valueOf(tsonRestClient.getResponseData().getTimeConnect()));
        report.addAttachment("time_response", String.valueOf(tsonRestClient.getResponseData().getTimeResponse()));
        report.addAttachment("time_end", String.valueOf(tsonRestClient.getResponseData().getTimeEnd()));
        return true;
    }
}
