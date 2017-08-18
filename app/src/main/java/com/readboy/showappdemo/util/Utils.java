package com.readboy.showappdemo.util;

/**
 * Utils�ࡣ<br/>
 * <br/>
 * ��ֹ��ť������� ��
 *
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01��Androidƽ̨����д�˴˴���
 */
public class Utils
{
	/**
	 * ��һ�ε����ʱ�䡣
	 */
	private static long lastClickTime;

	/**
	 * ���ε����ʱ������
	 */
	private static int clickTimeSpace = 50;

	/**
	 * �жϰ�ť�Ƿ������������
	 *
	 * @return						true-�ǣ�false-��
	 */
	public static boolean isFastDoubleClick()
	{
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;

		if (0 < timeD && timeD < clickTimeSpace)
		{
			return true;
		}

		lastClickTime = time;

		return false;
	}
}
