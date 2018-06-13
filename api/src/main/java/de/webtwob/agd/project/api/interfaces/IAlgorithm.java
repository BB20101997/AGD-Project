package de.webtwob.agd.project.api.interfaces;

import javax.swing.JPanel;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.AnimationSyncThread;

/**
 * An Interface used to represent all Algorithms that can be animated
 * 
 */
public interface IAlgorithm {

	/**
	 * @returns a JPanel containing all Animated Components already setup for
	 *          Animation the provided Graph
	 */
	AnimationSyncThread getAnimationPanel(JPanel panel,@SuppressWarnings("exports") ElkNode graph);
	
	String getName();

}
