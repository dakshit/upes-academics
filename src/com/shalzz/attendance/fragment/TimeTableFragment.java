package com.shalzz.attendance.fragment;

import java.util.List;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.shalzz.attendance.DatabaseHandler;
import com.shalzz.attendance.Miscellaneous;
import com.shalzz.attendance.R;
import com.shalzz.attendance.UserAccount;
import com.shalzz.attendance.model.Day;
import com.shalzz.attendance.model.Period;
import com.shalzz.attendance.wrapper.MySyncManager;
import com.shalzz.attendance.wrapper.MyVolley;

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
		if(db.getRowCountofTimeTable()<=0)
			getTimeTable();
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
	
	public void getTimeTable() {
		
		// Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        Account mAccount = MySyncManager.getSyncAccount(mContext);
        String AUTHORITY = MySyncManager.AUTHORITY;
        
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
        while(ContentResolver.isSyncActive(mAccount, AUTHORITY) || ContentResolver.isSyncPending(mAccount, AUTHORITY)){
        	
        }
        misc.dismissProgressDialog();
        setTimeTable();
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
			misc.showProgressDialog("Refreshing your TimeTable...", true, pdCancelListener());
			getTimeTable();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
//		DatabaseHandler db = new DatabaseHandler(mContext);
//		if(db.getRowCountofTimeTable()>0)
//			setTimeTable();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
}
