package com.monitor.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import com.mine.MyUI.MyButton;
import com.mine.MyUI.theme.ButtonTheme;
import com.mine.MyUI.theme.DefaultButtonTheme;
import com.mine.MyUI.MyFrame;
import com.mine.MyUI.MyLabel;
import com.mine.MyUI.MyTextField;
import com.mine.MyUI.MyWindow;
import com.mine.MyUI.UITools;
import com.mine.MyUI.theme.DefaultLabelTheme;
import com.mine.MyUI.theme.FrameTheme;
import com.mine.MyUI.theme.WindowTheme;
import com.mine.MyUI.theme.LabelTheme.stringType;
import com.monitor.core.LoginEven;
import com.monitor.core.LoginListener;
import com.monitor.core.UserData;

import javafx.util.Pair;

public class Login {
	private MyFrame login;
	MyLabel idLabel;
	MyLabel passwordLabel;
	MyTextField idInput;
	MyButton requireButton;
	MyWindow outerWindow;
	LinkedList<LoginListener> loginListeners;

	public Login() {

		login = new MyFrame(new FrameTheme(new Color(0x141947)) {
		});
		test();
		login.setSize(668, 441);
		outerWindow = new MyWindow(new WindowTheme("img/backgroudout.png") {
		}, login);
		outerWindow.setOpacity(0.5f);
		UITools.bindUI(login, outerWindow);
		MyLabel idlogo = new MyLabel(new DefaultLabelTheme("id", stringType.text));
		idLabel = new MyLabel(new DefaultLabelTheme("...", stringType.text));
		MyLabel passwordLogo = new MyLabel(new DefaultLabelTheme("password", stringType.text));
		passwordLabel = new MyLabel(new DefaultLabelTheme("...", stringType.text));

		Font font = new Font("楷体", Font.BOLD, 20);
		Color fontColor = Color.YELLOW;
		idlogo.setSize(45, 30);
		idlogo.setFont(font);
		idlogo.setForeground(fontColor);
		idlogo.setLocation(107, 150);
		idLabel.setSize(200, 30);
		idLabel.setLocation(125, 150);
		idLabel.setForeground(fontColor);
		idLabel.setFont(font);
		passwordLogo.setSize(100, 30);
		passwordLogo.setLocation(45, 210);
		passwordLogo.setForeground(fontColor);
		passwordLogo.setFont(font);
		passwordLabel.setSize(200, 30);
		passwordLabel.setLocation(125, 210);
		passwordLabel.setForeground(fontColor);
		passwordLabel.setFont(font);
		setIdAndPassword();

		login.add(idlogo);
		login.add(passwordLogo);
		login.add(idLabel);
		login.add(passwordLabel);

		MyButton close = new MyButton(
				new ButtonTheme("img/close_button.png", "img/close_button_press.png", "img/close_button_hover.png") {
					@Override
					public void onClick(MouseEvent e) {
						super.onClick(e);
						System.exit(0);
					}
				});

		login.add(close);

		close.setLocation(620, 20);
		close.setSize(20, 20);

		idInput = new MyTextField();
		idInput.setSize(150, 20);
		idInput.setLocation(384, 156);
		login.add(idInput);

		Color release = new Color(0xffffff);
		Color press = new Color(0x5f5f5f);
		Color hover = new Color(0x2f2f2f);
		requireButton = new MyButton(new DefaultButtonTheme(release, press, hover) {
			@Override
			public void onClick(MouseEvent e) {
				super.onClick(e);
				try {
					LoginEven le = new LoginEven("queryID", new Integer(idInput.getText()), login);
					new Thread(queryID).start();
					for (LoginListener l : loginListeners) {
						l.loginEvenOccer(le);
					}

				} catch (NumberFormatException e2) {
					// e2.printStackTrace();
					// 输入有误
					if (!idInput.getText().equals("")) {
						JOptionPane.showMessageDialog(login, "输入有误");
					}
				}
			}
		});
		requireButton.setText("连接该用户");
		requireButton.setFont(new Font("楷体", Font.BOLD, 15));
		requireButton.setForeground(new Color(0x000));
		requireButton.setSize(new Dimension(100, 20));
		requireButton.setLocation(430, 230);
		login.add(requireButton);

		loginListeners = new LinkedList<>();
		login.setLocationRelativeTo(null);
	}

	private void test() {
		login.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				System.out.println(Integer.toHexString(e.getKeyCode()));
			}
		});
	}

	private void setIdAndPassword() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Pair<Integer, Integer> user = UserData.getUser();
				idLabel.setText(user.getKey().toString());
				passwordLabel.setText(user.getValue().toString());
			}
		}).start();
	}

	public void setVisable(boolean visable) {
		if (visable) {
			outerWindow.setLocation(login.getX() - 83, login.getY() - 67);
		}
		outerWindow.setVisible(visable);
		login.setVisible(visable);
	}

	public void addLoginListener(LoginListener lis) {
		loginListeners.add(lis);
	}

	public void removeLoginListener(LoginListener lis) {
		loginListeners.remove(lis);
	}

	Runnable queryID = new Runnable() {

		@Override
		public void run() {
			if (UserData.waitForIDQuery()) {
				String s = JOptionPane.showInputDialog("请输入密码");
				if (s != null) {
					LoginEven le = new LoginEven("require",
							new Pair<Integer, Integer>(new Integer(idInput.getText()), new Integer(s)), login);
					for (LoginListener l : loginListeners) {
						l.loginEvenOccer(le);
					}
				}
			}
		}
	};

	public MyFrame getFrame() {
		return login;
	}

	class MyButtonListener implements MouseListener, MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}
}
