package com.stripe1.xmouse.util;

import java.util.ArrayList;
import java.util.List;

import com.stripe1.xmouse.MainActivity;
import com.stripe1.xmouse.R;
import com.stripe1.xmouse.R.id;
import com.stripe1.xmouse.R.layout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ScriptListAdapter extends BaseAdapter {
	//int w = 400;//fig.getWidth();
	//int h = 200;//fig.getHeight();
	//LayoutAnimationController controller;
	private ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
	private Activity myActivity;
	//private LayoutParams lp = new LayoutParams( 300,LayoutParams.MATCH_PARENT);
	
	//private final String TAG = "FlyMenuAdapter";
    public ScriptListAdapter(Activity _myActivity){
        myActivity = _myActivity;
        //items.add("New Command");
        items = MainActivity.db.listAll(DatabaseHandler.SCRIPT_TABLE_NAME, new String[] {"Alias","Script","id"});
        
    }
    public int getCount() {
        return items.size();
    }
    @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
    	//lp.setMargins(1, 1,1, 1);//left,top,right, bottom
    	String label = items.get(position).get(0);
    	String script = items.get(position).get(1);
    	int id = Integer.valueOf(items.get(position).get(2));
		//TODO work on new script interface
    	
		LayoutInflater inflater = (LayoutInflater) myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		View v = inflater.inflate(R.layout.scriptbutton, null);
		
		CustomButton name = (CustomButton) v.findViewById(R.id.scriptButton);
		CustomButton remove = (CustomButton) v.findViewById(R.id.scriptButton_delete);
		
        name.setText(label);
        name.setScript(script);
        remove.setDatadaseId(id);
        
        
        return v;
    }

	@Override
	public Object getItem(int position) {
		
		return items.get(position);
	}

	
	/*public String getItemDbId(int position) {
		return items.get(position).getTag(0);//index 0 is the `id` of monophage in lims db
	}*/
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}	
}