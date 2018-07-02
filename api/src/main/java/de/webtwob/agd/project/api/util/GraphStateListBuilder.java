package de.webtwob.agd.project.api.util;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;

/**
 * Helper Class to easily generate a list of GraphStates
 * 
 */
public class GraphStateListBuilder {

	/**
	 * A helper Interface for setting a Nodes Color
	 * */
	@FunctionalInterface
	public interface In {
		/**
		 * @param color the Color to use
		 * 
		 * @return the GraphStateBuilder that created this
		 * */
		GraphStateBuilder in(Color color);

		/**
		 * @param color the Color to use
		 * 
		 * @return the GraphStateBuilder that created this
		 * */
		default GraphStateBuilder as(Color color) {
			return in(color);
		}

		/**
		 * use the Color {@link Color#BLUE}
		 * 
		 * @return the GraphStateBuilder that created this
		 * */
		default GraphStateBuilder active() {
			return in(Color.BLUE);
		}
	}

	private int lastSink = 0;
	private int nextSource = 0;
	private int depth = 0;

	/**
	 * The Color of Sources
	 */
	public static final Color SOURCE = Color.CYAN;

	/**
	 * The Color of Sinks
	 */
	public static final Color SINK = Color.LIGHT_GRAY;

	List<GraphState> graphStateList = new LinkedList<>();
	GraphState current;

	private GraphStateListBuilder(ElkNode graph) {
		var state = new GraphState();
		GraphStateUtil.saveState(graph, state);
		current = state;
	}

	/**
	 * @param graph
	 *            the graph to start with
	 * @return a new GraphStateListBuilder containing no states
	 */
	public static GraphStateListBuilder startWith(ElkNode graph) {
		return new GraphStateListBuilder(graph);
	}

	/**
	 * @return the list of created states
	 */
	public List<GraphState> getList() {
		return List.copyOf(graphStateList);
	}

	/**
	 * @param line
	 *            the identifier of the pseudocode line
	 * @return the GraphState builder created
	 * 
	 *         Creates a new GraphStateBuilder for Building the next GraphState
	 */
	public GraphStateBuilder atLine(String line) {
		current = new GraphState(current);
		current.setPseudoCodeLine(line);
		graphStateList.add(current);
		return new GraphStateBuilder(current);
	}

	/**
	 * Decreases the depth by one
	 * 
	 * @return this
	 */
	public GraphStateListBuilder endIf() {
		return decreaseDepth();
	}

	/**
	 * Decreases the depth by one
	 * 
	 * @return this
	 */
	public GraphStateListBuilder endLoop() {
		return decreaseDepth();
	}

	/**
	 * Decreases the depth by one
	 * 
	 * @return this
	 */
	public GraphStateListBuilder endFunction() {
		return decreaseDepth();
	}

	private GraphStateListBuilder decreaseDepth() {
		depth--;
		return this;
	}

	/**
	 * Increases the depth by one
	 * 
	 * @return this
	 */
	public GraphStateListBuilder startDoWhileLoop() {
		depth++;
		return this;
	}

	/**
	 * A Builder for GraphState Objects
	 * */
	public class GraphStateBuilder {
		private GraphState current;

		private GraphStateBuilder(GraphState current) {
			this.current = current;
			int level = depth * 100;
			current.setVerbosity(() -> level);
		}

		/**
		 * @param object
		 *            the object to highlight
		 * @return a function to highlight the passed object
		 */
		public In highlight(Object object) {
			return color -> {
				current.setHighlight(object, color);
				return this;
			};
		}

		/**
		 * @param node
		 *            the object to no longer highlight
		 * @return this
		 */
		public GraphStateBuilder unhighlight(Object node) {
			current.setHighlight(node, null);
			return this;
		}

		/**
		 * @param node
		 *            the node who's state to update
		 * @return this
		 */
		public GraphStateBuilder updateNode(ElkNode node) {
			GraphStateUtil.saveState(node, current);
			return this;
		}

		/**
		 * @param node
		 *            the node to highlight as a source and set the Position
		 * @return this
		 */
		public GraphStateBuilder addSource(ElkNode node) {
			highlight(node).as(SOURCE);
			current.setPossition(node, nextSource++);
			return this;
		}

		/**
		 * @param node
		 *            the node to highlight as a sink and set the Position
		 * @return this
		 */
		public GraphStateBuilder addSink(ElkNode node) {
			highlight(node).as(SINK);
			current.setPossition(node, --lastSink);
			return this;
		}

		/**
		 * @param edge
		 *            the edge who's state to update
		 * @return this
		 */
		public GraphStateBuilder updateEdge(ElkEdge edge) {
			GraphStateUtil.saveState(edge, current);
			return this;
		}

		/**
		 * Increases the depth by 1
		 * 
		 * @return this
		 */
		public GraphStateBuilder starteIf() {
			return increaseDepth();
		}

		/**
		 * Increases the depth by 1
		 * 
		 * @return this
		 */
		public GraphStateBuilder starteLoop() {
			return increaseDepth();
		}

		/**
		 * Increases the depth by 1
		 * 
		 * @return this
		 */
		public GraphStateBuilder starteFunction() {
			return increaseDepth();
		}

		private GraphStateBuilder increaseDepth() {
			depth++;
			return this;
		}

	}

}
