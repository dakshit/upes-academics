package com.god.attendence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	private Button refreshCaptcha;
	private MySSLSocketFactory sslf = null;
    private String charset = HTTP.ISO_8859_1;
    private String query = "submit=Login&";
	private CookieManager cookieMan = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.activity_main);
		
		// Reference to the layout components
		sapid = (EditText) findViewById(R.id.etSapid);
		pass = (EditText) findViewById(R.id.etPass);
	    captcha =  (EditText) findViewById(R.id.etCap);
	    capImg = (ImageView) findViewById(R.id.imageView1);
	    login = (Button) findViewById(R.id.bLogin);
	    refreshCaptcha = (Button) findViewById(R.id.bCaptcha);
	    
	    // Tell the HttpsURLConnection to trust our certificate
        try 
        {
        	KeyStore ks = MySSLSocketFactory.getKeystoreOfCA(this.getResources().openRawResource(R.raw.gd_bundle));
			sslf = new MySSLSocketFactory(ks);
		}
        catch (Exception e)
        {
			e.printStackTrace();
		}
        finally
		{
			sslf.fixHttpsURLConnection();
		} 
		
	    // Get the hidden data and the captcha image
		new getHiddenData().execute();
		
		refreshCaptcha.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new getCaptchaImg().execute();
			}
		});
     		
		// OnClickListener event for the Login Button
	    login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Validate; basic http authentication
				//new LoginExecution().execute();

				HttpURLConnection connection = null;
				try
				{
					query += "username="+URLEncoder.encode(sapid.getText().toString(), charset)+"&passwd="+URLEncoder.encode(pass.getText().toString(), charset)+"&txtCaptcha="+URLEncoder.encode(captcha.getText().toString(), charset);
					System.out.println(query);
					
					 Authenticator.setDefault(new Authenticator() {
						 protected PasswordAuthentication getPasswordAuthentication() {
							 return new PasswordAuthentication(sapid.getText().toString(), pass.getText().toString().toCharArray());						 
						 }
					 });
					 
					connection = (HttpURLConnection) new URL("https://academics.ddn.upes.ac.in/upes/index.php").openConnection();
					connection.setDoOutput(true); // Triggers POST.
					connection.setRequestProperty("Accept-Charset", charset);
					connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
					connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
					connection.setRequestProperty("User-Agent", getString(R.string.UserAgent)); 
					
					OutputStream output = connection.getOutputStream();
					try {
					     output.write(query.getBytes(charset));
					} finally {
					     try { output.close(); } catch (IOException logOrIgnore) {}
					}

					InputStream response = connection.getInputStream();
					String html = "";
					BufferedReader reader = new BufferedReader(new InputStreamReader(response, charset));
					try {
						for (String line; (line = reader.readLine()) != null;) {
							html += line+"\n"; 
						}
					} finally {
						try { reader.close(); } catch (IOException logOrIgnore) {}
					}
					connection.disconnect();
					Document document = Jsoup.parse(html);
					System.out.println("At Login :\n"+document.text().toString());
					System.out.println("Status Code: "+connection.getResponseCode());
					System.out.println(cookieMan.getCookieStore().getCookies().toString());

					if(document.data().toString().equals("alert('Please enter correct code.'); window.history.go(-1);"))
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
						builder.setMessage("Incorrect Captcha!\nPlease try again.");
						builder.setCancelable(true);
						builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});

						AlertDialog alert = builder.create();
						alert.show();
					}
					else
					{
						connection = (HttpURLConnection) new URL("https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631").openConnection();
						connection.setRequestProperty("Accept-Charset", charset);
						connection.setRequestProperty("User-Agent", getString(R.string.UserAgent)); 
						InputStream res = connection.getInputStream();
						String html1 = "";
						BufferedReader reader1 = new BufferedReader(new InputStreamReader(res, charset));
						try {
							for (String line; (line = reader1.readLine()) != null;) {
								html1 += line+"\n"; 
							}
						} finally {
							try { reader1.close(); } catch (IOException logOrIgnore) {}
						}
						connection.disconnect();
						// Document doc = Jsoup.parse(html1);
						// System.out.println("After Login :\n"+doc.text().toString());
						
						Intent ourIntent = new Intent(Main.this, DisplayAtten.class);
						ourIntent.putExtra("com.god.attendence.ATTPAGE", html1);
						startActivity(ourIntent);
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	private class getHiddenData extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... arg0) 
		{
			HttpURLConnection connection = null;
			CookieHandler.setDefault(cookieMan);
			
			try 
			{	
				connection = (HttpURLConnection) new URL("https://academics.ddn.upes.ac.in/upes/").openConnection();
				connection.setRequestProperty("Accept-Charset", charset);
				connection.setRequestProperty("User-Agent", getString(R.string.UserAgent)); 
				connection.getContent();
				connection.disconnect();
				
				connection = (HttpURLConnection) new URL("https://academics.ddn.upes.ac.in/upes/").openConnection();
				connection.setRequestProperty("Accept-Charset", charset);
				connection.setRequestProperty("User-Agent", getString(R.string.UserAgent)); 
				InputStream response = connection.getInputStream();
				String html = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(response, charset));
			    try {
			        for (String line; (line = reader.readLine()) != null;) {
			            html += line+"\n"; 
			        }
			    } finally {
			        try { reader.close(); } catch (IOException logOrIgnore) {}
			    }
				Document doc = Jsoup.parse(html);
				System.out.println("Status Code: "+connection.getResponseCode());
				System.out.println(doc.text().toString());
				System.out.println(cookieMan.getCookieStore().getCookies().toString());
				System.out.println(connection.getHeaderFields().toString());
				
				// Get Hidden values
				Elements hiddenvalues = doc.select("input[type=hidden]");
				for(Element hiddenvalue : hiddenvalues)
				{
					String name = hiddenvalue.attr("name");
					String val = hiddenvalue.attr("value");
					if(name.length()!=0 && val.length()!=0)
					{
						query += name+"="+URLEncoder.encode(val, charset)+"&";
					}
				}	
				System.out.println(query);
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			} 
			finally {
				connection.disconnect();
			   }
			return null;
		}
		
		protected void onPostExecute(Void v) {
			new getCaptchaImg().execute();
		}
	}
	
	protected class getCaptchaImg extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			
			String captchaUrl = null;
			HttpURLConnection connection = null;
			// Get the captcha image and set it.
			try {		
				connection = (HttpURLConnection) new URL("https://academics.ddn.upes.ac.in/upes/").openConnection();
				connection.setDoOutput(true); // Triggers POST.
				connection.setRequestProperty("Accept-Charset", charset);
				connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
				connection.setRequestProperty("User-Agent", getString(R.string.UserAgent)); 
				
				InputStream response = connection.getInputStream();
				String html = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(response, charset));
			    try 
			    {
			        for (String line; (line = reader.readLine()) != null;)
			        {
			            html += line+"\n"; 
			        }
			    } 
			    finally 
			    {
			        try { reader.close(); } catch (IOException logOrIgnore) {}
			    }
				Document doc = Jsoup.parse(html);
				System.out.println("Status Code: "+connection.getResponseCode());
				System.out.println(doc.text().toString());
				System.out.println(cookieMan.getCookieStore().getCookies().toString());
				
				// Get Img URL
				Elements elements = doc.select("img#imgCaptcha");
				captchaUrl = elements.attr("src");
				System.out.println(captchaUrl);					
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			finally
			{
				connection.disconnect();
			}
			return captchaUrl;
		}	
		
		protected void onPostExecute(String URL) {
			// get the img and set it
			new getImg().execute(URL);	
		}
		
	}		
	
	private class getImg extends AsyncTask<String, Void, Bitmap>{
			@Override
			protected Bitmap doInBackground(String... Url) 
			{
				Bitmap icon = null;
				try 
				{
					URL newurl = new URL(Url[0]);
					HttpsURLConnection urlConnection = (HttpsURLConnection) newurl.openConnection();
					InputStream in = urlConnection.getInputStream();
					icon = BitmapFactory.decodeStream(in);
					in.reset() ;
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				} 
				return icon;
			}
			
			protected void onPostExecute(Bitmap icon) {
				// icon.setDensity(120);
				capImg.setImageBitmap(icon);
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
