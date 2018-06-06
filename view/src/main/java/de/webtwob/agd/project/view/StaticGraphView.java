package de.webtwob.agd.project.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;

import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.service.api.GraphUpdateEvent;
import de.webtwob.agd.project.service.api.IGraphUpdateEventHandler;

public class StaticGraphView extends JComponent implements IGraphUpdateEventHandler{
	
	/**
	 *  generated serialVarsionUID
	 */
	private static final long serialVersionUID = -6226316608311632721L;
	
	protected ElkNode graph = null;
	
	protected Point origin = new Point(10,10);
	protected double scale = 5;
	
	public StaticGraphView() {
		setDoubleBuffered(true);
		setIgnoreRepaint(true);
		setBackground(Color.WHITE);
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		repaint();
	}
	
	@SuppressWarnings("exports") //automatic modules should not be exported
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
			Graphics2D gCopy = (Graphics2D) g.create();	
			paintStaticGraph(gCopy);
	}
	
	protected void paintStaticGraph(Graphics2D graphic) {

		graphic.scale(scale, scale);
		graphic.translate((int)origin.getX(), (int)origin.getY());
		
		graphic.setColor(Color.BLACK);
		
		for(ElkNode child : graph.getChildren()){
			drawNode(child, graphic);
		}
		
		for(ElkEdge edge : graph.getContainedEdges()) {
			drawEdge(edge, graphic);
		}

	}
	
	protected void drawNode(ElkNode n, Graphics2D g) {
		g.drawRect((int)n.getX(),(int)n.getY(),(int)n.getWidth(),(int)n.getHeight());
		
		//draw the sub-graph
		Graphics2D subGraphic = (Graphics2D) g.create((int)n.getX(), (int)n.getY(), (int)n.getWidth(), (int)n.getHeight());
		n.getChildren().forEach(child->drawNode(child, subGraphic));
		n.getContainedEdges().forEach(edge -> drawEdge(edge,subGraphic));
	}
	
	protected void drawEdge(ElkEdge e, Graphics2D g) {
		e.getSections().forEach(s->drawEdgeSection(s,g));	
	}
	
	protected void drawEdgeSection(ElkEdgeSection s, Graphics2D g) {
		//TODO optionally draw arrows at the end of an edge
		
		int[] xCoords = new int[s.getBendPoints().size()+2];
		int[] yCoords = new int[s.getBendPoints().size()+2];
		xCoords[0] = (int) s.getStartX();
		yCoords[0] = (int) s.getStartY();
		xCoords[xCoords.length-1] = (int) s.getEndX();
		yCoords[xCoords.length-1] = (int) s.getEndY();
		
		for(int i = 0;i<s.getBendPoints().size();i++) {
			xCoords[i+1] = (int)s.getBendPoints().get(i).getX();
			yCoords[i+1] = (int)s.getBendPoints().get(i).getY();
		}
		
		g.drawPolyline(xCoords, yCoords, xCoords.length);
	}
	

}
