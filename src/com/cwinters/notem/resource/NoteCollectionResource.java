package com.cwinters.notem.resource;

import org.joda.time.DateTime;
import com.cwinters.notem.Note;
import com.sun.jersey.api.view.Viewable;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
@Path( "/notes" )
public class NoteCollectionResource extends BaseResource
{
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    @Path( "/{year}/{month}/{day}" )
    public List<Note> getByDay(
            @PathParam( "year" ) int year,
            @PathParam( "month" ) int month,
            @PathParam( "day" ) int day )
    {
        return storage.listByYearMonthDay( year, month, day );
    }

    @GET
    @Produces( { MediaType.TEXT_HTML } )
    @Path( "/{year}/{month}/{day}" )
    public Viewable viewByDay(
            @PathParam( "year" ) int year,
            @PathParam( "month" ) int month,
            @PathParam( "day" ) int day )
    {
        return new Viewable( "/list.ftl", getByDay( year, month, day ) );
    }

    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    @Path( "/{year}/{month}" )
    public List<Note> getByMonth(
            @PathParam( "year" ) int year,
            @PathParam( "month" ) int month )
    {
        return storage.listByYearMonth( year, month );
    }

    @GET
    @Produces( MediaType.TEXT_HTML )
    @Path( "/{year}/{month}" )
    public Viewable viewByMonth(
            @PathParam( "year" ) int year,
            @PathParam( "month" ) int month )
    {
        return new Viewable( "/list.ftl", getByMonth( year, month ) );
    }

    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    @Path( "/{year}" )
    public List<Note> getByYear( @PathParam( "year" ) int year )
    {
        return storage.listByYear( year );
    }

    @GET
    @Produces( MediaType.TEXT_HTML )
    @Path( "/{year}" )
    public Viewable viewByYear( @PathParam( "year" ) int year )
    {
        return new Viewable( "/list.ftl", getByYear( year ) );
    }

    @POST
    @Consumes( MediaType.APPLICATION_FORM_URLENCODED )
    public Response createNoteFromForm( @FormParam( "poster" ) final String poster,
                                        @FormParam( "text" ) final String text )
    {
        final DateTime now = new DateTime();
        final Note created = storage.add( new Note( now, poster, text ) );
        return Response.seeOther( uriFor( created ) ).build();
    }

    @POST
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    public Response createNote( final Note note )
    {
        log.info( "Creating note from: " + note );
        final Note created = storage.add( note );
        final DateTime instant = created.getTime();
        final URI uri = uriInfo.getBaseUriBuilder()
                .path( NoteResource.class )
                .segment( "{year}", "{month}", "{day}", "{order}" )
                .build( instant.getYear(), instant.getMonthOfYear(),
                        instant.getDayOfMonth(), created.getOrder() );
        log.info( "Created note, resulting URI: " + uri );
        return Response.created( uri ).build();
    }
}