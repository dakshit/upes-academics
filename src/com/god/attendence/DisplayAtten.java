package com.god.attendence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.util.Iterator;

import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.squareup.okhttp.OkHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayAtten extends Activity {

	private OkHttpClient client = new OkHttpClient();
	private String charset = HTTP.ISO_8859_1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attenview);

		// Reference to the layout components
		TextView display = (TextView) findViewById(R.id.tv1);
		
		// Tell the HttpsURLConnection to trust our certificate
				MySSLSocketFactory sslf = null;
				try {
					KeyStore ks = MySSLSocketFactory.getKeystoreOfCA(this.getResources().openRawResource(R.raw.gd_bundle));
					sslf = new MySSLSocketFactory(ks);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					sslf.fixHttpsURLConnection();
				} 

		Log.i(DisplayAtten.class.getName(), "Extracting persisten cookies...");
		SharedPreferences pcookies = getSharedPreferences("PERSISTCOOKIES", 0);	
		CookieManager cookieMan = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		Iterator<String> keyset = pcookies.getAll().keySet().iterator();
		while(keyset.hasNext())
		{
			String cookiename = keyset.next();
			String cookievalue = pcookies.getString(cookiename, "");
			HttpCookie cookie = null;
			try {
				cookie = new HttpCookie(cookiename,cookievalue);
				cookie.setDomain("academics.ddn.upes.ac.in");
				cookie.setPath("/");
				cookie.setVersion(0);
				cookieMan.getCookieStore().add(new URI("https://academics.ddn.upes.ac.in/upes/index.php"), cookie);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		Log.i(DisplayAtten.class.getName(), "Extracted persisten cookies.");
		Log.i(DisplayAtten.class.getName(), cookieMan.getCookieStore().getCookies().toString());
		CookieHandler.setDefault(cookieMan);

		String html1 = "";
		try 
		{
			HttpURLConnection connection = client.open(new URL("https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631"));
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("User-Agent", getString(R.string.UserAgent)); 
			connection.setConnectTimeout(5000);
			InputStream res = connection.getInputStream();		
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(res, charset));
			try {
				String line;
				while ( (line = reader1.readLine()) != null) {
					html1 += line+"\n"; 
				}
			}
			finally {
				try { reader1.close(); } catch (IOException logOrIgnore) {}
			}
			connection.disconnect();
			System.out.println("Status Code: "+connection.getResponseCode());
			System.out.println(connection.getHeaderFields().toString());
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}

		Document doc = Jsoup.parse(html1);
		System.out.println("At Login :\n"+doc.text().toString());
		
		Elements tddata = doc.select("td");
		int position[] = {30,37,44,51,58,65,72,79,86,93,100,107};
		int i=0,k=0;

		if (tddata != null && tddata.size() > 0)
		{
			for(Element element : tddata)
			{
				//				if(i==(position[k]-1))
				//				{
				//					display.append("\n");
				//					++k;
				//				}
				if(i>29)
				{
					if((i-30)%7==0)
					{
						display.append("\n");
					}
					String data= element.text();
					display.append(data+" ");
				}				
				++i;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_logout)
		{
			Log.i(DisplayAtten.class.getName(), "Removing persistent cookies...");
			SharedPreferences pcookies = getSharedPreferences("PERSISTCOOKIES", 0);
			SharedPreferences.Editor editor = pcookies.edit();
			Iterator<String> keyset = pcookies.getAll().keySet().iterator();
			while(keyset.hasNext())
			{
				String cookiename = keyset.next();
				editor.remove(cookiename);
			}
			editor.commit();
			Log.i(DisplayAtten.class.getName(),"Removed persistent cookies.");
			
			Intent intent = new Intent(DisplayAtten.this, Main.class);
			startActivity(intent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
