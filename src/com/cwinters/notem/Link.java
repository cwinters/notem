package com.cwinters.notem;

import com.cwinters.notem.jaxb.UriAdapter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
@XmlRootElement
public class Link
{
    @XmlAttribute( name = "method", required = true )
    private String _method = "GET";

    @XmlAttribute( name = "rel", required = true )
    private String _relationship;

    @XmlJavaTypeAdapter( UriAdapter.class )
    @XmlAttribute( name = "href", required = true )
    private Object _target;

    @XmlAttribute( name = "type" )
    private String _type;

    @SuppressWarnings( {"UnusedDeclaration"} )
    private Link() {}

    public Link( final Object target, final String relationship, final String type )
    {
        this( target, relationship, type, "GET" );
    }

    public Link( final Object target, final String relationship, final String type,
                 final String method )
    {
        _target = target;
        _relationship = relationship;
        _type = type;
        _method = method;
    }

    public String getMethod()
    {
        return _method;
    }

    public String getRelationship()
    {
        return _relationship;
    }

    public Object getTarget()
    {
        return _target;
    }

    public String getType()
    {
        return _type;
    }
}