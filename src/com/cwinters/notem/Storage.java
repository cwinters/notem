package com.cwinters.notem;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import com.cwinters.notem.util.FileUtil;
import com.cwinters.notem.util.PorterStemmer;
import com.cwinters.notem.util.RecursiveFileFinder;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.io.*;
import java.util.*;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
public class Storage
{
    private static final Logger log = Logger.getLogger( Storage.class );

    private static final String EXTENSION = ".txt";

    private static final Set<String> STOPWORDS = Sets.newHashSet(
            "and", "or", "not", "if", "a", "an", "for", "in", "on", "by" );

    private static Predicate<String> NOT_STOPWORD = new Predicate<String>() {
        public boolean apply( final String input ) {
            return ! STOPWORDS.contains( input.trim().toLowerCase() );
        }
    };

    private static final FileFilter NOTE_FILTER = new FileFilter() {
        public boolean accept( final File path ) {
            return path.getName().endsWith( EXTENSION );
        }
    };

    public static final Joiner PATH_JOINER = Joiner.on( "/" );

    private final static Storage STORAGE = new Storage();

    public static Storage getInstance()
    {
        return STORAGE;
    }

    
    private final Set<String> _existing = new HashSet<String>();
    private final Multimap<String,String> _index = HashMultimap.create();
    private File _rootDirectory;
    private final PorterStemmer _stemmer = new PorterStemmer();

    private Storage() {}

    public File getRootDirectory()
    {
        return _rootDirectory;
    }

    /**
     * Assign a new root directory for storage.
     *
     * When the directory gets assigned, clean out the index and reindex all known notes.
     *
     * @param rootDirectory root directory
     * @return this
     */
    public Storage setRootDirectory( final File rootDirectory )
    {
        _rootDirectory = rootDirectory;
        _index.clear();
        _existing.clear();
        indexAllNotes();
        return this;
    }

    private File getDirectory( final Object ... items )
    {
        final String path = PATH_JOINER.join( items );
        return new File( _rootDirectory, path );
    }

    private File getOrCreateDirectory( final Object ... items )
            throws IOException
    {
        final String path = PATH_JOINER.join( items );
        final File file = new File( _rootDirectory, path );
        if ( ! file.isDirectory() ) {
            if ( ! file.mkdirs() ) {
                throw new IOException( "Failed to create directory " + file.getAbsolutePath() );
            }
        }
        return file;
    }

    private File getOrCreateDirectory( final Note note ) throws IOException
    {
        final DateTime t = note.getTime();
        return getOrCreateDirectory( t.getYear(), t.getMonthOfYear(), t.getDayOfMonth() );
    }

    private String fileNameForId( final String id )
    {
        return id + EXTENSION;
    }

    private List<Note> listInDirectory( final File directory )
    {
        final List<Note> notes = new ArrayList<Note>();
        if ( ! directory.isDirectory() ) {
            return notes;
        }
        
        final RecursiveFileFinder finder = new RecursiveFileFinder( directory );
        final List<File> files = finder.find( NOTE_FILTER );
        for ( final File file : files ) {
            if ( ! file.isFile() ) {
                continue;
            }
            final Note note = readNote( file );
            if ( note != null ) {
                notes.add( note );
            }
        }
        Collections.sort( notes );
        return notes;
    }

    public List<Note> listByYearMonthDay( final int year, final int month, final int day )
    {
        return listInDirectory( getDirectory( year, month, day ) );
    }

    public List<Note> listByYearMonth( final int year, final int month )
    {
        return listInDirectory( getDirectory( year, month ) );
    }

    public List<Note> listByYear( final int year )
    {
        return listInDirectory( getDirectory( year ) );
    }

    public Note getByIdentifier( final String identifier )
    {
        final String[] items = identifier.split( "/", 4 );
        if ( items.length != 4 ) {
            throw new IllegalArgumentException(
                    "Invalid identifier (" + identifier + "); expected 4 items, got " + items.length );
        }
        try {
            final int year = Integer.parseInt( items[0] );
            final int month = Integer.parseInt( items[1] );
            final int day   = Integer.parseInt( items[2] );
            final String id = items[3];
            return get( year, month, day, id );
        }
        catch ( NumberFormatException e ) {
            throw new IllegalArgumentException(
                    "Invalid identifier (" + identifier + "); expected number" );
        }
    }

    public Note get( final int year, final int month, final int day, final String id )
    {
        final File dir = getDirectory( year, month, day );
        if ( ! dir.isDirectory() ) {
            log.warn( "No directory for date " + String.format( "%s/%s/%s", year, month, day ) );
            return null;
        }
        else {
            final File noteFile = new File( dir, fileNameForId( id ) );
            log.debug( "...trying to read note from: " + noteFile.getAbsolutePath() );
            return readNote( noteFile );
        }
    }

    private Note readNote( final File f )
    {
        FileInputStream in = null;
        try {
            in = new FileInputStream( f );
            return new Note( in );            
        }
        catch ( Throwable t ) {
            log.error( "Failed to turn file (" + f.getAbsolutePath() + ") into note: " + t.getMessage() );
            return null;
        }
        finally {
            FileUtil.closeCleanly( in, "note file" );            
        }
    }

    public Note add( final Note note )
    {
        final DateTime d = note.getTime() == null
                           ? new DateTime()
                           : note.getTime();
        final Note useNote = note.getTime() == null
                             ? note.withTime( d )
                             : note;
        try {
            final File storeInDir = getOrCreateDirectory(
                    d.getYear(), d.getMonthOfYear(), d.getDayOfMonth() );
            final File noteFile = findNextFile( storeInDir );
            final Note withOrder = useNote.withOrder(
                    Integer.parseInt( noteFile.getName().split( "\\." )[0] ) );
            FileUtil.dump( withOrder.serializeToText(), noteFile );
            index( withOrder );
            return withOrder;
        }
        catch ( IOException e ) {
            final String msg = "Failed to add note: " + e.getMessage();
            log.error( msg, e );
            throw new IllegalStateException( msg, e );
        }
    }

    public void update( final Note note )
    {
        try {
            FileUtil.dump( note.serializeToText(), fileForNote( note ) );
            index( note );
        }
        catch ( IOException e ) {
            final String msg = "Failed to update note: " + e.getMessage();
            log.error( msg, e );
            throw new IllegalStateException( msg, e );
        }
    }

    public void delete( final Note note )
    {
        _existing.remove( note.identifier() );
        final File file = fileForNote( note );
        final boolean deleted = file.delete();
        if ( ! deleted ) {
            final String msg = "Failed to delete note for unknown reason";
            log.error( msg );
            throw new IllegalStateException( msg );
        }
    }

    private File fileForNote( final Note note )
    {
        try {
            return new File(
                    getOrCreateDirectory( note ),
                    fileNameForId( note.getOrder() + "" ) );
        }
        catch ( IOException e ) {
            final String msg = "Caught exception getting directory for note: " + e.getMessage();
            throw new IllegalStateException( msg, e );
        }
    }

    private File findNextFile( final File directory )
    {
        int order = 1;
        while ( order < 4096 ) {
            final File check = new File( directory, fileNameForId( order + "" ) );
            try {
                if ( check.createNewFile() ) {
                    return check;
                }
            }
            catch ( IOException e ) {
                log.warn( "Caught exception checking for new file to create: " + e.getMessage() );
            }
            finally {
                order++;
            }
        }
        throw new IllegalStateException( "Failed to create new note file in " + directory.getAbsolutePath() );
    }

    ////////////////////////////////////////
    // INDEX/SEARCH

    private void indexAllNotes()
    {
        for ( final Note note : listInDirectory( _rootDirectory ) ) {
            index( note );
        }
    }

    private void index( final Note note )
    {
        final String identifier = note.identifier();
        _existing.add( identifier );
        final Set<String> words = new HashSet<String>();
        words.addAll( Arrays.asList( note.getText().split( "\\s+" ) ) );
        words.add( note.getPoster() );
        for ( final String word : Sets.filter( words, NOT_STOPWORD ) ) {
            _index.put( _stemmer.stem( word.trim().toLowerCase() ), identifier );
        }
        log.info( "Indexed note [" + identifier + "] with words: " + Joiner.on( ";" ).join( words ) );
    }

    public List<Note> search( final String... keywords )
    {
        final Set<Note> notes = new HashSet<Note>();
        for ( final String keyword : keywords ) {
            for ( final String identifier : _index.get( _stemmer.stem( keyword ) ) ) {
                if ( _existing.contains( identifier ) ) {
                    final Note note = getByIdentifier( identifier );
                    if ( note != null ) {
                        notes.add( note );
                    }
                }
            }
        }
        final List<Note> ordered = new ArrayList<Note>( notes );
        Collections.sort( ordered );
        return ordered;        
    }
}