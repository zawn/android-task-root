package com.example.t1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Activity2 extends Activity {

	private static final String TAG = "Activity2";
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.w(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.w(TAG, "onCreate");
		Log.w(TAG, "TaskId:" + this.getTaskId());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Activity2.this, Activity3.class);
				startActivity(intent);
			}
		});
		button.setText(getIntent().getCharSequenceExtra("zawn"));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.w(TAG, "onDestroy");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.w(TAG, "onKeyDown");
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			Intent intent = new Intent(Activity1.this, MainActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			intent.setAction("exit");
//			startActivity(intent);
//			return true;
//		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.w(TAG, "onNewIntent");
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.w(TAG, "onPause");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.w(TAG, "onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.w(TAG, "onResume");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.w(TAG, "onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.w(TAG, "onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.w(TAG, "onStop");
	}
}
