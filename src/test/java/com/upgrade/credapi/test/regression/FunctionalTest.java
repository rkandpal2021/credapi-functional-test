package com.upgrade.credapi.test.regression;

import com.upgrade.credapi.test.base.Constants;
import com.upgrade.credapi.test.base.TestBase;
import io.restassured.response.Response;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

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
        String requestPayload = readPayloadFromFile(testDataFolderPath + requestBody);
        Response response = given().spec(credapiRequestSpec).body(requestPayload).post(Constants.Login_API);
        //compare response code
        assertEquals(response.getStatusCode(), responseCode);
        String expectedPayload = readPayloadFromFile(testDataFolderPath + expectedResponsePayload);
        //compare the response payload using JSONAssert
        JSONAssert.assertEquals(expectedPayload, response.asString(), true);
    }

}
