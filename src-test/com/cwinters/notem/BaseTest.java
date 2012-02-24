package com.cwinters.notem;

import org.testng.Assert;

public class BaseTest
{
    public void assertNotEquals( Object actual, Object expected )
    {
        assertNotEquals( actual, expected, "Actual equals expected" );
    }

    public void assertNotEquals( Object actual, Object expected, String message )
    {
        if ( actual == null && expected == null ) {
            throw new AssertionError(
                    "Both actual and expected are null, should have been not equal" );
        }
        else if ( actual != null ) {
            assert ! actual.equals( expected ) : message;
        }
    }

    //****************************************
    // Shortcuts to TestNG assertions

    public void assertNull( Object object )
    {
        Assert.assertNull( object );
    }

    public void assertNull( Object object, String message )
    {
        Assert.assertNull( object, message );
    }

    public void assertNotNull( Object object )
    {
        Assert.assertNotNull( object );
    }

    public void assertNotNull( Object object, String message )
    {
        Assert.assertNotNull( object, message );
    }

    public void assertFalse( boolean condition )
    {
        Assert.assertFalse( condition );
    }

    public void assertFalse( boolean condition, String message )
    {
        Assert.assertFalse( condition, message );
    }

    public void assertTrue( boolean condition )
    {
        Assert.assertTrue( condition );
    }

    public void assertTrue( boolean condition, String message )
    {
        Assert.assertTrue( condition, message );
    }

    public void assertEquals( int actual, int expected )
    {
        Assert.assertEquals( actual, expected );
    }

    public void assertEquals( int actual, int expected, String message )
    {
        Assert.assertEquals( actual, expected, message );
    }

    public void assertEquals( Object actual, Object expected )
    {
        Assert.assertEquals( actual, expected );
    }

    public void assertEquals( Object actual, Object expected, String message )
    {
        Assert.assertEquals( actual, expected, message );
    }

}
