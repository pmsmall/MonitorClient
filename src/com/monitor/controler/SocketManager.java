package com.monitor.controler;

import java.awt.Dimension;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

import com.monitor.core.EventExecute;
import com.monitor.core.ImageSocket;
import com.monitor.core.MainSocket;
import com.monitor.core.UserData;
import com.monitor.pack.ControlerPack;
import com.monitor.pack.ImageSocketData;

import javafx.util.Pair;

public class SocketManager {
	MainSocket main;
	ImageSocket image;
	public static volatile boolean login = false;
	String host;
	ThreadPoolExecutor pool;
	Object imageLock = new Object();
	ScanManager scanManager;

	public SocketManager(String host, int port, ThreadPoolExecutor pool) throws UnknownHostException, IOException {
		main = new MainSocket(host, port, this, pool);
		this.host = host;
		this.pool = pool;
		new Thread(loginThread).start();
	}

	public void successfullyRequired(ImageSocketData data) {
		try {
			image = new ImageSocket(host, UserData.getUser().getKey(), data, pool);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Runnable loginThread = new Runnable() {

		@Override
		public void run() {
			while (!login) {
				try {
					main.sendLoginPack();
					if (!login)
						Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	public boolean isImageSocketAlive() {
		return image.isWorking();
	}

	public void closeImageSocket() {
		image.exit();
	}

	// TODO add ending
	public void dealwithPortResult(ControlerPack pack) {
		try {
			scanManager.write((HashMap<String, ArrayList<Integer>>) pack.getPortsInfomation());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendRequire(Pair<Integer, Integer> user, Dimension rawDimension) throws Exception {
		main.sendRequire(user.getKey(), user.getValue(), rawDimension);
	}

	public void sendRequire(int id, int password, Dimension rawDimension) throws Exception {
		main.sendRequire(id, password, rawDimension);
	}

	public void sendIDQuery(int id) throws Exception {
		main.sendIDQuery(id);
	}

	public void setListenState() {
		if (image.isFromHere)
			MonitorImageManager.setListeningState();
		else
			MonitorImageManager.setListenedState();
	}

	public void startSendImagePack() {
		image.startSendImagePack();
	}

	public float waitForScaleChange() throws InterruptedException {
		synchronized (imageLock) {
			if (image == null)
				imageLock.wait();
		}
		if (image != null) {
			return image.waitForScaleChanged();
		}
		return 1;
	}

	public float getScale() {
		if (image != null)
			return image.getScale();
		return 1;
	}

	public boolean sendScan() {
		try {
			main.sendScan();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendScan(String minIP, String maxIP, int minPort, int maxPort, int threadNum, int timeOut) {
		try {
			main.sendScan(minIP, maxIP, minPort, maxPort, threadNum, timeOut);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getHostName() {
		return image.getHostName();
	}

	public void setExectorToImageSocket(EventExecute e) {
		if (image != null)
			image.setExecutor(e);
	}

	public void requireFile() throws Exception {
		image.requireFile();
	}

	public void sendFile() {
		image.sendFile();
	}

	public void setScanManager(ScanManager manager) {
		this.scanManager = manager;
	}

	public void sendFlow() {
		try {
			main.sendFlow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendPort() {
		try {
			main.sendPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
