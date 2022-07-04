package com.nullin.testrail.sampleproj.pkg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.nullin.testrail.ResultStatus;
import com.nullin.testrail.TestRailReporter;
import com.nullin.testrail.annotations.TestRailCase;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author nullin
 */
public class TestClassC {

    @TestRailCase("C20351")
    @Test
    public void test1() {
        // do nothing always passes
    }

    @TestRailCase("C20352")
    @Test
    public void test4() {
        Assert.fail("Always fails!!");
    }

    @TestRailCase({"C220340", "C220341"})
    @Test
    public void test6() {
        // do nothing always passes
    }

    @TestRailCase(selfReporting = true)
    @Test
    public void test5() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(TestRailReporter.KEY_STATUS, ResultStatus.PASS);
        TestRailReporter.getInstance().reportResult("C220340", result);
        result.put(TestRailReporter.KEY_STATUS, ResultStatus.FAIL);
        result.put(TestRailReporter.KEY_THROWABLE, new IOException("Something very bad happened!!"));
        TestRailReporter.getInstance().reportResult("C220341", result);
    }
}
