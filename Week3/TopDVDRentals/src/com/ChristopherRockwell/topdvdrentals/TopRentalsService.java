/*
 *@author	Christopher Rockwell 
 *Description: This service retrieves the top DVD rental from the Rotten Tomatoes API for use 
 *within the application.
 */
package com.ChristopherRockwell.topdvdrentals;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/*
 * The Class TopRentalsService will retrieve the top DVD rental from the rotten tomatoes API.
 */
public class TopRentalsService extends IntentService {

	private static final String TAG = "MyActivity";
	String responseString = "";
	public static final String MSGR_KEY = "messenger";
	public static final String URL_STR = null;

	public TopRentalsService() {
		super(" TopRentalsService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHandleIntent Started");

		// get intent values
		Bundle extras = intent.getExtras();
		Messenger msgr = (Messenger) extras.get(MSGR_KEY);
		String urlStr = extras.getString(URL_STR);

		try {
			URL url = new URL(urlStr);
			responseString = getResponse(url);
		} catch (MalformedURLException e) {
			responseString = "Something went wrong";
			Log.e("Error: ", e.getMessage().toString());
		}

		Message message = Message.obtain();
		message.arg1 = Activity.RESULT_OK;
		message.obj = responseString;

		try {
			msgr.send(message);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Log.e("Error: ", e.getMessage().toString());
			e.printStackTrace();
		}

	}

	// This method get the JSON data from the rotten tomatoes API and returns it
	public static String getResponse(URL url) {
		String response = "";
		try {
			URLConnection connect =  url.openConnection();
			BufferedInputStream buffIn = new BufferedInputStream(connect.getInputStream());
			byte[] contextByte = new byte[1024];
			int bytesRead = 0;
			StringBuffer responseBuffer = new StringBuffer();
			while ((bytesRead = buffIn.read(contextByte)) != -1) {
				response = new String(contextByte, 0, bytesRead);
				responseBuffer.append(response);
			}

			response = responseBuffer.toString();
		} catch (IOException e) {
			response = "Something went wrong";
			Log.e("Error: ", e.getMessage().toString());
		}

		return response;
	}

	// This method writes my JSON data to a file
	public static boolean writeStrFile (Context context, String fileName, String content) {
		boolean result = false;

		FileOutputStream outStream = null;
		try {
			outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			outStream.write(content.getBytes());
			Log.i("Wrote string file", "successfully");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("Error: ", e.getMessage().toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("Error:", e.getMessage().toString());
		}

		return result;

	}

}
