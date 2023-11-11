package runner;

import org.junit.runner.RunWith;



import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;


@RunWith(Cucumber.class)
@CucumberOptions
(
		features = "classpath:Features"
		 ,glue={"classpath:com.lloyds.IPP.StepDefinitions"}
		 ,monochrome=true
		 ,strict = true
		 ,snippets = SnippetType.UNDERSCORE		 
		 //,plugin = {"com.lloyds.IPP.lloydsReport.ExtentCucumberFormatter:TestReport/report.html"}
		 ,tags ="@SSO"
)

public class API_Runner {
	
}
