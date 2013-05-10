package ece351.w.rdescent;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.parboiled.common.ImmutableList;

import ece351.util.ExaminableProperties;
import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;

public class TestWRDParserBasic {

	@Test
	public void test1() {
		WProgram built = new WProgram();
		built = built.append(new Waveform(ImmutableList.of("0"), "A"));
		
		final WProgram parsed = WRecursiveDescentParser.parse("A: 0;");
		
		check(built, parsed);
	}

	@Test
	public void test2() {
		WProgram built = new WProgram();
		built = built.append(new Waveform(ImmutableList.of("0", "1", "0", "1"), "A"));
		
		final WProgram parsed = WRecursiveDescentParser.parse("A: 0 1 0 1;");
		
		check(built, parsed);
	}


	@Test
	public void test3() {
		WProgram built = new WProgram();
		built = built.append(new Waveform(ImmutableList.of("0", "1", "0", "1"), "A"));
		built = built.append(new Waveform(ImmutableList.of("0", "0", "1", "1"), "B"));
		
		final WProgram parsed = WRecursiveDescentParser.parse("A: 0 1 0 1; B: 0 0 1 1;");
		
		check(built, parsed);
	}

	
	private void check(WProgram built, final WProgram parsed) {
		// check that the two ASTs are equals
		assertTrue("ASTs differ", parsed.equals(built));

		// check examinable sanity
		ExaminableProperties.checkAllUnary(parsed);
		ExaminableProperties.checkAllUnary(built);
		ExaminableProperties.checkAllBinary(parsed, built);
	}

}
