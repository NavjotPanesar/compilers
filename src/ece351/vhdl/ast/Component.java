package ece351.vhdl.ast;

import org.parboiled.common.ImmutableList;

import ece351.util.Examinable;
import ece351.util.Examiner;
import ece351.util.Utils351;

public final class Component implements Examinable {
	public final String entityName;
	public final String instanceName;
	public final ImmutableList<String> signalList;
	public Component(
			final String entityName , 
			final String instanceName) {
		this.entityName = entityName;
		this.instanceName = instanceName;
		this.signalList = ImmutableList.of();
	}
	public Component(
			final ImmutableList<String> signals,
			final String entityName , 
			final String instanceName) {
		this.entityName = entityName;
		this.instanceName = instanceName;
		this.signalList = signals;
	}
	
	public Component append(final String signal) {
		return new Component(signalList.append(signal), entityName, instanceName);
	}
	
    @Override
    public String toString() {
        return instanceName + " : entity work." + entityName + " port map( " + Utils351.bitListToString(signalList) + ");";
    }

	@Override
	public int hashCode() {
		return entityName.hashCode();
	}
	
    @Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final Component that = (Component) obj;
		
		// compare field values using Examiner.orderedEquals()
		if (!this.entityName.equals(that.entityName)
			|| !this.instanceName.equals(that.instanceName)
			|| !Examiner.orderedEquals(this.signalList, that.signalList)) return false;
		
		// no significant differences
		return true;
	}
	
	@Override
	public boolean isomorphic(final Examinable obj) {		
		return equals(obj);
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return isomorphic(obj);
	}

}
