package com.readboy.showappdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.readboy.showappdemo.data.AsmData;
import com.readboy.sound.Sound;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import static android.R.attr.name;

public class MainActivity extends AppCompatActivity {
    private String TAG = "KLIVITAM=========>>>>>";
    private AsmData mData;
    private String dataPathName;
    private Button mBtn;
    Sound sound = new Sound();
    RandomAccessFile raf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatas();
        mBtn = (Button) findViewById(R.id.btn01);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                read();
            }
        });

    }

    private void read() {
        try {
            raf = new RandomAccessFile(getPath("1003"),"r");
            sound.setDataSource(raf.getFD(),0,3762);
            sound.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "read: "+e);
        }


    }

    private String getPath(String s) throws IOException {
        File f1 = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/guoxuejingdian/music/");
        if (!f1.exists()) {
            f1.mkdirs();
        }
        String name = s + ".mp3";
        File f = new File(f1, name);
        if (!f.exists()) {
            f.createNewFile();
            f.setReadable(true);
        }
        Log.i(TAG, "getPath: "+f.getAbsolutePath());
        return f.getAbsolutePath();
    }

    private void initDatas() {
        mData = new AsmData();
        dataPathName = asmGetDataName();
        boolean ret = mData.asmDataReadInit(dataPathName);
        Log.i(TAG, "initDatas: "+ret);
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
        String name = getIntent().getStringExtra("data_name");

        if (name == null) {
            // 默认进入“成语典故”应用
//            name = getResources().getString(R.string.data_name_default);
            name = "guoxuejingdian.pin";
        }

        com.readboy.showappdemo.util.Log.i(TAG, "[asmGetDataName] name == " + name);

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
            com.readboy.showappdemo.util.Log.i(TAG, "[asmGetDataName] DataName =1= " + dataname);

            return dataname;
        }

        // 二、在/system/readboy/asm/目录下寻找*.pin数据文件
        path = "/system/readboy";
        path += "/asm/";
        dataname = path + name;
        if (new File(dataname).exists()) {
            com.readboy.showappdemo.util.Log.i(TAG, "[asmGetDataName] DataName =2= " + dataname);

            return dataname;
        }

        // 三、在/system/lib/目录下寻找lib*.so数据文件
        path = "/system/lib/";
        dataname = path + "lib" + name.replace(".pin", ".so");
        if (new File(dataname).exists()) {
            com.readboy.showappdemo.util.Log.i(TAG, "[asmGetDataName] DataName =3= " + dataname);

            return dataname;
        }

        // 四、在应用程序目录下寻找lib*.so数据文件
        path = getFilesDir().getPath().replace("files", "lib") + "/";
        dataname = path + "lib" + name.replace(".pin", ".so");
        if (new File(dataname).exists()) {
            com.readboy.showappdemo.util.Log.i(TAG, "[asmGetDataName] DataName =4= " + dataname);

            return dataname;
        }


        com.readboy.showappdemo.util.Log.i(TAG, "[asmGetDataName] DataName =0= " + dataname);

        return dataname;
    }
//    /**
//     * 获取数据文件名。
//     *
//     * @return 数据文件名
//     */
//    private String asmGetDataName() {
//        String dataname = null;
//        try {
//            dataname = Environment.getExternalStorageDirectory().getCanonicalPath() + "/klivitam_text/py.pin";
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.i(TAG, "asmGetDataName: error"+e);
//        }
//        return dataname;
//    }
}
