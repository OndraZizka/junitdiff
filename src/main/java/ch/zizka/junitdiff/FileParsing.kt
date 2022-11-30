package ch.zizka.junitdiff

import ch.zizka.junitdiff.ex.JUnitDiffException
import ch.zizka.junitdiff.model.*
import org.apache.commons.io.FileUtils
import org.jdom.Document
import org.jdom.Element
import org.jdom.JDOMException
import org.jdom.input.SAXBuilder
import org.jdom.xpath.XPath
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 *
 * @author Ondrej Zizka
 */
object FileParsing {

    private val log = LoggerFactory.getLogger(FileParsing::class.java)
    private const val MAX_LIST_FILE_SIZE_KB = 512

    /**
     * Get the test result lists - one for each XML file or a ".txt" list of XML files.
     */
    @Throws(JUnitDiffException::class)
    fun getSeparatedResultsLists(reportFiles: List<File>): List<TestSuite> {
        val reportsLists: MutableList<TestSuite> = LinkedList()
        val errors: MutableList<JUnitDiffException> = ArrayList()
        for (file in reportFiles) {
            if (!file.isFile) {
                log.warn("  Not a regular file: " + file.path)
                continue
            }
            if (file.length() == 0L) {
                log.warn("  File is empty: " + file.path)
                continue
            }
            try {
                val ts = parseFile(file)
                reportsLists.add(ts)
            } catch (ex: JUnitDiffException) {
                val msg = "  Error processing '" + file.path + "': " + ex.message
                log.error(msg)
                errors.add(JUnitDiffException(msg, ex))
                continue
            }
        }
        if (errors.isNotEmpty()) {
            throw JUnitDiffException("${errors.size} errors:" + errors.map { it.message }.joinToString(prefix = "\n") )
        }
        return reportsLists
    }

    /**
     * Parses test results from a file.
     *
     * @param file May be a text file with a list of files to parse, or a JUnit .xml report.
     */
    @Throws(JUnitDiffException::class)
    private fun parseFile(file: File?): TestSuite {
        // Determine whether a file is a list of .xml files or a .xml file (report)
        val isXml = file!!.name.endsWith(".xml")


        // Try to parse as XML.
        return try {
            tryParsingAsXml(file)
        } catch (ex: JDOMException) {
            // 		 *  @deprecated  List expanding is handled in FileParsing#preprocessPaths();
            //log.debug("  Failed parsing as XML JUnit test report: "+file.getPath());
            throw JUnitDiffException("  Failed parsing '" + file.path + "' as XML JUnit test report: " + ex.message)
        } catch (ex: IOException) {
            throw JUnitDiffException("  Error reading from file '" + file.path + "': " + ex.message)
        }


        // XML parsing failed, so we try to treat it as a list of files.
        //TestResultsList ar = parseAsListOfFiles( file );
        //return ar;
    }

    /**
     * Assumes the file contains a list of paths to JUnit XML reports.
     * Returns test data aggregated from all of them.
     */
    @Deprecated("List expanding is handled in FileParsing#preprocessPaths();")
    @Throws(JUnitDiffException::class)
    private fun parseAsListOfFiles(file: File): TestRunResultsList {
        if (file.length() > MAX_LIST_FILE_SIZE_KB * 1024) log.warn("  File is too big (" + file.length() / 1024 + " kb) : " + file.path)


        // Read lines and treat as XML files.
        val readLines = try { FileUtils.readLines(file) }
            catch (ex: IOException) { throw JUnitDiffException("  Error reading from file '" + file.path + "': " + ex.message) }

        val trls: MutableList<TestRunResultsList> = ArrayList()
        val errors: MutableList<Exception> = ArrayList()
        for (line in readLines) {
            val line2 = line.trim { it <= ' ' }
            try {
                val ts = tryParsingAsXml(File(line2))
                trls.add(ts.testRunResultsList)
            } catch (ex: JDOMException) {
                val msg = "  Error parsing '" + file.path + "': " + ex.message
                log.error(msg)
                errors.add(ex)
                continue
            } catch (ex: IOException) {
                val msg = "  Error reading from file '" + file.path + "': " + ex.message
                log.error(msg)
                errors.add(IOException(msg))
                continue
            }
        }
        if (!errors.isEmpty()) {
            throw JUnitDiffException("${errors.size} errors  when parsing the list of files:" + errors.map { it.message }.joinToString(prefix = "\n") )
        }
        return TestRunResultsList.fromList(trls)
    }

    /**
     * Tries parsing as XML.
     */
    @Throws(JDOMException::class, IOException::class)
    private fun tryParsingAsXml(stream: FileInputStream): TestSuite {
        val builder = SAXBuilder()
        val doc = builder.build(stream)
        return parseJUnitXmlReport(doc)
    }

    @Throws(JDOMException::class, IOException::class)
    private fun tryParsingAsXml(file: File?): TestSuite {
        val builder = SAXBuilder()
        val doc = builder.build(file)
        val parseJUnitXmlReport = parseJUnitXmlReport(doc)
        parseJUnitXmlReport.origin = file!!.path
        return parseJUnitXmlReport
    }

    /**
     * Parses test results from a JUnit .xml report.
     */
    @Throws(JDOMException::class)
    private fun parseJUnitXmlReport(doc: Document): TestSuite {


        // For all testcases...
        val xPath = XPath.newInstance("//testsuite/testcase")
        val testcaseElements: List<Element> = xPath.selectNodes(doc) as List<Element>
        val resultsList = TestRunResultsList()
        for (elm in testcaseElements) {
            val time = elm.getAttributeValue("time")
            val name = elm.getAttributeValue("name")
            val classname = elm.getAttributeValue("classname")
            val info = TestRunInfo(classname, name, TestRunInfo.Result.OK, time)


            // Failure.
            var child = elm.getChild("failure")
            if (null != child) {
                val message = child.getAttributeValue("message")
                val type = child.getAttributeValue("type")
                var trace = child.text
                val fail = Failure(message, type, trace)
                info.result = TestRunInfo.Result.FAIL
                info.failure = fail
            }

            // Error.
            child = elm.getChild("error")
            if (null != child) {
                val message = child.getAttributeValue("message")
                val type = child.getAttributeValue("type")
                var trace = child.text
                val fail = Failure(message, type, trace)
                info.result = TestRunInfo.Result.ERROR
                info.failure = fail
            }

            // Skipped.
            child = elm.getChild("skipped")
            if (null != child) {
                info.result = TestRunInfo.Result.SKIPPED
            }
            resultsList.add(info)
        }


        /*
       * <testsuite errors="0" failures="0" 
       *     hostname="mm18-3.mm.atl2.redhat.com"
       *     name="org.hibernate.test.annotations.access.AccessTest"
       *     tests="6"
       *     time="60.045"
       *     timestamp="2010-11-16T00:22:54">
       */

        // System output
        val systemOut = XPath.selectSingleNode(doc, "string(//testsuite/system-out)") as String

        // System error
        val systemErr = XPath.selectSingleNode(doc, "string(//testsuite/system-err)") as String

        // Class name
        val tsName = XPath.selectSingleNode(doc, "string(//testsuite/@name)") as String


        // Testsuite
        return TestSuite(tsName, resultsList, systemOut, systemErr)
    }
}
