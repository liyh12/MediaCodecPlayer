package com.youku.fftest;

import java.util.ArrayList;
import java.util.List;

import com.youku.player.util.ScreenUtil;
import com.youku.yplayer.FFPlayerListener;
import com.youku.yplayer.HwCodecPlayer;
import com.youku.yplayer.HwCodecPlayerListener;
import com.youku.yplayer.StringUtil;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.app.Activity;
import android.content.res.Configuration;

public class PlayerActivity extends Activity {
	private HwCodecPlayer hwCodecPlayer;
	private SurfaceView surfaceView1;
	private SeekBar seekBar1;
	private TextView seektime1, seektime2;
	private Button btn1, btn2, btn3, btn4, btn5, btn6;
	private ProgressBar bar1;
	private RelativeLayout playerView;
	 private static final String videoPath3 = Environment
	 .getExternalStorageDirectory() + "/mcodec/sh06.flv";
	 private static final String videoPath5 = Environment
	 .getExternalStorageDirectory() + "/mcodec/sh07.flv";

	 private static final String videoPath4 =
	 "http://7xploe.media1.z0.glb.clouddn.com/testcrash.mp4";
	private static final String videoPath1 = "http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/p1_185.flv";
	private static final String videoPath2 = "http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/p2_126.flv";
	 
//		private static final String videoPath1 = Environment
//				 .getExternalStorageDirectory() + "/cloud/XMTM0Mzg5MDk2OA==/0";
//		private static final String videoPath2 = Environment
//				 .getExternalStorageDirectory() + "/cloud/XMTM0Mzg5MDk2OA==/1";
//		private static final String videoPath1 = Environment
//				 .getExternalStorageDirectory() + "/cloud/XMTM0MzE3NTAyNA==/0";
//		private static final String videoPath2 = Environment
//				 .getExternalStorageDirectory() + "/cloud/XMTM0MzE3NTAyNA==/1";
//		private static final String videoPath1 = Environment
//				 .getExternalStorageDirectory() + "/cloud/XMTU1MjQ0MTcyOA==/0";
//		private static final String videoPath2 = Environment
//				 .getExternalStorageDirectory() + "/cloud/XMTU1MjQ0MTcyOA==/1";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
		bar1 = (ProgressBar) findViewById(R.id.progressbar1);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn3 = (Button) findViewById(R.id.btn3);
		btn4 = (Button) findViewById(R.id.btn4);
		btn5 = (Button) findViewById(R.id.btn5);
		btn6 = (Button) findViewById(R.id.btn6);
		playerView = (RelativeLayout) findViewById(R.id.playerview);
		seekBar1 = (SeekBar) findViewById(R.id.seek1);
		seektime1 = (TextView) findViewById(R.id.seektime1);
		seektime2 = (TextView) findViewById(R.id.seektime2);
		hwCodecPlayer = new HwCodecPlayer();
		hwCodecPlayer.setPlayerListener(new HwCodecPlayerListener() {

			@Override
			public void onVideoSizeChanged(int width, int height) {
				// TODO Auto-generated method stub
				surfaceView1.setVisibility(View.INVISIBLE);
				surfaceView1.setVisibility(View.VISIBLE);
				int a = ScreenUtil.getHeight(playerView);
				int b = ScreenUtil.getWidth(playerView);
				if (a != 0 && b != 0) {
					Configuration config = getResources().getConfiguration();

					android.view.ViewGroup.LayoutParams pa = surfaceView1
							.getLayoutParams();
					pa.height = a;
					pa.width = ((a * width) / height);
					surfaceView1.setLayoutParams(pa);
				}

			}

			@Override
			public void onVideoIndexUpdate(int currentIndex, int ip) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(),
						"inx" + currentIndex + "," + ip, Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onTimeOut() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartBuffering() {
				// TODO Auto-generated method stub
				bar1.setVisibility(View.VISIBLE);
			}

			@Override
			public void onSeekComplete() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPrepared() {
				// TODO Auto-generated method stub
				bar1.setVisibility(View.INVISIBLE);
				seektime2.setText(StringUtil.secToTime((int) (hwCodecPlayer
						.getDuration() / 1000)));

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
				bar1.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onCurrentPositionUpdate(int currentPosition) {
				// TODO Auto-generated method stub
				if (!seekBar1.isPressed()) {
					seektime1.setText(StringUtil
							.secToTime(currentPosition / 1000));
					seekBar1.setProgress((int) ((double) currentPosition * 100 / (double) hwCodecPlayer
							.getDuration()));
				}
			}

			@Override
			public void onCompletion() {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "complete", Toast.LENGTH_SHORT)
						.show();
				hwCodecPlayer.seekTo(0);
			}

			@Override
			public void onBufferingUpdate(int percent) {
				// TODO Auto-generated method stub
				seekBar1.setSecondaryProgress(percent);
			}
		});
		seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private int p;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				hwCodecPlayer.seekTo(p);
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
					this.p = progress * hwCodecPlayer.getDuration()
							/ seekBar.getMax();// 用于播放中定位
				}

			}
		});
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<String> filepaths = new ArrayList<String>();
//				filepaths.add(videoPath4);
				filepaths.add(videoPath1);
//				filepaths.add(videoPath3);
				filepaths.add(videoPath2);
//				filepaths.add(videoPath5);
				hwCodecPlayer.setDataSource(filepaths);
				hwCodecPlayer.prepareAsync();
				bar1.setVisibility(View.VISIBLE);
			}
		});
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (hwCodecPlayer.isPlaying()) {
					hwCodecPlayer.pause();
				} else {
					hwCodecPlayer.start();
				}
			}
		});
		btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		btn4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		btn5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		btn6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		surfaceView1.getHolder().addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				// hwCodecPlayer.release();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				hwCodecPlayer.setSurface(holder.getSurface());
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		hwCodecPlayer.release();
	}
}
