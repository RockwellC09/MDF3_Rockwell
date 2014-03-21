package com.ChristopherRockwell.topdvdrentals;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.Toast;

import com.ChristopherRockwell.topdvdrentals.getMovies.getData;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		// get top rentals again
		if (getMovies.connectionStatus(context)){
    		getMovies.getData data = new getData();
    		data.execute("http://api.rottentomatoes.com/api/public/v1.0/lists/dvds/top_rentals.json?apikey=bf72tfc2zjfbdscenpwx2e2r");
    	} else {
    		Toast.makeText(context, "Unable to get the Top Dvd Rentals. Check your connection and try again.", Toast.LENGTH_LONG).show();
    	}
		
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		
	}

}
