package com.ChristopherRockwell.topdvdrentals;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class getMovies {

	static Context context;
	static String TAG = "NETWORK DATA - MAINACTIVITY";

	// check to see if user have a valid internet connection
		public static boolean connectionStatus(Context context) {
			boolean isConnected = false;
			ConnectivityManager ConnectMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = ConnectMngr.getActiveNetworkInfo();
			if (netInfo != null) {
				if (netInfo.isConnected()) {
					isConnected = true;
				}
			}

			return isConnected;
		}

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
			Log.e(TAG, "Error: ", e);
		}

		return response;
	}

	// create async task
	public static class getData extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String responseString = "";
			try {
				URL url = new URL("http://api.rottentomatoes.com/api/public/v1.0/lists/dvds/top_rentals.json?apikey=bf72tfc2zjfbdscenpwx2e2r");
				responseString = getResponse(url);
			} catch (MalformedURLException e) {
				responseString = "Something went wrong";
				Log.e(TAG, "Error: ", e);
			}
			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			// read JSON and set Text View text
			String title, criticScore;

			try {
				// set JSONOject and cast into array the back into an object to
				// get movie proper info
				JSONObject obj = new JSONObject(result);
				JSONArray movies = obj.getJSONArray("movies");
				
				// get random number
				Random r = new Random();
				int rand = r.nextInt(movies.length() - 0) + 0;
				
				JSONObject castObj = movies.getJSONObject(rand);
				JSONObject antrCastObj = castObj.getJSONObject("ratings");
				title = castObj.getString("title");
				criticScore = antrCastObj.getString("critics_score");
				
				WidgetConfig.remoteV.setTextViewText(R.id.wid_title_tv, title);
				WidgetConfig.remoteV.setTextViewText(R.id.wid_critic_tv, criticScore + "%");
				WidgetConfig.dataLoaded = true;
				if (WidgetConfig.userClicked == true) {
					WidgetConfig.subButton.performClick();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("Error: ", e.getMessage().toString());
			}
			
			super.onPostExecute(result);
		}

	}
}
