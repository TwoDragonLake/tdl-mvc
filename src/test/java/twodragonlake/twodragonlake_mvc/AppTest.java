package twodragonlake.twodragonlake_mvc;


import java.util.ArrayList;
import java.util.List;

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
    public void testApp()
    {
        assertTrue( true );
    }
    
    public void testlist(){
        List<String> list = new ArrayList<String>();  
        System.out.println(list instanceof List);  
        System.out.println(list instanceof ArrayList);  
          
      System.out.println(list.getClass()); 
        System.out.println(List.class); 
        System.out.println(ArrayList.class);  
          
        System.out.println(list.getClass().isAssignableFrom(List.class));  
        System.out.println(List.class.isAssignableFrom(list.getClass()));
    }
    
}
