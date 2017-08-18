package com.readboy.showappdemo.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.widget.EditText;

import com.readboy.showappdemo.util.Log;


/**
 * TextView��
 *
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history 	lyj 2013.08.01��Androidƽ̨����д�˴˴���
 */
public class AsmTextView extends EditText
{
	//private final String TAG = getClass().getSimpleName();		// TAG
	private final String TAG = "AsmTextView";						// TAG

	private OnTextSelectedListener mOnTextSelectedListener;			// ѡ���ı��ļ�����
	private boolean mSelectEnable;									// �Ƿ����ȡ��
	private int mOffset; 											// �ַ�����ƫ��ֵ

	public AsmTextView(Context context)
	{
		super(context);

		initialize();
	}

	public AsmTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		initialize();
	}

	public AsmTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		initialize();
	}

	/**
	 * ��ʼ����
	 */
	private void initialize()
	{
		setTextColor(Color.BLACK);

		setHighlightColor(0x800050E0);

		setBackgroundColor(Color.TRANSPARENT);
	}

	/**
	 * ��ȡĬ�ϵ��˶�������
	 */
	@Override
	protected MovementMethod getDefaultMovementMethod()
	{
		//return null;
		return super.getDefaultMovementMethod();
		//return ArrowKeyMovementMethod.getInstance();
		//return ScrollingMovementMethod.getInstance();
	}

	/**
	 * ������ʱ�򵯳��������Ĳ˵���
	 */
	@Override
	protected void onCreateContextMenu(ContextMenu menu)
	{
		// �����κδ���Ϊ����ֹ������ʱ�򵯳������Ĳ˵�
		//super.onCreateContextMenu(menu);
	}

	@Override
	public boolean onTextContextMenuItem(int id) {
		//return super.onTextContextMenuItem(id);
		return true;
	}

	/**
	 * �Ƿ�ɱ༭��
	 */
	@Override
	public boolean getDefaultEditable()
	{
		return false;
	}

	/**
	 * �������¼��ļ�������
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		//Log.i(TAG, "[AsmTextView] onTouchEvent");

		super.onTouchEvent(event);

		if(!mSelectEnable || mOnTextSelectedListener == null)
		{
			return true;
		}

		int action = event.getAction();
		Layout layout = getLayout();
		Editable text = getText();

		if(layout == null || text == null || text.length() == 0)
		{
			return true;
		}

		//Log.i(TAG, "[AsmTextView] MotionEvent.Action == " + event.getAction() +
		//	", X == " + event.getX() + ", Y == " + event.getY());

		switch (action)
		{
			case MotionEvent.ACTION_DOWN:	// 0
				{
					getParent().requestDisallowInterceptTouchEvent(mSelectEnable);

					int line = layout.getLineForVertical(getScrollY() + (int) event.getY());
					mOffset = layout.getOffsetForHorizontal(line, (int) event.getX());
					Selection.setSelection(text, mOffset);
					//setSelection(mOffset);
				}
				break;

			/*case MotionEvent.ACTION_UP:	// 1
				{
					String selectText = null;

					if(hasSelection())
					{
						selectText = editable.subSequence(getSelectionStart(), getSelectionEnd()).toString();
					}

					if(selectText != null && selectText.length() > 0)
					{
						mOnTextSelectedListener.onTextSelection(selectText);

						Log.i(TAG, "[AsmTextView] SelectText == " + selectText);
					}
				}
				break;

			case MotionEvent.ACTION_MOVE:	// 2
				{
					int line = layout.getLineForVertical(getScrollY() + (int) event.getY());
					int curOffset = layout.getOffsetForHorizontal(line, (int) event.getX());
					int curStart = Math.min(mOffset, curOffset);
					int curEnd = Math.max(mOffset, curOffset);
					//Log.i(TAG, "[AsmTextView] curStart == " + curStart + ", curEnd == " + curEnd + ", length == " + text.length());
					Selection.setSelection(text, curStart, curEnd);
					//setSelection(curStart, curEnd);
				}
				break;*/

			case MotionEvent.ACTION_UP:		// 1
			case MotionEvent.ACTION_MOVE:	// 2
				{
					int line = layout.getLineForVertical(getScrollY() + (int) event.getY());
					int curOffset = layout.getOffsetForHorizontal(line, (int) event.getX());
					int curStart = Math.min(mOffset, curOffset);
					int curEnd = Math.max(mOffset, curOffset);
					//Log.i(TAG, "[AsmTextView] curStart == " + curStart + ", curEnd == " + curEnd + ", length == " + text.length());
					Selection.setSelection(text, curStart, curEnd);
					//setSelection(curStart, curEnd);

					if(action == MotionEvent.ACTION_UP)
					{
						String selectText = null;

						if(hasSelection())
						{
							selectText = text.subSequence(getSelectionStart(), getSelectionEnd()).toString();
						}

						if(selectText != null && selectText.length() > 0)
						{
							mOnTextSelectedListener.onTextSelection(selectText);

							//Log.i(TAG, "[AsmTextView] X == " + event.getRawX() +
							//		", Y == " + event.getRawY() + ", SelectText == " + selectText);
							Log.i(TAG, "[AsmTextView] SelectText == " + selectText);
						}
					}
				}
				break;

			case MotionEvent.ACTION_CANCEL:	// 3
				{
					Selection.removeSelection(getText());
				}
				break;
		}

		return true;
	}

	/**
	 * �����Ƿ����ȡ�ʡ�
	 *
	 * @param enable							�Ƿ����ȡ��
	 */
	public void setTextSelectEnable(boolean enable)
	{
		Log.i(TAG, "[AsmTextView] mSelectEnable == " + enable);

		mSelectEnable = enable;

		setSelected(enable);
	}

	/**
	 * ȡ���ı���ѡ��״̬��
	 */
	public void cancelTextSelectable()
	{
		Selection.removeSelection(getText());
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
		void onTextSelection(String selectText);
	}
}
