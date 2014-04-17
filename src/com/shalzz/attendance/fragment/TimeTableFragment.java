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

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
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
import com.shalzz.attendance.model.Day;
import com.shalzz.attendance.model.Period;
import com.shalzz.attendance.wrapper.MySyncManager;
import com.shalzz.attendance.wrapper.MyVolley;
import com.shalzz.attendance.wrapper.MyVolleyErrorHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class TimeTableFragment extends SherlockListFragment{
	
	private String myTag;
	private Context mContext;
	private Miscellaneous misc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		myTag = getClass().getName();
		misc  = new Miscellaneous(mContext);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(container==null)
			return null;
		
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.timetable_view, container, false);
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
			setTimeTable();
		
		super.onStart();
	}
	
	public void setTimeTable() {
		DatabaseHandler db = new DatabaseHandler(getActivity());
		Day day = db.getDay("mon");
		String PeriodArray[] = new String[20]; ;
		List<Period> periods = day.getAllPeriods();
		int i =0;
		for(Period period : periods)
		{
			PeriodArray[i]= period.getName();
			++i;
		}
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,PeriodArray ));
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
			DataAPI.getTimeTable(mContext, timeTableSuccessListener(), myErrorListener());
			misc.showProgressDialog("Refreshing your TimeTable...", true, pdCancelListener());
		}
		return super.onOptionsItemSelected(item);
	}
	
	private Response.Listener<String> timeTableSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {				
				DataAssembler.parseTimeTable(response,mContext);
				misc.dismissProgressDialog();
				setTimeTable();
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
		DatabaseHandler db = new DatabaseHandler(mContext);
		if(db.getRowCountofTimeTable()>0)
			setTimeTable();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
}
