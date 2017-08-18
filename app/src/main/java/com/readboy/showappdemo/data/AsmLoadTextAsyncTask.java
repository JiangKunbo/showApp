package com.readboy.showappdemo.data;

import java.io.UnsupportedEncodingException;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.readboy.showappdemo.util.Log;


/**
 * ASM异步加载文本类。<br/>
 * <br/>
 * AsyncTask<Params, Progress, Result>定义了三种泛型类型：<br/>
 * <li>Params 	：启动任务执行时接收的输入参数的类型；<br/>
 * <li>Progress ：后台任务执行的进度的类型；<br/>
 * <li>Result 	：后台执行任务执行的结果的类型。<br/>
 * 
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01在Android平台上重写了此代码
 */
public class AsmLoadTextAsyncTask extends AsyncTask<Object, Integer, String>
{
	//private final String TAG = getClass().getSimpleName();// TAG
	private final String TAG = "AsmLoadTextAsyncTask";		// TAG
	
	private Handler mHandler = null;
	private byte[] mBytes = null;
	
	public AsmLoadTextAsyncTask(Handler handler, byte[] bytes)
	{
		mHandler = handler;
		mBytes = bytes;
	}
	
	// Step 1  
	/**
	 * 在doInBackground()执行之前调用该方法，主要完成初始化工作。
	 */
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		
		Log.i(TAG, "[onPreExecute] Thread Id == " + Thread.currentThread().getId() + 
				", Name == " + Thread.currentThread().getName());
		
		Log.i(TAG, "[onPreExecute] Byte Length == " + mBytes.length);
	}
	
	// Step 2  
	/** 
	 * 在onPreExecute方法执行完毕后，执行该方法。<br/> 
	 * 比较耗时的操作都可以放在这里，注意这里不能直接操作UI。<br/> 
	 * 在执行过程中可以调用publishProgress(Progress…)，
	 * 就会执行异步类中的onProgressUpdate方法，用来更新任务的进度 。
	 */
	@Override
	protected String doInBackground(Object... params)
	{
		String string = null;
		
		Log.i(TAG, "[doInBackground] Thread Id == " + Thread.currentThread().getId() + 
				", Name == " + Thread.currentThread().getName());
		
		//publishProgress(0);
		
		if((mBytes[0] & 0xFF) == 0xFF && (mBytes[1] & 0xFF) == 0xFE)	// UTF16_CODE
		{
			string = new String(AsmYBToUnicode.asmGetStr(mBytes, 2));
		}
		else															// ASCII_CODE
		{
			string = asmGetString(mBytes, 0, AsmConstant.ASM_ASCII_CODE);
		}
		
		//publishProgress(100);
		
		Log.i(TAG, "[doInBackground] Text Length == " + string.length());
		
		return string;
	}

	// Step 2.1
	/**
	 * 主要用来更新任务的进度。<br/>
	 * 只有doInBackground()中调用了publishProgress(Progress…)方法时，才会执行该方法。
	 */
	@Override
	protected void onProgressUpdate(Integer... values)
	{
		super.onProgressUpdate(values);
		
		Log.i(TAG, "[onProgressUpdate] Value == " + values[0] + 
				", Thread Id == " + Thread.currentThread().getId() + 
				", Name == " + Thread.currentThread().getName());
	}
	
	// Step 3
	/**
	 * 相当于Handler处理UI的方式，在这里面可以使用在doInBackground 得到的结果处理操作UI。<br/> 
	 * 此方法在主线程执行，任务执行的结果作为此方法的参数返回。
	 */
	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
		
		Log.i(TAG, "[onPostExecute] Thread Id =1= " + Thread.currentThread().getId() + 
				", Name == " + Thread.currentThread().getName());
		
		
		// 测试用，将文本写入到SD卡上的指定文件
		/*try
		{
			FileWriter fileWriter = new FileWriter("/storage/sdcard0/asm/text.txt");
			fileWriter.write(result);
			fileWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}*/
		
		Log.i(TAG, "[onPostExecute] Text Length == " + result.length());
		
		// 获取消息的三种方式
		//Message msg = new Message();
		//msg.what = AsmConstant.ASM_TEXT_SHOW_ID;
		//msg.obj = result;
		//mHandler.sendMessage(msg);
		
		//Message msg = mHandler.obtainMessage(AsmConstant.ASM_TEXT_SHOW_ID, result);
		//msg.sendToTarget();
		
		Message msg = Message.obtain(mHandler, AsmConstant.ASM_TEXT_SHOW_ID, result);
		msg.sendToTarget();
		
		Log.i(TAG, "[onPostExecute] Thread Id =2= " + Thread.currentThread().getId() + 
				", Name == " + Thread.currentThread().getName());
	}

	// Step 4
	/**
	 * 当用户执行取消操作后，调用该方法。
	 */
	@Override
	protected void onCancelled()
	{
		super.onCancelled();
		
		Log.i(TAG, "[onCancelled] Thread Id == " + Thread.currentThread().getId() + 
				", Name == " + Thread.currentThread().getName());
	}
	
	// Step 4.1
	/**
	 * 当用户执行取消操作后，调用该方法。
	 */
	@Override
	protected void onCancelled(String result)
	{
		super.onCancelled(result);
		
		Log.i(TAG, "[onCancelled] Thread Id == " + Thread.currentThread().getId() + 
				", Name == " + Thread.currentThread().getName());
	}
	
	/**
	 * 获取字符串。
	 * 
	 * @param bytes				byte数组
	 * @param offset			偏移量
	 * @param charsetName		字符编码名称
	 * @return					字符串
	 */
	String asmGetString(byte[] bytes, int offset, String charsetName)
	{
		String str = null;
		
		try
		{
			str = new String(bytes, offset, asmStrlen(bytes, offset), charsetName);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		
		return str;
	}
	
	/**
	 * 获取字符串的长度。
	 * 
	 * @param buff				Buff
	 * @param start				开始位置
	 * @return					字符串的长度
	 */
	int asmStrlen(byte[] buff, int start)
	{
		int i, j;
		
		int len = buff.length;
		for (i = start, j = 0; i < len; i++, j++)
		{
			if (buff[i] == 0x0)
			{
				return j + 1;
			}
		}
		
		return j;
	}
}
