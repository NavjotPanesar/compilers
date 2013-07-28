package ece351.f;

import java.io.PrintWriter;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import kodkod.util.collections.IdentityHashSet;
import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.f.ast.FProgram;
import ece351.f.ast.PostOrderFExprVisitor;

public final class TechnologyMapper extends PostOrderFExprVisitor {

	/** Where we will write the output to. */
	private final PrintWriter out;
	
	/**
	 * Table of substitutions for common subexpression elimination. Note that
	 * this IdentityHashMap has non-deterministic iteration ordering. The staff
	 * solution iterates over substitutions.values(), but does not print those
	 * results directly: instead it stores those results in the nodes TreeSet
	 * (below) which sorts them before they are printed. When computing edges
	 * the staff solution uses this table only for lookups and does not iterate
	 * over the contents.
	 */
	private final IdentityHashMap<Expr,Expr> substitutions = new IdentityHashMap<Expr,Expr>();

	/**
	 * The set of nodes in our circuit diagram. (We'll produce a node for each
	 * .) We could just print the nodes directly to the output stream instead of
	 * building up this set, but then we might output the same node twice, and
	 * we might get a nonsensical order. The set uniqueness property ensure that
	 * we will ultimately print each node exactly once. TreeSet gives us
	 * deterministic iteration order: alphabetical.
	 */
	private final SortedSet<String> nodes = new TreeSet<String>();
	
	/**
	 * The set of edges in our circuit diagram. We could just print the edges
	 * directly to the output stream instead of building up this set, but then
	 * we might output the same edge twice. The set uniqueness property ensure
	 * that we will ultimately print each edge exactly once. LinkedHashSet gives
	 * us deterministic iteration order: insertion order. We need insertion
	 * order here because the elements will be inserted into this set by the
	 * post order traversal of the AST.
	 */
	private final Set<String> edges = new LinkedHashSet<String>();
	
	public TechnologyMapper(final PrintWriter out) {
		this.out = out;
	}
	
	public TechnologyMapper() {
		 this(new PrintWriter(System.out));
	}
	
	public static void main(final String arg) {
		main(new String[]{arg});
	}
	public static void main(final String[] args) {
		render(FParser.parse(args), new PrintWriter(System.out));
	}
	
	/**
	 * Translate an FProgram to Graphviz format.
	 */
	public static void render(final FProgram program, final PrintWriter out) {
		final TechnologyMapper tm = new TechnologyMapper(out);
		tm.render(program);
	}

	/** Where the real work happens. */
	public void render(final FProgram program) {
		header(out);

		// build a set of all of the exprs in the program
		IdentityHashSet<Expr> exprs = ExtractAllExprs.allExprs(program);

		// build substitutions by determining equivalences of exprs
		for(Expr current: exprs){
			if(!substitutions.containsKey(current)){
				for(Expr compare:exprs){
					if(compare.isomorphic(current)){
						substitutions.put(compare, current);
					}
				}
				if(current instanceof AndExpr || current instanceof NaryAndExpr){
					node(current.nameID(),current.nameID(),"../../gates/and_noleads.png");
				}
				else if(current instanceof OrExpr || current instanceof NaryOrExpr){
					node(current.nameID(),current.nameID(),"../../gates/or_noleads.png");
				}
				else if(current instanceof NotExpr){
					node(current.nameID(),current.nameID(),"../../gates/not_noleads.png");
				}
			}
		}

		// create nodes for output vars
		// attach images to gates
		// ../../gates/not_noleads.png
		// ../../gates/or_noleads.png
		// ../../gates/and_noleads.png
		// compute edges
		// print nodes
		// print edges
		for(Expr exp : substitutions.values()){
			traverse(exp);
		}
		for(AssignmentStatement ast : program.formulas){
			if(substitutions.containsKey(ast.expr)){
				edge(substitutions.get(ast.expr).nameID(),ast.outputVar.nameID());
			}else{
				edge(ast.expr.nameID(),ast.outputVar.nameID());
			}
		}
		for(String nde : nodes){
			out.println(nde);
		}
		for(String edge : edges){
			out.println(edge);
		}

		// print footer
		footer(out);
		out.flush();
		
		// release memory
		substitutions.clear();
		nodes.clear();
		edges.clear();
	}

	
	private static void header(final PrintWriter out) {
		out.println("digraph g {");
		out.println("    // header");
		out.println("    rankdir=LR;");
		out.println("    margin=0.01;");
		out.println("    node [shape=\"plaintext\"];");
		out.println("    edge [arrowhead=\"diamond\"];");
		out.println("    // circuit ");
	}

	private static void footer(final PrintWriter out) {
		out.println("}");
	}

    /**
     * ConstantExpr follows the Singleton pattern, so we don't need
     * to look in the substitution table: we already know there are
     * only ConstantExpr objects in existence, one for True and one
     * for False.
     */
	@Override
	public Expr visit(final ConstantExpr e) {
		node(e.nameID(), e.toString());
		return e;
	}

	@Override
	public Expr visit(final VarExpr e) {
		final Expr e2 = substitutions.get(e);
		assert e2 != null : "no substitution for " + e + " " + e.nameID();
		node(e2.nameID(), e2.toString());
		return e;
	}

	@Override
	public Expr visit(final NotExpr e) {
		edge(e.expr, e);
		return e;
	}

	@Override
	public Expr visit(final AndExpr e) {
		edge(e.left,e);
		edge(e.right,e);
		return e;
	}

	@Override
	public Expr visit(final OrExpr e) {
		edge(e.left,e);
		edge(e.right,e);
		return e;
	}
	
	@Override 
	public Expr visit(final NaryAndExpr e) {
		for(Expr ec:e.children){
			edge(ec,e);
		}
		return e;
	}

	@Override 
	public Expr visit(final NaryOrExpr e) {
		for(Expr ec:e.children){
			edge(ec,e);
		}
		return e;
	}


	private void node(final String name, final String label) {
		nodes.add("    " + name + "[label=\"" + label + "\"];");
	}

	private void node(final String name, final String label, final String image) {
		nodes.add(String.format("    %s [label=\"%s\", image=\"%s\"];", name, label, image));
	}

	private void edge(final Expr source, final Expr target) {
		edge(substitutions.get(source).nameID(), substitutions.get(target).nameID());
	}
	
	private void edge(final String source, final String target) {
		edges.add("    " + source + " -> " + target + " ;");
	}
}
