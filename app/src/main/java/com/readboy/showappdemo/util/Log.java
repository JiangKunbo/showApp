package com.readboy.showappdemo.util;

/**
 * Log类。<br/>
 * <br/>
 * 重新封装了Log的几种打印输出方法，使之具有选择输出打印消息的功能。
 *
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01在Android平台上重写了此代码
 */
public class Log
{
	/**
	 * 是否打印调试信息。
	 */
	private static boolean DEBUG = true;

	public static void v(String tag, String msg)
	{
		if (DEBUG)
		{
			android.util.Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr)
	{
		if (DEBUG)
		{
			android.util.Log.v(tag, msg, tr);
		}
	}

	public static void d(String tag, String msg)
	{
		if (DEBUG)
		{
			android.util.Log.d(tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr)
	{
		if (DEBUG)
		{
			android.util.Log.d(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg)
	{
		if (DEBUG)
		{
			android.util.Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr)
	{
		if (DEBUG)
		{
			android.util.Log.i(tag, msg, tr);
		}
	}

	public static void w(String tag, String msg)
	{
		if (DEBUG)
		{
			android.util.Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr)
	{
		if (DEBUG)
		{
			android.util.Log.w(tag, msg, tr);
		}
	}

	public static void w(String tag, Throwable tr)
	{
		if (DEBUG)
		{
			android.util.Log.w(tag, tr);
		}
	}

	public static void e(String tag, String msg)
	{
		//错误信息默认全部显示
		//if (DEBUG)
		{
			android.util.Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr)
	{
		//错误信息默认全部显示
		//if (DEBUG)
		{
			android.util.Log.e(tag, msg, tr);
		}
	}
}