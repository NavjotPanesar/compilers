package ece351.util;

import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitVersionHelper;

public class CSVFormatter implements JUnitResultFormatter {

	private enum Status {
            ERROR, PASS, FAIL
	}
	
	private Status status;
	private PrintStream out;

	@Override
	public void setOutput(OutputStream out) {
            this.out = new PrintStream(out, true);
	}

	@Override
	public void setSystemOutput(String s) {}

	@Override
	public void setSystemError(String s) {}

	@Override
	public void startTestSuite(JUnitTest t) {}

	@Override
	public void startTest(Test t) {
            status = Status.PASS;
	}
	
	@Override
	public void addError(Test t, Throwable e) {
            status = Status.ERROR;
	}

	@Override
	public void addFailure(Test t, AssertionFailedError e) {
            status = Status.FAIL;
	}

	@Override
	public void endTest(Test t) {
            out.println(JUnitVersionHelper.getTestCaseName(t) + "," + status);
	}

	@Override
	public void endTestSuite(JUnitTest t) {}

}
