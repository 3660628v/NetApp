package com.ning.demo.netapp;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateReportActivity extends Activity {
	
	private Spinner mSpinnerType;
	private Spinner mSpinnerPos;
	private EditText mEditCommitor;
	private Button mBtnCreate;
	private List<String> mListType, mListPos;
	private ArrayAdapter<String> mAdapterType, mAdapterPos;
	
	private String mCommitor, mUserAgent, mNewItemUrl;
	private String mCookie, mToken;
	
	private static final String _cookie_sess = "_check_app_session", _cookie_token = "remember_token";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_report);
		
		mSpinnerType = (Spinner)findViewById(R.id.spinnerReportType);
		mSpinnerPos = (Spinner)findViewById(R.id.spinnerPosition);
		mBtnCreate = (Button)findViewById(R.id.buttonNew);
		
		mListType = new ArrayList<String>();
		mListType.add("测试模板");
		
		mListPos = new ArrayList<String>();
		
		mAdapterType = new ArrayAdapter<String>(this, R.layout.activity_create_report, mListType);
		mSpinnerType.setAdapter(mAdapterType);
		
		mAdapterPos = new ArrayAdapter<String>(this, R.layout.activity_create_report, mListPos);
		mSpinnerPos.setAdapter(mAdapterPos);
		
		mEditCommitor = (EditText)findViewById(R.id.editTextCommitor);
		
		mBtnCreate.setOnClickListener(
				new View.OnClickListener() {	
					public void onClick(View v) {
						// TODO Auto-generated method stub

					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_create_report, menu);
		return true;
	}
	
	public class CreateReportTask extends AsyncTask<Void, Void, Boolean> {
		private String _errmsg;
		private final String _url_create = "http://www.365check.net/organizations/93/reports.mobile";
		
		protected void onPreExecute() {
			mCommitor = mEditCommitor.getText().toString();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				HttpPost httpRequest = new HttpPost(_url_create);
				
				List<NameValuePair> sess_params = new ArrayList<NameValuePair>();
				sess_params.add(new BasicNameValuePair("authenticity_token","erCMSeO6hGZvI9YK1jEwoVqZy+DCtquvcTxLsuNjwr0="));
				sess_params.add(new BasicNameValuePair("report[template_id]","57"));
				sess_params.add(new BasicNameValuePair("report[location_id]", ""));
				sess_params.add(new BasicNameValuePair("report[reporter_name]",mCommitor));
				
				CookieStore cookies = new BasicCookieStore();
				BasicClientCookie bc1 = new BasicClientCookie(_cookie_sess, mCookie);
				bc1.setVersion(0);
		        bc1.setDomain(".365check.net");
		        bc1.setPath("/");
				BasicClientCookie bc2 = new BasicClientCookie(_cookie_token, mToken);
				bc2.setVersion(0);
		        bc2.setDomain(".365check.net");
		        bc2.setPath("/");
		        cookies.addCookie(bc1);
		        cookies.addCookie(bc2);
				HttpContext context = new BasicHttpContext();
				context.setAttribute(ClientContext.COOKIE_STORE, cookies);
				
				HttpParams httpparam = new BasicHttpParams();
    			HttpProtocolParams.setUserAgent(httpparam, mUserAgent);
				
				httpRequest.setEntity(new UrlEncodedFormEntity(sess_params,HTTP.UTF_8));
				HttpClient httpCli = new DefaultHttpClient(httpparam);
				httpCli.execute(httpRequest, context);
				
				HttpUriRequest curReq = (HttpUriRequest)context.getAttribute(ExecutionContext.HTTP_REQUEST);
				mNewItemUrl = curReq.getURI().toString();
				
				if ( mNewItemUrl.isEmpty() ) {
					throw new Exception("create new failed!");
				}
			} catch ( Exception e ) {
				_errmsg = "stage 3: "+e.toString();
				return false;
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			//mAuthTask = null;
			//showProgress(false);

			if (success) {
        		Bundle sess_data = new Bundle();
    			sess_data.putString("sess", mCookie);
    			sess_data.putString("token", mToken);
    			//sess_data.putString("csrf", ((NetAppActivity)context)._csrf);
				sess_data.putString("useragent", mUserAgent);
    			sess_data.putString("url", mNewItemUrl);
    			Intent yunjianIntent = new Intent(CreateReportActivity.this, NetAppActivity.class);
    			yunjianIntent.putExtras(sess_data);
				
				startActivity(yunjianIntent);
			} else {
				Toast.makeText(CreateReportActivity.this, "create New report failed!"+_errmsg,
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(CreateReportActivity.this, "create New report CANCELED!", Toast.LENGTH_LONG).show();
		}
	}

}
