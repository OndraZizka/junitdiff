package ch.zizka.junitdiff.ex

/**
 *
 * @author Ondrej Zizka
 */
class JUnitDiffException : Exception {
    val errors: List<Exception>? = null

    constructor(cause: Throwable?) : super(cause) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
    constructor(message: String?) : super(message) {}
    constructor() {}
    constructor(errors: List<Exception?>?) {}
    constructor(string: String?, errors: List<Exception?>?) : this(string) {}
}
