package de.webtwob.agd.project.api;

import de.webtwob.agd.project.api.interfaces.IVerbosity;

public enum VerbosityEnum implements IVerbosity {

	FINEST(Integer.MAX_VALUE), FINER(1000), FINE(100), ONE(1);

	final int level;

	private VerbosityEnum(int level) {
		this.level = level;
	}

	@Override
	public int getLevel() {
		return level;
	}

}
