package com.stripe1.xmouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class HostListItemAdapter extends ArrayAdapter<HostItem> {

    private MyInterface myInterface;
    ListEditActivity.ActivityType type;
    private final class HostItemViewHolder {

        public TextView alias;
        public TextView desc;
        public TextView dbId;


    }
    private LayoutInflater mLayoutInflater = null;
    private Context mContext;

    public HostListItemAdapter(Context context, List<HostItem> plugins, MyInterface a, ListEditActivity.ActivityType t) {
        super(context, R.layout.host_list_item, plugins);
        myInterface = a;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        type= t;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final HostItemViewHolder itemViewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.host_list_item, parent, false);

            itemViewHolder = new HostItemViewHolder();

            itemViewHolder.alias = (TextView) convertView.findViewById(R.id.hostAlias);
            itemViewHolder.desc = (TextView) convertView.findViewById(R.id.hostDesc);
            itemViewHolder.dbId = (TextView) convertView.findViewById(R.id.hostDBID);

            convertView.setTag(itemViewHolder);

        } else {

            itemViewHolder = (HostItemViewHolder) convertView.getTag();
        }

        final HostItem item = getItem(position);

        //SpanVariableGridView.LayoutParams lp = new SpanVariableGridView.LayoutParams(convertView.getLayoutParams());
        //lp.span = item.getmSpans();
        //convertView.setLayoutParams(lp);

        itemViewHolder.alias.setText(item.getAlias());
        if(type== ListEditActivity.ActivityType.type_host) {
            itemViewHolder.desc.setText(item.getUsername() + "@" + item.getIP() + ":" + item.getPort());
            itemViewHolder.dbId.setText(String.valueOf(item.getDbId()));
        }else if(type== ListEditActivity.ActivityType.type_script){
            itemViewHolder.desc.setText(item.getIP());
            itemViewHolder.dbId.setText(item.getUsername());
        }

        /*if(MainActivity.setting_keyboard_show_details){
            itemViewHolder.itemCommand.setText(item.getmCommand());
            itemViewHolder.itemCommand.setSelected(true);
        }*/


        /*final RelativeLayout layoutHolder = (RelativeLayout) convertView.findViewById(R.id.textViewHolderLayout);
        try{
            layoutHolder.setBackgroundColor(Color.parseColor(item.getmColor()));
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(mContext, "Error parsing new layout: " + e.getMessage() + " " + item.getmColor(), Toast.LENGTH_SHORT).show();
        }*/


        convertView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //MainActivity.conn.executeShellCommand(item.getmCommand());

                //myInterface.performCallback2(item.getDbId());
                myInterface.performCallback2(position);

                //Toast.makeText(mContext, "do edit or delete", Toast.LENGTH_SHORT).show();
            }


        });


        return convertView;
    }
}
