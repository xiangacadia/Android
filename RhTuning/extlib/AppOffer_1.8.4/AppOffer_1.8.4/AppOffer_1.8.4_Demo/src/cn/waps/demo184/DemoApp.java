package cn.waps.demo184;

import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.waps.AdInfo;
import cn.waps.AdView;
import cn.waps.AppConnect;
import cn.waps.MiniAdView;
import cn.waps.UpdatePointsNotifier;
import cn.waps.extend.AppDetail;
import cn.waps.extend.AppWall;
import cn.waps.extend.QuitPopAd;
import cn.waps.extend.SlideWall;

public class DemoApp extends Activity implements View.OnClickListener, UpdatePointsNotifier {

	private TextView pointsTextView;
	private TextView SDKVersionView;

	private String displayPointsText;
	private String currencyName = "金币";

	final Handler mHandler = new Handler();

	// 抽屉广告布局
	private View slidingDrawerView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// 初始化统计器，并通过代码设置WAPS_ID, WAPS_PID
		AppConnect.getInstance("09f277ca386ee99cb4c910e09f562112", "WAPS", this);
		
		// 初始化统计器，需要在AndroidManifest中注册WAPS_ID和WAPS_PID值
		// AppConnect.getInstance(this);
		// 以上两种统计器初始化方式任选其一，不要同时使用

		// 使用自定义的OffersWebView
		AppConnect.getInstance(this).setAdViewClassName("cn.waps.demo184.MyAdView");

		// 禁用错误报告
		AppConnect.getInstance(this).setCrashReport(false);

		Button offersButton = (Button) findViewById(R.id.OffersButton);
		Button gameOffersButton = (Button) findViewById(R.id.gameOffersButton);
		Button appOffersButton = (Button) findViewById(R.id.appOffersButton);
		Button moreAppsButton = (Button) findViewById(R.id.moreAppsButton);
		Button spendButton = (Button) findViewById(R.id.spendButton);
		Button feedbackButton = (Button) findViewById(R.id.feedbackButton);
		Button awardButton = (Button) findViewById(R.id.awardButton);
		Button diyAdButton = (Button) findViewById(R.id.diyAdButton);
		Button diyAdListButton = (Button) findViewById(R.id.diyAdListButton);
		Button popAdButton = (Button) findViewById(R.id.popAdButton);
		Button ownAppDetailButton = (Button) findViewById(R.id.ownAppDetailButton);
		Button checkUpdateButton = (Button) findViewById(R.id.checkUpdateButton);
		
		offersButton.setOnClickListener(this);
		gameOffersButton.setOnClickListener(this);
		appOffersButton.setOnClickListener(this);
		moreAppsButton.setOnClickListener(this);
		spendButton.setOnClickListener(this);
		feedbackButton.setOnClickListener(this);
		awardButton.setOnClickListener(this);
		diyAdButton.setOnClickListener(this);
		diyAdListButton.setOnClickListener(this);
		popAdButton.setOnClickListener(this);
		ownAppDetailButton.setOnClickListener(this);
		checkUpdateButton.setOnClickListener(this);

		pointsTextView = (TextView) findViewById(R.id.PointsTextView);
		SDKVersionView = (TextView) findViewById(R.id.SDKVersionView);
		
		// 初始化自定义广告数据
		AppConnect.getInstance(this).initAdInfo();
		
		// 初始化插屏广告数据
		AppConnect.getInstance(this).initPopAd(this);
		
		// 带有默认参数值的在线配置，使用此方法，程序第一次启动使用的是"defaultValue"，之后再启动则是使用的服务器端返回的参数值
		String showAd = AppConnect.getInstance(this).getConfig("showAd", "defaultValue");
		
		SDKVersionView.setText("在线参数:showAd = "+showAd);
		
		SDKVersionView.setText(SDKVersionView.getText()+"\nSDK版本: " + AppConnect.LIBRARY_VERSION_NUMBER);
		
		// 互动广告调用方式
		LinearLayout container = (LinearLayout) findViewById(R.id.AdLinearLayout);
		new AdView(this, container).DisplayAd();
		
		// 迷你广告调用方式
		// AppConnect.getInstance(this).setAdBackColor(Color.argb(50, 120, 240, 120));//设置迷你广告背景颜色
		// AppConnect.getInstance(this).setAdForeColor(Color.YELLOW);//设置迷你广告文字颜色
		LinearLayout miniLayout = (LinearLayout) findViewById(R.id.miniAdLinearLayout);
		new MiniAdView(this, miniLayout).DisplayAd(10);// 10秒刷新一次
		
		// 抽屉式应用墙
		// 1,将drawable-hdpi文件夹中的图片全部拷贝到新工程的drawable-hdpi文件夹中
		// 2,将layout文件夹中的detail.xml和slidewall.xml两个文件，拷贝到新工程的layout文件夹中
		// 获取抽屉样式的自定义广告
    	slidingDrawerView = SlideWall.getInstance().getView(this);
    	// 获取抽屉样式的自定义广告,自定义handle距左边边距为150
    	// slidingDrawerView = SlideWall.getInstance().getView(this, 150);
    	// 获取抽屉样式的自定义广告,自定义列表中每个Item的宽度480,高度150
    	// slidingDrawerView = SlideWall.getInstance().getView(this, 480, 150);
    	// 获取抽屉样式的自定义广告,自定义handle距左边边距为150,列表中每个Item的宽度480,高度150
    	// slidingDrawerView = SlideWall.getInstance().getView(this, 150, 480, 150);
    	
    	if(slidingDrawerView != null){
    		this.addContentView(slidingDrawerView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    	}
    	
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(SlideWall.getInstance().slideWallDrawer != null
					&& SlideWall.getInstance().slideWallDrawer.isOpened()){
				
				// 如果抽屉式应用墙展示中，则关闭抽屉
				SlideWall.getInstance().closeSlidingDrawer();
			}else{
				// 调用退屏广告
				QuitPopAd.getInstance().show(this);
			}
			
		}
		return true;
	}
	
	//建议加入onConfigurationChanged回调方法
	//注:如果当前Activity没有设置android:configChanges属性,或者是固定横屏或竖屏模式,则不需要加入
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// 横竖屏状态切换时,关闭处于打开状态中的退屏广告
		QuitPopAd.getInstance().close();
		// 使用抽屉式应用墙,横竖屏状态切换时,重新加载抽屉,保证ListView重新加载,保证ListView中Item的布局匹配当前屏幕状态
		if(slidingDrawerView != null){
			// 先remove掉slidingDrawerView
			((ViewGroup)slidingDrawerView.getParent()).removeView(slidingDrawerView);
			slidingDrawerView = null;
			// 重新获取抽屉样式布局,此时ListView重新设置了Adapter
			slidingDrawerView = SlideWall.getInstance().getView(this);
			if(slidingDrawerView != null){
				this.addContentView(slidingDrawerView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			}
		}
		super.onConfigurationChanged(newConfig);
	}
	

	/**
	 * 用于监听插屏广告的显示与关闭
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Dialog dialog = AppConnect.getInstance(this).getPopAdDialog();
		if(dialog != null){
			if(dialog.isShowing()){
				// 插屏广告正在显示
			}
			dialog.setOnCancelListener(new OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog) {
					// 监听插屏广告关闭事件
				}
			});
		}
	}

	public void onClick(View v) {
		if (v instanceof Button) {
			int id = ((Button) v).getId();

			switch (id) {
			case R.id.OffersButton:
				//显示推荐列表（综合）
				AppConnect.getInstance(this).showOffers(this);
				break;
			case R.id.popAdButton:
				//显示插屏广告
				// AppConnect.getInstance(this).showPopAd(this);
				//根据指定的theme样式展示插屏广告，theme主要为系统样式id
				AppConnect.getInstance(this).showPopAd(this, android.R.style.Theme_Translucent_NoTitleBar);
				break;
			case R.id.appOffersButton:
				//显示推荐列表（软件）
				AppConnect.getInstance(this).showAppOffers(this);
				break;
			case R.id.gameOffersButton:
				//显示推荐列表（游戏）
				AppConnect.getInstance(this).showGameOffers(this);
				break;
			case R.id.diyAdListButton:
				//获取全部自定义广告数据
				Intent appWallIntent = new Intent(this, AppWall.class);
				this.startActivity(appWallIntent);
				break;
			case R.id.diyAdButton:
				//获取一条自定义广告数据
				AdInfo adInfo = AppConnect.getInstance(DemoApp.this).getAdInfo();
				AppDetail.getInstanct().showAdDetail(DemoApp.this,adInfo);
				break;
			case R.id.spendButton:
				//消费虚拟货币.
				AppConnect.getInstance(this).spendPoints(10, this);
				break;
			case R.id.awardButton:
				//奖励虚拟货币
				AppConnect.getInstance(this).awardPoints(10, this);
				break;
			case R.id.moreAppsButton:
				//显示自家应用列表
				AppConnect.getInstance(this).showMore(this);
				break;
			case R.id.ownAppDetailButton:
				//根据指定的应用app_id展示其详情
				AppConnect.getInstance(this).showMore(this, "c8c3dab81e65e695020e69a74ccff196");
				break;
			case R.id.checkUpdateButton:
				//手动检查新版本
				AppConnect.getInstance(this).checkUpdate(this);
				break;
			case R.id.feedbackButton:
				//用户反馈
				AppConnect.getInstance(this).showFeedback();
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		// 从服务器端获取当前用户的虚拟货币.
		// 返回结果在回调函数getUpdatePoints(...)中处理
		AppConnect.getInstance(this).getPoints(this);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		AppConnect.getInstance(this).finalize();
		super.onDestroy();
	}

	// 创建一个线程
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (pointsTextView != null) {
				pointsTextView.setText(displayPointsText);
			}
		}
	};

	/**
	 * AppConnect.getPoints()方法的实现，必须实现
	 * 
	 * @param currencyName
	 *            虚拟货币名称.
	 * @param pointTotal
	 *            虚拟货币余额.
	 */
	public void getUpdatePoints(String currencyName, int pointTotal) {
		this.currencyName = currencyName;
		displayPointsText = currencyName + ": " + pointTotal;
		mHandler.post(mUpdateResults);
	}

	/**
	 * AppConnect.getPoints() 方法的实现，必须实现
	 * 
	 * @param error
	 *            请求失败的错误信息
	 */
	public void getUpdatePointsFailed(String error) {
		displayPointsText = error;
		mHandler.post(mUpdateResults);
	}
	
}