package ece351.common.ast;

import java.util.List;

import ece351.vhdl.VConstants;


public final class NaryAndExpr extends NaryExpr {

	public NaryAndExpr(final Expr... exprs) {
		super(exprs);
	}

	public NaryAndExpr(final List<Expr> children) {
		super(children);
	}
	
	@Override
	public NaryExpr newNaryExpr(final List<Expr> children) {
		return new NaryAndExpr(children);
	}

	@Override
	public String operator() {
		return VConstants.AND;
	}

	@Override
	public ConstantExpr getIdentityElement() {
		return ConstantExpr.TrueExpr;
	}
	
	@Override
	public ConstantExpr getAbsorbingElement() {
		return ConstantExpr.FalseExpr;
	}
	
	@Override
	protected Class<? extends NaryExpr> getThatClass() {
		return NaryOrExpr.class;
	}

	@Override
	public Expr accept(ExprVisitor v) { return v.visit(this); }
	
}
