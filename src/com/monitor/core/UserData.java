package com.monitor.core;

import javafx.util.Pair;

public class UserData {
	private static Pair<Integer, Integer> user;
	private static boolean[] initedUser = { false, true };
	private static boolean[] queryID = { false, true };
	static {
		user = null;
	}

	public static boolean waitForIDQuery() {
		synchronized (queryID) {
			try {
				queryID.wait();
				return queryID[0];
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void setIDQueryResult(boolean result) {
		synchronized (queryID) {
			queryID[0] = result;
			queryID.notify();
		}
	}

	public static void setUser(Pair<Integer, Integer> user) {
		synchronized (initedUser) {
			UserData.user = user;
			initedUser[0] = true;
			initedUser.notifyAll();
		}
	}

	public static Pair<Integer, Integer> getUser() {
		synchronized (initedUser) {
			if (!initedUser[0])
				try {
					initedUser.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return user;
	}
}
