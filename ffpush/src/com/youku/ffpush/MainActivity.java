package com.youku.ffpush;

import com.youku.ypush.JniTest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = JniTest
						.GetStr("http://7xploe.media1.z0.glb.clouddn.com/XMTQ4NzA1Njc0OA==/hd2/p3_141.flv");
				Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}
}
