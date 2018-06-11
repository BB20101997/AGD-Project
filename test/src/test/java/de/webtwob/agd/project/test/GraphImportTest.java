package de.webtwob.agd.project.test;

import org.eclipse.elk.graph.ElkNode;
import org.junit.Assert;
import org.junit.Test;

import de.webtwob.agd.project.file.JSONGraphLoader;

import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
public class GraphImportTest {

    @Test
    public void importGraphFromFile() {
    	//TODO why does IGraphLoader.loadGraph not work here, try adding a testRuntime dependency on the file project
        Optional<ElkNode> optNode = JSONGraphLoader.importGraphFromFile(new File("src/test/resources/importTestGraph.json"));
        Assert.assertTrue("Importer returned no Graph",optNode.isPresent());
        ElkNode node = optNode.get();
        Assert.assertEquals("root", node.getIdentifier());
        Assert.assertEquals(2, node.getChildren().size());
        Assert.assertEquals(Set.of("n1","n2"),node.getChildren().stream().map(ElkNode::getIdentifier).collect(Collectors.toSet()));
    }
}
