/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	SendEmailLauncher
 * 
 * 	Package: 	com.RockwellChristopher.sendemail
 * 
 * 	File: 		MainActivity.java
 * 
 *	Purpose:	Launcher for my SimplEmail application
 * 
 */

package com.RockwellChristopher.sendemail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	ImageButton imgButton;
	Button btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		imgButton = (ImageButton) findViewById(R.id.img_btn);
		btn = (Button) findViewById(R.id.launch_btn);
		
		imgButton.setOnClickListener(new View.OnClickListener() {
			
            public void onClick(View v) {
                // Perform action on click
            	
            	Intent i = new Intent(Intent.ACTION_SEND);
            	i.setType("text/plain"); 
            	i.putExtra(Intent.EXTRA_EMAIL, "mdf3test@gmail.com");
            	startActivity(Intent.createChooser(i, "Send email via"));
            }
		});
		
		btn.setOnClickListener(new View.OnClickListener() {
			
            public void onClick(View v) {
                // Perform action on click
            	
            	Intent i = new Intent(Intent.ACTION_SEND);
            	i.setType("text/plain"); 
            	i.putExtra(Intent.EXTRA_EMAIL, "mdf3test@gmail.com");
            	startActivity(Intent.createChooser(i, "Send email via"));
            }
        });
	}

}
