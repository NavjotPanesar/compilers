package ece351.common.ast;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import ece351.util.CommandLine.FSimplifierOptions;
import ece351.util.Examinable;

public abstract class Expr implements Comparable<Expr>, Examinable {
	
	private static final AtomicInteger counter = new AtomicInteger();
	public final int serialNumber;
	
	public Expr() {
		serialNumber = counter.getAndIncrement();
	}
	
	/**
	 * Default implementation is to do nothing.
	 * @return this
	 */
	protected Expr simplifyOnce() {
		return this;
	}

	
	/**
	 * Keep applying the simplify method until no more changes occur.
	 * In other words, iterate to a fixed point.
	 * @param e
	 * @return
	 */
	final public Expr simplify() {
		Expr e = this;
		while (true) { // loop forever?
			final Expr simplified = e.simplifyOnce();
			if (simplified.equals(e)) {
				// we're done: nothing changed
				return simplified;
			} else {
				// something changed: keep working
				e = simplified;
			}
		}
	}
	
	public final String nameID(){ return operator() + serialNumber; };
	public abstract Expr accept(final ExprVisitor exprVisitor);

	
	@Override
	public final int compareTo(final Expr e) {
		if (getClass().equals(e.getClass())) {
			// same type
			return toString().compareTo(e.toString());
		} else {
			// different types
			return getClass().getName().compareTo(e.getClass().getName());
		}
	}

	public abstract String operator();
	
	/**
	 * Representation invariant. Assert at the beginning and end of public methods.
	 * @return true if this object is in a legal state
	 */
	public abstract boolean repOk();
	
	/**
	 * Call a SAT solver to compute logical equivalence.
	 */
	@Override
	public final boolean equivalent(final Examinable obj) {
		if (!(obj instanceof Expr)) return false;
		final VarExpr v = new VarExpr("_astmt");
		final AssignmentStatement a1 = new AssignmentStatement(v, this);
		final AssignmentStatement a2 = new AssignmentStatement(v, (Expr)obj);
		return a1.equivalent(a2);
	}
}
