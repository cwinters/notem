package com.cwinters.notem.util;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

/**
 * Find all files in a directory tree matching a given filter.
 *
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision: #1 $ (Last update: $DateTime: 2007/02/26 10:42:12 $ $Author: cwinters $)
 */
public class RecursiveFileFinder
{
    private final File startDirectory;

    public RecursiveFileFinder( final File startDirectory )
    {
        if ( startDirectory == null ) {
            throw new IllegalArgumentException(
                    "Cannot start looking for files in null directory" );
        }
        else if ( ! startDirectory.isDirectory() ) {
            throw new IllegalArgumentException(
                    "Cannot start looking for files in " +
                    startDirectory.getAbsolutePath() + "; invalid directory." );
        }
        else {
            this.startDirectory = startDirectory;
        }
    }

    /**
     * Find all files in the directory tree matching whatever criteria you've chosen.
     *
     * @param userFilter filter containing the criteria you've chosen; the filter
     * can assume it's being passed a file rather than a directory
     * @return all File objects found
     */
    public List<File> find( final FileFilter userFilter )
    {
        final FileFilter comboFilter = new FileFilter() {
            public boolean accept( File path ) {
                return path.isFile() && userFilter.accept( path );
            }
        };
        return descend( startDirectory, comboFilter );
    }

    private List<File> descend( final File dir, final FileFilter filter )
    {
        final List<File> found = new ArrayList<File>();

        // find files in current dir that match...
        found.addAll( Arrays.asList( dir.listFiles( filter )  ) );

        // then go into subdirs and find those (breadth-first)
        for ( final File subdir : dir.listFiles( FileUtil.DIR_FILTER ) ) {
            found.addAll( descend( subdir, filter ) );
        }

        return found;
    }
}