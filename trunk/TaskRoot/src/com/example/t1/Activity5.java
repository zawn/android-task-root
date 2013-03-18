package com.example.t1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class Activity5 extends Activity {

	private static final String TAG = "Activity5";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		Log.i(TAG, "TaskId:" + this.getTaskId());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main5);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "onKeyDown");
		// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		// Intent intent = new Intent(Activity1.this, MainActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// intent.setAction("exit");
		// startActivity(intent);
		// return true;
		// }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent");
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG, "onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, "onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "onStop");
	}

	public void onButtonClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.activity5_button_exit:
			InitialActivity.exitTask(getApplicationContext());
			break;
		case R.id.activity5_button_start_a1:
			intent = new Intent(Activity5.this, Activity1.class);
			startActivity(intent);
			break;
		case R.id.activity5_button_satart_a4:
			intent = new Intent(Activity5.this, Activity4.class);
			startActivity(intent);
			break;
		case R.id.activity5_button_switch_2:
			InitialActivity.switchActivity(this, Activity2.class);
			break;
		case R.id.activity5_button_manual_clear:
			intent = new Intent(getApplicationContext(), InitialActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra(InitialActivity.CURRENT_LAUNCH_ACTIVITY, InitialActivity.class);
			startActivity(intent);
			break;
		default:
			Log.e(TAG, "Activity5.onButtonClick() Did not match the Id!");
			break;
		}
	}
}
