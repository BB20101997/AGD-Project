package de.webtwob.agd.project.main;

import java.awt.BorderLayout;
import javax.swing.JFrame;

import de.webtwob.agd.project.view.panel.MainPanel;

public class Main {

	
	public static void main(String[] args) {

		JFrame frame = new JFrame("Cycle Break Animation");

		MainPanel mainPanel = new MainPanel();
		
		frame.setLayout(new BorderLayout());
		
		frame.add(mainPanel,BorderLayout.CENTER);

		frame.setVisible(true);
		frame.pack();

	}

}
