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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.shalzz.attendance.R;
import android.content.Context;

public class MyVolleyErrorHelper {
	
	/**
	 * Returns appropriate message which is to be displayed to the user 
	 * against the specified error object.
	 * 
	 * @param error
	 * @param context
	 * @return Error Message
	 */
	public static String getMessage(Object error, Context context) {
		if (error instanceof TimeoutError) {
			return context.getResources().getString(R.string.generic_server_down);
		}
		else if (isServerProblem(error)) {
			return handleServerError(error, context);
		}
		else if (isNetworkProblem(error)) {
			return context.getResources().getString(R.string.no_internet);
		}
		return context.getResources().getString(R.string.generic_error);
	}

	/**
	 * Determines whether the error is related to network
	 * @param error
	 * @return
	 */
	private static boolean isNetworkProblem(Object error) {
		return (error instanceof NetworkError) || (error instanceof NoConnectionError);
	}
	
	/**
	 * Determines whether the error is related to server
	 * @param error
	 * @return
	 */
	private static boolean isServerProblem(Object error) {
		return (error instanceof ServerError) || (error instanceof AuthFailureError);
	}
	
	/**
	 * Handles the server error, tries to determine whether to show a stock message or to 
	 * show a message retrieved from the server.
	 * 
	 * @param err
	 * @param context
	 * @return
	 */
	private static String handleServerError(Object err, Context context) {
		VolleyError error = (VolleyError) err;

		NetworkResponse response = error.networkResponse;

		if (response != null) {
			switch (response.statusCode) {
			case 404:
			case 422:
			case 401:
				return error.getMessage();
			case 407:
				return context.getResources().getString(R.string.proxy_error);

			default:
				return context.getResources().getString(R.string.generic_server_down);
			}
		}
		return context.getResources().getString(R.string.generic_error);
	}
}
