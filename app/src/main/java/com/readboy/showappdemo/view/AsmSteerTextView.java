/**
 * @purpose SteerTextView�࣬ʵ��ȡ�ʹ��ܣ�ȡ��ȡ�ʹ��ܵ�View
 * 
 * ʵ�֣�
 * ����TextView��дonTouchEventʵ��ѡ�����֣�֪ͨѡȡ�����ݣ�����ȡ���˵ȵ�
 * �ṩ��ȡȡ�����ݣ��������ݣ��Ƿ����ȡ�ʣ�ˢ�µ�ǰȡ��״̬�Ƚӿ�
 * 
 * @time 2013.10.11;
 * @author divhee
 * @modify by 
 */
package com.readboy.showappdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ReadboyTextView;

public class AsmSteerTextView extends ReadboyTextView {

	/**
	 * �ַ�����ƫ��ֵ
	 */
	private int off = 0;
	
	/**
	 * ��ǰ�ؼ�����������
	 */
	private String selfString = "";
	
	/**
	 * ��ǰ�ؼ���Layoutͨ��������Ի�ȡ�ؼ���λ��
	 * ��һ����ȡ��ָ���µ�λ��
	 */
	private Layout layout = null;
	
	/**
	 * �Ƿ����ȡ�ʵı�־
	 */
	private boolean bQcEnable = false;
	
	/**
	 * SpannableStringBuilder������Կ���������ʾ����ɫ���Լ�����ɫ
	 */
	private SpannableStringBuilder ssbCtn = new SpannableStringBuilder(selfString);
	
	/**
	 * ȡ�����ݼ�¼�����String��
	 */
	private static String selectedString = "";
	
	/**
	 * ������������ʵ����ʱ��ֻ��һ��ʵ����ȡ�ʣ�����ͬʱѡ�ж��ʵ��
	 */
	private static AsmSteerTextView focusView = null;
	
	/**
	 * ��¼���µ�λ��
	 */
	private Point mDnPoint = new Point();
	
	/**
	 * ��¼̧���λ��
	 */	
	private Point mUpPoint = new Point();
	
	/**
	 * ��ֱ���õ���Rect
	 */	
	//private Rect mRect = new Rect();
	
	/**
	 * ��ֱ���õ���Paint
	 */
	private Paint mPaint = new Paint();
	
	/**
	 * �����ڵľ��
	 */
	protected ViewParent mParent;
	
	
	
	/**
	 * ����ѡ�е��ı�
	 */
	private OnTextSelectedListener mOnTextSelectedListener;
	
	public AsmSteerTextView(Context context) {
		super(context);
		initialize();
	}

	public AsmSteerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}	
	
	public AsmSteerTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	/**
	 * @purpose �ؼ��ĳ�ʼ��
	 * @param ��
	 * @return ��
	 */
	private void initialize() {
		//setGravity(Gravity.TOP);
		setTextColor(Color.BLACK);
		setBackgroundColor(0x00000000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(0xFFFF8040);
	}

//	@Override
//	protected void onCreateContextMenu(ContextMenu menu) {
//		// �����κδ���Ϊ����ֹ������ʱ�򵯳������Ĳ˵�
//		super.onCreateContextMenu(menu);
//	}

	/**
	 * @purpose ����true��֤һֱ���Ա༭
	 * @param ��
	 * @return ��
	 */
	@Override
	public boolean getDefaultEditable() {
		return true;
	}
	
	/**
	 * @purpose ����onDraw
	 * @param ��
	 * @return ��
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		/*int count = getLineCount();
		Rect r = mRect;
		Paint paint = mPaint;
		int x = getWidth();
		for (int i = 0; i < count; i++) {
			int baseline = getLineBounds(i, r);
			canvas.drawLine(0, baseline + 1, x, baseline + 1, paint);
		}*/
		super.onDraw(canvas);
	}
	
	/**
	 * @purpose ����onTouchEvent����
	 * 			ʵ���϶�ȡ��ѡȡ���ֹ��ܣ���̧���ʱ��֪ͨ��Ӧ��ȡ��
	 * 
	 * @param event
	 * 				������Ϣ
	 * @return true ����Ϣ������ؼ�������
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		super.onTouchEvent(event);
		
		//Log.i("AsmSteerTextView", "[onTouchEvent] event == " + event);
		
		// ������㴰�ڲ��Ǳ����ڣ�����һ�����㴰�ڻָ�������ȡ������
		if (focusView != null && focusView != this){
			focusView.selfUpdateUnSelected();
		}
		// �������ȡ������ȡ�ʲ�������ֹȡ����ֱ�ӷ���
		try {
			if (bQcEnable){
				int line = 0;
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					// ��ȡgetLayout����
					layout = this.getLayout();
					if(layout != null) {
						line = layout.getLineForVertical(getStartPositionY(event));
//						line = layout.getLineForVertical(getScrollY() + (int) event.getY());
						off = layout.getOffsetForHorizontal(line, event.getX());
						// ���������ı�ѡ��״̬
						ssbCtn.clearSpans();
						this.setText(ssbCtn);
						// ��ȡ���ؼ�
						if (mParent == null){
//							mParent = this.getParent().getParent();
							mParent = this.getParent();
						}
						// �������ȡ�ʵĻ�֪ͨ���ؼ����߸����ؼ������ǲ�Ҫ���ش�����Ϣ������ؼ���Ҫ������Ϣ
						if (mParent != null){
							mParent.requestDisallowInterceptTouchEvent(true);
							//mParent.requestDisallowInterceptTouchEvent(false);
						}
						// ��¼����ؼ������ڻ���
						focusView = this;
						// ��¼���µ�λ��
						mDnPoint.set((int)event.getRawX(), (int)event.getRawY());
						
//						Log.e("onTouchEvent --- ", "onTouchEvent 11--- currentThread().getName() = "+Thread.currentThread().getName());
						int offCopy = off+1;
						if (offCopy > this.getText().toString().length()) {
							offCopy = this.getText().toString().length();
						} 
						//ssbCtn.setSpan(new ForegroundColorSpan(Color.RED), off, offCopy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						ssbCtn.setSpan(new BackgroundColorSpan(0x800050E0), off, offCopy, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						this.setText(ssbCtn);
					}
					break;
					
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					// ��ȡgetLayout����
					layout = this.getLayout();
					if(layout != null) {
						line = layout.getLineForVertical(getStartPositionY(event));
//						line = layout.getLineForVertical(getScrollY() + (int) event.getY());
						int curOff = layout.getOffsetForHorizontal(line, event.getX());
						// ע�����Ҫ����ÿ�ʼ�ͽ�����startСend�󣬷�����Ҫ�һ���
						int curStart = Math.min(off, curOff);
						int curEnd = Math.max(off, curOff);
//						Log.e("onTouchEvent --- ", "onTouchEvent 11--- lineEnd = "+layout.getLineEnd(line)+", curStart = "+curStart+", curEnd = "+curEnd+", (int)event.getX() = "+(int)event.getX()+", w = "+w);
//						Log.e("onTouchEvent --- ", "onTouchEvent 11--- currentThread().getName() = "+Thread.currentThread().getName());
						// ���������ı�ѡ��״̬
						ssbCtn.clearSpans();
						if (curStart != curEnd){
							curEnd = curEnd+1;
							if (curEnd > this.getText().toString().length()) {
								curEnd = this.getText().toString().length();
							} 
							//ssbCtn.setSpan(new ForegroundColorSpan(Color.RED), curStart, curEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							ssbCtn.setSpan(new BackgroundColorSpan(0x800050E0), curStart, curEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else if (curStart == curEnd) {
							curEnd = curEnd+1;
							if (curEnd > this.getText().toString().length()) {
								curEnd = this.getText().toString().length();
							} 
							//ssbCtn.setSpan(new ForegroundColorSpan(Color.RED), curStart, curEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							ssbCtn.setSpan(new BackgroundColorSpan(0x800050E0), curStart, curEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} 
						this.setText(ssbCtn);
						
						// �����̧����֪ͨ�����ڿ���������
						if (MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action){
							if (mParent != null){
								mParent.requestDisallowInterceptTouchEvent(false);
							}
						}
						
						// �����̧������ѡ���ı����ȡѡ������֣���֪ͨȡ��
						if (MotionEvent.ACTION_UP == action){
							if ((curEnd - curStart) <= 0){
								focusView.selfUpdateUnSelected();
							} else {
								// ��¼̧���λ��
								mUpPoint.set((int)event.getRawX(), (int)event.getRawY());
								// Ҫȡ�ʵ��������
//								int iQuCiType = util.DICT_TYPE_ERROR;
								// ѡ�е��ı����
								selectedString = "";
								
								// �ı���������
								if ((curEnd - curStart) > 100){
									curEnd = curStart + 100;
								}
								// ��ȡѡ����ı�
								char [] destSelected = new char [curEnd - curStart];
								ssbCtn.getChars(curStart, curEnd, destSelected, 0);					
								selectedString = new String(destSelected);
//								Log.i("onTouchEvent --- ", "onTouchEvent --- selectedString = "+selectedString);
								
								if (mOnTextSelectedListener != null) {
									//���ü�����֪ͨȡ�ʵ�����
									mOnTextSelectedListener.onTextSelection(selectedString);
								}
								
								/*// ������Ҫȡ�ʵ����ͣ�������Ӣ���򵯳�Ӣ���б������������򵯳����Ĵʵ��б�
								for (int index = 0 ; index < destSelected.length && iQuCiType == 0 ; index++){
									if ((destSelected[index] >= 'a' && destSelected[index] <= 'z')
											|| (destSelected[index] >= 'A' && destSelected[index] <= 'Z')){
										iQuCiType = util.DICT_TYPE_ENG;
									} else if (destSelected[index] > 0x80){
										iQuCiType = util.DICT_TYPE_CHI;
									}
								}
								if (iQuCiType != OperateJniData.QUCI_TYPE_NULL 
										&& OperateJniData.ActionID == OperateJniData.ELA_ACTBTN_QUCI){
									// ��ȡ��ģʽ�ŷ���ȡ����Ϣ
									int dnPos = OperateJniData.MKDWORD(mDnPoint.x, mDnPoint.y);
									int upPos = OperateJniData.MKDWORD(mUpPoint.x, mUpPoint.y);
									MainActivity.msgOperateEventDelayed(OperateJniData.WM_START_QCLIST, 
											(off < curOff) ? dnPos : upPos, (off < curOff) ? upPos : dnPos, iQuCiType, 0);
								} else {
									focusView.selfUpdateUnSelected();
								}
								if (iQuCiType == util.DICT_TYPE_ERROR) {
									Log.e("onTouchEvent --- ", "onTouchEvent --- 11 quci type error !");
									focusView.selfUpdateUnSelected();
								}*/
							}
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			Log.e("onTouchEvent --- ", "onTouchEvent --- 22 quci error !");
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * @purpose �����Ƿ����ȡ�ʣ������ı���ʾ
	 * @param qcEnable
	 * 			true ����ȡ��
	 * 			false ������ȡ��
	 * @return ��
	 */
	public void setDragEnable(boolean qcEnable) {
		bQcEnable = qcEnable;
		this.setText(ssbCtn);
	}	

	/**
	 * @purpose ���ÿؼ����ı�
	 * @param ctn
	 * 			�ı�����
	 * @return ��
	 */
	public void setTextCtn(String ctn) {
		selfString = ctn;
		ssbCtn.clear();
		ssbCtn = new SpannableStringBuilder(ctn);
		ssbCtn.clearSpans();
		selectedString = "";
		this.setText(ssbCtn);
	}
	
	/**
	 * @purpose ȡ���ı�ѡ��״̬
	 * @param ��
	 * @return ��
	 */
	public void selfUpdateUnSelected() {
		ssbCtn.clearSpans();
		this.setText(ssbCtn);
		if (focusView == this){
			focusView = null;
		}
		off = 0;
	}
	
	/**
	 * @purpose �Ƿ���ȡ�ʱ�ѡ��
	 * @param ��
	 * @return boolean 
	 * 			true ѡ����ȡ��
	 * 			false û��ѡ��ȡ��
	 */
	public static boolean getCurrentQuCiSelected() {
		if (focusView != null){
			return true;
		}
		return false;
	}
	
	/**
	 * @purpose ��ȡ��ǰѡ�е��ı�����
	 * @param ��
	 * @return ѡ�е��ı�����
	 */
	public static String getCurrentQuCiString() {
		return selectedString;
	}
	
	/**
	 * ��ȡevent.getY()��textview�Ŀ��ӷ�Χ�ڵ�ֵ
	 * @param event
	 * @return
	 */
	private int getStartPositionY(MotionEvent event) {
		// ��ȡtextview�ĸ��״���
		AsmScrollView scrollView = (AsmScrollView)this.getParent();
		int scrollY = scrollView.getScrollY();	//��ȡ���������ϻ�����Yֵ
		int scrollH = scrollView.getHeight();	//��ȡ�����ڵĿ��Ӹ߶�
		int paddingTop = scrollView.getPaddingTop();	//��ȡtextview�븸���ڵļ��
		int lineHeight = this.getLineHeight();	//��ȡtextview���и�
//		Log.e("onTouchEvent --- ", "scrollY = "+scrollY+", scrollH = "+scrollH+", maxH = "+(scrollY+scrollH-paddingTop));
		int positionY = getScrollY() + (int) event.getY();
//		Log.e("onTouchEvent --- ", "positionY = "+positionY);
//		Log.e("onTouchEvent --- ", "positionY = "+scrollView.get);
		
		if (positionY > (scrollY+scrollH-paddingTop-lineHeight/2)) {
			positionY = scrollY+scrollH-paddingTop-lineHeight/2;
		} else if (positionY < scrollY+lineHeight/2) {
			positionY = scrollY+lineHeight/2;
		}
		return positionY;
	}
	
	/**
	 * ����ѡ���ı��ļ�������
	 * 
	 * @param onTextSelectedListener			ѡ���ı��ļ�����
	 */
	public void setOnTextSelectedListener(OnTextSelectedListener onTextSelectedListener)
	{
		mOnTextSelectedListener = onTextSelectedListener;
	}
	
	/**
	 * ѡ���ı��ļ�������
	 */
	public interface OnTextSelectedListener 
	{
		void onTextSelection(final String selectText);
	}
	
}
