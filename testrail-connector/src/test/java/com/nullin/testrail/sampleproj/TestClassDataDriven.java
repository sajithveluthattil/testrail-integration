package com.nullin.testrail.sampleproj;

import java.util.Random;

import com.nullin.testrail.annotations.TestRailCase;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author nullin
 */
public class TestClassDataDriven {

    @DataProvider(name = "MYDP")
    public Object[][] getData() {
        return new Object[][] {
                {"C220342", 10, 3}
        };
    }

    @DataProvider(name = "MultiTestCasesDataProvider")
    public Object[][] getMultiTestCasesDataProvider() {
        return new Object[][] {
                {new String[] {"C220342", "C220343"}, 10, 3}
        };
    }

    @TestRailCase(dataDriven = true)
    @Test(dataProvider = "MYDP")
    public void test2(String _testId, int x, int y) {
        Assert.assertTrue(getResult(x, y));
    }

    @TestRailCase(dataDriven = true)
    @Test(dataProvider = "MultiTestCasesDataProvider")
    public void testMultiTestCasesDataProvider(String[] _testIds, int x, int y) {
        Assert.assertTrue(getResult(x, y));
    }

    public static boolean getResult(int max, int r) {
        return new Random(max).nextInt() % r != 0;
    }

}
