package com.shalzz.attendance;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haarman.listviewanimations.itemmanipulation.ExpandableListItemAdapter;
import com.shalzz.attendance.R;

public class ExpandableListAdapter extends ExpandableListItemAdapter<Subject> {

	private Context myContext;
	private List<Subject> mSubjects;

	public ExpandableListAdapter(Context context,List<Subject> subjects) {
		super(context,subjects);
		myContext = context;
		mSubjects = subjects;
	}

	@Override
	public View getTitleView(int position, View convertView, ViewGroup parent) {

		Float percent = mSubjects.get(position).getPercentage();
		LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if(percent<67.0) {
			convertView = inflater.inflate(R.layout.list_group_item_amber,null);
		}
		else if(percent<75.0) {
			convertView = inflater.inflate(R.layout.list_group_item_yellow,null);
		}
		else{
			convertView = inflater.inflate(R.layout.list_group_item_green,null);
		}

		TextView tvSubject = (TextView) convertView.findViewById(R.id.tvSubj);
		TextView tvPercent = (TextView) convertView.findViewById(R.id.tvPercent);
		TextView tvClasses = (TextView) convertView.findViewById(R.id.tvClasses);
		ProgressBar pbPercent = (ProgressBar) convertView.findViewById(R.id.pbPercent);

		tvSubject.setText(mSubjects.get(position).getName());
		tvPercent.setText(mSubjects.get(position).getPercentage().toString()+"%");
		tvClasses.setText(mSubjects.get(position).getClassesAttended().intValue()+"/"+mSubjects.get(position).getClassesHeld().intValue());
		pbPercent.setProgress(percent.intValue());	

		return convertView;
	}

	@Override
	public View getContentView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.list_child_item, null);

		TextView tvAbsent = (TextView) view.findViewById(R.id.tvAbsent);
		TextView tvProjected = (TextView) view.findViewById(R.id.tvProjected);
		TextView tvReach = (TextView) view.findViewById(R.id.tvReach);
		TextView tvClass = (TextView) view.findViewById(R.id.tvClass);
		ImageView ivAlert = (ImageView) view.findViewById(R.id.imageView1);

		int held = mSubjects.get(position).getClassesHeld().intValue();
		int attend = mSubjects.get(position).getClassesAttended().intValue();
		int percent = mSubjects.get(position).getPercentage().intValue();

		if(held==1)
			tvClass.setText("You have attended "+attend+ " out of "+held+ " class");
		else
			tvClass.setText("You have attended "+attend+ " out of "+held+ " classes");
		tvProjected.setText(mSubjects.get(position).getProjectedPercentage());
		tvAbsent.setText("Days Absent: "+mSubjects.get(position).getAbsentDates());		

		if (percent<67 && held!=0)
		{
			int x = (2*held) - (3*attend);
			tvReach.setText("Attend "+x+" more classes to reach 67%");
			tvReach.setTextColor(myContext.getResources().getColor(R.color.holo_orange_light));
			tvReach.setVisibility(View.VISIBLE);
			ivAlert.setVisibility(View.VISIBLE);
		}
		else if(percent<75 && held!=0)
		{
			int x = (3*held) - (4*attend);
			tvReach.setText("Attend "+x+" more classes to reach 75%");
			tvReach.setTextColor(myContext.getResources().getColor(R.color.holo_orange_light));
			tvReach.setVisibility(View.VISIBLE);
			ivAlert.setVisibility(View.VISIBLE);
		}
		else
		{
			int x = ((4*attend)/3)-held;
			if(x<=0)
			{
				tvReach.setVisibility(View.GONE);
				ivAlert.setVisibility(View.GONE);
			}
			else if(x==1)
			{
				tvReach.setText("You can safely miss "+x+" class");
				tvReach.setTextColor(myContext.getResources().getColor(R.color.holo_green_light));
				tvReach.setVisibility(View.VISIBLE);
				ivAlert.setVisibility(View.GONE);
			}
			else
			{
				tvReach.setText("You can safely miss "+x+" classes");
				tvReach.setTextColor(myContext.getResources().getColor(R.color.holo_green_light));
				tvReach.setVisibility(View.VISIBLE);
				ivAlert.setVisibility(View.GONE);
			}		
		}
		return view;
	}
}