/*
 *    UPES Academics, android attendance application for University of Petroleum and Energy Studies
 *    Copyright (C) 2014  Shaleen Jain
 *    shaleen.jain95@gmail.com
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
**/    

package com.shalzz.attendance;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.shalzz.attendance.R;
import com.shalzz.attendance.activity.LoginActivity;
import com.shalzz.attendance.activity.MainActivity;
import com.shalzz.attendance.wrapper.MyPreferencesManager;
import com.shalzz.attendance.wrapper.MyStringRequest;
import com.shalzz.attendance.wrapper.MyVolley;
import com.shalzz.attendance.wrapper.MyVolleyErrorHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class UserAccount {

	private String mUsername;
	private String mPassword;
	private String mCaptcha;
	private int retryCount=0;
	private Miscellaneous misc;

	/**
	 * The activity context used to Log the user from
	 */
	private Context mContext;

	/**
	 * Constructor to set the Activity context.
	 * @param context
	 */
	public UserAccount(Context context) {
		mContext = context;
		misc =  new Miscellaneous(mContext);
	}

	/**
	 * Sends the login request and saves the user details.
	 * @param username
	 * @param password
	 * @param captcha
	 * @param data
	 */
	public void Login(final String username, final String password, final String captcha, final Map<String, String> data) {

		mUsername = username;
		mPassword = password;
		mCaptcha = captcha;
		
		misc.showProgressDialog("Logging in...", false, pdCancelListener());
		String mURL = mContext.getResources().getString(R.string.URL_login);
		MyStringRequest request = new MyStringRequest(Method.POST,
				mURL,
				loginSuccessListener(),
				myErrorListener()) {

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("User-Agent", mContext.getString(R.string.UserAgent));
				return headers;
			};

			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = data;
				params.put("username", username);
				params.put("passwd", password);
				params.put("txtCaptcha", captcha);
				params.put("submit", "Login");
				params.put("remember","yes");
				return params;
			};
		};
		request.setShouldCache(false);
		request.setPriority(Priority.HIGH);
		request.setRetryPolicy(new DefaultRetryPolicy(1500, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		MyVolley.getInstance().addToRequestQueue(request,mContext.getClass().getName());
	}

	private Response.Listener<String> loginSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				Document document = Jsoup.parse(response);
				System.out.println(document.text());

				if(document.data().toString().equals(mContext.getString(R.string.incorrect_captcha)))
				{
					misc.showAlertDialog("Incorrect Captcha!\nPlease try again.");
				}
				else if(document.data().toString().equals(mContext.getString(R.string.incorrect_user_or_pass)))
				{
					misc.showAlertDialog("Incorrect username or password. Please try again");

				}
				else if(document.getElementsByTag("title").get(0).text().equals("UPES - Home"))
				{
					if(retryCount<2)
					{
						LoginWithNewHiddenData();
						retryCount++;
					}
					else if(retryCount==2)
					{
						new MyPreferencesManager(mContext).removePersistenCookies();
						LoginWithNewHiddenData();										
					}
					else
					{
						misc.dismissProgressDialog();
						Crouton.makeText((Activity) mContext,  "Error! Please try again later", Style.ALERT).show();	
					}
				}
				else
				{
					MyPreferencesManager settings = new MyPreferencesManager(mContext);
					settings.savePersistentCookies();
					// Used for future re-logins
					settings.saveUser(mUsername, mPassword);

					misc.dismissProgressDialog();
					Intent ourIntent = new Intent(mContext, MainActivity.class);
					mContext.startActivity(ourIntent);
					((Activity) mContext).finish();
				}
			}
		};
	}

	/**
	 * Sends the Logout request, clears the user details preferences and deletes all user attendance data.
	 */
	public void Logout() {

		misc.showProgressDialog( "Logging out...", true, pdCancelListener());
		Log.i(mContext.getClass().getName(), "Logging out...");

		String mURL = mContext.getResources().getString(R.string.URL_logout);
		MyStringRequest request = new MyStringRequest(Method.POST,
				mURL,
				new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.i(mContext.getClass().getName(), "Succesfully Logged out...");				
			}
		},
		myErrorListener()) {

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("User-Agent", mContext.getString(R.string.UserAgent));
				return headers;
			};

			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("submit", "Logout");
				params.put("option", "logout");
				params.put("op2", "logout");
				params.put("lang", "english");
				params.put("return", mContext.getResources().getString(R.string.URL_home));
				params.put("message", "0");
				return params;
			};
		};
		request.setShouldCache(false);
		request.setPriority(Priority.IMMEDIATE);
		request.setRetryPolicy(new DefaultRetryPolicy(1500, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		MyVolley.getInstance().addToRequestQueue(request,"LOGOUT");
		
		// Remove User Details from Shared Preferences.
		MyPreferencesManager settings = new MyPreferencesManager(mContext);
		settings.removeUser();

		// Remove user Attendance data from database.
		DatabaseHandler db = new DatabaseHandler(mContext);
		db.resetTables();
		
		// Remove Sync Account
		MySyncManager.removeSyncAccount(mContext);
		
		misc.dismissProgressDialog();
		Intent ourIntent = new Intent(mContext, LoginActivity.class);
		mContext.startActivity(ourIntent);
		((Activity) mContext).finish();
	}

	/**
	 * Progress Dialog cancel Listener.
	 * @return
	 */
	DialogInterface.OnCancelListener pdCancelListener() {
		return new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// Cancel all pending requests when user presses back button.
				MyVolley.getInstance().cancelPendingRequests(mContext.getClass().getName());
				MyVolley.getInstance().cancelPendingRequests("LOGOUT");
			}
		};

	}
	
	/**
	 * Logins in with new hidden data in case previous data is corrupted.
	 */
	private void LoginWithNewHiddenData()
	{
		Log.i(getClass().getName(),"Collecting hidden data...");
		String mURL = mContext.getResources().getString(R.string.URL_home);
		MyStringRequest request = new MyStringRequest(Method.GET,
				mURL,
				getHiddenDataSuccessListener(),
				myErrorListener()) {

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("User-Agent", mContext.getString(R.string.UserAgent));
				return headers;
			};
		};
		request.setShouldCache(false);
		request.setPriority(Priority.HIGH);
		request.setRetryPolicy(new DefaultRetryPolicy(1500, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		MyVolley.getInstance().addToRequestQueue(request,mContext.getClass().getName());
	}

	private Response.Listener<String> getHiddenDataSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				Log.i(getClass().getName(), "Collected hidden data.");
				Document doc = Jsoup.parse(response);
				Log.i(getClass().getName(),"Parsing hidden data...");

				// Get Hidden values
				Map<String, String> data = new HashMap<String, String>();
				Elements hiddenvalues = doc.select("input[type=hidden]");
				for(Element hiddenvalue : hiddenvalues)
				{
					String name = hiddenvalue.attr("name");
					String val = hiddenvalue.attr("value");
					if(name.length()!=0 && val.length()!=0)
					{
						data.put(name, val);
					}
				}
				Log.i(getClass().getName(), "Parsed hidden data.");
				Login(mUsername, mPassword, mCaptcha, data);
			}
		};
	}
	
	private Response.ErrorListener myErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String msg = MyVolleyErrorHelper.getMessage(error, mContext);
				misc.dismissProgressDialog();		
				Crouton.makeText((Activity) mContext,  msg, Style.ALERT).show();		
				Log.e(mContext.getClass().getName(), msg);
			}
		};
	}
}
