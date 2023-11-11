package configurations;

public class ConfigurationManager {
	private static ConfigurationManager ConfigurationManager = new ConfigurationManager();
	 private static PropertyConfiguration configFileReader;
	 
	 private ConfigurationManager() {
	 }
	 
	 public static ConfigurationManager getInstance( ) {
	       return ConfigurationManager;
	 }
	 
	 public PropertyConfiguration getConfigReader() {
	 return (configFileReader == null) ? new PropertyConfiguration() : configFileReader;
	 }
}
