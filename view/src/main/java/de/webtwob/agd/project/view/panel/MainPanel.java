package de.webtwob.agd.project.view.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.LoopEnum;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.interfaces.IController;

public class MainPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// this will contain the algorithm specific animation layout
	JPanel algorithmPanel = new JPanel();
	ControllPanel controllPanel;
	IAlgorithm algorithm = null;
	AnimationSyncThread syncThread;
	transient ElkNode graph;

	/*
	 * In Java > Code Style > Formatter
	 * 	Edit > Off/On Tags enable Off/On Tags
	 * @formatter:off
	 * _________________________________
	 * |                      |         |
	 * |                      |    C    |
	 * |                      |    o    |
	 * |      Algorithm       |    n    |
	 * |      dependent       |    t    |
	 * |        view          |    r    |
	 * |                      |    o    |
	 * |                      |    l    |
	 * |                      |    l    |
	 * |                      |    s    |
	 * |______________________|_________|
	 * 
	 * 
	 * @formatter:on
	 * 
	 */

	public MainPanel(@SuppressWarnings("exports") ElkNode graph, IController controller) {

		this.graph = graph;
		setLayout(new GridBagLayout());

		controllPanel = new ControllPanel(this, controller);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 4;
		constraints.gridheight = 4;
		constraints.weightx = 4;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;

		add(algorithmPanel, constraints);

		algorithmPanel.setBackground(Color.BLUE);

		constraints = new GridBagConstraints();
		constraints.gridx = 4;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 4;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;

		add(controllPanel, constraints);

	}

	public void setAlgorithm(IAlgorithm alg) {
		algorithm = alg;
		algorithmPanel.removeAll();
		if(syncThread!=null) {
			syncThread.interrupt();
		}
		syncThread = algorithm.getAnimationPanel(algorithmPanel,graph);
		syncThread.setLoopAction(LoopEnum.LOOP);
		syncThread.start();
		
	}

}
