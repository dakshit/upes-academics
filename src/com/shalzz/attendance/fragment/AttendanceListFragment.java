package com.shalzz.attendance.fragment;

import java.util.List;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.shalzz.attendance.DatabaseHandler;
import com.shalzz.attendance.ExpandableListAdapter;
import com.shalzz.attendance.Miscellaneous;
import com.shalzz.attendance.MySyncManager;
import com.shalzz.attendance.R;
import com.shalzz.attendance.UserAccount;
import com.shalzz.attendance.model.ListFooter;
import com.shalzz.attendance.model.ListHeader;
import com.shalzz.attendance.model.Subject;
import com.shalzz.attendance.wrapper.MyVolley;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AttendanceListFragment extends SherlockListFragment{

	private View footer;
	private View header;
	private View Drawerheader;
	private Context mContext;
	private Miscellaneous misc;
	private String myTag ;

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
		ListView mDrawerList = (ListView) getActivity().findViewById(R.id.list_slidermenu);
		LayoutInflater inflater = this.getLayoutInflater(savedInstanceState);

		header = inflater.inflate(R.layout.list_header, null);
		listview.addHeaderView(header);

		footer=inflater.inflate(R.layout.list_footer, null);
		listview.addFooterView(footer);	

		Drawerheader = inflater.inflate(R.layout.drawer_header, null);
		Drawerheader.setClickable(false);
		Drawerheader.setEnabled(false);
		if(mDrawerList.getHeaderViewsCount()==0)
			mDrawerList.addHeaderView(Drawerheader);

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		DatabaseHandler db = new DatabaseHandler(mContext);
		if(db.getRowCount()<=0)
			MySyncManager.setupSync(mContext);
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


		TextView tv_name = (TextView) Drawerheader.findViewById(R.id.drawer_header_name);
		TextView tv_course = (TextView) Drawerheader.findViewById(R.id.drawer_header_course);
		tv_name.setText(listheader.getName());
		tv_course.setText(listheader.getCourse());
	}

	public void getAttendance() {
		DatabaseHandler db = new DatabaseHandler(mContext);
		if(db.getRowCount()<=0)
			misc.showProgressDialog("Loading...", true, pdCancelListener());
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
        setAttendance();
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
			misc.showProgressDialog("Refreshing your attendance...", true, pdCancelListener());
			getAttendance();
		}
		return super.onOptionsItemSelected(item);
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
