package com.ChristopherRockwell.topdvdrentals;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetConfig extends Activity implements OnClickListener {
	
	static Button subButton;
	static String radioText = "Default";
	static RemoteViews remoteV;
	static boolean dataLoaded = false;
	static boolean userClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_config);
		
		subButton = (Button) findViewById(R.id.color_btn);
		subButton.setOnClickListener(this);
		
		remoteV = new RemoteViews(this.getPackageName(), R.layout.widget_layout);
		
		// check for selected radio button and set text color
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.trans_radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) 
            {
                RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
                radioText = checkedRadioButton.getText().toString();
                
                if (radioText.equals("Blue")) {
                	remoteV.setTextColor(R.id.wid_crit, getResources().getColor(R.color.btn_color3));
                	remoteV.setTextColor(R.id.wid_title, getResources().getColor(R.color.btn_color3));
                } else if (radioText == "Purple") {
                	remoteV.setTextColor(R.id.wid_crit, getResources().getColor(R.color.btn_color2));
                	remoteV.setTextColor(R.id.wid_title, getResources().getColor(R.color.btn_color2));
                } else {
                	remoteV.setTextColor(R.id.wid_crit, getResources().getColor(R.color.btn_color));
                	remoteV.setTextColor(R.id.wid_title, getResources().getColor(R.color.btn_color));
                }
                
            }
        });
        
        
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// wait for data to be loaded from URL
		if (dataLoaded == false) {
			userClicked = true;
			Toast.makeText(this, "Loading Data....", Toast.LENGTH_LONG).show();
		} else {
			Bundle extras = getIntent().getExtras();
			
			if (extras != null) {
				int widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
				
				if (widgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {
					
					AppWidgetManager.getInstance(this).updateAppWidget(widgetID, remoteV);
					
					Toast.makeText(this, "You've selected " + radioText + " as you text color.", Toast.LENGTH_LONG).show();
					
					Intent resultVal = new Intent();
					resultVal.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
					setResult(RESULT_OK, resultVal);
					finish();
				}
			}
		}
		
	}

}
