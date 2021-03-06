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

	/** 
	 *  The default Constructor used by the ServiceLoader to create a new Instance
	 */
	public JSONGraphLoader() {
		//since we have no other constructers this would not be nessesarcy be just to be save this is here anyway
	}

	@Override
	public Optional<ElkNode> loadGraphFromFile(File file) {
		if (file.exists() && file.isFile() && file.canRead()) {
			try {
				return Files.readAllLines(file.toPath().toAbsolutePath()).stream().reduce((s1, s2) -> s1 + s2)
						.map(ElkGraphJson::forGraph).map(ElkGraphJson.ImportBuilder::toElk);
			} catch (IOException | JsonParseException | JsonImportException e) {
				//loading failed going with empty optional
			}
		}

		return Optional.empty();
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileNameExtensionFilter("JSON-Files", "json");
	}
}
