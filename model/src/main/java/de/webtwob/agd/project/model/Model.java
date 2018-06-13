package de.webtwob.agd.project.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;

@SuppressWarnings("exports")
public class Model {
	
	
	private List<Map<ElkNode, NodeStates>> stepNodes = new LinkedList<Map<ElkNode, NodeStates>>();
	private List<Map<ElkEdge, EdgeStates>> stepEdges = new LinkedList<Map<ElkEdge, EdgeStates>>();

	// TODO Thread
	// TODO Find a good way to store the changes -> use
	// de.webtwob.agd.project.api.util.GraphMapping
	/**
	 * Greedy cycle break from before
	 * 
	 * @param graph
	 * @return
	 */
	public void getSteps(ElkNode graph) {
		stepNodes = new LinkedList<Map<ElkNode, NodeStates>>();
		stepEdges = new LinkedList<Map<ElkEdge, EdgeStates>>();
		// order nodes

		// copy child list so we can remove already sorted ones
		List<ElkNode> children = new LinkedList<>(graph.getChildren());

		// Information for the Steps
		List<ElkNode> allNodes = new LinkedList<>(graph.getChildren());
		List<ElkEdge> edges = new LinkedList<>(graph.getContainedEdges());
		List<ElkEdge> reversedEdges = new LinkedList<>();
		List<ElkEdge> incomingEdges = null;
		List<ElkEdge> outgoingEdges = null;
		ElkNode currentlyActive = null;

		// sources at the beginning add to the end
		LinkedList<ElkNode> sourceList = new LinkedList<>();
		// sinks at the end add to the beginning
		LinkedList<ElkNode> sinkList = new LinkedList<>();

		stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
		stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));
		// 0 Steps
		while (!children.isEmpty()) {
			boolean found;

			// sort out source
			do {
				found = false;
				for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {
					ElkNode node = iter.next();
					// Step choose node
					currentlyActive = node;
					stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
					stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));

					// Step compare edges
					incomingEdges = node.getIncomingEdges();

					stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
					stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));

					// is node a Source given the currently present nodes in children
					if (node.getIncomingEdges().parallelStream().map(Util::getSource).noneMatch(children::contains)) {
						sourceList.addLast(node);
						iter.remove(); // avoid ConcurrentModificationException
						found = true;
					}
					// Step result
					currentlyActive = null;
					incomingEdges = null;
					outgoingEdges = null;

					stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
					stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));

				}

			} while (found);// stop when an iteration didn't found sinks

			// sort out sink
			do {
				found = false;
				for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {

					ElkNode node = iter.next();

					// Step choose Node
					currentlyActive = node;
					stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
					stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));

					// Step compare edges of node
					outgoingEdges = node.getOutgoingEdges();

					stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
					stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));

					// is node a Source given the currently present nodes in children
					if (node.getOutgoingEdges().parallelStream().map(Util::getTarget).noneMatch(children::contains)) {
						sinkList.addFirst(node);
						iter.remove(); // avoid ConcurrentModificationException
						found = true;

					}

					// Step result
					currentlyActive = null;
					incomingEdges = null;
					outgoingEdges = null;

					stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
					stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));

				}

			} while (found);// stop when an iteration didn't found sinks

			// find edge with max in-degree to out-degree difference
			ElkNode maxNode = null;
			int maxDiff = Integer.MIN_VALUE;
			for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {

				ElkNode curNode = iter.next();
				// Step choose node
				currentlyActive = curNode;

				stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
				stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));

				// Step compare edges of node
				incomingEdges = curNode.getIncomingEdges();
				outgoingEdges = curNode.getOutgoingEdges();

				stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
				stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));

				int curVal = curNode.getOutgoingEdges().size() - curNode.getIncomingEdges().size();
				if (curVal > maxDiff) {
					maxDiff = curVal;
					maxNode = curNode;
				}

				// Step result
				currentlyActive = null;
				incomingEdges = null;
				outgoingEdges = null;
				stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
				stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));

			}

			// if we still had nodes add the one with max out to in diff to source list
			if (maxNode != null) {
				sourceList.addFirst(maxNode);
				children.remove(maxNode);
			}

		}
		// Step result
		currentlyActive = null;
		incomingEdges = null;
		outgoingEdges = null;
		stepNodes.add(getNewStepNodes(currentlyActive, sourceList, sinkList, allNodes));
		stepEdges.add(getNewStepEdges(edges, incomingEdges, outgoingEdges, reversedEdges));

		// remove cycles
		List<ElkNode> combinedList = new LinkedList<>();
		combinedList.addAll(sourceList);
		combinedList.addAll(sinkList);

		graph.getContainedEdges().stream().forEach(e -> {
			// reverse all edges where the source Node index is higher than the target node
			// index
			if (combinedList.indexOf(Util.getSource(e)) > combinedList.indexOf(Util.getTarget(e))) {
				Util.reverseEdge(e);
				reversedEdges.add(e);

				// Step reverse edge
				stepNodes.add(getNewStepNodes(null, sourceList, sinkList, allNodes));
				stepEdges.add(getNewStepEdges(edges, null, null, reversedEdges));

			}

		});
		// Step final
		stepNodes.add(getNewStepNodes(null, sourceList, sinkList, allNodes));
		stepEdges.add(getNewStepEdges(edges, null, null, reversedEdges));


	}

	private static Map<ElkEdge, EdgeStates> getNewStepEdges(List<ElkEdge> edgesList, List<ElkEdge> incomingEdges,
			List<ElkEdge> outgoingEdges, List<ElkEdge> reversedEdges) {
		Map<ElkEdge, EdgeStates> newStep = new HashMap<>();
		edgesList.forEach(l -> newStep.put(l, EdgeStates.NORMAL));
		if (incomingEdges != null)
			incomingEdges.forEach(l -> newStep.put(l, EdgeStates.INCOMING));
		if (outgoingEdges != null)
			outgoingEdges.forEach(l -> newStep.put(l, EdgeStates.OUTGOING));
		reversedEdges.forEach(l -> newStep.put(l, EdgeStates.REVERSED)); // As put with the same key replaces the old
																			// value
		return newStep;
	}

	private static Map<ElkNode, NodeStates> getNewStepNodes(ElkNode active, LinkedList<ElkNode> sourceList,
			LinkedList<ElkNode> sinkList, List<ElkNode> normal) {

		Map<ElkNode, NodeStates> newStep = new HashMap<>();
		normal.forEach(e -> newStep.put(e, NodeStates.NORMAL));
		sinkList.forEach(e -> newStep.put(e, NodeStates.SINK));
		sourceList.forEach(e -> newStep.put(e, NodeStates.SOURCE));
		if (active != null) {
			newStep.put(active, NodeStates.ACTIVE);
		}
		return newStep;
	}

}
