package utility;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.properties.PropertiesConfigurationBuilder; 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import configurations.ConfigurationManager;
import configurations.FilePath;
import configurations.ProjectSpecificInterface;
import report.JIRAReport;
import stepDefinitions.BasicDefinition;

public class CommonMethods implements ProjectSpecificInterface{

	public static Logger logger = LogManager.getLogger(CommonMethods.class);
	public static WebDriver driver;
	public static Properties logproperty;
	public static String currentImagePath = "";
	public static ReadExcel xl = new ReadExcel();
	public static String mainWindow = "";
	
	@Override
	public boolean isWebElementDisplayed(String objectReference) {
		boolean elementDisplayed = false;
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		
		try {
			boolean isPresent = driver.findElements(locator).size() > 0;
			if (isPresent) {
				moveToElement(locator);
				elementDisplayed = driver.findElement(locator).isDisplayed();
				}
		} catch (NoSuchElementException e) {
			logger.info(locator + " is not Displayed in DOM");

		}
		return elementDisplayed;
	}

	@Override
	public boolean isWebElementDisplayed(By objectReference) {
		boolean elementDisplayed = false;
		try {
			boolean isPresent = driver.findElements(objectReference).size() > 0;
			if (isPresent) {
				moveToElement(objectReference);
				elementDisplayed = driver.findElement(objectReference).isDisplayed();
				}
		} catch (NoSuchElementException e) {
			logger.info(objectReference + " is not Displayed in DOM");

		}
		return elementDisplayed;
	}
	@Override
	public void movetoSpecificLocation(String objectReference) {
		WebElement ele = driver.findElement(getLocator(objectReference));
		Actions act = new Actions(driver);
		act.moveToElement(ele);
		
		
	}

	@Override
	public boolean isWebElementEnabled(String objectReference) {
		boolean elementEnabled = false;
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		try {
			boolean isPresent = driver.findElements(locator).size() > 0;
			if (isPresent && driver.findElement(locator).isDisplayed())
				elementEnabled = driver.findElement(locator).isEnabled();
		} catch (NoSuchElementException e) {
			logger.info(locator + " is not Enabled");
		}
		return elementEnabled;
	}

	@Override
	public boolean isWebElementSelected(String objectReference) {
		boolean elementSelected = false;
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		try {
			boolean isPresent = driver.findElements(locator).size() > 0;
			if (isPresent && driver.findElement(locator).isDisplayed())
				elementSelected = driver.findElement(locator).isSelected();
		} catch (NoSuchElementException e) {
			logger.info(locator + " is not Selected");

		}
		return elementSelected;
	}

	@Override
	public void waitForPresence(By locator) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		} catch (NoSuchElementException e) {
			logger.error(e);
			logger.info(locator + " is not Displayed in DOM");
		}
		
	}
	@Override
	public void captureScreenshot(String screenshotName) {
			File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			Date date = new Date();
			SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yy'_'HH-mm-ss");
			try {
				String path = screenshotName + "_" + dateformat.format(date) + ".png";
				currentImagePath = "TestReport" + File.separator + "TestEvidence" + File.separator + path;
				FileUtils.copyFile(srcFile, new File(currentImagePath));

			} catch (IOException e) {
				System.out.println("Folder Not Exists to save a file");
				e.printStackTrace();
			}
			captureScreenshot(screenshotName);
	}

	@Override
	public void wait_For_ElementPresence(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		try {

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			
		} catch (NoSuchElementException e) {
			logger.error(e);
			logger.info(locator + " is not Displayed in DOM");
		}
		
	}

	@Override
	public void moveToElement(By locator) {
		waitForPresence(locator);
		WebElement element = driver.findElement(locator);
		((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void moveToElement(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		waitForPresence(locator);
		WebElement element = driver.findElement(getLocator(objectReference));
		((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void move_andClick(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		waitForPresence(locator);
		WebElement element = driver.findElement(locator);
		((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
		
	}

	@Override
	public void movetoframe(String framereference, String frametype) {
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
		if (frametype.equalsIgnoreCase("index")) {
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(Integer.parseInt(framereference)));
		} else if (frametype.equalsIgnoreCase("name") || frametype.equalsIgnoreCase("id")) {
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(framereference));
		}
		else if ((frametype.equalsIgnoreCase("webelement"))) {
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(driver.findElement(By.xpath(framereference))));
		}
		
	}

	@Override
	public void moveoutofframe() {
		
		driver.switchTo().defaultContent();
	}

	@Override
	public void click(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		moveToElement(objectReference);
		waitForPresence(locator);
		driver.findElement(locator).click();
		logger.info(objectReference+ " is Clicked");
		
	}

	@Override
	public void click(By locator){
		waitForPresence(locator);
		driver.findElement(locator).click();
		logger.info(locator + " is Clicked");
	}

	@SuppressWarnings("unused")
	@Override
	public void click(String objectReference, String referenceObject, String actionName) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		loadObjects(referenceObject.split("_")[0].toLowerCase());
		By referencelocator = getLocator(referenceObject);
		waitForPresence(locator);
		driver.findElement(locator).click();
		logger.info(objectReference+ " is Clicked");
		long starttime = System.currentTimeMillis();
		waitForPresence(referencelocator);
		long endtime = System.currentTimeMillis();
		long duration = endtime - starttime;
		float sec = (float) duration / (float) 1000;
		
	}

	@Override
	public void clickByJS(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		waitForPresence(locator);
		WebElement element = driver.findElement(locator);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
		logger.info(objectReference+ " is Clicked"); 
		
	}
	
	@Override
	public void clickByJS(By locator){
		waitForPresence(locator);
		WebElement element = driver.findElement(locator);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
		logger.info(locator+ " is Clicked");
	}

	 public void setProperty(String testcaseID) {
         FileOutputStream fileOut = null;
         FileInputStream fileIn = null;
         File files = new File(FilePath.TEST_CONFIG_PATH + "log4j2-test.properties");
         try {
               logproperty = new Properties();
               fileIn = new FileInputStream(files);
               logproperty.load(fileIn);
               logproperty.setProperty("property.filename",
                            FilePath.TEST_REPORT_PATH + "Logs" + File.separator + testcaseID + ".log");
               fileOut = new FileOutputStream(files);
               logproperty.store(fileOut, "log");
         } catch (Exception e) {
               e.printStackTrace();
         } finally {
               try {
                      fileOut.close();
               } catch (IOException e) {
                      e.printStackTrace();
               }
         }

         LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.setConfigLocation(files.toURI());

      }
	@Override
	public void enterValues(String objectReference, Keys keys) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
			waitForPresence( locator);
			driver.findElement(locator).sendKeys(keys);
	}

	@SuppressWarnings("null")
	@Override
	public String enterValues(String objectReference, String value) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		moveToElement(locator);
		if (value != null || !(value.equalsIgnoreCase(""))) {
			waitForPresence( locator);
			driver.findElement(locator).clear();
			driver.findElement(locator).sendKeys(value);
			logger.info("Enter the value: "+ value+" in "+objectReference);
		}
		return value;
	}

	@Override
	public void clear(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		waitForPresence(locator);
		driver.findElement(locator).clear();
	}

	@Override
	public String getColor(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		return driver.findElement(locator).getCssValue("color");
	}

	@Override
	public String getfontfamily(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		return driver.findElement(locator).getCssValue("font-family");
	}

	@Override
	public int getFontSize(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		return Integer.parseInt(driver.findElement(locator).getCssValue("font-size"));
	}

	@Override
	public String getElementText(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		String text = driver.findElement(locator).getText();
		return text;
	}

	@Override
	public String getElementValue(String objectReference, String attributeName) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		String text = driver.findElement(locator).getAttribute(attributeName);
		return text;
	}

	@Override
	public String getElementValue(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		this.moveToElement(objectReference);
		String text = driver.findElement(locator).getAttribute("value");
		this.highlight_Element(objectReference);
		return text;
	}

	@Override
	public boolean compareText(String objectReference, String objectType, String expectedText) {
		boolean matches = false;
		String actualText = "";
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		
		try {
			
			switch(objectType.toLowerCase().replace(" ", "")){
			case "textbox":
				actualText = driver.findElement(locator).getAttribute("value");
				break;
			case "combobox":
				actualText = this.displaySelectedValue(objectReference);
				break;
			case "valuebox":
				actualText = this.getElementValue(objectReference);
				break;
			case "label":
				actualText = driver.findElement(locator).getText();
				break;
			}
			this.highlight_Element(objectReference);
			} catch (Exception e) {
			e.printStackTrace();	
			Assert.fail(objectReference + " is not found");
		}
		if ((actualText != null) && (expectedText != null)) {
			if( expectedText.contains(".") &&
					(expectedText.length()>3) &&
					(".".equals(String.valueOf(expectedText.charAt(expectedText.length()-3)))	&& 	
					".".equals(String.valueOf(actualText.charAt(actualText.length()-3))))){

				actualText = actualText.replaceAll(",", "");
				expectedText = expectedText.replaceAll(",", "");
			}
			if (expectedText.equalsIgnoreCase(actualText)) {
				matches = true;
				logger.info(objectReference + " value in Application is : "+actualText + " The Value we need to compare is " + expectedText);
			}
			else
				logger.info(objectReference + " value in Application is : "+actualText + " The Value we need to compare is " + expectedText);
		
	}
		return matches;
	}

	@Override
	public boolean compareText(String expectedText, String actualText) {
		boolean matches = true;
		if ((actualText != null) && (expectedText != null)) {
			if (expectedText.trim().equalsIgnoreCase(actualText.trim())) {
				matches = true;
				logger.info("Value in Application is : "+actualText + " and the value we need to compare is " + expectedText);
			}
			else{
				logger.info("Value in Application is : "+actualText + " and the value we need to compare is " + expectedText);
			matches = false;
			}
		}
		else
			logger.info("Value in Application is : "+actualText + " and the value we need to compare is " + expectedText);
		return matches;
	}

	@Override
	public void openURL() {
		driver.get(ConfigurationManager.getInstance().getConfigReader().getURL());
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		logger.info("Opened the URL: "+ ConfigurationManager.getInstance().getConfigReader().getURL());
	}


	@Override
	public List<WebElement> getAllelements(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		return driver.findElements(locator);
	}

	@Override
	public List<WebElement> getAllelements(By objectReference) {
		return driver.findElements(objectReference);
	}
	
	@Override
	public void launchbrowser() {
		String browser = ConfigurationManager.getInstance().getConfigReader().getBrowser();
		switch (browser.toLowerCase()) {
		case "chrome":
			this.chromeBrowser();
			break;

		case "headless":
			this.virtualEdgeBrowser();
			break;
		case "chromeheadless":
			try {
				this.chromeHeadless();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			this.edgeBrowser();
		}

	}

	private void edgeBrowser() {
		System.setProperty("webdriver.edge.driver",
				FilePath.TEST_DRIVER_PATH+ "msedgedriver.exe");
		EdgeOptions option = new EdgeOptions();
		option.addArguments("ignore-certificate-errors");
		option.setAcceptInsecureCerts(true);
		option.addArguments("--log-level=3");
		option.addArguments("--silent");
		driver = new EdgeDriver(option);
		driver.manage().window().maximize();
		logger.info("Edge Browser Launched");

	}
	
	private void chromeBrowser() {
		System.setProperty("webdriver.chrome.driver",
				FilePath.TEST_DRIVER_PATH+"chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("useAutomationExtension", false);
		options.addArguments("ignore-certificate-errors");
		options.addArguments("--start-maximized");
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		driver = new ChromeDriver(options);
		logger.info("Chrome Browser Launched");		
	}

	private void chromeHeadless() throws Exception {
		String os = System.getProperty("os.name");
		System.out.println("Execution on "+os+" OS");
		
		if(os.contains("Windows")) {
			System.setProperty("webdriver.chrome.driver",
					FilePath.TEST_DRIVER_PATH+"chromedriver.exe");
		}else if(os.contains("Linux")){
			try {
				Process processs = Runtime.getRuntime().exec("chmod 777 "+FilePath.TEST_DRIVER_PATH+"Linux"+File.separator+"chromedriver");
				processs.waitFor();
				System.setProperty("webdriver.chrome.driver",
						FilePath.TEST_DRIVER_PATH+"Linux"+File.separator+"chromedriver");
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				throw new Exception("Something Problem in initiating the Chrome driver in Linux OS");
			}
		}
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("ignore-certificate-errors");
		options.setExperimentalOption("useAutomationExtension", false);
		options.addArguments("--start-maximized");
		options.addArguments("--headless");
		options.addArguments("enable-automation");
		options.addArguments("--no-sandbox");
		options.addArguments("disable-infobars"); 
		options.addArguments("--disable-dev-shm-usage"); 
		options.addArguments("--disable-extensions");
		options.addArguments("--dns-prefetch-disable");
		options.addArguments("--disable-gpu");
		options.addArguments("window-size=1382,744");
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		driver = new ChromeDriver(options);
		logger.info("Chrome Browser Launched");
	}
	
	private void virtualEdgeBrowser() {
		System.setProperty("webdriver.edge.driver", FilePath.TEST_DRIVER_PATH+"msedgedriver.exe");
		 EdgeOptions options = new EdgeOptions();
			options.addArguments("headless", "start-maximized", "ignore-certificate-errors");
			driver = new EdgeDriver(options);
			logger.info("Edge Browser Launched");
         
	}

	@Override
	public String responseDuration(long starttime, long endtime) {
		long duration = endtime - starttime;
		int hrs = (int) ((duration/1000)/60)/60;
		hrs %= 60;
		int min = (int) (duration/1000)/60;
		min %= 60;
		int sec = (int) (duration/1000);
		sec = sec %= 60;
		int millisec = (int) (duration - (sec*1000) + (min*60*1000) + (hrs*60*60*1000));
		millisec %= 1000;
		return hrs+"h "+min+"m "+sec+"s+"+millisec+"ms";
	}

	@Override
	public String responseDuration(long time) {
		int hrs = (int) ((time/1000)/60)/60;
		hrs %= 60;
		int min = (int) (time/1000)/60;
		min %= 60;
		int sec = (int) (time/1000);
		sec = sec %= 60;
		int millisec = (int) (time - (sec*1000) + (min*60*1000) + (hrs*60*60*1000));
		millisec %= 1000;
		return hrs+"h "+min+"m "+sec+"s+"+millisec+"ms";
	}

	@Override
	public void selectValuefromDropdown(String objectReference, String selectType, String passvalue) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
			waitForPresence( locator);
		Select value = new Select(driver.findElement(locator));
		if(selectType.toLowerCase().replace(" ", "").equalsIgnoreCase("visibletext"))
		value.selectByVisibleText(passvalue);
		else if(selectType.toLowerCase().replace(" ", "").equalsIgnoreCase("value"))
			value.selectByValue(passvalue);
		else if(selectType.toLowerCase().replace(" ", "").equalsIgnoreCase("index"))
			value.selectByIndex(Integer.parseInt(passvalue));
	}

	@Override
	public String displaySelectedValue(String objectReference){
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		waitForPresence( locator);
		Select displayvalue = new Select(driver.findElement(locator));
		return displayvalue.getFirstSelectedOption().getText();
	}
	
	
	
	private enum LocatorType{
		id,name, cssselector, tagname, partiallinktext, linktext, xpath, classname;
	}

	@Override
	public void loadObjects(String fileName) {
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(new FileReader(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"Object_Properties"+File.separator+fileName+".json"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
		BasicDefinition.objectRepo = (JSONObject) obj;
		
	}

	@Override
	public By getLocator(String objectReference) {
		if(objectReference!=null) {
			String dataReference = (String) BasicDefinition.objectRepo.get(objectReference);
			By locator = null;
			if(dataReference != null && dataReference.split(Pattern.quote("|")).length==2) {
				LocatorType locatorType = LocatorType.valueOf(dataReference.split(Pattern.quote("|"))[0]);
				switch(locatorType) {
				case id:
					locator = By.id(dataReference.split(Pattern.quote("|"))[1]);
					break;
				case name:
					locator = By.name(dataReference.split(Pattern.quote("|"))[1]);
					break;
				case xpath:
					locator = By.xpath(dataReference.split(Pattern.quote("|"))[1]);
					break;
				case cssselector:
					locator = By.cssSelector(dataReference.split(Pattern.quote("|"))[1]);
					break;
				case linktext:
					locator = By.linkText(dataReference.split(Pattern.quote("|"))[1]);
					break;
				case partiallinktext:
					locator = By.partialLinkText(dataReference.split(Pattern.quote("|"))[1]);
					break;
				case classname:
					locator = By.className(dataReference.split(Pattern.quote("|"))[1]);
					break;
				case tagname:
					locator = By.tagName(dataReference.split(Pattern.quote("|"))[1]);
					break;	
				}
			}
			return locator;
		}
		Assert.fail("Locator is Not Available");
		return null;
	}

	@Override
	public String getCurrentDate(String dateFormat) {
		return LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormat)); 
	}
	
	

	@Override
	public String getCurrentTime(String timeFormat) {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateformat = new SimpleDateFormat(timeFormat);
		return dateformat.format(date);
	}

	@Override
	public void navigateBack() {
		driver.navigate().back();
	}

	@Override
	public void navigateForward() {
		driver.navigate().forward();
	}

	@Override
	public void flushproperty(Properties property) {
		property = null;
		
	}

	@Override
	public void alert_accept() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		alert.accept();
		
	}

	@Override
	public void alert_dismiss() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		alert.dismiss();
		
	}

	@Override
	public String alert_readstring() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		return alert.getText();
	}

	@Override
	public String decryptText(String encryptedText) {
		byte[] passworddecoded=Base64.decodeBase64(new String(encryptedText));
		String password =  new String(passworddecoded);
		return password;
	}

	@Override
	public void SaveFile() {
		 Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	        robot.setAutoDelay(1000);
	        robot.keyPress(KeyEvent.VK_ALT);
	        robot.keyPress(KeyEvent.VK_S);
	        robot.keyRelease(KeyEvent.VK_S);
	        robot.keyRelease(KeyEvent.VK_ALT);
	        robot.setAutoDelay(2000);
		
	}

	@Override
	public void scrollDown() {
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,1000)");
		
	}

	@Override
	public void scrollUp() {
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,-250)", "");
		
	}

	@Override
	public String osdetails() {
		String realarch = System.getenv("PROCESSOR_ARCHITECTURE").endsWith("64") || System.getenv("PROCESSOR_ARCHITEW6432") != null && System.getenv("PROCESSOR_ARCHITEW6432").endsWith("64") ? "64" : "32";
		realarch = System.getProperty("os.name") + " "+realarch+"-Bit";
		return realarch;
	}

	@Override
	public void hardSleep(int seconds) {
		try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void isFileExists(String downloadPath, String fileName) {
		File file = new File(downloadPath + File.separator + fileName);
		if (file.exists() && !file.isDirectory()) {
			logger.info(fileName + " is exists in the local system");
		} else {
			logger.info(fileName + " is not exists in the local system");
			Assert.fail();
		}
	}

	@Override
	public boolean selectRadioButton(String objectReference, String value) {
		boolean elementenabled = false;
		try {
			List<WebElement> options = driver.findElement(getLocator(objectReference)).findElements(By.xpath("./md-radio-button"));
			for (WebElement ele : options) {
				if(ele.getAttribute("value").equalsIgnoreCase(value)) {
					
					if(!ele.isEnabled()) {
						elementenabled = true;
						if(!(ele.isSelected())) {
							ele.click();
							break;
						}
					}
					else if (ele.isSelected())
						logger.info("By default "+ele.getAttribute("value") + " is selected");
				}
			}
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return elementenabled;
	}

	@Override
	public boolean verify_given_File_is_available(String fileName) {
		boolean exists = false;
		File file = new File(System.getProperty("user.home") +"\\Downloads\\"+fileName);
		if(file.exists() && !file.isDirectory()) { 
		  logger.info(fileName + " is downloded in the local system");
		  exists = true;
		}
		else {
			logger.info(fileName + " is not downloded in the local system");
			Assert.fail(fileName + " Not exists");
		}
		return exists;
	}

	@Override
	public void string_to_XMLFile(String filepath, String xml_content) {
				try {
					//Parse String value to document
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document doc = builder.parse(new InputSource(new StringReader(xml_content)));

					// Write the parsed document to an xml file
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result =  new StreamResult(new File(filepath));
					transformer.transform(source, result);
				} catch (ParserConfigurationException | SAXException | IOException
						| TransformerFactoryConfigurationError | TransformerException e) {
					logger.error(e);
					Assert.fail("Not able to create the file with in "+filepath+" this location");
				}
			}

	@Override
	public String xmlfile_to_String(String filepath) {
		  StringWriter stringWriter = null;
		
			try {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance(); 
				  InputSource inputsrc = new InputSource(filepath); 
				  org.w3c.dom.Document document = docBuilderFactory.newDocumentBuilder().parse(inputsrc); 
				  stringWriter = new StringWriter(); 
				  Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
				  serializer.transform(new DOMSource(document), new StreamResult(stringWriter));
				  logger.info("XML file is successfully read from the filepath" + filepath);
			} catch (SAXException | IOException | ParserConfigurationException | TransformerFactoryConfigurationError
					| TransformerException e) {
				logger.info(e);
				e.printStackTrace();
			}
		
		  return stringWriter.toString();
		
	}

	@Override
	public void quitBrowser() {
		driver.quit();		
	}

	@Override
	public void closeBrowser() {
		driver.close();
		
	}

	public String randomvalue(String valueType, int count) {
		final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		final String NUMERIC_STRING = "0123456789";
		final String CHARACTER_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder builder = new StringBuilder();
		int character = 0;
		while (count-- != 0) {
			switch(valueType.toLowerCase().replaceAll(" ", "")){
			case "numeric":
				character = (int)(Math.random()*NUMERIC_STRING.length());
				builder.append(NUMERIC_STRING.charAt(character));
				break;
			case "character":
				character = (int)(Math.random()*CHARACTER_STRING.length());
				builder.append(CHARACTER_STRING.charAt(character));
				break;
			case "alphanumeric":
				character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
				builder.append(ALPHA_NUMERIC_STRING.charAt(character));
				break;	
			}
		}
		return builder.toString();
		}
	
	@SuppressWarnings("deprecation")
	@Override
	public void switchtoWindow(String pageTitle){
		try {
			this.hardSleep(2);
			Set<String> windows = driver.getWindowHandles();
			//check with Komali
			//mainWindow = driver.getWindowHandle();
			//if(windows.size()>1){
			for (String window : windows) {
				driver.switchTo().window(window);
				if(driver.getTitle().equalsIgnoreCase(pageTitle)){
					driver.manage().window().maximize();
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					break;
					}
			}
		}catch(Exception e)
		{
			System.out.println("Retrying switchtoWindow.................");
			switchtoWindow(pageTitle);
		} 
	//}
	}
	
	//Old method
//	@Override
//	public void switchtoWindow(String pageTitle){
//		this.hardSleep(2);
//		Set<String> windows = driver.getWindowHandles();
//		//if(windows.size()>1){
//		for (String window : windows) {
////			System.out.println(driver.getTitle());
//			driver.switchTo().window(window);
//			if(driver.getTitle().equalsIgnoreCase(pageTitle)){
//				driver.manage().window().maximize();
//				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//				break;
//			}
//		}
//
//		}
//	

	@Override
	public void switchBackToMainWindow(String windowHandle){
		driver.switchTo().window(windowHandle);
		System.out.println("Main title: "+windowHandle );
	}
	
	@Override
	public String mainWindowHandle(){
		return driver.getWindowHandle();
	}
	
	@Override
	public void  highlight_Element (String objectReference){
		loadObjects(objectReference.split("_")[0].toLowerCase());
		WebElement element = driver.findElement(getLocator(objectReference));
		((JavascriptExecutor)driver).executeScript("arguments[0].style.border='3px solid red'", element);
		this.captureEvidence();
		((JavascriptExecutor)driver).executeScript("arguments[0].style.border='none'", element);
	}


	
	@Override
	public HashMap<String,String> stringtoHashMap(String value){
		String[] cellSplit = null;
		HashMap<String,String> map = new HashMap<>();
		value = StringUtils.substringBetween(value, "{", "}");
		if (value!=null) {
			String[] keyValuePairs = value.split(",");
			for (String pair : keyValuePairs) {
				String[] entry = pair.split("=");
				if (entry.length == 1)
					map.put(entry[0].trim(), "");
				else {
					if (entry[1].trim().contains("|")) {
						cellSplit = entry[1].split("\\|");
						entry[1] = cellSplit[BasicDefinition.scenarioOutlineindex].toString().trim();
					}
					map.put(entry[0].trim(), entry[1].trim());
				}

			} 
		}
		return map;
	}
	

	@Override
	public String enterValues_JS(String objectReference, String value) {
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", 
				this.getElement(objectReference), "value", value);
		return null;
	}

	@Override
	public void addAttribute(String attributeName, String attributeValue, String objectReference) {
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('"+attributeName+"','"+attributeValue+"')",this.getElement(objectReference));		
	}

	@Override
	public void RemoveAttribute(String attributeName, String attributeValue, String objectReference) {
		((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('"+attributeName+"','"+attributeValue+"')",this.getElement(objectReference));		
	}

	@Override
    public void doubleclick(String objectReference) {
           Actions act = new Actions(driver);
           By locator = getLocator(objectReference);
           WebElement elementLoc = driver.findElement(locator);
           act.doubleClick(elementLoc).perform();
    }
	@Override
	public void hoveranddoubleclick(String objectReference){
		WebElement ele = driver.findElement(getLocator(objectReference));
    	Actions action = new Actions(driver);
		action.moveToElement(ele).doubleClick(ele).build().perform();
		System.out.println("clicked"); 
	}
	public String changeDateFormat(String inputdate, String dateformat) throws java.text.ParseException {
		DateFormat oldformat = new SimpleDateFormat("dd/mm/yyyy");
		Date date = oldformat.parse(inputdate);
		DateFormat newformat = new SimpleDateFormat(dateformat);
		return newformat.format(date);	
	}

	@Override
	public void captureEvidence() {
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		BasicDefinition.getScenario().attach(((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES), "image/png", randomvalue("alphanumeric", 10));
	}

	@Override
	public void captureEvidence(String comment) {
		BasicDefinition.getScenario().log(comment);
	} 
	
 @Override
	public String getElementTextBy(By locator) {
		String text = driver.findElement(locator).getText();
		return text;
	}
 
 @Override
	public void wait_For_ElementVisible(String objectReference) {
		loadObjects(objectReference.split("_")[0].toLowerCase());
		By locator = getLocator(objectReference);
		try {

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
			wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			
		} catch (NoSuchElementException e) {
			logger.error(e);
			logger.info(locator + " is not Visible in Page");
		}
		
	}
 
 /**
	 * Return the Given String with 2 decimal points and plain text
	 * 
	 * @param amount - Amount need to be change in the ###.00 formate
	 * @return
	 */
@Override
public String convertWith2Deciamls(String amount) {
		amount = amount.contains(",")?amount.replaceAll(",", ""):amount;
		BigDecimal cdt_Amt = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
		double amt = Double.parseDouble(cdt_Amt.toString());
		DecimalFormat formatter = new DecimalFormat("###.00");
		String convertedAmt = formatter.format(amt);
		return convertedAmt;
	}

@Override
public WebElement getElement(String objectReference) {
	// TODO Auto-generated method stub
	return null;
}
	

	}
	

