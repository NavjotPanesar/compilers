package ece351.vhdl.ast;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.BinaryExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NaryExpr;
import ece351.common.ast.UnaryExpr;

/**
 * This visitor rewrites the tree as it goes.
 */
public abstract class PostOrderVExprVisitor extends VExprVisitor {

	@Override
	public Expr traverse(BinaryExpr b) {
		// children first
		final Expr left = traverse(b.left);
		final Expr right = traverse(b.right);
		// only rewrite if something has changed
		if (left != b.left || right != b.right) {
			b = b.newBinaryExpr(left, right);
		}
		// now parent
		return b.accept(this);
	}

	@Override
	public Expr traverse(UnaryExpr u) {
		// child first
		final Expr child = traverse(u.expr);
		// only rewrite if something has changed
		if (child != u.expr) {
			u = u.newUnaryExpr(child);
		}
		// now parent
		return u.accept(this);
	}
	
	@Override
	public Expr traverse(NaryExpr e) {
		// children first
		ImmutableList<Expr> children = ImmutableList.of();
		boolean change = false;
		for (final Expr c1 : e.children) {
			final Expr c2 = traverse(c1);
			children = children.append(c2);
			if (c2 != c1) { change = true; }
		}
		// only rewrite if something changed
		if (change) {
			e = e.newNaryExpr(children);
		}
		// now parent
		return e.accept(this);
	}

}
