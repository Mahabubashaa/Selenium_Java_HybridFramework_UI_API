package stepDefinitions;

import java.util.Map;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import configurations.ProjectSpecificInterface;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import report.JIRAReport;
import utility.ConnectDatabase;
import utility.DBValidation;
import utility.IPPMethods;
import utility.ReadExcel;

public class DB_Validation_Stepdef {
	public IPPMethods ipp = new IPPMethods();
	public ReadExcel xl = new ReadExcel();
	public ConnectDatabase db = new ConnectDatabase();
	public DBValidation dbv = new DBValidation();
	public static Logger logger = LogManager.getLogger(DB_Validation_Stepdef.class);

//	@Given("{string} BIC is {string}")
//	public void bic_is(String strBICField, String BICtype) throws Throwable {
//		try {
//			String BICvalue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, strBICField);
//			dbv.verifytBICtype(BICtype, BICvalue);
//		} catch (Exception e) {
//			Assert.fail("Failed while verifying BIC type");
//		}
//	}
//
//	@Given("{string} is {string}")
//	public void transactiondata_receiver_is_scheme_member(String strBICField, String scheme) throws Throwable {
//		String BICvalue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, strBICField);
//		dbv.verifySchememember(strBICField, scheme, BICvalue);
//	}
//
//	@Given("{string} {string} in Instructed currency {string} to receive payments")
//	public void transactiondata_receiver_does_not_have_Account_Relationship_in_Instructed_currency_EUR_to_receive_payments(
//			String strBICField, String accountRelationship, String currency) throws Throwable {
//		String BICvalue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, strBICField);
//		dbv.verifyACRelationship(strBICField, accountRelationship, currency, BICvalue);
//	}
//
//	@Given("{string} {string} in instruction currency {string}")
//	public void transactiondata_receiver_have_SSI_in_instruction_currency_EUR(String strBICField,
//			String ssiRelationship, String currency) throws Throwable {
//		String BICvalue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, strBICField);
//		dbv.verifySSI(strBICField, ssiRelationship, currency, BICvalue);
//	}
//
//	@Then("User verifies {string} is SSI agent BIC for {string}")
//	public void user_verifies_Transactiondata_Receiver_is_SSI_agent_BIC(String agentBIC, String destinationBIC)
//			throws Throwable {
//		String agentBICValue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, agentBIC);
//		String destinationBICValue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, destinationBIC);
//		Map<String, String> values = db
//				.fetchQueryResult_SingleRow("SELECT * FROM BANK_ROUT WHERE BANK_ROUT.AGENT like '%" + agentBICValue
//						+ "%' AND BANK_ROUT.DESTINATION like '%" + destinationBICValue + "%'");
//		if (values.isEmpty()) {
//			Assert.fail("Agent & Destination BICS dont have SSI relationship");
//		}
//	}

	@Given("Creditor BIC is {string}")
	public void bic_is(String BICtype) throws Throwable {
		try {
			String BICvalue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, "Transactiondata_Receiver");
			verifytBICtype(BICtype, BICvalue);
		} catch (Exception e) {
			Assert.fail("Failed while verifying BIC type");
		}
	}

	public void verifytBICtype(String BICtype, String BICvalue) {
		switch (BICtype) {
		case "HeadOffice":
			if (!BICvalue.endsWith("XXX")) {
				Assert.fail(BICvalue + " is not a Head Office BIC");
			}
			break;
		case "BranchOffice":
			if (BICvalue.endsWith("XXX")) {
				Assert.fail(BICvalue + " is not a Branch Office BIC");
			}
			break;
		default:
			Assert.fail("Please enter valid BICtype");
		}
	}

	@Given("Creditor BIC is {string} member")
	public void transactiondata_receiver_is_scheme_member(String scheme) throws Throwable {
		String BICvalue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, "Transactiondata_Receiver");
		verifySchememember(scheme, BICvalue);
	}

	public void verifySchememember(String scheme, String BICvalue) {
		Map<String, String> values = db.fetchQueryResult_SingleRow(
				"SELECT * FROM MEMBERSHIP WHERE MEMBERSHIP.MEMBERASSOCIATE = 'M' AND MEMBERSHIP.MEMBER_ID = '"
						+ BICvalue + "'");
		if (!scheme.contains("not")) {
			if (values.isEmpty()) {
				Assert.fail(BICvalue + " is not a scheme member");
			} else {
				logger.info(BICvalue + " is a scheme member");
			}
		} else {
			if (values.isEmpty()) {
				logger.info(BICvalue + " is not a scheme member");
			} else {
				Assert.fail(BICvalue + " is a scheme member");
			}
		}
	}

	@Given("Creditor BIC {string} Account Relationship in Instructed currency {string} to receive payments")
	public void transactiondata_receiver_does_not_have_Account_Relationship_in_Instructed_currency_to_receive_payments(
			String accountRelationship, String currency) throws Throwable {
		String BICvalue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, "Transactiondata_Receiver");
		verifyACRelationship(accountRelationship, currency, BICvalue);
	}

	public void verifyACRelationship(String accountRelationship, String currency, String BICvalue) {
		Map<String, String> values = db.fetchQueryResult_SingleRow(
				"SELECT distinct substr(ACCOUNTS.CUST_CODE,6,11) FROM ACCOUNTS WHERE ACCOUNTS.CURRENCY = '" + currency
						+ "' AND ACCOUNTS.CUST_CODE like '%" + BICvalue + "%'");
		if (!accountRelationship.contains("not")) {
			if (values.isEmpty()) {
				Assert.fail(BICvalue + " does not have account relationship with currency " + currency);
			} else {
				logger.info(BICvalue + " have account relationship with currency " + currency);
			}
		} else {
			if (values.isEmpty()) {
				logger.info(BICvalue + " does not have account relationship with currency " + currency);
			} else {
				Assert.fail(BICvalue + " have account relationship with currency " + currency);
			}
		}
	}

	@Given("Creditor BIC {string} SSI in instruction currency {string}")
	public void transactiondata_receiver_have_SSI_in_instruction_currency(String ssiRelationship, String currency)
			throws Throwable {
		String BICvalue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, "Transactiondata_Receiver");
		verifySSI(ssiRelationship, currency, BICvalue);
	}

	public void verifySSI(String ssiRelationship, String currency, String BICvalue) {
		Map<String, String> values = db
				.fetchQueryResult_SingleRow("SELECT * FROM BANK_ROUT WHERE BANK_ROUT.CURRENCY = '" + currency
						+ "' AND BANK_ROUT.DESTINATION like '%" + BICvalue + "%'");
		if (!ssiRelationship.contains("not")) {
			if (values.isEmpty()) {
				Assert.fail(BICvalue + " does not have SSI relationship with currency " + currency);
			} else {
				logger.info(BICvalue + " have SSI relationship with currency " + currency);
			}
		} else {
			if (values.isEmpty()) {
				logger.info(BICvalue + " does not have SSI relationship with currency " + currency);
			} else {
				Assert.fail(BICvalue + " have SSI relationship with currency " + currency);
			}
		}
	}

	@Then("User verifies {string} is SSI agent BIC for {string}")
	public void user_verifies_Transactiondata_Receiver_is_SSI_agent_BIC(String agentBIC, String destinationBIC)
			throws Throwable {
		String agentBICValue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, agentBIC);
		String destinationBICValue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, destinationBIC);
		Map<String, String> values = db
				.fetchQueryResult_SingleRow("SELECT * FROM BANK_ROUT WHERE BANK_ROUT.AGENT like '%" + agentBICValue
						+ "%' AND BANK_ROUT.DESTINATION like '%" + destinationBICValue + "%'");
		if (values.isEmpty()) {
			Assert.fail("Agent & Destination BICS dont have SSI relationship");
		}
	}
	@Given("User picks the payment older than {string} days")
	public void user_picks_older_payment(String numofdays) {
		try {
			Map<String, String> MID_Db = db.fetchQueryResult_SingleRow("SELECT P_MID,P_PROC_DT FROM MINF WHERE P_DBT_MOP = 'SCT' AND P_CDT_MOP = 'BOOK' AND P_MSG_STS = 'COMPLETE' "
	        		+ "AND P_MSG_TYPE = 'Pacs_008' AND P_DBT_ACCT_CCY = 'EUR' AND P_CDT_ACCT_CCY = 'EUR' AND TRUNC(P_PROC_DT) = TRUNC(SYSDATE -"+ xl.getExcelValue("SEPA", BasicDefinition.TC_id, "Days_Count")+")");
            BasicDefinition.ReporttableContent .put(BasicDefinition.TC_id,MID_Db.get("P_MID"));

		}catch(Exception e) {
		Assert.fail("Failed");	
		}
	}
	
	@Given("Interface {string} should be active")
	public void verifyInterfaceStatus(String interfaceName){
		try {
			Map<String, String> interfaceStatus = db
					.fetchQueryResult_SingleRow("SELECT INTERFACE_STATUS FROM INTERFACE_TYPES WHERE INTERFACE_NAME ='"+interfaceName+"'");
			if(!interfaceStatus.get("INTERFACE_STATUS").equalsIgnoreCase("ACTIVE"))
				Assert.fail(interfaceName+" Interface is not Active");
		}catch(Exception e) {
			Assert.fail("Failed while verifying Interface status");
		}
	}
	
	@Given("Request Connection point for {string} should be {string}")
	public void verifyRequestConnectionPoint(String interfaceName, String connectionPoint){
		try {
			Map<String, String> interfaceValues = db
					.fetchQueryResult_SingleRow("SELECT REQUEST_CONNECTIONS_POINT FROM INTERFACE_TYPES WHERE INTERFACE_NAME ='"+interfaceName+"'");
			if(!interfaceValues.get("REQUEST_CONNECTIONS_POINT").contains(connectionPoint))
				Assert.fail(interfaceName+" Request Connection Point is not as expected");
		}catch(Exception e) {
			Assert.fail("Failed while verifying Interface Request Connection Point");
		}
	}
	
	@Then("{string} {string} Account Relationship in Instructed currency {string} to receive payments")
	public void validateaccountingRelationship_for_differentFileTypes(String fieldName,String accountRelationship,String currency) {
		String filename = xl.getExcelValue("TestData", BasicDefinition.TC_id, "FileType");
		String fieldValue=null;
		switch (filename.toLowerCase()) {
		case "pain001":
			fieldValue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, fieldName);
			break;
		case "SWIFTMT":	
			fieldValue = xl.getExcelValue("SWIFTMT", BasicDefinition.TC_id, fieldName);
			break;
		case "MT103":	
			fieldValue = xl.getExcelValue("SWIFTMT", BasicDefinition.TC_id, fieldName);
			break;
		}
	
		verifyACRelationship(accountRelationship, currency, fieldValue);
		}
	//And 'CreditorBIC' 'has' SSI for currency 'CNY'
	@Given("{string} {string} SSI for currency {string}") 
	public void validate_SSI_for_differentFileTypes(String fieldName,String SSI_Relationship,String currency) {
		String filename = xl.getExcelValue("TestData", BasicDefinition.TC_id, "FileType");
		String fieldValue=null;
		switch (filename.toLowerCase()) {
		case "pain001":
			fieldValue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, fieldName);
			break;
		case "SWIFTMT":	
			fieldValue = xl.getExcelValue("SWIFTMT", BasicDefinition.TC_id, fieldName);
			break;
		case "MT103":	
			fieldValue = xl.getExcelValue("SWIFTMT", BasicDefinition.TC_id, fieldName);
			break;
		}
		verifySSI(SSI_Relationship, currency, fieldValue);
	}
		
	//'CreditorBIC' 'has' RMA relationship for currency 'CNY'
	@Given("{string} {string} RMA relationship for currency {string}")
	public void validate_RMA_for_differentFileTypes(String fieldName,String RMA_Relationship,String currency) {
	String filename = xl.getExcelValue("TestData", BasicDefinition.TC_id, "FileType");
	String fieldValue=null;
	switch (filename.toLowerCase()) {
	case "pain001":
		fieldValue = xl.getExcelValue("Pain001", BasicDefinition.TC_id, fieldName);
		break;
	case "SWIFTMT":	
		fieldValue = xl.getExcelValue("SWIFTMT", BasicDefinition.TC_id, fieldName);
		break;
	case "MT103":	
		fieldValue = xl.getExcelValue("SWIFTMT", BasicDefinition.TC_id, fieldName);
		break;
	}
	fieldValue=fieldValue.substring(0, 8);
	verifyRMA(RMA_Relationship,currency,fieldValue);
	}
	
	public void verifyRMA(String RMA_Relationship, String currency, String BICvalue) {
		Map<String, String> values = db
				.fetchQueryResult_SingleRow("SELECT ISSUER FROM RMA_PROFILE where issuer like '%" + BICvalue + "%'");
		if (!RMA_Relationship.contains("not")) {
			if (values.isEmpty()) {
				Assert.fail(BICvalue + " does not have SSI relationship with currency " + currency);
			} else {
				logger.info(BICvalue + " have SSI relationship with currency " + currency);
			}
		} else {
			if (values.isEmpty()) {
				logger.info(BICvalue + " does not have SSI relationship with currency " + currency);
			} else {
				Assert.fail(BICvalue + " have SSI relationship with currency " + currency);
			}
		}
	}
}
