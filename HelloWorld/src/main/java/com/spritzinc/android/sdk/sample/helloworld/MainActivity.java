package com.spritzinc.android.sdk.sample.helloworld;
//import org.apache.pdfbox.pdmodel.*;
//import org.apache.pdfbox.util.*;

import com.spritzinc.android.sdk.sample.helloworld.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.spritzinc.android.SimpleSpritzSource;
import com.spritzinc.android.SpritzSource;
import com.spritzinc.android.UrlSpritzSource;
import com.spritzinc.android.sdk.SpritzSDK;
import com.spritzinc.android.sdk.SpritzUser;
import com.spritzinc.android.sdk.view.SpritzBaseView;
import com.spritzinc.android.sdk.view.SpritzControlView;

import java.util.Locale;
import java.io.*;
import java.lang.*;

public class MainActivity extends Activity implements SpritzSDK.LoginEventListener,
		SpritzSDK.LoginStatusChangeListener {

	private static final int VIEW_TYPE_BASE = 1;
	private static final int VIEW_TYPE_CONTROL = 2;
	private static final int VIEW_TYPE_FULL = 3;

	private static final int START_MODE_START = 1;
	private static final int START_MODE_LOAD = 2;

	private static final String SHARED_PREFS_NAME = "main";

	private static final String EXTRA_VIEW_TYPE = "viewType";

	private static final String PREF_VIEW_TYPE = "viewType";
	private static final String PREF_SPEED = "speed";
	private static final String PREF_START_MODE = "startMode";

	private SharedPreferences sharedPrefs;

	private RadioGroup rgMode;
	private FrameLayout flSpritzViewContainer;
    private SpritzBaseView spritzView;
	private TextView tvInfo;
	
	static private EditText inputField;
	static private String input;
	
	private Button playButton;
	private Button pauseButton;
	private Button backButton;
	private Button forwardButton;
	private Button rewindButton;
	private Button resetButton;
	private Button slowerButton;
	private Button fasterButton;
	
	private File sdCard;
	private File testFile;
	private String fileString;
	
	private void filePopulate(File sdCard) { /*
		int i = 0;
		String filePath = "";
		if (sdCard != null) {
			while (i != sdCard.length) {
				filePath = sdCard[i].getAbsolutePath();
				if (sdCard[i].isDirectory()) {
					File temp[] = sdCard[i].listFiles();
					filePopulate(temp);
				}
				
			}
		} */
		testFile = new File(sdCard, "test.txt");
		Log.v("Terence", sdCard.getAbsolutePath());
		Log.v("Terence", testFile.getAbsolutePath());
		if (!testFile.exists()) {
			Log.v("Terence", "File doesn't even exist, dumbass");
		}
		if (!testFile.canRead()) {
			Log.v("Terence", "File's broken, dumbass");
		}
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader tempBr = new BufferedReader(new FileReader(testFile));
			String line;
			while ((line = tempBr.readLine()) != null) {
				builder.append(line);
				builder.append('\n');
			}
		}
		catch (IOException e) {
			Log.v("Terence", e.toString());
			// Fill this in when I know what I'm doing lololol
		}
		fileString = builder.toString();
		/*StringBuilder tempStr = new StringBuilder();
		try {
			BufferedReader tempBr = new BufferedReader(new FileReader(testFile));
			String line;
			while ((line = tempBr.readLine()) != null) {
				tempStr.append(line);
				tempStr.append('\n');
			}
			fileString = tempStr.toString();
		}
		catch (IOException e) {
			// Fill this in when I know what I'm doing lololol
		}*/
		Log.v("Terence", "Thing is doing! " + fileString + " is the string.");
	}
	
	/* Public Static Methods */
	public static void startActivity(Context context, int viewType) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(EXTRA_VIEW_TYPE, viewType);
		context.startActivity(intent);
		
	}

	/* Constructors */

	/* Private Properties */

	private SharedPreferences getSharedPrefs() {
		if (sharedPrefs == null) {
			sharedPrefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		}

		return sharedPrefs;
	}

	private boolean isStartMode() {
		boolean startMode = rgMode.getCheckedRadioButtonId() == R.id.rbModeStart;
		return startMode;
	}

	/* Public Methods */

	public void onBtnFasterClick(View view) {
		spritzView.setSpeed(spritzView.getSpeed() + 5);
		updateInfo();
	}

    public void onBtnHelloWebClick(View view) { // Spritz from web
		Log.v("debug", "Spritz from web button has been pressed");
		input = inputField.getText().toString();
		if (!input.startsWith("http://")) {
			input = "http://" + input;
		}
		UrlSpritzSource source = new UrlSpritzSource(input);
        // UrlSpritzSource source = new UrlSpritzSource("http://www.aeaweb.org/sample_references.pdf"); //http://sdk.spritzinc.com/sampleText/HelloWorld.html
		Log.v("debug", "Spritzing now");
		spritz(source);
		Log.v("debug", "made it!");
    }

    public void onBtnHelloWeb2Click(View view) { // Spritz from string
		
		input = inputField.getText().toString();
		SimpleSpritzSource source = new SimpleSpritzSource(input, new Locale("en, US"));
		
		spritz(source);
		
    }
    public void onBtnClearClick(View view) {
    	inputField.setText("");
    }
    public void onBtnFileReadClick(View view) {
    	SimpleSpritzSource source = new SimpleSpritzSource(fileString, new Locale("en, US"));
    	
    	spritz(source);
    }
	public void onBtnLoginClick(View view) {
		SpritzSDK sdk = SpritzSDK.getInstance();
		
		if (sdk.getLoggedInUser() == null) {
			sdk.loginUser(getFragmentManager());
		} else {
			sdk.logoutUser();
		}
	}

	public void onBtnPauseClick(View view) {
		spritzView.pause();
		updateInfo();
	}

	public void onBtnPlayClick(View view) {
		spritzView.resume();
		updateInfo();
	}

	public void onBtnBackupClick(View view) {
		spritzView.goBackSentence();
		updateInfo();
	}

	public void onBtnForwardClick(View view) {
		spritzView.goForwardSentence();
		updateInfo();
	}

	public void onBtnResetClick(View view) {
		spritzView.reset();
		updateInfo();
	}

	public void onBtnRewindClick(View view) {
		spritzView.rewind();
		updateInfo();
	}

	public void onBtnSlowerClick(View view) {
		spritzView.setSpeed(spritzView.getSpeed() - 5);
		updateInfo();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main_activity, menu);

		return true;
	}

	@Override
	public void onLoginFail(String errorMessage, Throwable throwable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Login Failed");

		StringBuilder message = new StringBuilder();
		message.append(errorMessage);

		if (throwable != null) {
			message.append(": ");
			message.append(throwable.getMessage());
		}

		builder.setMessage(message.toString());
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Ignore
			}
		});
		builder.show();
	}

	@Override
	public void onLoginStart() {
		// Ignore
	}

	@Override
	public void onLoginSuccess() {
		// Ignore
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled;

		switch (item.getItemId()) {
			case R.id.mi_view_type_base:
				relaunch(VIEW_TYPE_BASE);
				handled = true;
				break;
			case R.id.mi_view_type_control:
				relaunch(VIEW_TYPE_CONTROL);
				handled = true;
				break;
			case R.id.mi_view_type_full:
				relaunch(VIEW_TYPE_FULL);
				handled = true;
				break;
			default:
				handled = super.onOptionsItemSelected(item);
				break;
		}

		return handled;
	}

	@Override
	public void onUserLoginStatusChanged(boolean userLoggedIn) {
		updateLoggedInUser();
	}

	/* Protected Methods */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		flSpritzViewContainer = (FrameLayout)findViewById(R.id.flSpritzViewContainer);
		/** Start weird button shit
		
		playButton = (Button) findViewByID(R.id.btnPlay);
		pauseButton = (Button) findViewByID(R.id.btnPause);
		backButton = (Button) findViewByID(R.id.btnBack);
		forwardButton = (Button) findViewByID(R.id.btnForward);
		rewindButton = (Button) findViewByID(R.id.btnRewind);
		resetButton = (Button) findViewByID(R.id.btnReset);
		slowerButton = (Button) findViewByID(R.id.btnSlower);
		fasterButton = (Button) findViewByID(R.id.btnFaster);
		
		// ** End weird button shit */
		Log.v("Terence", "Do the thing");
		sdCard = Environment.getExternalStorageDirectory();
		filePopulate(sdCard);
		Log.v("Terence", "Thing should have been done.");
		
		final int argViewType = getIntent().getIntExtra(EXTRA_VIEW_TYPE, -1);
		final SharedPreferences prefs = getSharedPrefs();
		final int savedViewType = prefs.getInt(PREF_VIEW_TYPE, VIEW_TYPE_BASE);
		int viewType;

		if (argViewType == -1) {
			// We weren't launched with a specific type so use the persisted value.
			viewType = savedViewType;
		} else {
			// We were launched with a specific view type, so use that.
			viewType = argViewType;

			if (viewType != savedViewType) {
				// Since this type is different than our persisted type, update our persisted value so the next launch
				// without a specific type uses that again.
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt(PREF_VIEW_TYPE, viewType);
				editor.commit();
			}
		}

		LayoutInflater inflater = LayoutInflater.from(this);

		switch (viewType) {
			case VIEW_TYPE_BASE:
				inflater.inflate(R.layout.activity_main_base, flSpritzViewContainer, true);
				break;
			case VIEW_TYPE_CONTROL:
				inflater.inflate(R.layout.activity_main_control, flSpritzViewContainer, true);
				break;
			case VIEW_TYPE_FULL:
				inflater.inflate(R.layout.activity_main_full, flSpritzViewContainer, true);
				break;
			default:
				inflater.inflate(R.layout.activity_main_base, flSpritzViewContainer, true);
				break;
		}

		spritzView = (SpritzBaseView) findViewById(R.id.spritzView);

		if (spritzView instanceof SpritzControlView) {
			((SpritzControlView)spritzView).setPopupMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					boolean handled = false;
					if (item.getItemId() == R.id.mi_doofus) {
						String message = "I'm sorry, Dave. I'm afraid I can't do that.";
						Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
						toast.show();
						handled = true;
					}
					return handled;
				}
			});
		}

		if (savedInstanceState == null) {
			// Restore saved speed, if any, on initial creation.  If this is a re-create because of an orientation
			// change, skip this step, because the spritzView will remember the selected speed.
			int savedSpeed = prefs.getInt(PREF_SPEED, -1);

			if (savedSpeed != -1) {
				spritzView.setSpeed(savedSpeed);
			}
		}

		rgMode = (RadioGroup)findViewById(R.id.rgMode);
		tvInfo = (TextView)findViewById(R.id.tvInfo);

		// Restore saved start mode
		int startMode = prefs.getInt(PREF_START_MODE, START_MODE_START);

		switch (startMode) {
			case START_MODE_LOAD:
				((RadioButton)findViewById(R.id.rbModeLoad)).setChecked(true);
				break;
			case START_MODE_START:
				((RadioButton)findViewById(R.id.rbModeStart)).setChecked(true);
				break;
		}

		rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				onStartModeOptionChanged(checkedId);
			}
		});
		
		// My additions start here
		
		inputField = (EditText)findViewById(R.id.inputField);
		inputField.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (keyCode == 66)
				{
					input = inputField.getText().toString();
					Log.v("input", "input has been acknowledged as "+input);
				}
				return false;
			}
		});
	}

	@Override
	protected void onDestroy() {
		SharedPreferences prefs = getSharedPrefs();

		if (spritzView.getSpeed() != prefs.getInt(PREF_SPEED, -1)) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(PREF_SPEED, spritzView.getSpeed());
			editor.commit();
		}

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// Pause the spritz view.
		spritzView.pause();

		SpritzSDK.getInstance().removeLoginStatusChangeListener(this);
		SpritzSDK.getInstance().removeLoginEventListener(this);

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SpritzSDK.getInstance().addLoginEventListener(this);
		SpritzSDK.getInstance().addLoginStatusChangeListener(this);

		updateLoggedInUser();
	}

	@Override
	protected void onStart() {
		super.onStart();
		updateInfo();
	}

	/* Private Methods */

	private void onStartModeOptionChanged(int selectedButtonId) {
		int startMode;

		if (selectedButtonId == R.id.rbModeLoad) {
			startMode = START_MODE_LOAD;
		} else if (selectedButtonId == R.id.rbModeStart) {
			startMode = START_MODE_START;
		} else {
			throw new IllegalArgumentException("Invalid button id: " + selectedButtonId);
		}

		SharedPreferences.Editor editor = getSharedPrefs().edit();
		editor.putInt(PREF_START_MODE, startMode);
		editor.commit();
	}

	private void relaunch(int viewType) {
		finish();
		startActivity(this, viewType);
	}

	private void spritz(SpritzSource source) {
		if (isStartMode()) {
			spritzView.start(source);
		} else {
			spritzView.load(source);
		}
	}

	private void updateInfo() {
		StringBuilder message = new StringBuilder();
		message.append("Char: ");
		message.append(spritzView.getCharacterPosition());
		message.append(", Word: ");
		message.append(spritzView.getWordPosition());
		message.append(", Time: ");
		message.append(spritzView.getTimePosition());
		message.append(", Speed: ");
		message.append(spritzView.getSpeed());
		tvInfo.setText(message.toString());
	}

	private void updateLoggedInUser() {
		
		SpritzUser user = SpritzSDK.getInstance().getLoggedInUser();
		
		Button btnLogin = (Button)findViewById(R.id.btnLogin);
		TextView tvLoggedInUser = (TextView)findViewById(R.id.tvLoggedInUser);

		if (user == null) {
			
			tvLoggedInUser.setText(getResources().getString(R.string.activity_main_none));
			btnLogin.setText(getResources().getString(R.string.activity_main_login));
			
		} else {
			
			StringBuilder userInfo = new StringBuilder();
			userInfo.append(user.getUserId());

			if (user.getNickname() != null) {
				userInfo.append(" (");
				userInfo.append(user.getNickname());
				userInfo.append(")");
			}

			tvLoggedInUser.setText(userInfo.toString());
			btnLogin.setText(getResources().getString(R.string.activity_main_logout));
		}
	}
	
}
