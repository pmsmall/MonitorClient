package com.net.capture;
import javax.swing.JOptionPane;
/**
 * “关于软件”界面的实现类
 * @author Frank
 *
 */
public class AboutWin {
	String str = "软件名：基于winpcap的以太网流量分析器\n" + "版本：v1.0.0\n" + "作者：Frank";

	// 显示
	public void showAboutWin() {
		// 弹窗
		JOptionPane.showMessageDialog( TestFrame.mainFrame, str,"关于软件",JOptionPane.INFORMATION_MESSAGE);
	}
	
}
