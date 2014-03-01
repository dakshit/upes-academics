package com.shalzz.attendance;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.Proxy;

import android.util.Log;
import com.android.volley.toolbox.OkHttpStack;
import com.squareup.okhttp.OkHttpClient;

public class MyOkHttpStack extends OkHttpStack{
	private final OkHttpClient client;

	public MyOkHttpStack() {
	    this(new OkHttpClient());
	  }

	  public MyOkHttpStack(OkHttpClient client) {
	    if (client == null) {
	      throw new NullPointerException("Client must not be null.");
	    }
	    this.client = client;
	  }
	  
	@Override 
	protected HttpURLConnection createConnection(URL url) throws IOException {
		if(Miscellanius.useProxy())
		{
			Log.i("MyOkHttpStack","Using Proxy!");
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.ddn.upes.ac.in", 8080));
			client.setProxy(proxy);
		}
		else if(client.getProxy()!=null)
		{
			Log.i("MyOkHttpStack","Proxy removed!");
			client.setProxy(null);
		}
		return client.open(url);			
	} 
}
