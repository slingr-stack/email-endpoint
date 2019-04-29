package io.slingr.endpoints.email.beans;

import io.slingr.endpoints.utils.Json;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * <p>Tests over the Processor class
 *
 * <p>Created by lefunes on 03/09/15.
 */
public class ProcessorTest {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorTest.class);

    /*
    @Test
    public void testProcessEmail() throws Exception {
        Json response;
        final Processor p = new Processor(new EndpointsServicesManagerMock());

        response = p.processEmail(Json.fromInternalFile("/example_01.txt"), new HashMap<>());
        processExample01(response);

        response = p.processEmail(Json.fromInternalFile("/example_02.txt"), new HashMap<>());
        processExample02(response);

        response = p.processEmail(Json.fromInternalFile("/example_03.txt"), new HashMap<>());
        processExample03(response);

        response = p.processEmail(Json.fromInternalFile("/example_04.txt"), new HashMap<>());
        processExample04(response);

        response = p.processEmail(Json.fromInternalFile("/example_06.txt"), new HashMap<>());
        processExample06(response);

        logger.info("-- END");
    }

    @Test
    public void testProcessTextEmail() throws Exception {
        Json response;
        final Processor p = new Processor(new EndpointsServicesManagerMock());

        HashMap<String, Object> headers = new HashMap<>();
        headers.put("From", " Luis Enrique Funes <lefunes@slingr.io> ");
        headers.put("To", "Test Integrations <test.integrations@slingr.io>");
        headers.put("Subject", "Test endpoints");
        headers.put("Date", "Tue, 1 Sep 2015 17:16:22 -0300");
        File file = FileUtils.toFile(ProcessorTest.class.getResource("/example_05.txt"));
        assertNotNull(file);
        response = p.processTextEmail(FileUtils.readFileToString(file, Charset.defaultCharset()), headers);
        processExample05(response);

        headers = new HashMap<>();
        headers.put("From", "Test Integrations <test.integrations@slingr.io>");
        headers.put("To", "Test Integrations <test.integrations@slingr.io>, L0 <lefunes@slingr.io>,\r\n L5 <lefunes+5@slingr.io>");
        headers.put("Cc", "L2 <lefunes+2@slingr.io>, L4 <lefunes+4@slingr.io>");
        headers.put("Bcc", "L3 <lefunes+3@slingr.io>");
        headers.put("Subject", "Multiple receivers");
        headers.put("Date", "Tue, 1 Sep 2015 17:16:22 -0300");
        file = FileUtils.toFile(ProcessorTest.class.getResource("/example_05.txt"));
        assertNotNull(file);
        response = p.processTextEmail(FileUtils.readFileToString(file, Charset.defaultCharset()), headers);
        processExample05WithMultipleReceivers(response);

        logger.info("-- END");
    }
    */

    public static void processExample01(Json response) {
        assertNotNull(response);
        assertEquals("lefunes@slingr.io", response.string("fromEmail"));
        assertEquals("Luis Enrique Funes", response.string("fromName"));
        assertEquals("test.integrations@slingr.io", response.string("toEmail"));
        assertEquals("Test Integrations", response.string("toName"));
        assertEquals(Collections.singletonList(
                Json.map().set("name", "Test Integrations").set("email", "test.integrations@slingr.io")
        ), response.jsons("to"));
        assertFalse(response.contains("cc"));
        assertFalse(response.contains("bcc"));
        assertEquals("1441979179992", response.string("date"));
        assertEquals("0012", response.string("subject"));
        assertEquals("<div dir=\"ltr\">antes <img src=\"cid:ii_ie2wvoof3_14f8e935669737e5\" width=\"178\" height=\"178\"> despues<br></div>\r\n", response.string("htmlBody"));
        assertEquals("antes  despues\r\n", response.string("textBody"));

        final List<Json> attachments = response.jsons("attachments");
        assertNotNull(attachments);
        assertEquals(4, attachments.size());
        assertTrue(StringUtils.isNotBlank(attachments.get(0).string("fileId")));
        assertEquals("image/png", attachments.get(0).string("contentType"));
        assertEquals("icon.png", attachments.get(0).string("fileName"));
        assertTrue(StringUtils.isNotBlank(attachments.get(1).string("fileId")));
        assertEquals("text/plain", attachments.get(1).string("contentType"));
        assertEquals("test_doc_1.txt", attachments.get(1).string("fileName"));
        assertTrue(StringUtils.isNotBlank(attachments.get(2).string("fileId")));
        assertEquals("image/png", attachments.get(2).string("contentType"));
        assertEquals("avatar0.png", attachments.get(2).string("fileName"));
        assertTrue(StringUtils.isNotBlank(attachments.get(3).string("fileId")));
        assertEquals("application/pdf", attachments.get(3).string("contentType"));
        assertEquals("test_doc_2.pdf", attachments.get(3).string("fileName"));
    }

    public static void processExample02(Json response) {
        assertNotNull(response);
        assertEquals("lefunes@slingr.io", response.string("fromEmail"));
        assertEquals("Luis Enrique Funes", response.string("fromName"));
        assertEquals("test.integrations@slingr.io", response.string("toEmail"));
        assertEquals("Test Integrations", response.string("toName"));
        assertEquals(Collections.singletonList(
                Json.map().set("name", "Test Integrations").set("email", "test.integrations@slingr.io")
        ), response.jsons("to"));
        assertFalse(response.contains("cc"));
        assertFalse(response.contains("bcc"));
        assertEquals("1441979395257", response.string("date"));
        assertEquals("0011", response.string("subject"));
        assertEquals("<div dir=\"ltr\">asdasd</div>\r\n", response.string("htmlBody"));
        assertEquals("asdasd\r\n", response.string("textBody"));

        final List<Json> attachments = response.jsons("attachments");
        assertNotNull(attachments);
        assertEquals(1, attachments.size());
        assertTrue(StringUtils.isNotBlank(attachments.get(0).string("fileId")));
        assertEquals("image/png", attachments.get(0).string("contentType"));
        assertEquals("avatar.png", attachments.get(0).string("fileName"));
    }

    public static void processExample03(Json response) {
        assertNotNull(response);
        assertEquals("lefunes@slingr.io", response.string("fromEmail"));
        assertEquals("Luis Enrique Funes", response.string("fromName"));
        assertEquals("test.integrations@slingr.io", response.string("toEmail"));
        assertEquals("Test Integrations", response.string("toName"));
        assertEquals(Collections.singletonList(
                Json.map().set("name", "Test Integrations").set("email", "test.integrations@slingr.io")
        ), response.jsons("to"));
        assertFalse(response.contains("cc"));
        assertFalse(response.contains("bcc"));
        assertEquals("1441979495223", response.string("date"));
        assertEquals("Test 010", response.string("subject"));
        assertEquals("<div dir=\"ltr\">sdfsdf</div>\r\n", response.string("htmlBody"));
        assertEquals("sdfsdf\r\n", response.string("textBody"));

        final List<Json> attachments = response.jsons("attachments");
        assertNotNull(attachments);
        assertEquals(1, attachments.size());
        assertTrue(StringUtils.isNotBlank(attachments.get(0).string("fileId")));
        assertEquals("text/plain", attachments.get(0).string("contentType"));
        assertEquals("test_doc_1.txt", attachments.get(0).string("fileName"));
    }

    public static void processExample04(Json response) {
        assertNotNull(response);
        assertEquals("lefunes@slingr.io", response.string("fromEmail"));
        assertEquals("Luis Enrique Funes", response.string("fromName"));
        assertEquals("test.integrations@slingr.io", response.string("toEmail"));
        assertEquals("Test Integrations", response.string("toName"));
        assertEquals(Collections.singletonList(
                Json.map().set("name", "Test Integrations").set("email", "test.integrations@slingr.io")
        ), response.jsons("to"));
        assertFalse(response.contains("cc"));
        assertFalse(response.contains("bcc"));
        assertEquals("1441979598623", response.string("date"));
        assertEquals("test 009", response.string("subject"));
        assertEquals("<div dir=\"ltr\">a</div>\r\n", response.string("htmlBody"));
        assertEquals("a\r\n", response.string("textBody"));

        final List<Json> attachments = response.jsons("attachments");
        assertNotNull(attachments);
        assertEquals(0, attachments.size());
    }

    public static void processExample05(Json response) {
        assertNotNull(response);
        assertEquals("lefunes@slingr.io", response.string("fromEmail"));
        assertEquals("Luis Enrique Funes", response.string("fromName"));
        assertEquals("test.integrations@slingr.io", response.string("toEmail"));
        assertEquals("Test Integrations", response.string("toName"));
        assertEquals(Collections.singletonList(
                Json.map().set("name", "Test Integrations").set("email", "test.integrations@slingr.io")
        ), response.jsons("to"));
        assertFalse(response.contains("cc"));
        assertFalse(response.contains("bcc"));
        assertEquals("1441138582000", response.string("date"));
        assertEquals("Test endpoints", response.string("subject"));
        assertEquals("asdadas\n\n--\nLuis Enrique Funes\n", response.string("textBody"));
        assertNull(response.string("htmlBody"));

        final List<Json> attachments = response.jsons("attachments");
        assertNotNull(attachments);
        assertEquals(0, attachments.size());
    }

    public static void processExample05WithMultipleReceivers(Json response) {
        assertNotNull(response);
        assertEquals("test.integrations@slingr.io", response.string("fromEmail"));
        assertEquals("Test Integrations", response.string("fromName"));
        assertEquals("test.integrations@slingr.io", response.string("toEmail"));
        assertEquals("Test Integrations", response.string("toName"));
        assertEquals(Arrays.asList(
                Json.map().set("name", "Test Integrations").set("email", "test.integrations@slingr.io"),
                Json.map().set("name", "L0").set("email", "lefunes@slingr.io"),
                Json.map().set("name", "L5").set("email", "lefunes+5@slingr.io")
        ), response.jsons("to"));
        assertEquals(Arrays.asList(
                Json.map().set("name", "L2").set("email", "lefunes+2@slingr.io"),
                Json.map().set("name", "L4").set("email", "lefunes+4@slingr.io")
        ), response.jsons("cc"));
        assertEquals(Collections.singletonList(
                Json.map().set("name", "L3").set("email", "lefunes+3@slingr.io")
        ), response.jsons("bcc"));
        assertEquals("1441138582000", response.string("date"));
        assertEquals("Multiple receivers", response.string("subject"));
        assertEquals("asdadas\n\n--\nLuis Enrique Funes\n", response.string("textBody"));
        assertNull(response.string("htmlBody"));

        final List<Json> attachments = response.jsons("attachments");
        assertNotNull(attachments);
        assertEquals(0, attachments.size());
    }

    public static void processExample06(Json response) {
        assertNotNull(response);
        assertEquals("test.integrations@slingr.io", response.string("fromEmail"));
        assertEquals("Test Integrations", response.string("fromName"));
        assertEquals("test.integrations@slingr.io", response.string("toEmail"));
        assertEquals("Test Integrations", response.string("toName"));
        assertEquals(Arrays.asList(
                Json.map().set("name", "Test Integrations").set("email", "test.integrations@slingr.io"),
                Json.map().set("name", "L0").set("email", "lefunes@slingr.io"),
                Json.map().set("name", "L5").set("email", "lefunes+5@slingr.io")
        ), response.jsons("to"));
        assertEquals(Arrays.asList(
                Json.map().set("name", "L2").set("email", "lefunes+2@slingr.io"),
                Json.map().set("name", "L4").set("email", "lefunes+4@slingr.io")
        ), response.jsons("cc"));
        assertEquals(Collections.singletonList(
                Json.map().set("name", "L3").set("email", "lefunes+3@slingr.io")
        ), response.jsons("bcc"));
        assertEquals("1441977608824", response.string("date"));
        assertEquals("Multiple receivers", response.string("subject"));
        assertEquals("<div dir=\"ltr\">Test multiple receiver email</div>\r\n", response.string("htmlBody"));
        assertEquals("Test multiple receiver email\r\n", response.string("textBody"));

        final List<Json> attachments = response.jsons("attachments");
        assertNotNull(attachments);
        assertEquals(0, attachments.size());
    }
}