package ch.zizka.junitdiff;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * HTML output.
     */
    public void testHibernateRunsHTML()
    {
		System.out.println("moje: " + System.getProperty("moje")); ///
		String dataDir = System.getProperty("junitdiff.test.data.dir");

		String outPath = "target/test-tmp/"+this.getName()+"/output.html";
		File outFile = new File(outPath);
		outFile.getParentFile().mkdirs();
        
		try {
			JUnitDiffApp.main( new String[]{
				dataDir + "/hibernate-run1",
				dataDir + "/hibernate-run2",
				dataDir + "/hibernate-run3",
				dataDir + "/hibernate-run4",
				"-o", outPath
			} );
		}
		catch( Throwable t ){
			fail( t.getMessage() );
		}

		assertTrue( outFile.exists() );
    }
	
    /**
     * XML output.
     */
    public void testHibernateRunsXML()
    {
		System.out.println("moje: " + System.getProperty("moje")); ///
		String dataDir = System.getProperty("junitdiff.test.data.dir");

		String outPath = "target/test-tmp/"+this.getName()+"/output.xml";
		File outFile = new File(outPath);
		outFile.getParentFile().mkdirs();

		try {
			JUnitDiffApp.main( new String[]{
				dataDir + "/hibernate-run1",
				dataDir + "/hibernate-run2",
				dataDir + "/hibernate-run3",
				dataDir + "/hibernate-run4",
				"-o", outPath, "-xml"
			} );
		}
		catch( Throwable t ){
			fail( t.getMessage() );
		}
		
		assertTrue( outFile.exists() );
    }

}
