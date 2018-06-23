package de.webtwob.agd.project.view.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JSlider;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.ControllerModel;
import de.webtwob.agd.project.api.enums.LoopEnum;
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
	transient ControllerModel model;
	transient Thread syncThread;
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

		timeLine.addChangeListener(event -> model.setFrame(timeLine.getValue()));

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
		if (model == null) {
			model = new ControllerModel();
			model.subscribeToAnimationEvent(event -> {
				if (event instanceof AnimationUpdateEvent) {
					var val = (int) ((AnimationUpdateEvent) event).getFrame();
					if (timeLine.getValue() != val && !timeLine.getValueIsAdjusting()) {
						timeLine.setValue(val);
					}
				}
			});
			pseudocodeView.setModel(model);
			controllPanel.setModel(model);
			model.start();
		}
		
		model.setPaused(true);
		model.setFrame(0);
		timeLine.setValue(0);

		model.removeAnimation(animation);

		if (algorithm != null) {
			pseudocodeView.setText(algorithm.getPseudoCode());
			if (graph != null) {
				animation = algorithm.getAnimationPanel(algorithmPanel, graph, model);
				model.addAnimation(animation);
				pseudocodeView.setAnimation(animation);
				timeLine.setMaximum((int) model.getEndAnimationAt());
				model.setPaused(false);
			}
		}
		
		model.setSpeed(Math.abs(model.getSpeed()));
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
		model.setLoopAction(item);
	}

	public ControllerModel getModel() {
		return model;
	}

}
