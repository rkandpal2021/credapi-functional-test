package com.upgrade.credapi.test.regression;

import com.upgrade.credapi.test.base.Constants;
import com.upgrade.credapi.test.base.TestBase;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import io.restassured.path.json.JsonPath;

public class FunctionalTestWithoutJSONAssert extends TestBase {

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
     * @throws IOException   when request or response file does not exist.
     * @throws JSONException this exception is thrown when either expected payload file or server response is not proper json.
     */
    private void test(RequestSpecification requestSpecification, String requestBody, String expectedResponsePayload, int responseCode) throws IOException, JSONException {
        String requestPayload = readPayloadFromFile(Constants.FUNCTIONAL_TEST_DATA_PATH + requestBody);
        JsonPath expectedApiResponse = new JsonPath(new File(Constants.FOLDER_PATH + expectedResponsePayload));
        //submit the request and assert the response.
        JsonPath actualApiResponse = given().spec(requestSpecification).body(requestPayload).post(Constants.Login_API).then()
                .contentType(ContentType.JSON).statusCode(responseCode).extract().body().jsonPath();;
        //assert the response
        if(responseCode==200){
            assertEquals(actualApiResponse.getString(Constants.FIRST_NAME), expectedApiResponse.getString(Constants.FIRST_NAME));
            assertEquals(actualApiResponse.getString(Constants.USERID), expectedApiResponse.getString(Constants.USERID));
            assertEquals(actualApiResponse.getString(Constants.USER_UUID), expectedApiResponse.getString(Constants.USER_UUID));
            assertEquals(actualApiResponse.getString(Constants.AUTHENTICATION_LEVEL), expectedApiResponse.getString(Constants.AUTHENTICATION_LEVEL));
            assertJSONArray(actualApiResponse, expectedApiResponse, Constants.LOAN_APPLICATIONS, Constants.LOAN_APPLICATIONS_MISMATCH_ERROR);
            assertJSONArray(actualApiResponse, expectedApiResponse, Constants.LOANS_IN_REVIEW, Constants.LOANS_IN_REVIEW_MISMATCH_ERROR);
            assertJSONArray(actualApiResponse, expectedApiResponse, Constants.LOAN_ACCOUNT_SUMMARY_ATO, Constants.LOAN_ACCOUNT_SUMMARY_ATO_MISMATCH_ERROR);
        }
        else{
            assertEquals(actualApiResponse.getString(Constants.CODE), expectedApiResponse.getString(Constants.CODE));
            assertEquals(actualApiResponse.getString(Constants.CODE_NAME), expectedApiResponse.getString(Constants.CODE_NAME));
            assertEquals(actualApiResponse.getString(Constants.MESSAGE), expectedApiResponse.getString(Constants.MESSAGE));
            assertEquals(actualApiResponse.getString(Constants.ERROR), expectedApiResponse.getString(Constants.ERROR));
            assertEquals(actualApiResponse.getString(Constants.RETRYABLE), expectedApiResponse.getString(Constants.RETRYABLE));
            assertEquals(actualApiResponse.getString(Constants.TYPE), expectedApiResponse.getString(Constants.TYPE));
            assertEquals(actualApiResponse.getString(Constants.HTTP_STATUS), expectedApiResponse.getString(Constants.HTTP_STATUS));
        }
    }
}