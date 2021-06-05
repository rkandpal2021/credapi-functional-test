package com.upgrade.credapi.test.regression;

import com.upgrade.credapi.test.base.Constants;
import com.upgrade.credapi.test.base.TestBase;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class FunctionalTestUsingJSONAssert extends TestBase {
    private static Logger logger = LoggerFactory.getLogger(FunctionalTestUsingJSONAssert.class);
    private static final String testDataFolderPath = Constants.FUNCTIONAL_TEST_DATA_PATH;

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
        test(credapiRequestSpec, requestBody, expectedResponsePayload, responseCode);
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
        test(requestSpecification, requestBody, expectedResponsePayload, responseCode);
    }

    /**
     * This method is called internally by functional test to submit a Login API request and validate it's response.
     * @param requestSpecification
     * @param requestBody
     * @param expectedResponsePayload
     * @param responseCode
     * @throws IOException   when request or response file does not exists.
     * @throws JSONException this exception is throw when either expected payload file or server response is not proper json.
     */
    private void test(RequestSpecification requestSpecification, String requestBody, String expectedResponsePayload, int responseCode) throws IOException, JSONException {
        String requestPayload = readPayloadFromFile(testDataFolderPath + requestBody);
        Response response = given().spec(requestSpecification).body(requestPayload).post(Constants.Login_API).then()
                .contentType(ContentType.JSON).statusCode(responseCode).extract().response();
        String expectedPayload = readPayloadFromFile(testDataFolderPath + expectedResponsePayload);
        //compare the response payload using JSONAssert
        JSONAssert.assertEquals(expectedPayload, response.asString(), new CustomComparator(JSONCompareMode.STRICT,
                new Customization(Constants.TIMESTAMP_FIELD_NAME, (o1, o2) -> true)
        ));
    }
}
