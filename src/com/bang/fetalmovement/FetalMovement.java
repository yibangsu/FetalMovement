package com.bang.fetalmovement;

import java.util.Calendar;

import com.bang.fetalmovement.untils.FetalMovementDatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.net.NetworkScoreManager;

public class FetalMovement extends Activity {
	
	// UI view
	private TextView mTotalText;
	private TextView mMaxText;
	private TextView mAvailText;
	private TextView mTimerText;
	
	private ImageView mProgressImg;
	
	// logic var
	private int mAvailCount = 0;
	private int mMaxCount = 0;
	private int mRecordMax = 0;
	private int mTotalCount = 0;
	private boolean bStart = false;
	
	private final int mSecondsInHour = 3600;
	private final long mMillisInFiveMins = 300000;
	private final long TIMER_PERIOD = 100;
	private int mCurTick = 0;
	
	private long mStart = -1;
	private long mCur;
	private Calendar cStartTime = Calendar.getInstance();
	private Calendar cStopTime = Calendar.getInstance();
	
	private final int EVENT_TICK = 100;
	
	private FetalMovementDatabaseHelper mDatabaseHelper;
	
	private Vibrator vibrator;
	
	private PowerManager.WakeLock wakeLock;
	private PowerManager mPowerManager;
	
	private final String TAG = "FetalMovement";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		
		setContentView(R.layout.fetal_movement_layout);
		
		//NetworkScoreManager nsm = (NetworkScoreManager) getSystemService(Context.NETWORK_SCORE_SERVICE);
		//nsm.setActiveScorer("com.bang.fetalmovement");
		
		mTotalText = (TextView) findViewById(R.id.total_count);
		mMaxText = (TextView) findViewById(R.id.max_count);
		mAvailText = (TextView) findViewById(R.id.avail_count);
		mTimerText = (TextView) findViewById(R.id.timer_text);
		
		mProgressImg = (ImageView) findViewById(R.id.image);
		
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
		wakeLock.acquire();
		
		init();
		
		freshUi();
		
		mDatabaseHelper = FetalMovementDatabaseHelper.getInstance(this);
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what) {
			case EVENT_TICK:
				if (bStart) {
					long now = System.currentTimeMillis();
					mCurTick = (int) ((now - cStartTime.getTimeInMillis())/1000);
					if (mCurTick < mSecondsInHour) {
						mHandler.sendEmptyMessageDelayed(EVENT_TICK, TIMER_PERIOD);
					} else { 
						store();
						init();
					}
					freshUi();
				}
				break;
			}
		}
		
	};
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!bStart) {
			init();
			bStart = true;
			startTimer();
			cStartTime.setTimeInMillis(System.currentTimeMillis());
		} else {
			mCur = System.currentTimeMillis();
			
			if (mStart < 0 || mCur - mStart > mMillisInFiveMins) { // first time or five minutes
				mStart = mCur;
				mAvailCount ++;
				mMaxCount = 0;
			} else {
				toastFiveMinInfo();
			}
			
			if (vibrator != null) {
				vibrator.vibrate(500);
			} else {
				Log.d("fetal", "no vibrator");
			}
			
			mMaxCount ++;
			if (mRecordMax < mMaxCount) mRecordMax = mMaxCount;
			
			mTotalCount ++;
			freshUi();
		}
	}
	
	private void freshUi() {
		mTotalText.setText(""+mTotalCount);
		mMaxText.setText(""+mRecordMax);
		mAvailText.setText(""+mAvailCount);
		
		int mTickLeft = mSecondsInHour - mCurTick;
		String str = String.format("%02d:%02d", mTickLeft/60, mTickLeft%60);
		mTimerText.setText(str);
		
		if (mCurTick%10 == 0) {
			mProgressImg.setRotation(mCurTick/10);
		}
	}
	
	private void store() {
		cStopTime.setTimeInMillis(System.currentTimeMillis());
		String dateStr = String.format("%04d.%02d.%02d %02d:%02d:%02d\r\n~%04d.%02d.%02d %02d:%02d:%02d"
				,cStartTime.get(Calendar.YEAR) ,cStartTime.get(Calendar.MONTH) ,cStartTime.get(Calendar.DAY_OF_MONTH)
				,cStartTime.get(Calendar.HOUR_OF_DAY) ,cStartTime.get(Calendar.MINUTE) ,cStartTime.get(Calendar.SECOND)
				,cStopTime.get(Calendar.YEAR) ,cStopTime.get(Calendar.MONTH) ,cStopTime.get(Calendar.DAY_OF_MONTH)
				,cStopTime.get(Calendar.HOUR_OF_DAY) ,cStopTime.get(Calendar.MINUTE) ,cStopTime.get(Calendar.SECOND));
		mDatabaseHelper.insert(dateStr, mAvailCount, mTotalCount, mRecordMax);
	}
	
	private void init() {
		mAvailCount = 0;
		mMaxCount = 0;
		mRecordMax = 0;
		mTotalCount = 0;
		bStart = false;
		mCurTick = 0;
		mStart = -1;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch(keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			popAlart();
			break;
		case KeyEvent.KEYCODE_MENU:
			break;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void popAlart() {
		if (bStart) {
			Dialog alertDialog = new AlertDialog.Builder(this)
			.setMessage(R.string.commit_to_stop)
			.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					store();
					init();
					freshUi();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			}).create();
		alertDialog.show();
		} else {
			Dialog alertDialog = new AlertDialog.Builder(this)
				.setMessage(R.string.commit_to_exit)
				.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).create();
			alertDialog.show();
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.xml.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case R.id.helpMenu:
			this.startActivity(new Intent(FetalMovement.this, FetalMovementHelp.class));
			break;
		case R.id.historyMenu:
			this.startActivity(new Intent(FetalMovement.this, FetalMovementHistory.class));
			break;
		case R.id.exitMenu:
			popAlart();
			break;
		
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void startTimer() {
		mHandler.sendEmptyMessage(EVENT_TICK);
	}
	
	private void toastFiveMinInfo() {
		Toast.makeText(this, getText(R.string.five_min), Toast.LENGTH_SHORT).show();
	}
	
	public void keepScreenOn(boolean on) {
		if (wakeLock != null) {
			if (on) {
				wakeLock.acquire();
			} else {
				wakeLock.release();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (wakeLock != null) {
			wakeLock.release();
		}
	}
	
}
