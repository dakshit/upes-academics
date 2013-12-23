package com.god.attendence;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	private Button refreshCaptcha;
	private List<NameValuePair> data = new ArrayList<NameValuePair>();
	private HashMap<String, String> cookies = new HashMap<String, String>();
    private MySSLSocketFactory sslf = null;
    private DefaultHttpClient client  = null ;
    private HttpContext localContext = new BasicHttpContext();
    
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
				// TODO Validate;
			    //new LoginExecution().execute();
				
				// Using aphache HTTP client
				try {
					CookieStore cookieStore = new BasicCookieStore();
					cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
					
					data.add(new BasicNameValuePair("username", sapid.getText().toString()));
					data.add(new BasicNameValuePair("passwd", pass.getText().toString()));
					data.add(new BasicNameValuePair("txtCaptcha", captcha.getText().toString()));
					HttpPost request1 = new HttpPost("https://academics.ddn.upes.ac.in/upes/index.php");
					request1.setEntity(new UrlEncodedFormEntity(data,HTTP.ISO_8859_1));
					HttpResponse r = client.execute(request1, localContext);
					if (r.getStatusLine().getStatusCode() == 200) {
						Log.d("login", "success!");
						System.out.println(cookieStore.toString());
						String html = EntityUtils.toString(r.getEntity());
						Document document = Jsoup.parse(html);
						System.out.println("At Login :\n"+document.text().toString());

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
							List<Cookie> ck = cookieStore.getCookies();
							cookies.put(ck.get(0).getName(), ck.get(0).getValue());
							cookies.put(ck.get(1).getName(), ck.get(1).getValue());

							Document doc = Jsoup
									.connect("https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631")
									.userAgent(getString(R.string.UserAgent))
									.cookies(cookies).get();

							System.out.println("the f?: "+doc.text().toString());

							HttpGet request2 = new HttpGet("https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631");
							request2.addHeader("Location","upes/index.php");
							HttpResponse res = client.execute(request2,localContext);
							res.addHeader("Location","upes/index.php");
							System.out.println(res.getAllHeaders().toString());
							if (res.getStatusLine().getStatusCode() == 200) 
							{
								String html1 = EntityUtils.toString(res.getEntity());
								Document document1 = Jsoup.parse(html1);
								System.out.println("Doc :"+document1.text().toString());
							}
							else
							{
								System.out.println("Response Status code:"+res.getStatusLine().getStatusCode());
								System.out.println(res.toString());
							}
						}
					}
					else
					{
						Log.d("login", "fail!");
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
			try 
			{
				KeyStore ks = MySSLSocketFactory.getKeystoreOfCA(Main.this.getResources().openRawResource(R.raw.gd_bundle));
				client  = MySSLSocketFactory.getNewHttpClient(ks);
				CookieStore cookieStore = new BasicCookieStore();
				localContext.setAttribute(ClientContext.COOKIE_STORE,cookieStore);
				HttpGet request = new HttpGet("https://academics.ddn.upes.ac.in/upes/index.php");
				HttpResponse res = client.execute(request, localContext);
				System.out.println(cookieStore.toString());
				String html = EntityUtils.toString(res.getEntity());
				Document doc = Jsoup.parse(html);
				
				// Get Hidden values
				Elements hiddenvalues = doc.select("input[type=hidden]");
				for(Element hiddenvalue : hiddenvalues)
				{
					String name = hiddenvalue.attr("name");
					String val = hiddenvalue.attr("value");
					if(name.length()!=0 && val.length()!=0)
					{
						//data.put(name.trim(), val.trim());
						data.add(new BasicNameValuePair(name.trim(), val.trim()));
					}
				}	
			} 
			catch (IOException e)
			{
				e.printStackTrace();
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
			// Get the captcha image and set it.
			try {		
				CookieStore cookieStore = new BasicCookieStore();
				cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
				
				List<NameValuePair> empdata = new ArrayList<NameValuePair>();
				empdata.add(new BasicNameValuePair("username", " "));
				empdata.add(new BasicNameValuePair("passwd", " "));
				empdata.add(new BasicNameValuePair("txtCaptcha", " "));
			
				HttpPost request = new HttpPost("https://academics.ddn.upes.ac.in/upes/index.php");
				request.setEntity(new UrlEncodedFormEntity(empdata,HTTP.ISO_8859_1));
				HttpResponse res = client.execute(request, localContext);
				System.out.println(cookieStore.toString());
				String html = EntityUtils.toString(res.getEntity());
				Document doc = Jsoup.parse(html);
				
				// Get Img URL
				Elements elements = doc.select("img#imgCaptcha");
				captchaUrl = elements.attr("src");
				System.out.println(captchaUrl);					
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
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
