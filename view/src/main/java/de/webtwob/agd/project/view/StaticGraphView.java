package de.webtwob.agd.project.view;

import java.awt.Graphics;

import javax.swing.JComponent;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.service.api.GraphUpdateEvent;
import de.webtwob.agd.project.service.api.IGraphUpdateEventHandler;

public class StaticGraphView extends JComponent implements IGraphUpdateEventHandler{
	
	/**
	 *  generated serialVarsionUID
	 */
	private static final long serialVersionUID = -6226316608311632721L;
	
	private ElkNode graph = null;
	
	public StaticGraphView() {
		setDoubleBuffered(true);
	}
	
	public void setGraph(ElkNode eg){
		graph = eg;
		repaint();
	}
	
	@Override
	public void graphUpdate(GraphUpdateEvent event) {
		if(graph==event.getSource()) {
			repaint();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//TODO draw Graph
	}
	
	
	

}
