package com.monitor.controler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.mine.util.io.TrasactBuffer;
import com.port.scaner.ScannerViewer;

public class UpdateScannerView {
	ScanManager scan;
	ScannerViewer view;
	private TrasactBuffer buff;
	BufferedReader reader;
	OutputStreamWriter writer;
	HashMap<String, ArrayList<Integer>> map;

	void initStream() {
		buff = new TrasactBuffer(2048);
		writer = new OutputStreamWriter(buff.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(buff.getInputStream()));
		map = new HashMap<>();
		ArrayList<Integer> available = new ArrayList<>();
		ArrayList<Integer> closed = new ArrayList<>();
		ArrayList<Integer> timeOut = new ArrayList<>();
		map.put("available", available);
		map.put("closed", closed);
		map.put("timeOut", timeOut);
	}

	public UpdateScannerView(ScanManager scan) {
		initStream();
		this.scan = scan;
		view = new ScannerViewer(reader, this);
	}

	public boolean startScanning(String minIP, String maxIP, int minPort, int maxPort, int threadNum, int timeOut) {
		return scan.startScanning(minIP, maxIP, minPort, maxPort, threadNum, timeOut);
	}

	public void write(HashMap<String, ArrayList<Integer>> map) throws IOException {
		ArrayList<Integer> available = map.get("available");
		ArrayList<Integer> closed = map.get("closed");
		ArrayList<Integer> timeOut = map.get("timeOut");
		if (!available.isEmpty()) {
			this.map.get("available").addAll(available);
			writer.write("Available ports: ");
			int i;
			for (i = 0; i < available.size() - 1; i++) {
				writer.write(available.get(i) + ",");
			}
			if (i < available.size()) {
				writer.write(available.get(i) + "\n");
			}
		}
		if (!closed.isEmpty()) {
			this.map.get("closed").addAll(closed);
			writer.write("Closed ports: ");
			int i;
			for (i = 0; i < available.size() - 1; i++) {
				writer.write(available.get(i) + ",");
			}
			if (i < available.size()) {
				writer.write(available.get(i) + "\n");
			}
		}
		if (!timeOut.isEmpty()) {
			this.map.get("timeOut").addAll(timeOut);
			writer.write("TimeOut ports: ");
			int i;
			for (i = 0; i < available.size() - 1; i++) {
				writer.write(available.get(i) + ",");
			}
			if (i < available.size()) {
				writer.write(available.get(i) + "\n");
			}
		}

	}
}
