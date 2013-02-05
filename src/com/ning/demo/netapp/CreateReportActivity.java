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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_report);
		
		mSpinnerType = (Spinner)findViewById(R.id.spinnerReportType);
		mSpinnerPos = (Spinner)findViewById(R.id.spinnerPosition);
		mBtnCreate = (Button)findViewById(R.id.buttonNew);
		
		mListType = new ArrayList<String>();
		mListType.add("≤‚ ‘ƒ£∞Â");
		
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
	
	public class CreateReportTask extends GetInfoTask {
		protected void initPostValues() {
			mCommitor = mEditCommitor.getText().toString();
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
