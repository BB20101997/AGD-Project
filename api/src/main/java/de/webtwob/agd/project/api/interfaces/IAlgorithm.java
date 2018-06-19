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
	 * @param panel
	 *            the panel to insert the Animation Setup into
	 * @param graph
	 *            the graph to animate
	 * 
	 * @return a the new AnimationSyncThread for the generated Animation
	 */
	IAnimation getAnimationPanel(JPanel panel, @SuppressWarnings("exports") ElkNode graph,AnimationSyncThread syncThread);
	
	String getPseudoCode();

	/**
	 * @return the name of the algorithm
	 */
	String getName();

}
