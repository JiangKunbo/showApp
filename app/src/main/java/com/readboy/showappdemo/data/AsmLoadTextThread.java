package com.readboy.showappdemo.data;

import java.io.UnsupportedEncodingException;


import android.os.Handler;
import android.os.Message;

import com.readboy.showappdemo.util.Log;

/**
 * ASM加载文本的线程。
 * 
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01在Android平台上重写了此代码
 */
public class AsmLoadTextThread extends Thread
{
	//final String TAG = getClass().getSimpleName();					// TAG
	final String TAG = "AsmLoadTextThread";								// TAG
	
	private Handler mHandler = null;
	private byte[] mBytes = null;
	
	public AsmLoadTextThread(Handler handler, byte[] bytes)
	{
		mHandler = handler;
		mBytes = bytes;
		
		setName(TAG);
	}
	
	@Override
	public void run()
	{
		Log.i(TAG, "[run] Thread Id =1= " + Thread.currentThread().getId() +
				", Name == " + Thread.currentThread().getName());
		
		String mString = null;
		
		if((mBytes[0] & 0xFF) == 0xFF && (mBytes[1] & 0xFF) == 0xFE)	// UTF16_CODE
		{
			mString = new String(AsmYBToUnicode.asmGetStr(mBytes, 2));
		}
		else															// ASCII_CODE
		{
			mString = asmGetString(mBytes, 0, AsmConstant.ASM_ASCII_CODE);
		}
		
		// 测试用，将文本写入到SD卡上的指定文件
		/*try
		{
			FileWriter fileWriter = new FileWriter("/storage/sdcard0/asm/text.txt");
			fileWriter.write(mString);
			fileWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}*/
		
		Log.i(TAG, "[run] Text Length == " + mString.length());
		
		// 获取消息的三种方式
		//Message msg = new Message();
		//msg.what = AsmConstant.ASM_TEXT_SHOW_ID;
		//msg.obj = mString;
		//mHandler.sendMessage(msg);
		
		//Message msg = mHandler.obtainMessage(AsmConstant.ASM_TEXT_SHOW_ID, mString);
		//msg.sendToTarget();
		
		Message msg = Message.obtain(mHandler, AsmConstant.ASM_TEXT_SHOW_ID, mString);
		msg.sendToTarget();
		
		Log.i(TAG, "[run] Thread Id =2= " + Thread.currentThread().getId() + 
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
