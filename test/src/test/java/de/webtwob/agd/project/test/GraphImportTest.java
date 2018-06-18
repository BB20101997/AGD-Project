package de.webtwob.agd.project.test;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.elk.graph.ElkNode;
import org.junit.Assert;
import org.junit.Test;

import de.webtwob.agd.project.file.json.JSONGraphLoader;
import de.webtwob.agd.project.file.toml.TOMLGraphLoader;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
@SuppressWarnings("unused")
public class GraphImportTest {

	@Test
	public void importGraphFromJSONFile() {

		// TODO why does GraphLoaderHelper not work here?
		var file = new File("src/test/resources/importTestGraph.json").getAbsoluteFile();
		var optNode = new JSONGraphLoader().loadGraphFromFile(file);

		Assert.assertTrue("Importer returned no Graph", optNode.isPresent());
		ElkNode node = optNode.get();
		Assert.assertEquals("root", node.getIdentifier());
		Assert.assertEquals(2, node.getChildren().size());
		Assert.assertEquals(Set.of("n1", "n2"),
				node.getChildren().stream().map(ElkNode::getIdentifier).collect(Collectors.toSet()));
	}
	
	@Test
	public void importGraphFromTOMLFile() {

		// TODO why does GraphLoaderHelper not work here?
		var file = new File("src/test/resources/importTestGraph.toml").getAbsoluteFile();
		var optNode = new TOMLGraphLoader().loadGraphFromFile(file);

		Assert.assertTrue("Importer returned no Graph", optNode.isPresent());
		ElkNode node = optNode.get();
		Assert.assertEquals("root", node.getIdentifier());
		Assert.assertEquals(2, node.getChildren().size());
		Assert.assertEquals(Set.of("n1", "n2"),
				node.getChildren().stream().map(ElkNode::getIdentifier).collect(Collectors.toSet()));
	}
}
