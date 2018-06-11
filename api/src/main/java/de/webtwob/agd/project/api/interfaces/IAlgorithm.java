package de.webtwob.agd.project.api.interfaces;

import javax.swing.JPanel;

import org.eclipse.elk.graph.ElkNode;

/**
 * An Interface used to represent all displayable Algorithms
 * 
 */
public interface IAlgorithm {

	/**
	 * @returns a JPanel containing all Animated Components allready setup for
	 *          Animation the provided Graph
	 */
	JPanel getAnimationPanel(@SuppressWarnings("exports") ElkNode graph);

	String getName();

}
