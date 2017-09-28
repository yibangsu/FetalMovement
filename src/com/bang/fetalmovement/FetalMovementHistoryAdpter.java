package com.bang.fetalmovement;

import java.util.ArrayList;
import java.util.List;

import com.bang.fetalmovement.untils.HistoryItem;
import com.bang.fetalmovement.untils.HistoryItemView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FetalMovementHistoryAdpter extends BaseAdapter {
	
	private ArrayList<HistoryItem> mList = new ArrayList<HistoryItem>();
	private LayoutInflater layoutInflater;
	private Context context;
	
	public FetalMovementHistoryAdpter(Context context,ArrayList<HistoryItem> data){
		this.context=context;
		this.mList=data;
		this.layoutInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HistoryItemView itemView=null;
		if(convertView==null){
			itemView=new HistoryItemView();
			//获得组件，实例化组件
			convertView=layoutInflater.inflate(R.layout.fetal_movement_history_item, null);
			itemView.dateView = (TextView) convertView.findViewById(R.id.history_date);
			itemView.availView = (TextView) convertView.findViewById(R.id.history_avail);
			itemView.totalView = (TextView) convertView.findViewById(R.id.history_total);
			itemView.maxView = (TextView) convertView.findViewById(R.id.history_max);
			convertView.setTag(itemView);
		}else{
			itemView=(HistoryItemView)convertView.getTag();
		}
		// 绑定数据
		itemView.dateView.setText(mList.get(position).date);
		itemView.availView.setText(mList.get(position).avail+"");
		itemView.totalView.setText(mList.get(position).total+"");
		itemView.maxView.setText(mList.get(position).max+"");
		return convertView;
	}


}
