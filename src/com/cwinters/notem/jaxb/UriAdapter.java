package com.cwinters.notem.jaxb;

import org.apache.log4j.Logger;
import com.cwinters.notem.Link;
import com.cwinters.notem.UriForLinkedResource;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
public class UriAdapter extends XmlAdapter<String, Object>
{
    private static final Logger log = Logger.getLogger( UriAdapter.class );

    private final UriInfo _uriInfo;

    // needed for JAXB initialization
    UriAdapter()
    {
        _uriInfo = null;
    }

    public UriAdapter( final UriInfo uriInfo )
    {
        _uriInfo = uriInfo;
    }

    public String marshal( final Object v ) throws Exception
    {
        return _uriInfo == null
               ? null
               : new UriForLinkedResource( _uriInfo, v ).create().toASCIIString();
    }

    /**
     * No-op for now; in theory this could be used to fetch a live copy of a referenced object.
     *
     * @param v uri
     * @return always null
     * @throws Exception
     */
    public Link unmarshal( final String v ) throws Exception
    {
        log.info( "Asked to unmarshal: " + v );
        return null;
    }
}