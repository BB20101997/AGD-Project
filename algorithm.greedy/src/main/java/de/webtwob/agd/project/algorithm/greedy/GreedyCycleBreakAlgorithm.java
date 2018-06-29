package de.webtwob.agd.project.algorithm.greedy;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.util.GraphStateListBuilder;
import de.webtwob.agd.project.api.util.InitialLayoutUtil;

public class GreedyCycleBreakAlgorithm implements IAlgorithm {

	@Override
	public List<GraphState> getGraphStates(ElkNode graph) {
		var list = new LinkedList<GraphState>();
		
		if (!graph.getProperty(CoreOptions.NO_LAYOUT)) {
			// apply the force layout algorithm to get an initial layout
			InitialLayoutUtil.setForceLayoutAlgorithm(graph);
			InitialLayoutUtil.layout(graph);
		}else {
			Util.routeEdges(graph);
		}
		
		getSteps(graph, list);
		
		return list;
	}

	@Override
	public String getName() {
		return "Cyclebreak Greedy";
	}

	@Override
	public String getPseudoCode() {
		String lines = null;

		// try to load the pseudocode from file
		try {
			var uri = getClass().getResource("de/webtwob/agd/project/algorithm/greedy/GreedyCycleBreakPseudoCode.html");

			if (uri == null) {
				uri = new File(
						"../algorithm.greedy/src/main/resources/de/webtwob/agd/project/algorithm/greedy/GreedyCycleBreakPseudoCode.html")
								.toURI().toURL();
			}
			try (var reader = new BufferedReader(new InputStreamReader(uri.openStream()))) {
				lines = reader.lines().collect(Collectors.joining());
			}
		} catch (IOException ignore) {
			// will just return null for pseudo-code
		}
		return lines;
	}

	/**
	 * Greedy cycle break from before
	 *
	 * @param graph
	 *            the graph which cycles shall be broken
	 * @param steps
	 *            the List to store the GraphStates into
	 */
	private static void getSteps(ElkNode graph, List<GraphState> steps) {

		var stateBuilder = GraphStateListBuilder.startWith(graph);

		stateBuilder.atLine("the_start").starteFunction();

		// copy child list so we can remove already sorted ones
		List<ElkNode> children = new LinkedList<>(graph.getChildren());

		// sources at the beginning add to the end
		LinkedList<ElkNode> sourceList = new LinkedList<>();
		// sinks at the end add to the beginning
		LinkedList<ElkNode> sinkList = new LinkedList<>();

		if (children.isEmpty()) {
			stateBuilder.atLine("while_has_children");
		}

		while (!children.isEmpty()) {
			stateBuilder.atLine("while_has_children").starteLoop();

			findSources(stateBuilder, children, sourceList);
			findSinks(stateBuilder, children, sinkList);
			findBestSource(stateBuilder, children, sourceList);
			
			stateBuilder.endLoop();
		}

		stateBuilder.atLine("no_children_left");

		removeCycles(graph, stateBuilder, sourceList, sinkList);

		stateBuilder.endFunction().atLine("the_end");

		steps.addAll(stateBuilder.getList());
	}

	/**
	 * @param stateBuilder
	 * @param children
	 * @param sourceList
	 */
	private static void findBestSource(GraphStateListBuilder stateBuilder, List<ElkNode> children, LinkedList<ElkNode> sourceList) {
		
		// find edge with max in-degree to out-degree difference
		ElkNode maxNode = null;
		int maxDiff = Integer.MIN_VALUE;
		if (children.isEmpty()) {
			stateBuilder.atLine("for_each_remaining");
		}

		ElkNode lastNode = null;
		for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {

			ElkNode currentNode = iter.next();

			stateBuilder.atLine("for_each_remaining").starteLoop().unhighlight(lastNode).highlight(currentNode)
					.active();

			int curVal = currentNode.getOutgoingEdges().size() - currentNode.getIncomingEdges().size();

			stateBuilder.atLine("is_new_max").starteIf();
			if (curVal > maxDiff) {
				stateBuilder.atLine("set_max").unhighlight(maxNode).highlight(currentNode).in(Color.RED);
				maxDiff = curVal;
				maxNode = currentNode;
				lastNode = null;
			} else {
				lastNode = currentNode;
			}
			stateBuilder.endIf().endLoop();
		}

		stateBuilder.atLine("end_for_each_remaining").unhighlight(lastNode);

		// if we still had nodes add the one with max out to in diff to source list
		if (maxNode != null) {
			stateBuilder.atLine("has_max_node").starteIf();
			sourceList.addLast(maxNode);
			children.remove(maxNode);
			stateBuilder.atLine("max_as_source").addSource(maxNode);
			stateBuilder.endIf();
		}
	}
	
	private static void findSinks(GraphStateListBuilder stateBuilder, List<ElkNode> children,
			LinkedList<ElkNode> sinkList) {
		boolean found;
		// sort out sink
		do {
			stateBuilder.startDoWhileLoop().atLine("do_sink_found");
			found = false;

			if (children.isEmpty()) {
				stateBuilder.atLine("for_each_child_sink");
			}

			ElkNode lastNode = null;

			for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {

				ElkNode currentNode = iter.next();

				stateBuilder.atLine("for_each_child_sink").starteLoop().unhighlight(lastNode).highlight(currentNode)
						.active();

				// is node a Source given the currently present nodes in children
				stateBuilder.atLine("is_sink").starteIf();
				if (currentNode.getOutgoingEdges().parallelStream().map(Util::getTarget)
						.noneMatch(children::contains)) {
					sinkList.addFirst(currentNode);

					stateBuilder.atLine("mark_sink").addSink(currentNode);

					iter.remove(); // avoid ConcurrentModificationException
					found = true;
					lastNode = null;
				} else {
					lastNode = currentNode;
				}
				stateBuilder.endIf().endLoop();
			}
			stateBuilder.endLoop().atLine("while_sink_found").unhighlight(lastNode);
		} while (found);// stop when an iteration didn't found sinks
	}

	private static void findSources(GraphStateListBuilder stateBuilder, List<ElkNode> children,
			LinkedList<ElkNode> sourceList) {
		boolean found;
		// sort out source
		do {
			stateBuilder.startDoWhileLoop().atLine("do_source_found");

			found = false;

			if (children.isEmpty()) {
				stateBuilder.atLine("for_each_child_source");
			}

			ElkNode lastNode = null;

			for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {
				ElkNode currentNode = iter.next();

				stateBuilder.atLine("for_each_child_source").starteLoop().unhighlight(lastNode).highlight(currentNode)
						.active();

				// is node a Source given the currently present nodes in children
				stateBuilder.atLine("is_source").starteIf();
				if (currentNode.getIncomingEdges().parallelStream().map(Util::getSource)
						.noneMatch(children::contains)) {
					stateBuilder.atLine("mark_source").addSource(currentNode);
					sourceList.addLast(currentNode);
					iter.remove(); // avoid ConcurrentModificationException
					found = true;
					lastNode = null;

				} else {
					lastNode = currentNode;
				}
				stateBuilder.endIf().endLoop();
			}

			stateBuilder.endLoop().atLine("while_source_found").unhighlight(lastNode);

		} while (found);// stop when an iteration didn't found sinks
	}

	private static void removeCycles(ElkNode graph, GraphStateListBuilder stateBuilder, LinkedList<ElkNode> sourceList,
			LinkedList<ElkNode> sinkList) {
		// remove cycles
		List<ElkNode> combinedList = new LinkedList<>();
		combinedList.addAll(sourceList);
		combinedList.addAll(sinkList);

		if (graph.getContainedEdges().isEmpty()) {
			stateBuilder.atLine("for_each_edge");
		}

		ElkEdge lastEdge = null;

		for (var edge : graph.getContainedEdges()) {

			stateBuilder.atLine("for_each_edge").starteLoop().unhighlight(lastEdge).highlight(edge).in(Color.BLUE);

			// reverse all edges where the source Node index is higher than the target node
			// index
			stateBuilder.atLine("needs_to_be_reversed").starteIf();
			if (combinedList.indexOf(Util.getSource(edge)) > combinedList.indexOf(Util.getTarget(edge))) {
				Util.reverseEdge(edge);
				stateBuilder.atLine("reverse_edge").updateEdge(edge).highlight(edge).in(Color.GREEN);
				lastEdge = null;
			} else {
				lastEdge = edge;
			}
			stateBuilder.endIf().endLoop();
		}

		stateBuilder.atLine("for_each_edge_end").unhighlight(lastEdge);
	}

	@Override
	public boolean animationTopology() {
		return true;
	}

}
