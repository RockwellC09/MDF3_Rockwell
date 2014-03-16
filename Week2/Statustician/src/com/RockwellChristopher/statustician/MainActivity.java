/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	Statistician
 * 
 * 	Package: 	com.RockwellChristopher.statistician
 * 
 * 	File: 		MainActivity.java
 * 
 *	Purpose:	This activity will get the current status of and the battery and output it to the user and display a battery tips video.
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
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity implements OnClickListener {

	TextView batteryTv;
	TextView batteryTvTitle;
	TextView tipTv;
	static TextView serviceTV;
	Button startBtn;
	Button playVidBtn;
	Button pauseBtn;
	static Button stopBtn;
	Context context;
	TelephonyManager telManager;
	BroadcastReceiver batReceiver;
	IntentFilter intentFil;
	Intent batteryIntent;
	int percentage;
	private static final String PREF_IS_RUNNING = "RUN";
	VideoView batVid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;

		// initialize view elements
		batteryTv = (TextView) findViewById(R.id.textView1);
		batteryTvTitle = (TextView) findViewById(R.id.TextViewTitle1);
		tipTv = (TextView) findViewById(R.id.tips_tv);
		serviceTV = (TextView) findViewById(R.id.service_tv);
		startBtn = (Button) findViewById(R.id.start);
		stopBtn = (Button) findViewById(R.id.stop);
		pauseBtn = (Button) findViewById(R.id.pause_video);
		playVidBtn = (Button) findViewById(R.id.play_video);

		// set custom font
		Typeface customFont = Typeface.createFromAsset(getAssets(),"SigmarOne.ttf");
		batteryTv.setTypeface(customFont);
		batteryTvTitle.setTypeface(customFont);
		tipTv.setTypeface(customFont);
		serviceTV.setTypeface(customFont);
		startBtn.setTypeface(customFont);
		stopBtn.setTypeface(customFont);
		pauseBtn.setTypeface(customFont);
		playVidBtn.setTypeface(customFont);
		

		// add click listeners to start and stop buttons
		startBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);
		pauseBtn.setOnClickListener(this);
		playVidBtn.setOnClickListener(this);

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
		
		// set up video
		batVid = (VideoView) findViewById(R.id.videoView1);
		String uriPath = "android.resource://" + getPackageName() + "/" + R.raw.battery_tips;
		batVid.setVideoURI(Uri.parse(uriPath));
		batVid.setMediaController(new MediaController(context));
	    
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

		} else if (v == playVidBtn) {
			// play video
			batVid.start();
		} else if (v == pauseBtn) {
			// pause video
			batVid.pause();
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
}
