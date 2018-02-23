package com.monitor.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import com.monitor.pack.Packet;

public class Test {
	// static {
	// System.loadLibrary("E:\\jnetpcap-1.3.0-1.win64\\jnetpcap-1.3.0\\jnetpcap.dll");
	// }

	static String host;
	static {
		try {
			host = InetAddress.getLocalHost().getHostAddress();
			host = "115.159.70.199";
		} catch (UnknownHostException e) {
			e.printStackTrace();
			host = "localhost0";
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// System.loadLibrary("E:\\jnetpcap-1.3.0-1.win64\\jnetpcap-1.3.0\\jnetpcap.dll");
		// new Test().test(35891, "10.10.28.182", 8888);
		// System.out.println(InetAddress.getLocalHost());
		// new Test().test2(8888);
		new Test().test(35891, host, 8888);
		// int a = 80;
		// int c = (int) ((a & 0xffff) | 0x8000);
		// int b = (int) ((short) (c & 0xffff));
		// System.out.println(b);
		// ArrayList<Integer> list = new ArrayList<>();
		// list.add(33);
		// list.add(55);
		// list.add(11);
		// Integer[] r = new Integer[list.size()];
		// r = list.toArray(r);
		// int[] s = new int[list.size()];
		// System.out.println(Arrays.toString(s));
		// System.out.println(InputEvent.getMaskForButton(1));
		// System.out.println(InputEvent.BUTTON1_MASK);
		// System.out.println(InputEvent.BUTTON1_DOWN_MASK);
		// System.out.println(InputEvent.getMaskForButton(2));
		// System.out.println(InputEvent.BUTTON2_MASK);
		// System.out.println(InputEvent.BUTTON2_DOWN_MASK);
		// System.out.println(InputEvent.getMaskForButton(3));
		// System.out.println(InputEvent.BUTTON3_MASK);
		// System.out.println(InputEvent.BUTTON3_DOWN_MASK);
		// System.out.println(InputEvent.getMaskForButton(4));
		// System.out.println(InputEvent.getMaskForButton(5));

		// Packet pack1 = ControlerPack.createErrorPack("123");
		// Packet pack2 = ControlerPack.createErrorPack("777");
		// byte[] data1 = printPacket(pack1);
		// byte[] data2 = printPacket(pack2);
		// ControlerPack pack3 = (ControlerPack) getPacket(data2);
		// ControlerPack pack4 = (ControlerPack) getPacket(data1);
		// System.out.println(pack3.getErrorMessage());
		// System.out.println(pack4.getErrorMessage());
		//
		// Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		//
		// clip.addFlavorListener(new FlavorListener() {
		//
		// @Override
		// public void flavorsChanged(FlavorEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// Transferable t = clip.getContents(null);
		//
		// try {
		// Object o =
		// (clip.getContents(null).getTransferData(DataFlavor.javaFileListFlavor));
		//
		// List<File> files = (List<File>) o;
		// ArrayList<byte[]> list = new ArrayList<>();
		// int length = 0;
		// for (File f : files) {
		// byte[] tmp = f.getPath().getBytes();
		// list.add(tmp);
		// length += tmp.length;
		// }
		// byte[] all = new byte[length];
		// length = 0;
		// for (byte[] b : list) {
		// System.arraycopy(b, 0, all, length, b.length);
		// length += b.length;
		// }
		// int[] num = new int[all.length];
		// for (int i = 0; i < num.length; i++)
		// num[i] = all[i];
		// System.out.println(Arrays.toString(all));
		// } catch (UnsupportedFlavorException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public void test(int localPort, String host, int port) {
		try {
			localPort = 6779;
			SocketAddress localAddr = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), localPort);

			// Socket socket = new Socket();
			// socket.setReuseAddress(true);
			// socket.bind(localAddr);

			SocketAddress inetAddr = new InetSocketAddress(host, port);
			System.out.println(inetAddr);
			// socket.connect(inetAddr);
			Socket socket = new Socket(host, port);
			localPort = socket.getLocalPort();

			System.out.println(socket.getLocalAddress() + ":" + localPort + ":" + socket.getLocalSocketAddress());
			// socket.shutdownOutput();
			// socket.shutdownInput();
			socket.close();
			

			ServerSocket server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(localAddr);
			// SocketAddress inetAddr = new InetSocketAddress(host, port);
			// socket.connect(inetAddr);

			System.out.println("123");
			Socket s = server.accept();
			// s.setr
			System.out.println(s.getInetAddress());

			// socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test2(int port) {
		try {
			ServerSocket server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(host, port));
			Socket socket = server.accept();
			InetAddress address = socket.getInetAddress();
			int p = socket.getPort();
			socket.close();
			socket = new Socket(address, port);
			System.out.println(socket.getInetAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static byte[] printPacket(Packet packet) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(packet);
		oout.close();
		byte[] result = bout.toByteArray();
		bout.close();
		return result;
	}

	public void test3() throws IOException {
		final int LOCAL_PORT = 8880;

		SocketAddress localAddr = new InetSocketAddress(LOCAL_PORT);

		ServerSocket ss = new ServerSocket();

		ss.setReuseAddress(true);

		ss.bind(localAddr);

		Socket socket = new Socket();

		socket.setReuseAddress(true);

		socket.bind(localAddr);

		final String SERVER_IP = "";

		final int SERVER_PORT = 60000;

		SocketAddress serverAddr = new InetSocketAddress(SERVER_IP, SERVER_PORT);

		socket.connect(serverAddr);

		// sendAndRegist(socket);

		// List<InetSocketAddress> addresses = receiveAndGetAddresses(socket);

		// createThreadToDoSomeThing(socket,addresses);

		while (true) {

			Socket s = ss.accept();

			// createThreadToAccept(s);
		}
	}

	// private static void createThreadToAccept(Socket s) {
	//
	// ClientInfo info = readRegistInfo(s);
	//
	// info.setSocketAdress(s.getRemoteSocketAddress());
	//
	// doRegist(info);
	//
	// List<SocketAddress> online = findOnlineAddresses();
	//
	// writeOnlineAddresses(online);
	//
	// }

	private static Packet getPacket(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
		ObjectInputStream oin = new ObjectInputStream(bin);
		Object obj = oin.readObject();
		bin.close();
		oin.close();
		if (obj instanceof Packet)
			return (Packet) obj;
		else
			throw new ClassCastException("can't cast to Packet");
	}
}
