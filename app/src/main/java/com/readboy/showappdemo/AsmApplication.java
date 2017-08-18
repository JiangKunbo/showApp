package com.readboy.showappdemo;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.readboy.showappdemo.gen.DaoMaster;
import com.readboy.showappdemo.gen.DaoSession;
import com.readboy.showappdemo.gen.TxtEntryDao;
import com.readboy.showappdemo.util.Log;

import java.io.File;
import java.io.IOException;


/**
 * ASM Application。
 *
 * @author lyj
 * @version 1.14.04.01
 * @date 2014.04.01
 * @history lyj 2013.08.01在Android平台上重写了此代码
 */
public class AsmApplication extends Application {
    //private final String TAG = getClass().getSimpleName();	// TAG
    private final String TAG = "AsmApplication";                // TAG

    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private TxtEntryDao dao;

    public static AsmApplication instances;

    public AsmApplication() {
        Log.i(TAG, "[AsmApplication]");
    }

    public static AsmApplication getInstances() {
        return instances;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        setDatabase();
        Log.i(TAG, "[onCreate]");
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    private void setDatabase() {
        mDaoMaster = getDaoMaster(this);
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 获取DaoMaster
     *
     * @param context
     * @return
     */
    private DaoMaster getDaoMaster(Context context) {

        if (mDaoMaster == null) {

            try {
                ContextWrapper wrapper = new ContextWrapper(context) {
                    /**
                     * 获得数据库路径，如果不存在，则创建对象对象
                     *
                     * @param name
                     */
                    @Override
                    public File getDatabasePath(String name) {
                        // 判断是否存在sd卡
                        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
                        if (!sdExist) {// 如果不存在,
                            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
                            return null;
                        } else {// 如果存在
                            // 获取sd卡路径
                            String dbDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                            dbDir += "/guoxuejingdian/db";// 数据库所在目录
                            String dbPath = dbDir + "/" + name;// 数据库路径
                            // 判断目录是否存在，不存在则创建该目录
                            File dirFile = new File(dbDir);
                            if (!dirFile.exists())
                                dirFile.mkdirs();

                            // 数据库文件是否创建成功
                            boolean isFileCreateSuccess = false;
                            // 判断文件是否存在，不存在则创建该文件
                            File dbFile = new File(dbPath);
                            if (!dbFile.exists()) {
                                try {
                                    isFileCreateSuccess = dbFile.createNewFile();// 创建文件
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else
                                isFileCreateSuccess = true;
                            // 返回数据库文件对象
                            if (isFileCreateSuccess)
                                return dbFile;
                            else
                                return super.getDatabasePath(name);
                        }
                    }

                    /**
                     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
                     *
                     * @param name
                     * @param mode
                     * @param factory
                     */
                    @Override
                    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
                        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
                    }

                    /**
                     * Android 4.0会调用此方法获取数据库。
                     *
                     * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String,
                     *      int,
                     *      android.database.sqlite.SQLiteDatabase.CursorFactory,
                     *      android.database.DatabaseErrorHandler)
                     * @param name
                     * @param mode
                     * @param factory
                     * @param errorHandler
                     */
                    @Override
                    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
                        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
                    }
                };
                DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(wrapper, "chengyugushi1.db", null);
                mDaoMaster = new DaoMaster(helper.getWritableDatabase()); //获取未加密的数据库
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mDaoMaster;
    }


    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        Log.i(TAG, "[onTerminate]");

        unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.i(TAG, "[onConfigurationChanged]");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Log.i(TAG, "[onLowMemory]");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        Log.i(TAG, "[onTrimMemory]");
    }

    /****************************************************************************************/

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);

        Log.i(TAG, "[registerComponentCallbacks]");
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        super.unregisterComponentCallbacks(callback);

        Log.i(TAG, "[unregisterComponentCallbacks]");
    }

    /****************************************************************************************/

    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);

        Log.i(TAG, "[registerActivityLifecycleCallbacks]");
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.unregisterActivityLifecycleCallbacks(callback);

        Log.i(TAG, "[unregisterActivityLifecycleCallbacks]");
    }

    /****************************************************************************************/

    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i(TAG, "[onActivityCreated]");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.i(TAG, "[onActivityStarted]");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.i(TAG, "[onActivityResumed]");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.i(TAG, "[onActivityPaused]");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.i(TAG, "[onActivityStopped]");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.i(TAG, "[onActivitySaveInstanceState]");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(TAG, "[onActivityDestroyed]");
        }
    };
}
