package com.cwinters.notem;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

/**
 * @author Chris Winters <cwinters@vocollect.com>
 * @version $Revision$ (Last update: $DateTime$ $Author$)
 */
public class NoteTest extends BaseTest
{
    @Test
    public void testCreate()
    {
        assertNotNull( new Note( new DateTime(), 15, "foo", "bar" ) );
    }

    @Test( expectedExceptions = AssertionError.class )
    public void testCreateWithNullDate()
    {
        new Note( null, 15, "foo", "bar" );
    }

    @Test( expectedExceptions = AssertionError.class )
    public void testCreateWithNegativeOrder()
    {
        new Note( new DateTime(), -1, "foo", "bar" );
    }

    @Test( expectedExceptions = AssertionError.class )
    public void testCreateWithZeroOrder()
    {
        new Note( new DateTime(), 0, "foo", "bar" );
    }

    @Test( expectedExceptions = AssertionError.class )
    public void testCreateWithNullPoster()
    {
        new Note( new DateTime(), 15, null, "bar" );
    }

    @Test( expectedExceptions = AssertionError.class )
    public void testCreateWithBlankPoster()
    {
        new Note( new DateTime(), 15, "   ", "bar" );
    }

    @Test( expectedExceptions = AssertionError.class )
    public void testCreateWithNullText()
    {
        new Note( new DateTime(), 15, "foo", null );
    }

    @Test( expectedExceptions = AssertionError.class )
    public void testCreateWithBlankText()
    {
        new Note( new DateTime(), 15, "foo", "   ");
    }
}