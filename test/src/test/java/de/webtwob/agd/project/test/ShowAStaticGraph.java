package de.webtwob.agd.project.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.webtwob.agd.project.api.util.GraphLoaderHelper;
import de.webtwob.agd.project.view.AnimatedView;

public class ShowAStaticGraph {

	public static void main(String[] args) {

		AnimatedView sgv = new AnimatedView();
		sgv.setMinimumSize(new Dimension(400, 400));
		sgv.setPreferredSize(new Dimension(400, 400));
		GraphLoaderHelper.loadGraph(new File("src/test/resources/staticTest.json")).ifPresent(sgv::setGraph);

		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(sgv, BorderLayout.CENTER);
		frame.setTitle("Loads a predefined Json File and displayes it's contained Graph!");
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}
