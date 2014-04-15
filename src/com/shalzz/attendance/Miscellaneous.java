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

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import com.actionbarsherlock.widget.SearchView;
import com.shalzz.attendance.wrapper.MyVolley;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Miscellaneous {

	private AlertDialog.Builder builder = null;
	private ProgressDialog pd = null;
	private Context mContext;
	//private String mTag = "Miscellaneous";

	public Miscellaneous(Context context) {
		mContext = context;
	}

	/**
	 * Shows the default user soft keyboard.
	 * @param mTextView
	 */
	public static void showKeyboard(Context context, EditText mTextView) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			// will trigger it only if no physical keyboard is open
			imm.showSoftInput(mTextView, 0);
		}
	}

	/**
	 * Closes the default user soft keyboard.
	 * @param context
	 * @param searchView
	 */
	public static void closeKeyboard(Context context, SearchView searchView) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			// only will trigger it if no physical keyboard is open
			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
	}


	/**
	 * Closes the default user soft keyboard.
	 * @param context
	 * @param editText
	 */
	public static void closeKeyboard(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			// only will trigger it if no physical keyboard is open
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}	
	}

	/**
	 * Displays the default Progress Dialog.
	 * @param mMessage
	 */
	public void showProgressDialog(String mMessage,boolean cancable, DialogInterface.OnCancelListener progressDialogCancelListener) {
		// lazy initialise
		if(pd==null)
		{
			// Setup the Progress Dialog
			pd = new ProgressDialog(mContext);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage(mMessage);
			pd.setIndeterminate(true);
			pd.setCancelable(cancable);
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(progressDialogCancelListener);
		}
		pd.show();
	}

	/**
	 * Dismisses the Progress Dialog.
	 */
	public void dismissProgressDialog() {
		if(pd!=null)
			pd.dismiss();
	}

	/**
	 * Displays a basic Alert Dialog.
	 * @param mMessage
	 */
	public void showAlertDialog(String mMessage) {
		// lazy initialise
		if(builder==null)
		{
			builder = new AlertDialog.Builder(mContext);
			builder.setCancelable(true);
			builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		dismissProgressDialog();
		builder.setMessage(mMessage);
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Determines whether to use proxy settings or not.
	 * @return true or false.
	 */
	public static boolean useProxy() {
		ConnectivityManager connManager = (ConnectivityManager) MyVolley.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi.isConnectedOrConnecting()) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyVolley.getAppContext());
			boolean useProxy = sharedPref.getBoolean("", false);
			final String username = sharedPref.getString("pref_key_proxy_username", "");
			final String password = sharedPref.getString("pref_key_proxy_password", "");
			if(useProxy && !username.isEmpty() && !password.isEmpty())
			{
				WifiManager wifiManager = (WifiManager) MyVolley.getAppContext().getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				Log.d("wifiInfo",""+ wifiInfo.toString());
				Log.d("SSID",""+wifiInfo.getSSID());
				Toast.makeText(MyVolley.getAppContext(), "Wifi changed to "+wifiInfo.getSSID(), Toast.LENGTH_LONG).show();
				if (wifiInfo.getSSID().contains("UPESNET"))
				{
					//					OkAuthenticator auth = new OkAuthenticator() {
					//						
					//						@Override
					//						public Credential authenticateProxy(Proxy arg0, URL arg1,
					//								List<Challenge> arg2) throws IOException {
					//							return Credential.basic(username,password);
					//						}
					//						
					//						@Override
					//						public Credential authenticate(Proxy arg0, URL arg1, List<Challenge> arg2)
					//								throws IOException {
					//							return Credential.basic(username,password);
					//						}
					//					};
					//					Authenticator.setDefault((Authenticator) auth);

					Authenticator authenticator = new Authenticator() {

						public PasswordAuthentication getPasswordAuthentication() {
							return (new PasswordAuthentication(username,password.toCharArray()));
						}
					};
					Authenticator.setDefault(authenticator);
					return true;
				}
			}
		}
		return false;
	}
}
