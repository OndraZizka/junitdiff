package ch.zizka.junitdiff.model

import java.io.Serializable

/**
 *
 * @author Ondrej Zizka
 */
class TestSuite(// <editor-fold defaultstate="collapsed" desc="get set">
    /* 
          * <testsuite errors="0" failures="0" 
          * hostname="mm18-3.mm.atl2.redhat.com" 
          * name="org.hibernate.test.annotations.access.AccessTest" 
          * tests="6" 
          * time="60.045" 
          * timestamp="2010-11-16T00:22:54">
          */
    var className: String?, val testRunResultsList: TestRunResultsList, var stdErr: String, var stdOut: String
) : Serializable {
    // Transfer group / origin to the collection of testRunResults, so they know.
    /**
     * Where did this results collection come from (e.g. a filename).
     */
    var origin: String? = null
        set(origin) {
            field = origin
            // Transfer group / origin to the collection of testRunResults, so they know.
            testRunResultsList.origin = origin
        }

    // Transfer group / origin to the collection of testRunResults, so they know.
    var group: String? = null
        set(group) {
            field = group
            // Transfer group / origin to the collection of testRunResults, so they know.
            testRunResultsList.group = group
        }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="overrides">
    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        if (javaClass != obj.javaClass) {
            return false
        }
        val other = obj as TestSuite
        return if (if (className == null) other.className != null else className != other.className) {
            false
        } else true
    }

    override fun hashCode(): Int {
        var hash = 3
        hash = 97 * hash + if (className != null) className.hashCode() else 0
        return hash
    }

    // </editor-fold>
    val fullName: String
        get() = group + "|" + className
}
