package stepDefinitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.cucumber.java.en.Then;
import utility.IPPMethods;
public class LoginPage_Stepdef{
	
	public IPPMethods ipp = new IPPMethods();
	public static Logger logger = LogManager.getLogger(LoginPage_Stepdef.class);

	
	@Then("User Logs in to the Application")
	public void launch_the_Application(){
		try {
			ipp.launchbrowser();
			ipp.openURL();
			ipp.hardSleep(5);

		} catch (Exception e) {
			e.printStackTrace();
			ipp.captureEvidence();
			
		}
		
	}
	
	
}
