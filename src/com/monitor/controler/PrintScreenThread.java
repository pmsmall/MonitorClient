package com.monitor.controler;

import java.awt.AWTException;

import com.monitor.core.PrintScreen;

public class PrintScreenThread implements Runnable {

	Thread thread;
	PrintScreen printScreen;
	public static final int printTimeSpace = 50;

	public PrintScreenThread() throws AWTException {
		printScreen = new PrintScreen();
	}

	@Override
	public void run() {
		while (true) {
			MonitorImageManager.setImage(printScreen.getFullScreenShot(), 1f);
			try {
				Thread.sleep(printTimeSpace);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		if (thread == null || thread.getState().equals(Thread.State.TERMINATED))
			thread = new Thread(this);
		if (thread.getState().equals(Thread.State.NEW))
			thread.start();
	}

	public boolean isAlive() {
		return thread.isAlive();
	}
}
