package de.webtwob.agd.project.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.lang.Thread.State;
import java.util.List;

import javax.swing.JComponent;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.interfaces.IAnimation;

public class PseudocodeView extends JComponent {
	/**
	 * generated serialVarsionUID
	 */
	private static final long serialVersionUID = -6226316608311632721L;

	private List<String> codeLines;

	private AnimationSyncThread frameSync = new AnimationSyncThread();
	private IAnimation animation;

	public PseudocodeView(List<String> codeLines,AnimationSyncThread syncThread,IAnimation animation) {
		setDoubleBuffered(true);
		setBackground(Color.WHITE);
		this.codeLines = codeLines;
		this.setAnimation(animation);
		
		frameSync = syncThread;

		frameSync.addFrameChangeCallback(this::repaint);

	}

	public PseudocodeView(List<String> codeLines,IAnimation animation) {
		this(codeLines,new AnimationSyncThread(),animation);
		if (frameSync.getState() == State.NEW) {
			frameSync.start();
		}
	}
	
	

	@SuppressWarnings("exports") // automatic modules should not be exported
	public void setCode(List<String> codeLines) {
		this.codeLines = codeLines;
		repaint();
	}

	private static final String LINE_INDICATOR = "\u27a1 ";

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D graphic2d = (Graphics2D) g;
		
		
		int lineHeight = graphic2d.getFontMetrics().getHeight();
		var indicatorWidth = graphic2d.getFontMetrics().stringWidth(LINE_INDICATOR);
		var mapping = animation.getGraphStateForFrame(frameSync.getFrame());
		var currentLine = mapping.getPseudoCodeLine();
		var color = graphic2d.getColor();
		
		for(int i = 0;i<codeLines.size();i++) {
			if(i==currentLine) {
				graphic2d.setColor(Color.RED);
				graphic2d.drawString(LINE_INDICATOR, 0, lineHeight*(i+1));
				graphic2d.setColor(color);
			}
			graphic2d.drawString(String.format("%02d: %2s", i+1,codeLines.get(i)),indicatorWidth  , lineHeight*(i+1));
		}
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public void setAnimation(IAnimation animation) {
		this.animation = animation;
	}

}
