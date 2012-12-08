
package org.jboss.qa.junitdiff.model;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Aggregated results:  A matrix of joined test results lists, with one "column" for each list.
 *
 * @author Ondrej Zizka
 */
public class AggregatedTestResults
{
	private static final Logger log = LoggerFactory.getLogger(AggregatedTestResults.class);


	// Name -> test results list.
	private Map<String, AggregatedTestInfo> byFullTestName = new LinkedHashMap();


	// Groups.
	private List<IGroup> groups = new ArrayList<IGroup>();

	private Groups groupsFactory = new Groups();



	/**
	 *  Finds a test by it's full name, i.e. "org.jboss.ClassName.testMethodName".
	 * @param fullName
	 * @return
	 */
	public AggregatedTestInfo findTestByFullName( String fullName ) {
			return byFullTestName.get( fullName );
	}


	public List<TestRunResultsList> mergeTestSuites( List<TestSuite> testSuites, String groupName ) {
		List<TestRunResultsList> reportsLists = new ArrayList( testSuites.size() );
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
	 *  @returns  newly created AggregatedTestResults.
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
			for( TestInfo curTest : testResultsList.getTestResults() )
			{
				String fullName = curTest.getFullName();
				AggregatedTestInfo aggTest = this.findTestByFullName(fullName);
				if( aggTest == null ){
					aggTest = new AggregatedTestInfo( curTest );
					this.add(aggTest);
				}
				curTest.setGroup( group );
				aggTest.add( curTest );
			}

		}
	}// merge()


		
	// Group name generation.
	private int nextGroupNum = 1;
	private String generateGroupName() {
		return "Group"+nextGroupNum++;
	}


	public int size() {
		return byFullTestName.size();
	}

	public boolean isEmpty() {
		return byFullTestName.isEmpty();
	}

	public AggregatedTestInfo get(int index) {
		return byFullTestName.get(index);
	}

	public boolean add(AggregatedTestInfo e) {
		byFullTestName.put(e.getFullName(), e);
		return true;
	}

	public boolean addAll(Collection<? extends AggregatedTestInfo> atis) {
		for( AggregatedTestInfo ati : atis ) {
			byFullTestName.put(ati.getFullName(), ati);
		}
		return true;
	}

	public boolean containsByName(String fullName) {
		return byFullTestName.containsKey(fullName);
	}

	public List<AggregatedTestInfo> getTestInfos() {
		return Collections.unmodifiableList(new ArrayList(byFullTestName.values()));
	}

	/*public List<String> getGroups() {
		return Collections.unmodifiableList(groups);
	}*/

	public List<IGroup> getGroups() {
		return Collections.unmodifiableList(groups);
	}

	public void shortenGroupsNames() {
		groupsFactory.shortenNames();
	}

	/**
	 * Returns the differing parts of group names.
	 *   {abcfoo123, abcbar123} => {foo, bar}
	 */
	/*public List<String> getGroupNamesDifferingParts() {

		// Get the common prefix.
		String commonPrefix = StringUtils.getCommonPrefix( groups.toArray( new String[groups.size()] ));
		if( commonPrefix.length() == 0 )
			return Collections.unmodifiableList(groups);

		// Cut off the common prefix.
		List<String> shortNames = new ArrayList<String>( groups.size() );
		List<String> revertedShortNames = new ArrayList( groups.size() );

		for( String string : groups ) {
			String differingPart = string.substring(commonPrefix.length());
			shortNames.add( differingPart );
			revertedShortNames.add( StringUtils.reverse(differingPart) );
		}

		// Get the common suffix.
		String commonSuffix = StringUtils.getCommonPrefix( revertedShortNames.toArray( new String[revertedShortNames.size()] ));
		if( commonSuffix.length() == 0 )
			return Collections.unmodifiableList( shortNames );

		// Cut off the common suffix.
		shortNames.clear();
		for( String revertedName : revertedShortNames ) {
			shortNames.add( StringUtils.reverse( revertedName.substring( commonSuffix.length()) ) );
		}

		return Collections.unmodifiableList( shortNames );
	}*/




}// class
