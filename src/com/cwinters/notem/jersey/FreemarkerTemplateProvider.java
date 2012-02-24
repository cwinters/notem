package com.cwinters.notem.jersey;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import com.cwinters.notem.Storage;
import com.cwinters.notem.util.Util;
import com.sun.jersey.spi.template.TemplateProcessor;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Match a Viewable-named view with a Freemarker template.
 *
 * <p>You can configure the location of your templates with the
 * context param 'freemarker.template.path'. If not assigned
 * we'll use a default of <tt>WEB-INF/templates</tt>. Note that this uses
 * Freemarker's {@link freemarker.cache.WebappTemplateLoader} to
 * load/cache the templates, so check its docs (or crank up the logging
 * under the 'freemarker.cache' package) if your templates
 * aren't getting loaded.</p>
 *
 * <p>This will put your Viewable's model object in the template
 * variable "it", unless the model is a Map. If so, the values
 * will be assigned to the template assuming the map is of
 * type <tt>Map&lt;String,Object></tt>.</p>
 *
 * Example of configuring the template path:
 *
 * <pre>
 * &lt;web-app ...
 *    &lt;display-name>Awesomeo 2000&lt;/display-name>
 *    &lt;context-param>
 *       &lt;param-name>freemarker.template.path&lt;/param-name>
 *       &lt;param-value>/WEB-INF/views&lt;/param-value>
 *   &lt;/context-param>
 *   ...
 *</pre>
 *
 * <p>You'll also need to tell Jersey the package where this provider
 * is stored. Typically this is through the servlet's init params -- for instance,
 * in the below configuration we could store this in <tt>com.myco.jersey</tt> and
 * my resources in <tt>com.myco.jersey.resource</tt></p>
 *
 * <pre>
 * &lt;servlet>
 *     &lt;servlet-name>My REST App&lt;/servlet-name>
 *     &lt;servlet-class>com.sun.jersey.spi.container.servlet.ServletContainer&lt;/servlet-class>
 *     &lt;init-param>
 *         &lt;param-name>com.sun.jersey.config.property.packages&lt;/param-name>
 *         &lt;param-value>com.myco.jersey;com.myco.jersey.resource&lt;/param-value>
 *     &lt;/init-param>
 * &lt;/servlet>
 * </pre>
 *
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision: #5 $ (Last update: $DateTime: 2009/04/03 20:12:26 $ $Author: cwinters $)
 */
@Provider
public class FreemarkerTemplateProvider implements TemplateProcessor
{
    private static final Logger log = Logger.getLogger( FreemarkerTemplateProvider.class );

    private static Configuration freemarkerConfig;

    private ServletContext servletContext;
    private String rootPath;

    public FreemarkerTemplateProvider() {}

    public String resolve( final String path )
    {
        if ( log.isDebugEnabled() )
            log.debug( "Resolving freemarker template path (" + path + ")" );

        // accept both '/path/to/template' and '/path/to/template.ftl'
        final String filePath = path.endsWith( "ftl" ) ? path : path + ".ftl";
        try {
            final String fullPath = rootPath + filePath;
            final boolean templateFound = servletContext.getResource( fullPath ) != null;
            if ( ! templateFound )
                log.warn( "Template not found [Given path: " + path + "] " +
                          "[Servlet context path: " + fullPath + "]" );
            return templateFound ? filePath : null;
        }
        catch ( MalformedURLException e ) {
            log.warn( "Caught MalformedURLException when trying to get freemarker resource (" + filePath + ") " +
                      "from the servlet context: " + e.getMessage() );
            return null;
        }
    }

    @SuppressWarnings( { "unchecked" } )
    public void writeTo( String resolvedPath, Object model, OutputStream out ) throws IOException
    {
        if ( log.isDebugEnabled() )
            log.debug( "Evaluating freemarker template (" + resolvedPath + ") with model of type " +
                       ( model == null ? "null" : model.getClass().getSimpleName() ) );

        out.flush(); // send status + headers

        final Template template = freemarkerConfig.getTemplate( resolvedPath );
        if ( log.isDebugEnabled() )
            log.debug( "OK: Resolved freemarker template" );

        final OutputStreamWriter writer = new OutputStreamWriter( out );

        final Map<String,Object> vars = new HashMap<String, Object>();

        if ( model instanceof Map ) {
            vars.putAll( (Map<String, Object>)model );
        }
        else {
            vars.put( "it", model );
        }

        vars.put( "storage", Storage.getInstance() );
        vars.put( "now", new DateTime() );

        try {
            template.process( vars, writer );
            if ( log.isDebugEnabled() )
                log.debug( "OK: Processed freemarker template" );
        }
        catch ( Throwable t ) {
            log.error( "Error processing freemarker template @ " + resolvedPath + ": " + t.getMessage(), t );
            out.write( "<pre>".getBytes() );
            t.printStackTrace( new PrintStream( out ) );
            out.write( "</pre>".getBytes() );
        }
    }

    @Context
    public void setServletContext( final ServletContext context )
    {
        this.servletContext = context;

        freemarkerConfig = new Configuration();

        rootPath = context.getInitParameter( "freemarker.template.path" );
        if ( Util.isBlank( rootPath ) ) {
            log.info( "No 'freemarker.template.path' context-param, " +
                      "defaulting to '/WEB-INF/templates'" );
            rootPath = "/WEB-INF/templates";
        }
        rootPath = rootPath.replaceAll( "/$", "" );

        freemarkerConfig.setTemplateLoader( new WebappTemplateLoader( context, rootPath ) );

        final InputStream fmProps = context.getResourceAsStream( "freemarker.properties" );
        if ( fmProps != null ) {
            try {
                freemarkerConfig.setSettings( fmProps );
                log.info( "OK: Assigned freemarker configuration from 'freemarker.properties'" );
                return;
            }
            catch ( Throwable t ) {
                log.warn( "Failed to load/assign freemarker.properties, will use default settings " +
                          "instead; error: " + t.getMessage() );
            }
        }

        // don't always put a ',' in numbers (e.g., id=2000 vs id=2,000)
        freemarkerConfig.setNumberFormat( "0" );

        // don't look for list.en.ftl when list.ftl requested
        freemarkerConfig.setLocalizedLookup( false );

        // don't cache
        freemarkerConfig.setTemplateUpdateDelay(0);

        log.info( "OK: Assigned default freemarker configuration" );
    }
}