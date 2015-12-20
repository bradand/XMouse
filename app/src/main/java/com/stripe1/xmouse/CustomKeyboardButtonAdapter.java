package com.stripe1.xmouse;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe1.xmouse.SpanVariableGridView.LayoutParams;

import java.util.List;

public class CustomKeyboardButtonAdapter extends ArrayAdapter<CustomKeyboardButton> implements SpanVariableGridView.CalculateChildrenPosition {

	private final class ItemViewHolder {

		public TextView itemTitle;
		//public TextView itemCommand;
		//public ImageView itemIcon;

	}

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	/*private View.OnClickListener onRemoveItemListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {

			Integer position = (Integer) view.getTag();
			removeItem(getItem(position));

		}
	};

	public void insertItem(CustomKeyboardButton item, int where) {

		if (where < 0 || where > (getCount() - 1)) {

			return;
		}

		insert(item, where);
	}

	public boolean removeItem(CustomKeyboardButton item) {

		remove(item);

		return true;
	}*/

	public CustomKeyboardButtonAdapter(Context context, List<CustomKeyboardButton> plugins) {

		super(context, R.layout.keyboard_button, plugins);

		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ItemViewHolder itemViewHolder;

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.keyboard_button, parent, false);

			itemViewHolder = new ItemViewHolder();
			itemViewHolder.itemTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
			//itemViewHolder.itemCommand = (TextView) convertView.findViewById(R.id.MarqueeText);
			//itemViewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
			convertView.setTag(itemViewHolder);

		} else {

			itemViewHolder = (ItemViewHolder) convertView.getTag();
		}

		final CustomKeyboardButton item = getItem(position);

		SpanVariableGridView.LayoutParams lp = new LayoutParams(convertView.getLayoutParams());
		lp.span = item.getmSpans();
		convertView.setLayoutParams(lp);

		itemViewHolder.itemTitle.setText(item.getmTitle());
		//if(MainActivity.setting_keyboard_show_details){
		//	itemViewHolder.itemCommand.setText(item.getmCommand());
		//	itemViewHolder.itemCommand.setSelected(true);
		//}


        final RelativeLayout layoutHolder = (RelativeLayout) convertView.findViewById(R.id.textViewHolderLayout);
        try{
            layoutHolder.setBackgroundColor(Color.parseColor(item.getmColor()));
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(mContext, "Error parsing new layout: " + e.getMessage() + " " + item.getmColor(), Toast.LENGTH_SHORT).show();
        }

		//itemViewHolder.itemIcon.setImageResource(keyboard_button.getIcon());
        /*convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //String ac = "";
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        //ac="down";
                        layoutHolder.setBackgroundColor(Color.parseColor("#00BFFF"));//lightblue
                        break;
                    case MotionEvent.ACTION_UP:
                        //ac="up";
                        try{
                            layoutHolder.setBackgroundColor(Color.parseColor(item.getmColor()));
                        }catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(mContext, "Error parsing new layout: " + e.getMessage() + " " + item.getmColor(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
                //MainActivity.recentCmdTextView.setText(ac);
                return false;//false=do not consume click event

            }
        });*/

		convertView.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				MainActivity.conn.executeShellCommand(item.getmCommand());
				//Toast.makeText(mContext, keyboard_button.getmCommand(), Toast.LENGTH_SHORT).show();
			}


		});


		return convertView;
	}

	@Override
	public void onCalculatePosition(View view, int position, int row, int column) {

	}
}
