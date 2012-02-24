package com.cwinters.notem;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import java.io.File;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
public class JettyService
{
    public static final int DEFAULT_PORT = 8765;

    private static final Logger log = Logger.getLogger( JettyService.class );

    private final int _port;
    private Server _server;

    public JettyService()
    {
        this( DEFAULT_PORT );
    }

    public JettyService( int port )
    {
        _port = port;
        init();
    }

    public JettyService init()
    {
        _server = new Server( _port );
        log.info( "Created Jetty server running on port " + _port );

        final WebAppContext webapp = new WebAppContext();
        final String contextPath = "/";
        webapp.setContextPath( contextPath );
        final String resourcePath = new File( "webapp" ).getAbsolutePath();
        webapp.setResourceBase( resourcePath );
        webapp.setClassLoader( Thread.currentThread().getContextClassLoader() );
        _server.addHandler( webapp );
        log.info( "Added webapp [" + contextPath + ": " + resourcePath + "]" );

        return this;
    }

    public JettyService start() throws Exception
    {
        _server.start();
        return this;
    }

    public JettyService stop() throws Exception
    {
        _server.stop();
        return this;
    }

    public int getPort()
    {
        return _port;
    }
}