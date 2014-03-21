/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	TopDVDRentals
 * 
 * 	Package: 	com.ChristopherRockwell.topdvdrentals
 * 
 * 	File: 		MainActivity.java
 * 
 *	Purpose:	This activity will output a list of top dvd rental from the Rotten Tomatoes API and display then to the user
 * 
 */



package com.ChristopherRockwell.topdvdrentals;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

// TODO: Auto-generated Javadoc
/**
 * The Class MainActivity.
 */
public class MainActivity extends Activity implements OnClickListener, RentalsFramgment.onRentalsClick {
	static Button getRentalsBtn;
	static Button srcButton;
	String response = null;
	static FileManagerSingleton file;
	static Context mContext;
	public static final String FILE_NAME = "TopRental.txt";
	public static final String MOVIE_KEY = "movie";
	public static final String LIST_KEY = "myList";
	boolean writeWorks;
	static ListView listV;
	public static SmartImageView smrtImg;
	private List<Movie> mList;
	public static Typeface customFont2;
	File mfile;
	Intent secondActivity;
	int selected;
	static MoviesArrayAdapter adapter;
	static boolean checkSrc = false;
	boolean haveResults = false;
	static String srcResult;
	JSONObject castObj;
	String itemText;
	String objText;
	static String dataString;
	static EditText input;
	static TextView listTv1;
	static TextView listTv2;
	static TextView listTv3;
	static TextView listTv4;
	static String eColor;
	static boolean rateBtnClick = false;
	static int selectRadio;
	static EditText fName;
	static EditText lName;
	static boolean parse = true;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.rentals_frament);
			//check to see if the users is back from the second activity and display the item they recently viewed in portrait mode
			if (dataString != null) {
				InfoFragment fragment = (InfoFragment) getFragmentManager().findFragmentById(R.id.myinfo_fragment);
				fragment.myData = dataString;
				fragment.DisplayMovie();
			}
		} else {
			setContentView(R.layout.main_fragment);
		}


		getRentalsBtn = (Button) this.findViewById(R.id.rentalsBtn);
		srcButton = (Button) this.findViewById(R.id.srcBtn);
		getRentalsBtn.setOnClickListener(this);
		mContext = this;
		mfile = mContext.getFileStreamPath(FILE_NAME);

		// custom typefaces 
		Typeface customFont = Typeface.createFromAsset(this.getAssets(), "Exo2-Bold.ttf");
		customFont2 = Typeface.createFromAsset(this.getAssets(), "Exo2-Medium.ttf");

		getRentalsBtn.setTypeface(customFont);
		srcButton.setTypeface(customFont);

		listV = (ListView) this.findViewById(R.id.listView1);
		View listHeader = this.getLayoutInflater().inflate(R.layout.list_header, null);

		// reference smart ImaveView to be used when setting the movie poster image
		smrtImg = (SmartImageView) findViewById(R.id.img);

		// set custom text for the list headers
		listTv1 = (TextView) listHeader.findViewById(R.id.title_header);
		listTv2 = (TextView) listHeader.findViewById(R.id.img_header);
		listTv3 = (TextView) listHeader.findViewById(R.id.rating1_header);
		listTv4 = (TextView) listHeader.findViewById(R.id.rating2_header);
		listTv1.setTypeface(customFont);
		listTv2.setTypeface(customFont);
		listTv3.setTypeface(customFont);
		listTv4.setTypeface(customFont);

		listV.addHeaderView(listHeader);

		file = FileManagerSingleton.getInstance();

		// check for savedInstance and populate list
		if (savedInstanceState != null) {
			mList = (List<Movie>) savedInstanceState.getSerializable(LIST_KEY);

			if (mList != null) {
				Log.i("List: ", "Not null");
				adapter = new MoviesArrayAdapter(MainActivity.this, R.layout.list_row, mList);

				listV.setAdapter(adapter);
				haveResults = true;
				// retain the search data in the list view
				if (savedInstanceState.getBoolean("bool")) {
					adapter.getFilter().filter(savedInstanceState.getString("eText").toString());
					checkSrc = true;
				}

			} else {
				Log.i("List: ", "null");
			}
		}

		// check for preference color
		if (eColor == "default" || eColor == null) { 
			// set default element colors
			getRentalsBtn.setBackgroundColor(getResources().getColor(R.color.btn_color));
			srcButton.setTextColor(getResources().getColor(R.color.btn_color));
		} else if (eColor == "purple") {
			// set purple element colors 
			getRentalsBtn.setBackgroundColor(getResources().getColor(R.color.btn_color2));
			srcButton.setTextColor(getResources().getColor(R.color.btn_color2));
			listTv1.setTextColor(getResources().getColor(R.color.btn_color2));
			listTv2.setTextColor(getResources().getColor(R.color.btn_color2));
			listTv3.setTextColor(getResources().getColor(R.color.btn_color2));
			listTv4.setTextColor(getResources().getColor(R.color.btn_color2));
		} else if (eColor == "blue") {
			// set blue element colors 
			getRentalsBtn.setBackgroundColor(getResources().getColor(R.color.btn_color3));
			srcButton.setTextColor(getResources().getColor(R.color.btn_color3));
			listTv1.setTextColor(getResources().getColor(R.color.btn_color3));
			listTv2.setTextColor(getResources().getColor(R.color.btn_color3));
			listTv3.setTextColor(getResources().getColor(R.color.btn_color3));
			listTv4.setTextColor(getResources().getColor(R.color.btn_color3));
		}

		// create intent and list view click listener in preparation of moving to the second view
		secondActivity = new Intent(mContext,InfoActivity.class);

		listV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				//selected = position - 1;
				itemText = listV.getItemAtPosition(position).toString();

				try {
					// send specific movie to the info activity
					String fileString = file.readStrFile(mContext, FILE_NAME);
					JSONObject obj = new JSONObject(fileString);
					JSONArray movies = obj.getJSONArray("movies");
					for (int i = 0; i < movies.length(); i++) {
						castObj = movies.getJSONObject(i);
						objText = castObj.getString("title");
						if (itemText.contains(objText)) {
							secondActivity.putExtra(MOVIE_KEY, castObj.toString());
							if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
								InfoFragment fragment = (InfoFragment) getFragmentManager().findFragmentById(R.id.myinfo_fragment);
								fragment.myData = castObj.toString();
								fragment.DisplayMovie();
								Log.i("movieObj:", castObj.toString() );
							} else {
								startActivityForResult(secondActivity,0);
							}
						}
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					Log.e("Error: ", e.getMessage().toString());
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						// do nothing
					} else {
						startActivityForResult(secondActivity,1);
					}
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.e("Error: ", e.getMessage().toString());
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						// do nothing
					} else {
						startActivityForResult(secondActivity,1);
					}
					e.printStackTrace();
				}

			}
		});
		//enables filtering for the contents of the given ListView
		listV.setTextFilterEnabled(true);
		srcButton.setOnClickListener(new OnClickListener() {
			// TODO Auto-generated method stub
			@Override
			public void onClick(View v) {
				// setup alert dialog that allows users to search the listView
				AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle("Search by Minimum Critic Rating");
				alert.setMessage("Input Minimum Rating ");
				// Set an EditText view to get user input 
				input = new EditText(mContext);
				input.setText(srcResult);
				alert.setView(input);
				alert.setPositiveButton("Search", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						srcResult = input.getText().toString();
						// check to see if get items has been clicked and the results are in the listView
						if (haveResults) {
							// check to see if its a number and between 0 and 100
							if (srcResult.matches("\\d+") && Integer.parseInt(srcResult) >= 0 && 
									Integer.parseInt(srcResult) <= 100) {
								adapter.getFilter().filter(srcResult);
								checkSrc = true;
								if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
									InfoFragment.infoView.setText(R.string.dummy_text);
									InfoFragment.titleView.setText(R.string.dummy_text);
								}
							} else {
								Toast.makeText(mContext, "Please enter a number between 0 and 100", Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(mContext, "You must get rental results before searching.", Toast.LENGTH_LONG).show();
						}
					}
				});
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
				// clear search query
				alert.setNeutralButton("Clear Search", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (checkSrc) {
							adapter.getFilter().filter("0");
						}
						checkSrc = false;
						srcResult = "";
					}
				});
				alert.show();
			}
		});

		// force click the rentals button for better data retention 
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && dataString != null || file.clickedRentals) {
			getRentalsBtn.performClick();

		}

		// check to see if shared preferences exist and if so display welcome message
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String firstName = sharedPreferences.getString("first", "");
		String lastName = sharedPreferences.getString("last", "");
		if (firstName != "" && lastName != "") {
			if (file.hasRan == false) {
				Toast.makeText(mContext, "Hello, Welcome " + firstName + " " + lastName, Toast.LENGTH_LONG).show();
				file.hasRan = true;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.search_menu_item) {
			showDialog();
		} else if (item.getItemId() == R.id.color_menu_item) {
			showColorsDialog();
		} else if (item.getItemId() == R.id.preferences_menu_item) {
			showPrefsDialog();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View arg0) {
		rateBtnClick = true;
		onRentalsButtonClick();
		file.clickedRentals = true;
	}  
	public List<Movie> getList() {
		return this.mList;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 0) {
			Bundle result = data.getExtras();
			Toast.makeText(mContext, "You just viewed " + result.getString("srcMovie") + " movie info.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		if (mList != null && !mList.isEmpty()) {
			savedInstanceState.putSerializable(LIST_KEY, (Serializable) mList);
			savedInstanceState.putBoolean("bool", checkSrc);
			savedInstanceState.putString("eText", srcResult);
			Log.i("Saved: ", "Instance data saved!");
		}
	}

	public void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
		savedInstanceState.getSerializable(LIST_KEY);
		checkSrc = savedInstanceState.getBoolean("bool");
		srcResult = savedInstanceState.getString("eText");
		Log.i("Restored: ", "Instance data restored!");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// restart the activity when the orientation is changed
		Intent firstActivity = new Intent(mContext,MainActivity.class);
		startActivity(firstActivity);
	}

	public void onRentalsButtonClick() {
		// check to see if there's a valid connection
		if (file.connectionStatus(mContext)){
			// TODO Auto-generated method stub
			Handler getRentalsHandler = new Handler(new Handler.Callback() {

				@Override
				public boolean handleMessage(Message msg) {
					// TODO Auto-generated method stub


					if (msg.arg1 == RESULT_OK && msg.obj != null) {
						try {
							response = (String)msg.obj;
							TopRentalsService.writeStrFile(mContext, FILE_NAME, response);
							writeWorks = true;
							Toast.makeText(mContext, "Wrote data to file", Toast.LENGTH_SHORT).show();

						} catch (Exception e) {
							writeWorks = false;
							// TODO Auto-generated catch block
							Log.e("Error: ", e.getMessage().toString());
							Toast.makeText(mContext, "Error: Couldn't write data to file", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}

					// if the write data works, read the the data
					if (writeWorks && parse) {
						readParse();
					}
					return true;
				}
			});
			Messenger rentalsMessenger = new Messenger(getRentalsHandler);
			Intent startRentalsIntent = new Intent(this, TopRentalsService.class);
			startRentalsIntent.putExtra(TopRentalsService.MSGR_KEY, rentalsMessenger);
			startRentalsIntent.putExtra(TopRentalsService.URL_STR, "http://api.rottentomatoes.com/api/public/v1.0/lists/dvds/top_rentals.json?apikey=bf72tfc2zjfbdscenpwx2e2r");

			startService(startRentalsIntent);
			// If the user doesn't have a connection, but has the txt file then the data will still output
		} else if (mfile.exists() && file.connectionStatus(mContext) == false) {
			readParse();
			// Check for no connection
		} else {
			// create alert dialog for users without a valid internet connection
			AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
			builder1.setTitle("No Connection");
			builder1.setMessage("You don't have a valid internet connection.");
			builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Check your connection and try again.", Toast.LENGTH_LONG).show();
				}
			});

			// show alert
			builder1.show();
		}
	}

	public void readParse(){
		String fileString = file.readStrFile(mContext, FILE_NAME);
		Toast.makeText(mContext, "Read data from file", Toast.LENGTH_SHORT).show();
		String result;
		String title, img, criticScore, audienceScore;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		mList = new ArrayList<Movie>();

		try {
			// set JSONOject and cast into array the back into an object to get movie proper info
			JSONObject obj = new JSONObject(fileString);
			JSONArray movies = obj.getJSONArray("movies");

			for (int i = 0; i < movies.length(); i++) {
				JSONObject castObj = movies.getJSONObject(i);
				JSONObject antrCastObj = castObj.getJSONObject("ratings");
				JSONObject imgCast = castObj.getJSONObject("posters");

				title = castObj.getString("title");
				img = imgCast.getString("profile");
				criticScore = antrCastObj.getString("critics_score");
				audienceScore = antrCastObj.getString("audience_score");

				result = "Title: " + title + "\r\n"
						+ "Year Released: " + img + "\r\n"
						+ "Critic Score: " + criticScore + "%\r\n"
						+ "Audience Score: " + audienceScore + "%\r\n";
				Log.i("Result", result);
				Movie movie = new Movie(title, img, criticScore, audienceScore);
				mList.add(movie);
				HashMap<String, String> displayMap = new HashMap<String, String>();
				displayMap.put("title", title);
				displayMap.put("critic", criticScore);
				displayMap.put("audience", audienceScore);

				list.add(displayMap);
			}

			//							SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, list, R.layout.list_row, 
			//									new String[] {"title", "critic", "audience"}, 
			//									new int[] {R.id.title, R.id.rating1, R.id.rating2});

			adapter = new MoviesArrayAdapter(MainActivity.this, R.layout.list_row, mList);

			listV.setAdapter(adapter);
			haveResults = true;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("Error: ", e.getMessage().toString());
			Toast.makeText(mContext, "Error: Couldn't retrieve the data", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		if (checkSrc) {
			srcResult = input.getText().toString();
			// check to see if get items has been clicked and the results are in the listView
			// check to see if its a number and between 0 and 100
			if (srcResult.matches("\\d+") && Integer.parseInt(srcResult) >= 0 && 
					Integer.parseInt(srcResult) <= 100) {
				adapter.getFilter().filter(srcResult);
				checkSrc = true;
			} else {
				Toast.makeText(mContext, "Please enter a number between 0 and 100", Toast.LENGTH_LONG).show();
			}
		}
	}

	public static class AlertDialogFragment extends DialogFragment {
		public static AlertDialogFragment newInstance(int title) {
			AlertDialogFragment frag = new AlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int title = getArguments().getInt("title");
			input = new EditText(mContext);
			input.setText(srcResult);



			return new AlertDialog.Builder(getActivity())
			.setIcon(null)
			.setTitle(title)
			.setMessage("Input Minimum Rating ")
			.setView(input)
			.setPositiveButton("Search",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					((MainActivity)getActivity()).doPositiveClick();
				}
			}
					)
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							((MainActivity)getActivity()).doNegativeClick();
						}
					}
							)
							.setNeutralButton("Clear Search",
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									if (checkSrc) {
										adapter.getFilter().filter("0");
									}
									checkSrc = false;
									srcResult = "";
								}
							}
									)
									.create();
		}
	}

	public static class ThemeDialogFragment extends DialogFragment {
		public static ThemeDialogFragment newInstance() {
			ThemeDialogFragment frag = new ThemeDialogFragment();
			return frag;

		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final CharSequence[] Themes = {"Default", "Purple", "Blue"};

			if (eColor == "default" || eColor == null) {
				selectRadio = 0;
			} else if (eColor == "purple") {
				selectRadio = 1;
			} else if (eColor == "blue") {
				selectRadio = 2;
			}

			return new AlertDialog.Builder(getActivity())
			.setIcon(null)
			.setTitle("Change element colors")
			.setSingleChoiceItems(Themes, selectRadio, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						// set default element colors
						getRentalsBtn.setBackgroundColor(getResources().getColor(R.color.btn_color));
						srcButton.setTextColor(getResources().getColor(R.color.btn_color));
						listTv1.setTextColor(getResources().getColor(R.color.btn_color));
						listTv2.setTextColor(getResources().getColor(R.color.btn_color));
						listTv3.setTextColor(getResources().getColor(R.color.btn_color));
						listTv4.setTextColor(getResources().getColor(R.color.btn_color));
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							InfoFragment.titleView.setTextColor(getResources().getColor(R.color.btn_color));
							InfoFragment.infoView.setTextColor(getResources().getColor(R.color.btn_color));
							InfoFragment.textButton.setTextColor(getResources().getColor(R.color.btn_color));
							InfoFragment.posterButton.setTextColor(getResources().getColor(R.color.btn_color));
						}
						eColor = "default";
						if (rateBtnClick) {
							getRentalsBtn.performClick();
						}
					} else if (item == 1) {
						// set purple element colors 
						getRentalsBtn.setBackgroundColor(getResources().getColor(R.color.btn_color2));
						srcButton.setTextColor(getResources().getColor(R.color.btn_color2));
						listTv1.setTextColor(getResources().getColor(R.color.btn_color2));
						listTv2.setTextColor(getResources().getColor(R.color.btn_color2));
						listTv3.setTextColor(getResources().getColor(R.color.btn_color2));
						listTv4.setTextColor(getResources().getColor(R.color.btn_color2));
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							InfoFragment.titleView.setTextColor(getResources().getColor(R.color.btn_color2));
							InfoFragment.infoView.setTextColor(getResources().getColor(R.color.btn_color2));
							InfoFragment.textButton.setTextColor(getResources().getColor(R.color.btn_color2));
							InfoFragment.posterButton.setTextColor(getResources().getColor(R.color.btn_color2));
						}
						eColor = "purple";
						if (rateBtnClick) {
							getRentalsBtn.performClick();
						}
					} else {
						// set blue element colors 
						getRentalsBtn.setBackgroundColor(getResources().getColor(R.color.btn_color3));
						srcButton.setTextColor(getResources().getColor(R.color.btn_color3));
						listTv1.setTextColor(getResources().getColor(R.color.btn_color3));
						listTv2.setTextColor(getResources().getColor(R.color.btn_color3));
						listTv3.setTextColor(getResources().getColor(R.color.btn_color3));
						listTv4.setTextColor(getResources().getColor(R.color.btn_color3));
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							InfoFragment.titleView.setTextColor(getResources().getColor(R.color.btn_color3));
							InfoFragment.infoView.setTextColor(getResources().getColor(R.color.btn_color3));
							InfoFragment.textButton.setTextColor(getResources().getColor(R.color.btn_color3));
							InfoFragment.posterButton.setTextColor(getResources().getColor(R.color.btn_color3));
						}
						eColor = "blue";
						if (rateBtnClick) {
							getRentalsBtn.performClick();
						}
					}
					Toast.makeText(mContext, "Colors changed to "+Themes[item], Toast.LENGTH_SHORT).show();
				}
			})
			.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do nothing
				}
			}
					)
					.create();
		}
	}

	public static class PrefsDialogFragment extends DialogFragment {
		public static PrefsDialogFragment newInstance() {
			PrefsDialogFragment frag = new PrefsDialogFragment();
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			fName = new EditText(mContext);
			lName = new EditText(mContext);
			fName.setHint("Enter First Name");
			lName.setHint("Enter Last Name");

			// check for values in shared preferences and populate edit text fields with those values
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String firstName = sharedPreferences.getString("first", "");
			String lastName = sharedPreferences.getString("last", "");
			if (firstName != null && lastName != null) {
				fName.setText(firstName);
				lName.setText(lastName);
			}

			LinearLayout layout = new LinearLayout(mContext);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(fName);
			layout.addView(lName);
			return new AlertDialog.Builder(getActivity())
			.setIcon(null)
			.setTitle("Input your name")
			.setView(layout)
			.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// remove whitespace
					String first = fName.getText().toString().replace(" ", "");
					String last = lName.getText().toString().replace(" ", "");
					if (!first.matches("") && !last.matches("")) {
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
						Editor edit = sharedPreferences.edit();
						edit.putString("first", first);
						edit.putString("last", last);
						edit.apply(); 
						// pull from preferences
						String firstN = sharedPreferences.getString("first", "");
						String lastN = sharedPreferences.getString("last", "");
						Toast.makeText(mContext, "Hello, Welcome " + firstN + " " + lastN, Toast.LENGTH_LONG).show();
						file.hasRan = true;
					} else {
						Toast.makeText(mContext, "Please enter your first and last name to save to preferencess.", Toast.LENGTH_LONG).show();
					}
				}
			}
					)
					.setNegativeButton("Cancel", 
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Do nothing
						}
					}
							)
							.setNeutralButton("Clear Preferences", 
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
									sharedPreferences.edit().clear().commit();
								}
							}
									)
									.create();
		}
	}


	void showDialog() {
		DialogFragment newFragment = AlertDialogFragment.newInstance(
				R.string.alert);
		newFragment.show(getFragmentManager(), "dialog");
	}

	void showColorsDialog() {
		DialogFragment newFragment = ThemeDialogFragment.newInstance();
		newFragment.show(getFragmentManager(), "dialog");
	}

	void showPrefsDialog() {
		DialogFragment newFragment = PrefsDialogFragment.newInstance();
		newFragment.show(getFragmentManager(), "dialog");
	}

	public void doPositiveClick() {
		// Do stuff here.
		srcResult = input.getText().toString();
		// check to see if get items has been clicked and the results are in the listView
		if (haveResults) {
			// check to see if its a number and between 0 and 100
			if (srcResult.matches("\\d+") && Integer.parseInt(srcResult) >= 0 && 
					Integer.parseInt(srcResult) <= 100) {
				adapter.getFilter().filter(srcResult);
				checkSrc = true;
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					InfoFragment.infoView.setText(R.string.dummy_text);
					InfoFragment.titleView.setText(R.string.dummy_text);
				}
			} else {
				Toast.makeText(mContext, "Please enter a number between 0 and 100", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(mContext, "You must get rental results before searching.", Toast.LENGTH_LONG).show();
		}
	}

	public void doNegativeClick() {
		// Do stuff here.
		Log.i("FragmentAlertDialog", "Negative click!");
	}

}
