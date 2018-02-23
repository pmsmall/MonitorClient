package com.monitor.controler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ScanManager {
	UpdateScannerView update;
	SocketManager sockets;

	public ScanManager(SocketManager sockets) {
		update = new UpdateScannerView(this);
		this.sockets = sockets;
	}

	public boolean startScanning(String minIP, String maxIP, int minPort, int maxPort, int threadNum, int timeOut) {
		
		return true;
	}

	public void write(HashMap<String, ArrayList<Integer>> map) throws IOException {
		update.write(map);
	}
}
