package ch.zizka.junitdiff.model

import java.io.Serializable

/**
 * A model for such structure:
 *
 * <testsuite errors="0" failures="0"
 *   hostname="mm18-3.mm.atl2.redhat.com"
 *   name="org.hibernate.test.annotations.access.AccessTest"
 *   tests="6"
 *   time="60.045"
 *   timestamp="2010-11-16T00:22:54">

 * @author Ondrej Zizka
 */
class TestSuite(
    var className: String?,
    val testRunResultsList: TestRunResultsList,
    var stdErr: String,
    var stdOut: String
)
    : Serializable
{

    /**
     * Where did this results collection come from (e.g. a filename).
     */
    var origin: String? = null
        set(origin) {
            field = origin
            // Transfer group / origin to the collection of testRunResults, so they know.
            testRunResultsList.origin = origin
        }

    var group: String? = null
        set(group) {
            field = group
            // Transfer group / origin to the collection of testRunResults, so they know.
            testRunResultsList.group = group
        }


    override fun equals(obj: Any?): Boolean {
        if (javaClass != obj?.javaClass) return false

        val other = obj as TestSuite
        return if (className == null) other.className == null else className == other.className
    }

    override fun hashCode() = className?.hashCode() ?: 0

    val fullName: String
        get() = "$group|$className"
}
