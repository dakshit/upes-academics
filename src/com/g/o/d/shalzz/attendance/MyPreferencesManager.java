package com.g.o.d.shalzz.attendance;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.Iterator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MyPreferencesManager {

	/**
	 * The activity context.
	 */
	private Context mContext;

	/**
	 * Constructor to set the Activity context.
	 * @param context
	 */
	public MyPreferencesManager(Context context) {
		mContext = context;
	}

	/**
	 * Gets the cookies from the shared preferences and adds them to the default CookieManager.
	 */
	public void getPersistentCookies()
	{
		CookieManager cookieMan = (CookieManager) CookieHandler.getDefault();
		SharedPreferences pcookies = mContext.getSharedPreferences("PERSISTCOOKIES", 0);	
		Iterator<String> keyset = pcookies.getAll().keySet().iterator();
		if(keyset.hasNext())
		{
			Log.i(Splash.class.getName(), "Persisten cookies found.");
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
				else
				{
					Log.i(mContext.getClass().getName(), "Persisten cookies not found.");
				}
			}
		}
	}

	/**
	 * Saves the cookies in shared preferences.
	 */
	public void savePersistentCookies() {
		CookieManager cookieMan = (CookieManager) CookieHandler.getDefault();
		SharedPreferences persistentcookies = mContext.getSharedPreferences("PERSISTCOOKIES", 0);
		SharedPreferences.Editor editor = persistentcookies.edit();
		for(HttpCookie cookie : cookieMan.getCookieStore().getCookies() ){
			editor.putString(cookie.getName(), cookie.getValue());
		}
		editor.commit();
	}

	/**
	 * Removes the cookies from the shared preferences and Cookie Manager
	 */
	public void removePersistenCookies() {
		SharedPreferences pcookies = mContext.getSharedPreferences("PERSISTCOOKIES", 0);
		SharedPreferences.Editor editor = pcookies.edit();
		Iterator<String> keyset = pcookies.getAll().keySet().iterator();
		while(keyset.hasNext())
		{
			String cookiename = keyset.next();
			editor.remove(cookiename);
		}
		editor.commit();
		
		CookieManager cookieMan = (CookieManager) CookieHandler.getDefault();
		cookieMan.getCookieStore().removeAll();
	}

	/**
	 * Gets the login status from the preferences
	 * @return true if logged in else false
	 */
	public boolean getLoginStatus() {

		Log.i(mContext.getClass().getName(), "Getting Logged in state.");
		SharedPreferences settings = mContext.getSharedPreferences("SETTINGS", 0);
		boolean loggedin = settings.getBoolean("LOGGEDIN", false);
		Log.i(mContext.getClass().getName(), "Logged in state: "+loggedin+".");
		return loggedin;
	}

	/**
	 * Saves the user details in shared preferences and sets login status to true.
	 * @param username
	 * @param password
	 */
	public void saveUser(String username, String password) {
		Log.i(mContext.getClass().getName(), "Setting LOGGEDIN pref to true");
		SharedPreferences settings = mContext.getSharedPreferences("SETTINGS", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("LOGGEDIN", true);
		editor.putString("USERNAME", username);
		editor.putString("PASSWORD", password);
		editor.commit();
	}

	/**
	 * Removes the user details from the shared preferences and sets login status to false.
	 */
	public void removeUser() {	
		Log.i(mContext.getClass().getName(), "Setting LOGGEDIN pref to false");
		SharedPreferences settings = mContext.getSharedPreferences("SETTINGS", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("LOGGEDIN", false);
		editor.remove("USERNAME");
		editor.remove("PASSWORD");
		editor.commit();
	}
}
