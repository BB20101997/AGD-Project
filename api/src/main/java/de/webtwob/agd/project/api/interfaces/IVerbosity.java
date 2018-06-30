package de.webtwob.agd.project.api.interfaces;

/**
 * An interface for representing the Verbosity Level
 */
public interface IVerbosity {
	/**
	 * @return the verbosity level
	 * 
	 *         A higher level means a finer step
	 */
	int getLevel();
	
	/**
	 * @return True if the animation should stop at the beginning of this step
	 * @param verbos
	 *            the IVerbosity object to compare to
	 */
	default boolean shouldStop(IVerbosity verbos) {
		return verbos.getLevel() <= this.getLevel();
	}
}