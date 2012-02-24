package com.cwinters.notem;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import com.cwinters.notem.resource.NoteResource;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
public class UriForLinkedResource
{
    private static final Logger log = Logger.getLogger( UriForLinkedResource.class );

    private final Object _target;
    private final UriInfo _uriInfo;

    public UriForLinkedResource( final UriInfo uriInfo, final Object target )
    {
        _uriInfo = uriInfo;
        _target = target;
    }

    public URI create()
    {
        log.info( "Creating URI from target (" + _target + ")" );
        if ( _target instanceof URI ) {
            return (URI)_target;
        }

        // urg! maybe use double-dispatch here?
        else if ( _target instanceof Note ) {
            final Note note = (Note)_target;
            final DateTime time = note.getTime();
            return _uriInfo.getBaseUriBuilder()
                    .path( NoteResource.class, "getNote" )
                    .build( time.getYear(), time.getMonthOfYear(),
                            time.getDayOfMonth(), note.getOrder() );
        }
        throw new IllegalArgumentException(
                "Do not know how to build URI for " + _target.getClass().getName() );
    }
}