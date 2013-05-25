package ece351.f.rdescent;

import ece351.f.AbstractTestFParser;
import ece351.f.ast.FProgram;


public final class TestFRDParser extends AbstractTestFParser {

 	@Override
	protected FProgram parse(final String input) {
		return FRecursiveDescentParser.parse(input);
	}
	
}
