package ch.zizka.junitdiff

import ch.zizka.junitdiff.JUnitDiffApp.Companion.main
import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestSuite
import java.io.File

/**
 * Unit test for simple App.
 *
 * Create the test case.
 * @param testName name of the test case
 */
class AppTest (testName: String) : TestCase(testName) {

    fun testHibernateRuns_HtmlOutput() {

        val dataDir = System.getProperty("junitdiff.test.data.dir")
        val outPath = "target/test-tmp/" + this.name + "/output.html"
        val outFile = File(outPath)
        outFile.parentFile.mkdirs()

        main(
            arrayOf(
                "$dataDir/hibernate-run1",
                "$dataDir/hibernate-run2",
                "$dataDir/hibernate-run3",
                "$dataDir/hibernate-run4",
                "-o", outPath
            )
        )

        assertTrue(outFile.exists())
    }

    /**
     * XML output.
     */
    fun testHibernateRunsXML() {

        val dataDir = System.getProperty("junitdiff.test.data.dir")
        val outPath = "target/test-tmp/" + this.name + "/output.xml"
        val outFile = File(outPath)
        outFile.parentFile.mkdirs()
        try {
            main(
                arrayOf(
                    "$dataDir/hibernate-run1",
                    "$dataDir/hibernate-run2",
                    "$dataDir/hibernate-run3",
                    "$dataDir/hibernate-run4",
                    "-o", outPath, "-xml"
                )
            )
        } catch (t: Throwable) {
            kotlin.test.fail(t.message)
        }
        assertTrue(outFile.exists())
    }

    companion object {
        /**
         * @return the suite of tests being tested
         */
        fun suite(): Test {
            return TestSuite(AppTest::class.java)
        }
    }
}