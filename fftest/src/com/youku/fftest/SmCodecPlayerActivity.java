package com.youku.fftest;

import java.util.ArrayList;
import java.util.List;

import com.youku.yplayer.FFPlayerListener;
import com.youku.yplayer.HwCodecPlayer;
import com.youku.yplayer.HwCodecPlayerListener;
import com.youku.yplayer.StringUtil;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SmCodecPlayerActivity extends Activity {

	private HwCodecPlayer codecTest1;
	private SurfaceView surfaceView1;
	private SeekBar seekBar1;
	private TextView seektime1, seektime2;
	private Button btn1, btn2, btn3, btn4, btn5, btn6;
	// private static final String videoPath1 = Environment
	// .getExternalStorageDirectory() + "/test1080.mp4";
	// private static final String videoPath1 = Environment
	// .getExternalStorageDirectory() + "/mcodec/sh06.flv";
	// private static final String videoPath2 = Environment
	// .getExternalStorageDirectory() + "/mcodec/sh07.flv";
	// private static final String videoPath1 =
	// "http://7xploe.media1.z0.glb.clouddn.com/a1.flv";
//	private static final String videoPath1 = "http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/p1_185.flv";

	private static final String videoPath1 = "http://7xploe.media1.z0.glb.clouddn.com/sgyy010101.mp4";
	// private static final String videoPath1 =
	// "http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd1/p0_352.mp4";

	// private static final String videoPath1 =
	// "http://7xploe.media1.z0.glb.clouddn.com/lyb_sd_01_374.flv";

	private static final String videoPath2 = "http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/p2_126.flv";

	// private static final String
	// videoPath1="http://7xploe.media1.z0.glb.clouddn.com/lyb265.mp4";
	// private static final String videoPath2 =
	// "http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/ad1_15.flv";
	// private static final String
	// videoPath1="http://7xploc.media1.z0.glb.clouddn.com/video_hls_1000k";
	// private static final String
	// videoPath1="http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8";

	// private static final String videoPath2 =
	// "http://7xplob.media1.z0.glb.clouddn.com/RoZOf31vAzHdqp_TwmzDSEtjz6w=/lv8HvGCAzqXriVDmCUBJezhN8jQB/000001.ts";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sm_codec_player);
		surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn3 = (Button) findViewById(R.id.btn3);
		btn4 = (Button) findViewById(R.id.btn4);
		btn5 = (Button) findViewById(R.id.btn5);
		btn6 = (Button) findViewById(R.id.btn6);
		seekBar1 = (SeekBar) findViewById(R.id.seek1);
		seektime1 = (TextView) findViewById(R.id.seektime1);
		seektime2 = (TextView) findViewById(R.id.seektime2);
		codecTest1 = new HwCodecPlayer();
		codecTest1.setPlayerListener(new HwCodecPlayerListener() {
			
			@Override
			public void onVideoSizeChanged(int width, int height) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onVideoIndexUpdate(int currentIndex, int ip) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onTimeOut() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartBuffering() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSeekComplete() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPrepared() {
				// TODO Auto-generated method stub
				seektime2.setText(StringUtil.secToTime((int) (codecTest1
						.getDuration() / 1000)));
				codecTest1.start();
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
			public void onNetworkSpeedChanged(int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onInfo(int what, int extra) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(int what, int extra) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEndBuffering() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCurrentPositionUpdate(int currentPosition) {
				// TODO Auto-generated method stub
				if (!seekBar1.isPressed()) {
					seektime1.setText(StringUtil.secToTime(currentPosition / 1000));
					seekBar1.setProgress((int) ((double) currentPosition * 100 / (double) codecTest1.getDuration()));
				}
			}
			
			@Override
			public void onCompletion() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onBufferingUpdate(int percent) {
				// TODO Auto-generated method stub
				
			}
		});
		seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private long p;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				codecTest1.seekTo((int) p);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (fromUser) {
					this.p = progress * codecTest1.getDuration()
							/ seekBar.getMax();// 用于播放中定位
				}

			}
		});
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<String> filepaths = new ArrayList<String>();
				filepaths.add(videoPath1);
				filepaths.add(videoPath2);
				codecTest1.setDataSource(filepaths);
				codecTest1.prepareAsync();
			}
		});
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// codecTest1.pauseCache();

			}
		});
		btn4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// codecTest1.resumeCache();
			}
		});
		btn5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				codecTest1.pause();
			}
		});
		btn6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				codecTest1.start();
			}
		});
		surfaceView1.getHolder().addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				codecTest1.stop();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				codecTest1.setSurface(holder.getSurface());
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub

			}
		});
	}
}
