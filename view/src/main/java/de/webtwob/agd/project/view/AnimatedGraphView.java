package de.webtwob.agd.project.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.RepaintManager;

import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import de.webtwob.agd.project.service.api.AnimationUpdateEvent;
import de.webtwob.agd.project.service.api.IGraphUpdateEventHandler;
import de.webtwob.agd.project.service.util.GraphMapping;
import de.webtwob.agd.project.service.util.GraphMapping.Pair;
import de.webtwob.agd.project.service.util.ViewUtil;

public class AnimatedGraphView extends StaticGraphView implements IGraphUpdateEventHandler {

	/**
	 * generated serialVarsionUID
	 */
	private static final long serialVersionUID = -6226316608311632721L;

	public static double DEFAULT_ANIMATION_LENGTH = 2000;

	GraphMapping graphMapping;

	Point originStart = new Point(0, 0);
	Point originEnd = new Point(0, 0);

	boolean isLooping;
	boolean reverseOnEnd;

	double animationPosition = 0; // current AnimationIteration [0,animationLength]
	double animationLength = 1; // how long should the animation be

	volatile double animationSpeed = 1; // by how much to increase the animationPosition after each iteration
	
	Thread animationThread = new Thread(this::animate);

	public AnimatedGraphView() {
		RepaintManager manager = RepaintManager.currentManager(this);
		manager.setDoubleBufferingEnabled(true);
		setDoubleBuffered(true);
		setIgnoreRepaint(true);
		setBackground(Color.WHITE);
		animationThread.setDaemon(true);
		animationThread.setName("AnimationGraphViewThread");
		animationThread.start();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		repaint();
	}

	public void setLooping(boolean loop) {
		isLooping = loop;
	}
	
	private void setAnimationSpeed(double speed) {
		if(animationSpeed==speed) {
			return;
		}
		
		if(animationSpeed==0) {
			synchronized (animationThread) {
				animationThread.notifyAll();
				animationSpeed = speed;
			}
		}else {
			animationSpeed  = speed;
		}
		
	}
	
	public void setAnimationPosition(double pos, boolean pause){
		if(pause) {
			animationSpeed = 0;
		}
		animationPosition = pos;
		repaint();
	}

	@SuppressWarnings("exports") // automatic modules should not be exported
	public void setGraph(ElkNode eg) {
		animationSpeed = 0;
		animationPosition = 0;
		graphMapping = new GraphMapping.DummyMapping();
		super.setGraph(eg);
	}

	/**
	 * @param length
	 *            how many frames long shall the animation be at speed 1
	 * 
	 *            If length is less or equal to 0 DEFAULT_ANIMATIN_LENGTH will be
	 *            used instead of length
	 */
	@SuppressWarnings("exports") // don't export automatic modules
	public void animateGraph(ElkNode graph, GraphMapping mapping, double length, double speed) {
		graphMapping = mapping;
		this.graph = graph;
		setAnimationSpeed(speed);
		animationLength = length > 0 ? length : DEFAULT_ANIMATION_LENGTH;
		animationPosition = 0;
	}

	@SuppressWarnings("exports") // not everyone need to send events
	public void animationUpdateEvent(AnimationUpdateEvent event) {
		// TODO adjust to new animation settings
	}

	private double getCurrent(double oldPos, double newPos) {
		return ViewUtil.getCurrent(oldPos, newPos, animationPosition, animationLength);
	}

	@Override
	protected void drawNode(ElkNode n, Graphics2D graphic) {
		int x = (int) getCurrent(graphMapping.getMapping(n).start.getX(), graphMapping.getMapping(n).end.getX());
		int y = (int) getCurrent(graphMapping.getMapping(n).start.getY(), graphMapping.getMapping(n).end.getY());
		int width = (int) getCurrent(graphMapping.getMapping(n).start.getWidth(), graphMapping.getMapping(n).end.getWidth());
		int height = (int) getCurrent(graphMapping.getMapping(n).start.getHeight(),	graphMapping.getMapping(n).end.getHeight());

		graphic.drawRect(x, y, width, height);
		
		// draw the sub-graph
		Graphics2D subGraphic = (Graphics2D) graphic.create(x, y, width, height);
		n.getChildren().forEach(child -> drawEdge((ElkEdge) child, subGraphic));
		n.getContainedEdges().forEach(edge -> drawEdge(edge, subGraphic));
	}

	@Override
	protected void drawEdgeSection(ElkEdgeSection s, Graphics2D g) {
		// TODO optionally draw arrows at the end of an edge

		int[] xCoords = new int[s.getBendPoints().size() + 2];
		int[] yCoords = new int[s.getBendPoints().size() + 2];
		//mapping.pointInTime.EndOfSection.pos
		xCoords[0]                = (int) getCurrent( graphMapping.getMapping(s).start.start.getX(),  graphMapping.getMapping(s).end.start.getX());
		yCoords[0]                = (int) getCurrent( graphMapping.getMapping(s).start.start.getY(),  graphMapping.getMapping(s).end.start.getY());
		xCoords[xCoords.length-1] = (int) getCurrent( graphMapping.getMapping(s).start.end.getX()  ,  graphMapping.getMapping(s).end.end.getX());
		yCoords[yCoords.length-1] = (int) getCurrent( graphMapping.getMapping(s).start.end.getY()  ,  graphMapping.getMapping(s).end.end.getY());

		for (int i = 0; i < s.getBendPoints().size(); i++) {
			Point p = getAnimatedBendPoint(s.getBendPoints().get(i));
			xCoords[i + 1] = (int) p.getX();
			yCoords[i + 1] = (int) p.getY();
		}

		g.drawPolyline(xCoords, yCoords, xCoords.length);
	}

	private Point getAnimatedBendPoint(ElkBendPoint p) {
		Pair<Point2D.Double> mapping = graphMapping.getMapping(p);
		return new Point((int)getCurrent(mapping.start.getX(), mapping.end.getX()),(int)getCurrent(mapping.start.getY(), mapping.end.getY()));
	}
	
	private void animate(){
		long time = System.currentTimeMillis();
		long offset;
		while(true) {
			if (animationSpeed==0) {
				synchronized (animationThread) {
					while(animationSpeed==0) {
						try {
							animationThread.wait();
						} catch (InterruptedException ignore) {}
					}
				}
			}
			
			paintImmediately(0, 0, getWidth(), getHeight());
			//update animationPosition
			animationPosition += animationSpeed;

			// stop the animation or restart it
			if (animationPosition > animationLength) {
				if (isLooping) {
					animationPosition = 0;
				} else if(reverseOnEnd){
					animationPosition = 0;
					animationSpeed *= -1;
				}else {
					animationSpeed = 0;
					animationPosition = animationLength;
					//TODO send AnimationEndEvent
					repaint();
				}
			} else if (animationPosition < 0) {
				if (isLooping) {
					animationPosition = animationLength;
				} else if(reverseOnEnd){
					animationPosition = animationLength;
					animationSpeed *= -1;
				}else {
					animationSpeed = 0;
					animationPosition = 0;
					//TODO send AnimationEndEvent
					repaint();
				}
			}
			
			//we limit us to 30 times per second or every 1000/30 milliseconds
			offset = 1000/30-System.currentTimeMillis()+time;
			
			if(offset>0) {
				try {
					synchronized (animationThread) {
						animationThread.wait(offset);
					}
				} catch (InterruptedException ignore) {}
			}
		}
	}

	public void setReverseOnEnd(boolean reverse) {
		reverseOnEnd = reverse;
	}

}
