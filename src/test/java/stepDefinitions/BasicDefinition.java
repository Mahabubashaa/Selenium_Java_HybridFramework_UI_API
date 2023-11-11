package stepDefinitions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import configurations.ConfigurationManager;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utility.DBValidation;
import utility.IPPMethods;
import utility.ReadExcel;

public class BasicDefinition {
	
	public IPPMethods ipp = new IPPMethods();
	public static Logger logger = LogManager.getLogger(BasicDefinition.class);
	public static String TC_id = "", TC_Description = "", response_duration = "", Screenshotpath = "",uniqueNumber = "",pmtInfoIDnumber="",grpIDnumber="",related_id="",s_msg_mid="", file_ref="", file_name="";
	public static String[] uniquenumbers = null;
	public static String scenario_Status = "";
	public static Scenario scenario = null;
	public static boolean flag, nextbusinessdate = false;
	public static JSONObject objectRepo, account_Details;
	public static JSONObject msgQueueRepo;
	public static LinkedHashMap<String, String> responseCalculation = new LinkedHashMap<String, String>();
	public static int scenarioOutlineindex = 0;
	public static ReadExcel xl = new ReadExcel();
	public static LinkedHashMap<String, String> scenarioResult = new LinkedHashMap<String, String>();
	public static LinkedHashSet<String> msgQueues = new LinkedHashSet<String>();
	public DBValidation dbv = new DBValidation();
	public static long starttime = 0, endtime = 0;
	public static LinkedHashMap<String, String> ReporttableContent = new LinkedHashMap<String, String>();
	public static String previousstepname = "";
	public static String dbDriver = null;
	public static String xray_TestKey = null;	
	public static String dbURL = null;
	public static String NFTdbURL = null;
	public static String dbUserName = null;
	public static String dbPassword = null;
	public static Connection dbconnection = null;
	static public String globalOffice = "***"; 
	static public String lukOffice = "LUK";
	public static File excel;
	public static FileInputStream fis;
	public static XSSFWorkbook wb;
	public static LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> testDataMap = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>>();

	
	@SuppressWarnings("static-access")
	@Before
	public void init(Scenario scenario) {
		uniqueNumber = "";
		TC_id = scenario.getName().split("-")[0].trim();
		TC_Description = scenario.getName().split("-")[1].trim();
		ArrayList<String> tags = (ArrayList<String>) scenario.getSourceTagNames();
		for (String tag : tags) {
			if(tag.startsWith("@"+ConfigurationManager.getInstance().getConfigReader().get_xRAY_ProjectKey()))
				xray_TestKey = tag;
		}
		this.scenario = scenario;
		flag = true;
		ipp.setProperty(TC_id);
	//	create_DBConnection();
		
	} 
	
	@Before("@Sanity_Pack_API,@Sanity_R2_API,@Sanity_FI_API,@Sanity_CBO_API")
    public void sanityAPI(){
        System.out.println("This will run before API Sanity Pack");
        Response response = RestAssured.given().relaxedHTTPSValidation().log().all().head("https://dpa-dev.nonprod.pffc-devops.alf.uk/ipptoolkitservice");
        if(response.getStatusCode()!=200) {
        	ipp.captureEvidence("Containerization is not configured ");
        	Assert.fail("Containerization is not configured ");
        }
	}

	public static String[] scenario_Outline() {
		String cellValue = "";
		String[] cellSplit = null;
		try {
			cellValue = testDataMap.get("TestData").get(BasicDefinition.TC_id)
					.get("Scenario_Outline");
		} catch (NullPointerException e) {
			logger.error(e);
		}
		if (cellValue != null && 
				(!cellValue.equalsIgnoreCase("")))
			cellSplit = cellValue.split("\\|");
		
		return cellSplit;
	}

	public static Scenario getScenario() {
		return scenario;
	}

	@Then("Verify the button {string} is {string}")
	public void verify_the_field_is_present(String objectReference, String isPresent) {
		
		try {

			if (ipp.isWebElementDisplayed(objectReference)
					&& (isPresent.toLowerCase().replace(" ", "").equalsIgnoreCase("present")
							|| isPresent.toLowerCase().replace(" ", "").equalsIgnoreCase("displayed"))) {
				logger.info(objectReference + " is Present in the WebPage");
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			} else if (!ipp.isWebElementDisplayed(objectReference)
					&& (isPresent.toLowerCase().replace(" ", "").equalsIgnoreCase("notpresent"))) {
				logger.info(objectReference + " is Not Present in the WebPage");
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			} else {
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
				Assert.fail();
			}

		} catch (Exception e) {
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
			logger.error(e);
			e.printStackTrace();
			Assert.fail();
		}

	}
	@Then("Verify {string} user ensures {string} is {string}")
	public void verify_the_field_is_present(String user, String objectReference, String isPresent) {
		
		try {
			ipp.hardSleep(3);
			String mid = ipp.getElementText(objectReference);
			System.out.println(mid + " : expected text is available");
			if (ipp.isWebElementDisplayed(objectReference)
					&& (isPresent.toLowerCase().replace(" ", "").equalsIgnoreCase("present")
							|| isPresent.toLowerCase().replace(" ", "").equalsIgnoreCase("displayed"))) {
				logger.info(objectReference + " is Present in the WebPage");
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			} else if (!ipp.isWebElementDisplayed(objectReference))
			Assert.fail();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	@And("Verify element {string} is {string} and {string} by default")
	public void verify_the_field_is_Selected(String objectReference, String isPresent, String isSelected) {
		
		try {
			if (ipp.isWebElementDisplayed(objectReference)
					&& (isPresent.toLowerCase().replace(" ", "").equalsIgnoreCase("present"))) {
				logger.info(objectReference + " is present in the WebPage");
				Assert.assertTrue(objectReference + " is displayed", true);
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			} else if (!ipp.isWebElementDisplayed(objectReference)
					&& (isPresent.toLowerCase().replace(" ", "").equalsIgnoreCase("notpresent"))) {
				logger.info(objectReference + " is not present in the WebPage");
				Assert.assertTrue(objectReference + " is not displayed as Expected", true);
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			}
			if (ipp.isWebElementSelected(objectReference)
					&& (isSelected.toLowerCase().replace(" ", "").equalsIgnoreCase("selected"))) {
				logger.info(objectReference + " is selected in the WebPage");
				Assert.assertTrue(objectReference + " is selected", true);
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			} else if (!ipp.isWebElementSelected(objectReference)
					&& (isSelected.toLowerCase().replace(" ", "").equalsIgnoreCase("Not Selected"))) {
				logger.info(objectReference + " is not selected in the WebPage");
				Assert.assertTrue(objectReference + " is not selected as Expected", true);
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			}

		} catch (Exception e) {
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
			logger.error(e);
			e.printStackTrace();
			Assert.fail("Expected and Output is mismatch for the locator: " + objectReference);
		}
	}

	@And("Verify {string} is {string} and it is {string} for {string} User")
	public void verify_the_field_is_enabled(String objectReference, String isPresent, String isClickable, String user) {
		
		try {

			if (ipp.isWebElementDisplayed(objectReference)
					&& (isPresent.toLowerCase().replace(" ", "").equalsIgnoreCase("present"))) {
				logger.info(objectReference + " is present in the WebPage");
				Assert.assertTrue(objectReference + " is displayed", true);
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			} else if (!ipp.isWebElementDisplayed(objectReference)
					&& (isPresent.toLowerCase().replace(" ", "").equalsIgnoreCase("notpresent"))) {
				logger.info(objectReference + " is not present in the WebPage");
				Assert.assertTrue(objectReference + " is not displayed as Expected", true);
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			}
			if (ipp.isWebElementEnabled(objectReference)
					&& (isClickable.toLowerCase().replace(" ", "").equalsIgnoreCase("enabled"))) {
				logger.info(objectReference + " is clickable in the WebPage");
				Assert.assertTrue(objectReference + " is displayed", true);
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			} else if (!ipp.isWebElementEnabled(objectReference)
					&& (isClickable.toLowerCase().replace(" ", "").equalsIgnoreCase("disabled"))) {
				logger.info(objectReference + " is not clickable in the WebPage");
				Assert.assertTrue(objectReference + " is not displayed as Expected", true);
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			}
		} catch (Exception e) {
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
			logger.error(e);
			e.printStackTrace();
			Assert.fail("Expected and Output is mismatch for the locator: " + objectReference);
		}
	}

	
	@Then("User clicks the {string} {string}")
	public void user_click_the_element(String elementType, String objectReference) {
		
		try {
            ipp.wait_For_ElementPresence(objectReference);
			ipp.clickByJS(objectReference);  
			if(objectReference.equalsIgnoreCase("Transactiondata_ProcessingCommunication")) {
				ipp.hardSleep(1);
				ipp.alert_accept();
			}
			if(!objectReference.equalsIgnoreCase("Transactiondata_VerifyButton")) {
				ipp.captureEvidence();
			}
			ipp.hardSleep(6);
//			ipp.captureScreenshot(screenShotName);
		} catch (Exception e) {
			logger.error(e);
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Then("{string} user select the value {string} for the field {string}")
	public void User_select_the_value_for_the_field(String user, String value, String objectReference) {
		
		try {
			ipp.moveToElement(objectReference);
			ipp.clickByJS(objectReference);
			List<WebElement> menus = ipp.getAllelements(By.xpath("//mat-option"));
			for (WebElement webElement : menus) {
				if (value.equalsIgnoreCase(webElement.getAttribute("value"))) {
					ipp.clickByJS(By.xpath("./span"));
					break;
				}
			}
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
		} catch (Exception e) {
			logger.error(e);
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
			e.printStackTrace();
			Assert.fail();
		}
	}

	@And("{string} user selects the option {string} from {string}")
	public void user_select_option_from_RadioButton(String user, String value, String objectReference) {
		
		try {
			ipp.selectRadioButton(objectReference, value);
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
		} catch (Exception e) {
			logger.error(e);
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Then("Verify {string} message {string} is displayed for {string}")
	public void verify_Error_Message(String alertType, String errorMessage, String objectReference) {
		
		if (errorMessage.equalsIgnoreCase(ipp.getElementText(objectReference))) {
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
		} else
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
		Assert.fail("Error not displayed as expected");
	}

	@Then("Validate Pen Test")
	public void validate_pen_test(){
		ipp.switchtoWindow("Transaction Data");
//		ipp.clickByJS("pentest_CreditChainInformation");
//		ipp.wait_For_ElementPresence("pentest_Debit_BIC");
		
		
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].removeAttribute('disabled','disabled')",ipp.getElement("pentest_202CreditorAcc_F58_ACCNO"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].removeAttribute('readonly','readonly')",ipp.getElement("pentest_202CreditorAcc_F58_ACCNO"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].removeAttribute('disabled','disabled')",ipp.getElement("pentest_Savebutton"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].setAttribute('readonly','readonly')",ipp.getElement("pentest_202CreditorAcc_F58_ACCNO"));

		
		ipp.RemoveAttribute("disabled", "disabled", "pentest_CreditorAcc_F58_ACCNO");
		ipp.RemoveAttribute("readonly", "readonly", "pentest_CreditorAcc_F58_ACCNO");
		ipp.RemoveAttribute("disabled", "disabled", "pentest_Savebutton");
		ipp.addAttribute("readonly", "readonly", "pentest_CreditorAcc_F58_ACCNO");
		
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].removeAttribute('disabled','disabled')",ipp.getElement("pentest_Debit_BIC"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].removeAttribute('readonly','readonly')",ipp.getElement("pentest_Debit_BIC"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].removeAttribute('disabled','disabled')",ipp.getElement("pentest_Savebutton"));

//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].setAttribute('readonly','readonly')",ipp.getElement("pentest_Debit_BIC"));
		ipp.enterValues_JS("pentest_CreditorAcc_F58_ACCNO", "4204200013499");
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", 
//		ipp.getElement("pentest_CreditorAcc_F58"), "value", "4204200013499");		
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", 
//				ipp.getElement("pentest_Debit_BIC"), "readonly", "true");
//		
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].removeAttribute('disabled','disabled')",ipp.getElement("pentest_Savebutton"));
		ipp.RemoveAttribute("disabled", "disabled", "pentest_Savebutton");
		ipp.clickByJS("pentest_Savebutton");
		ipp.clickByJS("pentest_CreditorAcc_F58");
		ipp.RemoveAttribute("disabled", "disabled", "pentest_CreditorAcc_F58");
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].removeAttribute('disabled','disabled')",ipp.getElement("pentest_CreditorAcc_F58"));
		ipp.clickByJS("pentest_CreditorAcc_F58");
		
//		ipp.hardSleep(10);
//		ipp.wait_For_ElementPresence("pentest_SelectAccount");
//		ipp.clickByJS("pentest_SelectAccount");
//		ipp.switchtoWindow("Accounts");
		
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", 
//				ipp.getElement("pentest_Balances"), "enabled", "enabled");

//		
		
//		ipp.clickByJS("pentest_Balances");
//		ipp.wait_For_ElementPresence("pentest_CalculatedBalance_Textbox");
//		logger.info(IPPMethods.driver.getPageSource());
//		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5");
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].value='9,90,001.00';", ipp.getElement("pentest_OpeningBalance_Textbox"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].value='9,90,001.00';", ipp.getElement("pentest_LedgerOpeningBalance_Textbox"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].value='9,90,001.00';", ipp.getElement("pentest_IntradayActualBalance_Textbox"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].value='9,90,001.00';", ipp.getElement("pentest_ClosingBookedBalance_Textbox"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].value='9,90,001.00';", ipp.getElement("pentest_ClosingAvailableBalance_Textbox"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].value='9,90,001.00';", ipp.getElement("pentest_ExtraAdditionalBalance_Textbox"));
//		
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].removeAttribute('id','4')",ipp.getElement("pentest_Close"));
//		((JavascriptExecutor) IPPMethods.driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", 
//				ipp.getElement("pentest_Close"), "id", "1");
//		logger.info(IPPMethods.driver.getPageSource());
//		ipp.clickByJS("pentest_Close");
//		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5");
//		logger.info(IPPMethods.driver.getPageSource());
System.out.println();		
	}
	
	/*
	 * @After public void closeBrowsers(Scenario scenario) { try { scenario_Status =
	 * scenario.getStatus().name(); ipp.flushproperty(IPPMethods.logproperty);
	 * scenarioResult.put(TC_id, scenario_Status); previousstepname = ""; try {
	 * 
	 * ipp.clickByJS("homepage_logout"); ipp.quitBrowser(); } catch
	 * (NoSuchElementException | WebDriverException e) { ipp.quitBrowser();
	 * e.printStackTrace(); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } // closeDBConnection(); }
	 */

	@And("Verify {string} for {string} user is autopopulated")
	public void verify_field_value_is_autopopulated(String objectReference, String user) {
		
		try {

			if (ipp.getElementValue(objectReference).equals("") || ipp.getElementValue(objectReference) != null) {
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
				Assert.fail();
			} else {
				ipp.captureEvidence();
//				ipp.captureScreenshot(screenShotName);
			}

		} catch (Exception e) {
			logger.error(e);
			ipp.captureEvidence();
//			ipp.captureScreenshot(screenShotName);
			e.printStackTrace();
		}
	}
	
//	public void updateEnvironmentDetails_in_Report(){
//		Reporter.loadXMLConfig(new File (ConfigurationManager.getInstance().getConfigReader().getreportConfigPath()));
//		Reporter.setSystemInfo("Executed On", ipp.getCurrentDate("dd-MMM-yyyy"));
//		Reporter.setSystemInfo("Executed In", ConfigurationManager.getInstance().getConfigReader().getEnvironment());
//		Reporter.setSystemInfo("Executed By", System.getProperty("user.name"));
//		Reporter.setSystemInfo("Browser Name", ConfigurationManager.getInstance().getConfigReader().getBrowser());
//
//		
//		try {
//			Reporter.setSystemInfo("HostName", java.net.InetAddress.getLocalHost().getHostName());
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		Reporter.setSystemInfo("Operating System", System.getProperty("os.name"));
//		String subContent = "", color = "", textContent = "" ;
//		 for (Entry<String, String> entry : BasicDefinition.ReporttableContent.entrySet()) {
//			 if(entry.getValue().split("-")[2].equalsIgnoreCase("COMPLETE")){
//				 textContent = "196619";
//				 color = "99e699";
//			 }
//			 else{
//				 textContent = "800000";
//				 color = "ff9999";
//			 }
//			 subContent = subContent + "<tr><td>"+entry.getKey()+"</td><td>"+entry.getValue().split("-")[0]+"</td> <td>"+entry.getValue().split("-")[1]+"</td> <td id=rpt {color: #"+textContent+";}><b>"+entry.getValue().split("-")[2]+"</b></td> </tr>";
//		 }
//		 String reportcontent = "<!DOCTYPE html><html><head><style>"+
//					"th, td#rpt {  padding: 3px;}tr > td#rpt:last-of-type {background-color:#"+color+"}</style></head><body><table style=\"width:100%\"> <tr> <th>TestCase ID</th><th>PAYMENT FLOW</th> "+
//				    "<th>MESSAGE ID</th>     <th>STATUS</th>   </tr> "+subContent+"</table>";
//		
//		Reporter.setTestRunnerOutput(reportcontent); 
//		
//	}
	
	//And User Enter TextNote 'textbox' 'Transactiondata_Text_Notes'
	@Then("User Enter TextNote {string} {string}")
    public void user_enter_value_in_Text_Notess(String elementType, String objectReference) {
           
           try {
        	   ipp.enterValues(objectReference, "For Testing");
        	 //  ipp.enterValues_JS("pentest_CreditorAcc_F58_ACCNO", "4204200013499");
                  /*ipp.clickByJS(objectReference);*/
        	   ipp.captureEvidence();
//                  ipp.captureScreenshot(screenShotName);
           } catch (Exception e) {
                  logger.error(e);
                  ipp.captureEvidence();
//                  ipp.captureScreenshot(screenShotName);
                  e.printStackTrace();
                  Assert.fail();
           }
    }
	

	@Then("User clicks Alert OK")
    public void user_click_alert_ok() {
        
           try {
        	  // ipp.enterValues(objectReference, "For Testing");
        	   ipp.alert_accept();
        	 //  ipp.enterValues_JS("pentest_CreditorAcc_F58_ACCNO", "4204200013499");
                  /*ipp.clickByJS(objectReference);*/

                 // ipp.captureScreenshot(screenShotName);
           } catch (Exception e) {
                  logger.error(e);
                  ipp.captureEvidence();
//                  ipp.captureScreenshot(screenShotName);
                  e.printStackTrace();
                  Assert.fail();
           }
    }
	
	

	public static void generateMap() throws IOException{
//		if(!(ConfigurationManager.getInstance().getConfigReader().getEnvironment().equalsIgnoreCase("NFT") ||
//				ConfigurationManager.getInstance().getConfigReader().getEnvironment().equalsIgnoreCase("PROD"))){
//	    	ConnectDatabase connectDb=new ConnectDatabase();
//	    	SOAPCall soapcall= new SOAPCall();
//	     	soapcall.validateAndChangeBusinessDate(connectDb, lukOffice);
//	    	soapcall.validateAndChangeBusinessDate(connectDb, globalOffice);
//		}
//	    	soapcall.messageDetails();
		excel = new File (Paths.get(".").toAbsolutePath().normalize().toString()+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"TestData"+File.separator+"TestData.xlsx");
		try {
			fis = new FileInputStream(excel);
			wb = new XSSFWorkbook(fis);
			testDataMap = xl.readsheet(wb);
			xl.merge_TestData_and_InputData();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		finally{
		fis.close();
		wb.close();
		}
	}
	
	public static Connection create_DBConnection() {
		setDBConfig();
		try {
			if (dbconnection == null || dbconnection.isClosed()) {
				Class.forName(dbDriver);
				dbconnection = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				Runtime.getRuntime().addShutdownHook(new Thread(() -> closeDBConnection()));
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return dbconnection;
	}



	public static void setDBConfig() {
		
	}

	public static void closeDBConnection() {

		try {
			if (!dbconnection.isClosed()) {
				dbconnection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String decryptText(String encryptedText) {
		byte[] passworddecoded = Base64.decodeBase64(new String(encryptedText));
		String password = new String(passworddecoded);
		return password;
	}
	}