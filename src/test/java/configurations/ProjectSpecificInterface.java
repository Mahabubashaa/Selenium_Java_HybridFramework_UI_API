package configurations;

public interface ProjectSpecificInterface extends CommonInterface {

	/**
	 * Convert the string content to XML File
	 * 
	 * @param filepath - path to write the XML File
	 */
	public void string_to_XMLFile(String filepath, String txt_content);

	/**
	 * Convert the XML file content to String
	 * 
	 * @param filepath - path to load the XML File
	 * @return return the content as String
	 */
	public String xmlfile_to_String(String filepath);

	
}
