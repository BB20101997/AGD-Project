package de.webtwob.agd.project.api.util;

import java.util.function.Supplier;

/**
 * @param <A> the type of this pairs elements
 * A simple Pair class with elements of equal Type
 * */
public class Pair<A> {

	private A start;
	private A end;

	/**
	 * @param sup the supplier supplying the initial values for start and end
	 */
	public Pair(Supplier<A> sup) {
		start = sup.get();
		end = sup.get();
	}

	/**
	 * Create a new Pair with the default initial values
	 */
	public Pair() {
	}

	/**
	 * @param start the inital value for starte
	 * @param end the inintial value for end
	 * */
	public Pair(A start, A end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * @return start
	 */
	public A getStart() {
		return start;
	}

	/**
	 * @param start what start should be from now on
	 */
	public void setStart(A start) {
		this.start = start;
	}

	/**
	 * @return end
	 */
	public A getEnd() {
		return end;
	}

	/**
	 * @param end  what end should be from now on
	 */
	public void setEnd(A end) {
		this.end = end;
	}
}