package com.god.attendence;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    Map<String, String> cookies;
    Map<String, String> hiddendata = new HashMap<String, String>();
    Connection.Response res;
    
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
        // SSL.manageHttps();
        
	 // Load CAs from an InputStream
		InputStream caInput=null;
		Certificate ca=null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			caInput = new BufferedInputStream(this.getResources().openRawResource(R.raw.gd_bundle));
			ca = (Certificate) cf.generateCertificate(caInput);
		} catch (CertificateException e1) {
			e1.printStackTrace();
		} finally {
			try {
				caInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	    // Create a KeyStore containing our trusted CAs
	    String keyStoreType = KeyStore.getDefaultType();
	    KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(keyStoreType);
		    keyStore.load(null, null);
		    keyStore.setCertificateEntry("ca", (java.security.cert.Certificate) ca);
		} catch (Exception e) {
			e.printStackTrace();
		}

	    // Create a TrustManager that trusts the CAs in our KeyStore
	    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
	    TrustManagerFactory tmf = null;
		try {
			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		    tmf.init(keyStore);
		} catch (Exception e) {
			e.printStackTrace();
		}


	    // Create an SSLContext that uses our TrustManager
	    SSLContext context = null;
		try {
			context = SSLContext.getInstance("TLS");
		    context.init(null, tmf.getTrustManagers(), null);
		    HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
    
	    // Get the captcha image and set it.
     		String captchaUrl = null;
     		try {
     			 res = Jsoup.connect("https://academics.ddn.upes.ac.in/upes/")
     											.userAgent(getString(R.string.UserAgent))
     											.referrer("http://stu.upes.ac.in/")
     											.method(Connection.Method.GET)
     											.execute();
     			
     			//Get the cookies
				cookies = res.cookies();
				System.out.println(cookies);
				System.out.println("phpsess:"+res.cookie("PHPSESSID"));
				
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
                    	hiddendata.put(name.trim(), val.trim());
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
     			Bitmap icon = null;
     			try {

     				URL newurl = new URL(captchaUrl);
     				HttpsURLConnection urlConnection = (HttpsURLConnection) newurl.openConnection();
     				InputStream in = urlConnection.getInputStream();
     				icon = BitmapFactory.decodeStream(in);
     			} 
     			catch (IOException e)
     			{
     				e.printStackTrace();
     			} 
     			finally 
     			{
     				capImg.setImageBitmap(icon);
     			}
     		}
     		
		// OnClickListener event for the Login Button
	    login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Validate;
				AsyncExecution task = new AsyncExecution();
				task.execute();
			}
		});
	}

	private class AsyncExecution extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO try HttpClient or URLConnection ;lookup working of captcha;get hidden values; find jscall
			try 
			{				
//				Connection.Response res = Jsoup.connect("https://academics.ddn.upes.ac.in/upes/")
//						.userAgent(getString(R.string.UserAgent))
//						.referrer("http://stu.upes.ac.in/")
//						.data(data)
//						.method(Method.POST)
//						.execute();
//
//				//Get the cookies
//				cookies = res.cookies();
//				System.out.println(cookies);
//				System.out.println("phpsess:"+res.cookie("PHPSESSID"));
				
				Connection.Response res1 = Jsoup.connect("https://academics.ddn.upes.ac.in/upes/index.php")
												.userAgent(getString(R.string.UserAgent))
												.data("username",sapid.getText().toString())
												.data("passwd",pass.getText().toString())
												.data("txtCaptcha",captcha.getText().toString())
												.data(hiddendata)
												.cookies(res.cookies())
												.header("Content-Type","text/html;charset=UTF-8")
												.method(Method.POST)
												.execute();
				
				// Get the cookies created after login
			    cookies = res1.cookies();
				System.out.println(res1.cookies());
				System.out.println("phpsess:"+res1.cookie("PHPSESSID"));
				// System.out.println(res1.parse().text().toString());
				Map<String, String> header = res1.headers();
				System.out.println(header);
				

				
				if(cookies!=null)
				{
				Document doc = Jsoup.connect("https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631")
						       .userAgent(getString(R.string.UserAgent))
						       .cookies(cookies)
					           .get();
				
			    System.out.println(doc.text().toString());
				}
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
