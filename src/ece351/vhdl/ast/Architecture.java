package ece351.vhdl.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.AssignmentStatement;
import ece351.f.ast.FProgram;
import ece351.util.Examinable;
import ece351.util.Examiner;
import ece351.util.Tuple;
import ece351.util.Utils351;

public final class Architecture implements Examinable {
	public final String architectureName;
	public final String entityName;
	public final ImmutableList<String> signals;
	public final ImmutableList<Component> components;
	public final ImmutableList<Statement> statements;

	public Architecture(final String ent, final String arch) {
		this.architectureName = arch;
		this.entityName = ent;
		this.statements = ImmutableList.of();
		this.signals = ImmutableList.of();
		this.components = ImmutableList.of();
	}

	public Architecture(final ImmutableList<Statement> statementList,
			final ImmutableList<Component> components,
			final ImmutableList<String> signalList, final String ent,
			final String arch) {
		this.architectureName = arch;
		this.entityName = ent;
		this.statements = statementList;
		this.signals = signalList;
		this.components = components;
	}

	public Architecture appendComponent(final Component c) {
		return new Architecture(statements, components.append(c), signals,
				entityName, architectureName);
	}

	public Architecture appendStatement(final Statement s) {
		return new Architecture(statements.append(s), components, signals,
				entityName, architectureName);
	}

	public Architecture appendSignal(final String signal) {
		return new Architecture(statements, components, signals.append(signal),
				entityName, architectureName);
	}

	public Architecture setSignals(final ImmutableList<String> list) {
		return new Architecture(statements, components, list,
				entityName, architectureName);
	}
	
	public Architecture setComponents(final ImmutableList<Component> list) {
		return new Architecture(statements, list, signals,
				entityName, architectureName);
	}
	
	public Architecture setStatements(final ImmutableList<Statement> list) {
		return new Architecture(list, components, signals,
				entityName, architectureName);
	}
	
	@Override
	public String toString() {
		final StringBuilder output = new StringBuilder();
		output.append("architecture ");
		output.append(architectureName);
		output.append(" of ");
		output.append(entityName);
		output.append(" is\n");
		if (signals.size() > 0) {
			output.append("    signal ");
			output.append(Utils351.bitListToString(signals));
			output.append(" : bit;");
		}
		output.append("\nbegin\n");
		for (Component c : components) {
			output.append("    ");
			output.append(c);
			output.append("\n");
		}
		for (Statement stmt : statements) {
			output.append("    ");
			output.append(stmt);
		}
		output.append("end ");
		output.append(architectureName);
		output.append(";\n");
		return output.toString();
	}

	@Override
	public int hashCode() {
		return architectureName.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null)
			return false;
		if (!obj.getClass().equals(this.getClass()))
			return false;
		final Architecture that = (Architecture) obj;

		// compare field values using Examiner.orderedExamination()
		if (!this.architectureName.equals(that.architectureName)
				|| !this.entityName.equals(that.entityName)
				|| !Examiner.orderedEquals(this.signals, that.signals)
				|| !Examiner.orderedExamination(Examiner.Equals,
						this.components, that.components)
				|| !Examiner.orderedExamination(Examiner.Equals,
						this.statements, that.statements))
			return false;

		// no significant differences
		return true;
	}

	@Override
	public boolean isomorphic(final Examinable obj) {
		return examine(Examiner.Isomorphic, obj);
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return examine(Examiner.Equivalent, obj);
	}

	private boolean examine(final Examiner examiner, final Examinable obj) {
		// basics
		if (obj == null)
			return false;
		if (!obj.getClass().equals(this.getClass()))
			return false;
		final Architecture that = (Architecture) obj;

		// compare field values using Examiner.unorderedExamination()
		if (!this.architectureName.equals(that.architectureName)) return false;
		if (!this.entityName.equals(that.entityName)) return false;
		if (!Examiner.unorderedEquals(this.signals, that.signals)) return false;
		if (!Examiner.unorderedExamination(examiner, this.components, that.components)) return false;
		
		// statements are more complicated
		// special case: all process statements with assignments
		final Tuple<FProgram,List<String>> thisT = this.assignments();
		if (thisT == null) {
			// not the special case, try general case
			return Examiner.unorderedExamination(examiner, this.statements, that.statements);
		} 
		final Tuple<FProgram,List<String>> thatT = that.assignments();
		if (thatT == null) {
			// not the special case, try general case
			return Examiner.unorderedExamination(examiner, this.statements, that.statements);
		} 
		// we're in the special case
		// check sensitivity lists first
		if (!thisT.y.equals(thatT.y)) return false;
		// now check assignments
		return examiner.examine(thisT.x, thatT.x);
	}

	/**
	 * If this Architecture is just a list of processes with assignments, 
	 * then merge the assignments, else return null.
	 */
	private Tuple<FProgram,List<String>> assignments() {
		FProgram fp = new FProgram();
		SortedSet<String> sensitivity = new TreeSet<String>();
		for (final Statement stmt1 : this.statements) {
			if (stmt1 instanceof Process) {
				final Process p = (Process) stmt1;
				for (final Statement stmt2 : p.sequentialStatements) {
					if (stmt2 instanceof AssignmentStatement) {
						final AssignmentStatement a = (AssignmentStatement) stmt2;
						fp = fp.append(a);
						sensitivity.addAll(p.sensitivityList);
					} else {
						return null;
					}
				}
			} else {
				return null;
			}
		}
		final List<String> list = Collections.unmodifiableList(new ArrayList<String>(sensitivity));
		return new Tuple<FProgram,List<String>>(fp, list);
	}
	
	public boolean repOk() {
		assert architectureName != null;
		assert entityName != null;
		assert signals != null;
		assert components != null;
		assert statements != null;
		for (final Component c : components) {
			assert c.repOk();
		}
		for (final Statement s : statements) {
			assert s.repOk();
		}
		return true;
	}

}
