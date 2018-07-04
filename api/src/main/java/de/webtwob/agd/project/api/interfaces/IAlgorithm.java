package de.webtwob.agd.project.api.interfaces;

import java.util.List;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;

/**
 * An Interface used to represent all Algorithms that can be animated
 * 
 */
public interface IAlgorithm {


	/**
	 * @return does this IAlgoritm include ordered nodes
	 */
	boolean animationTopology();
	
	/**
	 * @return the pseudocode for this algorithm as a HTML String
	 * */
	String getPseudoCode();

	/**
	 * @return the name of the algorithm
	 */
	String getName();

	
	/**
	 * @param graph the graph to act upon
	 * @return List of graph states for this graph and algorithm
	 * */
	List<GraphState> getGraphStates(ElkNode graph);
	
	/**
	 * @param graph the graph to reset
	 * @param start the initial state before this algorithm was applied, befor calling {@link IAlgorithm#getGraphStates(ElkNode)}
	 * @param end the last state in the List returned by {@link IAlgorithm#getGraphStates(ElkNode)}
	 * 
	 * */
	default void resetGraph(ElkNode graph, GraphState start, GraphState end) {
		
		start.applyToNode(graph);
		
	}

}
