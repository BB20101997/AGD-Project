package de.webtwob.agd.project.api.util;

import java.util.function.Supplier;

public class Pair<A> {

	private A start;
	private A end;

	public Pair(Supplier<A> sup) {
		start = sup.get();
		end = sup.get();
	}

	public Pair() {
	}
	
	public Pair(A start,A end) {
		this.start = start;
		this.end = end;
	}

	public A getStart() {
		return start;
	}

	public void setStart(A start) {
		this.start = start;
	}

	public A getEnd() {
		return end;
	}

	public void setEnd(A end) {
		this.end = end;
	}
}