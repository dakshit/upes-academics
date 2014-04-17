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

package com.shalzz.attendance.wrapper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class MySyncManager {

	// Sync interval constants
	public static final long SECONDS_PER_MINUTE = 60L;
	public static final String AUTHORITY = "com.shalzz.attendance.provider";
	public static final String ACCOUNT_TYPE = "com.shalzz";
	public static String ACCOUNT = "Default Account";
	private static String mTag = "Sync Manager";
	
	
	/**
	 * Create a new dummy account for the sync adapter
	 *
	 * @param context The application context
	 */
	public static Account CreateSyncAccount(Context context) {
		// Create the account type and default account
		Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager =
				(AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data
		 * If successful, return the Account object, otherwise report an error.
		 */
		if (!accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * The account exists or some other error occurred. Log this, report it,
			 * or handle it internally.
			 */
		}
		return newAccount;
	}
	
	public static Account getSyncAccount(Context mContext) {
		AccountManager accountManager = AccountManager.get(mContext);
		Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
		if(accounts.length==0)
			return null;
		return accounts[0];
	}

	public static void removeSyncAccount(Context mContext) {
		AccountManager accountManager = AccountManager.get(mContext);
		accountManager.removeAccount(getSyncAccount(mContext),null, null);

	}	
	
	public static void addPeriodicSync(Context mContext) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		final boolean sync = sharedPref.getBoolean("data_sync", false);
		Log.d(mTag,"Enable sync: "+sync);
		final long SYNC_INTERVAL_IN_MINUTES = Long.parseLong(sharedPref.getString("data_sync_interval", "2"));
		Log.d(mTag,"Sync Interval set to: "+SYNC_INTERVAL_IN_MINUTES);
		
		Account mAccount = getSyncAccount(mContext);
		
		if(mAccount==null)
			mAccount = CreateSyncAccount(mContext);	
		
		if(sync) 
		{	// Create the dummy account
			Bundle settingsBundle = new Bundle();
			final long SYNC_INTERVAL =
					SYNC_INTERVAL_IN_MINUTES *
					SECONDS_PER_MINUTE;

			ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
			ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
			ContentResolver.addPeriodicSync(
					mAccount,
					AUTHORITY,
					settingsBundle,
					SYNC_INTERVAL);
		}
	}
}
