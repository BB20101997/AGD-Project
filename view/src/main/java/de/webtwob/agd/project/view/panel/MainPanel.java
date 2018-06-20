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
import de.webtwob.agd.project.api.interfaces.IController;
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
	IAlgorithm algorithm;
	IAnimation animation;
	AnimationSyncThread syncThread;
	JSlider timeLine;
	transient ElkNode graph;

	/**
	 * @param controller
	 *            the controller to use (only passed on to {@link ControllPanel})
	 */
	public MainPanel(IController controller) {

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

		// algorithmPanel.setBackground(Color.BLUE);

		controllPanel = new ControllPanel(this, controller);

		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 5;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;

		add(controllPanel, constraints);

		timeLine = new JSlider();
		timeLine.setMaximum((int) syncThread.getEndAnimationAt());
		timeLine.setMajorTickSpacing(500);
		timeLine.setPaintTicks(true);

		syncThread.subscribeToAnimationEvent(event -> {
			if (event instanceof AnimationUpdateEvent) {
				var val = (int) ((AnimationUpdateEvent) event).getFrame();
				if (timeLine.getValue() != val && !timeLine.getValueIsAdjusting()) {
					timeLine.setValue(val);
				}
			}
		});

		timeLine.addChangeListener(event -> {
			// timeLine.setValueIsAdjusting(true);
			syncThread.setFrame(timeLine.getValue());
			// timeLine.setValueIsAdjusting(false);
		});

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;

		add(timeLine, constraints);
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
			syncThread.setLoopAction(LoopEnum.LOOP);
			pseudocodeView.setSyncThread(syncThread);
			syncThread.start();
		} else {
			syncThread.setPaused(true);
			syncThread.setFrame(0);
			syncThread.setLoopAction(LoopEnum.LOOP);
		}
		syncThread.removeAnimation(animation);
		
		if (algorithm != null) {
			pseudocodeView.setText(algorithm.getPseudoCode());
			if (graph != null) {
				animation = algorithm.getAnimationPanel(algorithmPanel, graph, syncThread);
				syncThread.addAnimation(animation);
				pseudocodeView.setAnimation(animation);
				timeLine.setMaximum((int) syncThread.getEndAnimationAt());
			}
		}
		syncThread.setSpeed(Math.abs(syncThread.getSpeed()));
		syncThread.setPaused(false);
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
