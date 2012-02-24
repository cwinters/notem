package com.cwinters.notem.jaxb;

import org.apache.log4j.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
@Provider
public class JAXBUnmarshallerResolver
    implements ContextResolver<Unmarshaller>
{
    private static final Logger log = Logger.getLogger( JAXBUnmarshallerResolver.class );

    private final JAXBContext _context;
    private final UriInfo _uriInfo;

    public JAXBUnmarshallerResolver( @Context UriInfo uriInfo )
    {
        _context = JAXBContextResolver.getGlobalContext();
        _uriInfo = uriInfo;
    }

    public Unmarshaller getContext( final Class<?> type )
    {
        try {
            final Unmarshaller um = _context.createUnmarshaller();
            um.setAdapter( new UriAdapter( _uriInfo ) );
            return um;
        }
        catch ( JAXBException e ) {
            log.error( "Failed to get unmarshaller: " + e.getMessage(), e );
            return null;
        }
    }
}