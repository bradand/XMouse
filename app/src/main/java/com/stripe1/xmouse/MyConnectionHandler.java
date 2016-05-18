package com.stripe1.xmouse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;


public class MyConnectionHandler {


    public JSch jsch=new JSch();
    public Session session =null;
    public Channel channel=null;
    public PrintStream shellStream;
    public ByteArrayOutputStream os = null;

    private Activity myActivity;

    private MyInterface myInterface;

    public MyConnectionHandler(Activity a, MyInterface b){

        myInterface = b;
        myActivity = a;
    }

    public void xMouseTryConnect(){ //change connection state
        if(session==null || !session.isConnected()){

            if(MainActivity.setting_user=="" || MainActivity.setting_host=="" || String.valueOf(MainActivity.setting_port)==""){

                Toast.makeText(myActivity, "A connection setting is blank", Toast.LENGTH_LONG).show();
                return;
            }

            SshConnectTask  t = (SshConnectTask) new SshConnectTask(	myActivity,
                    MainActivity.setting_user,
                    MainActivity.setting_host,
                    MainActivity.setting_pass,
                    MainActivity.setting_port) {
                protected void onPostExecute(String result) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if(result.isEmpty()){
                        //addTextToTextView("SSH connection failed");

                        Toast.makeText(myActivity, "Connection failed, check settings and try again", Toast.LENGTH_LONG).show();

                    }else{

                        if(result.equals("true")){
                            Toast.makeText(myActivity, "Connection established", Toast.LENGTH_LONG).show();
                            //import command to execute to porperly set the display window to be used
                            //':0.0' is the default of the user currently logged in
                            executeShellCommand(MainActivity.setting_xdotool_initial);
                        }else{

                            Toast.makeText(myActivity, "Error: "+result, Toast.LENGTH_LONG).show();
                        }
                        //executeShellCommand("xdotool behave_screen_edge --delay 0 --quiesce 0 right mousemove_relative -- -10 0");
                        //executeShellCommand("xdotool behave_screen_edge --delay 0 --quiesce 0 left mousemove_relative 10 0");
                    }
                    //MyCallback.callbackCall();
                    //showConnectionStat();
                    myInterface.performCallback();//refresh connection icon
                }
            };
            //dt.setCont(mCont);
            t.execute("");
        }else{
            xMouseDisconnect();
        }
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
        //showConnectionStat();
        //MyCallback.callbackCall();
        myInterface.performCallback();//refresh connection icon
    }

    public boolean executeExecCommand(final String cmd){


        if(session==null || cmd == null || cmd.length()==0){
            return false;
        }
        if(session.isConnected()){
            //log.append(cmd);
            SshExecTask  t = (SshExecTask) new SshExecTask(myActivity,cmd) {
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);
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
    public boolean executeShellCommand(String cmd){

        MainActivity.recentCmdTextView.setText(cmd);

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

                /*//log.getChars(0, 0, dst, dstStart)
                log.append(cmd).append("\n");


                if(mViewPager.getCurrentItem()==2){
                    actionBar.selectTab(actionBar.getTabAt(2)); //force refresh while user is looking at terminal page
                }

                if(log.length()>10000){
                    log.delete(0, log.indexOf("\n", 2000));

                }
                */
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
    public class SshConnectTask extends AsyncTask<String, String, String> {

        private String mUser = "";
        private String mHost ="";
        private int mPort =22;
        private String mPass ="";
        private String xhost = "127.0.0.1";
        private int xport = 0;
        private Activity a;

        public SshConnectTask(Activity a,String user, String host, String pass,int port){
            this.a=a;
            this.mUser=user;
            this.mHost=host;
            this.mPass=pass;
            this.mPort=port;

            dialog = new ProgressDialog(a);
        }

        /** progress dialog to show user that the backup is processing. */
        protected ProgressDialog dialog;
        protected String doInBackground(String... params) {
            try{
                session= jsch.getSession(mUser, mHost, mPort);

                if(MainActivity.setting_use_keys){
                    if(!MainActivity.setting_key_passphrase.isEmpty()){
                        jsch.addIdentity(MainActivity.setting_key_filename, MainActivity.setting_key_passphrase);
                        Log.d("SshConnectTask", "attempt to add identity WITH passphrase");
                    }
                    Log.d("SshConnectTask", "attempt to add identity WITHOUT passphrase");
                    jsch.addIdentity(MainActivity.setting_key_filename);


                }else{
                    session.setConfig("PreferredAuthentications", "password,keyboard-interactive");
                    session.setPassword(mPass);
                    Log.d("SshConnectTask", "attempt password auth");
                }
                session.setX11Host(xhost);
                session.setX11Port(xport + 6000);


                session.setConfig("StrictHostKeyChecking", "no");


                session.connect(30000);   // making a connection with timeout.

                channel= session.openChannel("shell");
                channel.setXForwarding(true);
                channel.connect();

                os = new ByteArrayOutputStream();
                channel.setOutputStream(os);
                //channel.setOutputStream(System.out);
                shellStream = new PrintStream(channel.getOutputStream());  // printStream for convenience


            }catch(Exception e){
                //Toast.makeText(a, e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return e.getMessage();//.toString();
            }
            if(session.isConnected()){
                return String.valueOf(session.isConnected());
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

                Channel channel = session.openChannel("exec");
                ((ChannelExec)channel).setCommand(cmd);

                // X Forwarding
                channel.setXForwarding(true);

                //channel.setInputStream(System.in);
                channel.setInputStream(null);

                //channel.setOutputStream(System.out);
                //StringOutputStream outputCollector = new StringOutputStream();
                //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
                //((ChannelExec)channel).setErrStream(fos);
                //((ChannelExec) channel).//.setErrStream(System.err);

                BufferedReader r = new BufferedReader(new InputStreamReader(((ChannelExec) channel).getErrStream()));

                //InputStream in=channel.getInputStream();
                BufferedReader r2 = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.connect();

                /*byte[] tmp=new byte[1024];
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
                    try{
                        Thread.sleep(250);}catch(Exception ee){}
                }*/

                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                while ((line = r2.readLine()) != null) {
                    total.append(line);
                }
                channel.disconnect();
                //session.disconnect();
                log.append(total.toString());

            /*} catch (JSchException e) {

                e.printStackTrace();
                log.append(e.getMessage());


            } catch (IOException e) {

                e.printStackTrace();
                log.append(e.getMessage());*/

            } catch (Exception e){
                e.printStackTrace();
                log.append(e.getMessage());
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

                Channel channel = session.openChannel("exec");
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
                        Log.d("doInBackground", "done");
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


        int checkAck(InputStream in) throws IOException {
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
                    Log.e("checkAck", sb.toString());
                }
                if(b==2){ // fatal error
                    Log.e("checkAck", sb.toString());
                }
            }
            return b;
        }
    }
}
