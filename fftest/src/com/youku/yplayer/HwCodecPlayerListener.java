package com.youku.yplayer;

public interface HwCodecPlayerListener {
	// 7个系统播放器必备回调
	public void onVideoSizeChanged( int width, int height);

	public void onSeekComplete();

	public void onError( int what, int extra);

	public void onCompletion();

	public void onBufferingUpdate( int percent);

	public void onPrepared();

	public void onInfo( int what, int extra);

	/**
	 * 开始播放 主要用于播放按钮状态的切换
	 */
	public void onPlayStart();

	/**
	 * 暂停 主要用于播放按钮状态的切换
	 */
	public void onPlayPause();

	/**
	 * 播放位置变化
	 * 
	 * @param currentPosition
	 *            当前播放位置，毫秒
	 */
	public void onCurrentPositionUpdate(int currentPosition);


	/**
	 * 视频缓冲开始
	 */
	public void onStartBuffering();

	/**
	 * 视频缓冲结束
	 */
	public void onEndBuffering();

	/**
	 * 网速更新
	 * 
	 * @param count
	 *            单位：kb/s
	 */
	public void onNetworkSpeedChanged(int count);

	/**
	 * 注意播放器播放过程中的timeout与获取信息时的timeout是不一样的
	 */
	public void onTimeOut();

	/**
	 * 播放位置变化
	 * 
	 * @param currentIndex
	 *            分片索引
	 * @param ip
	 *            ？
	 */
	public void onVideoIndexUpdate(int currentIndex, int ip);
}
