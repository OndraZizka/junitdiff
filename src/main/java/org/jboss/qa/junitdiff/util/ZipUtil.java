package org.jboss.qa.junitdiff.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Should I use Commons Compress instead?
 * 
 *  @author Ondrej Zizka
 */
public class ZipUtil {
    private static final Logger log = LoggerFactory.getLogger( ZipUtil.class );
    
    private static final int BUFFER_SIZE = 1024 * 32;
    
    
    /**
     *   Unzips a file to a temporary dir.
     */
    public static File unzipFileToTempDir( File zipFile ) throws IOException {

		String path = zipFile.getPath();
        // foo/bar.zip -> foo/bar/
		path = path.endsWith(".zip")
            ?	StringUtils.removeEndIgnoreCase( path, ".zip")
            : path + "-";

		// Try to keep the original path in the new path for the group naming purposes.
		//File tmpDir = File.createTempFile( "JUnitDiff-", "");
		//tmpDir.delete();

		File tmpDir = new File(path);
		tmpDir.mkdir();
		tmpDir.deleteOnExit();

        //unzipFileToDir( zipFile, tmpDir, TEST_XML_FILTER );
		
        byte data[] = new byte[BUFFER_SIZE];
		try {
			ZipFile zipfile = new ZipFile(zipFile);
			Enumeration e = zipfile.entries();
			while( e.hasMoreElements() ) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				if( entry.isDirectory() )	continue;
				if( entry.getName().contains("..") )  continue;

				if( ! TEST_XML_FILTER.accept( new File( entry.getName() )) ) continue;

				log.trace("  Extracting: " + entry);
				BufferedInputStream is = new BufferedInputStream(zipfile.getInputStream(entry));

				File destFile = new File(tmpDir, entry.getName());
				destFile.getParentFile().mkdirs();

				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);
				int count;
				while( (count = is.read(data, 0, BUFFER_SIZE)) != -1 ) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
			}
		}
        catch( Exception ex ) {
            log.error( " Error when unzipping " + zipFile.getPath() + ": " + ex.getMessage() );
            // FileUtils.deleteDirectory( tmpDir ); // TODO: Check for previous existence.
        }

        return tmpDir;
    }



    /**
     *  Unzip method with overwrite behavior option.
     *  @param mode ONLY_NEW, WRITE_INTO or DELETE_FIRST.
     *              If ONLY_NEW and the dir exists, returns false and does nothing.
     *  @returns true if the zip was unzipped to the given dir.
     */
    public final static boolean unzipFileToDir( File zip, File intoDir, OverwriteMode mode ) throws IOException {
        if( intoDir.exists() ){
            if( mode == OverwriteMode.ONLY_NEW ){
                return false;
            }
            else if( mode == OverwriteMode.DELETE_FIRST ){
                FileUtils.deleteDirectory( intoDir );
            }
        }
        unzipFileToDir( zip, intoDir );
        return true;
    }
    
    
    /**
     *  Unzip method.
     */
    public final static void unzipFileToDir( File zipFile, File intoDir ) throws IOException {
        unzipFileToDir( zipFile, intoDir, (FileFilter)null );
    }
    
    public final static void unzipFileToDir( File zipFile, File intoDir, FileFilter fileFilter ) throws IOException {
        
        ZipFile zip = new ZipFile( zipFile );
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        
        byte[] buf = new byte[BUFFER_SIZE];
            
        while( entries.hasMoreElements() ) {
            ZipEntry entry = entries.nextElement();
            if( entry.isDirectory() )	continue;
            if( entry.getName().contains("..") )  continue;
            
            if( fileFilter != null && ! fileFilter.accept( new File( entry.getName() )) ) continue;
            
            log.trace("  Extracting: " + entry);
            
            File f = new File( intoDir, entry.getName() );
            if( !f.exists() ) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            
            InputStream is = zip.getInputStream(entry);
            OutputStream os = new java.io.FileOutputStream(f);
            int r;
            while ((r = is.read(buf)) != -1) {
              os.write(buf, 0, r);
            }
            os.close();
            is.close();
        }
    }

    
    private final static IOFileFilter TEST_XML_FILTER = FileFilterUtils.and(
            FileFilterUtils.prefixFileFilter("TEST-"),
            FileFilterUtils.suffixFileFilter("xml")
    );
    
    public enum OverwriteMode {
        ONLY_NEW, WRITE_INTO, DELETE_FIRST
    }
    
}// class
