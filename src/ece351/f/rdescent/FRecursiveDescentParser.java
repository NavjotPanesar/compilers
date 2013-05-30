package ece351.f.rdescent;
import org.omg.PortableServer.IdAssignmentPolicy;
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
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;



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
    Expr expr() {
    	Expr expr = term();
        while(lexer.inspect(OR)){
            lexer.consume(OR);
            expr = new OrExpr(expr,term());
        }
        return expr;
    }
    Expr term() {
        Expr term = factor();
        while(lexer.inspect(AND)){
            lexer.consume(AND);
            term = new AndExpr(term,factor());
        }
        return term;
    }
    Expr factor() {
        if(lexer.inspect(NOT)){
        	lexer.consume(NOT);
        	return new NotExpr(factor());
        }else if(lexer.inspect("(")){
            lexer.consume("(");
            Expr value =  expr();
            lexer.consume(")");
            return value;
        }else if(peekConstant()){// we have a constant
            return constant();
        }else{
            return var();
        }
    }
    VarExpr var() { 
        return new VarExpr(lexer.consumeID());
    }
    ConstantExpr constant() {
        lexer.consume("'");
        ConstantExpr result = ConstantExpr.make(lexer.consume("0","1"));
        lexer.consume("'");
        return result;
    }
    // helper functions
    private boolean peekConstant() {
        return lexer.inspect("'");
    }

}

