/*
 *    UPES Academics, android attendance application for University of Petroleum and Energy Studies
 *    Copyright (C) 2014  Shaleen Jain
 *    shaleen.jain95@gmail.com
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.Proxy;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.OkHttpStack;
import com.shalzz.attendance.Miscellaneous;
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
		if(Miscellaneous.useProxy())
		{
			Log.i("MyOkHttpStack","Using Proxy!");
			Toast.makeText(MyVolley.getAppContext(), "Using Proxy!", Toast.LENGTH_LONG).show();
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.ddn.upes.ac.in", 8080));
			client.setProxy(proxy);
		}
		else if(client.getProxy()!=null)
		{
			Toast.makeText(MyVolley.getAppContext(), "Proxy removed!", Toast.LENGTH_LONG).show();
			Log.i("MyOkHttpStack","Proxy removed!");
			client.setProxy(null);
		}
		return client.open(url);			
	} 
}
