package com.monitor.view;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventListener;

import javax.swing.JButton;

import com.mine.MyUI.MyFrame;
import com.mine.MyUI.MyImageIcon;
import com.mine.MyUI.MyPanel;
import com.mine.MyUI.theme.DefaultFrameTheme;
import com.mine.MyUI.theme.DefaultPanelTheme;
import com.monitor.controler.EventManager;
import com.monitor.core.AddListener;

public class MonitorView {
	private MyFrame mview;
	private Image panelBackground;
	private JButton close;
	private JButton min;
	private JButton upload;
	private JButton download;
	private JButton scan;
	private JButton overflow;

	// private JButton

	// private volatile Image background;
	// private int x = 0;
	// private int y = 0;
	MyPanel panel;
	public static final Dimension rawDimension;

	static {
		Image panelBackground = new MyImageIcon("/img/monitor3.png").getImage();
		rawDimension = new Dimension(panelBackground.getWidth(null), panelBackground.getHeight(null));
	}

	public MonitorView(String title) {
		mview = new MyFrame(new DefaultFrameTheme("/img/monitor1.png"));

		// mview.setSize(500, 500);

		mview.getTheme().updateBackgroudImage("/img/monitor2.png");
		panelBackground = new MyImageIcon("/img/monitor3.png").getImage();

		panel = new MyPanel(new DefaultPanelTheme(panelBackground));

		panel.setLocation(50, 45);
		panel.setPreferredSize(new Dimension(500, 500));
		panel.setSize(panelBackground.getWidth(null), panelBackground.getHeight(null));
		mview.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				EventManager.add(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				EventManager.add(e);
			}
		});
		mview.add(panel);
		mview.setLocationRelativeTo(null);
		close = new JButton("+");
		close.setSize(30, 30);
		close.setLocation(720, 530);
		close.addActionListener((l) -> {
			mview.setVisible(false);
			mview.getTheme().dispose();
			mview.dispose();
		});
		mview.add(close);

		upload = new JButton("上传");
		upload.setSize(60, 30);
		upload.setLocation(81, 530);
		upload.setActionCommand("upload");
		mview.add(upload);

		download = new JButton("下载");
		download.setSize(60, 30);
		download.setLocation(168, 530);
		download.setActionCommand("download");
		mview.add(download);

		scan = new JButton("扫描");
		scan.setSize(60, 30);
		scan.setLocation(250, 530);
		scan.setActionCommand("scan");
		mview.add(scan);

		overflow = new JButton("流量分析");
		overflow.setSize(90, 30);
		overflow.setLocation(340, 530);
		overflow.setActionCommand("overflow");
		mview.add(overflow);

		// mview.setSize(500, 500);
		// mview.setPreferredSize(new Dimension(500, 500));
	}

	public void setVisible(boolean visible) {
		mview.setVisible(visible);
		// MonitorImageManager.setImage(panelBackground, 1);
	}

	public void addListener(EventListener listener) {
		AddListener.addListener(panel, listener);
	}

	public void setImage(Image background, float scale) {
		// this.background = background;
		panel.getTheme().setBackgroudImage(background);
		repaint();
	}

	public void repaint() {
		panel.repaint();
	}

	public static void main(String[] args) {
		MonitorView v = new MonitorView("123");
		v.setVisible(true);
	}

	public void addUploadActionListener(ActionListener l) {
		upload.addActionListener(l);
	}

	public void addDownloadActionListener(ActionListener l) {
		download.addActionListener(l);
	}

	public void addScanActionListener(ActionListener l) {
		scan.addActionListener(l);
	}

	public void addOverflowActionListener(ActionListener l) {
		overflow.addActionListener(l);
	}

}
