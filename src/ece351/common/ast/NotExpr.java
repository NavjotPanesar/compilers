package ece351.common.ast;

import java.util.Set;

import ece351.util.CommandLine.FSimplifierOptions;
import ece351.vhdl.VConstants;

public final class NotExpr extends UnaryExpr{
	public NotExpr(Expr argument) {
		super(argument);
	}

	public NotExpr(Object pop) {
		this( (Expr)pop );
	}

	public NotExpr() { this(null); }
	
	@Override
    protected final Expr simplifyOnce() {		
    	// simplify our child
    	final Expr localexpr = expr.simplify();
    	if(localexpr.equivalent(ConstantExpr.TrueExpr)) return ConstantExpr.FalseExpr;// !true = false
    	if(localexpr.equivalent(ConstantExpr.FalseExpr)) return ConstantExpr.TrueExpr;// !false = true
    	if(NotExpr.class.isInstance(localexpr)) return ((NotExpr)localexpr).expr;// !!x = x
    	return newUnaryExpr(localexpr);// nothing changed// something changed
    }
	
    public Expr accept(final ExprVisitor v){
    	return v.visit(this);
    }
	
	@Override
	public String operator() {
		return VConstants.NOT;
	}
	@Override
	public UnaryExpr newUnaryExpr(final Expr expr) {
		return new NotExpr(expr);
	}

}
