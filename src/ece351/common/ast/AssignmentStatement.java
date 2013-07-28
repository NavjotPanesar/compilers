package ece351.common.ast;

import java.util.Set;

import org.parboiled.common.ImmutableList;

import ece351.f.ast.FProgram;
import ece351.util.Examinable;
import ece351.util.CommandLine.FSimplifierOptions;
import ece351.vhdl.ast.Statement;

public final class AssignmentStatement extends Statement implements Examinable {

	public final VarExpr outputVar;
	public final Expr expr;
	
	public AssignmentStatement() {
		outputVar = null;
		expr = null;
	}
	
	public AssignmentStatement(String var, Expr expr)
	{
		this.outputVar = new VarExpr(var);
		this.expr = expr;
	}
	
	public AssignmentStatement(VarExpr var, Expr expr) {
		this.outputVar = var;
		this.expr = expr;
	}

	public AssignmentStatement(Object var) {
		this((VarExpr)var, null);
	}

	public boolean repOk() {
		assert outputVar != null;
		assert expr != null : "expr is null for " + outputVar;
		return true;
	}
	
    @Override
    public String toString() {
        return outputVar + " <= " + expr + ";" + System.getProperty("line.separator");
    }

	@Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(getClass())) return false;
		final AssignmentStatement that = (AssignmentStatement) obj;

		// compare fields
		if (!this.outputVar.equals(that.outputVar)) return false;
		if (!this.expr.equals(that.expr)) return false;

		// no significant differences
		return true;
	}

	@Override
	public boolean isomorphic(final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final AssignmentStatement that = (AssignmentStatement) obj;
		if(!that.outputVar.isomorphic(this.outputVar)) return false;
		if(!that.expr.isomorphic(this.expr)) return false;
		return true;
	}

	/**
	 * Call a SAT solver to compute logical equivalence.
	 */
	@Override
	public boolean equivalent(final Examinable obj) {
		if (!(obj instanceof AssignmentStatement)) return false;
		final FProgram fp1 = new FProgram(ImmutableList.of(this));
		final FProgram fp2 = new FProgram(ImmutableList.of((AssignmentStatement)obj));
		return fp1.equivalent(fp2);
	}
	
	public AssignmentStatement simplify() {
		return new AssignmentStatement(outputVar, expr.simplify());
	}

	public AssignmentStatement setExpr(final Object e) {
		return new AssignmentStatement(outputVar, (Expr)e);
	}
	
	public AssignmentStatement setOutputVar(final VarExpr v) {
		return new AssignmentStatement(v, expr);
	}

	public AssignmentStatement setOutputVar(final String s) {
		return new AssignmentStatement(new VarExpr(s), expr);
	}
}
