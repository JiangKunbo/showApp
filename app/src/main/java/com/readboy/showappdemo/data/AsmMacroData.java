package com.readboy.showappdemo.data;

import java.io.IOException;
import java.io.RandomAccessFile;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Rect;


import com.readboy.showappdemo.util.Log;
import com.readboy.showappdemo.view.AsmAnimView;
import com.readboy.sound.Sound;

/**
 * ASM相关的宏定义。
 * 
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01在Android平台上重写了此代码
 */
public class AsmMacroData extends AsmBase
{
	//private final String TAG = getClass().getSimpleName();			// TAG
	private final String TAG = "AsmMacroData";							// TAG
	
	AsmPlayAviData mPlayAviData;										// mPlayAviData
	
	private byte[] pBuff;												// 图片数据缓存
	private int[] cBuff;												// 图片颜色缓存
	
	/**
	 * 构造函数。
	 */
	public AsmMacroData()
	{
		mPlayAviData = new AsmPlayAviData();
		
		pBuff = new byte[AsmConstant.PANEL_WIDTH * AsmConstant.PANEL_HEIGHT * 2];
		cBuff = new int[AsmConstant.PANEL_WIDTH * AsmConstant.PANEL_HEIGHT];
	}
	
	/**
	 * 表示PlayAvi相关信息的类。
	 */
	class AsmPlayAviData
	{
		String FileName;							// 当前要播放的文件名
		
		RandomAccessFile CurrentSpeechFp;			// 当前播放语音需要的文件句柄
		RandomAccessFile CurrentAnimationFp;		// 当前播放图形需要的文件句柄
		
		int AviType;								// Avi的类型，即Macro_PlayAviEndByAni，Macro_PlayAviEndBySph，
													// Macro_PlayAviEndBoth，Macro_PlayAviEndEither
		int MuteType;								// Mute的类型
		
		int MuteAviTime;							// Avi暂停时间
		int MuteAnimationTime;						// Animation暂停时间
		int MuteSpeechTime;							// Speech暂停时间
		int MuteBiHuaTime;							// BiHua暂停时间
		
		boolean SpeechEnd;							// Speech是否播放结束
		boolean AnimationEnd;						// Animation是否播放结束
		boolean AviEnd;								// Avi是否播放结束
		
		int LoopType;								// Loop的类型
		int LoopTimes;								// Loop的时间间隔
		
		int FirstMapAddr;							// 第一张Map的地址
		int NextMapAddr;							// 下一张Map的地址
		
		int CurrentAviAddr;							// 当前播放的Avi地址
		int NextAviAddr;							// 下一个将要播放的Avi地址
		
		int CurrentSpeechAddr;						// 当前播放的语音地址（永远存放当前语音的起始地址，用于复读功能之用）
		int NextSpeechAddr;							// 下一个将要播放的语音地址
		
		int AllDataAddr;							// 外部数据（语音图片等）地址
		
		byte[] NextAddrBuff;						// 地址Buffer
		int BuffPointer;							// 地址Buffer的位置
		
		AsmDrawBiHuaData BiHuaData;					// SDrawBiHuaData
		int ClearBiHuaFlag;							// 清笔画的标志
		
		Sound mSound;								// Sound
		
		AsmAnimView mAnimView;						// AsmAnimView
		
		int is_flush_lcd;							// 是否刷新屏幕
		
		public AsmPlayAviData()
		{
			NextAddrBuff = new byte[AsmConstant.Macro_NextAddrBuffSize];
			
			BiHuaData = new AsmDrawBiHuaData();
		}
	}
	
	/**
	 * 表示DrawBiHua相关信息的类。
	 */
	class AsmDrawBiHuaData
	{
		short num;									// 总笔画数
		
		RandomAccessFile fp;						// 文件句柄
		
		int x;										// 演示位置X坐标
		int y;										// 演示位置Y坐标
		
		short currentPointNum;						// 当前笔画总点数
		short currentCount;							// 当前笔画点记数
		
		//int[] pointBuff;							// 点信息
		
		//String speechName;						// 语音文件名
		
		short[] readBuff;							// 将要读的点数
		
		int offset;									// 地址偏移量
		
		int nextStrokeDelay;						// 下一笔等待时间
		
		public AsmDrawBiHuaData()
		{
			//pointBuff = new int[10];
			
			readBuff = new short[AsmConstant.Macro_BiHuaBuffSize];
		}
	}
	
	/**
	 * 读取N字节数据。
	 * 
	 * @param fp						要读取的文件句柄
	 * @param buff						保存读取结果的Buff
	 * @param n							要读取的字节数
	 * @return							实际读取的字节数，-1表示出错
	 */
	private int asmCardRead(RandomAccessFile fp, byte[] buff, int n)
	{
		int ret = 0;
		
		try
		{
			ret = fp.read(buff, 0, n);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * 读取三个字节并转换成数字。
	 * 
	 * @param fp						要读取的文件句柄
	 * @return							获得的数值
	 */
	private int asmMacroRead3ByteToNum(RandomAccessFile fp)
	{
		int num = asmReadNBytesToNum(fp, 0, 3);
		
		if(num > 0x800000)
		{
			num -= 0x800000;
		}
		
		return num;
	}
	
	/**
	 * 读取地址数据到缓存。
	 * 
	 * @param dataAddr					地址
	 */
	private void asmMacroReadAddrDataToBuff(int dataAddr)
	{
		AsmPlayAviData pAvi = mPlayAviData;
		
		RandomAccessFile fp = pAvi.CurrentAnimationFp;
		
		pAvi.BuffPointer = 0;
		
		try
		{
			fp.seek(dataAddr);
			fp.read(pAvi.NextAddrBuff, 0, AsmConstant.Macro_NextAddrBuffSize);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 从Buff获取N Byte数据。
	 * 
	 * @param dataAddr					数据地址
	 * @param addr						地址
	 * @param num						数量
	 * @return							N Byte数据
	 */
	private int asmMacroGetNByteFromAddrBuff(int[] dataAddr, int addr, int num)
	{
		AsmPlayAviData pAvi = mPlayAviData;
		
		if(pAvi.BuffPointer > AsmConstant.Macro_NextAddrBuffSize - num)
		{
			dataAddr[0] += pAvi.BuffPointer;
			asmMacroReadAddrDataToBuff(dataAddr[0]);
			addr = dataAddr[0] & 0xFFFFFFFF;
		}
		
		int ret = asmBufferToNum(pAvi.NextAddrBuff, pAvi.BuffPointer, 3);
		
		pAvi.BuffPointer += num;
		
		return ret;
	}
	
	/**
	 * 填充颜色。
	 * 
	 * @param bitmap					Bitmap
	 * @param x							X坐标
	 * @param y							Y坐标
	 * @param color						颜色值
	 */
	private void asmSetPixel(Bitmap bitmap, int x, int y, int color)
	{
		//Log.i(TAG, "[asmDrvPutPixel] Start!!!");
		
		if(bitmap == null || x < 0 || x > bitmap.getWidth() || y < 0 || y > bitmap.getHeight())
		{
			Log.e(TAG, "[asmSetPixel] Point Error!!!");
			
			return;
		}
		
		bitmap.setPixel(x, y, color);
		
		//Log.i(TAG, "[asmDrvPutPixel] End!!!");
	}

	/**
	 * 停止播放声音。
	 * 
	 * @param sound						Sound
	 */
	private void asmStopSpeech(Sound sound)
	{
		if(sound != null && sound.isPlaying())
		{
			sound.stop();
		}
	}
	
	/**
	 * 是否正在播放声音。
	 * 
	 * @param sound						Sound
	 * @return							正在播放返回true，否则返回false
	 */
	private boolean asmGetSpeechStatus(Sound sound)
	{
		return sound.isPlaying();
	}

	/**
	 * 根据Index来播放声音。
	 * 
	 * @param id						Index
	 * @return							成功返回0，失败返回1
	 */
	private int asmPlaySpeechByIndex(int id)
	{
		//Log.i(TAG, "[asmPlaySpeechByIndex] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		
		int ret = 0;
		
		try
		{
			//Log.i(TAG, "[asmPlaySpeechByIndex] Index == " + id);
			
			pAvi.CurrentSpeechFp.seek(pAvi.AllDataAddr + 4 * id);
			
			int addr1 = asmReadNBytesToNum(pAvi.CurrentSpeechFp, 0, 4);
			int addr2 = asmReadNBytesToNum(pAvi.CurrentSpeechFp, 0, 4);
			int offset = pAvi.AllDataAddr + addr1;
			int length = addr2 - addr1;
			
			//Log.i(TAG, "offset == " + offset + ", length == " + length);
			
			pAvi.CurrentSpeechFp.seek(0);
			//p.mMediaPlayer.reset();
			pAvi.mSound.setDataSource(pAvi.CurrentSpeechFp.getFD(), offset, length);
			//p.mMediaPlayer.prepare();
			pAvi.mSound.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
			Log.e(TAG, "[asmPlaySpeechByIndex] Error!!!");
			
			ret = 1;
		}
		
		//Log.i(TAG, "[asmPlaySpeechByIndex] End!!!");
		
		return ret;
	}
	
	/**
	 * 根据Index来刷图。
	 * 
	 * @param index						Index
	 * @param x							X坐标
	 * @param y							Y坐标
	 */
	private void asmDrawMapByIndex(int index, int x, int y)
	{
		RandomAccessFile fp = mPlayAviData.CurrentAnimationFp;
		int extAddr = mPlayAviData.AllDataAddr;
		
		byte[] buff = new byte[8];
		
		try
		{
			fp.seek(extAddr + 4 * index);
			fp.read(buff, 0, 8);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
			return;
		}
		
		int addr1 = asmBufferToNum(buff, 0, 4);
		int addr2 = asmBufferToNum(buff, 4, 4);
		
		int size = addr2 - addr1;;
		
		int addr = extAddr + addr1;
		
		asmMarcoDrawImage(addr, x, y, size);
	}
	
	/**
	 * 刷图。
	 * 
	 * @param addr						图片地址
	 * @param x0						Ｘ坐标
	 * @param y0						Ｙ坐标
	 * @param size						图片数据大小
	 * @return							出错返回0，否则返回图片的宽度
	 */
	private int asmMarcoDrawImage(int addr, int x0, int y0, int size)
	{
		//Log.i(TAG, "[asmMarcoDrawImage] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		RandomAccessFile fp = mPlayAviData.CurrentAnimationFp;
		
		int ret = 0;
		
		int width = 0;												// 图片宽度
		int height = 0;												// 图片高度
		//byte[] pBuff = null;										// 图片数据
		
		try
		{
			//Log.i(TAG, "[asmMarcoDrawImage] ReadFile Start!!!");
			fp.seek(addr);
			
			byte[] pHead = new byte[100];
			fp.read(pHead, 0, 100);
			
			boolean isPng = asmPngFileFormat(pHead);				// 是否为Png图片文件
			
			if(isPng)			// Png图片
			{
				//Log.i(TAG, "[asmMarcoDrawImage] Is Png File!!!");
				
				fp.seek(addr);
				//pBuff = new byte[size];
				fp.read(pBuff, 0, size);
				
				//Log.i(TAG, "[asmMarcoDrawImage] Size == " + size);
				
				//Log.i(TAG, "[asmMarcoDrawImage] DecodeBitmap Start!!!");
				Options opts = new Options();
				opts.inPreferredConfig = Config.ARGB_8888;
				opts.inPurgeable = true;
				opts.inInputShareable = true;
				Bitmap bitmap = BitmapFactory.decodeByteArray(pBuff, 0, size, opts);
				//Bitmap bitmap = BitmapFactory.decodeByteArray(pBuff, 0, size);
				//pBuff = null;
				//Log.i(TAG, "[asmMarcoDrawImage] DecodeBitmap End!!!");
				
				if(bitmap == null)
				{
					Log.e(TAG, "[asmMarcoDrawImage] Png File Null!!!");
					
					return 0;
				}
				
				width = bitmap.getWidth();
				height = bitmap.getHeight();
				
				//Log.i(TAG, "[asmMarcoDrawImage] Png : " + " X == " + x0 + ", Y == " + y0 + 
				//		", W == " + width + ", H == " + height);
				
				if(width <= 0 || height <= 0 || (x0 + width) > AsmConstant.PANEL_WIDTH || 
						 (y0 + height) > AsmConstant.PANEL_HEIGHT)
				{
					Log.e(TAG, "[asmMarcoDrawImage] Png File Error : " + 
							" X == " + x0 + ", Y == " + y0 + 
							", W == " + width + ", H == " + height + 
							", X+W == " + (x0 + width) + ", Y+H == " + (y0 + height));
					
					return 0;
				}
				
				/*Paint paint = new Paint();
				
				Canvas canva = new Canvas();
				canva.setBitmap(bp);
				canva.drawBitmap(bitmap, x0, y0, paint);
				canva = null;*/
				
				/*Canvas canvas = mPlayAviData.mAnimView.lockCanvas(new Rect(x0, y0, x0 + width, y0 + height));
				//Canvas canvas = mPlayAviData.mSurfaceHolder.lockCanvas();
				if(canvas != null)
				{
					canvas.drawBitmap(bp, 0, 0, paint);
					mPlayAviData.mAnimView.unlockCanvasAndPost(canvas);
				}*/
				
				//mPlayAviData.mSurfaceHolder.lockCanvas(new Rect(0, 0, 0, 0));
				//mPlayAviData.mSurfaceHolder.unlockCanvasAndPost(canvas);
				
				pAvi.mAnimView.doDrawBitmap(bitmap, x0, y0);
				
				asmRecycleBitmap(bitmap);
			}
			else			// Res图片
			{
				//Log.i(TAG, "[asmMarcoDrawImage] Is Res File!!!");
				
				// 格式化Zip文件
				int[] zipInfo = new int[3];
				boolean isZip = asmZipFileFormat(pHead, zipInfo);		// 是否为Zip压缩文件
				
				// 压缩文件
				if(isZip)
				{
					//Log.i(TAG, "[asmMarcoDrawImage] Is Zip File!!!");
					
					int headerlen = zipInfo[0];
					int srcFileSize = zipInfo[1];
					//int dstFileSize = zipInfo[2];
					
					headerlen = headerlen * 2 + 38;						// ？？？
					
					//Log.i(TAG, "[asmMarcoDrawImage]" + " headerLen == " + headerlen + 
					//		", srcFileSize == " + srcFileSize + ", dstFileSize == " + dstFileSize);
					
					// 原始数据
					int srcDataSize = headerlen + srcFileSize;
					byte[] srcData = new byte[srcDataSize];
					
					fp.seek(addr);
					fp.read(srcData, 0, srcDataSize);
					
					// 解压缩Zip数据。
					byte[] dstData = asmZipUncompress(srcData);
					
					//Log.i(TAG, "[asmMarcoDrawImage] srcDataSize == " + srcData.length + 
					//		", dstDataSize == " + dstData.length);
					srcData = null;
					
					width = asmBufferToNum(dstData, 0, 2);
					height = asmBufferToNum(dstData, 2, 2);
					
					//Log.i(TAG, "[asmMarcoDrawImage] Res : " + " X == " + x0 + ", Y == " + y0 +
					//		", W == " + width + ", H == " + height);
					
					if(width <= 0 || height <= 0 || x0 + width > AsmConstant.PANEL_WIDTH || 
							 y0 + height > AsmConstant.PANEL_HEIGHT)
					{
						Log.e(TAG, "[asmMarcoDrawImage] Res File Error : " + 
								" X == " + x0 + ", Y == " + y0 + 
								", W == " + width + ", H == " + height + 
								", X+W == " + (x0 + width) + ", Y+H == " + (y0 + height));
						
						return 0;
					}
					
					//pBuff = new byte[width * height * 2];
					System.arraycopy(dstData, 4, pBuff, 0, width * height * 2);
					dstData = null;
				}
				// 非压缩文件
				else
				{
					//Log.i(TAG, "[asmMarcoDrawImage] Not Zip File!!!");
					
					fp.seek(addr);
					
					byte[] buff = new byte[4];
					fp.read(buff, 0, 4);
					
					width = asmBufferToNum(buff, 0, 2);
					height = asmBufferToNum(buff, 2, 2);
					
					//Log.i(TAG, "[asmMarcoDrawImage] Res : " + " X == " + x0 + ", Y == " + y0 +
					//		", W == " + width + ", H == " + height);
					
					if(width <= 0 || height <= 0 || (x0 + width) > AsmConstant.PANEL_WIDTH || 
							(y0 + height) > AsmConstant.PANEL_HEIGHT)
					{
						Log.e(TAG, "[asmMarcoDrawImage] Res File Error : " + 
								" X == " + x0 + ", Y == " + y0 + 
								", W == " + width + ", H == " + height + 
								", X+W == " + (x0 + width) + ", Y+H == " + (y0 + height));
						
						return 0;
					}
					
					//pBuff = new byte[width * height * 2];
					fp.read(pBuff, 0, width * height * 2);
				}
				//Log.i(TAG, "[asmMarcoDrawImage] ReadFile End!!!");
				
				//Log.i(TAG, "[asmMarcoDrawImage] SetPixels Start!!!");
				/*for(int y = 0; y < height; y++)
				{
					for(int x = 0; x < width; x++)
					{
						bp.setPixel(x0 + x, y0 + y, asmGetColors(pBuff, (x + y * width) * 2));
					}
				}*/
				//Log.i(TAG, "[asmMarcoDrawImage] SetPixels End!!!");
				
				//Log.i(TAG, "[asmMarcoDrawImage] GetColors Start!!!");
				//int[] cBuff = new int[width * height];
				for(int i = 0; i < width * height; i++)
				{
					cBuff[i] = asmGetColors(pBuff, i * 2);
				}
				//pBuff = null;
				//Log.i(TAG, "[asmMarcoDrawImage] GetColors End!!!");
				
				//Log.i(TAG, "[asmMarcoDrawImage] SetPixels Start!!!");
				pAvi.mAnimView.getBitmap().setPixels(cBuff, 0, width, x0, y0, width, height);
				//cBuff = null;
				//Log.i(TAG, "[asmMarcoDrawImage] SetPixels End!!!");
				
				//Log.i(TAG, "[asmMarcoDrawImage] Res : " + " X == " + x0 + ", Y == " + y0 +
				//		", W == " + width + ", H == " + height);
				
				//Canvas canvas = mPlayAviData.mSurfaceHolder.lockCanvas(new Rect(x0, y0, x0 + height, y0 + height));
				/*Canvas canvas = mPlayAviData.mAnimView.lockCanvas();
				if(canvas != null)
				{
					canvas.drawBitmap(bp, 0, 0, new Paint());
					mPlayAviData.mAnimView.unlockCanvasAndPost(canvas);
				}*/
				
				//mPlayAviData.mSurfaceHolder.lockCanvas(new Rect(0, 0, 0, 0));
				//mPlayAviData.mSurfaceHolder.unlockCanvasAndPost(canvas);
				
				//mPlayAviData.mAnimView.invalidate();
				pAvi.mAnimView.invalidate(new Rect(x0, y0, x0 + height, y0 + height));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			Log.e(TAG, "[asmMarcoDrawImage] Error!!!");
		}
		catch(OutOfMemoryError oome)
		{
			oome.printStackTrace();
			
			Log.e(TAG, "[asmMarcoDrawImage] OutOfMemory Error!!!");
		}
		
		ret = width;
		
		//Log.i(TAG, "[asmMarcoDrawImage] End!!!");
		
		return ret;
	}

	/**
	 * 语音和图形播放初始化。
	 */
	void asmAviAndSpeechInitOnce()
	{
		//Log.i(TAG, "[asmAviAndSpeechInit] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		
		pAvi.CurrentSpeechFp = null;		// 当前播放语音需要的文件句柄
		pAvi.CurrentAnimationFp = null;		// 当前播放图形需要的文件句柄
		
		pAvi.AviType = 0;					// Avi的类型
		pAvi.MuteType = 0;					// Mute的类型
		
		pAvi.NextSpeechAddr = 0;			// 下一个将要播放的语音地址
		
		pAvi.ClearBiHuaFlag = 0;			// 清笔画的标志
		
		pAvi.mSound = null;					// Sound
		
		pAvi.mAnimView = null;				// AsmAnimView
		
		//Log.i(TAG, "[asmAviAndSpeechInit] End!!!");
	}
	
	/**
	 * Avi播放初始化。
	 * @param filename					当前要播放的文件名
	 * @param speechFp					当前要播放的声音的文件句柄
	 * @param animFp					当前要播放的动画的文件句柄
	 * @param aviAddr					Avi地址
	 * @param extAddr					外部数据地址
	 * @param sound						Sound
	 * @param animvView					AsmAnimView
	 */
	void asmPlayAviInit(String filename, RandomAccessFile speechFp,
		RandomAccessFile animFp, int aviAddr, int extAddr, Sound sound, AsmAnimView animvView)
	{
		//Log.i(TAG, "[asmPlayAviInit] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		
		pAvi.FileName = filename;
		
		pAvi.CurrentSpeechFp = speechFp;
		pAvi.CurrentAnimationFp = animFp;
		
		pAvi.NextAviAddr = aviAddr;
		pAvi.AllDataAddr = extAddr;
		
		pAvi.mSound = sound;
		
		pAvi.mAnimView = animvView;
		
		pAvi.AviEnd = false;
		
		pAvi.MuteAviTime = 0;
		
		//Log.i(TAG, "[asmPlayAviInit] End!!!");
	}
	
	/**
	 * Avi播放退出。
	 */
	void asmPlayAviExit()
	{
		pBuff = null;
		cBuff = null;
		
		mPlayAviData = null;
	}
	
	/**
	 * 播放Avi。
	 */
	void asmPlayAvi()
	{
		//Log.i(TAG, "[asmPlayAvi] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		
		if(pAvi.AviEnd)
		{
			return;
		}
		
		try
		{
			RandomAccessFile fp = pAvi.CurrentAnimationFp;
			
			pAvi.CurrentAviAddr = pAvi.NextAviAddr;
			
			//Log.i(TAG, "[asmPlayAvi] CurrentAviAddr == " + p.CurrentAviAddr);
			
			byte[] buff = new byte[1];
			fp.seek(pAvi.CurrentAviAddr);
			asmCardRead(fp, buff, 1);
			
			int UValue = 0;
			UValue = buff[0] & 0xFF;
			
			//Log.i(TAG, "[asmPlayAvi] UValue == " + UValue);
			
			if((AsmConstant.Macro_PlayAviEndBoth == UValue) || 		// PlayAviEndBoth
				(AsmConstant.Macro_PlayAviEndBySph == UValue) || 	// PlayAviEndBySph
				(AsmConstant.Macro_PlayAviEndByAni == UValue) ||	// PlayAviEndByAni
				(AsmConstant.Macro_PlayAviEndEither == UValue))		// PlayAviEndEither
			{
				android.util.Log.i(TAG, "asmPlayAvi: 111");
				pAvi.AviType = UValue;
				pAvi.LoopType = AsmConstant.Macro_NoLoop;
				pAvi.MuteType = AsmConstant.Macro_NotAnyMute;
				pAvi.AnimationEnd = false;
				pAvi.SpeechEnd = false;
				
				int LValue = asmMacroRead3ByteToNum(fp);
				pAvi.NextSpeechAddr = LValue;
				LValue = asmMacroRead3ByteToNum(fp);
				pAvi.NextMapAddr = LValue;
				pAvi.NextAviAddr = (int) fp.getFilePointer();
				
				asmPlayAnimationInit();
				asmPlayAnimation();
				
				asmPlaySpeechInit();
				asmPlaySpeech();
			}
			else if(AsmConstant.Macro_Mute == UValue)				// Mute
			{
				android.util.Log.i(TAG, "asmPlayAvi: 222");
				asmCardRead(fp, buff, 1);
				
				UValue = 0;
				UValue = buff[0] & 0xFF;
				
				//Log.i(TAG, "[asmPlayAvi] UValue == " + UValue);
				
				pAvi.MuteType = AsmConstant.Macro_AviMute;
				pAvi.MuteAviTime = UValue;
				pAvi.NextAviAddr = (int) fp.getFilePointer();
			}
			else if(AsmConstant.Macro_End == UValue)				// End
			{
				android.util.Log.i(TAG, "asmPlayAvi: 333");

				pAvi.AviEnd = true;
				
				//Log.i(TAG, "[asmPlayAvi] AviEnd == " + p.AviEnd);
			}
			else
			{
				android.util.Log.i(TAG, "asmPlayAvi: 444");
				Log.e(TAG, "[asmPlayAvi] Macro Error!!! UValue == " + UValue);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
			Log.e(TAG, "[asmPlayAvi] Error!!!");
		}
		
		//Log.i(TAG, "[asmPlayAvi] End!!!");
	}
	
	/**
	 * Avi的播放状态。
	 * 
	 * @return							播放结束返回true；否则返回false
	 */
	boolean asmGetAviStatus()
	{
		return mPlayAviData.AviEnd;
	}

	/**
	 * 声音播放初始化。
	 */
	private void asmPlaySpeechInit()
	{
		//Log.i(TAG, "[asmPlaySpeechInit] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		
		pAvi.CurrentSpeechFp = pAvi.CurrentSpeechFp;
		pAvi.SpeechEnd = false;
		pAvi.MuteType &= ~AsmConstant.Macro_SpeechMute;
		pAvi.MuteSpeechTime = 0;
		pAvi.NextSpeechAddr = pAvi.NextSpeechAddr;
		pAvi.CurrentSpeechAddr = pAvi.NextSpeechAddr;
		
		//Log.i(TAG, "[asmPlaySpeechInit] End!!!");
	}
	
	/**
	 * 播放声音。
	 */
	private void asmPlaySpeech()
	{
		//Log.i(TAG, "[asmPlaySpeech] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		
		if(pAvi.SpeechEnd)
		{
			return;
		}
		
		asmMacroReadAddrDataToBuff(pAvi.NextSpeechAddr);
		
		int[] dataAddr = new int[1];
		int UValue = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextSpeechAddr, 1) & 0xFF;
		
		//Log.i(TAG, "[asmPlaySpeech] UValue == " + UValue);
		
		if(AsmConstant.Macro_End == UValue)			// End
		{
			asmStopSpeech(pAvi.mSound);
			
			pAvi.SpeechEnd = true;
			pAvi.NextSpeechAddr = 0;
		}
		else if(AsmConstant.Macro_Mute == UValue)	// Mute
		{
			UValue = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextSpeechAddr, 1) & 0xFF;
			
			//Log.i(TAG, "[asmPlaySpeech] UValue == " + UValue);
			
			pAvi.MuteType |= AsmConstant.Macro_SpeechMute;
			pAvi.MuteSpeechTime = UValue & 0xFF;
			pAvi.SpeechEnd = false;
			pAvi.NextSpeechAddr += pAvi.BuffPointer;
		}
		else										// Play
		{
			int speechindex = UValue;
			UValue = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextSpeechAddr, 1) & 0xFF;
			
			//Log.i(TAG, "[asmPlaySpeech] UValue == " + UValue);
			
			speechindex += UValue * 200;
			UValue = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextSpeechAddr, 1) & 0xFF;
			
			//Log.i(TAG, "[asmPlaySpeech] UValue == " + UValue);
			
			pAvi.SpeechEnd = false;
			pAvi.NextSpeechAddr += pAvi.BuffPointer;
			
			int ret = asmPlaySpeechByIndex(speechindex);
			if(ret != 0)
			{
				pAvi.SpeechEnd = true;
			}
		}
		
		//Log.i(TAG, "[asmPlaySpeech] End!!!");
	}
	
	/**
	 * Speech的播放状态。
	 * 
	 * @return							播放结束返回true；否则返回false
	 */
	@SuppressWarnings("unused")
	private boolean asmGetSpeechStatus()
	{
		return mPlayAviData.SpeechEnd;
	}
	
	/**
	 * Animation播放初始化。
	 */
	private void asmPlayAnimationInit()
	{
		//Log.i(TAG, "[asmPlayAnimationInit] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		
		pAvi.CurrentAnimationFp = pAvi.CurrentAnimationFp;
		pAvi.AnimationEnd = false;
		pAvi.MuteType &= ~AsmConstant.Macro_AnimationMute;
		pAvi.MuteAnimationTime = 0;
		pAvi.NextMapAddr = pAvi.NextMapAddr;
		pAvi.ClearBiHuaFlag = 0;
		
		//Log.i(TAG, "[asmPlayAnimationInit] End!!!");
	}
	
	/**
	 * 播放Animation。
	 */
	private void asmPlayAnimation()
	{
		//Log.i(TAG, "[asmPlayAnimation] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		
		if(pAvi.AnimationEnd)
		{
			return;
		}
		
		if(pAvi.ClearBiHuaFlag > 0)
		{
			int error = asmDrawBiHuaServiceLoop(pAvi.BiHuaData, pAvi.mAnimView.getBitmap());
			
			if(error > 0)
			{
				pAvi.ClearBiHuaFlag = 0;
			}
			else
			{
				int left = pAvi.BiHuaData.x;
				int top = pAvi.BiHuaData.y;
				
				// DrawBiHua的宽、高分别为234、167
				/*Canvas canvas = p.mAnimView.lockCanvas(new Rect(left, top, left + 234, top + 167));
				//Canvas canvas = p.mSurfaceHolder.lockCanvas();
				if(canvas != null)
				{
					canvas.drawBitmap(p.mBitmap, 0, 0, new Paint());
					p.mAnimView.unlockCanvasAndPost(canvas);
					
					//mPlayAviData.mSurfaceHolder.lockCanvas(new Rect(0, 0, 0, 0));
					//mPlayAviData.mSurfaceHolder.unlockCanvasAndPost(canvas);
				}*/
				
				//p.mAnimView.invalidate();
				pAvi.mAnimView.invalidate(new Rect(left, top, left + 234, top + 167));
			}
			
			return;
		}
		
		asmMacroReadAddrDataToBuff(pAvi.NextMapAddr);
		
		int[] dataAddr = new int[1];
		int UValue = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextMapAddr, 1) & 0xFF;
		
		Log.i(TAG, "[asmPlayAnimation] UValue == " + UValue);
		
		int error = 0;
		
		while(true)
		{
			if(UValue == AsmConstant.Macro_End || error == 1)	// End（图形显示完，若有语音则等语音完播下一个Avi或Mute）
			{
				pAvi.AnimationEnd = true;
				break;
			}
			else if(UValue == AsmConstant.Macro_DrawBiHua)		// DrawBiHua（画笔画有一个较长的的时间）
			{
				asmMacroDrawBiHuaFunction();
				pAvi.NextMapAddr += pAvi.BuffPointer;
				break;
			}
			else if(UValue == AsmConstant.Macro_Mute)			// Mute
			{
				UValue = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextMapAddr, 1) & 0xFF;
				pAvi.MuteAnimationTime = UValue & 0xFF;
				pAvi.MuteType |= AsmConstant.Macro_AnimationMute;
				pAvi.NextMapAddr += pAvi.BuffPointer;
				break;
			}
			else if(UValue == AsmConstant.Macro_SetLoop)		// SetLoop、SetLoopTimes
			{
				UValue = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextMapAddr, 1) & 0xFF;
				pAvi.LoopTimes = UValue & 0xFF;
				if(pAvi.LoopTimes == 1)
				{
					pAvi.LoopType = AsmConstant.Macro_LoopNoEnd;	// 无限循环
				}
				else
				{
					pAvi.LoopType = AsmConstant.Macro_LoopTimes;	// 有限循环
				}
				pAvi.FirstMapAddr = pAvi.NextMapAddr + pAvi.BuffPointer;
			}
			else if(UValue == AsmConstant.Macro_DoLoop)			// DoLoop
			{
				if(pAvi.LoopType == AsmConstant.Macro_LoopTimes)	// 有限循环
				{
					pAvi.LoopTimes--;
					
					if(pAvi.LoopTimes > 0)							// 重新开始循环显示图形
					{
						pAvi.NextMapAddr = pAvi.FirstMapAddr;
						asmMacroReadAddrDataToBuff(pAvi.NextMapAddr);
					}
				}
				else											// 无限循环
				{
					pAvi.NextMapAddr = pAvi.FirstMapAddr;				// 继续循环显示图形
					asmMacroReadAddrDataToBuff(pAvi.NextMapAddr);
				}
			}
			else												// 
			{
				error = asmMacroSomeActionFunction(UValue);// 实现画图，显示字符串，画线等动作
			}
			
			UValue = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextMapAddr, 1) & 0xFF;
			
			//Log.i(TAG, "[asmPlayAnimation] UValue == " + UValue);
		}
		
		//Log.i(TAG, "[asmPlayAnimation] End!!!");
	}
	
	/**
	 * Animation的播放状态。
	 * 
	 * @return							播放结束返回true；否则返回false
	 */
	@SuppressWarnings("unused")
	private boolean asmGetAnimationStatus()
	{
		return mPlayAviData.AnimationEnd;
	}
	
	/**
	 * DrawBiHua初始化。
	 * 
	 * @param fp						文件句柄
	 * @param biHuaAddr					BiHua地址
	 * @param pBiHua					SDrawBiHuaData
	 * @param x							X坐标
	 * @param y							Y坐标
	 * @return							出错返回0，否则返回1
	 */
	private int asmDrawBiHuaInit(RandomAccessFile fp, int biHuaAddr, AsmDrawBiHuaData pBiHua, int x, int y)
	{
		//Log.i(TAG, "[asmDrawBiHuaInit] Start!!!");
		
		byte[] buff = new byte[24];
				
		try
		{
			fp.seek(biHuaAddr);
			fp.read(buff, 0, 24);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		//String head = new String(buff);
		//boolean flag = head.equals(AsmConstant.AsmBiHuaHeadInfo);
		boolean flag = asmStrncmp(buff, AsmConstant.AsmBiHuaHeadInfo, 6);
		if(!flag)
		{
			Log.e(TAG, "[asmDrawBiHuaInit] Data Error!!!");
			
			return 0;
		}
		
		pBiHua.fp = fp;
		pBiHua.num = buff[4];
		pBiHua.nextStrokeDelay = 0;
		pBiHua.currentPointNum = 0;
		pBiHua.currentCount = 0;
		pBiHua.offset = biHuaAddr + 24;
		pBiHua.x = x;
		pBiHua.y = y;
		
		//Log.i(TAG, "[asmDrawBiHuaInit] End!!!");
		
		return 1;
	}
	
	/**
	 * DrawBiHua的服务函数。
	 * 
	 * @param pBiHua					SDrawBiHuaData
	 * @param bp						Bitmap
	 * @return							成功返回0
	 */
	int asmDrawBiHuaServiceLoop(AsmDrawBiHuaData pBiHua, Bitmap bp)
	{
		//Log.i(TAG, "[asmDrawBiHuaServiceLoop] Start!!!");
		
		int ret = 0;
		
		if(pBiHua.nextStrokeDelay > 0)
		{
			pBiHua.nextStrokeDelay -= 1;
			
			return ret;
		}
		
		try
		{
			if(pBiHua.currentPointNum == 0)
			{
				pBiHua.fp.seek(pBiHua.offset);
				
				int item = asmReadNBytesToNum(pBiHua.fp, 0, 1);
				pBiHua.currentPointNum = (short) asmReadNBytesToNum(pBiHua.fp, 0, 2);
				
				item = asmReadNBytesToNum(pBiHua.fp, 0, 1);
				
				if((item & 0xFF) == 0xFF)
				{
					pBiHua.offset += 4;
				}
				else
				{
					ret = 5;
					
					return ret;
				}
			}
			else
			{
				int readnum = 0;
				
				if((pBiHua.currentCount + AsmConstant.Macro_BiHuaBuffSize) > pBiHua.currentPointNum)
				{
					readnum = pBiHua.currentPointNum - pBiHua.currentCount;
				}
				else
				{
					readnum = AsmConstant.Macro_BiHuaBuffSize;
				}
				
				if(readnum == 0)
				{
					pBiHua.fp.seek(pBiHua.offset + pBiHua.currentCount * 2);
					
					int item = asmReadNBytesToNum(pBiHua.fp, 0, 2);
					if(item == 0xFFFF)
					{
						item = asmReadNBytesToNum(pBiHua.fp, 0, 2);
						if(item == 0xFFFF)
						{
							ret = 1;
							
							return ret;
						}
						else
						{
							pBiHua.offset += pBiHua.currentPointNum * 2 + 2;
							pBiHua.currentPointNum = 0;
							pBiHua.currentCount = 0;
							pBiHua.nextStrokeDelay = 10;
							
							return ret;
						}
					}
					else
					{
						ret = 1;
						
						return ret;
					}
				}
				
				pBiHua.fp.seek(pBiHua.offset + pBiHua.currentCount * 2);
				
				byte[] buff = new byte[readnum * 2];
				pBiHua.fp.read(buff, 0, readnum * 2);
				for(int i = 0; i < readnum; i++)
				{
					pBiHua.readBuff[i] = (short) asmBufferToNum(buff, i * 2, 2);
				}
				pBiHua.currentCount += readnum;
				
				int x = 0, y = 0;
				for(int i = 0; i < readnum; i++)
				{
					x = pBiHua.x + (pBiHua.readBuff[i] & 0xFF);
					y = pBiHua.y + ((pBiHua.readBuff[i] >> 8) & 0xFF);
					
					asmSetPixel(bp, x, y, Color.BLACK);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			Log.e(TAG, "[asmDrawBiHuaServiceLoop] Error!!!");
		}
		
		//Log.i(TAG, "[asmDrawBiHuaServiceLoop] End!!!");
		
		return ret;
	}
	
	/**
	 * 播放Avi和Animation的服务函数。
	 * 
	 * @return							成功返回0
	 */
	int asmPlayAviAndAnimationServiceLoop()
	{
		//Log.i(TAG, "[asmPlayAviAndAnimationServiceLoop] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		
		/*Log.i(TAG, "[asmPlayAviAndAnimationServiceLoop] " + 
				"MuteType == " + p.MuteType + 
				", MuteAnimationTime == " + p.MuteAnimationTime + 
				", MuteSpeechTime == " + p.MuteSpeechTime + 
				", MuteAviTime == " + p.MuteAviTime);*/
		
		if((pAvi.MuteType & AsmConstant.Macro_AnimationMute) > 0)
		{
			pAvi.MuteAnimationTime -= AsmConstant.Macro_MuteTimeUint;
		}
		
		if((pAvi.MuteType & AsmConstant.Macro_SpeechMute) > 0)
		{
			pAvi.MuteSpeechTime -= AsmConstant.Macro_MuteTimeUint;
		}
		
		if((pAvi.MuteType & AsmConstant.Macro_AviMute) > 0)
		{
			pAvi.MuteAviTime -= AsmConstant.Macro_MuteTimeUint;
		}
		
		if(pAvi.MuteAnimationTime < 1)
		{
			asmPlayAnimation();
		}
		
		if(pAvi.MuteSpeechTime < 1)
		{
			if(!asmGetSpeechStatus(pAvi.mSound))
			{
				asmPlaySpeech();
			}
		}
		
		if(pAvi.MuteAviTime < 1)
		{
			if(((AsmConstant.Macro_PlayAviEndBySph == pAvi.AviType) && pAvi.SpeechEnd) ||
				((AsmConstant.Macro_PlayAviEndByAni == pAvi.AviType) && pAvi.AnimationEnd) ||
				((AsmConstant.Macro_PlayAviEndBoth == pAvi.AviType) && pAvi.AnimationEnd && pAvi.SpeechEnd) ||
				((AsmConstant.Macro_PlayAviEndEither == pAvi.AviType) && pAvi.AnimationEnd && pAvi.SpeechEnd))
			{
				asmPlayAvi();
			}
		}
		
		//Log.i(TAG, "[asmPlayAviAndAnimationServiceLoop] End!!!");
		
		return 0;
	}
	
	/**
	 * 一些相关的处理函数。
	 * 
	 * @param action					操作
	 * @return							如果数据出错则返回1，否则返回0
	 */
	private int asmMacroSomeActionFunction(int action)
	{
		if(action == AsmConstant.Macro_DrawMap)
		{
			asmMacroDrawMapFunction();
		}
		else if(action == AsmConstant.Macro_DrawJPG)
		{
			asmMacroDrawJPGFunction();
		}
		else if(action == AsmConstant.Macro_ClearLCD)
		{
			
		}
		else
		{
			return 1;
		}
		
		return 0;
	}
	
	/**
	 * 刷JPG图函数。
	 */
	private void asmMacroDrawJPGFunction()
	{
		
	}
	
	/**
	 * 刷图函数。
	 */
	private void asmMacroDrawMapFunction()
	{
		AsmPlayAviData pAvi = mPlayAviData;
		int extAddr = mPlayAviData.AllDataAddr;
		
		int[] dataAddr = new int[1];
		
		int px = asmMacroGetNByteFromAddrBuff(dataAddr, extAddr, 2) & 0xFFFF;
		int py = asmMacroGetNByteFromAddrBuff(dataAddr, extAddr, 2) & 0xFFFF;
		if(px == 0xFFFF)
		{
			if(py == 0xFFFF)
			{
				
			}
			else
			{
				
			}
		}
		
		int index = asmMacroGetNByteFromAddrBuff(dataAddr, extAddr, 3) & 0xFFFFFF;
		
		/*int temp = index / 256;
		temp *= 200;
		temp += (index & 0xFF);
		index = temp;*/
		
		index = asmGetIndex(index);
		
		asmDrawMapByIndex(index, px, py);
		
		pAvi.is_flush_lcd = 1;
	}
	
	/**
	 * DrawBiHua函数。
	 */
	private void asmMacroDrawBiHuaFunction()
	{
		//Log.i(TAG, "[asmMacroDrawBiHuaFunction] Start!!!");
		
		AsmPlayAviData pAvi = mPlayAviData;
		
		try
		{
			RandomAccessFile fp = pAvi.CurrentAnimationFp;
			
			int[] dataAddr = new int[1];
			int px = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextMapAddr, 2) & 0xFFFF;
			int py = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextMapAddr, 2) & 0xFFFF;
			int index = asmMacroGetNByteFromAddrBuff(dataAddr, pAvi.NextMapAddr, 3) & 0xFFFFFF;
			
			/*int temp = index / 256;
			temp *= 200;
			temp += (index & 0xFF);
			index = temp;*/
			index = asmGetIndex(index);
			
			int LAddr = pAvi.AllDataAddr + 4 * index;
			
			fp.seek(LAddr);
			LAddr = asmReadNBytesToNum(fp, 0, 4);
			LAddr += pAvi.AllDataAddr;
			
			//Log.i(TAG, "[asmMacroDrawBiHuaFunction] px == " + px + 
			//		", py == " + py + ", LAddr == " + LAddr);
			
			int flag = asmDrawBiHuaInit(fp, LAddr, pAvi.BiHuaData, px, py);
			if(flag > 0)
			{
				pAvi.ClearBiHuaFlag = 1;
			}
			else
			{
				pAvi.ClearBiHuaFlag = 0;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		//Log.i(TAG, "[asmMacroDrawBiHuaFunction] End!!!");
	}
}
