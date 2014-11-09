package com.youmi.android.sample.offers;

import net.youmi.android.offers.EarnPointsOrderList;
import net.youmi.android.offers.PointsReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyPointsReceiver extends PointsReceiver {

	@Override
	protected void onEarnPoints(Context context, EarnPointsOrderList list) {
		// TODO Auto-generated method stub
		Log.d("test", "接收到赚取积分的订单");
		// 如果您使用自定义积分账户，那EarnPointsOrderList将会对您特别有用，可以在这里进行订单处理
	}

	@Override
	protected void onViewPoints(Context context) {
		// TODO Auto-generated method stub
		// 用户点击了积分赚取订单通知
		// 这里演示点击后打开到首页
		Intent intent = new Intent(context, MyPointsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}
