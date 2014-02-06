package com.shalzz.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.shalzz.attendance.R;

public class Splash extends SherlockActivity {
	
	MyPreferencesManager settings = new MyPreferencesManager(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Thread timer = new Thread(){
			public void run()
			{
				boolean loggedin = settings.getLoginStatus();
				
				try {
					settings.getPersistentCookies();
					sleep(0000);
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
				finally
				{
					Intent intent;
					if(!loggedin) {
						Log.i(Splash.class.getName(), "Starting Login Activity");
						intent = new Intent(Splash.this, Login.class);
					}
					else {
						Log.i(Splash.class.getName(), "Starting Attendance Activity");
						intent = new Intent(Splash.this, Attendance.class);
					}
					startActivity(intent);		
					finish();
				}
			}
		};
		timer.start();
	}
}
