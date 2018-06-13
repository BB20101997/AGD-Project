package de.webtwob.agd.project.model;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.util.GraphState;
import de.webtwob.agd.project.api.util.ViewUtil;

@SuppressWarnings("exports")
public class Model {

	private Model() {}
	
	/**
	 * Greedy cycle break from before
	 * 
	 * @param graph
	 * @return
	 */
	public static void getSteps(ElkNode graph,List<GraphState> steps) {

		var state = new GraphState();
		ViewUtil.saveState(graph, state);
		state.setPseudoCodeLine(0);
		steps.add(state);
		
		// copy child list so we can remove already sorted ones
		List<ElkNode> children = new LinkedList<>(graph.getChildren());

		// sources at the beginning add to the end
		LinkedList<ElkNode> sourceList = new LinkedList<>();
		// sinks at the end add to the beginning
		LinkedList<ElkNode> sinkList = new LinkedList<>();
		
		state =  new GraphState(state);
		ViewUtil.saveState(graph, state);
		state.setPseudoCodeLine(4);
		steps.add(state);

		// 0 Steps
		while (!children.isEmpty()) {
			boolean found;

			// sort out source
			do {
				found = false;
				if(children.isEmpty()) {
					state = new GraphState(state);
					state.setPseudoCodeLine(8);
					steps.add(state);
				}
				for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {
					ElkNode node = iter.next();
					
					state = new GraphState(state);
					state.setHighlight(node, Color.BLUE); //highlight active node in blue
					state.setPseudoCodeLine(8);
					steps.add(state);
					
					// is node a Source given the currently present nodes in children
					if (node.getIncomingEdges().parallelStream().map(Util::getSource).noneMatch(children::contains)) {
						sourceList.addLast(node);
						
						state = new GraphState(state);
						state.setHighlight(node, Color.CYAN); //highlight current node as source in CYAN
						state.setPseudoCodeLine(10);
						steps.add(state);
						
						iter.remove(); // avoid ConcurrentModificationException
						found = true;
					}else {
						
						state = new GraphState(state);
						state.setHighlight(node, null); //un-highlight current node
						state.setPseudoCodeLine(14);
						steps.add(state);
						
					}
				}
				
				state = new GraphState(state);
				state.setPseudoCodeLine(15);
				steps.add(state);

			} while (found);// stop when an iteration didn't found sinks

			// sort out sink
			do {
				state = new GraphState(state);
				state.setPseudoCodeLine(17);
				steps.add(state);
				found = false;
				
				if(children.isEmpty()) {
					state = new GraphState(state);
					state.setPseudoCodeLine(19);
					steps.add(state);
				}
				
				for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {

					ElkNode node = iter.next();

					state = new GraphState(state);
					state.setHighlight(node, Color.BLUE); //highlight active node in blue
					state.setPseudoCodeLine(19);
					steps.add(state);
					
					// is node a Source given the currently present nodes in children
					if (node.getOutgoingEdges().parallelStream().map(Util::getTarget).noneMatch(children::contains)) {
						sinkList.addFirst(node);
						
						state = new GraphState(state);
						state.setHighlight(node, Color.LIGHT_GRAY); //highlight sink in light-gray
						state.setPseudoCodeLine(21);
						steps.add(state);
						
						iter.remove(); // avoid ConcurrentModificationException
						found = true;

					}else {

						state = new GraphState(state);
						state.setHighlight(node, null); //un-highlight current node
						state.setPseudoCodeLine(25);
						steps.add(state);
						
					}

				}
				state = new GraphState(state);
				state.setPseudoCodeLine(26);
				steps.add(state);
			} while (found);// stop when an iteration didn't found sinks

			// find edge with max in-degree to out-degree difference
			ElkNode maxNode = null;
			int maxDiff = Integer.MIN_VALUE;
			if(children.isEmpty()) {
				state = new GraphState(state);
				state.setPseudoCodeLine(31);
				steps.add(state);
			}
			for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {

				ElkNode curNode = iter.next();
				
				state = new GraphState(state);
				state.setHighlight(curNode, Color.BLUE); //highlight active node in blue
				state.setPseudoCodeLine(31);
				steps.add(state);

				int curVal = curNode.getOutgoingEdges().size() - curNode.getIncomingEdges().size();
				state = new GraphState(state);
				state.setPseudoCodeLine(33);
				steps.add(state);
				if (curVal > maxDiff) {
					
					state = new GraphState(state);
					state.setHighlight(maxNode, null); //un-highlight old mox node
					state.setHighlight(curNode, Color.RED); //highlight max node in red
					state.setPseudoCodeLine(34);
					steps.add(state);
					
					maxDiff = curVal;
					maxNode = curNode;
				}else {
					state = new GraphState(state);
					state.setHighlight(curNode, null); //un-highlight current node
					state.setPseudoCodeLine(37);
					steps.add(state);
				}

			}
			if(children.isEmpty()) {
				state = new GraphState(state);
				state.setPseudoCodeLine(39);
				steps.add(state);
			}
			// if we still had nodes add the one with max out to in diff to source list
			if (maxNode != null) {
				sourceList.addFirst(maxNode);
				children.remove(maxNode);
				
				state = new GraphState(state);
				state.setHighlight(maxNode, Color.CYAN); //highlight max node as source in CYAN
				state.setPseudoCodeLine(40);
				steps.add(state);
			}

		}
		

		state = new GraphState(state);
		state.setPseudoCodeLine(45);
		steps.add(state);
	
		// remove cycles
		List<ElkNode> combinedList = new LinkedList<>();
		combinedList.addAll(sourceList);
		combinedList.addAll(sinkList);
		
		for(var edge:graph.getContainedEdges()) {
			
			state = new GraphState(state);
			state.setHighlight(edge, Color.BLUE); //highlight active edge in blue
			state.setPseudoCodeLine(47);
			steps.add(state);
			
			// reverse all edges where the source Node index is higher than the target node
			// index
			if (combinedList.indexOf(Util.getSource(edge)) > combinedList.indexOf(Util.getTarget(edge))) {
				Util.reverseEdge(edge);
				
				state = new GraphState(state);
				ViewUtil.saveState(edge, state);
				state.setHighlight(edge, Color.GREEN); //highlight active reversed edge in Green
				state.setPseudoCodeLine(51);
				steps.add(state);
				
			}else {
				state = new GraphState(state);
				state.setHighlight(edge, null); //un-highlight current edge
				steps.add(state);
			}
		}
		
		state = new GraphState(state);
		state.setPseudoCodeLine(51);
		steps.add(state);
		
	}

}
