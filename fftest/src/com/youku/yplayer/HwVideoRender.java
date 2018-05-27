package com.youku.yplayer;

import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaCodec.BufferInfo;
import android.os.Build;
import android.view.Surface;

public class HwVideoRender {
	private MediaCodec mediaCodec;
	private ByteBuffer[] codecInputBuffers = null;
	private int inputinx;
	private int outputinx;
	private ByteBuffer[] codecOutputBuffers = null;
	private int timeoutus = 10000;
//	private double timebase;
	private boolean hasConfigCodec=false;
	private BufferInfo outputBufferInfo = new BufferInfo();
	
	public HwVideoRender(Surface surface,int width,int height,float framerate) {
		init(surface,width,height,framerate);
		// configCodec(packetCache.getCurrentExtractor()
		// .getVideoExtraData());
	}

	private void init(Surface surface,int width,int height,float framerate) {
		synchronized (this) {
//			timebase = codecPlayer.getCurrentExtractor().getvTimeBase();
//			if (mediaCodec == null) {
				try {
					mediaCodec = MediaCodec.createDecoderByType("video/avc");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//			}
			MediaFormat vformat = MediaFormat.createVideoFormat("video/avc",
					width, height);
			vformat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 655360);
			vformat.setFloat(MediaFormat.KEY_FRAME_RATE, framerate);
			mediaCodec.configure(vformat, surface, null, 0);
			mediaCodec.start();
			hasConfigCodec=false;
			codecInputBuffers = mediaCodec.getInputBuffers();
			codecOutputBuffers = mediaCodec.getOutputBuffers();
		}
	}

	public boolean canputdata() {
		inputinx = mediaCodec.dequeueInputBuffer(timeoutus);
		if (inputinx >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public void putData(AvPacket avPacket) {
		ByteBuffer putBuffer = avPacket.getData();
		putBuffer.position(0);
		putBuffer.putInt(1);
		putBuffer.position(0);
		ByteBuffer inputBuffer = codecInputBuffers[inputinx];
		inputBuffer.position(0);
		inputBuffer.put(putBuffer);
		mediaCodec.queueInputBuffer(inputinx, 0, putBuffer.limit(),
				avPacket.getPts(), 0);
	}

	public boolean canrender() {
		outputinx = 0;
		outputinx = mediaCodec.dequeueOutputBuffer(outputBufferInfo, timeoutus);
		if (outputinx == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
			codecOutputBuffers = mediaCodec.getOutputBuffers();
			outputinx = mediaCodec.dequeueOutputBuffer(outputBufferInfo,
					timeoutus);
		}
		if (outputinx >= 0) {
			return true;
		}
		return false;
	}

	public long getCurrentPts() {
		return outputBufferInfo.presentationTimeUs;
	}

	public void configCodec(ByteBuffer extdata) {
		fixVideoExt(extdata);
		extdata.position(4);
		ByteBuffer inputBuffer = codecInputBuffers[inputinx];
		inputBuffer.position(0);
		inputBuffer.put(extdata);
		mediaCodec.queueInputBuffer(inputinx, 0, extdata.limit() - 4, 0, 0);
		hasConfigCodec=true;
	}

	public void render() {
		mediaCodec.releaseOutputBuffer(outputinx, true);
	}
	public void tryRender(){
		outputinx = mediaCodec.dequeueOutputBuffer(outputBufferInfo, timeoutus);
		if (outputinx == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
			codecOutputBuffers = mediaCodec.getOutputBuffers();
			outputinx = mediaCodec.dequeueOutputBuffer(outputBufferInfo,
					timeoutus);
		}
		if (outputinx >= 0) {
			mediaCodec.releaseOutputBuffer(outputinx, true);
		}
	}
	@SuppressLint("NewApi")
	public void restart(Surface surface,int width,int height,float framerate) {
		mediaCodec.flush();
		mediaCodec.stop();
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			mediaCodec.reset();
		}
		init(surface,width,height,framerate);
	}
	/**
	 * 用于清空待render的帧 seek后，可能还会播seek前outputbuffer里残余的帧，所以seek时要把这些帧去掉
	 */
	public void flush() {
		mediaCodec.flush();
	}

	private void fixVideoExt(ByteBuffer buf) {
		int csd0sz = buf.getShort(6);
		int csd1sz = buf.getShort(csd0sz + 9);
		buf.position(4);
		buf.putInt(1);
		buf.position(csd0sz + 11);
		byte[] cds1 = new byte[csd1sz];
		buf.get(cds1);
		buf.position(csd0sz + 8);
		buf.putInt(1);
		buf.put(cds1);
	}

	public void release() {
		if (mediaCodec != null) {
			mediaCodec.stop();
			mediaCodec.release();
		}
	}

	public boolean isHasConfigCodec() {
		return hasConfigCodec;
	}
}
