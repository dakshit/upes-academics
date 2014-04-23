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

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shalzz.attendance.DataAPI;
import com.shalzz.attendance.DataAssembler;
import com.shalzz.attendance.DatabaseHandler;
import com.shalzz.attendance.Miscellaneous;
import com.shalzz.attendance.R;
import com.shalzz.attendance.UserAccount;
import com.shalzz.attendance.adapter.MySpinnerAdapter;
import com.shalzz.attendance.adapter.TimeTablePagerAdapter;
import com.shalzz.attendance.wrapper.MySyncManager;
import com.shalzz.attendance.wrapper.MyVolley;
import com.shalzz.attendance.wrapper.MyVolleyErrorHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class TimeTablePagerFragment extends SherlockFragment{

	private TimeTablePagerAdapter mTimeTablePagerAdapter;
	private ViewPager mViewPager;
	private String myTag = "Pager Fragment";
	private Context mContext;
	private Miscellaneous misc;
	private ActionBar actionbar;
	private static final String PREFERENCE_ACTIVATED_TAB = "ActivatedTab";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		misc  = new Miscellaneous(mContext);
		actionbar= getSherlockActivity().getSupportActionBar();
		actionbar.setDisplayShowTitleEnabled(false);

		SpinnerAdapter mSpinnerAdapter = new MySpinnerAdapter(mContext);
		
		OnNavigationListener mOnNavigationListener = new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int position, long itemId) {
				if(position == 0) {

				}
				else if (position == 1) {
					scrollToToday();
					persistCurrentTab();
					Log.d(myTag,"Scrolling to Today");
					return true;
				}

				return true;
			}
		};

		actionbar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(container==null)
			return null;

		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.swipe_layout, container, false);

		mTimeTablePagerAdapter = new TimeTablePagerAdapter(getSherlockActivity().getSupportFragmentManager());
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setAdapter(mTimeTablePagerAdapter);
		return view;
	}

	@Override
	public void onStart() {
		DatabaseHandler db = new DatabaseHandler(mContext);
		if(db.getRowCountofTimeTable()<=0) {
			MySyncManager.addPeriodicSync(mContext);
			DataAPI.getTimeTable(mContext, timeTableSuccessListener(), myErrorListener());
			misc.showProgressDialog("Loading your TimeTable...", true, pdCancelListener());
		}
		else
			scrollToToday();
		super.onStart();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.time_table, menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view

		DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
		ListView mDrawerList = (ListView) getActivity().findViewById(R.id.list_slidermenu);
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.menu_refresh).setVisible(!drawerOpen);

		// set the Navigation mode to standard
		if(drawerOpen) {
			actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionbar.setDisplayShowTitleEnabled(true);
		}
		else {
			actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			actionbar.setDisplayShowTitleEnabled(false);
		}

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
			persistCurrentTab();
			DataAPI.getTimeTable(mContext, timeTableSuccessListener(), myErrorListener());
			misc.showProgressDialog("Refreshing your TimeTable...", true, pdCancelListener());
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateFragments() {
		for (DayFragment fragment : mTimeTablePagerAdapter.getActiveFragments()) {
			Log.d("TimeTableActivity", "Update Fragment " + fragment.getDate() + " with new data.");
			fragment.setTimeTable();
		}
	}

	private void scrollToToday() {
		mViewPager.setCurrentItem(14, true);
	}

	private void persistCurrentTab() {
		SharedPreferences settings = mContext.getSharedPreferences("SETTINGS", 0);
		SharedPreferences.Editor editor = settings.edit();
		int currentTab = mViewPager.getCurrentItem();
		editor.putInt(PREFERENCE_ACTIVATED_TAB, currentTab);
		editor.commit();

		Log.d(myTag, "Saving current view " + currentTab);
	}

	private void reloadCurrentTab() {
		SharedPreferences settings = mContext.getSharedPreferences("SETTINGS", 0);
		int activatedTab = settings.getInt(PREFERENCE_ACTIVATED_TAB, 14);

		mViewPager.setCurrentItem(activatedTab);
		Log.d(myTag, "Scrolling to " + activatedTab);
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

	private Response.Listener<String> timeTableSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {				
				DataAssembler.parseTimeTable(response,mContext);
				misc.dismissProgressDialog();
				updateFragments();
				reloadCurrentTab();
				Log.i(myTag,"Sync complete");
			}
		};
	}

	private Response.ErrorListener myErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				misc.dismissProgressDialog();
				String msg = MyVolleyErrorHelper.getMessage(error, mContext);
				Crouton.makeText(getActivity(),  msg, Style.ALERT).show();
				Log.e(myTag, msg);
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();
		reloadCurrentTab();
	}

	@Override
	public void onPause() {
		super.onPause();
		persistCurrentTab();
	}

	@Override
	public void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
}
