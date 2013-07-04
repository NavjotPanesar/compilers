package ece351.f;

import java.io.PrintWriter;
import java.util.Set;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.BinaryExpr;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.UnaryExpr;
import ece351.common.ast.VarExpr;
import ece351.f.ast.FProgram;
import ece351.f.ast.PreOrderFExprVisitor;
import ece351.util.CommandLine;

public final class SimulatorGenerator extends PreOrderFExprVisitor {

	private PrintWriter out = new PrintWriter(System.out);
	private String indent = "";

	public static void main(final String arg) {
		main(new String[]{arg});
	}
	
	public static void main(final String[] args) {
		final CommandLine c = new CommandLine(args);
		final SimulatorGenerator s = new SimulatorGenerator();
		final PrintWriter pw = new PrintWriter(System.out);
		s.generate(c.getInputName(), FParser.parse(c), pw);
		pw.flush();
	}

	private void println(final String s) {
		out.print(indent);
		out.println(s);
	}
	
	private void println() {
		out.println();
	}
	
	private void print(final String s) {
		out.print(s);
	}

	private void indent() {
		indent = indent + "    ";
	}
	
	private void outdent() {
		indent = indent.substring(0, indent.length() - 4);
	}
	
	

	@Override
	public Expr traverse(final NaryExpr e) {
		e.accept(this);
		final int size = e.children.size();
		for (int i = 0; i < size; i++) {
			final Expr c = e.children.get(i);
			traverse(c);
			if (i < size - 1) {
				// common case
				out.print(", ");
			}
		}
		out.print(") ");
		return e;
	}

	@Override
	public Expr traverse(final BinaryExpr e) {
		e.accept(this);
		traverse(e.left);
		out.print(", ");
		traverse(e.right);
		out.print(") ");
		return e;
	}

	@Override
	public Expr traverse(final UnaryExpr e) {
		e.accept(this);
		traverse(e.expr);
		out.print(") ");
		return e;
	}

	@Override
	public Expr visit(final ConstantExpr e) {
		out.print(Boolean.toString(e.b));
		return e;
	}

	@Override
	public Expr visit(final VarExpr e) {
		out.print(e.identifier);
		return e;
	}

	@Override
	public Expr visit(final NotExpr e) {
		out.print("not(");
		return e;
	}

	@Override
	public Expr visit(final AndExpr e) {
		out.print("and(");
		return e;
	}

	@Override
	public Expr visit(final OrExpr e) {
		out.print("or(");
		return e;
	}
	
	@Override public Expr visit(NaryAndExpr e) { 
		out.print("and(");
		return e; 
	}
	@Override public Expr visit(NaryOrExpr e) { 
		out.print("or(");
		return e; 
	}
	
	public void generate(final String fName, final FProgram program, final PrintWriter out) {
		this.out = out;
		final String cleanFName = fName.replace('-', '_');
		
		// header
		println("import java.util.*;");
		println("import ece351.w.ast.*;");
		println("import ece351.w.parboiled.*;");
		println("import static ece351.util.Boolean351.*;");
		println("import ece351.util.CommandLine;");
		println("import java.io.File;");
		println("import java.io.FileWriter;");
		println("import java.io.StringWriter;");
		println("import java.io.PrintWriter;");
		println("import java.io.IOException;");
		println("import ece351.util.Debug;");
		println();
		println("public final class Simulator_" + cleanFName + " {");
		indent();
		println("public static void main(final String[] args) {");
		indent();
		println("final String s = File.separator;");
		println("final CommandLine cmd = new CommandLine(args);");
		println("final String input = cmd.readInputSpec();");
		println("final WProgram wprogram = WParboiledParser.parse(input);");
		println("final Map<String,StringBuilder> output = new LinkedHashMap<String,StringBuilder>();");
		println("// read the input F program");
		
		
		for(AssignmentStatement ast : program.formulas){
			println("output.put(\""+ast.outputVar+"\",new StringBuilder());");
		}
		
		println("// write the output");
		println("// read input WProgram");
		println("// construct storage for output");
		println("// loop over each time step");
		println("// values of input variables at this time step");
		println("// values of output variables at this time step");
		println("// store outputs");
		
		println("final int timeCount = wprogram.timeCount();");
		println("for (int time = 0; time < timeCount; time++) {");
		indent();
		for(String var : DetermineInputVars.inputVars(program)){
			println("final boolean in_"+var+" = wprogram.valueAtTime(\""+var+"\",time);");
		}
		for(AssignmentStatement ast : program.formulas){
			println("final String out_"+ast.outputVar+" = "+generateCall(ast)+" ? \"1 \" : \"0 \";");
			println("output.get(\""+ast.outputVar+"\").append(out_"+ast.outputVar+");");
		}
		outdent();
		// end the time step loop
		println("}");

		// boilerplate
		println("try {");
		indent();
		println("final File f = cmd.getOutputFile();");
		println("f.getParentFile().mkdirs();");
		println("final PrintWriter pw = new PrintWriter(new FileWriter(f));");
	// write the input
		println("System.out.println(wprogram.toString());");
		println("pw.println(wprogram.toString());");
		// write the output
		println("System.out.println(f.getAbsolutePath());");
		println("for (final Map.Entry<String,StringBuilder> e : output.entrySet()) {");
		indent();
		println("System.out.println(e.getKey() + \":\" + e.getValue().toString()+ \";\");");
		println("pw.write(e.getKey() + \":\" + e.getValue().toString()+ \";\\n\");");
		outdent();
		println("}");
		println("pw.close();");
		outdent();
		println("} catch (final IOException e) {");
		indent();
		println("Debug.barf(e.getMessage());");
		outdent();
		println("}");
		// end main method
		outdent();
		println("}");
		
		println("// methods to compute values for output pins");
		for(AssignmentStatement ast : program.formulas){
			print(indent);
			print(this.generateSignature(ast)+"{ return ");
			traverse(ast.expr);
			print("; }");
			println();
		}
		// end class
		outdent();
		println("}");

	}
	public String generateSignature(final AssignmentStatement f) {
		return generateList(f, true);
	}
	
	public String generateCall(final AssignmentStatement f) {
		return generateList(f, false);
	}

	private String generateList(final AssignmentStatement f, final boolean signature) {
		final StringBuilder b = new StringBuilder();
		if (signature) {
			b.append("public static boolean ");
		}
		b.append(f.outputVar);
		b.append("(");
		// loop over f's input variables
		Set<String> vars = DetermineInputVars.inputVars(f);
		int i = vars.size();
		for(String var : vars){
			i -=1;
			if(signature){
				b.append("final boolean ");
			}
			if(!signature){
				b.append("in_");
			}
			b.append(var);
			if(i != 0){
				b.append(", ");
			}
		}
		b.append(")");
		return b.toString();
	}

}
