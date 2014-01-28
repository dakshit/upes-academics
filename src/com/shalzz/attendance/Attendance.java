package com.shalzz.attendance;

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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shalzz.attendance.R;

public class Attendance extends ActionBarListActivity {

	private String charset = HTTP.ISO_8859_1;
	private CookieManager cookieMan = (CookieManager) CookieHandler.getDefault();
	private ProgressDialog pd;
	private DatabaseHandler db = new DatabaseHandler(Attendance.this);
	// TODO: add myTag

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attenview);
        
		setAttendance();
		//getAttendance();
		
	}

	/**
	 * Displays the default Progress Dialog.
	 * @param mMessage
	 */
	private void showProgressDialog(String mMessage) {
		// lazy initialize
		if(pd==null)
		{
			// Setup the Progress Dialog
			pd = new ProgressDialog(this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage(mMessage);
			pd.setIndeterminate(true);
			pd.setCancelable(true);
		}
		// if there's a progressDialog dismiss it.
		dismissProgressDialog();
		pd.show();
	}

	/**
	 * Dismisses the Progress Dialog.
	 */
	private void dismissProgressDialog() {
		if(pd!=null)
			pd.dismiss();
	}

	private void setAttendance() {
		DatabaseHandler db = new DatabaseHandler(Attendance.this);
		if(db.getRowCount()>0)
		{
			ExpandableListView listview = getListView();
			LayoutInflater inflater=this.getLayoutInflater();
			
			View header=inflater.inflate(R.layout.list_header, null);
			listview.addHeaderView(header);
			
			View footer=inflater.inflate(R.layout.list_footer, null);
			listview.addFooterView(footer);
			
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			boolean alpha = sharedPref.getBoolean("alpha_subject_order", true);
			List<Subject> subjects;
			
			if(alpha) {
				subjects = db.getAllOrderedSubjects();
			}
			else {
				subjects = db.getAllSubjects();
			}
			// TODO header and footer.
			setListAdapter(new ExpandableListAdapter(this,subjects));
		}
		else
		{
			getAttendance();
		}
	}

	private void getAttendance() {

		DatabaseHandler db = new DatabaseHandler(Attendance.this);
		if(db.getRowCount()<=0)
			showProgressDialog("Loading your attendance...");
		Log.d(Attendance.class.getName() , cookieMan.getCookieStore().getCookies().toString());

		String mURL = "https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631";
		StringRequest request = new StringRequest(Method.POST,
				mURL,
				createMyReqSuccessListener(),
				myErrorListener()) {

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
		MyVolley.getInstance().addToRequestQueue(request ,"ATTENDENCE");
		Log.i(Attendance.class.getName(), "Request added to queue");
	}

	private Response.Listener<String> createMyReqSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {				

				ArrayList<Float> claHeld = new ArrayList<Float>();
				ArrayList<Float> claAttended = new ArrayList<Float>();
				ArrayList<String> abDates = new ArrayList<String>();
				ArrayList<String> projPer = new ArrayList<String>();
				ArrayList<String> subjectName = new ArrayList<String>();
				ArrayList<Float> percentage = new ArrayList<Float>();

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
						//						if(i==5)					
						//							data=("Name: "+element.text());					
						//						if(i==11)
						//							subjectName.add(data+"\nCourse: "+element.text());
						if(i>29)
						{
							// for subjects
							if ((i - 30) % 7 == 0) {
								subjectName.add(element.text());
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

				for(i=0;i<claHeld.size();i++)
				{
					Subject subject = new Subject(i+1, 
							subjectName.get(i),
							claHeld.get(i),
							claAttended.get(i),
							abDates.get(i),
							percentage.get(i),
							projPer.get(i));
					DatabaseHandler db = new DatabaseHandler(Attendance.this);
					db.addOrUpdateSubject(subject);
				}

				setAttendance();
				dismissProgressDialog();
			}
		};
	}

	private Response.ErrorListener myErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String msg = VolleyErrorHelper.getMessage(error, Attendance.this);
				Toast.makeText(Attendance.this, msg, Toast.LENGTH_LONG).show();
				Log.e(Login.class.getName(), msg);
				dismissProgressDialog();
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
			new UserAccount(Attendance.this).Logout();

		}
		else if(item.getItemId() == R.id.menu_search)
		{			
			//List<Subject> subjects = db.getAllSubjectsLike(wildcard);
			//setListAdapter(new ExpandableListAdapter(this,subjects));

		}
		else if(item.getItemId() == R.id.menu_settings)
		{
			startActivity(new Intent(this, Settings.class));
		}
		else if(item.getItemId() == R.id.menu_refresh)
		{
			showProgressDialog("Refreshing your attendance...");
			getAttendance();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		setAttendance();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		MyVolley.getInstance().cancelPendingRequests("ATTENDENCE");
		super.onDestroy();
	}
}


