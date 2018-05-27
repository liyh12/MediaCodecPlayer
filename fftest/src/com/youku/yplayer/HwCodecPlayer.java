package com.youku.yplayer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

public class HwCodecPlayer {
	// 播放器所有的状态
	// 状态切换时，主线程的操作，如start，stop可以使用set直接设置
	// 子线程，必须使用CompareAndSet，因为设置之前，值可能已经被主线程修改，要以主线程的修改为主。
	private static final int state_idle = 10;
	private static final int state_initialized = 11;
	private static final int state_preparing = 12;
	private static final int state_prepared = 13;
	private static final int state_started = 14;
	private static final int state_pausing = 15;
	private static final int state_paused = 16;
	private static final int state_stopping = 17;
	private static final int state_stopped = 18;
	private static final int state_completed = 19;
	private static final int state_error = 20;
	private static final int state_end = 21;
	private static final int state_seeking = 22;
	private static final int state_buffering = 23;

	private static final int packet_type_normal = 0;
	private static final int packet_type_restart = 1;
	private static final int packet_type_configcodec = 2;
	private static final int packet_type_changeindex = 3;
	// private static final int packet_type_eof = -1;

	private AtomicInteger CurrentState = new AtomicInteger(10);

	// TODO 只有音频，只有视频的情况，不支持的格式选择系统播放器
	// 用于seeking，pausing，stoping的变量
	// sleep只是暂停渲染，循环没有停止
	private boolean videoSleep = true;
	private boolean audioSleep = true;
	private boolean cacheSleep = false;
	// die终止渲染，并且跳出循环，线程执行完毕
	private boolean videoDie = true;
	private boolean audioDie = true;

	// 用于音视频同步
	private long lastPtsTime = 0;
	// private long lastPts = 0;
	private long lastDtsTime = 0;
	private long lastDts = 0;
	private long vdelay = 0;
	private long adelay = 0;
	private int framedelay = 0;
	private long lastvdelay = 0;
	// 用于seek，进度上报
	private long seekmsec = 0;
	private int currentPosition = 0;
	private long duration = 0;
	// 上一次上报进度
	private long lastreport = 0;

	// private FFPlayerListener ffPlayerListener;
	private FFExtractor currentExtractor;
	private HwVideoRender videoRender;
	private HwAudioRender audioRender;
	private Surface surface;
	private SurfaceHolder surfaceHolder;
	private int preinx = 0;
	private int currentinx = 0;
	private long[] durationArray;
	private List<FFExtractor> ffExtractors = new ArrayList<FFExtractor>();
	// ConcurrentLinkedQueue
	private ConcurrentLinkedQueue<AvPacket> videoQueue = new ConcurrentLinkedQueue<AvPacket>();
	private ConcurrentLinkedQueue<AvPacket> audioQueue = new ConcurrentLinkedQueue<AvPacket>();
	private Thread cacheThread, videoThread, audioThread, prepareThread;
	private FutureTask<Void> cacheTask;
	private FutureTask<Void> prepareTask;

	private AvPacketPool pool = new AvPacketPool(500);
	private AvPacket lastInCachePacket;
	private HwCodecPlayerListener playerListener;
	private int streamInx;
	private int width = 0, height = 0;

	public HwCodecPlayer() {
	}

	public void setSurface(Surface surface) {
		this.surface = surface;
	}

	public void setDisplay(SurfaceHolder surfaceHolder) {
		this.surface = surfaceHolder.getSurface();
		this.surfaceHolder = surfaceHolder;
	}

	public void setDataSource(List<String> filenames)
			throws IllegalStateException {
		if (CurrentState.compareAndSet(state_idle, state_initialized)) {
			for (String string : filenames) {
				FFExtractor ffexe = new FFExtractor();
				ffexe.setDataSource(string);
				ffExtractors.add(ffexe);
			}
			durationArray = new long[filenames.size() + 1];
			currentExtractor = ffExtractors.get(0);
			currentinx = 0;
		} else {
			throw new IllegalStateException("idle->setdatasource->init");
		}
	}

	public void prepare() throws IllegalStateException {
		if (CurrentState.compareAndSet(state_initialized, state_preparing)
				|| CurrentState.compareAndSet(state_stopped, state_preparing)) {
			durationArray[0] = 0l;
			for (int i = 0; i < ffExtractors.size(); i++) {
				if (ffExtractors.get(i).prep()) {
					android.util.Log.e("stop", "prepare:" + i);
					try {
						durationArray[i + 1] = durationArray[i]
								+ ffExtractors.get(i).getDuration();
					} catch (Exception e) {
						// TODO: handle exception
						CurrentState
								.compareAndSet(state_preparing, state_error);
						break;
					}

				} else {
					CurrentState.compareAndSet(state_preparing, state_error);
					break;
				}
			}
			if (CurrentState.get() == state_preparing) {
				duration = durationArray[durationArray.length - 1];
				videoDie = false;
				audioDie = false;
				framedelay = (int) (1000d / currentExtractor.getFrameRate());
				videoRender = new HwVideoRender(surface,
						currentExtractor.getWidth(),
						currentExtractor.getHeight(),
						(float) currentExtractor.getFrameRate());
				audioRender = new HwAudioRender(
						currentExtractor.getSampleRate(),
						currentExtractor.getAudioChannels());
				putFFExtraData();
				videoThread = new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						doVideoTask();
					}
				};
				audioThread = new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						doAudioTask();
					}
				};

				cacheTask = new FutureTask<>(new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						// TODO Auto-generated method stub
						doCacheTask();
						return null;
					}
				});
				cacheThread = new Thread(cacheTask);
				cacheThread.start();
				videoThread.start();
				audioThread.start();

			}
			if (CurrentState.compareAndSet(state_preparing, state_prepared)) {
				playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_PREPARED)
						.sendToTarget();
			} else if (CurrentState.get() == state_error) {
				playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_ERROR,
						MediaPlayer.MEDIA_ERROR_SERVER_DIED,
						MPPErrorCode.MEDIA_INFO_PREPARE_ERROR).sendToTarget();
			}

		} else {
			throw new IllegalStateException("init->prepare->preparing");
		}
	}

	public void prepareAsync() throws IllegalStateException {
		if (CurrentState.get() == state_initialized
				|| CurrentState.get() == state_stopped) {
			prepareTask = new FutureTask<>(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					// TODO Auto-generated method stub
					prepare();
					return null;
				}
			});
			prepareThread = new Thread(prepareTask);
			prepareThread.start();
		} else {
			throw new IllegalStateException("init->prepare->preparing");
		}
	}

	public void start() {
		// 从prepared状态到start 第一次start
		if (CurrentState.compareAndSet(state_prepared, state_started)
				|| CurrentState.compareAndSet(state_paused, state_started)) {
			// TODO onStart();
			cacheSleep = false;
			playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_PLAY_START)
					.sendToTarget();
			// currentExtractor.readPlay();
		}
	}

	public void pause() {
		CurrentState.compareAndSet(state_started, state_pausing);
		// currentExtractor.readPause();
	}

	public void seekTo(int msec) {
		if (CurrentState.get() == state_prepared
				|| CurrentState.get() == state_started
				|| CurrentState.get() == state_paused
				|| CurrentState.get() == state_completed
				|| CurrentState.get() == state_buffering) {
			cacheSleep = false;
			seekmsec = msec;
			notifyUpdatePosition(msec);
			CurrentState.getAndSet(state_seeking);
		}
	}

	// 停止，如果要播放，需要重新prepare
	//主要是把cachetask停掉
	public void stop() throws IllegalStateException {
		// TODO 还缺少error状态stop的，应该改成任意状态可以stop
		android.util.Log.e("stop", CurrentState.get() + "");
		if (CurrentState.compareAndSet(state_preparing, state_stopping)
				|| CurrentState.compareAndSet(state_prepared, state_stopping)
				|| CurrentState.compareAndSet(state_started, state_stopping)
				|| CurrentState.compareAndSet(state_paused, state_stopping)
				|| CurrentState.compareAndSet(state_completed, state_stopping)
				|| CurrentState.compareAndSet(state_pausing, state_stopping)
				|| CurrentState.compareAndSet(state_seeking, state_stopping)
				|| CurrentState.compareAndSet(state_buffering, state_stopping)
				|| CurrentState.compareAndSet(state_error, state_stopping)) {
			android.util.Log.e("stop", "stoping" + CurrentState.get() + "");
			try {
				for (FFExtractor ffExtractor : ffExtractors) {
					ffExtractor.stopread();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				if(prepareTask!=null){
					prepareTask.get();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				// 阻塞线程，等待cacheTask完成
				if (cacheTask != null) {
					cacheTask.get();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (CurrentState.compareAndSet(state_stopping, state_stopped)) {
					android.util.Log.e("stop", "stopped");
				} else {
					android.util.Log.e("stop", "stop_error");
					throw new IllegalStateException("stop");
				}
			}
		} else {
			throw new IllegalStateException("stop");
		}

	}

	// 如果要播放，需要重新setDataSource
	public void reset() {
		try {
			stop();
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (videoRender != null) {
			videoRender.release();
		}
		if (audioRender != null) {
			audioRender.release();
		}
		if (ffExtractors != null) {
			for (FFExtractor ffExtractor : ffExtractors) {
				ffExtractor.relse();
			}
		}
		if (audioThread != null) {
			audioThread.interrupt();
			audioThread = null;
		}
		if (videoThread != null) {
			videoThread.interrupt();
			videoThread = null;
		}
		if (cacheThread != null) {
			cacheThread.interrupt();
			cacheThread = null;
		}
		if (cacheTask != null) {
			cacheTask.cancel(true);
			cacheTask = null;
		}
		if (prepareThread != null) {
			prepareThread.interrupt();
			prepareThread = null;
		}
		if (prepareTask != null) {
			prepareTask.cancel(true);
			prepareTask = null;
		}

		flushCache();

		CurrentState.set(state_idle);
		android.util.Log.e("stop", "reset");
	}

	//彻底结束，不能恢复，需要重新new一个player
	public void release() {
		reset();
		// TODO 释放监听
		playerListener = null;
		playerHandler = null;
		pool.release();
		pool = null;
		videoQueue = null;
		audioQueue = null;
		ffExtractors.clear();
		ffExtractors = null;
		CurrentState.set(state_end);
		android.util.Log.e("stop", "release");
	}

	private void doVideoTask() {
		VideoLoop: for (;;) {
			switch (CurrentState.get()) {
			case state_started:
				videoSleep = false;
				if (!videoQueue.isEmpty()) {
					AvPacket avPacket = videoQueue.peek();
					switch (avPacket.getType()) {
					case packet_type_normal:
						if (videoRender.canputdata()) {
							makeVideoDelay(avPacket);
							videoQueue.poll();
							videoRender.putData(avPacket);
							avPacket.setBusy(false);
						}
						break;
					case packet_type_restart:
						notifyChangeVideoSize(avPacket.getWidth(),
								avPacket.getHeight());
						if (surface.isValid()) {
							// TODO 超时判断
							videoRender.restart(surface, avPacket.getWidth(),
									avPacket.getHeight(),
									(float) avPacket.getFrameRate());
							avPacket.setType(packet_type_configcodec);
						}
						break;
					case packet_type_configcodec:
						if (videoRender.canputdata()) {
							videoQueue.poll();
							videoRender.configCodec(avPacket.getData());
							avPacket.setBusy(false);
						}
						// TODO notify size change
						break;
					case packet_type_changeindex:
						playerHandler.obtainMessage(
								HwCodecPlayerMsg.MEDIA_VIDEOINDEX,
								avPacket.getIndex(), ffExtractors.size())
								.sendToTarget();
						videoQueue.poll();
						avPacket.setBusy(false);
						break;
					default:
						break;
					}
					if (surface.isValid()) {
						videoRender.tryRender();
					}
				} else {
					if (cacheSleep) {
						if (CurrentState.compareAndSet(state_started,
								state_completed)) {
							playerHandler.obtainMessage(
									HwCodecPlayerMsg.MEDIA_COMPLETE)
									.sendToTarget();
						}
					} else {
						if (CurrentState.compareAndSet(state_started,
								state_buffering)) {
							playerHandler.obtainMessage(
									HwCodecPlayerMsg.MEDIA_STARTBUFFERING)
									.sendToTarget();
						}
					}
				}

				break;
			case state_stopping:
				break VideoLoop;
			case state_error:
				break VideoLoop; 
			default:
				videoSleep = true;
				break;
			}
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		videoDie = true;
	}

	private void doAudioTask() {
		AudioLoop: for (;;) {
			switch (CurrentState.get()) {
			case state_started:
				audioSleep = false;
				if (!audioQueue.isEmpty()) {
					AvPacket avPacket = audioQueue.peek();
					switch (avPacket.getType()) {
					case packet_type_normal:
						if (audioRender.canputdata()) {
							makeAudioDelay(avPacket);

							audioQueue.poll();
							audioRender.putData(avPacket);
							avPacket.setBusy(false);
						}
						break;
					case packet_type_restart:
						audioRender.restart(avPacket.getSampleRate(),
								avPacket.getAudioChannels());
						avPacket.setType(packet_type_configcodec);
						break;
					case packet_type_configcodec:
						if (audioRender.canputdata()) {
							audioQueue.poll();
							audioRender.configCodec(avPacket.getData());
							avPacket.setBusy(false);
						}
						break;
					default:
						break;
					}
					if (audioRender.canrender()) {
						if (adelay < -100) {
							audioRender.render(false);
						} else {
							audioRender.render(true);
						}

					}
				}
				break;
			case state_stopping:
				break AudioLoop;
			case state_error:
				break AudioLoop; 
			default:
				audioSleep = true;
				break;
			}
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		audioDie = true;
	}

	private void doCacheTask() {
		// 循环标志位，用于在内部跳出外部指定的循环
		CacheLoop: for (;;) {
			switch (CurrentState.get()) {
			case state_pausing:
				if (videoSleep && audioSleep) {
					if (CurrentState.compareAndSet(state_pausing, state_paused)) {
						// TODO 这样才是真的停了
						playerHandler.obtainMessage(
								HwCodecPlayerMsg.MEDIA_PLAY_PAUSE)
								.sendToTarget();
					}
				}
				break;
			case state_seeking:
				if (videoSleep && audioSleep) {
					preinx = currentinx;
					currentinx = getSeekInx(seekmsec);
					flushCache();
					currentExtractor = ffExtractors.get(currentinx);
					currentExtractor.seek(getRealSeek(seekmsec, currentinx));
					currentExtractor.setHasRead(true);
					if (currentinx + 1 < ffExtractors.size()) {
						ffExtractors.get(currentinx + 1).seek(-1);
						currentExtractor.setHasRead(true);
					}

					AvPacket v = pool.getAvPacket();
					v.setType(packet_type_changeindex);
					v.setIndex(currentinx);
					if (!videoQueue.offer(v)) {
						v.setBusy(false);
						if (CurrentState.compareAndSet(state_seeking,
								state_error)) {
							playerHandler.obtainMessage(
									HwCodecPlayerMsg.MEDIA_ERROR,
									MediaPlayer.MEDIA_ERROR_SERVER_DIED,
									MPPErrorCode.MEDIA_INFO_QUEUE_ERROR)
									.sendToTarget();
						}
					}

					// TODO FIX
					if (checkExtraData(preinx, currentinx)) {
						android.util.Log.e("eof", "putextra");
						putFFExtraData();
					} else {
						android.util.Log.e("eof", "dont putextra");
					}
					videoRender.flush();
					audioRender.flush();
					lastDts = 0;
					lastDtsTime = 0;
					playerHandler.obtainMessage(
							HwCodecPlayerMsg.MEDIA_SEEK_COMPLETE)
							.sendToTarget();

					if (CurrentState
							.compareAndSet(state_seeking, state_started)) {
						playerHandler.obtainMessage(
								HwCodecPlayerMsg.MEDIA_PLAY_START)
								.sendToTarget();
						// OnStart();
					}

				}
				break;
			case state_stopping:
				if (videoDie && audioDie) {
					android.util.Log.e("stop", "cachedie");
					break CacheLoop;
				}
				break;
			case state_completed:
				break;
			case state_buffering:
				cacheMain();
				if (audioQueue.size() > 150 && videoQueue.size() > 150) {
					if (CurrentState.compareAndSet(state_buffering,
							state_started)) {
						playerHandler.obtainMessage(
								HwCodecPlayerMsg.MEDIA_ENDBUFFERING)
								.sendToTarget();
					}
				}
				break;
			case state_error:
				break CacheLoop;
			default:
				cacheMain();
				break;
			}
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void cacheMain() {
		long curtime = SystemClock.elapsedRealtime();
		if (curtime - lastreport > 1000) {
			// Fix me
			playerHandler.obtainMessage(
					HwCodecPlayerMsg.MEDIA_BUFFERING_UPDATE, getRealPercent(),
					0).sendToTarget();
			lastreport = curtime;
		}
		streamInx = currentExtractor.prereadNextPacket();
		if (streamInx == currentExtractor.getStreamAudioInx()) {
			if (audioQueue.size() < 200) {
				int a = currentExtractor.hasError();
				AvPacket poolPacket = pool.getAvPacket();
				currentExtractor.getReadPacket2().copyTo(poolPacket);
				poolPacket.setType(packet_type_normal);
				poolPacket.setDts(poolPacket.getDts()
						+ durationArray[currentinx]);
				poolPacket.setPts(poolPacket.getPts()
						+ durationArray[currentinx]);
				if (!audioQueue.offer(poolPacket)) {
					poolPacket.setBusy(false);
				} else {
					lastInCachePacket = poolPacket;
				}
			}
		} else if (streamInx == currentExtractor.getStreamVideoInx()) {
			if (videoQueue.size() < 200) {
				AvPacket poolPacket = pool.getAvPacket();
				currentExtractor.getReadPacket2().copyTo(poolPacket);
				poolPacket.setType(packet_type_normal);

				poolPacket.setDts(poolPacket.getDts()
						+ durationArray[currentinx]);
				poolPacket.setPts(poolPacket.getPts()
						+ durationArray[currentinx]);
				if (!videoQueue.offer(poolPacket)) {
					poolPacket.setBusy(false);
				} else {
					lastInCachePacket = poolPacket;
				}
			}
		} else {
			android.util.Log.e("eof", currentExtractor.hasError() + ","
					+ streamInx + "," + currentExtractor.getBufferPos() + ","
					+ currentExtractor.getFileLength());

			if (currentExtractor.getBufferPos() < currentExtractor
					.getFileLength()) {
				if (lastInCachePacket == null) {
					CurrentState.set(state_error);
					playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_ERROR,
							MediaPlayer.MEDIA_ERROR_SERVER_DIED,
							MPPErrorCode.MEDIA_INFO_NETWORK_CHECK)
							.sendToTarget();
				} else {
					sekandread();
				}

			} else {
				if (currentinx + 1 < ffExtractors.size()) {
					currentinx += 1;
					currentExtractor = ffExtractors.get(currentinx);
					framedelay = (int) (1000d / currentExtractor.getFrameRate());
					currentExtractor.seek(-1);
					currentExtractor.setHasRead(true);

					AvPacket v = pool.getAvPacket();
					v.setType(packet_type_changeindex);
					v.setIndex(currentinx);
					if (!videoQueue.offer(v)) {
						v.setBusy(false);
						playerHandler.obtainMessage(
								HwCodecPlayerMsg.MEDIA_ERROR,
								MediaPlayer.MEDIA_ERROR_SERVER_DIED,
								MPPErrorCode.MEDIA_INFO_QUEUE_ERROR)
								.sendToTarget();
						CurrentState.set(state_error);
					}
					if (checkExtraData(currentinx - 1, currentinx)) {
						putFFExtraData();
					}
					// inx，videosize，extradata

				} else {
					if (!cacheSleep) {
						cacheSleep = true;
					}
				}
			}
		}
	}

	/**
	 * 写入codecConfigData
	 */
	private void putFFExtraData() {
		AvPacket v = pool.getAvPacket();
		AvPacket a = pool.getAvPacket();
		ByteBuffer videoExtraData = currentExtractor.getVideoExtraData();
		ByteBuffer audioExtraData = currentExtractor.getAudioExtraData();

		if (videoExtraData != null) {
			v.setType(packet_type_restart);
			v.copyData(videoExtraData);
			v.setWidth(currentExtractor.getWidth());
			v.setHeight(currentExtractor.getHeight());
			v.setFrameRate(currentExtractor.getFrameRate());
			videoQueue.offer(v);
		} else {
			v.setBusy(false);
			playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_ERROR,
					MediaPlayer.MEDIA_ERROR_SERVER_DIED,
					MPPErrorCode.MEDIA_INFO_DATA_SOURCE_ERROR).sendToTarget();
			CurrentState.set(state_error);
			// 这里应该抛出error
		}
		if (audioExtraData != null) {
			a.setType(packet_type_restart);
			a.copyData(audioExtraData);
			a.setSampleRate(currentExtractor.getSampleRate());
			a.setAudioChannels(currentExtractor.getAudioChannels());
			audioQueue.offer(a);
		} else {
			a.setBusy(false);
			playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_ERROR,
					MediaPlayer.MEDIA_ERROR_SERVER_DIED,
					MPPErrorCode.MEDIA_INFO_DATA_SOURCE_ERROR).sendToTarget();
			CurrentState.set(state_error);
			// 这里应该抛出error
		}
	}

	// TODO buffering 的时候，seek对他的影响
	private void sekandread() {
		// TODO 把因为超时，断网，中断的播放流续起来
		if (CurrentState.get() == state_seeking
				|| CurrentState.get() == state_stopping) {
			return;
		}
		long starttime = SystemClock.elapsedRealtime();
		long realdts = lastInCachePacket.getDts() - durationArray[currentinx];
		int backtime = 10;
		int readtimes = 0;
		currentExtractor.seek(realdts - backtime);
		currentExtractor.setHasRead(true);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (;;) {
			if (CurrentState.get() == state_seeking
					|| CurrentState.get() == state_stopping) {
				break;
			}
			if (SystemClock.elapsedRealtime() - starttime > 20000) {
				CurrentState.set(state_error);
				playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_ERROR,
						MediaPlayer.MEDIA_ERROR_SERVER_DIED,
						MPPErrorCode.MEDIA_INFO_NETWORK_CHECK).sendToTarget();
				android.util.Log.e("eof", "error_timeout");
				break;
			}
			streamInx = currentExtractor.prereadNextPacket();
			readtimes++;
			android.util.Log.e("eof", readtimes + "");
			if (streamInx < 0) {
				android.util.Log.e("eof", currentExtractor.hasError() + ","
						+ streamInx + "," + currentExtractor.getBufferPos()
						+ "," + currentExtractor.getFileLength());
				if (currentExtractor.getRetrytimes() > 10) {
					CurrentState.set(state_error);
					playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_ERROR,
							MediaPlayer.MEDIA_ERROR_SERVER_DIED,
							MPPErrorCode.MEDIA_INFO_NETWORK_CHECK)
							.sendToTarget();
					android.util.Log.e("eof", "error_always_error");
					break;
				} else {
					backtime += 2000;
					currentExtractor.addRetrytimes(1);
					currentExtractor.seek(realdts - backtime);
					currentExtractor.setHasRead(true);
					readtimes = 0;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				AvPacket avPacket = currentExtractor.getReadPacket2();
				if (avPacket.getDts() > realdts) {
					android.util.Log.e("eof", "now:" + avPacket.getDts()
							+ ",target:" + realdts + ",back:" + backtime);
					if (readtimes == 1) {
						backtime += 600;
						currentExtractor.seek(realdts - backtime);
						currentExtractor.setHasRead(true);
						readtimes = 0;
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						// CurrentState.set(state_error);
						// playerHandler.obtainMessage(
						// HwCodecPlayerMsg.MEDIA_ERROR,
						// MediaPlayer.MEDIA_ERROR_SERVER_DIED,
						// MPPErrorCode.MEDIA_INFO_NETWORK_CHECK)
						// .sendToTarget();
						// android.util.Log.e("eof", "error_bigger");
						break;
					}
				} else {
					if (avPacket.getDts() == realdts
							&& streamInx == lastInCachePacket.getStreamInx()
							&& avPacket.getData().limit() == lastInCachePacket
									.getData().limit()) {
						android.util.Log.e("eof", "reseek_success");
						break;
					} else {
						android.util.Log.e("eof", "reading");
					}
				}
			}

		}

	}

	private void notifyChangeVideoSize(int newwidth, int newheight) {
		if (newwidth != width || newheight != height) {
			if (surface.isValid()) {
				surface.release();
			}
			width = newwidth;
			height = newheight;
			playerHandler.obtainMessage(
					HwCodecPlayerMsg.MEDIA_VIDEO_SIZE_CHANGE, width, height)
					.sendToTarget();
			int delaytime = 0;
			try {
				Thread.sleep(100);
				for (;;) {
					if (surface.isValid()) {
						break;
					} else {
						if (delaytime > 1000) {
							break;
						}
						Thread.sleep(100);
						delaytime += 100;
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void notifyUpdatePosition(int msec) {
		if (CurrentState.get() == state_started) {
			playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_POSITION_UPDATE,
					msec, (int) getDuration()).sendToTarget();
		}
	}

	private void makeVideoDelay(AvPacket avPacket) {
		if (lastDtsTime == 0) {
			lastDtsTime = SystemClock.elapsedRealtime();
			lastDts = avPacket.getDts();
		} else {
			vdelay = avPacket.getDts() - lastDts
					- (SystemClock.elapsedRealtime() - lastDtsTime);

			if (vdelay > 0) {
				if (vdelay < 2 * framedelay) {
					lastvdelay = vdelay;
					try {
						Thread.sleep(vdelay);
					} catch (Exception e) {
						// TODO: handle exception
					}
				} else {
					Log.e("keyframe", "v:longdelay");
					try {
						Thread.sleep(lastvdelay);
					} catch (Exception e) {
						// TODO: handle exception
					}
					lastDtsTime = SystemClock.elapsedRealtime();
					lastDts = avPacket.getDts();
				}
			} else if (vdelay < -10) {
				// video的delay小于0，需要校准lastDts
				lastDtsTime = SystemClock.elapsedRealtime();
				lastDts = avPacket.getDts();
			}
			Log.e("keyframe", "v:" + vdelay);
		}
		if (CurrentState.get() == state_started) {
			currentPosition = (int) avPacket.getDts();
			notifyUpdatePosition(currentPosition);
		}
	}

	private void makeAudioDelay(AvPacket avPacket) {
		if (lastDtsTime == 0) {
			lastDtsTime = SystemClock.elapsedRealtime();
			lastDts = avPacket.getDts();
		} else {
			adelay = avPacket.getDts() - lastDts
					- (SystemClock.elapsedRealtime() - lastDtsTime);
			if (adelay > 0) {
				if (adelay < 0.5 * framedelay) {
					try {
						Thread.sleep(adelay);
					} catch (Exception e) {
						// TODO: handle exception
					}
				} else {
					Log.e("keyframe", "a:longdelay");
					lastDtsTime = SystemClock.elapsedRealtime();
					lastDts = avPacket.getDts();
				}
			}
			Log.e("keyframe", "a:" + adelay);
		}
	}

	private boolean checkExtraData(int formInx, int ToInx) {
		if ((!videoRender.isHasConfigCodec())
				|| (!audioRender.isHasConfigCodec())) {
			return true;
		}
		if (formInx == ToInx) {
			return false;
		}
		ByteBuffer videoExtraData = ffExtractors.get(ToInx).getVideoExtraData();
		ByteBuffer audioExtraData = ffExtractors.get(ToInx).getAudioExtraData();
		if (videoExtraData != null) {
			videoExtraData.position(0);
		} else {
			playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_ERROR,
					MediaPlayer.MEDIA_ERROR_SERVER_DIED,
					MPPErrorCode.MEDIA_INFO_DATA_SOURCE_ERROR).sendToTarget();
			CurrentState.set(state_error);
			// 这里应该抛出error
			return false;
		}
		if (audioExtraData != null) {
			audioExtraData.position(0);
		} else {
			playerHandler.obtainMessage(HwCodecPlayerMsg.MEDIA_ERROR,
					MediaPlayer.MEDIA_ERROR_SERVER_DIED,
					MPPErrorCode.MEDIA_INFO_DATA_SOURCE_ERROR).sendToTarget();
			CurrentState.set(state_error);
			// 这里应该抛出error
			return false;
		}
		ByteBuffer videoExtraData1 = ffExtractors.get(formInx)
				.getVideoExtraData();
		ByteBuffer audioExtraData1 = ffExtractors.get(formInx)
				.getAudioExtraData();
		videoExtraData1.position(0);
		audioExtraData1.position(0);
		// TODO 这个判断方式+1-1也可以等于0 有漏洞
		if (videoExtraData.compareTo(videoExtraData1) == 0
				&& audioExtraData.compareTo(audioExtraData1) == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 清空残留的缓存数据
	 */
	private void flushCache() {
		for (AvPacket vpack : videoQueue) {
			vpack.setBusy(false);
		}
		for (AvPacket apack : audioQueue) {
			apack.setBusy(false);
		}
		videoQueue.clear();
		audioQueue.clear();
		lastInCachePacket = null;
	}

	/**
	 * 获取seek到的extractor
	 * 
	 * @param msec
	 * @return
	 */
	private int getSeekInx(long msec) {
		for (int i = 0; i < durationArray.length; i++) {
			if (msec < durationArray[i]) {
				return i - 1;
			}
		}
		return 0;
	}

	/**
	 * 获取应该seek多少
	 * 
	 * @param msec
	 * @param inx
	 * @return
	 */
	private long getRealSeek(long msec, int inx) {
		if (inx > 0) {
			return msec - durationArray[inx];
		}
		return msec;
	}

	private int getRealPercent() {
		double time = currentPosition;
		double currentbuffertime = currentExtractor.getBufferPercent()
				* currentExtractor.getDuration() / 100d;
		double lastcompletetime = durationArray[currentinx];
		double realbuffertime = currentbuffertime + lastcompletetime;
		double p = realbuffertime / duration;
		android.util.Log.e("keyframe", p * 100 + "%");
		return (int) (p * 100);

		// return (int) ((((double) currentExtractor.getBufferPercent() / 100d)
		// * currentExtractor.getDuration() + durationArray[currentinx]) * 100 /
		// duration);
	}

	public FFExtractor getCurrentExtractor() {
		return currentExtractor;
	}

	public Surface getSurface() {
		return surface;
	}

	public int getCurrentPosition() {
		return (int) currentPosition;
	}

	public int getDuration() {
		return (int) duration;
	}

	public boolean isPlaying() {
		return (CurrentState.get() == state_started);
	}

	public int getVideoHeight() {
		return currentExtractor.getHeight();
	}

	public int getVideoWidth() {
		return currentExtractor.getWidth();
	}

	public void setVolume(float leftVolume, float rightVolume) {
		// TODO 获取audioTrack并设置音量

	}

	/**
	 * 初始化时设置的是surfaceHolder，这个函数才有用
	 * 
	 * @param screenOn
	 */
	public void setScreenOnWhilePlaying(boolean screenOn) {
		// TODO Auto-generated method stub
		if (surfaceHolder != null) {
			surfaceHolder.setKeepScreenOn(screenOn);
		}
	}

	public void setPlayerListener(HwCodecPlayerListener playerListener) {
		this.playerListener = playerListener;
	}

	private Handler playerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (playerListener != null) {
				switch (msg.what) {
				case HwCodecPlayerMsg.MEDIA_VIDEO_SIZE_CHANGE:
					playerListener.onVideoSizeChanged(msg.arg1, msg.arg2);
					break;
				case HwCodecPlayerMsg.MEDIA_SEEK_COMPLETE:
					playerListener.onSeekComplete();
					break;
				case HwCodecPlayerMsg.MEDIA_ERROR:
					playerListener.onError(msg.arg1, msg.arg2);
					break;
				case HwCodecPlayerMsg.MEDIA_COMPLETE:
					playerListener.onCompletion();
					break;
				case HwCodecPlayerMsg.MEDIA_BUFFERING_UPDATE:
					playerListener.onBufferingUpdate(msg.arg1);
					break;
				case HwCodecPlayerMsg.MEDIA_PREPARED:
					playerListener.onPrepared();
					break;
				case HwCodecPlayerMsg.MEDIA_INFO:
					playerListener.onInfo(msg.arg1, msg.arg2);
					break;
				case HwCodecPlayerMsg.MEDIA_PLAY_START:
					playerListener.onPlayStart();
					break;
				case HwCodecPlayerMsg.MEDIA_PLAY_PAUSE:
					playerListener.onPlayPause();
					break;
				case HwCodecPlayerMsg.MEDIA_POSITION_UPDATE:
					playerListener.onCurrentPositionUpdate(msg.arg1);
					break;
				case HwCodecPlayerMsg.MEDIA_STARTBUFFERING:
					playerListener.onStartBuffering();
					break;
				case HwCodecPlayerMsg.MEDIA_ENDBUFFERING:
					playerListener.onEndBuffering();
					break;
				case HwCodecPlayerMsg.MEDIA_NETWORKSPEED:
					playerListener.onNetworkSpeedChanged(msg.arg1);
					break;
				case HwCodecPlayerMsg.MEDIA_TIMEOUT:
					playerListener.onTimeOut();
					break;
				case HwCodecPlayerMsg.MEDIA_VIDEOINDEX:
					playerListener.onVideoIndexUpdate(msg.arg1, msg.arg2);
					break;
				default:
					break;
				}
			}
			super.handleMessage(msg);
		}
	};
}
