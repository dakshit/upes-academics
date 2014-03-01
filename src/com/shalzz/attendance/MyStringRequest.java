package com.shalzz.attendance;

import java.io.UnsupportedEncodingException;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

public class MyStringRequest extends StringRequest {

	private Priority mPriority = Priority.NORMAL;
	/**
	 * Creates a new request with the given method.
	 *
	 * @param method the request {@link Method} to use
	 * @param url URL to fetch the string at
	 * @param listener Listener to receive the String response
	 * @param errorListener Error listener, or null to ignore errors
	 */
	public MyStringRequest(int method, String url, Listener<String> listener,
			ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	@Override
	public Priority getPriority() {
		return mPriority;
	}
	
	public void setPriority(Priority priority)
	{
		mPriority = priority;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, ignoreCacheHeaders(response));
	}

	private Cache.Entry ignoreCacheHeaders(NetworkResponse response) {
		Cache.Entry entry = new Cache.Entry();
		entry.data = response.data;
		entry.responseHeaders = response.headers;
		return entry;
	}
}
