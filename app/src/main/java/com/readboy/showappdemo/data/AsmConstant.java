package com.readboy.showappdemo.data;

/**
 * ASM相关的常量值。统一写成一个类，方便维护。
 * 
 * @author 		lyj
 * @version 	1.14.04.01
 * @date 		2014.04.01
 * @history		lyj 2013.08.01在Android平台上重写了此代码
 */
public class AsmConstant
{
	static int PANEL_WIDTH							=	1280;			// 屏幕宽度
	static int PANEL_HEIGHT							=	800;			// 屏幕高度
	
	//*****************************************文件头信息*****************************************************//
	static final String AsmDataHeadInfo				= 	"Computer";		// ASM数据文件头信息
	static final String AsmBiHuaHeadInfo			= 	"DSLHZN";		// 描红数据文件头信息
	
	static final int Version						=	0x0100;			// 版本
	static final int Compact_Format					=	0x01;			// 压缩格式
	static final int Card_Flag						=	Compact_Format;	// 
	
	//**************************************MenuStruct的取值**************************************************//
	static final int MenuStruct						=	0xF0;			// 后接具体 0，1，2等 区分不同的菜单类型
	
	static final int MenuTypeMain					=	0;				// 菜单
	static final int MenuType1						=	1;				// 图片菜单
	static final int MenuType2						=	2;				// 文字菜单
	static final int MenuType3						=	3;				// 按钮菜单
	
	//**************************************DataStruct的取值**************************************************//
	static final int DataStruct						=	0xF1;			// 后接具体 0，1，2等 区分不同的具体数据
	
	static final int DataType1						=	1;				// 纯文本
	static final int DataType2						=	2;				// 语音+图片操作
	static final int DataType3						=	3;				// 题目+答案
	static final int DataType4						=	4;				// 语音+文字高亮显示（百家姓）
	static final int DataType5						=	5;				// 语音+文字显示（三字经、弟子规、千字文）
	static final int DataType6						=	6;				// 
	static final int DataType7						=	7;				// 
	static final int DataType8						=	8;				// 菜单跟内容文字在同一界面
	static final int DataType9						=	9;				// 
	static final int DataType10						=	10;				// 
	
	static final int FunctionAp						=	0xFC;			// 后接具体 0，1，2等 区分不同的APP
	static final int ItemAndMenu					=	0x10;			// 小项目和内容同级界面
	
	static final int MenuType1_ITEM_NUM_MAX			=	50;				// MenuType1条目的最大数量
	
	static final int MenuType2_ITEM_NUM_MAX			=	512;			// MenuType2条目的最大数量
	static final int MenuType2_ITEM_TEXT_MAX		=	64;				// MenuType2条目的文本最大数量
	
	static final int MenuType2_LEVEL_MAX			=	30;				// MenuType2列表最大数
	
	static final int MenuType2_HEIGHT				=	40;				// MenuType2条目的高度
	
	static final int MenuType3_ITEM_NUM_MAX			=	200;			// MenuType3条目的最大数量
	
	static final int DataType3_BUTTON_NUM			=	4;				// DataType3按钮的最大数量
	static final int DataType3_ITEM_NUM_MAX			=	256;			// DataType3条目的最大数量
	static final int DataType3_TEXT_NUM_MAX			=	1024 * 8;		// DataType3条目的文本最大数量
	
	static final int DataType4_ITEM_NUM_MAX			=	100;			// DataType4条目的最大数量
	static final int DataType4_ITEM_TEXT_MAX		=	64;				// DataType4条目的文本最大数量
	static final int DataType4_TEXT_MAX				=	1024 * 2;		// DataType4的文本最大数量
	
	//****************************************************************************************************//
	static final int Macro_TimerInterval			=	10; 			// 时间控制间隔为 0.01 秒
	static final int Macro_MuteTimeUint				=	10; 			// 时间控制间隔为 0.01 秒
	
	static final int Macro_Mute						=	0xC8; 			// Mute
	static final int Macro_MuteWord					=	0xC9;			// MuteWord
	
	static final int Macro_PlayAviEndByAni			=	0xCA; 			// PlayAviEndByAni
	static final int Macro_PlayAviEndBySph			=	0xCB; 			// PlayAviEndBySph
	static final int Macro_PlayAviEndBoth			=	0xCC;			// PlayAviEndBoth
	static final int Macro_PlayAviEndEither			=	0xCD;			// PlayAviEndEither
	
	static final int Macro_DrawTextIndex			=	0xCE;			// 文件标号
	static final int Macro_DrawText					=	0xCF;			// DrawText
	
	static final int Macro_DrawMap					=	0xD0; 			// DrawMap
	static final int Macro_DrawMapNoxy				=	0xD0;			// DrawMapNoxy
	static final int Macro_DrawMapWithItem			=	0xD0;			// DrawMapWithItem
	
	static final int Macro_DrawJPG					=	0xD1;			// DrawJPG
	
	static final int Macro_ClearLCD					=	0xD2; 			// ClearLCD

	static final int Macro_SetLoop					=	0xD3; 			// SetLoop
	static final int Macro_SetLoopTimes				=	0xD3;			// SetLoopTimes
	static final int Macro_DoLoop					=	0xD4; 			// DoLoop
	
	static final int Macro_DrawBiHua				=	0xD5;			// DrawBiHua
	
	static final int Macro_MouseArea				=	0xD6;			// MouseArea
	
	static final int Macro_DrawItemText				=	0xE0;			// DrawItemText
	static final int Macro_DrawItemTextIndex		=	0xE1;			// DrawItemTextIndex
	static final int Macro_DrawItemMap				=	0xE2;			// DrawItemMap
	
	static final int Macro_End						=	0xFF;			// 结束标记
	
	//**************************************LoopType的取值**************************************************//
	static final int Macro_LoopTimes				=	1;				// 图形有限循环
	static final int Macro_LoopNoEnd				=	2;				// 图形无限循环，不必重复定义
	static final int Macro_NoLoop					=	3;				// 图形没有循环

	//**************************************MuteType的取值*************************************************//
	static final int Macro_NotAnyMute				=	0;				// NotAnyMute
	static final int Macro_AviMute					=	1;				// AviMute
	static final int Macro_AnimationMute			=	2;				// AnimationMute
	static final int Macro_SpeechMute				=	4;				// SpeechMute
	static final int Macro_DrawBiHuaMute			=	8;				// DrawBiHuaMute
		
	static final int Macro_NextAddrBuffSize			=	50;				// 地址Buff大小
	static final int Macro_PlayAviSt_Space			=	150;			// 
	
	static final int Macro_BiHuaBuffSize			=	80;				// 笔画Buff大小
	
	//****************************************************************************************************//
	static final int TEAM_BUTTON_ID_MIN				=	0x1000;			// 按钮组起始ID
	static final int BUTTON_ID_MIN					=	0x2000;			// 按钮起始ID
	static final int LIST_ID_MIN					=	0x3000;			// 列表起始ID
	static final int TEXT_ID_MIN					=	0x4000;			// 文本起始ID
	
	static final int ASM_AVI_TIMER_ID				=	0x5000;			// 播放Avi的定时器消息ID
	
	static final int ASM_TEXT_SHOW_ID				=	0x6000;			// 显示文本的消息ID
	
	static final int ASM_AVI_TIMER_PERIOD			=	50;				// 播放Avi的时间间隔
	
	static final int MENUTYPE1_LIST_ID 				=	LIST_ID_MIN + 1;// 图片菜单列表消息ID
	static final int MENUTYPE2_LIST_ID 				=	LIST_ID_MIN + 2;// 文字菜单列表消息ID
	static final int MENUTYPE3_LIST_ID 				=	LIST_ID_MIN + 3;// 按钮菜单列表消息ID
	
	static final int ASM_DELAY_TIME					=	0;				// 延时时间间隔
	static final int ASM_FONT_SIZE					=	24;				// 字体大小
	static final int ASM_FONT_COLOR_TITLE			=	0xFF0000FF;		// 标题字体颜色
	static final int ASM_FONT_COLOR_TEXT			=	0xFF000000;		// 正文字体颜色
	
	static final int ASM_TEXT_SIZE_MAX				=	1024 * 200;		// 文本的最大数量
	
	static final int ASM_FLIP_DISTANCE				=	50;				// 两个触点的距离
	
	static final String ASM_ASCII_CODE				=	"GB2312";		// ASCII_CODE
	
	//****************************************字典类型***************************************************//
	static final int DICT_TYPE_ERROR				=	-1;				// 未知类型
	static final int DICT_TYPE_ENG					=	0;				// 英语字典类型
	static final int DICT_TYPE_CHI					=	1;				// 中文字典类型
}
