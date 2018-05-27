package com.youku.yplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.SurfaceHolder;

public class UMediaPlayer2 extends MediaPlayer {
	// private HwCodecPlayer adPlayer;
	private HwCodecPlayer player;
	
	//player的七个回调
	private OnBufferingUpdateListener onBufferingUpdateListener;
	private OnCompletionListener onCompletionListener;
	private OnErrorListener onErrorListener;
	private OnInfoListener onInfoListener;
	private OnPreparedListener onPreparedListener;
	private OnSeekCompleteListener onSeekCompleteListener;
	private OnVideoSizeChangedListener onVideoSizeChangedListener;
	//TODO adPlayer的回调

	public UMediaPlayer2() {
		// adPlayer = new HwCodecPlayer();
		player = new HwCodecPlayer();
		player.setPlayerListener(playerListener);
	}

	@Override
	public void setDisplay(SurfaceHolder sh) {
		// TODO Auto-generated method stub
		// adPlayer.setDisplay(sh);
		player.setDisplay(sh);
	}

	@Override
	public void setSurface(Surface surface) {
		// TODO Auto-generated method stub
		// adPlayer.setSurface(surface);
		player.setSurface(surface);
	}

	@Override
	public void setDataSource(String path) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		// TODO Auto-generated method stub
		List<String> paths = new ArrayList<String>();
		paths.add(path);
		setDataSource(paths);
		// super.setDataSource(path);
	}

	public void setDataSource(List<String> path) {
		player.setDataSource(path);
	}

	public void setAdDataSource(List<String> path) {
		// adPlayer.setDataSource(path);
	}

	@Override
	public void prepare() throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		// adPlayer.prepare();
		player.prepare();
	}

	@Override
	public void prepareAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		player.prepareAsync();
	}

	@Override
	public void start() throws IllegalStateException {
		// TODO Auto-generated method stub
		player.start();
	}

	@Override
	public void stop() throws IllegalStateException {
		// TODO Auto-generated method stub
		player.stop();
	}

	@Override
	public void pause() throws IllegalStateException {
		// TODO Auto-generated method stub
		player.pause();
	}

	@Override
	public void seekTo(int msec) throws IllegalStateException {
		// TODO Auto-generated method stub
		player.seekTo(msec);
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		super.release();
		onBufferingUpdateListener=null;
		onCompletionListener=null;
		onErrorListener=null;
		onInfoListener=null;
		onPreparedListener=null;
		onSeekCompleteListener=null;
		onVideoSizeChangedListener=null;
		player.release();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		player.reset();
	}

	@Override
	public boolean isLooping() {
		// TODO Auto-generated method stub
		return super.isLooping();
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		if(player!=null){
			return player.isPlaying();
		}else{
			return super.isPlaying();
		}
		
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		if(player!=null){
			return (int) player.getCurrentPosition();
		}else{
			return super.getCurrentPosition();
		}
		
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return (int) player.getDuration();
	}

	@Override
	public int getVideoHeight() {
		// TODO Auto-generated method stub
		return player.getVideoHeight();
	}

	@Override
	public int getVideoWidth() {
		// TODO Auto-generated method stub
		return player.getVideoWidth();
	}

	@Override
	public void setVolume(float leftVolume, float rightVolume) {
		// TODO Auto-generated method stub
		player.setVolume(leftVolume, rightVolume);
	}

	@Override
	public void setScreenOnWhilePlaying(boolean screenOn) {
		// TODO Auto-generated method stub
		player.setScreenOnWhilePlaying(screenOn);
	}

	// 以下是回调
	@Override
	public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
		// TODO Auto-generated method stub
		this.onBufferingUpdateListener=listener;

	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		// TODO Auto-generated method stub
		this.onCompletionListener=listener;
	}

	@Override
	public void setOnErrorListener(OnErrorListener listener) {
		// TODO Auto-generated method stub
		this.onErrorListener=listener;
	}

	@Override
	public void setOnInfoListener(OnInfoListener listener) {
		// TODO Auto-generated method stub
		this.onInfoListener=listener;
	}

	@Override
	public void setOnPreparedListener(OnPreparedListener listener) {
		// TODO Auto-generated method stub
		this.onPreparedListener=listener;
	}

	@Override
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
		// TODO Auto-generated method stub
		this.onSeekCompleteListener=listener;
	}

	@Override
	public void setOnVideoSizeChangedListener(
			OnVideoSizeChangedListener listener) {
		// TODO Auto-generated method stub
		this.onVideoSizeChangedListener=listener;
	}

	private HwCodecPlayerListener playerListener = new HwCodecPlayerListener() {

		@Override
		public void onVideoSizeChanged(int width, int height) {
			// TODO Auto-generated method stub
			onVideoSizeChangedListener.onVideoSizeChanged(UMediaPlayer2.this, width, height);
		}

		@Override
		public void onSeekComplete() {
			// TODO Auto-generated method stub
			onSeekCompleteListener.onSeekComplete(UMediaPlayer2.this);
		}

		@Override
		public void onError(int what, int extra) {
			// TODO Auto-generated method stub
			onErrorListener.onError(UMediaPlayer2.this, what, extra);
		}

		@Override
		public void onCompletion() {
			// TODO Auto-generated method stub
			onCompletionListener.onCompletion(UMediaPlayer2.this);
		}

		@Override
		public void onBufferingUpdate(int percent) {
			// TODO Auto-generated method stub
			onBufferingUpdateListener.onBufferingUpdate(UMediaPlayer2.this, percent);
		}

		@Override
		public void onPrepared() {
			// TODO Auto-generated method stub
			onPreparedListener.onPrepared(UMediaPlayer2.this);
		}

		@Override
		public void onInfo(int what, int extra) {
			// TODO Auto-generated method stub
			onInfoListener.onInfo(UMediaPlayer2.this, what, extra);
		}

		@Override
		public void onPlayStart() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPlayPause() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCurrentPositionUpdate(int currentPosition) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStartBuffering() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onEndBuffering() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNetworkSpeedChanged(int count) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTimeOut() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onVideoIndexUpdate(int currentIndex, int ip) {
			// TODO Auto-generated method stub
			
		}
	};
}
