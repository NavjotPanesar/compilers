package ece351.common.ast;

import java.util.Set;

import ece351.util.CommandLine.FSimplifierOptions;
import ece351.vhdl.VConstants;


public final class OrExpr extends CommutativeBinaryExpr{
	
	public OrExpr(Expr left, Expr right) {
		super(left,right);
	}
	public OrExpr() {this(null, null);}
    
    public OrExpr(Object pop1, Object pop2) {
    	this( (Expr)pop1, (Expr)pop2 );
	}
	public Expr accept(final ExprVisitor v){
    	return v.visit(this);
    }

	@Override
	public Expr simplifyOnce() {
		return new NaryOrExpr(left, right);
	}
	@Override
	public String operator() {
		return VConstants.OR;
	}
	@Override
	public BinaryExpr newBinaryExpr(final Expr left, final Expr right) {
		return new OrExpr(left, right);
	}
}
