package ch.zizka.junitdiff.model

/**
 * Super-group - to cluster multiple groups.
 * Used to bind multiple groups (collumns) visually.
 *
 * @author Ondrej Zizka
 */
@Deprecated("Probably not used anywhere.")
class SuperGroup( // </editor-fold>
    val name: String
) {

    // <editor-fold defaultstate="collapsed" desc="get/set">
    var color: String? = null
    private val groups: List<GroupPrecept?> = ArrayList()
}
