package com.kyee.upgrade.utils;

import cn.hutool.extra.pinyin.engine.pinyin4j.Pinyin4jEngine;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

/**
 * 汉语转换拼音工具类
 */
public class PinyinUtils {

    /**
     * 汉语转换拼音
     * @param userName
     * @return
     */
    public static String convertPinyin(String userName) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 大小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不加声调
        format.setVCharType(HanyuPinyinVCharType.WITH_V);// 'ü' 使用 "v" 代替
        Pinyin4jEngine engine = new Pinyin4jEngine(format);
        return engine.getPinyin(userName, "");
    }
}
