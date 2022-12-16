package com.nullin.testrail;

import java.util.ArrayList;
import java.util.List;

/**
 * Arguments for {@link com.nullin.testrail.TestRailListener}
 *
 * @author nullin
 */
public class TestRailArgs {

    //if the listener is enabled or not
    private Boolean enabled;
    //test plan id (if one already exists)
    private Integer testPlanId;
    // test run id
    private Integer testRunId;
    //suite names
    private List<String> suiteNames;
    //url to the TestRail instance
    private String url;
    //username to login to TestRail
    private String username;
    //password to login to TestRail
    private String password;
    // by default will lookup testautomationid to get actual case id from testrail
    private Boolean enableAutomationIdLookup = true;
    //device on which executed
    private String primaryDevice;

    private TestRailArgs() {}

    public static TestRailArgs getNewTestRailListenerArgs() {
        TestRailArgs args = new TestRailArgs();
        args.enabled = Boolean.valueOf(System.getProperty("testRail.enabled"));

        if (args.enabled == null || !args.enabled) {
            return args; //no need to process further. TestRail reporting is not enabled
        }

        String planId = System.getProperty("testRail.testPlanId");
        if (planId == null) {
            throw new IllegalArgumentException("TestRail Test Plan ID not specified");
        } else {
            try {
                args.testPlanId = Integer.valueOf(planId);
            } catch(NumberFormatException ex) {
                throw new IllegalArgumentException("Plan Id is not an integer as expected");
            }
        }


        String runId = System.getProperty("testRail.testRunId");
        if (runId != null) {
            try {
                args.testRunId = Integer.valueOf(runId);
            } catch(NumberFormatException ex) {
                args.testRunId = null;
            }
        }

        String suiteNamesStr = System.getProperty("testRail.suiteNames");
        if (suiteNamesStr != null) {
            try {
                String[] suiteNamesArr = suiteNamesStr.split(",");
                args.suiteNames = new ArrayList<String>();
                for (String suiteName : suiteNamesArr) {
                    if (suiteName != null && !suiteName.trim().isEmpty()) {
                        args.suiteNames.add(suiteName.trim());
                    }
                }

            } catch(NumberFormatException ex) {
                throw new IllegalArgumentException("Plan Id is not an integer as expected");
            }
        }

        String deviceName = System.getProperty("testRail.device");
        System.out.println("DEVICE IS"+deviceName);
        if (deviceName != null) {
            try {
                args.primaryDevice = deviceName;
            } catch(NumberFormatException ex) {
                args.primaryDevice = null;
            }
        }

        if ((args.url = System.getProperty("testRail.url")) == null) {
            throw new IllegalArgumentException("TestRail URL not specified (testRail.url)");
        }

        if ((args.username = System.getProperty("testRail.username")) == null) {
            throw new IllegalArgumentException("TestRail user not specified (testRail.username)");
        }

        if ((args.password = System.getProperty("testRail.password")) == null) {
            throw new IllegalArgumentException("TestRail password not specified (testRail.password)");
        }

        if (System.getProperty("testRail.enableAutomationIdLookup") == null)
            args.enableAutomationIdLookup = true;
        else
            args.enableAutomationIdLookup = Boolean.valueOf(System.getProperty("testRail.enableAutomationIdLookup"));

        return args;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Integer getTestPlanId() {
        return testPlanId;
    }

    public Integer getTestRunId() {
        return testRunId;
    }

    public List<String> getSuiteNames() {
        return suiteNames;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getEnableAutomationIdLookup() {
        return enableAutomationIdLookup;
    }

    public String getDevice() {
        return primaryDevice;
    }
}