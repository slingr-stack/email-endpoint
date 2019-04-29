package io.slingr.endpoints.email;

import io.slingr.endpoints.Endpoint;
import io.slingr.endpoints.email.beans.Processor;
import io.slingr.endpoints.framework.annotations.EndpointProperty;
import io.slingr.endpoints.framework.annotations.SlingrEndpoint;
import io.slingr.endpoints.utils.Json;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Email endpoint
 *
 * <p>Created by lefunes on 08/31/15.
 */
@SlingrEndpoint(name = "email")
public class EmailEndpoint extends Endpoint {
    private static final Logger logger = LoggerFactory.getLogger(EmailEndpoint.class);

    @EndpointProperty(name = "type")
    private String type;

    @EndpointProperty(name = "imapServer")
    private String host;

    @EndpointProperty(name = "imapPort")
    private String port;

    @EndpointProperty(name = "imapUser")
    private String user;

    @EndpointProperty(name = "imapPassword")
    private String password;

    @EndpointProperty(name = "imapFolder")
    private String fromFolder;

    @EndpointProperty(name = "imapMoveToFolder")
    private String toFolder;

    @EndpointProperty(name = "imapCheckFrequency")
    private String checkFrequency;

    private Processor processor = null;

    @Override
    public void endpointStarted() {
        // options
        final List<String> options = new ArrayList<>();
        final Json parametersToPrint = Json.map();

        String protocol = "imaps";
        String protocolPort = this.port;
        boolean imap = true;

        final String tParameter = type;
        if(StringUtils.isNotBlank(tParameter)){
            switch (tParameter.trim().toLowerCase()){
                case "imap":
                    protocol = "imap";
                    if(StringUtils.isBlank(protocolPort)){
                        protocolPort = "143";
                    }
                    break;
                case "pop3":
                    imap = false;
                    protocol = "pop3";
                    if(StringUtils.isBlank(protocolPort)){
                        protocolPort = "110";
                    }
                    break;
                case "pop3s":
                    imap = false;
                    protocol = "pop3s";
                    if(StringUtils.isBlank(protocolPort)){
                        protocolPort = "995";
                    }
                    break;
                default:
                    if(StringUtils.isBlank(protocolPort)){
                        protocolPort = "993";
                    }
            }
        }

        final String mPassword = StringUtils.isBlank(this.password) ? "-" : StringUtils.repeat("*", this.password.trim().length());

        logger.info(String.format("Configured Email endpoint: protocol [%s] user [%s/%s] host [%s:%s] folders [%s] to [%s] every [%s] ms", protocol, this.user, mPassword, this.host, protocolPort, this.fromFolder, this.toFolder, this.checkFrequency));

        parametersToPrint.set("type", protocol);
        parametersToPrint.set("user", user);
        parametersToPrint.set("host", host);
        parametersToPrint.set("port", protocolPort);

        if (StringUtils.isNotBlank(password)) {
            options.add(String.format("password=%s", password));
            parametersToPrint.set("password", true);
        } else {
            parametersToPrint.set("password", false);
        }

        options.add(String.format("folderName=%s", fromFolder));
        parametersToPrint.set("folderName", fromFolder);

        parametersToPrint.set("imap", imap);
        if(imap) {
            options.add(String.format("copyTo=%s", toFolder));
            parametersToPrint.set("copyTo", toFolder);

            options.add("unseen=true");
            parametersToPrint.set("unseen", true);
        } else {
            options.add("unseen=false");
            parametersToPrint.set("unseen", false);
        }

        options.add("delete=true");
        parametersToPrint.set("delete", true);

        options.add(String.format("consumer.delay=%s", checkFrequency));
        parametersToPrint.set("consumer.delay", checkFrequency);

        options.add("connectionTimeout=120000");
        parametersToPrint.set("connectionTimeout", 120000);

        options.add("skipFailedMessage=true");
        parametersToPrint.set("skipFailedMessage", true);

        options.add("disconnect=true");
        parametersToPrint.set("disconnect", true);

        //options.add("debugMode=true");
        //parametersToPrint.set("debugMode", true);

        logger.info(String.format("Starting Email endpoint with parameters: %s", parametersToPrint.toString()));

        final String uri = String.format("%s://%s@%s:%s?%s", protocol, user, host, protocolPort, StringUtils.join(options, "&"));
        processor = new Processor(uri, appLogs(), events(), files());
        processor.start();
    }

    @Override
    public void endpointStopped(String cause) {
        logger.info(String.format("Stopping Email endpoint: %s", cause));

        if(processor != null){
            processor.stop();
        }
    }
}
