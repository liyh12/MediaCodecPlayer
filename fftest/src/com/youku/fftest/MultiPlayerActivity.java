package com.youku.fftest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.youku.player.util.ScreenUtil;
import com.youku.yplayer.HwCodecPlayer;
import com.youku.yplayer.HwCodecPlayerListener;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

public class MultiPlayerActivity extends Activity implements OnClickListener {
	private HwCodecPlayer hwCodecPlayer1;
	private SurfaceView surfaceView1;
	private HwCodecPlayer hwCodecPlayer2;
	private SurfaceView surfaceView2;
	private Button btn1, btn2, btn3, btn4;
	private static final String videoPath1 = "http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/p1_185.flv";
	private static final String videoPath2 = "http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/p2_126.flv";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_player);
		hwCodecPlayer1 = new HwCodecPlayer();
		hwCodecPlayer2 = new HwCodecPlayer();
		surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
		surfaceView2 = (SurfaceView) findViewById(R.id.surfaceView2);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn3 = (Button) findViewById(R.id.btn3);
		btn4 = (Button) findViewById(R.id.btn4);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		
		
		
		surfaceView1.getHolder().addCallback(new Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				hwCodecPlayer1.setSurface(holder.getSurface());
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub
				
			}
		});
		
		surfaceView2.getHolder().addCallback(new Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				hwCodecPlayer2.setSurface(holder.getSurface());
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub
				
			}
		});
		initListener();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn1:
			List<String> list1=new ArrayList<>();
			list1.add(videoPath1);
			hwCodecPlayer1.setDataSource(list1);
			hwCodecPlayer1.prepareAsync();
			break;
		case R.id.btn2:
			List<String> list2=new ArrayList<>();
			list2.add(videoPath2);
			hwCodecPlayer2.setDataSource(list2);
			hwCodecPlayer2.prepareAsync();
			break;
		case R.id.btn3:
			hwCodecPlayer1.start();
			break;
		case R.id.btn4:
			hwCodecPlayer2.start();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		hwCodecPlayer1.release();
		hwCodecPlayer2.release();
	}
	
	
	
	
	private void initListener(){
		hwCodecPlayer1.setPlayerListener(new HwCodecPlayerListener() {
			
			@Override
			public void onVideoSizeChanged(int width, int height) {
				// TODO Auto-generated method stub
				surfaceView1.setVisibility(View.INVISIBLE);
				surfaceView1.setVisibility(View.VISIBLE);
				int b = ScreenUtil.getScreenWidth(getBaseContext());
				android.view.ViewGroup.LayoutParams pa = surfaceView1
						.getLayoutParams();
				pa.width = b;
				pa.height = (9*b)/16;
				surfaceView1.setLayoutParams(pa);
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
		hwCodecPlayer2.setPlayerListener(new HwCodecPlayerListener() {
			
			@Override
			public void onVideoSizeChanged(int width, int height) {
				// TODO Auto-generated method stub
				surfaceView2.setVisibility(View.INVISIBLE);
				surfaceView2.setVisibility(View.VISIBLE);
				int b = ScreenUtil.getScreenWidth(getBaseContext());
				android.view.ViewGroup.LayoutParams pa = surfaceView2
						.getLayoutParams();
				pa.width = b;
				pa.height = (9*b)/16;
				surfaceView2.setLayoutParams(pa);
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
	}
}
