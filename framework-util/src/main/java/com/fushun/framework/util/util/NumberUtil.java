package com.fushun.framework.util.util;

public class NumberUtil {


    /**
     * 获取i到j之间的随机数
     *
     * @param i
     * @param j
     */
    public static int getRandom(int i, int j) {
        return (int) (i + Math.random() * (j - i + 1));
    }

}
