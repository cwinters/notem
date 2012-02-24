package com.cwinters.notem.resource;

import org.joda.time.DateTime;
import com.cwinters.notem.Link;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.net.HttpURLConnection;
import java.util.*;

/**
 * Resource representing the stuff you can do; it's not a
 * <b>real</b> workspace in that it doesn't accompany every
 * response, but it's a decent bootstrap.
 *
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
@Path( "/" )
public class WorkspaceResource extends BaseResource
{
    @SuppressWarnings( {"UnusedDeclaration"} )
    @Context
    private UriInfo _uriInfo;

    @GET
    @Produces( MediaType.TEXT_HTML )
    public Response getWebHome()
    {
        return Response
                .status( HttpURLConnection.HTTP_MOVED_PERM )
                .location( _uriInfo.getBaseUriBuilder().replacePath( "/" ).build() )
                .build();
    }

    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    public List<Link> getResourceOptions()
    {
        log.info( "Returning workspace for data interface..." );
        final DateTime now = new DateTime();
        final int year = now.getYear();
        final int month = now.getMonthOfYear();
        final int day = now.getDayOfMonth();
        final Class rc = NoteCollectionResource.class;
        return Arrays.asList(
                new Link( _uriInfo.getBaseUriBuilder().path( rc, "getByYear" ).build( year ), "foryear", null, "GET" ),
                new Link( _uriInfo.getBaseUriBuilder().path( rc, "getByMonth" ).build( year, month ), "formonth", null, "GET" ),
                new Link( _uriInfo.getBaseUriBuilder().path( rc, "getByDay" ).build( year, month, day ), "fortoday", null, "GET" ),
                new Link( _uriInfo.getBaseUriBuilder().path( rc ).build(), "create", null, "POST" )
        );
    }
}