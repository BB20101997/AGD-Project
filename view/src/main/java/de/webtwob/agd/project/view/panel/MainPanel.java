package de.webtwob.agd.project.view.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;

import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

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
	JPanel algorithmTopoPanel;
	PseudocodeView pseudocodeView;
	ControllPanel controllPanel;
	transient IAlgorithm algorithm;
	transient IAnimation animation;
	transient IAnimation animationTopo;
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
		
		algorithmTopoPanel = new JPanel();

		constraints = new GridBagConstraints();
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 4;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;

		add(algorithmTopoPanel, constraints);

		timeLine = new JSlider();
		timeLine.setMajorTickSpacing(500);
		timeLine.setPaintTicks(true);

		timeLine.addChangeListener(event -> model.setFrame(timeLine.getValue()));

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;

		add(timeLine, constraints);

		controllPanel = new ControllPanel();
		controllPanel.setMainPanel(this);

		constraints = new GridBagConstraints();
		constraints.gridx = 3;
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

	private void redoAnimationPanel() {
		algorithmPanel.removeAll();
		algorithmTopoPanel.removeAll();
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
				animationTopo = algorithm.getAnimationPanelTopo(algorithmTopoPanel, graph, model);
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

	/**
	 * @return a JMenuItem for saving the current animation to a gif
	 * */
	public JMenuItem getSaveMenuItem() {
		var save = new JMenuItem("Save Animation");
		save.addActionListener(e -> {
			var choose = new JFileChooser();
			choose.setFileSelectionMode(JFileChooser.FILES_ONLY);
			choose.setMultiSelectionEnabled(false);
			if (choose.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				if(choose.getSelectedFile().exists()) {
					var answer = JOptionPane.showConfirmDialog(this, "The selected File exists already, overwrite?", "Overwrite File?", JOptionPane.YES_NO_OPTION);
					if(answer!=JOptionPane.YES_OPTION) {
						return;
					}
				}else {
					choose.getSelectedFile().getParentFile().mkdirs();
				}
				var dialog = new JDialog();
				var progressBar = new JProgressBar();
				dialog.add(progressBar);
				saveAnimation(choose.getSelectedFile(),progressBar);
				dialog.setTitle("Animation saving Progress");
				dialog.pack();
				dialog.setLocationRelativeTo(this);
				dialog.setLocation(this.getWidth()/2, this.getHeight()/2);
				dialog.setVisible(true);
				progressBar.addChangeListener(event->{
					if(progressBar.getValue()==progressBar.getMaximum()) {
						dialog.setVisible(false);
						dialog.dispose();
					}
					});
			}
		});
		return save;
	}

	private void saveAnimation(File file, JProgressBar progressBar){

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

				writer.prepareWriteSequence(null);
				
				progressBar.setMaximum((int) (animation.getLength()/100));

				for (long frame = 0; frame < animation.getLength(); frame += 100) {
					BufferedImage frameImage = new BufferedImage((int) Math.ceil(animation.getWidth()*Math.sqrt(10)),
							(int) Math.ceil(animation.getHeight()*Math.sqrt(10)), BufferedImage.TYPE_INT_RGB);
					
					var canvis = frameImage.createGraphics();
					canvis.fillRect(0, 0, frameImage.getWidth(), frameImage.getHeight());
					canvis.setBackground(Color.WHITE);
					canvis.setColor(Color.BLACK);
					canvis.scale(Math.sqrt(10), Math.sqrt(10));
					animation.generateFrame(frame, canvis);
					canvis.dispose();
					writer.writeToSequence(new IIOImage(frameImage, null, null), null);
					progressBar.setValue((int)(frame/100));
				}

				writer.endWriteSequence();
				writer.reset();
				writer.dispose();

				stream.flush();
				stream.close();
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
