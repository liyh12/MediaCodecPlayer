package com.youku.yplayer;

import java.nio.ByteBuffer;

import android.util.Log;

public class FFExtractor {
	private AvPacket avPacket;
	private int prepared = 0;
	private long mNativePointer;
	private String metaData;
	private long duration;
	private long bitRate;
	private int width;
	private int height;
	private int streamNum;
	private int sampleRate;
	private int audioChannels;
	private double frameRate;
	private int streamVideoInx;
	private int streamAudioInx;
	private double vTimeBase;
	private double aTimeBase;
	private int videoCodecId;
	private int audioCodecId;
	private boolean hasread = true;
	private int readystreamInx = -1;
	private int retrytimes=0;

	public FFExtractor() {
		mNativePointer = init();
		avPacket = new AvPacket();
	}

	public ByteBuffer getVideoExtraData() {
		return getExtraData(streamVideoInx);
	}

	public ByteBuffer getAudioExtraData() {
		return getExtraData(streamAudioInx);
	}

	/**
	 * 
	 * @return 返回streaminx，-1表示失败
	 */
	public int prereadNextPacket() {
		if (hasread) {
			readystreamInx = prereadNext();
			hasread = false;
		}
		return readystreamInx;
	}

	public void getReadPacket(AvPacket avp, byte[] jba) {
		getRead(avp, jba);
		hasread = true;
	}

	public AvPacket getReadPacket2() {
		getRead2(avPacket);
		hasread = true;
		return avPacket;
	}

	public void setHasRead(boolean bb) {
		hasread = bb;
	}

	public boolean prep() {
		//TODO 去掉
		int prep=prepare();
		if (prep >= 0) {
			prepared = 1;
			return true;
		}else{
			prepared = -1;
			Log.e("eof", "prepare:"+prep);
		}
		return false;
	}

	public boolean seek(long ms) {
		if (prepared==0) {
			if (!prep()) {
				return false;
			}
		}
		if(prepared==1){
			if (seekfile(ms) >= 0) {
				return true;
			}
		}
		return false;
	}

	public void relse() {
		if (prepared!=0) {
			release();
		}
	}

	public int getBufferPercent() {
		int a = getBufferPos();
		int b = getFileLength();
		b/=100;
		if(b!=0){
			return a / b;
		}else{
			return 0;
		}
	}

	private native int prereadNext();
	// 将数据写入给的byte[]中
	private native void getRead(AvPacket avp, byte[] jba);

	// 返回一个带有new好的bytebuff的AVpacket
	private native void getRead2(AvPacket avp);

	private native long init();

	public native void setDataSource(String str);

	public native int seekfile(long ms);
	public native int seekframe(long ms);

	public native int getBufferPos();

	public native int getFileLength();
	
	public native int hasError();

	public native int prepare();

	private native ByteBuffer getExtraData(int streamInx);

	public native void stopread();
	
	public native void release();
	
	
	///
	public native byte[] _aestest2(byte[] plaintxt,byte[] key,byte[] result);
	public native byte[] native_aestest2(byte[] plaintxt,byte[] key,byte[] result);

	public AvPacket getAvPacket() {
		return avPacket;
	}

	public void setAvPacket(AvPacket avPacket) {
		this.avPacket = avPacket;
	}

	public long getmNativePointer() {
		return mNativePointer;
	}

	public void setmNativePointer(long mNativePointer) {
		this.mNativePointer = mNativePointer;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getBitRate() {
		return bitRate;
	}

	public void setBitRate(long bitRate) {
		this.bitRate = bitRate;
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

	public int getStreamNum() {
		return streamNum;
	}

	public void setStreamNum(int streamNum) {
		this.streamNum = streamNum;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getAudioChannels() {
		return audioChannels;
	}

	public void setAudioChannels(int audioChannels) {
		this.audioChannels = audioChannels;
	}

	public int getStreamVideoInx() {
		return streamVideoInx;
	}

	public void setStreamVideoInx(int streamVideoInx) {
		this.streamVideoInx = streamVideoInx;
	}

	public int getStreamAudioInx() {
		return streamAudioInx;
	}

	public void setStreamAudioInx(int streamAudioInx) {
		this.streamAudioInx = streamAudioInx;
	}

	public double getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(double frameRate) {
		this.frameRate = frameRate;
	}

	public double getvTimeBase() {
		return vTimeBase;
	}

	public void setvTimeBase(double vTimeBase) {
		this.vTimeBase = vTimeBase;
	}

	public double getaTimeBase() {
		return aTimeBase;
	}

	public void setaTimeBase(double aTimeBase) {
		this.aTimeBase = aTimeBase;
	}

	public int getVideoCodecId() {
		return videoCodecId;
	}

	public void setVideoCodecId(int videoCodecId) {
		this.videoCodecId = videoCodecId;
	}

	public int getAudioCodecId() {
		return audioCodecId;
	}

	public void setAudioCodecId(int audioCodecId) {
		this.audioCodecId = audioCodecId;
	}

	public int getRetrytimes() {
		return retrytimes;
	}

	public void addRetrytimes(int addtime) {
		this.retrytimes+=addtime;
	}
}
