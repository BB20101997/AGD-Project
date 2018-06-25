package de.webtwob.agd.project.test.algorith.force;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.util.GraphStateListBuilder;
import de.webtwob.agd.project.api.util.InitialLayoutUtil;

public class ForceTestAlgorithm implements IAlgorithm {

	@Override
	public List<GraphState> getGraphStates(ElkNode graph) {
		LinkedList<GraphState> steps = new LinkedList<>();

		var builder = GraphStateListBuilder.startWith(graph);
		
		builder.atLine("line0").starteFunction();
		
		InitialLayoutUtil.setForceLayoutAlgorithm(graph);
		InitialLayoutUtil.layout(graph);	
		
		builder.endFunction().atLine("line0").updateNode(graph);
		
		steps.addAll(builder.getList());
		
		return steps;
	}
	
	@Override
	public String getPseudoCode() {
		return "<code id='line0'>applyForceDirectedLayoutAlgorithm(graph);</code>";
	}

	@Override
	public String getName() {
		return "Test Algorithm, Force Directed";
	}

	@Override
	public boolean animationTopology() {
		return false;
	}

}
