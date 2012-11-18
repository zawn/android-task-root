package cn.mimessage.mqttv3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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

public class MainActivity extends Activity {

	protected static final String TAG = "MainActivity";
	private EditText mTopic;
	private EditText mTopicName;
	private EditText mContent;
	private TextView mMsg;
	private Button mPubBtn;
	private Button mSubBtn;
	private Button mUnSubBtn;
	private Button mDisConnectBtn;
	private Button mConnectBtn;
	
	private static boolean mIsNewIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "MainActivity.onCreate()");
		setContentView(R.layout.activity_main);
		mConnectBtn = (Button) findViewById(R.id.connectBtn);

		mConnectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "启动服务");
				toPushServer(PushIntent.CONNECT);
			}
		});
		mDisConnectBtn = (Button) findViewById(R.id.disConnectBtn);
		mDisConnectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "关闭服务");
				toPushServer(PushIntent.DISCONNECT);
			}
		});
		mTopic = (EditText) findViewById(R.id.topic);
		mTopicName = (EditText) findViewById(R.id.topicName);
		mContent = (EditText) findViewById(R.id.content);
		mMsg = (TextView) findViewById(R.id.msg);

		mPubBtn = (Button) findViewById(R.id.pub);
		mSubBtn = (Button) findViewById(R.id.sub);
		mUnSubBtn = (Button) findViewById(R.id.unsub);
		mSubBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "订阅主题");
				String topic = mTopicName.getText().toString();
				toPushServer(PushIntent.SUBSCRIBE, new PushMessage(topic));
			}
		});
		mUnSubBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "退订主题");
				String topic = mTopicName.getText().toString();
				toPushServer(PushIntent.UNSUBSCRIBE, new PushMessage(topic));
			}
		});
		mPubBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "发布消息");
				String topic = mTopic.getText().toString();
				String msg = mContent.getText().toString();
				toPushServer(PushIntent.PUBLISH, new PushMessage(topic, msg));
			}
		});
		
		if (savedInstanceState != null) {
			// 在低内存的情况下Activity将被销毁,销毁后下面两种操作将触发此条件
			// 1,用户按返回键重新返回当前Activity,此时程序应该自动退出.
			// 2,程序通过Intent启动该Activity.
			mIsNewIntent = false;
		} else {
			// 新的TaskRoot实例,是第一次接收到该Intent,所以置位为true
			mIsNewIntent = true;
			// 这是一个新的TaskRoot实例,执行默认的操作
			final Intent intent = getIntent();
			intentHandler(intent);
		}

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (BuildConfig.DEBUG)
			Log.i(TAG, "onSaveInstanceState");
		outState.putSerializable("Mark", "Mark This is an instance already exists");
		super.onSaveInstanceState(outState);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent action=" + intent.getAction());
		mIsNewIntent = true;
		setIntent(intent);
		intentHandler(intent);
	}

	/**
	 * @param intent
	 */
	private void intentHandler(Intent intent) {
		if (intent.getAction().equals(PushIntent.MESSAGE_ARRIVED)) {
			ArrayList<PushMessage> messages = (ArrayList<PushMessage>) intent.getSerializableExtra(PushIntent.MESSAGE);
			for (Iterator iterator = messages.iterator(); iterator.hasNext();) {
				PushMessage message = (PushMessage) iterator.next();
				String t = mMsg.getText().toString();
				StringBuilder sb = new StringBuilder();
				sb.append(t);
				sb.append("主题：");
				sb.append(message.getTopicName());
				sb.append("\n时间：");
				sb.append((new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date()).toString());
				sb.append("\nQoS：");
				sb.append(message.getQos());
				sb.append("\n消息：\n    ");
				sb.append(new String(message.getPayload()));
				sb.append("\n------------------------------\n");
				mMsg.setText(sb.toString());
				
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void toPushServer(String action) {
		Log.i(TAG, "toPushServer action=" + action);
		Intent intent = new Intent(action);
		intent.setClass(getApplicationContext(), Push.class);
		startService(intent);
	}

	private void toPushServer(String action, PushMessage message) {
		Log.i(TAG, "toPushServer action=" + action + "  " + message.toString());
		Intent intent = new Intent(action);
		intent.setClass(getApplicationContext(), Push.class);
		intent.putExtra(PushIntent.MESSAGE, message);
		startService(intent);
	}
}
