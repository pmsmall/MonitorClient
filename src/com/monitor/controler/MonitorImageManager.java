package com.monitor.controler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javafx.util.Pair;

public class MonitorImageManager {

	private BufferedImage image;
	private float scale = 1;
	public static final int DEFAULT_WIDTH;
	public static final int DEFAULT_HEIGHT;;
	private boolean isStart;
	private static MonitorState state = MonitorState.normal;
	private static Object listeningLock = new Object();
	private static Object listenedLock = new Object();

	/**
	 * 阻塞等待监听准备完毕
	 */
	public static void wairForListening() {
		synchronized (listeningLock) {
			if (state != MonitorState.listening)
				try {
					listeningLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 监听准备完毕，通知所有等待线程
	 */
	public static void setListeningState() {
		synchronized (listeningLock) {
			state = MonitorState.listening;
			listeningLock.notifyAll();
		}
	}

	/**
	 * 阻塞等待被监听准备完毕
	 */
	public static void wairForListened() {
		synchronized (listenedLock) {
			if (state != MonitorState.listened)
				try {
					listenedLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 被监听准备完毕，通知所有等待线程
	 */
	public static void setListenedState() {
		synchronized (listenedLock) {
			state = MonitorState.listened;
			listenedLock.notifyAll();
		}
	}

	public static enum MonitorState {
		normal, listening, listened
	}

	private static MonitorImageManager manager;

	static {
		manager = new MonitorImageManager();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		DEFAULT_HEIGHT = size.height;
		DEFAULT_WIDTH = size.width;
	}

	private MonitorImageManager() {
		image = null;
		isStart = false;
	}

	/**
	 * 添加画面，并且唤醒所有等待新画面的线程
	 * 
	 * @param image
	 *            新画面
	 * @param scale
	 *            缩放比
	 */
	private void addImage(BufferedImage image, float scale) {
		synchronized (this) {
			this.image = image;
			this.scale = scale;
			if (image != null) {
				isStart = true;
				notifyAll();
			} else {
				isStart = false;
			}
		}

	}

	/**
	 * 添加画面，不会唤醒所有等待新画面的线程
	 * 
	 * @param image
	 *            新画面
	 * @param scale
	 *            缩放比
	 */
	private void privateSetImage(Image image, float scale) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		if (this.image == null) {
			this.image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		}
		Graphics g = this.image.getGraphics();
		g.drawImage(image, 0, 0, null);
	}

	/**
	 * 得到画面和放缩比，返回值不会为空
	 * 
	 * @return 画面好缩放比，没有画面就会返回一个默认的
	 */
	private Pair<BufferedImage, Float> createImage() {
		if (image == null) {
			BufferedImage buffer = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics g = buffer.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
			image = buffer;
		}
		return new Pair<BufferedImage, Float>(image, scale);
	}

	public static void setImage(BufferedImage image, float scale) {
		manager.addImage(image, scale);
	}

	public static Pair<BufferedImage, Float> getImage() {
		return manager.createImage();
	}

	public static Pair<BufferedImage, Float> getCurrentImage() {
		return manager.image();
	}

	public static void setImage(Image image, float scale) {
		manager.privateSetImage(image, scale);
	}

	/**
	 * 获取画面，如果没有就会阻塞等待
	 * 
	 * @return 新画面的缩放比
	 */
	public Pair<BufferedImage, Float> image() {
		synchronized (this) {
			if (isStart == false)
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			return new Pair<BufferedImage, Float>(image, scale);
		}
	}
}
