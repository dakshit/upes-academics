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

package com.shalzz.attendance.fragment;

import java.util.List;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.shalzz.attendance.DataAPI;
import com.shalzz.attendance.DataAssembler;
import com.shalzz.attendance.DatabaseHandler;
import com.shalzz.attendance.Miscellaneous;
import com.shalzz.attendance.R;
import com.shalzz.attendance.UserAccount;
import com.shalzz.attendance.activity.MainActivity;
import com.shalzz.attendance.adapter.ExpandableListAdapter;
import com.shalzz.attendance.model.ListFooter;
import com.shalzz.attendance.model.ListHeader;
import com.shalzz.attendance.model.Subject;
import com.shalzz.attendance.wrapper.MySyncManager;
import com.shalzz.attendance.wrapper.MyVolley;
import com.shalzz.attendance.wrapper.MyVolleyErrorHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AttendanceListFragment extends SherlockListFragment{

	private View footer;
	private View header;
	private Context mContext;
	private Miscellaneous misc;
	private String myTag ;
	Object syncObserverHandle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		misc = new Miscellaneous(mContext);
		myTag = getClass().getName();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(container==null)
			return null;

		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.attenview, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		ListView listview = getListView();
		LayoutInflater inflater = this.getLayoutInflater(savedInstanceState);

		header = inflater.inflate(R.layout.list_header, null);
		listview.addHeaderView(header);

		footer=inflater.inflate(R.layout.list_footer, null);
		listview.addFooterView(footer);	

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		DatabaseHandler db = new DatabaseHandler(mContext);
		if(db.getRowCount()<=0) {
			MySyncManager.addPeriodicSync(mContext);
			DataAPI.getAttendance(mContext, successListener(), errorListener());
			misc.showProgressDialog("Loading your attendance...", true, pdCancelListener());
		}
		else
			setAttendance();
		super.onStart();
	}

	private void setAttendance() {
		DatabaseHandler db = new DatabaseHandler(getActivity());
		if(db.getRowCount()>0)
		{
			updateHeaderNFooter();
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
			boolean alpha = sharedPref.getBoolean("alpha_subject_order", true);
			int expandLimit = Integer.parseInt(sharedPref.getString("subjects_expanded_limit", "0"));

			List<Subject> subjects;
			if(alpha) 
				subjects = db.getAllOrderedSubjects();
			else 
				subjects = db.getAllSubjects();
			ListView listview = getListView();
			ExpandableListAdapter mAdapter = new ExpandableListAdapter(mContext,subjects);
			SwingRightInAnimationAdapter animationAdapter = new SwingRightInAnimationAdapter(mAdapter);
			animationAdapter.setAbsListView(listview);
			animationAdapter.setInitialDelayMillis(500);
			listview.setAdapter(animationAdapter);

			mAdapter.setLimit(expandLimit);
		}
	}

	private void updateHeaderNFooter() {

		TextView tvPercent = (TextView) footer.findViewById(R.id.tvTotalPercent);
		TextView tvClasses = (TextView) footer.findViewById(R.id.tvClass);
		ProgressBar pbPercent = (ProgressBar) footer.findViewById(R.id.pbTotalPercent);
		DatabaseHandler db = new DatabaseHandler(mContext);
		ListFooter listfooter = db.getListFooter();
		Float percent = listfooter.getPercentage();

		Rect bounds = pbPercent.getProgressDrawable().getBounds();
		if(percent<67.0) {
			pbPercent.setProgressDrawable(getResources().getDrawable(R.drawable.progress_amber));
		}
		else if(percent<75.0) {
			pbPercent.setProgressDrawable(getResources().getDrawable(R.drawable.progress_yellow));
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
		
		MainActivity.getInstance().updateDrawerHeader();

	}

	DialogInterface.OnCancelListener pdCancelListener() {
		return new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// Cancel all pending requests when user presses back button.
				Crouton.makeText(getActivity(), "Refresh canceled", Style.INFO).show();
				MyVolley.getInstance().cancelPendingRequests(myTag);
			}
		};

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.main, menu);
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		final SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setQueryHint("Search subjects");

		searchItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				DatabaseHandler db = new DatabaseHandler(mContext);
				List<Subject> subjects = db.getAllOrderedSubjects();
				getListView().setAdapter(new ExpandableListAdapter(mContext,subjects));
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
				Miscellaneous.closeKeyboard(mContext, searchView);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				DatabaseHandler db = new DatabaseHandler(mContext);
				List<Subject> subjects = db.getAllSubjectsLike(arg0);
				getListView().setAdapter(new ExpandableListAdapter(mContext,subjects));
				return false;
			}
		});
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view

		DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
		ListView mDrawerList = (ListView) getActivity().findViewById(R.id.list_slidermenu);
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.menu_search).setVisible(!drawerOpen);
		menu.findItem(R.id.menu_refresh).setVisible(!drawerOpen);
		return;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_logout)
		{			
			new UserAccount(mContext).Logout();
		}
		else if(item.getItemId() == R.id.menu_refresh)
		{
			DataAPI.getAttendance(mContext, successListener(), errorListener());
			misc.showProgressDialog("Refreshing your attendance...", true, pdCancelListener());
		}
		return super.onOptionsItemSelected(item);
	}

	private Response.Listener<String> successListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {				
				misc.dismissProgressDialog();
				DataAssembler.parseAttendance(response,mContext);
				setAttendance();
			}
		};
	}

	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				misc.dismissProgressDialog();
				String msg = MyVolleyErrorHelper.getMessage(error, mContext);
				Miscellaneous.makeCroutonInfinity((Activity)mContext, msg);
				Log.e(myTag, msg);
			}
		};
	}

	@Override
	public void onResume() {
		setAttendance();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
}
