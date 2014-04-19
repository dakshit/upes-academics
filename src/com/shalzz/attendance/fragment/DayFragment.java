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

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.shalzz.attendance.DatabaseHandler;
import com.shalzz.attendance.R;
import com.shalzz.attendance.model.Day;
import com.shalzz.attendance.model.Period;
import com.shalzz.attendance.wrapper.DateHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class DayFragment extends SherlockListFragment {
	
	private Context mContext;
	private Date date = null;
	public static final String ARG_WEEKDAY = "weekday";
	public static final String ARG_DATE = "date";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
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
		if(db.getRowCountofTimeTable()>0) 
			setTimeTable();
		super.onStart();
	}
	
	public void setTimeTable() {
		DatabaseHandler db = new DatabaseHandler(getActivity());
		Day day = db.getDay(getWeekDay());
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

	public String getWeekDay() {
		if(date == null)
			setDate();
		return DateHelper.getWeekday(date);
	}
	
	public void setDate() {
		date = (Date) getArguments().getSerializable(ARG_DATE);
	}
	
	public Date getDate() {
		return date;
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
