
package org.jboss.qa.junitdiff.model;


import java.io.Serializable;



/**
 *  
 * @author Ondrej Zizka
 */
public class TestSuite implements Serializable
{
  
  
		/**
		 *  Where did this results collection come from (e.g. a filename).
		 */
		private String origin;
		private String group;

    /* 
     * <testsuite errors="0" failures="0" 
     * hostname="mm18-3.mm.atl2.redhat.com" 
     * name="org.hibernate.test.annotations.access.AccessTest" 
     * tests="6" 
     * time="60.045" 
     * timestamp="2010-11-16T00:22:54">
     */

    private String className;

    private TestRunResultsList testRunResultsList;

    private String stdErr;
    private String stdOut;


    public TestSuite( String className, TestRunResultsList testRunResultsList, String stdErr, String stdOut ) {
        this.className = className;
        this.testRunResultsList = testRunResultsList;
        this.stdErr = stdErr;
        this.stdOut = stdOut;
    }





    // <editor-fold defaultstate="collapsed" desc="get set">
    public String getClassName() {    return className;  }  
    public void setClassName( String className ) {    this.className = className;  }  
    public String getStdErr() {    return stdErr;  }  
    public void setStdErr( String stdErr ) {    this.stdErr = stdErr;  }  
    public String getStdOut() {    return stdOut;  }  
    public void setStdOut( String stdOut ) { this.stdOut = stdOut; }
    public String getOrigin() {				return origin;		}
    public void setOrigin(String origin) {
      this.origin = origin;  
      // Transfer group / origin to the collection of testRunResults, so they know.
      this.testRunResultsList.setOrigin( origin );
    }
    public String getGroup() {      return group;    }
    public void setGroup( String group ) {
      this.group = group;
      // Transfer group / origin to the collection of testRunResults, so they know.
      this.testRunResultsList.setGroup( group );
    }
    public TestRunResultsList getTestRunResultsList() {    return testRunResultsList;  }
    // </editor-fold>



    // <editor-fold defaultstate="collapsed" desc="overrides">
    @Override
    public boolean equals( Object obj ) {
      if( obj == null ){
        return false;
      }
      if( getClass() != obj.getClass() ){
        return false;
      }
      final TestSuite other = (TestSuite) obj;
      if( (this.className == null) ? (other.className != null) : !this.className.equals( other.className ) ){
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 97 * hash + (this.className != null ? this.className.hashCode() : 0);
      return hash;
    }

    public String getFullName() {
      return this.getGroup() + "|" + this.getClassName();
    }
    // </editor-fold>




}// class TestSuite
