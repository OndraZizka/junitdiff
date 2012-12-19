package org.jboss.qa.junitdiff.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class TestClassInfo {
	private String classname;
	private Map<String,TestCaseInfo> testsByTestName = new HashMap<String,TestCaseInfo>();
	private List<TestRunInfo> classRuns = new ArrayList<TestRunInfo>();

	public TestClassInfo(String classname) {
		this.classname = classname;
	}

	private void setTests(List<TestCaseInfo> tests){
		testsByTestName.clear();
		for(TestCaseInfo test : tests){
			testsByTestName.put(test.getName(), test);
		}
	}

	void add(TestRunInfo testrun){
		if(testrun.getClassname().equals(testrun.getName())){
			classRuns.add(testrun);
			return;
		}
		TestCaseInfo testcase = testsByTestName.get(testrun.getName());
		if(testcase == null){
			testcase = new TestCaseInfo(testrun);
			addTestCase(testcase);
		}
		testcase.add(testrun);
	}

	Collection<TestCaseInfo> getTestCases() {
		return Collections.unmodifiableCollection(testsByTestName.values());
	}

	void generatePseudoRuns() {
		for (TestRunInfo classrun : classRuns) {
			for (TestCaseInfo testcase : testsByTestName.values()) {
				TestRunInfo pseudoTestrun = new TestRunInfo(classname, testcase.getName(), classrun.getResult(), classrun.getTime());
				pseudoTestrun.setFailure(classrun.getFailure());
				pseudoTestrun.setGroup(classrun.getGroup());
				testcase.add(pseudoTestrun);
			}
		}
	}

	String getClassName() {
		return classname;
	}

	private void addTestCase(TestCaseInfo testcase) {
		TestCaseInfo old = this.testsByTestName.put(testcase.getName(), testcase);
	}
}
