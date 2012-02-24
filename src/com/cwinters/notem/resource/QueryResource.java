package com.cwinters.notem.resource;

import com.cwinters.notem.Note;
import com.sun.jersey.api.view.Viewable;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
@Path( "/search" )
public class QueryResource extends BaseResource
{
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    public List<Note> findSearchResults( @QueryParam( "q" ) Set<String> keywords )
    {
        return storage.search( keywords.toArray( new String[ keywords.size() ] ) );        
    }

    @GET
    @Produces( MediaType.TEXT_HTML )
    public Viewable viewSearchResults( @QueryParam( "q" ) Set<String> keywords )
    {
        return new Viewable( "/list.ftl", findSearchResults( keywords ) );
    }
}