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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{
	
	Button batteryBtn;
	Button phoneBtn;
	TextView tv;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		
		// initialize view elements
		batteryBtn = (Button) findViewById(R.id.battery_btn);
		phoneBtn = (Button) findViewById(R.id.phone_btn);
		tv = (TextView) findViewById(R.id.textView1);
		
		batteryBtn.setOnClickListener(this);
		phoneBtn.setOnClickListener(this);
		
		Intent service = new Intent(context, BatteryService.class);
        context.startService(service);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		// battery button click
		if (v == batteryBtn) {
			IntentFilter intentFil = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryIntent =  this.registerReceiver(null, intentFil);
			int lev =  batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL,  -1);
			int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int healthCode = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
			
			if (lev > 0 && scale > 0) {
				int percentage = (lev * 100) / scale;
				tv.setText(percentage + "% " + getBatteryHealthStr(healthCode));
			}
			
		// phone button click	
		} else if (v == phoneBtn) {

		}
	}

	// get batter health string base on the INT given by EXTRA_HEALTH i from the battery manager
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
}
