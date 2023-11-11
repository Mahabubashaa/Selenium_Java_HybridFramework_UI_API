Automation suite which consists of
	 i. Sanity Test Cases
	ii. Regression Test Cases
       iii. In-Sprint Test Cases
   
and the entire package will be stored in GitHub and executed thourgh Kubernetes Container in Headless Mode. 
Test Results will be auto uploaded for Regression cases in JIRA if the user opted

FAQ:
1. How can i execute specific test case from Maven?
In command prompt enter the below syntax and pass the specific test case tagname
	mvn test -Dcucumber.filter.tags="@Smoke"
	
2. How can i execute multiple test case from Maven?
	mvn test -Dcucumber.filter.tags="@smoke and @Regression"		// Execute test cases named as Smoke and Regression
	mvn test -Dcucumber.filter.tags="@smoke or @Regression"			// Execute test cases either named as Smoke or Regression
	mvn test -Dcucumber.filter.tags="not @smoke"                    	// Execute all cases except Smoke
	mvn test -Dcucumber.filter.tags="(@smoke or @GUI) and (not @in-sprint)"	// Execute test cases either named as smoke or GUI and not mentioned as in-sprint

