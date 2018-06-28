@SuppressWarnings("module")
module de.webtwob.agd.project.api {

	requires transitive java.desktop;

	requires org.eclipse.elk.alg.force;
	//if this weren't an automatic module this would be transitive
	requires org.eclipse.elk.graph;
	requires org.eclipse.elk.core;

	exports de.webtwob.agd.project.api;
	exports de.webtwob.agd.project.api.util;
	exports de.webtwob.agd.project.api.events;
	exports de.webtwob.agd.project.api.enums;
	exports de.webtwob.agd.project.api.interfaces;

	uses de.webtwob.agd.project.api.interfaces.IGraphLoader;
	uses de.webtwob.agd.project.api.interfaces.IAlgorithm;
}
