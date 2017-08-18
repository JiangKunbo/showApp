package com.readboy.showappdemo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ReadboyActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.loveplusplus.update.UpdateChecker;
import com.readboy.showappdemo.data.AsmApp;
import com.readboy.showappdemo.util.Log;


/**
 * ASM APP Activity。
 * 
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01在Android平台上重写了此代码
 */
public class AsmAppActivity extends ReadboyActivity
{
	//private final String TAG = getClass().getSimpleName();	// TAG
	private final String TAG = "AsmAppActivity";				// TAG
	
	private AsmApp mAsm;										// 表示ASM相关数据和操作的类。

	@Override
	protected boolean onInit()
	{
		Log.i(TAG, "[-----------------------------onCreate-----------------------------]");
		
		/*LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderCallbacks<Cursor>()
		{
			@Override
			public Loader<Cursor> onCreateLoader(int id, Bundle args)
			{
				return null;
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor data)
			{
				
			}

			@Override
			public void onLoaderReset(Loader<Cursor> loader)
			{
				
			}
		};
		LoaderManager mLoaderManager = getLoaderManager();
		mLoaderManager.initLoader(0, null, mLoaderCallbacks);*/

		// 检测更新
		try{
			//设置下载完成后，弹出安装提示，默认 false
			UpdateChecker.setPopInstallActivity(true);
			//设置检测间隔时间，以小时为单位，默认 0，表示次次都检测
			//UpdateChecker.setCheckTimeDistance(24);
			//不用指定URL，自动从商城获取。
			//指定机型类型，默认程序自动读取机型
			//UpdateChecker.setCheckUrl(HostInfo.getHostG100());
			//检测更新
			UpdateChecker.checkForNotification(this);
			UpdateChecker.addActivity(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
		mAsm = new AsmApp(this);
		
		// ASM初始化应用
		mAsm.asmInitApp();

		return true;
	}
	
	@Override
	protected void onMarch()
	{
		super.onMarch();
		
		Log.i(TAG, "[-----------------------------onStart-----------------------------]");
	}

	@Override
	public void onReinit()
	{
		super.onReinit();
		
		Log.i(TAG, "[-----------------------------onRestart-----------------------------]");
	}

	@Override
	protected void onContinue()
	{
		super.onContinue();
		
		Log.i(TAG, "[-----------------------------onResume-----------------------------]");
		
		// ASM恢复应用
		mAsm.asmResumeApp();
	}

	@Override
	protected void onSuspend()
	{
		super.onSuspend();
		
		Log.i(TAG, "[-----------------------------onPause-----------------------------]");
		
		// ASM暂停应用
		mAsm.asmPauseApp();
	}

	@Override
	public void onHalt()
	{
		super.onHalt();
		
		Log.i(TAG, "[-----------------------------onStop-----------------------------]");
	}

	@Override
	public void onExit()
	{
		super.onExit();
		
		Log.i(TAG, "[-----------------------------onDestroy-----------------------------]");
		
		// ASM退出应用

		mAsm = null;

		try{
			UpdateChecker.removeActivity(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id)
	{
		Log.i(TAG, "[-----------------------------onCreateDialog-----------------------------]");
		
		return ProgressDialog.show(this, "", "加载中，请稍候！！！", true, true);
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		
		Log.i(TAG, "[-----------------------------onNewIntent-----------------------------]");
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		
		Log.i(TAG, "[-----------------------------onSaveInstanceState-----------------------------]");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		
		Log.i(TAG, "[-----------------------------onRestoreInstanceState-----------------------------]");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		
		Log.i(TAG, "[-----------------------------onConfigurationChanged-----------------------------]");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		//Log.i(TAG, "[-----------------------------onTouchEvent-----------------------------]");
		
		if(mAsm.asmOnTouchEvent(event))
		{
			return true;
		}
		
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		Log.i(TAG, "[-----------------------------onKeyUp-----------------------------]");
		
		if(mAsm.asmOnKeyUp(keyCode, event))
		{
			return true;
		}
		
		return super.onKeyUp(keyCode, event);
	}
}
