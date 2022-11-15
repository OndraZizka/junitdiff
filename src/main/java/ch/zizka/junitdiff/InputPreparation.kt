package ch.zizka.junitdiff

import ch.zizka.junitdiff.util.FileUtil
import ch.zizka.junitdiff.util.ZipUtil
import ch.zizka.junitdiff.util.ZipUtil.OverwriteMode
import org.apache.commons.io.DirectoryWalker
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets
import java.util.*

/**
 *
 * @author Ondrej Zizka
 */
object InputPreparation {
    private val log = LoggerFactory.getLogger(InputPreparation::class.java)

    /**
     * Takes a list of paths which may be directories, .txt lists of paths, or JUnit XML reports,
     * and replaces directories with TEST-*.xml's inside,
     * and expands .txt lists of paths.
     */
    fun preprocessPaths(reportFiles: List<File>): List<File> {
        val expandedPaths: MutableList<File> = LinkedList()
        for (path in reportFiles) {
            var path2 = path

            if (path2!!.isFile) {
                if (path2.name.endsWith(".zip")) {
                    try {
                        // Unzip & continue with the dir.
                        path2 = ZipUtil.unzipFileToTempDir(path2, OverwriteMode.DELETE_FIRST)
                    } catch (ex: IOException) {
                        log.error("Can't unzip $path2: $ex")
                    }
                }
                else {
                    if (path2.name.endsWith(".xml")) {
                        expandedPaths.add(path2)
                    } else {
                        expandedPaths.addAll(readListOfPaths(path2))
                    }
                }
            }
            if (path2.isDirectory) {
                expandedPaths.addAll(scanDirForJUnitReports(path2))
                continue
            }
        }
        return expandedPaths
    }

    /**
     * Handle URLs: If the path starts with http://, it downloads the file and unzips if it ends with .zip.
     * Paths will be replaced in-place in the array.
     */
    fun handleURLs(paths: MutableList<String>) {
        for (i in paths.indices) {
            val path = paths[i] ?: continue

            // Only replace URL's.
            if (!path.startsWith("http://")) {
                continue
            }
            try {
                var resultDir: File?
                resultDir = if (path.endsWith(".zip")) {
                    downloadZipAndExtractToTempDir(path)
                } else if (path.endsWith(".xml")) {
                    downloadUrlToTempFile(path)
                } else {
                    log.warn("  URL is not .zip nor .xml - skipping: $path")
                    continue
                }
                paths[i] = resultDir!!.path
            } catch (ex: IOException) {
                log.warn("  Error when processing URL " + path + ": " + ex.message, ex)
            }
        }
    }

    /**
     *
     */
    @Throws(IOException::class)
    private fun downloadZipAndExtractToTempDir(urlStr: String): File? {
        // Download
        val tmpFile = downloadUrlToTempFile(urlStr)

        // Unzip & return the tmp dir.
        val dirWithZipContent = ZipUtil.unzipFileToTempDir(tmpFile, OverwriteMode.DELETE_FIRST)
        tmpFile.delete()
        return dirWithZipContent
    }

    @Throws(IOException::class)
    private fun downloadUrlToTempFile(urlStr: String): File {
        val url = URL(urlStr)

        // Create the directory.
        var path = url.path
        path = StringUtils.strip(path, "\\/")
        path = path.replace('*', '-')
        path = path.replace('?', '-')
        val destZipFile = File(path)
        destZipFile.parentFile.mkdirs()
        val rbc = Channels.newChannel(url.openStream())
        //File tmp = File.createTempFile( "JUnitDiff-tmp-", ".zip" );
        val fos = FileOutputStream(destZipFile)
        fos.channel.transferFrom(rbc, 0, (1 shl 24).toLong())
        rbc.close()
        fos.close()
        return destZipFile
    }

    /**
     * Reads a list of paths from a text file, one per line. Not recursive.
     */
    private fun readListOfPaths(path: File): List<File> {
        return try {
            if (FileUtil.isBinaryFile(path)) {
                log.warn("  Can't read list of paths from a binary file: " + path!!.path)
                return emptyList<File>()
            }
            val lines = FileUtils.readLines(path, StandardCharsets.UTF_8)
            val paths: MutableList<File> = ArrayList(lines.size)
            for (line in lines) {
                val f = File(line)
                if (!f.exists()) {
                    log.warn("  Does not exist: " + f.path)
                } else if (!f.isFile) {
                    log.warn("  Not a regular file: " + f.path)
                } else {
                    paths.add(f)
                }
            }
            paths
        } catch (ex: IOException) {
            log.warn("Error reading " + path!!.path + " : " + ex.message)
            emptyList<File>()
        }
    }

    /**
     * Scans a directory for JUnit test reports.
     */
    private fun scanDirForJUnitReports(path: File?): List<File> {
        val rigidFilter = FileFilterUtils.or(
            FileFilterUtils.directoryFileFilter(),
            FileFilterUtils.and( // Perhaps make this an option - some other filters like content-based etc.
                FileFilterUtils.suffixFileFilter(".xml"),
                UpperCasePrefixFilter.INSTANCE,
                FileFilterUtils.magicNumberFileFilter("<?xml")
            ) // Maybe we could simply scan for TEST-*.xml names.
        )


        // Walk trough the dir tree...
        return try {
            val resultList = LinkedList<File?>()

            object : DirectoryWalker<File>(rigidFilter, -1) {
                override fun handleFile(file: File, depth: Int, results: MutableCollection<File>) {
                    //		log.info("  Handling file: " + file.getPath());///
                    results.add(file)
                }

                override fun handleDirectory(directory: File, depth: Int, results: Collection<File>): Boolean {
                    //log.info( "  Handling dir: "+directory.getPath() );
                    return true
                }

                fun doWalk(list: LinkedList<File?>) {
                    this.walk(path, list)
                }
            }.doWalk(resultList)

            resultList.filterNotNull()
        } catch (ex: IOException) {
            emptyList<File>()
        }
    }

    internal enum class UpperCasePrefixFilter : IOFileFilter {
        INSTANCE;

        override fun accept(file: File): Boolean {
            return file.name.matches("^\\p{javaUpperCase}+.*$".toRegex())
        }

        override fun accept(file: File, s: String): Boolean {
            return this.accept(File(file, s))
        }
    }
}