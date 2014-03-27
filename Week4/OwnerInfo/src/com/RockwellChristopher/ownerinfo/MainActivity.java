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
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends Activity {
	WebView myWebView;
	boolean hasErrors = false;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		
		myWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.addJavascriptInterface(new WebAppInterface(this), "JSInterface");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
			
			myWebView.loadUrl("file:///android_asset/info.html");
	    }
	    
	    @JavascriptInterface
	    public void getData() {
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String first = prefs.getString("first", "");
			String last = prefs.getString("last", "");
			String address = prefs.getString("address", "");
			String number = prefs.getString("number", "");
			
			myWebView.loadUrl("javascript:setData('" + first + "," + last + "," + address + "," + number + "')");
	    }
	    
	    @JavascriptInterface
	    public void showToast() {
	    	Toast.makeText(context, "It Works!!", Toast.LENGTH_LONG).show();
	    }
	    
	    // go back to home.html for user to edit their information
	    @JavascriptInterface
	    public void editData() {
	    	myWebView.goBack();
	    }
	    
	    // delete shared preferences data
	    @JavascriptInterface
	    public void deleteData() {
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    	SharedPreferences.Editor editor = prefs.edit();
	    	editor.clear();
	    	editor.commit();
	    	myWebView.loadUrl("file:///android_asset/home.html");
	    }
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
