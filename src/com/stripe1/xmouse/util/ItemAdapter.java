package com.stripe1.xmouse.util;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe1.xmouse.MainActivity;
import com.stripe1.xmouse.R;
import com.stripe1.xmouse.R.id;
import com.stripe1.xmouse.R.layout;
import com.stripe1.xmouse.util.SpanVariableGridView.LayoutParams;

public class ItemAdapter extends ArrayAdapter<CustomItem> implements SpanVariableGridView.CalculateChildrenPosition {

	private final class ItemViewHolder {

		public TextView itemTitle;
		public TextView itemCommand;
		//public ImageView itemIcon;

	}

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	private View.OnClickListener onRemoveItemListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {

			Integer position = (Integer) view.getTag();
			removeItem(getItem(position));

		}
	};

	public void insertItem(CustomItem item, int where) {

		if (where < 0 || where > (getCount() - 1)) {

			return;
		}

		insert(item, where);
	}

	public boolean removeItem(CustomItem item) {

		remove(item);

		return true;
	}

	public ItemAdapter(Context context, List<CustomItem> plugins) {

		super(context, R.layout.item, plugins);

		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ItemViewHolder itemViewHolder;

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item, parent, false);
			
			itemViewHolder = new ItemViewHolder();
			itemViewHolder.itemTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
			itemViewHolder.itemCommand = (TextView) convertView.findViewById(R.id.MarqueeText);
			//itemViewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
			convertView.setTag(itemViewHolder);

		} else {

			itemViewHolder = (ItemViewHolder) convertView.getTag();
		}

		final CustomItem item = getItem(position);
		
		SpanVariableGridView.LayoutParams lp = new LayoutParams(convertView.getLayoutParams());
		lp.span = item.getmSpans();
		convertView.setLayoutParams(lp);
		
		itemViewHolder.itemTitle.setText(item.getmTitle());
		if(MainActivity.setting_keyboard_show_details){
			itemViewHolder.itemCommand.setText(item.getmCommand());
			itemViewHolder.itemCommand.setSelected(true);
		}
		//itemViewHolder.itemIcon.setImageResource(item.getIcon());
		
		convertView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				MainActivity.executeShellCommand(item.getmCommand());
				//Toast.makeText(mContext, item.getmCommand(), Toast.LENGTH_SHORT).show();
			}
			
			
		});
		
		RelativeLayout layoutHolder = (RelativeLayout) convertView.findViewById(R.id.textViewHolderLayout);
		try{
			layoutHolder.setBackgroundColor(Color.parseColor(item.getmColor()));
		}catch(Exception e){
			e.printStackTrace();
		}
		return convertView;
	}

	@Override
	public void onCalculatePosition(View view, int position, int row, int column) {

	}
}
