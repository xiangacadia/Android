package com.youmi.android.sample.offers;

import net.youmi.android.offers.PointsManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MyPointsActivity extends Activity implements OnClickListener {

	private TextView mTextViewMyPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mypoints);

		mTextViewMyPoints = (TextView) findViewById(R.id.textView_mypoints);

		findViewById(R.id.button_exit).setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showPoints();
	}

	private void showPoints() {
		mTextViewMyPoints.setText("积分:"
				+ PointsManager.getInstance(this).queryPoints());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}

}
