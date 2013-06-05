package ece351.common.ast;

import java.util.List;

import ece351.vhdl.VConstants;

public final class NaryOrExpr extends NaryExpr {

	public NaryOrExpr(final Expr... exprs) {
		super(exprs);
	}
	
	public NaryOrExpr(final List<Expr> children) {
		super(children);
	}
	
	@Override
	public NaryExpr newNaryExpr(final List<Expr> children) {
		return new NaryOrExpr(children);
	}

	@Override
	public String operator() {
		return VConstants.OR;
	}

	@Override
	public ConstantExpr getAbsorbingElement() {
		return ConstantExpr.TrueExpr;
	}

	@Override
	public ConstantExpr getIdentityElement() {
		return ConstantExpr.FalseExpr;
	}
	
	@Override
	protected Class<? extends NaryExpr> getThatClass() {
		return NaryAndExpr.class;
	}

	@Override
	public Expr accept(ExprVisitor v) { return v.visit(this); }

}
