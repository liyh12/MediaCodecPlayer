package com.youku.yplayer;

import java.util.Enumeration;
import java.util.Vector;

public class AvPacketPool {

	private Vector<AvPacket> avPackets;

	public AvPacketPool(int cap) {
		avPackets = new Vector<>();
		for (int i = 0; i < cap; i++) {
			avPackets.add(new AvPacket());
		}
	}

	public AvPacket getAvPacket() {
		AvPacket pObj = null;
		for (AvPacket avPacket : avPackets) {
			if(!avPacket.getBusy()){
				pObj=avPacket;
				pObj.setBusy(true);
				break;
			}
		}
		if(pObj==null){
			//如果没有，也要保证返回，这个就直接new了，而且也不保存到vector里，使vector不至于过大，new出来的也可以被gc，就不管了
			pObj=new AvPacket();
		}
		return pObj;
	}

	public void release(){
		avPackets.clear();
	}
}
