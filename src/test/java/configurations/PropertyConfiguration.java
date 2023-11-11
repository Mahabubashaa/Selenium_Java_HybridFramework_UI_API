package configurations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import cucumber.api.Scenario;
import utility.ConnectDatabase;

public class PropertyConfiguration {
	private Properties properties, apiproperties;
	 public static Map<String, String> usersMap = new LinkedHashMap<String, String>(); 
	 public static Map<String, String> sec_usersMap = new LinkedHashMap<String, String>(); 
	 public ConnectDatabase db = new ConnectDatabase();
	 private final String propertyFilePath= System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"Configuration"+File.separator+"Credentials.properties";
	 private final String reportconfigpath = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"Configuration"+File.separator+"extent-config.xml";
	 public PropertyConfiguration(){
	 BufferedReader reader, apireader;
	 try {
	 reader = new BufferedReader(new FileReader(propertyFilePath));
	 properties = new Properties();
	 apiproperties = new Properties();
	 try {
	 properties.load(reader);
	 reader.close();
	 } catch (IOException e) {
	 e.printStackTrace();
	 }
	 } catch (FileNotFoundException e) {
	 e.printStackTrace();
	 throw new RuntimeException("Credentials.properties not found at " + propertyFilePath);
	 } 
	 }
	 
	 public String get_xRAY_ProjectKey() {
		 String testKey = apiproperties.getProperty("PROJECT_KEY");
		 if(testKey!= null) return testKey;
		 else throw new RuntimeException("PROJECT_KEY is not specified in the API_Credentials.properties file.");
	 }
	 
	 public String get_xRAY_JIRA_CREDENTIALS() {
		 String jira_Credentials = apiproperties.getProperty("JIRA_CREDENTIALS");
		 if(jira_Credentials!= null) return jira_Credentials;
		 else throw new RuntimeException("JIRA_CREDENTIALS is not specified in the API_Credentials.properties file.");
	 }
	 
	 public String get_xRAY_testEvidence() {
		 String testEvidence = apiproperties.getProperty("JIRA_TESTRUN_TEST_EVIDENCE");
		 if(testEvidence!= null) return testEvidence;
		 else throw new RuntimeException("JIRA_TESTRUN_TEST_EVIDENCE is not specified in the API_Credentials.properties file.");
	 } 
	 
	 
	 public String get_API_CertPassword() {
		 String certificatePassword = apiproperties.getProperty("certificatePassword");
		 if(certificatePassword!= null) return certificatePassword;
		 else throw new RuntimeException("Certificate Password is not specified in the API_Credentials.properties file.");
	 }
	 
	 public String get_xRAY_TestexecutionKey() {
		 String testexecutionKey = apiproperties.getProperty("TEST_EXECUTION_KEY");
		 if(testexecutionKey!= null) return testexecutionKey;
		 else throw new RuntimeException("TEST_EXECUTION_KEY is not specified in the API_Credentials.properties file.");
	 }
	 public String get_xRAY_testCommentsUpdate() {
		 String commentsUpdate = apiproperties.getProperty("JIRA_TESTRUN_UPDATE_COMMENT");
		 if(commentsUpdate!= null) return commentsUpdate;
		 else throw new RuntimeException("JIRA_TESTRUN_UPDATE_COMMENT is not specified in the API_Credentials.properties file.");
	 }
	 public String get_xRAY_importTestResults() {
		 String importTestResults = apiproperties.getProperty("IMPORT_XRAY_CUCUMBER_TEST_RESULTS");
		 if(importTestResults!= null) return importTestResults;
		 else throw new RuntimeException("IMPORT_XRAY_CUCUMBER_TEST_RESULTS is not specified in the API_Credentials.properties file.");
	 }
	 
	 
	 public String getBrowser() { 
	 String browser = properties.getProperty("Browser");
	 if(browser != null) return browser;
	 else throw new RuntimeException("Browser not specified in the Credentials.properties file."); 
	 }
	 
	 public String getreportConfigPath() {
	 if(reportconfigpath != null) return reportconfigpath;
	 else throw new RuntimeException("url not specified in the Credentials.properties file.");
	 }
		
	public String getTestCaseID(Scenario scenario){
		return scenario.getName().split("-")[0].trim();
	}
	public String getTestCaseDesc(Scenario scenario){
		return scenario.getName().split("-")[1].trim();
	}
	
	 public String getEnvironment() { 
	 String env = properties.getProperty("Browser");
	 if(env != null) return env;
	 else throw new RuntimeException("Browser not specified in the Credentials.properties file."); 
	 }
	
	public boolean getJIRAUPDATE_Required() {
		
		String jira_Key = properties.getProperty("JIRA_UPDATE_REQUIRED");
		boolean flag = false;
		if (jira_Key != null) {
			if(jira_Key.equalsIgnoreCase("Yes")) {
				flag = true;
				return flag;
			}
			else
				return flag;
		}else
			throw new RuntimeException("JIRA_UPDATE_REQUIRED is not specified in the Credentials.properties file.");}

	public String getURL() {
		String url = properties.getProperty("Url");
		 if(url != null) return url;
		 else throw new RuntimeException("Url not specified in the Credentials.properties file."); 
	}


	
	
}

