package com.readboy.showappdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.ReadboySurfaceView;
import android.view.SurfaceHolder;

/**
 * SurfaceView��
 * 
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01��Androidƽ̨����д�˴˴���
 */
public class AsmSurfaceView extends ReadboySurfaceView
{
	public AsmSurfaceView(Context context)
	{
		super(context);
	}

	public AsmSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public AsmSurfaceView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public SurfaceHolder getSurface()
	{
		return super.getSurface();
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility)
	{
		super.onWindowVisibilityChanged(visibility);
	}

	@Override
	public void setVisibility(int visibility)
	{
		super.setVisibility(visibility);
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean gatherTransparentRegion(Region region)
	{
		return super.gatherTransparentRegion(region);
	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
	}

	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);
	}

	@Override
	public void setZOrderMediaOverlay(boolean isMediaOverlay)
	{
		super.setZOrderMediaOverlay(isMediaOverlay);
	}

	@Override
	public void setZOrderOnTop(boolean onTop)
	{
		super.setZOrderOnTop(onTop);
	}
}
