package ch.zizka.junitdiff.model

import org.apache.commons.lang3.StringUtils

/**
 * Group of groups, used for logical separation of test runs.
 * Can serve e.g. for coloring of columns in HTML output.
 *
 * @author jbrazdil
 */
class Groups {

    private val groups: MutableMap<String, Group> = HashMap()
    private var id = 1

    fun getGroup(path: String): IGroup {
        val group: Group
        if (groups.containsKey(path)) {
            group = groups[path]!!
        } else {
            group = Group(path, id)
            id++
            groups[path] = group
        }
        return group
    }

    /**
     * Set the differing parts of group paths as name.
     * {abcfoo1234, abcbarbar1234} => {foo, barbar}
     */
    fun shortenNames() {
        val allPaths = arrayOfNulls<String>(groups.size)
        val allPathsRev = arrayOfNulls<String>(groups.size)
        var i = 0
        for (g in groups.values) {
            allPaths[i] = g.path
            allPathsRev[i] = StringUtils.reverse(g.path)
            i++
        }

        // Get the common prefix and sufix
        val commonPrefix = StringUtils.getCommonPrefix(*allPaths) // abc
        val commonSuffix = StringUtils.getCommonPrefix(*allPathsRev) // 1234

        // Get the common prefix and sufix lengths
        val prefixLength = commonPrefix.length // 3
        val suffixLength = commonSuffix.length // 4

        // Cut off the common prefix and sufix
        if (prefixLength + suffixLength == 0) return
        for (g in groups.values) {
            val nameLength = g.path.length // abcfoo1234 = 10; abcbarbar1234 = 13
            //  abc|foo|1234      ->  foo          3             10  - 4 = 6
            //  abc|bar bar|1234  ->  barbar       3             13  - 4 = 9
            //  012|345|678|9012
            var end = nameLength - suffixLength
            if (prefixLength >= end) end = nameLength
            g.name = g.path.substring(prefixLength, end)
        }
    }

    private class Group(
        override val path: String,
        override val id: Int
    ) : IGroup
    {
        override var name: String = path
        override fun toString() = "Groups{name=$name}"
    }
}