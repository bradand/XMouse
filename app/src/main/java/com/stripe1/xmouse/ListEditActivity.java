package com.stripe1.xmouse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListEditActivity extends AppCompatActivity implements MyInterface {

    private static ListView mList;
    private static List<HostItem> mHostItems = new LinkedList<HostItem>();

    public enum ActivityType {


        type_host,
        type_script
    }

    private ActivityType type;
    private static HostListItemAdapter adapter;
    private static int selectedHostIndex=0;
    //CharSequence[] hostsNames;
    ////CharSequence[] hostsValues;
    //ListPreference listPrefHost;
    //ListPreference listPrefKeys;
    private static LayoutInflater layoutInflater;
    private int activityType; //0=hosts, 1=scripts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layoutInflater = getLayoutInflater();

        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("intVariableType", 0);


        if(intValue==0){ //hosts
            type = ActivityType.type_host;
            setTitle("Manage Hosts");
        }else if(intValue ==1){ //scripts
            type=ActivityType.type_script;
            setTitle("Manage Scripts");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG) .setAction("Action", null).show();
                xMouseManage(null);
            }
        });

        mList = (ListView) findViewById(R.id.myListView);
        if(MainActivity.db!=null) {
            loadCurrentHostList();
        }

    }
    public void loadCurrentHostList(){
        mHostItems = new LinkedList<HostItem>();
        ArrayList<ArrayList<String>> hosts = new ArrayList<ArrayList<String>>();

        if(type==ActivityType.type_host) {
            hosts = MainActivity.db.listAll(DatabaseHandler.HOST_TABLE_NAME, new String[]{"Alias", "Host", "Username", "Port", "Password", "id"});
        }else if(type == ActivityType.type_script) {
            hosts = MainActivity.db.listAll(DatabaseHandler.SCRIPT_TABLE_NAME, new String[]{"Alias", "Script", "id"});
        }

        if (hosts.size() > 0) {

            for (int i = hosts.size()-1; i>=0;i--) {
                //Log.d("ListEditActivity", "found host " + i);
                HostItem thisItem = new HostItem();
                thisItem.setAlias(hosts.get(i).get(0)); //alias for host and script
                thisItem.setIP(hosts.get(i).get(1)); //ip for host, content for script
                thisItem.setUsername(hosts.get(i).get(2)); //username for host, dbid for script

                if(type==ActivityType.type_host) {

                    thisItem.setPassword(hosts.get(i).get(4));
                    thisItem.setPort(hosts.get(i).get(3));
                    thisItem.setDbId(Integer.valueOf(hosts.get(i).get(5)));
                }
                mHostItems.add(thisItem);
            }
        }
        adapter = new HostListItemAdapter(this, mHostItems,this,type);
        mList.setAdapter(adapter);
    }
    public void xMouseManage(final Integer pos){
        //open dialog for new command

        //Preparing views
        //LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.manage_host_item, null);
        //layout_root should be the name of the "top-level" layout node in the dialog_layout.xml file.
        int nameId = R.id.new_host_alias;
        int hostId = R.id.new_host_ip;
        int userId = R.id.new_host_username;
        int portId = R.id.new_host_port;
        int passId = R.id.new_host_password;


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(layout);
        if(type==ActivityType.type_script) {
            layout = layoutInflater.inflate(R.layout.manage_script_item, null);
            builder.setView(layout);
            nameId = R.id.new_script_alias;
            hostId = R.id.new_script_command;
        }

        final EditText nameBox = (EditText) layout.findViewById(nameId);
        final EditText hostBox = (EditText) layout.findViewById(hostId);
        final EditText userBox = (EditText) layout.findViewById(userId);
        final EditText portBox = (EditText) layout.findViewById(portId);
        final EditText passwordBox = (EditText) layout.findViewById(passId);

        //Building dialog

        if(pos != null) {

            if(type==ActivityType.type_host) {
                builder.setTitle("Edit Host");
            }else if(type == ActivityType.type_script) {
                builder.setTitle("Edit Script");
            }

            nameBox.setText(mHostItems.get(pos).getAlias());
            hostBox.setText(mHostItems.get(pos).getIP());

            if(type==ActivityType.type_host) {
                userBox.setText(mHostItems.get(pos).getUsername());
                portBox.setText(mHostItems.get(pos).getPort());
                passwordBox.setText(mHostItems.get(pos).getPassword());
            }

        }else{

            if(type==ActivityType.type_host) {
                builder.setTitle("New Host");
            }else if(type == ActivityType.type_script) {
                builder.setTitle("New Script");
            }
        }
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name = nameBox.getText().toString();
                String host = hostBox.getText().toString();

                if(pos!=null){

                    mHostItems.get(pos).setAlias(name);
                    mHostItems.get(pos).setIP(host);

                    if(type==ActivityType.type_host) {
                        mHostItems.get(pos).setUsername(userBox.getText().toString());
                        mHostItems.get(pos).setPort(portBox.getText().toString());
                        mHostItems.get(pos).setPassword(passwordBox.getText().toString());

                        String[] updateValues = new String[] {name,
                                host,
                                userBox.getText().toString(),
                                portBox.getText().toString(),
                                passwordBox.getText().toString()};

                        MainActivity.db.updateRow(DatabaseHandler.HOST_TABLE_NAME,
                                new String[]{"Alias", "Host", "Username", "Port", "Password"},
                                updateValues,String.valueOf(mHostItems.get(pos).getDbId()));

                    }else if(type==ActivityType.type_script){


                        String[] updateValues = new String[] {name, host};

                        MainActivity.db.updateRow(DatabaseHandler.SCRIPT_TABLE_NAME,
                                new String[]{"Alias", "Script"},
                                updateValues,mHostItems.get(pos).getUsername()); //username holds value of dbid in case of script

                    }

                }else {
                    HostItem newHostItem = new HostItem();

                    newHostItem.setAlias(name);
                    newHostItem.setIP(host);

                    if(type==ActivityType.type_host) {
                        newHostItem.setUsername(userBox.getText().toString());
                        newHostItem.setPort(portBox.getText().toString());
                        newHostItem.setPassword(passwordBox.getText().toString());

                        ArrayList<ArrayList<String>> newHost = new ArrayList<ArrayList<String>>();

                        ArrayList<String> valArr = new ArrayList<String>();
                        valArr.add(MainActivity.hostDBKeys.get(0).get(0));
                        valArr.add(name);
                        newHost.add(valArr);

                        valArr = new ArrayList<String>();
                        valArr.add(MainActivity.hostDBKeys.get(1).get(0));
                        valArr.add(host);
                        newHost.add(valArr);

                        valArr = new ArrayList<String>();
                        valArr.add(MainActivity.hostDBKeys.get(2).get(0));
                        valArr.add(userBox.getText().toString());
                        newHost.add(valArr);

                        valArr = new ArrayList<String>();
                        valArr.add(MainActivity.hostDBKeys.get(3).get(0));
                        valArr.add(portBox.getText().toString());
                        newHost.add(valArr);

                        valArr = new ArrayList<String>();
                        valArr.add(MainActivity.hostDBKeys.get(4).get(0));
                        valArr.add(passwordBox.getText().toString());
                        newHost.add(valArr);

                        MainActivity.db.addRow(DatabaseHandler.HOST_TABLE_NAME,newHost);


                    }else if(type== ActivityType.type_script){
                        ArrayList<ArrayList<String>> newHost = new ArrayList<ArrayList<String>>();

                        ArrayList<String> valArr = new ArrayList<String>();
                        valArr.add("Alias");
                        valArr.add(name);
                        newHost.add(valArr);

                        valArr = new ArrayList<String>();
                        valArr.add("Script");
                        valArr.add(host);
                        newHost.add(valArr);

                        MainActivity.db.addRow(DatabaseHandler.SCRIPT_TABLE_NAME,newHost);
                    }

                    mHostItems.add(newHostItem);
                }

                adapter.notifyDataSetChanged();

                //Toast.makeText(getBaseContext(), "Host saved", Toast.LENGTH_LONG).show();
                loadCurrentHostList();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(pos==null){

                    //do nothing with temp item
                }else {
                    if (type == ActivityType.type_host) {

                        MainActivity.db.deleteRow(DatabaseHandler.HOST_TABLE_NAME, mHostItems.get(pos).getDbId());

                    } else if (type == ActivityType.type_script) {

                        MainActivity.db.deleteRow(DatabaseHandler.SCRIPT_TABLE_NAME, Integer.valueOf(mHostItems.get(pos).getUsername()));
                    }


                    adapter.remove(mHostItems.get(pos));
                    mHostItems.remove(pos);
                    adapter.notifyDataSetChanged();
                    //adapter=new HostListItemAdapter(getBaseContext(),mHostItems,ListEditActivity.this);
                    //mList.setAdapter(adapter);


                    //Toast.makeText(getBaseContext(), "Host removed "+pos, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();


            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    public void performCallback() {

    }

    @Override
    public void performCallback2(Integer i) {
        xMouseManage(i);
    }
}
