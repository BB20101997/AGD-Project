package de.webtwob.agd.project.api.enums;

import de.webtwob.agd.project.api.interfaces.IVerbosity;


/**
 * Stop at everything with a lower or equal level, unless level is Integer.MAX_VALUE than stop nowhere
 * or Skip everything with a greater level or with Integer.MAX_VALUE
 * */
public enum VerbosityEnum implements IVerbosity {

	OFF(Integer.MAX_VALUE,"Off"){
		@Override
		public boolean shouldStop(IVerbosity verbos) {
			return false;
		}
	},

	DEPTH_6(600, "Depth 6"),
	
	DEPTH_5(500, "Depth 5"),
	
	DEPTH_4(400, "Depth 4"),
	
	DEPTH_3(300, "Depth 3"),
	
	DEPTH_2(200, "Depth 2"),
	
	DEPTH_1(100,"Depth 1"),
	
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

	public String getName() {
		return name;
	}
	
	public boolean shouldStop(IVerbosity verbos) {
		return verbos.getLevel() <= level;
	}
	
	

}
