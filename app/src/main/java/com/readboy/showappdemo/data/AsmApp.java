package com.readboy.showappdemo.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.app.ReadboyActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


import com.readboy.showappdemo.AsmApplication;
import com.readboy.showappdemo.R;
import com.readboy.showappdemo.bean.TxtBean;
import com.readboy.showappdemo.gen.TxtEntryDao;
import com.readboy.showappdemo.util.Log;
import com.readboy.showappdemo.util.Utils;
import com.readboy.showappdemo.view.AsmAnimView;
import com.readboy.showappdemo.view.AsmListView;
import com.readboy.showappdemo.view.AsmScrollView;
import com.readboy.showappdemo.view.AsmSteerTextView;
import com.readboy.sound.Sound;
import com.readboy.sound.Sound.OnCompletionListener;

/**
 * ASM相关数据和操作类。
 *
 * @author lyj
 * @version 1.14.04.01
 * @date 2014.04.01
 * @history lyj 2013.08.01在Android平台上重写了此代码
 */
public class AsmApp extends AsmBase {
    //private final String TAG = getClass().getSimpleName();// TAG
    private final String TAG = "AsmApp";                    // TAG


    ReadboyActivity mContext;                                // Context
    Resources mResources;                                    // Resources

    AsmData mData;                                            // AsmData
    AsmMacroData mMacroData;                                // AsmMacroData

    String mDataName;                                        // Asm数据文件名

    Timer mTimer;                                            // Timer
    Handler mHandler;                                        // Handler

    RelativeLayout mMainView;                                // Asm主界面

    AsmAnimView mAnimview;                                    // 显示Asm动画的AsmAnimView

    Dialog mQuCiDialog;                                        // 取词对话框
    ImageButton mBtnQuCi;                                    // 取词按钮
    boolean mSelectEnable;                                    // 文本框的文本是否可选

    AsmListView mMenuListView;                                // 显示图片菜单的ListView
    AsmListView mTextListView;                                // 显示文字菜单及DataType4的ListView

    AsmScrollView mScrollView;                                // 显示纯文本滑动效果的ScrollView
    AsmSteerTextView mTextView;                                // 显示纯文本的AsmTextView

    AsmMemuType2Save[] mMenuType2Save;                        // 保存MenuType2的状态，用于返回上一级MenuType2菜单
    int mMemuType2LevelSave;                                // MenuType2的状态

    ArrayList<ImageButton> mMenuType3List;                    // 保存MenuType3的所有按钮
    ImageButton mMenuType3Sel;                                // MenuType3当前选中的按钮

    AsmDataType3Button mDataType3Button;                    // DataType3按钮

    Sound mSound;                                            // 媒体播放器
    RandomAccessFile mMpRaf;                                // 媒体播放器专用文件句柄

    boolean mIsAppPause;                                    // 应用是否暂停
    boolean mIsSoundPause;                                    // 声音是否暂停
    boolean mIsTimerPause;                                    // 定时器是否暂停

    GestureDetector mGestureDetector;                        // 手势检测器

    Button mBtnEnterGame;                                    // “进入游戏”按钮
    Button mBtnSearch;                                        // “查找”按钮
    ImageView mImgSearch;                                    // “查找”文本框背景
    AutoCompleteTextView mTxtSearch;                        // “查找”文本框
    AutoCompleteTextViewAdapter mTxtAdapter;                // Adapter
    Dialog mSearchDialog;                                    // 查找对话框

    MenuType2Adapter mMenuType2Adapter;
    MenuType2OnItemClickListener mMenuType2OnItemClickListener;

    MediaPlayer mMediaPlayer;                                // 播放背景音乐的MediaPlayer
    boolean mIsMediaPlayerPause;                            // 背景音乐是否暂停

    /**
     * 构造函数。
     */
    @SuppressWarnings("deprecation")
    public AsmApp(ReadboyActivity activity) {
        mContext = activity;
        mResources = mContext.getResources();

        AsmConstant.PANEL_WIDTH = mResources.getDimensionPixelSize(R.dimen.panel_w);
        AsmConstant.PANEL_HEIGHT = mResources.getDimensionPixelSize(R.dimen.panel_h);

        if (mContext.getWindowManager().getDefaultDisplay().getWidth() == 1024) {
            // 设置应用全屏显示
            mContext.getWindow().setFlags(0x80000000, 0x80000000);        // Android 4.0有效
        } else {
            //mContext.getWindow().setFlags(0x02000000, 0x02000000);	// Android 4.2有效
        }

        mContext.setContentView(R.layout.main);
    }

    /**
     * 保存MemuType2状态的类。
     */
    class AsmMemuType2Save {
        int item_top;                                    // 当前显示的首条目
        int item_sel;                                    // 当前选择的条目
        int addr;                                        // 地址
    }

    /**
     * DataType3按钮类。
     */
    class AsmDataType3Button {
        ImageButton btn_last;                            // 上一题
        ImageButton btn_next;                            // 下一题
        ImageButton btn_answer;                            // 答案
        ImageButton btn_question;                        // 题目
    }

    /**
     * asmOnTouchEvent。
     *
     * @param event MotionEvent
     * @return 已处理完触碰消息返回true，否则返回false
     */
    public boolean asmOnTouchEvent(MotionEvent event) {
        if (mData != null &&
                (mData.data_type == AsmConstant.DataType3 ||
                        mData.data_type == AsmConstant.DataType5)) {
            return mGestureDetector.onTouchEvent(event);
        }

        return false;
    }

    /**
     * asmOnKeyUp。
     *
     * @param keyCode KeyCode
     * @param event   KeyEvent
     * @return 已处理完键盘消息返回true，否则返回false
     */
    public boolean asmOnKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 返回上一级MenuType2菜单
            if (mMemuType2LevelSave > 0) {
                mMemuType2LevelSave--;

                int type = mData.asmDataReadNode(mMenuType2Save[mMemuType2LevelSave].addr);
                if (type == AsmConstant.MenuType2) {
                    // 隐藏DataType1
                    asmHideDataType1(mScrollView, mTextView);

                    // 隐藏DataType3的按钮
                    asmHideDataType3Button(mDataType3Button);

                    asmInitMenuType2(mData.pMenuType2, mTextListView);

                    mTextListView.setItemChecked(mMenuType2Save[mMemuType2LevelSave].item_sel, true);
                    mTextListView.setSelection(mMenuType2Save[mMemuType2LevelSave].item_top);
                    //mTextListView.smoothScrollToPosition(mSaveMenuType2[mSaveMemuType2Level].item_top);
                    mTextListView.smoothScrollToPosition(mMenuType2Save[mMemuType2LevelSave].item_sel);

                    return true;
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            android.util.Log.i(TAG, "asmOnKeyUp: llllllllef");
            if (mData != null && mTextView != null) {
                if (mData.data_type == AsmConstant.DataType3)        // 题目+答案
                {
                    asmDataType3ButtonOnClick(mMainView, false);

                    return true;
                } else if (mData.data_type == AsmConstant.DataType5)    // 三字经、弟子规、千字文
                {
                    asmInitDataType5Text(mData.pDataType5, mTextView, false);

                    return true;
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mData != null && mTextView != null) {
                if (mData.data_type == AsmConstant.DataType3)        // 题目+答案
                {
                    asmDataType3ButtonOnClick(mMainView, true);

                    return true;
                } else if (mData.data_type == AsmConstant.DataType5)    // 三字经、弟子规、千字文
                {
                    asmInitDataType5Text(mData.pDataType5, mTextView, true);

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * ASM初始化应用。
     */
    public void asmInitApp() {
        mIsAppPause = false;
        mIsSoundPause = false;
        mIsTimerPause = false;

        mIsMediaPlayerPause = false;
        mData = new AsmData();
        mMacroData = new AsmMacroData();

        mMenuType2Save = new AsmMemuType2Save[AsmConstant.MenuType2_LEVEL_MAX];
        for (int i = 0; i < AsmConstant.MenuType2_LEVEL_MAX; i++) {
            mMenuType2Save[i] = new AsmMemuType2Save();
        }

        mMenuType3List = new ArrayList<ImageButton>();

        mDataType3Button = new AsmDataType3Button();
        mSound = new Sound();
        mSound.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onComplete(Sound sound) {
                asmOnSoundPlayEnd(sound);
            }
        });

        asmInitBgMusic();

        // 获取数据文件路径
        mDataName = asmGetDataName();
        android.util.Log.i(TAG, "asmInitApp: " + mDataName);
        // ASM数据读取初始化
        boolean ret = mData.asmDataReadInit(mDataName);

        if (!ret) {
            Toast.makeText(mContext, "数据文件错误！！！请更新数据文件！！！",
                    Toast.LENGTH_LONG).show();

            mContext.finish();

            return;
        }

        // ASM Handler初始化
        asmInitHandler();

        // ASM GestureDetector初始化
        asmInitGestureDetector();

        // ASM界面组件初始化
        asmInitUI();

        // ASM初始化退出按钮
        asmInitExitButton();

        // ASM取词功能初始化。
        asmInitQuCi();
    }

    /**
     * ASM退出应用。
     */
    public void asmExitApp() {
        asmStopAvi();

        if (mSound != null) {
            mSound.release();
            mSound = null;
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mMpRaf != null) {
            try {
                mMpRaf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mMpRaf = null;
        }

        mData.asmDataReadExit();
        mData = null;

        mMacroData.asmPlayAviExit();
        mMacroData = null;

        if (mAnimview != null) {
            mAnimview.exit();
            mAnimview = null;
        }

        mIsAppPause = false;
        mIsSoundPause = false;
        mIsTimerPause = false;
        mSelectEnable = false;

        mIsMediaPlayerPause = false;
    }

    /**
     * ASM暂停应用。
     */
    public void asmPauseApp() {
        mIsAppPause = true;

        // 停止定时器
        if (mTimer != null) {
            mIsTimerPause = true;

            mTimer.cancel();
            mTimer = null;
        }

        // 停止播放声音
        if (mSound != null && mSound.isPlaying() && !mIsSoundPause) {
            mIsSoundPause = true;

            mSound.pause();
        }

        // 停止播放背景音乐
        if (mMediaPlayer != null && mMediaPlayer.isPlaying() && !mIsMediaPlayerPause) {
            mIsMediaPlayerPause = true;

            mMediaPlayer.pause();
        }

        // 关掉取词对话框
        /*if(mQuCiDialog != null && mQuCiDialog.isShowing())
		{
			mQuCiDialog.dismiss();
		}*/

        // 没有显示取词对话框，取消选中内容
        if (mQuCiDialog == null || !mQuCiDialog.isShowing()) {
            if (mTextView != null) {
                mTextView.selfUpdateUnSelected();
            }
        }
    }

    /**
     * ASM恢复应用。
     */
    public void asmResumeApp() {
        mIsAppPause = false;

        if (mIsTimerPause) {
            mIsTimerPause = false;

            asmInitTimer(0, AsmConstant.ASM_AVI_TIMER_PERIOD);
        }

        if (mSound != null && mIsSoundPause) {
            mIsSoundPause = false;

            mSound.start();
        }

        if (mMediaPlayer != null && mIsMediaPlayerPause) {
            mIsMediaPlayerPause = false;

            mMediaPlayer.start();
        }
    }

    /**
     * ASM界面组件初始化。
     */
    private void asmInitUI() {
        mMainView = (RelativeLayout) mContext.findViewById(R.id.mainview);

        mAnimview = (AsmAnimView) mMainView.findViewById(R.id.animview);
        mAnimview.init(AsmConstant.PANEL_WIDTH, AsmConstant.PANEL_HEIGHT);

        mMenuListView = (AsmListView) mMainView.findViewById(R.id.menulistview);
        mTextListView = (AsmListView) mMainView.findViewById(R.id.textlistview);
        mScrollView = (AsmScrollView) mMainView.findViewById(R.id.scrollview);
        mTextView = (AsmSteerTextView) mMainView.findViewById(R.id.textview);

        mBtnEnterGame = (Button) mMainView.findViewById(R.id.btn_entergame);
        mBtnSearch = (Button) mMainView.findViewById(R.id.btn_search);
        mImgSearch = (ImageView) mMainView.findViewById(R.id.img_search);
        mTxtSearch = (AutoCompleteTextView) mMainView.findViewById(R.id.txt_search);

        mBtnEnterGame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("entry", 0);
                intent.setClassName("com.readboy.idiomdoyen",
                        "com.readboy.idiomdoyen.IdiomDoyenActivity");

                try {
                    //mContext.startActivity(intent);
                    mContext.launchForResult(intent, -1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();

                    Toast.makeText(mContext, "进入游戏失败！\n请检查成语达人应用是否被停用！",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        asmSendMessageDelayed(mHandler, AsmConstant.LIST_ID_MIN, 0, 0, AsmConstant.ASM_DELAY_TIME);
    }

    /**
     * ASM初始化退出按钮。
     */
    private void asmInitExitButton() {
        // 1024*600分辨率的机器加上退出按钮
		/*if(mContext.getWindowManager().getDefaultDisplay().getWidth() == 1024)
		{
			Button btnExit = new Button(mContext);
			btnExit.setBackgroundResource(R.drawable.exit_selector);
			RelativeLayout.LayoutParams params = 
					new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			int x = mResources.getDimensionPixelSize(R.dimen.btn_exit_x);
			int y = mResources.getDimensionPixelSize(R.dimen.btn_exit_y);
			params.leftMargin = x;
			params.topMargin = y;
			
			// 貌似还有点问题，暂时不加
			mMainView.addView(btnExit, params);
			
			btnExit.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mContext.finish();
				}
			});
		}*/
    }

    /**
     * ASM初始化背景音乐
     */
    private void asmInitBgMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        mMediaPlayer = MediaPlayer.create(mContext, R.raw.bg);

        mMediaPlayer.setLooping(true);

        if (!mIsAppPause) {
            mMediaPlayer.start();
        } else {
            mIsMediaPlayerPause = true;
        }
    }

    /**
     * ASM取词功能初始化。
     */
    private void asmInitQuCi() {
        // 初始化取词按钮
        mBtnQuCi = new ImageButton(mContext);
        mBtnQuCi.setBackgroundResource(R.drawable.btn_quci_nor);
        LayoutParams params =
                new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        int x = mResources.getDimensionPixelSize(R.dimen.btn_quci_x);
        int y = mResources.getDimensionPixelSize(R.dimen.btn_quci_y);
        params.leftMargin = x;
        params.topMargin = y;
        mMainView.addView(mBtnQuCi, params);
        mBtnQuCi.setVisibility(View.INVISIBLE);
        mBtnQuCi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectEnable) {
                    //mScrollView.clearAnimation();

                    mSelectEnable = false;
                    mTextView.setDragEnable(false);
                    mBtnQuCi.setBackgroundResource(R.drawable.btn_quci_nor);
                } else {
                    mSelectEnable = true;
                    mTextView.setDragEnable(true);
                    mBtnQuCi.setBackgroundResource(R.drawable.btn_quci_sel);
                }
            }
        });

        // 为TextView设置选中文本的监听器。
        mTextView.setOnTextSelectedListener(new AsmSteerTextView.OnTextSelectedListener() {
            @Override
            public void onTextSelection(final String selectText) {
                // 关掉先前存在的取词对话框
                if (mQuCiDialog != null && mQuCiDialog.isShowing()) {
                    //mQuCiDialog.dismiss();
                    return;
                }

                // 初始化取词对话框
                if (mQuCiDialog == null) {
                    mQuCiDialog = new Dialog(mContext, R.style.QuCiDialogTheme);
                    mQuCiDialog.setCancelable(true);
                    mQuCiDialog.setCanceledOnTouchOutside(true);
					/*View view = mContext.getLayoutInflater().inflate(R.layout.qucidialog, null);
					WindowManager.LayoutParams params = new WindowManager.LayoutParams();
					params.x = x;
					params.y = y;
					mQuCiDialog.setContentView(view, params);*/
                    mQuCiDialog.setContentView(R.layout.qucidialog);
                    mQuCiDialog.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (mTextView != null) {
                                mTextView.selfUpdateUnSelected();
                                if (mSelectEnable) {
                                    mTextView.setDragEnable(true);
                                } else {
                                    mTextView.setDragEnable(false);
                                }
                            }

                            //mQuCiDialog = null;
                        }
                    });
                }

                View quciDialogView = mQuCiDialog.findViewById(R.id.quci_dialog);

                TextView quciDialogTitle = (TextView) quciDialogView.findViewById(R.id.quci_dialog_title);
                quciDialogTitle.setText(selectText);
                quciDialogTitle.setSelected(true);

                ListView quciDialogList = (ListView) quciDialogView.findViewById(R.id.quci_dialog_list);
                quciDialogList.setSelector(R.drawable.quci_item_selector);

                final int dictType = asmGetDictType(selectText);
                int dictNameListId = 0;

                if (dictType == AsmConstant.DICT_TYPE_ENG)            // 英语字典类型
                {
                    dictNameListId = R.array.dict_select_eng_list;
                } else if (dictType == AsmConstant.DICT_TYPE_CHI)        // 中文字典类型
                {
                    dictNameListId = R.array.dict_select_chi_list;
                } else if (dictType == AsmConstant.DICT_TYPE_ERROR)    // 未知类型
                {
                    if (mTextView != null) {
                        // 取消文本的选中状态
                        mTextView.selfUpdateUnSelected();
                    }

                    return;
                }

                ArrayAdapter<CharSequence> adapter =
                        ArrayAdapter.createFromResource(mContext, dictNameListId, R.layout.quciitem);
                quciDialogList.setAdapter(adapter);
                quciDialogList.setSelection(0);
                quciDialogList.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int[] ids = null;
                        if (dictType == AsmConstant.DICT_TYPE_CHI) {
                            ids = mResources.getIntArray(R.array.dict_select_chi_id);
                        } else if (dictType == AsmConstant.DICT_TYPE_ENG) {
                            ids = mResources.getIntArray(R.array.dict_search_eng_id);
                        }

                        // 关掉先前存在的取词对话框
                        if (mQuCiDialog != null && mQuCiDialog.isShowing()) {
                            mQuCiDialog.dismiss();
                        }

                        if (ids != null) {
                            Intent intent = new Intent("com.readboy.Dictionary.DICTSEARCH");
                            intent.putExtra("dict_id", ids[position]);
                            intent.putExtra("dict_key", selectText);

                            try {
                                //mContext.startActivity(intent);
                                mContext.launchForResult(intent, -1);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();

                                Toast.makeText(mContext, "取词功能不可用！！！\n请检查词典是否被停用！！！",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
		        
		        /*Window window = mQuCiDialog.getWindow();
		        WindowManager.LayoutParams params = window.getAttributes();
		        params.x = x;
		        params.y = y;
		        params.horizontalWeight = 136;
		        params.verticalWeight = 181;
		        window.setAttributes(params);*/

                if (mQuCiDialog != null) {
                    mQuCiDialog.show();
                }
                if (mTextView != null) {
                    mTextView.setDragEnable(false);
                }
            }
        });
    }

    /**
     * ASM Timer初始化。
     *
     * @param delay  延时
     * @param period 间隔
     */
    private void asmInitTimer(int delay, int period) {
        //Log.i(TAG, "[asmInitTimer] Start!!!");

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!mIsAppPause) {
                    if (mTimer != null && mHandler != null) {
                        mHandler.sendEmptyMessage(AsmConstant.ASM_AVI_TIMER_ID);
                    }
                } else {
                    // 停止定时器
                    if (mTimer != null) {
                        mIsTimerPause = true;

                        mTimer.cancel();
                        mTimer = null;
                    }
                }
            }
        }, delay, period);

        //Log.i(TAG, "[asmInitTimer] End!!!");
    }

    /**
     * ASM Handler初始化。
     */
//	@SuppressLint("HandlerLeak")
    private void asmInitHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //Log.i(TAG, "[asmInitHandler] HandleMessage!!!");

                switch (msg.what) {
                    // 播放Avi的定时器消息ID
                    case AsmConstant.ASM_AVI_TIMER_ID: {
                        if (mData.data_type == AsmConstant.DataType2) {
                            mMacroData.asmPlayAviAndAnimationServiceLoop();

                            if (mMacroData.asmGetAviStatus()) {
                                // 停止播放Avi
                                asmStopAvi();

                                // 自动播放MenuType3的下一项
                                if (mData.pMenuType3 != null) {
                                    //Log.i(TAG, "[asmInitHandler] HandleMessage Auto Play Next!!!");

                                    mData.pMenuType3.select_item += 1;
                                    if (mData.pMenuType3.select_item < mData.pMenuType3.total_item) {
                                        asmMenuType3OnClick(mMainView, mData.pMenuType3.select_item);
                                    }
                                }
                            }
                        }
                    }
                    break;

                    // 初始化菜单消息ID
                    case AsmConstant.LIST_ID_MIN: {
                        if (mData.pMenuType1 != null) {
                            // 初始化图片列表菜单
                            asmInitMenuType1(mData.pMenuType1, mMenuListView);
                        } else if (mData.pMenuType2 != null) {
                            // 初始化文字列表菜单
                            asmInitMenuType2(mData.pMenuType2, mTextListView);
                        } else if (mData.pMenuType3 != null) {
                            // 初始化图片按钮菜单
                            asmInitMenuType3(mData.pMenuType3, mMainView);
                        }
                    }
                    break;

                    // 图片列表菜单消息ID
                    case AsmConstant.MENUTYPE1_LIST_ID: {
                        asmMenuType1OnItemClick(mMainView, msg.arg1);
                    }
                    break;

                    // 文字列表菜单消息ID
                    case AsmConstant.MENUTYPE2_LIST_ID: {
                        asmMenuType2OnItemClick(mMainView, msg.arg1);
                    }
                    break;

                    // 图片按钮菜单消息ID
                    case AsmConstant.MENUTYPE3_LIST_ID: {
                        asmMenuType3OnClick(mMainView, msg.arg1);
                    }
                    break;

                    // 显示文本消息ID
                    case AsmConstant.ASM_TEXT_SHOW_ID: {
                        //mContext.dismissDialog(0);

                        String text = (String) msg.obj;

                        int length = text.length();
                        Log.i(TAG, "[asmInitHandler] Text Length =1= " + length);
							
							/*if(length > 5000)
							{
								mTextView.setText(text.substring(0, 5000));
							}
							else*/
                        {
                            mTextView.setTextCtn(text);
                        }
                        Log.i(TAG, "[asmInitHandler] Text Length =2= " + length);
                    }
                    break;

                    default:
                        break;
                }
            }
        };
    }

    /**
     * ASM GestureDetector初始化。
     */
    private void asmInitGestureDetector() {
        mGestureDetector = new GestureDetector(mContext, new OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                //Log.i(TAG, "[asmGestureDetector] onDown");

                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                //Log.i(TAG, "[asmGestureDetector] onShowPress");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //Log.i(TAG, "[asmGestureDetector] onSingleTapUp");

                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //Log.i(TAG, "[asmGestureDetector] onScroll");

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                //Log.i(TAG, "[asmGestureDetector] onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //Log.i(TAG, "[asmGestureDetector] onFling");

                if (mData == null || e1 == null || e2 == null) {
                    return false;
                }

                // 手势由下向上滑动
                if ((e1.getY() - e2.getY() > AsmConstant.ASM_FLIP_DISTANCE) ||
                        // 手势由右向左滑动
                        (e1.getX() - e2.getX() > AsmConstant.ASM_FLIP_DISTANCE)) {
                    //Log.i(TAG, "[asmGestureDetector] onFling Down To Up");

                    if (mData.data_type == AsmConstant.DataType3)        // 题目+答案
                    {
                        //asmDataType3ButtonOnClick(mMainView, true);
                    } else if (mData.data_type == AsmConstant.DataType5)    // 三字经、弟子规、千字文
                    {
                        android.util.Log.i(TAG, "onFling: 11111");
                        asmInitDataType5Text(mData.pDataType5, mTextView, true);
                    }
                }
                // 手势由上向下滑动
                else if ((e2.getY() - e1.getY() > AsmConstant.ASM_FLIP_DISTANCE) ||
                        // 手势由左向右滑动
                        (e2.getX() - e1.getX() > AsmConstant.ASM_FLIP_DISTANCE)) {
                    //Log.i(TAG, "[asmGestureDetector] onFling Up To Down");

                    if (mData.data_type == AsmConstant.DataType3)        // 题目+答案
                    {
                        //asmDataType3ButtonOnClick(mMainView, false);
                    } else if (mData.data_type == AsmConstant.DataType5)    // 三字经、弟子规、千字文
                    {
                        android.util.Log.i(TAG, "onFling: 2222");
                        asmInitDataType5Text(mData.pDataType5, mTextView, false);
                    }
                }

                return false;
            }

        }, null, true);
    }

    /**
     * 给Handler发送消息。
     *
     * @param handler Handler
     * @param what    what
     * @param arg1    arg1
     * @param arg2    arg2
     * @param delay   延时
     */
    private void asmSendMessageDelayed(Handler handler, int what, int arg1, int arg2, int delay) {
        Message msg = new Message();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        handler.removeMessages(what);
        handler.sendMessageDelayed(msg, delay);
    }

    /**
     * 获取数据文件名。
     *
     * @return 数据文件名
     */
    private String asmGetDataName() {
        String dataname = null;
        String path = null;

        // 从启动Activity的启动参数Intent中获取data_name
        String name = mContext.getIntent().getStringExtra("data_name");

        if (name == null) {
            // 默认进入“成语典故”应用
//			name = mResources.getString(R.string.data_name_default);
            name = "quweituxing.pin";
        }

        Log.i(TAG, "[asmGetDataName] name == " + name);

        // 一、先在/mnt/sdcard/asm/目录下寻找*.pin数据文件
        try {
            // "/mnt/sdcard"
            path = Environment.getExternalStorageDirectory().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();

            path = "/mnt/sdcard/.readboy";
        }
        path += "/.readboy";
        path += "/asm/";
        dataname = path + name;
        if (new File(dataname).exists()) {
            Log.i(TAG, "[asmGetDataName] DataName =1= " + dataname);

            return dataname;
        }

        // 二、在/system/readboy/asm/目录下寻找*.pin数据文件
        path = "/system/readboy";
        path += "/asm/";
        dataname = path + name;
        if (new File(dataname).exists()) {
            Log.i(TAG, "[asmGetDataName] DataName =2= " + dataname);

            return dataname;
        }

        // 三、在/system/lib/目录下寻找lib*.so数据文件
        path = "/system/lib/";
        dataname = path + "lib" + name.replace(".pin", ".so");
        if (new File(dataname).exists()) {
            Log.i(TAG, "[asmGetDataName] DataName =3= " + dataname);

            return dataname;
        }

        // 四、在应用程序目录下寻找lib*.so数据文件
        path = mContext.getFilesDir().getPath().replace("files", "lib") + "/";
        dataname = path + "lib" + name.replace(".pin", ".so");
        if (new File(dataname).exists()) {
            Log.i(TAG, "[asmGetDataName] DataName =4= " + dataname);

            return dataname;
        }

        // 将数据文件从assets释放到应用程序文件夹下
		/*Log.i(TAG, "GetDataFile Start!!!");
		try
		{
			File file = new File(dataname);
			
			if(!file.exists())
			{
				//Log.i(TAG, "Dir == " + mContext.getFilesDir());
				//Log.i(TAG, "Name == " + name);
				
				FileOutputStream outputStream = new FileOutputStream(file);
				InputStream inputStream = mResources.getAssets().open(name);
				
				byte[] buffer = new byte[1024*4];// 4K缓存
				int len = 0;
				while((len = inputStream.read(buffer)) != -1)
				{
					outputStream.write(buffer, 0, len);
				}
				outputStream.flush();
				
				inputStream.close();
				outputStream.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		Log.i(TAG, "GetDataFile End!!!");*/

        Log.i(TAG, "[asmGetDataName] DataName =0= " + dataname);

        return dataname;
    }

    /**
     * 设定控件的显示位置。（仅适用于相对布局中的子控件）
     *
     * @param view 要设定显示位置的控件。
     * @param rect 显示位置
     */
    private void asmSetViewPosition(View view, Rect rect) {
        if (rect != null) {
            Log.i(TAG, "[asmSetViewPosition] left == =======" + rect.left + ", top == " + rect.top +
                    ", right == " + rect.right + ", bottom == " + rect.bottom);

            LayoutParams params =
                    new LayoutParams(rect.right - rect.left, rect.bottom - rect.top);
            params.leftMargin = rect.left;
            params.topMargin = rect.top;
            view.setLayoutParams(params);
        } else {
            view.setLayoutParams(new LayoutParams(0, 0));
        }
    }

    /**
     * 设定控件的显示位置。（仅适用于相对布局中的子控件）
     *
     * @param view 要设定显示位置的控件。
     * @param x    X
     * @param y    Y
     * @param w    Width
     * @param h    Height
     */
    private void asmSetViewPosition(View view, int x, int y, int w, int h) {
        //Log.i(TAG, "[asmSetViewPosition] x == " + x + ", y == " + y + ", w == " + w + ", h == " + h);

        LayoutParams params = new LayoutParams(w, h);
        params.leftMargin = x;
        params.topMargin = y;
        //ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(w, h);
        //marginParams.leftMargin = x;
        //marginParams.topMargin = y;
        view.setLayoutParams(params);
    }

    /**
     * 播放Avi。
     *
     * @param aviAddr Avi地址
     * @return 成功返回true，失败返回false
     */
    private boolean asmPlayAvi(int aviAddr) {
        //Log.i(TAG, "[asmPlayAvi] Start!!!");
        Log.i(TAG, "[asmInitDataType2] Start!!!111111111");

        mAnimview.clearDraw();

        // 刷一次背景，把上一次的画面覆盖掉
        if (mData.hdcBk != null) {
            mAnimview.doDrawBitmap(mData.hdcBk, mData.x_bk, mData.y_bk);

        }
        if (mData.pMenuType3 != null) {
            mAnimview.doDrawBitmap(mData.pMenuType3.hdcBk, mData.pMenuType3.x_bk, mData.pMenuType3.y_bk);

        }

        // 语音和图形播放初始化
        mMacroData.asmAviAndSpeechInitOnce();

        if (mMpRaf == null) {
            try {
                mMpRaf = new RandomAccessFile(mDataName, "r");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Avi播放初始化
        mMacroData.asmPlayAviInit(mDataName, mMpRaf, mData.fp,
                aviAddr, mData.ExternalDataAddr, mSound, mAnimview);

        // 播放Avi
        mMacroData.asmPlayAvi();

        // 控制播放动画的定时器
        asmInitTimer(0, AsmConstant.ASM_AVI_TIMER_PERIOD);

        //Log.i(TAG, "[asmPlayAvi] End!!!");
        Log.i(TAG, "[asmInitDataType2] End!!!");

        return true;
    }

    /**
     * 停止播放Avi。
     */
    private void asmStopAvi() {
        //Log.i(TAG, "[asmStopAvi] Start!!!");

        if (mHandler != null) {
            mHandler.removeMessages(AsmConstant.ASM_AVI_TIMER_ID);
        }

        // 停止定时器
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        // 停止播放声音
        if (mSound != null && mSound.isPlaying()) {
            mSound.stop();
        }

        //Log.i(TAG, "[asmStopAvi] End!!!");
    }

    /**
     * 播放声音。
     *
     * @param addr 声音地址
     * @return 成功返回true，失败返回false
     */
    private boolean asmPlaySound(int addr) {
        //Log.i(TAG, "[asmPlaySound] Start!!!");

        try {
            if (mMpRaf == null) {
                mMpRaf = new RandomAccessFile(mDataName, "r");
            }

            mMpRaf.seek(addr);

            int checkChar = mData.asmReadNBytesToNum(mMpRaf, 0, 1);
            //Log.i(TAG, "[asmPlaySound]" + " checkChar == " + checkChar);

            if (checkChar == 0xFF) {
                Log.i(TAG, "[asmPlaySound]" + " No Sound To Play!!!");

                return true;
            }

            mMpRaf.seek(addr);

            int index = mData.asmReadNBytesToNum(mMpRaf, 0, 3);
            //Log.i(TAG, "[asmPlaySound]" + " index 111== " + index);

            index = mData.asmGetIndex(index);
            //Log.i(TAG, "[asmPlaySound]" + " index 222== " + index);

            mMpRaf.seek(mData.ExternalDataAddr + 4 * index);

            int addr1 = mData.asmReadNBytesToNum(mMpRaf, 0, 4);
            int addr2 = mData.asmReadNBytesToNum(mMpRaf, 0, 4);
            int size = addr2 - addr1;
            int offset = mData.ExternalDataAddr + addr1;
            //Log.i(TAG, "[asmPlaySound]" + " addr1 == " + addr1 +
            //	", addr2 == " + addr2 + ", sound size == " + size);

            //mMediaPlayer.reset();
            mMpRaf.seek(0);
            android.util.Log.i(TAG, "asmPlaySound: "+offset+","+size);
            mSound.setDataSource(mMpRaf.getFD(), offset, size);
            //mMediaPlayer.prepare();
            if (!mIsAppPause) {
                mSound.start();
            } else {
                mIsSoundPause = true;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        //Log.i(TAG, "[asmPlaySound] End!!!");

        return true;
    }

    public String getPath(String name) throws IOException {
        File f1 = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/guoxuejingdian/music/");
        if (!f1.exists()) {
            f1.mkdirs();
        }
        name = name + ".mp3";
        File f = new File(f1, name);
        if (!f.exists()) {
            f.createNewFile();
            f.setReadable(true);
        }
        return f.getAbsolutePath();
    }

    /**
     * 停止播放声音。
     */
    @SuppressWarnings("unused")
    private void asmStopSound() {
        if (mSound != null && mSound.isPlaying()) {
            mSound.stop();
        }
    }

    /**
     * 处理声音播放结束事件。
     *
     * @param sound Sound
     */
    private void asmOnSoundPlayEnd(Sound sound) {
        //Log.i(TAG, "[asmOnSoundPlayEnd] Start!!!");

        if (mData.data_type == AsmConstant.DataType4)        // 百家姓
        {
            asmInitDataType4Text(mData.pDataType4, mTextListView);
        } else if (mData.data_type == AsmConstant.DataType5)    // 三字经、弟子规、千字文
        {
            asmInitDataType5Text(mData.pDataType5, mTextView, true);
        }

        //Log.i(TAG, "[asmOnSoundPlayEnd] End!!!");
    }

    /**
     * 初始化图片菜单。
     *
     * @param pMenuType1 SMenuType1
     * @param listView   ListView
     */
    private void asmInitMenuType1(AsmData.AsmMenuType1 pMenuType1, ListView listView) {
        Log.i(TAG, "[asmInitMenuType1] Start!!!");

        // 清除MenuType3的一组按钮
        asmHideMenuType3TeamButton(mMainView);

        // 设定显示位置
        int w = pMenuType1.rect.right - pMenuType1.rect.left + 1;
        int h = 0;

        if (pMenuType1.total_item <= pMenuType1.screen_item) {
            h = pMenuType1.rect.bottom - pMenuType1.rect.top + pMenuType1.screen_item;
        } else {
            h = pMenuType1.rect.bottom - pMenuType1.rect.top + 1;
        }

        asmSetViewPosition(listView, pMenuType1.rect.left, pMenuType1.rect.top, w, h);
        //asmSetViewPosition(listView, pMenuType1.rect);
        android.util.Log.i(TAG, "asmInitMenuType1: h =" + h + ",w = " + w + "left = " + pMenuType1.rect.left + "top = " + pMenuType1.rect.top);
        // 绘制背景
        if (mData.hdcBk != null) {
            mAnimview.doDrawBitmap(mData.hdcBk, mData.x_bk, mData.y_bk);
        }
        if (pMenuType1.hdcBk != null) {
            mAnimview.doDrawBitmap(pMenuType1.hdcBk, pMenuType1.x_bk, pMenuType1.y_bk);
        }

        // 添加列表内容
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                //Log.i(TAG, "[asmInitMenuType1] getCount == " + pMenuType1.total_item);

                return mData.pMenuType1.total_item;
            }

            @Override
            public Object getItem(int position) {
                //Log.i(TAG, "[asmInitMenuType1] getItem == " + position);

                return mData.pMenuType1.first_pic[position];
            }

            @Override
            public long getItemId(int position) {
                //Log.i(TAG, "[asmInitMenuType1] getItemId == " + position);

                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //Log.i(TAG, "[asmInitMenuType1] getView == " + position);

                ImageView imageView = new ImageView(mContext);

                // 设定列表每项的宽、高
                int h = 0;
                if (mData.pMenuType1.total_item <= mData.pMenuType1.screen_item) {
                    h = (mData.pMenuType1.rect.bottom - mData.pMenuType1.rect.top + mData.pMenuType1.screen_item)
                            / mData.pMenuType1.screen_item;
                } else {
                    h = (mData.pMenuType1.rect.bottom - mData.pMenuType1.rect.top + 1) /
                            mData.pMenuType1.screen_item;
                }

                AbsListView.LayoutParams params =
                        new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, h);

                //AbsListView.LayoutParams params =
                //		new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                //				ViewGroup.LayoutParams.WRAP_CONTENT);

                imageView.setLayoutParams(params);

                StateListDrawable drawable = new StateListDrawable();

                drawable.addState(new int[]{android.R.attr.state_activated},        // 激活状态的图片
                        new BitmapDrawable(mResources, mData.pMenuType1.last_pic[position]));
                drawable.addState(new int[]{android.R.attr.state_selected},            // 选中状态的图片
                        new BitmapDrawable(mResources, mData.pMenuType1.second_pic[position]));
                //drawable.addState(new int[]{android.R.attr.state_checked},		// 勾选状态的图片
                //		new BitmapDrawable(mResources, pMenuType1.last_pic[position]));
                //drawable.addState(new int[]{android.R.attr.state_pressed},		// 按下状态的图片
                //		new BitmapDrawable(mResources, mData.pMenuType1.second_pic[position]));
                drawable.addState(new int[]{},                                        // 正常状态的图片
                        new BitmapDrawable(mResources, mData.pMenuType1.first_pic[position]));

                imageView.setImageDrawable(drawable);

                return imageView;
            }
        });

        // 点击监听器
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Utils.isFastDoubleClick()) {
                    //Log.i(TAG, "[asmInitMenuType1] onItemClick position == " + position);

                    mData.pMenuType1.select_item = position;

                    // 隐藏所有的子控件
                    asmHideAllView();

                    asmSendMessageDelayed(mHandler, AsmConstant.MENUTYPE1_LIST_ID, position, 0,
                            AsmConstant.ASM_DELAY_TIME);
                }
            }
        });

        // 选择监听器
        listView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.i(TAG, "[asmInitMenuType1] onItemSelected position == " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Log.i(TAG, "[asmInitMenuType1] onNothingSelected");
            }
        });

        // 键盘监听器
        listView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Log.i(TAG, "[asmInitMenuType1] onKey keyCode == "  + keyCode + ", KeyEvent == " + event);

                return false;
            }
        });

        // 设置列表背景颜色
        StateListDrawable drawable = new StateListDrawable();

        drawable.addState(new int[]{android.R.attr.state_activated},    // 激活（选中）状态
                new ColorDrawable(Color.TRANSPARENT));
        drawable.addState(new int[]{android.R.attr.state_pressed},        // 点击状态
                new ColorDrawable(Color.TRANSPARENT));
        drawable.addState(new int[]{},                                    // 正常状态
                new ColorDrawable(Color.TRANSPARENT));

        listView.setSelector(drawable);

        // 默认选中第一项
        //listView.requestFocus();
        //listView.setSelected(true);
        listView.setItemChecked(0, true);
        //listView.setSelection(0);
        listView.smoothScrollToPosition(0);

        pMenuType1.select_item = 0;
        //asmMenuType1OnItemClick(mMainView, 0);
        asmSendMessageDelayed(mHandler, AsmConstant.MENUTYPE1_LIST_ID, 0, 0, AsmConstant.ASM_DELAY_TIME);

        Log.i(TAG, "[asmInitMenuType1] End!!!");
    }

    /**
     * 初始化文字菜单。
     *
     * @param pMenuType2 SMenuType2
     * @param listView   ListView
     */
    private void asmInitMenuType2(AsmData.AsmMenuType2 pMenuType2, ListView listView) {
        Log.i(TAG, "[asmInitMenuType2] Start!!!");

        //隐藏输入法
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mContext.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        // 关掉先前存在的取词对话框
        if (mQuCiDialog != null && mQuCiDialog.isShowing()) {
            mQuCiDialog.dismiss();
        }

        // 隐藏DataType1
        asmHideDataType1(mScrollView, mTextView);

        // 清除MenuType3的一组按钮
        asmHideMenuType3TeamButton(mMainView);

        // 设定显示位置
        asmSetViewPosition(listView, pMenuType2.rect);

        // 绘制背景
        if (mData.hdcBk != null) {
            mAnimview.doDrawBitmap(mData.hdcBk, mData.x_bk, mData.y_bk);
        }
        if (pMenuType2.hdcBk != null) {
            mAnimview.doDrawBitmap(pMenuType2.hdcBk, pMenuType2.x_bk, pMenuType2.y_bk);
        }

        mMenuType2Adapter = new MenuType2Adapter(mContext);
        for (int i = 0; i < mData.pMenuType2.total_item; i++) {
            mMenuType2Adapter.mHashMap.put(i, i);
        }

        listView.setAdapter(mMenuType2Adapter);

        // 添加列表内容
		/*listView.setAdapter(new BaseAdapter()
		{
			@Override
			public int getCount()
			{
				//Log.i(TAG, "[asmInitMenuType2] getCount == " + pMenuType2.total_item);
				
				return mData.pMenuType2.total_item;
			}

			@Override
			public Object getItem(int position)
			{
				//Log.i(TAG, "[asmInitMenuType2] getItem == " + position);
				
				//return mData.pMenuType2.ItemText[position];
				return mData.pMenuType2.ItemName[position];
			}

			@Override
			public long getItemId(int position)
			{
				//Log.i(TAG, "[asmInitMenuType2] getItemId == " + position);
				
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				//Log.i(TAG, "[asmInitMenuType2] getView == " + position);
				
				TextView textView = new TextView(mContext);
				
				// 设定每项的宽、高
				AbsListView.LayoutParams params = 
						new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
								AsmConstant.MenuType2_HEIGHT);
				textView.setLayoutParams(params);
				
				// 设置AsmTextView背景颜色
				StateListDrawable drawable = new StateListDrawable();
				
				drawable.addState(new int[]{android.R.attr.state_activated}, 	// 激活（选中）状态
						new ColorDrawable(Color.TRANSPARENT));
				drawable.addState(new int[]{android.R.attr.state_pressed},		// 点击状态
						new ColorDrawable(Color.TRANSPARENT));
				drawable.addState(new int[]{android.R.attr.state_selected}, 	// 选中状态
						mResources.getDrawable(R.drawable.list_select));
				drawable.addState(new int[]{}, 									// 正常状态
						new ColorDrawable(Color.TRANSPARENT));
				
				textView.setBackgroundDrawable(drawable);
				
				// 设置AsmTextView字体颜色
				ColorStateList colors = new ColorStateList(
						new int[][]{						// 状态
								new int[]{android.R.attr.state_activated},		// 激活（选中）状态
								new int[]{android.R.attr.state_pressed},		// 点击状态
								new int[]{}},									// 正常状态
						new int[]{							// 颜色
								Color.RED, 
								Color.RED, 
								Color.BLACK});
				
				textView.setTextColor(colors);
				
				// 设置字体大小
				//textView.setTextSize(AsmConstant.ASM_FONT_SIZE);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, AsmConstant.ASM_FONT_SIZE);
				
				// 设置文字
				//String text = asmGetString(mData.pMenuType2.ItemText[position], 0, AsmConstant.ASM_ASCII_CODE);
				textView.setText(mData.pMenuType2.ItemName[position]);
				
				return textView;
			}
		});*/

        mMenuType2OnItemClickListener = new MenuType2OnItemClickListener();

        listView.setOnItemClickListener(mMenuType2OnItemClickListener);

        // 点击监听器
		/*listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if(!Utils.isFastDoubleClick())
				{
					Log.i(TAG, "[asmInitMenuType2] onItemClick position == " + position);
					
					// 保存MemuType2状态
					if(mMemuType2LevelSave < AsmConstant.MenuType2_LEVEL_MAX)
					{
						mMenuType2Save[mMemuType2LevelSave].addr = mData.pMenuType2.self_addr;
						mMenuType2Save[mMemuType2LevelSave].item_sel = mTextListView.getCheckedItemPosition();
						mMenuType2Save[mMemuType2LevelSave].item_top = mTextListView.getFirstVisiblePosition();
						
						mMemuType2LevelSave++;
						
						//Log.i(TAG, "asmMenuType2OnItemClick " + 
						//		"FirstVisiblePosition == " + mTextListView.getFirstVisiblePosition() + 
						//		", CheckedItemPosition == " + mTextListView.getCheckedItemPosition() + 
						//		", mSaveMemuType2Level == " + mSaveMemuType2Level);
					}
					
					// 隐藏所有的子控件
					asmHideAllView();
					
					asmSendMessageDelayed(mHandler, AsmConstant.MENUTYPE2_LIST_ID, position, 0, 
							AsmConstant.ASM_DELAY_TIME);
				}
			}
		});*/

        // 设置列表背景颜色
        StateListDrawable drawable = new StateListDrawable();

        drawable.addState(new int[]{android.R.attr.state_activated},    // 激活（选中）状态
                new ColorDrawable(Color.TRANSPARENT));
        drawable.addState(new int[]{android.R.attr.state_pressed},        // 点击状态
                new ColorDrawable(Color.TRANSPARENT));
        drawable.addState(new int[]{},                                    // 正常状态
                new ColorDrawable(Color.TRANSPARENT));

        listView.setSelector(drawable);

        listView.setVisibility(View.VISIBLE);

        mBtnSearch.setVisibility(View.VISIBLE);
        mImgSearch.setVisibility(View.VISIBLE);
        mImgSearch.setBackgroundResource(R.drawable.img_search_empty);
        mTxtSearch.setVisibility(View.VISIBLE);
        //mTxtSearch.requestFocus();
        mTxtSearch.setThreshold(1);
        mTxtSearch.setText("");
        mTxtAdapter = new AutoCompleteTextViewAdapter(mContext);
        mTxtSearch.setAdapter(mTxtAdapter);
        mTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "[asmInitMenuType2] beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "[asmInitMenuType2] onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "[asmInitMenuType2] afterTextChanged");

                String input = s.toString();
                mTxtAdapter.mList.clear();
                for (int i = 0; i < mData.pMenuType2.total_item; i++) {
                    if (mData.pMenuType2.ItemName[i].contains(input)) {
                        mTxtAdapter.mList.add(mData.pMenuType2.ItemName[i]);
                    }
                }
                mTxtAdapter.notifyDataSetChanged();
                mTxtSearch.showDropDown();
            }
        });

        mBtnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mTxtSearch.getText().toString();
                if (!input.isEmpty()) {
                    //隐藏输入法
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (mContext.getCurrentFocus() != null) {
                        imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    mMenuType2Adapter.mHashMap.clear();
                    for (int i = 0, j = 0; i < mData.pMenuType2.total_item; i++) {
                        if (mData.pMenuType2.ItemName[i].contains(input)) {
                            mMenuType2Adapter.mHashMap.put(j, i);
                            j++;
                        }
                    }
                    if (!mMenuType2Adapter.mHashMap.isEmpty()) {
                        mMenuType2Adapter.notifyDataSetChanged();
                    } else {
                        for (int i = 0; i < mData.pMenuType2.total_item; i++) {
                            mMenuType2Adapter.mHashMap.put(i, i);
                        }
                        mMenuType2Adapter.notifyDataSetChanged();

                        if (mSearchDialog != null) {
                            return;
                        }

                        mSearchDialog = new Dialog(mContext, R.style.SearchDialogTheme);
                        mSearchDialog.setCancelable(true);
                        mSearchDialog.setCanceledOnTouchOutside(true);
                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        View view = inflater.inflate(R.layout.searchdialog, null);
                        int w = mContext.getResources().getDimensionPixelSize(R.dimen.panel_w);
                        int h = mContext.getResources().getDimensionPixelSize(R.dimen.panel_h);
                        LayoutParams params = new LayoutParams(w, h);
                        mSearchDialog.setContentView(view, params);
                        mSearchDialog.setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mSearchDialog = null;
                            }
                        });
                        Button btnConfirm = (Button) view.findViewById(R.id.btn_search_confirm);
                        btnConfirm.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSearchDialog.dismiss();
                            }
                        });
                        mSearchDialog.show();
                    }
                }
            }

        });

        Log.i(TAG, "[asmInitMenuType2] End!!!");
    }

    /**
     * 隐藏文字菜单。
     *
     * @param listView ListView
     */
    private void asmHideMenuType2View(ListView listView) {
        listView.setAdapter(null);
        asmSetViewPosition(listView, null);
        listView.setVisibility(View.INVISIBLE);

        mBtnSearch.setVisibility(View.INVISIBLE);
        mImgSearch.setVisibility(View.INVISIBLE);
        mTxtSearch.setVisibility(View.INVISIBLE);
        mTxtSearch.setText("");
    }

    /**
     * 初始化按钮菜单。
     *
     * @param pMenuType3 SMenuType3
     * @param view       ViewGroup
     */
    private void asmInitMenuType3(AsmData.AsmMenuType3 pMenuType3, ViewGroup view) {
        Log.i(TAG, "[asmInitMenuType3] Start!!!");

        // 绘制背景
        if (mData.hdcBk != null) {
            mAnimview.doDrawBitmap(mData.hdcBk, mData.x_bk, mData.y_bk);
        }
        if (pMenuType3.hdcBk != null) {
            mAnimview.doDrawBitmap(pMenuType3.hdcBk, pMenuType3.x_bk, pMenuType3.y_bk);
        }

        //Log.e(TAG, "[asmInitMenuType3] x1 == " + mData.x_bk + ", y1 == " + mData.y_bk +
        //		"; x2 == " + pMenuType3.x_bk + ", y2 == " + pMenuType3.y_bk);

        // 初始化MenuType3的一组按钮
        asmInitMenuType3TeamButton(pMenuType3, view);

        Log.i(TAG, "[asmInitMenuType3] End!!!");
    }

    /**
     * 初始化MenuType3的一组按钮。
     *
     * @param pMenuType3 SMenuType3
     * @param view       ViewGroup
     */
    private void asmInitMenuType3TeamButton(AsmData.AsmMenuType3 pMenuType3, ViewGroup view) {
        // 清除MenuType3的一组按钮
        asmHideMenuType3TeamButton(view);

        // 显示按钮组
        for (int i = 0; i < pMenuType3.total_item; i++) {
            ImageButton imageButton = new ImageButton(mContext);
            imageButton.setBackgroundColor(Color.TRANSPARENT);

            StateListDrawable drawable = new StateListDrawable();

            drawable.addState(new int[]{android.R.attr.state_pressed},                // 按下状态的图片
                    new BitmapDrawable(mResources, pMenuType3.second_pic[i]));
            drawable.addState(new int[]{android.R.attr.state_focused},                // 聚焦状态的图片
                    new BitmapDrawable(mResources, pMenuType3.second_pic[i]));
            drawable.addState(new int[]{android.R.attr.state_hovered},                // 划过状态的图片
                    new BitmapDrawable(mResources, pMenuType3.second_pic[i]));
            drawable.addState(new int[]{},                                            // 正常状态的图片
                    new BitmapDrawable(mResources, pMenuType3.first_pic[i]));

            imageButton.setImageDrawable(drawable);

            ViewGroup.LayoutParams params =
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            imageButton.setLayoutParams(params);

            view.addView(imageButton);
            //Log.e(TAG, "Left == " + pMenuType3.rect[i].left + ", Top == " + pMenuType3.rect[i].top +
            //		", Right == " + pMenuType3.rect[i].right +", Bottom == " + pMenuType3.rect[i].bottom);
            if (pMenuType3.first_pic[i] != null) {
                // 数据里面定义的坐标有问题，宽度小于图片的实际宽度，所以要在代码里面处理
                pMenuType3.rect[i].right = pMenuType3.rect[i].left + pMenuType3.first_pic[i].getWidth();
                pMenuType3.rect[i].bottom = pMenuType3.rect[i].top + pMenuType3.first_pic[i].getHeight();
            }
            asmSetViewPosition(imageButton, pMenuType3.rect[i]);
            //Log.e(TAG, "W == " + pMenuType3.first_pic[i].getWidth() + ", H == " + pMenuType3.first_pic[i].getHeight());
            //Log.e(TAG, "Left == " + pMenuType3.rect[i].left + ", Top == " + pMenuType3.rect[i].top +
            //		", Right == " + pMenuType3.rect[i].right + ", Bottom == " + pMenuType3.rect[i].bottom);

            //Log.e(TAG, "----------------------------------------------------------------------------------");

            // 设置按钮的ID
            imageButton.setId(AsmConstant.TEAM_BUTTON_ID_MIN + i);

            mMenuType3List.add(imageButton);

            imageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.isFastDoubleClick()) {
                        mData.pMenuType3.select_item = v.getId() - AsmConstant.TEAM_BUTTON_ID_MIN;

                        // 隐藏所有的子控件
                        asmHideAllView();

                        asmSendMessageDelayed(mHandler, AsmConstant.MENUTYPE3_LIST_ID,
                                mData.pMenuType3.select_item, 0, AsmConstant.ASM_DELAY_TIME);
                    }
                }
            });
        }

        // 默认选中第一项
        mMenuType3Sel = new ImageButton(mContext);
        mMenuType3Sel.setBackgroundColor(Color.TRANSPARENT);
        mMenuType3Sel.setImageDrawable(new BitmapDrawable(mResources, pMenuType3.last_pic[0]));

        view.addView(mMenuType3Sel);
        if (pMenuType3.first_pic[0] != null) {
            // 数据里面定义的坐标有问题，宽度小于图片的实际宽度，所以要在代码里面处理
            pMenuType3.rect[0].right = pMenuType3.rect[0].left + pMenuType3.first_pic[0].getWidth();
            pMenuType3.rect[0].bottom = pMenuType3.rect[0].top + pMenuType3.first_pic[0].getHeight();
        }
        asmSetViewPosition(mMenuType3Sel, pMenuType3.rect[0]);

        // 响应第一项的点击事件
        mData.pMenuType3.select_item = 0;
        //asmMenuType3OnClick(mMainView, 0);
        asmSendMessageDelayed(mHandler, AsmConstant.MENUTYPE3_LIST_ID, 0, 0, AsmConstant.ASM_DELAY_TIME);
    }

    /**
     * 清除MenuType3的一组按钮。
     *
     * @param view ViewGroup
     */
    private void asmHideMenuType3TeamButton(ViewGroup view) {
        // 清空之前显示的按钮
        if (!mMenuType3List.isEmpty()) {
            for (int i = 0; i < mMenuType3List.size(); i++) {
                //mMenuType3List.get(i).setVisibility(View.GONE);
                view.removeView(mMenuType3List.get(i));
            }

            mMenuType3List.clear();
        }

        if (mMenuType3Sel != null) {
            //mMenuType3Sel.setVisibility(View.GONE);
            view.removeView(mMenuType3Sel);
            mMenuType3Sel = null;
        }
    }

    /**
     * 初始化DataType1。
     *
     * @param pDataType1 SDataType1
     * @param textView   AsmTextView
     */
    private void asmInitDataType1(AsmData.AsmDataType1 pDataType1, AsmSteerTextView textView) {
        Log.i(TAG, "[asmInitDataType1] Start!!!");

        //隐藏输入法
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mContext.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        // 设定显示位置
        asmSetViewPosition(mScrollView, pDataType1.rect);

        // 绘制背景
        if (mData.hdcBk != null) {
            mAnimview.doDrawBitmap(mData.hdcBk, mData.x_bk, mData.y_bk);
        }
        if (pDataType1.hdcBk != null) {
            mAnimview.doDrawBitmap(pDataType1.hdcBk, pDataType1.x_bk, pDataType1.y_bk);
        }

        // 文本颜色
        //textView.setTextColor(Color.BLACK);

        // 显示文本
		/*Log.i(TAG, "[asmInitDataType1] SetText Start!!!");
		String text = null;
		if((pDataType1.text_buff[0] & 0xFF) == 0xFF && 
				(pDataType1.text_buff[1] & 0xFF) == 0xFE)				// UTF16_CODE
		{
			text = new String(AsmYBToUnicode.asmGetStr(pDataType1.text_buff, 2));
			//text = new String(new AsmLoadTextThread(pDataType1.text_buff, 2).mResult);
		}
		else															// ASCII_CODE
		{
			text = asmGetString(pDataType1.text_buff, 0, AsmConstant.ASM_ASCII_CODE);
		}
		textView.setText(text);
		Log.i(TAG, "[asmInitDataType1] SetText End!!!");*/

        new AsmLoadTextThread(mHandler, pDataType1.text_buff).start();
        //new AsmLoadTextAsyncTask(mHandler, pDataType1.text_buff).execute();

        // 显示DataType1的内容
        asmShowDataType1(mScrollView, textView);

        Log.i(TAG, "[asmInitDataType1] End!!!");
    }

    /**
     * 显示DataType1的内容。
     *
     * @param scrollView AsmScrollView
     * @param textView   AsmTextView
     */
    private void asmShowDataType1(AsmScrollView scrollView, AsmSteerTextView textView) {
        // 关掉先前存在的取词对话框
        if (mQuCiDialog != null && mQuCiDialog.isShowing()) {
            mQuCiDialog.dismiss();
        }

        // 隐藏MenuType2
        asmHideMenuType2View(mTextListView);

        textView.setVisibility(View.VISIBLE);

        //scrollView.clearAnimation();
        scrollView.scrollTo(0, 0);
        scrollView.setVisibility(View.VISIBLE);

        // 显示取词按钮
        if (mData.data_type == AsmConstant.DataType1 || mData.data_type == AsmConstant.DataType3) {
            mBtnQuCi.setBackgroundResource(R.drawable.btn_quci_nor);
            mBtnQuCi.setVisibility(View.VISIBLE);
        }

        mTextView.setDragEnable(false);
        mSelectEnable = false;
    }

    /**
     * 隐藏DataType1的内容。
     *
     * @param scrollView AsmScrollView
     * @param textView   AsmTextView
     */
    private void asmHideDataType1(AsmScrollView scrollView, AsmSteerTextView textView) {
        textView.setTextCtn("");
        textView.setVisibility(View.INVISIBLE);
        android.util.Log.i(TAG, "asmHideDataType1: ++++++");
        asmSetViewPosition(scrollView, null);
        scrollView.setVisibility(View.INVISIBLE);

        // 隐藏取词按钮
        mBtnQuCi.setBackgroundColor(Color.TRANSPARENT);
        mBtnQuCi.setVisibility(View.INVISIBLE);
        //mTextView.setTextSelectEnable(false);
        //mSelectEnable = false;
    }

    /**
     * 初始化DataType3。
     *
     * @param pDataType3 SDataType3
     * @param textView   AsmTextView
     */
    private void asmInitDataType3(AsmData.AsmDataType3 pDataType3, AsmSteerTextView textView) {
        Log.i(TAG, "[asmInitDataType3] Start!!!");

        // 绘制背景
        if (mData.hdcBk != null) {
            mAnimview.doDrawBitmap(mData.hdcBk, mData.x_bk, mData.y_bk);
        }
        if (pDataType3.hdcBk != null) {
            mAnimview.doDrawBitmap(pDataType3.hdcBk, pDataType3.x_bk, pDataType3.y_bk);
        }

        // 文本颜色
        //textView.setTextColor(Color.BLACK);

        // 初始化SDataType3的按钮
        asmInitDataType3Button(pDataType3, mMainView);

        pDataType3.item_cur = 0;

        // 读取DataType3的题目/答案
        mData.asmReadDataType3Content(mData.fp, mData.pDataType3.ItemFunc[0], mData.pDataType3);

        // 初始化SDataType3的题目
        asmInitDataType3Question(pDataType3, mTextView);

        Log.i(TAG, "[asmInitDataType3] End!!!");
    }

    /**
     * 初始化DataType3的按钮。
     *
     * @param pDataType3 SDataType3
     * @param view       ViewGroup
     */
    private void asmInitDataType3Button(AsmData.AsmDataType3 pDataType3, ViewGroup view) {
        //Log.i(TAG, "[asmInitDataType3Button] Start!!!");

        // 上一题
        if (mDataType3Button.btn_last == null) {
            mDataType3Button.btn_last = new ImageButton(mContext);
            mDataType3Button.btn_last.setBackgroundColor(Color.TRANSPARENT);
            view.addView(mDataType3Button.btn_last);
            mDataType3Button.btn_last.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.isFastDoubleClick()) {
                        asmDataType3ButtonOnClick(v, false);
                    }
                }
            });
        }
        {
            //pDataType3.btn[0].rect.right =
            //	pDataType3.btn[0].rect.left + pDataType3.btn[0].hdc[0].getWidth();
            //pDataType3.btn[0].rect.bottom =
            //	pDataType3.btn[0].rect.top + pDataType3.btn[0].hdc[0].getHeight();
            asmSetViewPosition(mDataType3Button.btn_last, pDataType3.btn[0].rect);
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled},    // 点击状态
                    new BitmapDrawable(mResources, pDataType3.btn[0].hdc[2]));
            drawable.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled},    // 聚焦状态
                    new BitmapDrawable(mResources, pDataType3.btn[0].hdc[1]));
            drawable.addState(new int[]{android.R.attr.state_hovered, android.R.attr.state_enabled},    // 划过状态
                    new BitmapDrawable(mResources, pDataType3.btn[0].hdc[1]));
            drawable.addState(new int[]{-android.R.attr.state_enabled},                                    // 无效状态
                    new BitmapDrawable(mResources, pDataType3.btn[0].hdc[3]));
            drawable.addState(new int[]{},                                                                // 正常状态
                    new BitmapDrawable(mResources, pDataType3.btn[0].hdc[0]));
            mDataType3Button.btn_last.setImageDrawable(drawable);
            mDataType3Button.btn_last.setEnabled(false);
            mDataType3Button.btn_last.setVisibility(View.VISIBLE);
        }

        // 下一题
        if (mDataType3Button.btn_next == null) {
            mDataType3Button.btn_next = new ImageButton(mContext);
            mDataType3Button.btn_next.setBackgroundColor(Color.TRANSPARENT);
            view.addView(mDataType3Button.btn_next);
            mDataType3Button.btn_next.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.isFastDoubleClick()) {
                        asmDataType3ButtonOnClick(v, true);
                    }
                }
            });
        }
        {
            asmSetViewPosition(mDataType3Button.btn_next, pDataType3.btn[1].rect);
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled},    // 点击状态
                    new BitmapDrawable(mResources, pDataType3.btn[1].hdc[2]));
            drawable.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled},    // 聚焦状态
                    new BitmapDrawable(mResources, pDataType3.btn[1].hdc[1]));
            drawable.addState(new int[]{android.R.attr.state_hovered, android.R.attr.state_enabled},    // 划过状态
                    new BitmapDrawable(mResources, pDataType3.btn[1].hdc[1]));
            drawable.addState(new int[]{-android.R.attr.state_enabled},                                    // 无效状态
                    new BitmapDrawable(mResources, pDataType3.btn[1].hdc[3]));
            drawable.addState(new int[]{},                                                                // 正常状态
                    new BitmapDrawable(mResources, pDataType3.btn[1].hdc[0]));
            mDataType3Button.btn_next.setImageDrawable(drawable);
            mDataType3Button.btn_next.setEnabled(true);
            mDataType3Button.btn_next.setVisibility(View.VISIBLE);
        }

        // 答案
        if (mDataType3Button.btn_answer == null) {
            mDataType3Button.btn_answer = new ImageButton(mContext);
            mDataType3Button.btn_answer.setBackgroundColor(Color.TRANSPARENT);
            view.addView(mDataType3Button.btn_answer);
            mDataType3Button.btn_answer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.isFastDoubleClick()) {
                        mDataType3Button.btn_answer.setVisibility(View.INVISIBLE);
                        mDataType3Button.btn_question.setVisibility(View.VISIBLE);

                        asmInitDataType3Answer(mData.pDataType3, mTextView);
                    }
                }
            });
        }
        {
            asmSetViewPosition(mDataType3Button.btn_answer, pDataType3.btn[2].rect);
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed},                // 点击状态
                    new BitmapDrawable(mResources, pDataType3.btn[2].hdc[2]));
            drawable.addState(new int[]{android.R.attr.state_focused},                // 聚焦状态
                    new BitmapDrawable(mResources, pDataType3.btn[2].hdc[1]));
            drawable.addState(new int[]{android.R.attr.state_hovered},                // 划过状态
                    new BitmapDrawable(mResources, pDataType3.btn[2].hdc[1]));
            drawable.addState(new int[]{},                                            // 正常状态
                    new BitmapDrawable(mResources, pDataType3.btn[2].hdc[0]));
            mDataType3Button.btn_answer.setImageDrawable(drawable);
            mDataType3Button.btn_answer.setVisibility(View.VISIBLE);
        }

        // 题目
        if (mDataType3Button.btn_question == null) {
            mDataType3Button.btn_question = new ImageButton(mContext);
            mDataType3Button.btn_question.setBackgroundColor(Color.TRANSPARENT);
            view.addView(mDataType3Button.btn_question);
            mDataType3Button.btn_question.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.isFastDoubleClick()) {
                        mDataType3Button.btn_question.setVisibility(View.INVISIBLE);
                        mDataType3Button.btn_answer.setVisibility(View.VISIBLE);

                        asmInitDataType3Question(mData.pDataType3, mTextView);
                    }
                }
            });
        }
        {
            asmSetViewPosition(mDataType3Button.btn_question, pDataType3.btn[3].rect);
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed},                // 点击状态
                    new BitmapDrawable(mResources, pDataType3.btn[3].hdc[2]));
            drawable.addState(new int[]{android.R.attr.state_focused},                // 聚焦状态
                    new BitmapDrawable(mResources, pDataType3.btn[3].hdc[1]));
            drawable.addState(new int[]{android.R.attr.state_hovered},                // 划过状态
                    new BitmapDrawable(mResources, pDataType3.btn[3].hdc[1]));
            drawable.addState(new int[]{},                                            // 正常状态
                    new BitmapDrawable(mResources, pDataType3.btn[3].hdc[0]));
            mDataType3Button.btn_question.setImageDrawable(drawable);
            mDataType3Button.btn_question.setVisibility(View.INVISIBLE);
        }

        //Log.i(TAG, "[asmInitDataType3Button] End!!!");
    }

    /**
     * 隐藏DataType3的按钮。
     *
     * @param dataType3Button DataType3Button
     */
    private void asmHideDataType3Button(AsmDataType3Button dataType3Button) {
        if (dataType3Button.btn_last != null) {
            dataType3Button.btn_last.setVisibility(View.INVISIBLE);
        }

        if (dataType3Button.btn_next != null) {
            dataType3Button.btn_next.setVisibility(View.INVISIBLE);
        }

        if (dataType3Button.btn_answer != null) {
            dataType3Button.btn_answer.setVisibility(View.INVISIBLE);
        }

        if (dataType3Button.btn_question != null) {
            dataType3Button.btn_question.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 初始化SDataType3的答案。
     *
     * @param pDataType3 SDataType3
     * @param textView   AsmTextView
     */
    private void asmInitDataType3Answer(AsmData.AsmDataType3 pDataType3, AsmSteerTextView textView) {
        // 设定文本显示位置
        asmSetViewPosition(mScrollView, pDataType3.rect);

        // 显示文本
		/*String text = null;
		if((pDataType3.text_answer[0] & 0xFF) == 0xFF && 
				(pDataType3.text_answer[1] & 0xFF) == 0xFE)					// UTF16_CODE
		{
			text = new String(AsmYBToUnicode.asmGetStr(pDataType3.text_answer, 2));
			//text = new String(new AsmLoadTextThread(pDataType3.text_answer, 2).mResult);
		}
		else																// ASCII_CODE
		{
			text = asmGetString(pDataType3.text_answer, 0, AsmConstant.ASM_ASCII_CODE);
		}
		textView.setText(text);*/

        new AsmLoadTextThread(mHandler, pDataType3.text_answer).start();
        //mContext.showDialog(0);
        //new AsmLoadTextAsyncTask(mHandler, pDataType3.text_answer).execute();

        // 显示DataType1的内容
        asmShowDataType1(mScrollView, textView);
    }

    /**
     * 初始化SDataType3的题目。
     *
     * @param pDataType3 SDataType3
     * @param textView   AsmTextView
     */
    private void asmInitDataType3Question(AsmData.AsmDataType3 pDataType3, AsmSteerTextView textView) {
        // 设定文本显示位置
        asmSetViewPosition(mScrollView, pDataType3.rect);

        // 显示文本
		/*String text = null;
		if((pDataType3.text_question[0] & 0xFF) == 0xFF && 
				(pDataType3.text_question[1] & 0xFF) == 0xFE)				// UTF16_CODE
		{
			text = new String(AsmYBToUnicode.asmGetStr(pDataType3.text_question, 2));
			//text = new String(new AsmLoadTextThread(pDataType3.text_question, 2).mResult);
		}
		else																// ASCII_CODE
		{
			text = asmGetString(pDataType3.text_question, 0, AsmConstant.ASM_ASCII_CODE);
		}
		textView.setText(text);*/

        new AsmLoadTextThread(mHandler, pDataType3.text_question).start();
        //mContext.showDialog(0);
        //new AsmLoadTextAsyncTask(mHandler, pDataType3.text_question).execute();

        // 显示DataType1的内容
        asmShowDataType1(mScrollView, textView);
    }

    /**
     * 初始化SDataType4（百家姓）。
     *
     * @param pDataType4 SDataType4
     * @param listView   ListView
     */
    private void asmInitDataType4(AsmData.AsmDataType4 pDataType4, ListView listView) {
        Log.i(TAG, "[asmInitDataType4] Start!!!");

        Paint paint = new Paint();

        // 绘制背景
        if (mData.hdcBk != null) {
            mAnimview.doDrawBitmap(mData.hdcBk, mData.x_bk, mData.y_bk);
        }
        if (pDataType4.hdcBk != null) {
            mAnimview.doDrawBitmap(pDataType4.hdcBk, pDataType4.x_bk, pDataType4.y_bk);
        }

        paint.setTextSize(AsmConstant.ASM_FONT_SIZE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setTextAlign(Align.CENTER);

        // 显示标题
        String title = asmGetString(pDataType4.ItemText[0], 0, AsmConstant.ASM_ASCII_CODE);
        paint.setColor(AsmConstant.ASM_FONT_COLOR_TITLE);

        int datatype4_title_x = mResources.getDimensionPixelSize(R.dimen.datatype4_title_x);
        int datatype4_title_y = mResources.getDimensionPixelSize(R.dimen.datatype4_title_y);
        int datatype4_title_w = mResources.getDimensionPixelSize(R.dimen.datatype4_title_w);
        int datatype4_title_h = mResources.getDimensionPixelSize(R.dimen.datatype4_title_h);

        mAnimview.doDrawText(title, paint,
                datatype4_title_x, datatype4_title_y, datatype4_title_w, datatype4_title_h);

        int datatype4_list_x =
                mResources.getDimensionPixelSize(R.dimen.datatype4_list_x);
        int datatype4_list_y =
                mResources.getDimensionPixelSize(R.dimen.datatype4_list_y);
        int datatype4_list_w =
                mResources.getDimensionPixelSize(R.dimen.datatype4_list_w);
        int datatype4_list_h =
                mResources.getDimensionPixelSize(R.dimen.datatype4_list_h);

        asmSetViewPosition(listView,
                new Rect(datatype4_list_x, datatype4_list_y, datatype4_list_w, datatype4_list_h));

        // 添加列表内容
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mData.pDataType4.total_item - 1;
            }

            @Override
            public Object getItem(int position) {
                return mData.pDataType4.ItemText[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = new TextView(mContext);

                // 设定列表每项的宽、高
                AbsListView.LayoutParams params =
                        new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                AsmConstant.MenuType2_HEIGHT);
                textView.setLayoutParams(params);

                // 设置AsmTextView背景颜色
                StateListDrawable drawable = new StateListDrawable();

                drawable.addState(new int[]{android.R.attr.state_activated},    // 激活（选中）状态
                        new ColorDrawable(Color.TRANSPARENT));
				/*drawable.addState(new int[]{android.R.attr.state_pressed}, 	// 点击状态
						new ColorDrawable(Color.TRANSPARENT));*/
                drawable.addState(new int[]{android.R.attr.state_selected},    // 选中状态
                        mResources.getDrawable(R.drawable.list_select));
                drawable.addState(new int[]{},                                    // 正常状态
                        new ColorDrawable(Color.TRANSPARENT));

                textView.setBackgroundDrawable(drawable);

                // 设置字体颜色
                ColorStateList colors = new ColorStateList(
                        new int[][]{                                    // 状态
                                new int[]{android.R.attr.state_activated},    // 激活（选中）状态
                                new int[]{android.R.attr.state_pressed},    // 点击状态
                                new int[]{}},                                // 正常状态
                        new int[]{Color.RED,                            // 颜色
                                Color.RED,
                                Color.BLACK});

                textView.setTextColor(colors);

                // 设置字体大小
                //textView.setTextSize(AsmConstant.ASM_FONT_SIZE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, AsmConstant.ASM_FONT_SIZE);

                textView.setGravity(Gravity.CENTER);

                // 设置文字
                String text = asmGetString(mData.pDataType4.ItemText[position + 1], 0, AsmConstant.ASM_ASCII_CODE);
                textView.setText(text);

                return textView;
            }
        });

        // 点击监听器
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Utils.isFastDoubleClick()) {
                    asmDatatype4OnItemClick(view, position);
                }
            }
        });

        // 设置列表背景颜色
        StateListDrawable drawable = new StateListDrawable();

        drawable.addState(new int[]{android.R.attr.state_activated},    // 激活（选中）状态
                new ColorDrawable(Color.TRANSPARENT));
        drawable.addState(new int[]{android.R.attr.state_pressed},        // 点击状态
                new ColorDrawable(Color.TRANSPARENT));
        drawable.addState(new int[]{},                                    // 正常状态
                new ColorDrawable(Color.TRANSPARENT));

        listView.setSelector(drawable);

        listView.setVisibility(View.VISIBLE);

        mData.pDataType4.cur_item = 0;

        // 播放标题声音
        asmPlaySound(mData.pDataType4.Itemspeech[0]);

        Log.i(TAG, "[asmInitDataType4] End!!!");
    }

    /**
     * 初始化DataType4（百家姓）的文字。
     *
     * @param pDataType4 SDataType4
     * @param listView   ListView
     */
    private void asmInitDataType4Text(AsmData.AsmDataType4 pDataType4, ListView listView) {
        //Log.i(TAG, "[asmInitDataType4Text] Start!!!");

        pDataType4.cur_item++;
        if (pDataType4.cur_item > pDataType4.total_item - 1) {
            mTextListView.setItemChecked(pDataType4.total_item - 1, false);

            pDataType4.cur_item = 0;

            return;
        }

        mTextListView.setItemChecked(pDataType4.cur_item - 1, true);
        //if(pDataType4.cur_item > mTextListView.getLastVisiblePosition() ||
        //	pDataType4.cur_item < mTextListView.getFirstVisiblePosition())
        {
            //mTextListView.setSelection(pDataType4.cur_item - 1);
            mTextListView.smoothScrollToPosition(pDataType4.cur_item - 1);
        }
        android.util.Log.i("jjkb", "asmInitDataType4Text: "+mData.pDataType4.Itemspeech[mData.pDataType4.cur_item]);

        // 播放声音
        asmPlaySound(mData.pDataType4.Itemspeech[mData.pDataType4.cur_item]);

        //Log.i(TAG, "[asmInitDataType4Text] End!!!");
    }

    /**
     * 初始化DataType5（三字经、弟子规、千字文）。
     *
     * @param pDataType5 SDataType5
     * @param textView   AsmTextView
     */
    private void asmInitDataType5(AsmData.AsmDataType5 pDataType5, AsmSteerTextView textView) {
        Log.i(TAG, "[asmInitDataType5] Start!!!");

        Paint paint = new Paint();

        // 绘制背景
        if (mData.hdcBk != null) {
            mAnimview.doDrawBitmap(mData.hdcBk, mData.x_bk, mData.y_bk);
        }
        if (pDataType5.hdcBk != null) {
            mAnimview.doDrawBitmap(pDataType5.hdcBk, pDataType5.x_bk, pDataType5.y_bk);
        }

        paint.setTextSize(AsmConstant.ASM_FONT_SIZE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setTextAlign(Align.CENTER);

        // 显示标题
        String title = asmGetString(pDataType5.ItemText[0], 0, AsmConstant.ASM_ASCII_CODE);
        paint.setColor(AsmConstant.ASM_FONT_COLOR_TITLE);

        int datatype5_title_x =
                mResources.getDimensionPixelSize(R.dimen.datatype5_title_x);
        int datatype5_title_y =
                mResources.getDimensionPixelSize(R.dimen.datatype5_title_y);
        int datatype5_title_w =
                mResources.getDimensionPixelSize(R.dimen.datatype5_title_w);
        int datatype5_title_h =
                mResources.getDimensionPixelSize(R.dimen.datatype5_title_h);

        mAnimview.doDrawText(title, paint,
                datatype5_title_x, datatype5_title_y, datatype5_title_w, datatype5_title_h);

        mData.pDataType5.cur_item = 0;

        // 播放标题声音
        asmPlaySound(mData.pDataType5.Itemspeech[0]);

        Log.i(TAG, "[asmInitDataType5] End!!!");
    }

    /**
     * 初始化DataType5（三字经、弟子规、千字文）的文字。
     *
     * @param pDataType5 SDataType5
     * @param textView   AsmTextView
     * @param forward    是否往前播放
     */
    private void asmInitDataType5Text(AsmData.AsmDataType5 pDataType5, AsmSteerTextView textView, boolean forward) {
        //Log.i(TAG, "[asmInitDataType5Text] Start!!!");

        if (forward)            // 下一条
        {
            pDataType5.cur_item++;
            if (pDataType5.cur_item > pDataType5.total_item - 1) {
                pDataType5.cur_item = pDataType5.total_item - 1;

                return;
            }
        } else                // 上一条
        {
            pDataType5.cur_item--;
            if (pDataType5.cur_item < 1) {
                pDataType5.cur_item = 1;

                return;
            }
        }

        // 切换显示内容时，禁止取词，清空已选的内容
        textView.setDragEnable(false);
        textView.selfUpdateUnSelected();

        // 译文段首加上四个空格，好看！！！！！
        byte[] interBuff = new byte[]{0x20, 0x20, 0x20, 0x20};
        String interStr = asmGetString(interBuff, 0, AsmConstant.ASM_ASCII_CODE);

        // 绘制背景
        if (mData.hdcBk != null) {
            mAnimview.doDrawBitmap(mData.hdcBk, mData.x_bk, mData.y_bk);
        }
        if (pDataType5.hdcBk != null) {
            mAnimview.doDrawBitmap(pDataType5.hdcBk, pDataType5.x_bk, pDataType5.y_bk);
        }

        // 显示正文
        byte[] buff = pDataType5.ItemText[pDataType5.cur_item];
        android.util.Log.i("jkb", "asmInitDataType5Text: "+asmGetString(buff, 0, AsmConstant.ASM_ASCII_CODE));
        if (buff[0] == 1)            // 两行文本（三字经）
        {
            // 标题
            String title = asmGetString(pDataType5.ItemText[0], 0, AsmConstant.ASM_ASCII_CODE);

            // 诗句
            int off = 0, len = 0;
            off += 1;
            String poem = asmGetString(buff, off, AsmConstant.ASM_ASCII_CODE);

            Paint paint = new Paint();
            paint.setTextSize(AsmConstant.ASM_FONT_SIZE);
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setTextAlign(Align.CENTER);

            // 显示标题
            paint.setColor(AsmConstant.ASM_FONT_COLOR_TITLE);

            int datatype5_title_x =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_title_x);
            int datatype5_title_y =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_title_y);
            int datatype5_title_w =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_title_w);
            int datatype5_title_h =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_title_h);

            mAnimview.doDrawText(title, paint,
                    datatype5_title_x, datatype5_title_y, datatype5_title_w, datatype5_title_h);

            // 显示诗句
            paint.setColor(AsmConstant.ASM_FONT_COLOR_TEXT);

            int datatype5_style1_poem_x =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style1_poem_x);
            int datatype5_style1_poem_y =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style1_poem_y);
            int datatype5_style1_poem_w =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style1_poem_w);
            int datatype5_style1_poem_h =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style1_poem_h);
            android.util.Log.i(TAG, "asmInitDataType5Textsss: "+poem);

            mAnimview.doDrawText(poem, paint,
                    datatype5_style1_poem_x, datatype5_style1_poem_y,
                    datatype5_style1_poem_w, datatype5_style1_poem_h);

            // 译文
            len = asmStrlen(buff, off);
            off += len;
            String translate = asmGetString(buff, off, AsmConstant.ASM_ASCII_CODE);
            android.util.Log.i(TAG, "asmInitDataType5Text: 111"+off+len);

            // 显示译文
            int datatype5_style1_translate_x =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style1_translate_x);
            int datatype5_style1_translate_y =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style1_translate_y);
            int datatype5_style1_translate_w =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style1_translate_w);
            int datatype5_style1_translate_h =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style1_translate_h);

            asmSetViewPosition(mScrollView,
                    new Rect(datatype5_style1_translate_x, datatype5_style1_translate_y,
                            datatype5_style1_translate_w, datatype5_style1_translate_h));

            textView.setDragEnable(false);
            textView.selfUpdateUnSelected();
            textView.setTextCtn(interStr + translate);
            android.util.Log.i(TAG, "asmInitDataType5Text: "+interStr+","+translate);
        } else if (buff[0] == 2)        // 三行文本（弟子规、千字文）
        {
            // 标题
            String title = asmGetString(pDataType5.ItemText[0], 0, AsmConstant.ASM_ASCII_CODE);

            // 诗句1
            int off = 0, len = 0;
            off += 1;
            android.util.Log.i(TAG, "asmInitDataType5Text: +++"+off);
            String poem1 = asmGetString(buff, off, AsmConstant.ASM_ASCII_CODE);

            // 诗句2
            len = asmStrlen(buff, off);
            off += len;
            android.util.Log.i(TAG, "asmInitDataType5Text: +++"+off);
            String poem2 = asmGetString(buff, off, AsmConstant.ASM_ASCII_CODE);

            Paint paint = new Paint();
            paint.setTextSize(AsmConstant.ASM_FONT_SIZE);
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setTextAlign(Align.CENTER);

            // 显示标题
            paint.setColor(AsmConstant.ASM_FONT_COLOR_TITLE);

            int datatype5_title_x =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_title_x);
            int datatype5_title_y =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_title_y);
            int datatype5_title_w =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_title_w);
            int datatype5_title_h =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_title_h);

            mAnimview.doDrawText(title, paint,
                    datatype5_title_x, datatype5_title_y, datatype5_title_w, datatype5_title_h);

            // 显示诗句1
            paint.setColor(AsmConstant.ASM_FONT_COLOR_TEXT);

            int datatype5_style2_poem1_x =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_poem1_x);
            int datatype5_style2_poem1_y =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_poem1_y);
            int datatype5_style2_poem1_w =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_poem1_w);
            int datatype5_style2_poem1_h =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_poem1_h);
            android.util.Log.i(TAG, "asmInitDataType5Textsss: "+poem1);
            mAnimview.doDrawText(poem1, paint,
                    datatype5_style2_poem1_x, datatype5_style2_poem1_y,
                    datatype5_style2_poem1_w, datatype5_style2_poem1_h);

            // 显示诗句2
            paint.setColor(AsmConstant.ASM_FONT_COLOR_TEXT);

            int datatype5_style2_poem2_x =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_poem2_x);
            int datatype5_style2_poem2_y =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_poem2_y);
            int datatype5_style2_poem2_w =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_poem2_w);
            int datatype5_style2_poem2_h =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_poem2_h);

            mAnimview.doDrawText(poem2, paint,
                    datatype5_style2_poem2_x, datatype5_style2_poem2_y,
                    datatype5_style2_poem2_w, datatype5_style2_poem2_h);

            // 译文
            len = asmStrlen(buff, off);
            off += len;
            String translate = asmGetString(buff, off, AsmConstant.ASM_ASCII_CODE);
            android.util.Log.i(TAG, "asmInitDataType5Text: +++"+off);

            // 显示译文
            int datatype5_style2_translate_x =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_translate_x);
            int datatype5_style2_translate_y =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_translate_y);
            int datatype5_style2_translate_w =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_translate_w);
            int datatype5_style2_translate_h =
                    mResources.getDimensionPixelSize(R.dimen.datatype5_style2_translate_h);

            asmSetViewPosition(mScrollView,
                    new Rect(datatype5_style2_translate_x, datatype5_style2_translate_y,
                            datatype5_style2_translate_w, datatype5_style2_translate_h));

            textView.setDragEnable(false);
            textView.selfUpdateUnSelected();
            textView.setTextCtn(interStr + translate);
            android.util.Log.i(TAG, "asmInitDataType5Text: "+interStr+","+translate);
        }

        // 设置文本颜色
        //textView.setTextColor(Color.BLACK);

        // 显示DataType1的内容
        asmShowDataType1(mScrollView, textView);

        // 播放声音
        asmPlaySound(mData.pDataType5.Itemspeech[mData.pDataType5.cur_item]);

        //Log.i(TAG, "[asmInitDataType5Text] End!!!");
    }

    /**
     * 隐藏所有的子控件。
     */
    private void asmHideAllView() {
        // 停止播放Avi
        asmStopAvi();

        // 隐藏MenuType2
        //asmHideMenuType2View(mTextListView);

        // 清除MenuType3的一组按钮。
        //asmHideMenuType3TeamButton(mMainView);

        // 隐藏DataType1
        //asmHideDataType1(mScrollView, mTextView);

        // 隐藏DataType3的按钮
        //asmHideDataType3Button(mDataType3Button);
    }

    /**
     * 响应图片菜单的点击事件。
     *
     * @param view     View
     * @param position 列表点击位置
     */
    private void asmMenuType1OnItemClick(View view, int position) {
        //Log.i(TAG, "[asmMenuType1OnItemClick] Start!!!");

        // 关掉先前存在的取词对话框
        if (mQuCiDialog != null && mQuCiDialog.isShowing()) {
            mQuCiDialog.dismiss();
        }

        mMemuType2LevelSave = 0;

        int type = mData.asmDataReadNode(mData.pMenuType1.ItemFunc[position]);

        //Log.i(TAG, "[asmMenuType1OnItemClick] type == " + type);

        if ((type & 0x40000000) > 0)        // 数据
        {
            type &= 0xBFFFFFFF;

            Log.i("jkb", "[asmMenuType1OnItemClick] Data Type == " + type);

            if (type == AsmConstant.DataType1)                // 纯文本
            {
                // 清除MenuType3的一组按钮。
                asmHideMenuType3TeamButton(mMainView);

                // 隐藏DataType1（国学经典里面由DataType5切换到DataType1时会用到）
                //asmHideDataType1(mScrollView, mTextView);

                asmInitDataType1(mData.pDataType1, mTextView);
            } else if (type == AsmConstant.DataType2)            // 语音+图片操作
            {
                android.util.Log.i("jkb", "asmMenuType1OnItemClick: 11111");
                // 隐藏DataType1
                asmHideDataType1(mScrollView, mTextView);

                // 停止播放Avi
                asmStopAvi();

                asmPlayAvi(mData.pMenuType1.ItemFunc[position] + 2);
            } else if (type == AsmConstant.DataType3)            // 题目+答案
            {
                asmInitDataType3(mData.pDataType3, mTextView);
            } else if (type == AsmConstant.DataType4)            // 语音+文字高亮显示（百家姓）
            {
                // 隐藏DataType1
                asmHideDataType1(mScrollView, mTextView);

                asmInitDataType4(mData.pDataType4, mTextListView);
            } else if (type == AsmConstant.DataType5)            // 语音+文字显示（三字经、弟子规、千字文）
            {
                // 隐藏MenuType2
                asmHideMenuType2View(mTextListView);

                // 隐藏DataType1
                asmHideDataType1(mScrollView, mTextView);

                asmInitDataType5(mData.pDataType5, mTextView);
            }
        } else                            // 目录
        {
            Log.i(TAG, "[asmMenuType1OnItemClick] Menu Type == " + type);

            if (type == AsmConstant.MenuType1)                // 图片菜单
            {
            } else if (type == AsmConstant.MenuType2)            // 文字菜单
            {
                asmInitMenuType2(mData.pMenuType2, mTextListView);
            } else if (type == AsmConstant.MenuType3)            // 按钮菜单
            {
                // 隐藏DataType1（国际音标里面由音标基础切换到英文字母时会用到）
                asmHideDataType1(mScrollView, mTextView);

                asmInitMenuType3(mData.pMenuType3, mMainView);
            }
        }

        //Log.i(TAG, "[asmMenuType1OnItemClick] End!!!");
    }

    /**
     * 响应文字菜单的点击事件。
     *
     * @param view     View
     * @param position 列表点击位置
     */
    private void asmMenuType2OnItemClick(View view, int position) {
        //Log.i(TAG, "[asmMenuType2OnItemClick] Start!!!");

        int type = mData.asmDataReadNode(mData.pMenuType2.ItemFunc[position]);

        //Log.i(TAG, "[asmMenuType2OnItemClick] type == " + type);

        if ((type & 0x40000000) > 0)        // 数据
        {
            type &= 0xBFFFFFFF;

            Log.i(TAG, "[asmMenuType2OnItemClick] Data Type == " + type);

            if (type == AsmConstant.DataType1)                // 纯文本
            {
                // 清除MenuType3的一组按钮
                asmHideMenuType3TeamButton(mMainView);

                asmInitDataType1(mData.pDataType1, mTextView);
            } else if (type == AsmConstant.DataType2)            // 语音+图片操作
            {
                //asmPlayAvi(mData.pMenuType2.ItemFunc[position] + 2);
            } else if (type == AsmConstant.DataType3)            // 题目+答案
            {
                asmInitDataType3(mData.pDataType3, mTextView);
            }
        } else                            // 目录
        {
            Log.i(TAG, "[asmMenuType2OnItemClick] Menu Type == " + type);

            if (type == AsmConstant.MenuType1)                // 图片菜单
            {
            } else if (type == AsmConstant.MenuType2)            // 文字菜单
            {
                asmInitMenuType2(mData.pMenuType2, mTextListView);
            } else if (type == AsmConstant.MenuType3)            // 按钮菜单
            {
                asmInitMenuType3(mData.pMenuType3, mMainView);
            }
        }

        //Log.i(TAG, "[asmMenuType2OnItemClick] End!!!");
    }

    /**
     * 响应按钮菜单的点击事件。
     *
     * @param view View
     * @param id   点击按钮的ID
     */
    private void asmMenuType3OnClick(View view, int id) {
        //Log.i(TAG, "[asmMenuType3OnClick] Start!!!");

        // 显示当前选中的按钮
        mMenuType3Sel.setImageDrawable(new BitmapDrawable(mResources, mData.pMenuType3.last_pic[id]));
        asmSetViewPosition(mMenuType3Sel, mData.pMenuType3.rect[id]);

        int type = mData.asmDataReadNode(mData.pMenuType3.ItemFunc[id]);

        //Log.i(TAG, "[asmMenuType3OnClick] type == " + type);

        if ((type & 0x40000000) > 0)        // 数据
        {
            type &= 0xBFFFFFFF;

            Log.i(TAG, "[asmMenuType3OnClick] Data Type == " + type);

            if (type == AsmConstant.DataType1)                // 纯文本
            {
                asmInitDataType1(mData.pDataType1, mTextView);
            } else if (type == AsmConstant.DataType2)            // 语音+图片操作
            {
                android.util.Log.i(TAG, "asmMenuType3OnClick: 2222");
                // 隐藏DataType1
                asmHideDataType1(mScrollView, mTextView);

                // 停止播放Avi
                asmStopAvi();

                asmPlayAvi(mData.pMenuType3.ItemFunc[id] + 2);
            } else if (type == AsmConstant.DataType3)            // 题目+答案
            {
                asmInitDataType3(mData.pDataType3, mTextView);
            }
        }

        //Log.i(TAG, "[asmMenuType3OnClick] End!!!");
    }

    /**
     * 响应DataType3按钮的点击事件。
     *
     * @param view    View
     * @param forward 是否下一题
     */
    private void asmDataType3ButtonOnClick(View view, boolean forward) {
        if (forward)                // 下一题
        {
            if (mData.pDataType3.item_cur == 0) {
                mDataType3Button.btn_last.setEnabled(true);
            }
            mData.pDataType3.item_cur++;
            if (mData.pDataType3.item_cur >= mData.pDataType3.item_num - 1) {
                mData.pDataType3.item_cur = mData.pDataType3.item_num - 1;
                mDataType3Button.btn_next.setEnabled(false);
            }
        } else                    // 上一题
        {
            if (mData.pDataType3.item_cur == mData.pDataType3.item_num - 1) {
                mDataType3Button.btn_next.setEnabled(true);
            }
            mData.pDataType3.item_cur--;
            if (mData.pDataType3.item_cur <= 0) {
                mData.pDataType3.item_cur = 0;
                mDataType3Button.btn_last.setEnabled(false);
            }
        }

        // 显示答案按钮
        mDataType3Button.btn_answer.setVisibility(View.VISIBLE);
        mDataType3Button.btn_question.setVisibility(View.INVISIBLE);

        // 读取题目/答案
        mData.asmReadDataType3Content(mData.fp, mData.pDataType3.ItemFunc[mData.pDataType3.item_cur],
                mData.pDataType3);

        // 显示题目
        asmInitDataType3Question(mData.pDataType3, mTextView);

        Log.i(TAG, "[asmDataType3ButtonOnClick] item_cur == " + mData.pDataType3.item_cur);
    }

    /**
     * 响应Datatype4（百家姓）的点击事件。
     *
     * @param view     View
     * @param position 列表点击位置
     */
    private void asmDatatype4OnItemClick(View view, int position) {
        mTextListView.setItemChecked(position, true);
        mData.pDataType4.cur_item = position + 1;
        asmPlaySound(mData.pDataType4.Itemspeech[mData.pDataType4.cur_item]);
    }

    class AutoCompleteTextViewAdapter extends BaseAdapter implements Filterable {
        ArrayList<String> mList = null;
        Context mContext = null;
        MyFilter mFilter = null;

        public AutoCompleteTextViewAdapter(Context context) {
            mContext = context;
            mList = new ArrayList<String>();
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList == null ? null : mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                TextView textview = new TextView(mContext);
                textview.setTextColor(Color.BLACK);
                textview.setTextSize(20);
                //textview.setBackgroundColor(Color.WHITE);
                convertView = textview;
            }

            TextView textview = (TextView) convertView;
            textview.setText(mList.get(position));

            return textview;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new MyFilter();
            }

            return mFilter;
        }

        class MyFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (mList == null) {
                    mList = new ArrayList<String>();
                }
                results.values = mList;
                results.count = mList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }

    class MenuType2Adapter extends BaseAdapter implements Filterable {
        Context mContext = null;
        MyFilter mFilter = null;
        HashMap<Integer, Integer> mHashMap;

        public MenuType2Adapter(Context context) {
            mContext = context;
            mHashMap = new HashMap<Integer, Integer>();
        }

        @Override
        public int getCount() {
            return mHashMap == null ? 0 : mHashMap.size();
        }

        @Override
        public Object getItem(int position) {
            return mHashMap == null ? null : mData.pMenuType2.ItemName[mHashMap.get(position)];
        }

        @Override
        public long getItemId(int position) {
            return mHashMap.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                TextView textView = new TextView(mContext);

                // 设定每项的宽、高
                AbsListView.LayoutParams params =
                        new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                AsmConstant.MenuType2_HEIGHT);
                textView.setLayoutParams(params);

                // 设置AsmTextView背景颜色
                StateListDrawable drawable = new StateListDrawable();

                drawable.addState(new int[]{android.R.attr.state_activated},    // 激活（选中）状态
                        new ColorDrawable(Color.TRANSPARENT));
				/*drawable.addState(new int[]{android.R.attr.state_pressed}, 	// 点击状态
						new ColorDrawable(Color.TRANSPARENT));*/
                drawable.addState(new int[]{android.R.attr.state_selected},    // 选中状态
                        mResources.getDrawable(R.drawable.list_select));
                drawable.addState(new int[]{},                                    // 正常状态
                        new ColorDrawable(Color.TRANSPARENT));

                textView.setBackgroundDrawable(drawable);

                // 设置AsmTextView字体颜色
                ColorStateList colors = new ColorStateList(
                        new int[][]{                        // 状态
                                new int[]{android.R.attr.state_activated},        // 激活（选中）状态
                                new int[]{android.R.attr.state_pressed},        // 点击状态
                                new int[]{}},                                    // 正常状态
                        new int[]{                            // 颜色
                                Color.RED,
                                Color.RED,
                                Color.BLACK});

                textView.setTextColor(colors);

                // 设置字体大小
                //textView.setTextSize(AsmConstant.ASM_FONT_SIZE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, AsmConstant.ASM_FONT_SIZE);

                textView.setGravity(Gravity.CENTER);

                convertView = textView;
            }

            TextView textView = (TextView) convertView;

            // 设置文字
            //String text = asmGetString(mData.pMenuType2.ItemText[position], 0, AsmConstant.ASM_ASCII_CODE);
            textView.setText(mData.pMenuType2.ItemName[mHashMap.get(position)]);

            return textView;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new MyFilter();
            }

            return mFilter;
        }

        class MyFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (mHashMap == null) {
                    mHashMap = new HashMap<Integer, Integer>();
                }
                results.values = mHashMap;
                results.count = mHashMap.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }

    class MenuType2OnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!Utils.isFastDoubleClick()) {
                Log.i(TAG, "[asmInitMenuType2] onItemClick position == " + position);

                // 保存MemuType2状态
                if (mMemuType2LevelSave < AsmConstant.MenuType2_LEVEL_MAX) {
                    mMenuType2Save[mMemuType2LevelSave].addr = mData.pMenuType2.self_addr;
                    mMenuType2Save[mMemuType2LevelSave].item_sel = mTextListView.getCheckedItemPosition();
                    mMenuType2Save[mMemuType2LevelSave].item_top = mTextListView.getFirstVisiblePosition();

                    mMemuType2LevelSave++;

                    //Log.i(TAG, "asmMenuType2OnItemClick " +
                    //		"FirstVisiblePosition == " + mTextListView.getFirstVisiblePosition() +
                    //		", CheckedItemPosition == " + mTextListView.getCheckedItemPosition() +
                    //		", mSaveMemuType2Level == " + mSaveMemuType2Level);
                }

                // 隐藏所有的子控件
                asmHideAllView();

                asmSendMessageDelayed(mHandler, AsmConstant.MENUTYPE2_LIST_ID, mMenuType2Adapter.mHashMap.get(position), 0,
                        AsmConstant.ASM_DELAY_TIME);
            }
        }
    }

}