package com.stripe1.xmouse;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.stripe1.xmouse.MouseSectionFragment.ClickType;
import com.stripe1.xmouse.util.CustomItem;

import com.stripe1.xmouse.util.ItemAdapter;
import com.stripe1.xmouse.util.SimpleScrollingStrategy;

import com.stripe1.xmouse.util.SpanVariableGridView;
import com.jcraft.jsch.*;
import com.stripe1.xmouse.tasks.SshConnectTask;
import com.stripe1.xmouse.tasks.SshExecTask;
import com.stripe1.xmouse.util.ButtonListAdapter;
import com.stripe1.xmouse.util.CoolDragAndDropGridView;
import com.stripe1.xmouse.util.CustomButton;
import com.stripe1.xmouse.util.CustomViewPager;
import com.stripe1.xmouse.util.DatabaseHandler;
import com.stripe1.xmouse.util.ScriptListAdapter;
import com.stripe1.xmouse.util.SlidingPane;
import com.stripe1.xmouse.util.ColorPickerDialog.OnColorChangedListener;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, 
	OnColorChangedListener, 
	MouseSectionFragment.OnXMouseClickListener,
	MouseSectionFragment.OnXMouseMoveListener {
    
	static ItemAdapter mItemAdapter;
	static CoolDragAndDropGridView mCoolDragAndDropGridView;
	static List<CustomItem> mItems = new LinkedList<CustomItem>();
	static View keyView;
	private static String KEYLOAYOUTFILENAME = "keyLayoutFile.csv";
    static SectionsPagerAdapter mSectionsPagerAdapter;
    public static JSch jsch=new JSch();
    public static Session session =null;
    public static Channel channel=null;
    static StringBuilder log = new StringBuilder();
    static ArrayList<ArrayList<String>> hostDBKeys;
    public static PrintStream shellStream;
    public static ByteArrayOutputStream os = null;
    static CustomViewPager mViewPager;
    static ListView scriptList;
    private Fragment cmdLogFragment;
    static EditText ET;
    public static long pTime=System.currentTimeMillis();
    static MenuItem conDiscButton;
    String setting_host="";
    String setting_user="";
    int setting_port=22;
    String setting_pass="";
    float setting_sensitivity=1.5f;
    boolean setting_autoconnect=false;
	static boolean setting_keyboard_locked=false;
    String setting_xdotool_initial="";
    //static boolean setting_mouse_background=false;
    static boolean setting_keyboard_batch=true;
    static boolean setting_keyboard_autoclear=true;
    public static String setting_key_passphrase="";
    public static boolean setting_use_keys = false;
    public static String setting_key_filename = "";
    public static boolean setting_keyboard_show_details;
    static boolean setting_command_overlay=true;
    //static boolean setting_log_staydown = true;
    public static DatabaseHandler db;
    //public static ArrayList<ArrayList<String>> dbKeys;
    static ActionBar actionBar;
    static ImageView DeleteView;
    static RelativeLayout ETLayout;
    static int potentialDeletePosition;
    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }
    @Override
    protected void onResume() {
        super.onResume();
		
        // The activity has become visible (it is now "resumed").
        getPreferences();
        //Toast.makeText(getBaseContext(), "setting_autoconnect "+String.valueOf(setting_autoconnect), Toast.LENGTH_SHORT).show();
        if(setting_autoconnect){
        	xMouseTryConnect();
        }
        showConnectionStat();
        if(mCoolDragAndDropGridView!=null){
	        mItemAdapter = new ItemAdapter(MainActivity.this, mItems);
	        mCoolDragAndDropGridView.setAdapter(mItemAdapter);
	    	mItemAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
    	saveKeyboardLayout();
        super.onStop();  // Always call the superclass method first
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        xMouseDisconnect();
    }
   
    public void xMouseDisconnect(){
    	
    	if(session!=null){
        	if(session.isConnected()){
        		channel.disconnect();
        		session.disconnect();
        		session=null;
        		channel = null;
        		
        	}
        }
    	showConnectionStat();
    }
    @Override
    public void onBackPressed() {
     //Toast.makeText(this, "Back key pressed =)", Toast.LENGTH_SHORT).show();
     super.onBackPressed();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	 switch(keyCode){
	 	/*case KeyEvent.KEYCODE_MENU:
	   case KeyEvent.KEYCODE_SEARCH:
	   case KeyEvent.KEYCODE_BACK:*/
	   case KeyEvent.KEYCODE_VOLUME_UP:
	     event.startTracking();
	     return true;
	   case KeyEvent.KEYCODE_VOLUME_DOWN:
		   event.startTracking();
	     return true;
	 }
	 return super.onKeyDown(keyCode, event);
	}
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
     switch(keyCode){
       /*case KeyEvent.KEYCODE_MENU:
       case KeyEvent.KEYCODE_SEARCH:*/
       case KeyEvent.KEYCODE_VOLUME_UP:
    	   if(event.isTracking() && !event.isCanceled()){
    	       //Toast.makeText(this, "Volumen Up released", Toast.LENGTH_SHORT).show();
    	       executeShellCommand("xdotool key XF86AudioRaiseVolume");
	   				
    	   }
    	     return true;
       case KeyEvent.KEYCODE_VOLUME_DOWN:
    	   if(event.isTracking() && !event.isCanceled()){
    	       //Toast.makeText(this, "Volumen Down released", Toast.LENGTH_SHORT).show();
    	       executeShellCommand("xdotool key XF86AudioLowerVolume");
    	   }
    	       return true;
         
       }
       return super.onKeyUp(keyCode, event);
    }
    public void getPreferences(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		setting_host= prefs.getString("setting_host", "");
		setting_user= prefs.getString("setting_user", "");
		setting_port= Integer.valueOf(prefs.getString("setting_port", "22"));
		setting_pass= prefs.getString("setting_pass", "");
		
		String setting_host_all = prefs.getString("hostPreferenceList", "0");//id of host in db
		
		
		ArrayList<ArrayList<String>> host = db.getRowWithId(DatabaseHandler.HOST_TABLE_NAME,new String[] {"Host","Username","Port","Password"},setting_host_all);
		
		if(host.size()>0){
		
			setting_host= host.get(0).get(0);
			setting_user= host.get(0).get(1);
			setting_port= Integer.valueOf(host.get(0).get(2));
			setting_pass= host.get(0).get(3);
		}else{
			
			//Log.d("getPreferences", "Zero host records, will default to last used settings");
			Toast.makeText(getBaseContext(), "No Saved Host selected, defaulting to previous settings", Toast.LENGTH_LONG).show();
		}
		setting_sensitivity = Float.valueOf(prefs.getString("sensitivity_list", "1.0f"));
		setting_autoconnect=prefs.getBoolean("autologin_checkbox", false);
		setting_xdotool_initial=prefs.getString("setting_xdotool_initial", "export DISPLAY=':0'");//changes from export DISPLAY=':0.0' 4/29/14, seems to help lag
		setting_keyboard_autoclear=prefs.getBoolean("keyboard_autoclear", true);
		setting_keyboard_show_details=prefs.getBoolean("show_commands_on_buttons",false);
		setting_use_keys = prefs.getBoolean("pref_usekeyauth",false);
		setting_key_filename=prefs.getString("pref_addkeybutton", "");
		setting_key_passphrase=prefs.getString("pref_key_passphrase", "");
		setting_keyboard_batch=prefs.getBoolean("setting_keyboard_batch",true);
		//setting_mouse_background=prefs.getBoolean("setting_mouse_background",false);
		setting_keyboard_locked=prefs.getBoolean("keyboard_layout_locked",false);
		//setting_log_staydown=prefs.getBoolean("log_staydown", true);
		//setting_command_overlay=prefs.getBoolean("command_overlay", false);
	}
    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
       
        super.onConfigurationChanged(newConfig);
        FrameLayout sh = (FrameLayout) findViewById(R.id.content_frame_keyboard);
       
        sh.removeAllViews();
        keyView = View.inflate(sh.getContext(), R.layout.keyboard, null);
        sh.addView(keyView);
        ET = (EditText) sh.findViewById(R.id.keyboard_input);
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDb();
     
        // Set up the action bar.
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        
    }
    public void initDb(){
    	
    	hostDBKeys = new ArrayList<ArrayList<String>>();
        ArrayList<String> dataKey = new ArrayList<String>();
        dataKey.add("Alias");
        dataKey.add("TEXT");
        hostDBKeys.add(dataKey);
        dataKey = new ArrayList<String>();
        dataKey.add("Host");
        dataKey.add("TEXT");
        hostDBKeys.add(dataKey);
        dataKey = new ArrayList<String>();
        dataKey.add("Username");
        dataKey.add("TEXT");
        hostDBKeys.add(dataKey);
        dataKey = new ArrayList<String>();
        dataKey.add("Port");
        dataKey.add("TEXT");
        hostDBKeys.add(dataKey);
        dataKey = new ArrayList<String>();
        dataKey.add("Password");
        dataKey.add("TEXT");
        hostDBKeys.add(dataKey);
        
        
        ArrayList<ArrayList<String>> scriptDBKeys = new ArrayList<ArrayList<String>>();
        dataKey = new ArrayList<String>();
        dataKey.add("Alias");
        dataKey.add("TEXT");
        scriptDBKeys.add(dataKey);
        dataKey = new ArrayList<String>();
        dataKey.add("Script");
        dataKey.add("TEXT");
        scriptDBKeys.add(dataKey);
        
        db = new DatabaseHandler(this, hostDBKeys,scriptDBKeys);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        conDiscButton = menu.getItem(1);
        return true;
    	//super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem keys_checked = menu.getItem(2);
        keys_checked.setChecked(setting_keyboard_locked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	switch (item.getItemId()) {
	    	case R.id.action_settings:
	    		Intent settingsActivity = new Intent(MainActivity.this,SettingsActivity.class);
				MainActivity.this.startActivity(settingsActivity);
	    		break;
	    	case R.id.action_conn_disc:
	    		xMouseTryConnect();
	    		break;
	    	case R.id.action_restore_default_keys:
	    		KEYLOAYOUTFILENAME = "keyLayoutFile.csv";
	    		confirmLayoutReload("Restore default Keyboard?","Any unsaved buttons will be lost",true);
	    		break;
	    	case R.id.action_save_preset_one:
	    		KEYLOAYOUTFILENAME = "keyFileOne.csv";
	    		saveKeyboardLayout();
	    		break;
	    	case R.id.action_save_preset_two:
	    		KEYLOAYOUTFILENAME = "keyFileTwo.csv";
	    		saveKeyboardLayout();
	    		break;
	    	case R.id.action_save_preset_three:
	    		KEYLOAYOUTFILENAME = "keyFileThree.csv";
	    		saveKeyboardLayout();
	    		break;
	    	case R.id.action_load_preset_one:
	    		KEYLOAYOUTFILENAME = "keyFileOne.csv";
	    		confirmLayoutReload("Load Keyboard Layout 1","Any unsaved buttons will be lost",false);
	    		break;
	    	case R.id.action_load_preset_two:
	    		KEYLOAYOUTFILENAME = "keyFileTwo.csv";
	    		confirmLayoutReload("Load Keyboard Layout 2","Any unsaved buttons will be lost",false);
	    		break;
	    	case R.id.action_load_preset_three:
	    		KEYLOAYOUTFILENAME = "keyFileThree.csv";
	    		confirmLayoutReload("Load Keyboard Layout 3","Any unsaved buttons will be lost",false);
	    		break;
	    	case R.id.action_lock_keys:
	    		setting_keyboard_locked=!setting_keyboard_locked;
	    		item.setChecked(!item.isChecked());
	    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    		SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("keyboard_layout_locked", setting_keyboard_locked);
				editor.commit();
	    		break;
	    	case R.id.action_exit:
	    		
	    		//close all connections and finish.
	    		xMouseDisconnect();
	    		
	    		this.finish();
	    		break;
    		default:
    			break;
    	}
        return super.onOptionsItemSelected(item);
    }
    public void confirmLayoutReload(String title, String msg,final boolean def){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    builder.setTitle(title);
	    builder.setMessage(msg);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	mItems = loadKeyboardLayout(MainActivity.this,def);
	        	mItemAdapter = new ItemAdapter(MainActivity.this, mItems);
                mCoolDragAndDropGridView.setAdapter(mItemAdapter);
	        	mItemAdapter.notifyDataSetChanged();
	            
	        }
	    });
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	        }
	    });
	    AlertDialog dialog = builder.create();
	    dialog.show();
    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.hello
        mViewPager.setCurrentItem(tab.getPosition());
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //Log.d("XMouse tabs", String.valueOf(tab.getPosition()));
        switch(tab.getPosition()){
        	case 0:
        		//mViewPager.setSwipeable(false);
        		//imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);refreshLog
        		break;
        	case 1:
        		//mViewPager.setSwipeable(true);
        		break;
        	case 2:
        		//imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        		//mViewPager.setSwipeable(true);
        		if(cmdLogFragment!=null){
	        		//force reload of log fragment
	        		((CommandLogFragment) cmdLogFragment).refreshLog();
	        		//Log.d("refresh",String.valueOf(tab.getPosition()));
        		}
        		break;        
        }
    }
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    	
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    
    	/*if(tab.getPosition()!=1){//hide keyboard on mouse and activity log tabs
    	InputMethodManager inputManager = (InputMethodManager)            
    			  this.getSystemService(Context.INPUT_METHOD_SERVICE); 
    			    inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),      
    			    InputMethodManager.HIDE_NOT_ALWAYS);
    	}*/
	    switch(tab.getPosition()){
	    	case 0:
	    		//mViewPager.setSwipeable(false);
	    		//imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);refreshLog
	    		break;
	    	case 1:
	    		//mViewPager.setSwipeable(true);
	    		break;
	    	case 2:
	    		//imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	    		//mViewPager.setSwipeable(true);
	    		
	    		
	    		//force reload of log fragment
	    		if(cmdLogFragment!=null){
	    			((CommandLogFragment) cmdLogFragment).refreshLog();
	    		}
	    		//Log.d("refresh",String.valueOf(tab.getPosition()));
	    		break;        
		}
    }
    public void xMouseLeftClick(View v){
    	executeShellCommand("xdotool click 1");
    }
    public void xMouseRightClick(View v){
    	executeShellCommand("xdotool click 3");
    }
    public void xMouseMiddleClick(View v){
    	executeShellCommand("xdotool click 2");
    }
    public void xMouseTryConnect(){ //change connection state
    	if(session==null || !session.isConnected()){
    		
    		if(setting_user=="" || setting_host=="" || setting_pass==""){
    			
    			Toast.makeText(getBaseContext(), "A connection setting is blank", Toast.LENGTH_LONG).show();
    			return;
    		}
    		
    		SshConnectTask  t = (SshConnectTask) new SshConnectTask(	MainActivity.this,
    																	setting_user,
    																	setting_host,
    																	setting_pass,
    																	setting_port) {
	        	protected void onPostExecute(String result) {
	        		if (dialog.isShowing()) {
			            dialog.dismiss();
			        }
	        		if(result.isEmpty()){
	        			//addTextToTextView("SSH connection failed");
	        			
	        			Toast.makeText(getBaseContext(), "SSH connection failed, check settings and try again", Toast.LENGTH_LONG).show();
	        			
	        		}else{
	        			
	        			if(result.equals("true")){
	        				Toast.makeText(getBaseContext(), "SSH connection established", Toast.LENGTH_LONG).show();
		        			//import command to execute to porperly set the display window to be used
			        		//':0.0' is the default of the user currently logged in
		        			executeShellCommand(setting_xdotool_initial);
	        			}else{
	        				
	        				Toast.makeText(getBaseContext(), "Error: "+result, Toast.LENGTH_LONG).show();
	        			}
	        			//executeShellCommand("xdotool behave_screen_edge --delay 0 --quiesce 0 right mousemove_relative -- -10 0");
	        			//executeShellCommand("xdotool behave_screen_edge --delay 0 --quiesce 0 left mousemove_relative 10 0");
	        		}
	        		showConnectionStat();
	   	       }
	        };
	        //dt.setCont(mCont);
	        t.execute("");
		}else{
			xMouseDisconnect();
		}
    }
    public void showConnectionStat(){
    	if(conDiscButton != null){
    	if(session==null || !session.isConnected()){
    		conDiscButton.setIcon(R.drawable.state_disc);
    	}else{
    		conDiscButton.setIcon(R.drawable.state_conn);
    	}}
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	switch(position){
        		case 0:
        			
        			Fragment fragment = new MouseSectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(MouseSectionFragment.ARG_SECTION_NUMBER, position + 1);
                    fragment.setArguments(args);
                    
                    return fragment;
        		
        		case 1:
        			Fragment fragment2 = new KeyBoardSectionFragment();
                    Bundle args2 = new Bundle();
                    args2.putInt(KeyBoardSectionFragment.ARG_SECTION_NUMBER, position + 1);
                    fragment2.setArguments(args2);
                    return fragment2;
        		default:
        			
        			cmdLogFragment = new CommandLogFragment();
                    Bundle args3 = new Bundle();
                    args3.putInt(CommandLogFragment.ARG_SECTION_NUMBER, position + 1);
                    cmdLogFragment.setArguments(args3);
                    return cmdLogFragment;
        		
        	}
        	
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                
            }
            return null;
        }
    }
    public static class CommandLogFragment extends Fragment {
        
		/**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        private TextView lt;
        private ScrollView sv;
        //private Button clearBtn;
        //Handler mHandler = new Handler();
       
        public CommandLogFragment() {
        }
        //My watcher reading OutputStream from SSH to the textView
        /*private Runnable mWatcher = new Runnable() 
        {
            @Override
            public void run()
            {
            	lt.setText(log.toString());
            	if(setting_log_staydown){
            		sv.fullScroll(View.FOCUS_DOWN);
            	}
                //if (os!=null){
                //	clearBtn.setText("Clear "+os.size()+" bytes");
                //	lt.setText(os.toString());
                //	sv.fullScroll(View.FOCUS_DOWN);
               // }
                //Delay between each read of OutputStream
                mHandler.postDelayed(this, 500);
            }
        }; */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.cmdlog, container, false);
            
            View rootView = inflater.inflate(R.layout.slideholder_terminal, container, false);
            View contentView = inflater.inflate(R.layout.cmdlog, container, false);
            //RelativeLayout k = (RelativeLayout) rootView.findViewById(R.id.keyboard_full_layout);
            FrameLayout sh = (FrameLayout) rootView.findViewById(R.id.content_frame_terminal);
            sh.addView(contentView);
            
            SlidingPane SH = (SlidingPane) rootView.findViewById(R.id.slideHolder_terminal); 
            //SH.setParallaxDistance(100);
            //SH.setEnabled(false);
            SH.setCoveredFadeColor(Color.TRANSPARENT);
            //SH.setShadowResource(R.drawable.shadow);
            scriptList = (ListView) rootView.findViewById(R.id.drawerList); 
            
            ScriptListAdapter adapter = new ScriptListAdapter(getActivity());
            scriptList.setAdapter(adapter);
            
            lt = (TextView) contentView.findViewById(R.id.logText);
            sv = (ScrollView) contentView.findViewById(R.id.log_ScrollView);
            
            //refreshLog();
            //mHandler.post(mWatcher);
            return rootView;
        }
        public void refreshLog(){
        	
        	//log.append(log.length()).append("\n");
        	
        	 lt.setText(log.toString());
        	 sv.post(new Runnable()
	 	        {
	 	            public void run()
	 	            {
	 	            	sv.fullScroll(View.FOCUS_DOWN);
	 	            }
	 	        });
        }
        
    }
    public static class KeyBoardSectionFragment extends Fragment implements CoolDragAndDropGridView.DragAndDropListener, 
	SpanVariableGridView.OnItemClickListener,
	SpanVariableGridView.OnItemLongClickListener{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public KeyBoardSectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.slideholder_keyboard, container, false);
            
            keyView = inflater.inflate(R.layout.drag_and_drop, container, false);
            
            ScrollView scrollView = (ScrollView) keyView.findViewById(R.id.key_drag_scrollView);
            mCoolDragAndDropGridView = (CoolDragAndDropGridView) keyView.findViewById(R.id.key_drag_DragAndDropGridView);
            
            mItems = loadKeyboardLayout(getActivity(),false);//new LinkedList<Item>();
            /*for (int r = 0; r < 2; r++) {
            	mItems.add(new Item( 2, "Airport", "Heathrow"));
    			mItems.add(new Item( 1, "Bar", "Connaught Bar"));
            }*/
            mItemAdapter = new ItemAdapter(getActivity(), mItems);
            mCoolDragAndDropGridView.setAdapter(mItemAdapter);
            mCoolDragAndDropGridView.setScrollingStrategy(new SimpleScrollingStrategy(scrollView));
            mCoolDragAndDropGridView.setDragAndDropListener(this);
            mCoolDragAndDropGridView.setOnItemLongClickListener(this);
            //RelativeLayout k = (RelativeLayout) rootView.findViewById(R.id.keyboard_full_layout);
            FrameLayout sh = (FrameLayout) rootView.findViewById(R.id.content_frame_keyboard);
            sh.addView(keyView);
            ET = (EditText) keyView.findViewById(R.id.keyboard_input);
            
            ET.addTextChangedListener(new TextWatcher() {
			        @Override
					public void afterTextChanged(Editable s) {
			        	if(!setting_keyboard_batch){
							
			        		useKeyboardSendText();
							
						}
					}
					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					}
			}); 
            
            DeleteView = (ImageView)keyView.findViewById(R.id.deleteButton);
            DeleteView.setVisibility(View.GONE);
            ETLayout = (RelativeLayout)keyView.findViewById(R.id.keyboard_send_layout);
            SlidingPane SH = (SlidingPane) rootView.findViewById(R.id.slideHolder_keyboard); 
            //SH.setParallaxDistance(100);
            //SH.setEnabled(false);
            SH.setCoveredFadeColor(Color.TRANSPARENT);
            //SH.setShadowResource(R.drawable.shadow);
            ListView drawerList = (ListView) rootView.findViewById(R.id.drawerList); 
            
            ButtonListAdapter adapter = new ButtonListAdapter(getActivity());
            drawerList.setAdapter(adapter);
            /*chkIos = (CheckBox) rootView.findViewById(R.id.chkIos);
            
        	chkIos.setOnClickListener(new OnClickListener() {
         
        	  @Override
        	  public void onClick(View v) {
                        //is chkIos checked?
        		if (((CheckBox) v).isChecked()) {
        			//Toast.makeText(MyAndroidAppActivity.this, "Bro, try Android :)", Toast.LENGTH_LONG).show();
        			capsLock = true;
        		}else{
        			capsLock = false;
        		}
         
        	  }
        	});*/
           
            return rootView;
        }
        @Override
    	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        	if(!setting_keyboard_locked){
	    		mCoolDragAndDropGridView.startDragAndDrop();
	    		DeleteView.setVisibility(View.VISIBLE);
	    		ETLayout.setVisibility(View.GONE);
        	}
    		return false;
    	}

    	@Override
    	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

    	}

    	@Override
    	public void onDragItem(int from) {

    	}

    	@Override
    	public void onDraggingItem(int from, int to) {

    	}

    	@Override
    	public void onDropItem(int from, int to) {
    		if (from != to) {

    			mItems.add(to, mItems.remove(from));
    			mItemAdapter.notifyDataSetChanged();
    		}
    		potentialDeletePosition = to;
    		DeleteView.setVisibility(View.GONE);
    		ETLayout.setVisibility(View.VISIBLE);
    	}

    	@Override
    	public boolean isDragAndDropEnabled(int position) {
    		return true;
    	}

		@Override
		public void onDeleteItem(int mDragPosition) {
			//Log.d("onDeleteItem", ""+mDragPosition);
			mItems.remove(potentialDeletePosition);
			mItemAdapter.notifyDataSetChanged();
		}
    }
    
	@Override
	public void colorChanged(int color) {
		
	}
	public void xMouseKeyboardSend(View v){
		
		
		switch(v.getId()){
			case R.id.keyboard_send_f1:
				executeShellCommand("xdotool key F1");
				break;
			case R.id.keyboard_send_f2:
				executeShellCommand("xdotool key F2");
				break;
			case R.id.keyboard_send_f3:
				executeShellCommand("xdotool key F3");
			case R.id.keyboard_send_f4:
				executeShellCommand("xdotool key F4");
				break;
			case R.id.keyboard_send_f5:
				executeShellCommand("xdotool key F5");
				break;
			case R.id.keyboard_send_f6:
				executeShellCommand("xdotool key F6");
				break;
			case R.id.keyboard_send_f7:
				executeShellCommand("xdotool key F7");
				break;
			case R.id.keyboard_send_f8:
				executeShellCommand("xdotool key F8");
				break;
			case R.id.keyboard_send_f9:
				executeShellCommand("xdotool key F9");
				break;
			case R.id.keyboard_send_f10:
				executeShellCommand("xdotool key F10");
				break;
			case R.id.keyboard_send_f11:
				executeShellCommand("xdotool key F11");
				break;
			case R.id.keyboard_send_f12:
				executeShellCommand("xdotool key F12");
				break;
			case R.id.keyboard_send_up:
				executeShellCommand("xdotool key Up");
				break;
			case R.id.keyboard_send_left:
				executeShellCommand("xdotool key Left");
				break;
			case R.id.keyboard_send_right:
				executeShellCommand("xdotool key Right");
				break;
			case R.id.keyboard_send_down:
				executeShellCommand("xdotool key Down");
				break;
			case R.id.keyboard_send_esc:
				executeShellCommand("xdotool key Escape");
				break;
			case R.id.keyboard_send_backspace:
				executeShellCommand("xdotool key BackSpace");
				break;
			/*case R.id.keyboard_send_space:
				executeShellCommand("xdotool key space");
				break;*/
			case R.id.keyboard_send_enter:
				executeShellCommand("xdotool key Return");
				break;
			case R.id.keyboard_send:
				
				useKeyboardSendText();
				
				break;
			case R.id.keyboard_send_media_prev:
				executeShellCommand("xdotool key XF86AudioPrev");
				break;
			case R.id.keyboard_send_media_next:
				executeShellCommand("xdotool key XF86AudioNext");
				break;
			case R.id.keyboard_send_media_play:
				executeShellCommand("xdotool key XF86AudioPlay");
				break;
			case R.id.keyboard_send_media_pause:
				executeShellCommand("xdotool key XF86AudioPause");
				break;
			case R.id.keyboard_send_media_mute:
				executeShellCommand("xdotool key XF86AudioMute");
				break;
				
			case R.id.keyboard_send_back:
				executeShellCommand("xdotool key XF86Back");
				break;
			case R.id.keyboard_send_forward:
				executeShellCommand("xdotool key XF86Forward");
				break;
				
			case R.id.keyboard_send_altf4:
				executeShellCommand("xdotool key alt+F4");
				break;
			default: //qwerty keysthisforallmyhomies
				Button b = (Button)v;
				
				CharSequence c = b.getText();
				//c= translateKeystroke(c);
				Log.d("SpecialbuttonPress",String.valueOf(c));
				executeShellCommand("xdotool key "+c);
				break;
		}
	}
	private static void useKeyboardSendText() {
		CharSequence contents = ET.getText();
		String t = contents.toString();
		Log.d("type",String.valueOf(t));
		if(!t.isEmpty()){
			t = t.replaceAll("'", "'\\\"'\\\"'"); // -> xdotool type ''"'"''
			t = t.replaceAll("\"", "\\\"");       // -> xdotool type '"'
			
			executeShellCommand("xdotool type '" + t + "'");
			if(setting_keyboard_autoclear){
				ET.setText("");
			}
		}
	}
	public void xMouseIssueCustomCommand(View v){
		CustomButton b = (CustomButton)v;
		//String scriptId = b.getText().toString();
		//Log.d("xMouseIssueCustomCommand","customButton "+b.getId()+" "+b.getScript());
		switch(v.getId()){
			case R.id.scriptButton:
				
				String cmd = b.getScript();
				
				
				executeExecCommand(cmd);
					
				//Toast.makeText(getApplicationContext(), cmd, Toast.LENGTH_SHORT).show();
				
				break;
			case R.id.scriptButton_delete:
				
				final int id = b.getDatabaseId();
				
				//Building dialog
			    AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setTitle("Really remove script?");
			    builder.setMessage("This can't be undone");
			    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
			        	db.deleteRow(DatabaseHandler.SCRIPT_TABLE_NAME, id);
						refreshScriptList();
						Toast.makeText(getApplicationContext(), "Script deleted", Toast.LENGTH_SHORT).show();
			        }
			    });
			    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
			            dialog.dismiss();
			        }
			    });
			    AlertDialog dialog = builder.create();
			    dialog.show();
				
				
				break;
		}
	}
	public void refreshScriptList(){
		ScriptListAdapter adapter = new ScriptListAdapter(this);
        scriptList.setAdapter(adapter);
	}
	public void xMouseNewCustomButton(View v){
		//open dialog for new command
		
			//Preparing views
		    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		    View layout = inflater.inflate(R.layout.new_button_dialog, null);
		    //layout_root should be the name of the "top-level" layout node in the dialog_layout.xml file.
		    final EditText nameBox = (EditText) layout.findViewById(R.id.new_command_name);
		    final EditText script = (EditText) layout.findViewById(R.id.new_command_command);
		    final EditText spans = (EditText) layout.findViewById(R.id.new_command_width);
		    //Building dialog
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setView(layout);
		    builder.setTitle("New Button");
		    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		           
		            String name = nameBox.getText().toString();
		            String content = script.getText().toString();
		            String spanStr = spans.getText().toString();
		            
		           
		            if(!spanStr.isEmpty()){
		            
			            if(name.length()>0 && content.length()>0){
			            	int span = Integer.valueOf(spanStr);
			            	
			            	if(span>0 && span<11){
			                
				            	mItems.add(new CustomItem( 0,span, name, content));
				            	 
				            	
				            	mItemAdapter = new ItemAdapter(MainActivity.this, mItems);
				                mCoolDragAndDropGridView.setAdapter(mItemAdapter);
				            	mItemAdapter.notifyDataSetChanged();
				                
				                Toast.makeText(getBaseContext(), "Button \""+name+"\" saved to main layout", Toast.LENGTH_LONG).show();
				            	
				            	dialog.dismiss();
			            	}else{
			            		Toast.makeText(getBaseContext(), "Width must be between 1 and 10", Toast.LENGTH_LONG).show();
				            	
			            	}
			            }else{
			            	
			            	Toast.makeText(getBaseContext(), "A required value was empty, button not saved", Toast.LENGTH_LONG).show();
			            	
			            }
		            }else{
		            	Toast.makeText(getBaseContext(), "A required value was empty, button not saved", Toast.LENGTH_LONG).show();
		            }
		        }
		    });
		    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
		    AlertDialog dialog = builder.create();
		    dialog.show();
		
		
	}
	public void xMouseNewCommand(View v){
		//open dialog for new command
		
		//Preparing views
	    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
	    View layout = inflater.inflate(R.layout.new_command_dialog, null);
	    //layout_root should be the name of the "top-level" layout node in the dialog_layout.xml file.
	    final EditText nameBox = (EditText) layout.findViewById(R.id.new_command_name);
	    final EditText script = (EditText) layout.findViewById(R.id.new_command_command);

	    //Building dialog
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setView(layout);
	    builder.setTitle("New Command");
	    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	           
	            String name = nameBox.getText().toString();
	            String content = script.getText().toString();
	            if(name.length()>0 && content.length()>0){
	            	
	            	
	            	ArrayList<ArrayList<String>> newScript = new ArrayList<ArrayList<String>>();
					
					ArrayList<String> valArr = new ArrayList<String>();
					valArr.add("Alias");
					valArr.add(name);
					newScript.add(valArr);
	            	
					valArr = new ArrayList<String>();
					valArr.add("Script");
					valArr.add(content);
					newScript.add(valArr);
					
	            	db.addRow(DatabaseHandler.SCRIPT_TABLE_NAME,newScript );
	            	refreshScriptList();
	            	Toast.makeText(getBaseContext(), "Command \""+name+"\" saved", Toast.LENGTH_LONG).show();
	            	 dialog.dismiss();
	            }else{
	            	
	            	Toast.makeText(getBaseContext(), "A required value was empty, command not saved", Toast.LENGTH_LONG).show();
	            	
	            }
	            
	        }
	    });
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	        }
	    });
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}
	
	@Override
	public void OnXMouseMoved(float dx, float dy,boolean scroll) {
		
		dx=dx*setting_sensitivity;
		dy=dy*setting_sensitivity;
		String cmd="";
		if(dx <0 || dy <0){
			cmd="xdotool mousemove_relative -- "+dx+" "+dy;
			if(scroll){
				cmd="xdotool click 4";//mouse wheel
			}
			
		}else{
			cmd="xdotool mousemove_relative "+dx+" "+dy;
			if(scroll){
				cmd="xdotool click 5";//mouse wheel
			}
		}
		executeShellCommand(cmd);
	}

	@Override
	public void OnXMouseClicked(ClickType type) {
		
    	/*if(System.currentTimeMillis()-pTime<200){ //200 milliseconds minimum time for double click
    		
    		executeShellCommand("xdotool click --repeat 2 --delay 100 1")){ //doubleclick left mouse with 100 milisec delay
    			addTextToTextView("xdotool click --repeat 2 --delay 100 1");
    		}
    	}else{*/
    		
    		//executeShellCommand("xdotool click 1");
    	//}
    	//pTime=System.currentTimeMillis();
	    
		
		switch(type){
			case Standard:
				executeShellCommand("xdotool click 1");
				break;
			case Drag_Down:
				executeShellCommand("xdotool mousedown 1");
				break;
			case Drag_up:
				executeShellCommand("xdotool mouseup 1");
				break;
		default:
			break;
		}
		
	}
	/*
	public CharSequence translateKeystroke(CharSequence i){
		char p = i.charAt(0);
		int keyCode = p;
		Log.d("keystroke",String.valueOf(keyCode));
		switch(keyCode){
		
			case 32:
				i = "space";
				break;
			case 13:
			case 10: // carrage return '\r'
				i = "Return";
				break;
			case 47: // forward slash '/'
				i = "slash";
				break;
			case 92: // backward slash '\'
				i = "backslash";
				break;
			//case 33: // Exclamation '!'
				//i = "'!'";
				//break;
			case 58: //colon : 
				i = "colon";
				break;
			case 59: //semicolon ; 
				i = "semicolon";
				break;
			case 46: //perdiod .
				i = "period";
				break;
			case 45: //hypen -
				i = "minus";
				break;
			case 95: //underscore _
				i = "underscore";
				break;
			case 63: //question mark ?
				i = "question";
				break;
			//numbers 0-9	
			case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57:
				//do nothing?
				break;
			case 65: case 66: case 67: case 68: case 69: case 70: case 71: case 72: case 73: case 74:
			case 75: case 76: case 77: case 78: case 79: case 80: case 81: case 82: case 83: case 84:
			case 85: case 86: case 87: case 88: case 89: case 90: 
				//cap latters A-Z 65-90
			case 97: case 98: case 99: case 100: case 101: case 102: case 103: case 104: case 105: case 106:
			case 107: case 108: case 109: case 110: case 111: case 112: case 113: case 114: case 115: case 116:
			case 117: case 118: case 119: case 120: case 121: case 122: 
				//lettters a-z 97-122
				
				break;
			default:
				//if(capsLock){
				//	p = String.valueOf(p).toUpperCase().charAt(0);
				//}
				////i = ""+p;
				//i = "!";
				
				break;
		}
		return i;
	}
	*/
	/*
	 * public void xMouseClick(View v){
		switch(v.getId()){
		case R.id.mouse_send_scrollup:
			executeShellCommand("xdotool click 4");
			break;
		case R.id.mouse_send_scrolldown:
			executeShellCommand("xdotool click 5");
			break;
			
		}
	}
	*/
	public boolean executeExecCommand(final String cmd){
		
		
		if(session==null){
			return false;
		}
		if(session.isConnected()){
			//log.append(cmd);
			SshExecTask  t = (SshExecTask) new SshExecTask(MainActivity.this,cmd) {
				protected void onPostExecute(String result) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
					/*log.append(result);
					if(mViewPager.getCurrentItem()==2){
			        	actionBar.selectTab(actionBar.getTabAt(2)); //force refresh while user is looking at terminal page
			        }
			        
			        if(log.length()>10000){
		        		log.delete(0, log.indexOf("\n", 2000));
		        		
		        	}*/
					
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				    builder.setTitle(cmd);
				    builder.setMessage(result);
				    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				        @Override
				        public void onClick(DialogInterface dialog, int which) {
				        	
				        }
				    });
				    
				    AlertDialog dialog = builder.create();
				    dialog.show();
				    
				}
			};
			//dt.setCont(mCont);
			t.execute("");
			return true;
		}
		return false;
	}
	public static boolean executeShellCommand(String cmd){
		
		
		if(session==null){
			
			return false;
		}
		if(session.isConnected()){
			
				try {
					
					os = new ByteArrayOutputStream();
					channel.setOutputStream(os);
					shellStream = new PrintStream(channel.getOutputStream());// printStream for convenience 
					shellStream.println(cmd); 
			        shellStream.flush();
			        
			        //log.getChars(0, 0, dst, dstStart)
			        log.append(cmd).append("\n");
			        
			        
			        if(mViewPager.getCurrentItem()==2){
			        	actionBar.selectTab(actionBar.getTabAt(2)); //force refresh while user is looking at terminal page
			        }
			        
			        if(log.length()>10000){
		        		log.delete(0, log.indexOf("\n", 2000));
		        		
		        	}
			        
			        return true;
			        //return shellStream.checkError();
					
				/*} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();*/
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return false;
	}
	public void saveKeyboardLayout(){
		StringBuilder sb = new StringBuilder();
		for(int x=0;x<mItems.size();x++){
			sb.append(mItems.get(x).getmTitle())
				.append("<xmousesep>").append(mItems.get(x).getmCommand()).
				append("<xmousesep>").append(mItems.get(x).getmSpans());
			
			if(x!=mItems.size()-1){
				sb.append("\n");
			}
		}
		//Toast.makeText(getBaseContext(), sb.toString(), Toast.LENGTH_LONG).show();
		
		try{
			Log.d("saveKeyboardLayout",KEYLOAYOUTFILENAME);
			FileOutputStream fos = openFileOutput(KEYLOAYOUTFILENAME, Context.MODE_PRIVATE);
			fos.write(sb.toString().getBytes());
			fos.close();
			//Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_SHORT).show();
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public static LinkedList<CustomItem> loadKeyboardLayout(Context mCont,boolean def){
		
		if(def){
			return loadDefaultKeyboardLayout(mCont);
		}
		
		LinkedList<CustomItem> views = new LinkedList<CustomItem>();
		FileInputStream fis;
		Log.d("loadKeyboardLayout",KEYLOAYOUTFILENAME);
		try {
			fis = mCont.openFileInput(KEYLOAYOUTFILENAME);
			
			 //StringBuilder sb = new StringBuilder();
		        try{
		            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
		            String line = null;
		            while ((line = reader.readLine()) != null) {
		                //sb.append(line).append("\n");
		            	if(line.contains("<xmousesep>")){
			            	//parse line in to customItems
			            	String[] temp =line.split("<xmousesep>");
			            	views.add(new CustomItem( 0,Integer.valueOf(temp[2]), temp[0], temp[1]));
			            	Log.d("loadKeyboardLayout","using new <xmousesep> separator");
		            	}else{
		            		Log.d("loadKeyboardLayout","using old comma separator");
		            		String[] temp =line.split(",");
		            		views.add(new CustomItem( 0,Integer.valueOf(temp[2]), temp[0], temp[1]));
		            		
		            	}
		            }
		            fis.close();
		        } catch(OutOfMemoryError om){
		            om.printStackTrace();
		        } catch(Exception ex){
		            ex.printStackTrace();
		        }
		        //String result = sb.toString();
		        //Toast.makeText(mCont, result, Toast.LENGTH_LONG).show();
		        
		} catch (Exception e) {
			//Toast.makeText(mCont, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		if(views.size()<1){
		 views = loadDefaultKeyboardLayout(mCont);
		}
		
		return views;
	}
	public static LinkedList<CustomItem> loadDefaultKeyboardLayout(Context mCont){
		LinkedList<CustomItem> views = new LinkedList<CustomItem>();
		//10 cols per row
		views.add(new CustomItem( 0,2, "F1", "xdotool key F1"));
		views.add(new CustomItem( 0,2, "F2", "xdotool key F2"));
		views.add(new CustomItem( 0,2, "F3", "xdotool key F3"));
		views.add(new CustomItem( 0,2, "F4", "xdotool key F4"));
		views.add(new CustomItem( 0,2, "F5", "xdotool key F5"));
		
		views.add(new CustomItem( 0,2, "[Prev]", "xdotool key XF86AudioPrev"));
		views.add(new CustomItem( 0,3, "[Pause]", "xdotool key XF86AudioPause"));
		views.add(new CustomItem( 0,3, "[Play]", "xdotool key XF86AudioPlay"));
		views.add(new CustomItem( 0,2, "[Next]", "xdotool key XF86AudioNext"));
		
		views.add(new CustomItem( 0,3, "Esc", "xdotool key Escape"));
		views.add(new CustomItem( 0,3, "Alt|F4", "xdotool key alt+F4"));
		views.add(new CustomItem( 0,4, "Backspace", "xdotool key BackSpace"));
		
		views.add(new CustomItem( 0,5, "<Back", "xdotool key XF86Back"));
		views.add(new CustomItem( 0,5, "Forward>", "xdotool key XF86Forward"));
		
		views.add(new CustomItem( 0,3, "-Mute-", "xdotool key XF86AudioMute"));
		views.add(new CustomItem( 0,4, "Up", "xdotool key Up"));
		views.add(new CustomItem( 0,3, "Enter", "xdotool key Return"));
		
		views.add(new CustomItem( 0,3, "Left", "xdotool key Left"));
		views.add(new CustomItem( 0,4, "Down", "xdotool key Down"));
		views.add(new CustomItem( 0,3, "Right", "xdotool key Right"));
		
		return views;
	}
}
