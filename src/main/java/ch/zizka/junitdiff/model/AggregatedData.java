
package ch.zizka.junitdiff.model;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 *
 * @author Ondrej Zizka
 */
public class AggregatedData implements Serializable
{
  
  private final AggregatedTestResults aggregatedTestResults = new AggregatedTestResults();;
  
  private final List<TestSuite> testSuites = new LinkedList<TestSuite>();
  
  // map "groupname|test-class name" -> testsuite?
  private final Map<String, TestSuite> testSuiteByGroupAndClassName = new TreeMap<String, TestSuite>();
  
  



  /**
   * 
   */
  public void mergeTestSuites( List<TestSuite> testSuites, String groupName ){
    
    this.testSuites.addAll( testSuites );
    for( TestSuite testSuite : testSuites ) {
      this.testSuiteByGroupAndClassName.put( testSuite.getFullName(), testSuite );
    }
  	
    this.getAggregatedTestResults().mergeTestSuites( testSuites, groupName );
  
  }
  
  
 
  
  
  public AggregatedTestResults getAggregatedTestResults() {    return aggregatedTestResults;  }
  public List<TestSuite> getTestSuites() {    return testSuites;  }  
  
}// class AggregatedData
