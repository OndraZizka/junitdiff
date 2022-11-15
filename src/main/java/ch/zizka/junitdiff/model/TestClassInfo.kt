package ch.zizka.junitdiff.model

/**
 *
 * @author Honza Brázdil <jbrazdil></jbrazdil>@redhat.com>
 */
class TestClassInfo(
    val className: String?
) {
    private val testsByTestName: MutableMap<String?, TestCaseInfo> = HashMap()
    private val classRuns: MutableList<TestRunInfo> = ArrayList()
    private fun setTests(tests: List<TestCaseInfo>) {
        testsByTestName.clear()
        for (test in tests) {
            testsByTestName[test.name] = test
        }
    }

    fun add(testRun: TestRunInfo) {
        // FIXME hard to figure out what is the reason of this call
        if (testRun.classname != null && testRun.classname == testRun.name) {
            classRuns.add(testRun)
            return
        }
        var testcase = testsByTestName[testRun.name]
        if (testcase == null) {
            testcase = TestCaseInfo(testRun)
            addTestCase(testcase)
        }
        testcase.add(testRun)
    }


    /**
     * Returns list of testCases with guessed skipped and failed runs.
     *
     * If whole test class is skiped, or it's initialization fails,
     * JUnit generates testcase element which have classname == name.
     *
     * This method tries to generate for such testcase new testcases (runs)
     * (based on other runs of same class) which have result set according to the wohole class result.
     */
    val testCasesWithPseudoRuns: List<TestCaseInfo>
        get() {
            val testcases: MutableList<TestCaseInfo> = ArrayList()
            for (tc in testsByTestName.values) {
                val testcase = TestCaseInfo(tc)
                for (classRun in classRuns) {
                    val pseudoTestrun = TestRunInfo(className, testcase.name, classRun.result, classRun.time)
                    pseudoTestrun.failure = classRun.failure
                    pseudoTestrun.group = classRun.group
                    testcase.add(pseudoTestrun)
                }
                testcases.add(testcase)
            }
            return testcases
        }

    private fun addTestCase(testcase: TestCaseInfo) {
        testsByTestName[testcase.name] = testcase
    }
}