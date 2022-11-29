package ch.zizka.junitdiff.model

import org.slf4j.LoggerFactory
import java.util.*

/**
 * Aggregated results:  A matrix of joined test results lists, with one "column" for each list.
 *
 * @author Ondrej Zizka
 */
class AggregatedTestResults {
    // ClassName -> test results list.
    private val byTestClassName: MutableMap<String?, TestClassInfo> = LinkedHashMap()

    // Groups.
    val groups: MutableList<IGroup> = ArrayList()
    private val groupsFactory = Groups()

    fun mergeTestSuites(testSuites: List<TestSuite>, groupName: String): List<TestRunResultsList> {
        val reportsLists: MutableList<TestRunResultsList> = ArrayList(testSuites.size)
        for (testSuite in testSuites) {
            reportsLists.add(testSuite.testRunResultsList)
        }
        merge(reportsLists, groupName)
        return reportsLists
    }

    /**
     * Processes given list of test results collections and aggregates them to a matrix -
     * tests will be grouped by full name; each item of the list will create one column
     * if it contains a test of the respective name.
     *
     * run1   run2   run3
     * testMyTest      OK     FAIL   OK
     * testOtherTest   FAIL   OK     OK
     *
     * @return  newly created AggregatedTestResults.
     */
    @JvmOverloads
    fun merge(reportsLists: List<TestRunResultsList>, groupName: String = generateGroupName()) {
        val trace = log.isTraceEnabled
        val group = groupsFactory.getGroup(groupName)
        groups.add(group)

        // For all reports...
        for (testResultsList in reportsLists) {
            if (trace) log.trace("  Aggregating {}", testResultsList) ///

            // Add all their tests, grouped by full name, to the aggregated matrix.
            for (curTest in testResultsList.testResults) {
                var testclass = findTestsByClassName(curTest.classname)
                if (testclass == null) {
                    testclass = TestClassInfo(curTest.classname)
                    add(testclass)
                }
                curTest.group = group
                testclass.add(curTest)
            }
        }
    } // merge()

    // Group name generation.
    private var nextGroupNum = 1
    private fun generateGroupName(): String {
        return "Group" + nextGroupNum++
    }

    fun add(testClass: TestClassInfo): Boolean {
        byTestClassName[testClass.className] = testClass
        return true
    }

    fun containsByClassName(className: String?): Boolean {
        return byTestClassName.containsKey(className)
    }

    val testCases: List<TestCaseInfo>
        get() {
            val ret: MutableList<TestCaseInfo> = ArrayList()
            for (testclass in byTestClassName.values) {
                ret.addAll(testclass.testCasesWithPseudoRuns)
            }
            return ret
        }

    fun getGroupsUnmodifiable(): List<IGroup?> {
        return Collections.unmodifiableList(groups)
    }

    /**
     * Finds a test by it's class name, i.e. "org.jboss.ClassName".
     * @param className
     * @return
     */
    fun findTestsByClassName(className: String?): TestClassInfo? {
        return byTestClassName[className]
    }

    fun shortenGroupsNames() {
        groupsFactory.shortenNames()
    }

    companion object {
        private val log = LoggerFactory.getLogger(AggregatedTestResults::class.java)
    }
}
