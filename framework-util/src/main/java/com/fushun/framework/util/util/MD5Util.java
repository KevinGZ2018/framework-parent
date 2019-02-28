package com.fushun.framework.util.util;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * 采用MD5加密解密
 *
 * @datetime 2011-10-13
 */
public class MD5Util {

    /***
     * MD5加码 生成32位md5码
     */
    public static String string2MD5(String inStr) {

        Hasher hasher = Hashing.md5().newHasher();
        hasher.putString(inStr, Charset.defaultCharset());
        byte[] md5 = hasher.hash().asBytes();

        byte b[] = md5;
        int i;

        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        // 32位加密
        return buf.toString();
        // 16位的加密
        // return buf.toString().substring(8, 24);
    }


    // 测试主函数
    public static void main(String args[]) {
        for (int i = 0; i < 10; i++) {

            Thread t1 = new Thread(new Runnable() {

                @Override
                public void run() {
                    String s = "123456";
                    System.out.println("原始：" + s);
                    System.out.println("MD5后：" + string2MD5(s));
                }
            });
            t1.start();
        }
    }
}
