package ece351.f.parboiled;

import org.parboiled.Rule;

import ece351.util.CommandLine;
import ece351.vhdl.VConstants;

//Parboiled requires that this class not be final
public /*final*/ class FParboiledRecognizer extends FBase implements VConstants {

	
	public static void main(final String... args) {
		final CommandLine c = new CommandLine(args);
    	process(FParboiledRecognizer.class, c.readInputSpec());
    }

	@Override
	public Rule Program() {
		
		// For the grammar production Id, ensure that the Id does not match any of the keywords specified
		// in the rule, 'Keyword'
		return Sequence(OneOrMore(Formula()),EOI);
	}
    public Rule Formula() {
    	return Sequence(Var(),W0(),'<','=',W0(),Expr(),';',W0());
    }

    public Rule Var() {
    	return Sequence(Letter(),ZeroOrMore(FirstOf(Letter(),'_',CharRange('0','9'))));
    }
    public Rule Letter() {
    	return FirstOf(CharRange('A','Z'),CharRange('a','z'));
    }
    public Rule Expr() {
    	return Sequence(Term(),W0(),ZeroOrMore(Sequence(W0(),OR,W0(),Term())));
    }
    public Rule Term() {
    	return Sequence(Factor(),W0(),ZeroOrMore(Sequence(W0(),AND,W0(),Factor())));
    }
    public Rule Factor() {
    	return FirstOf(Sequence(NOT,W0(),Factor()),
    			Sequence('(',W0(),Expr(),W0(),')',W0()),
    			Var(),
    			Sequence("' ",Constant(),"' ")
    			);
    }
    public Rule Constant() {
    	return FirstOf("0 ","1 ");
    }

}
