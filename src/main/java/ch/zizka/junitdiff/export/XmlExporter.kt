package ch.zizka.junitdiff.export

import ch.zizka.junitdiff.ex.JUnitDiffException
import ch.zizka.junitdiff.model.AggregatedData
import cz.dynawest.xslt.XsltTransformer
import org.apache.commons.io.input.ReaderInputStream
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.commons.text.StringEscapeUtils
import java.io.*
import javax.xml.transform.TransformerException

/**
 *
 * @author Ondrej Zizka
 */
object XmlExporter {
    private const val XSL_TEMPLATE_PATH = "/JUnitDiff-to-HTML.xsl"

    /**
     * Exports given matrix to the given file, as a JUnit-like XML.
     * Intentionally written using appender for the sake of speed.
     */
    @Throws(JUnitDiffException::class)
    fun exportToHtmlFile(aggData: AggregatedData, fout: File?, title: String?) {
        //exportToXML( atr, new PrintStream( fout, "uft8" ) );
        val baos = ByteArrayOutputStream()
        exportToXML(aggData, PrintStream(baos))
        try {
            val ris = ReaderInputStream(StringReader(baos.toString("utf8")), "utf8")
            val xslTemplate = XmlExporter::class.java.getResourceAsStream(XSL_TEMPLATE_PATH)
            val params: MutableMap<String?, Any?> = HashMap()
            if (title != null) params["title"] = title
            XsltTransformer.transform(ris, xslTemplate, fout, params)
        } catch (ex: TransformerException) {
            throw JUnitDiffException("Error when creating HTML file: " + ex.message, ex)
        } catch (ex: UnsupportedEncodingException) {
            throw RuntimeException(ex)
        }
    }

    /**
     * Exports given matrix to the given file, as a JUnit-like XML.
     */
    @Throws(FileNotFoundException::class)
    fun exportToXML(aggData: AggregatedData, fout: File) {
        exportToXML(aggData, PrintStream(fout))
    }

    /**
     * Exports given matrix to the given PrintStream, as a JUnit-like XML.
     * TODO: SAX-like output?
     */
    private fun exportToXML(aggData: AggregatedData, out: PrintStream) {
        out.println("<aggregate>")
        val atr = aggData.aggregatedTestResults
        // TODO: Move groups to AggregatedData? But that would be redundant... we have it in attr's map.


        // Groups.
        out.println("\t<groups>")
        atr.shortenGroupsNames()
        val groups = atr.groups

        /*for (String group : groups) {
				out.append("\t\t<group name=\"").append(x( group )).append("\" path=\"").append(x( group )).append("\"/>\n");
		}*/
        for (g in groups) {
            out.append("\t\t<group name=\"").append(x(g.name))
                .append("\" path=\"").append(x(g.path))
                .append("\" id=\"").append(x(g.id.toString()))
                .append("\"/>\n")
        }
        out.println("\t</groups>\n")


        // Test cases.
        for (testcase in atr.testCases) {
            out.append("\t<testcase classname=\"").append(x(testcase.className))
                .append("\" name=\"").append(x(testcase.name)).append("\">\n")
            for (testRun in testcase.testRuns) {
                out.append("\t\t<testrun result=\"").append(x(testRun.result?.name))
                    .append("\" time=\"").append(x(testRun.time))
                    .append("\" group=\"").append(x(testRun.groupID))
                    .append("\">\n")

                // <failure message="Exception message" type="java.lang.Exception">
                if (null != testRun.failure) {
                    out.append("\t\t\t<failure message=\"").append(x(testRun.failure!!.message))
                        .append("\" type=\"").append(x(testRun.failure!!.type)).append("\">\n")
                    out.print(x(testRun.failure!!.trace))
                    out.println("</failure>")
                }
                out.println("\t\t</testrun>")
            }
            out.println("\t</testcase>")
        }


        // Test suites.
        // TODO:  Rename the top-level element.
        // TODO: Add TestSuite reference to TestInfo and delegate TestInfo.getOrigin() to that.
        // TODO: Perhaps the test cases could be moved to the TestSuite, after all.
        out.println("\t<testsuites>")
        for (ts in aggData.testSuites) {
            out.append("\t\t<testsuite group=\"").append(x(ts.group))
                .append("\" name=\"").append(x(ts.className))
                .append("\" origin=\"").append(x(ts.origin))
                .append("\">\n")
            out.append("\t\t<system-out><![CDATA[").append(ts.stdOut).append("]]></system-out>\n")
            out.append("\t\t<system-err><![CDATA[").append(ts.stdErr).append("]]></system-err>\n")
            out.println("\t\t</testsuite>")
        }
        out.println("\t</testsuites>\n")
        out.println("</aggregate>")
    }

    /** Helper - XML escape.  */
    private fun x(s: String?): String {
        if (s == null) return ""
        return StringEscapeUtils.escapeXml11(s)
        //return StringUtils.replaceEach(s, arrayOf("<", "&"), arrayOf("&lt;", "&amp;"))
    }
}
