package com.monitor.core;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.mine.MyUI.MyTransactionUI;

/**
 * This class including the method of translating the InputEvent of mouses and
 * keyboards into an integer command code, as well as, another method executing
 * the specific command code.
 * 
 * @author Frank
 *
 */
public class EventExecute {
	private static Robot robot;
	/**
	 * 0001 xxxx xxxx xxxx
	 */
	public static final int MOUSE_TYPE = 0x10000000;
	/**
	 * 0001 1100 xxxx xxxx
	 */
	public static final int MOUSE_TYPE_CLICKED = 0x1c000000;
	/**
	 * 0001 1000 xxxx xxxx
	 */
	public static final int MOUSE_TYPE_PRESSED = 0x18000000;
	/**
	 * 0001 0100 xxxx xxxx
	 */
	public static final int MOUSE_TYPE_RELEASED = 0x14000000;
	/**
	 * 0001 0010 xxxx xxxx
	 */
	public static final int MOUSE_TYPE_MOVED = 0x12000000;
	/**
	 * 0001 0001 xxxx xxxx
	 */
	public static final int MOUSE_TYPE_WHEEL = 0x11000000;

	/**
	 * 0010 xxxx xxxx xxxx
	 */
	public static final int KEY_TYPE = 0x20000000;

	/**
	 * 0010 1000 xxxx xxxx
	 */
	public static final int KEY_TYPE_PRESSED = 0x28000000;

	/**
	 * 0010 0100 xxxx xxxx
	 */
	public static final int KEY_TYPE_RELEASED = 0x24000000;

	/**
	 * 0010 1100 xxxx xxxx
	 */
	public static final int KEY_TYPE_TYPED = 0x2c000000;

	/**
	 * An array of extended modifiers for additional buttons.
	 * 
	 * @see getButtonDownMasks There are twenty buttons fit into 4byte space.
	 *      one more bit is reserved for FIRST_HIGH_BIT.
	 * @since 7.0
	 */
	private static final int[] BUTTON_DOWN_MASK = new int[] {
			/*
			 * 4th phisical button (this is not a wheel!)
			 */
			InputEvent.BUTTON1_DOWN_MASK, InputEvent.BUTTON2_DOWN_MASK, InputEvent.BUTTON3_DOWN_MASK, 1 << 14,

			/*
			 * (this is not a wheel!)
			 */
			1 << 15, 1 << 16, 1 << 17, 1 << 18, 1 << 19, 1 << 20, 1 << 21, 1 << 22, 1 << 23, 1 << 24, 1 << 25, 1 << 26,
			1 << 27, 1 << 28, 1 << 29, 1 << 30 };

	static {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private volatile float scale = 1;

	public EventExecute() {
	}

	/**
	 * Execute the command code, which is encoded by especial rules, including
	 * the event of mouses of keyboards need to be executed.
	 * 
	 * @param code
	 *            the command code
	 */
	public void execute(int code) {
		int mainType = code & 0xf0000000;
		if (mainType == MOUSE_TYPE) {
			int type = code & 0xff000000;
			switch (type) {
			case MOUSE_TYPE_CLICKED:
				click(BUTTON_DOWN_MASK[((code >> 16) & 0xff) - 1], (code >> 8) & 0xff);
				break;
			case MOUSE_TYPE_PRESSED:
				robot.mousePress(BUTTON_DOWN_MASK[((code >> 16) & 0xff) - 1]);
				break;
			case MOUSE_TYPE_RELEASED:
				robot.mouseRelease(BUTTON_DOWN_MASK[((code >> 16) & 0xff) - 1]);
				break;
			case MOUSE_TYPE_MOVED:
				Point p = getPoint(code);
				// System.out.println(scale);
				robot.mouseMove((int) (p.getX() / scale), (int) (p.getY() / scale));
				break;
			case MOUSE_TYPE_WHEEL:
				robot.mouseWheel(getWheelAmt(code));
				break;
			}
		} else if (mainType == KEY_TYPE) {
			int type = code & 0xff000000;
			switch (type) {
			case KEY_TYPE_TYPED:
				keyType(type & 0x00ffffff);
				break;
			case KEY_TYPE_PRESSED:
				robot.keyPress(type & 0x00ffffff);
				break;
			case KEY_TYPE_RELEASED:
				robot.keyRelease(type & 0x00ffffff);
				break;
			}
		}
	}

	/**
	 * According to the InputEvent e, compile the event to an integer command
	 * code. To deal with the events of mouses, {@link #compile(MouseEvent)}
	 * will be used; as for then events of keyboard, {@link #compile(KeyEvent)}
	 * will be called.
	 * 
	 * @param e
	 *            the InputEvent which need to be compiled
	 * @return the command code depends on the specific event
	 */
	public int compile(InputEvent e) {
		int code = 0;
		if (e instanceof MouseEvent)
			return compile((MouseEvent) e);
		else if (e instanceof KeyEvent)
			return compile((KeyEvent) e);
		return code;
	}

	/**
	 * According to the MouseEvent e, compile the event to an integer command
	 * code.
	 * 
	 * @param e
	 *            the MouseEvent which need to be compiled
	 * @return the command code depends on the specific event
	 */
	public int compile(MouseEvent e) {
		int code = 0;
		int buttonMask = e.getButton();
		switch (e.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			code |= MOUSE_TYPE_CLICKED;
			code |= (e.getClickCount() & 0xff) << 16;
			code |= (buttonMask & 0xff) << 8;
			break;
		case MouseEvent.MOUSE_PRESSED:
			code |= MOUSE_TYPE_PRESSED;
			code |= (buttonMask & 0xff) << 16;
			break;
		case MouseEvent.MOUSE_RELEASED:
			code |= MOUSE_TYPE_RELEASED;
			code |= (buttonMask & 0xff) << 16;
			break;
		case MouseEvent.MOUSE_MOVED: {
			Point p;
			if (e.getSource() instanceof MyTransactionUI) {
				MyTransactionUI myui = (MyTransactionUI) (e.getSource());
				p = myui.getRelativePoint(e.getPoint());
			} else {
				p = e.getLocationOnScreen();
			}
			code |= MOUSE_TYPE_MOVED;
			code |= getPointCode(p);
		}
			break;
		case MouseEvent.MOUSE_WHEEL:
			code |= MOUSE_TYPE_WHEEL;
			code |= getWheelCode((MouseWheelEvent) e);
			break;
		}
		return code;
	}

	/**
	 * According to the KeyEvent e, compile the event to an integer command
	 * code.
	 * 
	 * @param e
	 *            the KeyEvent which need to be compiled
	 * @return the command code depends on the specific event
	 */
	public int compile(KeyEvent e) {
		int code = 0;
		switch (e.getID()) {
		case KeyEvent.KEY_TYPED:
			code |= KEY_TYPE_TYPED;
			code |= e.getKeyCode();
			break;
		case KeyEvent.KEY_PRESSED:
			code |= KEY_TYPE_PRESSED;
			code |= e.getKeyCode();
			break;
		case KeyEvent.KEY_RELEASED:
			code |= KEY_TYPE_RELEASED;
			code |= e.getKeyCode();
			break;
		}
		return code;
	}

	public void click(int buttons) {
		robot.mousePress(buttons);
		robot.mouseRelease(buttons);
	}

	public void click(int buttons, int times) {
		for (int i = 0; i < times; i++) {
			click(buttons);
		}
	}

	public void keyPress(int code) {
		robot.keyPress(code);
	}

	public void keyRelease(int code) {
		robot.keyRelease(code);
	}

	public void keyType(int code) {
		robot.keyPress(code);
		robot.keyRelease(code);
	}

	int getPointCode(Point p) {
		int code = ((p.x & 0xfff) << 12) | ((p.y & 0xfff));
		return code;
	}

	Point getPoint(int code) {
		return new Point((code >> 12) & 0xfff, code & 0xfff);
	}

	int getWheelCode(MouseWheelEvent e) {
		int way = e.getScrollAmount();
		// 1 down; -1 up
		return e.getWheelRotation() > 0 ? (way & 0x7fff) : (way & 0xffff) | (0x8000);
	}

	int getWheelAmt(int code) {
		return (int) ((short) (code & 0xffff));
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
}
