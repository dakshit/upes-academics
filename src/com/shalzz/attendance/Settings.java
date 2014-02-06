package com.shalzz.attendance;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.support.v4.app.NavUtils;

@SuppressWarnings("deprecation")
public class Settings extends SherlockPreferenceActivity implements OnPreferenceClickListener{

	public static final String KEY_PREF_CONTACT = "pref_info_contact";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.pref);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
	
	@Override
	public boolean onPreferenceClick (Preference preference)
	{

    	System.out.println("hmm...");
	    String key = preference.getKey();
	    if (key.equals(KEY_PREF_CONTACT)) {
        	FeedbackUtils.askForFeedback(this);
        	System.out.println("hmm...");
    		return true;
        }
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   switch (item.getItemId()) {
	      case android.R.id.home:
	         NavUtils.navigateUpTo(this,
	               new Intent(this, Attendance.class));
	         return true;
	   }
	   return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    PreferenceCategory prefCategory = (PreferenceCategory) getPreferenceScreen().getPreference(2);
		Preference pref = prefCategory.getPreference(1);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {
            	FeedbackUtils.askForFeedback(Settings.this);
         		return true;
            }
        });
	}
}