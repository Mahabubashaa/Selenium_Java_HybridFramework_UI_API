package report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import configurations.FilePath;

//import com.lloyds.IPP.IPP_Configurations.FilePath;

import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.GsonBuilder;
import gherkin.deps.net.iharder.Base64;
import gherkin.formatter.Formatter;
import gherkin.formatter.NiceAppendable;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;
//import io.cucumber.messages.internal.com.google.gson.JsonIOException;

public class JSONFormatter implements Reporter, Formatter {

	 private final List<Map<String, Object>> featureMaps = new ArrayList<Map<String, Object>>();
	    private final NiceAppendable out;
	    private Map<String, Object> scenarioJSONMap;
	    private Map<String, Object> featureMap;
	    private String uri;
	    @SuppressWarnings("rawtypes")
		private List<Map> beforeHooks = new ArrayList<Map>();

	    private enum Phase {step, match, embedding, output, result};

	    /**
	     * In order to handle steps being added all at once, this method determines allows methods to
	     * opperator correctly if
	     *
	     * step
	     * match
	     * embedding
	     * output
	     * result
	     * step
	     * match
	     * embedding
	     * output
	     * result
	     *
	     * or if
	     *
	     * step
	     * step
	     * match
	     * embedding
	     * output
	     * result
	     * match
	     * embedding
	     * output
	     * result
	     *
	     * is called
	     *
	     * @return the correct step for the current operation based on past method calls to the formatter interface
	     */
	    @SuppressWarnings("rawtypes")
		private Map getCurrentStep(Phase phase) {
	        String target = phase.ordinal() <= Phase.match.ordinal()?Phase.match.name():Phase.result.name();
	        Map lastWithValue = null;
	        for (Map stepOrHook : getSteps()) {
	            if (stepOrHook.get(target) == null) {
	            	return stepOrHook;
	            } else {
	                lastWithValue = stepOrHook;
	            }
	        }
	        return lastWithValue;
	    }


	    public JSONFormatter(Appendable out) {
	        this.out = new NiceAppendable(out);
	    }

	    @Override
	    public void uri(String uri) {
	        this.uri = uri;
	    }

	    @Override
	    public void feature(Feature feature) {
	        featureMap = feature.toMap();
	        scenarioJSONMap = feature.toMap();
	        featureMap.put("uri", uri);
	        scenarioJSONMap.put("uri", uri);
	        featureMaps.add(featureMap);
	    }

	    @Override
	    public void background(Background background) {
	        getFeatureElements().add(background.toMap());
	    }

	    @SuppressWarnings("rawtypes")
		@Override
	    public void scenario(Scenario scenario) {
	        getFeatureElements().add(scenario.toMap());
	        if (beforeHooks.size() > 0) {
	            getFeatureElement().put("before", beforeHooks);
	            beforeHooks = new ArrayList<Map>();
	        }
	    }

	    @Override
	    public void scenarioOutline(ScenarioOutline scenarioOutline) {
	        getFeatureElements().add(scenarioOutline.toMap());
	    }

	    @Override
	    public void examples(Examples examples) {
	        getAllExamples().add(examples.toMap());
	    }

	    @Override
	    public void step(Step step) {
	        getSteps().add(step.toMap());
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    public void match(Match match) {
	        getCurrentStep(Phase.match).put("match", match.toMap());
	    }

	    @Override
	    public void embedding(String mimeType, byte[] data) {
	        final Map<String, String> embedding = new HashMap<String, String>();
	        embedding.put("mime_type", mimeType);
	        embedding.put("data", Base64.encodeBytes(data));
	        getEmbeddings().add(embedding);
	    }

	    @Override
	    public void write(String text) {
	        getOutput().add(text);
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    public void result(Result result) {
	        getCurrentStep(Phase.result).put("result", result.toMap());
	    }

	    @Override
	    public void before(Match match, Result result) {
	    	beforeHooks.add(buildHookMap(match,result));
	    }

	    @SuppressWarnings("rawtypes")
		@Override
	    public void after(Match match, Result result) {
	        List<Map> hooks = getFeatureElement().get("after");
	        if (hooks == null) {
	            hooks = new ArrayList<Map>();
	            getFeatureElement().put("after", hooks);
	        }
	        hooks.add(buildHookMap(match,result));
	    }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
		private Map buildHookMap(final Match match, final Result result) {
	        final Map hookMap = new HashMap();
	        hookMap.put("match", match.toMap());
	        hookMap.put("result", result.toMap());
	        return hookMap;
	    }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
		public void appendDuration(final int timestamp) {
	        final Map result = (Map) getCurrentStep(Phase.result).get("result");
	        // check to make sure result exists (scenario outlines do not have results yet)
	        if (result != null) {
	            //convert to nanoseconds
	            final long nanos = timestamp * 1000000000L;
	            result.put("duration", nanos);
	        }
	    }

	    @Override
	    public void eof() {
	    }

	    @Override
	    public void done() {
	        out.append(gson().toJson(featureMaps));
	        // We're *not* closing the stream here.
	        // https://github.com/cucumber/gherkin/issues/151
	        // https://github.com/cucumber/cucumber-jvm/issues/96
	    }

	    @Override
	    public void close() {
	        out.close();
	    }

	    @Override
	    public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
	        throw new UnsupportedOperationException();
	    }

	    @Override
	    public void startOfScenarioLifeCycle(Scenario scenario) {
	        // NoOp
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    public void endOfScenarioLifeCycle(Scenario scenario) {
	        // NoOp
	    	PrintWriter  file = null;
	    	Properties prop = new Properties();
	    	

	    	String json_FileName = scenario.getName().split("-")[0];
	    	ArrayList<HashMap<String, Object>> element = (ArrayList<HashMap<String, Object>>) scenarioJSONMap.get("elements");
	    	for (HashMap<String, Object> hashMap : element) {
	    		if(!hashMap.get("name").toString().split("-")[0].equalsIgnoreCase(json_FileName)) {
	    			element.remove(hashMap);
	    		}

	    		scenarioJSONMap.put("elements", element);

			}
	    		try {
	    			prop.load(new BufferedReader(new FileReader(FilePath.TEST_CONFIG_PATH  +"API" + File.separator + "API_Configuration.properties")));
		    		String fileName = FilePath.TEST_REPORT_PATH+ json_FileName.split("-")[0].trim()+".json";
					file = new PrintWriter (fileName);
					gson().toJson(scenarioJSONMap, file);
					file.close();
					Thread.sleep(2000);
					 String text = new String(Files.readAllBytes(Paths.get(new File(fileName).toURI())), StandardCharsets.UTF_8);
					 text = "["+text.replaceAll("TEST_EXECUTION_KEY",prop.getProperty("TEST_EXECUTION_KEY")).replaceAll("\\{\\},", "")+"]";
					 
					 FileWriter files = new FileWriter(fileName);
					 files.write(text);
					 files.flush();
					 files.close();
	    		}catch(Exception e) {
	    			
	    		}
	    }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
		private List<Map<String, Object>> getFeatureElements() {
	        List<Map<String, Object>> featureElements = (List) featureMap.get("elements");
	        if (featureElements == null) {
	            featureElements = new ArrayList<Map<String, Object>>();
	            featureMap.put("elements", featureElements);
	            scenarioJSONMap.put("elements", featureElements);
//	            scenarioJSONMap.putAll(featureMap);
	        }
	       
	        return featureElements;
	    }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
		private Map<Object, List<Map>> getFeatureElement() {
	        if (getFeatureElements().size() > 0) {
	            return (Map) getFeatureElements().get(getFeatureElements().size() - 1);
	        } else {
	            return null;
	        }
	    }

	    @SuppressWarnings("rawtypes")
		private List<Map> getAllExamples() {
	        List<Map> allExamples = getFeatureElement().get("examples");
	        if (allExamples == null) {
	            allExamples = new ArrayList<Map>();
	            getFeatureElement().put("examples", allExamples);
	        }
	        return allExamples;
	    }

	    @SuppressWarnings("rawtypes")
		private List<Map> getSteps() {
	        List<Map> steps = getFeatureElement().get("steps");
	        if (steps == null) {
	            steps = new ArrayList<Map>();
	            getFeatureElement().put("steps", steps);
	        }
	        return steps;
	    }

	    @SuppressWarnings("unchecked")
		private List<Map<String, String>> getEmbeddings() {
	        List<Map<String, String>> embeddings = (List<Map<String, String>>) getCurrentStep(Phase.embedding).get("embeddings");
	        if (embeddings == null) {
	            embeddings = new ArrayList<Map<String, String>>();
	            getCurrentStep(Phase.embedding).put("embeddings", embeddings);
	        }
	        return embeddings;
	    }

	    @SuppressWarnings("unchecked")
		private List<String> getOutput() {
	        List<String> output = (List<String>) getCurrentStep(Phase.output).get("output");
	        if (output == null) {
	            output = new ArrayList<String>();
	            getCurrentStep(Phase.output).put("output", output);
	        }
	        return output;
	    }

	    protected Gson gson() {
	        return new GsonBuilder().setPrettyPrinting().create();
	    }
	
}
