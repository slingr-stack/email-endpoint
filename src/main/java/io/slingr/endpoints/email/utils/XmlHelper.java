package io.slingr.endpoints.email.utils;

import io.slingr.endpoints.exceptions.EndpointException;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.utils.converters.JsonToXmlParser;
import io.slingr.endpoints.utils.converters.XmlToJsonParser;

/**
 * <p>Helper that permits to interact with XML documents
 *
 * <p>Created by lefunes on 28/09/15.
 */
public class XmlHelper {
    public static final String TEXT_KEY = "keyValue";
    public static final String USE_CDATA = "useCDATA";

    /**
     * Converts the XML document to an equivalent Json object using the
     * <a href="https://developer.mozilla.org/en-US/docs/JXON">the JXON principles.</a>
     *
     * @param xml XML document to convert
     * @return equivalent Json
     * @throws EndpointException if any error happens
     */
    public static Json xmlToJson(String xml) throws EndpointException {
        return XmlToJsonParser.parse(xml);
    }

    /**
     * Converts the Json to an equivalent XML document using the
     * <a href="https://developer.mozilla.org/en-US/docs/JXON">the JXON principles.</a>
     *
     * @param json Json object to convert
     * @return equivalent XML document
     * @throws EndpointException if any error happens
     */
    public static String jsonToXml(Json json) throws EndpointException {
        return JsonToXmlParser.parse(json);
    }

    /**
     * Converts the Json to an equivalent XML document using the
     * <a href="https://developer.mozilla.org/en-US/docs/JXON">the JXON principles.</a>
     *
     * @param json Json object to convert
     * @param complete if this is false, the XML header and tabulation are ignored
     * @return equivalent XML document
     * @throws EndpointException if any error happens
     */
    public static String jsonToXml(Json json, boolean complete) throws EndpointException {
        return JsonToXmlParser.parse(json, complete);
    }
}