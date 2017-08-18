package com.readboy.showappdemo.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ReadboyScrollView;

/**
 * ������ͼ��<br/>
 * <br/>
 * 1����д��overScrollBy������ʹ֮���С����ᡰЧ��;<br/>
 * 2�������˼��������仯�Ľӿڣ�OnScrollChangedListener�������������������ײ�λ�á�<br/>
 * 
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01��Androidƽ̨����д�˴˴���
 */
public class AsmScrollView extends ReadboyScrollView
{
	//private final String TAG = getClass().getSimpleName();		// TAG
	//private final String TAG = "AsmScrollView";					// TAG
		
	private OnScrollChangedListener mOnScrollChangedListener = null;// ���������仯�Ľӿ�
	private long mMillis = 0;										// ��һ�ι�����ʱ��

	private static final int MaxOverScrollYDistance = 200;			// ����Y�߽��������
	
	@SuppressWarnings("unused")
	private static int mMaxOverScrollY = 0;							// ����Y�߽�����ֵ
	
	//private static final float ScrollRatio = 0.5f;				// ����ϵ��
	
	public AsmScrollView(Context context)
	{
		super(context);

		initMaxOverScrollY(context);
	}

	public AsmScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		initMaxOverScrollY(context);
	}

	public AsmScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		initMaxOverScrollY(context);
	}

	/**
	 * Init number of pixels to overscroll by in either direction along the Y axis.
	 */
	private void initMaxOverScrollY(Context context)
	{
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		float density = displayMetrics.density;
		mMaxOverScrollY = (int) (density * MaxOverScrollYDistance);

		// Log.i("AsmScrollView", "mMaxOverScrollY == " + mMaxOverScrollY);
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, 
		int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
	{
		//int delta = (int) (deltaY * ScrollRatio);
		//int newDeltaY = delta == 0 ? deltaY : delta;
		
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, 
			maxOverScrollX, maxOverScrollY, isTouchEvent);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);

		if (mOnScrollChangedListener == null)
		{
			return;
		}
		
		// ֪ͨ�����ߵ�ǰ�����ľ�����Ϣ
		mOnScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
		
		// ������������ǰ�᣺�Ӳ��Ƕ���������������
		if (oldt != 0 && t == 0)
		{
			mOnScrollChangedListener.onScrollTop(); 		// ֪ͨ�����߹���������
		}
		
		long now = System.currentTimeMillis();
		if (now - mMillis > 1000l)
		{
			// �������ײ���ǰ�᣺�Ӳ��ǵײ��������ײ���
			if ((getHeight() + oldt) != getTotalVerticalScrollRange() && 
					(getHeight() + t) == getTotalVerticalScrollRange())
			{
	
				mOnScrollChangedListener.onScrollBottom(); 	// ֪ͨ�����߹������ײ�
				
				mMillis = now;
			}
		}
	}

	/**
	 * ��ȡOnScrollChangedListener��
	 */
	public OnScrollChangedListener getOnScrollChangedListener()
	{
		return mOnScrollChangedListener;
	}

	/**
	 * ����OnScrollChangedListener��
	 */
	public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener)
	{
		mOnScrollChangedListener = onScrollChangedListener;
	}

	/**
	 * ��ù����ܳ��ȡ�
	 */
	public int getTotalVerticalScrollRange()
	{
		return computeVerticalScrollRange();
	}

	/**
	 * computeScrollDeltaToGetChildRectOnScreen��
	 */
	@Override
	protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect)
	{
		// ��ֹScrollView���ӿؼ��Ĳ��ָı�ʱ�Զ�����
		return 0;
	}

	/**
	 *	���������仯�Ľӿڡ�
	 */
	public interface OnScrollChangedListener
	{
		/**
		 * ���������仯��
		 * 
		 * @param l 				Current horizontal scroll origin.
		 * @param t 				Current vertical scroll origin.
		 * @param oldl 				Previous horizontal scroll origin.
		 * @param oldt 				Previous vertical scroll origin.
		 */
		public void onScrollChanged(int l, int t, int oldl, int oldt);

		/**
		 * ����������������
		 */
		public void onScrollTop();

		/**
		 * �����������ײ���
		 */
		public void onScrollBottom();
	}
}
