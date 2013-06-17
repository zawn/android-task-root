package com.example.intentlaunch;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i(TAG, "TaskId : " + this.getTaskId());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onButtonClick(View v) {
		Log.i(TAG, "TaskId : " + this.getTaskId());
		Intent intent = null;
		String packageName = "com.example.t1";
		String className = "com.example.t1.InitialActivity";
		ComponentName cn = new ComponentName(packageName, className);
		switch (v.getId()) {
		case R.id.button1:
			Log.i(TAG, "MainActivity.onButtonClick():R.id.button1");
			intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.setPackage(packageName);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setComponent(cn);
			startActivity(intent);
			break;
		case R.id.button2:
			Log.i(TAG, "MainActivity.onButtonClick():R.id.button2");
			intent = new Intent();
			// intent.setAction(Intent.ACTION_MAIN);
			intent.setClassName(packageName, className);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.button3:
			Log.i(TAG, "MainActivity.onButtonClick():R.id.button3");
			intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.setClassName(packageName, className);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.button4:
			Log.i(TAG, "MainActivity.onButtonClick():R.id.button4");
			intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.setPackage(packageName);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			startActivity(intent);
			break;
		case R.id.button5:
			Log.i(TAG, "MainActivity.onButtonClick():R.id.button5");
			intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.setPackage(packageName);
			intent.setClassName(packageName, className);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.button6:
			Log.i(TAG, "MainActivity.onButtonClick():R.id.button6");
			intent = getPackageManager().getLaunchIntentForPackage(packageName);
			Log.i(TAG, intent.toString());
			startActivity(intent);
			break;
		default:
			Log.e(TAG, "Activity5.onButtonClick() Did not match the Id!");
			break;
		}
	}
}
