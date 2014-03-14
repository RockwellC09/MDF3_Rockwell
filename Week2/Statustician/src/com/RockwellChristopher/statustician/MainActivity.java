/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	Statistician
 * 
 * 	Package: 	com.RockwellChristopher.statistician
 * 
 * 	File: 		MainActivity.java
 * 
 *	Purpose:	This activity will get the current status of the phone and the battery and output it to the user.
 * 
 */

package com.RockwellChristopher.statustician;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	TextView batteryTv;
	TextView phoneTv;
	TextView networkTv;
	TextView batteryTvTitle;
	TextView phoneTvTitle;
	TextView networkTvTitle;
	static TextView serviceTV;
	Button startBtn;
	static Button stopBtn;
	Context context;
	TelephonyManager telManager;
	PhoneStateListener phoneStateLis;
	int numRings = 0;
	String stateStr = "N/A";
	String typeStr = "N/A";
	String inNum = "N/A";
	String connStr = "Off";
	int phoneType;
	BroadcastReceiver batReceiver;
	IntentFilter intentFil;
	Intent batteryIntent;
	int percentage;
	private static final String PREF_IS_RUNNING = "RUN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;

		// initialize view elements
		batteryTv = (TextView) findViewById(R.id.textView1);
		phoneTv = (TextView) findViewById(R.id.textView2);
		networkTv = (TextView) findViewById(R.id.textView3);
		batteryTvTitle = (TextView) findViewById(R.id.TextViewTitle1);
		phoneTvTitle = (TextView) findViewById(R.id.TextViewTitle2);
		networkTvTitle = (TextView) findViewById(R.id.TextViewTitle3);
		serviceTV = (TextView) findViewById(R.id.service_tv);
		startBtn = (Button) findViewById(R.id.start);
		stopBtn = (Button) findViewById(R.id.stop);

		// set custom font
		Typeface customFont = Typeface.createFromAsset(getAssets(),
				"SigmarOne.ttf");
		batteryTv.setTypeface(customFont);
		phoneTv.setTypeface(customFont);
		networkTv.setTypeface(customFont);
		batteryTvTitle.setTypeface(customFont);
		phoneTvTitle.setTypeface(customFont);
		networkTvTitle.setTypeface(customFont);
		serviceTV.setTypeface(customFont);
		startBtn.setTypeface(customFont);
		stopBtn.setTypeface(customFont);

		// add click listeners to start and stop buttons
		startBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);

		// check preference to see if service was set to running
		if (isRunning(context)) {
			Intent service = new Intent(context, BatteryService.class);
			context.startService(service);
		} else {
			Intent service = new Intent(context, BatteryService.class);
			context.stopService(service);
		}

		// check to see if service is running and output to user
		if (isMyServiceRunning()) {
			// service is running
			serviceTV.setText("Service: Running");
		} else {
			// service not running
			serviceTV.setText("Service: Not Running");
		}

		// get battery status
		getBatteryStatus();

		// receiver for change in battery
		batReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				getBatteryStatus();
			}

		};

		// register receiver
		intentFil = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryIntent = this.registerReceiver(batReceiver, intentFil);

		// setup telephone manager
		telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		phoneStateLis = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {

				phoneType = telManager.getPhoneType();
				// get phone type
				if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
					typeStr = "CDMA";
				} else if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
					typeStr = "GSM";
				} else if (phoneType == TelephonyManager.PHONE_TYPE_SIP) {
					typeStr = "SIP";
				}

				// get phone state
				if (state == TelephonyManager.CALL_STATE_IDLE) {
					stateStr = "Idle";
				} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
					stateStr = "On a Call";
				} else if (state == TelephonyManager.CALL_STATE_RINGING) {
					stateStr = "Ringing";
					numRings++;
				}

				if (incomingNumber.length() > 1) {
					inNum = incomingNumber;
				}

				phoneTv.setText(Html
						.fromHtml("<font color=\"#545454\"><b>Phone Type: </b></font>"
								+ typeStr
								+ "<br /><font color=\"#545454\"><b>Phone State: </b></font>"
								+ stateStr
								+ "<br />"
								+ "<font color=\"#545454\"><b>Incoming Number: </b></font>"
								+ inNum
								+ "<br />"
								+ "<font color=\"#545454\"><b>Rang: </b></font>"
								+ numRings + " times"));
			}
		};

		// add listener
		telManager.listen(phoneStateLis, PhoneStateListener.LISTEN_CALL_STATE);
	}

	// get batter health string base on the INT given by EXTRA_HEALTH i from the
	// battery manager
	private String getBatteryHealthStr(int code) {
		String healthStr = "";
		switch (code) {
		case 1:
			healthStr = "Unknown";
			break;
		case 2:
			healthStr = "Good";
			break;
		case 3:
			healthStr = "Overheating";
			break;
		case 4:
			healthStr = "Dead";
			break;
		case 5:
			healthStr = "Over Voltage";
			break;
		case 6:
			healthStr = "Unspecified Failure";
			break;
		case 7:
			healthStr = "Cold";
			break;

		}

		return healthStr;

	}

	@Override
	public void onResume() {
		// get connection status
		getConnectionInfo();
		super.onResume();
	}

	// get the current battery information and output it to the user
	private void getBatteryStatus() {
		// register receiver
		intentFil = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryIntent = this.registerReceiver(batReceiver, intentFil);
		int lev = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int healthCode = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH,
				-1);

		if (lev > 0 && scale > 0) {
			percentage = (lev * 100) / scale;
			batteryTv
					.setText(Html
							.fromHtml("<font color=\"#545454\"><b>Battery Level: </b></font>"
									+ percentage
									+ "% <br />"
									+ "<font color=\"#545454\"><b>Battery Health: </b></font>"
									+ getBatteryHealthStr(healthCode)));
		}
	}

	// check to see if service is running
	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.RockwellChristopher.statustician.BatteryService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == startBtn) {
			if (percentage > 25) {
				// check to see if service is running and output to user
				if (isMyServiceRunning()) {
					// do nothing
				} else {
					// service not running
					// start BatteryService
					Intent service = new Intent(context, BatteryService.class);
					context.startService(service);
					setRunning(true);
					serviceTV.setText("Service: Running");
				}
			} else {
				Toast.makeText(context,
						"Service can't be started due to low battery.",
						Toast.LENGTH_LONG).show();
			}

		} else if (v == stopBtn) {
			if (percentage > 25) {
				// check to see if service is running and output to user
				if (isMyServiceRunning()) {
					// stop BatteryService
					Intent service = new Intent(context, BatteryService.class);
					context.stopService(service);
					setRunning(false);
					serviceTV.setText("Service: Not Running");
				} else {
					// do nothing
				}
			}

		}
	}

	// set preference to whether this service is running or not
	private void setRunning(boolean running) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = pref.edit();

		editor.putBoolean(PREF_IS_RUNNING, running);
		editor.apply();
	}

	// get shared preference to see if this service is running
	public static boolean isRunning(Context ctx) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx.getApplicationContext());
		return pref.getBoolean(PREF_IS_RUNNING, false);
	}

	private void getConnectionInfo() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobile = ni.isConnected();

		if (isMobile) {
			connStr = "On";
		} else {
			connStr = "Off";
		}

		networkTv.setText(Html
				.fromHtml("<font color=\"#545454\"><b>Mobile Data: </b></font>"
						+ connStr));
	}
}
