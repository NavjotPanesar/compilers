package ece351.f.ast;

import ece351.common.ast.*;

public abstract class PostOrderFExprVisitor extends FExprVisitor {

	@Override
	public Expr traverse(final NaryExpr e) {
// TODO: 5 lines snipped
throw new ece351.util.Todo351Exception();
	}

	@Override
	public Expr traverse(final BinaryExpr b) {
// TODO: 4 lines snipped
throw new ece351.util.Todo351Exception();
	}

	@Override
	public Expr traverse(final UnaryExpr u) {
// TODO: 3 lines snipped
throw new ece351.util.Todo351Exception();
	}
}
