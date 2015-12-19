package com.stripe1.xmouse.tasks;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.stripe1.xmouse.MainActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SshConnectTask extends AsyncTask<String, String, String> {
	
	private String mUser = "";
	private String mHost ="";
	private int mPort =22;
	private String mPass ="";
	private String xhost = "127.0.0.1";
	private int xport = 0;
	public SshConnectTask(Activity a,String user, String host, String pass,int port){
		this.a=a;
		this.mUser=user;
		this.mHost=host;
		this.mPass=pass;
		this.mPort=port;
		
		dialog = new ProgressDialog(a);
    }
    private Activity a;
    /** progress dialog to show user that the backup is processing. */
    protected ProgressDialog dialog;
	protected String doInBackground(String... params) {
		try{
			MainActivity.session=MainActivity.jsch.getSession(mUser, mHost, mPort);
			
			if(MainActivity.setting_use_keys){
				if(!MainActivity.setting_key_passphrase.isEmpty()){
					MainActivity.jsch.addIdentity(MainActivity.setting_key_filename,MainActivity.setting_key_passphrase);
					Log.d("SshConnectTask","attempt to add identify WITH passphrase");
				}
				MainActivity.jsch.addIdentity(MainActivity.setting_key_filename);
				
						
			}else{
				MainActivity.session.setPassword(mPass);
				Log.d("SshConnectTask","attempt password auth");
			}
			MainActivity.session.setX11Host(xhost);
			MainActivity.session.setX11Port(xport+6000);
			MainActivity.session.setConfig("StrictHostKeyChecking", "no");
			MainActivity.session.connect(30000);   // making a connection with timeout.
			  
			MainActivity.channel=MainActivity.session.openChannel("shell");
			MainActivity.channel.setXForwarding(true);
			MainActivity.channel.connect();
			
			MainActivity.os = new ByteArrayOutputStream();
			MainActivity.channel.setOutputStream(MainActivity.os);
			//MainActivity.channel.setOutputStream(System.out); 
			MainActivity.shellStream = new PrintStream(MainActivity.channel.getOutputStream());  // printStream for convenience 
			
		      
		}catch(Exception e){
			//Toast.makeText(a, e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return e.getMessage();//.toString();
	    }
		if(MainActivity.session.isConnected()){
			return String.valueOf(MainActivity.session.isConnected());
		}else{
			return "Not connected, no exception thrown.";
		}
    }
	@Override
	protected void onProgressUpdate(String... text) {
		//finalResult.setText(text[0]);
		// Things to be done while execution of long running operation is in
		// progress. For example updating ProgessDialog
	}
	@Override
	protected void onPreExecute() {
        this.dialog.setMessage("Connecting to "+mUser+"@"+mHost+":"+mPort);
        this.dialog.show();
    }
}
