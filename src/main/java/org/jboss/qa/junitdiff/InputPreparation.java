package org.jboss.qa.junitdiff;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class InputPreparation
{
    private static final Logger log = LoggerFactory.getLogger( InputPreparation.class );



    /**
     *  Takes a list of paths which may be directories, .txt lists of paths, or JUnit XML reports,
     *  and replaces directories with TEST-*.xml's inside,
     *  and expands .txt lists of paths.
     */
    static List<File> preprocessPaths( List<File> reportFiles ) {
        List<File> expandedPaths = new LinkedList();

        for( File path : reportFiles ) {

            if( path.isFile() ){
                if( path.getName().endsWith(".xml") ){
                    expandedPaths.add( path );
                }
                else {
                    expandedPaths.addAll( readListOfPaths( path ) );
                }
            }

            if( path.isDirectory() ){
                expandedPaths.addAll( scanDirForJUnitReports( path ) );
                continue;
            }

        }

        return expandedPaths;
    }





    /**
     * Handle URLs: If the path starts with http://, it downlads the file and unzips if it ends with .zip.
     * Paths will be replaced in-place in the array.
     */
    static void handleURLs( String[] paths ) {
        for( int i = 0; i < paths.length; i++ ) {
            String path = paths[i];
            if( path == null ) {
                continue;
            }

            // Only replace URL's.
            if( !path.startsWith( "http://" ) ) {
                continue;
            }

            try {
                File resultDir;
                if( path.endsWith( ".zip" ) ) {
                    resultDir = downloadZipAndExtractToTempDir( path );
                } else if( path.endsWith( ".xml" ) ) {
                    resultDir = downloadUrlToTempFile( path );
                } else {
                    log.warn( "  URL is not .zip nor .xml - skipping: " + path );
                    continue;
                }
                paths[i] = resultDir.getPath();
            } catch( IOException ex ) {
                log.warn( "  Error when processing URL " + path + ": " + ex.getMessage(), ex );
            }

        }
    }


    /**
     * 
     */
    private static File downloadZipAndExtractToTempDir( String urlStr ) throws MalformedURLException, IOException {
        // Download
        File tmpFile = downloadUrlToTempFile( urlStr );

        // Unzip & return the tmp dir.
        File dirWithZipContent = unzipFileToTempDir( tmpFile );
        tmpFile.delete();
        return dirWithZipContent;
    }

    private static File downloadUrlToTempFile( String urlStr ) throws MalformedURLException, IOException {
        URL url = new URL( urlStr );

        // Create the directory.
        String path = url.getPath();
        path = StringUtils.strip(path, "\\/");
        path = path.replace('*', '-');
        path = path.replace('?', '-');
        File destZipFile = new File( path );
        destZipFile.getParentFile().mkdirs();


        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        //File tmp = File.createTempFile( "JUnitDiff-tmp-", ".zip" );
        FileOutputStream fos = new FileOutputStream( destZipFile );
        fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        rbc.close();
        fos.close();
        return destZipFile;
    }



    /**
     *  Reads a list of paths from a text file, one per line. Not recursive.
     */
    private static List<File> readListOfPaths( File path ) {

        try {
            List<String> lines = FileUtils.readLines( path );
            List<File> paths = new ArrayList( lines.size() );

            for( String line : lines ) {
                File f = new File( line );
                if( !f.exists() ) {
                    log.warn( "  Does not exist: " + f.getPath() );
                } else if( !f.isFile() ) {
                    log.warn( "  Not a regular file: " + f.getPath() );
                } else {
                    paths.add( f );
                }
            }

            return paths;
        } catch( IOException ex ) {
            log.warn( "Error reading " + path.getPath() + " : " + ex.getMessage() );
            return Collections.EMPTY_LIST;
        }

    }


    /**
     * Scans a directory for JUnit test reports.
     */
    private static List<File> scanDirForJUnitReports( final File path ) {
        IOFileFilter filter = FileFilterUtils.or(
                FileFilterUtils.directoryFileFilter(),
                FileFilterUtils.and(
                // Try to filter out as much as possible before opening the file below.
                FileFilterUtils.notFileFilter(
                FileFilterUtils.or(
                FileFilterUtils.suffixFileFilter( ".jar", IOCase.INSENSITIVE ),
                FileFilterUtils.suffixFileFilter( ".html", IOCase.INSENSITIVE ),
                FileFilterUtils.suffixFileFilter( ".zip", IOCase.INSENSITIVE ),
                FileFilterUtils.suffixFileFilter( ".txt", IOCase.INSENSITIVE ),
                FileFilterUtils.suffixFileFilter( ".log", IOCase.INSENSITIVE ),
                FileFilterUtils.suffixFileFilter( ".class", IOCase.INSENSITIVE ),
                FileFilterUtils.suffixFileFilter( ".java", IOCase.INSENSITIVE ),
                FileFilterUtils.suffixFileFilter( ".", IOCase.INSENSITIVE ) ) ),
                FileFilterUtils.or(
                // Perhaps make this an option - some other filters like content-based etc.
                FileFilterUtils.suffixFileFilter( ".xml" ),
                FileFilterUtils.prefixFileFilter( "TEST" ),
                FileFilterUtils.magicNumberFileFilter( "<?xml" ) ) )// and
                // Maybe we could simply scan for TEST-*.xml names.
                );

        IOFileFilter rigidFilter = FileFilterUtils.or(
                FileFilterUtils.directoryFileFilter(),
                FileFilterUtils.and(
                // Perhaps make this an option - some other filters like content-based etc.
                FileFilterUtils.suffixFileFilter( ".xml" ),
                FileFilterUtils.prefixFileFilter( "TEST" ) //FileFilterUtils.magicNumberFileFilter("<?xml")
                )// and
                // Maybe we could simply scan for TEST-*.xml names.
                );


        // Walk trough the dir tree...
        try {
            LinkedList resultList = new LinkedList();
            new DirectoryWalker( rigidFilter, -1 ) {
                @Override
                protected void handleFile( File file, int depth, Collection results ) throws IOException {
                    //		log.info("  Handling file: " + file.getPath());///
                    results.add( file );
                }

                @Override
                protected boolean handleDirectory( File directory, int depth, Collection results ) throws IOException {
                    //log.info( "  Handling dir: "+directory.getPath() );
                    return true;
                }

                public void doWalk( LinkedList list ) throws IOException {
                    this.walk( path, list );
                }
            }.doWalk( resultList );

            return resultList;
        } catch( IOException ex ) {
            return Collections.EMPTY_LIST;
        }

    }// scanDirForJUnitReports()




    private static final int BUFFER_SIZE = 2048;

    /**
     *   Unzips a file to a temporary dir.
     */
    private static File unzipFileToTempDir( File file ) throws IOException {

		//if( true ) throw new UnsupportedOperationException("downloadZipAndExtractToTempDir()");

		String path = file.getPath();
		path = path.endsWith(".zip")
		?	StringUtils.removeEndIgnoreCase( path, ".zip")
		: path + "-";


		// Try to keep the original path in the new path for the group naming purposes.
		//File tmpDir = File.createTempFile( "JUnitDiff-", "");
		//tmpDir.delete();

		File tmpDir = new File(path);
		tmpDir.mkdir();
		tmpDir.deleteOnExit();

		byte data[] = new byte[BUFFER_SIZE];

		try {
			ZipFile zipfile = new ZipFile(file);
			Enumeration e = zipfile.entries();
			while( e.hasMoreElements() ) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				if( entry.isDirectory() )	continue;
				if( entry.getName().contains("..") )  continue;

				IOFileFilter fileFilter = FileFilterUtils.and(
						FileFilterUtils.prefixFileFilter("TEST-"),
						FileFilterUtils.suffixFileFilter("xml")
				);
				if( ! fileFilter.accept( new File( entry.getName() )) ) continue;

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
            log.error( " Error when unzipping " + file.getPath() + ": " + ex.getMessage() );
            tmpDir.delete();
        }

        return tmpDir;
    }


}// class
