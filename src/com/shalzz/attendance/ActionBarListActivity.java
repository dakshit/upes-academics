package com.shalzz.attendance;

import android.support.v7.app.ActionBarActivity;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ExpandableListView;

public class ActionBarListActivity  extends ActionBarActivity {

	private ExpandableListView mListView;

	protected ExpandableListView getListView() {
	    if (mListView == null) {
	        mListView = (ExpandableListView) findViewById(android.R.id.list);
	    }
	    return mListView;
	}

	protected void setListAdapter(ExpandableListAdapter expandableListAdapter) {
	    getListView().setAdapter(expandableListAdapter);
	}

	protected ListAdapter getListAdapter() {
	    ListAdapter adapter = getListView().getAdapter();
	    if (adapter instanceof HeaderViewListAdapter) {
	        return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
	    } else {
	        return adapter;
	    }
	}
}
