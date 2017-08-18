package com.readboy.showappdemo.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.readboy.showappdemo.AsmApplication;
import com.readboy.showappdemo.bean.TxtBean;
import com.readboy.showappdemo.entry.TxtEntry;


/**
 * ASM相关数据的处理，与平台独立，不使用全局变量。
 *
 * @author lyj
 * @version 1.14.04.01
 * @date 2014.04.01
 * @history lyj 2013.08.01在Android平台上重写了此代码
 */
public class AsmData extends AsmBase {
    // =================================================================================================
    // 表示相关数据结构的类。
    // =================================================================================================

    /**
     * 表示DataType3（题目 + 答案数据）按钮的类。
     */
    class AsmDataType3Button {
        Rect rect;                                                    // 按钮显示区域
        Bitmap[] hdc;                                                // 按钮图片

        public AsmDataType3Button() {
            rect = new Rect();
            hdc = new Bitmap[AsmConstant.DataType3_BUTTON_NUM];
        }
    }

    /**
     * 表示MenuType1（图片列表菜单）的类。
     */
    class AsmMenuType1 {
        int is_exit_father;                                            // 父菜单项目，0xFF表示没有
        int father_addr;                                            // 父菜单地址，0x00表示没有
        int total_item;                                                // 子项目个数
        int L_MouseArea;                                            // 列表区域地址
        int L_First_PicTable;                                        // 列表图片1地址
        int L_Second_PicTable;                                        // 列表图片2地址
        int L_LastPic_PicTable;                                        // 列表图片3地址
        int L_ExtItemFuncxx;                                        // 列表操作地址
        int L_BackGroundPic;                                        // 背景图片地址

        int screen_item;                                            // 每屏显示的条目个数
        int select_item;                                            // 当前选择的条目

        Rect rect;                                                    // 列表区域
        Bitmap[] first_pic;                                            // 列表图片1
        Bitmap[] second_pic;                                        // 列表图片2
        Bitmap[] last_pic;                                            // 列表图片1

        int[] ItemFunc;                                                // 列表操作

        int x_bk;                                                    // 背景图片显示X坐标
        int y_bk;                                                    // 背景图片显示Y坐标
        Bitmap hdcBk;                                                // 背景图片

        boolean is_read_second;                                        // 貌似是先把界面显示出来，再加载菜单图片内容

        public AsmMenuType1() {
            rect = new Rect();

            first_pic = new Bitmap[AsmConstant.MenuType1_ITEM_NUM_MAX];
            second_pic = new Bitmap[AsmConstant.MenuType1_ITEM_NUM_MAX];
            last_pic = new Bitmap[AsmConstant.MenuType1_ITEM_NUM_MAX];

            ItemFunc = new int[AsmConstant.MenuType1_ITEM_NUM_MAX];
        }
    }

    /**
     * 表示MenuType2（文字列表菜单）的类。
     */
    class AsmMenuType2 {
        int is_exit_father;                                            // 父菜单项目，0xFF表示没有
        int father_addr;                                            // 父菜单地址，0x00 表示没有
        int total_item;                                                // 子项目个数
        int L_ExtItemTableCL;                                        // 列表文字地址地址
        int L_ExtItemFuncxx;                                        // 列表操作地址地址
        int L_DisplayArea;                                            // 列表区域地址
        int L_BackGroundPic;                                        // 背景图片地址

        Rect rect;                                                    // 列表区域
        int[] ItemFunc;                                                // 列表文字地址
        byte[][] ItemText;                                            // 列表文字
        String[] ItemName;                                            // 列表名称

        int x_bk;                                                    // 背景图片显示X坐标
        int y_bk;                                                    // 背景图片显示Y坐标
        Bitmap hdcBk;                                                // 背景图片

        int self_addr;                                                // 文字列表菜单地址，供从下一级返回到文字列表菜单使用

        public AsmMenuType2() {
            rect = new Rect();
            ItemFunc = new int[AsmConstant.MenuType2_ITEM_NUM_MAX];
            ItemText = new byte[AsmConstant.MenuType2_ITEM_NUM_MAX][AsmConstant.MenuType2_ITEM_TEXT_MAX];
            ItemName = new String[AsmConstant.MenuType2_ITEM_NUM_MAX];
        }
    }

    /**
     * 表示MenuType3（图片按钮菜单）的类。
     */
    class AsmMenuType3 {
        int is_exit_father;                                            // 父菜单项目，0xFF表示没有
        int father_addr;                                            // 父菜单地址，0x00 表示没有
        int total_item;                                                // 子项目个数
        int L_DisplayArea;                                            // 显示区域地址
        int L_First_PicTable;                                        // 列表图片1地址
        int L_Second_PicTable;                                        // 列表图片2地址
        int L_LastPic_PicTable;                                        // 列表图片3地址
        int L_ExtItemFuncxx;                                        // 列表操作地址地址
        int L_BackGroundPic;                                        // 背景图片地址

        int screen_item;                                            // 每屏显示的条目个数

        int select_item;                                            // 当前选中的条目

        Rect[] rect;                                                // 列表区域
        int[] ItemFunc;                                                // 列表操作地址
        Bitmap[] first_pic;                                            // 列表图片1
        Bitmap[] second_pic;                                        // 列表图片2
        Bitmap[] last_pic;                                            // 列表图片3

        int x_bk;                                                    // 背景图片显示X坐标
        int y_bk;                                                    // 背景图片显示Y坐标
        Bitmap hdcBk;                                                // 背景图片

        public AsmMenuType3() {
            rect = new Rect[AsmConstant.MenuType3_ITEM_NUM_MAX];

            for (int i = 0; i < AsmConstant.MenuType3_ITEM_NUM_MAX; i++) {
                rect[i] = new Rect();
            }

            ItemFunc = new int[AsmConstant.MenuType3_ITEM_NUM_MAX];

            first_pic = new Bitmap[AsmConstant.MenuType3_ITEM_NUM_MAX];
            second_pic = new Bitmap[AsmConstant.MenuType3_ITEM_NUM_MAX];
            last_pic = new Bitmap[AsmConstant.MenuType3_ITEM_NUM_MAX];
        }
    }

    /**
     * 表示DataType1（纯文本数据）的类。
     */
    class AsmDataType1 {
        int x_bk;                                                    // 背景图片显示X坐标
        int y_bk;                                                    // 背景图片显示Y坐标
        int is_have_bk;                                                // 是否有背景图片

        Bitmap hdcBk;                                                // 背景图片
        Rect rect;                                                    // 文本显示区域
        byte[] text_buff;                                            // 文本内容
        String text;                                                // 文本

        public AsmDataType1() {
            rect = new Rect();
            text_buff = new byte[AsmConstant.ASM_TEXT_SIZE_MAX];
        }
    }

    /**
     * 表示SDataType3（题目 + 答案数据）的类。
     */
    class AsmDataType3 {
        Rect rect;                                                    // 题目/答案显示区域
        AsmDataType3Button[] btn;                                    // 按钮图片

        int item_cur;                                                // 当前题目
        int item_num;                                                // 题目个数
        int[] ItemFunc;                                                // 操作地址

        int x_bk;                                                    // 背景图片显示X坐标
        int y_bk;                                                    // 背景图片显示Y坐标
        Bitmap hdcBk;                                                // 背景图片

        byte[] text_question;                                        // 题目文本
        byte[] text_answer;                                            // 答案文本

        //String str_question;										//  题目
        //String str_answer;										//  答案

        public AsmDataType3() {
            rect = new Rect();
            ItemFunc = new int[AsmConstant.DataType3_ITEM_NUM_MAX];
            btn = new AsmDataType3Button[AsmConstant.DataType3_BUTTON_NUM];
            for (int i = 0; i < AsmConstant.DataType3_BUTTON_NUM; i++) {
                btn[i] = new AsmDataType3Button();
            }
            text_question = new byte[AsmConstant.DataType3_TEXT_NUM_MAX];
            text_answer = new byte[AsmConstant.DataType3_TEXT_NUM_MAX];
        }
    }

    /**
     * 表示SDataType4（百家姓）的类。
     */
    class AsmDataType4 {
        int is_exit_father;                                            // 父菜单项目，0xFF表示没有
        int father_addr;                                            // 父菜单地址，0x00表示没有
        int L_SentenceSectionBJXTable;                                // 列表文本地址
        int L_DisplayArea;                                            // 文本显示区域地址
        int L_BackGroundPic;                                        // 背景图片地址

        int total_item;                                                // 列表条目个数

        int cur_item;                                                // 当前条目

        byte[][] ItemText;                                            // 列表文本
        int[] Itemspeech;                                            // 列表文本声音

        //String[] ItemName;										// 列表名称

        Rect rect;                                                    // 文本显示区域

        int x_bk;                                                    // 背景图片显示X坐标
        int y_bk;                                                    // 背景图片显示Y坐标
        Bitmap hdcBk;                                                // 背景图片

        public AsmDataType4() {
            rect = new Rect();
            ItemText = new byte[AsmConstant.DataType4_ITEM_NUM_MAX][AsmConstant.DataType4_ITEM_TEXT_MAX];
            Itemspeech = new int[AsmConstant.DataType4_ITEM_NUM_MAX];
            //ItemName = new String[AsmConstant.DataType4_ITEM_NUM_MAX];
        }
    }

    /**
     * 表示SDataType5（三字经、弟子规、千字文）的类。
     */
    class AsmDataType5 {
        int is_exit_father;                                            // 父菜单项目，0xFF表示没有
        int father_addr;                                            // 父菜单地址，0x00表示没有
        int L_SentenceSectionBJXTable;                                // 列表文本地址
        int L_DisplayArea;                                            // 文本显示区域地址
        int L_BackGroundPic;                                        // 背景图片地址

        int total_item;                                                // 列表条目个数

        int cur_item;                                                // 当前条目

        byte[][] ItemText;                                            // 列表文本
        int[] Itemspeech;                                            // 列表文本声音

        //String[] ItemName;										// 列表名称

        Rect rect;                                                    // 文本显示区域

        int x_bk;                                                    // 背景图片显示X坐标
        int y_bk;                                                    // 背景图片显示Y坐标
        Bitmap hdcBk;                                                // 背景图片

        public AsmDataType5() {
            rect = new Rect();
            ItemText = new byte[AsmConstant.DataType4_ITEM_NUM_MAX][AsmConstant.DataType4_TEXT_MAX];
            Itemspeech = new int[AsmConstant.DataType4_ITEM_NUM_MAX];
            //ItemName = new String[AsmConstant.DataType4_ITEM_NUM_MAX];
        }
    }

    // =================================================================================================
    // 成员变量。
    // =================================================================================================
    //private final String TAG = getClass().getSimpleName();		// TAG
    private final String TAG = "AsmData";                            // TAG

    int x_bk;                                                        // 背景图片显示X坐标
    int y_bk;                                                        // 背景图片显示Y坐标

    Bitmap hdcBk;                                                    // 背景图片

    int data_type;                                                    // 数据类型

    int ExternalDataAddr;                                            // 外部数据地址

    RandomAccessFile fp;                                            // 文件句柄

    AsmMenuType1 pMenuType1;                                        // SMenuType1
    AsmMenuType2 pMenuType2;                                        // SMenuType2
    AsmMenuType3 pMenuType3;                                        // SMenuType3

    AsmDataType1 pDataType1;                                        // SDataType1
    AsmDataType3 pDataType3;                                        // SDataType3
    AsmDataType4 pDataType4;                                        // SDataType4
    AsmDataType5 pDataType5;                                        // SDataType5

    byte[] pBuff;                                                    // 图片数据缓存
    int[] cBuff;                                                    // 图片颜色缓存

    // =================================================================================================
    // 相关的数据操作方法。
    // =================================================================================================

    /**
     * 构造函数。
     */
    public AsmData() {
        pBuff = new byte[AsmConstant.PANEL_WIDTH * AsmConstant.PANEL_HEIGHT * 2];
        cBuff = new int[AsmConstant.PANEL_WIDTH * AsmConstant.PANEL_HEIGHT];
    }

    /**
     * 创建一个Bitmap，并填入图片内容。
     *
     * @param fp     文件句柄
     * @param offset 地址偏移量
     * @param size   数据大小
     * @return Bitmap对象
     */
    private Bitmap asmCreateBitmapByData(RandomAccessFile fp, int offset, int size) {
        Log.i(TAG, "[asmCreateBitmapByData] Start!!!");

        Bitmap bitmap = null;
        //byte[] pBuff = null;
        int width = 0;                                            // 图片宽度
        int height = 0;                                            // 图片高度

        try {
            Log.i(TAG, "[asmCreateBitmapByData] ReadFile Start!!!111111");

            fp.seek(offset);

            byte[] pHead = new byte[100];
            fp.read(pHead, 0, 100);

            boolean isPng = asmPngFileFormat(pHead);                    // 是否为Png图片

            if (isPng)                        // Png图片
            {
                Log.i(TAG, "[asmCreateBitmapByData] Is Png File!!!");

                //pBuff = new byte[size];
                fp.seek(offset);
                fp.read(pBuff, 0, size);

                //Log.i(TAG, "[asmCreateBitmapByData] Size == " + size);

                //Log.i(TAG, "[asmCreateBitmapByData] DecodeBitmap Start!!!");
                Options opts = new Options();
                opts.inPreferredConfig = Config.ARGB_8888;
                opts.inPurgeable = true;
                opts.inInputShareable = true;
                bitmap = BitmapFactory.decodeByteArray(pBuff, 0, size, opts);
                //bitmap = BitmapFactory.decodeByteArray(pBuff, 0, size);
                //pBuff = null;
                //Log.i(TAG, "[asmCreateBitmapByData] DecodeBitmap End!!!");
                width = bitmap.getWidth();
                height = bitmap.getHeight();

                //Log.i(TAG, "[asmCreateBitmapByData] Png : " + "W == " + width + ", H == " + height);

                if (width <= 0 || height <= 0 || width > AsmConstant.PANEL_WIDTH ||
                        height > AsmConstant.PANEL_HEIGHT) {
                    Log.e(TAG, "[asmCreateBitmapByData] Png File Error : " +
                            "W == " + width + ", H == " + height);

                    return null;
                }
            } else                            // Res图片
            {
                Log.i(TAG, "[asmCreateBitmapByData] Is Res File!!!");

                // 格式化Zip文件
                int[] zipInfo = new int[3];
                boolean isZip = asmZipFileFormat(pHead, zipInfo);        // 是否为Zip压缩文件

                // Zip压缩文件
                if (isZip) {
                    //Log.i(TAG, "[asmCreateBitmapByData] Is Zip File!!!");

                    int headerlen = zipInfo[0];                            // Zip文件头信息长度
                    int srcFileSize = zipInfo[1];                        // 源文件大小
                    //int dstFileSize = zipInfo[2];						// 目标文件大小

                    headerlen = headerlen * 2 + 38;                        // ？？？

                    //Log.i(TAG, "[asmCreateBitmapByData]" + " headerLen == " + headerlen +
                    //		", srcFileSize == " + srcFileSize + ", dstFileSize == " + dstFileSize);

                    // 原始数据
                    int srcDataSize = headerlen + srcFileSize;
                    byte[] srcData = new byte[srcDataSize];

                    fp.seek(offset);
                    fp.read(srcData, 0, srcDataSize);


                    // 解压缩Zip数据。
                    byte[] dstData = asmZipUncompress(srcData);

                    //Log.i(TAG, "[asmCreateBitmapByData]" + " srcDataSize == " + srcData.length +
                    //		", dstDataSize == " + dstData.length);

                    srcData = null;

                    width = asmBufferToNum(dstData, 0, 2);
                    height = asmBufferToNum(dstData, 2, 2);

                    //Log.i(TAG, "[asmCreateBitmapByData] Png : " + "W == " + width + ", H == " + height);

                    if (width > AsmConstant.PANEL_WIDTH || height > AsmConstant.PANEL_HEIGHT ||
                            width <= 0 || height <= 0) {
                        Log.e(TAG, "[asmCreateBitmapByData] Res Zip File Error : " +
                                "W == " + width + ", H == " + height);

                        return null;
                    }

                    //pBuff = new byte[width * height * 2];
                    System.arraycopy(dstData, 4, pBuff, 0, width * height * 2);
                    dstData = null;
                }
                // 非Zip压缩文件
                else {
                    Log.i(TAG, "[asmCreateBitmapByData] Not Zip File!!!");

                    fp.seek(offset);

                    byte[] buff = new byte[4];
                    fp.read(buff, 0, 4);
                    width = asmBufferToNum(buff, 0, 2);
                    height = asmBufferToNum(buff, 2, 2);

                    //Log.i(TAG, "[asmCreateBitmapByData] Png : " + "W == " + width + ", H == " + height);

                    if (width > AsmConstant.PANEL_WIDTH || height > AsmConstant.PANEL_HEIGHT ||
                            width <= 0 || height <= 0) {
                        Log.e(TAG, "[asmCreateBitmapByData] Res File Error : " +
                                "W == " + width + ", H == " + height);

                        return null;
                    }

                    //pBuff = new byte[width * height * 2];
                    fp.read(pBuff, 0, width * height * 2);
                }
                //Log.i(TAG, "[asmCreateBitmapByData] ReadFile End!!!");

                //Log.i(TAG, "[asmCreateBitmapByData] SetPixels Start!!!");
                /*bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                for(int y = 0; y < height; y++)
				{
					for(int x = 0; x < width; x++)
					{
						bitmap.setPixel(x, y, asmGetColors(pBuff, (x + y * width) * 2));
					}
				}*/
                //Log.i(TAG, "[asmCreateBitmapByData] SetPixels End!!!");

                //Log.i(TAG, "[asmCreateBitmapByData] GetColors Start!!!");
                //int[] cBuff = new int[width * height];
                for (int i = 0; i < width * height; i++) {
                    cBuff[i] = asmGetColors(pBuff, i * 2);
                }
                //pBuff = null;
                //Log.i(TAG, "[asmCreateBitmapByData] GetColors End!!!");

                //Log.i(TAG, "[asmCreateBitmapByData]" + " W == " + width + ", H == " + height);

                //Log.i(TAG, "[asmCreateBitmapByData] SetPixels Start!!!");
                bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                bitmap.setPixels(cBuff, 0, width, 0, 0, width, height);
                //cBuff = null;
                //Log.i(TAG, "[asmCreateBitmapByData] SetPixels End!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmCreateBitmapByData] Error!!!");
        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();

            Log.e(TAG, "[asmCreateBitmapByData] OutOfMemory Error!!!");
        }

        if (bitmap == null) {
            Log.e(TAG, "[asmCreateBitmapByData] Bitmap == null!!!");
        }

        Log.i(TAG, "[asmCreateBitmapByData] End!!!");

        return bitmap;
    }

    /**
     * 从DrawMap中按图片大小创建一个Bitmap，并填入图片内容。
     *
     * @param fp          文件句柄
     * @param offset      地址偏移
     * @param extDataAddr 外部数据地址
     * @param pxpyBuff    保存结果buff
     * @return Bitmap
     */
    private Bitmap asmCreateBitmapByDrawMap(RandomAccessFile fp, int offset, int extDataAddr, int[] pxpyBuff) {
        Log.i(TAG, "[asmCreateBitmapByDrawMap] Start!!!");

        Bitmap bitmap = null;

        if (offset == 0) {
            return null;
        }

        try {
            byte[] buff = new byte[8];

            fp.seek(offset);
            fp.read(buff, 0, 8);

            int off = 0;
            int temp = asmBufferToNum(buff, off, 1);

            // DrawMap头校验
            if (temp == AsmConstant.Macro_DrawMap) {
                // px
                off += 1;
                pxpyBuff[0] = asmBufferToNum(buff, off, 2);

                // py
                off += 2;
                pxpyBuff[1] = asmBufferToNum(buff, off, 2);

                // MapIndex
                off += 2;
                temp = asmBufferToNum(buff, off, 3);
                int index = asmGetIndex(temp);

                //Log.i(TAG, "[asmCreateBitmapByDrawMap] Pic Index == " + temp);

                // Define_Animation Addr
                fp.seek(extDataAddr + index * 4);

                int addr1 = asmReadNBytesToNum(fp, 0, 4);
                int addr2 = asmReadNBytesToNum(fp, 0, 4);

                int size = addr2 - addr1;
                ;

                int addr = addr1 + extDataAddr;

                bitmap = asmCreateBitmapByData(fp, addr, size);

            }
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmCreateBitmapByDrawMap] Error!!!");
        }
        try {
            saveBitmap(bitmap,"bg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "[asmCreateBitmapByDrawMap] End!!!");
        return bitmap;
    }

    /**
     * 读取列表图片。
     *
     * @param fp          文件句柄
     * @param offset      地址偏移
     * @param itemNum     列表数量
     * @param extDataAddr 外部数据地址
     * @return 列表图片
     */
    private Bitmap[] asmReadDrawItemMap(RandomAccessFile fp, int offset, int itemNum, int extDataAddr) {
//        Log.i(TAG, "[asmReadItemMap] Start!!!1111111111111");

        Bitmap[] bitmap = new Bitmap[itemNum];

        try {
            byte[] buff = new byte[itemNum * 8];
            fp.seek(offset);
            fp.read(buff, 0, itemNum * 8);

            for (int i = 0; i < itemNum; i++) {
                int off = i * 8 + 5;

                int index = asmBufferToNum(buff, off, 3);
                index = asmGetIndex(index);

                //Log.i(TAG, "[asmReadDrawItemMap] Pic Index == " + index);

                fp.seek(extDataAddr + index * 4);

                int addr1 = asmReadNBytesToNum(fp, 0, 4);
                int addr2 = asmReadNBytesToNum(fp, 0, 4);

                //Bitmap Size
                int size = addr2 - addr1;

                //Bitmap Addr
                int addr = addr1 + extDataAddr;

                bitmap[i] = asmCreateBitmapByData(fp, addr, size);
            }
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmReadItemMap] Error!!!");
        }

        //Log.i(TAG, "[asmReadItemMap] End!!!");

        return bitmap;
    }

    /**
     * 读取MemuType1（图片列表菜单）。
     *
     * @param fp          文件句柄
     * @param offset      地址偏移量
     * @param p           SMenuType1
     * @param extDataAddr 外部数据地址
     * @return 成功返回true，失败返回false
     */
    private boolean asmReadMemuType1(RandomAccessFile fp, int offset, AsmMenuType1 p, int extDataAddr) {
        Log.i(TAG, "[asmReadMemuType1] Start!!!");

        boolean ret = true;

        try {
            byte[] buff = new byte[64];
            fp.seek(offset);
            fp.read(buff, 0, 64);

            int off = 0;
            p.is_exit_father = asmBufferToNum(buff, off, 2);        // 父菜单项目，0xFF表示没有
            off += 2;
            p.father_addr = asmBufferToNum(buff, off, 4);            // 父菜单地址，0x00 表示没有
            off += 4;
            p.total_item = asmBufferToNum(buff, off, 2);            // 子项目个数
            off += 2;
            p.L_MouseArea = asmBufferToNum(buff, off, 4);            // 列表区域地址
            off += 4;
            p.L_First_PicTable = asmBufferToNum(buff, off, 4);        // 列表图片1地址
            off += 4;
            p.L_Second_PicTable = asmBufferToNum(buff, off, 4);        // 列表图片2地址
            off += 4;
            p.L_LastPic_PicTable = asmBufferToNum(buff, off, 4);    // 列表图片3地址
            off += 4;
            p.L_ExtItemFuncxx = asmBufferToNum(buff, off, 4);        // 列表操作地址地址
            off += 4;
            p.L_BackGroundPic = asmBufferToNum(buff, off, 4);        // 背景图片地址

			/*Log.i(TAG, "[asmReadMemuType1] " + "is_exit_father == " + p.is_exit_father +
                    ", father_addr == " + p.father_addr + ", total_item == " + p.total_item +
					", L_MouseArea == " + p.L_MouseArea +
					", L_First_PicTable == " + p.L_First_PicTable +
					", L_Second_PicTable == " + p.L_Second_PicTable +
					", L_LastPic_PicTable == " + p.L_LastPic_PicTable +
					", L_ExtItemFuncxx == " + p.L_ExtItemFuncxx +
					", L_BackGroundPic == " + p.L_BackGroundPic);*/

            if (p.total_item > AsmConstant.MenuType1_ITEM_NUM_MAX) {
                p.total_item = AsmConstant.MenuType1_ITEM_NUM_MAX - 1;

                Log.w(TAG, "[asmReadMemuType1] Warning:MenuType1 num is more than MenuType1_ITEM_NUM_MAX!!!");
            }

            // 读取背景图片
            asmRecycleBitmap(p.hdcBk);

            int[] pxpyBuff = new int[2];
            p.hdcBk = asmCreateBitmapByDrawMap(fp, p.L_BackGroundPic, extDataAddr, pxpyBuff);

            p.x_bk = pxpyBuff[0];
            p.y_bk = pxpyBuff[1];

			/*Log.i(TAG, "[asmReadMemuType1] " + "x_bk == " + p.x_bk + ", y_bk == " + p.y_bk);
			if(p.hdcBk != null)
			{
				Log.i(TAG, "[asmReadMemuType1] " + "w_bk == " + p.hdcBk.getWidth() + ", h_bk == " + p.hdcBk.getHeight());
			}*/

            // 读取列表操作地址
            asmReadIntArray(fp, p.L_ExtItemFuncxx, p.ItemFunc, p.total_item);
            for (int i = 0; i < p.ItemFunc.length; i++) {
                Log.i(TAG, "asmReadMemuType1: " + p.ItemFunc[i]);
            }

			/*for(int i = 0; i < p.total_item; i++)
			{
				Log.i(TAG, "[asmReadMemuType1] " + "ItemFunc[" + i + "] == " + p.ItemFunc[i]);
			}*/

            // 读取列表区域
            buff = new byte[10];
            fp.seek(p.L_MouseArea);
            fp.read(buff, 0, 10);

            off = 0;
            p.screen_item = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.left = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.top = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.right = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.bottom = asmBufferToNum(buff, off, 2);

			/*Log.i(TAG, "[asmReadMemuType1] " + "screen_item == " + p.screen_item +
					", rect.left == " + p.rect.left + ", rect.top == " + p.rect.top +
					", rect.right == " + p.rect.right + ", rect.bottom == " + p.rect.bottom);*/

            // 读取列表图片
            p.first_pic = asmReadDrawItemMap(fp, p.L_First_PicTable, p.total_item, extDataAddr);
            for (int i = 0; i < p.first_pic.length; i++) {
                saveBitmap(p.first_pic[i], "frist0" + i);
            }
            p.second_pic = asmReadDrawItemMap(fp, p.L_Second_PicTable, p.total_item, extDataAddr);
            for (int i = 0; i < p.second_pic.length; i++) {
                saveBitmap(p.first_pic[i], "second0" + i);
            }
            p.last_pic = asmReadDrawItemMap(fp, p.L_LastPic_PicTable, p.total_item, extDataAddr);
            for (int i = 0; i < p.last_pic.length; i++) {
                saveBitmap(p.first_pic[i], "last0" + i);
            }

        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmReadMemuType1] Error!!!");

            ret = false;
        }

        Log.i(TAG, "[asmReadMemuType1] End!!!");

        return ret;
    }

    public static void saveBitmap(Bitmap bitmap, String name) throws IOException {
        File f1 = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/quweituxing/bitmap/");
        if (!f1.exists()) {
            f1.mkdirs();
        }
        name = name + ".png";
        File f = new File(f1, name);
        if (!f.exists()) {
            f.createNewFile();
            f.setReadable(true);
        } else {
            return;
        }
        FileOutputStream out = new FileOutputStream(f);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
    }

    /**
     * 把Bitmap转Byte
     */
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 读取MemuType1的列表图片。
     *
     * @param fp          文件句柄
     * @param offset      地址偏移量
     * @param p           SMenuType1
     * @param extDataAddr 外部数据地址
     * @return 成功返回true，失败返回false
     */
    @SuppressWarnings("unused")
    private boolean asmReadMemuType1ItemMap(RandomAccessFile fp, int offset, AsmMenuType1 p, int extDataAddr) {
        Log.i(TAG, "[asmReadMemuType1ItemMap] Start!!!");

        boolean ret = true;

        if (p == null) {
            return false;
        }

        if (p.is_read_second) {
            return false;
        }

        p.is_read_second = true;

        try {
            // 读取列表图片
            p.first_pic = asmReadDrawItemMap(fp, p.L_First_PicTable, p.total_item, extDataAddr);
            for (int i = 0; i < p.first_pic.length; i++) {
                saveBitmap(p.first_pic[i], "frist0" + i);
            }
            p.second_pic = asmReadDrawItemMap(fp, p.L_Second_PicTable, p.total_item, extDataAddr);
            for (int i = 0; i < p.second_pic.length; i++) {
                saveBitmap(p.second_pic[i], "second0" + i);
            }
            p.last_pic = asmReadDrawItemMap(fp, p.L_LastPic_PicTable, p.total_item, extDataAddr);
            for (int i = 0; i < p.last_pic.length; i++) {
                saveBitmap(p.last_pic[i], "second0" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmReadMemuType1ItemMap] Error!!!");

            ret = false;
        }

        Log.i(TAG, "[asmReadMemuType1ItemMap] End!!!");

        return ret;
    }

    /**
     * 读取MemuType2（文字列表菜单）。
     *
     * @param fp          文件句柄
     * @param offset      地址偏移量
     * @param p           SMenuType2
     * @param extDataAddr 外部数据地址
     * @return 成功返回true，失败返回false
     */
    private boolean asmReadMemuType2(RandomAccessFile fp, int offset, AsmMenuType2 p, int extDataAddr) {
        Log.i(TAG, "[asmReadMemuType2] Start!!!");

        boolean ret = true;

        try {
            byte[] buff = new byte[64];
            fp.seek(offset);
            fp.read(buff, 0, 64);

            int off = 0;
            p.is_exit_father = asmBufferToNum(buff, off, 2);        // 父菜单项目，0xFF表示没有
            off += 2;
            p.father_addr = asmBufferToNum(buff, off, 4);            // 父菜单地址，0x00 表示没有
            off += 4;
            p.total_item = asmBufferToNum(buff, off, 2);            // 子项目个数
            off += 2;
            p.L_ExtItemTableCL = asmBufferToNum(buff, off, 4);        // 列表文字地址地址
            off += 4;
            p.L_ExtItemFuncxx = asmBufferToNum(buff, off, 4);        // 列表操作地址地址
            off += 4;
            p.L_DisplayArea = asmBufferToNum(buff, off, 4);            // 列表区域地址
            off += 4;
            p.L_BackGroundPic = asmBufferToNum(buff, off, 4);        // 背景图片地址

            if (p.total_item > AsmConstant.MenuType2_ITEM_NUM_MAX) {
                p.total_item = AsmConstant.MenuType2_ITEM_NUM_MAX - 1;

                Log.w(TAG, "[asmReadMemuType2] Warning:MenuType2 num is more than MenuType2_ITEM_NUM_MAX!!!");
            }

            // 读取背景图片
            asmRecycleBitmap(p.hdcBk);

            int[] pxpyBuff = new int[2];
            p.hdcBk = asmCreateBitmapByDrawMap(fp, p.L_BackGroundPic, extDataAddr, pxpyBuff);
            p.x_bk = pxpyBuff[0];
            p.y_bk = pxpyBuff[1];


            // 读取列表文字地址
            asmReadIntArray(fp, p.L_ExtItemTableCL, p.ItemFunc, p.total_item);
            // 读取列表文字
            List<TxtBean> list = new ArrayList<>();
            for (int i = 0; i < p.total_item; i++) {
                fp.seek(p.ItemFunc[i] + 1);
                fp.read(p.ItemText[i], 0, AsmConstant.MenuType2_ITEM_TEXT_MAX);
                p.ItemText[i][AsmConstant.MenuType2_ITEM_TEXT_MAX - 1] = 0;
                p.ItemName[i] = asmGetString(p.ItemText[i], 0, AsmConstant.ASM_ASCII_CODE);
                Log.i("jkb", "asmReadMemuType2: " + p.ItemName[i]);
//                asmFreeDataType1(pDataType1);
//                pDataType1 = new AsmDataType1();
//                asmReadDataType1(fp, num, pDataType1, ExternalDataAddr);
//                Log.i("jkb", "asmReadMemuType2: "+pDataType1.text);
//                TxtBean bean = new TxtBean(p.ItemName[i],pDataType1.text);
//                list.add(bean);
            }
//            TxtEntry entry = new TxtEntry();
//            entry.setOffset(offset);
//            entry.setMDatas(new Gson().toJson(list));
//            Log.i("klivitam", "asmReadMemuType2: "+new Gson().toJson(list));
//            AsmApplication.getInstances().getDaoSession().getTxtEntryDao().insert(entry);

            // 读取列表操作地址
            asmReadIntArray(fp, p.L_ExtItemFuncxx, p.ItemFunc, p.total_item);

            // 读取列表区域
            buff = new byte[8];
            fp.seek(p.L_DisplayArea);
            fp.read(buff, 0, 8);

            off = 0;
            p.rect.left = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.top = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.right = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.bottom = asmBufferToNum(buff, off, 2);
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmReadMemuType2] Error!!!" + e);

            ret = false;
        }

        Log.i(TAG, "[asmReadMemuType2] End!!!");

        return ret;
    }

    /**
     * 读取MemuType3（图片按钮菜单）。
     *
     * @param fp          文件句柄
     * @param offset      地址偏移量
     * @param p           SMenuType3
     * @param extDataAddr 外部数据地址
     * @return 成功返回true，失败返回false
     */
    private boolean asmReadMemuType3(RandomAccessFile fp, int offset, AsmMenuType3 p, int extDataAddr) {
        Log.i(TAG, "[asmReadMemuType3] Start!!!");

        boolean ret = true;

        try {
            byte[] buff = new byte[64];
            fp.seek(offset);
            fp.read(buff, 0, 64);

            int off = 0;
            p.is_exit_father = asmBufferToNum(buff, off, 2);        // 父菜单项目，0xFF表示没有
            off += 2;
            p.father_addr = asmBufferToNum(buff, off, 4);            // 父菜单地址，0x00 表示没有
            off += 4;
            p.total_item = asmBufferToNum(buff, off, 2);            // 子项目个数
            off += 2;
            p.L_DisplayArea = asmBufferToNum(buff, off, 4);            // 显示区域地址
            off += 4;
            p.L_First_PicTable = asmBufferToNum(buff, off, 4);        // 列表图片1地址
            off += 4;
            p.L_Second_PicTable = asmBufferToNum(buff, off, 4);        // 列表图片2地址
            off += 4;
            p.L_LastPic_PicTable = asmBufferToNum(buff, off, 4);    // 列表图片3地址
            off += 4;
            p.L_ExtItemFuncxx = asmBufferToNum(buff, off, 4);        // 列表操作地址地址
            off += 4;
            p.L_BackGroundPic = asmBufferToNum(buff, off, 4);        // 背景图片地址

			/*Log.i(TAG, "[asmReadMemuType3] " + "is_exit_father == " + p.is_exit_father +
					", father_addr == " + p.father_addr + ", total_item == " + p.total_item +
					", L_DisplayArea == " + p.L_DisplayArea +
					", L_First_PicTable == " + p.L_First_PicTable +
					", L_Second_PicTable == " + p.L_Second_PicTable +
					", L_LastPic_PicTable == " + p.L_LastPic_PicTable +
					", L_ExtItemFuncxx == " + p.L_ExtItemFuncxx +
					", L_BackGroundPic == " + p.L_BackGroundPic);*/

            if (p.total_item > AsmConstant.MenuType3_ITEM_NUM_MAX) {
                p.total_item = AsmConstant.MenuType3_ITEM_NUM_MAX - 1;

                Log.w(TAG, "[asmReadMemuType3] Warning:MenuType3 num is more than MenuType3_ITEM_NUM_MAX!!!");
            }

            // 读取背景图片
            asmRecycleBitmap(p.hdcBk);

            int[] pxpyBuff = new int[2];
            p.hdcBk = asmCreateBitmapByDrawMap(fp, p.L_BackGroundPic, extDataAddr, pxpyBuff);
            p.x_bk = pxpyBuff[0];
            p.y_bk = pxpyBuff[1];

			/*Log.i(TAG, "[asmReadMemuType3] " + "x_bk == " + p.x_bk + ", y_bk == " + p.y_bk);
			if(p.hdcBk != null)
			{
				Log.i(TAG, "[asmReadMemuType3] " + "w_bk == " + p.hdcBk.getWidth() + ", h_bk == " + p.hdcBk.getHeight());
			}*/

            // 读取列表操作地址
            asmReadIntArray(fp, p.L_ExtItemFuncxx, p.ItemFunc, p.total_item);

			/*for(int i = 0; i < p.total_item; i++)
			{
				Log.i(TAG, "[asmReadMemuType3] " + "ItemFunc[" + i + "] == " + p.ItemFunc[i]);
			}*/

            // 读取显示区域
            buff = new byte[p.total_item * 8 + 2];
            fp.seek(p.L_DisplayArea);
            fp.read(buff, 0, p.total_item * 8 + 2);

            off = 0;
            p.screen_item = asmBufferToNum(buff, off, 2);
            off += 2;
            for (int i = 0; i < p.total_item; i++) {
                p.rect[i].left = asmBufferToNum(buff, off, 2);
                off += 2;
                p.rect[i].top = asmBufferToNum(buff, off, 2);
                off += 2;
                p.rect[i].right = asmBufferToNum(buff, off, 2);
                off += 2;
                p.rect[i].bottom = asmBufferToNum(buff, off, 2);
                off += 2;
            }

			/*Log.i(TAG, "[asmReadMemuType3] " + "screen_item == " + p.screen_item );
			for(int i = 0; i < p.total_item; i++)
			{
				Log.i(TAG, "[asmReadMemuType3] " +
						"rect[" + i + "].left == " + p.rect[i].left +
						", rect[" + i + "].top == " + p.rect[i].top +
						", rect[" + i + "].right == " + p.rect[i].right +
						", rect[" + i + "].bottom == " + p.rect[i].bottom);
			}*/

            // 读取列表图片
            p.first_pic = asmReadDrawItemMap(fp, p.L_First_PicTable, p.total_item, extDataAddr);
            p.second_pic = asmReadDrawItemMap(fp, p.L_Second_PicTable, p.total_item, extDataAddr);
            p.last_pic = asmReadDrawItemMap(fp, p.L_LastPic_PicTable, p.total_item, extDataAddr);
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmReadMemuType3] Error!!!");

            ret = false;
        }

        Log.i(TAG, "[asmReadMemuType3] End!!!");

        return ret;
    }

    /**
     * 读取DataType1（纯文本数据）。
     *
     * @param fp          文件句柄
     * @param offset      地址偏移量
     * @param p           DataType1
     * @param extDataAddr 外部数据地址
     * @return 成功返回true，失败返回false
     */
    private boolean asmReadDataType1(RandomAccessFile fp, int offset, AsmDataType1 p, int extDataAddr) {
        Log.i("jkbbb", "[asmReadDataType1] Start!!!" + offset);

        boolean ret = true;

        try {
            byte[] buff = new byte[64];

            fp.seek(offset);
            fp.read(buff, 0, 64);
            int off = 0;
            off += 6;
            int L_BackGroundPic = asmBufferToNum(buff, off, 4);            // 背景图片地址
            off += 4;
            int L_DisplayArea = asmBufferToNum(buff, off, 4);            // 文本显示区域地址
            off += 4;
            int L_ExtTextFuncCL = asmBufferToNum(buff, off, 4);            // 文本内容地址

			/*Log.i(TAG, "[asmReadDataType1] " + "L_BackGroundPic == " + L_BackGroundPic +
					", L_DisplayArea == " + L_DisplayArea + ", L_ExtTextFuncCL == " + L_ExtTextFuncCL);*/

            // 读取背景图片
            p.is_have_bk = L_BackGroundPic;
            asmRecycleBitmap(p.hdcBk);

            int[] pxpyBuff = new int[2];
            p.hdcBk = asmCreateBitmapByDrawMap(fp, L_BackGroundPic, extDataAddr, pxpyBuff);
            p.x_bk = pxpyBuff[0];
            p.y_bk = pxpyBuff[1];

			/*Log.i(TAG, "[asmReadDataType1] " + "x_bk == " + p.x_bk + ", y_bk == " + p.y_bk);
			if(p.hdcBk != null)
			{
				Log.i(TAG, "[asmReadDataType1] " + "w_bk == " + p.hdcBk.getWidth() +
					", h_bk == " + p.hdcBk.getHeight());
			}*/

            // 读取文本显示区域
            buff = new byte[8];
            fp.seek(L_DisplayArea);
            fp.read(buff, 0, 8);

            off = 0;
            p.rect.left = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.top = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.right = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.bottom = asmBufferToNum(buff, off, 2);

			/*Log.i(TAG, "[asmReadDataType1] " +
					"rect.left == " + p.rect.left + ", rect.top == " + p.rect.top +
					", rect.right == " + p.rect.right + ", rect.bottom == " + p.rect.bottom);*/

            // 读取文本内容
            buff = new byte[8];
            fp.seek(L_ExtTextFuncCL);
            fp.read(buff, 0, 8);

            off = 0;
            int temp = asmBufferToNum(buff, off, 1);
            off += 1;
            int x = asmBufferToNum(buff, off, 2);
            off += 2;
            int y = asmBufferToNum(buff, off, 2);
            off += 2;

            int size = 0;// 文本内容大小
            if (temp == AsmConstant.Macro_DrawTextIndex && x == 0 && y == 0)    // DrawTextIndex
            {
                temp = asmBufferToNum(buff, off, 3);
                temp = asmGetIndex(temp);

                buff = new byte[8];
                fp.seek(extDataAddr + 4 * temp);
                fp.read(buff, 0, 8);

                off = 0;
                temp = asmBufferToNum(buff, off, 4);
                off += 4;
                size = asmBufferToNum(buff, off, 4);
                size -= temp;
                temp += extDataAddr;
                L_ExtTextFuncCL = temp;

                if (size > AsmConstant.ASM_TEXT_SIZE_MAX - 1) {
                    size = AsmConstant.ASM_TEXT_SIZE_MAX - 1;
                }
            } else                                                        // Text
            {
                size = AsmConstant.ASM_TEXT_SIZE_MAX - 1;
            }

            //Log.i(TAG, "[asmReadDataType1] " + "Size == " + size);

            fp.seek(L_ExtTextFuncCL);
            fp.read(p.text_buff, 0, size);

			/*for(int i = 0; i < size; i++)
			{
				Log.d(TAG, "p.text_buff[" + i + "] == " + p.text_buff[i]);
			}*/

			/*int i = 0;
			String data = "";
			for(byte d : p.text_buff)
			{
				data += d + " ";
				if(i%16 == 15)
				{
					Log.d(TAG, data);
					data = "";
				}
				i++;
			}*/

            if ((p.text_buff[0] & 0xFF) == 0xFF &&
                    (p.text_buff[1] & 0xFF) == 0xFE)                // UTF16_CODE
            {
                p.text = new String(AsmYBToUnicode.asmGetStr(p.text_buff, 2));
            } else                                                    // ASCII_CODE
            {
                p.text = asmGetString(p.text_buff, 0, AsmConstant.ASM_ASCII_CODE);
            }
//            TxtEntry entry = new TxtEntry();
//            entry.setOffset(offset);
//            entry.setMDatas(p.text);
//            AsmApplication.getInstances().getDaoSession().getTxtEntryDao().insert(entry);

            Log.i("jkb", "[asmReadDataType1] " + "p.text == " + p.text);
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmReadDataType1] Error!!!");

            ret = false;
        }

        Log.i(TAG, "[asmReadDataType1] End!!!");

        return ret;
    }

    /**
     * 读取DataType3（题目 + 答案数据）。
     *
     * @param fp          文件句柄
     * @param offset      地址偏移量
     * @param p           DataType3
     * @param extDataAddr 外部数据地址
     * @return 成功返回true，失败返回false
     */
    private boolean asmReadDataType3(RandomAccessFile fp, int offset, AsmDataType3 p, int extDataAddr) {
        Log.i(TAG, "[asmReadDataType3] Start!!!");

        boolean ret = true;

        try {
            byte[] buff = new byte[64];
            fp.seek(offset);
            fp.read(buff, 0, 64);

            int off = 0;
            off += 6;
            int L_BackGroundPic = asmBufferToNum(buff, off, 4);            // 背景图片地址
            off += 4;
            int L_DisplayAreaQuestion = asmBufferToNum(buff, off, 4);    // 题目/答案显示区域地址
            off += 4;
            int L_YuWen_MiYuCiMi = asmBufferToNum(buff, off, 4);        // 操作地址地址
            off += 4;
            int L_ExtItemAnswerQuestion = asmBufferToNum(buff, off, 4);    // 按钮图片地址

			/*Log.i(TAG, "[asmReadDataType3] " +
					"L_BackGroundPic == " + L_BackGroundPic +
					", L_DisplayAreaQuestion == " + L_DisplayAreaQuestion +
					", L_YuWen_MiYuCiMi == " + L_YuWen_MiYuCiMi +
					", L_ExtItemAnswerQuestion == " + L_ExtItemAnswerQuestion);*/

            // 读取背景图片
            asmRecycleBitmap(p.hdcBk);

            int[] pxpyBuff = new int[2];
            p.hdcBk = asmCreateBitmapByDrawMap(fp, L_BackGroundPic, extDataAddr, pxpyBuff);
            p.x_bk = pxpyBuff[0];
            p.y_bk = pxpyBuff[1];

			/*Log.i(TAG, "[asmReadDataType3] " + "x_bk == " + p.x_bk + ", y_bk == " + p.y_bk);
			if(p.hdcBk != null)
			{
				Log.i(TAG, "[asmReadDataType3] " + "w_bk == " + p.hdcBk.getWidth() +
					", h_bk == " + p.hdcBk.getHeight());
			}*/

            // 读取操作地址
            buff = new byte[2];
            fp.seek(L_YuWen_MiYuCiMi);
            fp.read(buff, 0, 2);
            p.item_num = asmBufferToNum(buff, 0, 2);

            //Log.i(TAG, "[asmReadDataType3] " + "item_num == " + p.item_num);

            if (p.item_num > AsmConstant.DataType3_ITEM_NUM_MAX) {
                p.item_num = AsmConstant.DataType3_ITEM_NUM_MAX;
            }
            asmReadIntArray(fp, L_YuWen_MiYuCiMi + 2, p.ItemFunc, p.item_num);// 2为item_num的宏大小

			/*for(int i = 0; i < p.item_num; i++)
			{
				Log.i(TAG, "[asmReadDataType3] " + "ItemFunc[" + i + "] == " + p.ItemFunc[i]);
			}*/

            // 读取题目/答案显示区域
            buff = new byte[8];
            fp.seek(L_DisplayAreaQuestion);
            fp.read(buff, 0, 8);
            off = 0;
            p.rect.left = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.top = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.right = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.bottom = asmBufferToNum(buff, off, 2);

			/*Log.i(TAG, "[asmReadDataType3] " +
					"rect.left == " + p.rect.left + ", rect.top == " + p.rect.top +
					", rect.right == " + p.rect.right + ", rect.bottom == " + p.rect.bottom);*/

            // 读取按钮图片
            int[] pic_addr = new int[AsmConstant.DataType3_BUTTON_NUM];        // 图片地址
            asmReadIntArray(fp, L_ExtItemAnswerQuestion, pic_addr, AsmConstant.DataType3_BUTTON_NUM);

			/*for(int i = 0; i < AsmConstant.DataType3_BUTTON_NUM; i++)
			{
				Log.i(TAG, "[asmReadDataType3] " + "pic_addr[" + i + "] == " + pic_addr[i]);
			}*/

            int pic_num = 0;        // 图片数量
            for (int i = 0; i < AsmConstant.DataType3_BUTTON_NUM; i++) {
                if (i <= 1) {
                    pic_num = 4;
                } else {
                    pic_num = 3;
                }

                buff = new byte[8 + 8 * pic_num];
                fp.seek(pic_addr[i]);
                fp.read(buff, 0, 8 + 8 * pic_num);

                // rect
                off = 0;
                p.btn[i].rect.left = asmBufferToNum(buff, off, 2);
                off += 2;
                p.btn[i].rect.top = asmBufferToNum(buff, off, 2);
                off += 2;
                p.btn[i].rect.right = asmBufferToNum(buff, off, 2);
                off += 2;
                p.btn[i].rect.bottom = asmBufferToNum(buff, off, 2);
                off += 2;

				/*Log.i(TAG, "[asmReadDataType3] " +
						"btn[" + i + "].rect.left == " + p.btn[i].rect.left +
						", btn[" + i + "].rect.top == " + p.btn[i].rect.top +
						", btn[" + i + "].rect.right == " + p.btn[i].rect.right +
						", btn[" + i + "].rect.bottom == " + p.btn[i].rect.bottom);*/

                // hdc
                for (int j = 0; j < pic_num; j++) {
                    int addr = pic_addr[i] + off + 8 * j;
                    p.btn[i].hdc[j] = asmCreateBitmapByDrawMap(fp, addr, extDataAddr, pxpyBuff);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmReadDataType3] Error!!!");

            ret = false;
        }

        Log.i(TAG, "[asmReadDataType3] End!!!");

        return ret;
    }

    /**
     * 读取DataType3的题目/答案。
     *
     * @param fp     文件句柄
     * @param offset 地址偏移量
     * @param p      DataType3
     * @return 成功返回true，失败返回false
     */
    boolean asmReadDataType3Content(RandomAccessFile fp, int offset, AsmDataType3 p) {
        Log.i(TAG, "[asmReadDataType3Content] Start!!!");

        boolean ret = true;

        try {
            byte[] buff = new byte[8];
            fp.seek(offset);
            fp.read(buff, 0, 8);

            int off = 0;
            int L_Question = asmBufferToNum(buff, off, 4);                    // 题目地址
            off += 4;
            int L_Answer = asmBufferToNum(buff, off, 4);                    // 答案地址

            //Log.i(TAG, "[asmReadDataType3Content] L_Question == " + L_Question +
            //		", L_Answer == " + L_Answer);

            // 读取题目
            fp.seek(L_Question);
            fp.read(p.text_question, 0, AsmConstant.DataType3_TEXT_NUM_MAX);

            // 读取答案
            fp.seek(L_Answer);
            fp.read(p.text_answer, 0, AsmConstant.DataType3_TEXT_NUM_MAX);
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmReadDataType3Content] Error!!!");

            ret = false;
        }

        Log.i(TAG, "[asmReadDataType3Content] End!!!");

        return ret;
    }

    /**
     * 读取DataType4（百家姓）。
     *
     * @param fp          文件句柄
     * @param offset      地址偏移量
     * @param p           DataType4
     * @param extDataAddr 外部数据地址
     * @return 成功返回true，失败返回false
     */
    private boolean asmReadDataType4(RandomAccessFile fp, int offset, AsmDataType4 p, int extDataAddr) {
        Log.i(TAG, "[asmReadDataType4] Start!!!");

        boolean ret = true;

        try {
            byte[] buff = new byte[64];
            fp.seek(offset);
            fp.read(buff, 0, 64);

            int off = 0;
            p.is_exit_father = asmBufferToNum(buff, off, 2);            // 父菜单项目，0xFF表示没有
            off += 2;
            p.father_addr = asmBufferToNum(buff, off, 4);                // 父菜单地址，0x00表示没有
            off += 4;
            p.L_BackGroundPic = asmBufferToNum(buff, off, 4);            // 背景图片地址
            off += 4;
            p.L_DisplayArea = asmBufferToNum(buff, off, 4);                // 文本显示区域地址
            off += 4;
            p.L_SentenceSectionBJXTable = asmBufferToNum(buff, off, 4);    // 列表文本地址

			/*Log.i(TAG, "[asmReadDataType4] " +
					"is_exit_father == " + p.is_exit_father +
					", father_addr == " + p.father_addr +
					", L_BackGroundPic == " + p.L_BackGroundPic +
					", L_DisplayArea == " + p.L_DisplayArea +
					", L_SentenceSectionBJXTable == " + p.L_SentenceSectionBJXTable);*/

            // 读取背景图片
            asmRecycleBitmap(p.hdcBk);

            int[] pxpyBuff = new int[2];
            p.hdcBk = asmCreateBitmapByDrawMap(fp, p.L_BackGroundPic, extDataAddr, pxpyBuff);
            p.x_bk = pxpyBuff[0];
            p.y_bk = pxpyBuff[1];

			/*Log.i(TAG, "[asmReadDataType34] " + "x_bk == " + p.x_bk + ", y_bk == " + p.y_bk);
			if(p.hdcBk != null)
			{
				Log.i(TAG, "[asmReadDataType3] " + "w_bk == " + p.hdcBk.getWidth() +
					", h_bk == " + p.hdcBk.getHeight());
			}*/

            // 读取文本显示区域
            buff = new byte[8];
            fp.seek(p.L_DisplayArea);
            fp.read(buff, 0, 8);

            off = 0;
            p.rect.left = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.top = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.right = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.bottom = asmBufferToNum(buff, off, 2);

			/*Log.i(TAG, "[asmReadDataType4] " +
					"rect.left == " + p.rect.left + ", rect.top == " + p.rect.top +
					", rect.right == " + p.rect.right + ", rect.bottom == " + p.rect.bottom);*/

            // 读取列表文本
            buff = new byte[AsmConstant.DataType4_ITEM_NUM_MAX * 7];
            fp.seek(p.L_SentenceSectionBJXTable);
            fp.read(buff, 0, AsmConstant.DataType4_ITEM_NUM_MAX * 7);

            off = 0;
            int checkChar, temp;
            List<TxtBean> list = new ArrayList<>();
            for (int i = 0; i < AsmConstant.DataType4_ITEM_NUM_MAX; i++) {
                checkChar = asmBufferToNum(buff, off, 1);

                if (checkChar != AsmConstant.Macro_PlayAviEndBoth) {
                    p.total_item = i;
                    break;
                }

                temp = asmBufferToNum(buff, off + 1, 3);        // SphAddr，1为checkChar的宏大小
                p.Itemspeech[i] = temp;
                Log.i(TAG, "asmReadDataType4: " + p.Itemspeech[i]);
                temp = asmBufferToNum(buff, off + 1 + 3, 3);    // AniAddr，3为SphAddr的宏大小

                fp.seek(temp + 1);
                fp.read(p.ItemText[i], 0, AsmConstant.DataType4_ITEM_TEXT_MAX);
                Log.i(TAG, "asmReadDataType4: " + asmGetString(p.ItemText[i], 0, AsmConstant.ASM_ASCII_CODE));

                off += 7;                                        // 7为PlayAviEndBoth宏的大小
            }
//            TxtEntry db = new TxtEntry();
//            db.setOffset(100);
//            db.setMDatas(new Gson().toJson(list));
//            Log.i("klivitam", "asmReadDataType4: "+new Gson().toJson(list));
//            AsmApplication.getInstances().getDaoSession().getTxtEntryDao().insert(db);

			/*Log.i(TAG, "[asmReadDataType4] total_item == " + p.total_item);
			for(int i = 0; i < p.total_item; i++)
			{
				Log.i(TAG, "[asmReadDataType4] Itemspeech[" + i + "] == " + p.Itemspeech[i]);
			}*/
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmReadDataType4] Error!!!");

            ret = false;
        }

        Log.i(TAG, "[asmReadDataType4] End!!!");

        return ret;
    }

    /**
     * 读取DataType5（三字经、弟子规、千字文）。
     *
     * @param fp          文件句柄
     * @param offset      地址偏移量
     * @param p           DataType5
     * @param extDataAddr 外部数据地址
     * @return 成功返回true，失败返回false
     */
    private boolean asmReadDataType5(RandomAccessFile fp, int offset, AsmDataType5 p, int extDataAddr) {
        Log.i(TAG, "[asmReadDataType5] Start!!!");

        boolean ret = true;

        try {
            byte[] buff = new byte[64];
            fp.seek(offset);
            fp.read(buff, 0, 64);

            int off = 0;
            p.is_exit_father = asmBufferToNum(buff, off, 2);            // 父菜单项目，0xFF表示没有
            off += 2;
            p.father_addr = asmBufferToNum(buff, off, 4);                // 父菜单地址，0x00表示没有
            off += 4;
            p.L_BackGroundPic = asmBufferToNum(buff, off, 4);            // 背景图片地址
            off += 4;
            p.L_DisplayArea = asmBufferToNum(buff, off, 4);                // 文本显示区域地址
            off += 4;
            p.L_SentenceSectionBJXTable = asmBufferToNum(buff, off, 4);    // 列表文本地址

			/*Log.i(TAG, "[asmReadDataType5] " +
					"is_exit_father == " + p.is_exit_father +
					", father_addr == " + p.father_addr +
					", L_BackGroundPic == " + p.L_BackGroundPic +
					", L_DisplayArea == " + p.L_DisplayArea +
					", L_SentenceSectionBJXTable == " + p.L_SentenceSectionBJXTable);*/

            // 读取背景图片
            asmRecycleBitmap(p.hdcBk);

            int[] pxpyBuff = new int[2];
            p.hdcBk = asmCreateBitmapByDrawMap(fp, p.L_BackGroundPic, extDataAddr, pxpyBuff);
            p.x_bk = pxpyBuff[0];
            p.y_bk = pxpyBuff[1];

			/*Log.i(TAG, "[asmReadDataType5] " + "x_bk == " + p.x_bk + ", y_bk == " + p.y_bk);
			if(p.hdcBk != null)
			{
				Log.i(TAG, "[asmReadDataType5] " + "w_bk == " + p.hdcBk.getWidth() +
					", h_bk == " + p.hdcBk.getHeight());
			}*/

            // 读取文本显示区域
            buff = new byte[8];
            fp.seek(p.L_DisplayArea);
            fp.read(buff, 0, 8);

            off = 0;
            p.rect.left = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.top = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.right = asmBufferToNum(buff, off, 2);
            off += 2;
            p.rect.bottom = asmBufferToNum(buff, off, 2);

			/*Log.i(TAG, "[asmReadDataType5] " +
					"rect.left == " + p.rect.left + ", rect.top == " + p.rect.top +
					", rect.right == " + p.rect.right + ", rect.bottom == " + p.rect.bottom);*/

            // 读取列表文本
            buff = new byte[AsmConstant.DataType4_ITEM_NUM_MAX * 7];
            fp.seek(p.L_SentenceSectionBJXTable);
            fp.read(buff, 0, AsmConstant.DataType4_ITEM_NUM_MAX * 7);

            off = 0;
            int checkChar, temp;
            List<TxtBean> list = new ArrayList<>();
            String s;
            for (int i = 0; i < AsmConstant.DataType4_ITEM_NUM_MAX; i++) {
                checkChar = asmBufferToNum(buff, off, 1);

                if (checkChar != AsmConstant.Macro_PlayAviEndBoth) {
                    p.total_item = i;
                    break;
                }

                temp = asmBufferToNum(buff, off + 1, 3);        // SphAddr，1为checkChar的宏大小
                p.Itemspeech[i] = temp;
                temp = asmBufferToNum(buff, off + 1 + 3, 3);    // AniAddr，3为SphAddr的宏大小

                fp.seek(temp);
                fp.read(p.ItemText[i], 0, AsmConstant.DataType4_TEXT_MAX);
                String translate;
                if (i == 0) {
                    translate = "标题";
                } else {
                    translate = asmGetString(p.ItemText[i], 43, AsmConstant.ASM_ASCII_CODE);
                }
                String poem1 = asmGetString(p.ItemText[i], 1, AsmConstant.ASM_ASCII_CODE);
                String poem2 = asmGetString(p.ItemText[i], 22, AsmConstant.ASM_ASCII_CODE);
                String poem = poem1 + "\n" + poem2;
                Log.i(TAG, "asmReadDataType5: ===" + poem);
                Log.i(TAG, "asmReadDataType4: ====4" + translate);
                TxtBean bean = new TxtBean(poem, String.valueOf(temp), translate);
                list.add(bean);
                off += 7;                                        // 7为PlayAviEndBoth宏的大小
            }
//            TxtEntry db = new TxtEntry();
//            db.setOffset(new Random().nextInt(1000));
//            db.setMDatas(new Gson().toJson(list));
            Log.i("klivitam", "asmReadDataType4: " + new Gson().toJson(list));
//            AsmApplication.getInstances().getDaoSession().getTxtEntryDao().insert(db);

			/*Log.i(TAG, "[asmReadDataType5] total_item == " + p.total_item);
			for(int i = 0; i < p.total_item; i++)
			{
				Log.i(TAG, "[asmReadDataType5] Itemspeech[" + i + "] == " + p.Itemspeech[i]);
			}*/
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmReadDataType5] Error!!!");

            ret = false;
        }

        Log.i(TAG, "[asmReadDataType5] End!!!");

        return ret;
    }

    /**
     * 释放SMenuType1的资源。
     *
     * @param p SMenuType1
     */
    private void asmFreeMenuType1(AsmMenuType1 p) {
        if (p != null) {
            asmRecycleBitmap(p.hdcBk);

            for (int i = 0; i < p.total_item; i++) {
                asmRecycleBitmap(p.first_pic[i]);
                asmRecycleBitmap(p.second_pic[i]);
                asmRecycleBitmap(p.last_pic[i]);
            }

            p = null;
        }
    }

    /**
     * 释放SMenuType2的资源。
     *
     * @param p SMenuType2
     */
    private void asmFreeMenuType2(AsmMenuType2 p) {
        if (p != null) {
            asmRecycleBitmap(p.hdcBk);

            p = null;
        }
    }

    /**
     * 释放SMenuType3的资源。
     *
     * @param p SMenuType3
     */
    private void asmFreeMenuType3(AsmMenuType3 p) {
        if (p != null) {
            asmRecycleBitmap(p.hdcBk);

            for (int i = 0; i < p.total_item; i++) {
                asmRecycleBitmap(p.first_pic[i]);
                asmRecycleBitmap(p.second_pic[i]);
                asmRecycleBitmap(p.last_pic[i]);
            }

            p = null;
        }
    }

    /**
     * 释放SDataType1的资源。
     *
     * @param p SDataType1
     */
    private void asmFreeDataType1(AsmDataType1 p) {
        if (p != null) {
            asmRecycleBitmap(p.hdcBk);

            p = null;
        }
    }

    /**
     * 释放SDataType3的资源。
     *
     * @param p SDataType3
     */
    private void asmFreeDataType3(AsmDataType3 p) {
        if (p != null) {
            asmRecycleBitmap(p.hdcBk);

            for (int i = 0; i < AsmConstant.DataType3_BUTTON_NUM; i++) {
                for (int j = 0; j < AsmConstant.DataType3_BUTTON_NUM; j++) {
                    asmRecycleBitmap(p.btn[i].hdc[j]);
                }
            }

            p = null;
        }
    }

    /**
     * 释放SDataType4的资源。
     *
     * @param p SDataType4
     */
    private void asmFreeDataType4(AsmDataType4 p) {
        if (p != null) {
            asmRecycleBitmap(p.hdcBk);

            p = null;
        }
    }

    /**
     * 释放SDataType5的资源。
     *
     * @param p SDataType5
     */
    private void asmFreeDataType5(AsmDataType5 p) {
        if (p != null) {
            asmRecycleBitmap(p.hdcBk);

            p = null;
        }
    }

    /**
     * 获取菜单或数据类型。
     *
     * @param offset 偏移量
     * @return 菜单或数据类型，最高位区分菜单和数据,1-数据,0-菜单
     */
    int asmDataReadNode(int offset) {
        Log.i(TAG, "[asmDataReadNode] Start!!!");

        byte[] buff = new byte[4];

        try {
            fp.seek(offset);
            fp.read(buff, 0, 4);
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmDataReadNode] Error!!!");
        }

        // 菜单还是数据
        int off = 0;
        int nodeStruct = asmBufferToNum(buff, off, 1);
//        int nodeStruct = 240;

        // 菜单或者数据类型
        off += 1;
        int nodeType = asmBufferToNum(buff, off, 1);
//        int nodeType = 2;

        Log.i("jkb", "[asmDataReadNode] " + "NodeStruct == " + nodeStruct + ", NodeType == " + nodeType);

        data_type = -1;

        if (nodeStruct == AsmConstant.MenuStruct)    // 菜单
        {
            if (nodeType == AsmConstant.MenuType1)            // 图片菜单
            {
                asmFreeMenuType1(pMenuType1);

                pMenuType1 = new AsmMenuType1();

                // 读取MemuType1。（2为nodeStruct + nodeType的宏大小）
                asmReadMemuType1(fp, offset + 2, pMenuType1, ExternalDataAddr);
            } else if (nodeType == AsmConstant.MenuType2)        // 文字菜单
            {
                asmFreeMenuType2(pMenuType2);

                pMenuType2 = new AsmMenuType2();

                pMenuType2.self_addr = offset;

                // 读取MemuType2。（2为nodeStruct + nodeType的宏大小）
                asmReadMemuType2(fp, offset + 2, pMenuType2, ExternalDataAddr);
            } else if (nodeType == AsmConstant.MenuType3)        // 按钮菜单
            {
                asmFreeMenuType3(pMenuType3);

                pMenuType3 = new AsmMenuType3();

                // 读取MemuType3。（2为nodeStruct + nodeType的宏大小）
                asmReadMemuType3(fp, offset + 2, pMenuType3, ExternalDataAddr);
            }
        } else if (nodeStruct == AsmConstant.DataStruct)// 数据
        {
            if (nodeType == AsmConstant.DataType1)            // 纯文本
            {
                asmFreeDataType1(pDataType1);

                pDataType1 = new AsmDataType1();

                // 读取DataType1。（2为nodeStruct + nodeType的宏大小）
                asmReadDataType1(fp, offset + 2, pDataType1, ExternalDataAddr);
            } else if (nodeType == AsmConstant.DataType3)        // 题目 + 答案
            {
                asmFreeDataType3(pDataType3);

                pDataType3 = new AsmDataType3();

                // 读取DataType3。（2为nodeStruct + nodeType的宏大小）
                asmReadDataType3(fp, offset + 2, pDataType3, ExternalDataAddr);

                // 读取DataType3第一题的题目/答案
                asmReadDataType3Content(fp, pDataType3.ItemFunc[0], pDataType3);
            } else if (nodeType == AsmConstant.DataType4)        // 百家姓
            {
                asmFreeDataType4(pDataType4);

                pDataType4 = new AsmDataType4();

                Log.i(TAG, "asmDataReadNode: " + offset + "," + ExternalDataAddr);

                // 读取DataType4。（2为nodeStruct + nodeType的宏大小）
                asmReadDataType4(fp, offset + 2, pDataType4, ExternalDataAddr);
            } else if (nodeType == AsmConstant.DataType5)        // 三字经、弟子规、千字文
            {
                asmFreeDataType5(pDataType5);

                pDataType5 = new AsmDataType5();
                Log.i(TAG, "asmDataReadNode: " + offset + "," + ExternalDataAddr);

                // 读取DataType5。（2为nodeStruct + nodeType的宏大小）
                asmReadDataType5(fp, offset + 2, pDataType5, ExternalDataAddr);
            }

            data_type = nodeType;

            nodeType |= 0x40000000;            // 区分菜单和数据类型
        }

        Log.i(TAG, "[asmDataReadNode] End!!!");

        return nodeType;
    }

    /**
     * ASM数据读取初始化。
     *
     * @param filename 文件名
     * @return true-成功；false-失败
     */
    public boolean asmDataReadInit(String filename) {
        Log.i(TAG, "[asmDataReadInit] Start!!!");

        boolean ret = true;

        try {
            fp = new RandomAccessFile(filename, "r");

            fp.seek(0);

            byte[] buff = new byte[8];
            fp.read(buff, 0, 8);

            // 文件头校验
            String head = new String(buff);
            if (!head.equals(AsmConstant.AsmDataHeadInfo)) {
                fp.close();

                Log.e(TAG, "[asmDataReadInit] Data Error!!!");

                return false;
            }

            fp.seek(0x80);

            buff = new byte[12];
            fp.read(buff, 0, 12);

            // ExtCard_Menu_Table__xx
            int off = 0;
            int menuAddr = asmBufferToNum(buff, off, 4);
            Log.i("jkb", "asmDataReadInit: " + menuAddr);

            // ReadBook_External_AllData
            off += 4;
            ExternalDataAddr = asmBufferToNum(buff, off, 4);

            // GlobalBackGround
            off += 4;
            int bk_addr = asmBufferToNum(buff, off, 4);

            // 读取背景图片
            int[] pxpyBuff = new int[2];
            hdcBk = asmCreateBitmapByDrawMap(fp, bk_addr, ExternalDataAddr, pxpyBuff);
            x_bk = pxpyBuff[0];
            y_bk = pxpyBuff[1];

			/*Log.i(TAG, "[asmDataReadInit] X == " + p.x_bk + ", Y == " + p.y_bk +
					", Width == " + p.hdcBk.getWidth() + ", Height == " + p.hdcBk.getHeight());*/

            // 获取菜单或数据类型。
            asmDataReadNode(menuAddr);

            // 在asmReadExit中统一关闭
            //fp.close();
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "[asmDataReadInit] Data Error!!!");

            ret = false;
        }

        Log.i(TAG, "[asmDataReadInit] End!!!");

        return ret;
    }

    /**
     * ASM结束读取数据。
     */
    void asmDataReadExit() {
        Log.i(TAG, "[asmDataReadExit] Start!!!");

        try {
            if (fp != null) {
                fp.close();
                fp = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        asmRecycleBitmap(hdcBk);

        asmFreeMenuType1(pMenuType1);
        asmFreeMenuType2(pMenuType2);
        asmFreeMenuType3(pMenuType3);

        asmFreeDataType1(pDataType1);
        asmFreeDataType3(pDataType3);
        asmFreeDataType4(pDataType4);
        asmFreeDataType5(pDataType5);

        if (pBuff != null) {
            pBuff = null;
        }

        if (cBuff != null) {
            cBuff = null;
        }

        Log.i(TAG, "[asmDataReadExit] End!!!");
    }
}