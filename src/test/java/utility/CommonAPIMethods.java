package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import report.JIRAReport;
import stepDefinitions.BasicDefinition;

public class CommonAPIMethods{

	public static Response response;
	public static Logger logger = LogManager.getLogger(CommonAPIMethods.class);
	public ReadExcel xl = new ReadExcel();
	ConnectDatabase db = new ConnectDatabase();
	public static LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> xlContent = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>>();
	public static HashMap<String, String> existingUserDetails = null;
	public String createdUserId = "";
	public static String certificatePath = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"Certificate"+File.separator+"ipp-wssec.jks";
	public static int recordsinDBBeforeRequest = 0;
	
	/**
	 * This Method used to get the details of the request in JSON Format
	 * 
	 * @return Response will be the return type of the request
	 */
	
	public  Response getUser() {
	
		Response response = RestAssured.given().config(RestAssured.config).log().all().contentType(ContentType.JSON).get();
//		Response response = RestAssured.given().relaxedHTTPSValidation().log().all().contentType(ContentType.JSON).get();

		logger.info("Response for the GET request is: \n\n"+response.prettyPrint());
		return response;
	}
	
	/**
	 * This Method used to get the details with mentioned query of the request in JSON Format
	 * 
	 * @param query_param - Parameter to filter the query
	 * @return - Response will be the return type of the request
	 */
	public  Response getUser(HashMap<String, String> query_param) {
		Response response = RestAssured.given().config(RestAssured.config).log().all().contentType(ContentType.JSON).params(query_param).get();
		logger.info("Response for the GET request is: \n\n"+response.prettyPrint());
		return response;
	}
	
	/**
	 * This Method used to get the Response code of the request sent.
	 * 
	 * @param response Response received from the server
	 * @return	return the response code as integer value such as 200, 201, 404, 500 e.t.c.,
	 */
	
	public  int getresponseStatus(Response response) {
		int responseCode = response.getStatusCode();
		logger.info("Response code for the Request is : " + responseCode);
		return responseCode;
	}
	
	/**
	 * This Method used to get the Response ContentType of the request sent.
	 * 
	 * @param response Response received from the server
	 * @return	return the response Content Type as String value such as application/json or application/XML or application/txt
	 */
	
	public  String getresponseContentType(Response response) {
		String contentType = response.getContentType();
		logger.info("Response ContentType for the request is : "+contentType);
		return contentType;
	}
	
	/**
	 * This Method used to get the Response body of the request sent.
	 * 
	 * @param response Response received from the server
	 * @return	return the response Body as String value.
	 */
	
	public  String getresponseBody(Response response) {
		String responseBody = response.getBody().asString();
		logger.info("Response Body of the Request is : " + responseBody);
		return responseBody;
	}
	
	/**
	 * This Method used to get the Response Status Line of the request sent.
	 * 
	 * @param response Response received from the server
	 * @return	return the response Status Line as String value such as OK, Internal Server Error, Created based on the status code e.t.c
	 */
	
	public  String getresponseStatusLine(Response response) {
		String responseLine = response.getStatusLine();
		logger.info("Response Line for the request is :  ");
		return responseLine;
	}
	
	/**
	 * This Method used to get the Response time of the request sent.
	 * 
	 * @param response Response received from the server
	 * @return	return the response time as Long value such as 2 seconds 3 seconds or 45 Milliseconds
	 */
	
	public  long getresponseTime(Response response) {
		long responeTime = response.getTimeIn(TimeUnit.SECONDS);
		if (responeTime <1)
			logger.info("Time taken to respond for the request is less than a Second. Exact time in milliseconds is  "+ response.getTimeIn(TimeUnit.MILLISECONDS));
		else
			logger.info("Time taken to respond for the request is "+responeTime + " Seconds");
		return responeTime;
	}
	
	/**
	 * This Method used to get the Total Users in the request sent.
	 * 
	 * @param response Response received from the server
	 * @return	return the number of userId's.
	 */
	
	public int totalusers_inJSON_Response(Response response) {
		JsonPath jsonpath = response.jsonPath();
		int total_Users = 0;
		total_Users = jsonpath.getList("userFileId").size();
		logger.info("Total users in the response is " + total_Users);
		return total_Users;
	}
	
	public String errorMessageValdiation(Response response, String key) {
		JsonPath jsonpath = response.jsonPath();
		logger.info("Error Code / Message is " + jsonpath.get(key));
		return jsonpath.get(key);
	}
	/**
	 * This Method used to Post the Request with Header and Body. 
	 * @param Send the body information of  the request
	 * 
	 * @return return the response of the request Sent
	 */
	public Response createUserRequest(String requestBody) {
//		Response response = RestAssured.given().config(RestAssured.config).log().all().contentType(ContentType.JSON).headers(this.readexcel_createRequestHeader()).body(requestBody).post();
		Response response = RestAssured.given().log().all().contentType(ContentType.JSON).headers(this.readexcel_createRequestHeader()).body(requestBody).post();

		logger.info("Response for the POST Request is :" + response.prettyPrint());
		return response;
	}
	
	/**
	 * This Method used to Post the Request without Header and with Body.
	 * @param Send the body information of  the request
	 * @return return the response of the request Sent
	 */
	
	public Response createUserRequest_withoutHeader(String requestBody) {
		Response response = RestAssured.given().config(RestAssured.config).log().all().contentType(ContentType.JSON).body(requestBody).post();
		logger.info("Response for the POST Request is :\n\n" + response.prettyPrint());
		return response;
	}
	
	/**
	 * This Method used to PATCH the Request without Header and with Body.
	 * @param Send the body information of  the request
	 * @return return the response of the request Sent
	 */
	
	public Response updateUserRequest_withoutHeader(String requestBody) {
		Response response = RestAssured.given().config(RestAssured.config).log().all().contentType(ContentType.JSON).body(requestBody).patch();
		logger.info("Response for the PATCH Request is :\n\n" + response.prettyPrint());
		return response;
	}
	
	/**
	 * This Method used to Update the existing content with PATCH request
	 * @param Send the body information of the request (Body may contain required or all fields)
	 * @return return the response of the request Sent
	 */
	public Response updateUserrequest_PATCH(String requestBody) {
		Response response = RestAssured.given().config(RestAssured.config).log().all().contentType(ContentType.JSON).headers(this.readexcel_createRequestHeader()).body(requestBody).patch();
		logger.info("Response for the PATCH Request is :\n\n" + response.prettyPrint());
		return response;	
		}
	
	/**
	 * This Method used to Update the existing content with PUT request
	 * @param Send the body information of the request (Update all the records. If details not provided, updated as null)
	 * @return return the response of the request Sent
	 */
	public Response updateUser_Put(String requestBody) {
		Response response = RestAssured.given().config(RestAssured.config).log().all().contentType(ContentType.JSON).headers(this.readexcel_createRequestHeader()).body(requestBody).put();
		logger.info("Response for the PATCH Request is :\n\n" + response.prettyPrint());
		return response;	
		}
		
	/**
	 * This Method used to Delete the existing content with delete request
	 * @param Send the body information of the request 
	 * @return return the response of the request Sent
	 */
	public Response deleteUser(String requestBody) {
		Response response = RestAssured.given().config(RestAssured.config).log().all().contentType(ContentType.JSON).body(requestBody).delete();
		logger.info("Response for the DELETE Request is :\n\n" + response.prettyPrint());
		return response;	
		}
	
	/**
	 * This method used to create a request body in JSON format from the excel file.
	 * 
	 * @return the JSON request in String format
	 */
	public String  readexcel_convertasRequestBody(String value) {
//		String entryValue = "";
		StringBuilder jsonbodyContent = new StringBuilder("{");
		FileInputStream fis = null;
		XSSFWorkbook wb = null;
		try {
			fis = new FileInputStream(new File (Paths.get(".").toAbsolutePath().normalize().toString()+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"TestData"+File.separator+"API_TestData.xlsx"));
			wb = new XSSFWorkbook(fis);
			xlContent = xl.readsheet(wb);
			HashMap<String, String> jsonBody = xlContent.get("TestData").get(BasicDefinition.TC_id);
			String outLine = jsonBody.get("Scenario Outline");
			int scenarioOutline = 0;
			if(outLine!=null && (!outLine.equals("")) && outLine.contains("|")) {
				scenarioOutline = outLine.split("\\|").length;
				for (int i=0; i<scenarioOutline;i++) {
					if(outLine.split("\\|")[i].trim().equalsIgnoreCase(value)) {
						scenarioOutline=i;
						break;
				}
			}
		}
			for ( Entry<String, String> entry: jsonBody.entrySet()) {
				if(entry.getValue()!=null && !entry.getValue().equals(""))
					if(!(entry.getKey().equalsIgnoreCase("lbgApplicationId") ||entry.getKey().equalsIgnoreCase("Scenario Outline")|| entry.getKey().equalsIgnoreCase("x-lbg-ma"))){
						if(!entry.getValue().contains("|"))
							jsonbodyContent.append("\"" +entry.getKey()+ "\" : \""+ entry.getValue()+"\",");
						else 
							jsonbodyContent.append("\"" +entry.getKey()+ "\" : \""+ entry.getValue().split("\\|")[scenarioOutline]+"\",");
					}
				if(entry.getKey().equalsIgnoreCase("userId") && !entry.getValue().contains("|") )
					createdUserId = entry.getValue();
				else if(entry.getKey().equalsIgnoreCase("userId") && entry.getValue().contains("|"))
					createdUserId = entry.getValue().split("\\|")[scenarioOutline];
			}
			jsonbodyContent.deleteCharAt(jsonbodyContent.length()-1).append("}");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				wb.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.info("The request body created is : \n\n" + jsonbodyContent.toString());
		
//		Map<String, String> userId= db.fetchQueryResult_SingleRow("SELECT USER_ID, FIRST_NAME, USER_NAME, U_ENT_NAME, LASTLOGIN_DATE, EMAIL_ADDRESS, REC_STATUS, EFFECTIVE_DATE, UPDATE_DATE, BUSINESS_AREA, DEPARTMENT  FROM USERS WHERE USER_ID='"+createdUserId + "'");
//			if(!userId.isEmpty()) {
//				existingUserDetails = (HashMap<String, String>) userId;
//				System.out.println(existingUserDetails);
//			}
		return jsonbodyContent.toString();
	}
	
	/**
	 * This method used to create a request Header in HashMap format from the excel file.
	 * 
	 * @return the list of Headers in HashMap format
	 */
	public HashMap<String, String> readexcel_createRequestHeader() {
		HashMap<String, String> headers = new HashMap<String, String>();
		try {
			FileInputStream fis = new FileInputStream(new File (Paths.get(".").toAbsolutePath().normalize().toString()+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"TestData"+File.separator+"API_TestData.xlsx"));
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			xlContent = xl.readsheet(wb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMap<String, String> jsonBody = xlContent.get("TestData").get(BasicDefinition.TC_id);
		for ( Entry<String, String> entry: jsonBody.entrySet()) {
				if((entry.getKey().equalsIgnoreCase("lbgApplicationId") || entry.getKey().equalsIgnoreCase("x-lbg-ma"))) {
					if(entry.getValue()!=null && !entry.getValue().equals(""))
							headers.put(entry.getKey(), entry.getValue());
				}
		}
		return headers;
	}
	
	/**
	 * This Method used to create the EndPoint from the Property file
	 * 
	 * @return the URI/EndPoint/URL in String format
	 */
	public String configureEndPoint(String key) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(System.getProperty("user.dir")+File.separator +"src"+File.separator +"test"+File.separator +"resources"+File.separator +"Configuration"+File.separator +"API"+File.separator +"API_Configuration.properties")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(key.contentEquals("URI"))
			return RestAssured.baseURI = prop.getProperty("endpoint")+prop.getProperty("resource");
		else
			return prop.getProperty("certificatePassword");
	}
	
	/**
	 * This method is used to configure the Default / No Authentication
	 */
	public void setAuthentication() {
		RestAssured.authentication = RestAssured.DEFAULT_AUTH;
	}
	
	/**
	 * This method is used to configure the oAuth 2.0 Authentication
	 * @param oAuthToken : Provide the Access Token
	 */
	public void setAuthentication(String oAuthToken) {
		RestAssured.authentication = RestAssured.oauth2("Access Token");
	}
	
	/**
	 * This method is used to configure the Basic Authentication
	 * @param userName : Enter the userName of the endpoint / application
	 * @param password : Enter the Password of the endpoint / application
	 */
	public void setAuthentication(String userName, String password) {
		RestAssured.authentication = RestAssured.basic(userName, password);
	}
	
	
	/**
	 * This Method is used to configure the certificate.
	 * 
	 * @param filepath - Mention the file path where the certificate is available
	 * @param password - Password to open the certificate
	 * @return 
	 */
	public static SSLConfig installCertificate(String filepath, String password) {
		password = new CommonMethods().decryptText(password);
		SSLConfig config = new SSLConfig();
		config = config.allowAllHostnames();
//		config = config.keystoreType("JKS");
//		config = config.trustStoreType("JKS");
		config = config.keyStore(filepath, password).trustStore(filepath, password);
		RestAssured.config = RestAssuredConfig.config().sslConfig(config);
		return config;
		
	}
	
	/**
	 * This method is used to find the user availability in Database
	 * 
	 * @return Boolean Value of availability
	 */
	public boolean recordavailability_inDB() {
		boolean flag = false;

		Map<String, String> map = db.fetchQueryResult_SingleRow("SELECT COUNT(*) AS COUNT FROM IPP_USER.USERS");
		logger.info("No of records available after create request is " + map.get("COUNT"));
		switch (getresponseStatus(response)) {
		case 201:
			if (recordsinDBBeforeRequest + 1 == Integer.parseInt(map.get("COUNT")))
				if (!db.fetchQueryResult_SingleRow(
						"SELECT USER_ID FROM IPP_USER.USERS WHERE USER_ID='" + createdUserId + "'").isEmpty()) {
					logger.info("Newly created user is available in DB");
					flag = true;
				} else
					flag = false;

			break;
		case 400:
		case 403:
		case 404:
		case 409:
		case 500:
			if (recordsinDBBeforeRequest == Integer.parseInt(map.get("COUNT")))
				if (db.fetchQueryResult_SingleRow(
						"SELECT USER_ID FROM IPP_USER.USERS WHERE USER_ID='" + createdUserId + "'").isEmpty()) {
					flag = false;
					logger.info("Newly created entry is not available in DB");
				}

				else
					flag = true;
			break;
		default:
			Assert.fail("Invalid Error Code");
		}
		return flag;
	}

	/**
	 * This method used to find the number of users in DB
	 * 
	 * @return No of records in Database for the table Users
	 */
	public int userCount_inDB() {
		Map<String, String> count = db.fetchQueryResult_SingleRow("SELECT COUNT(*) AS COUNT FROM IPP_USER.USERS");
		logger.info("Total users in the DB is " + count.get("COUNT"));
		return Integer.parseInt(count.get("COUNT"));
	}
	
	/**
	 * This method used to find the number of ACTIVE / INACTIVE users in DB
	 * 
	 * @return No of records in Database for the table Users
	 */
	public int userCount_inDB(String userType) {
		Map<String, String> count = db.fetchQueryResult_SingleRow("SELECT COUNT(*) AS COUNT FROM IPP_USER.USERS WHERE REC_STATUS = '"+userType+"'");
		logger.info("Total users in the DB is " + count.get("COUNT"));
		return Integer.parseInt(count.get("COUNT"));
	}
	
	public boolean is_userExistsinDB(String userName) {
		boolean flag = false;
		if(!db.fetchQueryResult_SingleRow("SELECT COUNT(*) AS COUNT FROM IPP_USER.USERS WHERE USER_ID='"+userName + "'").isEmpty())
			flag = true;
		return flag;
	}
	
	/**
	 * This method is used to find the user details defined in Excel are updated in DB or not
	 * 
	 * @return return the boolean true or false.
	 */
	public boolean recordupdated_inDB(String example, String field) {
		boolean flag = false,firstNameflag = false,lastNameflag = false,roleflag = false,emailflag = false,statusflag = false;
		
		Map<String, String> currentUserDetails= db.fetchQueryResult_SingleRow("SELECT USER_ID, FIRST_NAME, USER_NAME, U_ENT_NAME, EMAIL_ADDRESS, REC_STATUS, EFFECTIVE_DATE, BUSINESS_AREA, DEPARTMENT  FROM USERS WHERE USER_ID='"+createdUserId + "'");
	
		String outLine = xlContent.get("TestData").get(BasicDefinition.TC_id).get("Scenario Outline");
		int scenarioOutline = -1;
		if(outLine!=null && (!outLine.equals("")) && outLine.contains("|")) {
			scenarioOutline = outLine.split("\\|").length;
			for (int i=0; i<scenarioOutline;i++) {
				if(outLine.split("\\|")[i].trim().equalsIgnoreCase(example)) {
					scenarioOutline=i;
					break;
			}
		}
	}
		
		if(is_DBandXLDataMatches(currentUserDetails, "userFirstName", "FIRST_NAME",scenarioOutline))
			firstNameflag = true;
		if(is_DBandXLDataMatches(currentUserDetails, "userLastName", "USER_NAME",scenarioOutline))
			lastNameflag = true;
		if(is_DBandXLDataMatches(currentUserDetails, "userEntitlementRole", "U_ENT_NAME",scenarioOutline))
			roleflag = true;
		if(is_DBandXLDataMatches(currentUserDetails, "userEmailAddress", "EMAIL_ADDRESS",scenarioOutline))
			emailflag = true;
//		if(is_DBandXLDataMatches(currentUserDetails, "userEffecDate", "EFFECTIVE_DATE",scenarioOutline))
//			dateflag = true;
//		if(is_DBandXLDataMatches(currentUserDetails, "UserBusinessUnit", "BUSINESS_AREA",scenarioOutline))
//			businessflag = true;
		if(is_DBandXLDataMatches(currentUserDetails, "userStatus", "REC_STATUS",scenarioOutline))
			statusflag = true;
		//&& dateflag && businessflag
		if(firstNameflag && lastNameflag && roleflag && emailflag && statusflag)
			flag = true;
		
		return flag;
	}
	
	/**
	 * This method is used to compare the provided xlReference and DB reference is matches or not
	 * 
	 * @param currentUserDetails - DB result set stored as Map
	 * @param xlReference - XL Column Name
	 * @param dBReference - DB Column Name
	 * @param scenarioOutline - index of the scenario outline reference.
	 * @return
	 */
	private boolean is_DBandXLDataMatches(Map<String, String> currentUserDetails, String xlReference, String dBReference, int scenarioOutline) {
		boolean flag = false;
		String xlValue = xlContent.get("TestData").get(BasicDefinition.TC_id).get(xlReference);
		logger.info("XML VALUE IS " + xlValue + " && DB VALUE IS " + currentUserDetails.get(dBReference));
		if(scenarioOutline>0) {
			if(xlValue.contains("|"))
				xlValue = xlValue.split("\\|")[scenarioOutline];
		}
		if(xlValue==null || xlValue.equals(""))
			flag = true;
		
		if(xlValue!=null && !xlValue.equals(""))
				if(xlValue.equalsIgnoreCase(currentUserDetails.get(dBReference)))
					flag = true;
		return flag;
	}
	
	
}
