package ece351.f;

import ece351.f.ast.FProgram;
import ece351.util.ExaminableProperties;

/**
 * Testing equations: 
 * 		AST.equals(parse(prettyprint(AST)))
 * 		AST.isomorphic(parse(prettyprint(AST)))
 * 		AST.equivalent(parse(prettyprint(AST)))
 * 
 * Note that if equals/isomorphic/equivalent always return true then this
 * equation will succeed regardless of whether the parser and pretty printer are
 * correct (as long as they don't throw exceptions).
 * 
 * TestObjectContractF checks that equals/isomorphic/equivalent are doing
 * the right thing without engaging the parser and pretty printer.
 * 
 * 
 */
public abstract class AbstractTestFParser extends AbstractTestF {

	protected abstract FProgram parse(final String s);
	
	@Override
	protected void test(final String name, final FProgram fp1) {
		// pretty-print the input AST
		final String pp = fp1.toString();
		// parse the pretty-print
		final FProgram fp2 = parse(pp);
		// check that they are the same
		AbstractTestFParserBasic.compareExpectSame(fp1, fp2);
		// check object contract
		ExaminableProperties.checkAllUnary(fp1);
		ExaminableProperties.checkAllUnary(fp2);
		ExaminableProperties.checkAllBinary(fp1, fp2);
		// success!
		System.out.println("accepted, as expected:  " + name);
	}

}
