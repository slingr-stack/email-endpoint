package io.slingr.endpoints.email;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Test over the EmailEndpoint class
 *
 * <p>Created by lefunes on 08/31/15.
 */
public class EmailEndpointTest {
    private static final Logger logger = LoggerFactory.getLogger(EmailEndpointTest.class);

    /*
    @Override
    protected EmailEndpoint createEndpoint() {
        return new EmailEndpoint();
    }

    @Override
    protected String getPropertyFile() {
        return "endpoint.properties";
    }

    @Test
    public void testIsAlive() throws Exception {
        Json res = endpoint.isAlive();
        assertNotNull(res);
        assertTrue(res.contains("started"));

        logger.info("-- END");
    }

    @Test
    public void testHealthCheck() throws Exception {
        Json res = Json.map();

        boolean response = endpoint.healthCheck(res);
        assertTrue(response);

        assertNotNull(res);
        assertTrue(res.contains("connectionInfo"));
        assertFalse(res.json("connectionInfo").isEmpty());
        assertEquals("test.integrations%40slingr.io", res.json("connectionInfo").string("user"));
        assertTrue(res.json("connectionInfo").bool("password", true));

        logger.info("-- END");
    }
    */
}
