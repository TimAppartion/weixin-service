package com.example.jiuzhou.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
public class AbDateUtil {
    /** 时间日期格式化到年月日时分秒. */
    public static final String dateFormatYMDHM2 = "yyyy-MM-dd HH:mm:ss";

    public static final String dateFormatYMDHMS3 = "yyyyMMddHHmmss";

    /** 时间日期格式化到年月日. */
    public static final String dateFormatYMD = "yyyy/MM/dd";
    public static final String dateFormatYMD1 = "yyyyMMdd";
    public static final String dateFormatYMD2 = "yyyy-MM-dd";

    /** 时间日期格式化到年月. */
    public static final String dateFormatYM = "yyyy-MM";

    /** 时间日期格式化到年月日时分. */
    // public static final String dateFormatYMDHM = "yyyy-MM-dd HH:mm";

    /** 时间日期格式化到月日. */
    public static final String dateFormatMD = "MM/dd";

    /** 时分秒. */
    public static final String dateFormatHMS = "HH:mm:ss";
    public static final String dateFormatHMS1 = "HHmmss";
    /** 时分秒. */
    public static final String dateFormatHMSSS = "HH:mm:ss:SSS";
    /** 时分. */
    public static final String dateFormatHM = "HH:mm";

    /** 上午. */
    public static final String AM = "AM";

    /** 下午. */
    public static final String PM = "PM";

    public static final String dateFormatYMDHM = "yyyy/MM/dd HH:mm:ss";

    /**
     * 描述：String类型的日期时间转化为Date类型.
     *
     * @param strDate
     *            String形式的日期时间
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return Date Date类型日期时间
     */
    public static Date getDateByFormat(String strDate, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = mSimpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDateByCst(String strDate,String format){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format);
        try {
            DateFormat cst = new SimpleDateFormat(format);
            DateFormat gmt = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date dateTime = gmt.parse(strDate);
            String dateString = cst.format(dateTime);
            return simpleDateFormat.parse(dateString);
        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }
    /**
     * 描述：获取milliseconds表示的日期时间的字符串.
     *
     * @param milliseconds
     *            the milliseconds
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String 日期时间字符串
     */
    public static String getStringByFormat(long milliseconds, String format) {
        String thisDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            thisDateTime = mSimpleDateFormat.format(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thisDateTime;
    }

    /**
     * 描述：获取偏移之后的Date.
     *
     * @param date
     *            日期时间
     * @param calendarField
     *            Calendar属性，对应offset的值，
     *            如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
     * @param offset
     *            偏移(值大于0,表示+,值小于0,表示－)
     * @return Date 偏移之后的日期时间
     */
    public static Date getDateByOffset(Date date, int calendarField, int offset) {
        Calendar c = new GregorianCalendar();
        try {
            c.setTime(date);
            c.add(calendarField, offset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 描述：计算两个日期所差的分钟数.
     *
     * @param date1
     *            第一个时间的毫秒表示
     * @param date2
     *            第二个时间的毫秒表示
     * @return int 所差的分钟数
     */
    public static int getOffectMinutes(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        int m1 = calendar1.get(Calendar.MINUTE);
        int m2 = calendar2.get(Calendar.MINUTE);
        int h = getOffectHour(date1, date2);
        int m = 0;
        m = m1 - m2 + h * 60;
        return m;
    }
    /**
     * 描述：计算两个日期所差的小时数.
     *
     * @param date1
     *            第一个时间的毫秒表示
     * @param date2
     *            第二个时间的毫秒表示
     * @return int 所差的小时数
     */
    public static int getOffectHour(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        int h1 = calendar1.get(Calendar.HOUR_OF_DAY);
        int h2 = calendar2.get(Calendar.HOUR_OF_DAY);
        int h = 0;
        int day = getOffectDay(date1, date2);
        h = h1 - h2 + day * 24;
        return h;
    }

    /**
     *
     * @param value
     * @return
     */
    public static double toDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * 描述：计算两个日期所差的天数.
     *
     * @param milliseconds1
     *            the milliseconds1
     * @param milliseconds2
     *            the milliseconds2
     * @return int 所差的天数
     */
    public static int getOffectDay(long milliseconds1, long milliseconds2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(milliseconds1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(milliseconds2);
        // 先判断是否同年
        int y1 = calendar1.get(Calendar.YEAR);
        int y2 = calendar2.get(Calendar.YEAR);
        int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
        int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
        int maxDays = 0;
        int day = 0;
        if (y1 - y2 > 0) {
            maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
            day = d1 - d2 + maxDays;
        } else if (y1 - y2 < 0) {
            maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
            day = d1 - d2 - maxDays;
        } else {
            day = d1 - d2;
        }
        return day;
    }

    /**
     * 描述：Date类型转化为String类型.
     *
     * @param date
     *            the date
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getStringByFormat(Date date, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        String strDate = null;
        try {
            strDate = mSimpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }
}
