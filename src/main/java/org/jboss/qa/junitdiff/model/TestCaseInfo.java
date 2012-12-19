package org.jboss.qa.junitdiff.model;

import java.util.*;

/**
 * One test with multiple test infos - one for each "column" (test run)
 *
 * @author Ondrej Zizka
 */
public class TestCaseInfo {

	private String className;
	private String name;

	private List<TestRunInfo> testInfos = new ArrayList<TestRunInfo>();

	public TestCaseInfo(String className, String name) {
		this.name = name;
		this.className = className;
	}

	public TestCaseInfo(String name, List<TestRunInfo> testInfos) {
		this.name = name;
		this.testInfos = testInfos;
	}

	public TestCaseInfo( TestRunInfo test ) {
		this.name = test.getName();
		this.className = test.getClassname();
	}

	// <editor-fold defaultstate="collapsed" desc="List overrides">
	public int size() {
		return testInfos.size();
	}

	public boolean isEmpty() {
		return testInfos.isEmpty();
	}

	public TestRunInfo get(int index) {
		return testInfos.get(index);
	}

	public boolean addAll(Collection<? extends TestRunInfo> c) {
		return testInfos.addAll(c);
	}

	public boolean add(TestRunInfo e) {
		return testInfos.add(e);
	}// </editor-fold>

	public String getClassName() {		return className;	}
	public void setClassName(String className) {		this.className = className;	}
	public String getName() {		return name;	}
	public void setName(String name) {		this.name = name;	}

	public List<TestRunInfo> getTestRuns() {		return Collections.unmodifiableList(testInfos);	}

	public String getFullName() {
		return this.getClassName() + "." + this.getName();
	}

}// class
