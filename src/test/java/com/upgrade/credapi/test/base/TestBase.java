package com.upgrade.credapi.test.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * This base class holds all the test code to be used across testing various end points (scenarios).
 */
public class TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);
    protected static String environment;
    protected static Properties testProperties;
    protected static RequestSpecification credapiRequestSpec;
    protected static RequestSpecification missingHeaderRequestSpec;

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
        logger.info("Test Env = {}", environment);

        //load properties from corresponding environment
        testProperties = new Properties();
        InputStream resourceStream = TestBase.class.getResourceAsStream("/credapi-" + environment + ".properties");
        testProperties.load(resourceStream);
        credapiRequestSpec = createRequestSpecifications(true);
        missingHeaderRequestSpec = createRequestSpecifications(false);
    }

    /**
     * create request specifications for API call in the tests.
     */
    private RequestSpecification createRequestSpecifications(boolean addHeaders) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri(testProperties.getProperty(Constants.CREDAPI_BASE_URI))
                .setContentType(ContentType.JSON);

        if (addHeaders) {
            return requestSpecBuilder
                    .addHeader(Constants.SOURCE_ID_HEADER, Constants.SOURCE_ID_HEADER_VALUE)
                    .addHeader(Constants.CORR_ID_HEADER, UUID.randomUUID().toString())
                    .build();
        } else {
            return requestSpecBuilder.build();
        }
    }

    /**
     * @param fileName
     * @return
     * @throws Exception throws exception when the file is not found.
     */
    protected String readPayloadFromFile(String fileName) throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if(inputStream==null){
            throw new FileNotFoundException("file " + fileName + " do not exist.");
        }
        String payload = IOUtils.toString(inputStream);
        inputStream.close();
        return payload;
    }

    /**
     * Compare two JSONArray.
     *
     * @param actualApiResponse
     * @param expectedApiResponse
     * @param jsonPath
     * @param errorMessage
     */
    protected void assertJSONArray(JsonPath actualApiResponse, JsonPath expectedApiResponse, String jsonPath, String errorMessage) {
        List<Map<String, String>> actualJSONArray = actualApiResponse.get(jsonPath);
        List<Map<String, String>> expectedJSONArray = expectedApiResponse.get(jsonPath);
        if(actualJSONArray.size()!=expectedJSONArray.size()){
            logger.info("actualJSONArray={}, expectedJSONArray={}", actualJSONArray, expectedJSONArray);
            fail(errorMessage);
        }
        for(int i=0; i<actualJSONArray.size(); i++) {
            boolean jsonArrayComparison = actualJSONArray.get(i).equals(expectedJSONArray.get(i));
            if (!jsonArrayComparison) {
                logger.info("actualJSON={}, expectedJSON={}", actualJSONArray.get(i), expectedJSONArray.get(i));
            }
            assertTrue(jsonArrayComparison, errorMessage);
        }
    }

    /**
     * Compare two maps.
     *
     * @param actualApiResponse
     * @param expectedApiResponse
     * @param jsonPath
     * @param errorMessage
     */
    protected void assertMap(JsonPath actualApiResponse, JsonPath expectedApiResponse, String jsonPath, String errorMessage) {
        Map<String, String> expectedMap = expectedApiResponse.getMap(jsonPath);
        Map<String, String> actualMap = actualApiResponse.getMap(jsonPath);
        boolean jsonComparison = actualMap.equals(expectedMap);
        if (!jsonComparison) {
            logger.info("actualJSON={}, expectedJSON={}", actualMap, expectedMap);
        }
        assertTrue(jsonComparison, errorMessage);
    }
}

