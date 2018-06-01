package de.webtwob.agd.project.file;

import com.google.gson.JsonParseException;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.json.ElkGraphJson;
import org.eclipse.elk.graph.json.JsonImportException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
public class GraphImport {

    private GraphImport(){}

    /**
     * Uses ElkGraphJson to import an ElkGraph from a Json file
     * */
    @SuppressWarnings("exports") //automatic modules should not be exported
	public static Optional<ElkNode> importGraphFromFile(File file) {
        if (file.exists() && file.isFile() && file.canRead()) {
            try {
                String content = Files.readAllLines(file.toPath().toAbsolutePath())
                                      .stream()
                                      .reduce((s1, s2) -> s1 + s2)
                                      .orElse("");
                return Optional.of(ElkGraphJson.forGraph(content).toElk());
            } catch (IOException | JsonParseException | JsonImportException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println(file.getAbsolutePath());
        }

        return Optional.empty();
    }
}
