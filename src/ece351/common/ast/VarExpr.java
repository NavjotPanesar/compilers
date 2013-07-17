package ece351.common.ast;

import ece351.util.Examinable;

public final class VarExpr extends Expr {
	
	public String identifier;
	
	public VarExpr(final String name){
		this.identifier = name;
	}
	
	public VarExpr(final Object name) {
		this((String)name);
	}

	@Override
	public final boolean repOk() {
		assert identifier != null : "identifier should not be null";
		assert identifier.length() > 0 : "identifier should be a non-empty string";
		return true;
	}


    public String toString() {
    	return this.identifier;
    }
    
    public Expr accept(final ExprVisitor v){
    	return v.visit(this);
    }

    @Override
    public int hashCode() {
    	return identifier.hashCode();
    }

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Examinable)) return false;
		return isomorphic((Examinable)obj);
	}

	@Override
	public boolean isomorphic(final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!getClass().equals(obj.getClass())) return false;
		final VarExpr that = (VarExpr) obj;
		return this.identifier.equals(that.identifier);
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return isomorphic(obj);
	}

	public String operator() {
		return "var";
	}
}
