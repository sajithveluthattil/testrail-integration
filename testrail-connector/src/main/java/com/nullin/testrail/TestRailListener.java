package com.nullin.testrail;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

import com.nullin.testrail.annotations.TestRailCase;
import org.testng.IConfigurationListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.Test;

/**
 * A TestNG listener to report results to TestRail instance
 *
 * @author nullin
 */
public class TestRailListener implements ITestListener, IConfigurationListener {

	private Logger logger = Logger.getLogger(TestRailListener.class.getName());

	private TestRailReporter reporter;
	private boolean enabled;

	/**
	 * Store the result associated with a failed configuration here. This can then
	 * be used when reporting the result of a skipped test to provide additional
	 * information in TestRail
	 */
	private ThreadLocal<ITestResult> testSkipResult = new ThreadLocal<ITestResult>();

	public TestRailListener() {
		try {
			reporter = TestRailReporter.getInstance();
			enabled = reporter.isEnabled();
		} catch (Throwable ex) {
			logger.severe("Ran into exception initializing reporter: " + ex.getMessage());
			ex.printStackTrace();
		}
	}


	private void reportResult(ITestResult result) {
		if (!enabled) {
			return; // do nothing
		}

		int status = result.getStatus();
		Throwable throwable = result.getThrowable();

		Method method = result.getMethod().getConstructorOrMethod().getMethod();
		String className = result.getTestClass().getName();
		String methodName = result.getMethod().getMethodName();
		Object[] params = result.getParameters();
		String firstParam = null;

		TestRailCase trCase = method.getAnnotation(TestRailCase.class);
		Test test = method.getAnnotation(Test.class);
		Boolean isUsingDataProvider = null != test.dataProvider() && !test.dataProvider().isEmpty();
		Boolean markedAsDataDriven = trCase.dataDriven();
		// list of string to hold test case IDs
		List<String> testCaseIds = new ArrayList<String>();

		// marked with testrail annotation
		if (trCase != null) {
			testCaseIds.addAll(Arrays.asList(trCase.value()));
		} else {
			logger.warning("No TestRailCase annotation found on method " + className + "." + methodName);
		}

		if (isUsingDataProvider && markedAsDataDriven) {
			// support Data Driven test
			// first param MUST be a single test-case ID
			// or array of test-case IDs
			if (params != null && params.length > 0) {
				if (params[0].getClass().isArray()) {
					// can be multiple test case ID
					testCaseIds.addAll(Arrays.asList((String[]) params[0]));
				} else {
					firstParam = (String) params[0];
					testCaseIds.add(firstParam);
				}
			}

			logger.info("Data Driven Test: " + className + "." + methodName);
		}

		if (isUsingDataProvider && !markedAsDataDriven) {
			logger.warning("Data Driven Test is not marked as data driven, but test is using data provider");
		}

		if (markedAsDataDriven && !isUsingDataProvider) {
			logger.warning("Data Driven Test is marked as data driven, but test is not using data provider");
		}

		List<Map<String, Object>> properties = new ArrayList<>();
		for (String testcaseId : testCaseIds) {
			Map<String, Object> props = addMoreDetails(testcaseId, result, status, throwable);
			properties.add(props);
		}

		reporter.reportResult(properties);
	}

	private Map<String, Object> addMoreDetails(String caseId, ITestResult result, int status, Throwable throwable) {
		Map<String, Object> props = new HashMap<>();
		long elapsed = (result.getEndMillis() - result.getStartMillis()) / 1000;
		elapsed = elapsed == 0 ? 1 : elapsed; // we can only track 1 second as the smallest unit
		props.put(TestRailReporter.KEY_ELAPSED, elapsed + "s");
		props.put(TestRailReporter.KEY_STATUS, getStatus(status));
		props.put(TestRailReporter.KEY_THROWABLE, throwable);
		props.put(TestRailReporter.KEY_CASEID, caseId);
		// override if needed
		if (status == ITestResult.SKIP) {
			ITestResult skipResult = testSkipResult.get();
			if (skipResult != null) {
				props.put(TestRailReporter.KEY_THROWABLE, skipResult.getThrowable());
			}
		}
		props.put(TestRailReporter.KEY_SCREENSHOT_URL, getScreenshotUrl(result));
		Map<String, String> moreInfo = new LinkedHashMap<>();
		moreInfo.put("class", result.getMethod().getRealClass().getCanonicalName());
		moreInfo.put("method", result.getMethod().getMethodName());
		if (result.getParameters() != null) {
			moreInfo.put("parameters", Arrays.toString(result.getParameters()));
		}
		moreInfo.putAll(getMoreInformation(result));
		props.put(TestRailReporter.KEY_MORE_INFO, moreInfo);
		return props;
	}

	public void onTestStart(ITestResult result) {
		// not reporting a started status
	}

	public void onTestSuccess(ITestResult result) {
		reportResult(result);
	}

	public void onTestFailure(ITestResult result) {
		reportResult(result);
	}

	public void onTestSkipped(ITestResult result) {
		if (result.getThrowable() != null) {
			// test failed, but is reported as skipped because of RetryAnalyzer.
			// so, changing result status and reporting this as failure instead.
			result.setStatus(ITestResult.FAILURE);
		}
		reportResult(result);
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// nothing here
	}

	public void onStart(ITestContext context) {
		// nothing here
	}

	public void onFinish(ITestContext context) {
		// nothing here
	}

	/**
	 * TestRail currently doesn't support uploading screenshots via APIs. Suggested
	 * method is to upload screenshots to another server and provide a URL in the
	 * test comments.
	 *
	 * This method should be overridden in a sub-class to provide the URL for the
	 * screenshot.
	 *
	 * @param result result of test execution
	 * @return the URL to where the screenshot can be accessed
	 */
	public String getScreenshotUrl(ITestResult result) {
		return null; // should be extended & overridden if needed
	}

	/**
	 * In case, we want to log more information about the test execution, this
	 * method can be used.
	 *
	 * NOTE: the test class/method/parameter information is automatically logged.
	 *
	 * This method should be overridden in a sub-class to provide map containing
	 * information that should be displayed for each test result in TestRail
	 */
	public Map<String, String> getMoreInformation(ITestResult result) {
		return Collections.emptyMap(); // should be extended & overridden if needed
	}

	/**
	 * @param status TestNG specific status code
	 * @return TestRail specific status IDs
	 */
	private ResultStatus getStatus(int status) {
		switch (status) {
		case ITestResult.SUCCESS:
			return ResultStatus.PASS;
		case ITestResult.FAILURE:
			return ResultStatus.FAIL;
		case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
			return ResultStatus.FAIL;
		case ITestResult.SKIP:
			return ResultStatus.SKIP;
		default:
			return ResultStatus.UNTESTED;
		}
	}

	public void onConfigurationSuccess(ITestResult iTestResult) {
		testSkipResult.remove();
	}

	public void onConfigurationFailure(ITestResult iTestResult) {
		testSkipResult.set(iTestResult);
	}

	public void onConfigurationSkip(ITestResult iTestResult) {
		// nothing here
	}
}
