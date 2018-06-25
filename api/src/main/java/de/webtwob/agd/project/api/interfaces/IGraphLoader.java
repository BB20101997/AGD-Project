package de.webtwob.agd.project.api.interfaces;

import java.io.File;
import java.util.Optional;

import javax.swing.filechooser.FileFilter;

import org.eclipse.elk.graph.ElkNode;

public interface IGraphLoader {

	/**
	 * @param file the File to try and load
	 * @return if the loading was successful return the graph wrapped in an optional else an empty optional
	 * */
	@SuppressWarnings("exports")
	Optional<ElkNode> loadGraphFromFile(File file);

	/**
	 * @return a filter for the JFileChooser Dialog
	 * */
	FileFilter getFileFilter();

}
