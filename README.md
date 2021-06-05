# This repository contains the functional test for the login API.

## Test Scenarios -
Following test scenarios are automated -
- Valid login with correct username, password and recaptchaToken.
- Valid login with only username and password.
- Login attempt with invalid username.
- Login attempt with invalid password. 
- Login attempt with missing username.
- Login attempt with missing password. 
- Login attempt with a payload containing multiple username.
- Login attempt with a bad username(' or ''=').
- Login request without proper headers.

## Test Execution -
### There are three ways in which you can execute the functional tests -
- Method 1 - Execute 'mvn clean test -DTEST_ENV=dev' or 'mvn clean test' from Terminal.
- Method 2 - Execute 'testng-regression' from IDE.
- Method 3 - Execute the FunctionalTest class directly from IDE. 

## Automation Code Implementation
### All the Functional Tests are implemented in following two ways [i.e every functional test has been implemented twice]
- FunctionalTestUsingJSONAssert - This is the recommended approach. In these tests we are using JSONAssert library to compare two JSON. This library compares every single field of JSON thus all fields get tested.
- FunctionalTestWithoutJSONAssert - In this approach we have developed methods to assert JSON fields. Here we are not using JSONAssert library for comparing JSON.

### Test Data -
All the test data (request payload & expected response payload) is stored as JSON file under "functional-test-data" folder.