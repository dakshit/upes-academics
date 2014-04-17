package com.shalzz.attendance.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.shalzz.attendance.R;

public class PagerFragment extends SherlockFragment{
	TimeTablePagerAdapter mTimeTablePagerAdapter;
	ViewPager mViewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(container==null)
			return null;

		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.swipe_layout, container, false);
		// ViewPager and its adapters use support library
		// fragments, so use getSupportFragmentManager.
		mTimeTablePagerAdapter =
				new TimeTablePagerAdapter(
						getFragmentManager());
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setAdapter(mTimeTablePagerAdapter);
		return view;
	}

	// Since this is an object collection, use a FragmentStatePagerAdapter,
	// and NOT a FragmentPagerAdapter.
	public class TimeTablePagerAdapter extends FragmentStatePagerAdapter {
		public TimeTablePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new TimeTableFragment();
			Bundle args = new Bundle();
			// Our object is just an integer :-P
			//args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
			//fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 100;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "OBJECT " + (position + 1);
		}
	}
}
