package de.webtwob.agd.project.file.json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.json.ElkGraphJson;
import org.eclipse.elk.graph.json.JsonImportException;

import com.google.gson.JsonParseException;

import de.webtwob.agd.project.api.interfaces.IGraphLoader;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
public class JSONGraphLoader implements IGraphLoader {

	public JSONGraphLoader() {
		// this constructor needs to exist for ServiceLoader
		// we added it explicitly to make sure it is there in case we add
		// more constructors
	}

	/**
	 * @param file
	 *            the file to try and load a graph from
	 * @return an Optional which if loading was successful contains the loaded graph
	 * 
	 *         Uses ElkGraphJson to import an ElkGraph from a Json file
	 */
	@SuppressWarnings("exports") // automatic modules should not be exported
	public static Optional<ElkNode> importGraphFromFile(File file) {
		if (file.exists() && file.isFile() && file.canRead()) {
			try {
				return Files.readAllLines(file.toPath().toAbsolutePath()).stream().reduce((s1, s2) -> s1 + s2)
						.map(ElkGraphJson::forGraph).map(ElkGraphJson.ImportBuilder::toElk);
			} catch (IOException | JsonParseException | JsonImportException e) {
				e.printStackTrace();
			}
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
