package com.monitor.controler;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import com.monitor.listener.MonitorViewListener;
import com.monitor.view.MonitorView;

import javafx.util.Pair;

public class UpdateMonitorView {
	MonitorView mview;
	Thread updateThread;
	Thread freshThread;
	SocketManager socketManager;

	public UpdateMonitorView(String title, SocketManager socketManager) {
		mview = new MonitorView(title);
		addActionListener();
		MonitorViewListener listener = new MonitorViewListener(mview);
		mview.addListener(listener);
		updateThread = new Thread(updateImage);
		freshThread = new Thread(repanitThread);
		this.socketManager = socketManager;
	}

	public void start() {
		mview.setVisible(true);
		updateThread.start();
		freshThread.start();
	}

	Runnable updateImage = new Runnable() {

		@Override
		public void run() {
			MonitorImageManager.wairForListening();
			while (true) {
				Pair<BufferedImage, Float> data = MonitorImageManager.getImage();
				Image image = data.getKey();
				mview.setImage(image, data.getValue());
				try {
					Thread.sleep(PrintScreenThread.printTimeSpace);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	Runnable repanitThread = new Runnable() {

		@Override
		public void run() {
			while (true) {
				mview.repaint();
				try {
					Thread.sleep(PrintScreenThread.printTimeSpace);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	public void addActionListener() {
		mview.addUploadActionListener(l -> {
			System.out.println("upload");
			uploadActionPerformed(l);
		});

		mview.addDownloadActionListener(l -> {
			System.out.println("download");
			downloadActionPerformed(l);
		});
		mview.addScanActionListener(l -> {
			System.out.println("scan");
			scanActionPerformed(l);
		});
		mview.addOverflowActionListener(l -> {
			System.out.println("overflow");
			overflowActionPerformed(l);
		});

	}

	public void uploadActionPerformed(ActionEvent e) {
		socketManager.sendFile();
	}

	public void downloadActionPerformed(ActionEvent e) {
		try {
			socketManager.requireFile();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void scanActionPerformed(ActionEvent e) {
		socketManager.sendScan();
	}

	public void overflowActionPerformed(ActionEvent e) {
		socketManager.sendFlow();
	}
}
