package com.cwinters.notem.resource;

import com.cwinters.notem.Note;
import com.cwinters.notem.util.Util;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import java.util.*;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
@Path( "/note" )
public class NoteResource extends BaseResource
{
    @GET
    @Produces( MediaType.TEXT_HTML )
    public Viewable getBlankForm()
    {
        log.info( "Displaying blank HTML form..." );
        return new Viewable( "/view.ftl", new HashMap<String,Object>() );
    }

    @GET
    @Path( "/{year}/{month}/{day}/{id}" )
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    public Note getNote(
            @PathParam( "year" ) int year,
            @PathParam( "month" ) int month,
            @PathParam( "day" ) int day,
            @PathParam( "id" ) String id )
    {
        final Note note = storage.get( year, month, day, id );
        if ( note == null ) {
            final String date = Util.join( "-", year, month, day ); 
            throw new NotFoundException( "No note found on " + date + " with ID " + id );
        }
        return note;
    }

    @GET
    @Path( "/{year}/{month}/{day}/{id}" )
    @Produces( MediaType.TEXT_HTML )
    public Viewable viewNote(
            @PathParam( "year" ) int year,
            @PathParam( "month" ) int month,
            @PathParam( "day" ) int day,
            @PathParam( "id" ) String id )
    {
        return new Viewable( "/view.ftl", getNote( year, month, day, id ) );
    }

    @DELETE
    @Path( "/{year}/{month}/{day}/{id}" )
    public Response delete(
            @PathParam( "year" ) int year,
            @PathParam( "month" ) int month,
            @PathParam( "day" ) int day,
            @PathParam( "id" ) String id )
    {
        final Note note = storage.get( year, month, day, id );
        if ( note == null ) {
            throw new NotFoundException();
        }
        log.info( "Deleting note:\n" + note.serializeToText() );
        storage.delete( note );
        return Response.status( HTTP_NO_CONTENT ).build();
    }

    @PUT
    @Path( "/{year}/{month}/{day}/{id}" )
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    public Note update( final Note note,
                            @PathParam( "year" ) int year,
                            @PathParam( "month" ) int month,
                            @PathParam( "day" ) int day,
                            @PathParam( "id" ) String id )
    {
        final Note toUpdate = getNote( year, month, day, id ).asCopyWith( note );
        log.info( "Updating note:\n" + toUpdate.serializeToText() );
        storage.update( toUpdate );
        return getNote( year, month, day, id );
    }
}