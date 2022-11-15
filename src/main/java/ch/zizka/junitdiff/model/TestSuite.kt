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
            testRunResultsList.origin = origin
        }

    var group: String? = null
        set(group) {
            field = group
            testRunResultsList.group = group
        }


    override fun equals(other: Any?) = other is TestSuite && other.className == this.className
    override fun hashCode() = className?.hashCode() ?: 0

    val fullName: String
        get() = "$group|$className"
}
