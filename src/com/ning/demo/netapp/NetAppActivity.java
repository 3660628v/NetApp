package com.ning.demo.netapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
//used for interacting with user interface
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.ning.demo.netapp.TableAdapter.TableCell;
import com.ning.demo.netapp.TableAdapter.TableRow;

public class NetAppActivity extends Activity {
	Handler _h;
	//EditText _etext;
	TextView _content, _info;
	Button _btn_go, _btn_post;
	Runnable _dlrun, _dlrun_post;
	ArrayList<String> _buf;
	InputStreamReader _ins;
	ProgressBar _bar;
	ListView _lv, _lvBtn;
	ArrayList<TableRow> _tab, _tabBtn;
	//TableCell[] _titles;
	int _width;
	String _urlapi_login, _fetchurl, _htmlbuf;
	final String _cookie_sess = "_check_app_session", _cookie_token = "remember_token";
	public String _cookie, _token, _csrf, _useragent;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //_etext = (EditText)findViewById(R.id.address);
        _content = (TextView)findViewById(R.id.pagetext);
        _info = (TextView)findViewById(R.id.debuginfo);
        _btn_go = (Button)findViewById(R.id.ButtonGo);
        _btn_post = (Button)findViewById(R.id.ButtonPost);
        _buf = new ArrayList<String>();
        _bar = (ProgressBar)findViewById(R.id.progressBar1);
        _lv = (ListView)findViewById(R.id.ListView01);
        _lvBtn = (ListView)findViewById(R.id.ListView_btn);
        _tab = _tabBtn = null;
        //_tab = new ArrayList<TableRow>();
        //_tabBtn = new ArrayList<TableRow>();
        
        //_titles = new TableCell[3];
        
        //_titles[0] = new TableCell( "编号", _width, LayoutParams.MATCH_PARENT, TableCell.STRING);
        //_titles[1] = new TableCell( "分区名称", _width+8, LayoutParams.MATCH_PARENT, TableCell.STRING);
        //_titles[2] = new TableCell( "报告列表", _width+16, LayoutParams.MATCH_PARENT, TableCell.STRING);
        
        _lv.setVisibility(View.GONE);
        _lvBtn.setVisibility(View.GONE);
        
        _content.setMovementMethod(LinkMovementMethod.getInstance());
        _info.setMovementMethod(LinkMovementMethod.getInstance());
        
        Bundle sess_data = new Bundle();
        sess_data = this.getIntent().getExtras();
        _cookie = new String(sess_data.getString("sess"));
        _token = new String(sess_data.getString("token"));
        _csrf = new String(sess_data.getString("csrf"));
        _urlapi_login = new String(sess_data.getString("url"));
        _useragent = new String(sess_data.getString("useragent"));
        
        _info.append("url: "+_urlapi_login+"\n");
        _info.append("token: "+_token+"\n");
        _info.append("csrf: "+_csrf+"\n");
        _info.append("useragent: "+_useragent+"\n");
        
        _h = new Handler() {
        	public void handleMessage(Message msg) {
        		switch(msg.what) {
        		case 0:
        			_bar.setProgress(100);
        			_bar.setVisibility(View.GONE);
        			_lv.setVisibility(View.GONE);
        			_lvBtn.setVisibility(View.GONE);
        			_content.setVisibility(View.VISIBLE);
        			_content.append("size: "+_buf.size()+"\n");
        			for (int i=0; i<100 && i<_buf.size(); ++i) {
        				_htmlbuf = _htmlbuf + i + ". " + _buf.get(i) + "\n";
        			}
        			
        			_content.append(_htmlbuf);
        			break;
        		case 1:
        			_info.append((String)msg.obj+"\n");
        			break;
        		case 2:
        			_bar.setProgress(100);
        			_bar.setVisibility(View.GONE);
        			_content.setVisibility(View.GONE);
        			
        			if ( _tabBtn != null && _tabBtn.size() > 0 ) {
        				_lvBtn.setVisibility(View.VISIBLE);
        				_lvBtn.setAdapter(new TableAdapter(NetAppActivity.this, _tabBtn));
        				_info.append("Button rows: "+_tabBtn.size()+"\n");
        			} else {
        				_info.append("no button found!\n");
        			}
        			
        			if ( _tab != null && _tab.size() > 0 ) {
	        			_lv.setVisibility(View.VISIBLE);
	        	        _lv.setAdapter(new TableAdapter(NetAppActivity.this, _tab));
	        	        _info.append("Table rows: "+_tab.size()+"\n");
        			} else {
        				_info.append("no table found!\n");
        			}
        	        //_lv.setOnItemClickListener(new ItemClickEvent());
        	        break;
        		default:
        			if ( msg.what > 1000000 ) {
        				int proc = msg.what - 1000000;
        				_bar.setProgress(proc);
        			}
        			break;
        		}
        		super.handleMessage(msg);
        	}
        };
        
        _btn_go.setOnClickListener(
        	new	Button.OnClickListener() {
        		public void onClick(View v) {
        			_fetchurl = _urlapi_login; //_etext.getText().toString();
        			
        			Pattern patt_http = Pattern.compile("^https?://");
        			if ( ! patt_http.matcher(_fetchurl).find() ) {
        				_fetchurl = "http://"+_fetchurl;
        			}
        			_info.setText("fetching "+_fetchurl+"\n");
        			_content.setText("");
        			_buf.clear();
        			_htmlbuf = "";
        			
        			_bar.setVisibility(View.VISIBLE);
        			_bar.setMax(100);
        			_bar.setProgress(0);
        			new Thread(_dlrun).start();
        			_info.append("started thread ...\n");
        		}

        	});
        
        _btn_post.setOnClickListener(
            	new	Button.OnClickListener() {
            		public void onClick(View v) {
            			_info.setText("Posting data ...\n");
            			_content.setText("");
            			_buf.clear();
            			
            			if ( _tab != null ) _tab.clear();
            			if ( _tabBtn != null ) _tabBtn.clear();
            			//_tab.add(new TableRow(_titles));
            			
            			_bar.setVisibility(View.VISIBLE);
            			_bar.setMax(100);
            			_bar.setProgress(0);
            			new Thread(_dlrun_post).start();
            			_info.append("started thread ...\n");
            		}

            	});
        
        _lv.setOnItemClickListener(new OnItemClickListener() {   
            public void onItemClick(AdapterView<?> parent, View view, int position,  
                    long id)
            {  
            	Toast.makeText(NetAppActivity.this, "pos: "+position+" id: "+id, Toast.LENGTH_SHORT).show();
            	
            }
        });
        
        _dlrun = new Runnable() {
        	public void run() {
        		try {
        			Message infoMsg;
        			infoMsg = new Message();
        			infoMsg.what = 1;
        			infoMsg.obj = "Get "+_fetchurl;
        			NetAppActivity.this._h.sendMessage(infoMsg);
        			
        			CookieStore cs = new BasicCookieStore();
					BasicClientCookie bc1 = new BasicClientCookie(_cookie_sess, _cookie);
					bc1.setVersion(0);
			        bc1.setDomain(".365check.net");
			        bc1.setPath("/");
					BasicClientCookie bc2 = new BasicClientCookie(_cookie_token, _token);
					bc2.setVersion(0);
			        bc2.setDomain(".365check.net");
			        bc2.setPath("/");
			        cs.addCookie(bc1);
			        cs.addCookie(bc2);
					
        			HttpContext hcon = new BasicHttpContext();
        			hcon.setAttribute(ClientContext.COOKIE_STORE, cs);
        			
        			HttpParams httpparam = new BasicHttpParams();
        			HttpProtocolParams.setUserAgent(httpparam, _useragent);
        			
					HttpGet httpRequest = new HttpGet(_fetchurl);
					HttpResponse httpRep = new DefaultHttpClient(httpparam).execute(httpRequest, hcon);
					
					_htmlbuf = EntityUtils.toString(httpRep.getEntity());
					
					//infoMsg.obj = "send html ...";
					//_h.sendMessage(infoMsg);
					
					Message lmsg;
					lmsg = new Message();
					lmsg.what = 0;
					_h.sendMessage(lmsg);
					//infoMsg.obj = "proc done!";
					//_h.sendMessage(infoMsg);
        		} catch (Exception e) {
        			//_info.append("url exception: "+e.toString()+"\n");
        			Message lmsg;
        			lmsg = new Message();
        			lmsg.what = 1;
        			lmsg.obj = "url exception: "+e.toString();
        			_h.sendMessage(lmsg);
        		}
        	}
        };
        
        _dlrun_post = new Runnable() {
        	public void run() {
        		try {
        			Message infoMsg;
        			infoMsg = new Message();
        			infoMsg.what = 1;
        			infoMsg.obj = "Post "+_urlapi_login;
        			NetAppActivity.this._h.sendMessage(infoMsg);
        			
					//_info.setText(_etext.getText().toString()+"\n");
        			CookieStore cs = new BasicCookieStore();
					BasicClientCookie bc1 = new BasicClientCookie(_cookie_sess, _cookie);
					bc1.setVersion(0);
			        bc1.setDomain(".365check.net");
			        bc1.setPath("/");
					BasicClientCookie bc2 = new BasicClientCookie(_cookie_token, _token);
					bc2.setVersion(0);
			        bc2.setDomain(".365check.net");
			        bc2.setPath("/");
			        cs.addCookie(bc1);
			        cs.addCookie(bc2);
					
        			HttpContext hcon = new BasicHttpContext();
        			hcon.setAttribute(ClientContext.COOKIE_STORE, cs);
        			
        			HttpParams httpparam = new BasicHttpParams();
        			HttpProtocolParams.setUserAgent(httpparam, _useragent);
        			
					HttpGet httpRequest = new HttpGet(_urlapi_login);
					HttpResponse httpRep = new DefaultHttpClient(httpparam).execute(httpRequest, hcon);
					
					String html = EntityUtils.toString(httpRep.getEntity());
			        Pattern patt_btn = Pattern.compile("<a href=\"([^\"]+)\" class=\"btn ([^\"]+)\"(.*?)>(.*?)</a>");
			        Matcher mat_btn = patt_btn.matcher(html);
			        ArrayList<TableCell> trBtn = new ArrayList<TableCell>();
			        while ( mat_btn.find() ) {
			        	String path = mat_btn.group(1);
			        	String property = mat_btn.group(3);
			        	String title = mat_btn.group(4);
			        	
			        	if ( Pattern.matches(".*new.*", path) ) {
			        		trBtn.add(new TableCell(title, _width, LayoutParams.MATCH_PARENT, TableCell.BTN_NEW, 
			        				"http://www.365check.net"+path));
			        	} else if ( Pattern.matches(".*edit.*", path) ) {
			        		trBtn.add(new TableCell(title, _width, LayoutParams.MATCH_PARENT, TableCell.BTN_EDIT, 
			        				"http://www.365check.net"+path));
			        	} else if ( Pattern.matches(".*delete.*", property) ) {
			        		trBtn.add(new TableCell(title, _width, LayoutParams.MATCH_PARENT, TableCell.BTN_DEL, 
			        				"http://www.365check.net"+path));
			        	} else {
			        		trBtn.add(new TableCell(title, _width, LayoutParams.MATCH_PARENT, TableCell.BUTTON, 
			        				"http://www.365check.net"+path));
			        	}
			        }
			        if ( trBtn.size() > 0 ) {
			        	_tabBtn = new ArrayList<TableRow>();
			        	_tabBtn.add(new TableRow(trBtn));
			        }
			        
					_tab = DecodeHtml(html, _width);
					
					//TableCell[] cells = new TableCell[3];
			        //cells[0] = new TableCell( "1", _width, LayoutParams.MATCH_PARENT, TableCell.STRING);
			        //cells[1] = new TableCell( "测试分区", _width+8, LayoutParams.MATCH_PARENT, TableCell.STRING);
			        //cells[2] = new TableCell( "全部报告", _width+16, LayoutParams.MATCH_PARENT, TableCell.STRING);
			        
					//_tab.add(new TableRow(cells));
					//infoMsg.obj = "url read ...";
					//_h.sendMessage(infoMsg);
					
					//infoMsg.obj = "send html ...";
					//_h.sendMessage(infoMsg);
					
					Message lmsg;
					lmsg = new Message();
					lmsg.what = 2;
					_h.sendMessage(lmsg);
					//infoMsg.obj = "proc done!";
					//_h.sendMessage(infoMsg);
        		} catch (Exception e) {
        			//_info.append("url exception: "+e.toString()+"\n");
        			Message lmsg;
        			lmsg = new Message();
        			lmsg.what = 1;
        			lmsg.obj = "url exception: "+e.toString();
        			_h.sendMessage(lmsg);
        		}
        	}
        };
    }
    
    public static ArrayList<TableRow> DecodeHtml(String html, int width) {  
        ArrayList<TableRow> rowTab = null; // = new ArrayList<TableRow>();
        
        //this._html = html;
        String reg_title = "<table [^<>]*>(.+?)</table>";
        String rawtab = "";
        Pattern patt_title = Pattern.compile(reg_title, Pattern.MULTILINE|Pattern.DOTALL);
        Matcher m = patt_title.matcher(html);
        if ( m.find() ) {
        	//_head = new TableCell(m.group(1), width, LayoutParams.MATCH_PARENT, TableCell.STRING);
        	rawtab = m.group(1);
        }
        
        if( ! rawtab.isEmpty() ) {
            ArrayList<ArrayList<TableCell>> tab = new ArrayList<ArrayList<TableCell>>();
        	String reg = "<tr>(.+?)</tr>";
        	int max_col = 0;
        	
        	Pattern patt = Pattern.compile(reg, Pattern.MULTILINE|Pattern.DOTALL);
        	Matcher mm = patt.matcher(rawtab);
        	while ( mm.find() ) {
        		ArrayList<TableCell> tc_lst = new ArrayList<TableCell>();
        		
                String cell = mm.group(1);
                String reg_tab = "<t[hd].*?>[ \t\r\n]*([^\r\n]+?)[ \t\r\n]*((?=</t[hd])|(?=</a))";
                Pattern pp = Pattern.compile(reg_tab, Pattern.MULTILINE|Pattern.DOTALL);
                Matcher mmm = pp.matcher(cell);
                while ( mmm.find() ) {
                	String item = mmm.group(1);
                	String tag = "";
                	Pattern pp1 = Pattern.compile("^[ \t\r\n]*<([^<>]+)>[\r\n]*([^<>\r\n]*)[\r\n]*");
                	Matcher mm1 = pp1.matcher(item);
                	if ( mm1.find() ) {
                		item = mm1.group(2);
                		tag = mm1.group(1);
                	}
                	
                	Pattern pp3 = Pattern.compile("^[ \t\r\n]*([^<>\r\n]+)");
                	Matcher mm3 = pp3.matcher(item);
                	if ( mm3.find() ) {
                		item = mm3.group(1);
                	}
                	Pattern pp2 = Pattern.compile("^a href=\"([^\"]+)\"");
                	Pattern pp4 = Pattern.compile("^img src=\"([^\"]+)\"");
                	Matcher mm2 = pp2.matcher(tag);
                	Matcher mm4 = pp4.matcher(tag);
                	
                	if ( mm2.find() ) {
                		tc_lst.add(new TableCell(item, width, LayoutParams.MATCH_PARENT, TableCell.BUTTON, 
                				"http://www.365check.net"+mm2.group(1)));
                	} else if ( mm4.find() ) {
                		Bitmap bitmap = null;
                		try {
                			URL myImgUrl = new URL("http://www.365check.net"+mm4.group(1));
                			HttpURLConnection conn = (HttpURLConnection)myImgUrl.openConnection();   
                			conn.setDoInput(true);   
                			conn.connect();   
                			InputStream is = conn.getInputStream();   
           					bitmap = BitmapFactory.decodeStream(is);   
                			is.close();
                			
                			tc_lst.add(new TableCell(bitmap, width, LayoutParams.MATCH_PARENT, TableCell.IMAGE));
                		} catch (IOException e) {   
                			//e.printStackTrace();
                			tc_lst.add(new TableCell(item, width, LayoutParams.MATCH_PARENT, TableCell.STRING));
                		}
                		
                	} else {
                		tc_lst.add(new TableCell(item, width, LayoutParams.MATCH_PARENT, TableCell.STRING));
                	}
                }
                
                if ( tc_lst.size() > 0 ) {
                	tab.add(tc_lst);
                	if ( tc_lst.size() > max_col ) max_col = tc_lst.size();
                }
        	}
        	
        	if ( tab.size() > 0 ) rowTab = new ArrayList<TableRow>();
        	
        	for ( int i=0; i<tab.size(); ++i ) {
        		ArrayList<TableCell> tr = tab.get(i);
        		if ( tr.size() < max_col ) {
        			int ss = max_col - tr.size();
        			for ( int j=0; j<ss; ++j ) {
        				tr.add(new TableCell("", width, LayoutParams.MATCH_PARENT, TableCell.STRING));
        			}
        		}
        		rowTab.add(new TableRow(tr));
        	}
        }
        return rowTab;
    }
}