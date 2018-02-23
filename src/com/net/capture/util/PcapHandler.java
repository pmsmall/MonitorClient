package com.net.capture.util;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

/**
 * 实现了PcapPacketHandler的接口，重写nextPacket方法
 * handler：一个处理，监听，回调的接口，用于在一个新的packet捕获的时候，获得通知
 * @author Frank
 *
 */
public class PcapHandler<Object> implements PcapPacketHandler<Object>{

	@Override
	public void nextPacket(PcapPacket packet, Object user) {
		//对数据包的处理
		PacketMatch mpm = PacketMatch.getInstance();
		
		mpm.handlePacket(packet);
	}
	

}
