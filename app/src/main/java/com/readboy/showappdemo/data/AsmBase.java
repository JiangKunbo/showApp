package com.readboy.showappdemo.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.graphics.Bitmap;

import com.readboy.showappdemo.util.Log;


/**
 * ASM基类。<br/>
 * <br/>
 * 提供了ASM相关的公用操作方法。
 * 
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01在Android平台上重写了此代码
 */
public class AsmBase
{
	//private final String TAG = getClass().getSimpleName();		// TAG
	private final String TAG = "AsmBase";							// TAG
	
	/**
	 * 获取字符串的长度。
	 * 
	 * @param buff						Buff
	 * @param start						开始位置
	 * @return							字符串的长度
	 */
	protected int asmStrlen(byte[] buff, int start)
	{
		int i, j;
		
		for (i = start, j = 0; i < buff.length; i++, j++)
		{
			if (buff[i] == 0x00)
			{
				return j + 1;
			}
		}
		
		return j;
	}
	
	/**
	 * 获取字符串。
	 * 
	 * @param bytes						byte数组
	 * @param offset					偏移量
	 * @param charsetName				字符编码名称
	 * @return							字符串
	 */
	protected String asmGetString(byte[] bytes, int offset, String charsetName)
	{
		String str = null;
		
		try
		{
			str = new String(bytes, offset, asmStrlen(bytes, offset), charsetName);
		}
		catch (UnsupportedEncodingException e)
		{
			//e.printStackTrace();
		}
		
		return str;
	}
	
	/**
	 * 释放Bitmap资源。
	 * 
	 * @param bitmap					Bitmap
	 */
	protected void asmRecycleBitmap(Bitmap bitmap)
	{
		if(bitmap != null && !bitmap.isRecycled())
		{
			bitmap.recycle();
			bitmap = null;
		}
	}
	
	/**
	 * 将byte数组从指定位置开始的连续n个元素转换成数字。
	 * 
	 * @param buff						要转换的byte数组
	 * @param offset					转换的开始位置
	 * @param n							要转换的元素数量
	 * @return							转换成数字的结果
	 */
	protected int asmBufferToNum(byte[] buff, int offset, int n)
	{
		int num = 0;
		int index = offset + (n - 1);
		
		while ((n--) > 0)
		{
			num <<= 8;
			num += buff[index] & 0xff;
			index--;
		}
		
		return num & 0xffffffff;
	}
	
	/**
	 * 从文件的指定位置读取n个字节并转换成数字。
	 * 
	 * @param fp						要读取的文件句柄
	 * @param offset					偏移量
	 * @param n							要读取的字节数量
	 * @return							获得的数字
	 */
	protected int asmReadNBytesToNum(RandomAccessFile fp, int offset, int n)
	{
		int num = 0;
		
		try
		{
			byte[] buff = new byte[n];
			fp.read(buff, offset, n);
			
			int index = n - 1;
			
			while((n--) > 0)
			{
				num <<= 8;
				num += buff[index] & 0xff;
				index--;
			}
		}
		catch (IOException e)
		{
			//e.printStackTrace();
			
			Log.e(TAG, "[asmReadNBytesToNum] Error!!!");
		}
		
		return num & 0xffffffff;
	}
	
	/**
	 * 从文件的指定位置读取n个int类型（4 bytes）数值。
	 * 
	 * @param fp						要读取的文件句柄
	 * @param offset					偏移量
	 * @param buffer					保存结果的buffer
	 * @param n							要读取的数量
	 */
	protected void asmReadIntArray(RandomAccessFile fp, int offset, int[] buffer, int n)
	{
		try
		{
			byte[] buff = new byte[4 * n];
			
			fp.seek(offset);
			fp.read(buff, 0, 4 * n);
			
			int off = 0;
			for(int i = 0; i < n; i++)
			{
				buffer[i] = asmBufferToNum(buff, off, 4);
				off += 4;
			}
		}
		catch (IOException e)
		{
			//e.printStackTrace();
			
			Log.e(TAG, "[asmReadIntArray] Error!!!");
		}
	}
	
	/**
	 * 判断字符是否相等。
	 * 
	 * @param buffer					要比较的字符数组
	 * @param str						目标字符串
	 * @param n							要比较的数量
	 * @return							true-相等；false-不相等
	 */
	protected boolean asmStrncmp(byte[] buffer, String str, int n)
	{
		for (int i = 0; i < n; i++)
		{
			if (buffer[i] != str.codePointAt(i))
			{
				return false;
			}
		}
		
		/*String s = new String(buffer, 0, n);
		
		if(!str.equals(s))
		{
			return false;
		}*/
		
		return true;
	}
	
	/**
	 * 获取Index。
	 * 
	 * @param n							Num
	 * @return							Index
	 */
	protected int asmGetIndex(int n)
	{
		return ((((n) >> 8) & 0xff) * 200 + ((n) & 0xff));
	}
	
	/**
	 * 获取颜色值。
	 * 
	 * @param buff						buff
	 * @param index						Index
	 * @return							颜色值。
	 */
	protected int asmGetColors(byte[] buff, int index)
	{
		int r, g, b, rbg565;

		rbg565 = ((buff[index] & 0xff) | ((buff[index + 1] << 8) & 0xff00)) & 0xffff;
		
		r = ((((rbg565 & 0xf800) >> 8) & 0xff)) & 0xff;
		g = ((((rbg565 & 0x07e0) >> 3) & 0xff)) & 0xff;
		b = ((rbg565 & 0x001f) << 3) & 0xff;

		return 0xff000000 | ((r << 16) & 0xff0000) | ((g << 8) & 0xff00) | b;
	}
	
	/**
	 * 获取颜色值。
	 * 
	 * @param pBuff						pBuff
	 * @return							颜色值。
	 */
	protected int[] newAsmGetColors(byte[] pBuff, int width, int height)
	{
		int[] cBuff = new int[width * height];
		
		int r = 0;
		int g = 0;
		int b = 0;
		int rgb565 = 0;
		
		for(int i = 0; i < width * height; i++)
		{
			rgb565 = ((pBuff[i * 2] & 0xff) | ((pBuff[i * 2 + 1] << 8) & 0xff00)) & 0xffff;
			b = ((rgb565 & 0x001f) << 3) & 0xff;
			r = ((((rgb565 & 0xf800) >> 8) & 0xff)) & 0xff;
			g = ((((rgb565 & 0x07e0) >> 3) & 0xff)) & 0xff;
			
			cBuff[i] = 0xff000000 | ((r << 16) & 0xff0000) | ((g << 8) & 0xff00) | b;
		}
		
		return cBuff;
	}

	/**
	 * 格式化Png文件。
	 * 
	 * @param pHead						文件头信息
	 * @return							是Png文件返回true，否返回false
	 */
	protected boolean asmPngFileFormat(byte[] pHead)
	{
		//Log.i(TAG, "[asmPngFileFormat] Start!!!");
		
		final char[] PNG_HEADER_SIGNATURE = {0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
		
		int len = PNG_HEADER_SIGNATURE.length;
		for(int i = 0; i < len; i++)
		{
			if((pHead[i] & 0xFF) != PNG_HEADER_SIGNATURE[i])
			{
				return false;
			}
		}
		
		//Log.i(TAG, "[asmPngFileFormat] End!!!");
		
		return true;
	}

	/**
	 * 格式化Zip文件。
	 * 
	 * @param pHead						文件头信息
	 * @param zipInfo					Zip文件信息(zipInfo[0]：文件头长度；
	 * 									zipInfo[1]：源文件大小； zipInfo[2]：目标文件大小 )
	 * @return							是Zip压缩文件返回true，否返回false
	 */
	protected boolean asmZipFileFormat(byte[] pHead, int[] zipInfo)
	{
		//Log.i(TAG, "[asmZipFileFormat] Start!!!");
		
		final int ZIP_FILE_HEADER_SIGNATURE = 0x04034b50;			// 文件头标识
		final int ZIP_COMPRESSION_METHOD_DEFLATE = 0x0008;			// 压缩方法标志
		
		int off = 0;
		
		int headerSignature = asmBufferToNum(pHead, off, 4);
		if(ZIP_FILE_HEADER_SIGNATURE != headerSignature)
		{
			return false;
		}
		
		off += 8;
		
		int compressionMethod = asmBufferToNum(pHead, off, 2);
		if(ZIP_COMPRESSION_METHOD_DEFLATE != compressionMethod)
		{
			return false;
		}
		
		off += 2 + 2 + 2 + 4;// Compression method + time + date + CRC-32
		
		zipInfo[1] = asmBufferToNum(pHead, off, 4);
		
		off += 4;
		
		zipInfo[2] = asmBufferToNum(pHead, off, 4);
		
		off += 4;
		
		int fileNameLen = asmBufferToNum(pHead, off, 2);
		
		off += 2;
		
		int extraFieldLen = asmBufferToNum(pHead, off, 2);
		
		zipInfo[0] = 30 + fileNameLen + extraFieldLen;
		
		//Log.i(TAG, "[asmZipFileFormat] End!!!");
		
		return true;
	}

	/**
	 * 解压缩Zip数据。
	 * 
	 * @param inData					要解压的数据
	 * @return							解压后的数据
	 */
	protected byte[] asmZipUncompress(byte[] inData)
	{
		//Log.i(TAG, "[asmZipUncompress] Start!!!");
		
		byte[] result = null;
		
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(inData);
			ZipInputStream zis = new ZipInputStream(bais);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			ZipEntry ze = null;
			
			while((ze = zis.getNextEntry()) != null)
			{
				ze.getName();
				//Log.i(TAG, "[asmZipUncompress] Name == " + ze.getName());
				
				byte[] buff = new byte[1024];
				
				int len;
				while ((len = zis.read(buff, 0, buff.length)) != -1)
				{
					baos.write(buff, 0, len);
					baos.flush();
				}
			}
				
			result = baos.toByteArray();
			//Log.i(TAG, "[asmZipUncompress] Length == " + result.length);
			
			baos.close();
			zis.close();
			bais.close();
		}
		catch (IOException e)
		{
			//e.printStackTrace();
			
			Log.e(TAG, "[asmZipUncompress] Error!!!");
			
			return null;
		}
		
		//Log.i(TAG, "[asmZipUncompress] End!!!");
		
		return result;
	}
	
	/**
	 * 获得当前文本的词典类型（中文还是英文）。
	 * 
	 * @param str						String			
	 * @return							词典类型
	 */
	protected int asmGetDictType(String str) 
	{
		int value;

		if(str == null) 
		{
			return AsmConstant.DICT_TYPE_ERROR;
		}
		
		int len = str.length();
		for(int i = 0; i < len; i++) 
		{
			value = str.charAt(i);
			
			if(value >= 'a' && value <= 'z' || value >= 'A' && value <= 'Z') 
			{
				return AsmConstant.DICT_TYPE_ENG;
			}
			else if(value >= 0x80 && value != 0x3000)// TAB键要过滤掉
			{
				return AsmConstant.DICT_TYPE_CHI;
			}
			else if(value >= '0' && value <= '9') 
			{
				return AsmConstant.DICT_TYPE_CHI;
			}
		}

		return AsmConstant.DICT_TYPE_ERROR;
	}
}
