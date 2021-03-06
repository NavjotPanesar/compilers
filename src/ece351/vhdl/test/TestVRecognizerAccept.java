package ece351.vhdl.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ece351.TestPrelab;
import ece351.util.CommandLine;
import ece351.util.TestInputs351;
import ece351.vhdl.VRecognizer;

@RunWith(Parameterized.class)
public class TestVRecognizerAccept {

	private final File f;

	public TestVRecognizerAccept(final File f) {
		this.f = f;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> files() {
		return TestInputs351.vhdlFiles();
	}

	@Test
	public void accept() {
		assertTrue(TestPrelab.areAssertionsEnabled());

		final String inputSpec = f.getAbsolutePath();
		System.out.println("reading: " + inputSpec);
		final CommandLine c = new CommandLine(inputSpec);
		System.out.println(c.readInputSpec());
		VRecognizer.main(inputSpec);
		System.out.println("accepted, as expected:  " + inputSpec);
	}


}
