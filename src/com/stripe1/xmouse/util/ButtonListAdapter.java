package com.stripe1.xmouse.util;

import java.util.ArrayList;
import java.util.List;

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


public class ButtonListAdapter extends BaseAdapter {
	//int w = 400;//fig.getWidth();
	//int h = 200;//fig.getHeight();
	//LayoutAnimationController controller;
	private ArrayList<String> items = new ArrayList<String>();
	private Activity myActivity;
	//private LayoutParams lp = new LayoutParams( 300,LayoutParams.MATCH_PARENT);
	
	//private final String TAG = "FlyMenuAdapter";
    public ButtonListAdapter(Activity _myActivity){
        myActivity = _myActivity;
        
        items.add("XF86WWW");
        items.add("XF86Display");
        items.add("XF86Eject");
        items.add("XF86LogOff");
        items.add("XF86PowerDown");
		items.add("XF86PowerOff");
		items.add("XF86Sleep");
		
		
        items.add("XF86AddFavorite"); 
        items.add("XF86ApplicationLeft");
        items.add("XF86ApplicationRight");
        items.add("XF86AudioMedia");
        items.add("XF86AudioMute");
        items.add("XF86AudioNext");
        items.add("XF86AudioPause");
        items.add("XF86AudioPlay");
        items.add("XF86AudioPrev");
		items.add("XF86AudioLowerVolume");
		items.add("XF86AudioRaiseVolume");
		items.add("XF86AudioRecord");
		items.add("XF86AudioRewind");
		items.add("XF86AudioStop");
		items.add("XF86Away");
		items.add("XF86Back");
		items.add("XF86Book");
		items.add("XF86BrightnessAdjust"); 
		items.add("XF86CD");
		items.add("XF86Calculator"); 
		items.add("XF86Calendar");
		items.add("XF86Clear");
		items.add("XF86ClearGrab");
		items.add("XF86Close");
		items.add("XF86Community");
		items.add("XF86ContrastAdjust");
		items.add("XF86Copy");
		items.add("XF86Cut");
		items.add("XF86DOS");
		
		items.add("XF86Documents");
		
		items.add("XF86Excel");
		items.add("XF86Explorer");
		items.add("XF86Favorites");
		items.add("XF86Finance");
		items.add("XF86Forward");
		items.add("XF86Game");
		items.add("XF86Go");
		items.add("XF86History");
		items.add("XF86HomePage");
		items.add("XF86HotLinks");
		items.add("XF86Launch0");
		items.add("XF86Launch1");
		items.add("XF86Launch2");
		items.add("XF86Launch3");
		items.add("XF86Launch4");
		items.add("XF86Launch5");
		items.add("XF86Launch6");
		items.add("XF86Launch7");
		items.add("XF86Launch8");
		items.add("XF86Launch9");
		items.add("XF86LaunchA");
		items.add("XF86LaunchB");
		items.add("XF86LaunchC");
		items.add("XF86LaunchD");
		items.add("XF86LaunchE");
		items.add("XF86LaunchF");
		items.add("XF86LightBulb");
		
		items.add("XF86Mail");
		items.add("XF86MailForward");
		items.add("XF86Market");
		items.add("XF86Meeting");
		items.add("XF86Memo");
		items.add("XF86MenuKB");
		items.add("XF86MenuPB");
		items.add("XF86Messenger");
		items.add("XF86Music");
		items.add("XF86MyComputer");
		items.add("XF86MySites");
		items.add("XF86New");
		items.add("XF86News");
		items.add("XF86Next_VMode");
		items.add("XF86Prev_VMode");
		items.add("XF86OfficeHome");
		items.add("XF86Open");
		items.add("XF86OpenURL");
		items.add("XF86Option");
		items.add("XF86Paste");
		items.add("XF86Phone");
		items.add("XF86Pictures");
		
		items.add("XF86Next_VMode");
		items.add("XF86Prev_VMode");
		items.add("XF86Q");
		items.add("XF86Refresh");
		items.add("XF86Reload");
		items.add("XF86Reply");
		items.add("XF86RockerDown");
		items.add("XF86RockerEnter");
		items.add("XF86RockerUp");
		items.add("XF86RotateWindows");
		items.add("XF86RotationKB");
		items.add("XF86RotationPB");
		items.add("XF86Save");
		items.add("XF86ScreenSaver");
		items.add("XF86ScrollClick");
		items.add("XF86ScrollDown");
		items.add("XF86ScrollUp");
		items.add("XF86Search");
		items.add("XF86Send");
		items.add("XF86Shop");
		
		items.add("XF86Spell");
		items.add("XF86SplitScreen");
		items.add("XF86Standby");
		items.add("XF86Start");
		items.add("XF86Stop");
		items.add("XF86Support");
		items.add("XF86Switch_VT_1");
		items.add("XF86Switch_VT_10");
		items.add("XF86Switch_VT_11");
		items.add("XF86Switch_VT_12");
		items.add("XF86Switch_VT_2");
		items.add("XF86Switch_VT_3");
		items.add("XF86Switch_VT_4");
		items.add("XF86Switch_VT_5");
		items.add("XF86Switch_VT_6");
		items.add("XF86Switch_VT_7");
		items.add("XF86Switch_VT_8");
		items.add("XF86Switch_VT_9");
		items.add("XF86TaskPane");
		items.add("XF86Terminal");
		items.add("XF86ToDoList");
		items.add("XF86Tools");
		items.add("XF86Travel");
		items.add("XF86Ungrab");
		items.add("XF86User1KB");
		items.add("XF86User2KB");
		items.add("XF86UserPB");
		items.add("XF86VendorHome");
		items.add("XF86Video");
		
		items.add("XF86WakeUp");
		items.add("XF86WebCam");
		items.add("XF86WheelButton");
		items.add("XF86Word");
		items.add("XF86BackForward");
		items.add("XF86Xfer");
		items.add("XF86ZoomIn");
		items.add("XF86ZoomOut");
		items.add("XF86iTouch");
        
        /*
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(300);
        set.addAnimation(animation);
        controller = new LayoutAnimationController(set, 0.25f);
        */
    }
    public int getCount() {
        return items.size();
    }
    @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
    	//lp.setMargins(1, 1,1, 1);//left,top,right, bottom
    	String label = items.get(position);
		
		LayoutInflater inflater = (LayoutInflater) myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		View v = inflater.inflate(R.layout.listbutton, null);
		
		Button tl = (Button) v.findViewById(R.id.listButton);
        tl.setText(label);
        
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