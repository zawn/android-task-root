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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mConnectBtn = (Button) findViewById(R.id.connectBtn);

		mConnectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "启动服务");
				toPushServer(MqttIntent.CONNECT);
			}
		});
		mDisConnectBtn = (Button) findViewById(R.id.disConnectBtn);
		mDisConnectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "关闭服务");
				toPushServer(MqttIntent.DISCONNECT);
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
				toPushServer(MqttIntent.SUBSCRIBE, new MQMessage(topic));
			}
		});
		mUnSubBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "退订主题");
				String topic = mTopicName.getText().toString();
				toPushServer(MqttIntent.UNSUBSCRIBE, new MQMessage(topic));
			}
		});
		mPubBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "发布消息");
				String topic = mTopic.getText().toString();
				String msg = mContent.getText().toString();
				toPushServer(MqttIntent.PUBLISH, new MQMessage(topic, msg));
			}
		});

	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent action=" + intent.getAction());
		if (intent.getAction().equals(MqttIntent.MSGARRIVED)) {
			MQMessage message = (MQMessage) intent.getSerializableExtra(MqttIntent.MSG);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void toPushServer(String action) {
		Log.i(TAG, "toPushServer action=" + action);
		Intent intent = new Intent(action);
		intent.setClass(getApplicationContext(), PushService.class);
		startService(intent);
	}

	private void toPushServer(String action, MQMessage message) {
		Log.i(TAG, "toPushServer action=" + action + "  " + message.toString());
		Intent intent = new Intent(action);
		intent.setClass(getApplicationContext(), PushService.class);
		intent.putExtra(MqttIntent.MSG, message);
		startService(intent);
	}
}
