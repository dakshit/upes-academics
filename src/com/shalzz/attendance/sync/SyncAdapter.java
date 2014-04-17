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

package com.shalzz.attendance.sync;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.shalzz.attendance.DataAPI;
import com.shalzz.attendance.DataAssembler;
import com.shalzz.attendance.wrapper.MyVolleyErrorHelper;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	// Global variables
	private String myTag = "Sync Adapter";
	private Context mContext;
	
	/**
	 * Set up the sync adapter
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		/*
		 * If your app uses a content resolver, get an instance of it
		 * from the incoming Context
		 */
		mContext = context;
	}

	/**
	 * Set up the sync adapter. This form of the
	 * constructor maintains compatibility with Android 3.0
	 * and later platform versions
	 */
	@SuppressLint("NewApi")
	public SyncAdapter(
			Context context,
			boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		/*
		 * If your app uses a content resolver, get an instance of it
		 * from the incoming Context
		 */
		mContext = context;
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.i(myTag,"Running sync adapter");

		DataAPI.getAttendance(mContext, attendanceSuccessListener(), myErrorListener());
		DataAPI.getTimeTable(mContext, timeTableSuccessListener(), myErrorListener());
	}   
	
	private Response.Listener<String> attendanceSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {				
				DataAssembler.parseAttendance(response,mContext);
			}
		};
	}
	
	private Response.Listener<String> timeTableSuccessListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {				
				DataAssembler.parseTimeTable(response,mContext);
				Log.i(myTag,"Sync complete");
			}
		};
	}

	private Response.ErrorListener myErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				String msg = MyVolleyErrorHelper.getMessage(error, mContext);
				Log.e(myTag, msg);
			}
		};
	}
}


