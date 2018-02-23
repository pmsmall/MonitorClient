package com.monitor.core;

import java.awt.event.InputEvent;
import java.util.ArrayList;

public class EventList {
	ArrayList<Integer> list;
	EventExecute execute;
	boolean needNotify = false;

	public EventList() {
		list = new ArrayList<>(10);
		execute = new EventExecute();
	}

	public boolean add(InputEvent e) {
		int code = execute.compile(e);
		boolean flag = false;
		synchronized (list) {
			flag = list.add(code);
			if (needNotify) {
				needNotify = false;
				list.notifyAll();
			}
		}
		return flag;
	}

	public int[] getCodes() {
		int[] codes = null;
		synchronized (list) {
			while (true)
				if (list.isEmpty()) {
					needNotify = true;
					try {
						list.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
			codes = new int[list.size()];
			for (int i = 0; i < codes.length; i++) {
				codes[i] = list.get(i);
			}
			list.clear();
		}
		return codes;
	}
}
