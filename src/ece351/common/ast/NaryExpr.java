package ece351.common.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.parboiled.common.ImmutableList;

import ece351.util.Examinable;
import ece351.util.Examiner;
import ece351.vhdl.VConstants;

/**
 * An expression with multiple children. Must be commutative.
 */
public abstract class NaryExpr extends Expr {

	public final ImmutableList<Expr> children;

	public NaryExpr(final Expr... exprs) {
		Arrays.sort(exprs);
		ImmutableList<Expr> c = ImmutableList.of();
		for (final Expr e : exprs) {
			c = c.append(e);
		}
		this.children = c;
	}

	public NaryExpr(final List<Expr> children) {
		final ArrayList<Expr> a = new ArrayList<Expr>(children);
		Collections.sort(a);
		this.children = ImmutableList.copyOf(a);
	}

	/**
	 * Each subclass must implement this factory method to return a new object
	 * of its own type.
	 */
	public abstract NaryExpr newNaryExpr(final List<Expr> children);

	/**
	 * Construct a new NaryExpr (of the appropriate subtype) with one extra
	 * child.
	 * 
	 * @param e
	 *            the child to append
	 * @return a new NaryExpr
	 */
	public NaryExpr append(final Expr e) {
		return newNaryExpr(children.append(e));
	}

	/**
	 * Construct a new NaryExpr (of the appropriate subtype) with the extra
	 * children.
	 * 
	 * @param list
	 *            the children to append
	 * @return a new NaryExpr
	 */
	public NaryExpr appendAll(final List<Expr> list) {
		final List<Expr> a = new ArrayList<Expr>(children.size() + list.size());
		a.addAll(children);
		a.addAll(list);
		return newNaryExpr(a);
	}

	/**
	 * Check the representation invariants.
	 */
	public boolean repOk() {
		// programming sanity
		assert this.children != null;
		// should not have a single child: indicates a bug in simplification
		assert this.children.size() > 1 : "should have more than one child, probably a bug in simplification";
		// check that children is sorted
		int i = 0;
		for (int j = 1; j < this.children.size(); i++, j++) {
			final Expr x = this.children.get(i);
			assert x != null : "null children not allowed in NaryExpr";
			final Expr y = this.children.get(j);
			assert y != null : "null children not allowed in NaryExpr";
			assert x.compareTo(y) <= 0 : "NaryExpr.children must be sorted";
		}
		// no problems found
		return true;
	}

	/**
	 * The name of the operator represented by the subclass. To be implemented
	 * by each subclass.
	 */
	public abstract String operator();

	/**
	 * The complementary operation: NaryAnd returns NaryOr, and vice versa.
	 */
	abstract protected Class<? extends NaryExpr> getThatClass();

	/**
	 * e op x = e for absorbing element e and operator op.
	 * 
	 * @return
	 */
	public abstract ConstantExpr getAbsorbingElement();

	/**
	 * e op x = x for identity element e and operator op.
	 * 
	 * @return
	 */
	public abstract ConstantExpr getIdentityElement();

	@Override
	public final String toString() {
		final StringBuilder b = new StringBuilder();
		b.append("(");
		int count = 0;
		for (final Expr c : children) {
			b.append(c);
			if (++count < children.size()) {
				b.append(" ");
				b.append(operator());
				b.append(" ");
			}

		}
		b.append(")");
		return b.toString();
	}

	@Override
	public final int hashCode() {
		return 17 + children.hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		if (!(obj instanceof Examinable))
			return false;
		return examine(Examiner.Equals, (Examinable) obj);
	}

	@Override
	public final boolean isomorphic(final Examinable obj) {
		return examine(Examiner.Isomorphic, obj);
	}

	@Override
	public final boolean equivalent(final Examinable obj) {
		return examine(Examiner.Isomorphic, obj);
	}

	private boolean examine(final Examiner e, final Examinable obj) {
		// basics
		if (obj == null)
			return false;
		if (!this.getClass().equals(obj.getClass()))
			return false;
		final NaryExpr that = (NaryExpr) obj;
		assert repOk();
		assert that.repOk();

		// if the number of children are different, consider them not equivalent
		// since the n-ary expressions have the same number of children and they
		// are sorted, just iterate and check
		// no significant differences
		if(this.children.size() != that.children.size()) return false;
		for(int i = 0; i < children.size(); i++){
			if(!this.children.get(i).equals(that.children.get(i)))return false;
		}
		return true;
	}

	@Override
	protected final Expr simplifyOnce() {
		assert repOk();
		final Expr result = simplifyChildren().mergeGrandchildren()
				.removeIdentityElements().removeDuplicates().simpleAbsorption()
				.subsetAbsorption().singletonify();
		assert result.repOk();
		return result;
	}

	private NaryExpr simplifyChildren() {
		final ImmutableList<Expr> emptyList = ImmutableList.of();
		NaryExpr result = newNaryExpr(emptyList);
		for (final Expr e : children) {
			result = result.append(e.simplify());
		}
		// note: we do not assert repOk() here because the rep might not be ok
		// the result might contain duplicate children, and the children
		// might be out of order
		return result;
	}

	private NaryExpr mergeGrandchildren() {
		// extract children to merge using filter (because they are the same
		// type as us)
		// remove children to merge from result by using filter
		// merge in the grandchildren
		ArrayList<Expr> matching_children = new ArrayList<Expr>();
		for (Expr e : this.children) {
			if (e.getClass().equals(this.getClass())) {
				matching_children.addAll(((NaryExpr) e).children);
			}	
		}
		return this.appendAll(matching_children).filter(this.getClass(), false);
	}

	private NaryExpr removeDuplicates() {
		// remove duplicate children: x.x=x and x+x=x
		// since children are sorted this is fairly easy
		// no changes
		// removed some duplicates
		if (this.children.size() > 1) {
			ArrayList<Expr> new_children = new ArrayList<Expr>();
			new_children.add(this.children.get(0));
			for (int i = 0; i < this.children.size() - 1; i++) {
				if (!this.children.get(i).equals(this.children.get(i + 1))) {
					new_children.add(this.children.get(i + 1));
				}
			}
			return newNaryExpr(new_children);
		}
		return this;
	}

	private NaryExpr removeIdentityElements() {
		// if we have only one child stop now and return self
		if (this.children.size() == 1)
			return this;
		ArrayList<Expr> list_clean = new ArrayList<Expr>();
		// we have multiple children, remove the identity elements
		// all children were identity elements, so now our working list is empty
		// return a new list with a single identity element
		for (Expr e : this.children) {
			if (!e.equivalent(getIdentityElement()))
				list_clean.add(e);
		}
		if (list_clean.size() == 0)
			list_clean.add(getIdentityElement());
		return newNaryExpr(list_clean);
	}

	private NaryExpr simpleAbsorption() {
		// (x.y) + x ... = x ...
		// check if there are any conjunctions that can be removed
		List<Expr> opt_children = new ArrayList<Expr>();
		opt_children.addAll(this.children);
		if (opt_children.contains(this.getIdentityElement())) {
			opt_children.clear();
			opt_children.add(this.getIdentityElement());
		} else {
			for (int i = this.children.size() - 1; i >= 0; i--) {
				Expr var_term = this.children.get(i);
				if (VarExpr.class.isInstance(var_term)) {
					for (int k = i - 1; k >= 0; k--) {
						Expr e = this.children.get(k);
						if (NaryExpr.class.isInstance(e)) {
							if (((NaryExpr) e).contains(var_term,Examiner.Equals)) {
								opt_children.remove(e);
							}
						}else if(NotExpr.class.isInstance(e) && ((NotExpr) e).expr.equals(var_term)) {
							// Case of x.!x
							opt_children.clear();
							opt_children.add(getAbsorbingElement());
							return this.newNaryExpr(opt_children);
							// find all negations
							// for each negation, see if we find its complement
							// found matching negation and its complement
							// return absorbing element
						}
					}
				}
			}
		}
		return newNaryExpr(opt_children);
	}

	private NaryExpr subsetAbsorption() {
		List<Expr> opt_children = new ArrayList<Expr>();
		opt_children.addAll(this.children);
		for (int i = this.children.size() - 1; i >= 0; i--) {
			Expr nary_term = this.children.get(i);
			if (NaryExpr.class.isInstance(nary_term)) {
				for (int k = i - 1; k >= 0; k--) {
					Expr e = this.children.get(k);
					if (NaryExpr.class.isInstance(e)) {
						Boolean contains_all = true;
						for(Expr term : ((NaryExpr)nary_term).children){
							if (!((NaryExpr) e).contains(term,Examiner.Equals)) contains_all = false;
						}
						if(contains_all){
							opt_children.remove(e);
						}
					}
				}
			}
		}
		// check if there are any conjunctions that are supersets of others
		// e.g., ( a . b . c ) + ( a . b ) = a . b
		return newNaryExpr(opt_children);
	}

	private Expr singletonify() {
		// if we have only one child, return it
		// absorbing element: 0.x=0 and 1+x=1
		// collapse complements
		// x op !x = absorbing element
		// nothing to do, return self
		if (this.children.size() == 1) return this.children.get(0);
		if(this.children.contains(this.getAbsorbingElement())) return this.getAbsorbingElement();
		return this;
	}

	/**
	 * Return a new NaryExpr with only the children of a certain type, or
	 * excluding children of a certain type.
	 * 
	 * @param cls
	 * @param match
	 * @return
	 */
	public final NaryExpr filter(final Class<? extends Expr> filter,
			final boolean shouldMatchFilter) {
		ImmutableList<Expr> l = ImmutableList.of();
		for (final Expr child : children) {
			if (child.getClass().equals(filter)) {
				if (shouldMatchFilter) {
					l = l.append(child);
				}
			} else {
				if (!shouldMatchFilter) {
					l = l.append(child);
				}
			}
		}
		return newNaryExpr(l);
	}

	public final NaryExpr filter(final Expr filter, final Examiner examiner,
			final boolean shouldMatchFilter) {
		ImmutableList<Expr> l = ImmutableList.of();
		for (final Expr child : children) {
			if (examiner.examine(child, filter)) {
				if (shouldMatchFilter) {
					l = l.append(child);
				}
			} else {
				if (!shouldMatchFilter) {
					l = l.append(child);
				}
			}
		}
		return newNaryExpr(l);
	}

	public final NaryExpr removeAll(final List<Expr> toRemove,
			final Examiner examiner) {
		NaryExpr result = this;
		for (final Expr e : toRemove) {
			result = result.filter(e, examiner, false);
		}
		return result;
	}

	public final boolean contains(final Expr expr, final Examiner examiner) {
		for (final Expr child : children) {
			if (examiner.examine(child, expr)) {
				return true;
			}
		}
		return false;
	}

}
