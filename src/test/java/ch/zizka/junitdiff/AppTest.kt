package ch.zizka.junitdiff

import ch.zizka.junitdiff.JUnitDiffApp.Companion.main
import junit.framework.TestCase
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

/**
 * Unit test for simple App.
 *
 * Create the test case.
 * @param testName name of the test case
 */
class AppTest (testName: String) : TestCase(testName) {

    fun testHtmlOutput_hibernateDataset() {

        val dataDir = System.getProperty("junitdiff.test.data.dir")
        val outDir = Path.of("testOutput/", this.name)
        val outFilePath = outDir.resolve("output.html")
        val outFile = outFilePath.toFile()
        outFile.parentFile.mkdirs()

        main(
            arrayOf(
                "$dataDir/hibernate-run1",
                "$dataDir/hibernate-run2",
                "$dataDir/hibernate-run3",
                "$dataDir/hibernate-run4",
                "-o", outFilePath.pathString
            )
        )

        assertTrue(outFilePath.exists())
        assertTrue(outDir.resolve("functions.js").exists())
    }


    fun testXmlOutput_hibernateDataset() {

        val dataDir = System.getProperty("junitdiff.test.data.dir")
        val outDir = Path.of("testOutput/", this.name)
        val outFilePath = outDir.resolve("aggregatedReport.xml")

        outDir.toFile().mkdirs()

        main(
            arrayOf(
                "$dataDir/hibernate-run1",
                "$dataDir/hibernate-run2",
                "$dataDir/hibernate-run3",
                "$dataDir/hibernate-run4",
                "-o", outFilePath.pathString, "-xml"
            )
        )
        assertTrue(outFilePath.exists())
    }

}