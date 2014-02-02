package com.shalzz.attendance;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockExpandableListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Attendance extends SherlockExpandableListActivity {

	private String charset = HTTP.ISO_8859_1;
	private ProgressDialog pd;
	private View footer;
	private View header;
	public static final int DEVICE_VERSION   = Build.VERSION.SDK_INT;
	public static final int DEVICE_HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;
	// TODO: add myTag

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attenview);

		ExpandableListView listview = getExpandableListView();
		LayoutInflater inflater = this.getLayoutInflater();

		header=inflater.inflate(R.layout.list_header, null);
		listview.addHeaderView(header);

		footer=inflater.inflate(R.layout.list_footer, null);
		listview.addFooterView(footer);

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
			pd.setOnCancelListener(progressDialogCancelListener());
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
			updateHeaderNFooter();			
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			boolean alpha = sharedPref.getBoolean("alpha_subject_order", true);
			List<Subject> subjects;

			if(alpha) {
				subjects = db.getAllOrderedSubjects();
			}
			else {
				subjects = db.getAllSubjects();
			}
			setListAdapter(new ExpandableListAdapter(this,subjects));
		}
		else
		{
			getAttendance();
		}
	}

	private void updateHeaderNFooter() {

		TextView tvPercent = (TextView) footer.findViewById(R.id.tvTotalPercent);
		TextView tvClasses = (TextView) footer.findViewById(R.id.tvClass);
		ProgressBar pbPercent = (ProgressBar) footer.findViewById(R.id.pbTotalPercent);
		DatabaseHandler db = new DatabaseHandler(Attendance.this);
		ListFooter listfooter = db.getListFooter();
		Float percent = listfooter.getPercentage();


		Rect bounds = pbPercent.getProgressDrawable().getBounds();
		if(percent<67.0) {
			pbPercent.setProgressDrawable(getResources().getDrawable(R.drawable.progress_amber));
		}
		else if(percent<75.0) {
			pbPercent.setProgressDrawable(getResources().getDrawable(R.drawable.progress_yellow));
		}
		else {
			pbPercent.setProgressDrawable(getResources().getDrawable(R.drawable.progress_neon_green));
		}
		pbPercent.getProgressDrawable().setBounds(bounds);

		tvPercent.setText(listfooter.getPercentage()+"%");
		tvClasses.setText(listfooter.getAttended().intValue()+"/"+listfooter.getHeld().intValue());
		pbPercent.setProgress(percent.intValue());

		TextView tvName = (TextView) header.findViewById(R.id.tvName);
		TextView tvSap = (TextView) header.findViewById(R.id.tvSAP);
		TextView tvcourse = (TextView) header.findViewById(R.id.tvCourse);

		ListHeader listheader = db.getListHeader();
		tvName.setText(listheader.getName());
		tvSap.setText(String.valueOf(listheader.getSAPId()));
		tvcourse.setText(listheader.getCourse());
	}

	private void getAttendance() {

		DatabaseHandler db = new DatabaseHandler(Attendance.this);
		if(db.getRowCount()<=0)
			showProgressDialog("Loading your attendance...");

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
				System.out.println(doc.text().toString());
				System.out.println(doc.getElementsByTag("title").get(0).text());

				Elements tddata = doc.select("td");
				ListHeader header = new ListHeader();
				int i=0;

				if(doc.getElementsByTag("title").get(0).text().equals("UPES - Home"))
				{
					// TODO: relogin
					Toast.makeText(Attendance.this, "It seems your session has expired.\nPlease Login again.", Toast.LENGTH_LONG).show();
				}
				else if (tddata != null && tddata.size() > 0)
				{
					for(Element element : tddata)
					{
						if(i==5)					
							header.setName(element.text());	
						else if(i==8)					
							header.setFatherName(element.text());
						else if(i==11)
							header.setCourse(element.text());
						else if(i==14)					
							header.setSection(element.text());
						else if(i==17)					
							header.setRollNo(element.text());
						else if(i==20)					
							header.setSAPId(Integer.parseInt(element.text()));
						else if(i>29)
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

					Elements total = doc.select("th");
					ListFooter footer = new ListFooter();
					footer.setAttended(Float.parseFloat(total.get(10).text()));
					footer.setHeld(Float.parseFloat(total.get(9).text()));
					footer.setPercentage(Float.parseFloat(total.get(12).text()));
					DatabaseHandler db = new DatabaseHandler(Attendance.this);
					db.addOrUpdateListFooter(footer);
					db.addOrUpdateListHeader(header);

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
						db.addOrUpdateSubject(subject);
					}
					db.close();
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
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (DEVICE_VERSION < DEVICE_HONEYCOMB) {
			if (event.getAction() == KeyEvent.ACTION_UP &&
					keyCode == KeyEvent.KEYCODE_MENU) {
				openOptionsMenu();
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * Closes the default user soft keyboard.
	 * @param searchView
	 */
	public void closeKeyboard(SearchView searchView) {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			// only will trigger it if no physical keyboard is open
			imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		final SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setQueryHint("Search for subjects");

		searchItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				DatabaseHandler db = new DatabaseHandler(Attendance.this);
				List<Subject> subjects = db.getAllOrderedSubjects();
				setListAdapter(new ExpandableListAdapter(Attendance.this,subjects));
				return true;  // Return true to collapse action view
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				// Do something when expanded
				return true;  // Return true to expand action view
			}
		});

		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				closeKeyboard(searchView);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				DatabaseHandler db = new DatabaseHandler(Attendance.this);
				List<Subject> subjects = db.getAllSubjectsLike(arg0);
				setListAdapter(new ExpandableListAdapter(Attendance.this,subjects));
				return false;
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_logout)
		{			
			new UserAccount(Attendance.this).Logout();

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


