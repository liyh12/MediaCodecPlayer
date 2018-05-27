package com.youku.yplayer;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class AvPacket {
	private ByteBuffer data;
	private int dataSize;
	private long dts;
	private long pts;
	private long duration;
	private int streamInx;
	private long pos;
	private int flags;
	private int width,height;
	private double frameRate;
	private int audioChannels,sampleRate;
	private int index;
	
	//类型0普通，1configCodec，2restart,3changeInx

	
	private int type=0;

	private AtomicBoolean busy = new AtomicBoolean(false);

	public AvPacket() {

	}

	public AvPacket(int cap) {
		data = ByteBuffer.allocate(cap);
	}

	public ByteBuffer getData() {
		return data;
	}

	public void setData(ByteBuffer data) {
		this.data = data;
	}

	public void copyData(ByteBuffer datafrom) {
		
		if (this.data == null) {
			// 如果没有就创建一个等大小的
			this.data = ByteBuffer.allocate(datafrom.limit());
		} else {
			// 如果存在但是容量不够，就进行扩容
			if (this.data.capacity() < datafrom.limit()) {
				this.data = ByteBuffer.allocate(datafrom.limit());
			}
		}
		datafrom.position(0);
		this.data.position(0);
		this.data.limit(datafrom.limit());
		this.data.put(datafrom);
		datafrom.position(0);
		this.data.position(0);
		this.dataSize = datafrom.limit();
	}

	public int getDataSize() {
		return dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	public long getDts() {
		return dts;
	}

	public void setDts(long dts) {
		this.dts = dts;
	}

	public long getPts() {
		return pts;
	}

	public void setPts(long pts) {
		this.pts = pts;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getStreamInx() {
		return streamInx;
	}

	public void setStreamInx(int streamInx) {
		this.streamInx = streamInx;
	}

	public long getPos() {
		return pos;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void copyTo(AvPacket dst) {
		// byte[] bb=new byte[this.data.limit()];
		// this.data.get(bb);
		if (dst.data == null) {
			// 如果没有就创建一个等大小的
			dst.data = ByteBuffer.allocate(this.data.limit());
		} else {
			// 如果存在但是容量不够，就进行扩容
			if (dst.data.capacity() < this.data.limit()) {
				dst.data = ByteBuffer.allocate(this.data.limit());
			}
		}
		dst.data.limit(this.data.limit());
		dst.data.position(0);
		dst.data.put(data);
		dst.data.position(0);
		// dst.data=this.data;
		// dst.data=data;
		dst.dataSize = dataSize;
		dst.dts = dts;
		dst.duration = duration;
		dst.flags = flags;
		dst.pos = pos;
		dst.pts = pts;
		dst.streamInx = streamInx;
	}

	public boolean getBusy() {
		return busy.get();
	}

	public void setBusy(boolean b) {
		this.busy.set(b);
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(double frameRate) {
		this.frameRate = frameRate;
	}

	public int getAudioChannels() {
		return audioChannels;
	}

	public void setAudioChannels(int audioChannels) {
		this.audioChannels = audioChannels;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
}
