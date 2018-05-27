package com.youku.yplayer;

public interface AvPacketCacheListener {

	/**
	 * 开始seek
	 */
	public void onStartSeek();
	/**
	 * 结束seek
	 */
	public void onEndSeek();
	/**
	 * 视频缓冲开始
	 */
	public void onStartBuffering();

	/**
	 * 视频缓冲结束
	 */
	public void onEndBuffering();
}
