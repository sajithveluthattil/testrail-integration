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

    @TestRailCase("C220340")
    @Test
    public void oneTestPass() {
    	 assertTrue(false);
    }
    
    @TestRailCase("C220341")
    @Test
    public void oneTestFail() {
    	assertTrue(true);
    }
    
    @TestRailCase({"C220342", "C220343"})
    @Test
    public void multipletestPass() {
        assertTrue(false);
    }
    
    @TestRailCase({"C310306", "C220340"})
    @Test
    public void multipletestFail() {
        assertTrue(true);
    }

}
