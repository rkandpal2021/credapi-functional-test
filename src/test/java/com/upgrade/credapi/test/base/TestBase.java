package com.upgrade.credapi.test.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

/**
 * This base class holds all the test code to be used across testing various end points (scenarios).
 */
public class TestBase {
    private static Logger logger = LoggerFactory.getLogger(TestBase.class);
    protected static String environment;
    protected static Properties testProperties;
    protected static RequestSpecification credapiRequestSpec;

    /**
     * In this method we initialize all the required parameters that are
     * used across various test.
     *
     * @throws IOException - throws exception when the environment property file is not found.
     */
    @BeforeSuite
    public void setup() throws IOException {
        //get the test environment
        environment = System.getProperty("TEST_ENV");
        if (environment == null) {
            environment = Constants.DEFAULT_TEST_ENV;
        }
        logger.info("Test env = {}", environment);

        //load properties from corresponding environment
        testProperties = new Properties();
        InputStream resourceStream = TestBase.class.getResourceAsStream("/credapi-" + environment + ".properties");
        testProperties.load(resourceStream);
        createRequestSpecifications();
    }

    /**
     * create request specifications for API call in the tests.
     */
    private void createRequestSpecifications() {
        credapiRequestSpec = new RequestSpecBuilder()
                .setBaseUri(testProperties.getProperty(Constants.CREDAPI_BASE_URI))
                .addHeader(Constants.SOURCE_ID_HEADER, Constants.SOURCE_ID_HEADER_VALUE)
                .addHeader(Constants.CORR_ID_HEADER, UUID.randomUUID().toString())
                .setContentType(ContentType.JSON)
                .build();
    }

    /**
     * @param fileName
     * @return
     * @throws IOException throws exception when the file is not found.
     */
    protected String readPayloadFromFile(String fileName) throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }


    /**
     * This method is called internally by functional test to submit a API request and validate it's response.
     * @param requestSpecification
     * @param requestBody
     * @param expectedResponsePayload
     * @param responseCode
     * @throws IOException   when request or response file does not exists.
     * @throws JSONException this exception is throw when either expected payload file or server response is not proper json.
     */
    protected void test(RequestSpecification requestSpecification, String requestBody, String expectedResponsePayload, int responseCode) throws IOException, JSONException {
        String requestPayload = readPayloadFromFile(requestBody);
        Response response = given().spec(requestSpecification).body(requestPayload).post(Constants.Login_API);
        //compare response code
        assertEquals(response.getStatusCode(), responseCode);
        String expectedPayload = readPayloadFromFile(expectedResponsePayload);
        //compare the response payload using JSONAssert
        JSONAssert.assertEquals(expectedPayload, response.asString(), new CustomComparator(JSONCompareMode.STRICT,
                new Customization(Constants.TIMESTAMP_FIELD_NAME, (o1, o2) -> true)
        ));
    }
}
