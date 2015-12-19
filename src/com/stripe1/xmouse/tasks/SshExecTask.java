package com.stripe1.xmouse.tasks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.stripe1.xmouse.MainActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class SshExecTask extends AsyncTask<String, String, String> {
	
	String cmd;
	public SshExecTask(Activity a, String cmd){
		this.a=a;
		this.cmd=cmd;
		dialog = new ProgressDialog(a);
    }
    private Activity a;
    /** progress dialog to show user that the backup is processing. */
    protected ProgressDialog dialog;
	protected String doInBackground(String... params) {
		StringBuilder log = new StringBuilder();
		try {
			
			Channel channel = MainActivity.session.openChannel("exec");
			((ChannelExec)channel).setCommand(cmd);
			 
		      // X Forwarding
		      channel.setXForwarding(true);
		 
		      //channel.setInputStream(System.in);
		      channel.setInputStream(null);
		 
		      //channel.setOutputStream(System.out);
		 
		      //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
		      //((ChannelExec)channel).setErrStream(fos);
		      ((ChannelExec)channel).setErrStream(System.err);
		 
		      InputStream in=channel.getInputStream();
		 
		      channel.connect();
		      
		      byte[] tmp=new byte[1024];
		      while(true){
		        while(in.available()>0){
		          int i=in.read(tmp, 0, 1024);
		          if(i<0)break;
		          //System.out.print(new String(tmp, 0, i));
		          log.append((new String(tmp, 0, i)));
		        }
		        if(channel.isClosed()){
		          //System.out.println("exit-status: "+channel.getExitStatus());
		          break;
		        }
		        try{Thread.sleep(250);}catch(Exception ee){}
		      }
		      channel.disconnect();
		      //session.disconnect();
		      
		      
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return log.toString();
    }
	@Override
	protected void onProgressUpdate(String... text) {
		//finalResult.setText(text[0]);
		// Things to be done while execution of long running operation is in
		// progress. For example updating ProgessDialog
	}
	@Override
	protected void onPreExecute() {
        this.dialog.setMessage("Executing: "+cmd);
        this.dialog.show();
    }
}
