package com.monitor.core;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class MonitorFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5770425788422868323L;

	Image background;
	private ArrayList<BackGroundListener> baclis = new ArrayList<>(5);
	private int x, y;

	/**
	 * Constructs a new frame that is initially invisible.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @exception HeadlessException
	 *                if GraphicsEnvironment.isHeadless() returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see Component#setSize
	 * @see Component#setVisible
	 * @see JComponent#getDefaultLocale
	 */
	public MonitorFrame() throws HeadlessException {
		super();
		init();
	}

	/**
	 * Creates a <code>Frame</code> in the specified
	 * <code>GraphicsConfiguration</code> of a screen device and a blank title.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param gc
	 *            the <code>GraphicsConfiguration</code> that is used to
	 *            construct the new <code>Frame</code>; if <code>gc</code> is
	 *            <code>null</code>, the system default
	 *            <code>GraphicsConfiguration</code> is assumed
	 * @exception IllegalArgumentException
	 *                if <code>gc</code> is not from a screen device. This
	 *                exception is always thrown when
	 *                GraphicsEnvironment.isHeadless() returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 * @since 1.3
	 */
	public MonitorFrame(GraphicsConfiguration gc) {
		super(gc);
		init();
	}

	/**
	 * Creates a new, initially invisible <code>Frame</code> with the specified
	 * title.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param title
	 *            the title for the frame
	 * @exception HeadlessException
	 *                if GraphicsEnvironment.isHeadless() returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see Component#setSize
	 * @see Component#setVisible
	 * @see JComponent#getDefaultLocale
	 */
	public MonitorFrame(String title) throws HeadlessException {
		super(title);
		init();
	}

	public void init() {
		x = 0;
		y = 0;
	}

	public void setBackground(Image background) {
		Image old = this.background;
		this.background = background;
		for (BackGroundListener lis : baclis) {
			lis.backGroundChanged(old, background);
		}
	}

	private void changeX(int dx) {
		int tempx = x + dx;
		if (tempx > 0)
			tempx = 0;
		if (tempx + PrintScreen.screenWidth < getWidth())
			tempx = getWidth() - PrintScreen.screenWidth;
		x = tempx;
	}

	private void changeY(int dy) {
		int tempy = y + dy;
		if (tempy > 0)
			tempy = 0;
		if (tempy + PrintScreen.screenHeight < getHeight())
			tempy = getWidth() - PrintScreen.screenHeight;
		y = tempy;
	}

	public void changeBackgroundLocation(int dx, int dy) {
		Location old = new Location(x, y);
		changeX(dx);
		changeY(dy);
		for (BackGroundListener lis : baclis) {
			lis.backGroundLocationChanged(old, new Location(x, y));
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(background, x, y, null);
	}

	public void addBackGroundListener(BackGroundListener lis) {
		baclis.add(lis);
	}

}
