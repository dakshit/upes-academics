package com.shalzz.attendance;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.toolbox.StringRequest;
import com.shalzz.attendance.R;


public class UserAccount {

	private ProgressDialog pd = null;
	private AlertDialog.Builder builder = null;
	private String charset = HTTP.ISO_8859_1;

	private String mUsername;
	private String mPassword;
	private String mCaptcha;
	private int retryCount=0;

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
	}

	/**
	 * Displays the default Progress Dialog.
	 * @param mMessage
	 */
	private void showProgressDialog(String mMessage,boolean cancable) {
		// lazy initialize
		if(pd==null)
		{
			// Setup the Progress Dialog
			pd = new ProgressDialog(mContext);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage(mMessage);
			pd.setIndeterminate(true);
			pd.setCancelable(cancable);
			pd.setOnCancelListener(progressDialogCancelListener());
		}
		pd.show();
	}

	/**
	 * Dismisses the Progress Dialog.
	 */
	private void dismissProgressDialog() {
		if(pd!=null)
			pd.dismiss();
	}

	/**
	 * Displays a basic Alert Dialog.
	 * @param mMessage
	 */
	private void showAlertDialog(String mMessage) {
		// lazy initialize
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
		showProgressDialog("Logging in...",false);
		String mURL = "https://academics.ddn.upes.ac.in/upes/index.php";
		StringRequest request = new StringRequest(Method.POST,
				mURL,
				loginSuccessListener(),
				myErrorListener()) {

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept-Charset", charset);
				headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
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
					showAlertDialog("Incorrect Captcha!\nPlease try again.");
				}
				else if(document.data().toString().equals(mContext.getString(R.string.incorrect_user_or_pass)))
				{
					showAlertDialog("Incorrect username or password. Please try again");

				}
				else if(document.getElementsByTag("title").get(0).text().equals("UPES - Home"))
				{
					if(retryCount<3)
					{
						LoginWithNewHiddenData();
						retryCount++;
					}
					else if(retryCount==3)
					{
						new MyPreferencesManager(mContext).removePersistenCookies();
						dismissProgressDialog();
						LoginWithNewHiddenData();										
					}
					else
					{
						Toast.makeText(mContext, "Error! Please try again later", Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					MyPreferencesManager settings = new MyPreferencesManager(mContext);
					settings.savePersistentCookies();
					settings.saveUser(mUsername, mPassword);

					dismissProgressDialog();
					Intent ourIntent = new Intent(mContext, Attendance.class);
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

		showProgressDialog("Logging out...",true);
		Log.i(mContext.getClass().getName(), "Logging out...");

		String mURL = "https://academics.ddn.upes.ac.in/upes/index.php?option=logout";
		StringRequest request = new StringRequest(Method.POST,
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
				headers.put("Accept-Charset", charset);
				headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
				headers.put("User-Agent", mContext.getString(R.string.UserAgent));
				return headers;
			};

			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("submit", "Logout");
				params.put("option", "logout");
				params.put("op2", "logout");
				params.put("lang", "english");
				params.put("return", "https://academics.ddn.upes.ac.in/upes/index.php");
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
		
		dismissProgressDialog();
		Intent ourIntent = new Intent(mContext, Login.class);
		mContext.startActivity(ourIntent);
		((Activity) mContext).finish();
	}

	DialogInterface.OnCancelListener progressDialogCancelListener() {
		return new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// Cancel all pending requests when user presses back button.
				MyVolley.getInstance().cancelPendingRequests(mContext.getClass().getName());
				MyVolley.getInstance().cancelPendingRequests("LOGOUT");
			}
		};

	}
	
	private void LoginWithNewHiddenData()
	{
		Log.i(getClass().getName(),"Collecting hidden data...");
		String mURL = "https://academics.ddn.upes.ac.in/upes/";
		StringRequest request = new StringRequest(Method.GET,
				mURL,
				getHiddenDataSuccessListener(),
				myErrorListener()) {

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept-Charset", charset);
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
				String msg = VolleyErrorHelper.getMessage(error, mContext);
				dismissProgressDialog();
				Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();			
				Log.e(mContext.getClass().getName(), msg);
			}
		};
	}
}
