package com.youmi.android.sample.offers;

import net.youmi.android.AdManager;
import net.youmi.android.dev.AppUpdateInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

/**
 * 检查更新的工具类
 * @author youmi
 *
 */
public class UpdateHelper extends AsyncTask<Void, Void, AppUpdateInfo> {

	private Context mContext;
	private AppUpdateInfo mAppUpdateInfo;

	public UpdateHelper(Context context) {
		mContext = context;
	}

	@Override
	protected AppUpdateInfo doInBackground(Void... params) {
		// TODO Auto-generated method stub

		try {

			//调用该接口获取检查更新信息
			return AdManager.getInstance(mContext).checkAppUpdate();

		} catch (Throwable e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(AppUpdateInfo result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		try {

			if (result == null || result.getUrl() == null) {
				return;
			}

			mAppUpdateInfo = result;

			new AlertDialog.Builder(mContext)
					.setTitle("发现新版本")
					.setMessage(result.getUpdateTips())
					.setNegativeButton("马上升级",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											Intent.ACTION_VIEW, Uri
													.parse(mAppUpdateInfo
															.getUrl()));
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									mContext.startActivity(intent);

								}
							})
					.setPositiveButton("下次再说",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							}).create().show();

		} catch (Throwable e) {
			// TODO: handle throwable
			e.printStackTrace();
		}
	}
}
