package com.ning.demo.netapp;

import java.util.ArrayList;
import java.util.List;  
import android.content.Context;  
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;  
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.Button;
import android.widget.ImageView;  
import android.widget.LinearLayout;  
import android.widget.TextView; 
import android.widget.Toast;

public class TableAdapter extends BaseAdapter {
    private Context context;
    //private TableCell _head;
    private List<TableRow> table;
    //private String _html;
    
    public TableAdapter(Context context, List<TableRow> table) {  
        this.context = context;  
        this.table = table;  
    }
    
    public int getCount() {  
        return table.size();  
    }
    
    public long getItemId(int position) {  
        return position; 
    }  
    
    public TableRow getItem(int position) {  
        return table.get(position);  
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {  
        TableRow tableRow = table.get(position);  
        return new TableRowView(this.context, tableRow);  
    }  
    /** 
     * TableRowView ʵ�ֱ���е���ʽ 
     * @author hellogv 
     */  
    class TableRowView extends LinearLayout {  
        public TableRowView(Context context, TableRow tableRow) {  
            super(context);
            
            DisplayMetrics dm = new DisplayMetrics();
            ((NetAppActivity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels/tableRow.getSize();
              
            this.setOrientation(LinearLayout.HORIZONTAL);  
            for (int i = 0; i < tableRow.getSize(); i++) {//�����Ԫ��ӵ���  
                TableCell tableCell = tableRow.getCellValue(i);  
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(  
                        width, tableCell.height);//���ո�Ԫָ���Ĵ�С���ÿռ�  
                layoutParams.setMargins(0, 0, 1, 1);//Ԥ����϶����߿�  
                if (tableCell.type == TableCell.STRING) {//�����Ԫ���ı�����  
                    TextView textCell = new TextView(context);
                    textCell.setLines(3);  
                    textCell.setGravity(Gravity.CENTER);  
                    textCell.setBackgroundColor(Color.BLACK);//������ɫ  
                    textCell.setText(String.valueOf(tableCell.value));
                    addView(textCell, layoutParams);  
                } else if (tableCell.type == TableCell.IMAGE) {//�����Ԫ��ͼ������  
                    ImageView imgCell = new ImageView(context);  
                    imgCell.setBackgroundColor(Color.BLACK);//������ɫ  
                    imgCell.setImageBitmap((Bitmap) tableCell.value);
                    addView(imgCell, layoutParams);  
                } else if (tableCell.type == TableCell.BUTTON) {
                	Button btncell = new Button(context);
                	btncell.setText(String.valueOf(tableCell.value));
                	btncell.setOnClickListener(new TableButtonListener(context, 
                			String.valueOf(tableCell.link)));
                	addView(btncell, layoutParams);
                } else if (tableCell.type == TableCell.BTN_NEW) {
                	Button btncell = new Button(context);
                	btncell.setText(String.valueOf(tableCell.value));
                	btncell.setOnClickListener(new TableNewBtnListener(context, 
                			String.valueOf(tableCell.link)));
                	addView(btncell, layoutParams);
                } else if (tableCell.type == TableCell.BTN_EDIT) {
                	Button btncell = new Button(context);
                	btncell.setText(String.valueOf(tableCell.value));
                	btncell.setOnClickListener(new TableEditBtnListener(context, 
                			String.valueOf(tableCell.link)));
                	addView(btncell, layoutParams);
                }
            }  
            this.setBackgroundColor(Color.WHITE);//������ɫ�����ÿ�϶��ʵ�ֱ߿�  
        }
        
        
        public class TableButtonListener implements Button.OnClickListener {
        	Context _context;
        	String _url;
        	public TableButtonListener(Context context, String val) {
        		_context = context;
        		_url = val;
        	}
        	
        	public Class<?> getIntentClass() {
        		return NetAppActivity.class;
        	}
        	
        	public void onClick(View v) {
        		Toast.makeText(_context, _url, Toast.LENGTH_SHORT).show();
        		Bundle sess_data = new Bundle();
    			sess_data.putString("sess", ((NetAppActivity)context)._cookie);
    			sess_data.putString("token", ((NetAppActivity)context)._token);
    			sess_data.putString("csrf", ((NetAppActivity)context)._csrf);
				sess_data.putString("useragent", ((NetAppActivity)context)._useragent);
    			sess_data.putString("url", _url);
    			Intent yunjianIntent = new Intent(_context, getIntentClass());
    			yunjianIntent.putExtras(sess_data);
    			
    			_context.startActivity(yunjianIntent);
        	}
        }
        
        public class TableNewBtnListener extends TableButtonListener {
        	public TableNewBtnListener(Context con, String val) {
        		super(con, val);
        	}
        	
        	public Class<?> getIntentClass() {
        		return NewReportActivity.class;
        	}
        }
        
        public class TableEditBtnListener extends TableButtonListener {
        	public TableEditBtnListener(Context con, String val) {
        		super(con, val);
        	}
        	
        	public Class<?> getIntentClass() {
        		return NewReportActivity.class;
        	}
        }
    }

    /** 
     * TableRow ʵ�ֱ����� 
     * @author hellogv 
     */  
    static public class TableRow {  
        private ArrayList<TableCell> _cells;  
        public TableRow(TableCell[] cell) {
        	_cells = new ArrayList<TableCell>();
            for ( int i=0; i<cell.length; ++i ) {
            	_cells.add(cell[i]);
            }
        }
        
        public TableRow(ArrayList<TableCell> cells) {
        	_cells = cells;
        }
        
        public int getSize() {  
            return _cells.size();  
        }  
        public TableCell getCellValue(int index) {  
            if (index >= _cells.size())  
                return null;  
            return _cells.get(index);  
        }  
    }  
    /** 
     * TableCell ʵ�ֱ��ĸ�Ԫ 
     * @author hellogv 
     */  
    static public class TableCell {  
        static public final int STRING = 0;  
        static public final int IMAGE = 1;
        static public final int BUTTON = 2;
        static public final int BTN_NEW = 3;
        static public final int BTN_EDIT = 4;
        static public final int BTN_DEL = 5;
        public Object value, link;  
        public int width;  
        public int height;

        private int type;
        public TableCell(Object value, int width, int height, int type) {  
            this.value = value;  
            this.width = width;  
            this.height = height;  
            this.type = type;  
        }
        
        public TableCell(Object value, int width, int height, int type, Object link) {  
            this.value = value;
            this.link = link;
            this.width = width;  
            this.height = height;  
            this.type = type;  
        }
    }
}
