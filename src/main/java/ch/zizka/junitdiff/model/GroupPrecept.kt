package ch.zizka.junitdiff.model

/**
 * Information about group - built from the app's params.
 *
 * @author Ondrej Zizka
 */
class GroupPrecept(var path: String) {
    var name: String? = null

    // <editor-fold defaultstate="collapsed" desc="get/set">
    var isBorder = false
    var supGroup: SuperGroup? = null

    // </editor-fold>
    override fun toString(): String {
        return "GroupPrecept{name=$name}"
    }
}
