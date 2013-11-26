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

import android.view.Menu;

public class SSL {

	protected static void manageHttps() {
		
//		// Load CAs from an InputStream
//		InputStream caInput=null;
//		Certificate ca=null;
//		Main main= new Main();
//		try {
//			CertificateFactory cf = CertificateFactory.getInstance("X.509");
//			caInput = new BufferedInputStream(main.getResources().openRawResource(R.raw.gd_bundle));
//			ca = (Certificate) cf.generateCertificate(caInput);
//		} catch (CertificateException e1) {
//			e1.printStackTrace();
//		} finally {
//			try {
//				caInput.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//	    // Create a KeyStore containing our trusted CAs
//	    String keyStoreType = KeyStore.getDefaultType();
//	    KeyStore keyStore = null;
//		try {
//			keyStore = KeyStore.getInstance(keyStoreType);
//		    keyStore.load(null, null);
//		    keyStore.setCertificateEntry("ca", (java.security.cert.Certificate) ca);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	    // Create a TrustManager that trusts the CAs in our KeyStore
//	    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//	    TrustManagerFactory tmf = null;
//		try {
//			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//		    tmf.init(keyStore);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//
//	    // Create an SSLContext that uses our TrustManager
//	    SSLContext context = null;
//		try {
//			context = SSLContext.getInstance("TLS");
//		    context.init(null, tmf.getTrustManagers(), null);
//		    HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
