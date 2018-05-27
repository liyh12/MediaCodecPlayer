package com.youku.yplayer;

import android.media.MediaPlayer;

public abstract class FFPlayerListener {
	// 7个系统播放器必备回调
	public void onVideoSizeChanged( int width, int height) {
	}

	public void onSeekComplete() {
	}

	public void onError( int what, int extra) {
	}

	public void onCompletion() {
	}

	public void onBufferingUpdate( int percent) {
	}

	public void onPrepared() {
	}

	public void onSetDataSource(){
		
	}

	public void onInfo( int what, int extra) {

	}

	// 我给播放器添加的回调
	/**
	 * 
	 * @param position
	 *            当前ms
	 * @param duration
	 *            总ms
	 */
	public void onCurrentPositionUpdate(int position, int duration) {

	}

	/**
	 * 开始播放 主要用于播放按钮状态的切换
	 */
	public void onPlayStart() {

	}

	/**
	 * 暂停 主要用于播放按钮状态的切换
	 */
	public void onPlayPause() {

	}

	// 优酷播放器特有回调

	/**
	 * 广告倒计时更新
	 * 
	 * @param count
	 *            秒数
	 */
	public void onAdCountUpdate(int count) {
	}

	/**
	 * 开始播放广告
	 * 
	 * @param index
	 *            第几个广告
	 */
	public void onAdStartPlay(int index) {
	}

	/**
	 * 结束播放广告
	 * 
	 * @param index
	 *            第几个广告
	 */
	public void onAdEndPlay(int index) {
	}

	/**
	 * 播放位置变化
	 * 
	 * @param currentPosition
	 *            当前播放位置，毫秒
	 */
	public void onCurrentPositionUpdate(int currentPosition) {
	}

	/**
	 * 硬解出错 TODO 是不是要添加错误码之类的信息？
	 */
	public void onHwDecodeError() {
	}

	/**
	 * 视频缓冲开始
	 */
	public void onStartBuffering() {
	}

	/**
	 * 视频缓冲结束
	 */
	public void onEndBuffering() {
	}

	/**
	 * 网速更新
	 * 
	 * @param count
	 *            单位：kb/s
	 */
	public void onNetworkSpeedChanged(int count) {
	}

	/**
	 * 正片开始播放
	 * 
	 */
	public void onRealVideoStart() {
	}

	/**
	 * 注意播放器播放过程中的timeout与获取信息时的timeout是不一样的
	 */
	public void onTimeOut() {
	}

	/**
	 * 切换清晰度 //TODO 是不是要添加之前和之后的清晰度参数？
	 */
	public void onNotifyChangeVideoQuality() {
	}

	/**
	 * 播放位置变化
	 * 
	 * @param currentIndex
	 *            分片索引
	 * @param ip
	 *            ？
	 */
	public void onVideoIndexUpdate(int currentIndex, int ip) {
	}
}
