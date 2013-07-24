package ece351.f;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;

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
import ece351.f.SimulatorGeneratorMasm32.Register;
import ece351.f.ast.FProgram;
import ece351.f.ast.PostOrderFExprVisitor;
import ece351.util.CommandLine;

import ece351.f.parboiled.FParboiledParser;


public final class SimulatorGeneratorMasm32 extends PostOrderFExprVisitor {

	private PrintWriter out = new PrintWriter(System.out);
	private String indent = "";
	
	private final ArrayList< Expr > operations = new ArrayList< Expr >();
	private final IdentityHashMap< Expr, String > storageMap = new IdentityHashMap< Expr, String >();
	private final LinkedHashMap< String, Boolean > memoryMap = new LinkedHashMap< String, Boolean >();
	private final LinkedList< Register > registerQueue = new LinkedList< Register >();
	private int memId;
	
	public final static class Register {
		public final String Reg32;
		public final String Reg16;
		public final String Reg8L;
		public final String Reg8H;
		
	    public final static Register EAX = new Register( "EAX", "AX", "AH", "AL" );
	    public final static Register EBX = new Register( "EBX", "BX", "BH", "BL" );
	    public final static Register ECX = new Register( "ECX", "CX", "CH", "CL" );
	    public final static Register EDX = new Register( "EDX", "DX", "DH", "DL" );
		public final static Register[] General = { EAX, EBX, ECX, EDX };
	    
		private Register(final String reg32, final String reg16, final String reg8H, final String reg8L) {
			this.Reg32 = reg32;
			this.Reg16 = reg16;
			this.Reg8H = reg8H;
			this.Reg8L = reg8L;
		}
		
	    public String toString() {
	    	return this.Reg32;
	    }
	}

	public static void main(final String arg) {
		main(new String[]{arg});
	}
	
	public static void main(final String[] args) {
		final CommandLine c = new CommandLine(args);
		final SimulatorGeneratorMasm32 s = new SimulatorGeneratorMasm32();
		final PrintWriter pw = new PrintWriter(System.out);
		s.generate(c.getInputName(), "Simulator_" + c.getInputName() + ".dll", FParser.parse(c), pw, pw, pw );
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
	
	private void clearIndent() {
		indent = "";
	}
	
	public void generate(final String fName, final String dllPath, final FProgram program, final PrintWriter outJava, final PrintWriter outDef, final PrintWriter outAsm) {
		final String cleanFName = fName.replace('-', '_');
		final String className = "Simulator_" + cleanFName;
		
		// generate java code
		generateJava(fName, dllPath, program, outJava);
		
		// generate def file
		generateDefintions(className, program, outDef);
		
		// generate assembly code
		generateAssembly(className, program, outAsm);
	}

	@Override
	public Expr traverse(final NaryExpr e) {
		System.err.println("NaryExpr should be converted to BinaryExpr");
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
		out.print(" (");
		traverse(e.left);
		out.print(", ");
		traverse(e.right);
		e.accept(this);
		return e;
	}

	@Override
	public Expr traverse(final UnaryExpr e) {
		out.print(" (");
		traverse(e.expr);
		e.accept(this);
		return e;
	}

	@Override
	public Expr visit(final ConstantExpr e) {
		out.print(Boolean.toString(e.b));
		operations.add( e );
		return e;
	}

	@Override
	public Expr visit(final VarExpr e) {
		out.print(e.identifier);
		operations.add( e );
		return e;
	}

	@Override
	public Expr visit(final NotExpr e) {
		out.print(")not");
		operations.add( e );
		return e;
	}

	@Override
	public Expr visit(final AndExpr e) {
		out.print(")and");
		operations.add( e );
		return e;
	}

	@Override
	public Expr visit(final OrExpr e) {
		out.print(")or");
		operations.add( e );
		return e;
	}
	
	@Override public Expr visit(NaryAndExpr e) { 
		System.err.println("NaryAndExpr should be converted to AndExpr");
		out.print("and(");
		return e; 
	}
	@Override public Expr visit(NaryOrExpr e) { 
		System.err.println("NaryOrExpr should be converted to OrExpr");
		out.print("or(");
		return e; 
	}
	
	public String generateSignature(final AssignmentStatement f) {
		return generateList(f, true );
	}
	
	public String generateCall(final AssignmentStatement f) {
		return generateList(f, false );
	}
	
	public String generateDefintion(final AssignmentStatement f) {
		return generateList(f, true );
	}

	private String generateList(final AssignmentStatement f, final boolean signature ) {
		final StringBuilder b = new StringBuilder();
		if (signature) {
			b.append("public native boolean ");
		}
		b.append("Eval");
		b.append(f.outputVar);
		b.append("(");
		// loop over f's input variables (same as lab8)
		// TODO: 17 lines snipped
		throw new ece351.util.Todo351Exception();
		b.append(")");
		return b.toString();
	}
	
	private String jniSymbol( final String libName, final String methodName)
	{
		return "Java_" + libName.replace( "_", "_1" ) + "_Eval" + methodName;
	}
	
	private void generateDefintions(final String libName, final FProgram f, final PrintWriter outDef)
	{
		this.out = outDef;
		clearIndent();
		
		println("LIBRARY " + libName);
		
		// iterate over assignment statements and print the exports to outDef
		// hint use jniSymbol -> EXPORTS Java_ClassName_Evalx
		
		// TODO: 4 lines snipped
		throw new ece351.util.Todo351Exception();
	}
	
	private void initStorageMaps()
	{
		storageMap.clear();
		registerQueue.clear();
		memoryMap.clear();
		operations.clear();
		memId = 0;
	}
	
	private Register assignRegister(final Expr expr) {
		
		if(registerQueue.size() < Register.General.length) {
			// iterate over Register.General
				// find a free register
				// allocate, assign and return it;
			// TODO: 7 lines snipped
			throw new ece351.util.Todo351Exception();
		}
		else {
			final Register reg = registerQueue.poll();
			registerQueue.add(reg);

			// find the Expr that output to the register (hint iterate storageMap)
			// write an X86 instruction to move the register value to memory (use println)
			
			// update the storageMap
			
			// TODO: 8 lines snipped
			throw new ece351.util.Todo351Exception();
			
			return reg;
		}
		
		System.err.println("Failed to assign register");
		return null;
	}
	
	private Register retrieveRegister(final Expr expr) {
		String storage = storageMap.get(expr);
		
		// iterate over Register.Generate
			// return register if a match
			
		// TODO: 5 lines snipped
		throw new ece351.util.Todo351Exception();
		
		// check if it is in memory
		if(memoryMap.containsKey(storage)) {
			// assign a register to expr
			// free memory
			// write an instruction to move memory to register (use println)
			// return register
			
			// TODO: 6 lines snipped
			throw new ece351.util.Todo351Exception();
		}
		
		System.err.println("Failed to retrieve register");
		return null;
	}
	
	private void releaseRegister( final Register reg, final Expr expr) {
		// free register and de-assign from expr
		// TODO: 2 lines snipped
		throw new ece351.util.Todo351Exception();
	}
	
	private void transferRegister( final Register reg, final Expr from, final Expr to) { 
		
		// assign register to "to" expression, clear "from"
		// TODO: 2 lines snipped
		throw new ece351.util.Todo351Exception();
	}
	
	private String assignMemory(final Expr expr) {
		// iterate over memoryMap keys
			// find empty memory
			// assign and allocate memory if found and return name
		
		String mem = "m" + (memId++);
		// create/allocate new memory and add to memorymap
		// assign (storagemap) and return name
		
		// TODO: 10 lines snipped
		throw new ece351.util.Todo351Exception();
		return mem;
	}
	
	private void generateAssembly(final String libName, final FProgram fProgram, final PrintWriter outAsm)
	{
		this.out = outAsm;
		clearIndent();
		
		// output configuration
		println(".386");
		println(".model flat,stdcall");
		println("option casemap:none");
		println();
		
		// remove NaryExpr and use only AndExpr and OrExpr (accomplished by pretty printing and re-parsing)
		final FProgram fBinary = FParboiledParser.parse(fProgram.toString());
				
		// output prototypes
		{ 
			final Iterator< AssignmentStatement > it = fBinary.formulas.iterator();
			while(it.hasNext()) {
				final AssignmentStatement as = it.next();
				outAsm.print( indent + jniSymbol(libName, as.outputVar.identifier) + " PROTO :DWORD, :DWORD");
				
				// iterate over input variables
				// output a "Byte" for each boolean input
				// TODO: 3 lines snipped
				throw new ece351.util.Todo351Exception();
				println();
			}
		}
		
		// output dllmain
		println("\n.code");
		println("LibMain proc hInstance:DWORD, reason:DWORD, reserved1:DWORD");
		indent();
		println("mov eax, 1");
		println("ret");
		outdent();
		println("LibMain endp");
		
		// output X86 assembly functions
		{ 
			for(final AssignmentStatement as : fBinary.formulas) {
				// output definition
				outAsm.printf("\n" + indent + jniSymbol(libName, as.outputVar.identifier) + " proc JNIEnv:DWORD, jobject:DWORD");
				// iterate over input variables
				// output input name and "Byte" for each boolean input
				// TODO: 5 lines snipped
				throw new ece351.util.Todo351Exception();
				println();
				indent();
				print(indent + "; ");
				
				// initialize storage maps
				initStorageMaps();
				
				// walk the tree and derive the order of operators / loading
				traverse( as.expr );
				println();
				
				// write the instructions to a temporary stream to print to outAsm later
				final StringWriter swInst = new StringWriter();
				this.out = new PrintWriter(swInst);
				
				// output instructions to clear all general registers (ie. EAX)
				// TODO: 2 lines snipped
				throw new ece351.util.Todo351Exception();
				
				// Iterate over all operations to be converted to instructions
				for(final Expr expr : operations) {
					// output assembly instructions for each expression and allocate/memory and registers for inputs and outputs
					
					// if ConstantExpr
					// if VarExpr
					// if BinaryExpr (hint: NaryExpr's have been removed already)
					// if NotExpr (hint: do not need to use conditionals)
					
					// TODO: 29 lines snipped
					throw new ece351.util.Todo351Exception();
				}
				
				// Copy the result of the last expression to EAX if it is not in EAX
				{
					// TODO: 3 lines snipped
					throw new ece351.util.Todo351Exception();
				}
				
				// Stop capturing assembly instructions
				try {	
					swInst.close();
					this.out = outAsm;
				}
				catch(Exception e) {
					System.err.println("Error: " + e.getMessage());
				}
				
				// Write memory allocations to outAsm (hint: iterate memoryMap and use "LOCAL")
				// TODO: 3 lines snipped
				throw new ece351.util.Todo351Exception();
				println();
				
				// Write assembly instructions to outAsm
				print(swInst.toString());
				
				// Write return statement and endp
				println("ret");
				outdent();
				outAsm.println(jniSymbol(libName, as.outputVar.identifier) + " endp");
			}
		}
		
		// End
		outAsm.println( "End LibMain");
	}
	
	public void generateJava( final String cleanFName, final String dllPath, final FProgram program, final PrintWriter outJava )
	{
		this.out = outJava;
		clearIndent();

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
		
		// add code to import the dll
		println("static {");
		indent();
		println("try {");
		indent();
		println("System.load(\"" + dllPath.replace( "\\", "\\\\") + "\");");
		outdent();
		println("} catch( UnsatisfiedLinkError e ) {");
		indent();
		println("  System.err.println(\"Native library failed to load.\" + e);");
		outdent();
		println("}");
		outdent();
		println("}");
		
		println("public static void main(final String[] args) {");
		indent();
		println("// instantiate class to call native methods");
		println("Simulator_" + cleanFName + " sim = new Simulator_"+ cleanFName + "();");
		
		// copy code from lab 8 starting here
		println("// read the input F program");
		println("// write the output");
		println("// read input WProgram");
		println("// construct storage for output");
		println("// loop over each time step");
		println("// values of input variables at this time step");
		println("// values of output variables at this time step");
		println("// store outputs");
		// end the time step loop
		// boilerplate
		println("// write the input");
		println("// write the output");
		// TODO: 58 lines snipped
		throw new ece351.util.Todo351Exception();
		// end main method
		outdent();
		println("}");
		
		println("// declearations for methods to compute values for output pins");
		// TODO: 10 lines snipped (different from lab8 only output the signature/declaration and do not define)
		throw new ece351.util.Todo351Exception();
		// end class
		outdent();
		println("}");
	}
}
