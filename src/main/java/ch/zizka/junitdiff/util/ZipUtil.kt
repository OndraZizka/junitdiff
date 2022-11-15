package ch.zizka.junitdiff.util

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.file.Files
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Should I use Commons Compress instead?
 *
 * @author Ondrej Zizka
 */
object ZipUtil {
    private val log = LoggerFactory.getLogger(ZipUtil::class.java)
    private const val BUFFER_SIZE = 1024 * 32

    /**
     * Unzips a file to a temporary dir.
     */
    @Throws(IOException::class)
    fun unzipFileToTempDir(zipFile: File?, mode: OverwriteMode?): File {

        // Try to keep the original path in the new path for the group naming purposes.
        var path = zipFile!!.path
        // foo/bar.zip -> foo/bar/
        path = if (path.endsWith(".zip")) StringUtils.removeEndIgnoreCase(path, ".zip") else "$path-"
        var tmpDir = File(path)
        if (tmpDir.parentFile.canWrite()) {
            tmpDir.mkdir()
        } else {
            tmpDir = File.createTempFile("JUnitDiff-", "")
            tmpDir.delete()
        }
        tmpDir.deleteOnExit()
        unzipFileToDir(zipFile, tmpDir, TEST_XML_FILTER, mode)
        return tmpDir
    }

    /**
     * Unzip method with overwrite behavior option.
     * @param mode ONLY_NEW, WRITE_INTO or DELETE_FIRST.
     * If ONLY_NEW and the dir exists, returns false and does nothing.
     * @return true if the zip was unzipped to the given dir.
     */
    @Throws(IOException::class)
    fun unzipFileToDir(zip: File?, intoDir: File, mode: OverwriteMode): Boolean {
        if (intoDir.exists()) {
            if (mode == OverwriteMode.ONLY_NEW) {
                return false
            } else if (mode == OverwriteMode.DELETE_FIRST) {
                FileUtils.deleteDirectory(intoDir)
            }
        }
        unzipFileToDir(zip, intoDir)
        return true
    }
    /**
     * Unzip method which can filter files to extract.
     */
    /**
     * Unzip method.
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun unzipFileToDir(zipFile: File?, intoDir: File?, fileFilter: FileFilter? = null as FileFilter?, mode: OverwriteMode? = OverwriteMode.WRITE_INTO) {
        val zip = ZipFile(zipFile)
        val entries = zip.entries() as Enumeration<ZipEntry>
        val buf = ByteArray(BUFFER_SIZE)
        try {
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (entry.isDirectory) continue
                if (entry.name.contains("..")) continue
                if (fileFilter != null && !fileFilter.accept(File(entry.name))) continue
                log.trace("  Extracting: $entry")
                val f = File(intoDir, entry.name)
                if (!f.exists()) {
                    f.parentFile.mkdirs()
                    f.createNewFile()
                }
                val `is` = zip.getInputStream(entry)
                val os = Files.newOutputStream(f.toPath())
                var r: Int
                while (`is`.read(buf).also { r = it } != -1) {
                    os.write(buf, 0, r)
                }
                os.close()
                `is`.close()
            }
        } catch (ex: Exception) {
            log.error(" Error when unzipping " + zipFile!!.path + ": " + ex.message)
        }
    }

    /**
     * Filters only files TEST-*.xml
     */
    private val TEST_XML_FILTER = FileFilterUtils.and(
        FileFilterUtils.prefixFileFilter("TEST-"),
        FileFilterUtils.suffixFileFilter(".xml")
    )

    enum class OverwriteMode {
        ONLY_NEW, WRITE_INTO, DELETE_FIRST
    }
}
