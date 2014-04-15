/*  
 *    Copyright (C) 2013 - 2014 Shaleen Jain <shaleen.jain95@gmail.com>
 *
 *	  This file is part of UPES Academics.
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

package com.shalzz.attendance.activity;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.shalzz.attendance.Miscellaneous;
import com.shalzz.attendance.R;
import com.shalzz.attendance.UserAccount;
import com.shalzz.attendance.fragment.CaptchaDialogFragment;
import com.shalzz.attendance.wrapper.MyStringRequest;
import com.shalzz.attendance.wrapper.MyVolley;
import com.shalzz.attendance.wrapper.MyVolleyErrorHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends SherlockFragmentActivity implements CaptchaDialogFragment.CaptchaDialogListener{

	private EditText etSapid;
	private EditText etPass;
	private Button bLogin;
	private String charset = HTTP.ISO_8859_1;
	private Map<String, String> data = new HashMap<String, String>();
	private String myTag = getClass().getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);

		// Reference to the layout components
		etSapid = (EditText) findViewById(R.id.etSapid);
		etPass = (EditText) findViewById(R.id.etPass);
		bLogin = (Button) findViewById(R.id.bLogin);

		getHiddenData();

		// Shows the CaptchaDialog when user presses 'Done' on keyboard.
		etPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {                    
					if(isValid())
					{
						showCaptchaDialog();
					}
					return true;
				}
				return false;
			}});

		// OnClickListener event for the Login Button
		bLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {		
				if(isValid())
				{
					showCaptchaDialog();
				}
			}
		});

	}

	/**
	 * Checks if the form is valid
	 * @return true or false
	 */
	public boolean isValid() {		
		String sapid = etSapid.getText().toString();
		String password = etPass.getText().toString();	

		if(sapid.length()==0 || sapid.length()!=9) {
			// workaround for enrollment number.
			if(sapid.length()==10)
			{
				if(sapid.charAt(0)=='#')
					sapid.replaceFirst("#","R");
				else {
					etSapid.requestFocus();
					etSapid.setError("SAP ID should be of 9 digits");
					Miscellaneous.showKeyboard(this,etSapid);
					return false;
				}			
			} 
			else {
				etSapid.requestFocus();
				etSapid.setError("SAP ID should be of 9 digits");
				Miscellaneous.showKeyboard(this,etSapid);
				return false;
			}
		}
		else if (password.length()==0) {
			etPass.requestFocus();
			etPass.setError("Password cannot be empty");
			Miscellaneous.showKeyboard(this,etPass);
			return false;
		}
			return true;
	}

	/**
	 * Creates an instance of the dialog fragment and shows it
	 */
	public void showCaptchaDialog() {
		DialogFragment dialog = new CaptchaDialogFragment();
		dialog.show(getSupportFragmentManager(), "CaptchaDialogFragment");
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {

		Dialog dialogView = dialog.getDialog();
		final EditText Captxt = (EditText) dialogView.findViewById(R.id.etCapTxt);

		if(data.isEmpty())
			getHiddenData();
		new UserAccount(LoginActivity.this)
		.Login(etSapid.getText().toString(), 
				etPass.getText().toString(),
				Captxt.getText().toString(),
				data);		
		Miscellaneous.closeKeyboard(this, Captxt);
		dialog.dismiss();
	}

	private void getHiddenData()
	{
		Log.i(getClass().getName(),"Collecting hidden data...");
		String mURL = getResources().getString(R.string.URL_home);
		MyStringRequest request = new MyStringRequest(Method.GET,
				mURL,
				getHiddenDataSuccessListener(),
				myErrorListener()) {

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept-Charset", charset);
				headers.put("User-Agent", getString(R.string.UserAgent));
				return headers;
			};
		};
		request.setShouldCache(false);
		request.setPriority(Priority.HIGH);
		request.setRetryPolicy(new DefaultRetryPolicy(1500, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		MyVolley.getInstance().addToRequestQueue(request,myTag);
	}

	private Response.Listener<String> getHiddenDataSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				Log.i(getClass().getName(), "Collected hidden data.");
				Document doc = Jsoup.parse(response);
				Log.i(getClass().getName(),"Parsing hidden data...");

				// Get Hidden values
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
			}
		};
	}

	private Response.ErrorListener myErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String msg = MyVolleyErrorHelper.getMessage(error, LoginActivity.this);
				Crouton.makeText(LoginActivity.this,  msg, Style.ALERT).show();	
				Log.e(getClass().getName(), msg);
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.login, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_help)
		{
			startActivity(new Intent(this, AboutUsActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		MyVolley.getInstance().cancelPendingRequests(myTag);
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
}