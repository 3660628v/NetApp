package com.ning.demo.netapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class GetInfoTask extends AsyncTask<String, Integer, Boolean> {
	protected String _errmsg;
	protected List<NameValuePair> sess_params;
	protected HttpContext hcon;
	final private String _cookie_sess = "_check_app_session", _cookie_token = "remember_token";
	public String result;
	
	protected void initPostValues() {
		sess_params = new ArrayList<NameValuePair>();
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO: attempt authentication against a network service.
		String urlstr = params[0];
		String cookie = params[1];
		String token = params[2];
		//String code = params[3];
		String useragent = params[3];
		String type = params[4];
		
		try {
			CookieStore cs = new BasicCookieStore();
			BasicClientCookie bc1 = new BasicClientCookie(_cookie_sess, cookie);
			bc1.setVersion(0);
	        bc1.setDomain(".365check.net");
	        bc1.setPath("/");
			BasicClientCookie bc2 = new BasicClientCookie(_cookie_token, token);
			bc2.setVersion(0);
	        bc2.setDomain(".365check.net");
	        bc2.setPath("/");
	        cs.addCookie(bc1);
	        cs.addCookie(bc2);
			
			hcon = new BasicHttpContext();
			hcon.setAttribute(ClientContext.COOKIE_STORE, cs);
			
			HttpParams httpparam = new BasicHttpParams();
			HttpProtocolParams.setUserAgent(httpparam, useragent);
			
			if ( type.equals("get") ) {
				HttpGet httpRequest = new HttpGet(urlstr);
				HttpResponse httpRep = new DefaultHttpClient(httpparam).execute(httpRequest, hcon);
				result = EntityUtils.toString(httpRep.getEntity());
			} else {
				initPostValues();
				HttpPost httpRequest = new HttpPost(urlstr);
				httpRequest.setEntity(new UrlEncodedFormEntity(sess_params,HTTP.UTF_8));
				new DefaultHttpClient(httpparam).execute(httpRequest, hcon);
			}
			
			//_tab = TableAdapter.DecodeHtml(EntityUtils.toString(httpRep.getEntity()), _width);

		} catch ( Exception e ) {
			_errmsg = "stage 3: "+e.toString();
			return false;
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
		return true;
	}

	@Override
	protected void onPostExecute(final Boolean succ) {
		//showProgress(false);

		if ( succ ) {
			// TODO: get data
			//Bundle sess_data = new Bundle();
			//sess_data.putString("html", result);
			//Intent yunjianIntent = new Intent(LoginActivity.this,NetAppActivity.class);
			//yunjianIntent.putExtras(sess_data);
			
			//startActivity(yunjianIntent);
		} else {
			// TODO: get data faild
			//mPasswordView.setError(_errmsg);
			//mPasswordView.requestFocus();
		}
	}

	@Override
	protected void onCancelled() {
		//showProgress(false);
	}
}
