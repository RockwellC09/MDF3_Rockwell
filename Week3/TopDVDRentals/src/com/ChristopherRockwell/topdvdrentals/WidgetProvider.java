package com.ChristopherRockwell.topdvdrentals;

import java.net.MalformedURLException;
import java.net.URL;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
	
	FileManagerSingleton file;
	String responseString = null;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		RemoteViews remoteV = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		
		file = FileManagerSingleton.getInstance();
		
		// check to see if there's a valid connection
		if (file.connectionStatus(context)) {
			URL url;
			try {
				url = new URL("http://api.rottentomatoes.com/api/public/v1.0/lists/dvds/top_rentals.json?apikey=bf72tfc2zjfbdscenpwx2e2r");
				responseString = TopRentalsService.getResponse(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		
	}

}
