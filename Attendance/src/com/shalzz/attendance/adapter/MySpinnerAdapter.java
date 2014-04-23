package com.shalzz.attendance.adapter;

import com.shalzz.attendance.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MySpinnerAdapter extends BaseAdapter {
    
    private Context mContext;
    private String[] dropDownList;
     
    public MySpinnerAdapter(Context context){
    	mContext = context;
    	dropDownList = mContext.getResources().getStringArray(R.array.action_list);
    }
 
    @Override
    public int getCount() {
    	return 2;
    }
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        
    	if ( position == 0) {
            convertView = mInflater.inflate(R.layout.spinner_header, null);
        }
    	else  
        {
    		 convertView = mInflater.inflate(R.layout.spinner_item, null);
    		 TextView textview = (TextView) convertView.findViewById(R.id.spinner_item);
    		 textview.setText(dropDownList[position]);
        }
        return convertView;
    }
}
