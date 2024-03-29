/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	Statustician
 * 
 * 	Package: 	com.RockwellChristopher.statustician
 * 
 * 	File: 		BatteryService.java
 * 
 *	Purpose:	When the battery status changes, the receiver in this service will analyze it and notify 
 *				the users and/or stop this service
 * 
 */

package com.RockwellChristopher.statustician;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class BatteryService extends Service {

	static BroadcastReceiver receiver;
	private static final String PREF_IS_RUNNING = "RUN";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				// check for battery level and show notification if requirements
				// are met
				int battery = getBattery();
				if (battery <= 25) {
					serviceStopped();
					stopService();
				} else if (battery == 50) {
					halfCharge();
				} else if (battery == 100) {
					chargeComplete();
				}
			}

		};
		IntentFilter intentFil = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		this.registerReceiver(receiver, intentFil);

		return super.onStartCommand(intent, flags, startId);
	}

	public void serviceStopped() {
		NotificationManager notManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Battery Service Stopped")
				.setContentText("Stopped due to low battery");
		Notification notification = notBuilder.build();
		notManager.notify(01, notification);
	}

	public void chargeComplete() {
		NotificationManager notManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Battery Fully Charged!")
				.setContentText("Unplug your device");
		Notification notification = notBuilder.build();
		notManager.notify(02, notification);
	}

	public void halfCharge() {
		NotificationManager notManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Battery at 50 Percent")
				.setContentText("Consider charging your device soon");
		Notification notification = notBuilder.build();
		notManager.notify(03, notification);
	}

	private void stopService() {
		// TODO Auto-generated method stub
		// stop BatteryService
		Intent service = new Intent(getApplicationContext(),
				BatteryService.class);
		getApplicationContext().stopService(service);
		setRunning(false);
		MainActivity.serviceTV.setText("Service: Not Running");
	}

	// get current battery level
	private int getBattery() {
		IntentFilter intentFil = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryIntent = this.registerReceiver(null, intentFil);
		int lev = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int percentage = 0;
		if (lev > 0 && scale > 0) {
			percentage = (lev * 100) / scale;
		}

		return percentage;
	}

	// set preference to whether this service is running or not
	private void setRunning(boolean running) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = pref.edit();

		editor.putBoolean(PREF_IS_RUNNING, running);
		editor.apply();
	}
}
