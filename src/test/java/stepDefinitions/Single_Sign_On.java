package stepDefinitions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import report.JIRAReport;
import utility.CommonAPIMethods;
import utility.ConnectDatabase;


public class Single_Sign_On extends CommonAPIMethods {
	
	public static Logger logger = LogManager.getLogger(Single_Sign_On.class);
	
	ConnectDatabase db = new ConnectDatabase();
		
	@Then("Send a request to read all the users")
	public void read_all_users() {
		configureEndPoint("URI");
		setAuthentication();
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		response = getUser();

	}
	
	
	
	@And ("Validate Response data with Database {string} Records")
	public void Validate_Response_data_with_Database_Records(String userType) {
		if(userType.toLowerCase().equalsIgnoreCase("active"))
			userType = "AC";
		else if(userType.toLowerCase().equals("inactive")||userType.toLowerCase().equals("delete"))
				userType = "DL";
		
		if( totalusers_inJSON_Response(response) != userCount_inDB(userType))
			Assert.fail("Count mismatch between WebService and DB");
		
		String dbResponse = db.dbResult_to_JSON("SELECT USER_ID, FIRST_NAME, USER_NAME, U_ENT_NAME, LASTLOGIN_DATE, EMAIL_ADDRESS, REC_STATUS, EFFECTIVE_DATE, UPDATE_DATE, BUSINESS_AREA, DEPARTMENT  FROM USERS WHERE REC_STATUS = '"+userType+"'");
		if(response.toString().equalsIgnoreCase(dbResponse))
			Assert.fail("Content Mismatch between WebService and DB");
	}
	
	@And ("Verify user status {string} updated as {string} in Database")
	public void Verify_user_status_updated_as_dbStatus_in_Database(String availability, String dbStatus) {
		Map<String, String> dbStatusValue = db.fetchQueryResult_SingleRow("SELECT REC_STATUS FROM USERS WHERE USER_ID='"+createdUserId+"'");
		if(availability.equalsIgnoreCase("is not")) {
			if(dbStatus.equalsIgnoreCase(dbStatusValue.get("REC_STATUS")))
				Assert.fail("REC STATUS is updated as " + dbStatusValue.get("REC_STATUS") + " instead of "+ dbStatus);
	}
			else if(availability.equalsIgnoreCase("is"))
				if(!dbStatus.equalsIgnoreCase(dbStatusValue.get("REC_STATUS")))
					Assert.fail("REC STATUS is updated as " + dbStatusValue.get("REC_STATUS") + " instead of "+ dbStatus);
	}
	
	@And ("Validate Response data with Database Records")
	public void Validate_Response_data_with_Database_Records() {
		
		if( totalusers_inJSON_Response(response) != userCount_inDB())
			Assert.fail("Count mismatch between WebService and DB");
		
		String dbResponse = db.dbResult_to_JSON("SELECT USER_ID, FIRST_NAME, USER_NAME, U_ENT_NAME, LASTLOGIN_DATE, EMAIL_ADDRESS, REC_STATUS, EFFECTIVE_DATE, UPDATE_DATE, BUSINESS_AREA, DEPARTMENT  FROM USERS");
		if(response.toString().equalsIgnoreCase(dbResponse))
			Assert.fail("Content Mismatch between WebService and DB");
	}
		
	@And ("Validate Response data with DB record")
	public void Validate_Response_data_with_DB_Record() {
		
		if(!is_userExistsinDB(createdUserId))
		Assert.fail(createdUserId + " not Exists in DB");
	String dbResponse = db.dbResult_to_JSON("SELECT USER_ID, FIRST_NAME, USER_NAME, U_ENT_NAME, LASTLOGIN_DATE, EMAIL_ADDRESS, REC_STATUS, EFFECTIVE_DATE, UPDATE_DATE, BUSINESS_AREA, DEPARTMENT  FROM USERS WHERE USER_ID='"+createdUserId + "'");
	if(response.toString().equalsIgnoreCase(dbResponse))
		Assert.fail("Content Mismatch between WebService and DB");
	}
	
	@Then("Send a request to read all the {string} users")
	public void read_all_related_users(String userType) {
		if(userType.toLowerCase().equalsIgnoreCase("active"))
			userType = "AC";
		else if(userType.toLowerCase().equals("inactive")||userType.toLowerCase().equals("delete"))
				userType = "DL";
		configureEndPoint("URI");
		setAuthentication();
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userStatus", userType);
		response = getUser(params);
	}
	
	@Then("Send a request to read the created user")
	public void read_user() {
		String userName = createdUserId;
		RestAssured.baseURI = configureEndPoint("URI") + "/" + userName;
		setAuthentication();
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		response = getUser();
		

	}
	
	@And("Verify Status Code {int} is displayed")
	public void errorCode(int statusCode) {
		if(getresponseStatus(response)!=statusCode)
			Assert.fail("Status expected is "+statusCode+" but actual response is " + getresponseStatus(response));
	}
	
	@Given ("Send a request to create new user with {string} contains {string} Value")
	public void create_the_new_user(String field, String value) {
		configureEndPoint("URI");
		setAuthentication();
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		response = createUserRequest(readexcel_convertasRequestBody(value));
		recordsinDBBeforeRequest = userCount_inDB();
		logger.info("No of records available before create request is " + recordsinDBBeforeRequest);
		
//		Map<String, String> count = db.fetchQueryResult_SingleRow("SELECT COUNT(*) AS COUNT FROM IPP_USER.USERS");
//		logger.info("No of records available before create request is " + count.get("COUNT"));

	}
	
	@Given ("Send a request to update {string} user with {string} contains {string} Value")
	public void update_the_existing_user(String existance, String field, String value) {
		configureEndPoint("URI");
		setAuthentication();
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		response = updateUserrequest_PATCH(readexcel_convertasRequestBody(value));
		recordsinDBBeforeRequest = userCount_inDB();
		logger.info("No of records available before create request is " + recordsinDBBeforeRequest);
		
//		Map<String, String> count = db.fetchQueryResult_SingleRow("SELECT COUNT(*) AS COUNT FROM IPP_USER.USERS");
//		logger.info("No of records available before create request is " + count.get("COUNT"));

	}
	
	@Then("Send a request to create new user")
	public void createUser() {
		RestAssured.baseURI ="https://run.mocky.io/v3/8ec8f4f7-8e68-4f4b-ad18-4f0940d40bb7";
		setAuthentication();
//		HashMap<String, String> headerlist = new HashMap<String, String>();
//		headerlist.put("Content-Type", "application/xml");
		String requestBody = "{ \"store\": {\r\n"
				+ "   \"book\": [\r\n"
				+ "    { \"category\": \"reference\",\r\n"
				+ "      \"author\": \"Nigel Rees\",\r\n"
				+ "      \"title\": \"Sayings of the Century\",\r\n"
				+ "      \"price\": 8.95\r\n"
				+ "    },\r\n"
				+ "    { \"category\": \"fiction\",\r\n"
				+ "      \"author\": \"Evelyn Waugh\",\r\n"
				+ "      \"title\": \"Sword of Honour\",\r\n"
				+ "      \"price\": 12.99\r\n"
				+ "    },\r\n"
				+ "    { \"category\": \"fiction\",\r\n"
				+ "      \"author\": \"Herman Melville\",\r\n"
				+ "      \"title\": \"Moby Dick\",\r\n"
				+ "      \"isbn\": \"0-553-21311-3\",\r\n"
				+ "      \"price\": 8.99\r\n"
				+ "    },\r\n"
				+ "    { \"category\": \"fiction\",\r\n"
				+ "      \"author\": \"J. R. R. Tolkien\",\r\n"
				+ "      \"title\": \"The Lord of the Rings\",\r\n"
				+ "      \"isbn\": \"0-395-19395-8\",\r\n"
				+ "      \"price\": 22.99\r\n"
				+ "    }\r\n"
				+ "  ],\r\n"
				+ "    \"bicycle\": {\r\n"
				+ "      \"color\": \"red\",\r\n"
				+ "      \"price\": 19.95\r\n"
				+ "    }\r\n"
				+ "  }\r\n"
				+ " }";
		
		Response response = RestAssured.given()
//				.headers(headerlist)
//				.body(requestBody)
				.get();
       List<String> s =  JsonPath.with(response.getBody().prettyPrint()).get("Location.State");
       for (String string : s) {
		System.out.println(string);
	}
        String category = JsonPath.with(response.getBody().prettyPrint()).get("Location.State[0]");
        System.out.println(category);
        String lastcategory = JsonPath.with(response.getBody().prettyPrint()).get("Location.State[-1]");
        System.out.println(lastcategory);
        String price = JsonPath.with(response.getBody().prettyPrint()).get("Items.Price");
        System.out.println(price);
        
        
	//	System.out.println("Response Code is " + response.getBody().prettyPrint());
		
		configureEndPoint("URI");
		setAuthentication();
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		recordsinDBBeforeRequest = userCount_inDB();
		logger.info("No of records available before create request is " + recordsinDBBeforeRequest);
//		String jsonBody = readexcel_convertasRequestBody("");
		response = createUserRequest(readexcel_convertasRequestBody(""));
	}
	
	@When("Send a request to create new user without Headers")
	public void createUser_withoutHeaders() {
		configureEndPoint("URI");
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		setAuthentication();
		response = createUserRequest_withoutHeader(readexcel_convertasRequestBody(""));
	}
	
	@When("Send a request to update {string} user without Headers")
	public void updateUser_withoutHeaders(String existance) {
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		configureEndPoint("URI");
		setAuthentication();
		
		
		response = updateUserRequest_withoutHeader(readexcel_convertasRequestBody(""));
	}
	
	@Then("Send a request to update {string} user")
	public void update_user(String existance) {
		String userName = createdUserId;
		RestAssured.baseURI = configureEndPoint("URI") + "/" + userName;
		setAuthentication();
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		recordsinDBBeforeRequest = userCount_inDB();
		logger.info("No of records available before create request is " + recordsinDBBeforeRequest);
		response = updateUserrequest_PATCH(readexcel_convertasRequestBody(""));
	
	}
	
	@Then("Soft delete the created user")
	public void delete_user() {
		String userName = createdUserId;
		RestAssured.baseURI = configureEndPoint("URI") + "/" + userName;
		setAuthentication();
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		Map<String, String> count = db.fetchQueryResult_SingleRow("SELECT COUNT(*) AS COUNT FROM IPP_USER.USERS WHERE USER_ID='"+userName + "'");
		if(1 != Integer.parseInt(count.get("COUNT")))
			Assert.fail("Count mismatch between WebService and DB");
		
		response = updateUserrequest_PATCH("{\"userStatus\" : \"DL\"}");
		String dbResponse = db.dbResult_to_JSON("SELECT USER_ID, FIRST_NAME, USER_NAME, U_ENT_NAME, LASTLOGIN_DATE, EMAIL_ADDRESS, REC_STATUS, EFFECTIVE_DATE, UPDATE_DATE, BUSINESS_AREA, DEPARTMENT  FROM USERS WHERE USER_ID='"+userName + "'");
		if(response.toString().equalsIgnoreCase(dbResponse))
			Assert.fail("Content Mismatch between WebService and DB");
		if(getresponseStatus(response)!=204)
			Assert.fail("Status expected is 204 but actual response is " + getresponseStatus(response));
	}

	@And ("Verify {string} is {string} in DataBase for {string} {string}")
	public void verify_record_available_in_DB(String userType, String availability, String example,String field) {
		if(userType.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("newuser")) {
			if(availability.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("available"))
				if(!recordavailability_inDB())
					Assert.fail("Newly created user is not available in DB");		
			if(availability.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("notavailable"))
				if(recordavailability_inDB())
					Assert.fail("Newly created user is available in DB");	
		}
		if(userType.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("existinguser")) {
			if(availability.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("updated"))
				if(!recordupdated_inDB(example, field))
						Assert.fail("Existing user details not updated in DB");
			if(availability.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("notupdated"))
				if(recordupdated_inDB(example, field))
						Assert.fail("Existing user details updated in DB");
		}
	}

	@And ("Verify {string} is {string} in DataBase")
	public void verify_record_available_in_DB(String userType, String availability) {
		if(userType.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("newuser")) {
			if(availability.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("available"))
				if(!recordavailability_inDB())
					Assert.fail("Newly created user is not available in DB");		
			if(availability.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("notavailable"))
				if(recordavailability_inDB())
					Assert.fail("Newly created user is available in DB");	
		}
		if(userType.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("existinguser")) {
			if(availability.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("updated"))
				if(!recordupdated_inDB("", ""))
						Assert.fail("Existing user details not updated in DB");
			if(availability.toLowerCase().replaceAll(" ", "").equalsIgnoreCase("notupdated"))
				if(recordupdated_inDB("", ""))
						Assert.fail("Existing user details updated in DB");
		}
	}
	
	
	@And("Send a request to read the {string} user")
	public void send_a_request_to_read_the_user(String userType) {
		readexcel_convertasRequestBody(userType);
		String uri = configureEndPoint("URI");
		uri = uri.substring(0, uri.length()-1) + "/"+createdUserId;
		RestAssured.baseURI = uri;
//		configureEndPoint("URI" + "/" + createdUserId);
		setAuthentication();
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		response = getUser();
	}

//	@And("Send a request to read the {string} user")
//	public void send_a_request_to_read_the_user(String userType) {
//		if(userType.toLowerCase().equalsIgnoreCase("active"))
//			userType = "AC";
//		else if(userType.toLowerCase().equals("inactive")||userType.toLowerCase().equals("delete"))
//				userType = "DL";
//		configureEndPoint("URI" + "/" + createdUserId);
//		setAuthentication();
//		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
//		response = getUser();
//	}
	
	@And ("Verify error code {int} and error description {string} is displayed in the Response")
	public void Verify_error_code_and_error_description_is_displayed_in_the_Response(int errorCode, String errorMessage){
		if(!errorMessageValdiation(response, "code").contains(String.valueOf(errorCode)))
			Assert.fail("Error Code is not displayed");
		if(!errorMessageValdiation(response, "message").contains(errorMessage))
			Assert.fail("Error Message is not displayed");
	}
	
	@And ("Send a request to update all details of existing user")
	public void Send_a_request_to_update_all_details_of_existing_user() {
		String userName = createdUserId;
		RestAssured.baseURI = configureEndPoint("URI") + "/" + userName;
		setAuthentication();
		installCertificate(certificatePath, configureEndPoint("Certificate Password"));
		recordsinDBBeforeRequest = userCount_inDB();
		logger.info("No of records available before create request is " + recordsinDBBeforeRequest);
		response = updateUser_Put(readexcel_convertasRequestBody(""));
	}
	
	
	@Given("Send a request to delete the existing user")
	public void send_a_request_to_delete_the_existing_user() {
		String requestBody = readexcel_convertasRequestBody("");
		String uri = configureEndPoint("URI");
		uri = uri.substring(0, uri.length()-1) + "/"+createdUserId;
		RestAssured.baseURI = uri;
		setAuthentication();
		response = deleteUser(requestBody);
	}
	
	@Given("Test JIRA")
	public void jira() {
		configureEndPoint("URI");
		setAuthentication("TkdJUC1wb3dlcmJpLmppcmFzcnY6VXNlbWVhc2FrZXkjMTIzNDU2Nw==");
		Response response = RestAssured.given().log().all().contentType(ContentType.JSON).relaxedHTTPSValidation().get();
		System.out.println(response.prettyPrint());
		
		RestAssured.baseURI = "https://jira.devops.lloydsbanking.com/rest/api/2/issue/NGIP-2632";
		RestAssured.authentication = RestAssured.basic("NGIP-powerbi.jirasrv", "Usemeasakey#1234567");
		Response getresponse = RestAssured.given().log().all().contentType(ContentType.JSON).relaxedHTTPSValidation().get();
		System.out.println(getresponse.prettyPrint());
	}
	
	public void createHttpPostRequest( String strJsonData) {
		String TC_ID = "Basha"; 
		File file = new File("C:\\Users\\8205786\\Desktop\\Temp\\FI\\NGIP 587.txt");
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost postRequest = new HttpPost("https://jira.devops.lloydsbanking.com/rest/api/2/issue/NGIP-587");
//			postRequest.setHeader("Authorization", "Basic TWFoYWJ1LUJhc2hhLkFuc2FyQGxsb3lkc2JhbmtpbmcuY29tOk1Ac2hhQTExYWg=");
			postRequest.setHeader("Authorization", "Basic ODIwNTc4NjpNQHNoYUExMWFo");
			postRequest.setHeader("Content-type", "application/json");
			postRequest.setHeader("X-Atlassian-Token", "nocheck");
			postRequest.setEntity(new FileEntity(file));

			HttpResponse response = client.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				logger.info(TC_ID + " : Testcase details Uploaded in JIRA successfully");
			} else {
				logger.error(TC_ID + " : Testcase details were not uploaded in JIRA with Status code : " + statusCode + " and Status Line : " + response.getStatusLine() + ". Please upload the execution report manually");
				System.err.println(TC_ID + " : Testcase details were not uploaded in JIRA with Status code : " + statusCode + " and Status Line : " + response.getStatusLine() + ". Please upload the execution report manually");
			}
		} catch (IOException e) {
			logger.error(TC_ID + " : IOException occured while uploading results in JIRA", e);
			System.err.println(TC_ID + " : Exception occured while uploading results in JIRA");
		} catch (Exception e) {
			logger.error(TC_ID + " : Exception occured while uploading results in JIRA", e);
			System.err.println(TC_ID + " : Exception occured while uploading results in JIRA");
		}
	}// end method
	
}
