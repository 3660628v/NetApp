package com.ning.demo.netapp;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;

public class GetInfoTask extends AsyncTask<String, Integer, String> {
	private String _errmsg;
	final private String _cookie_sess = "_check_app_session", _cookie_token = "remember_token";
	public String result;
	
	@Override
	protected String doInBackground(String... params) {
		// TODO: attempt authentication against a network service.
		String urlstr = params[0];
		String cookie = params[1];
		String token = params[2];
		//String code = params[3];
		String buf = "";
		
		try {
			CookieStore cs = new BasicCookieStore();
			BasicClientCookie bc1 = new BasicClientCookie(_cookie_sess, cookie);
			bc1.setVersion(0);
	        bc1.setDomain("365check.net");
	        bc1.setPath("/");
			BasicClientCookie bc2 = new BasicClientCookie(_cookie_token, token);
			bc2.setVersion(0);
	        bc2.setDomain("365check.net");
	        bc2.setPath("/");
	        cs.addCookie(bc1);
	        cs.addCookie(bc2);
			
			HttpContext hcon = new BasicHttpContext();
			hcon.setAttribute(ClientContext.COOKIE_STORE, cs);
			
			HttpGet httpRequest = new HttpGet(urlstr);
			HttpResponse httpRep = new DefaultHttpClient().execute(httpRequest, hcon);
			
			//_tab = TableAdapter.DecodeHtml(EntityUtils.toString(httpRep.getEntity()), _width);
			result = EntityUtils.toString(httpRep.getEntity());

		} catch ( Exception e ) {
			_errmsg = "stage 3: "+e.toString();
			return "";
		}

		/*
		for (String credential : DUMMY_CREDENTIALS) {
			String[] pieces = credential.split(":");
			if (pieces[0].equals(mEmail)) {
				// Account exists, return true if the password matches.
				return pieces[1].equals(mPassword);
			}
		}
		*/

		// TODO: register the new account here.
		return buf;
	}

	@Override
	protected void onPostExecute(final String buff) {
		//showProgress(false);

		if ( ! buff.isEmpty() ) {
			Bundle sess_data = new Bundle();
			sess_data.putString("html", buff);
			//Intent yunjianIntent = new Intent(LoginActivity.this,NetAppActivity.class);
			//yunjianIntent.putExtras(sess_data);
			
			//startActivity(yunjianIntent);
		} else {
			//mPasswordView.setError(_errmsg);
			//mPasswordView.requestFocus();
		}
	}

	@Override
	protected void onCancelled() {
		//showProgress(false);
	}
}
