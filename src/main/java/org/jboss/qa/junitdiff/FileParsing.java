
package org.jboss.qa.junitdiff;


import org.jboss.qa.junitdiff.model.Failure;
import org.jboss.qa.junitdiff.model.TestRunResultsList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.qa.junitdiff.ex.JUnitDiffException;
import org.jboss.qa.junitdiff.model.TestRunInfo;
import org.jboss.qa.junitdiff.model.TestSuite;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;



/**
 *
 * @author Ondrej Zizka
 */
public class FileParsing
{
		private static final Logger log = LoggerFactory.getLogger( FileParsing.class );

		private static final int MAX_LIST_FILE_SIZE_KB = 512;





		/**
		 *  Get the test result lists - one for each XML file or a ".txt" list of XML files.
		 */
		public static List<TestSuite> getSeparatedResultsLists(List<File> reportFiles) throws JUnitDiffException {

				List<TestSuite>  reportsLists = new LinkedList();
				List<JUnitDiffException> errors = new ArrayList();

				for( File file : reportFiles ) {

						if( ! file.isFile()  ){
								log.warn("  Not a regular file: "+file.getPath());
								continue;
						}
						if( file.length() == 0  ){
								log.warn("  File is empty: "+file.getPath());
								continue;
						}
						try {
								TestSuite ts = parseFile(file);
								reportsLists.add( ts );
						} catch (JUnitDiffException ex) {
								String msg = "  Error processing '"+file.getPath()+"': "+ ex.getMessage();
								log.error( msg );
								errors.add( new JUnitDiffException(msg, ex) );
								continue;
						}

				}

				if( ! errors.isEmpty() ){
						throw new JUnitDiffException("Errors ("+errors.size()+") when processing files.", errors);
				}

				return reportsLists;
		}



		/**
		 * Parses test results from a file.
		 *
		 * @param file May be a text file with a list of files to parse, or a JUnit .xml report.
		 */
		private static TestSuite parseFile( File file ) throws JUnitDiffException {
				// Determine whether a file is a list of .xml files or a .xml file (report)
				boolean isXml = file.getName().endsWith(".xml");


				// Try to parse as XML.
				try {
						TestSuite ts = tryParsingAsXml( file );
						return ts;
				}
				catch( JDOMException ex ) {
						// 		 *  @deprecated  List expanding is handled in FileParsing#preprocessPaths();
						//log.debug("  Failed parsing as XML JUnit test report: "+file.getPath());
						throw new JUnitDiffException("  Failed parsing '"+file.getPath()+"' as XML JUnit test report: "+ex.getMessage());
				}
				catch( IOException ex ) {
						throw new JUnitDiffException("  Error reading from file '"+file.getPath()+"': "+ex.getMessage());
				}


				// XML parsing failed, so we try to treat it as a list of files.
				//TestResultsList ar = parseAsListOfFiles( file );
				//return ar;

		}


		/**
		 *  Assumes the file contains a list of paths to JUnit XML reports.
		 *  Returns test data aggregated from all of them.
		 *  @deprecated  List expanding is handled in FileParsing#preprocessPaths();
		 */
		private static TestRunResultsList parseAsListOfFiles( File file ) throws JUnitDiffException {

				if( file.length() > MAX_LIST_FILE_SIZE_KB * 1024 )
						log.warn("  File is too big (" + file.length() / 1024 +" kb) : "+file.getPath());


				// Read lines and treat as XML files.
				List<String> readLines;
				try {
						readLines = FileUtils.readLines(file);
				} catch (IOException ex) {
						throw new JUnitDiffException("  Error reading from file '"+file.getPath()+"': "+ex.getMessage());
				}


				List<TestRunResultsList> trls = new ArrayList();
				List<Exception> errors = new ArrayList();

				for( String line : readLines ) {
						line = line.trim();
						try {
								TestSuite ts = tryParsingAsXml(new File(line));
								trls.add( ts.getTestRunResultsList() );
						}
						catch (JDOMException ex) {
								String msg = "  Error parsing '"+file.getPath()+"': "+ ex.getMessage();
								log.error( msg );
								errors.add(ex); continue;
						}
						catch (IOException ex) {
								String msg = "  Error reading from file '"+file.getPath()+"': "+ex.getMessage();
								log.error( msg );
								errors.add( new IOException(msg) );
								continue;
						}
				}

				if( ! errors.isEmpty() ){
						throw new JUnitDiffException( "  Errors ("+errors.size()+") occured when parsing the list of files", errors );
				}


				TestRunResultsList concatenatedResults = TestRunResultsList.fromList( trls );
				return concatenatedResults;

		}


		/**
		 *   Tries parsing as XML.
		 */
		private static TestSuite tryParsingAsXml( FileInputStream is ) throws JDOMException, IOException {

				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build( is );
				return parseJUnitXmlReport( doc );

		}
		private static TestSuite tryParsingAsXml( File file ) throws JDOMException, IOException {

				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build( file );
				TestSuite parseJUnitXmlReport = parseJUnitXmlReport( doc );
				parseJUnitXmlReport.setOrigin( file.getPath() );
				return parseJUnitXmlReport;

		}


		/**
		 * Parses test results from a JUnit .xml report.
		 */
		private static TestSuite parseJUnitXmlReport( Document doc ) throws JDOMException {

        
        // For all testcases...
				XPath xPath = XPath.newInstance("//testsuite/testcase");
				List<Element> testcaseElements = xPath.selectNodes(doc);


				TestRunResultsList resultsList = new TestRunResultsList();

				for( Element elm : testcaseElements ) {
						String time = elm.getAttributeValue("time");
						String name = elm.getAttributeValue("name");
						String classname = elm.getAttributeValue("classname");

						TestRunInfo info = new TestRunInfo(classname, name, TestRunInfo.Result.OK, time);


						// Failure.
						Element child = elm.getChild("failure");
						if( null != child ){
								String message = child.getAttributeValue("message");
								String type = child.getAttributeValue("type");
								String trace = child.getTextNormalize();
								trace = StringUtils.substringAfter(trace, "\n");
								Failure fail = new Failure(message, type, trace);

								info.setResult(TestRunInfo.Result.FAIL);
								info.setFailure(fail);
						}

						// Error.
						child = elm.getChild("error");
						if( null != child ){
								String message = child.getAttributeValue("message");
								String type = child.getAttributeValue("type");
								String trace = child.getTextNormalize();
								trace = StringUtils.substringAfter(trace, "\n");
								Failure fail = new Failure(message, type, trace);

								info.setResult(TestRunInfo.Result.ERROR);
								info.setFailure(fail);
						}

						// Skipped.
						child = elm.getChild("skipped");
						if( null != child ){
								info.setResult(TestRunInfo.Result.SKIPPED);
						}


						resultsList.add( info );
				}


        
      /*
       * <testsuite errors="0" failures="0" 
       * hostname="mm18-3.mm.atl2.redhat.com" 
       * name="org.hibernate.test.annotations.access.AccessTest" 
       * tests="6" 
       * time="60.045" 
       * timestamp="2010-11-16T00:22:54">
       */

        // System output
        String systemOut = (String) XPath.selectSingleNode( doc, "string(//testsuite/system-out)" );
       
        // System error
        String systemErr = (String) XPath.selectSingleNode( doc, "string(//testsuite/system-err)" );
        
        // Class name
        String tsName = (String) XPath.selectSingleNode( doc, "string(//testsuite/@name)" );
    
        
        // Testsuite
        TestSuite testSuite = new TestSuite( tsName, resultsList, systemOut, systemErr );
        
        
				return testSuite;

		}




}// class
