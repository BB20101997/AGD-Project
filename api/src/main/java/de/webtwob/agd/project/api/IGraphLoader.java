package de.webtwob.agd.project.api;

import java.io.File;
import java.util.Optional;

import javax.swing.filechooser.FileFilter;

import org.eclipse.elk.graph.ElkNode;

public interface IGraphLoader {

	@SuppressWarnings("exports")
	public Optional<ElkNode> loadGraphFromFile(File file);
	
	public FileFilter getFileFilter();
	
}
