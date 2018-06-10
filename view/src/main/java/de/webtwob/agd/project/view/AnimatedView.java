package de.webtwob.agd.project.view;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.lang.Thread.State;

import javax.swing.JComponent;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.util.GraphMapping;
import de.webtwob.agd.project.api.util.ViewUtil;

public class AnimatedView extends JComponent {

	/**
	 * generated serialVarsionUID
	 */
	private static final long serialVersionUID = -6226316608311632721L;

	//variables determining the position and size of animation
	private volatile double scale = 1;
	private volatile Point2D.Double origin = new Point2D.Double(0, 0);
	
	private volatile IAnimation animation;
	
	private AnimationSyncThread frameSync = new AnimationSyncThread();

	public AnimatedView(AnimationSyncThread syncThread) {
		setDoubleBuffered(true);
		setBackground(Color.WHITE);
		
		frameSync = syncThread;
		
		frameSync.addFrameChangeCallback(()->paintImmediately(0, 0, getWidth(), getHeight()));
		
		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		
		MouseAdapter adapter = new MouseAdapter() {

			Point mouseClick = new Point();
			Point2D.Double oldOrigin = new Point2D.Double();
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				/*
				 * TODO this should not scale linearly
				 * for smaller scale it should increase/decrease by smaller amounts
				 * */
				scale = scale + scale*e.getUnitsToScroll()/32;
				if(scale<1) {
					scale = 1;
				}
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				mouseClick.setLocation(e.getPoint());
				oldOrigin.setLocation(origin);
				repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				origin = new Point2D.Double(oldOrigin.getX()+e.getX()-mouseClick.getX(), oldOrigin.getY()+e.getY()-mouseClick.getY());
				repaint();
			}
			
		};
		
		addMouseWheelListener(adapter);
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}
	
	public AnimatedView() {
		this(new AnimationSyncThread());
		if(frameSync.getState() == State.NEW) {
			frameSync.start();
		}
	}

	@SuppressWarnings("exports") // automatic modules should not be exported
	public void setGraph(ElkNode eg) {
		setAnimation(new Animation(eg,ViewUtil.createMapping(eg, eg), 2));
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
		setAnimation(new Animation(graph, mapping, length));
	}
	
	public void setAnimation(IAnimation animation) {
		this.animation = animation;
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
		var subScale = Math.min(getWidth()/animation.getWidth(),getHeight()/animation.getHeight());
		graphic.scale(subScale,subScale);
		graphic.scale(scale, scale);

		animation.generateFrame(frameSync.getFrame(), graphic);

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

	

}
