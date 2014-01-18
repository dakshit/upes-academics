package com.god.attendance;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.god.attendence.R;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
// TODO add ActionBar
public class Login extends ActionBarActivity implements CaptchaDialogFragment.CaptchaDialogListener{

	private EditText etSapid;
	private EditText etPass;
	private Button bLogin;
	private String charset = HTTP.ISO_8859_1;
	private CookieManager cookieMan = (CookieManager) CookieHandler.getDefault();
	private Map<String, String> data = new HashMap<String, String>();
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		

		setContentView(R.layout.activity_main);

		// Reference to the layout components
		etSapid = (EditText) findViewById(R.id.etSapid);
		etPass = (EditText) findViewById(R.id.etPass);
		bLogin = (Button) findViewById(R.id.bLogin);

		// Get the hidden data
		getHiddenData();

		// Setup the Progress Dialog
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Logging in...");
		pd.setIndeterminate(true);
		pd.setCancelable(false);

		// OnClickListener event for the Login Button
		bLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {		
				if(validate())
				{
					showCaptchaDialog();
				}
				else
				{
					// TODO tell user
				}
			}
		});
		
	}
	
	public boolean validate() {
		String sapid = etSapid.getText().toString();
		String password = etPass.getText().toString();
		if(sapid.isEmpty() && password.isEmpty() && sapid.length()!=9)
			return false;
		else
			return true;
	}
	
	public void showCaptchaDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CaptchaDialogFragment();
        dialog.show(getSupportFragmentManager(), "CaptchaDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the CaptchaDialogFragment.CaptchaDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
    	
    	Dialog dialogView = dialog.getDialog();
    	final EditText Captxt = (EditText) dialogView.findViewById(R.id.etCapTxt);
    	dialog.dismiss();
    	
    	pd.show();
		String mURL = "https://academics.ddn.upes.ac.in/upes/index.php";
		StringRequest request = new StringRequest(Method.POST,
				mURL,
				loginOnCLickSuccessListener(),
				myErrorListener()) {

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept-Charset", charset);
				headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
				headers.put("User-Agent", getString(R.string.UserAgent));
				headers.put("Connection", "keep-alive");
				
//				SharedPreferences pcookies = getSharedPreferences("PERSISTCOOKIES", 0);	
//				Iterator<String> keyset = pcookies.getAll().keySet().iterator();
//				while(keyset.hasNext())
//				{
//					String cookiename = keyset.next();
//					String cookievalue = pcookies.getString(cookiename, "");
//					headers.put("Cookie", cookiename+"="+cookievalue);
//				}
				return headers;
			};

			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = data;
				params.put("username", etSapid.getText().toString());
				params.put("passwd", etPass.getText().toString());
				params.put("txtCaptcha", Captxt.getText().toString());
				params.put("submit", "Login");
				return params;
			};
		};
		request.setShouldCache(true);
		MyVolley.getInstance().addToRequestQueue(request,"LOGIN");
    }

	private void getHiddenData()
	{
		Log.i(Login.class.getName(),"Collecting hidden data...");
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
		request.setShouldCache(true);
		request.setPriority(Priority.HIGH);
		request.setRetryPolicy(new DefaultRetryPolicy(1500, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		MyVolley.getInstance().addToRequestQueue(request);
	}

	private Response.Listener<String> getHiddenDataSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				Log.i(Login.class.getName(), "Collected hidden data.");
				Document doc = Jsoup.parse(response);
				Log.i(Login.class.getName(),"Parsing hidden data...");

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
				Log.i(Login.class.getName(), "Parsed hidden data.");
			}
		};
	}

	private Response.Listener<String> loginOnCLickSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				Document document = Jsoup.parse(response);
				System.out.println(cookieMan.getCookieStore().getCookies().toString());
				
				AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
				builder.setCancelable(true);
				builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
			
				if(document.data().toString().equals(getString(R.string.incorrect_captcha)))
				{
					pd.dismiss();
					
					builder.setMessage("Incorrect Captcha!\nPlease try again.");
					AlertDialog alert = builder.create();
					alert.show();
				}
				else if(document.data().toString().equals(getString(R.string.incorrect_user_or_pass)))
				{
					pd.dismiss();
					
					builder.setMessage("Incorrect username or password. Please try again");
					AlertDialog alert = builder.create();
					alert.show();
					
				}
				//				else if(document.getElementsByTag("title").get(0).text().equals("UPES - Home"))
				//				{
				//					// reset cookies and try again
				//					SharedPreferences pcookies = getSharedPreferences("PERSISTCOOKIES", 0);
				//					SharedPreferences.Editor editor = pcookies.edit();
				//					Iterator<String> keyset = pcookies.getAll().keySet().iterator();
				//					while(keyset.hasNext())
				//					{
				//						String cookiename = keyset.next();
				//						editor.remove(cookiename);
				//					}
				//					
				//					cookieMan.getCookieStore().removeAll();
				//					
				//					pd.dismiss();
				//					// show toast
				//					getImg();
				//					etCaptcha.setText("");
				//				}
				else
				{
					SharedPreferences persistentcookies = getSharedPreferences("PERSISTCOOKIES", 0);
					SharedPreferences.Editor editor = persistentcookies.edit();
					for(HttpCookie cookie : cookieMan.getCookieStore().getCookies() ){
						editor.putString(cookie.getName(), cookie.getValue());
					}
					editor.commit();

					Log.i(Login.class.getName(), "Setting LOGGEDIN pref to true");
					SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
					SharedPreferences.Editor editor1 = settings.edit();
					editor1.putBoolean("LOGGEDIN", true);
					editor1.commit();

					pd.dismiss();
					Intent ourIntent = new Intent(Login.this, Attendance.class);
					startActivity(ourIntent);
					finish();
				}
			}
		};
	}

	private Response.ErrorListener myErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String msg = VolleyErrorHelper.getMessage(error, Login.this);
				Toast toast = Toast.makeText(Login.this, msg, Toast.LENGTH_LONG);
				pd.dismiss();
				toast.show();
				Log.e(Login.class.getName(), msg);
			}
		};
	}
	
	@Override
	protected void onDestroy() {
		MyVolley.getInstance().cancelPendingRequests();
		super.onDestroy();
	}
}
