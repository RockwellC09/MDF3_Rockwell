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
	
	Button subButton;
	static String radioText = "Slow";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_config);
		
		subButton = (Button) findViewById(R.id.trans_btn);
		subButton.setOnClickListener(this);
		
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.trans_radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) 
            {
                RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
                radioText = checkedRadioButton.getText().toString();
            }
        });
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			int widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			
			if (widgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {
				
				RemoteViews remoteV = new RemoteViews(this.getPackageName(), R.layout.widget_layout);
				AppWidgetManager.getInstance(this).updateAppWidget(widgetID, remoteV);
				
				Toast.makeText(this, "You've selected a " + radioText + " transition speed.", Toast.LENGTH_LONG).show();
				
				Intent resultVal = new Intent();
				resultVal.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
				setResult(RESULT_OK, resultVal);
				finish();
			}
		}
	}

}
