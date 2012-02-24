package com.cwinters.notem.jaxb;

import org.apache.log4j.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
@Provider
public class JAXBMarshallerResolver
    implements ContextResolver<Marshaller>
{
    private static final Logger log = Logger.getLogger( JAXBMarshallerResolver.class );

    private final JAXBContext _context;
    private final UriInfo _uriInfo;

    public JAXBMarshallerResolver( @Context UriInfo uriInfo )
    {
        _context = JAXBContextResolver.getGlobalContext();
        _uriInfo = uriInfo;
    }

    public Marshaller getContext( final Class<?> type )
    {
        try {
            final Marshaller m = _context.createMarshaller();
            m.setAdapter( new UriAdapter( _uriInfo ) );
            
            // note: pretty-printing only works for XML right now, not sure
            // of handwaving needed for JSON...
            m.setProperty( "com.sun.xml.bind.indentString", "    " );
            m.setProperty( "jaxb.formatted.output", Boolean.TRUE );
            
            return m;
        }
        catch ( JAXBException e ) {
            log.error( "Failed to get marshaller: " + e.getMessage(), e );
            return null;
        }
    }
}