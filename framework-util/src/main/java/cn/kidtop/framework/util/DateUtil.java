package cn.kidtop.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zpcsa
 */
public class DateUtil {
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    /**
     * 锁对象
     */
    private static final Object lockObj = new Object();
    private static final String shortSdf = "yyyy-MM-dd";
    private static final String longHourSdf = "yyyy-MM-dd HH";
    private static final String longSdf = "yyyy-MM-dd HH:mm:ss";
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static String FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
    /**
     * yyyy-MM-dd
     */
    public static String FORMAT_DATE_STR = "yyyy-MM-dd";
    /***
     * yyyyMMdd
     */
    public static String FORMAT_DATE_STR2 = "yyyyMMdd";
    /**
     * yyyy年MM月dd日
     */
    public static String FORMAT_DATE_STR3 = "yyyy年MM月dd日";
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    public static String FORMAT_ISO_DATE_STR = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSXXX
     */
    public static String FORMAT_ISO_DATE_STR2 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    /**
     * HH:mm:ss
     */
    public static String FORMAT_TIME_STR = "HH:mm:ss";
    /**
     * HH
     */
    public static String FORMAT_TIME_STR2 = "HH";
    /**
     * yyyy.MM.dd
     */
    public static String FORMAT_DATE_STR4 = "yyyy.MM.dd";
    /**
     * yyyy.MM.dd HH:mm
     */
    public static String FORMAT_DATE_STR5 = "yyyy.MM.dd HH:mm";
    /**
     * yyyy.MM.dd HH:mm:ss
     */
    public static String FORMAT_DATE_STR6 = "yyyy.MM.dd HH:mm:ss";
    /**
     * yyyy.MM.dd HH:mm:ss
     */
    public static String FORMAT_DATE_STR7 = "MM-dd HH:mm";
    public static String FORMAT_DATE_STR8 = "yyyyMMddHHmmssSSS";
    public static ExecutorService es = new ThreadPoolExecutor(20, 500,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
    /**
     * 存放不同的日期模板格式的SimpleDateFormat的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> simpleDateFormat = new HashMap<String, ThreadLocal<SimpleDateFormat>>();
    private static ThreadLocal<HashMap<String, SimpleDateFormat>> simpleDateFormatMap = new ThreadLocal<HashMap<String, SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的SimpleDateFormat,每个线程只会new一次SimpleDateFormat
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSimpleDateFormat(final String pattern) {
        HashMap<String, SimpleDateFormat> tlMap = null;
        synchronized (lockObj) {
            HashMap<String, SimpleDateFormat> tl = simpleDateFormatMap.get();
            if (tl == null) {
                tlMap = new HashMap<String, SimpleDateFormat>();
                logger.debug("thread: " + Thread.currentThread() + " init map pattern: " + pattern);
                tlMap.put(pattern, new SimpleDateFormat(pattern));
                simpleDateFormatMap.set(tlMap);
            } else {
                tlMap = tl;

                if (BeanUtils.isEmpty(tlMap.get(pattern))) {
                    logger.debug("thread: " + Thread.currentThread() + " init pattern: " + pattern);
                    tlMap.put(pattern, new SimpleDateFormat(pattern));
                }
                logger.debug("thread: " + Thread.currentThread() + " pattern: " + pattern);
            }
        }
        return tlMap.get(pattern);
    }

    /**
     * 返回一个ThreadLocal的SimpleDateFormat,每个线程只会new一次SimpleDateFormat
     *
     * @param pattern
     * @return
     */
    @SuppressWarnings("unused")
    private static SimpleDateFormat getSimpleDateFormat1(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = simpleDateFormat.get(pattern);
        //双重判断和同步是为了防止sdfMap这个单例被多次put重复的SimpleDateFormat
        if (tl == null) {
            synchronized (lockObj) {
                tl = simpleDateFormat.get(pattern);
                if (tl == null) {
//                    System.out.println("put new sdf of pattern " + pattern + " to map");
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
//                            System.out.println("thread: " + Thread.currentThread() + " init pattern: " + pattern);
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    simpleDateFormat.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    /**
     * 转换指定的时间
     *
     * @param date
     * @param formatType
     * @return
     */
    public static String getDateStr(Date date, String formatType) {
        SimpleDateFormat myFmt = getSimpleDateFormat(formatType);
        String timeStr = myFmt.format(date).toString();
        return timeStr;
    }

    /***
     * @Title: toDate
     * @Description: 转换为日期
     * @param fmt
     *            格式
     * @param dateStr
     *            日期串
     * @return Date
     * @throws
     */
    public static Date toDate(String fmt, String dateStr) {
        SimpleDateFormat sdf = getSimpleDateFormat(fmt);
        try {
            Date date = sdf.parse(dateStr);
            return date;
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    /**
     * 判断系统当前时间是否大于等于时间date
     *
     * @param date       时间字符串
     *                   date时间类型必须已经包括formatType要求的格式的了,比如把“12/25/2009”转化成“yyyy-MM-dd”
     *                   这种是不支持的,转换要求是在相同format下的
     * @param formatType 日期格式类型,例如:"yyyy-MM-dd" "yyyy-MM-dd HH:mm"
     * @return boolean 系统时间大于等于参数时间,则返回TRUE,否则返回false
     * @throws Exception      异常
     * @throws ParseException 异常
     */
    public static boolean isDateBefore(String date, String formatType) throws IllegalArgumentException, ParseException {
        if (StringUtils.isEmpty(date) || StringUtils.isEmpty(formatType)) {
            return false;
        }
        String result;

        Date systemDate = new Date();
        DateFormat df = getSimpleDateFormat(formatType);

        result = df.format(df.parse(date));
        if (date.startsWith(result)) {
            if (df.format(systemDate).equals(df.format(df.parse(date))))
                return false;
            return systemDate.after(df.parse(date));
        } else {
            throw new IllegalArgumentException(String.format("错误的日期参数：%1$s，不满足[%2$s]格式要求", date, formatType));
        }

    }

    /**
     * 判断系统当前时间是否大于时间date
     *
     * @param date       时间字符串
     *                   date时间类型必须已经包括formatType要求的格式的了,比如把“12/25/2009”转化成“yyyy-MM-dd”
     *                   这种是不支持的,转换要求是在相同format下的
     * @param formatType 日期格式类型,例如:"yyyy-MM-dd" "yyyy-MM-dd HH:mm"
     * @return boolean 系统时间大于参数时间,则返回FALSE,否则返回TRUE
     * @throws Exception      异常
     * @throws ParseException 异常
     */
    public static boolean isDateBefore2(String date, String formatType) throws Exception, ParseException {
        if (StringUtils.isEmpty(date) || StringUtils.isEmpty(formatType)) {
            return false;
        }
        String result;

        Date systemDate = new Date();
        DateFormat df = getSimpleDateFormat(formatType);
        result = df.format(df.parse(date));
        if (date.startsWith(result))
            return systemDate.after(df.parse(date));
        else
            throw new IllegalArgumentException(String.format("错误的日期参数：%1$s，不满足[%2$s]格式要求", date, formatType));

    }

    /**
     * 格式化日期
     *
     * @param date       时间
     * @param formatType 日期格式类型,例如:"yyyy-MM-dd" "yyyy-MM-dd HH:mm"
     * @return String 格式转换成功后的字符串
     */
    public static String doDateFormat(Date date, String formatType) {
        DateFormat df = getSimpleDateFormat(formatType);
        return df.format(date);

    }

    /**
     * @param dateOneBef 被比较时间起
     * @param dateTwoBef 被比较时间止
     * @param dateOneAft 比较时间起
     * @param dateTwoAft 比较时间止
     * @return boolean
     * @Title: compareTwoDate
     * @Description: 判断一个时间区间是否在另一个时间区间内
     */
    public static boolean compareTwoGrane(String dateOneBef, String dateTwoBef, String dateOneAft, String dateTwoAft) {
        int befCompare = dateOneBef.compareTo(dateOneAft);
        int aftCompare = dateTwoBef.compareTo(dateTwoAft);
        if (befCompare >= 0 && aftCompare <= 0) {
            return true;
        }
        return false;
    }

    /**
     * @param dateOne       第一个日期
     * @param formatTypeOne 第一个日期的格式
     * @param dateTwo       第二个日期
     * @param formatTypeTwo 第二个日期的格式
     * @return -1：第一个日期小于第二个日期；0：第一个日期等于第二个日期；1：第一个日期大于第二个日期
     * @throws Exception
     * @Title: compareDate
     * @Description: 比较两个日期的大小
     */
    public static int compareTwoDate(String dateOne, String formatTypeOne, String dateTwo, String formatTypeTwo) {
        DateFormat dfOne = getSimpleDateFormat(formatTypeOne);
        DateFormat dfTwo = getSimpleDateFormat(formatTypeTwo);
        long dtTimeOne = 0;
        long dtTimeTwo = 0;
        try {
            dtTimeOne = dfOne.parse(dateOne).getTime();
            dtTimeTwo = dfTwo.parse(dateTwo).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dtTimeOne > dtTimeTwo) {
            return 1;
        } else if (dtTimeOne < dtTimeTwo) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 获取日期字符串
     *
     * @param formatType 日期格式类型,例如:"yyyy-MM-dd" "yyyy-MM-dd HH:mm"
     * @return String 格式转换成功后的字符串
     */
    public static String getNowDateStr(String formatType) {
        if (StringUtils.isEmpty(formatType)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat myFmt = getSimpleDateFormat(formatType);
        String timeStr = myFmt.format(cal.getTime());
        return timeStr;
    }

    /**
     * @param date 日期
     * @return boolean 是否符合格式
     * @throws
     * @Title: isDateYYYYMMDD
     * @Description: 判断格式
     */
    public static boolean isDateYYYYMMDD(String date) {
        String regEx = "^(1|2)\\d{3}-((0{0,1}[1-9])|(1[012]))-\\d{1,2}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(date);
        return m.find();
    }

    /**
     * @param date 日期
     * @return boolean
     * @Title: isDateYYYYMMDD2
     * @Description: 判断一个日期格式是否合法
     */
    public static boolean isDateYYYYMMDD2(String date) {
        if (StringUtils.isEmpty(date)) {
            return false;
        }
        String formatter = null;
        if (date.indexOf('-') != -1) {
            formatter = "yyyy-MM-dd";
        } else if (date.indexOf('/') != -1) {
            formatter = "yyyy/MM/dd";
        } else {
            formatter = "yyyyMMdd";
        }
        SimpleDateFormat dateFormat = getSimpleDateFormat(formatter);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param date 日期
     * @return boolean 是否符合格式
     * @throws
     * @Title: isDateYYYYMM
     * @Description: 判断格式
     */
    public static boolean isDateYYYYMM(String date) {
        String regEx = "^(1|2)\\d{3}-((0{0,1}[1-9])|(1[012]))$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(date);
        return m.find();
    }

    /**
     * @param dateStr 日期
     * @return boolean 是否符合格式
     * @throws
     * @Title: isDateYYYYMM
     * @Description: 判断格式
     */
    public static boolean isDateYYYY(String dateStr) {
        String regEx = "^(19|20)\\d{2}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(dateStr);
        return m.find();
    }

    /**
     * 根据日期的格式校验是否为合理的日期
     *
     * @param pattern 日期的格式
     * @param dateStr 值
     * @return boolean
     */
    public static boolean isValidateDate(String pattern, String dateStr) {
        try {
            SimpleDateFormat dateFormat = getSimpleDateFormat(pattern);
            dateFormat.setLenient(false);
            dateFormat.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取某月的最后一天
     *
     * @param dateStr
     * @param informat
     * @param outformat
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年5月19日
     */
    public static String getLastDayOfMonth(String dateStr, String informat, String outformat) {
        String lastDateStr = "";
        DateFormat indf = getSimpleDateFormat(informat);
        DateFormat outdf = getSimpleDateFormat(outformat);
        Date date;
        try {
            date = indf.parse(dateStr);
            Calendar ca = Calendar.getInstance();
            ca.setTime(date); // someDate 为你要获取的那个月的时间
            ca.set(Calendar.DAY_OF_MONTH, 1);
            ca.add(Calendar.MONTH, 1);
            ca.add(Calendar.DAY_OF_MONTH, -1);
            Date lastDate = ca.getTime();
            lastDateStr = outdf.format(lastDate);
        } catch (ParseException e) {
            logger.error("输入日期格式不合法。dateStr{}", dateStr, e);
            return null;
        }
        return lastDateStr;
    }

    /***
     *
     * @Title: getLastDayOfMonth2
     * @Description: 获取某月的最后一天
     * @param date
     *            日期串
     * @return String
     * @records  <p>  CP131080 修改人：周云 修改功能：增加获取获取某月的最后一天方法
     */
    public static Date getLastDayOfMonth2(Date date) {

        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.add(Calendar.MONTH, 1);
        ca.add(Calendar.DAY_OF_MONTH, -1);
        return ca.getTime();
    }

    /**
     * 获取某月的第一天
     *
     * @param date
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年5月19日
     */
    public static Date getFirstDayOfMonth(Date date) {

        Calendar ca = Calendar.getInstance();
        ca.setTime(date); // someDate 为你要获取的那个月的时间
        ca.set(Calendar.DAY_OF_MONTH, 1);
        return ca.getTime();
    }

    /**
     * @param date  日期串
     * @param years 年数
     * @return String
     * @throws
     * @Title: addYears 增加N年
     * @Description: 增加年
     */
    public static Date addYears(Date date, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    /**
     * @param date   日期串
     * @param months 月数
     * @return String
     * @throws
     * @Title: addMonths
     * @Description: 获取N月后日期字符串
     */
    public static Date addMonths(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    /**
     * @param date 日期串
     * @param days 天数
     * @return String
     * @throws
     * @Title: addDays
     * @Description: 获取N天后日期字符串
     */
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 获取N分钟后日期字符串
     *
     * @param date
     * @param minutes
     * @return
     * @author fushun
     * @version V3.0商城
     * @creation 2016年12月22日
     * @records <p>  fushun 2016年12月22日</p>
     */
    public static Date addMinute(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    /**
     * @param date    给定的时间
     * @param seconds 秒数
     * @return String 结果
     * @throws
     * @Title: getDateAfterMinusSeconds
     * @Description: 获得给定时间N秒前的时间
     */
    public static Date addSeconds(Date date, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }

    /**
     * 判断日期是否比系统时间大指定的月数
     *
     * @param date   指定的日期
     * @param months 相加的月数
     * @return true/false
     */
    public static boolean isDateBeforeMonths(Date date, int months) {


        Date systemDate = DateUtil.addMonths(new Date(), months);
        // 格式化到月
        if (date.getTime() > systemDate.getTime()) {
            return true;
        }
        return false;
    }

    /**
     * @param n -1：前一月，0：当月，1：后一月
     * @return String
     * @throws
     * @Title: getFirstDayOfMonth
     * @Description: 获取前后N月第一天
     */
    public static Date getFirstDayOfMonth(int n) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.MONTH, n);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        return ca.getTime();
    }

    /**
     * 获取前后n季第一天
     *
     * @param n -1：前一季，0：当季，1：后一季
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年5月21日
     */
    public static Date getFirstDayOfSeason(int n) {
        Calendar ca = Calendar.getInstance();
        // 计算当前月份属于哪季，设置月份为季初，再根据n推算
        int month = ca.get(Calendar.MONTH);
        int season = (int) Math.ceil(month / 3);
        int temp = 0;
        // 根据季度，返回季初月份
        switch (season) {
            case 0:
                temp = 0;
                break;
            case 1:
                temp = 3;
                break;
            case 2:
                temp = 6;
                break;
            case 3:
                temp = 9;
                break;
        }
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.set(Calendar.MONTH, temp);
        ca.add(Calendar.MONTH, 3 * n);
        return ca.getTime();
    }

    /**
     * @param n -1：前一季，0：当季，1：后一季
     * @return String
     * @throws
     * @Title: getFirstDayOfSeason
     * @Description: 获取前后n季最后一天
     */
    public static Date getLastDayOfSeason(int n) {
        Calendar ca = Calendar.getInstance();
        // 计算当前月份属于哪季，设置月份为季末，再根据n推算
        int month = ca.get(Calendar.MONTH);
        int season = (int) Math.ceil(month / 3);
        int temp = 0;
        // 根据季度，返回季末月份
        switch (season) {
            case 0:
                temp = 2;
                break;
            case 1:
                temp = 5;
                break;
            case 2:
                temp = 8;
                break;
            case 3:
                temp = 11;
                break;
        }
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.set(Calendar.MONTH, temp);
        ca.add(Calendar.MONTH, 3 * n);
        // 下个月1号减一天即为上个月最后一天
        ca.add(Calendar.MONTH, 1);
        ca.add(Calendar.DAY_OF_MONTH, -1);
        return ca.getTime();
    }

    /**
     * @param n -1：前一季，0：当季，1：后一季
     * @return String
     * @throws
     * @Title: getFirstDayOfHalfYear
     * @Description: 获取前后n半年第一天
     */
    public static Date getFirstDayOfHalfYear(int n) {
        Calendar ca = Calendar.getInstance();
        // 计算当前月份属于哪半年，设置月份为半年初，再根据n推算
        int month = ca.get(Calendar.MONTH);
        int halfYear = (int) Math.ceil(month / 6);
        int temp = 0;
        switch (halfYear) {
            case 0:
                temp = 0;
                break;
            case 1:
                temp = 6;
                break;
        }
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.set(Calendar.MONTH, temp);
        ca.add(Calendar.MONTH, 6 * n);
        return ca.getTime();
    }

    /**
     * @param n -1：前一季，0：当季，1：后一季
     * @return String
     * @throws
     * @Title: getLastDayOfHalfYear
     * @Description: 获取前后n半年最后一天
     */
    public static Date getLastDayOfHalfYear(int n) {
        Calendar ca = Calendar.getInstance();
        // 计算当前月份属于哪半年，设置月份为半年末，再根据n推算
        int month = ca.get(Calendar.MONTH);
        int halfYear = (int) Math.ceil(month / 6);
        int temp = 0;
        // 根据季度，返回季末月份
        switch (halfYear) {
            case 0:
                temp = 5;
                break;
            case 1:
                temp = 11;
                break;
        }
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.set(Calendar.MONTH, temp);
        ca.add(Calendar.MONTH, 6 * n);
        // 下个月1号减一天即为上个月最后一天
        ca.add(Calendar.MONTH, 1);
        ca.add(Calendar.DAY_OF_MONTH, -1);
        return ca.getTime();
    }

    /**
     * @param n -1：前一月，0：当月，1：后一月
     * @return String
     * @throws
     * @Title: getLastDayOfMonth
     * @Description: 获取前后N月最后一天
     */
    public static Date getLastDayOfMonth(int n) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.MONTH, n);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.add(Calendar.MONTH, 1);
        ca.add(Calendar.DAY_OF_MONTH, -1);
        return ca.getTime();
    }

    /**
     * @param dateStr   日期字符串 yyyy-MM-dd
     * @param n         -1：前一月，0：当月，1：后一月
     * @param inFormat  输入日期格式
     * @param outFormat 输出日期格式
     * @return String
     * @throws
     * @Title: getLastDayOfMonth
     * @Description: 获取前后N月最后一天
     */
    public static String getLastDayOfMonth(String dateStr, int n, String inFormat, String outFormat) {
        DateFormat inDf = getSimpleDateFormat(inFormat);
        DateFormat outDf = getSimpleDateFormat(outFormat);
        try {
            Date date = inDf.parse(dateStr);
            Calendar ca = Calendar.getInstance();
            ca.setTime(date);
            ca.add(Calendar.MONTH, n);
            ca.set(Calendar.DAY_OF_MONTH, 1);
            ca.add(Calendar.MONTH, 1);
            ca.add(Calendar.DAY_OF_MONTH, -1);
            return outDf.format(ca.getTime());
        } catch (Exception e) {
            logger.error("输入日期格式不合法。" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * @param n -1：前一年， 0：当年， 1：后一年
     * @return String
     * @throws
     * @Title: getFirstDayOfYear 获取前后N年的第一天
     * @Description:
     */
    public static String getFirstDayOfYear(int n) {
        DateFormat df = getSimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.YEAR, n);
        ca.set(Calendar.DAY_OF_YEAR, 1);
        return df.format(ca.getTime());
    }

    /**
     * @param n -1：前一年， 0：当年， 1：后一年
     * @return String
     * @throws
     * @Title: getLastDayOfYear 获取前后年的最后一天
     * @Description:
     */
    public static String getLastDayOfYear(int n) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.YEAR, n + 1);
        ca.set(Calendar.DAY_OF_YEAR, 0);
        return df.format(ca.getTime());
    }

    /**
     * @return String
     * @throws
     * @Title: getSysDate
     * @Description: 获取当前系统时间
     */
    public static String getSysTime() {
        DateFormat df = getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    /**
     * @return String
     * @throws
     * @Title: getSysDate
     * @Description: 获取当前系统日期
     */
    public static String getSysDate() {
        DateFormat df = getSimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    /**
     * @param format 格式
     * @return String
     * @throws
     * @Title: getSysDate
     * @Description: 获取当前系统日期
     */
    public static String getSysDate(String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * @param n -1：前一天，0：当天，1：后一天
     * @return String
     * @throws
     * @Title: getDate
     * @Description: 获取当前的日期的前后N天
     */
    public static Date getDate(int n) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_MONTH, n);
        return ca.getTime();
    }

    /**
     * 获取指定日期的前后N天
     *
     * @param date 指定日期
     * @param n    -1：前一天，0：当天，1：后一天
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年5月16日
     */
    public static Date getDate(Date date, int n) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DAY_OF_MONTH, n);
        return ca.getTime();
    }

    /**
     * @param kssj 开始时间 格式为yyyy-mm-dd
     * @param jssj 结束时间 格式为yyyy-mm-dd
     * @return Map<String,List<String>>
     * @Title: getSjqj
     * @Description: 根据起始日期获取期间中的所有月份
     */
    public static Map<String, List<String>> getSjqj(String kssj, String jssj) {
        // 循环年份获取月份
        Map<String, List<String>> rqqjMap = null;
        try {
            DateFormat dateFormat = getSimpleDateFormat("yyyy-MM-dd");
            // 开始日期
            Date date1 = dateFormat.parse(kssj);
            // 结束日期
            Date date2 = dateFormat.parse(jssj);
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTime(date1);
            calendar2.setTime(date2);
            // 年份集合
            List<Integer> nfList = new ArrayList<Integer>();
            // 获取初始年份
            int csnf = calendar1.get(Calendar.YEAR);
            // 结束初始年份
            int jsnf = calendar2.get(Calendar.YEAR);
            nfList.add(csnf);
            // 获取年份区间
            while (calendar1.get(Calendar.YEAR) < jsnf) {
                calendar1.add(Calendar.YEAR, 1);
                nfList.add(calendar1.get(Calendar.YEAR));
            }
            rqqjMap = new TreeMap<String, List<String>>();
            // 循环年份获取每年的月份
            for (Integer integer : nfList) {
                Date dateq = null;
                Date datez = null;
                Calendar calendarq = Calendar.getInstance();
                Calendar calendarz = Calendar.getInstance();
                // 起始时期为同一年
                if (csnf == integer.intValue() && jsnf == integer.intValue()) {
                    dateq = dateFormat.parse(kssj);
                    datez = dateFormat.parse(jssj);
                }
                // 起始年份
                else if (csnf == integer.intValue()) {
                    dateq = dateFormat.parse(kssj);
                    datez = dateFormat.parse(String.valueOf(csnf) + "-12-01");
                }
                // 结束年份
                else if (jsnf == integer.intValue()) {
                    dateq = dateFormat.parse(String.valueOf(integer.intValue()) + "-01-01");
                    datez = dateFormat.parse(jssj);
                } else {
                    dateq = dateFormat.parse(String.valueOf(integer.intValue()) + "-01-01");
                    datez = dateFormat.parse(String.valueOf(integer.intValue()) + "-12-01");
                }
                calendarq.setTime(dateq);
                calendarz.setTime(datez);
                List<String> list = new ArrayList<String>();
                list.add(dateFormat.format(dateq).substring(0, dateFormat.format(dateq).lastIndexOf("-")));
                while (calendarq.compareTo(calendarz) < 0) {
                    // 开始日期加一个月直到等于结束日期为止
                    calendarq.add(Calendar.MONTH, 1);
                    Date tDate = calendarq.getTime();
                    list.add(dateFormat.format(tDate).substring(0, dateFormat.format(tDate).lastIndexOf("-")));
                }
                rqqjMap.put(String.valueOf(integer.intValue()), list);
            }
        } catch (Exception exception) {
            logger.error("DataUtil获取日期出错", exception);
            return rqqjMap;
        }
        return rqqjMap;
    }

    /**
     * @param kssj 开始时间 格式为yyyy-mm
     * @param jssj 结束时间 格式为yyyy-mm
     * @return Map<String,List<String>>
     * @Title: getSjqj2
     * @Description: 根据起始日期获取期间中的所有月份
     */
    public static List<String> getSjqj2(String kssj, String jssj) {
        List<String> result = new ArrayList<String>();
        Collection<List<String>> monthList = DateUtil.getSjqj(kssj + "-01", jssj + "-01").values();
        for (List<String> list : monthList) {
            for (String string : list) {
                if (string.length() == 6) {
                    result.add(string.replace("-", "-0"));
                } else {
                    result.add(string);
                }
            }
        }
        return result;
    }

    /**
     * @return String
     * @Title: getYear
     * @Description: 获取当前年份
     */
    public static String getYear() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    /**
     * @return String
     * @Title: getMon
     * @Description: 获取当前月份]
     * 格式
     */
    public static String getMon() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.MONTH) + 1);
    }

    /**
     * @return String
     * @Title: getDay
     * @Description: 获取当前日期
     */
    public static String getDay() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.DATE));
    }

//	/**
//	 * 已知2个日期，求相隔天数！！！
//	 * @param timeFrist
//	 * @param timeSecond
//	 * @return
//	 * @author fushun
//	 * @version VS1.3
//	 * @creation 2016年5月16日
//	 */
//	public static long getSubDay(Date timeFrist, Date timeSecond) {
//		long quot = 0;
//		quot = timeFrist.getTime() - timeSecond.getTime();
//		quot = quot / 1000 / 60 / 60 / 24;
//		return quot;
//	}

//	/**
//	 * 求相隔天数！！！
//	 * @param timeFrist
//	 * @param timeSecond
//	 * @param dateFormat 日期格式
//	 * @return
//	 * @author fushun
//	 * @version VS1.3
//	 * @creation 2016年5月16日
//	 */
//	public static long getSubDay(String timeFrist, String timeSecond, String dateFormat) {
//		long quot = 0;
//		SimpleDateFormat ft =getSimpleDateFormat(dateFormat);
//		try {
//			Date date1 = ft.parse(timeFrist);
//			Date date2 = ft.parse(timeSecond);
//			quot = date1.getTime() - date2.getTime();
//			quot = quot / 1000 / 60 / 60 / 24;
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return quot;
//	}

    /**
     * 获取传入日期的 天
     *
     * @param date
     * @return
     * @author fushun
     * @version V3.0商城
     * @creation 2017年1月10日
     * @records <p>  fushun 2017年1月10日</p>
     */
    public static String getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.DATE));
    }

    /**
     * 计算两个日期之间相隔的月份数
     *
     * @param timeFrist  时间一
     * @param timeSecond 时间二
     * @return 两个日期之间相隔的月份数
     * @throws ParseException
     * @Title getMonthSpace
     */
    public static int getMonthSpace(Date timeFrist, Date timeSecond) {
        int result = 0;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(timeFrist);
        c2.setTime(timeSecond);
        result = c2.get(Calendar.MONDAY) - c1.get(Calendar.MONTH);
        int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        result = year * 12 + result;
        return result;
    }

    /**
     * 获取两个日期相差的天
     *
     * @param startDate
     * @param endDate
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年5月18日
     */
    public static int getDaySpace(Date startDate, Date endDate) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(startDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endDate);
        long l = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        int days = new Long(l / (1000 * 60 * 60 * 24)).intValue();
        return days;
    }

    /**
     * 获取两个日期相差的分
     *
     * @param startDate
     * @param endDate
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年5月18日
     */
    public static Long getMinuteSpace(Date startDate, Date endDate) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(startDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endDate);
        long l = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        Long minute = new Long(l / (1000 * 60));
        return minute;
    }

    /**
     * 获取两个时间相差 N天N分
     *
     * @param startDate
     * @param endDate
     * @return
     * @author fushun
     * @version V3.0商城
     * @creation 2016年12月22日
     * @records <p>  fushun 2016年12月22日</p>
     */
    public static String getDayOrMinuteSpace(Date startDate, Date endDate) {
        int day = getDaySpace(startDate, endDate);
        long minute = getMinuteSpace(startDate, endDate) % 60;

        String result = "";
        if (day > 0) {
            result += day + "天";
        }
        if (minute != 0) {
            result += minute + "分钟";
        }
        return result;

    }

    /**
     * @return String
     * @throws
     * @Title: getLastMonthOfFirstDay
     * @Description: 返回上一个月的第一天
     */
    public static Date getLastMonthOfFirstDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 获得本周的第一天，周一
     *
     * @return
     */
    public static Date getCurrentWeekDayStartTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        try {
            int weekday = c.get(Calendar.DAY_OF_WEEK) - 2;
            c.add(Calendar.DATE, -weekday);
            c.setTime(lSdf.parse(sSdf.format(c.getTime()) + " 00:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 获得本周的最后一天，周日
     *
     * @return
     */
    public static Date getCurrentWeekDayEndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        try {
            int weekday = c.get(Calendar.DAY_OF_WEEK);
            c.add(Calendar.DATE, 8 - weekday);
            c.setTime(lSdf.parse(sSdf.format(c.getTime()) + " 23:59:59"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 获得本天的开始时间，即2012-01-01 00:00:00
     *
     * @return
     */
    public static Date getCurrentDayStartTime() {
        Date now = new Date();
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        try {
            now = sSdf.parse(sSdf.format(now));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 获得本天的结束时间，即2012-01-01 23:59:59
     *
     * @return
     */
    public static Date getCurrentDayEndTime() {
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        Date now = new Date();
        try {
            now = lSdf.parse(sSdf.format(now) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 获得指定日期当天的开始时间，即2012-01-01 00:00:00
     *
     * @return
     */
    public static Date getDayStartTime(Date date) {
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        try {
            date = sSdf.parse(sSdf.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获得指定日期当天的结束时间，即2012-01-01 23:59:59
     *
     * @return
     */
    public static Date getDayEndTime(Date date) {
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        try {
            date = lSdf.parse(sSdf.format(date) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获得本小时的开始时间，即2012-01-01 23:59:59
     *
     * @return
     */
    public static Date getCurrentHourStartTime() {
        Date now = new Date();
        SimpleDateFormat lhSdf = getSimpleDateFormat(longHourSdf);
        try {
            now = lhSdf.parse(lhSdf.format(now));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 获得本小时的结束时间，即2012-01-01 23:59:59
     *
     * @return
     */
    public static Date getCurrentHourEndTime() {
        SimpleDateFormat lhSdf = getSimpleDateFormat(longHourSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        Date now = new Date();
        try {
            now = lSdf.parse(lhSdf.format(now) + ":59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 获得本月的开始时间，即2012-01-01 00:00:00
     *
     * @return
     */
    public static Date getCurrentMonthStartTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        Date now = null;
        try {
            c.set(Calendar.DATE, 1);
            now = sSdf.parse(sSdf.format(c.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前月的结束时间，即2012-01-31 23:59:59
     *
     * @return
     */
    public static Date getCurrentMonthEndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        Date now = null;
        try {
            c.set(Calendar.DATE, 1);
            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DATE, -1);
            now = lSdf.parse(sSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前年的开始时间，即2012-01-01 00:00:00
     *
     * @return
     */
    public static Date getCurrentYearStartTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        Date now = null;
        try {
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DATE, 1);
            now = sSdf.parse(sSdf.format(c.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前年的结束时间，即2012-12-31 23:59:59
     *
     * @return
     */
    public static Date getCurrentYearEndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        Date now = null;
        try {
            c.set(Calendar.MONTH, 11);
            c.set(Calendar.DATE, 31);
            now = lSdf.parse(sSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的开始时间，即2012-01-1 00:00:00
     *
     * @return
     */
    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 4);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            now = lSdf.parse(sSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间，即2012-03-31 23:59:59
     *
     * @return
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                c.set(Calendar.MONTH, 2);
                c.set(Calendar.DATE, 31);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                c.set(Calendar.MONTH, 8);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
            now = lSdf.parse(sSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 获取前/后半年的开始时间
     *
     * @return
     */
    public static Date getHalfYearStartTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 0);
            } else if (currentMonth >= 7 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 6);
            }
            c.set(Calendar.DATE, 1);
            now = lSdf.parse(sSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;

    }

    /**
     * 获取前/后半年的结束时间
     *
     * @return
     */
    public static Date getHalfYearEndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sSdf = getSimpleDateFormat(shortSdf);
        SimpleDateFormat lSdf = getSimpleDateFormat(longSdf);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
            now = lSdf.parse(sSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * @return String
     * @throws
     * @Title: getPrevFirstDayOfWeek
     * @Description: 获取前一周第一天yyyy-MM-dd
     */
    public static Date getPrevFirstDayOfWeek() {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.WEEK_OF_YEAR, -1);
        ca.set(Calendar.DAY_OF_WEEK, 2);
        return ca.getTime();
    }

    /**
     * @return String
     * @throws
     * @Title: getPrevLastDayOfWeek
     * @Description: 获取前一周最后一天yyyy-MM-dd
     */
    public static Date getPrevLastDayOfWeek() {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.WEEK_OF_YEAR, -1);
        ca.set(Calendar.DAY_OF_WEEK, 1);
        ca.add(Calendar.WEEK_OF_YEAR, 1);
        ca.add(Calendar.DAY_OF_WEEK, 0);
        return ca.getTime();
    }

    /**
     * 得到某年某月的第一天yyyy-MM-dd
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getFirstDayOfYearAndMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
        return cal.getTime();
    }

    /**
     * 得到某年某月的最后一天yyyy-MM-dd
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getLastDayOfYearAndMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, value);
        return cal.getTime();
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public static long getTimestamp() {
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();
    }

    /**
     * 添加分钟
     *
     * @param minute
     * @author fushun
     * @version V3.0商城
     * @creation 2016年11月22日
     * @records <p>  fushun 2016年11月22日</p>
     */
    public static Date addMinute(Integer minute) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.MINUTE, minute);
        return ca.getTime();
    }

    //public static void main(String[] args) throws EncoderException {

//		Date date=new Date();
//		
//		Date date2=addMonths(date, 10);
//		date2=addDays(date2, 11);
//		date2=addMinute(date2, 11);
//		
//		System.out.println(getDateStr(date, FORMAT_STR));
//		System.out.println(getDateStr(date2, FORMAT_STR));
//		
//		int month=getMonthSpace(date,date2);
//		int day =getDaySpace(date, date2);
//		long minute =getMinuteSpace(date, date2);
//		String t =getDayOrMinuteSpace(date, date2);
//		System.out.println(month);
//		System.out.println(day);
//		System.out.println(minute);
//		System.out.println(t);
//		Date date2=new Date();
//		System.out.println(DateUtil.doDateFormat(date2, DateUtil.FORMAT_STR));
//		Date date3=getLastDayOfYearAndMonth(2016, 2);
//		System.out.println(DateUtil.doDateFormat(date3, DateUtil.FORMAT_STR));
//		
//		long i= getMinuteSpace(date3,date2);
//		System.out.println(i);

//		for(int i=0;i<10000;i++) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			CompletableFuture.runAsync(new Runnable() {
//				@Override
//				public void run() {	
//					System.out.println(getCurrentDayStartTime());
//				}
//			},es);
//		}
//		for(int i=0;i<10000;i++) {
//
//			Thread t1 = new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					System.out.println(getCurrentDayStartTime());
//					System.out.println(getCurrentDayStartTime());
//				}
//			});
//			t1.start();
//		}
    //}

    /**
     * 秒转换为时间字符串
     * <p> 1d24h59m59s
     * <p> 24:59:59
     *
     * @param mss
     * @return
     * @date: 2017-09-22 11:19:23
     * @author:wangfushun
     * @version 1.0
     */
    public static String secondToDateTimeString(long mss) {
        String DateTimes = null;
        mss = mss / 1000 + (mss % 1000 == 0 ? 0 : 1);
        long days = mss / (60 * 60 * 24);
        mss = mss - (days * 60 * 60 * 24);
        long hours = mss / (60 * 60);
        mss = mss - (hours * 60 * 60);
        long minutes = mss / 60;
        mss = mss - (minutes * 60);
        long seconds = mss;
        String hoursStr = hours < 10 ? "0" + hours : hours + "";
        String minutesStr = minutes < 10 ? "0" + minutes : minutes + "";
        String secondsStr = seconds < 10 ? "0" + seconds : seconds + "";
        if (days > 0) {
            DateTimes = days + "d" + hoursStr + "h" + minutesStr + "m" + secondsStr + "s";
        } else if (hours > 0) {
            DateTimes = hoursStr + ":" + minutesStr + ":" + secondsStr;
        } else if (minutes > 0) {
            DateTimes = minutesStr + ":" + secondsStr;
        } else {
            DateTimes = seconds + "";
        }

        return DateTimes;
    }

}
