package com.keunsy.wechat.utils;

import java.io.UnsupportedEncodingException;

public class BasicUtil {

    /**
     * 计算采用utf-8编码方式时字符串所占字节数
     * 
     * @param content
     * @return
     */
    public static int getByteSize(String content) {
	int size = 0;
	if (null != content) {
	    try {
		// 汉字采用utf-8编码时占3个字节
		size = content.getBytes("utf-8").length;
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    }
	}
	return size;
    }

    /**
     * emoji表情转换(hex -> utf-16)
     * 
     * @param hexEmoji
     * @return
     */
    public static String emoji(int hexEmoji) {
	return String.valueOf(Character.toChars(hexEmoji));
    }

    public static void main(String[] args) {
	System.out.println("的嘛西亚" + emoji(0x1F5C2));
    }

}
