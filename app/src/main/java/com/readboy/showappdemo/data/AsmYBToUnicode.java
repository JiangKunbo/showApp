package com.readboy.showappdemo.data;

/**
 * ASM音标转换成Unicode编码。
 * 
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01在Android平台上重写了此代码
 */
public class AsmYBToUnicode
{
	/**
	 * 转换表,如果不全自己添加。<br/>
	 * 前面的是Unicode编码，后面是数据中的编码。
	 */
	static final char [][]codepage = 
	{
			{0x026a, 	0x056a},	// 
			{0x00e6, 	0x056c},	// 
			{0x025c, 	0x0578},	// 
			{0x0259, 	0x0574},	// 
			{0x028c, 	0x0572},	// 
			{0x0254, 	0x056e},	// 
			{0x028a, 	0x0584},	// 
			{0x0283, 	0x0560},	// 
			{0x0292, 	0x0568},	// 
			{0x03b8, 	0x0563},	// 
			{0x00f0, 	0x0552},	// 
			{0x014b, 	0x055c},	// 
			{0x0251, 	0x058d}		// 
	};
	
	/**
	 * 获取Unicode编码。
	 * 
	 * @param old_code					数据中的编码
	 * @return							Unicode编码
	 */
	static char asmGetCode(char old_code)
	{
		char new_code = old_code;
		
		if(old_code >= 0x550 && old_code <= 0x5bb)
		{
			int len = codepage.length;
			for(int i = 0; i < len; i++)
			{
				if(old_code == codepage[i][1])
				{
					new_code = codepage[i][0];
					break;
				}
			}
		}
		
		return new_code;
	}
	
	/**
	 * 从byte数组中获取char数组。
	 * 
	 * @param bytes						byte数组
	 * @param start						起始位置
	 * @return							char数组
	 */
	static char[] asmGetChar(byte[] bytes, int start)
	{
		int i, j, cnt = 1;
		
		int len = bytes.length - 1;
		for (i = start; i < len; i += 2)
		{
			if ((bytes[i] & 0xFF) == 0 && (bytes[i + 1] & 0xFF) == 0)
			{
				cnt = i / 2 + 1;
				break;
			}
		}

		char[] data = new char[cnt];

		for (i = start, j = 0; i < len; i += 2, ++j)
		{
			if ((bytes[i + 1] & 0xFF) == 0 && (bytes[i] & 0xFF) == 0)
			{
				break;
			}
			
			data[j] = (char) (((bytes[i + 1] << 8) & 0xFF00) | (bytes[i] & 0xFF));
		}

		return data;
	}
	
	/**
	 * 从char数组中获取转换Unicode编码后的char数组。
	 * 
	 * @param str						char数组
	 * @return							转换Unicode编码后的char数组
	 */
	static char[] asmGetStr(char[] str)
	{
		int len = str.length;
		for(int i = 0; i < len; i++)
		{
			str[i] = asmGetCode(str[i]);
		}
		
		/*for(char c : str)
		{
			c = getCode(c);
		}*/
		
		return str;
	}
	
	/**
	 * 从char数组中获取转换Unicode编码后的char数组。
	 * 
	 * @param str						char数组
	 * @param start						起始位置
	 * @return							转换Unicode编码后的char数组
	 */
	static char[] asmGetStr(char[] str, int start)
	{
		int len = str.length;
		for(int i = start; i < len; i++)
		{
			str[i] = asmGetCode(str[i]);
		}
		
		return str;
	}
	
	/**
	 * 从char数组中获取转换Unicode编码后的char数组。
	 * 
	 * @param str						char数组
	 * @param start						起始位置
	 * @param end						结束位置
	 * @return							转换Unicode编码后的char数组
	 */
	static char[] asmGetStr(char[] str, int start, int end)
	{
		int len = str.length;
		for(int i = start; i < len; i++)
		{
			if(i >= end)
			{
				break;
			}
			str[i] = asmGetCode(str[i]);
		}
		
		return str;
	}
	
	/**
	 * 从byte数组中获取转换Unicode编码后的char数组。
	 * 
	 * @param bytes						
	 * @param start						起始位置
	 * @return							转换Unicode编码后的char数组
	 */
	static char[] asmGetStr(byte[] bytes, int start)
	{
		return asmGetStr(asmGetChar(bytes, start));
	}
	
	/**
	 * 音标列表。
	 */
	static char[] mYinBiaoList = 
	{
    	    0x0062,		/* 0x50 'b' */
    	    0x0064,		/* 0x51 'd' */
    	    0x00f0,		/* 0x52 'e'*/
    	    0,			/* 0x53 ''*/
    	    0x0066,		/* 0x54 'f'*/
    	    0x0067,		/* 0x55 'g' */
    	    0x0068,		/* 0x56 'h'*/
    	    0x006a,		/* 0x57 'j'*/
    	    0x006b,		/* 0x58 'k'*/
    	    0x006c,		/* 0x59 'l'*/
    	    0x006d,		/* 0x5a 'm'*/
    	    0x006e,		/* 0x5b 'n'*/
    	    0x014b,		/* 0x5c '?'*/
    	    0x0070,		/* 0x5d 'p'*/
    	    0x0072,		/* 0x5e 'r'*/
    	    0x0073,		/* 0x5f 's'*/
    	    0x0283,		/* 0x60 '?'*/
    	    0x0074,		/* 0x61 't'*/
    	    0,			/* 0x62 ''*/
    	    0x03b8,		/* 0x63 'θ'*/
    	    0x0076,		/* 0x64 'v'*/
    	    0x0077,		/* 0x65 'w'*/
    	    0x0078,		/* 0x66 'x'*/
    	    0x007a,		/* 0x67 'z'*/
    	    0x0292,		/* 0x68 '?'*/
    	    0x0069,		/* 0x69 'i'*/
    	    0x026a,		/* 0x6a '?'*/
    	    0x025b,		/* 0x6b '?'*/
    	    0x00e6,		/* 0x6c '?'*/
    	    0x0061,		/* 0x6d 'a'*/
    	    0x0254,		/* 0x6e '?'*/
    	    0x0075,		/* 0x6f 'u'*/
    	    0x006d,		/* 0x70 'm'*/
    	    0,			/* 0x71 ''*/
    	    0x028c,		/* 0x72 '?'*/
    	    0x025c,		/* 0x73 '?'*/
    	    0x0259,		/* 0x74 '?'*/
    	    0x0259,		/* 0x75 '?'*/
    	    0,			/* 0x76 ''*/
    	    0x006f,		/* 0x77 'o'*/
    	    0x25c,		/* 0x78 '?'*/
    	    0,			/* 0x79 ''*/
    	    0,			/* 0x7a ''*/
    	    0x02d0,		/* 0x7b '?'*/
    	    0x02c8,		/* 0x7c '?'*/
    	    0,			/* 0x7d ''*/
    	    0,			/* 0x7e ''*/
    	    0,			/* 0x7f ''*/
    	    0x0259,		/* 0x80 '?'*/
    	    0x0254,		/* 0x81 '?'*/
    	    0x02cc,		/* 0x82 '?'*/
    	    0,			/* 0x83 '?'*/
    	    0x0075,		/* 0x84 'u'*/
    	    0x0075,		/* 0x85 'u'*/
    	    0x006e,		/* 0x86 'n'*/
    	    0,			/* 0x87 ''*/
    	    0,			/* 0x88 ''*/
    	    0x0101,		/* 0x89 'ā'*/
    	    0x00e1,		/* 0x8a 'á'*/
    	    0x0103,		/* 0x8b '?'*/
    	    0x00e0,		/* 0x8c 'à'*/
    	    0x0251,		/* 0x8d 'ɑ'*/
    	    0x0062,		/* 0x8e 'b'*/
    	    0x0063,		/* 0x8f 'c'*/
    	    0x0064,		/* 0x90 'd'*/
    	    0x0113,		/* 0x91 'ē'*/
    	    0x00e9,		/* 0x92 'é'*/
    	    0x011b,		/* 0x93 'ě'*/
    	    0x00e8,		/* 0x94 'è'*/
    	    0x0065,		/* 0x95 'e'*/
    	    0x0066,		/* 0x96 'f'*/
    	    0x0067,		/* 0x97 'g'*/
    	    0x0068,		/* 0x98 'h'*/
    	    0x012b,		/* 0x99 'ī'*/
    	    0x00ed,		/* 0x9a 'í'*/
    	    0x01d0,		/* 0x9b 'ǐ'*/
    	    0x00ec,		/* 0x9c 'ì'*/
    	    0x0069,		/* 0x9d 'i'*/
    	    0x006a,		/* 0x9e 'j'*/
    	    0x006b,		/* 0x9f 'k'*/
    	    0x006c,		/* 0xa0 'l'*/
    	    0x006d,		/* 0xa1 'm'*/
    	    0x006e,		/* 0xa2 'n'*/
    	    0x014d,		/* 0xa3 'ō'*/
    	    0x00f3,		/* 0xa4 'ó'*/
    	    0x01d2,		/* 0xa5 'ǒ'*/
    	    0x00f2,		/* 0xa6 'ò'*/
    	    0x006f,		/* 0xa7 'o'*/
    	    0x0070,		/* 0xa8 'p'*/
    	    0x0071,		/* 0xa9 'q'*/
    	    0x0072,		/* 0xaa 'r'*/
    	    0x0073,		/* 0xab 's'*/
    	    0x0074,		/* 0xac 't'*/
    	    0x016b,		/* 0xad 'ū'*/
    	    0x00fa,		/* 0xae 'ú'*/
    	    0x016d,		/* 0xaf '?'*/
    	    0x00f9,		/* 0xb0 'ù'*/
    	    0x0075,		/* 0xb1 'u'*/
    	    0,			/* 0xb2 ''*/
    	    0x01d8,		/* 0xb3 'ǘ'*/
    	    0x01da,		/* 0xb4 'ǚ'*/
    	    0x01dc,		/* 0xb5 'ǜ'*/
    	    0x00fc,		/* 0xb6 'ü'*/
    	    0x0077,		/* 0xb7 'w'*/
    	    0x0078,		/* 0xb8 'x'*/
    	    0x0079,		/* 0xb9 'y'*/
    	    0x007a,		/* 0xba 'z'*/
    	    0,			/* 0xbb ''*/
    	    0,			/* 0xbc ''*/
    	    0,			/* 0xbd ''*/
    	    0,			/* 0xbe ''*/
    	    0,			/* 0xbf ''*/
	};
}
