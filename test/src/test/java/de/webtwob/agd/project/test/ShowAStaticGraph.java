package de.webtwob.agd.project.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.webtwob.agd.project.file.GraphImport;
import de.webtwob.agd.project.view.AnimatedGraphView;

public class ShowAStaticGraph {

	public static void main(String[] args) {


		AnimatedGraphView sgv = new AnimatedGraphView();
		sgv.setMinimumSize(new Dimension(400, 400));
		sgv.setPreferredSize(new Dimension(400, 400));
		GraphImport.importGraphFromFile(new File("src/test/resources/staticTest.json")).ifPresent(sgv::setGraph);
		
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(sgv,BorderLayout.CENTER);
		frame.setTitle("Loads a predefined Json File and displayes it's contained Graph!");
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}
