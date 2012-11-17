package cn.mimessage.mqttv3;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RootActivity extends Activity {

	protected static final String TAG = "RootActivity";
	private EditText mTopic;
	private EditText mTopicName;
	private EditText mContent;
	private TextView mMsg;
	private Button mPubBtn;
	private Button mSubBtn;
	private Button mUnSubBtn;
	private Button mDisConnectBtn;
	private Button mConnectBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_root);

	}

	@Override
	protected void onNewIntent(Intent intent) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void toPushServer(String action) {
	}

	private void toPushServer(String action, PushMessage message) {
		Log.i(TAG, "toPushServer action=" + action + "  " + message.toString());
	}
}
