package com.stripe1.xmouse.tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.stripe1.xmouse.MainActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SshScpTask extends AsyncTask<String, String, String> {
	
	String cmd;
	public SshScpTask(Activity a,String W,String H){
		this.a=a;
		//this.cmd="sizeX="+W+";sizeY="+H+";X=`xdotool getmouselocation|sed 's/x:\\(.*\\) y:\\(.*\\) screen:.*/\\1/'`;Y=`xdotool getmouselocation|sed 's/x:\\(.*\\) y:\\(.\\) screen:.*/\2/'`;X=$((X-sizeX/2));Y=$((Y-sizeY/2));import -window root -crop `echo $sizeX`x`echo $sizeY`+$X+$Y test.jpg;scp -f test.jpg";
		this.cmd="scp -f test.jpg";
		
    }
    private Activity a;
    
	protected String doInBackground(String... params) {
		StringBuilder log = new StringBuilder();
		try {
			String lfile = "mouse_bg.jpg";
			/*String prefix=null;
			  if(new File(lfile).isDirectory()){
			    prefix=lfile+File.separator;
			  }*/
			
			Channel channel = MainActivity.session.openChannel("exec");
			((ChannelExec)channel).setCommand(cmd);
			 
			 
			// get I/O streams for remote scp
			OutputStream out=channel.getOutputStream();
			InputStream in=channel.getInputStream();
			channel.connect();
			 
			byte[] buf=new byte[1024];
 
			  // send '\0'
			  buf[0]=0; out.write(buf, 0, 1); out.flush();
		 
			  while(true){
			  int c=checkAck(in);
				if(c!='C'){
				  break;
				}
		 
		        // read '0644 '
		        in.read(buf, 0, 5);
		 
		        long filesize=0L;
		        while(true){
					          if(in.read(buf, 0, 1)<0){
					            // error
						    break; 
						  }
						  if(buf[0]==' ')break;
						  filesize=filesize*10L+(long)(buf[0]-'0');
			        }
			 
			        String file=null;
			        for(int i=0;;i++){
			          in.read(buf, i, 1);
			          if(buf[i]==(byte)0x0a){
			            file=new String(buf, 0, i);
			            break;
			  	  }
			        }
		 
			//System.out.println("filesize="+filesize+", file="+file);
		 
		        // send '\0'
		        buf[0]=0; out.write(buf, 0, 1); out.flush();
		 
		        
		        // read a content of lfile
			//FileOutputStream fos=new FileOutputStream(prefix==null ? lfile : prefix+file);
		    FileOutputStream fos = a.openFileOutput(lfile, Context.MODE_PRIVATE);
		    int foo;
		    while(true){
		      if(buf.length<filesize) foo=buf.length;
		      else foo=(int)filesize;
			      foo=in.read(buf, 0, foo);
			      if(foo<0){
			        // error 
			            break;
			          }
			          fos.write(buf, 0, foo);
			          filesize-=foo;
			          if(filesize==0L) break;
			        }
			        fos.close();
			        fos=null;
		 
			if(checkAck(in)!=0){
			  Log.d("doInBackground","done");
			}
		 
		        // send '\0'
		    buf[0]=0; out.write(buf, 0, 1); out.flush();
		  
		  } 
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return log.toString();
    }
	
	
	static int checkAck(InputStream in) throws IOException{
	    int b=in.read();
	    // b may be 0 for success,
	    //          1 for error,
	    //          2 for fatal error,
	    //          -1
	    if(b==0) return b;
	    if(b==-1) return b;
	 
	    if(b==1 || b==2){
	      StringBuffer sb=new StringBuffer();
	      int c;
	      do {
		c=in.read();
		sb.append((char)c);
	      }
	      while(c!='\n');
	      if(b==1){ // error
		Log.e("checkAck",sb.toString());
	      }
	      if(b==2){ // fatal error
	    	  Log.e("checkAck",sb.toString());
	      }
	    }
	    return b;
	  }
}
