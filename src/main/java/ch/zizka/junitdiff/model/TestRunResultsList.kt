package ch.zizka.junitdiff.model

import java.io.Serializable

/**
 * A list with tests results data - name, result (OK | FAIL | ERROR), reason.
 *
 * @author Ondrej Zizka
 */
class TestRunResultsList(
    var testResults: MutableList<TestRunInfo> = mutableListOf()
) : Serializable
{
    /**
     * Where did this results collection come from (e.g. a filename).
     */
    var origin: String? = null

    var group: String? = null

    // <editor-fold defaultstate="collapsed" desc="List delegates">
    val isEmpty: Boolean
        get() = testResults.isEmpty()

    operator fun get(index: Int): TestRunInfo {
        return testResults[index]
    }

    operator fun contains(o: Any?): Boolean {
        return testResults.contains(o)
    }

    fun addAll(c: Collection<TestRunInfo>?): Boolean {
        return testResults.addAll(c!!)
    }

    fun add(e: TestRunInfo): Boolean {
        return testResults.add(e)
    } // </editor-fold>

    override fun toString(): String {
        return "TestResultsList{ [" + testResults.size + "], origin: " + origin + '}'
    }

    companion object {
        /**
         * Concatenates the given list of other instances.
         */
        fun fromList(trls: List<TestRunResultsList?>): TestRunResultsList {

            // Count the total size...
            var totalTests = 0
            for (trl in trls) {
                totalTests += trl!!.testResults.size
            }

            // Concatenate multiple report files to one.
            val results: MutableList<TestRunInfo> = ArrayList(totalTests)
            for (trl in trls) {
                results.addAll(trl!!.testResults)
            }
            return TestRunResultsList(results)
        }
    }
}
