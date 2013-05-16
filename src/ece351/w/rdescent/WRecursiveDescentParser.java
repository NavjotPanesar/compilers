package ece351.w.rdescent;

import org.parboiled.common.ImmutableList;

import ece351.util.Lexer;
import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;

public final class WRecursiveDescentParser {
    private final Lexer lexer;

    public WRecursiveDescentParser(final Lexer lexer) {
        this.lexer = lexer;
    }

    public static WProgram parse(final String input) {
    	final WRecursiveDescentParser p = new WRecursiveDescentParser(new Lexer(input));
        return p.parse();
    }

    public WProgram parse() {
    	ece351.w.ast.WProgram prog = new ece351.w.ast.WProgram();
    	do{
	    	String name = lexer.consumeID();
	    	lexer.consume(":");
	    	ece351.w.ast.Waveform wf = new ece351.w.ast.Waveform(name);
	    	wf = wf.append(lexer.consume("0","1"));//Must be at least one digit
	    	while(!lexer.inspect(";")){
	    		wf = wf.append(lexer.consume("0","1"));//Really not efficient, but oh well :)
	    	}
	    	lexer.consume(";");
	    	prog = prog.append(wf);
    	}while(!lexer.inspectEOF());
    	return prog;
    }
}
