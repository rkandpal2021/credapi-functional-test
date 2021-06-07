package com.upgrade.credapi.test.base;

/**
 * Globally defined constant value.
 */
public interface Constants {
    String DEFAULT_TEST_ENV = "dev";
    String SOURCE_ID_HEADER = "x-cf-source-id";
    String CORR_ID_HEADER = "x-cf-corr-id";
    String CREDAPI_BASE_URI = "credapiBaseUri";
    String SOURCE_ID_HEADER_VALUE = "coding-challenge";
    String FUNCTIONAL_TEST_DATA_PATH = "functional-test-data/";
    String FOLDER_PATH = "./src/test/resources/" + FUNCTIONAL_TEST_DATA_PATH;
    String Login_API = "/api/brportorch/v2/login";
    String TIMESTAMP_FIELD_NAME = "timestamp";
    String ERROR = "error";
    String MESSAGE = "message";
    String HTTP_STATUS = "httpStatus";
    String FIRST_NAME = "firstName";
    String USERID = "userId";
    String USER_UUID = "userUuid";
    String AUTHENTICATION_LEVEL = "authenticationLevel";
    String LOANS_IN_REVIEW = "loansInReview";
    String LOANS_IN_REVIEW_MISMATCH_ERROR = "loansInReview json array is not matching.";
    String LOAN_APPLICATIONS = "loanApplications";
    String LOAN_APPLICATIONS_MISMATCH_ERROR = "loansApplications json array is not matching.";
    String LOAN_ACCOUNT_SUMMARY_ATO = "loanAccountSummaryAto";
    String LOAN_ACCOUNT_SUMMARY_ATO_MISMATCH_ERROR = "loanAccountSummaryAto json array is not matching.";
    String GENERIC_FAILURE_MESSAGE = "Actual and expected Json are different";
    String ROOT_ELEMENT_JSON_PATH = "$";
}
