import de.webtwob.agd.project.service.api.IGraphUpdateEventQueue;
import de.webtwob.agd.project.service.impl.GraphUpdateEventQueue;

module de.webtwob.agd.project.service{
	
	requires org.eclipse.elk.graph;
	requires transitive java.desktop;
	
	exports de.webtwob.agd.project.service.api;
	exports de.webtwob.agd.project.service.util;
	
	provides IGraphUpdateEventQueue with GraphUpdateEventQueue;
}
