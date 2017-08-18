package com.readboy.showappdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ReadboyView;

/**
 * ��ʾ�������ࡣ
 * 
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history 	lyj 2013.08.01��Androidƽ̨����д�˴˴���
 */
public class AsmAnimView extends ReadboyView
{
	//private final String TAG = getClass().getSimpleName();
	//private final String TAG = "AsmAnimView";
	
	private Paint mPaint = null;
	private Bitmap mBitmap = null;
	private Canvas mCanvas = null;
		
	public AsmAnimView(Context context)
	{
		super(context);
	}

	public AsmAnimView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public AsmAnimView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	/**
	 * ��ʼ����
	 * 
	 * @param w							���
	 * @param h							�߶�
	 */
	public void init(int w, int h)
	{
		//Log.i(TAG, "W == " + w + ", H == " + h);
		
		mBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		
		mCanvas = new Canvas(mBitmap);
		
		mPaint = new Paint();
		mPaint.setTextAlign(Align.CENTER);
	}
	
	/**
	 * �˳���
	 */
	public void exit()
	{
		if(mBitmap != null && !mBitmap.isRecycled())
		{
			mBitmap.recycle();
			mBitmap = null;
		}
		
		if(mCanvas != null)
		{
			mCanvas = null;
		}
		
		if(mPaint != null)
		{
			mPaint = null;
		}
	}
	
	/**
	 * ��ȡPaint��
	 * 
	 * @return							Paint
	 */
	public Paint getPaint()
	{
		return mPaint;
	}
	
	/**
	 * ��ȡ����Bitmap��
	 * 
	 * @return							Bitmap
	 */
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
	
	/**
	 * ��ȡCanvas��
	 * 
	 * @return							Canvas
	 */
	public Canvas getCanvas()
	{
		return mCanvas;
	}
	
	/**
	 * ������档
	 */
	public void clearDraw()
	{
		mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		
		//mBitmap.eraseColor(Color.TRANSPARENT);
		
		invalidate();
	}
	
	/**
	 * ��ʾ�ı���
	 * 
	 * @param text				�ı�
	 * @param paint				Paint
	 * @param x					��ʼλ��X����
	 * @param y					��ʼλ��Y����
	 * @param w					�������
	 * @param h					�����߶�
	 */
	public void doDrawText(String text, Paint paint, float x, float y, float w, float h)
	{
		FontMetrics fontMetrics = paint.getFontMetrics();
		
		// �������ָ߶�
		float fontHeight = fontMetrics.bottom - fontMetrics.top;
		
		// �����������ĵ����λ�á� -6����Ϊ����õ���λ����ƫ���ȥ6������������СһЩ
		float textBaseY = h - (h - fontHeight) / 2 - fontMetrics.bottom - 6;
		
		float newX = x + w / 2;
		float newY = y + textBaseY;
		
		mCanvas.drawText(text, newX, newY, paint);
		
		invalidate(new Rect((int) x, (int) y, (int) w, (int) h));
	}
	
	/**
	 * ��ʾ�ı���
	 * 
	 * @param text				�ı�
	 * @param paint				Paint
	 * @param rect				������ʾ����
	 */
	public void doDrawText(String text, Paint paint, Rect rect)
	{
		FontMetrics fontMetrics = paint.getFontMetrics();
		
		int x = rect.left;
		int y = rect.top;
		int w = rect.bottom - rect.top;
		int h = rect.right - rect.left;
		
		// �������ָ߶�
		float fontHeight = fontMetrics.bottom - fontMetrics.top;
		
		// �����������ĵ����λ�á� -6����Ϊ����õ���λ����ƫ���ȥ6������������СһЩ
		float textBaseY = rect.bottom - rect.top - (h - fontHeight) / 2 - fontMetrics.bottom - 6;
		
		float newX = x + w / 2;
		float newY = y + textBaseY;
		
		mCanvas.drawText(text, newX, newY, paint);
		
		invalidate(rect);
	}
	
	/**
	 * ָ��λ�û�ͼ��
	 * 
	 * @param bitmap					Bitmap
	 * @param x							X����
	 * @param y							Y����
	 */
	public void doDrawBitmap(Bitmap bitmap, int x, int y)
	{
		if(bitmap != null)
		{
			mCanvas.drawBitmap(bitmap, x, y, mPaint);
			
			invalidate(new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight()));
		}
	}
	
	/**
	 * ָ��λ�û�ͼ��������ָ������
	 * 
	 * @param bitmap					Bitmap
	 * @param x							X����
	 * @param y							Y����
	 * @param rect						��������
	 */
	public void doDrawBitmap(Bitmap bitmap, int x, int y, Rect rect)
	{
		if(bitmap != null)
		{
			mCanvas.drawBitmap(bitmap, x, y, mPaint);
			
			invalidate(rect);
		}
	}
	
	/**
	 * ָ�������ͼ��
	 * 
	 * @param bitmap					Bitmap
	 * @param src						Դ����
	 * @param dst						Ŀ������
	 */
	public void doDrawBitmap(Bitmap bitmap, Rect src, Rect dst)
	{
		if(bitmap != null)
		{
			mCanvas.drawBitmap(bitmap, src, dst, mPaint);
			
			invalidate(dst);
		}
	}

	/**
	 * onDraw��
	 */
	@Override
	protected void onPaint(Canvas canvas)
	{
		super.onPaint(canvas);
		
		//Log.i(TAG, "[onDraw]");
		
		if(mBitmap != null)
		{
			canvas.drawBitmap(mBitmap, 0, 0, mPaint);
		}
	}
}
