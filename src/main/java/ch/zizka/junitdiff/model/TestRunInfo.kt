package ch.zizka.junitdiff.model

/**
 * Test info - name, result, reason.
 *
 * @author Ondrej Zizka
 */
class TestRunInfo(
    var classname: String?,
    var name: String?,
    var result: Result?,
    var time: String?,
) {
    enum class Result {
        OK, FAIL, ERROR, SKIPPED
    }

    var failure: Failure? = null
    var group: IGroup? = null

    val fullName: String
        get() = "$classname.$name"

    val groupID: String
        get() = group?.id.toString()

    override fun toString(): String {
        return "TestInfo{" + "classname=" + classname + "name=" + name + "result=" + result + "time=" + time + "failure=" + failure + '}'
    }
}
