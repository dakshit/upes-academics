package com.g.o.d.shalzz.attendance;

import java.util.List;

import com.god.attendence.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context myContext;
	private List<Subject> subjects;

	public ExpandableListAdapter(Context context) {
		myContext = context;
		DatabaseHandler db = new DatabaseHandler(myContext);
		subjects= db.getAllSubjects();
	}
	
	@Override
	public int getGroupCount() {
		return subjects.size();
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_row,null);
		}

		TextView tvSubject = (TextView) convertView.findViewById(R.id.tvSubj);
		TextView tvPercent = (TextView) convertView.findViewById(R.id.tvPercent);
		TextView tvClasses = (TextView) convertView.findViewById(R.id.tvClasses);
		ProgressBar pbPercent = (ProgressBar) convertView.findViewById(R.id.pbPercent);

		tvSubject.setText(subjects.get(groupPosition).getName());
		tvPercent.setText(subjects.get(groupPosition).getPercentage().toString()+"%");
		tvClasses.setText(subjects.get(groupPosition).getClassesAttended().intValue()+"/"+subjects.get(groupPosition).getClassesHeld().intValue());
		pbPercent.setProgress(subjects.get(groupPosition).getPercentage().intValue());
		
		return convertView;
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_child_item, null);
		}
		// TODO: add code.
		return convertView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
