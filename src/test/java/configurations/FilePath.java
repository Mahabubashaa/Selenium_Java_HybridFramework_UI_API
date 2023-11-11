package configurations;

import java.io.File;

public class FilePath {

	public static final String PROJECT_FOLDER = System.getProperty("user.dir");
	public static final String TEST_SRC_PATH = PROJECT_FOLDER + File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator;
	public static final String TEST_RESOURCE_PATH = PROJECT_FOLDER +File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator;
	public static final String TEST_CONFIG_PATH = TEST_RESOURCE_PATH + "Configuration"+File.separator;
	public static final String TEST_REPORT_PATH = PROJECT_FOLDER +File.separator+"TestReport"+File.separator;
	public static final String API_CONFIGURATION = TEST_RESOURCE_PATH +"Configuration"+File.separator+"API"+File.separator+"API_Configuration.properties";
	public static final String API_CERTZ_PATH = TEST_RESOURCE_PATH +"Certificate"+File.separator;
	public static final String TEST_DRIVER_PATH = TEST_RESOURCE_PATH + "Drivers" + File.separator;
	public static final String API_REQUEST_PATH = TEST_CONFIG_PATH + "API_Requests"+File.separator;
	
}
