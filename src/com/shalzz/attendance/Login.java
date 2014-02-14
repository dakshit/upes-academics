package com.shalzz.attendance;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.shalzz.attendance.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.os.Bundle;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends SherlockFragmentActivity implements CaptchaDialogFragment.CaptchaDialogListener{

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
			etSapid.requestFocus();
			etSapid.setError("SAP ID should be of 9 digits");
			Miscellanius.showKeyboard(this,etSapid);
			return false;
		}
		else if (password.length()==0) {
			etPass.requestFocus();
			etPass.setError("Password cannot be empty");
			Miscellanius.showKeyboard(this,etPass);
			return false;
		}
		else
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
		dialog.dismiss();

		if (Captxt.getText().toString().length()==6) {

			new UserAccount(Login.this)
			.Login(etSapid.getText().toString(), 
					etPass.getText().toString(),
					Captxt.getText().toString(),
					data);		
		}
		else {
			Crouton.makeText(Login.this,  "Captcha must be of 6 digits", Style.ALERT).show();
		}

	}

	private void getHiddenData()
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
				String msg = VolleyErrorHelper.getMessage(error, Login.this);
				Crouton.makeText(Login.this,  msg, Style.ALERT).show();	
				Log.e(getClass().getName(), msg);
			}
		};
	}

	@Override
	protected void onDestroy() {
		MyVolley.getInstance().cancelPendingRequests(myTag);
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
}
