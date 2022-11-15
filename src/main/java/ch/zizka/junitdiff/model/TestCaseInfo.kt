package ch.zizka.junitdiff.model

import java.util.*

/**
 * One test with multiple test infos - one for each "column" (test run)
 *
 * @author Ondrej Zizka
 */
class TestCaseInfo {
    var className: String? = null
    var name: String?
    private var testInfos: MutableList<TestRunInfo> = ArrayList()

    constructor(className: String?, name: String?) {
        this.name = name
        this.className = className
    }

    constructor(name: String?, testInfos: MutableList<TestRunInfo>) {
        this.name = name
        this.testInfos = testInfos
    }

    constructor(test: TestRunInfo) {
        name = test.name
        className = test.classname
    }

    internal constructor(testCase: TestCaseInfo) {
        className = testCase.className
        name = testCase.name
        testInfos.addAll(testCase.testInfos)
    }

    // <editor-fold defaultstate="collapsed" desc="List overrides">
    fun size() = testInfos.size

    val isEmpty: Boolean
        get() = testInfos.isEmpty()

    operator fun get(index: Int) = testInfos[index]

    fun addAll(c: Collection<TestRunInfo>) = testInfos.addAll(c)

    fun add(e: TestRunInfo) = testInfos.add(e)
    // </editor-fold>

    val testRuns: List<TestRunInfo>
        get() = Collections.unmodifiableList(testInfos)

    val fullName: String?
        get() = if (className == null) name else "$className.$name"
}
