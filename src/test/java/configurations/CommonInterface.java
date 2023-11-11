package configurations;

import java.awt.AWTException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.ibm.disthub2.impl.matching.selector.ParseException;

public interface CommonInterface {

	/**
	 * @author Mahabu Basha
	 * @param objectReference - Verify the element is displayed in DOM or not
	 * @return Return True or false
	 */
	public boolean isWebElementDisplayed(String objectReference);

	/**
	 * @param objectReference - Verify the element is displayed in DOM or not
	 * @return Return True or false
	 */
	public boolean isWebElementDisplayed(By objectReference);

	/**
	 * Mouse pointer will move to desired location
	 * 
	 * @param objectReference
	 */
	public void movetoSpecificLocation(String objectReference);

	/**
	 * Verify element is enabled in the WebPage
	 * 
	 * @param objectReference
	 * @return true or false
	 */
	public boolean isWebElementEnabled(String objectReference);

	/**
	 * Verify element is Selected / Checked in the WebPage
	 * 
	 * @param driver
	 * @param locator
	 * @return
	 */
	public boolean isWebElementSelected(String objectReference);

	/**
	 * Used to wait till the element is visible in DOM This method has been
	 * implemented to reduce the load of objects from JSON
	 * 
	 * @param driver
	 * @param locator
	 * 
	 */
	public void waitForPresence(By locator);

	/**
	 * Used to wait till the element is visible in DOM
	 * 
	 * @param objectReference
	 * 
	 */
	public void wait_For_ElementPresence(String objectReference);

	/**
	 * Used to set the mouse focus to desired locator and reduce the loading JSON
	 * Objects
	 * 
	 * @param locator
	 */
	public void moveToElement(By locator);

	/**
	 * Used to set the mouse focus to desired locator
	 * 
	 * @param locator
	 *
	 */
	public void moveToElement(String objectReference);

	/**
	 * Used to set the mouse focus to desired locator and to select the option
	 * 
	 * @param objectReference
	 * 
	 */
	public void move_andClick(String objectReference);

	/**
	 * Navigate to frame
	 * 
	 * 
	 * @param framereference - Enter frame reference like 1 , framename, //div
	 * @param frametype      - Enter the frame type index, name or webelement
	 */
	public void movetoframe(String framereference, String frametype);

	/**
	 * Exit from the frames and switch to Main Window
	 * 
	 * @param driver
	 * 
	 */
	public void moveoutofframe();

	/**
	 * Click the desired element
	 * 
	 * @param objectReference
	 *
	 */
	public void click(String objectReference);

	public void click(By objectReference);

	/**
	 * Click the element and ensure the desired page or pop up is getting displayed
	 * 
	 * @param objectReference
	 * @param referenceObject
	 * @param actionName
	 * @throws IOException
	 */
	public void click(String objectReference, String referenceObject, String actionName);

	/**
	 * Click the desired element using JavaScript
	 * 
	 * @param driver
	 * @param objectReference
	 * 
	 */
	public void clickByJS(String objectReference);

	public void clickByJS(By objectReference);

	/**
	 * To Set the property for the logger file
	 * 
	 * @param testcaseID
	 */
	public void setProperty(String testcaseID);

	/**
	 * Enter the Functional Keys for the desired element
	 * 
	 * @param objectReference
	 * @param keys
	 */
	public void captureScreenshot(String screenshotName);

	/**
	 * To ensure what type of the field is. Such as, Numeric, Alphanumeric or
	 * Special characters
	 * 
	 * @param objectReference
	 * @param fieldtype       - Numeric, Alphanumeric, Characters or Special
	 *                        characters
	 * @param supports        - Upto, Exact or MoreThan
	 * @param size            - Field length
	 * @return
	 */
	public void enterValues(String objectReference, Keys keys);

	/**
	 * Enter the values in the desired element
	 * 
	 * @param objectReference
	 * @param value
	 * @return
	 */

	public String enterValues(String objectReference, String value);

	/**
	 * Enter the values in the desired element
	 * 
	 * @param objectReference
	 * @param value
	 * @return
	 */
	public String enterValues_JS(String objectReference, String value);

	/**
	 * To Clear the Text field
	 * 
	 * @param objectReference
	 */

	public void clear(String objectReference);

	/**
	 * To Obtain the color of the WebElement
	 * 
	 * @param objectReference
	 * @return
	 */
	public String getColor(String objectReference);

	/**
	 * To Obtain the Font Family of the WebElement
	 * 
	 * @param objectReference
	 * @return
	 */
	public String getfontfamily(String objectReference);

	/**
	 * To Obtain the Font Size of the WebElement
	 * 
	 * @param objectReference
	 * @return
	 */
	public int getFontSize(String objectReference);

	/**
	 * To obtain the text value of the field
	 * 
	 * @param objectReference
	 * @return the description in the label / message of the locator
	 */
	public String getElementText(String objectReference);

	/**
	 * To obtain the attribute value of the field
	 * 
	 * @param objectReference
	 * @return
	 */
	public String getElementValue(String objectReference, String attributeName);

	/**
	 * To obtain the default attribute of the field
	 * 
	 * @param objectReference
	 * @return
	 */
	public String getElementValue(String objectReference);

	/**
	 * 
	 * Compare the UI text against the given text
	 * 
	 * @param objectReference - Element of the textBox or label to compare
	 * @param expectedText    - Actual value to compare with UI value
	 * @param objectType      - mention the object is Text box or Combo box
	 * @param fieldType       - Text field or label. i.e., if getText() then enter
	 *                        label or getAttribute() enter value
	 * @return
	 */
	public boolean compareText(String objectReference, String objectType, String expectedText);

	/**
	 * Compare the text with expected and actual text
	 * 
	 * @param actualText   - Received from WebPage
	 * @param expectedText - As per the test case
	 * @return
	 */

	public boolean compareText(String expectedText, String actualText);

	/**
	 * 
	 * To Launch the URL
	 * 
	 * @param url - URL address
	 */
	public void openURL();

	/**
	 * Return the list of identified elements
	 * 
	 * @param objectReference
	 * @return the list of web Elements identified
	 */
	public List<WebElement> getAllelements(String objectReference);

	public List<WebElement> getAllelements(By objectReference);

	/**
	 * Launch the browser which has been configured in Property File
	 */

	public void launchbrowser();

	/**
	 * Calculate the time difference between start time and end time defined in the
	 * parameter
	 * 
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public String responseDuration(long starttime, long endtime);

	/**
	 * 
	 * @param time
	 * @return
	 */
	public String responseDuration(long time);

	/**
	 * Used to fetch the desired value from the dropdown
	 * 
	 * @param objectReference : Webelement
	 * @param selectType      : How we going to select ["VisibleText / Value /
	 *                        Index"]
	 * @param passvalue       : if Visible Text [Naked value seen in UI] / Value
	 *                        [DOM Value] / Index [0-n]
	 */
	public void selectValuefromDropdown(String objectReference, String selectType, String passvalue);

	/**
	 * Load the JSON File
	 * 
	 * @param fileName - Specifiy the filename to get loaded
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void loadObjects(String fileName);

	/**
	 * To identify what type of locator to be chosen based on the object reference
	 * given
	 * 
	 * @param objectReference
	 * @return
	 */
	public By getLocator(String objectReference);

	/**
	 * To get the current Date
	 * 
	 * @param dateFormat E.g: "dd-MM-yyyy"
	 * @return
	 */
	public String getCurrentDate(String dateFormat);

	/**
	 * To get the current Time
	 * 
	 * @param dateFormat E.g: "HH_mm_ss"
	 * @return
	 */
	public String getCurrentTime(String timeFormat);

	/**
	 * Enter the left Arrow in the Web Page
	 */
	public void navigateBack();

	/**
	 * Enter the Right Arrow in the Web Page
	 */
	public void navigateForward();

	/**
	 * Clear the properties
	 * 
	 * @param property
	 */
	public void flushproperty(Properties property);

	/**
	 * Accept the Web alert pop up
	 */
	public void alert_accept();

	/**
	 * Dismiss the Web alert pop up
	 */
	public void alert_dismiss();

	/**
	 * Read the content from the Web alert pop up
	 */
	public String alert_readstring();

	/**
	 * Decrypt the encrypted text i.e., Get the plain text from the encrypted text
	 * 
	 * @param encryptedText
	 * @return
	 */
	public String decryptText(String encryptedText);

	/**
	 * Save the file from IE browser - Not Recommended
	 * 
	 * @throws AWTException
	 */
	public void SaveFile();

	/**
	 * Scroll down the Web Page
	 */
	public void scrollDown();

	/**
	 * Scroll up the Web Page
	 */
	public void scrollUp();

	/**
	 * Get the OS Details
	 * 
	 * @return
	 */
	public String osdetails();

	/**
	 * Quit all the opened browser initiated by the Webdriver
	 */
	public void quitBrowser();

	/**
	 * Close the current browser initiated by the Webdriver
	 */
	public void closeBrowser();

	/**
	 * Hard Sleep - Not Recommended
	 * 
	 * @param seconds
	 */
	public void hardSleep(int seconds);

	/**
	 * To Ensure file is available in the given path
	 * 
	 * @param downloadPath
	 * @param fileName
	 */
	public void isFileExists(String downloadPath, String fileName);

	/**
	 * Select the Radio button
	 * 
	 * @param objectReference
	 * @param value
	 * @return
	 */
	public boolean selectRadioButton(String objectReference, String value);

	/**
	 * Verify fileName defined is displayed or not
	 * 
	 * @param user
	 * @param fileName
	 */
	public boolean verify_given_File_is_available(String fileName);

	/**
	 * Switch the Window from one to another based on title of the page to open
	 * 
	 * @param Which window you need to switch
	 */
	public void switchtoWindow(String pageTitle);

	/**
	 * Highlight the parameterized element
	 * 
	 * @param objectReference element reference
	 */
	public void highlight_Element(String objectReference);

	/**
	 * Generate a random value based on the type mentioned
	 * 
	 * @param valueType - Numeric, Character or Alpha Numeric
	 * @param count     - number of length required
	 * @return
	 */
	String randomvalue(String valueType, int count);

	/**
	 * Display the value which has been selected from the combo box
	 * 
	 * @param objectReference Web element reference
	 * @return
	 */
	public String displaySelectedValue(String objectReference);

	/**
	 * Return the WebElement
	 * 
	 * @param objectReference - Element defined in JSON File
	 * @return
	 */
	public WebElement getElement(String objectReference);

	/**
	 * This method is used to convert the string which has been used for TestData
	 * HashMap and store in seperate map of sheet value
	 * 
	 * @param value content of test data sheet
	 * @return convert the string of test data in Map value
	 */
	public HashMap<String, String> stringtoHashMap(String value);

	/**
	 * This method is useful to add a new attribute in the WebElement
	 * 
	 * @param attributeName   - What attribute you want to add.
	 * @param attributeValue  - What value you want to add for the attribute.
	 * @param objectReference - Attribute set for the WebElement
	 */
	public void addAttribute(String attributeName, String attributeValue, String objectReference);

	/**
	 * This method is useful to remove a existing attribute in the WebElement
	 * 
	 * @param attributeName   - What attribute you want to add.
	 * @param attributeValue  - What value you want to add for the attribute.
	 * @param objectReference - Attribute to remove from the WebElement
	 */
	public void RemoveAttribute(String attributeName, String attributeValue, String objectReference);

	void doubleclick(String objectReference);

	/**
	 * This method helps to switch back to the main window in case of multiple
	 * windows exists
	 * 
	 * @param windowHandle - Holds main window unique ID
	 */

	void switchBackToMainWindow(String windowHandle);

	/**
	 * Embed the captured screenshot in the report
	 */
	public void captureEvidence();

	/**
	 * Embed the logs in the report
	 * 
	 * @param comment
	 */
	public void captureEvidence(String comment);

	/**
	 * This method helps to get the main window handle in case of multiple windows
	 * exists
	 */
	public String mainWindowHandle();

	String changeDateFormat(String date, String newformat) throws Exception;

	/**
	 * To obtain the text value of the field
	 * 
	 * @param Locator
	 * @return the description in the label / message of the locator
	 */
	String getElementTextBy(By locator);

	/**
	 * Used to wait till the element is visible in UI
	 * 
	 * @param objectReference
	 * 
	 */
	void wait_For_ElementVisible(String objectReference);

	/**
	 * Return the Given String with 2 decimal points and plain text
	 * 
	 * @param amount - Amount need to be change in the ###.00 formate
	 * @return
	 */
	String convertWith2Deciamls(String amount);

	void hoveranddoubleclick(String objectReference);
}
