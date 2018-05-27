package com.youku.yplayer;

/**
 * 错误信息
 * 
 * @author dell
 * 
 */
public class MPPErrorCode {
	// 播放错误
	public static final int MEDIA_INFO_PLAYERROR = 1002;
	// 网络连接失败
	public static final int MEDIA_INFO_NETWORK_DISSCONNECTED = 1005;
	// 数据源错误
	public static final int MEDIA_INFO_DATA_SOURCE_ERROR = 1006;
	// 播放器准备失败
	public static final int MEDIA_INFO_PREPARE_ERROR = 1007;
	// 网络出错
	public static final int MEDIA_INFO_NETWORK_ERROR = 1008;
	// 搜索出错
	public static final int MEDIA_INFO_SEEK_ERROR = 1009;
	// 缓存队列出错
	public static final int MEDIA_INFO_QUEUE_ERROR = 1011;
	
	// 播放20秒播放点不动
	public static final int MEDIA_INFO_NETWORK_CHECK = 2004;
	// 播放广告时播放器准备出错
	public static final int MEDIA_INFO_PREPARED_AD_CHECK = 2005;
	// 准备超时
	public static final int MEDIA_INFO_PREPARE_TIMEOUT_ERROR = 1010;
	// 其他错误
	public static final int MEDIA_INFO_PLAY_UNKNOW_ERROR = 1;
}
