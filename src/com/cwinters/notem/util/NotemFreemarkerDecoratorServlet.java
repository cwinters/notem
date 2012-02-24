package com.cwinters.notem.util;

import org.joda.time.DateTime;
import com.opensymphony.module.sitemesh.freemarker.FreemarkerDecoratorServlet;
import freemarker.template.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
public class NotemFreemarkerDecoratorServlet
        extends FreemarkerDecoratorServlet
{
    @Override
    protected boolean preTemplateProcess( final HttpServletRequest request,
                                          final HttpServletResponse response,
                                          final Template template,
                                          final TemplateModel templateModel )
            throws ServletException, IOException
    {
        final boolean result = super.preTemplateProcess(
                 request, response, template, templateModel );
        SimpleHash hash = (SimpleHash) templateModel;
        hash.put( "now", new DateTime() );
        return result;
    }

    @Override
    protected Configuration createConfiguration()
    {
        final Configuration config = super.createConfiguration();
        config.setNumberFormat( "0" );
        return config;
    }
}