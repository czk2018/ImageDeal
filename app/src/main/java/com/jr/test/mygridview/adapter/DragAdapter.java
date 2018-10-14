package com.jr.test.mygridview.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jr.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DragAdapter extends BaseAdapter {
	/** TAG */
	private final static String TAG = "DragAdapter";
	/** 是否显示底部的ITEM */
	private boolean isItemShow = false;
	private Context context;
	/** 控制的postion */
	private int holdPosition;
	/** 是否改变 */
	private boolean isChanged = false;
	/** 是否可见 */
	boolean isVisible = true;
	/** 是否编辑状态 */
	boolean isEdiState = false;
	/** 可以拖动的列表（即用户选择的频道列表） */
	// public List<ChannelItem> channelList;
	/** TextView 频道内容 */
	private TextView item_text;
	/** 要删除的position */
	public int remove_position = -1;
	ArrayList<HashMap<String, String>> list;
	private ImageLoader imageLoader;
	ImageView iv;
	ImageButton delete;// 删除按钮

	public DragAdapter(Context context, ArrayList<HashMap<String, String>> list2) {
		this.context = context;
		this.list = list2;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list == null ? 0 : list.size();
	}

	@Override
	public HashMap<String, String> getItem(int position) {
		// TODO Auto-generated method stub
		if (list != null && list.size() != 0) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.subscribe_category_item, null);
		delete = (ImageButton) view.findViewById(R.id.delete);
		iv = (ImageView) view.findViewById(R.id.iv);
		item_text = (TextView) view.findViewById(R.id.text_item);
		HashMap map = getItem(position);
		item_text.setText((String) map.get("name"));
		if (isEdiState) {
			delete.setVisibility(View.VISIBLE);

			if (isChanged && (position == holdPosition) && !isItemShow) {
				// item_text.setText("" + (String) map.get("name"));
				item_text.setText("");
				iv.setVisibility(View.INVISIBLE);
				delete.setVisibility(View.INVISIBLE);
				item_text.setSelected(true);
				item_text.setEnabled(true);
				isChanged = false;

			}
			if (!isVisible && (position == -1 + list.size())) {
				item_text.setText("");
				delete.setVisibility(View.INVISIBLE);
				iv.setVisibility(View.INVISIBLE);
				item_text.setSelected(true);
				item_text.setEnabled(true);
			}
			if (remove_position == position) {
				item_text.setText("");
				delete.setVisibility(View.INVISIBLE);
			}
		} else {
			delete.setVisibility(View.INVISIBLE);
		}
		imageLoader.displayImage((String) map.get("url"), iv);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				list.remove(position);
				notifyDataSetChanged();
				Toast.makeText(context, "删除", Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}

	/** 拖动变更频道排序 */
	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		HashMap dragItem = getItem(dragPostion);

		if (dragPostion < dropPostion) {
			list.add(dropPostion + 1, dragItem);
			list.remove(dragPostion);
		} else {
			list.add(dropPostion, dragItem);
			list.remove(dragPostion + 1);
		}
		isChanged = true;
		notifyDataSetChanged();
	}
	

	/** 显示放下的ITEM */
	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}

	public void setEdit(boolean b) {
		// TODO Auto-generated method stub
		isEdiState = b;
		notifyDataSetChanged();
	}
	

    private static class ViewHolder
    {
        TextView name;
        ImageView iv;
        ImageButton delete;
    }
	
	
}