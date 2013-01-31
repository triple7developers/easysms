package org.easysms.android;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.easysms.android.ui.KaraokeLayout;
import org.easysms.android.util.TextToSpeechManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

@TargetApi(8)
public class MessagingActivity extends SherlockActivity {

	/**
	 * Handles the response of the broadcast request about the recognizer
	 * supported languages.
	 * 
	 * The receiver is required only if the application wants to do recognition
	 * in a specific language.
	 */
	private class SupportedLanguageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, final Intent intent) {
			Log.i("MessageActivity", "Receiving broadcast " + intent);

			final Bundle extra = getResultExtras(false);

			if (getResultCode() != Activity.RESULT_OK) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						showToast("Error code:" + getResultCode());
						TextToSpeechManager.getInstance().say(
								"Error code:" + getResultCode());
					}
				});
			}

			if (extra == null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						showToast("No extra");
					}
				});
			}

			if (extra.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES)) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
					}
				});
			}

			if (extra.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE)) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
					}
				});
			}
		}

		private void showToast(String text) {
			// Toast.makeText(this, text, 1000).show();
		}
	}

	private static final int PICK_CONTACT = 4321;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	private KaraokeLayout flowlayout;
	private ArrayList<String> matches;
	private Handler mHandler;
	private String phoneNo = "";
	private ImageView profile;
	private TextView recipient;
	private TextView recipientnum;
	private ImageView speakButton;
	private LinearLayout speechrecolayout;

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {

			// fill the list view with the strings the recognizer thought it
			// could have heard
			matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			final Vibrator vibrationadd = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			// fill the list view with the strings the recognizer thought it
			// could have heard

			speechrecolayout.removeAllViewsInLayout();

			for (int it = 0; it < 3; it++) {

				Date currentDate = new Date(System.currentTimeMillis());
				String date = (String) android.text.format.DateFormat.format(
						"yyyy-MM-dd'T'kk:mm:ss'Z'", currentDate);

				LinearLayout wholelayout = new LinearLayout(this);
				LinearLayout.LayoutParams layoutParamsWhole = new LinearLayout.LayoutParams(
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 310,
								getResources().getDisplayMetrics()),
						LinearLayout.LayoutParams.WRAP_CONTENT);
				layoutParamsWhole.setMargins(0, 5, 0, 5);
				wholelayout.setLayoutParams(layoutParamsWhole);
				wholelayout.setOrientation(LinearLayout.HORIZONTAL);
				// create a new flow layout for each choice
				final KaraokeLayout fl = new KaraokeLayout(this);// bubble
				// conversation
				LayoutParams layoutParams = new LayoutParams(
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 260,
								getResources().getDisplayMetrics()),
						LayoutParams.WRAP_CONTENT);
				fl.setLayoutParams(layoutParams);
				fl.setBackgroundResource(R.drawable.bubblelast);

				// add the microphone
				// ImageView micro = new ImageView(this);
				// micro.setBackgroundResource(R.drawable.androidmic);
				// fl.addView(micro);
				// add the view number
				ImageView number = new ImageView(this);
				if (it == 0) {
					number.setBackgroundResource(R.drawable.one);
					fl.addView(number);

				} else if (it == 1) {
					number.setBackgroundResource(R.drawable.two);
					fl.addView(number);

				} else if (it == 2) {
					number.setBackgroundResource(R.drawable.three);
					fl.addView(number);

				}

				String str = matches.get(it).toString();
				// write the 3 voice recognition results

				StringTokenizer st = new StringTokenizer(str);
				String[] tabWords = new String[100];
				int nbWords = 0;
				while (st.hasMoreElements()) {
					tabWords[nbWords] = (String) st.nextElement();
					nbWords++;
				}
				// create a button for each words and append it to the bubble
				// composition
				for (int i = 0; i < nbWords; ++i) {
					final Button btn = new Button(this);
					btn.setText(tabWords[i]);
					// btn.setTextSize((int)
					// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,10,
					// getResources().getDisplayMetrics()));
					btn.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					btn.setBackgroundResource(R.drawable.button);

					final String toSay = tabWords[i];
					// play each button
					btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// String sentence = tabWords[i];
							// btn.setBackgroundColor(Color.RED);

							// plays the audio.
							TextToSpeechManager.getInstance().say(toSay);

						}
					});
					final Vibrator vibrationdelete = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					// ici code
					btn.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {

							final Button bouton = new Button(getBaseContext());
							bouton.setText(btn.getText());
							bouton.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
							bouton.setBackgroundResource(R.drawable.button);

							// play each button
							bouton.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {

									// plays the audio.
									TextToSpeechManager.getInstance()
											.say(toSay);

								}
							});

							final Vibrator vibrationdelete = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
							// on long click, delete the button
							bouton.setOnLongClickListener(new OnLongClickListener() {
								@Override
								public boolean onLongClick(View v) {
									flowlayout.removeView(bouton);

									vibrationdelete.vibrate(200);
									return true;
								}
							});
							// add the button to the flow layout
							flowlayout.addView(bouton, new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
							vibrationdelete.vibrate(200);

							return true;
						}
					});

					// ((MarginLayoutParams) layoutParams).setMargins(50, 80,
					// 50, 80);
					fl.addView(btn);

					vibrationadd.vibrate(200);
				} // end for each word

				wholelayout.addView(fl);
				speechrecolayout.addView(wholelayout);
				// play button
				ImageView playbutton = new ImageView(this);
				playbutton.setBackgroundResource(R.drawable.playsmsclick);
				playbutton.setOnCreateContextMenuListener(this);
				wholelayout.addView(playbutton);
				playbutton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// playKaraoke(fl);
					}

				});
				final Vibrator vibrationvoicerecog = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

				number.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						String sentenceChoosen = "";
						for (int i = 1; i < fl.getChildCount(); ++i) {
							// get the first word of the results
							Button btn = (Button) fl.getChildAt(i);
							// create a new word with the same characteristics
							final Button bouton = new Button(getBaseContext());
							bouton.setText(btn.getText());
							// bouton.setTextSize((int)
							// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,10,
							// getResources().getDisplayMetrics()));
							bouton.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
							bouton.setBackgroundResource(R.drawable.button);
							// add the button to the flow layout
							flowlayout.addView(bouton, new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
							sentenceChoosen += btn.getText();

						}
						vibrationvoicerecog.vibrate(200);

						return true;
					}

				});

				fl.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						String sentenceChoosen = "";
						for (int i = 1; i < fl.getChildCount(); ++i) {
							// get the first word of the results
							Button btn = (Button) fl.getChildAt(i);
							// create a new word with the same characteristics
							final Button bouton = new Button(getBaseContext());
							bouton.setText(btn.getText());
							// bouton.setTextSize((int)
							// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,10,
							// getResources().getDisplayMetrics()));
							bouton.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
							bouton.setBackgroundResource(R.drawable.button);
							// add the button to the flow layout
							flowlayout.addView(bouton, new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT));
							sentenceChoosen += btn.getText();

						}
						vibrationvoicerecog.vibrate(200);

						return true;
					}

				});

			}// end main for loop
		}

		else if (requestCode == PICK_CONTACT) {
			if (resultCode == Activity.RESULT_OK) {

				phoneNo = "";
				Uri contactData = data.getData();
				Cursor cur = getContentResolver().query(contactData, null,
						null, null, null);
				String nameContact = "Nom inconnu";
				String photoId = null;
				if (cur.moveToFirst()) {
					String id = cur.getString(cur
							.getColumnIndexOrThrow(Phone._ID));
					// contact name
					nameContact = cur
							.getString(cur
									.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
					// photoID
					long photo = cur
							.getLong(cur
									.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID));
					String no = "Num�ro inconnu";
					// if the contact has a phone number
					if (Integer
							.parseInt(cur.getString(cur
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						Cursor pCur = getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = ?", new String[] { id },
										null);
						// this second loop will retrieve all the contact
						// numbers for a particular contact id
						String mobilePhone = "Inconnu";
						String homePhone = "Inconnu";
						String workPhone = "Inconnu";
						String otherPhone = "Inconnu";

						if (pCur != null) {
							while (pCur.moveToNext()) {
								// takes only the MOBILE number
								if (pCur.getInt(pCur.getColumnIndex(Phone.TYPE)) == Phone.TYPE_MOBILE) {
									/*
									 * int phNumber =
									 * pCur.getColumnIndexOrThrow(
									 * ContactsContract
									 * .CommonDataKinds.Phone.NUMBER); no =
									 * pCur.getString(phNumber);
									 */
									switch (pCur.getInt(pCur
											.getColumnIndex(Phone.TYPE))) {
									case Phone.TYPE_MOBILE:
										mobilePhone = pCur.getString(pCur
												.getColumnIndex(Phone.NUMBER));
										break;
									case Phone.TYPE_HOME:
										homePhone = pCur.getString(pCur
												.getColumnIndex(Phone.NUMBER));
										break;
									case Phone.TYPE_WORK:
										workPhone = pCur.getString(pCur
												.getColumnIndex(Phone.NUMBER));
										break;
									case Phone.TYPE_OTHER:
										otherPhone = pCur.getString(pCur
												.getColumnIndex(Phone.NUMBER));
										break;
									}
								}
							}
							if (mobilePhone != "Inconnu") {
								no = mobilePhone;
							}
						}

						pCur.close();
					}

					photoId = cur.getString(cur
							.getColumnIndex(Contacts.PHOTO_ID));
					if (photo != 0) {
						Cursor photo2 = getContentResolver().query(
								// column for the blob
								Data.CONTENT_URI, new String[] { Photo.PHOTO },
								Data._ID + "=?", // select row by id
								new String[] { photoId }, // filter by photoId
								null);

						if (photo2.moveToFirst()) {
							byte[] photoBlob = photo2.getBlob(photo2
									.getColumnIndex(Photo.PHOTO));
							Bitmap photoBitmap = BitmapFactory.decodeByteArray(
									photoBlob, 0, photoBlob.length);
							profile.setImageBitmap(photoBitmap);
						}
						photo2.close();
					} else {

						profile.setImageResource(R.drawable.nophotostored);
					}

					/*
					 * if (photo != 0) { Uri uri =
					 * ContentUris.withAppendedId(People.CONTENT_URI, photo);
					 * Bitmap bitmap = People.loadContactPhoto(getBaseContext(),
					 * uri, R.drawable.icon, null);
					 * profile.setImageBitmap(bitmap); }
					 */

					// reinitialise le textview
					recipient.setText("");
					recipientnum.setText("");
					// append new name selected
					recipient.append(nameContact);
					recipientnum.append(no);
					phoneNo += no;

					id = null;
					nameContact = null;
					no = null;

					cur.close();
					cur = null;

				}
			}
		} else {
			// plays the audio.
			TextToSpeechManager.getInstance().say(
					"Il n'y a pas de connection internete.");

		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// checks the bundle to handle correctly the two cases.
		Bundle bundle = getIntent().getExtras();
		Boolean newMsg = bundle.getBoolean(MessageActivity.NEW_MESSAGE_EXTRA);

		if (newMsg) { // if new message don't display the message details page
			setContentView(R.layout.act_new_message);

			profile = (ImageView) findViewById(R.id.selectcontact);
			profile.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_PICK,
							ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(intent, PICK_CONTACT);
				}

			});
			recipient = (TextView) findViewById(R.id.contactname);
			recipientnum = (TextView) findViewById(R.id.contactnumber);

		}
		// --------------------speech to text-------------------
		mHandler = new Handler();
		// Get display items for later interaction
		// ImageView speakButton = (ImageView) findViewById(R.id.btn_speak);

		// Check to see if a recognition activity is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
			// speakButton.setOnClickListener(this);
		} else {
			speakButton.setEnabled(false);
			// speakButton.setText("Recognizer not present");
		}
	}
}
