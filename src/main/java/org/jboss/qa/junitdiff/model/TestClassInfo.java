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
		// FIXME hard to figure out what is the reason of this call
		if(testrun.getClassname()!=null && testrun.getClassname().equals(testrun.getName())){
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

	/**
	 * Returns list of testCases with guessed skipped and failed runs.
	 * If whole test class is skiped or it's initialization fails, JUnit
	 * generates testcase element which have classname == name.
	 * This method tries to generate for such testcase new testcases (runs)
	 * (based on other runs of same class) which have result set according to
	 * the wohole class result.
	 *
	 * More in: JBQA-5466
	 */
	List<TestCaseInfo> getTestCasesWithPseudoRuns() {
		List<TestCaseInfo> testcases = new ArrayList<TestCaseInfo>();
		for(TestCaseInfo tc : testsByTestName.values()){
			TestCaseInfo testcase = new TestCaseInfo(tc);
			for (TestRunInfo classrun : classRuns) {
				TestRunInfo pseudoTestrun = new TestRunInfo(classname, testcase.getName(), classrun.getResult(), classrun.getTime());
				pseudoTestrun.setFailure(classrun.getFailure());
				pseudoTestrun.setGroup(classrun.getGroup());
				testcase.add(pseudoTestrun);
			}
			testcases.add(testcase);
		}
		return testcases;
	}

	/*void generatePseudoRuns() {
		for (TestRunInfo classrun : classRuns) {
			for (TestCaseInfo testcase : testsByTestName.values()) {
				TestRunInfo pseudoTestrun = new TestRunInfo(classname, testcase.getName(), classrun.getResult(), classrun.getTime());
				pseudoTestrun.setFailure(classrun.getFailure());
				pseudoTestrun.setGroup(classrun.getGroup());
				testcase.add(pseudoTestrun);
			}
		}
	}*/

	String getClassName() {
		return classname;
	}

	private void addTestCase(TestCaseInfo testcase) {
		TestCaseInfo old = this.testsByTestName.put(testcase.getName(), testcase);
	}
}
