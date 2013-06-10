package ece351.f.parboiled;

import ece351.f.AbstractTestFParser;
import ece351.f.ast.FProgram;


public final class TestFParboiledParser extends AbstractTestFParser {

	@Override
	protected FProgram parse(final String input) {
		return FParboiledParser.parse(input);
	}

}
