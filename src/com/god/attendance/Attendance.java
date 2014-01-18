package com.god.attendance;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.god.attendence.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Attendance extends ActionBarListActivity {

	private String charset = HTTP.ISO_8859_1;
	private CookieManager cookieMan = (CookieManager) CookieHandler.getDefault();
	private ProgressDialog pd;
	private ArrayList<String> subjects = new ArrayList<String>();
	private ArrayList<Float> percentage = new ArrayList<Float>();
	// TODO: add myTag

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attenview);

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
		Log.d(Attendance.class.getName() , cookieMan.getCookieStore().getCookies().toString());

		String mURL = "https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631";
		StringRequest request = new StringRequest(Method.GET,
				mURL,
				createMyReqSuccessListener(),
				myErrorListener()) {

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept-Charset", charset);
				headers.put("User-Agent", getString(R.string.UserAgent));
				headers.put("Connection", "keep-alive");
				return headers;
			};
		};
		request.setShouldCache(true);
		request.setPriority(Priority.IMMEDIATE);
		request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		MyVolley.getInstance().addToRequestQueue(request ,"ATTENDENCE");
		Log.i(Attendance.class.getName(), "Request added to queue");
		//		try {
		//			System.out.println(req.getHeaders());
		//		} catch (AuthFailureError e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
	}

	private void Logout() {

		pd.setMessage("Logging out...");
		pd.setCancelable(false);
		pd.show();

		String mURL = "https://academics.ddn.upes.ac.in/upes/index.php?option=logout";
		StringRequest request = new StringRequest(Method.POST,
				mURL,
				new Response.Listener<String>() {
			    	@Override
			    	public void onResponse(String response) {
			    		System.out.println(Jsoup.parse(response).text().toString());

			    		Log.i(Attendance.class.getName(), "Setting LOGGEDIN pref to false");
			    		SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
			    		SharedPreferences.Editor editor = settings.edit();
			    		editor.putBoolean("LOGGEDIN", false);
			    		editor.commit();

			    		pd.dismiss();
			    		Intent intent = new Intent(Attendance.this, Login.class);
			    		startActivity(intent);
			    		finish();
			    	}
		       },
		       myErrorListener()) {

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept-Charset", charset);
				headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
				headers.put("User-Agent", getString(R.string.UserAgent));
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
		request.setShouldCache(true);
		request.setPriority(Priority.IMMEDIATE);
		MyVolley.getInstance().addToRequestQueue(request,"ATTENDENCE");
	}

	private Response.Listener<String> createMyReqSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {				

				ArrayList<Float> claHeld = new ArrayList<Float>();
				ArrayList<Float> claAttended = new ArrayList<Float>();
				ArrayList<String> abDates = new ArrayList<String>();
				ArrayList<String> projPer = new ArrayList<String>();
				
				Log.i(Attendance.class.getName(), "Parsing response...");
				Document doc = Jsoup.parse(response);
				System.out.println("At Login :\n"+doc.text().toString());

				Elements tddata = doc.select("td");
				int i=0;

				if (tddata != null && tddata.size() > 0)
				{
					String data = null;
					for(Element element : tddata)
					{
						if(i==5)					
							data=("Name: "+element.text());					
						if(i==11)
							subjects.add(data+"\nCourse: "+element.text());
						if(i>29)
						{
							// for subjects
							if ((i - 30) % 7 == 0) {
								subjects.add(element.text());
							}
							// for Classes Held
							if ((i - 31) % 7 == 0) {
								claHeld.add(Float.parseFloat(element.text()));
							}
							// for Classes attended
							if ((i - 32) % 7 == 0) {
								claAttended.add(Float.parseFloat(element.text()));
							}
							// for Dates Absent
							if ((i - 33) % 7 == 0) {
								abDates.add(element.text());
							}
							// for attendance percentage
							if ((i - 34) % 7 == 0) {
								percentage.add(Float.parseFloat(element.text()));
							}
							// for projected percentage
							if ((i - 35) % 7 == 0) {
								projPer.add(element.text());
							}
						}
						++i;
					}
				}
				Log.i(Attendance.class.getName(), "Response parsing complete.");
				
				SQLiteDatabase db = openOrCreateDatabase("attendence", MODE_PRIVATE, null);
				db.execSQL("CREATE TABLE IF NOT EXIST attenData (Subject VARCHAR PRIMARYKEY, ClaHeld FLOAT, ClaAttended FLOAT, AbDate VARCHAR, Percentage FLOAT,ProjPer VARCHAR);");
				
				// TODO: reset list view
				setListAdapter(new MyAdapter(Attendance.this, android.R.layout.simple_list_item_1,R.id.tvSubj, subjects));
				pd.dismiss();
			}
		};
	}

	private Response.ErrorListener myErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String msg = VolleyErrorHelper.getMessage(error, Attendance.this);
				Toast toast = Toast.makeText(Attendance.this, msg, Toast.LENGTH_LONG);
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
				MyVolley.getInstance().cancelPendingRequests("ATTENDENCE");
			}
		};

	}

	private class MyAdapter extends ArrayAdapter<String> {

		public MyAdapter(Context context, int resource, int textViewResourceId,
				List<String> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.list_row, parent ,false);

			TextView tvSubject = (TextView) row.findViewById(R.id.tvSubj);
			TextView tvPercent = (TextView) row.findViewById(R.id.tvPercent);
			ProgressBar pbPercent = (ProgressBar) row.findViewById(R.id.pbPercent);

			tvSubject.setText(subjects.get(position));

			if(position!=0)
			{
				int pos= position-1;
				int percent = Float.floatToIntBits(percentage.get(pos));				
				tvPercent.setText(percentage.get(pos).toString()+"%");
				pbPercent.setProgress(percent);
			}

			if(position==0)
			{
				tvPercent.setVisibility(View.GONE);
				pbPercent.setVisibility(View.GONE);
			}

			return row;
		}
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
			Logout();

			//			// TODO remove persistent cookies or send logout request or both.
			//			SharedPreferences pcookies = getSharedPreferences("PERSISTCOOKIES", 0);
			//			SharedPreferences.Editor editor1 = pcookies.edit();
			//			Iterator<String> keyset = pcookies.getAll().keySet().iterator();
			//			while(keyset.hasNext())
			//			{
			//				String cookiename = keyset.next();
			//				editor1.remove(cookiename);
			//			}
			//			editor1.commit();
			//
			//			cookieMan.getCookieStore().removeAll();
		}
		else if(item.getItemId() == R.id.menu_refresh)
		{
			pd.setMessage("Refreshing your attendance...");
			getAttendance();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		MyVolley.getInstance().cancelPendingRequests("ATTENDENCE");
		super.onDestroy();
	}
}


