package ece351.f.rdescent;
import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.f.ast.FProgram;
import ece351.util.CommandLine;
import ece351.util.Lexer;
import ece351.util.Todo351Exception;
import ece351.vhdl.VConstants;



public final class FRecursiveDescentParser implements VConstants {
   
	// instance variables
	private final Lexer lexer;

    public FRecursiveDescentParser(String... args) {
    	final CommandLine c = new CommandLine(args);
        lexer = new Lexer(c.readInputSpec());
    }
    
    public FRecursiveDescentParser(final Lexer lexer) {
        this.lexer = lexer;
    }

    public static void main(final String arg) {
    	main(new String[]{arg});
    }
    
    public static void main(final String[] args) {
    	parse(args);
    }

    public static FProgram parse(final String... args) {
        final FRecursiveDescentParser p = new FRecursiveDescentParser(args);
        return p.parse();
    }
    
    public FProgram parse() {
        return program();
    }

    FProgram program() {
    	FProgram fp = new FProgram();
        while (!lexer.inspectEOF()) {
        	fp = fp.append(formula());
        }
        lexer.consumeEOF();
        assert fp.repOk();
        return fp;
    }

    AssignmentStatement formula() {
        final VarExpr var = var();
        lexer.consume("<=");
        final Expr expr = expr();
        lexer.consume(";");
        return new AssignmentStatement(var, expr);
    }
    
    Expr expr() { throw new Todo351Exception(); } // TODO
    Expr term() { throw new Todo351Exception(); } // TODO
    Expr factor() { throw new Todo351Exception(); } // TODO
    VarExpr var() { throw new Todo351Exception(); } // TODO
    ConstantExpr constant() { throw new Todo351Exception(); } // TODO
// TODO: 56 lines snipped

    // helper functions
    private boolean peekConstant() {
        return lexer.inspect("'");
    }

}

