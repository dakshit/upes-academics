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

package com.shalzz.attendance.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/*
 * Implement AbstractAccountAuthenticator and stub out all
 * of its methods
 */
public class Authenticator extends AbstractAccountAuthenticator {
	// Simple constructor
	public Authenticator(Context context) {
		super(context);
	}
	// Editing properties is not supported
	@Override
	public Bundle editProperties(
			AccountAuthenticatorResponse r, String s) {
		throw new UnsupportedOperationException();
	}
	// Don't add additional accounts
	@Override
	public Bundle addAccount(
			AccountAuthenticatorResponse r,
			String s,
			String s2,
			String[] strings,
			Bundle bundle) throws NetworkErrorException {
		return null;
	}
	// Ignore attempts to confirm credentials
	@Override
	public Bundle confirmCredentials(
			AccountAuthenticatorResponse r,
			Account account,
			Bundle bundle) throws NetworkErrorException {
		return null;
	}
	// Getting an authentication token is not supported
	@Override
	public Bundle getAuthToken(
			AccountAuthenticatorResponse r,
			Account account,
			String s,
			Bundle bundle) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}
	// Getting a label for the auth token is not supported
	@Override
	public String getAuthTokenLabel(String s) {
		throw new UnsupportedOperationException();
	}
	// Updating user credentials is not supported
	@Override
	public Bundle updateCredentials(
			AccountAuthenticatorResponse r,
			Account account,
			String s, Bundle bundle) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}
	// Checking features for the account is not supported
	@Override
	public Bundle hasFeatures(
			AccountAuthenticatorResponse r,
			Account account, String[] strings) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}
}
