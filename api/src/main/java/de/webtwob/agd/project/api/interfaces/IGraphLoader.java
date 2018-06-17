package de.webtwob.agd.project.api.interfaces;

import java.io.File;
import java.util.Optional;

import javax.swing.filechooser.FileFilter;

import org.eclipse.elk.graph.ElkNode;

public interface IGraphLoader {

	@SuppressWarnings("exports")
	Optional<ElkNode> loadGraphFromFile(File file);

	FileFilter getFileFilter();

}
