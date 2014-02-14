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

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Attendance extends SherlockExpandableListActivity {

	private String charset = HTTP.ISO_8859_1;
	private View footer;
	private View header;
	private Miscellanius misc = new Miscellanius(this);
	private String myTag = getClass().getName();
	
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
		getAttendance();
	}

	private void setAttendance() {
		DatabaseHandler db = new DatabaseHandler(Attendance.this);
		if(db.getRowCount()>0)
		{
			updateHeaderNFooter();
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			boolean alpha = sharedPref.getBoolean("alpha_subject_order", true);
			boolean expandLimit = sharedPref.getBoolean("subjects_expanded_limit", false);

			List<Subject> subjects;
			if(alpha) 
				subjects = db.getAllOrderedSubjects();
			else 
				subjects = db.getAllSubjects();
			final ExpandableListAdapter mAdapter = new ExpandableListAdapter(this,subjects);
			final ExpandableListView listview = getExpandableListView();
			setListAdapter(mAdapter);

			if(expandLimit) {
				listview.setOnGroupExpandListener(new OnGroupExpandListener() {
					public void onGroupExpand(int groupPosition) {
						int len = mAdapter.getGroupCount();
						for (int i = 0; i < len; i++) {
							if (i != groupPosition) {
								listview.collapseGroup(i);
							}
						}
					}
				});
			}
			else
			{
				listview.setOnGroupExpandListener(new OnGroupExpandListener() {
					public void onGroupExpand(int groupPosition) {
						return;
					}
				});
			}
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
			misc.showProgressDialog("Loading your attendance...", true, pdCancelListener());

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
		MyVolley.getInstance().addToRequestQueue(request ,myTag);
		Log.i(myTag, "Request added to queue");
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

				Log.i(myTag, "Parsing response...");
				Document doc = Jsoup.parse(response);
				System.out.println(doc.text().toString());
				System.out.println(doc.getElementsByTag("title").get(0).text());

				Elements tddata = doc.select("td");

				if(doc.getElementsByTag("title").get(0).text().equals("UPES - Home"))
				{
					// TODO: relogin
					String msg ="It seems your session has expired.\nPlease Login again.";
					Crouton.makeText(Attendance.this,  msg, Style.ALERT).show();
				}
				else if (tddata != null && tddata.size() > 0)
				{
					int i=0;
					ListHeader header = new ListHeader();
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

					Log.i(myTag, "Response parsing complete.");

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
					if(db.getRowCount()>0)
						Crouton.makeText(Attendance.this, "Successfully updated attendance", Style.CONFIRM).show();
				}

				setAttendance();
				misc.dismissProgressDialog();
			}
		};
	}

	private Response.ErrorListener myErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String msg = VolleyErrorHelper.getMessage(error, Attendance.this);
				Crouton.clearCroutonsForActivity(Attendance.this);
				Crouton.makeText(Attendance.this,  msg, Style.ALERT).show();
				Log.e(myTag, msg);
				misc.dismissProgressDialog();
			}
		};
	}

	DialogInterface.OnCancelListener pdCancelListener() {
		return new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// Cancel all pending requests when user presses back button.
				Crouton.makeText(Attendance.this, "Network Request canceled", Style.INFO).show();
				MyVolley.getInstance().cancelPendingRequests(myTag);
			}
		};

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		final SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setQueryHint("Search subjects");

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
				Miscellanius.closeKeyboard(Attendance.this, searchView);
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
			misc.showProgressDialog("Refreshing your attendance...", true, pdCancelListener());
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
		MyVolley.getInstance().cancelPendingRequests(myTag);
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
}


