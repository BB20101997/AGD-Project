package de.webtwob.agd.project.api.enums;

import de.webtwob.agd.project.api.interfaces.IVerbosity;


/**
 * Stop at everything with a lower or equal level, unless level is Integer.MAX_VALUE than stop nowhere
 * or Skip everything with a greater level or with Integer.MAX_VALUE
 * */
public enum VerbosityEnum implements IVerbosity {

	/**
	 * Turn off steps
	 * */
	OFF(Integer.MAX_VALUE,"Off"){
		@Override
		public boolean shouldStop(IVerbosity verbos) {
			return false;
		}
	},

	/**
	 * Stop at depth 0 to 6 stuff
	 * */
	DEPTH_6(600, "Depth 6"),

	/**
	 * Stop at depth 0 to 5 stuff
	 * */
	DEPTH_5(500, "Depth 5"),

	/**
	 * Stop at depth 0 to 4 stuff
	 * */
	DEPTH_4(400, "Depth 4"),
	
	/**
	 * Stop at depth 0 to 3 stuff
	 * */
	DEPTH_3(300, "Depth 3"),
	
	/**
	 * Stop at depth 0 to 2 stuff
	 * */
	DEPTH_2(200, "Depth 2"),
	
	/**
	 * Stop at depth 0 to 1 stuff
	 * */
	DEPTH_1(100,"Depth 1"),
	
	/**
	 * Stop only at depth 0 stuff
	 * */
	DEPTH_0(0,"ROOT");
	
	final int level;
	final String name;
	
	private VerbosityEnum(int level,String name) {
		this.level = level;
		this.name = name;
	}

	@Override
	public int getLevel() {
		return level;
	}

	/**
	 * The name for the UI
	 * */
	public String getName() {
		return name;
	}
	
	/**
	 * True if the animation should stop at the beginning of this step
	 * */
	public boolean shouldStop(IVerbosity verbos) {
		return verbos.getLevel() <= level;
	}
	
	

}
