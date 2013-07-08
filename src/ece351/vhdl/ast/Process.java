package ece351.vhdl.ast;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.AssignmentStatement;
import ece351.util.Examinable;
import ece351.util.Examiner;
import ece351.util.Utils351;

public final class Process extends Statement implements Examinable {
	public final ImmutableList<String> sensitivityList;
	public final ImmutableList<Statement> sequentialStatements;
	public Process() {
		this.sensitivityList = ImmutableList.of();
		this.sequentialStatements = ImmutableList.of();
	}
	public Process(
			final ImmutableList<Statement> statements,
			final ImmutableList<String> sensitivityList) {
		this.sensitivityList = sensitivityList;
		this.sequentialStatements = statements;
	}
	
	public Process appendSensitivity(final String s) {
		return new Process(sequentialStatements, sensitivityList.append(s));
	}
	
	public Process appendStatement(final Statement s) {
		return new Process(sequentialStatements.append(s), sensitivityList);
	}

	public Process setSensitivityList(final ImmutableList<String> list) {
		return new Process(sequentialStatements, list);
	}
	
	public Process setStatements(final ImmutableList<Statement> list) {
		return new Process(list, sensitivityList);
	}
	
    @Override
    public String toString() {
    	final StringBuilder output = new StringBuilder();
    	output.append("process ( ");
    	output.append(Utils351.bitListToString(sensitivityList));
    	output.append(" ) \n\t\tbegin\n");
    	for (Statement stmt : sequentialStatements) {
    		if (stmt instanceof AssignmentStatement) output.append("\t\t\t");
    		output.append(stmt);
    	}
    	output.append("\t\tend process;\n");
    	return output.toString();
    }

	@Override
	public int hashCode() {
		return 42;
	}
	
    @Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final Process that = (Process) obj;
		
		// compare field values using Examiner.orderedExamination()
		if (!Examiner.orderedEquals(this.sensitivityList, that.sensitivityList)
			|| !Examiner.orderedExamination(Examiner.Equals, this.sequentialStatements, that.sequentialStatements)) return false;
		
		// no significant differences
		return true;
	}
	
	@Override
	public boolean isomorphic(final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final Process that = (Process) obj;
		
		// compare field values using Examiner.orderedExamination()
		// the statements within each process should be ordered, since the statements execute in sequence (and not parallel)
		// however, compare each statement isomorphically
		if (!Examiner.unorderedEquals(this.sensitivityList, that.sensitivityList)
			|| !Examiner.orderedExamination(Examiner.Isomorphic, this.sequentialStatements, that.sequentialStatements)) return false;
		
		// no significant differences
		return true;
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return isomorphic(obj);
	}
}
