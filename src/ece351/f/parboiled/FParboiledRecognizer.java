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
// TODO: 39 lines snipped
throw new ece351.util.Todo351Exception();
	}

}
