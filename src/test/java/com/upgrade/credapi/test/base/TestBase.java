package com.upgrade.credapi.test.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

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
    private void createRequestSpecifications(){
        credapiRequestSpec = new RequestSpecBuilder()
                .setBaseUri(testProperties.getProperty(Constants.CREDAPI_BASE_URI))
                .setBasePath(Constants.CREDAPI_BASE_PATH)
                .addHeader(Constants.SOURCE_ID_HEADER, Constants.SOURCE_ID_HEADER_VALUE)
                .addHeader(Constants.CORR_ID_HEADER, UUID.randomUUID().toString())
                .setContentType(ContentType.JSON)
                .build();
    }

    /**
     *
     * @param fileName
     * @return
     * @throws IOException throws exception when the file is not found.
     */
    public String readPayloadFromFile(String fileName) throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}
