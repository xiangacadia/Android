package net.youmi.sdkv4.sample;

import net.youmi.android.YoumiAdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.banner.AdViewLinstener;
import net.youmi.android.dev.AppUpdateInfo;
import net.youmi.android.spot.SpotManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        //初始化接口，应用启动的时候调用 
        //参数：appId, appSecret, 测试模式
        YoumiAdManager.getInstance(this).init("9a5999a6cafdccdc", "473b61f79ead45f3", false);
        // 广告条接口调用        
        // 将广告条adView添加到需要展示的layout控件中
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
        AdView adView = new AdView(this, AdSize.SIZE_320x50);
        adLayout.addView(adView);

        // 监听广告条接口
        adView.setAdListener(new AdViewLinstener() {
            
            @Override
            public void onSwitchedAd(AdView arg0) {
                Log.i("YoumiSample", "广告条切换");
            }
            
            @Override
            public void onReceivedAd(AdView arg0) {
                Log.i("YoumiSample", "请求广告成功");
                
            }
            
            @Override
            public void onFailedToReceivedAd(AdView arg0) {
                Log.i("YoumiSample", "请求广告失败");
                
            }
        });
        
        
        // 插播接口调用        
        // 预先加载插播广告的资源，请在应用启动后调用，将会
        
        SpotManager.getInstance(this).loadSpotAds();
        Button spotBtn = (Button) findViewById(R.id.showSpot);
        spotBtn.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                SpotManager.getInstance(MainActivity.this).showSpotAds(MainActivity.this);
                ////
                // 可以根据需要设置Theme，如下调用，如果无特殊需求，直接调用上方的接口即可
                // SpotManager.getInstance(MainActivity.this).showSpotAds(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                ////
            }
        });
        
        
        // 调用更新接口
        Button updateBtn = (Button) findViewById(R.id.updateApp);
        updateBtn.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                
                //检查应用是否有更新，如果没有返回null
                AppUpdateInfo appUpdateInfo = YoumiAdManager.getInstance(MainActivity.this).checkAppUpdate();
                if (appUpdateInfo==null) {
                    Toast.makeText(MainActivity.this, "当前版本已经是最新版", Toast.LENGTH_SHORT).show();
                }else {
                    //获取版本号
                    int versionCode = appUpdateInfo.getVersionCode();
                    //获取版本
                    String versionName = appUpdateInfo.getVersionName();
                    //获取新版本的信息
                    String updateTips = appUpdateInfo.getUpdateTips();
                    //获取apk下载地址
                    final String downloadUrl = appUpdateInfo.getUrl();
                    
                    AlertDialog updateDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setTitle("应用更新 "+versionName)
                                        .setMessage(updateTips)
                                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {                                    
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    Intent intent = Intent.parseUri(downloadUrl, Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);                                           
                                                } catch (Exception e) {
                                                    // TODO: handle exception
                                                }
                                            }
                                        })
                                        .setNegativeButton("下次再说", null)
                                        .create();
                    updateDialog.show();
                }
            }
        });

    }

}
