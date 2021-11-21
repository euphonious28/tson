package com.euph28.tson.restclientinterface.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.reporter.report.Report;
import com.euph28.tson.reporter.report.ReportType;
import com.euph28.tson.reporter.TSONReporter;
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
    public boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, String value) {
        // Send request
        tsonRestClient.setRequestBody(value, true);
        tsonRestClient.send();

        // Report
        Report report = tsonReporter.getReport();
        report.setReportType(ReportType.INFO);
        report.setReportFallbackTitle("Send " + value);
        report.setReportStep(String.format("Send %s to %s", value, tsonRestClient.getRequestData().getRequestUrl()));
        return true;
    }
}
