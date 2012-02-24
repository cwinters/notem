package com.cwinters.notem.jaxb;

import com.cwinters.notem.Note;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import java.util.*;

public final class JAXBContextResolver
        implements ContextResolver<JAXBContext>
{
    private static JAXBContextResolver instance;

    public static JAXBContext getGlobalContext() 
    {
        try {
            if ( instance == null ) {
                instance = new JAXBContextResolver();
            }
            return instance.getContext();
        }
        catch ( Exception e ) {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }

    private final JAXBContext _context;
    private final Set<Class> _types = new HashSet<Class>( Arrays.asList(
            Note.class
    ));

    public JAXBContextResolver() throws Exception
    {
        final JSONConfiguration config = JSONConfiguration
                .mapped()
                .rootUnwrapping( true )
                .attributeAsElement( "href", "method", "order", "poster", "rel", "time" )
                .build();
        this._context = new JSONJAXBContext(
                config, _types.toArray( new Class[_types.size() ] ) );
    }

    public JAXBContext getContext()
    {
        return _context;
    }

    public JAXBContext getContext( final Class<?> objectType )
    {
        return _types.contains( objectType ) ? _context : null;
    }
}