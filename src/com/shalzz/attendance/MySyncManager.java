package com.shalzz.attendance;

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
	public static final long MILLISECONDS_PER_SECOND = 1000L;
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
		return accounts[0];
	}

	public static void removeSyncAccount(Context mContext) {
		AccountManager accountManager = AccountManager.get(mContext);
		accountManager.removeAccount(getSyncAccount(mContext),null, null);

	}

	public static void setupSync(Context mContext) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		final boolean sync = sharedPref.getBoolean("data_sync", false);
		Log.d(mTag,"Enable sync: "+sync);
		final long SYNC_INTERVAL_IN_MINUTES = Long.parseLong(sharedPref.getString("data_sync_interval", "2"));
		Log.d(mTag,"Sync Interval set to: "+SYNC_INTERVAL_IN_MINUTES);

		Account mAccount = CreateSyncAccount(mContext);		
		if(sync && mAccount!=null) 
		{	// Create the dummy account
			Bundle settingsBundle = new Bundle();
			final long SYNC_INTERVAL =
					SYNC_INTERVAL_IN_MINUTES *
					SECONDS_PER_MINUTE *
					MILLISECONDS_PER_SECOND;

			ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
			ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
			ContentResolver.addPeriodicSync(
					mAccount,
					AUTHORITY,
					settingsBundle,
					SYNC_INTERVAL);
		}
	}
}
