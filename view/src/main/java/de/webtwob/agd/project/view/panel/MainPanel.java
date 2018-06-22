package de.webtwob.agd.project.view.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JSlider;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.LoopEnum;
import de.webtwob.agd.project.api.events.AnimationUpdateEvent;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.view.PseudocodeView;

public class MainPanel extends JPanel {

	/**
	 * the serial version uid
	 */
	private static final long serialVersionUID = 1L;

	// this will contain the algorithm specific animation layout
	JPanel algorithmPanel;
	PseudocodeView pseudocodeView;
	ControllPanel controllPanel;
	transient IAlgorithm algorithm;
	transient IAnimation animation;
	transient AnimationSyncThread syncThread;
	JSlider timeLine;
	transient ElkNode graph;

	/**
	 */
	public MainPanel() {

		setLayout(new GridBagLayout());

		GridBagConstraints constraints;

		pseudocodeView = new PseudocodeView();

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 4;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;

		add(pseudocodeView, constraints);

		algorithmPanel = new JPanel();

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 4;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;

		add(algorithmPanel, constraints);

		timeLine = new JSlider();
		timeLine.setMajorTickSpacing(500);
		timeLine.setPaintTicks(true);

		timeLine.addChangeListener(event -> syncThread.setFrame(timeLine.getValue()));

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;

		add(timeLine, constraints);
		
		controllPanel = new ControllPanel();
		controllPanel.setMainPanel(this);

		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 5;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;

		add(controllPanel, constraints);
		
		revalidate();
		repaint();

	}

	public void setGraph(ElkNode node) {
		if (this.graph == node)
			return;
		this.graph = node;
		redoAnimationPanel();
	}

	public void redoAnimationPanel() {
		algorithmPanel.removeAll();
		if (syncThread == null) {
			syncThread = new AnimationSyncThread();
			syncThread.subscribeToAnimationEvent(event -> {
				if (event instanceof AnimationUpdateEvent) {
					var val = (int) ((AnimationUpdateEvent) event).getFrame();
					if (timeLine.getValue() != val && !timeLine.getValueIsAdjusting()) {
						timeLine.setValue(val);
					}
				}
			});
			pseudocodeView.setSyncThread(syncThread);
			controllPanel.setSyncThread(syncThread);
			syncThread.start();
		}
		
		syncThread.setPaused(true);
		syncThread.setFrame(0);
		timeLine.setValue(0);

		syncThread.removeAnimation(animation);

		if (algorithm != null) {
			pseudocodeView.setText(algorithm.getPseudoCode());
			if (graph != null) {
				animation = algorithm.getAnimationPanel(algorithmPanel, graph, syncThread);
				syncThread.addAnimation(animation);
				pseudocodeView.setAnimation(animation);
				timeLine.setMaximum((int) syncThread.getEndAnimationAt());
				syncThread.setPaused(false);
			}
		}
		
		syncThread.setSpeed(Math.abs(syncThread.getSpeed()));
		revalidate();
		repaint();
	}

	public void setAlgorithm(IAlgorithm alg) {
		if (algorithm != alg) {
			algorithm = alg;
			redoAnimationPanel();
		}
	}

	public void setLoopType(LoopEnum item) {
		syncThread.setLoopAction(item);
	}

	public AnimationSyncThread getSyncThread() {
		return syncThread;
	}

}
