module de.webtwob.agd.project.api{

	requires transitive java.desktop;

    requires org.eclipse.elk.alg.force;
    requires org.eclipse.elk.graph;
    requires org.eclipse.elk.core;
    
	exports de.webtwob.agd.project.api;
	exports de.webtwob.agd.project.api.util;
	exports de.webtwob.agd.project.api.events;
	exports de.webtwob.agd.project.api.interfaces;
	
	uses de.webtwob.agd.project.api.interfaces.IGraphLoader;
}
