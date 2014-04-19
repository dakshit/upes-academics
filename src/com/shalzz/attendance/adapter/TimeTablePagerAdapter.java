package com.shalzz.attendance.adapter;

import java.util.Collection;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.shalzz.attendance.fragment.DayFragment;
import com.shalzz.attendance.wrapper.DateHelper;

public class TimeTablePagerAdapter extends FragmentStatePagerAdapter {

	@SuppressLint("UseSparseArrays")
	private final HashMap<Integer, DayFragment> activeFragments = new HashMap<Integer, DayFragment>();
	
	public TimeTablePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		DayFragment fragment = new DayFragment();
		Bundle args = new Bundle();
		args.putInt(DayFragment.ARG_WEEKDAY, DateHelper.getWeekday()+position);
		fragment.setArguments(args);
		
		activeFragments.put(position, fragment);
		
		return fragment;
	}

	@Override
	public int getCount() {
		return 31;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		activeFragments.remove(position);
		super.destroyItem(container, position, object);
	}

	public Collection<DayFragment> getActiveFragments() {
		return activeFragments.values();
	}
}
