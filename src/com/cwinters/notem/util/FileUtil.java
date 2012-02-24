package com.cwinters.notem.util;

import org.apache.log4j.Logger;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static utility methods for working with files, and general I/O streams.
 *
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision: #15 $ (Last update: $DateTime: 2009/02/13 13:11:01 $ $Author: cwinters $)
 */
public class FileUtil
{
    private static final Logger log = Logger.getLogger( FileUtil.class );

    public static final Pattern CONTINUE_LINE = Pattern.compile( "^(.+)\\s*\\\\s*$" );

    // prevent anyone creating on object, just for consistency
    private FileUtil() {}

    /**
     * Filter for finding subdirectories.
     */
    public static final FileFilter DIR_FILTER = new FileFilter() {
        public boolean accept( final File pathname ) {
            return pathname.isDirectory();
        }
    };

    /**
     * Filter for finding files in a directory.
     */
    public static final FileFilter FILE_FILTER = new FileFilter() {
        public boolean accept( final File pathname ) {
            return pathname.isFile();
        }
    };


    /**
     * Filter for finding files in a directory case-insensitively matching an extension.
     * @param extension extension to match (e.g, "js", "txt")
     * @return filter for use in File methods
     */
    public static FileFilter FILE_EXTENSION_FILTER( final String extension )
    {
        return new FileFilter() {
            private final String lcExtension = extension.toLowerCase();
            public boolean accept( final File pathname ) {
                return pathname.getName().toLowerCase().endsWith( lcExtension );
            }
        };
    }

    private static final Pattern EXTENSION = Pattern.compile( "^.*\\.(\\w+)$" );

    /**
     * Get the extension from the given file; if no extension available returns an empty string.
     * @param file file
     * @return extension of file, or empty string if none or if given null file
     */
    public static String extension( final File file )
    {
        if ( file == null ) {
            return "";
        }
        final Matcher m = EXTENSION.matcher( file.getName() );
        return m.matches()
               ? m.group(1)
               : "";
    }

    /**
     * Close the given item without throwing an exception, instead logging it at
     * the ERROR level.
     *
     * @param stream thing to close
     * @param identifier identifier to put into the logging message
     */
    public static void closeCleanly( final Closeable stream, final String identifier )
    {
        if ( stream == null ) return;
        try {
            stream.close();
        }
        catch ( final IOException e ) {
            log.error( identifier + ": Failed to close properly: " + e.getMessage() );
        }
    }

    /**
     * Dump a string to a file.
     *
     * @param source text to dump
     * @param destinationFile file to dump to
     * @throws IOException on any write error
     * @throws IllegalArgumentException if destination is null
     */
    public static void dump( final String source, final File destinationFile )
        throws IOException
    {
        checkDestinationNotNull( destinationFile );
        final FileOutputStream out = new FileOutputStream( destinationFile );
        out.write( source.getBytes() );
        out.close();
    }

    /**
     * Read all lines from a file.
     *
     * @param reader source to read lines from
     * @return all lines from file
     * @throws IOException on any read error
     * @throws IllegalArgumentException if source is null
     */
    public static String[] slurpLines( final Reader reader )
            throws IOException
    {
        checkSourceNotNull( reader );
        final BufferedReader bufferedReader = new BufferedReader( reader );
        final List<String> lines = new ArrayList<String>();
        String line;
        while ( ( line = bufferedReader.readLine() ) != null ) {
            lines.add( line );
        }
        closeCleanly( bufferedReader, "reader for slurpLines" );
        return lines.toArray( new String[ lines.size() ] );
    }


    /**
     * Read lines from a reader but skip blank lines and those beginning with a '#'
     *
     * @param reader reader for lines
     * @return valid lines from file, trimmed
     * @throws IOException on any read error
     * @throws IllegalArgumentException if reader is null
     */
    public static String[] slurpValidLines( final Reader reader )
            throws IOException
    {
        final List<String> validLines = new ArrayList<String>();
        for ( String line : slurpLines( reader ) ) {
            line = line.trim();
            if ( line.length() == 0 || line.charAt(0) == '#' ) {
                continue;
            }
            validLines.add( line );
        }
        return validLines.toArray( new String[ validLines.size() ] );
    }
    public static Map<String,String> slurpKeyValuePairs(
            final InputStream in, final String delimiterPattern )
            throws IOException
    {
        final Map<String,String> pairs = new HashMap<String, String>();
        final String[] lines = slurpValidLines( new InputStreamReader( in, "UTF-8" ) );
        for ( int i = 0; i < lines.length; i++ ) {
            final String line = lines[i];
            final String[] pair = line.trim().split( delimiterPattern, 2 );
            final String key = pair[0];
            String fullValue;
            Matcher matcher = CONTINUE_LINE.matcher( pair[1] );
            if ( matcher.matches() ) {
                int matchCount = 1;
                fullValue = "";
                log.debug( "Got line continuation on (" + fullValue + ")..." );
                while ( true ) {
                    if ( matchCount++ > 1 ) {
                        fullValue += " ";
                    }
                    fullValue += matcher.group(1);
                    log.debug( "Continued line now: (" + fullValue +")" );
                    String nextLine = lines[++i];
                    matcher = CONTINUE_LINE.matcher( nextLine );
                    if ( ! matcher.matches() ) {
                        log.debug( "...next line (" + nextLine + ") doesn't match, line completed" );
                        fullValue += " " + nextLine.trim();
                        break;
                    }
                }
            }
            else {
                fullValue = pair[1];
            }

            if ( pair.length == 1 ) {
                pairs.put( key, "" );
            }
            else if ( pair.length == 2 && ! pairs.containsKey( key ) ) {
                pairs.put( key, fullValue );
            }
        }
        return pairs;
    }
    
    private static void checkSourceNotNull( final Object o )
    {
        if ( o == null ) {
            throw new IllegalArgumentException( "Cannot read from source that is null" );
        }
    }

    private static void checkDestinationNotNull( final Object o )
    {
        if ( o == null ) {
            throw new IllegalArgumentException( "Cannot write to destination that is null" );
        }
    }
}