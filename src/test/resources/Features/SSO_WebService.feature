#Author: Mahabu-Basha.Ansar@lloydsbanking.com


Feature: Verify web services is working fine for Users Table

Scenario: API_CHAINING - Validate the User can able Create, Edit, Read and Delete the User.
Given Send a request to create new user
And Send a request to update 'existing' user
When Send a request to read the created user
Then Soft delete the created user

Scenario Outline: TC_009 - Validate the User can able to create new user with invalid Entitlement in the request.
Given Send a request to create new user with 'Entitlement' contains '<Entitlement>' Value
When Verify Status Code 400 is displayed
Then Verify 'New User' is 'not available' in DataBase
Examples:
| Entitlement   |
|Null|
|INVALID ROLE|
|BLANK SPACES| 

