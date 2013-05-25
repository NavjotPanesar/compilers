package ece351.f.ast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.VarExpr;
import ece351.util.CommandLine.FSimplifierOptions;
import ece351.util.Examinable;
import ece351.util.Examiner;
import ece351.util.RunAlloy351;


public final class FProgram implements Examinable {
	
    public final ImmutableList<AssignmentStatement> formulas;

    public FProgram() {
    	this.formulas = ImmutableList.of();
	}

    /**
     * Construct a new FProgram from a given immutable list of formulas.
     * We can just alias the argument because it's immutable.
     */
    public FProgram(final ImmutableList<AssignmentStatement> formulas) {
    	this.formulas = formulas;
    }
    
    /**
     * Construct a new FProgram from a given list of formulas.
     * We might need to make a defensive copy if the list is mutable.
     */
    public FProgram(final List<AssignmentStatement> formulas) {
    	if (formulas instanceof ImmutableList<?>) {
    		// argument is immutable, alias it
    		this.formulas = (ImmutableList<AssignmentStatement>)formulas;
    	} else {
    		// argument is mutable, make a defensive copy
    		this.formulas = ImmutableList.copyOf(formulas);
    	}
    }
    
	public boolean repOk() {
    	// some formulas
    	assert formulas != null;
    	assert !formulas.isEmpty();
    	// no duplicate output vars
    	assert formulas.size() == outputVars().size();
    	// representation is ok
    	return true;
    }
    
	public FProgram append(final Object formula) {
		return new FProgram(formulas.append((AssignmentStatement)formula));
	}

    public FProgram simplify() {
    	final List<AssignmentStatement> newformulas = new ArrayList<AssignmentStatement>(formulas.size());
    	for (final AssignmentStatement f : formulas) {
    		newformulas.add(f.simplify());
    	}
    	return new FProgram(newformulas);
    }
    
    public Set<VarExpr> outputVars() {
    	final Set<VarExpr> vars = new TreeSet<VarExpr>();
    	for (final AssignmentStatement f : formulas) {
    		vars.add(f.outputVar);
    	}
    	return Collections.unmodifiableSet(vars);
    }

    @Override
    public String toString() {
		if (formulas == null || formulas.isEmpty()) return "";
		final String sep = System.getProperty("line.separator");
    	final StringBuilder b = new StringBuilder();
    	for (final AssignmentStatement f : formulas) {
    		b.append(f.toString());
			b.append(sep);
    	}
    	return b.substring(0, b.length() - sep.length());
    }
    
	@Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final FProgram that = (FProgram) obj;
		
		// compare field values using Examiner.orderedExamination()
		// no significant differences
// TODO: 4 lines snipped
throw new ece351.util.Todo351Exception();
	}
	
	@Override
	public boolean isomorphic(final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final FProgram that = (FProgram) obj;
		
		// compare field values using Examiner.unorderedExamination()
		// no significant differences
// TODO: 4 lines snipped
throw new ece351.util.Todo351Exception();
	}

	/**
	 * Check that two FPrograms are equivalent by translating them to SAT
	 * and asking a SAT solver to compute the answer.
	 */
	@Override
	public boolean equivalent(final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final FProgram that = (FProgram) obj;
		
		// check that we have the same output variables
		final int size = this.formulas.size();
		if (size != that.formulas.size()) return false;
		final Set<VarExpr> thisOutputVars = this.outputVars();
		final Set<VarExpr> thatOutputVars = that.outputVars();
		if (!thisOutputVars.equals(thatOutputVars)) return false;
		// input variables could be different, because some
		// of them might be effectively don't care
		// so don't need to check input vars
		
		// now the hard part ...
		boolean result = false;
		try {
			// ask a SAT solver if these two FPrograms are equivalent
			result = !RunAlloy351.check(AlloyConverter.convert(this, that));
		} catch (final Exception e) {
			// maybe a system problem happened calling the SAT solver,
			// so let's just do an isomorphism check instead
			System.err.println("Problem running SAT solver for FProgram.equivalent(), resorting to isomorphic()");
			result = isomorphic(obj);
		}
		return result;
	}
}
