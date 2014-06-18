package com.spritzinc.android.sdk.sample.helloworld;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spritzinc.android.SimpleSpritzSource;
import com.spritzinc.android.UrlSpritzSource;
import com.spritzinc.android.sdk.SpritzSDK;
import com.spritzinc.android.sdk.SpritzUser;
import com.spritzinc.android.sdk.view.SpritzControlView;

public class MainActivity extends Activity implements SpritzSDK.LoginStatusChangeListener {
	
    private SpritzControlView spritzView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spritzView = (SpritzControlView) findViewById(R.id.spritzView);
        
        spritzView.setPopupMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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

	@Override
	protected void onResume() {
		super.onResume();
		SpritzSDK.getInstance().addLoginStatusChangeListener(this);
		updateLoggedInUser();
	}

	@Override
	protected void onPause() {
		SpritzSDK.getInstance().removeLoginStatusChangeListener(this);
		super.onPause();
	}

    public void onBtnHelloAppClick(View view) {
        SimpleSpritzSource source = new SimpleSpritzSource("This is the most simple way to Spritz. " +
                "However, it is only available if the Spritz engine is bundled into the application.", Locale.US);
        String loadingMessage = "Hello from a local string";

        spritzView.start(source);
    }

    public void onBtnHelloWebClick(View view) {
    	
        UrlSpritzSource source = new UrlSpritzSource("http://longestjokeintheworld.com"); // http://sdk.spritzinc.com/sampleText/HelloWorld.html
        
        String loadingMessage = "Hello via the web";

        spritzView.start(source);
        
    }

    public void onBtnHelloWeb2Click(View view) {
    	spritzView.start(new SimpleSpritzSource("Here's to Sprtizing arbitrary text!", new Locale("en", "US")));
    }

	public void onBtnLoginClick(View view) {
		
		SpritzSDK sdk = SpritzSDK.getInstance();
		
		if (sdk.getLoggedInUser() != null) {
			
			sdk.logoutUser();
			
		} else {
			
			sdk.loginUser(getFragmentManager(), new SpritzSDK.LoginListener() {
				@Override
				public void onLoginSuccess() {
					updateLoggedInUser();
				}

				@Override
				public void onLoginFailure(Throwable throwable) {
					// TODO: Display an alert dialog
					Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
				}
			});
			
		}
	}

	public void onBtnPauseClick(View view) {
		spritzView.pause();
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

			if (user.getUsername() != null) {
				userInfo.append(" (");
				userInfo.append(user.getUsername());
				userInfo.append(")");
			}

			tvLoggedInUser.setText(userInfo.toString());
			btnLogin.setText(getResources().getString(R.string.activity_main_logout));
		}
	}

	@Override
	public void onUserLoginStatusChanged(boolean userLoggedIn) {
		updateLoggedInUser();
	}

}
