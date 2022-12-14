package com.example.jiuzhou.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
/**
 * @author Appartion
 * @data 2022/11/28
 * 一入代码深似海，从此生活是路人
 */
public class DateUtils {

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

    public static String addOneDay(String currentDay) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/M/d");
        try {
            Date d = new Date(f.parse(currentDay).getTime() + 24 * 3600 * 1000);
            return f.format(d);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String minusOneDay(String currentDay) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/M/d");
        try {
            Date d = new Date(f.parse(currentDay).getTime() - 24 * 3600 * 1000);
            return f.format(d);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static long getMillByFormat(String strDate, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = mSimpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
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
     * 描述：获取指定日期时间的字符串(可偏移).
     *
     * @param strDate
     *            String形式的日期时间
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @param calendarField
     *            Calendar属性，对应offset的值，
     *            如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
     * @param offset
     *            偏移(值大于0,表示+,值小于0,表示－)
     * @return String String类型的日期时间
     */
    public static String getStringByOffset(String strDate, String format,
                                           int calendarField, int offset) {
        String mDateTime = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            c.setTime(mSimpleDateFormat.parse(strDate));
            c.add(calendarField, offset);
            mDateTime = mSimpleDateFormat.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mDateTime;
    }

    /**
     * 描述：Date类型转化为String类型(可偏移).
     *
     * @param date
     *            the date
     * @param format
     *            the format
     * @param calendarField
     *            the calendar field
     * @param offset
     *            the offset
     * @return String String类型日期时间
     */
    public static String getStringByOffset(Date date, String format,
                                           int calendarField, int offset) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            c.setTime(date);
            c.add(calendarField, offset);
            strDate = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }
    /// <summary>
    /// 日期字符格式化 yyyy-MM-dd HH:mm:ss
    /// </summary>
    /// <param name="str"></param>
    /// <returns></returns>
    public static  String DateStringFromat(String str)
    {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = sdf.parse("1999-01-01");
            //  Date date = sdf.parse(str);
            String dateDefault="1999-01-01";
            if (str == null)
                return dateDefault;
            if (str.length() < 8)
                return dateDefault;

            String year = str.substring(0, 4);
            if(year=="0000")
                return dateDefault;
            String month = str.substring(4, 6);
            String day = str.substring(6, 8);

            String date = (year + "-" + month + "-" + day);
            if (str.length() < 14)
                return date;
            String hour = str.substring(8, 10);
            String mini = str.substring(10, 12);
            String sec = str.substring(12, 14);
            String time = hour + ":" + mini + ":" + sec;
            return (date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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

    /**
     * 描述：获取指定日期时间的字符串,用于导出想要的格式.
     *
     * @param strDate
     *            String形式的日期时间，必须为yyyy-MM-dd HH:mm:ss格式
     * @param format
     *            输出格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String 转换后的String类型的日期时间
     */
    public static String getStringByFormat(String strDate, String format) {
        String mDateTime = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
                    dateFormatYMDHM2);
            c.setTime(mSimpleDateFormat.parse(strDate));
            SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat(format);
            mDateTime = mSimpleDateFormat2.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDateTime;
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
     * 描述：获取表示当前日期时间的字符串.
     *
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String String类型的当前日期时间
     */
    public static String getCurrentDate(String format) {
        String curDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            Calendar c = new GregorianCalendar();
            curDateTime = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;

    }

    /**
     * 描述：获取表示当前日期时间的字符串(可偏移).
     *
     * @param format
     *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @param calendarField
     *            Calendar属性，对应offset的值，
     *            如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
     * @param offset
     *            偏移(值大于0,表示+,值小于0,表示－)
     * @return String String类型的日期时间
     */
    public static String getCurrentDateByOffset(String format,
                                                int calendarField, int offset) {
        String mDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            Calendar c = new GregorianCalendar();
            c.add(calendarField, offset);
            mDateTime = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDateTime;

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
     * 描述：获取本周一.
     *
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getFirstDayOfWeek(String format) {
        return getDayOfWeek(format, Calendar.MONDAY);
    }

    /**
     * 描述：获取本周日.
     *
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getLastDayOfWeek(String format) {
        return getDayOfWeek(format, Calendar.SUNDAY);
    }

    /**
     * 描述：获取本周的某一天.
     *
     * @param format
     *            the format
     * @param calendarField
     *            the calendar field
     * @return String String类型日期时间
     */
    private static String getDayOfWeek(String format, int calendarField) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            int week = c.get(Calendar.DAY_OF_WEEK);
            if (week == calendarField) {
                strDate = mSimpleDateFormat.format(c.getTime());
            } else {
                int offectDay = calendarField - week;
                if (calendarField == Calendar.SUNDAY) {
                    offectDay = 7 - Math.abs(offectDay);
                }
                c.add(Calendar.DATE, offectDay);
                strDate = mSimpleDateFormat.format(c.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 描述：获取本月第一天.
     *
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getFirstDayOfMonth(String format) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            // 当前月的第一天
            c.set(GregorianCalendar.DAY_OF_MONTH, 1);
            strDate = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;

    }

    /**
     * 描述：获取本月最后一天.
     *
     * @param format
     *            the format
     * @return String String类型日期时间
     */
    public static String getLastDayOfMonth(String format) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            // 当前月的最后一天
            c.set(Calendar.DATE, 1);
            c.roll(Calendar.DATE, -1);
            strDate = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 取指定日期为星期几.
     *
     * @param strDate
     *            指定日期
     * @param inFormat
     *            指定日期格式
     * @return String 星期几
     */
    public static String getWeekNumber(String strDate, String inFormat) {
        String week = "星期日";
        Calendar calendar = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat(inFormat);
        try {
            calendar.setTime(df.parse(strDate));
        } catch (Exception e) {
            return "错误";
        }
        int intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        switch (intTemp) {
            case 0:
                week = "星期日";
                break;
            case 1:
                week = "星期一";
                break;
            case 2:
                week = "星期二";
                break;
            case 3:
                week = "星期三";
                break;
            case 4:
                week = "星期四";
                break;
            case 5:
                week = "星期五";
                break;
            case 6:
                week = "星期六";
                break;
        }
        return week;
    }

    /**
     * 根据给定的毫秒数算得时间的描述.
     *
     * @param milliseconds
     *            the milliseconds
     * @return the time description
     */
    public static String getTimeDescription(long milliseconds) {
        if (milliseconds > 1000) {
            // 大于一分
            if (milliseconds / 1000 / 60 > 1) {
                long minute = milliseconds / 1000 / 60;
                long second = milliseconds / 1000 % 60;
                return minute + "分" + second + "秒";
            } else {
                // 显示秒
                return milliseconds / 1000 + "秒";
            }
        } else {
            return milliseconds + "毫秒";
        }
    }

    public static String getNowTime() {
        return getStringByFormat(new Date(), dateFormatHMS1);
    }

    public static String getNowDate() {
        return getStringByFormat(new Date(), dateFormatYMD1);
    }

    /*
     * 获取时间间隔
     */
    public static int getGabTime(String startTime, String endTime) {
        long date1 = DateUtils.getMillByFormat(getCurrentDate(startTime),
                DateUtils.dateFormatYMDHM2);
        long date2 = DateUtils.getMillByFormat(endTime,
                DateUtils.dateFormatYMDHM2);
        int minutes = DateUtils.getOffectMinutes(date2, date1);
        return minutes;
    }

    public static String getGabTimDes(String inTime, String outTime) {
        int minutes = Math.abs(DateUtils.getGabTime(inTime, outTime));

        int hours = minutes / 60;
        int minutes_ = minutes - hours * 60;
        String last = "";
        if (hours == 0) {
            last = minutes + "分钟";
        } else if (minutes_ == 0) {
            last = hours + "小时";
        } else {
            last = hours + "小时" + minutes_ + "分钟";
        }
        return last;
    }
}