package ece351.vhdl.ast;

import org.parboiled.common.ImmutableList;

import ece351.util.Examinable;
import ece351.util.Examiner;
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
		// basics
		if (obj == null)
			return false;
		if (!obj.getClass().equals(this.getClass()))
			return false;
		final Architecture that = (Architecture) obj;

		// compare field values using Examiner.unorderedExamination()
		if (!this.architectureName.equals(that.architectureName)
				|| !this.entityName.equals(that.entityName)
				|| !Examiner.unorderedEquals(this.signals, that.signals)
				|| !Examiner.unorderedExamination(Examiner.Isomorphic,
						this.components, that.components)
				|| !Examiner.unorderedExamination(Examiner.Isomorphic,
						this.statements, that.statements))
			return false;

		// no significant differences
		return true;
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return isomorphic(obj);
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
