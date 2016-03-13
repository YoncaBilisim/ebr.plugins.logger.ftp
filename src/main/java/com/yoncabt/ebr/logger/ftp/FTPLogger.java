/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoncabt.ebr.logger.ftp;

import com.yoncabt.abys.core.util.EBRConf;
import com.yoncabt.ebr.ReportOutputFormat;
import com.yoncabt.ebr.ReportRequest;
import com.yoncabt.ebr.logger.ReportLogger;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author myururdurmaz
 */
public class FTPLogger implements ReportLogger {

    @PostConstruct
    private void init() {
    }

    @Override
    public void logReport(ReportRequest request, ReportOutputFormat outputFormat, InputStream reportData) throws IOException {
        FTPClient ftp = createClient();
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.storeFile(request.getUuid(), reportData);
        ftp.disconnect();
    }

    private FTPClient createClient() throws IOException {
        FTPClient ftp = new FTPClient();
        FTPClientConfig config = new FTPClientConfig();
        ftp.configure(config);
        ftp.connect(EBRConf.INSTANCE.getValue("report.logger.ftplogger.host", "localhost"), EBRConf.INSTANCE.getValue("report.logger.ftplogger.port", 21));
        ftp.login(EBRConf.INSTANCE.getValue("report.logger.ftplogger.user", "anoynymous"), EBRConf.INSTANCE.getValue("report.logger.ftplogger.pass", "@yoncabt.com.tr"));

        int reply = ftp.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("ftp status " + reply + " " + ftp.getReplyString());
        }
        return ftp;
    }

    @Override
    public byte[] getReportData(String uuid) throws IOException {
        FTPClient ftp = createClient();
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        try {
            return IOUtils.toByteArray(ftp.retrieveFileStream(uuid));
        } finally {
            ftp.disconnect();
        }
    }

}
