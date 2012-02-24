package com.cwinters.notem.resource;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import com.cwinters.notem.Note;
import com.cwinters.notem.Storage;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
public abstract class BaseResource
{
    protected static final Logger log = Logger.getLogger( NoteCollectionResource.class );

    protected Storage storage = Storage.getInstance();

    @Context
    protected UriInfo uriInfo;

    protected URI uriFor( final Note note )
    {
        final DateTime t = note.getTime();
        return uriInfo
                .getBaseUriBuilder()
                .path( NoteResource.class )
                .build( t.getYear(), t.getMonthOfYear(),
                        t.getDayOfMonth(), note.getOrder() );
    }
}