package runner;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import stepDefinitions.BasicDefinition;

@RunWith(Cucumber.class)

@CucumberOptions(features = "classpath:Features", 
				glue = {"classpath:com.lloyds.IPP.StepDefinitions"}, 
				monochrome = true, 
				strict = true, 
				publish = true,
				stepNotifications = true, 
				snippets = SnippetType.UNDERSCORE, 

				plugin = {"html:TestReport/TestRunReport.html","json:TestReport/Report.json","com.lloyds.IPP.lloydsReport.JIRAReport:TestReport/report.json"},
				tags = "@MVP4TC01")

public class Runner {
	public static LinkedHashMap<String, String> pmid = new LinkedHashMap<String, String>();
	@BeforeClass
	public static void generateXLMap() throws IOException {
		BasicDefinition.generateMap();
	}
}
