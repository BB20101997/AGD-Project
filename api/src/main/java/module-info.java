module de.webtwob.agd.project.api{
	
	requires org.eclipse.elk.graph;
	requires transitive java.desktop;
	
	exports de.webtwob.agd.project.api;
	exports de.webtwob.agd.project.api.util;
	exports de.webtwob.agd.project.api.events;
	exports de.webtwob.agd.project.api.interfaces;
	
	uses de.webtwob.agd.project.api.IGraphLoader;
}
