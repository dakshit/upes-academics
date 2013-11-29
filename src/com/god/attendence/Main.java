package com.god.attendence;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Main extends Activity {

	private EditText sapid;
	private EditText pass;
	private EditText captcha;
	private ImageView capImg;
	private Button login;
	private Map<String, String> cookies;
    private Map<String, String> hiddendata = new HashMap<String, String>();	 
    List<NameValuePair> data = new ArrayList<NameValuePair>();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		
		// Reference to the layout components
		sapid = (EditText) findViewById(R.id.etSapid);
		pass = (EditText) findViewById(R.id.etPass);
	    captcha = (EditText) findViewById(R.id.etCap);
	    capImg = (ImageView) findViewById(R.id.imageView1);
	    login = (Button) findViewById(R.id.bLogin);
	    
	    // Tell the HttpsURLConnection to trust our certificate
	    MySSLSocketFactory sslf = null;
        try {
        	KeyStore ks = MySSLSocketFactory.getKeystoreOfCA(Main.this.getResources().openRawResource(R.raw.gd_bundle));
			sslf = new MySSLSocketFactory(ks);
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			sslf.fixHttpsURLConnection();
		} 
		
	    // Get the captcha image and set it.
		CaptchExecution task = new CaptchExecution();
		Bitmap icon = task.doInBackground();
		capImg.setImageBitmap(icon);
     		
		// OnClickListener event for the Login Button
	    login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Validate;
			   new LoginExecution().execute();
			}
		});
	}
	
	private class CaptchExecution extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			 // Get the captcha image and set it.
			String captchaUrl = null;
			Bitmap icon = null;
			try {
				Response res = Jsoup.connect("https://academics.ddn.upes.ac.in/upes/")
						.userAgent(getString(R.string.UserAgent))
						.referrer("http://stu.upes.ac.in/")
						.method(Connection.Method.GET)
						.execute();

				// Get Img URL
				Document doc = res.parse();
				Elements elements = doc.select("img#imgCaptcha");
				captchaUrl=elements.attr("src");
				System.out.println(captchaUrl);			

				// Get Hidden values
				Elements hiddenvalues = doc.select("input[type=hidden]");
				for(Element hiddenvalue : hiddenvalues)
				{
					String name = hiddenvalue.attr("name");
					String val = hiddenvalue.attr("value");
					if(name.length()!=0 && val.length()!=0)
					{
						//hiddendata.put(name.trim(), val.trim());
						data.add(new BasicNameValuePair(name.trim(), val.trim()));
					}
				}

				System.out.println(hiddendata.toString());
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}	 
			finally 
			{				
				try 
				{

					URL newurl = new URL(captchaUrl);
					HttpsURLConnection urlConnection = (HttpsURLConnection) newurl.openConnection();
					InputStream in = urlConnection.getInputStream();
					icon = BitmapFactory.decodeStream(in);
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				} 
			}
			return icon;
		}		
	}

	private class LoginExecution extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO try HttpClient or URLConnection ;lookup working of captcha;find jscall
			try 
			{			
//				// Using Jsoup
//				Connection.Response res1 = Jsoup.connect("https://academics.ddn.upes.ac.in/upes/index.php")
//												.userAgent(getString(R.string.UserAgent))
//												.data("username",sapid.getText().toString())
//												.data("passwd",pass.getText().toString())
//												.data("txtCaptcha",captcha.getText().toString())
//												.data(hiddendata)
//												.cookies(cookies)
//												.header("Content-Type","text/html;charset=UTF-8")
//												.method(Method.POST)
//												.execute();
//				
//				// Get the cookies created after login
//			    cookies = res1.cookies();
//				System.out.println(cookies);
//
//				
//
//				
//				if(cookies!=null)
//				{
//				Document doc = Jsoup.connect("https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631")
//						       .userAgent(getString(R.string.UserAgent))
//						       .cookies(cookies)
//					           .get();
//				
//			    System.out.println(doc.text().toString());
//				}
				
				// Using aphache HTTP client
				KeyStore ks = MySSLSocketFactory.getKeystoreOfCA(Main.this.getResources().openRawResource(R.raw.gd_bundle));
				DefaultHttpClient client =  MySSLSocketFactory.getNewHttpClient(ks);
				HttpContext localContext = new BasicHttpContext();
				CookieStore cookieStore = new BasicCookieStore();
				localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
				HttpPost request = new HttpPost("https://academics.ddn.upes.ac.in/upes/index.php");
				client.execute(request,localContext);
				System.out.println(cookieStore.toString());
				
				data.add(new BasicNameValuePair("username", sapid.getText().toString()));
				data.add(new BasicNameValuePair("passwd",pass.getText().toString()));
				data.add(new BasicNameValuePair("txtCaptcha",captcha.getText().toString()));
				HttpPost request1 = new HttpPost("https://academics.ddn.upes.ac.in/upes/index.php");
				request1.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));
				HttpResponse r = client.execute(request1,localContext);
				if (r.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = r.getEntity();
					Log.d("login", "success!");
					if (entity != null) {
						System.out.println(cookieStore.toString());
					}
				}
				
				HttpPost request2 = new HttpPost("https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631");
				HttpResponse res = client.execute(request2,localContext);
			    String html = EntityUtils.toString(res.getEntity());
			    Document document = Jsoup.parse(html);
			    System.out.println(document.toString());
			} 
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
			finally
			{
				// startActivity(new Intent("com.god.attendence.DISPLAYATTEN"));
			}
			return null;
		}
	
	}
	
	@Override
	protected void onPause() {
		// TODO Save the user account;
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
