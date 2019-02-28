package com.fushun.framework.util.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常堆栈打印类
 *
 * @author fushun
 * @version V3.0商城
 * @creation 2017年1月17日
 */
public class ExceptionUtil {

    /**
     * 异常类堆栈打印
     *
     * @param e
     * @return
     * @author fushun
     * @version V3.0商城
     * @creation 2017年1月17日
     * @records <p>  fushun 2017年1月17日</p>
     */
    public static String getPrintStackTrace(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            //将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}
