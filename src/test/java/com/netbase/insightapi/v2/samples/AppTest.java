package com.netbase.insightapi.v2.samples;

import net.sf.json.JSONObject;

import com.netbase.insightapi.v2.NetBase;
import com.netbase.insightapi.v2.NetBaseFactory;
import com.netbase.insightapi.v2.Request;
import com.netbase.insightapi.v2.Response;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testHelloWorld() throws Exception
    {
    	String user = "";
    	String password = "";
    	NetBase nb = NetBaseFactory.getInstance(user, password, false);
    	Response res = nb.helloWorld(new Request());
    	String message = ((JSONObject) res.getJSON()).getString("message");
    	
        assertEquals("Hello, world!", message);
    }
}
