package com.monitor.controler;

import java.awt.AWTException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import com.monitor.core.LoginEven;
import com.monitor.core.LoginListener;
import com.monitor.view.Login;
import com.monitor.view.MonitorView;

import javafx.util.Pair;

public class MainThread {
	SocketManager socketManager;
	Login login;
	UpdateMonitorView updateMonitor;
	PrintScreenThread printThread;
	Thread checkForListenedThread;
	LinkedBlockingQueue<Runnable> tasks;
	ThreadPoolExecutor pool;
	private boolean[] successfully_link = { false };

	public MainThread() throws UnknownHostException, IOException {
		tasks = new LinkedBlockingQueue<>();
		pool = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 1000, TimeUnit.MILLISECONDS, tasks);

		login = new Login();
		addListener();

	}

	public void waitForSuccessfullyLink() throws InterruptedException {
		synchronized (successfully_link) {
			if (successfully_link[0])
				successfully_link.wait();
		}
	}

	public void run() {
		login.setVisable(true);
		pool.execute(() -> {
			checkForListenedThread = new Thread(checkForListened);
			String host;
			try {
				// host = "115.159.70.199";
				// host = InetAddress.getLocalHost().getHostAddress();
				host = "172.16.0.122";
				socketManager = new SocketManager(host, 8889, pool);
				checkForListenedThread.start();
				synchronized (successfully_link) {
					successfully_link[0] = true;
					successfully_link.notifyAll();
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(login.getFrame(), "连接失败", "error", JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
		});
	}

	public void start() {
		run();
	}

	private void startMonitoring(String title) {
		login.setVisable(false);
		if (updateMonitor == null) {

			// TODO to add computer name
			updateMonitor = new UpdateMonitorView(title, socketManager);

			updateMonitor.start();
		}
	}

	public void addListener() {
		login.addLoginListener(new LoginListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void loginEvenOccer(LoginEven e) {
				switch (e.type) {
				case "queryID":
					try {
						socketManager.sendIDQuery((int) e.content);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					break;
				// socketManager.
				case "require":
					try {
						socketManager.sendRequire((Pair<Integer, Integer>) e.content, MonitorView.rawDimension);
						MonitorImageManager.wairForListening();
						e.sourceRootContainer.setVisible(false);
						startMonitoring(socketManager.getHostName());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					break;

				default:
					break;
				}
			}
		});
	}

	public static void main(String[] args) {
		try {
			new MainThread().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Runnable checkForListened = new Runnable() {

		@Override
		public void run() {
			MonitorImageManager.wairForListened();
			try {
				login.setVisable(false);
				if (printThread == null)

					printThread = new PrintScreenThread();

				printThread.start();
				socketManager.startSendImagePack();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	};
}
