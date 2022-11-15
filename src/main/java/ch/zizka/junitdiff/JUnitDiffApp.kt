package ch.zizka.junitdiff

import ch.zizka.junitdiff.ex.JUnitDiffException
import ch.zizka.junitdiff.export.XmlExporter
import ch.zizka.junitdiff.model.AggregatedData
import ch.zizka.junitdiff.model.TestSuite
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * Useful comment.
 */
class JUnitDiffApp {
    /**
     * runApp()
     */
    private fun runApp(paths: MutableList<String>, outPath: String, htmlOutput: Boolean, toStdOut: Boolean, title: String?) {
        val reportFiles: MutableList<File?> = ArrayList(paths.size)

        // Handle URLs
        InputPreparation.handleURLs(paths)

        // Check files...
        for (path in paths) {
            val reportFile = File(path)
            require(reportFile.exists()) { "  File " + reportFile.path + " does not exist." }
            reportFiles.add(reportFile)
        }

        // Preprocess files - scan directories, expand .txt lists of paths.
        //reportFiles = InputPreparation.preprocessPaths( reportFiles );
        // No no. That would stack everything on a pile. Instead,


        // CONSIDER:  Hide atr under aggregatedData, or keep it separated?  TestSuite lists, + map group+name -> testsuite?
        // The top-level result structure.
        val aggregatedData = AggregatedData()


        // Do it arg-by-arg to keep groups.
        for (file in reportFiles) {
            val groupName = file!!.path
            val inList = listOf(file)
            val filesToProcess = InputPreparation.preprocessPaths(inList)
            if (filesToProcess.isEmpty()) {
                log.warn("No report files to process from source '$groupName'.")
                continue
            }
            val testSuites = processGroup(groupName, filesToProcess)
            if (testSuites.isEmpty()) {
                log.warn("No testsuites to process from source '$groupName'.")
                continue
            }


            // Add to the top-level result structure.

            // Aggregate the results lists - "create columns".
            log.info("Aggregating test results - group: " + groupName + " - " + testSuites.size + " test suites.")
            try {
                aggregatedData.mergeTestSuites(testSuites, groupName)
            } catch (ex: Exception) {
                log.error("Error when aggregating: " + ex.message, ex)
                System.exit(3)
            }
        }
        if (aggregatedData.testSuites.size == 0) {
            log.error("No results to process.")
            System.exit(4)
        }


        // Export the aggregated matrix to a file.
        // TODO: Use a Writer.
        log.info("Exporting to $outPath")
        val outFile = File(outPath)
        if (!htmlOutput) {
            // XML
            try {
                XmlExporter.exportToXML(aggregatedData, outFile)
            } catch (ex: FileNotFoundException) {
                log.error("Can't write to file '" + outPath + "': " + ex.message)
                System.exit(5)
            }
        } else {
            // HTML
            try {
                XmlExporter.exportToHtmlFile(aggregatedData, outFile, title)
            } catch (ex: JUnitDiffException) {
                log.error(ex.message, ex)
                System.exit(6)
            }
        }

        // Dump to stdout.
        // TODO: Rewrite whole these two parts.
        if (toStdOut) {
            log.debug("Output goes to stdout.")
            var fileReader: FileReader? = null
            try {
                fileReader = FileReader(outPath)
                IOUtils.copy(fileReader, System.out as OutputStream, StandardCharsets.UTF_8)
            } catch (ex: FileNotFoundException) {
                log.error(ex.toString())
            } catch (ex: IOException) {
                log.error(ex.toString())
            } finally {
                if (fileReader != null) {
                    try {
                        fileReader.close()
                    } catch (ex: IOException) {
                        log.error(ex.toString())
                    }
                }
            }
        }
    } // runApp

    companion object {
        private val log = LoggerFactory.getLogger(JUnitDiffApp::class.java)
        private const val DEFAULT_OUT_FILE = "JUnitDiff" // Suffix appened according to output type.

        /**
         * main ()
         */
		@JvmStatic
        fun main(args: Array<String?>) {

            log.debug("Starting JUnitDiff - multiple test runs results comparison.")
            if (args.isEmpty()) {
                //log.info(" Usage: junitdiff  ( dir | TEST-foo.xml | list-of-paths.txt )+");
                println("  Aggregates multiple JUnit XML reports into one comprehensible page.")
                println("  Usage:")
                println("    java -jar JUnitDiff.jar [options] ( dir | TEST-foo.xml | list-of-paths.txt | http://host/reports.zip )+")
                println("")
                println("  Options:")
                println("    -o ('-' | outputPath)   Output file. '-' dumps the result to the stdout. Logging output always goes to the stderr.")
                println("    -xml                    XML output (default is HTML).")
                println("    --title <title>         Title and heading for the HTML report.")
                println("")
                println("  Examples:")
                println("    java -jar JUnitDiff.jar -o - > aggregated-test-report.html")
                println("    java -jar JUnitDiff.jar -xml -o aggregated-test-report.xml")
                System.exit(1)
            }
            var outFile: String? = null
            var title: String? = null
            var htmlOutput = true
            var stdOut = false

            // Process arguments...
            // TODO: Some options data holder class...
            var i = 0
            while (i < args.size) {
                if ("-xml" == args[i]) {
                    htmlOutput = false
                    args[i] = null
                } else if ("-o" == args[i]) {
                    args[i] = null
                    if (args.size > i) {
                        outFile = args[i + 1]
                        args[i + 1] = null
                        i++
                    }
                } else if ("--title" == args[i]) {
                    args[i] = null
                    if (args.size > i) {
                        title = args[i + 1]
                        args[i + 1] = null
                        i++
                    }
                }
                i++
            }
            if ("-" == outFile) {
                stdOut = true
                outFile = null
            }
            if (null == outFile) {
                outFile = DEFAULT_OUT_FILE + if (htmlOutput) ".html" else ".xml"
            }
            JUnitDiffApp().runApp(args.filterNotNull().toMutableList(), outFile, htmlOutput, stdOut, title)
        }

        /**
         *
         * @param groupName  De-facto name of the group.
         * @param reportFiles
         *
         * TODO: Maybe introduce some TestRunGroup class.
         */
        private fun processGroup(groupName: String, reportFiles: List<File>): List<TestSuite> {

            //  Get the test result lists - one for each XML file or a ".txt" list of XML files.
            log.info("Parsing test reports group '" + groupName + "': " + reportFiles!!.size + " files.")

            val testSuites: List<TestSuite>
            testSuites = try {
                FileParsing.getSeparatedResultsLists(reportFiles)
            }
            catch (ex: JUnitDiffException) {
                log.error(ex.message, ex)
                return emptyList<TestSuite>()
            }
            for (testSuite in testSuites) {
                testSuite.group = groupName
            }
            return testSuites
        }
    }
}
