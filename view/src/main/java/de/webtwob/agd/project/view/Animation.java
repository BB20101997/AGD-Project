package de.webtwob.agd.project.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.service.util.GraphMapping;
import de.webtwob.agd.project.service.util.GraphMapping.Pair;

import static de.webtwob.agd.project.service.util.ViewUtil.getCurrent;

public class Animation {

	long lengthInMills;
	int scale = 10;
	double speed;

	GraphMapping mapping;
	ElkNode root;

	/**
	 * length in Frames
	 */
	@SuppressWarnings("exports")
	public Animation(ElkNode root, GraphMapping mapping, int length) {
		this.root = root;
		this.mapping = mapping;
		lengthInMills = length;
	}

	public BufferedImage generateFrame(long frame, BufferedImage img) {
		if(img == null||(img.getHeight()!=getHeight())||(img.getWidth()!=getWidth())) {
			img = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		
		Graphics2D graphic = (Graphics2D) img.getGraphics();
		graphic.scale(scale,scale);
		graphic.setStroke(new BasicStroke(0.1f));
		
		graphic.setBackground(Color.WHITE);
		graphic.clearRect(0, 0, getWidth(), getHeight());

		graphic.setColor(Color.BLACK);

		for (ElkNode child : root.getChildren()) {
			drawNode(child, graphic, frame);
		}

		for (ElkEdge edge : root.getContainedEdges()) {
			drawEdge(edge, graphic, frame);
		}
		return img;
	}

	private void drawNode(ElkNode node, Graphics2D graphic, long frame) {
		int x = (int) getCurrent(mapping.getMapping(node).start.getX(), mapping.getMapping(node).end.getX(), frame,
				lengthInMills);
		int y = (int) getCurrent(mapping.getMapping(node).start.getY(), mapping.getMapping(node).end.getY(), frame,
				lengthInMills);
		int width = (int) getCurrent(mapping.getMapping(node).start.getWidth(), mapping.getMapping(node).end.getWidth(),
				frame, lengthInMills);
		int height = (int) getCurrent(mapping.getMapping(node).start.getHeight(),
				mapping.getMapping(node).end.getHeight(), frame, lengthInMills);

		graphic.drawRect(x, y, width, height);

		// draw the sub-graph
		Graphics2D subGraphic = (Graphics2D) graphic.create(x, y, width, height);
		node.getChildren().forEach(child -> drawEdge((ElkEdge) child, subGraphic, frame));
		node.getContainedEdges().forEach(edge -> drawEdge(edge, subGraphic, frame));
		subGraphic.dispose();
	}

	protected void drawEdge(ElkEdge e, Graphics2D g, long frame) {
		e.getSections().forEach(s -> drawEdgeSection(s, g, frame));
	}

	private void drawEdgeSection(ElkEdgeSection s, Graphics2D g, long frame) {
		// TODO optionally draw arrows at the end of an edge

		int[] xCoords = new int[s.getBendPoints().size() + 2];
		int[] yCoords = new int[s.getBendPoints().size() + 2];
		// mapping.pointInTime.EndOfSection.pos
		xCoords[0] = (int) getCurrent(mapping.getMapping(s).start.start.getX(), mapping.getMapping(s).end.start.getX(),
				frame, lengthInMills);
		yCoords[0] = (int) getCurrent(mapping.getMapping(s).start.start.getY(), mapping.getMapping(s).end.start.getY(),
				frame, lengthInMills);
		xCoords[xCoords.length - 1] = (int) getCurrent(mapping.getMapping(s).start.end.getX(),
				mapping.getMapping(s).end.end.getX(), frame, lengthInMills);
		yCoords[yCoords.length - 1] = (int) getCurrent(mapping.getMapping(s).start.end.getY(),
				mapping.getMapping(s).end.end.getY(), frame, lengthInMills);

		for (int i = 0; i < s.getBendPoints().size(); i++) {
			Point p = getBendPoint(s.getBendPoints().get(i), frame);
			xCoords[i + 1] = (int) p.getX();
			yCoords[i + 1] = (int) p.getY();
		}

		g.drawPolyline(xCoords, yCoords, xCoords.length);
	}

	private Point getBendPoint(ElkBendPoint p, long frame) {
		Pair<Point2D.Double> bendMapping = mapping.getMapping(p);
		return new Point((int) getCurrent(bendMapping.start.getX(), bendMapping.end.getX(), frame, lengthInMills),
				(int) getCurrent(bendMapping.start.getY(), bendMapping.end.getY(), frame, lengthInMills));
	}

	public int getWidth() {
		return (int) Math.max(mapping.getMapping(root).start.getWidth(), mapping.getMapping(root).end.getWidth())*scale;
	}


	public int getHeight() {
		return (int) Math.max(mapping.getMapping(root).start.getHeight(), mapping.getMapping(root).end.getHeight())*scale;
	}

	public void setLength(int length) {
		lengthInMills = length;
	}
}
