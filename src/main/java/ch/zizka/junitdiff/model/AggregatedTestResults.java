
package ch.zizka.junitdiff.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 *  Aggregated results:  A matrix of joined test results lists, with one "column" for each list.
 *
 * @author Ondrej Zizka
 */
public class AggregatedTestResults
{
	private static final Logger log = LoggerFactory.getLogger(AggregatedTestResults.class);


	// ClassName -> test results list.
	private Map<String, TestClassInfo> byTestClassName = new LinkedHashMap<String, TestClassInfo>();


	// Groups.
	private List<IGroup> groups = new ArrayList<IGroup>();

	private Groups groupsFactory = new Groups();

	public List<TestRunResultsList> mergeTestSuites( List<TestSuite> testSuites, String groupName ) {
		List<TestRunResultsList> reportsLists = new ArrayList<TestRunResultsList>( testSuites.size() );
		for( TestSuite testSuite : testSuites ) {
			reportsLists.add( testSuite.getTestRunResultsList() );
		}

		this.merge( reportsLists, groupName );

		return reportsLists;
    }
    

	/**
	 *  Processes given list of test results collections and aggregates them to a matrix -
	 *  tests will be grouped by full name; each item of the list will create one column
	 *  if it contains a test of the respective name.
	 *
	 *                  run1   run2   run3
	 *  testMyTest      OK     FAIL   OK
	 *  testOtherTest   FAIL   OK     OK
	 *
	 *  @return  newly created AggregatedTestResults.
	 */
	public void merge( List<TestRunResultsList> reportsLists )
	{
		this.merge( reportsLists, this.generateGroupName() );
	}


    public void merge( List<TestRunResultsList> reportsLists, String groupName ) {
        final boolean trace = log.isTraceEnabled();

		IGroup group = groupsFactory.getGroup(groupName);
		this.groups.add(group);

		// For all reports...
		for( TestRunResultsList testResultsList : reportsLists ){

			if( trace )  log.trace("  Aggregating {}", testResultsList);///

			// Add all their tests, grouped by full name, to the aggregated matrix.
			for( TestRunInfo curTest : testResultsList.getTestResults() )
			{
				TestClassInfo testclass = this.findTestsByClassName(curTest.getClassname());
				if( testclass == null ){
					testclass = new TestClassInfo( curTest.getClassname() );
					this.add(testclass);
				}
				curTest.setGroup( group );
				testclass.add(curTest);
			}

		}
	}// merge()


		
	// Group name generation.
	private int nextGroupNum = 1;
	private String generateGroupName() {
		return "Group"+nextGroupNum++;
	}

	public boolean add(TestClassInfo testClass) {
		byTestClassName.put(testClass.getClassName(), testClass);
		return true;
	}

	public boolean containsByClassName(String className) {
		return byTestClassName.containsKey(className);
	}

	public List<TestCaseInfo> getTestCases() {
		List<TestCaseInfo> ret = new ArrayList<TestCaseInfo>();
		for(TestClassInfo testclass : byTestClassName.values()){
			ret.addAll(testclass.getTestCasesWithPseudoRuns());
		}
		return ret;
	}

	public List<IGroup> getGroups() {
		return Collections.unmodifiableList(groups);
	}

	/**
	 *  Finds a test by it's class name, i.e. "org.jboss.ClassName".
	 * @param className
	 * @return
	 */
	public TestClassInfo findTestsByClassName( String className ) {
		return byTestClassName.get( className );
	}

	public void shortenGroupsNames() {
		groupsFactory.shortenNames();
	}
}// class
