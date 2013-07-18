package ece351.common.ast;

import ece351.util.Examinable;

/**
 * Represents either true (1) or false (0).
 * The Singleton design pattern is used here so that this class is
 * instantiated exactly twice.
 * @see http://en.wikipedia.org/wiki/Singleton_pattern
 *
 */
public final class ConstantExpr extends Expr {
	public final Boolean b;

    public final static ConstantExpr TrueExpr = new ConstantExpr(true);
    public final static ConstantExpr FalseExpr = new ConstantExpr(false);
    
    private ConstantExpr(final Boolean b) { this.b = b; }

	@Override
	public final boolean repOk() {
		assert b != null : "b should not be null";
		return true;
	}


    
    public static ConstantExpr make(final String s) {
    	return make(s.startsWith("1"));
    }
    
    public static ConstantExpr make(final Boolean b) {
    	if (b) {
    		return TrueExpr;
    	} else {
    		return FalseExpr;
    	}
    }
    
    public String toString() {
    	return (this.b ? "'1'" : "'0'");
    }
    
    public Expr accept(final ExprVisitor v){
    	return v.visit(this);
    }

	@Override
	public String operator() {
		return "Const";
	}

	@Override
	public int hashCode() {
		return Boolean.valueOf(b).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final ConstantExpr that = (ConstantExpr) obj;
		
		// compare field values
		// no significant differences
// TODO: 4 lines snipped
throw new ece351.util.Todo351Exception();
	}
	
	@Override
	public boolean isomorphic(final Examinable obj) {
		return equals(obj);
	}

}
