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

import com.shalzz.attendance.FeedbackUtils;
import com.shalzz.attendance.R;
import com.shalzz.attendance.wrapper.MySyncManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.support.v4.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener, OnSharedPreferenceChangeListener{

	public static final String KEY_PREF_CONTACT = "pref_info_contact";
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        
        addPreferencesFromResource(R.xml.preferences);
        
        String key = "pref_key_proxy_username";
        Preference connectionPref = findPreference(key);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        connectionPref.setSummary(sharedPref.getString(key, ""));

        key = "subjects_expanded_limit";
        ListPreference listPref = (ListPreference) findPreference(key);
        listPref.setSummary(listPref.getEntry());
        
        key = "data_sync_interval";
        ListPreference synclistPref = (ListPreference) findPreference(key);
        synclistPref.setSummary(synclistPref.getEntry());
    }
	
	@Override
	public boolean onPreferenceClick (Preference preference)
	{
    	System.out.println("hmm...");
	    String key = preference.getKey();
	    if (key.equals(KEY_PREF_CONTACT)) {
        	FeedbackUtils.askForFeedback(mContext);
        	System.out.println("hmm...");
    		return true;
        }
		return false;
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_key_proxy_username")) {
            Preference connectionPref = findPreference(key);
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
        else if(key.equals("subjects_expanded_limit")) {
            ListPreference connectionPref = (ListPreference) findPreference(key);
            connectionPref.setSummary(connectionPref.getEntry());
        }
        else if(key.equals("data_sync_interval")) {
            ListPreference connectionPref = (ListPreference) findPreference(key);
            connectionPref.setSummary(connectionPref.getEntry());
    		MySyncManager.addPeriodicSync(mContext);
        }
    }

	@Override
	public void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onResume() {
	    super.onResume();
	    // Set up a listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	    PreferenceCategory prefCategory = (PreferenceCategory) getPreferenceScreen().getPreference(3);
		Preference pref = prefCategory.getPreference(1);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {
            	FeedbackUtils.askForFeedback(mContext);
         		return true;
            }
        });
	}
}