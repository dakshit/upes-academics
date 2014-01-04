package com.god.attendence;

import java.security.KeyStore;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.OkHttpStack;
import com.android.volley.toolbox.Volley;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;

public class MyVolley extends Application {

	/**
	 * Log or request TAG
	 */
	public static final String TAG = "VolleyPatterns";

	/**
	 * Global request queue for Volley
	 */
	private RequestQueue mRequestQueue;
	
	/**
	 * Global Image Loader for Volley
	 */
	private ImageLoader mImageLoader;
	
	/**
     * A singleton instance of the application class for easy access in other places
     */
    private static MyVolley sInstance;

	@Override
	public void onCreate() {
		super.onCreate();

		// initialize the singleton
		sInstance = this;
		
		// fix the SSLSocket
		fixSSLSocket();
	}
	
	/**
     * @return ApplicationController singleton instance
     */
	 public static synchronized MyVolley getInstance() {
	        return sInstance;
	    }

	/**
	 * @return The Volley Request queue.
	 */
	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
		if(mRequestQueue == null)
			mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack());
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

		VolleyLog.d("Adding request to queue: %s", req.getUrl());

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
