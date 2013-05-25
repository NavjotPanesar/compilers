package ece351.common.ast;

import java.util.Set;

import ece351.util.CommandLine.FSimplifierOptions;
import ece351.vhdl.VConstants;

public final class AndExpr extends CommutativeBinaryExpr {

	public AndExpr(Expr left, Expr right) {
		super(left, right);
	}

	public AndExpr() {this(null, null);}
    
	public AndExpr(Object pop1, Object pop2) {
		this( (Expr)pop1, (Expr)pop2 );
	}

	public Expr accept(final ExprVisitor v) { return v.visit(this); }
    
	@Override
	protected Expr simplifyOnce() {
		return new NaryAndExpr(left, right);
	}

	@Override
	public String operator() {
		return VConstants.AND;
	}
	@Override
	public BinaryExpr newBinaryExpr(final Expr left, final Expr right) {
		return new AndExpr(left, right);
	}
}
