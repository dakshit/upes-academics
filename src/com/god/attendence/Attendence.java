package com.god.attendence;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class Attendence extends Activity {

	private String charset = HTTP.ISO_8859_1;
	private TextView display;
	private CookieManager cookieMan = (CookieManager) CookieHandler.getDefault();
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attenview);

		// Reference to the layout components
		display = (TextView) findViewById(R.id.tv1);
		
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Loading your attendance...");
		pd.setIndeterminate(true);
		pd.setCancelable(true);
		pd.setOnCancelListener(progressDialogCancelListener());
		
		getAttendance();
	}
	
	private void getAttendance() {
		
		pd.show();
		Log.d(Attendence.class.getName() , cookieMan.getCookieStore().getCookies().toString());
		
		String mURL = "https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631";
		StringRequest request = new StringRequest(Method.GET,
												  mURL,
												  createMyReqSuccessListener(),
												  createMyReqErrorListener()) {
			
            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
            	Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept-Charset", charset);
                headers.put("User-Agent", getString(R.string.UserAgent));
                return headers;
            };
        };
		request.setShouldCache(true);
		request.setPriority(Priority.IMMEDIATE);
		request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		MyVolley.getInstance().addToRequestQueue(request);
		Log.i(Attendence.class.getName(), "Request added to queue");
//		try {
//			System.out.println(req.getHeaders());
//		} catch (AuthFailureError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private Response.Listener<String> createMyReqSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				Log.i(Attendence.class.getName(), "Parsing response...");
				Document doc = Jsoup.parse(response);
				System.out.println("At Login :\n"+doc.text().toString());

				Elements tddata = doc.select("td");
				int position[] = {30,37,44,51,58,65,72,79,86,93,100,107};
				int i=0,k=0;

				display.setText("");
				if (tddata != null && tddata.size() > 0)
				{
					for(Element element : tddata)
					{
						if(i>29)
						{
							if((i-30)%7==0)
							{
								display.append("\n");
							}
							String data= element.text();
							display.append(data+" ");
						}				
						++i;
					}
				}
				Log.i(Attendence.class.getName(), "Response parsing complete.");
				pd.dismiss();
			}
		};
	}

	private Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String msg = VolleyErrorHelper.getMessage(error, Attendence.this);
				Toast toast = Toast.makeText(Attendence.this, msg, Toast.LENGTH_LONG);
				toast.show();
				Log.e(Login.class.getName(), msg);
			}
		};
	}

	DialogInterface.OnCancelListener progressDialogCancelListener() {
		return new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// Cancel all pending requests when user presses back button.
				MyVolley.getInstance().cancelPendingRequests();
			}
		};
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_logout)
		{
			Log.i(Attendence.class.getName(), "Setting LOGGEDIN pref to false");
			SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("LOGGEDIN", false);
			editor.commit();

			// TODO remove persistent cookies or send logout request or both.
			
			Intent intent = new Intent(Attendence.this, Login.class);
			startActivity(intent);
			finish();
		}
		else if(item.getItemId() == R.id.menu_refresh)
		{
			getAttendance();
		}
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	protected void onStop() {
		MyVolley.getInstance().cancelPendingRequests();
		super.onStop();
	}
}


