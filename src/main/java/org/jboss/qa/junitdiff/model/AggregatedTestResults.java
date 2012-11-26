
package org.jboss.qa.junitdiff.model;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Aggregated results:  A matrix of joined test results lists, with one "column" for each list.
 *
 * @author Ondrej Zizka
 *
 * TODO: Change List + Map  to OrderedSet or such.
 */
public class AggregatedTestResults
{
	private static final Logger log = LoggerFactory.getLogger(AggregatedTestResults.class);


	// List of test with all their runs.
	private List<AggregatedTestInfo> testInfos = new ArrayList();


	// Name -> test results list.
	private Map<String, AggregatedTestInfo> byFullTestName = new TreeMap();


	// Groups.
	//private List<String> groups = new ArrayList();
	private List<Group> groups = new ArrayList<Group>();



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

		Group group = new Group(groupName);
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
		return testInfos.size();
	}

	public boolean isEmpty() {
		return testInfos.isEmpty();
	}

	public AggregatedTestInfo get(int index) {
		return testInfos.get(index);
	}

	public boolean add(AggregatedTestInfo e) {
		byFullTestName.put(e.getFullName(), e);
		return testInfos.add(e);
	}

	public boolean addAll(Collection<? extends AggregatedTestInfo> atis) {
		for( AggregatedTestInfo ati : atis ) {
			byFullTestName.put(ati.getFullName(), ati);
		}
		return testInfos.addAll(atis);
	}

	public boolean containsByName(String fullName) {
		return byFullTestName.containsKey(fullName);
	}

	public List<AggregatedTestInfo> getTestInfos() {
		return Collections.unmodifiableList(testInfos);
	}

	/*public List<String> getGroups() {
		return Collections.unmodifiableList(groups);
	}*/

	public List<Group> getGroups() {
		return Collections.unmodifiableList(groups);
	}

	/**
	 * Set the differing parts of group paths as name.
	 *   {abcfoo1234, abcbarbar1234} => {foo, barbar}
	 */
	public void createGroupNamesDifferingParts(){
		String[] allPaths = new String[groups.size()];
		String[] allPathsRev = new String[groups.size()];
		int i=0;
		for(Group g : groups){
			allPaths[i]=g.getPath();
			allPathsRev[i]=StringUtils.reverse(g.getPath());
			i++;
		}

		// Get the common prefix and sufix
		String commonPrefix = StringUtils.getCommonPrefix(allPaths); // abc
		String commonSufix = StringUtils.getCommonPrefix(allPathsRev); // 1234

		// Get the common prefix and sufix lengths
		int prefixLength = commonPrefix.length(); // 3
		int sufixLength = commonSufix.length(); // 4

		// Cut off the common prefix and sufix
		if( prefixLength + sufixLength != 0 ){ // 7
			for(Group g : groups){
				int nameLength = g.getPath().length(); // abcfoo1234 = 10; abcbarbar1234 = 13
				//  abc|foo|1234      ->  foo          3             10  - 4 = 6
				//  abc|bar bar|1234  ->  barbar       3             13  - 4 = 9
				//  012|345|678|9012
				g.setName(g.getPath().substring(prefixLength, nameLength - sufixLength));
			}
		}

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
