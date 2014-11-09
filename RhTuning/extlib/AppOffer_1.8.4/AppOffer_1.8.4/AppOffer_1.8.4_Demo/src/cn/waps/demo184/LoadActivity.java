package cn.waps.demo184;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import cn.waps.AppConnect;
import cn.waps.extend.LoadingPopAd;


public class LoadActivity extends Activity {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	//设置界面跳转时间为5秒
	int time = 5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		AppConnect.getInstance("09f277ca386ee99cb4c910e09f562112", "waps", this);
        
        // 使用自定义的OffersWebView
		AppConnect.getInstance(this).setAdViewClassName(this.getPackageName() + ".MyAdView");
        
        // 初始化自定义广告数据
    	AppConnect.getInstance(this).initAdInfo();
    	// 初始化插屏广告数据
    	AppConnect.getInstance(this).initPopAd(this);
    	
    	// 开屏广告
    	View loadingAdView = LoadingPopAd.getInstance().getContentView(this, time);
    	if(loadingAdView != null){
    		// 将开屏广告的布局View设置到当前Activity的整体布局中
    		this.setContentView(loadingAdView);
    	}
    	
    	//设置延时跳转到主界面
    	scheduler.schedule(new Runnable(){
			@Override
			public void run() {
				Intent intent = new Intent(LoadActivity.this, DemoApp.class);
				LoadActivity.this.startActivity(intent);
				LoadActivity.this.finish();
				AppConnect.getInstance(LoadActivity.this).finalize(); 
			}
    	}, time, TimeUnit.SECONDS);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
		}
		return true;
	}
}
