package de.webtwob.agd.project.file.toml;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import com.moandjiezana.toml.Toml;

import de.webtwob.agd.project.api.interfaces.IGraphLoader;

public class TOMLGraphLoader implements IGraphLoader {

	@Override
	public Optional<ElkNode> loadGraphFromFile(File file) {
		Toml toml = new Toml().read(file);
		return loadGraph(toml);
	}

	private Optional<ElkNode> loadGraph(Toml toml) {
		Map<String, ElkNode> idToNodeMap = new HashMap<>();
		var graph = loadNode(toml, idToNodeMap);
		return graph.flatMap(g -> loadEdges(g, toml, idToNodeMap));
	}

	private Optional<ElkNode> loadEdges(ElkNode elkNode, Toml toml, Map<String, ElkNode> idToNodeMap) {
		return loadEdges(elkNode, toml, idToNodeMap, new HashSet<>()) ? Optional.of(elkNode) : Optional.empty();
	}

	private boolean loadEdges(ElkNode elkNode, Toml toml, Map<String, ElkNode> idToNodeMap, Set<String> edgeIds) {

		if (toml.containsTableArray("edges")) {
			for (Toml edge : toml.getTables("edges")) {
				var maybeEdge = loadEdge(edge, idToNodeMap, edgeIds);
				if (maybeEdge.isPresent()) {
					elkNode.getContainedEdges().add(maybeEdge.get());
				} else {
					return false;
				}
			}
		}

		if (toml.containsTableArray("children")) {
			for (Toml child : toml.getTables("children")) {
				if (!loadEdges(idToNodeMap.get(child.getString("id")), child, idToNodeMap, edgeIds)) {
					return false;
				}
			}
		}

		return true;
	}

	private Optional<ElkEdge> loadEdge(Toml edgeToml, Map<String, ElkNode> idToNodeMap, Set<String> edgeIds) {
		if (!isValidEdge(edgeToml)) {
			return Optional.empty();
		}

		var id = edgeToml.getString("id");
		var targets = edgeToml.getList("targets");
		var sources = edgeToml.getList("sources");
		
		if(edgeIds.contains(id)) {
			return Optional.empty();
		}
		
		var edge = ElkGraphUtil.createEdge(null);
		edge.setIdentifier(id);
		
		for(Object target :  targets) {
			if(! (target instanceof String)|| ! idToNodeMap.containsKey(target)) {
				return Optional.empty();
			}
			edge.getTargets().add(idToNodeMap.get(target));
		}
		
		for(Object source :  sources) {
			if(! (source instanceof String)|| ! idToNodeMap.containsKey(source)) {
				return Optional.empty();
			}
			edge.getSources().add(idToNodeMap.get(source));
		}
		

		return Optional.of(edge);
	}

	private boolean isValidEdge(Toml edge) {
		if (!edge.containsPrimitive("id")) {
			return false;
		}
		if (!edge.contains("targets")) {
			return false;
		}

		var targets = edge.getList("targets");
		if (targets.isEmpty() || !(targets.get(0) instanceof String)) {
			return false;
		}
		
		var sources = edge.getList("sources");
		if (sources.isEmpty() || !(sources.get(0) instanceof String)) {
			return false;
		}

		return true;
	}

	private Optional<ElkNode> loadNode(Toml toml, Map<String, ElkNode> idMap) {
		if (!isValidNode(toml)) {
			return Optional.empty();
		}
		
		ElkNode node = ElkGraphUtil.createGraph();
		var id = toml.getString("id");
		if (idMap.containsKey(id)) {
			// duplicate id
			return Optional.empty();
		}
		
		node.setIdentifier(id);
		
		node.setWidth(toml.getDouble("width",0.0));
		node.setHeight(toml.getDouble("height",0.0));
		
		idMap.put(id, node);

		if (toml.containsTableArray("children")) {
			for (Toml child : toml.getTables("children")) {
				var maybeChild = loadNode(child, idMap);
				if (maybeChild.isPresent()) {
					node.getChildren().add(maybeChild.get());
				} else {
					// failed to parse child, makes us fail to parse parent
					return Optional.empty();
				}
			}
		}

		return Optional.of(node);
	}

	private boolean isValidNode(Toml toml) {
		return toml.containsPrimitive("id");
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileNameExtensionFilter("TOML-Files", "toml");
	}

}
