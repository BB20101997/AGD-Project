package de.webtwob.agd.project.view;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.service.api.AnimationEvent;
import de.webtwob.agd.project.service.api.AnimationEventHandler;
import de.webtwob.agd.project.service.util.GraphMapping;
import de.webtwob.agd.project.service.util.ViewUtil;

public class AnimatedGraphView extends JComponent {

	/**
	 * generated serialVarsionUID
	 */
	private static final long serialVersionUID = -6226316608311632721L;

	private volatile long frame;
	private volatile double scale = 1;
	
	Point mouseClick = new Point();

	Point2D.Double origin = new Point2D.Double(0, 0);
	Point2D.Double oldOrigin = new Point2D.Double();

	volatile Animation animation;

	private double speed = 1; // milliseconds skipped per millisecond
	Thread animationThread = new Thread(this::animate);
	
	List<AnimationEventHandler> handlerList = new LinkedList<>();

	LoopEnum end = LoopEnum.STOP;

	public AnimatedGraphView() {
		setDoubleBuffered(true);
		setBackground(Color.WHITE);
		animationThread.setDaemon(true);
		animationThread.setName("AnimationGraphViewThread");
		animationThread.start();
		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		//setFocusable(true);
		addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				//TODO this is somehow jumpy, also keep the point the mouse is at where it is
				scale += e.getUnitsToScroll();
			}
			
		});
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				mouseClick.setLocation(e.getPoint());
				oldOrigin.setLocation(origin);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				origin = new Point2D.Double(oldOrigin.getX()+e.getX()-mouseClick.getX(), oldOrigin.getY()+e.getY()-mouseClick.getY());
			}
		});
		
		
	}

	@SuppressWarnings("exports") // automatic modules should not be exported
	public void setGraph(ElkNode eg) {
		setSpeed(0);
		setFrame(0);
		animation = new Animation(eg,ViewUtil.createMapping(eg, eg), 2);
		repaint();
	}

	/**
	 * @param length
	 *            how many frames long shall the animation be at speed 1
	 * 
	 *            If length is less or equal to 0 DEFAULT_ANIMATIN_LENGTH will be
	 *            used instead of length
	 */
	@SuppressWarnings("exports") // don't export automatic modules
	public void animateGraph(ElkNode graph, GraphMapping mapping, int length) {
		animation = new Animation(graph, mapping, length);
		setSpeed(1);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (animation == null)
			return;
		Graphics2D graphic = (Graphics2D) g.create();

		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphic.setRenderingHint(RenderingHints.KEY_RESOLUTION_VARIANT,
				RenderingHints.VALUE_RESOLUTION_VARIANT_DPI_FIT);
		graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		graphic.translate(origin.getX(), origin.getY());
		graphic.scale(scale, scale);

		animation.generateFrame(getFrame(), graphic);

		graphic.dispose();
	}

	@SuppressWarnings("exports")
	@Override
	public void update(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		paint(g);
	}

	public void substcribeToAnimationEvent(AnimationEventHandler aeh) {
		handlerList.add(aeh);
	}
	
	private void animate() {

		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();

		while (true) {
			if (animation != null && getSpeed() != 0) {
				setFrame((long) (getFrame() + (end - start) * getSpeed()));
				start = System.currentTimeMillis();
				paintImmediately(0, 0, getWidth(), getHeight());
				if (getFrame() < 0 || getFrame() > animation.lengthInMills) {
					this.end.handle(this::setSpeed,this::setFrame, animation.lengthInMills, getFrame(), getSpeed());
					handlerList.parallelStream().forEach(h->EventQueue.invokeLater(()->h.animationEvent(new  AnimationEvent())));
				}
				end = System.currentTimeMillis();
			}
		}
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setLoop(LoopEnum reverse) {
		end = reverse;
	}

	public double getSpeed() {
		return speed;
	}

	public double setSpeed(double speed) {
		this.speed = speed;
		return speed;
	}

	public long getFrame() {
		return frame;
	}

	public long setFrame(long frame) {
		this.frame = frame;
		return frame;
	}

}
