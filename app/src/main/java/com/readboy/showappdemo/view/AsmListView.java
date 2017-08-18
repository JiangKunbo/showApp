package com.readboy.showappdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ReadboyListView;


public class AsmListView extends ReadboyListView
{
	private int MaxOverScrollYDistance = 200;
	
	@SuppressWarnings("unused")
	private int mMaxOverScrollY;
	
	public AsmListView(Context context)
	{
		super(context);
		
		initMaxOverScrollY(context);
	}

	public AsmListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		initMaxOverScrollY(context);
	}

	public AsmListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		initMaxOverScrollY(context);
	}
	
	private void initMaxOverScrollY(Context context)
	{
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		float density = displayMetrics.density;
		mMaxOverScrollY = (int) (density * MaxOverScrollYDistance);

	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, 
			int scrollRangeX,int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
	{
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, 
				maxOverScrollX, maxOverScrollY, isTouchEvent);
	}
}
