package ece351.vhdl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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
import ece351.vhdl.ast.Component;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.PostOrderVExprVisitor;
import ece351.vhdl.ast.Process;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.VProgram;

/**
 * Inlines logic in components to architecture body.
 */
public final class Elaborator extends PostOrderVExprVisitor {

	private final Map<String, String> current_map = new LinkedHashMap<String, String>();
	
	public static void main(String[] args) {
		System.out.println(elaborate(args));
	}
	
	public static VProgram elaborate(final String[] args) {
		return elaborate(new CommandLine(args));
	}
	
	public static VProgram elaborate(final CommandLine c) {
        final VProgram program = DeSugarer.desugar(c);
        return elaborate(program);
	}
	
	public static VProgram elaborate(final VProgram program) {
		final Elaborator e = new Elaborator();
		return e.elaborateit(program);
	}

	private VProgram elaborateit(final VProgram root) {
			// In the elaborator, an architecture's list of signals, 
			//and set of statements may change (grow)
			//populate dictionary/map
		int num = 0;
		VProgram result = new VProgram();
		for(DesignUnit dunit : root.designUnits){
			Architecture modArch = dunit.arch;
			//dunit.entity.input;
			//dunit.entity.output;
			for(Component comp :dunit.arch.components){
				current_map.clear();
				int signal_id = 0;
				num++;
				//add input signals, map to ports
				//add output signals, map to ports
				//add local signals, add to signal list of i
				DesignUnit source_unit = null;
				for(DesignUnit du : result.designUnits){
					if(du.identifier.equals(comp.entityName)){
						source_unit = du;
						break;
					}
				}
				for(String sig_key: source_unit.entity.input){
					current_map.put(
							sig_key,
							comp.signalList.get(signal_id++)
							);
				}
				for(String sig_key: source_unit.entity.output){
					current_map.put(
							sig_key,
							comp.signalList.get(signal_id++)
							);
				}
				for(String sig_key: source_unit.arch.signals){
					String sig_new = "comp"+num+"_"+sig_key;
					current_map.put(
							sig_key,
							sig_new
							);
					modArch = modArch.appendSignal(sig_new);
				}
				//loop through the statements in the architecture body
				for(Statement ent_stm: source_unit.arch.statements){
					//make the appropriate variable substitutions for signal assignment statements
					//make the appropriate variable substitutions for processes (sensitivity list, if/else body statements)
					Statement elab_stm = null;
					if(ent_stm instanceof Process){
						elab_stm = expandProcessComponent((Process)ent_stm);
					}else if(ent_stm instanceof IfElseStatement){
						elab_stm = changeIfVars((IfElseStatement)ent_stm);
					}else if(ent_stm instanceof AssignmentStatement){
						elab_stm = changeStatementVars((AssignmentStatement)ent_stm);
					}
					modArch = modArch.appendStatement((Statement)elab_stm);
				}
			}
			modArch = modArch.setComponents(ImmutableList.<Component>of());
			result = result.append(dunit.setArchitecture(modArch));
		}
		return result;
	}
	
	// you do not have to use these helper methods; we found them useful though
	private Process expandProcessComponent(final Process process) {
			Process new_proc = new Process();
			for(String sens :process.sensitivityList){
				String s = sens;
				if(current_map.containsKey(sens)){
					s = current_map.get(sens);
				}
				new_proc = new_proc.appendSensitivity(s);
			}
			for(Statement stmt :process.sequentialStatements){
				if(stmt instanceof IfElseStatement){
					new_proc = new_proc.appendStatement(
							changeIfVars((IfElseStatement)stmt)
							);
				}else if(stmt instanceof AssignmentStatement){
					new_proc = new_proc.appendStatement(
							changeStatementVars((AssignmentStatement)stmt)
					);
				}
			}
			return new_proc;
	}
	
	// you do not have to use these helper methods; we found them useful though
	private  IfElseStatement changeIfVars(final IfElseStatement s) {
		IfElseStatement new_stm = new IfElseStatement(traverse(s.condition));
		for(Statement stmt :s.ifBody){
			new_stm = new_stm.appendToTrueBlock(
					changeStatementVars((AssignmentStatement)stmt)
					);
		}
		for(Statement stmt :s.elseBody){
			new_stm = new_stm.appendToElseBlock(
					changeStatementVars((AssignmentStatement)stmt)
					);
		}
		return new_stm;
	}

	// you do not have to use these helper methods; we found them useful though
	private AssignmentStatement changeStatementVars(final AssignmentStatement s){
		return new AssignmentStatement(current_map.get(s.outputVar.identifier),traverse(s.expr));
	}
	
	
	@Override
	public Expr visit(VarExpr e) {
		//  replace/substitute the variable found in the map
		return new VarExpr(current_map.get(e.identifier));
	}
	
	// do not rewrite these parts of the AST
	@Override public Expr visit(ConstantExpr e) { return e; }
	@Override public Expr visit(NotExpr e) { return e; }
	@Override public Expr visit(AndExpr e) { return e; }
	@Override public Expr visit(OrExpr e) { return e; }
	@Override public Expr visit(XOrExpr e) { return e; }
	@Override public Expr visit(EqualExpr e) { return e; }
	@Override public Expr visit(NAndExpr e) { return e; }
	@Override public Expr visit(NOrExpr e) { return e; }
	@Override public Expr visit(XNOrExpr e) { return e; }
	@Override public Expr visit(NaryAndExpr e) { return e; }
	@Override public Expr visit(NaryOrExpr e) { return e; }
}
