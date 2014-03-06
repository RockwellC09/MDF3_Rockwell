/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	SimplEmail
 * 
 * 	Package: 	com.RockwellChristopher.simplemail
 * 
 * 	File: 		MainActivity.java
 * 
 *	Purpose:	This activity sends an email to and from the email addresses specified by the users and allows the users to add
 * 				smileys/emojis to the message as well
 * 
 */


package com.RockwellChristopher.simplemail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
public class MainActivity extends Activity {
	TextView from;
	TextView to;
	TextView sbj;
	TextView msg;
	EditText fromEdit;
	EditText toEdit;
	EditText sbjEdit;
	EditText msgEdit;
	Button send;
	Context context;
	boolean hasError = false;
	ImageButton smiley1;
	ImageButton smiley2;
	ImageButton smiley3;
	ImageButton smiley4;
	ImageButton smiley5;
	ImageButton smiley6;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;

		// initialize view elements
		from = (TextView) findViewById(R.id.from);
		to = (TextView) findViewById(R.id.to);
		sbj = (TextView) findViewById(R.id.sbj);
		msg = (TextView) findViewById(R.id.msg);
		send = (Button) findViewById(R.id.send);
		from = (TextView) findViewById(R.id.from);

		fromEdit = (EditText) findViewById(R.id.from_edit);
		toEdit = (EditText) findViewById(R.id.to_edit);
		sbjEdit = (EditText) findViewById(R.id.sbj_edit);
		msgEdit = (EditText) findViewById(R.id.msg_edit);

		smiley1 = (ImageButton) findViewById(R.id.smiley1);
		smiley2 = (ImageButton) findViewById(R.id.smiley2);
		smiley3 = (ImageButton) findViewById(R.id.smiley3);
		smiley4 = (ImageButton) findViewById(R.id.smiley4);
		smiley5 = (ImageButton) findViewById(R.id.smiley5);
		smiley6 = (ImageButton) findViewById(R.id.smiley6);

		// set custom fonts
		Typeface customFont = Typeface.createFromAsset(getAssets(),"DroidSerif-Bold.ttf");
		Typeface customFont2 = Typeface.createFromAsset(getAssets(), "Cambo-Regular.ttf");

		from.setTypeface(customFont);
		to.setTypeface(customFont);
		sbj.setTypeface(customFont);
		msg.setTypeface(customFont);
		send.setTypeface(customFont);
		fromEdit.setTypeface(customFont2);
		toEdit.setTypeface(customFont2);
		sbjEdit.setTypeface(customFont2);
		msgEdit.setTypeface(customFont2);

		// Get intent, action and MIME type
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				// Handle text being sent
				handleSendText(intent);
			}
		}

		send.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// send the what in the toEdit EditText as an email
				sendMail(toEdit.getText().toString(), sbjEdit.getText().toString(), msgEdit.getText().toString());
			}
		});

		// set click listeners for smileys/emojis
		smiley1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// add emoji/smiley to email 
				msgEdit.append("😁");
			}
		});

		smiley2.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// add emoji/smiley to email
				msgEdit.append("😔");
			}
		});

		smiley3.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// add emoji/smiley to email 
				msgEdit.append("😭");
			}
		});

		smiley4.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// add emoji/smiley to email
				msgEdit.append("😉");
			}
		});

		smiley5.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// add emoji/smiley to email 
				msgEdit.append("😖");
			}
		});

		smiley6.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// add emoji/smiley to email 
				msgEdit.append("😡");
			}
		});

	}

	// get the text sent from my handler
	void handleSendText(Intent intent) {
		String sharedText = intent.getStringExtra(Intent.EXTRA_EMAIL);
		if (sharedText != null) {
			// Update UI to reflect text being shared
			fromEdit.setText(sharedText);
		}
	}

	// create the email message
	private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromEdit.getText().toString(), ""));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
		message.setSubject(subject);
		message.setText(messageBody);
		return message;
	}

	// send the email
	private void sendMail(String email, String subject, String messageBody) {
		Session session = createSessionObject();

		try {
			Message message = createMessage(email, subject, messageBody, session);
			new SendMailTask().execute(message);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private Session createSessionObject() {
		// setup gmail STMP server for sending the email
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		return Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("mdf3test@gmail.com", "devtest3");
			}
		});
	}

	// Send Mail AsyncTask that tries to send the email and catches the possible errors
	private class SendMailTask extends AsyncTask<Message, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Sending email", true, false);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			progressDialog.dismiss();
			if (hasError) {
				failMessage();
			} else {
				successMessage();
			}
		}

		@Override
		protected Void doInBackground(Message... messages) {
			try {
				Transport.send(messages[0]);
			} catch (MessagingException e) {
				e.printStackTrace();
				hasError = true;
				Log.e("Error: ", e.getMessage().toString());
			}
			return null;
		}
	}

	// alert shown when email is successful
	public void successMessage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Email Sent");
		builder.setMessage("Your email was sent successfully!");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Dismiss dialog
			}
		});
		builder.show();
	}

	// alert shown when sending the email is unsuccessful
	public void failMessage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Error");
		builder.setMessage("There was an error and your email wasn't sent.");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Show Toast message
				Toast.makeText(context, "Double check your email address(es) to make sure they're correct.", Toast.LENGTH_LONG).show();
				hasError = false;
			}
		});
		builder.show();
	}
}
