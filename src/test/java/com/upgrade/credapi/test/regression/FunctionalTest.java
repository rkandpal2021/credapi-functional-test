package com.upgrade.credapi.test.regression;

import com.upgrade.credapi.test.base.Constants;
import com.upgrade.credapi.test.base.TestBase;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class FunctionalTest extends TestBase {
    private static Logger logger = LoggerFactory.getLogger(FunctionalTest.class);
    private static final String testDataFolderPath = Constants.FUNCTIONAL_TEST_DATA_PATH + File.separator;

    //NOTE: we can use parallel = true if we want to run all the test together.
    @DataProvider(name = "loginTestData")
    public Object[][] loginTestData() {
        return new Object[][]{
                {"valid-login-payload.json", "expected-response-payload-valid-login.json", 200},
                {"valid-only-required-fields.json", "expected-response-payload-valid-login.json", 200},
                {"invalid-username-payload.json", "expected-response-payload-invalid-username.json", 401},
                {"invalid-password-payload.json", "expected-response-payload-invalid-password.json", 401},
                {"missing-username-payload.json", "expected-response-payload-missing-username.json", 400},
                {"missing-password-payload.json", "expected-response-payload-missing-password.json", 400},
                {"multiple-username-payload.json", "expected-response-payload-multiple-username.json", 400},
                {"bad-username-payload.json", "expected-response-payload-invalid-username.json", 403}
        };
    }

    @DataProvider(name = "missingHeaderTestData")
    public Object[][] missingHeaderTestData() {
        return new Object[][]{
                {"valid-login-payload.json", "expected-response-payload-missing-header.json", 500}
        };
    }


    /**
     * Following test scenarios are automated -
     * - valid login with correct username, password and recaptchaToken.
     * - valid login with only username and password.
     * - login attempt with invalid username.
     * - login attempt with invalid password.
     * - login attempt with missing username.
     * - login attempt with missing password.
     * - login attempt with a payload containing multiple username.
     * - login attempt with a bad username(' or ''=').
     */
    @Test(dataProvider = "loginTestData")
    public void testLogin(String requestBody, String expectedResponsePayload, int responseCode) throws IOException, JSONException {
        test(credapiRequestSpec, testDataFolderPath + requestBody, testDataFolderPath + expectedResponsePayload, responseCode);
    }

    /**
     * Login request without proper headers should get rejected. This test is to simulate this scenario.
     */
    @Test(dataProvider = "missingHeaderTestData")
    public void missingHeaderTest(String requestBody, String expectedResponsePayload, int responseCode) throws IOException, JSONException {
        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .setBaseUri(testProperties.getProperty(Constants.CREDAPI_BASE_URI))
                .setContentType(ContentType.JSON)
                .build();
        test(requestSpecification, testDataFolderPath + requestBody, testDataFolderPath + expectedResponsePayload, responseCode);
    }
}
