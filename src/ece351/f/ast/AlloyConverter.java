package ece351.f.ast;

import java.util.Set;
import java.util.TreeSet;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.BinaryExpr;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.UnaryExpr;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.f.DetermineInputVars;

public final class AlloyConverter extends FExprVisitor {

	private final static String linesep = System.getProperty("line.separator");
	
	private final StringBuilder b = new StringBuilder();

	private AlloyConverter(final Expr e) {
		traverse(e);
	}
	
	public static String convert(final FProgram fp1, final FProgram fp2) {
		final StringBuilder m = new StringBuilder();
		
		m.append(linesep);
		// signatures
		m.append("abstract sig Boolean {}");
		m.append(linesep);
		m.append("one sig true extends Boolean {}");
		m.append(linesep);
		m.append("abstract sig Var { v : lone Boolean }");
		m.append(linesep);
		m.append("one sig  TRUE,FALSE extends Var {}");
		m.append(linesep);
		m.append("fact{");
		m.append(linesep);
		m.append("	some TRUE.v");
		m.append(linesep);
		m.append("	no FALSE.v");
		m.append(linesep);
		m.append("}");
		m.append(linesep);
		m.append("fun _equal[a,b:Var]:Var{_xnor[a,b]}");
		m.append(linesep);
		m.append("fun _xor[a,b:Var]:Var{ _or[_and[a, _not[b]], _and[_not[a], b]] }");
		m.append(linesep);
		m.append("fun _or[a,b:Var]:Var{{v':(a+b) | ((some a.v)or(some b.v))<=>(v' in a+b)}}");
		m.append(linesep);
		m.append("fun _and[a,b:Var]:Var{{v':(a+b) | ((some a.v)and(some b.v))<=>(v' in a+b)}}");
		m.append(linesep);
		m.append("fun _xnor[a,b:Var]:Var{_not[_xor[a,b]]}");
		m.append(linesep);
		m.append("fun _nor[a,b:Var]:Var{_not[_or[a,b]]}");
		m.append(linesep);
		m.append("fun _nand[a,b:Var]:Var{_not[_and[a,b]]}");
		m.append(linesep);
		m.append("fun _not[a:Var]:Var{{v':Var|v'.v!=a.v}}");
		m.append(linesep);
		m.append("one sig ");
		final Set<String> inputVars = new TreeSet<String>();
		inputVars.addAll(DetermineInputVars.inputVars(fp1));
		inputVars.addAll(DetermineInputVars.inputVars(fp2));
		final int inputVarSize1 = inputVars.size()-1;
		int counter = 0;
		for (final String v : inputVars) {
			m.append(sanitize(v));
			if (counter < inputVarSize1) {
				m.append(", ");
				counter++;
			}
		}
		if (inputVars.isEmpty()) {
			m.append("NOVARS");
		}
		m.append(" extends Var {}");
		m.append(linesep);
		
		// predicates
		pred(fp1, m, "1");
		pred(fp2, m, "2");
		
		// check
		m.append("assert equivalent {");
		m.append(linesep);
		for (final AssignmentStatement a : fp1.formulas) {
			m.append("    ");
			m.append(sanitize(a.outputVar));
			m.append("1 <=> ");
			m.append(sanitize(a.outputVar));
			m.append("2");
			m.append(linesep);
		}
		m.append("}");
		m.append(linesep);
		m.append("check equivalent");
		m.append(linesep);
		
		// return result
		return m.toString();
	}

	private static String sanitize(final VarExpr v) {
		return sanitize(v.identifier);
	}

	private static String sanitize(final String varName) {
		return "_" + varName;
	}
	
	private static void pred(final FProgram fp, final StringBuilder m, final String suffix) {
		for (final AssignmentStatement a : fp.formulas) {
			m.append("pred ");
			m.append(sanitize(a.outputVar));
			m.append(suffix);
			m.append("[] {some ");
			m.append((new AlloyConverter(a.expr)).toString());
			m.append(".v}");
			m.append(linesep);
		}
	}
	
	@Override
	public String toString() {
		return b.toString();
	}
	
	@Override
	public Expr visit(final ConstantExpr e) {
		if (e.b) {
			b.append(" TRUE ");
		} else {
			b.append(" FALSE ");
		}
		return e;
	}

	@Override
	public Expr visit(final VarExpr e) {
		//b.append(" (some ");
		b.append(sanitize(e.identifier));
		//b.append(".v) ");
		return e;
	}

	@Override
	public Expr visit(final NotExpr e) {
		b.append(" _not[ ");
		e.expr.accept(this);
		b.append("] ");
		return e;
	}

	@Override
	public Expr visit(final AndExpr e) {
		helperBXE(e);
		return e;
	}

	@Override
	public Expr visit(final OrExpr e) {
		helperBXE(e);
		return e;
	}

	private void helperBE(final BinaryExpr e) {
		b.append(" ( ");
		e.left.accept(this);
		b.append(" ");
		b.append(e.operator());
		b.append(" ");
		e.right.accept(this);
		b.append(" ) ");
	}
	private void helperBXE(final BinaryExpr e) {
		b.append("_"+(e.operator().equals("=")?"equal":e.operator()));
		b.append("[ ");
		e.left.accept(this);
		b.append(", ");
		e.right.accept(this);
		b.append("] ");
	}
	@Override
	public Expr visit(XOrExpr e) {
		helperBXE(e);
		return e;
	}

	@Override
	public Expr visit(NAndExpr e) {
		helperBXE(e);
		return e;
	}

	@Override
	public Expr visit(NOrExpr e) {
		helperBXE(e);
		return e;
	}

	@Override
	public Expr visit(XNOrExpr e) {
		helperBXE(e);
		return e;
	}

	@Override
	public Expr visit(EqualExpr e) {
		helperBXE(e);
		return e;
	}


	@Override
	public Expr visit(final NaryAndExpr e) {
		helperNE(e);
		return e;
	}

	private void helperNE(final NaryExpr e) {		
		final int size1 = e.children.size()-1;
		for(int i=0;i<e.children.size()-1;i++){
			b.append("_"+(e.operator().equals("=")?"equal":e.operator()));
			b.append("[ ");
			e.children.get(i).accept(this);
			b.append(", ");
		}
		e.children.get(size1).accept(this);
		for(int i=0;i<size1;i++){
			b.append("] ");
		}
	}

	@Override
	public Expr visit(final NaryOrExpr e) {
		helperNE(e);
		return e;
	}

	@Override
	public Expr traverse(final NaryExpr e) {
		e.accept(this);
		return e;
	}

	@Override
	public Expr traverse(final BinaryExpr e) {
		e.accept(this);
		return e;
	}

	@Override
	public Expr traverse(final UnaryExpr e) {
		e.accept(this);
		return e;
	}
	
}
