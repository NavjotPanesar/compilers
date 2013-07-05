package ece351.f.parboiled;

import org.parboiled.Rule;
import ece351.util.BaseParser351;

public abstract class FBase extends BaseParser351 {
	
    Rule Char() {
        return FirstOf(CharRange('a', 'z'),
                       CharRange('A', 'Z'));
    }
	
    Rule Digit() {
    	return CharRange('0', '9');
    }
	
    public Rule Keyword() {
        return FirstOf(AND(),
                       OR(),
                       NOT());
    }
	
    public Rule AND() {
        return Sequence(IgnoreCase("and"),
                        TestNot(FirstOf(Char(),Digit(), "_")));
    }

    public Rule OR() {
        return Sequence(IgnoreCase("or"),
                        TestNot(FirstOf(Char(),Digit(), "_")));
    }

    public Rule NOT() {
        return Sequence(IgnoreCase("not"),
                        TestNot(FirstOf(Char(),Digit(), "_")));
    }

}
