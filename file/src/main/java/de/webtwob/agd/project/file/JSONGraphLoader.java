package de.webtwob.agd.project.file;

import com.google.gson.JsonParseException;

import de.webtwob.agd.project.api.interfaces.IGraphLoader;

import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.json.ElkGraphJson;
import org.eclipse.elk.graph.json.JsonImportException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
public class JSONGraphLoader implements IGraphLoader{

    public JSONGraphLoader(){}

    /**
     * Uses ElkGraphJson to import an ElkGraph from a Json file
     * */
    @SuppressWarnings("exports") //automatic modules should not be exported
	public static Optional<ElkNode> importGraphFromFile(File file) {
        if (file.exists() && file.isFile() && file.canRead()) {
            try {
                return Files.readAllLines(file.toPath().toAbsolutePath())
                                      .stream()
                                      .reduce((s1, s2) -> s1 + s2)
                                      .map(ElkGraphJson::forGraph)
                                      .map(e->e.toElk());
            } catch (IOException | JsonParseException | JsonImportException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println(file.getAbsolutePath());
        }

        return Optional.empty();
    }

	@SuppressWarnings("exports")
	@Override
	public Optional<ElkNode> loadGraphFromFile(File file) {
		return importGraphFromFile(file);
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileNameExtensionFilter("JSON-Files", "json");
	}
}
