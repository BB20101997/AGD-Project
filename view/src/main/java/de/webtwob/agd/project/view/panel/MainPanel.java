package de.webtwob.agd.project.view.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class MainPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//this will contain the algorithm specific animation layot
	JPanel algorithmPanel = new JPanel();
	ControllPanel controllPanel = new ControllPanel();
	
	/*
	 * In Java > Code Style > Formatter
	 * 	Edit > Off/On Tags enable Off/On Tags
	 * @formatter:off
	 * _________________________________
	 * |                      |   C     |
	 * |                      |   o     |
	 * |      Algorithm       |   n     |
	 * |      dependent       |   t     |
	 * |        view          |   r     |
	 * |                      |   o     |
	 * |                      |   l     |
	 * |                      |   l     |
	 * |______________________|___s_____|
	 * 
	 * @formatter:on
	 * 
	 * */
	
	public MainPanel() {
		
		setLayout(new GridBagLayout());
		
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 4;
		constraints.gridheight = 4;
		constraints.weightx = 4;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;


		add(algorithmPanel,constraints);
		
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
	
}
