package de.webtwob.agd.project.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.file.GraphImport;
import de.webtwob.agd.project.service.util.ViewUtil;
import de.webtwob.agd.project.view.AnimatedGraphView;

public class ShowAnAnimatedGraph {

	public static void main(String[] args) {
		
		AnimatedGraphView sgv = new AnimatedGraphView();
		sgv.setMinimumSize(new Dimension(400, 400));
		sgv.setPreferredSize(new Dimension(400, 400));
		sgv.setLooping(false);
		sgv.setReverseOnEnd(true);
		
		ElkNode start = GraphImport.importGraphFromFile(new File("src/test/resources/animationTestStart.json")).orElse(null);
		ElkNode end   = GraphImport.importGraphFromFile(new File("src/test/resources/animationTestEnd.json")).orElse(null);
		
		sgv.animateGraph(start, ViewUtil.createMapping(start,end), 2000,1);
		
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(sgv,BorderLayout.CENTER);
		frame.setTitle("Loads predefined Json Files and displayes an Animated Graph!");
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}
