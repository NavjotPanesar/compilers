package ece351.vhdl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.parboiled.common.ImmutableList;

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
import ece351.util.CommandLine;
import ece351.vhdl.ast.Architecture;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.PostOrderVExprVisitor;
import ece351.vhdl.ast.Process;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.VProgram;

/**
 * Process splitter.
 */
public final class Splitter extends PostOrderVExprVisitor {
	private final Set<String> usedVarsInExpr = new LinkedHashSet<String>();

	public static void main(String[] args) {
		System.out.println(split(args));
	}
	
	public static VProgram split(final String[] args) {
		return split(new CommandLine(args));
	}
	
	public static VProgram split(final CommandLine c) {
		final VProgram program = DeSugarer.desugar(c);
        return split(program);
	}
	
	public static VProgram split(final VProgram program) {
		VProgram p = Elaborator.elaborate(program);
		final Splitter s = new Splitter();
		return s.splitit(p);
	}

	private VProgram splitit(final VProgram program) {
					// Determine if the process needs to be split into multiple processes
						// Split the process if there are if/else statements so that the if/else statements only assign values to one pin
		VProgram result = new VProgram();
		for(DesignUnit dunit : program.designUnits){
			Architecture modArch = dunit.arch;
			ImmutableList<Statement> final_processes = ImmutableList.<Statement>of();
			for(Statement stm : dunit.arch.statements){
				if(stm instanceof Process){
					Boolean contains_assignments = false;
					Process assignment_proc = new Process().setSensitivityList(((Process)stm).sensitivityList);
					for(Statement p_stm : ((Process)stm).sequentialStatements){
						if(p_stm instanceof IfElseStatement){
							ImmutableList<Statement> split_stm = splitIfElseStatement((IfElseStatement)p_stm);//create a list of processes
							for(Statement split_proc : split_stm){
								final_processes = final_processes.append(split_proc);
							}
						}else{
							contains_assignments = true;
							//this.usedVarsInExpr.clear();
							//this.traverse(((AssignmentStatement)p_stm).expr);
							//for(String sens : usedVarsInExpr){
							//	if(!assignment_proc.sensitivityList.contains(sens)){
							//		assignment_proc = assignment_proc.appendSensitivity(sens);
							//	}
							//}
							assignment_proc = assignment_proc.appendStatement(p_stm);
						}
					}
					if(contains_assignments){
						final_processes = final_processes.append(assignment_proc);
					}
				}else{
					final_processes = final_processes.append(stm);
				}
			}
			modArch = modArch.setStatements(final_processes);
			result = result.append(dunit.setArchitecture(modArch));
		}
		return result;
	}
	
	// You do not have to use this helper method, but we found it useful
	
	private ImmutableList<Statement> splitIfElseStatement(final IfElseStatement ifStmt) {
		ImmutableList<Statement> processes = ImmutableList.<Statement>of();
		for(Statement if_stm : ifStmt.ifBody){
			String if_output = ((AssignmentStatement)if_stm).outputVar.identifier;
			for(Statement else_stm :ifStmt.elseBody){
				if(if_output.equals(((AssignmentStatement)else_stm).outputVar.identifier)){
					this.usedVarsInExpr.clear();
					this.traverse(ifStmt.condition);
					this.traverse(((AssignmentStatement)if_stm).expr);
					this.traverse(((AssignmentStatement)else_stm).expr);
					Process split_proc = new Process();
					IfElseStatement split_if = new IfElseStatement(ImmutableList.<AssignmentStatement>of().append((AssignmentStatement)else_stm),
																ImmutableList.<AssignmentStatement>of().append((AssignmentStatement)if_stm),
																ifStmt.condition);
					split_proc = split_proc.appendStatement(split_if);
					for(String sens : usedVarsInExpr){
						split_proc = split_proc.appendSensitivity(sens);
					}
					processes = processes.append(split_proc);
				}
			}
		}
		return processes;
		// loop over each statement in the ifBody
			// loop over each statement in the elseBody
				// check if outputVars are the same
					// initialize/clear this.usedVarsInExpr
					// call traverse a few times to build up this.usedVarsInExpr
					// build sensitivity list from this.usedVarsInExpr
					// build the resulting list of split statements
		// return result
	}

	@Override
	public Expr visit(final VarExpr e) {
		this.usedVarsInExpr.add(e.identifier);
		return e;
	}

	@Override public Expr visit(ConstantExpr e) { return e; }
	@Override public Expr visit(NotExpr e) { return e; }
	@Override public Expr visit(AndExpr e) { return e; }
	@Override public Expr visit(OrExpr e) { return e; }
	@Override public Expr visit(XOrExpr e) { return e; }
	@Override public Expr visit(NAndExpr e) { return e; }
	@Override public Expr visit(NOrExpr e) { return e; }
	@Override public Expr visit(XNOrExpr e) { return e; }
	@Override public Expr visit(EqualExpr e) { return e; }
	@Override public Expr visit(NaryAndExpr e) { return e; }
	@Override public Expr visit(NaryOrExpr e) { return e; }

}
