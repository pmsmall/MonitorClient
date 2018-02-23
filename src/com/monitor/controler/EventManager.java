package com.monitor.controler;

import java.awt.event.InputEvent;

import com.monitor.core.EventList;

public class EventManager {

	private static EventList list;
	static {
		list = new EventList();
	}

	public static boolean add(InputEvent e) {
		return list.add(e);
	}

	public static int[] getCodes() {
		return list.getCodes();
	}

}
