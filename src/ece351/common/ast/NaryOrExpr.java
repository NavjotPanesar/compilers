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
// TODO: 1 lines snipped
throw new ece351.util.Todo351Exception();
	}

	@Override
	public ConstantExpr getIdentityElement() {
// TODO: 1 lines snipped
throw new ece351.util.Todo351Exception();
	}
	
	@Override
	protected Class<? extends NaryExpr> getThatClass() {
		return NaryAndExpr.class;
	}

	@Override
	public Expr accept(ExprVisitor v) { return v.visit(this); }

}
