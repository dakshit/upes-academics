package com.god.attendence;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayAtten extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.attenview);
		
		// Reference to the layout components
		TextView display = (TextView) findViewById(R.id.tv1);
		// Tell the HttpsURLConnection to trust our certificate
        //SSL.manageHttps();
		
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
               
        Document doc = null;
		try {
			Response s = Jsoup.connect("https://academics.ddn.upes.ac.in/upes/index.php?option=com_stuattendance&task='view'&Itemid=7631").execute();
			doc = s.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements tddata = doc.select("td");
		int position[] = {30,34};
		
		if (tddata != null && tddata.size() > 0)
		{
			for(Element element : tddata)
			{
				String data= element.text();
				display.append(data+" ");
			}
		}
	}

}
