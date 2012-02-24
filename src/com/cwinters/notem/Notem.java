package com.cwinters.notem;

import java.io.File;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
public class Notem
{
    public static final String WEB_PATH  = "web";
    public static final String DATA_PATH = "data";

    private static final int DEFAULT_PORT = 8765;

    private static Notem _server;

    public static Notem getInstance()
    {
        return _server;
    }

    private final JettyService _jetty;
    private final int _port;
    private final File _rootDirectory;

    public Notem( final File root, int port )
    {
        _rootDirectory = root;
        _port          = port;
        _jetty         = new JettyService( _port );
        Storage.getInstance().setRootDirectory( root );
    }

    public void run() throws Exception
    {
        _jetty.start();
    }

    public void halt() throws Exception
    {
        _jetty.stop();
    }

    public int getPort()
    {
        return _port;
    }

    public File getRootDirectory()
    {
        return _rootDirectory;
    }

    @SuppressWarnings( {"ResultOfMethodCallIgnored"} )
    public static void main( final String... args ) throws Exception
    {
        final String path = args.length > 0 ? args[0]                     : ".";
        final int port    = args.length > 1 ? Integer.parseInt( args[1] ) : DEFAULT_PORT;
        _server = new Notem( new File( path ), port );
        _server.run();
        System.out.println( "Running @ http://localhost:" + port );
        System.out.println( "Enter to stop server" );
        System.in.read();
        _server.halt();
        System.exit(0);
    }
}