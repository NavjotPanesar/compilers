package ece351.vhdl.ast;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.BinaryExpr;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.UnaryExpr;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.common.ast.ExprVisitor;

public abstract class VExprVisitor extends ExprVisitor {
	public abstract Expr visit(ConstantExpr e);
	public abstract Expr visit(VarExpr e);
	public abstract Expr visit(NotExpr e);
	public abstract Expr visit(AndExpr e);
	public abstract Expr visit(OrExpr e);
	public abstract Expr visit(XOrExpr e);
	public abstract Expr visit(NAndExpr e);
	public abstract Expr visit(NOrExpr e);
	public abstract Expr visit(XNOrExpr e);
	public abstract Expr visit(EqualExpr e);
	public abstract Expr visit(NaryAndExpr e);
	public abstract Expr visit(NaryOrExpr e);

	/**
	 * Dispatch to the appropriate traverse method.
s	 */
	public Expr traverse(final Expr e) {
		if (e instanceof BinaryExpr) {
			return traverse( (BinaryExpr) e );
		} else if (e instanceof NotExpr) {
			return traverse( (NotExpr) e );
		} else if (e instanceof NaryExpr) {
			return traverse( (NaryExpr) e );
		} else {
			return e.accept(this);
		}
	}
	
	public abstract Expr traverse(final BinaryExpr e);
	public abstract Expr traverse(final UnaryExpr e);
	public abstract Expr traverse(final NaryExpr e);
		
	/**
	 * Visit/rewrite all of the exprs in this VProgram.
	 * @param v
	 * @return
	 */
	public VProgram traverse(final VProgram v) {
		assert v.repOk();
		VProgram result = new VProgram();
		
		for (final DesignUnit d : v.designUnits) {
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

				} else {
					throw new IllegalStateException("unknown statement type in VExprVisitor: " + i.getClass());
				}
			}
			
			result = result.append(d.setArchitecture(d.arch.setStatements(architectureStatements)));
		}
		
		assert result.repOk();
		return result;
	}
}
