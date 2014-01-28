package com.shalzz.attendance;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.shalzz.attendance.R;

public class Settings extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
	}

}
