package com.port.scaner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 完成Tcp connect 扫描------>ScanTcpConnect类
 * 
 * @author Frank
 *
 */
public class ScanTcpConnect {
	int time;
	String str = "";
	InetAddress hostAddress;
	BufferedWriter out;
	ThreadPoolExecutor pool;
	LinkedBlockingQueue<Runnable> workQueue;
	private volatile int count = 0;

	// 构造函数
	ScanTcpConnect() {
		this(Integer.MAX_VALUE);
	}

	// 构造函数
	ScanTcpConnect(int capacity) { // 传递参数
		workQueue = new LinkedBlockingQueue<>(capacity);
		pool = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 2000, TimeUnit.MILLISECONDS, workQueue);
	}

	public void run(String host, int minPort, int maxPort, BufferedWriter out) {
		// 扫描指定端口
		synchronized (startLock) {
			if (!istart) {
				startLock.notifyAll();
				istart = true;
			}
		}
		for (int i = minPort; i < maxPort; i++) {
			String tmp;
			tmp = "主机：" + host + " TCP端口：" + i + "\r\n";
			try {
				out.write(tmp);
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			try {
				out.write("正在扫描" + i + "端口\n");
				// 根据主机名和端口号创建套接字地址
				// InetSocketAddress是SocketAddress的实现子类
				SocketAddress sockaddr = new InetSocketAddress(hostAddress, i);
				Socket socket = new Socket();

				// 将创建的套接字连接到具有指定超时值得服务器
				socket.connect(sockaddr, time);
				tmp = "主机：" + host + " TCP端口号为:" + i + "  ---------> 端口开放" + "\r\n";
				out.write(tmp);
				socket.close();
				updateStr(tmp);
			} catch (ConnectException e) { // 因端口关闭而失败的情况
				tmp = "主机：" + host + " TCP端口号为:" + i + " --------->  端口关闭" + "\r\n";
				try {
					out.write(tmp);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateStr(tmp);
			} catch (SocketTimeoutException e) { // 因请求超时而失败的情况
				tmp = "主机：" + host + " TCP端口号为:" + i + " --------->  请求超时" + "\r\n";
				try {
					out.write(tmp);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				updateStr(tmp);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				count--;
			}
			try {
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void run(String host, int minPort, int maxPort, OutputStream out) {
		// 扫描指定端口
		for (int i = minPort; i <= maxPort; i++) {

			try {
				// 根据主机名和端口号创建套接字地址
				// InetSocketAddress是SocketAddress的实现子类
				SocketAddress sockaddr = new InetSocketAddress(hostAddress, i);
				Socket socket = new Socket();

				// 将创建的套接字连接到具有指定超时值得服务器
				socket.connect(sockaddr, time);
				out.write(getBytes(i, 1));
				socket.close();
			} catch (ConnectException e) { // 因端口关闭而失败的情况
				try {
					out.write(getBytes(i, 2));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (SocketTimeoutException e) { // 因请求超时而失败的情况
				try {
					out.write(getBytes(i, 3));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			try {
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateStr(String s) {
		this.str += s;
	}

	private byte[] getBytes(int port, int state) {
		byte[] b = new byte[3];
		b[0] = (byte) (port >> 8);
		b[1] = (byte) (port & 0xff);
		b[2] = (byte) (state & 0xff);
		return b;
	}

	public String getAllOutputString() {
		return str;
	}

	public void clearOutputString() {
		str = "";
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setHostAddr(InetAddress host) {
		this.hostAddress = host;
	}

	boolean istart = false;
	Object startLock = new Object();

	public boolean start(String host, int minPort, int maxPort, Writer out) {
		return start(host, minPort, maxPort, new BufferedWriter(out));
	}

	public boolean start(String host, int minPort, int maxPort, OutputStream out) {
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			pool.execute(() -> {
				run(host, minPort, maxPort, out);
			});
			return true;
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean start(String host, int minPort, int maxPort, BufferedWriter out) {
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			pool.execute(() -> {
				run(host, minPort, maxPort, out);
			});
			return true;
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		synchronized (startLock) {
			if (!istart) {
				startLock.wait();
			}
		}
		while (true) {
			if (pool.getQueue().isEmpty())
				break;
			else
				Thread.sleep(200);
			System.out.println(pool.getTaskCount());
		}
	}

	public boolean isTerminated() {
		return pool.isTerminated();
	}

	public void startCount(int count) {
		this.count = count;
	}

	public void waitForCountZero() throws InterruptedException {
		while (count > 0) {
			Thread.sleep(200);
		}
	}
}
