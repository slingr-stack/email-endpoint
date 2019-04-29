package io.slingr.endpoints.email.beans;

import io.slingr.endpoints.email.utils.EmailHelper;
import io.slingr.endpoints.email.utils.ToJsonConverter;
import io.slingr.endpoints.exceptions.EndpointException;
import io.slingr.endpoints.exceptions.ErrorCode;
import io.slingr.endpoints.services.AppLogs;
import io.slingr.endpoints.services.Events;
import io.slingr.endpoints.services.Files;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.utils.converters.JsonSource;
import org.apache.camel.*;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Process the new emails to convert them to the requested format
 *
 * <p>Created by lefunes on 02/09/15.
 */
public class Processor extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(Processor.class);
    public static final String INBOUND_EMAIL_RECEIVED = "inboundEmailReceived";

    private final String connection;
    private final AppLogs appLogs;
    private final Files files;
    private final Events events;

    private final Main main = new Main();

    public Processor(String connection, AppLogs appLogs, Events events, Files files) {
        this.connection = connection;
        this.appLogs = appLogs;
        this.events = events;
        this.files = files;
    }

    public void start(){
        try {
            main.addRouteBuilder(this);
            main.run();
        } catch (Exception ex){
            String message = String.format("Error when try to start the email component: %s", ex.getMessage());
            appLogs.error(message);
            throw EndpointException.permanent(ErrorCode.ARGUMENT, message, ex);
        }
    }

    public void stop(){
        try {
            main.stop();
        } catch (Exception ex){
            String message = String.format("Error when try to stop the email component: %s", ex.getMessage());
            appLogs.error(message);
            throw EndpointException.permanent(ErrorCode.ARGUMENT, message, ex);
        }
    }

    @Override
    public void configure() throws Exception {

        // predicates
        final Predicate isTextEmail = body().isInstanceOf(String.class);
        final Predicate isValidResponse = PredicateBuilder.and(body().isInstanceOf(JsonSource.class), simple("${body.isEmpty()} != true"));

        from(connection)
                .routeId("mail-inbound")
                .streamCaching()
                .choice()
                .when(isTextEmail).bean(this, "processTextEmail").endChoice()
                .otherwise().bean(this, "processEmail").endChoice()
                .end()
                .choice()
                .when(isValidResponse).bean(this, "sendEvent").endChoice()
                .otherwise().log(LoggingLevel.INFO, "Discarded empty email event.")
                .end();
    }

    @Handler
    public Json processTextEmail(@Body String body, @Headers Map<String, Object> headers){
        final Json email = Json.map();

        List<Json> ems = EmailHelper.processReceiversLine((String) headers.get("From"));
        if(ems != null && !ems.isEmpty()){
            email.setIfNotEmpty("fromEmail", ems.get(0).string("email"));
            email.setIfNotEmpty("fromName", ems.get(0).string("name"));
        }

        ems = EmailHelper.processReceiversLine((String) headers.get("To"));
        if(ems != null && !ems.isEmpty()){
            email.setIfNotEmpty("to", ems);
            email.setIfNotEmpty("toEmail", ems.get(0).string("email"));
            email.setIfNotEmpty("toName", ems.get(0).string("name"));
        }

        ems = EmailHelper.processReceiversLine((String) headers.get("Cc"));
        if(ems != null && !ems.isEmpty()){
            email.setIfNotEmpty("cc", ems);
        }

        ems = EmailHelper.processReceiversLine((String) headers.get("Bcc"));
        if(ems != null && !ems.isEmpty()){
            email.setIfNotEmpty("bcc", ems);
        }

        try {
            email.setIfNotEmpty("date", FastDateFormat.getInstance("EEE, d MMM yyyy HH:mm:ss Z").parse((String) headers.get("Date")).getTime()); 
        } catch (Exception ex){
            email.setIfNotEmpty("date", new Date().getTime());
        }
        email.setIfNotEmpty("subject", headers.get("Subject"));
        email.setIfNotEmpty("textBody", body);

        headers.put("deleted", true);
        return email;
    }

    @Handler
    public Json processEmail(@Body Object originalBody, @Headers Map<String, Object> headers, Exchange exchange) throws Exception {
        final Json body = ToJsonConverter.baseFromObject(originalBody, exchange);

        final Json email = Json.map();
        email.setIfNotEmpty("fromName", body.string("fromName"));
        email.setIfNotEmpty("fromEmail", body.string("fromEmail"));
        email.setIfNotEmpty("toName", body.string("toName"));
        email.setIfNotEmpty("toEmail", body.string("toEmail"));
        email.setIfNotEmpty("to", body.json("to"));
        email.setIfNotEmpty("cc", body.json("cc"));
        email.setIfNotEmpty("bcc", body.json("bcc"));
        email.setIfNotEmpty("date", body.longInteger("date"));
        email.setIfNotEmpty("subject", body.string("subject"));
        email.setIfNotEmpty("textBody", body.string("text"));
        email.setIfNotEmpty("htmlBody", body.string("html"));
        email.setIfNotEmpty("attachments", processAttachments(body));

        headers.put("deleted", true);
        return email;
    }

    @Handler
    public void sendEvent(@Body Json body, @Headers Map<String, Object> headers){
        events.send(INBOUND_EMAIL_RECEIVED, body);
    }

    private List<Json> processAttachments(Json json){
        final List<Json> attachments = new ArrayList<>();
        if(json != null && !json.isEmpty()){
            if(json.contains("attachment") && json.bool("attachment")){
                try {
                    final Json att = files.upload(json.string("fileName"), json.string("content"), json.string("contentType"), json.bool("base64"));
                    if (!att.isEmpty()) {
                        attachments.add(att);
                    }
                } catch (Exception ex){
                    logger.warn(String.format("Error when try to upload the file [%s] - Exception [%s]", json.string("fileName"), ex.toString()));
                }
            } else {
                if(json.contains("fileName")){
                    try {
                        final Json att = files.upload(json.string("fileName"), json.string("content"), json.string("contentType"), "BASE64".equalsIgnoreCase(json.string("encoding")));
                        if (!att.isEmpty()) {
                            attachments.add(att);
                        }
                    } catch (Exception ex){
                        logger.warn(String.format("Error when try to upload the file [%s] - Exception [%s]", json.string("fileName"), ex.toString()));
                    }
                }
                if(json.contains("parts")){
                    for (Json part : json.jsons("parts")) {
                        for (Json att : processAttachments(part)) {
                            if(att != null && !att.isEmpty()){
                                attachments.add(att);
                            }
                        }
                    }
                }
                if(json.contains("content")){
                    final Object cnt = json.object("content");
                    if(cnt instanceof Json){
                        for (Json att : processAttachments((Json) cnt)) {
                            if(att != null && !att.isEmpty()){
                                attachments.add(att);
                            }
                        }
                    }
                }
            }
        }
        return attachments;
    }
}
