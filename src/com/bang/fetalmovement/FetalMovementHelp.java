package com.bang.fetalmovement;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class FetalMovementHelp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.fetal_movement_help);
	}
}
