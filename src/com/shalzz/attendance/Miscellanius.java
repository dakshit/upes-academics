package com.shalzz.attendance;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import com.actionbarsherlock.widget.SearchView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public abstract class Miscellanius {


	/**
	 * Shows the default user soft keyboard.
	 * @param mTextView
	 */
	protected static void showKeyboard(Context context, EditText mTextView) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			// only will trigger it if no physical keyboard is open
			imm.showSoftInput(mTextView, 0);
		}
	}

	/**
	 * Closes the default user soft keyboard.
	 * @param searchView
	 */
	protected static void closeKeyboard(Context context, SearchView searchView) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			// only will trigger it if no physical keyboard is open
			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
	}



	public static boolean useProxy() {
		ConnectivityManager connManager = (ConnectivityManager) MyVolley.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi.isConnectedOrConnecting()) {
			WifiManager wifiManager = (WifiManager) MyVolley.getAppContext().getSystemService(MyVolley.getAppContext().WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			Log.d("wifiInfo",""+ wifiInfo.toString());
			Log.d("SSID",""+wifiInfo.getSSID());
			if (wifiInfo.getSSID().contains("UPESNET"))
			{
				Authenticator authenticator = new Authenticator() {

			        public PasswordAuthentication getPasswordAuthentication() {
			            return (new PasswordAuthentication("UPESDDN\500029039",
			                    "Gmail@123".toCharArray()));
			        }
			    };
			    Authenticator.setDefault(authenticator);
				return false;
			}
			else
				return false;
		}
		
		else
			return false;
	}
}
