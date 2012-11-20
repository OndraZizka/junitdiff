
package org.jboss.qa.junitdiff;


import org.jboss.qa.junitdiff.model.AggregatedTestResults;
import org.jboss.qa.junitdiff.model.AggregatedTestInfo;
import org.jboss.qa.junitdiff.model.TestRunResultsList;
import java.util.List;
import org.jboss.qa.junitdiff.model.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *
 * @author Ondrej Zizka
 */
public class Processing
{
		private static final Logger log = LoggerFactory.getLogger(Processing.class);


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
		 *  @deprecated  use AggregatedTestResults#merge()
		 */
		private AggregatedTestResults xaggregateResultsLists( List<TestRunResultsList> reportsLists ) {

				AggregatedTestResults agg = new AggregatedTestResults();
				return aggregateResultsLists( reportsLists, agg );

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
		 *  @returns  param agg.
		 *  @throws NPE if any param is null.
		 *  @deprecated  use AggregatedTestResults#merge()
		 */
		private AggregatedTestResults aggregateResultsLists(List<TestRunResultsList> reportsLists, AggregatedTestResults agg ) {

				// For all reports...
				for( TestRunResultsList testResultsList : reportsLists ){

						log.debug("  Aggregating " + testResultsList.toString() + "...");

						// Add all their tests, grouped by full name, to the aggregated matrix.
						for( TestInfo curTest : testResultsList.getTestResults() ){
								String fullName = curTest.getFullName();
								AggregatedTestInfo aggTest = agg.findTestByFullName(fullName);
								if( aggTest == null ){
										aggTest = new AggregatedTestInfo( curTest );
										agg.add(aggTest);
								}
								aggTest.add( curTest );
						}

				}

				return agg;

		}


}// class
