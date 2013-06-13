package ece351.w.parboiled;
import java.io.File;

import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.common.FileUtils;
import org.parboiled.common.ImmutableList;

import ece351.util.BaseParser351;
import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;

@BuildParseTree
//Parboiled requires that this class not be final
public /*final*/ class WParboiledParser extends BaseParser351 {

	/**
	 * Run this parser, exit with error code 1 for malformed input.
	 * Called by wave/Makefile.
	 * @param args
	 */
	public static void main(final String[] args) {
    	process(WParboiledParser.class, FileUtils.readAllText(args[0]));
    }

	/**
	 * Construct an AST for a W program. Use this for debugging.
	 */
	public static WProgram parse(final String inputText) {
		return (WProgram) process(WParboiledParser.class, inputText).resultValue;
	}

	/**
	 * By convention we name the top production in the grammar "Program".
	 */
	@Override
    public Rule Program() {
		return Sequence(
				push(new WProgram()),
				OneOrMore(Waveform()),EOI);
        		// push empty WProgram
    }

	/**
	 * Each line of the input W file represents a "pin" in the circuit.
	 */
    public Rule Waveform() {
    	return Sequence(W0(),Name(),push(new Waveform(match())),
    			W0(),':',W0(),
    			BitString(),
    			W0(),';',
    			W0(),
    			swap(),
    			push(((WProgram)pop()).append(
    					((Waveform)pop())
    					)
    					)
    			);
    			// peek() == [pin name, WProgram]
    			// push empty Waveform with name
    			// peek() = [Waveform, WProgram]
    			// swap, pop, append, push
    			// peek() = [WProgram]
    }

    /**
     * The first token on each line is the name of the pin that line represents.
     */
    public Rule Name() {
    	return Sequence(Letter(),ZeroOrMore(FirstOf(Letter(),'_',CharRange('0','9'))));
    }
    
    /**
     * A Name is composed of a sequence of Letters. 
     * Recall that PEGs incorporate lexing into the parser.
     */
    public Rule Letter() {
    	return FirstOf(CharRange('A','Z'),CharRange('a','z'));
    }

    /**
     * A BitString is the sequence of values for a pin.
     */
    public Rule BitString() {
    	return OneOrMore(W0(),Bit());
    }
    
    /**
     * A BitString is composed of a sequence of Bits. 
     * Recall that PEGs incorporate lexing into the parser.
     */
    public Rule Bit() {
    	return Sequence(
    			FirstOf('0','1'),
    			push(
    					((Waveform)pop()).append(match())
    					));
        		// peek() = [Waveform, WProgram]
    }

}

