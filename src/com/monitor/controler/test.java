package com.monitor.controler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class test {
	public static void main(String[] args) {
		// System.out.println("" + Integer.toBinaryString(0x3));
		// Locale list[] = DateFormat.getAvailableLocales();
		// for(Locale l:list){
		// System.out.println(l+":"+l.getDisplayName());
		// }
		// System.getProperties().list(System.out);
		new Thread(() -> {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				try {
					String fileName = reader.readLine();
					if(fileName.equals("exit")|fileName.equals("quit"))
						break;
					File file = new File(fileName);
					if (file.exists()) {
						FileInputStream input = new FileInputStream(file);
						ArrayList<byte[]> buffs = new ArrayList<>(5);
						byte[] b = new byte[1024 * 1024];
						int len = 0;
						int allLen = 0;
						while ((len = input.read(b)) > 0) {
							allLen += len;
							byte[] tmp = new byte[len];
							System.arraycopy(b, 0, tmp, 0, len);
							buffs.add(tmp);
						}
						input.close();
						byte[] data = new byte[allLen];
						allLen = 0;
						for (int i = 0; i < buffs.size(); i++) {
							byte[] tmp = buffs.get(i);
							System.arraycopy(tmp, 0, data, allLen, tmp.length);
							allLen += tmp.length;
						}
						String content = new String(data, "GBK");
						data = content.getBytes("UTF8");
						FileOutputStream out = new FileOutputStream(file);
						out.write(data);
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}).start();
	}
}
