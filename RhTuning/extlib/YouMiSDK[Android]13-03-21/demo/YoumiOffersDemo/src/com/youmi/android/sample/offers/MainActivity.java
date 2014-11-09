package com.youmi.android.sample.offers;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements PointsChangeNotify,
		OnClickListener {

	private TextView mTextViewPointsBalance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();

		// 请务必在应用程序的主Activity的onCreate中调用初始化接口
		AdManager.getInstance(this).init("6cb91d796d022782",
				"34fb70c1f650d6ba", false);
		// 请务必在应用程序的主Activity的onCreate中调用积分墙启动接口
		OffersManager.getInstance(this).onAppLaunch();

		// 监听积分余额变动，必须在onCreate中注册监听
		PointsManager.getInstance(this).registerNotify(this);

		//检查更新
		new UpdateHelper(this).execute();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 请务必在应用程序的主Activity的onDestroy中调用积分墙退出接口
		OffersManager.getInstance(this).onAppExit();

		// 如果有监听积分余额变动，请在onDestroy中调用注销接口
		PointsManager.getInstance(this).unRegisterNotify(this);
	}

	@Override
	public void onPointBalanceChange(int pointsBalance) {
		// TODO Auto-generated method stub
		// 积分余额变动的时候，这里会收到通知
		// 直接将余额设置到demo的TextView中显示
		mTextViewPointsBalance.setText("积分:" + pointsBalance);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_show_offers:
			// 显示积分墙
			OffersManager.getInstance(this).showOffersWall();
			break;
		case R.id.button_award_5_points:
			// 奖励10积分示例
			PointsManager.getInstance(this).awardPoints(5);
			break;
		case R.id.button_spend_10_points:
			// 消耗10积分示例
			if (!PointsManager.getInstance(this).spendPoints(10)) {
				Toast.makeText(this, "消耗积分失败,余额不足", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	private void initViews() {
		//
		findViewById(R.id.button_show_offers).setOnClickListener(this);
		findViewById(R.id.button_award_5_points).setOnClickListener(this);
		findViewById(R.id.button_spend_10_points).setOnClickListener(this);
		//
		mTextViewPointsBalance = (TextView) findViewById(R.id.textView_points);
		mTextViewPointsBalance.setText("积分:"
				+ PointsManager.getInstance(this).queryPoints());
	}
}
