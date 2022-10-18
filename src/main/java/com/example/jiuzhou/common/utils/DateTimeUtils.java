package com.example.jiuzhou.common.utils;

import net.sourceforge.jtds.jdbc.DateTime;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Appartion
 * @data 2022/10/14
 * 一入代码深似海，从此生活是路人
 */
public class DateTimeUtils  {
    public static Date stepMonth(Date sourceDate, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceDate);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static String getTimeDifference(String startTime,String endTime){
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(startTime==null){
            startTime=(df.format(new Date()));
        }
        if(endTime==null){
            endTime=(df.format(new Date()));
        }
        try{
            Date start=df.parse(startTime);
            Date end=df.parse(endTime);
            long difference=end.getTime()-start.getTime();
            long day=difference/(24*60*60*1000);
            long hour=difference/(60*60*1000)-day*24;
            long min=difference/(60*1000)-day*24*60-hour*60;
            long s=difference/1000-day*24*60*60-hour*60*60-min*60;
//            return day!=0?(day+"天"+hour+"小时"+min+"分"+s+"秒"):(hour!=0?hour+"小时"+min+"分"+s+"秒":(min!=0?min+"分"+s+"秒":s+"秒"));
            return day!=0?(day+"天"+hour+"小时"+min+"分"):(hour!=0?hour+"小时"+min+"分":(min!=0?min+"分":"0分"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return "0秒";
    }
}
