package de.webtwob.agd.project.api.enums;

import de.webtwob.agd.project.api.interfaces.IVerbosity;

public enum VerbosityEnum implements IVerbosity {

	ALLWAYS(0),
	
	DEFAULT(500),
	
	NEVER(Integer.MAX_VALUE);
	
	final int level;

	private VerbosityEnum(int level) {
		this.level = level;
	}

	@Override
	public int getLevel() {
		return level;
	}

}
