package com.youku.fftest;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.youku.player.util.MyAESUtil;
import com.youku.yplayer.AvPacket;
import com.youku.yplayer.AvPacketCache;
import com.youku.yplayer.FFExtractor;
import com.youku.yplayer.UMediaPlayer2;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String fpath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/myuv.yuv";
	private FutureTask<Long> futureTask;
	private TextView txt1;
	private boolean stop = false;
	private FFExtractor ffExtractor1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.loadLibrary("ijkffmpeg");
		System.loadLibrary("yplayer");
		txt1 = (TextView) findViewById(R.id.txt1);
		ffExtractor1 = new FFExtractor();
		findViewById(R.id.btn1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						PlayerActivity.class);
				startActivity(intent);
				// MediaPlayer md=new UMediaPlayer2();
			}
		});
		findViewById(R.id.btn2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(MainActivity.this,
				// MultiPlayerActivity.class);
				// startActivity(intent);
				new Thread() {
					@Override
					public void run() {
						Log.e("liyh", "run");
						for (int i = 0; i < 1000; i++) {
							FFExtractor ff = new FFExtractor();
							ff.setDataSource("http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/p1_185.flv");
							
						}
						Log.e("liyh", "run comp");
					};
				}.start();

			}
		});

		findViewById(R.id.btn3).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread() {
					@Override
					public void run() {
						Log.e("liyh", "run");
						for (int i = 0; i < 500; i++) {
							FFExtractor ff = new FFExtractor();
							ff.setDataSource("http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/p1_185.flv");
							ff.prep();
						}
						Log.e("liyh", "run comp");
					};
				}.start();
//				String str = "aaaaaaaaaaaaaaaa" + "9e3633aadde6bfec";
//				String key = "9e3633aadde6bfec";
//				byte[] bb = new byte[16];
//				byte[] aes = ffExtractor1.native_aestest2(str.getBytes(),
//						key.getBytes(), bb);
//				Log.e("liyh", aes[0] + "");
			}
		});

	}

	private void readframetask() {
		ffExtractor1 = new FFExtractor();
		ffExtractor1
				.setDataSource("http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/p1_185.flv");
		Log.e("keyframe", "preparing...");
		ffExtractor1.prep();
		int inx = 0;
		Log.e("keyframe", "prepared");
		for (;;) {
			if (stop) {
				break;
			}
			inx = ffExtractor1.prereadNextPacket();
			if (inx == ffExtractor1.getStreamAudioInx()) {
				AvPacket avPacket1 = ffExtractor1.getReadPacket2();
				byte[] abuf = new byte[avPacket1.getDataSize()];
				avPacket1.getData().get(abuf);
				Log.e("keyframe", "reada");
			} else if (inx == ffExtractor1.getStreamVideoInx()) {
				AvPacket avPacket1 = ffExtractor1.getReadPacket2();
				byte[] abuf = new byte[avPacket1.getDataSize()];
				avPacket1.getData().get(abuf);
				Log.e("keyframe", "readv");
			} else {
				Log.e("keyframe", "end");
				break;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
