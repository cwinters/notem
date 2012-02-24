package com.cwinters.notem.jaxb;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import com.cwinters.notem.util.Util;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision: #2 $ (Last update: $DateTime: 2009/03/14 10:56:40 $ $Author: cwinters $)
 */
public class JodaDateTimeAdapter extends XmlAdapter<String, DateTime>
{
    public static final DateTimeFormatter FMT =
            DateTimeFormat.forPattern( "yyyy-MM-dd'T'H:mm:ss" );

    public String marshal( final DateTime v ) throws Exception
    {
        return v == null ? null : FMT.print( v );
    }

    public DateTime unmarshal( final String v ) throws Exception
    {
        return Util.isBlank( v ) ? null : FMT.parseDateTime( v );
    }
}