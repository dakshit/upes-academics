package com.god.attendence;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
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
				Log.i(Splash.class.getName(), "Getting Logged in state.");
				SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
				boolean loggedin = settings.getBoolean("LOGGEDIN", false);
				Log.i(Splash.class.getName(), "Logged in state:"+loggedin+".");
				
				Log.i(Splash.class.getName(), "Getting persisten cookies.");
				final CookieManager cookieMan = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
				try
				{
					SharedPreferences pcookies = getSharedPreferences("PERSISTCOOKIES", 0);	
					Iterator<String> keyset = pcookies.getAll().keySet().iterator();
					if(keyset.hasNext())
					{
						while(keyset.hasNext())
						{
							String cookiename = keyset.next();
							String cookievalue = pcookies.getString(cookiename, "");
							if(!cookievalue.isEmpty()) 
							{
								try {
									HttpCookie cookie = new HttpCookie(cookiename,cookievalue);
									cookie.setDomain("academics.ddn.upes.ac.in");
									cookie.setPath("/");
									cookie.setVersion(0);
									cookieMan.getCookieStore().add(new URI("https://academics.ddn.upes.ac.in/upes/"), cookie);
								} catch (Exception e) {
									e.printStackTrace();
								} 
							}
						}
					}
					else
					{
						Log.i(Splash.class.getName(), "Persisten cookies not found.");
					}
					sleep(0000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				finally
				{
					// Set the cookie manager irrespective of weather persistent cookies exist.
					Log.i(Splash.class.getName(), "Setting CookieHandler");
					CookieHandler.setDefault(cookieMan);
					
					if(!loggedin)
					{
						Log.i(Splash.class.getName(), "Starting Login Activity");
						Intent intent = new Intent(Splash.this, Login.class);
						startActivity(intent);		
						finish();
					}
					else
					{
						Log.i(Splash.class.getName(), "Persisten cookies found.");
						Log.d(Splash.class.getName(), cookieMan.getCookieStore().getCookies().toString());
						Log.i(Splash.class.getName(), "Starting Attendance Activity");
						Intent intent = new Intent(Splash.this, Attendence.class);
						startActivity(intent);
						finish();
					}
				}
			}
		};
		timer.start();
	}
}
