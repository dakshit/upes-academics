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

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyStore;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.shalzz.attendance.R;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

/**
 * Wrapper class for Volley which provides a singleton instance.
 * @author shalzz
 *
 */
public class MyVolley extends Application {

	/**
	 * Log or request TAG
	 */
	public static final String TAG = "VOLLEY";

	/**
	 * Global request queue for Volley
	 */
	private RequestQueue mRequestQueue;
	
	/**
	 * Global Image Loader for Volley
	 */
	private ImageLoader mImageLoader;
	
	/**
	 * Application Context.
	 */
	private static  Context mContext;
	
	/**
     * A singleton instance of the application class for easy access in other places
     */
    private static MyVolley sInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		MyVolley.mContext = getApplicationContext();
		
		// Initialize the singleton
		sInstance = this;
		
		// Fix the SSLSocket
		fixSSLSocket();
		
		// Set a cookie manager
		Log.i(MyVolley.class.getName(), "Setting CookieHandler");
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		MyPreferencesManager settings = new MyPreferencesManager(mContext);
		settings.getPersistentCookies();
	}
	
	/**
     * @return ApplicationController singleton instance
     */
	 public static synchronized MyVolley getInstance() {
	        return sInstance;
	    }
	 
	 public static Context getAppContext() {
	        return MyVolley.mContext;
	    }

	/**
	 * @return The Volley Request queue.
	 */
	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
		if(mRequestQueue == null )
			mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new MyOkHttpStack());
		return mRequestQueue;
	}
	
	/**
     * Returns instance of ImageLoader initialized with {@link FakeImageCache} which effectively means
     * that no memory caching is used. This is useful for images that you know that will be show
     * only once.
     * 
     * @return
     */
    public ImageLoader getImageLoader() {
    	// lazy initialize the image loader
        if (mImageLoader == null) 
             mImageLoader = new ImageLoader(getRequestQueue(), new FakeImageCache());
        return mImageLoader;
    }

	/**
	 * Adds the specified request to the global queue, if tag is specified
	 * then it is used else Default TAG is used.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

		VolleyLog.d("Adding request to queue: %s with tag: %s", req.getUrl(),tag);

		getRequestQueue().add(req);
	}

	/**
	 * Adds the specified request to the global queue using the Default TAG.
	 * 
	 * @param req
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		// set the default tag
		req.setTag(TAG);
		
		VolleyLog.d("Adding request to queue: %s with tag: %s", req.getUrl(),TAG);
		
		// add to the requestQueue
		getRequestQueue().add(req);
	}

	/**
	 * Cancels all pending requests registered by the default {@link MyVolley#TAG}.
	 */
	public void cancelPendingRequests() {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(TAG);
		}
		Log.d(MyVolley.class.getName(),"Cancelling requests with tag: "+TAG);
	}

	/**
	 * Cancels all pending requests by the specified TAG, it is important
	 * to specify a TAG so that the pending/ongoing requests can be cancelled.
	 * 
	 * @param tag
	 */
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
		VolleyLog.d("Cancelling requests with tag: "+tag);
	}
	
	/**
	 * Fixes the SSLHanshakeException
	 */
	private void fixSSLSocket() {

		// Tell the HttpsURLConnection to trust our certificate
		MySSLSocketFactory sslf = null;
		try {
			KeyStore ks = MySSLSocketFactory.getKeystoreOfCA(getApplicationContext().getResources().openRawResource(R.raw.gd_bundle));
			sslf = new MySSLSocketFactory(ks);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			sslf.fixHttpsURLConnection();
		}  	
	}
	
	/**
	 * Fake cache, i.e. no caching is done. 
	 * This class exist just to implement ImageLoader.ImageCache and be used 
	 * when no memory cache is needed
	 * @author Ognyan Bankov
	 *
	 */
    public class FakeImageCache implements ImageCache {

		@Override
		public Bitmap getBitmap(String url) {
			return null;
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
		}

	}
}
