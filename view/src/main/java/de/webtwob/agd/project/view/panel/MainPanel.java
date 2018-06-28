package de.webtwob.agd.project.view.panel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSplitPane;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.ControllerModel;
import de.webtwob.agd.project.api.enums.LoopEnum;
import de.webtwob.agd.project.api.events.AnimationUpdateEvent;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.view.AnimatedView;
import de.webtwob.agd.project.view.AnimationTopo;
import de.webtwob.agd.project.view.CompoundAnimation;
import de.webtwob.agd.project.view.PseudocodeView;

public class MainPanel extends JPanel {

	/**
	 * the serial version uid
	 */
	private static final long serialVersionUID = 1L;

	// this will contain the algorithm specific animation layout
	private JSplitPane algorithmPanel;
	private PseudocodeView pseudocodeView;
	private ControllPanel controllPanel;
	private transient IAlgorithm algorithm;
	private transient IAnimation animation;
	private transient IAnimation animationTopo;
	private transient ControllerModel model;
	private JSlider timeLine;
	private transient ElkNode graph;

	public MainPanel() {

		setLayout(new GridBagLayout());

		GridBagConstraints constraints;

		pseudocodeView = new PseudocodeView();

		algorithmPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		algorithmPanel.setBorder(BorderFactory.createEmptyBorder());
		algorithmPanel.setResizeWeight(1);
		algorithmPanel.setOneTouchExpandable(true);

		var splitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitePane.setBorder(BorderFactory.createEmptyBorder());
		splitePane.setResizeWeight(0);
		splitePane.setOneTouchExpandable(true);

		splitePane.setLeftComponent(pseudocodeView);
		splitePane.setRightComponent(algorithmPanel);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;

		add(splitePane, constraints);

		timeLine = new JSlider();
		timeLine.setMajorTickSpacing(500);
		timeLine.setPaintTicks(true);

		timeLine.addChangeListener(event -> model.setFrame(timeLine.getValue()));

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;

		add(timeLine, constraints);

		controllPanel = new ControllPanel();
		controllPanel.setMainPanel(this);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;

		add(controllPanel, constraints);

		revalidate();
		repaint();

	}

	/**
	 * @param node
	 *            the graph to animate
	 */
	public void setGraph(ElkNode node) {
		if (this.graph == node)
			return;
		this.graph = node;
		redoAnimationPanel();
	}

	private void redoAnimationPanel() {
		algorithmPanel.setLeftComponent(null);
		algorithmPanel.setRightComponent(null);

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

		model.removeAllAnimations();
		model.setPaused(true);
		model.setFrame(0);
		timeLine.setValue(0);

		if (algorithm != null) {
			pseudocodeView.setText(algorithm.getPseudoCode());
			if (graph != null) {
				var states = algorithm.getGraphStates(graph);

				animation = new CompoundAnimation(graph, states, 500);
				model.addAnimation(animation);
				pseudocodeView.setAnimation(animation);

				var animView = new AnimatedView(model);
				animView.setAnimation(animation);
				algorithmPanel.add(animView, JSplitPane.LEFT);
				algorithmPanel.setBackground(Color.red);

				if (algorithm.animationTopology()) {
					animationTopo = new CompoundAnimation(graph, states, 500, AnimationTopo::new);
					model.addAnimation(animationTopo);
					var animTopoView = new AnimatedView(model);
					animTopoView.setAnimation(animationTopo);
					animTopoView.setFixed(true);
					algorithmPanel.add(animTopoView, JSplitPane.RIGHT);
					var ratio = animationTopo.getWidth() / animationTopo.getHeight();
					algorithmPanel.setDividerLocation(
							(int) (algorithmPanel.getMaximumDividerLocation() - ratio * algorithmPanel.getHeight()));
				}else {
					animationTopo = null;
				}

				repaint();

				timeLine.setMaximum((int) model.getEndAnimationAt());
				model.setPaused(false);
			}
		}

		model.setSpeed(Math.abs(model.getSpeed()));
		revalidate();
		repaint();
	}

	/**
	 * @param alg
	 *            change the algorithm to this
	 */
	public void setAlgorithm(IAlgorithm alg) {
		if (algorithm != alg) {
			algorithm = alg;
			redoAnimationPanel();
		}
	}

	/**
	 * @param item
	 *            the value to set the models' Action to be performed at the end of
	 *            the animation
	 * @see ControllerModel#setLoopAction(LoopEnum)
	 */
	public void setLoopType(LoopEnum item) {
		model.setLoopAction(item);
	}

	/**
	 * @return the current model
	 */
	public ControllerModel getModel() {
		return model;
	}

	/**
	 * @return a JMenuItem for saving the current animation to a gif
	 */
	public JMenuItem getSaveMenuItem() {
		var save = new JMenuItem("Save Animation");
		save.addActionListener(e -> {
			var choose = new JFileChooser();
			choose.setFileSelectionMode(JFileChooser.FILES_ONLY);
			choose.setMultiSelectionEnabled(false);
			if (choose.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				if (choose.getSelectedFile().exists()) {
					var answer = JOptionPane.showConfirmDialog(this, "The selected File exists already, overwrite?",
							"Overwrite File?", JOptionPane.YES_NO_OPTION);
					if (answer != JOptionPane.YES_OPTION) {
						return;
					}
				} else {
					choose.getSelectedFile().getParentFile().mkdirs();
				}
				var dialog = new JDialog();
				var progressBar = new JProgressBar();
				dialog.add(progressBar);
				saveAnimation(choose.getSelectedFile(), progressBar);
				dialog.setTitle("Animation saving Progress");
				dialog.pack();
				dialog.setLocationRelativeTo(this);
				dialog.setLocation(this.getWidth() / 2, this.getHeight() / 2);
				dialog.setVisible(true);
				progressBar.addChangeListener(event -> {
					if (progressBar.getValue() == progressBar.getMaximum()) {
						dialog.setVisible(false);
						dialog.dispose();
					}
				});
			}
		});
		return save;
	}

	/**
	 * @param file
	 *            the file to save the Animation into
	 * @param progressBar
	 *            to be kept updated
	 */
	private void saveAnimation(File file, JProgressBar progressBar) {

		var thread = new Thread(() -> {

			try {
				var imageio = ImageIO.getImageWriters(
						ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB), "gif");

				ImageWriter writer = null;

				while (imageio.hasNext()) {
					ImageWriter tmp = imageio.next();
					if (tmp.canWriteSequence()) {
						writer = tmp;
						break;
					}
				}

				if (writer == null) {
					JOptionPane.showMessageDialog(this, "No ImageWriter for gif found!", "Error saving Animation!",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				var stream = ImageIO.createImageOutputStream(file);

				writer.setOutput(stream);

				/*
				 * start writing animation
				 */
				writer.prepareWriteSequence(null);

				progressBar.setMaximum((int) (animation.getLength() / 100));

				var scale = Math.sqrt(10);

				var animWidth = animation.getWidth() * scale;
				var animHeight = (int) (animation.getHeight() * scale);

				var topoScale = animationTopo!=null?(double) animHeight / animationTopo.getHeight():1;
				var topoWidth = animationTopo!=null?animationTopo.getWidth() * topoScale:0;

				var totalWidth = (int) Math.ceil(animWidth + topoWidth);

				var interval = 100;

				if (model.getDebug()) {
					// speed up gif generation by lowering the frame count and the resolution
					interval = 500;
					scale = 1;
				}

				BufferedImage frameImage;
				Graphics2D canvis;
				Graphics2D animCanvis;
				// draw and save every interval's frame
				for (long frame = 0; frame < animation.getLength(); frame += interval) {
					frameImage = new BufferedImage(totalWidth, animHeight, BufferedImage.TYPE_INT_RGB);

					canvis = frameImage.createGraphics();
					canvis.fillRect(0, 0, totalWidth, animHeight);
					canvis.setBackground(Color.WHITE);
					canvis.setColor(Color.BLACK);

					animCanvis = (Graphics2D) canvis.create(0, 0, (int) animWidth, (int) animHeight);
					animCanvis.scale(scale, scale);
					animation.generateFrame(frame, animCanvis);

					if (animationTopo != null) {
						var topoCanvis = (Graphics2D) canvis.create((int) animWidth, 0, (int) topoWidth, animHeight);
						topoCanvis.scale(topoScale, topoScale);

						animationTopo.generateFrame(frame, topoCanvis);
					}
					canvis.dispose();

					writer.writeToSequence(new IIOImage(frameImage, null, null), null);
					progressBar.setValue((int) (frame / 100));
				}

				// last frame not currently in animation
				if ((animation.getLength() - 1) % interval != 0) {
					frameImage = new BufferedImage(totalWidth, animHeight, BufferedImage.TYPE_INT_RGB);

					canvis = frameImage.createGraphics();
					canvis.fillRect(0, 0, totalWidth, animHeight);
					canvis.setBackground(Color.WHITE);
					canvis.setColor(Color.BLACK);

					animCanvis = (Graphics2D) canvis.create(0, 0, (int) animWidth, (int) animHeight);
					animCanvis.scale(scale, scale);
					animation.generateFrame(animation.getLength() - 1, animCanvis);

					if (animationTopo != null) {
						var topoCanvis = (Graphics2D) canvis.create((int) animWidth, 0, (int) topoWidth, animHeight);
						topoCanvis.scale(topoScale, topoScale);

						animationTopo.generateFrame(animation.getLength() - 1, topoCanvis);
					}
					
					canvis.dispose();

					writer.writeToSequence(new IIOImage(frameImage, null, null), null);
				}

				/*
				 * end writhing animation and cleanup resources
				 */
				writer.endWriteSequence();
				writer.reset();
				writer.dispose();

				/*
				 * make sure file is actually flushed and close it
				 */
				stream.flush();
				stream.close();

				/*
				 * complete progressbar
				 */
				progressBar.setValue(progressBar.getMaximum());
				JOptionPane.showMessageDialog(this, "Animation saving completed!", "Completed saving Animation!",
						JOptionPane.PLAIN_MESSAGE);
			} catch (IOException io) {
				JOptionPane.showMessageDialog(this, "IOException while saving Animation!", "Error saving Animation!",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		thread.setName("Animation Save Thread!");
		thread.setDaemon(true);
		thread.start();

	}

}
