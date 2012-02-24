package com.cwinters.notem;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import com.cwinters.notem.jaxb.JodaDateTimeAdapter;
import com.cwinters.notem.util.FileUtil;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.InputStream;
import java.util.*;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
@XmlRootElement( name = "note" )
public class Note implements Comparable<Note>
{
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormat.forPattern( "yyyy-MM-dd'T'H:mm:ss" );

    @XmlAttribute( name = "order" )
    private int _order;

    @XmlAttribute( name = "poster", required = true )
    private String _poster;

    @XmlElement( name = "text", required = true, nillable = false)
    private String _text;

    @XmlAttribute( name = "time", required = true )
    @XmlJavaTypeAdapter( value = JodaDateTimeAdapter.class )
    private DateTime _time;

    @SuppressWarnings( {"UnusedDeclaration"} )
    private Note() {} // no-arg constructor only used by JAXB

    public Note( final InputStream in )
    {
        try {
            final Map<String,String> props = FileUtil.slurpKeyValuePairs( in, "\\s*:\\s*" );
            _time = DT_FORMAT.parseDateTime( props.get( "time" ) );
            _poster = props.get( "poster" );
            _order = Integer.parseInt( props.get( "order" ) );
            _text = props.get( "text" );
        }
        catch ( Exception e ) {
            throw new IllegalArgumentException(
                    "Failed to read note properties from stream: " + e.getMessage() );
        }
    }

    public Note( final DateTime time, final String poster, final String text )
    {
        this( time, Integer.MAX_VALUE, poster, text );
    }

    public Note( final DateTime time, final int order,
                 final String poster, final String text )
    {
        assert time != null
                : "Cannot create note with null time";
        assert order > 0
                : "Order cannot be zero or negative";
        assert poster != null && poster.trim().length() > 0
                : "Note poster cannot be null or blank";
        assert text != null && text.trim().length() > 0
                : "Note cannot be null or blank";
        _time = time.withMillisOfSecond(0);
        _order = order;
        _poster = poster;
        _text = text;
    }

    public Note withOrder( final int order )
    {
        return new Note( _time, order, _poster, _text );
    }

    public Note withTime( final DateTime time )
    {
        System.out.println( "Creating note with time [" + time + "]" );
        return new Note( time, _order, _poster, _text );
    }

    public Note asCopyWith( final Note other )
    {
        return new Note( other.getTime(), _order, other.getPoster(), other.getText() );
    }

    public String identifier()
    {
        if ( _time == null || _order <= 0 ) {
            throw new IllegalStateException(
                    "Cannot create identifier for partially filled object " +
                    "(time: " + _time + ") (order: " + _order + ")" );
        }
        return Storage.PATH_JOINER.join(
                _time.getYear(), _time.getMonthOfYear(),
                _time.getDayOfMonth(), _order );
    }
    public int getOrder()
    {
        return _order;
    }

    public String getPoster()
    {
        return _poster;
    }

    @XmlElement( name = "link" )
    public Link getLink()
    {
        return new Link( this, "self", null );
    }

    public String getText()
    {
        return _text;
    }

    public DateTime getTime()
    {
        return _time;
    }

    /*
     * Represent this note as a string.
     * @return string with content and metadata for this note; if you translate this
     * text to an input stream and feed it back into the constructor you'll get the
     * same note
     */
    public String serializeToText()
    {
        return "time:   " + DT_FORMAT.print( _time ) + "\n" +
               "order:  " + _order + "\n" +
               "poster: " + _poster + "\n" +
               "text:   " + writeLongText( _text ) + "\n";
    }

    private String writeLongText( final String text )
    {
        return text.replaceAll( "([\r|\r\n|\n])", "\\\n" );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( ! ( obj instanceof Note ) ) return false;
        final Note other = (Note)obj;
        return new EqualsBuilder()
                .append( _time, other.getTime() )
                .append( _order, other.getOrder() )
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 17, 61 )
                .append( _time )
                .append( _order )
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return _time == null
               ? "(bad state)"
               : _time.toString( "MMM dd, H:mm" ) + ":" + _order;
    }

    public int compareTo( final Note o )
    {
        return _time.compareTo( o.getTime() ); 
    }
}