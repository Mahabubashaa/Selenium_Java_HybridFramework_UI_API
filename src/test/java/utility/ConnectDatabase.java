package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import configurations.ProjectSpecificInterface;
import report.JIRAReport;
import stepDefinitions.BasicDefinition;

public class ConnectDatabase {
	public ProjectSpecificInterface ipp = new IPPMethods();
	public static Logger logger = LogManager.getLogger(ConnectDatabase.class);
	public String dbDriver = null;
	public String dbURL = null;
	public String NFTdbURL = null;
	public String dbUserName = null;
	public String dbPassword = null;
	Connection connect = null;

	/**
	 * Collect the information to collect the DB
	 */
//	public void setDBConfig() {
//		String dbService = "";
//		dbDriver = ConfigurationManager.getInstance().getConfigReader().getDatabaseDriverName();
//		if(ConfigurationManager.getInstance().getConfigReader().getSID()==null || ConfigurationManager.getInstance().getConfigReader().getSID().equals("")) {
//			dbService = ConfigurationManager.getInstance().getConfigReader().getServiceName();
//			dbURL = "jdbc:oracle:thin:@"+ConfigurationManager.getInstance().getConfigReader().getDB_IP_Address()+":"+ ConfigurationManager.getInstance().getConfigReader().getDBPort()+"/"+dbService;
//		}
//		else {
//			dbService = ConfigurationManager.getInstance().getConfigReader().getSID();	
//			dbURL = "jdbc:oracle:thin:@"+ConfigurationManager.getInstance().getConfigReader().getDB_IP_Address()+":"+ ConfigurationManager.getInstance().getConfigReader().getDBPort()+":"+dbService;
//		}
//		if(ConfigurationManager.getInstance().getConfigReader().is_LoadBalancing_On().replaceAll(" ","").equalsIgnoreCase("yes"))	
//			dbURL = "jdbc:oracle:thin:"+ConfigurationManager.getInstance().getConfigReader().getDBUserName() +"/"
//						+ ipp.decryptText(ConfigurationManager.getInstance().getConfigReader().getDBPassword()) +"@"
//						+ "(DESCRIPTION=(CONNECT_TIMEOUT=5)"
//						+ "(LOAD_BALANCE=YES)"
//						+ "(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST="
//						+ ConfigurationManager.getInstance().getConfigReader().get_LB1_IPAddress() + ")(PORT="+ConfigurationManager.getInstance().getConfigReader().get_LB1_Port() + "))"
//						+ "(ADDRESS=(PROTOCOL=TCP)(HOST="
//						+ ConfigurationManager.getInstance().getConfigReader().get_LB2_IPAddress() + ")(PORT="+ConfigurationManager.getInstance().getConfigReader().get_LB2_Port() + ")))"
//						+ "(CONNECT_DATA= (SERVICE_NAME="+dbService+")(FAILOVER_MODE=(TYPE=select)(METHOD=BASIC))))";
//		
//		dbUserName = ConfigurationManager.getInstance().getConfigReader().getDBUserName();
//		dbPassword = ipp.decryptText(ConfigurationManager.getInstance().getConfigReader().getDBPassword());
//		
////		dbURL = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS =(PROTOCOL=TCP)(HOST=tdecc1-scan1.gb3.ocm.s7519772.oraclecloudatcustomer.com)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=LBGDT003_PDB001.gb3.ocm.s7519772.oraclecloudatcustomer.com)(SERVER=DEDICATED)))";
////		NFTdbURL = "jdbc:oracle:thin:IPP_RO/IPP_RO_USER@(DESCRIPTION=(CONNECT_TIMEOUT=5)(LOAD_BALANCE=YES)(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=rac94dtw-scan.service.test.group)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=rac95dtw-scan.service.test.group)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=IPP1E_APP.test.lloydsbanking.com)(FAILOVER_MODE=(TYPE=select)(METHOD=BASIC))))";
//		
////		NFTdbURL = 
////		"jdbc:oracle:thin:"+ConfigurationManager.getInstance().getConfigReader().getDBUserName() +"/"
////		+ ipp.decryptText(ConfigurationManager.getInstance().getConfigReader().getDBPassword()) +"@"
////		+ "(DESCRIPTION=(CONNECT_TIMEOUT=5)"
////		+ "(LOAD_BALANCE=YES)"
////		+ "(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST="
////		+ ConfigurationManager.getInstance().getConfigReader().get_LB1_IPAddress() + ")(PORT="+ConfigurationManager.getInstance().getConfigReader().get_LB1_Port() + "))"
////		+ "(ADDRESS=(PROTOCOL=TCP)(HOST="
////		+ ConfigurationManager.getInstance().getConfigReader().get_LB2_IPAddress() + ")(PORT="+ConfigurationManager.getInstance().getConfigReader().get_LB2_Port() + ")))"
////		+ "(CONNECT_DATA= (SERVICE_NAME="+dbService+")(FAILOVER_MODE=(TYPE=select)(METHOD=BASIC))))";
////	
//	
//	
//	}

//	/**
//	 * Fetch the unique record result from the database and store in a Map
//	 * @param query
//	 * @return
//	 */
//	@SuppressWarnings("unused")
//	public Map<String, String> fetchQueryResult_SingleRow(String query) {
//		setDBConfig();
//		String fetchedValue = null;
//		Connection connect = null;
//		ResultSet result = null;
//		Map<String, String> resultMap = new HashMap<String, String>();
//		int totalRows = 0;
//
//		try {
//			Class.forName(dbDriver);
//			connect = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
//			Statement statement = connect.createStatement();
//			result = statement.executeQuery(query);
////			System.out.println(query);
//			ResultSetMetaData header = result.getMetaData();
//			ipp.hardSleep(2);
//			int columnCount = header.getColumnCount();
//			while (result.next()) {
//				fetchedValue = result.getString(1);
//				totalRows++;
//				if (totalRows == 1) {
//					for (int i = 1; i <= columnCount; i++) {
//						resultMap.put(header.getColumnName(i), result.getString(i));
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (connect != null && !connect.isClosed()) {
//					connect.close();
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		return resultMap;
//	}
//
//	/**
//	 * Fetch the Multi record result from the database and store in a Map
//	 * @param query
//	 * @return
//	 */
//	@SuppressWarnings({ "unused", "rawtypes" })
//	public Map<String, String> fetchQueryResult_Multirows(String query) {
//		setDBConfig();
//		String fetchedValue = null;
//		Connection connect = null;
//		ResultSet result = null;
//		Map<String, String> resultMap = new HashMap<String, String>();
//		Map<String, String> multiresultMap = new HashMap<String, String>();
//		int totalRows = 0;
//
//		try {
//			Class.forName(dbDriver);
//			connect = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
//			Statement statement = connect.createStatement();
//			result = statement.executeQuery(query);
//			ResultSetMetaData header = result.getMetaData();
//
//			int columnCount = header.getColumnCount();
//			while (result.next()) {
//				resultMap.putAll(multiresultMap);
//				for (int i = 1; i <= columnCount; i++) {
//					multiresultMap.put(header.getColumnName(i), result.getString(i));
//				}
//				for (Map.Entry currentColumn : multiresultMap.entrySet()) {
//					if (resultMap.containsKey(currentColumn.getKey()))
//						multiresultMap.put(currentColumn.getKey().toString(),
//								resultMap.get(currentColumn.getKey()).toString() + "~" + currentColumn.getValue());
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (connect != null && !connect.isClosed()) {
//					connect.close();
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		return multiresultMap;
//	}
	
	
	@SuppressWarnings("unused")
	public String dbResult_to_JSON(String query) {
		BasicDefinition.setDBConfig();
		String fetchedValue = null;
		Connection connect = null;
		ResultSet result = null;
		int totalRows = 0;
		StringBuilder json = new StringBuilder("[");
		try {
			Class.forName(dbDriver);
			connect = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
			Statement statement = connect.createStatement();
			result = statement.executeQuery(query);
			ResultSetMetaData header = result.getMetaData();
			int columnCount = header.getColumnCount();

			while (result.next()) {
				totalRows++;
				json.append("{");
				for (int i = 1; i <= columnCount; i++) {
					json.append("\"").append(header.getColumnName(i)).append("\":\"").append(result.getString(i))
							.append("\",");
				}
				json.deleteCharAt(json.length() - 1);
				json.append("},");
			}
			json.deleteCharAt(json.length() - 1);
			json.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connect != null && !connect.isClosed()) {
					connect.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json.toString();
	}
	
	public LocalDate getBusinessDateFromDB(String office) {
		BasicDefinition.setDBConfig();
		Connection connect = null;
		ResultSet result = null;
		String businessDate=null;
		try {
			Class.forName(dbDriver);
			connect = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
			PreparedStatement statement = connect.prepareStatement("select BSNESSDATE from banks where OFFICE=?");
			statement.setString(1, office);
			result = statement.executeQuery();
			
			while (result.next()) {
				businessDate=result.getString("BSNESSDATE");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connect != null && !connect.isClosed()) {
					connect.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	return LocalDate.parse(businessDate.split(" ")[0], formatter);
	}

	
	
	/**
	 * Fetch the unique record result from the database and store in a Map
	 * @param query
	 * @return
	 */

	public List<Map<String, String>> getMultiRowsDB(String query) throws ClassNotFoundException, SQLException{
		BasicDefinition.setDBConfig();
		List<Map<String, String>> results = new ArrayList<>();

			Class.forName(dbDriver);
			connect = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
			Statement stmt = this.connect.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData metaData = rs.getMetaData();
			while(rs.next()) {
				Map<String, String> record = new LinkedHashMap<>();
				for(int col = 1; col<=metaData.getColumnCount(); col++)
					record.put(metaData.getColumnLabel(col),rs.getString(col));
				results.add(record);
			}
			return results;
}
	
	public Map<String, String> fetchQueryResult_SingleRow(String query) {
//		System.out.println(query);		
		Map<String, String> resultMap = new HashMap<String, String>();
		int totalRows = 0;
		try (PreparedStatement preparedStatement = BasicDefinition.create_DBConnection().prepareStatement(query)) {
			try (ResultSet result = preparedStatement.executeQuery()) {
				ResultSetMetaData header = result.getMetaData();
				int columnCount = header.getColumnCount();
				if (result.isBeforeFirst()) {
					while (result.next()) {
						
						totalRows++;
						if (totalRows == 1) {
							for (int i = 1; i <= columnCount; i++) {
//								System.out.println("DB Record: "+result.getString(i));
								resultMap.put(header.getColumnName(i), result.getString(i));
							}
						} else {
//							Assert.fail("Multiple Records Found");
							System.out.println("Multiple Records Found in Database. Have a look");
						}
					}
				}
				
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, String> fetchQueryResult_Multirows(String query) {
		Map<String, String> resultMap = new HashMap<String, String>();
		Map<String, String> multiresultMap = new HashMap<String, String>();
		try (PreparedStatement preparedStatement = BasicDefinition.create_DBConnection().prepareStatement(query)) {
			try (ResultSet result = preparedStatement.executeQuery()) {
				ResultSetMetaData header = result.getMetaData();
				int columnCount = header.getColumnCount();
				if (result.isBeforeFirst()) {
					while (result.next()) {
						resultMap.putAll(multiresultMap);
						for (int i = 1; i <= columnCount; i++) {
							multiresultMap.put(header.getColumnName(i), result.getString(i));
						}
						for (Map.Entry currentColumn : multiresultMap.entrySet()) {
							if (resultMap.containsKey(currentColumn.getKey()))
								multiresultMap.put(currentColumn.getKey().toString(),
										resultMap.get(currentColumn.getKey()).toString() + "~"
												+ currentColumn.getValue());
						}
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return multiresultMap;
	}
}
