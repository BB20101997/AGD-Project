package de.webtwob.agd.project.api.interfaces;

import org.eclipse.elk.graph.ElkNode;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Optional;

public interface IGraphLoader {

	@SuppressWarnings("exports")
	Optional<ElkNode> loadGraphFromFile(File file);

	FileFilter getFileFilter();

}
