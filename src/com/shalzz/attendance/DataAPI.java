package com.shalzz.attendance;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.Request.Method;
import com.shalzz.attendance.wrapper.DateHelper;
import com.shalzz.attendance.wrapper.MyStringRequest;
import com.shalzz.attendance.wrapper.MyVolley;

public class DataAPI {

	public static void getAttendance(final Context mContext,Response.Listener<String> successListener, Response.ErrorListener errorListener) {

		String mURL = "https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631";
		MyStringRequest requestAttendance = new MyStringRequest(Method.POST,
				mURL,
				successListener,
				errorListener) {

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("User-Agent", mContext.getString(R.string.UserAgent));
				return headers;
			};
		};
		requestAttendance.setShouldCache(true);
		requestAttendance.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		MyVolley.getInstance().addToRequestQueue(requestAttendance ,mContext.getClass().getName());
	}

	public static void getTimeTable(final Context mContext,Response.Listener<String> successListener, Response.ErrorListener errorListener) {

		String mURL = "https://academics.ddn.upes.ac.in/upes/index.php?option=com_course_report&Itemid=7794";
		MyStringRequest requestTimeTable = new MyStringRequest(Method.POST,
				mURL,
				successListener,
				errorListener) {

			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				String date = DateHelper.getNetworkRequestDate(DateHelper.getToDay());
				params.put("fromdate", date);
				params.put("submit","Show Result");
				return params;
			};

			public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("User-Agent", mContext.getString(R.string.UserAgent));
				return headers;
			};
		};
		requestTimeTable.setShouldCache(true);
		requestTimeTable.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		MyVolley.getInstance().addToRequestQueue(requestTimeTable ,mContext.getClass().getName());
	}
}
