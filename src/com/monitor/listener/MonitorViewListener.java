package com.monitor.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.monitor.controler.EventManager;
import com.monitor.view.MonitorView;

public class MonitorViewListener implements MouseListener, MouseMotionListener, KeyListener {
	MonitorView mview;

	public MonitorViewListener(MonitorView mview) {
		this.mview = mview;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			EventManager.add(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		EventManager.add(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		EventManager.add(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		EventManager.add(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("123");
		EventManager.add(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		EventManager.add(e);
	}

}
