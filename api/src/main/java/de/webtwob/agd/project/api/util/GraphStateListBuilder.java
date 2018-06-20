package de.webtwob.agd.project.api.util;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.VerbosityEnum;
import de.webtwob.agd.project.api.interfaces.IVerbosity;

public class GraphStateListBuilder {
	
	int lastSink = 0;
	int lastSource = 0;
	
	@FunctionalInterface
	public interface AtLine {
		GraphStateListBuilder atLine(String t);
	}
	
	@FunctionalInterface
	public interface StartNode {
		AtLine startWith(ElkNode t);
	}
	
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

	public static final Color SOURCE = Color.CYAN;

	public static final Color SINK = Color.LIGHT_GRAY;

	List<GraphState> graphStateList = new LinkedList<>();
	GraphState current;

	private GraphStateListBuilder(ElkNode graph, String line) {
		var state = new GraphState();
		GraphStateUtil.saveState(graph, state);
		state.setPseudoCodeLine(line);
		current = state;
		current.setVerbosity(VerbosityEnum.ALLWAYS);
		graphStateList.add(current);
	}

	public static StartNode createBuilder() {
		return  graph -> line -> new GraphStateListBuilder(graph, line);
	}

	public List<GraphState> getList() {
		return List.copyOf(graphStateList);
	}

	public GraphStateBuilder atLine(String line) {
		current = new GraphState(current);
		current.setPseudoCodeLine(line);
		graphStateList.add(current);
		return  new GraphStateBuilder(current);
	}

	
	
	public class GraphStateBuilder {
		private GraphState current;
		
		private GraphStateBuilder(GraphState current) {
			this.current = current;
			current.setVerbosity(VerbosityEnum.DEFAULT);
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
		
		public GraphStateBuilder withVerbosity(IVerbosity verbos) {
			current.setVerbosity(verbos);
			return this;
		}

		
	}

}
