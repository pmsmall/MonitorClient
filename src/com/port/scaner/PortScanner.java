package com.port.scaner;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import javax.swing.*;

import com.mine.MyUI.MyImageIcon;
import com.mine.util.io.TrasactBuffer;

import java.net.*;

/**
 * 
 * @author Frank
 *
 */
public class PortScanner implements ActionListener {

	int j = 0;

	// 主窗口的创建，new组件
	JFrame Frame = new JFrame();
	Label labelIPStart = new Label("start IP");
	Label labelIPEnd = new Label("final IP");
	Label labelPortStart = new Label("start port");
	Label labelPortEnd = new Label("final port");
	Label labelThread = new Label("Thread number");
	Label labelTimeOut = new Label("time out：");
	Label labelState = new Label("scan state：");
	Label labelResult = new Label("scan result：");
	Label labelScanning = new Label("waiting");

	JLabel photo = new JLabel(new MyImageIcon("/img/pikachu.jpg"));

	JTextField hostNameStart = new JTextField("127.0.0.1");
	JTextField hostNameEnd = new JTextField("127.0.0.1");
	JTextField PortStart = new JTextField("10");
	JTextField PortEnd = new JTextField("30");
	JTextField ThreadNum = new JTextField("9");
	JTextField time = new JTextField("2000");

	TextArea Result1 = new TextArea();
	TextArea Result2 = new TextArea();
	Label DLGINFO = new Label("");
	JButton Tcp_connect = new JButton("Tcp connect scan");
	JButton Tcp_SYN = new JButton("Tcp SYN scan");
	JButton ICMP_echo = new JButton("ICMP echo scan");
	JButton Exit = new JButton("exit");
	JButton save = new JButton("save result");
	BufferedReader reader;
	OutputStreamWriter writer;

	// 错误提示对话框
	JDialog DLGError = new JDialog(Frame, "ERROR");
	JButton OK = new JButton("OK");

	ScanTcpConnect conn;
	private Thread updateResult;

	// 在构造函数里进行窗口设计
	public PortScanner() {
		initScannerP();
		initUI();
		initStream();
	}

	void initScannerP() {
		conn = new ScanTcpConnect();
	}

	void initUI() {
		// 设置主窗体标题
		Frame.setTitle("计算机网络课设：多线程端口扫描");

		Frame.setSize(700, 700); // 设置主窗体大小

		// 添加窗口监听，使之可以关闭窗口

		// 设置一系列按钮和小窗口及图片
		// IP
		labelIPStart.setBounds(17, 13, 50, 30);
		hostNameStart.setBounds(90, 13, 90, 30);
		hostNameStart.setHorizontalAlignment(JTextField.CENTER);// 文本水平对齐方式
		labelIPEnd.setBounds(17, 63, 50, 30);
		hostNameEnd.setBounds(90, 63, 90, 30);
		hostNameEnd.setHorizontalAlignment(JTextField.CENTER);// 文本水平对齐方式
		// 起始端口
		labelPortStart.setBounds(17, 113, 50, 20);
		PortStart.setBounds(90, 113, 90, 25);
		PortStart.setHorizontalAlignment(JTextField.CENTER);
		// 结束端口
		labelPortEnd.setBounds(17, 163, 50, 20);
		PortEnd.setBounds(90, 163, 90, 25);
		PortEnd.setHorizontalAlignment(JTextField.CENTER);
		// 线程数
		labelThread.setBounds(17, 213, 50, 20);
		ThreadNum.setBounds(90, 213, 90, 25);
		ThreadNum.setHorizontalAlignment(JTextField.CENTER);
		// 请求超时
		labelTimeOut.setBounds(17, 263, 50, 20);
		time.setBounds(90, 263, 90, 25);
		time.setHorizontalAlignment(JTextField.CENTER);
		// Tcp connect扫描按钮
		Tcp_connect.setBounds(17, 313, 140, 30);
		Tcp_connect.setActionCommand("Tcp_connect");// 设置JButton的属性值
		Tcp_connect.addActionListener(this);// 监听按钮的行为
		// Tcp SYN扫描按钮
		Tcp_SYN.setBounds(17, 363, 140, 30);
		Tcp_SYN.setActionCommand("Tcp_SYN");// 设置JButton的属性值
		Tcp_SYN.addActionListener(this);// 监听按钮的行为
		// Icmp echo扫描按钮
		ICMP_echo.setBounds(17, 413, 140, 30);
		ICMP_echo.setActionCommand("ICMP_echo");// 设置JButton的属性值
		ICMP_echo.addActionListener(this);// 监听按钮的行为
		// 退出扫描按钮
		Exit.setBounds(17, 463, 140, 30);
		Exit.setActionCommand("Exit");
		Exit.addActionListener(this);
		// 保存结果按钮
		save.setBounds(17, 513, 140, 30);
		save.setActionCommand("save");
		save.addActionListener(this);
		// 扫描状态栏
		labelState.setBounds(230, 13, 70, 20);
		labelScanning.setBounds(310, 8, 120, 30);
		Result1.setBounds(230, 40, 210, 332);
		Result1.setEditable(false);
		Result1.setBackground(Color.WHITE);
		// 图片的显示
		photo.setBounds(393, 13, 283, 360);
		// 扫描结果栏
		labelResult.setBounds(230, 390, 100, 20);
		Result2.setBounds(230, 420, 500, 200);
		Result2.setEditable(false);
		Result2.setBackground(Color.WHITE);

		// 设置错误提示框
		Container ErrorDisplay = DLGError.getContentPane();
		ErrorDisplay.setLayout(null);
		ErrorDisplay.add(DLGINFO);
		ErrorDisplay.add(OK);
		OK.setActionCommand("OK");
		OK.addActionListener(this);

		// 将组件添加到主窗体
		Frame.setLayout(null);
		Frame.setResizable(false);// 窗口大小设置为不可变
		Frame.add(labelIPStart);
		Frame.add(labelIPEnd);
		Frame.add(labelPortStart);
		Frame.add(labelPortEnd);
		Frame.add(labelThread);
		Frame.add(labelTimeOut);
		Frame.add(hostNameStart);
		Frame.add(hostNameEnd);
		Frame.add(PortStart);
		Frame.add(PortEnd);
		Frame.add(ThreadNum);
		Frame.add(time);
		Frame.add(Tcp_connect);
		Frame.add(Tcp_SYN);
		Frame.add(ICMP_echo);
		Frame.add(Exit);
		Frame.add(save);
		Frame.add(labelState);
		Frame.add(labelScanning);
		Frame.add(Result1);
		Frame.add(photo);
		Frame.add(labelResult);
		Frame.add(Result2);
	}

	private TrasactBuffer buff;

	void initStream() {
		buff = new TrasactBuffer(2048);
		writer = new OutputStreamWriter(buff.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(buff.getInputStream()));
		updateResult = new Thread(() -> {
			String s;
			while (true) {
				try {
					s = reader.readLine();

					if (s != null) {
						Result1.append(s + "\n");
						// System.out.println(s);
					} else {
						Thread.sleep(1000);
					}
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void addWindowListener(WindowListener l) {
		Frame.addWindowListener(l);
	}

	public void setVisible(boolean visible) {
		Frame.setVisible(visible);
	}

	// 几个产生错误的原因
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String cmd = arg0.getActionCommand();

		int minPort, maxPort, threadNum;

		try {
			// 获取输入的相关数据
			minPort = Integer.parseInt(PortStart.getText());
			maxPort = Integer.parseInt(PortEnd.getText());
			threadNum = Integer.parseInt(ThreadNum.getText());
		} catch (NumberFormatException e1) {
			DLGError.setBounds(300, 280, 300, 200);
			DLGINFO.setText("port and thread number must be integer");
			DLGINFO.setBounds(25, 15, 350, 20);
			OK.setBounds(110, 50, 60, 30);
			DLGError.setVisible(true);
			return;
		}

		if ((minPort < 0) || (maxPort > 65536) || (minPort > maxPort)) { // 输入信息错误处理
			DLGError.setBounds(300, 280, 400, 200);
			DLGINFO.setText("port is between 0 and 65536，and the end port is bigger than the start one");
			DLGINFO.setBounds(25, 15, 350, 20);
			OK.setBounds(110, 50, 60, 30);
			DLGError.setVisible(true);
			return;
		} else if ((threadNum > 200) || (threadNum < 0)) {
			DLGError.setBounds(300, 280, 300, 200);
			DLGINFO.setText("thread number is integer between 1 and 200");
			DLGINFO.setBounds(25, 15, 200, 20);
			OK.setBounds(110, 50, 60, 30);
			DLGError.setVisible(true);
			return;
		} else if (cmd.equals("save")) {// 如果按了保存按钮
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter("\\扫描结果.txt"));
				bw.write(conn.getAllOutputString());
				bw.newLine();
				conn.clearOutputString();
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (cmd.equals("Tcp_connect") || cmd.equals("Tcp_SYN") || cmd.equals("ICMP_echo")) {
			// 如果按了开始扫描按钮
			if (Frame.isVisible() && updateResult.getState().equals(Thread.State.NEW)) {
				updateResult.start();
			}
			Result1.setText(null);
			Result2.setText(null);
			// 相关显示,.append()的用途是将后面的字符串接到原来的字符串上面来
			labelScanning.setText("start scanning......\n");
			Result1.append("scanning:" + hostNameStart.getText() + ", thread number is:" + threadNum + "\n");
			Result1.append("start port:" + minPort + "; end port: " + maxPort + "\n");
			// j++;
			try {
				conn.setHostAddr(InetAddress.getByName(hostNameStart.getText()));
				conn.setTime(Integer.parseInt(time.getText()));
			} catch (UnknownHostException e1) {
				DLGError.setBounds(300, 280, 300, 200);
				DLGINFO.setText("there is something wrong about ths host\n");
				DLGINFO.setBounds(25, 15, 200, 20);
				OK.setBounds(110, 50, 60, 30);
				DLGError.setVisible(true);
				return;
			}

			int dnum = threadNum - 1;
			int tmpMax = maxPort + 1;
			conn.startCount(maxPort - minPort + 1);
			// 多线程核心算法
			for (int i = minPort; i <= tmpMax;) {
				if ((i + dnum) <= tmpMax) {
					conn.start(hostNameStart.getText(), i, i + dnum, writer);
					// new ScanTcpConnect(i, i + threadNum).run();//
					// 引用Scan类的run()方法
					i += dnum;
				} else {
					conn.start(hostNameStart.getText(), i, tmpMax, writer);
					// new ScanTcpConnect(i, maxPort).run();
					i += tmpMax - +1;
				}
			}
			// try {
			// Thread.sleep(1);// 设置睡眠时间
			// } catch (InterruptedException e1) {
			// e1.printStackTrace();
			// }
			// TODO end of scan
			final JButton button = (JButton) (arg0.getSource());
			button.disable();
			new Thread(() -> {
				try {
					conn.waitForCountZero();
					Result1.append("scanner finished！\n");
					labelScanning.setText("scanner finished！\n");
					button.enable();
				} catch (InterruptedException e) {
					Result1.append("failed！time out\n");
					labelScanning.setText("time out\n");
				}
			}).start();

		} else if (cmd.equals("OK")) {// 如果按了OK按钮
			DLGError.dispose();// 释放资源
		} else if (cmd.equals("Eixt")) {// 如果按了退出扫描按钮
			// System.exit(0);// 非正常退出正在运行中的java虚拟机
			Frame.setVisible(false);
			Frame.dispose();
		}
	}

	public static void main(String[] args) { // 程序入口
		PortScanner ps = new PortScanner();
		ps.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// System.exit(0);
				ps.Frame.setVisible(false);
				ps.Frame.dispose();
			}
		});
		ps.setVisible(true);
	}

}
