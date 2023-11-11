package report;

import static io.cucumber.core.exception.ExceptionUtils.printStackTrace;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toList;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import configurations.ConfigurationManager;
import io.cucumber.messages.Messages.GherkinDocument.Feature;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Background;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Step;
import io.cucumber.messages.internal.com.google.gson.Gson;
import io.cucumber.messages.internal.com.google.gson.GsonBuilder;
import io.cucumber.messages.internal.com.google.gson.JsonIOException;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.Argument;
import io.cucumber.plugin.event.DataTableArgument;
import io.cucumber.plugin.event.DocStringArgument;
import io.cucumber.plugin.event.EmbedEvent;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.HookTestStep;
import io.cucumber.plugin.event.HookType;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.StepArgument;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestSourceRead;
import io.cucumber.plugin.event.TestStep;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;
import io.cucumber.plugin.event.WriteEvent;
import stepDefinitions.BasicDefinition;

public class JIRAReport implements EventListener {
	private final String before = "before";
	private final String after = "after";
	private final List<Map<String, Object>> featureMaps = new ArrayList<>();
	private final Map<String, Object> currentBeforeStepHookList = new HashMap<>();
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@SuppressWarnings("unused")
	private Writer writer;
	private final TestSourcesModel testSources = new TestSourcesModel();
	private URI currentFeatureFile;
	private List<Map<String, Object>> currentElementsList;
	private Map<String, Object> currentElementMap;
	private Map<String, Object> currentTestCaseMap;
	private List<Map<String, Object>> currentStepsList;
	private Map<String, Object> currentStepOrHookMap;
	private Map<String, Object> currentscenarioFeatureMap;
	public static String scenarioruntime = "", featureName = "";
	public static String scneariotype = "";
	private static final String ZIP_FILE = "fileName.zip";
	private static final String FOLDER_TO_ZIP = "target filepath";

	public static Logger logger = LogManager.getLogger(JIRAReport.class);
	public JIRAReport(OutputStream out) {
		this.writer = new UTF8OutputStreamWriter(out);

	}

	public void setEventPublisher(EventPublisher publisher) {
		publisher.registerHandlerFor(TestSourceRead.class, this::handleTestSourceRead);
		publisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
		publisher.registerHandlerFor(TestStepStarted.class, this::handleTestStepStarted);
		publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
		publisher.registerHandlerFor(WriteEvent.class, this::handleWrite);
		publisher.registerHandlerFor(EmbedEvent.class, this::handleEmbed);
		publisher.registerHandlerFor(TestCaseFinished.class, this::generateJIRAReport);
		publisher.registerHandlerFor(TestRunFinished.class, event -> {
			try {
				zipReportFile(FOLDER_TO_ZIP, ZIP_FILE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void handleTestSourceRead(TestSourceRead event) {
		testSources.addTestSourceReadEvent(event.getUri(), event);
	}

	@SuppressWarnings("unchecked")
	private void handleTestCaseStarted(TestCaseStarted event) {
		if (currentFeatureFile == null || !currentFeatureFile.equals(event.getTestCase().getUri())) {
			currentFeatureFile = event.getTestCase().getUri();
			Map<String, Object> currentFeatureMap = createFeatureMap(event.getTestCase());
			currentscenarioFeatureMap = currentFeatureMap;
			featureMaps.add(currentFeatureMap);
			currentElementsList = (List<Map<String, Object>>) currentFeatureMap.get("elements");
		}
		currentTestCaseMap = createTestCase(event);
		if (testSources.hasBackground(currentFeatureFile, event.getTestCase().getLocation().getLine())) {
			currentElementMap = createBackground(event.getTestCase());
			currentElementsList.add(currentElementMap);
		} else {
			currentElementMap = currentTestCaseMap;
		}
		currentElementsList.add(currentTestCaseMap);
		currentStepsList = (List<Map<String, Object>>) currentElementMap.get("steps");
	}

	@SuppressWarnings("unchecked")
	private void handleTestStepStarted(TestStepStarted event) {
		if (event.getTestStep() instanceof PickleStepTestStep) {
			PickleStepTestStep testStep = (PickleStepTestStep) event.getTestStep();
			if (isFirstStepAfterBackground(testStep)) {
				currentElementMap = currentTestCaseMap;
				currentStepsList = (List<Map<String, Object>>) currentElementMap.get("steps");
			}
			currentStepOrHookMap = createTestStep(testStep);
			// add beforeSteps list to current step
			if (currentBeforeStepHookList.containsKey(before)) {
				currentStepOrHookMap.put(before, currentBeforeStepHookList.get(before));
				currentBeforeStepHookList.clear();
			}
			currentStepsList.add(currentStepOrHookMap);
		} else if (event.getTestStep() instanceof HookTestStep) {
			HookTestStep hookTestStep = (HookTestStep) event.getTestStep();
			currentStepOrHookMap = createHookStep(hookTestStep);
			addHookStepToTestCaseMap(currentStepOrHookMap, hookTestStep.getHookType());
		} else {
			throw new IllegalStateException();
		}
	}

	private void handleTestStepFinished(TestStepFinished event) {
		currentStepOrHookMap.put("match", createMatchMap(event.getTestStep(), event.getResult()));
		currentStepOrHookMap.put("result", createResultMap(event.getResult()));
	}

	private void handleWrite(WriteEvent event) {
		addOutputToHookMap(event.getText());
	}

	private void handleEmbed(EmbedEvent event) {
		addEmbeddingToHookMap(event.getData(), event.getMediaType(), event.getName());
	}

	private void generateJIRAReport(TestCaseFinished event) {
		PrintWriter file = null;
		Properties prop = new Properties();
		String json_FileName = (String) currentElementMap.get("name");
		try {
			prop.load(new BufferedReader(new FileReader(
					"API_Configuration.properties")));
			String fileName = "tcnamae" + ".json";
			file = new PrintWriter(fileName);
			gson.toJson(currentscenarioFeatureMap, file);
			file.close();
			currentElementMap.clear();

			Thread.sleep(2000);
			String text = new String(Files.readAllBytes(Paths.get(new File(fileName).toURI())), StandardCharsets.UTF_8);
			text = "[" + text.replaceAll("TEST_EXECUTION_KEY", prop.getProperty("TEST_EXECUTION_KEY"))
					.replaceAll("\\{\\},", "") + "]";

			FileWriter files = new FileWriter(fileName);
			files.write(text);
			files.flush();
			files.close();
			Thread.sleep(2000);
			this.wordReport(fileName);
			if (ConfigurationManager.getInstance().getConfigReader().getJIRAUPDATE_Required()) {
				this.importCucumberTestResults(fileName);
				if (BasicDefinition.ReporttableContent.get(BasicDefinition.TC_id) != null)
					this.updateTestRunComments(BasicDefinition.ReporttableContent.get(BasicDefinition.TC_id),
							getxrayTestID() /* "1175628" */);
				this.updateTestEvidences(getxrayTestID(),
						 BasicDefinition.TC_id + ".log");
				this.updateTestEvidences(getxrayTestID(), BasicDefinition.TC_id + ".docx");
			}
		} catch (JsonIOException | IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void updateTestEvidences(String xrayTestID, String filepath) throws IOException {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost postRequest = new HttpPost(ConfigurationManager.getInstance().getConfigReader()
					.get_xRAY_testEvidence().replace("{id}", xrayTestID));
			postRequest.setHeader("Authorization",
					"Basic " + ConfigurationManager.getInstance().getConfigReader().get_xRAY_JIRA_CREDENTIALS());
			postRequest.setHeader("Content-type", "application/json");
			postRequest.setHeader("X-Atlassian-Token", "nocheck");
			postRequest.setEntity(new StringEntity(getTestEvidenceRequest(filepath)));
			try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(postRequest)) {
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == 200) {
					logger.info("Test Evidence Updated Successfully");
				} else {
					logger.info(statusLine.getStatusCode());
					logger.info("Test Evidence not updated in JIRA");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Test Evidence not updated in JIRA");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Test Evidence not updated in JIRA");
		}

	}

	private String getTestEvidenceRequest(String filepath) throws IOException {
		File file = new File(filepath);
		String fileExtension = file.getName().substring(file.getName().indexOf(".") + 1).toLowerCase(), request = "",
				contentType = "";
		byte[] fileContent = Files.readAllBytes(file.toPath());
		String encodedMime = Base64.getEncoder().encodeToString(fileContent);
		switch (fileExtension) {
		case "txt":
		case "log":
			contentType = "text/plain";
			break;
		case "jpg":
		case "png":
		case "jpeg":
			if (fileExtension.equalsIgnoreCase("jpg"))
				fileExtension = "jpeg";
			contentType = "image/" + fileExtension;
			break;
		case "docx":
			contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			break;
		default:
			logger.info("Invalid File format you trying to attach.");
		}
		request = "{\r\n" + "   \"data\":\"" + encodedMime + "\",\r\n" + "   \"filename\":\"" + file.getName()
				+ "\",\r\n" + "   \"contentType\":\"" + contentType + "\"\r\n" + "}";
		return request;
	}

	private String getxrayTestID() {
		String responseBody = "";
		StatusLine statusLine = null;
		JSONObject object = null;
		try {
			HttpClient client = HttpClientBuilder.create().build();
			String endpoint = ConfigurationManager.getInstance().getConfigReader().get_xRAY_testCommentsUpdate();
			HttpGet getRequest = new HttpGet(endpoint.substring(0, endpoint.indexOf("{") - 1) + "?testExecIssueKey="
					+ ConfigurationManager.getInstance().getConfigReader().get_xRAY_TestexecutionKey()
					+ "&testIssueKey=" + BasicDefinition.xray_TestKey.replace("@", ""));
			getRequest.setHeader("Authorization",
					"Basic " + ConfigurationManager.getInstance().getConfigReader().get_xRAY_JIRA_CREDENTIALS());
			getRequest.setHeader("Content-type", "application/json");
			getRequest.setHeader("X-Atlassian-Token", "nocheck");
			try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(getRequest)) {
				statusLine = response.getStatusLine();
				responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
				object = (JSONObject) JSONValue.parse(responseBody);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Not able to extract Test ID");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Not able to extract Test ID");
		}

		if (statusLine.getStatusCode() == 200) {
			logger.info("Id for the Test Key is " + object.get("id").toString());
			return object.get("id").toString();
		} else {
			logger.info(statusLine.getStatusCode());
			logger.info("Something error while generating the Test Key");
			return null;
		}
	}

	private Map<String, Object> createFeatureMap(TestCase testCase) {
		Map<String, Object> featureMap = new HashMap<>();
		featureMap.put("uri", TestSourcesModel.relativize(testCase.getUri()));
		featureMap.put("elements", new ArrayList<Map<String, Object>>());
		Feature feature = testSources.getFeature(testCase.getUri());
		if (feature != null) {
			featureMap.put("keyword", feature.getKeyword());
			featureMap.put("name", feature.getName());
			featureMap.put("description", feature.getDescription() != null ? feature.getDescription() : "");
			featureMap.put("line", feature.getLocation().getLine());
			featureMap.put("id", TestSourcesModel.convertToId(feature.getName()));
			featureMap.put("tags", feature.getTagsList().stream().map(tag -> {
				Map<String, Object> json = new LinkedHashMap<>();
				json.put("name", tag.getName());
				json.put("type", "Tag");
				Map<String, Object> location = new LinkedHashMap<>();
				location.put("line", tag.getLocation().getLine());
				location.put("column", tag.getLocation().getColumn());
				json.put("location", location);
				return json;
			}).collect(toList()));

		}
		return featureMap;
	}

	@SuppressWarnings("deprecation")
	private Map<String, Object> createTestCase(TestCaseStarted event) {
		Map<String, Object> testCaseMap = new HashMap<>();

		testCaseMap.put("start_timestamp", getDateTimeFromTimeStamp(event.getInstant()));

		TestCase testCase = event.getTestCase();

		testCaseMap.put("name", testCase.getName());
		testCaseMap.put("line", testCase.getLine());
		testCaseMap.put("type", "scenario");
		TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile, testCase.getLine());
		if (astNode != null) {
			testCaseMap.put("id", TestSourcesModel.calculateId(astNode));
			Scenario scenarioDefinition = TestSourcesModel.getScenarioDefinition(astNode);
			testCaseMap.put("keyword", scenarioDefinition.getKeyword());
			testCaseMap.put("description",
					scenarioDefinition.getDescription() != null ? scenarioDefinition.getDescription() : "");
		}
		testCaseMap.put("steps", new ArrayList<Map<String, Object>>());
		if (!testCase.getTags().isEmpty()) {
			List<Map<String, Object>> tagList = new ArrayList<>();
			for (String tag : testCase.getTags()) {
				Map<String, Object> tagMap = new HashMap<>();
				tagMap.put("name", tag);
				tagList.add(tagMap);
			}
			testCaseMap.put("tags", tagList);
		}
		return testCaseMap;
	}

	private Map<String, Object> createBackground(TestCase testCase) {
		TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile, testCase.getLocation().getLine());
		if (astNode != null) {
			Background background = TestSourcesModel.getBackgroundForTestCase(astNode);
			Map<String, Object> testCaseMap = new HashMap<>();
			testCaseMap.put("name", background.getName());
			testCaseMap.put("line", background.getLocation().getLine());
			testCaseMap.put("type", "background");
			testCaseMap.put("keyword", background.getKeyword());
			testCaseMap.put("description", background.getDescription() != null ? background.getDescription() : "");
			testCaseMap.put("steps", new ArrayList<Map<String, Object>>());
			return testCaseMap;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private boolean isFirstStepAfterBackground(PickleStepTestStep testStep) {
		TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile, testStep.getStepLine());
		if (astNode == null) {
			return false;
		}
		return currentElementMap != currentTestCaseMap && !TestSourcesModel.isBackgroundStep(astNode);
	}

	@SuppressWarnings("deprecation")
	private Map<String, Object> createTestStep(PickleStepTestStep testStep) {
		Map<String, Object> stepMap = new HashMap<>();
		stepMap.put("name", testStep.getStepText());
		stepMap.put("line", testStep.getStepLine());
		TestSourcesModel.AstNode astNode = testSources.getAstNode(currentFeatureFile, testStep.getStepLine());
		StepArgument argument = testStep.getStepArgument();
		if (argument != null) {
			if (argument instanceof DocStringArgument) {
				DocStringArgument docStringArgument = (DocStringArgument) argument;
				stepMap.put("doc_string", createDocStringMap(docStringArgument));
			} else if (argument instanceof DataTableArgument) {
				DataTableArgument dataTableArgument = (DataTableArgument) argument;
				stepMap.put("rows", createDataTableList(dataTableArgument));
			}
		}
		if (astNode != null) {
			Step step = (Step) astNode.node;
			stepMap.put("keyword", step.getKeyword());
		}
		return stepMap;
	}

	private Map<String, Object> createHookStep(HookTestStep hookTestStep) {
		return new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	private void addHookStepToTestCaseMap(Map<String, Object> currentStepOrHookMap, HookType hookType) {
		String hookName;
		if (hookType == HookType.AFTER || hookType == HookType.AFTER_STEP)
			hookName = after;
		else
			hookName = before;

		Map<String, Object> mapToAddTo;
		switch (hookType) {
		case BEFORE:
			mapToAddTo = currentTestCaseMap;
			break;
		case AFTER:
			mapToAddTo = currentTestCaseMap;
			break;
		case BEFORE_STEP:
			mapToAddTo = currentBeforeStepHookList;
			break;
		case AFTER_STEP:
			mapToAddTo = currentStepsList.get(currentStepsList.size() - 1);
			break;
		default:
			mapToAddTo = currentTestCaseMap;
		}

		if (!mapToAddTo.containsKey(hookName)) {
			mapToAddTo.put(hookName, new ArrayList<Map<String, Object>>());
		}
		((List<Map<String, Object>>) mapToAddTo.get(hookName)).add(currentStepOrHookMap);
	}

	private Map<String, Object> createMatchMap(TestStep step, Result result) {
		Map<String, Object> matchMap = new HashMap<>();
		if (step instanceof PickleStepTestStep) {
			PickleStepTestStep testStep = (PickleStepTestStep) step;
			if (!testStep.getDefinitionArgument().isEmpty()) {
				List<Map<String, Object>> argumentList = new ArrayList<>();
				for (Argument argument : testStep.getDefinitionArgument()) {
					Map<String, Object> argumentMap = new HashMap<>();
					if (argument.getValue() != null) {
						argumentMap.put("val", argument.getValue());
						argumentMap.put("offset", argument.getStart());
					}
					argumentList.add(argumentMap);
				}
				matchMap.put("arguments", argumentList);
			}
		}
		if (!result.getStatus().is(Status.UNDEFINED)) {
			matchMap.put("location", step.getCodeLocation());
		}
		return matchMap;
	}

	private Map<String, Object> createResultMap(Result result) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("status", result.getStatus().name().toLowerCase(ROOT));
		if (result.getError() != null) {
			resultMap.put("error_message", printStackTrace(result.getError()));
		}
		if (!result.getDuration().isZero()) {
			resultMap.put("duration", result.getDuration().toNanos());
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	private void addOutputToHookMap(String text) {
		if (!currentStepOrHookMap.containsKey("output")) {
			currentStepOrHookMap.put("output", new ArrayList<String>());
		}
		((List<String>) currentStepOrHookMap.get("output")).add(text);

	}

	@SuppressWarnings("unchecked")
	private void addEmbeddingToHookMap(byte[] data, String mediaType, String name) {
		if (!currentStepOrHookMap.containsKey("embeddings")) {
			currentStepOrHookMap.put("embeddings", new ArrayList<Map<String, Object>>());
		}
		Map<String, Object> embedMap = createEmbeddingMap(data, mediaType, name);
		((List<Map<String, Object>>) currentStepOrHookMap.get("embeddings")).add(embedMap);

	}

	private String getDateTimeFromTimeStamp(Instant instant) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
				.withZone(ZoneOffset.UTC);
		return formatter.format(instant);
	}

	private Map<String, Object> createDocStringMap(DocStringArgument docString) {
		Map<String, Object> docStringMap = new HashMap<>();
		docStringMap.put("value", docString.getContent());
		docStringMap.put("line", docString.getLine());
		docStringMap.put("content_type", docString.getMediaType());
		return docStringMap;
	}

	private List<Map<String, List<String>>> createDataTableList(DataTableArgument argument) {
		List<Map<String, List<String>>> rowList = new ArrayList<>();
		for (List<String> row : argument.cells()) {
			Map<String, List<String>> rowMap = new HashMap<>();
			rowMap.put("cells", new ArrayList<>(row));
			rowList.add(rowMap);
		}
		return rowList;
	}

	private Map<String, Object> createEmbeddingMap(byte[] data, String mediaType, String name) {
		Map<String, Object> embedMap = new HashMap<>();
		embedMap.put("mime_type", mediaType); // Should be media-type but not
												// worth migrating for
		embedMap.put("data", Base64.getEncoder().encodeToString(data));
		if (name != null) {
			embedMap.put("name", name);
		}
		return embedMap;
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, HashMap<String, Object>> readJSONReport(String fileName) {
		LinkedHashMap<String, HashMap<String, Object>> jsonReports = new LinkedHashMap<String, HashMap<String, Object>>();
		HashMap<String, Object> stepResults = new HashMap<String, Object>();
		String duration = "", status = "", stepName = "";
		ArrayList<BufferedImage> screenCapture = new ArrayList<BufferedImage>();
		ArrayList<String> outputText;
		try {
			JSONParser parser = new JSONParser();
			JSONArray resultarray = (JSONArray) parser.parse(new FileReader(fileName));

			for (Object result : resultarray) {
				JSONObject resultValues = (JSONObject) result;
				featureName = resultValues.get("name").toString();
				JSONArray elements = (JSONArray) resultValues.get("elements");
				for (Object element : elements) {
					JSONObject elementValues = (JSONObject) element;
					scneariotype = elementValues.get("keyword") + " " + elementValues.get("name");
					JSONArray stepelements = (JSONArray) elementValues.get("steps");
					for (Object stepelement : stepelements) {
						JSONObject stepValues = (JSONObject) stepelement;
						stepName = stepValues.get("keyword") + "" + stepValues.get("name");
						outputText = new ArrayList<String>();
						if (stepValues.get("output") != null) {
							JSONArray messages = (JSONArray) stepValues.get("output");
							for (Object message : messages) {
								outputText.add(message.toString());
							}
						}
						JSONArray screenshots = (JSONArray) stepValues.get("embeddings");
						if (screenshots != null) {
							screenCapture = new ArrayList<BufferedImage>();
							for (Object screenshot : screenshots) {
								JSONObject encryptedbytes = (JSONObject) screenshot;
								byte[] decodedBytes = Base64.getMimeDecoder()
										.decode(encryptedbytes.get("data").toString());

								InputStream is = new ByteArrayInputStream(decodedBytes);
								BufferedImage newBi = ImageIO.read(is);
								screenCapture.add(newBi);
							}
						}
						HashMap<String, Object> map = (HashMap<String, Object>) stepValues.get("result");
						status = map.get("status").toString();
						if (map.get("duration") != null)
							duration = TimeUnit.NANOSECONDS.toMinutes((long) map.get("duration")) + "m "
									+ TimeUnit.NANOSECONDS.toSeconds((long) map.get("duration")) + "s";
						else
							duration = "0m 0s";

						stepResults.put("duration", duration);
						stepResults.put("screenshot", screenCapture);
						stepResults.put("outputtext", outputText);
						stepResults.put("status", status);
						jsonReports.put(stepName, stepResults);
						stepResults = new HashMap<String, Object>();
					}

				}
			}

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return jsonReports;
	}

	@SuppressWarnings("unchecked")
	private void wordReport(String fileName) {
		String featureTags = "", scenarioTags = "";

		LinkedHashMap<String, HashMap<String, Object>> jsonReports = readJSONReport(fileName);

		try {
			XWPFDocument doc = new XWPFDocument();

			// the body content
			XWPFParagraph paragraph = doc.createParagraph();
			XWPFRun run = paragraph.createRun();
			run = paragraph.createRun();
			run.setBold(true);
			run.setColor("0071C1");
			run.setUnderline(UnderlinePatterns.THICK);
			run.setText("Environment Details:");

			// Environment Details
			// Create Table
			XWPFTable tableContent = doc.createTable();

			CTTbl tab = tableContent.getCTTbl();
			CTTblPr pr = tab.getTblPr();
			CTTblWidth tbW = pr.getTblW();
			tbW.setW(BigInteger.valueOf(5000));
			tbW.setType(STTblWidth.PCT);
			pr.setTblW(tbW);
			tab.setTblPr(pr);

			CTJc jc = pr.addNewJc();
			jc.setVal(STJc.CENTER);
			pr.setJc(jc);

			// Table Header
			XWPFTableRow executedBy = tableContent.getRow(0);

			headerdata(executedBy, 0, "Executed By");
			executedBy.addNewTableCell().setColor("0071C1");
			headerdata(executedBy, 1, "Executed On");
			executedBy.addNewTableCell().setColor("0071C1");
			headerdata(executedBy, 2, "Executed In");
			executedBy.addNewTableCell().setColor("0071C1");
			headerdata(executedBy, 3, "Execution Time");
			executedBy.addNewTableCell().setColor("0071C1");
			headerdata(executedBy, 4, "Browser Name");
			executedBy.addNewTableCell().setColor("0071C1");
			headerdata(executedBy, 5, "OS Name");
			executedBy.addNewTableCell().setColor("0071C1");
			headerdata(executedBy, 6, "OS Version");

			// Table Body
			XWPFTableRow executedOn = tableContent.createRow();
			contentdata(executedOn, 0, System.getProperty("user.name"));

			executedOn.getCell(0).removeParagraph(0);
			XWPFParagraph username = executedOn.getCell(0).addParagraph();
			username.setAlignment(ParagraphAlignment.CENTER);
			username.setSpacingBefore(10);
			username.setSpacingAfter(10);
			executedOn.getCell(0).setText(System.getProperty("user.name"));

			executedOn.getCell(1).removeParagraph(0);
			XWPFParagraph reportdate = executedOn.getCell(1).addParagraph();
			reportdate.setAlignment(ParagraphAlignment.CENTER);
			reportdate.setSpacingBefore(10);
			reportdate.setSpacingAfter(10);
			executedOn.getCell(1).setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

			executedOn.getCell(2).removeParagraph(0);
			XWPFParagraph env = executedOn.getCell(2).addParagraph();
			env.setAlignment(ParagraphAlignment.CENTER);
			env.setSpacingBefore(10);
			env.setSpacingAfter(10);
			executedOn.getCell(2).setText(ConfigurationManager.getInstance().getConfigReader().getEnvironment());

			executedOn.getCell(3).removeParagraph(0);
			XWPFParagraph osd = executedOn.getCell(3).addParagraph();
			osd.setAlignment(ParagraphAlignment.CENTER);
			osd.setSpacingBefore(10);
			osd.setSpacingAfter(10);
			executedOn.getCell(3).setText(scenarioruntime);

			executedOn.getCell(4).removeParagraph(0);
			XWPFParagraph browsernam = executedOn.getCell(4).addParagraph();
			browsernam.setAlignment(ParagraphAlignment.CENTER);
			browsernam.setSpacingBefore(10);
			browsernam.setSpacingAfter(10);
			executedOn.getCell(4).setText(ConfigurationManager.getInstance().getConfigReader().getBrowser());

			executedOn.getCell(5).removeParagraph(0);
			XWPFParagraph osnam = executedOn.getCell(5).addParagraph();
			osnam.setAlignment(ParagraphAlignment.CENTER);
			osnam.setSpacingBefore(10);
			osnam.setSpacingAfter(10);
			executedOn.getCell(5).setText(System.getProperty("os.name"));

			executedOn.getCell(6).removeParagraph(0);
			XWPFParagraph time = executedOn.getCell(6).addParagraph();
			time.setAlignment(ParagraphAlignment.CENTER);
			time.setSpacingBefore(10);
			time.setSpacingAfter(10);
			executedOn.getCell(6).setText(System.getProperty("os.version"));

			// Step Execution Time

			XWPFParagraph perfmon = doc.createParagraph();
			XWPFRun perfrun = perfmon.createRun();
			perfrun.addBreak();
			perfrun.setBold(true);
			perfrun.setColor("0071C1");
			perfrun.setUnderline(UnderlinePatterns.THICK);
			perfrun.setText("Step Wise Performance Report:");

			XWPFTable perf_tableContent = doc.createTable();
			CTTbl perf_tab = perf_tableContent.getCTTbl();
			CTTblPr perf_pr = perf_tab.getTblPr();
			CTTblWidth perf_tbW = perf_pr.getTblW();
			perf_tbW.setW(BigInteger.valueOf(5000));
			perf_tbW.setType(STTblWidth.PCT);
			perf_pr.setTblW(perf_tbW);
			perf_tab.setTblPr(perf_pr);

			CTJc perf_jc = perf_pr.addNewJc();
			perf_jc.setVal(STJc.CENTER);
			perf_pr.setJc(perf_jc);

			// Table Header
			XWPFTableRow name_of_Step = perf_tableContent.getRow(0);
			name_of_Step.getCell(0).removeParagraph(0);
			name_of_Step.getCell(0).setColor("0071C1");
			XWPFParagraph step_paragraph = name_of_Step.getCell(0).addParagraph();
			;
			step_paragraph.setSpacingBefore(10);
			step_paragraph.setSpacingAfter(10);
			step_paragraph.setAlignment(ParagraphAlignment.CENTER);
			setRun(step_paragraph.createRun(), "Calibri", 10, "FFFFFF", "Step Name", true, false);

			name_of_Step.addNewTableCell().setColor("0071C1");
			name_of_Step.getCell(1).removeParagraph(0);
			XWPFParagraph stepExecutionTime = name_of_Step.getCell(1).addParagraph();
			stepExecutionTime.setAlignment(ParagraphAlignment.CENTER);
			stepExecutionTime.setSpacingBefore(10);
			stepExecutionTime.setSpacingAfter(10);
			setRun(stepExecutionTime.createRun(), "Calibri", 10, "FFFFFF", "Executed Time", true, false);

//		                     // Table Body

			for (Entry<String, HashMap<String, Object>> stepname : jsonReports.entrySet()) {
				String stepexctime = stepname.getValue().get("duration").toString();
				XWPFTableRow step_timeunits = perf_tableContent.createRow();
				step_timeunits.getCell(0).removeParagraph(0);
				XWPFParagraph stepiteration = step_timeunits.getCell(0).addParagraph();
				stepiteration.setAlignment(ParagraphAlignment.LEFT);
				stepiteration.setSpacingBefore(10);
				stepiteration.setSpacingAfter(10);
				step_timeunits.getCell(0).setText(stepname.getKey());

				step_timeunits.getCell(1).removeParagraph(0);
				XWPFParagraph exectime_iteration = step_timeunits.getCell(1).addParagraph();
				exectime_iteration.setAlignment(ParagraphAlignment.RIGHT);
				exectime_iteration.setSpacingBefore(10);
				exectime_iteration.setSpacingAfter(10);
				step_timeunits.getCell(1).setText(stepexctime.replace("+", " "));

			}
			XWPFParagraph pagebreak = doc.createParagraph();
			pagebreak.setPageBreak(true);

			// Feature TagName
//		             for (String featureTag : featureTagName) {
//		                 featureTags = featureTags + featureTag + " ";
//		             }
			XWPFParagraph bdy_paragraph = doc.createParagraph();
			run = bdy_paragraph.createRun();
			run.addBreak();
			run.setFontSize(9);
			run.setText(featureTags);
			run.addBreak();

			// Feature Name
			run = bdy_paragraph.createRun();
			run.setBold(true);
			run.setFontFamily("Calibri");
			run.setFontSize(14);
			run.setText("Feature: " + featureName);
			run.addBreak();

			// Scenario TagName
//		             for (String scenarioTag : scenarioTagName) {
//		                        scenarioTags = scenarioTags + scenarioTag + " ";
//		                   }
			run = bdy_paragraph.createRun();
			run.setFontSize(9);
			run.setText(scenarioTags);
			run.addBreak();

			// Scenario Name
//		             String bgcolor = "";
//		             if(BasicDefinition.scenario_Status.equalsIgnoreCase("passed")) 
//		                         bgcolor = "B5D6A7";
//		                   else if (BasicDefinition.scenario_Status.equalsIgnoreCase("failed"))
//		                          bgcolor = "FF999A";
//		                   else
//		                          bgcolor = "5DBCD2";

			run = bdy_paragraph.createRun();
			run.setBold(true);
			run.setFontFamily("Calibri");
			run.setFontSize(13);
			run.setText(scneariotype.split(" ")[0] + ": ");
			CTShd scenarioNamebgcolor = run.getCTR().addNewRPr().addNewShd();
			scenarioNamebgcolor.setVal(STShd.CLEAR);
			scenarioNamebgcolor.setColor("auto");
//		             scenarioNamebgcolor.setFill(bgcolor);
			run = bdy_paragraph.createRun();
			run.setBold(false);
			run.setText(scneariotype.substring(scneariotype.indexOf(scneariotype.split(" ")[1])));
			CTShd scenariobgcolor = run.getCTR().addNewRPr().addNewShd();
			scenariobgcolor.setVal(STShd.CLEAR);
			scenariobgcolor.setColor("auto");
//		             scenariobgcolor.setFill(bgcolor);
			run = bdy_paragraph.createRun();
			run.addBreak();
			run.addBreak();

			// Step Name
			String backgroundcolor = "";

			for (Entry<String, HashMap<String, Object>> stepname : jsonReports.entrySet()) {
				if (stepname.getValue().get("status").toString().equalsIgnoreCase("passed"))
					backgroundcolor = "B5D6A7";
				else if (stepname.getValue().get("status").toString().equalsIgnoreCase("failed"))
					backgroundcolor = "FF999A";
				else
					backgroundcolor = "5DBCD2";

				run = bdy_paragraph.createRun();
				run.setBold(true);
				run.setFontFamily("Calibri");
				run.setFontSize(13);
				run.setText(stepname.getKey().split(" ")[0]);
				CTShd stepKeywordbgcolor = run.getCTR().addNewRPr().addNewShd();
				stepKeywordbgcolor.setVal(STShd.CLEAR);
				stepKeywordbgcolor.setColor("auto");
				stepKeywordbgcolor.setFill(backgroundcolor);

				run = bdy_paragraph.createRun();
				run.setBold(false);
				run.setText(stepname.getKey().substring(stepname.getKey().indexOf(" "), stepname.getKey().length()));
				CTShd stepNamebgcolor = run.getCTR().addNewRPr().addNewShd();
				stepNamebgcolor.setVal(STShd.CLEAR);
				stepNamebgcolor.setColor("auto");
				stepNamebgcolor.setFill(backgroundcolor);
				run.addBreak();

				if (stepname.getValue().get("outputtext") != null) {

					ArrayList<String> comments = (ArrayList<String>) stepname.getValue().get("outputtext");
					for (String comment : comments) {
						run = bdy_paragraph.createRun();
						run.setBold(true);
						run.setFontFamily("Calibri");
						run.setFontSize(13);
						run.setText("\t Comment: ");

						run = bdy_paragraph.createRun();
						run.setBold(false);
						run.setText(comment);
						run.addBreak();
					}

				}
				if (stepname.getValue().get("screenshot") != null) {
					run = bdy_paragraph.createRun();
					ArrayList<BufferedImage> screenshots = (ArrayList<BufferedImage>) stepname.getValue()
							.get("screenshot");
					for (BufferedImage screenshot : screenshots) {
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						ImageIO.write(screenshot, "png", os);
						InputStream is = new ByteArrayInputStream(os.toByteArray());
						run.addPicture(is, XWPFDocument.PICTURE_TYPE_PICT,
								"Sample_"
										+ new SimpleDateFormat("HH_mm_ss").format(new Date(System.currentTimeMillis())),
								Units.toEMU(500), Units.toEMU(300));
						run.addBreak();
						run.addBreak();
					}

				}
			}

			/*
			 * // CTPermStart marking the start of unprotected range CTPermStart ctPermStart
			 * = doc.getDocument().getBody().addNewPermStart();
			 * ctPermStart.setEdGrp(STEdGrp.EVERYONE); ctPermStart.setId("8205786"); //note
			 * the Id // // CTPerm marking the end of unprotected range
			 * doc.getDocument().getBody().addNewPermEnd().setId("8205786"); //note the same
			 * Id doc.enforceReadonlyProtection(); //enforce readonly protection
			 */
			// create header start
			CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
			XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(doc, sectPr);
			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

			paragraph = header.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.LEFT);

			CTTabStop tabStop = paragraph.getCTP().getPPr().addNewTabs().addNewTab();
			tabStop.setVal(STTabJc.RIGHT);
			int twipsPerInch = 1440;
			tabStop.setPos(BigInteger.valueOf(6 * twipsPerInch));
			// Wipro and Lloyds Logo
			run = paragraph.createRun();
			String wiproLogo = System.getProperty("user.dir") + "/src/test/resources/Images/Partner.jpg";
			// Set EMU as length * Width
			run.addPicture(new FileInputStream(wiproLogo), XWPFDocument.PICTURE_TYPE_JPEG, wiproLogo, Units.toEMU(500),
					Units.toEMU(100));

			// create footer start
			XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
			paragraph = footer.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			// Footer Content
			run = paragraph.createRun();
			run.setText("");

			// Write the content in document
			String path = BasicDefinition.TC_id + ".docx";
			if (scneariotype.split(" ")[0].equalsIgnoreCase("Scenario Outline"))
				path = BasicDefinition.TC_id + "_"
						+ LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MMM_yyyy")) + "_"
						+ new SimpleDateFormat("HH_mm_ss").format(new Date(System.currentTimeMillis())) + ".docx";
			doc.write(new FileOutputStream(path));
			System.out.println("\nWord Report Generated Successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void contentdata(XWPFTableRow tablerow, int cellNumber, String tableContent) {
		tablerow.getCell(cellNumber).removeParagraph(0);
		XWPFParagraph paragraph = tablerow.getCell(cellNumber).addParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		paragraph.setSpacingBefore(10);
		paragraph.setSpacingAfter(10);
		tablerow.getCell(cellNumber).setText(tableContent);
	}

	private static void headerdata(XWPFTableRow tablerow, int cellNumber, String headerContent) {
		tablerow.getCell(cellNumber).removeParagraph(0);
		tablerow.getCell(cellNumber).setColor("0071C1");
		XWPFParagraph paragraph = tablerow.getCell(cellNumber).addParagraph();
		;
		paragraph.setSpacingBefore(10);
		paragraph.setSpacingAfter(10);
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		setRun(paragraph.createRun(), "Calibri", 10, "FFFFFF", headerContent, true, false);
	}

	private static void setRun(XWPFRun run, String fontFamily, int fontsize, String colorRGB, String text, boolean bold,
			boolean addbreak) {
		run.setFontFamily(fontFamily);
		run.setFontSize(fontsize);
		run.setColor(colorRGB);
		run.setText(text);
		run.setBold(bold);
		if (addbreak)
			run.addBreak();
	}

	private void importCucumberTestResults(String file) {
		File testResult = new File(file);
		if(System.getProperty("javax.net.ssl.trustStore")!=null) {
			System.clearProperty("javax.net.ssl.trustStore");
			System.clearProperty("javax.net.ssl.trustStorePassword");
		} 
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost postRequest = new HttpPost(
					ConfigurationManager.getInstance().getConfigReader().get_xRAY_importTestResults());
			postRequest.setHeader("Authorization",
					"Basic " + ConfigurationManager.getInstance().getConfigReader().get_xRAY_JIRA_CREDENTIALS());
			postRequest.setHeader("Content-type", "application/json");
			postRequest.setHeader("X-Atlassian-Token", "nocheck");
			postRequest.setEntity(new FileEntity(testResult));
			try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(postRequest)) {
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == 200) {
					logger.info("Test Report imported for the Test Case : "
							+ testResult.getName().substring(0, testResult.getName().length() - 5));
				} else {
					logger.info(statusLine.getStatusCode());
					logger.info("Test Report NOT imported for the Test Case : "
							+ testResult.getName().substring(0, testResult.getName().length() - 5));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Test Report NOT imported for the Test Case : "
					+ testResult.getName().substring(0, testResult.getName().length() - 5));
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Test Report NOT imported for the Test Case : "
					+ testResult.getName().substring(0, testResult.getName().length() - 5));
		}
	}

	private void updateTestRunComments(String comments, String testKey) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPut putRequest = new HttpPut(ConfigurationManager.getInstance().getConfigReader()
					.get_xRAY_testCommentsUpdate().replace("{id}", testKey));
			putRequest.setHeader("Authorization",
					"Basic " + ConfigurationManager.getInstance().getConfigReader().get_xRAY_JIRA_CREDENTIALS());
			putRequest.setHeader("Content-type", "application/json");
			putRequest.setHeader("X-Atlassian-Token", "nocheck");
			putRequest.setEntity(new StringEntity(comments));
			try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(putRequest)) {
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == 200) {
					logger.info("Comments Updated Successfully");
				} else {
					logger.info(statusLine.getStatusCode());
					logger.info("Comments not updated in JIRA");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Comments not updated in JIRA");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Comments not updated in JIRA");
		}
	}

	private void zipReportFile(final String sourcNoteseDirPath, final String zipFilePath) throws IOException {
		File zip = new File(zipFilePath);
		if (zip.exists())
			zip.delete();
		Path zipFile = Files.createFile(Paths.get(zipFilePath));
		Path sourceDirPath = Paths.get(sourcNoteseDirPath);

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile));
				Stream<Path> paths = Files.walk(sourceDirPath)) {
			paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
				ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
				try {
					zipOutputStream.putNextEntry(zipEntry);
					Files.copy(path, zipOutputStream);
					zipOutputStream.closeEntry();
				} catch (IOException e) {
					System.err.println(e);
				}
			});
		}
		System.out.println("Zip is created at : " + zipFile);
	}

}
