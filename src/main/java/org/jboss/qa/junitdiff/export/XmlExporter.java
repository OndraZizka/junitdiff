
package org.jboss.qa.junitdiff.export;

import cz.dynawest.xslt.XsltTransformer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.jboss.qa.junitdiff.model.AggregatedTestResults;
import org.jboss.qa.junitdiff.model.AggregatedTestInfo;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.List;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.qa.junitdiff.JUnitDiffApp;
import org.jboss.qa.junitdiff.ex.JUnitDiffException;
import org.jboss.qa.junitdiff.model.AggregatedData;
import org.jboss.qa.junitdiff.model.TestInfo;
import org.jboss.qa.junitdiff.model.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class XmlExporter
{
	private static final Logger log = LoggerFactory.getLogger(JUnitDiffApp.class);

    private static final String XSL_TEMPLATE_PATH = "/JUnitDiff-to-HTML.xsl";

    
	/**
	 *  Exports given matrix to the given file, as a JUnit-like XML.
	 */
	public static void exportToHtmlFile( AggregatedData aggData, File fout ) throws JUnitDiffException
    {
        //exportToXML( atr, new PrintStream( fout, "uft8" ) );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exportToXML( aggData, new PrintStream( baos ) );
        
        try {
          ReaderInputStream ris = new ReaderInputStream( new StringReader(baos.toString("utf8")), "utf8");
          InputStream xslTemplate = XmlExporter.class.getResourceAsStream( XSL_TEMPLATE_PATH );
          XsltTransformer.transform( ris, xslTemplate, fout );
        }
        catch( TransformerException ex ){
          throw new JUnitDiffException("Error when creating HTML file: "+ex.getMessage(), ex);
        }
        catch( UnsupportedEncodingException ex ) {
          throw new RuntimeException( ex );
        }
	}

    

	/**
	 *  Exports given matrix to the given file, as a JUnit-like XML.
	 */
	public static void exportToXML( AggregatedData aggData, File fout ) throws FileNotFoundException {

			exportToXML( aggData, new PrintStream(fout) );

	}


	/**
	 *  Exports given matrix to the given printstream, as a JUnit-like XML.
	 *  TODO: SAX-like output?
	 */
	public static void exportToXML( AggregatedData aggData, PrintStream out ) {
		out.println("<aggregate>");

		AggregatedTestResults atr = aggData.getAggregatedTestResults();
		// TODO: Move groups to AggregatedData? But that would be redundant... we have it in atr's map.


		// Groups.
		out.println("\t<groups>");
		List<String> groups = atr.getGroups();
		List<String> groupDiffNames = atr.getGroupNamesDifferingParts();

		/*for (String group : groups) {
				out.append("\t\t<group name=\"").append(x( group )).append("\" path=\"").append(x( group )).append("\"/>\n");
		}*/

		for( int i = 0; i < groups.size(); i++ ) {
			String groupName = groups.get(i);
			String groupNameDifferingPart = groupDiffNames.get(i);
			out.append("\t\t<group name=\"").append(x( groupNameDifferingPart )).append("\" path=\"").append(x( groupName )).append("\"/>\n");
		}

		out.println("\t</groups>\n");



		// Test cases.
		for( AggregatedTestInfo ati : atr.getTestInfos() ) {
			out.append("\t<testcase classname=\"").append(x( ati.getClassName() ))
				 .append("\" name=\"").append(x( ati.getName() )) .append("\">\n");

			for( TestInfo ti : ati.getTestInfos() ){
				out.append("\t\t<testrun result=\"").append(x( ti.getResult().name() ))
					 .append("\" time=\"").append(x( ti.getTime() ))
					 .append("\" group=\"").append(x( ti.getGroup() )) // TODO: JBQA-4131
					 .append("\">\n");

				// <failure message="Exception message" type="java.lang.Exception">
				if( null != ti.getFailure() ){
					out.append("\t\t\t<failure message=\"").append(x( ti.getFailure().getMessage() ))
					   .append("\" type=\"").append(x( ti.getFailure().getType() )).append("\">\n");
					out.print(x( ti.getFailure().getTrace() ));
					out.println("</failure>");
				}

				out.println("\t\t</testrun>");
			}
			out.println("\t</testcase>");
		}


		// Testsuites,  BTW TODO:  Rename the top-level element.
		// TODO: Add TestSuite reference to TestInfo and delegate TestInfo.getOrigin() to that.
		// TODO: Perhaps the test cases could be moved to the TestSuite, after all.

		out.println("\t<testsuites>");
		for( TestSuite ts : aggData.getTestSuites() ){
			out.append("\t\t<testsuite group=\"").append(x( ts.getGroup() ))
				 .append("\" name=\"").append(x( ts.getClassName() ))
				 .append("\" origin=\"").append(x( ts.getOrigin() ))
				 .append("\">\n");

			out.append("\t\t<system-out><![CDATA[").append( ts.getStdOut() ).append("]]></system-out>\n");
			out.append("\t\t<system-err><![CDATA[").append( ts.getStdErr() ).append("]]></system-err>\n");

							out.println("\t\t</testsuite>");
			}
			out.println("\t</testsuites>\n");


			out.println("</aggregate>");
		}


	/** Helper - XML escape. */
	private static String x( String s ){ return StringEscapeUtils.escapeXml( s ); }


}// class
