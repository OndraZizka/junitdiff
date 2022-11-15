package ch.zizka.junitdiff.model

import java.io.Serializable
import java.util.*

/**
 *
 * @author Ondrej Zizka
 */
class AggregatedData : Serializable {
    val aggregatedTestResults = AggregatedTestResults()
    val testSuites: MutableList<TestSuite> = LinkedList()

    // map "groupname|test-class name" -> testsuite?
    private val testSuiteByGroupAndClassName: MutableMap<String, TestSuite?> = TreeMap()

    /**
     *
     */
    fun mergeTestSuites(testSuites: List<TestSuite>, groupName: String) {
        this.testSuites.addAll(testSuites)
        for (testSuite in testSuites) {
            testSuiteByGroupAndClassName[testSuite.fullName] = testSuite
        }
        aggregatedTestResults.mergeTestSuites(testSuites, groupName)
    }

}
