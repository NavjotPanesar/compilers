package ece351;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Test;

import ece351.util.Todo351Exception;

public class TestPrelab {

	@Test
	public void testJUnitConfiguration() {
		boolean x = false;
		assert x=true : "the assignment will always succeed, but will only be executed if assertions are enabled";
		assertTrue("JUnit not adding -ea to the VM arguments for new launch configurations", x);
	}
	
	public static boolean areAssertionsEnabled() {
		boolean x = false;
		assert x=true : "the assignment will always succeed, but will only be executed if assertions are enabled";
		return x;
	}

	@Test
	public void testEclipseJDKConfiguration() {
		final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		assertNotNull("Eclipse is running with a JRE rather than a JDK", javac);
	}

	/**
	 * This test intended to error/fail. The point is to learn how to read
	 * the nested stack trace.
	 */
    @Test
    public void testNestedException() {
        try {
        	final File f = new File("non-existant-file");
        	final FileReader r = new FileReader(f);
        	r.close();
        } catch (final Exception e) {
            throw new RuntimeException("this is not the exception that you are looking for", e);
        }
    }

    /**
     * Wherever you see a Todo351Exception being thrown you should
     * replace it with working code.
     */
    @Test
    public void testTodo351Exception() {
    	throw new Todo351Exception("replace these exceptions with working code");
    }
}
