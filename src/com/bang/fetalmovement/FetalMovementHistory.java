package com.bang.fetalmovement;

import java.util.ArrayList;

import com.bang.fetalmovement.untils.FetalMovementDatabaseHelper;
import com.bang.fetalmovement.untils.HistoryItem;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

public class FetalMovementHistory extends Activity {
	private ListView mHistoryListView;
	private ArrayList<HistoryItem> mHistoryList = new ArrayList<HistoryItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.fetal_movement_history);
		mHistoryListView = (ListView) findViewById(R.id.history_list);
		getHostoryData();
		FetalMovementHistoryAdpter mAdapter = new FetalMovementHistoryAdpter(this, mHistoryList);
		mHistoryListView.setAdapter(mAdapter);
	}
	
	private void getHostoryData() {
		FetalMovementDatabaseHelper mDatabaseHelper = FetalMovementDatabaseHelper.getInstance(this);
		mHistoryList = mDatabaseHelper.getAll();
	}
}
