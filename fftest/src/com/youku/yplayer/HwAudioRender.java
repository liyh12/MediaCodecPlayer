package com.youku.yplayer;

import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaCodec.BufferInfo;
import android.util.Log;

public class HwAudioRender {
	private AudioTrack audioTrack = null;
	private MediaCodec mediaCodec;
	private ByteBuffer[] codecInputBuffers = null;
	private ByteBuffer[] codecOutputBuffers = null;
	private int inputinx;
	private int outputinx;
	private int timeoutus = 10000;
//	private double timebase;
	private int freq, audioChannels;
	private BufferInfo outputBufferInfo = new BufferInfo();
	private byte[] audiobyte;
	private boolean hasConfigCodec=false;
	// private HwCodecPlayer codecPlayer;

	public HwAudioRender(int freq,  int audioChannels) {
		init(freq,  audioChannels);
	}

	private void init(int fq, int ac) {
			if(mediaCodec==null){
				try {
					mediaCodec = MediaCodec.createDecoderByType("audio/mp4a-latm");
					audiobyte = new byte[65535];
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			MediaFormat format = MediaFormat.createAudioFormat(
					"audio/mp4a-latm", freq, audioChannels);
			mediaCodec.configure(format, null, null, 0);
			mediaCodec.start();
			hasConfigCodec=false;
			codecInputBuffers = mediaCodec.getInputBuffers();
			codecOutputBuffers = mediaCodec.getOutputBuffers();
			if(audioTrack!=null){
				if(fq==freq&&ac==audioChannels){
					return;
				}
			}
			if(audioTrack!=null){
				audioTrack.pause();
				audioTrack.flush();
				audioTrack.release();
			}
			this.freq = fq;
			this.audioChannels = ac;
			int af = AudioFormat.CHANNEL_OUT_DEFAULT;
			if (audioChannels == 1) {
				af = AudioFormat.CHANNEL_OUT_MONO;
			} else if (audioChannels == 2) {
				af = AudioFormat.CHANNEL_OUT_STEREO;
			} else {

			}
			int bufsize = AudioTrack.getMinBufferSize(freq, af,
					AudioFormat.ENCODING_PCM_16BIT);
			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, freq, af,
					AudioFormat.ENCODING_PCM_16BIT, bufsize,
					AudioTrack.MODE_STREAM);
			audioTrack.play();
			Log.e("abc", "audioTrak renew");
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
		ByteBuffer inputBuffer = codecInputBuffers[inputinx];
		ByteBuffer putBuffer = avPacket.getData();
		putBuffer.position(0);
		inputBuffer.position(0);
		inputBuffer.put(putBuffer);
		mediaCodec.queueInputBuffer(inputinx, 0, putBuffer.limit(),
				avPacket.getPts(), 0);
	}

	public boolean canrender() {
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
		ByteBuffer inputBuffer = codecInputBuffers[inputinx];
		inputBuffer.position(0);
		inputBuffer.put(extdata);
		inputBuffer.position(0);
		mediaCodec.queueInputBuffer(inputinx, 0, extdata.limit() - 1, 0,
				MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
		hasConfigCodec=true;
	}

	public void render(boolean render) {
		if (render) {
			ByteBuffer obuf = codecOutputBuffers[outputinx];
			int size = outputBufferInfo.size;
			int orgPos = obuf.position();
			obuf.get(audiobyte, 0, size);
			obuf.position(orgPos);
			audioTrack.write(audiobyte, 0, size);
		}
		mediaCodec.releaseOutputBuffer(outputinx, false);
	}

	public void restart(int fq,  int ac) {
		mediaCodec.stop();
		init(fq,ac);
	}

	public void flush() {
		if (mediaCodec != null) {
			mediaCodec.flush();
		}
	}

	public void release() {
		if (mediaCodec != null) {
			mediaCodec.stop();
			mediaCodec.release();
		}
		if (audioTrack != null) {
			audioTrack.stop();
			audioTrack.release();
		}
	}

	public boolean isHasConfigCodec() {
		return hasConfigCodec;
	}
}
