package com.cwinters.notem.util;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
public class Util
{
        /**
     * @param toCheck objects to check
     * @return true if any given object is not null, false if all are null
     * or if given no objects to check
     */
    public static boolean anyNotNull( final Object ... toCheck )
    {
        for ( final Object o : toCheck ) {
            if ( o != null ) return true;
        }
        return false;
    }

    /**
     * @param toCheck objects to check
     * @return true if any given object is null, false if all are not null or
     * if given no objects to check
     */
    public static boolean anyNull( final Object ... toCheck )
    {
        for ( final Object o : toCheck ) {
            if ( o == null ) return true;
        }
        return false;
    }

    /**
     * @param check string to check
     * @return true if string is null, empty, or contains only spaces
     */
    public static boolean isBlank( final String check )
    {
        return check == null || check.length() == 0 || check.trim().length() == 0;
    }

    /**
     * @param check string to check
     * @return true if string is not null and has non-whitespace characters
     */
    public static boolean isNotBlank( final String check )
    {
        return ! isBlank( check );
    }

    /**
     * @param check strings to check
     * @return true if any string in given array is blank, false if all are not blank
     */
    public static boolean anyBlank( final String ... check )
    {
        for ( final String s : check ) {
            if ( isBlank( s ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param check strings to check
     * @return true if all strings are non-blank, false if any are blank (inverse of {@link #anyBlank(String[])})
     */
    public static boolean allNotBlank( final String ... check )
    {
        return ! anyBlank( check );
    }

    /**
     * @param check strings to check
     * @return true if any string in given array is NOT blank
     */
    public static boolean anyNotBlank( final String ... check )
    {
        for ( final String s : check ) {
            if ( isNotBlank( s ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param check strings to check
     * @return true if all strings in given array are blank (inverse of {@link #anyNotBlank(String[])}
     */
    public static boolean allBlank( final String ... check )
    {
        return ! anyNotBlank( check );
    }

    /**
     * @param items     items to join
     * @param delimiter separator for joined items
     * @return string of items separated by the given delimiter
     */
    public static String join( final String delimiter, final Object ... items )
    {
        final StringBuffer sb = new StringBuffer();
        final int numMsg = items.length;
        for ( int i = 0; i < numMsg; i++ ) {
            sb.append( items[i] == null ? "null" : items[i].toString() );
            if ( i < numMsg - 1 ) {
                sb.append( delimiter );
            }
        }
        return sb.toString();
    }
}