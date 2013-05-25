package ece351.f;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import ece351.f.ast.FProgram;
import ece351.util.ExaminableProperties;
import ece351.util.TestInputs351;

/**
 * Compare the given FProgram against every other FProgram,
 * check whether equals/isomorphic/equivalent return the expected results.
 * This takes a long time to run because the test suite contains about
 * 80 FPrograms and every one is compared to every other one, for a total
 * of about 80^2 = 6400 FProgram comparisons.
 * 
 * These tests do not exercise the FProgram parser nor pretty printer.
 */
public final class TestObjectContractF extends AbstractTestF {

	@Override
	protected void test(final String name1, final FProgram fp1) {

		// object contract basics for fp1
		ExaminableProperties.checkAllUnary(fp1);

		// compare fp1 to every other FProgram in the test suite
		for (final Map.Entry<String, FProgram> e : FPROGRAMS.entrySet()) {
			final String name2 = e.getKey();
			final FProgram fp2 = e.getValue();

			// check object contract basics
			ExaminableProperties.checkAllBinary(fp1, fp2);
			
			// we've already done the self comparisons with 
			// ExaminableProperties, so skip it here
			if (name1.equals(name2)) continue;

			// compare to other
			if (TestInputs351.knownIsomorphicFormulas(name2, name1)) {
				// known cases of isomorphism
				// should be isomorphic and equivalent (all the files we have meet both criteria)
				assertTrue("Unexpectedly non-isomorphic: " + name1 + " " + name2, fp1.isomorphic(fp2));
				assertTrue("Unexpectedly non-equivalent: " + name1 + " " + name2, fp1.equivalent(fp2));
			} else {
				// it's a different file, not known isomorphic
				// check that they are different
				assertFalse("Unexpectedly equals: " + name1 + " " + name2, fp1.equals(fp2));
				assertFalse("Unexpectedly isomorphic: " + name1 + " " + name2, fp1.isomorphic(fp2));
				if (TestInputs351.knownEquivalentFormulas(name2, name1)) {
					assertTrue("Unexpectedly non-equivalent: " + name1 + " " + name2, fp1.equivalent(fp2));
				} else {
					assertFalse("Unexpectedly equivalent: " + name1 + " " + name2, fp1.equivalent(fp2));
				}
			}
		}
		
		// success!
		System.out.println("object contract ok for:  " + name1);
	}

}
