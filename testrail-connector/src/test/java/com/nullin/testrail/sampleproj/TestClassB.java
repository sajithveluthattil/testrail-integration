package com.nullin.testrail.sampleproj;

import com.nullin.testrail.TestRailListener;
import com.nullin.testrail.annotations.TestRailCase;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 *
 * @author nullin
 */
@Listeners(TestRailListener.class)
public class TestClassB {

    @TestRailCase("C180531")
    @Test
    public void oneTestPass() {
    	 assertTrue(false);
    }
    
    @TestRailCase("C180532")
    @Test
    public void oneTestFail() {
    	assertTrue(true);
    }
    
    @TestRailCase({"C180533", "C180534"})
    @Test
    public void multipletestPass() {
        assertTrue(false);
    }
    
    @TestRailCase({"C180535", "C180536"})
    @Test
    public void multipletestFail() {
        assertTrue(true);
    }

}
