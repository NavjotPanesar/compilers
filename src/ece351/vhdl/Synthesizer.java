package ece351.vhdl;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.f.ast.FProgram;
import ece351.util.CommandLine;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.PostOrderVExprVisitor;
import ece351.vhdl.ast.Process;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.VProgram;

/**
 * Translates VHDL to F.
 */
public final class Synthesizer extends PostOrderVExprVisitor {
	
	private String varPrefix;
	private int condCount;
	private static String conditionPrefix = "condition";
	
	public static void main(String[] args) { 
		System.out.println(synthesize(args));
	}
	
	public static FProgram synthesize(final String[] args) {
		return synthesize(new CommandLine(args));
	}
	
	public static FProgram synthesize(final CommandLine c) {
        final VProgram program = DeSugarer.desugar(c);
        return synthesize(program);
	}
	
	public static FProgram synthesize(final VProgram program) {
		VProgram p = Splitter.split(program);
		final Synthesizer synth = new Synthesizer();
		return synth.synthesizeit(p);
	}
	
	public Synthesizer(){
		condCount = 0;
	}
		
	private FProgram synthesizeit(final VProgram root) {	
		FProgram result = new FProgram();
			// set varPrefix for this design unit
		for(DesignUnit dunit : root.designUnits){
			this.varPrefix = dunit.arch.entityName;
			for(Statement stm : dunit.arch.statements){
				if(stm instanceof Process){
					for(Statement p_stm : ((Process)stm).sequentialStatements){
						if(p_stm instanceof IfElseStatement){
							//for(AssignmentStatement f : ((FProgram)this.implication((IfElseStatement)p_stm)).formulas){
							//	result = result.append(f);
							//}
							result = result.appendAll((FProgram)this.implication((IfElseStatement)p_stm));
						}else{
							result = result.append(
									new AssignmentStatement(
									(VarExpr)this.traverse((Expr)((AssignmentStatement)p_stm).outputVar),
									this.traverse(((AssignmentStatement)p_stm).expr))
									);
						}
					}
				}else{
					result = result.append(
							new AssignmentStatement(
							(VarExpr)this.traverse((Expr)((AssignmentStatement)stm).outputVar),
							this.traverse(((AssignmentStatement)stm).expr))
							);
				}
			}
		}
		return result;
	}
	
	private FProgram implication(final IfElseStatement statement) {
		// error checking
		if( statement.ifBody.size() != 1) {
			throw new IllegalArgumentException("if/else statement: " + statement + "\n can only have one assignment statement in the if-body and else-body where the output variable is the same!");
		}
		if (statement.elseBody.size() != 1) {
			throw new IllegalArgumentException("if/else statement: " + statement + "\n can only have one assignment statement in the if-body and else-body where the output variable is the same!");
		}
		final AssignmentStatement ifb = statement.ifBody.get(0);
		final AssignmentStatement elb = statement.elseBody.get(0);
		if (!ifb.outputVar.equals(elb.outputVar)) {
			throw new IllegalArgumentException("if/else statement: " + statement + "\n can only have one assignment statement in the if-body and else-body where the output variable is the same!");
		}
		FProgram result = new FProgram();
		this.condCount++;
		VarExpr condition = new VarExpr(conditionPrefix+condCount);
		result = result.append(new AssignmentStatement(condition,this.traverse(statement.condition)));
		result = result.append(new AssignmentStatement(varPrefix+ifb.outputVar,
				new OrExpr(
						new AndExpr(condition,this.traverse(ifb.expr)),
						new AndExpr(new NotExpr(condition),this.traverse(elb.expr))
						))
				);
		return result;
	}

	/** Rewrite var names with prefix to mitigate name collision. */
	@Override
	public Expr visit(final VarExpr e) {
		return new VarExpr(varPrefix + e.identifier);
	}
	
	@Override public Expr visit(ConstantExpr e) { return e; }
	@Override public Expr visit(NotExpr e) { return e; }
	@Override public Expr visit(AndExpr e) { return e; }
	@Override public Expr visit(OrExpr e) { return e; }
	@Override public Expr visit(NaryAndExpr e) { return e; }
	@Override public Expr visit(NaryOrExpr e) { return e; }
	
	// We shouldn't see these in the AST, since F doesn't support them
	// They should have been desugared away previously
	@Override public Expr visit(XOrExpr e) { throw new IllegalStateException("xor not desugared"); } 
	@Override public Expr visit(EqualExpr e) { throw new IllegalStateException("EqualExpr not desugared"); }
	@Override public Expr visit(NAndExpr e) { throw new IllegalStateException("nand not desugared"); }
	@Override public Expr visit(NOrExpr e) { throw new IllegalStateException("nor not desugared"); }
	@Override public Expr visit(XNOrExpr e) { throw new IllegalStateException("xnor not desugared"); }
	
}



