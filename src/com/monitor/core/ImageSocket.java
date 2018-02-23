package com.monitor.core;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JFileChooser;


import com.monitor.controler.MonitorImageManager;
import com.monitor.controler.PrintScreenThread;
import com.monitor.pack.ImagePack;
import com.monitor.pack.ImageSocketData;
import com.monitor.pack.PackFileManager;
import com.monitor.pack.Packet;
import com.net.PacketArrivedListener;
import com.net.capture.util.FileHelper;
import com.security.net.SafeSocket;

import javafx.util.Pair;

public class ImageSocket {
	SafeSocket socket;
	int security;
	String key;
	int from;
	int to;
	int id;
	String hostName;
	Thread thread;
	public final boolean isFromHere;
	// private float scale = 1;
	private Dimension rawDimension;
	private Object rawDimensionLock = new Object();
	private boolean needReculculateScale = true;
	private boolean needScale = true;
	PackFileManager fileManager;
	ThreadPoolExecutor pool;

	public ImageSocket(String host, int port, int from, int to, int id, int security, String key,
			Dimension rawDimension, String hostName, ThreadPoolExecutor pool) throws UnknownHostException, IOException {
		socket = new SafeSocket(host, port, false, false);
		this.pool = pool;
		socket.addPacketArrivedListener(new ImageTask());
		this.from = from;
		this.to = to;
		this.id = id;
		this.security = security;
		this.key = key;
		this.rawDimension = rawDimension;
		this.hostName = hostName;
		isFromHere = from == id;
		fileManager = new PackFileManager();
		login();
	}

	public ImageSocket(String host, int id, ImageSocketData data, ThreadPoolExecutor pool)
			throws UnknownHostException, IOException {
		this(host, data.port, data.from, data.to, id, data.security, data.key, data.rawDimension, data.hostName, pool);
	}

	private void login() {
		try {
			System.out.println("login");
			socket.sendPacket(ImagePack.createLoginPack(from, to, id, security, key));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startSendImagePack() {
		thread = new Thread(sendImage);
		thread.start();
	}

	class ImageTask implements PacketArrivedListener {

		@Override
		public void onPacketArrived(Packet packet, long time) {
			ImagePack pack = (ImagePack) packet;
			switch (pack.type) {
			case ImagePack.TYPE_IMAGE:
				try {
					MonitorImageManager.setImage(pack.getImage(), scale);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				break;
			case ImagePack.TYPE_EXIT:
				MonitorImageManager.setImage(null, scale);
				exit();
				break;
			case ImagePack.TYPE_FILE:

				File file;
				System.out.println("file Receive");

				do {
					file = FileHelper.getFile("请将文件" + pack.getFileName() + "保存到本地", FileHelper.SAVE);
				} while (file == null);
				try {
					fileManager.addNewImagePack(pack, file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case ImagePack.TYPE_FILE_EXTERN:
				System.out.println("file Receive");
				try {
					fileManager.addImagePack(pack);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case ImagePack.TYPE_REQUIRE_FILE:
				sendFile();
				break;
			default:
			}
		}

	}

	public void exit() {
		socket.close();
	}

	public boolean isWorking() {
		return socket.isConnected() && !socket.isClosed();
	}

	public void updateDimension(Dimension dimension) {
		synchronized (rawDimensionLock) {
			if (rawDimension != null && rawDimension.equals(dimension))
				return;
			this.rawDimension = dimension;
			needReculculateScale = true;
		}
	}

	private Object scaleLock = new Object();

	public float waitForScaleChanged() throws InterruptedException {
		synchronized (scaleLock) {
			if (!scaleChanged) {
				scaleLock.wait();
			}
			scaleChanged = false;
			return scale;
		}
	}

	public boolean scaleChanged = false;

	public float getScale() {
		// System.out.println("12333：" + scale);
		scaleChanged = false;
		return scale;
	}

	private EventExecute executor;

	public void setExecutor(EventExecute e) {
		executor = e;
	}

	volatile float scale = 1;

	private void setScale(float scale) {
		synchronized (scaleLock) {
			// System.out.println("12333:" + scale);
			if (scale != ImageSocket.this.scale) {
				ImageSocket.this.scale = scale;
				if (executor != null)
					executor.setScale(scale);
				scaleChanged = true;
				scaleLock.notifyAll();
			}
		}

	}

	Runnable sendImage = new Runnable() {

		@Override
		public void run() {
			setScale(1);
			while (true) {
				try {
					Pair<BufferedImage, Float> data = MonitorImageManager.getCurrentImage();
					float scale = 1;
					if (needScale) {
						if (needReculculateScale) {
							setScale((float) (rawDimension.getWidth() / data.getKey().getWidth()));
							needReculculateScale = false;
						}
						scale = ImageSocket.this.scale;
					}
					pool.execute(new SendRunnable(data, scale));

					Thread.sleep(PrintScreenThread.printTimeSpace);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		class SendRunnable implements Runnable {
			volatile float scale;
			Pair<BufferedImage, Float> data;

			public SendRunnable(Pair<BufferedImage, Float> data, float scale) {
				this.data = data;
				this.scale = scale;
			}

			@Override
			public void run() {
				// long time1 = System.currentTimeMillis();
				ImagePack pack;
				try {
					pack = new ImagePack(from, to, data.getKey(), ImagePack.IMAGE_QUICK, scale);
					// long time2 = System.currentTimeMillis();
					// System.out.println((time2 - time1) / 1000.0);
					socket.sendPacket(pack, PrintScreenThread.printTimeSpace + 10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

	};

	public String getHostName() {
		return hostName;
	}

	public void requireFile() throws Exception {
		socket.sendPacket(ImagePack.createFileRequirePack(), 3000);
	}

	public void sendFile() {
		File file = FileHelper.getFile("请选择要发送的文件", JFileChooser.OPEN_DIALOG);
		if (file == null || !file.exists()) {

		} else {
			try {
				FileInputStream in = new FileInputStream(file);
				int l;
				byte[] data = new byte[1024 * 1024];
				int offset = 0;
				ImagePack pack = null;
				while ((l = in.available()) > 0) {
					l = in.read(data);
					byte[] tmp = new byte[l];
					System.arraycopy(data, 0, tmp, 0, l);

					l = in.available();
					if (offset == 0) {
						if (l > 0) {
							pack = ImagePack.createFilePack(file.getName(), tmp);
						} else {
							pack = ImagePack.createSingleFilePack(file.getName(), tmp);
						}
					} else {
						if (l > 0)
							pack = ImagePack.createExternFilePack(pack, tmp);
						else
							pack = ImagePack.createEndFilePack(pack, tmp);
					}
					socket.sendPacket(pack, 3000);
					offset++;
				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
