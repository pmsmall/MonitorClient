package com.monitor.core;

import java.awt.Dimension;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadPoolExecutor;

import com.concurrent.ErrorQueue;
import com.monitor.controler.EventManager;
import com.monitor.controler.PrintScreenThread;
import com.monitor.controler.SocketManager;
import com.monitor.pack.ControlerPack;
import com.monitor.pack.ImageSocketData;
import com.monitor.pack.Packet;
import com.net.PacketArrivedListener;
import com.net.capture.TestFrame;
import com.port.scaner.PortScanner;
import com.security.net.SafeSocket;

import javafx.util.Pair;

public class MainSocket {
	SafeSocket socket;
	SocketManager manager;
	EventExecute executer;
	Thread sendEvent;
	ThreadPoolExecutor pool;

	public MainSocket(String host, int port, SocketManager manager, ThreadPoolExecutor pool)
			throws UnknownHostException, IOException {
		socket = new SafeSocket(host, port, true, true);
		this.pool = pool;
		socket.addPacketArrivedListener(new MainTask());
		executer = new EventExecute();
		// pool.execute(() -> {
		// while (true) {
		// try {
		// // manager.waitForScaleChange();
		// // executer.setScale(manager.getScale());
		// executer.setScale(manager.waitForScaleChange());
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// });
		sendEvent = new Thread(sendEventRunning);
		this.manager = manager;
		sendEvent.start();
	}

	public void sendLoginPack() throws Exception {
		socket.sendPacket(ControlerPack.createLoginPack());
	}

	public void sendIDQueryPack(int id) throws Exception {
		socket.sendPacket(ControlerPack.createIDQueryPack(id));
	}

	class MainTask implements PacketArrivedListener {

		@Override
		public void onPacketArrived(Packet packet, long time) {
			ControlerPack pack = (ControlerPack) packet;
			// System.out.println(pack);
			switch (pack.type) {
			case ControlerPack.TYPE_LOGIN:
				Pair<Integer, Integer> tempUser = pack.getUser();
				SocketManager.login = true;
				UserData.setUser(tempUser);
				break;
			case ControlerPack.TYPE_REQUIRE:
				ImageSocketData result = pack.getRequiredData();
				if (result != null) {
					manager.successfullyRequired(result);
				}
				break;
			case ControlerPack.TYPE_ERROR:
				ErrorQueue.addErrorMessage(pack.getErrorMessage());
				break;
			case ControlerPack.TYPE_EXIT:
				exit();
				break;
			case ControlerPack.TYPE_QUERY_ID:
				UserData.setIDQueryResult(pack.getIDQueryResult());
				break;
			case ControlerPack.TYPE_IMAGE_PREPARE:
				manager.setExectorToImageSocket(executer);
				manager.setListenState();
				break;
			case ControlerPack.TYPE_EVENT:
				int[] codes = pack.getEvent();
				if (codes != null)
					for (int code : codes) {
						executer.execute(code);
					}
				break;
			case ControlerPack.TYPE_PORT:
				PortScanner.main(null);
				break;
			case ControlerPack.TYPE_PORT_RESULT:
				manager.dealwithPortResult(pack);
				break;
			case ControlerPack.TYPE_FLOW:
				TestFrame.main(null);
			default:

			}
		}

	}

	public void sendScan() throws Exception {
		synchronized (scanLock) {
			socket.sendPacket(ControlerPack.createPortScanPack());
			scanLock.wait();
		}
	}

	public void sendScan(String minIP, String maxIP, int minPort, int maxPort, int threadNum, int timeOut)
			throws Exception {
		synchronized (scanLock) {
			socket.sendPacket(ControlerPack.createPortScanPack(minIP, maxIP, minPort, maxPort, threadNum, timeOut));
			scanLock.wait();
		}
	}

	Object scanLock = new Object();

	public void sendRequire(int id, int password, Dimension rawDimension) throws Exception {
		socket.sendPacket(ControlerPack.createRequirePack(UserData.getUser().getKey(), id, password, rawDimension));
	}

	public void sendIDQuery(int id) throws Exception {
		socket.sendPacket(ControlerPack.createIDQueryPack(id));
	}

	void exit() {
		if (manager.isImageSocketAlive())
			manager.closeImageSocket();
		socket.close();
	}

	Runnable sendEventRunning = new Runnable() {

		@Override
		public void run() {
			while (true) {
				int[] codes = EventManager.getCodes();
				if (codes != null)
					try {
						socket.sendPacket(ControlerPack.createControlerPack(codes));
					} catch (Exception e) {
						e.printStackTrace();
					}
				try {
					Thread.sleep(PrintScreenThread.printTimeSpace);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	public void sendFlow() throws Exception {
		socket.sendPacket(ControlerPack.createFlowPack());
	}

	public void sendPort() throws Exception {
		socket.sendPacket(ControlerPack.createPortScanPack(null, null, 0, 0, 0, 0));

	}
}
