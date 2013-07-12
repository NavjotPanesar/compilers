package ece351.vhdl;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.util.CommandLine;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.PostOrderVExprVisitor;
import ece351.vhdl.ast.Process;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.VProgram;

public final class DeSugarer extends PostOrderVExprVisitor {

	public DeSugarer() { super(); }
	
	public static void main(final String[] args) {
		System.out.println(desugar(args));
    }
	
	public static VProgram desugar(final String[] args) {
		return desugar(new CommandLine(args));
	}
	
	public static VProgram desugar(final CommandLine c) {
        final VProgram program = VParser.parse(c.readInputSpec());
        return desugar(program);
	}
	
	public static VProgram desugar(final VProgram program) {
		final DeSugarer d = new DeSugarer();
		return d.desugarit(program);
	}
	
	private VProgram desugarit(final VProgram program) {
		VProgram result = new VProgram();
		
		for (final DesignUnit d : program.designUnits) {
			ImmutableList<Statement> architectureStatements = ImmutableList.of();
			
			for (final Statement i : d.arch.statements) {				
				if (i instanceof Process) {
					ImmutableList<Statement> sequentialStatements = ImmutableList.of();
					
					for (final Statement proc_stmt : ((Process) i).sequentialStatements) {
						if (proc_stmt instanceof IfElseStatement) {
							final IfElseStatement ifElseStmt = (IfElseStatement) proc_stmt;
							final Expr condition = traverse(ifElseStmt.condition);
							ImmutableList<AssignmentStatement> ifBody = ImmutableList.of();
							ImmutableList<AssignmentStatement> elseBody = ImmutableList.of();
							
							for(final AssignmentStatement stmt : ifElseStmt.ifBody) {
								ifBody = ifBody.append(new AssignmentStatement(stmt.outputVar, traverse(stmt.expr)));
							}
							for(final AssignmentStatement stmt : ifElseStmt.elseBody) {
								elseBody = elseBody.append(new AssignmentStatement(stmt.outputVar, traverse(stmt.expr)));
							}				
							sequentialStatements = sequentialStatements.append(new IfElseStatement(elseBody, ifBody, condition));
						} else if (proc_stmt instanceof AssignmentStatement) {
							final AssignmentStatement stmt = (AssignmentStatement) proc_stmt;
							sequentialStatements = sequentialStatements.append(new AssignmentStatement(stmt.outputVar, traverse(stmt.expr)));
						}
					}
					
					architectureStatements = architectureStatements.append(new Process(sequentialStatements, ((Process)i).sensitivityList));
				} else if (i instanceof AssignmentStatement) {
					final AssignmentStatement stmt = (AssignmentStatement) i;
					architectureStatements = architectureStatements.append(new AssignmentStatement(stmt.outputVar, traverse(stmt.expr)));
				}
			}
			
			result = result.append(d.setArchitecture(d.arch.setStatements(architectureStatements)));
		}
		
		assert result.repOk();
		return result;
	}
	

	@Override
	public Expr visit(final XOrExpr e) {
		// TODO: rewrite XOR and return new expression
// TODO: 2 lines snipped
throw new ece351.util.Todo351Exception();
	}
	
	@Override
	public Expr visit(final NAndExpr e) {
		// TODO: rewrite NAND and return new expression
// TODO: 1 lines snipped
throw new ece351.util.Todo351Exception();
	}
	
	@Override
	public Expr visit(final NOrExpr e) {
		// TODO: rewrite NOR and return new expression
// TODO: 1 lines snipped
throw new ece351.util.Todo351Exception();
	}
	
	@Override
	public Expr visit(final XNOrExpr e) {
		// TODO: rewrite XNOR and return new expression
// TODO: 2 lines snipped
throw new ece351.util.Todo351Exception();
	}

	@Override
	public Expr visit(final EqualExpr e) {
		//TODO: equals operator has the same truth table as xnor
// TODO: 2 lines snipped
throw new ece351.util.Todo351Exception();
	}

	// these stay the same, no desugaring
	@Override public Expr visit(final ConstantExpr e) { return e; }
	@Override public Expr visit(final VarExpr e) { return e; }
	@Override public Expr visit(final NotExpr e) { return e; }
	@Override public Expr visit(final AndExpr e) { return e; }
	@Override public Expr visit(final OrExpr e) { return e; }
	@Override public Expr visit(final NaryAndExpr e) { return e; }
	@Override public Expr visit(final NaryOrExpr e) { return e; }
}
