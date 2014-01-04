package com.god.attendence;

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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.ImageLoader.ImageContainer;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class Login extends Activity {

	private EditText etSapid;
	private EditText etPass;
	private EditText etCaptcha;
	private ImageView ivCapImg;
	private Button bLogin;
	private Button bRefreshCaptcha;
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
		etCaptcha =  (EditText) findViewById(R.id.etCap);
		ivCapImg = (ImageView) findViewById(R.id.imageView1);
		bLogin = (Button) findViewById(R.id.bLogin);
		bRefreshCaptcha = (Button) findViewById(R.id.bCaptcha);

		// Get the captcha image and the hidden data
		getImg();
		getHiddenData();

		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Logging in...");
		pd.setIndeterminate(true);
		pd.setCancelable(false);

		// OnClickListener event for the Reload captcha Button
		bRefreshCaptcha.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {	
				Log.i(Login.class.toString(), "Refreshing Captcha...");
				getImg();
			}
		});

		// OnClickListener event for the Login Button
		bLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Validate;

				pd.show();
				String mURL = "https://academics.ddn.upes.ac.in/upes/index.php";
				StringRequest request = new StringRequest(Method.POST,
						mURL,
						loginOnCLickSuccessListener(),
						loginOnCLickErrorListener()) {

					public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
						Map<String, String> headers = new HashMap<String, String>();
						headers.put("Accept-Charset", charset);
						headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
						headers.put("User-Agent", getString(R.string.UserAgent));
						return headers;
					};

					protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
						Map<String, String> params = data;
						params.put("username", etSapid.getText().toString());
						params.put("passwd", etPass.getText().toString());
						params.put("txtCaptcha", etCaptcha.getText().toString());
						return params;
					};
				};
				request.setShouldCache(true);
				MyVolley.getInstance().addToRequestQueue(request);
			}
		});
	}

	private void getImg() 
	{
		// TODO Set priority and timeout
		Log.i(Login.class.getName(), "Loading captcha image...");
		ImageLoader imageLoader = MyVolley.getInstance().getImageLoader();
		imageLoader.setBatchedResponseDelay(0);
		imageLoader.get("https://academics.ddn.upes.ac.in/upes/modules/create_image.php",
				new ImageLoader.ImageListener() {

			final ImageView view = ivCapImg;
			final int defaultImageResId = R.drawable.spinner_black_48;
			final int errorImageResId = R.drawable.ic_menu_report_image;
			@Override
			public void onErrorResponse(VolleyError error) {
				if (errorImageResId != 0) {
					view.setImageResource(errorImageResId);
				}
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					view.setScaleType(ScaleType.FIT_XY);
					view.setImageBitmap(response.getBitmap());
					Log.i(Login.class.getName(), "Loaded captcha image.");
				} else if (defaultImageResId != 0) {
					view.setScaleType(ScaleType.FIT_CENTER);
					view.setImageResource(defaultImageResId);
				}
			}
		});
	}

	private void getHiddenData()
	{
		Log.i(Login.class.getName(),"Collecting hidden data...");
		String mURL = "https://academics.ddn.upes.ac.in/upes/";
		StringRequest request = new StringRequest(Method.GET,
				mURL,
				getHiddenDataSuccessListener(),
				getHiddenDataErrorListener()) {

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

				if(document.data().toString().equals("alert('Please enter correct code.'); window.history.go(-1);"))
				{
					pd.dismiss();
					AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
					builder.setMessage("Incorrect Captcha!\nPlease try again.");
					builder.setCancelable(true);
					builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

					AlertDialog alert = builder.create();
					alert.show();
				}
				else if(document.data().toString().equals("alert('Incorrect username or password. Please try again.'); window.history.go(-1);"))
				{
					pd.dismiss();
					AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
					builder.setMessage("Incorrect username or password. Please try again");
					builder.setCancelable(true);
					builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

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

					Log.i(Attendence.class.getName(), "Setting LOGGEDIN pref to true");
					SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
					SharedPreferences.Editor editor1 = settings.edit();
					editor1.putBoolean("LOGGEDIN", true);
					editor1.commit();

					pd.dismiss();
					MyVolley.getInstance().cancelPendingRequests();
					Intent ourIntent = new Intent(Login.this, Attendence.class);
					startActivity(ourIntent);
					finish();
				}
			}
		};
	}

	private Response.ErrorListener getHiddenDataErrorListener() {
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

	private Response.ErrorListener loginOnCLickErrorListener() {
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
	protected void onPause() {
		// TODO Save the user account;
		super.onPause();
	}	

	@Override
	protected void onDestroy() {
		MyVolley.getInstance().cancelPendingRequests();
		super.onDestroy();
	}
}
