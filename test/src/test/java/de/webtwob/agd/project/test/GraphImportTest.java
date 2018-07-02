package de.webtwob.agd.project.test;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.elk.graph.ElkNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.webtwob.agd.project.api.util.GraphLoaderHelper;
import de.webtwob.agd.project.file.json.JSONGraphLoader;
import de.webtwob.agd.project.file.toml.TOMLGraphLoader;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
@SuppressWarnings("javadoc")
public class GraphImportTest {

	@Test
	public void importGraphFromJSONFile() {

		var file = new File("src/test/resources/importTestGraph.json").getAbsoluteFile();
		var optNode = new JSONGraphLoader().loadGraphFromFile(file);

		Assertions.assertTrue(optNode.isPresent(), "Importer returned no Graph");
		ElkNode node = optNode.get();
		Assertions.assertEquals("root", node.getIdentifier());
		Assertions.assertEquals(2, node.getChildren().size());
		Assertions.assertEquals(Set.of("n1", "n2"),
				node.getChildren().stream().map(ElkNode::getIdentifier).collect(Collectors.toSet()));
	}
	
	@Test
	public void importGraphFromTOMLFile() {

		var file = new File("src/test/resources/importTestGraph.toml").getAbsoluteFile();
		var optNode = new TOMLGraphLoader().loadGraphFromFile(file);

		Assertions.assertTrue(optNode.isPresent(), "Importer returned no Graph");
		ElkNode node = optNode.get();
		Assertions.assertEquals("root", node.getIdentifier());
		Assertions.assertEquals(2, node.getChildren().size());
		Assertions.assertEquals(Set.of("n1", "n2"),
				node.getChildren().stream().map(ElkNode::getIdentifier).collect(Collectors.toSet()));
	}
	
	@Test
	public void serviceLoaderTestTOMLFile() {
		var graph = GraphLoaderHelper.loadGraph(new File("src/test/resources/importTestGraph.toml").getAbsoluteFile());
		Assertions.assertTrue(graph.isPresent());
	}
	
	@Test
	public void serviceLoaderTestJSONFile() {
		var graph = GraphLoaderHelper.loadGraph(new File("src/test/resources/importTestGraph.json").getAbsoluteFile());
		Assertions.assertTrue(graph.isPresent());
	}
}
