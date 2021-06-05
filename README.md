#This repository contains the functional test for the login API.

##Test Scenarios -
Following test scenarios are automated -
- Valid login with correct username, password and recaptchaToken.
- Valid login with only username and password.
- Login attempt with invalid username.
- Login attempt with invalid password. 
- Login attempt with missing username.
- Login attempt with missing password. 
- Login attempt with a payload containing multiple username.
- Login attempt with a bad username(' or ''=').

## Test Execution -
- Method 1 - Execute 'mvn clean test' from Terminal.
- Method 2 - Execute 'testng-regression' from IDE.
- Method 3 - Execute the FunctionalTest class directly from IDE. 