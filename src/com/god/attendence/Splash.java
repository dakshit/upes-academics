package com.god.attendence;

import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Thread timer = new Thread(){
			public void run()
			{
				boolean prefExist = true;
				try
				{
					SharedPreferences pcookies = getSharedPreferences("PERSISTCOOKIES", 0);	
					Iterator<String> keyset = pcookies.getAll().keySet().iterator();
					if(keyset.hasNext())
					{
						int count = 0;
						while(keyset.hasNext())
						{
							String cookiename = keyset.next();
							String cookievalue = pcookies.getString(cookiename, "");
							if(cookievalue.isEmpty())
							{
								++count;
							}
						}
						if(count>0)
						{
							Log.i(Splash.class.getName(), "Persisten cookies not found.");
							prefExist = false;
						}
					}
					else
					{
						Log.i(Splash.class.getName(), "Preferences do not exist.");
						prefExist = false;
					}
					sleep(0000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				finally
				{
					if(!prefExist)
					{
						Log.i(Splash.class.getName(), "Starting Login Activity");
						Intent openMainActivity = new Intent("com.god.attendence.MAIN");
						startActivity(openMainActivity);		
						finish();
					}
					else
					{
						Log.i(Splash.class.getName(), "Persisten cookies found.");
						Log.i(Splash.class.getName(), "Starting Attendance Activity");
						Intent intent = new Intent(Splash.this, DisplayAtten.class);
						startActivity(intent);
						finish();
					}
				}
			}
		};
		timer.start();
	}
}
