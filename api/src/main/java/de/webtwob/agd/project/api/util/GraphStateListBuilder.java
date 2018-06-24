package de.webtwob.agd.project.api.util;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;

public class GraphStateListBuilder {
	
	@FunctionalInterface
	public interface In{
		GraphStateBuilder in(Color color);
		
		default GraphStateBuilder as(Color color) {
			return in(color);
		}
		
		default GraphStateBuilder active() {
			return in(Color.BLUE);
		}
	}
	
	int lastSink = 0;
	int lastSource = 0;
	int depth = 0;

	public static final Color SOURCE = Color.CYAN;

	public static final Color SINK = Color.LIGHT_GRAY;

	List<GraphState> graphStateList = new LinkedList<>();
	GraphState current;

	private GraphStateListBuilder(ElkNode graph) {
		var state = new GraphState();
		GraphStateUtil.saveState(graph, state);
		current = state;
	}

	public static GraphStateListBuilder startWith(ElkNode graph) {
		return  new GraphStateListBuilder(graph);
	}

	public List<GraphState> getList() {
		return List.copyOf(graphStateList);
	}

	public GraphStateBuilder atLine(String line) {
		current = new GraphState(current);
		current.setPseudoCodeLine(line);
		graphStateList.add(current);
		return new GraphStateBuilder(current);
	}
	
	public GraphStateListBuilder endIf() {
		return decreaseDepth();
	}
	
	public GraphStateListBuilder endLoop() {
		return decreaseDepth();
	}
	
	public GraphStateListBuilder endFunction() {
		return decreaseDepth();
	}
	
	private GraphStateListBuilder decreaseDepth() {
		depth --;
		return this;
	}
	
	public class GraphStateBuilder {
		private GraphState current;
		
		private GraphStateBuilder(GraphState current) {
			this.current = current;
			int level = depth*100;
			current.setVerbosity(()->level);
		}

		public In highlight(Object object) {
			return color -> {
				current.setHighlight(object, color);
				return this;
			};
		}
		
		public GraphStateBuilder unhighlight(Object node) {
			current.setHighlight(node, null);
			return this;
		}
		
		public GraphStateBuilder updateNode(ElkNode node) {
			GraphStateUtil.saveState(node, current);
			return this;
		}
		
		public GraphStateBuilder addSource(ElkNode node){
			highlight(node).as(SOURCE);
			current.setPossition(node, ++lastSource);
			return this;
		}
		
		public GraphStateBuilder addSink(ElkNode node) {
			highlight(node).as(SINK);
			current.setPossition(node, --lastSource);
			return this;
		}
		
		public GraphStateBuilder updateEdge(ElkEdge edge) {
			GraphStateUtil.saveState(edge,current);
			return this;
		}
		
		public GraphStateBuilder starteIf() {
			return increaseDepth();
		}
		
		public GraphStateBuilder starteLoop() {
			return increaseDepth();
		}
		
		public GraphStateBuilder starteFunction() {
			return increaseDepth();
		}
		
		private GraphStateBuilder increaseDepth() {
			depth++;
			return this;
		}

	}


}
