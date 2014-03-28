/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	OwnerInfo
 * 
 * 	Package: 	com.RockwellChristopher.ownerinfo
 * 
 * 	File: 		MainActivity.java
 * 
 *	Purpose:	This activity gathers the users information and saves it just in case the users loses the device, so 
 *				it can be returned.
 * 
 */

package com.RockwellChristopher.ownerinfo;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {
	WebView myWebView;
	boolean hasErrors = false;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		
		// setup WebView
		myWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.addJavascriptInterface(new WebAppInterface(this), "JSInterface");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		// check for stored data and load proper URL accordingly
		if (prefs.getString("first", "").equals("")) {
			myWebView.loadUrl("file:///android_asset/home.html");
		} else {
			myWebView.loadUrl("file:///android_asset/info.html");
		}
		
	}
	
	public class WebAppInterface {
	    Context mContext;

	    /** Instantiate the interface and set the context */
	    WebAppInterface(Context c) {
	        mContext = c;
	    }

	    // save user info data
	    @JavascriptInterface
	    public void saveData(String firstname, String lastname, String address, String number) {
	    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = pref.edit();

			editor.putString("first", firstname);
			editor.putString("last", lastname);
			editor.putString("address", address);
			editor.putString("number", number);
			editor.apply();
			
			Toast.makeText(context, "Info Saved", Toast.LENGTH_SHORT).show();
			
			myWebView.loadUrl("file:///android_asset/info.html");
	    }
	    
	    // get the stored data to display on the info.html page
	    @JavascriptInterface
	    public void getData() {
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String first = prefs.getString("first", "");
			String last = prefs.getString("last", "");
			String address = prefs.getString("address", "");
			String number = prefs.getString("number", "");
			
			myWebView.loadUrl("javascript:setData('" + first + "," + last + "," + address + "," + number + "')");
	    }
	    
	    // set input values to previously stored data
	    @JavascriptInterface
	    public void getDataInput() {
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String first = prefs.getString("first", "");
			String last = prefs.getString("last", "");
			String address = prefs.getString("address", "");
			String number = prefs.getString("number", "");
			
			if(first != "") {
				myWebView.loadUrl("javascript:setDataInput('" + first + "," + last + "," + address + "," + number + "')");
			}
	    }
	    
	    // go back to home.html for user to edit their information
	    @JavascriptInterface
	    public void editData() {
	    	myWebView.loadUrl("file:///android_asset/home.html");
	    }
	    
	    // delete shared preferences data
	    @JavascriptInterface
	    public void deleteData() {
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    	SharedPreferences.Editor editor = prefs.edit();
	    	editor.clear();
	    	editor.commit();
	    	Toast.makeText(context, "Info Deleted", Toast.LENGTH_SHORT).show();
	    	myWebView.loadUrl("file:///android_asset/home.html");
	    }
	    
	    // share application with friends
	    @JavascriptInterface
	    public void shareApp() {
	    	Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, you should checkout \"Owner Info\" in the Google Play Store.");
			sendIntent.setType("text/plain");
			try {
				startActivity(sendIntent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("Error: ", e.getMessage().toString());
				e.printStackTrace();
			}
	    }
	    
	}

}
