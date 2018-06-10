package de.webtwob.agd.project.api.util;

import java.util.function.Supplier;

public class Pair<A> {

	public Pair(Supplier<A> sup) {
		start = sup.get();
		end = sup.get();
	}
	
	public Pair() {}

	public A start;
	public A end;
}