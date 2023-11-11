package utility;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import report.JIRAReport;
import stepDefinitions.BasicDefinition;

public class DBValidation extends CommonMethods {
public ConnectDatabase db = new ConnectDatabase();
public ReadExcel xl = new ReadExcel();
public static Logger logger = LogManager.getLogger(DBValidation.class);

// Can be optimise the code precisely
	/**
	 * Compare the record result with the Excel Value
	 * @param query
	 * @param dbColumnName
	 * @param xlvalue
	 */
public void dbFieldValidation(String query, String dbColumnName, String xlvalue) {
	Map<String, String> profileDetails = db.fetchQueryResult_Multirows(query);
	try {
		String excelfieldvalue = xl.getExcelValue("TestData",BasicDefinition.TC_id, xlvalue);
		String DBTrimmedValue = "";
		if(profileDetails.get(dbColumnName)!=null) {
			DBTrimmedValue = profileDetails.get(dbColumnName).trim();
		}
		else {
			DBTrimmedValue = profileDetails.get(dbColumnName);
		}
		if(excelfieldvalue.equalsIgnoreCase(DBTrimmedValue) ||
				(excelfieldvalue.equalsIgnoreCase("0") && DBTrimmedValue==null) ||
				(excelfieldvalue.equalsIgnoreCase("") && Integer.parseInt(DBTrimmedValue) ==0))
				logger.info(dbColumnName + " from UI - " + excelfieldvalue + "is match with "+ dbColumnName + " " + DBTrimmedValue);
		else
			logger.info(dbColumnName + ": DBValue is : " + DBTrimmedValue+ " "+ xlvalue + " : Excel value is "+excelfieldvalue );
			Assert.fail(dbColumnName + "doesn't match between DB and UI");
	}
	catch(Exception e) {
		e.printStackTrace();
	}
}

/**
 * Compare the result record with UI Value
 * @param query
 * @param dbColumnName
 * @param uiValue
 */
public void dbFieldValidation(String query, String dbColumnName, int uiValue) {
	Map<String, String> queryResult = db.fetchQueryResult_Multirows(query);
	String[] multivalues=null;
	int number_of_count=0;
	try {
		
		String DBTrimmedValue = "";
		if(queryResult.get(dbColumnName)!=null) {
			DBTrimmedValue = queryResult.get(dbColumnName).trim();
			if(DBTrimmedValue.contains("~"))
			 multivalues =	DBTrimmedValue.split("~");
		}
		else {
			DBTrimmedValue = queryResult.get(dbColumnName);
		}
		if(dbColumnName.contains("NO_OF_VIEWS") || dbColumnName.contains("NO_OF_SHARES") || dbColumnName.contains("NO_OF_SAVES")) {
			for (String string : multivalues) {
				number_of_count+=Integer.parseInt(string);
			}
			DBTrimmedValue = String.valueOf(number_of_count);
		}
			
		if(String.valueOf(uiValue).equalsIgnoreCase(DBTrimmedValue) ||
				(String.valueOf(uiValue).equalsIgnoreCase("") && DBTrimmedValue==null) ||
				(String.valueOf(uiValue).equalsIgnoreCase("") && Integer.parseInt(DBTrimmedValue) ==0) || 
				(uiValue==0 && (DBTrimmedValue) ==null))
			logger.info(dbColumnName + ": DBValue is : " + DBTrimmedValue+ " and UI value is "+uiValue);
				
		else
		{
			logger.info(dbColumnName + ": DBValue is : " + DBTrimmedValue+ " and UI value is "+uiValue);
			Assert.fail(dbColumnName + " doesn't match between DB and UI");
		}
	}
	catch(Exception e) {
		e.printStackTrace();
		Assert.fail();
	}
}


@SuppressWarnings("unused")
public void validatefields(String query) {
	Map<String, String> profileDetails = db.fetchQueryResult_SingleRow(query);
	try {
		dbFieldValidation(query, "DBColumnName", "excelcolumnName");
	}
	catch(Exception e) {
		e.printStackTrace();
	}
}

public void verifytBICtype(String BICtype, String BICvalue) {
	switch(BICtype) {
	case "HeadOffice":
		if(!BICvalue.endsWith("XXX")) {
			Assert.fail(BICvalue+" is not a Head Office BIC");
		}
		break;
	case "BranchOffice":
		if(BICvalue.endsWith("XXX")) {
			Assert.fail(BICvalue+" is not a Branch Office BIC");
		}
		break;
	default:
		Assert.fail("Please enter valid BICtype");
	}
}


public void verifySchememember(String strBICField, String scheme, String BICvalue) {
	Map<String, String> values = db.fetchQueryResult_SingleRow(
            "SELECT * FROM MEMBERSHIP WHERE MEMBERSHIP.MEMBERASSOCIATE = 'M' AND MEMBERSHIP.MEMBER_ID = '"+BICvalue+"'");
	if(!scheme.contains("not")) {
		if(values.isEmpty()) {
			Assert.fail(strBICField+" is not a scheme member");
		}
		else {
			logger.info(strBICField+" is a scheme member");
		}
	}
	else {
		if(values.isEmpty()) {
			logger.info(strBICField+" is not a scheme member");
		}
		else {
			Assert.fail(strBICField+" is a scheme member");
		}
	}
}


public void verifyACRelationship(String strBICField, String accountRelationship, String currency, String BICvalue) {
	Map<String, String> values = db.fetchQueryResult_SingleRow(
            "SELECT distinct substr(ACCOUNTS.CUST_CODE,6,11) FROM ACCOUNTS WHERE ACCOUNTS.CURRENCY = '"+currency+"' AND ACCOUNTS.CUST_CODE like '%"+BICvalue+"%'");
	if(!accountRelationship.contains("not")) {
		if(values.isEmpty()) {
			Assert.fail(strBICField+" does not have account relationship with currency "+currency);
		}
		else {
			logger.info(strBICField+" have account relationship with currency "+currency);
		}
	}
	else {
		if(values.isEmpty()) {
			logger.info(strBICField+" does not have account relationship with currency "+currency);
		}
		else {
			Assert.fail(strBICField+" have account relationship with currency "+currency);
		}
	}
}

public void verifySSI(String strBICField, String ssiRelationship, String currency, String BICvalue) {
	Map<String, String> values = db.fetchQueryResult_SingleRow(
            "SELECT * FROM BANK_ROUT WHERE BANK_ROUT.CURRENCY = '"+currency+"' AND BANK_ROUT.DESTINATION like '%"+BICvalue+"%'");
	if(!ssiRelationship.contains("not")) {
		if(values.isEmpty()) {
			Assert.fail(strBICField+" does not have SSI relationship with currency "+currency);
		}
		else {
			logger.info(strBICField+" have SSI relationship with currency "+currency);
		}
	}
	else {
		if(values.isEmpty()) {
			logger.info(strBICField+" does not have SSI relationship with currency "+currency);
		}
		else {
			Assert.fail(strBICField+" have SSI relationship with currency "+currency);
		}
	}
}

public String getLUK_TXID(String mid) {
	Map<String, String> values = db.fetchQueryResult_SingleRow(
            "select P_INSTR_ID from minf where p_mid='"+mid+"'");
	return values.get("P_INSTR_ID");
}

}
