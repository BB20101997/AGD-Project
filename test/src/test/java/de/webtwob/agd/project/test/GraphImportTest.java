package de.webtwob.agd.project.test;

import org.eclipse.elk.graph.ElkNode;
import org.junit.Assert;
import org.junit.Test;

import de.webtwob.agd.project.api.interfaces.IGraphLoader;
import de.webtwob.agd.project.api.util.GraphLoaderHelper;

import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
@SuppressWarnings("unused")
public class GraphImportTest {

	@Test
	public void importGraphFromFile() {
		var optNode = GraphLoaderHelper.loadGraph(new File("src/test/resources/importTestGraph.json"));
		Assert.assertTrue("Importer returned no Graph", optNode.isPresent());
		ElkNode node = optNode.get();
		Assert.assertEquals("root", node.getIdentifier());
		Assert.assertEquals(2, node.getChildren().size());
		Assert.assertEquals(Set.of("n1", "n2"),
				node.getChildren().stream().map(ElkNode::getIdentifier).collect(Collectors.toSet()));
	}
}
