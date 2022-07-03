package com.ytspilot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.ytspilot.R;
import com.ytspilot.util.NetworkHandler;
import com.ytspilot.util.Store_pref;

public class Splash extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;

	Store_pref mStore_pref ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		mStore_pref = new Store_pref(this);
		if(NetworkHandler.isOnline(this)){
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (LoginActivity.islogin == true || mStore_pref.getisTrip()) {
					Intent i = new Intent(Splash.this, MainActivity.class);
					startActivity(i);
				} else {
					Intent i = new Intent(Splash.this, LoginActivity.class);
					startActivity(i);
				}

				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
		}else{
			Toast.makeText(this, R.string.nointernet,Toast.LENGTH_SHORT).show();
		}
	}

}
