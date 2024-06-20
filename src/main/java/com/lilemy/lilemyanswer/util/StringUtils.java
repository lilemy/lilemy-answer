package com.lilemy.lilemyanswer.util;

/**
 * 字符串工具
 */
public class StringUtils {
    /**
     * 获取指定第几位字符串后面字符串，如：截取第二个逗号后面的数据
     *
     * @param str:要处理的字符串
     * @param mediumStr：根据截取的媒介字符串，如逗号
     * @param index：根据第几个媒介进行截取
     * @return {@link String}
     **/
    public static String subStringAssignEnd(String str, String mediumStr, Integer index) {
        int strIndex;
        for (int i = 0; i < index - 1; i++) {
            strIndex = str.indexOf(mediumStr);
            str = str.substring(strIndex + 1);
        }
        strIndex = str.indexOf(mediumStr);
        str = str.substring(strIndex);
        return str;
    }

    /**
     * 获取指定第几位字符串前面字符串，如：截取第二个逗号前面的数据
     *
     * @param str:要处理的字符串
     * @param mediumStr：根据截取的媒介字符串，如逗号
     * @param index：根据第几个媒介进行截取
     * @return {@link String}
     **/
    public static String subStringAssignFront(String str, String mediumStr, Integer index) {
        int strIndex = 0;
        String tmpStr = str;
        for (int i = 0; i < index; i++) {
            strIndex = tmpStr.indexOf(mediumStr);
            tmpStr = tmpStr.substring(strIndex + 1);
        }
        str = str.substring(0, strIndex);
        return str;
    }
}
