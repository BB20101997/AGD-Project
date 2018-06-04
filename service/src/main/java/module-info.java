import de.webtwob.agd.project.service.api.IGraphUpdateEventQueue;
import de.webtwob.agd.project.service.impl.GraphUpdateEventQueue;

module de.webtwob.agd.project.service{
	
	requires org.eclipse.elk.graph;
	
	exports de.webtwob.agd.project.service.api;
	
	provides IGraphUpdateEventQueue with GraphUpdateEventQueue;
}
