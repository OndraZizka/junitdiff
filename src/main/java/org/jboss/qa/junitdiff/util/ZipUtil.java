
package org.jboss.qa.junitdiff.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;

/**
 *  Should I use Commons Compress instead?
 * 
 *  @author Ondrej Zizka
 */
public class ZipUtil {
    
    private static int BUF_SIZE = 1024 * 32;

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
        
        ZipFile zip = new ZipFile( zipFile );
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        
        while( entries.hasMoreElements() ) {
            ZipEntry entry = entries.nextElement();
            java.io.File f = new java.io.File( intoDir, entry.getName() );
            if( entry.isDirectory() ) { // if its a directory, create it
                continue;
            }

            if( !f.exists() ) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            
            InputStream is = zip.getInputStream(entry); // get the input stream
            OutputStream os = new java.io.FileOutputStream(f);
            byte[] buf = new byte[BUF_SIZE];
            int r;
            while ((r = is.read(buf)) != -1) {
              os.write(buf, 0, r);
            }
            os.close();
            is.close();
        }
    }

    public enum OverwriteMode {
        ONLY_NEW, WRITE_INTO, DELETE_FIRST
    }
    
}// class
