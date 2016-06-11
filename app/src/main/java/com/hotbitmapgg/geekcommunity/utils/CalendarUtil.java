package com.hotbitmapgg.geekcommunity.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class for date manipulation. This class gives a simple interface for
 * common Date, Calendar and Timezone operations. It is possible to apply
 * subsequent transformations to an initial date, and retrieve the changed Date
 * object at any point.
 */
public class CalendarUtil
{

    /**
     * Get the current TimeZone
     */
    public String getTZ(Calendar cal)
    {

        return cal.getTimeZone().getID();
    }

    /**
     * Convert the time to the midnight of the currently set date. The internal
     * date is changed after this call.
     *
     * @return a reference to this DateUtil, for concatenation.
     */
    public static Calendar toMidnight(Calendar cal)
    {

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    /**
     * Make the date go back of the specified amount of days The internal date
     * is changed after this call.
     *
     * @return a reference to this DateUtil, for concatenation.
     */
    public Calendar removeDays(Date date, int days)
    {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal = toMidnight(cal);

        long time = cal.getTimeInMillis();
        time -= days * 24 * 3600 * 1000;
        cal.setTimeInMillis(time);

        return cal;
    }

    public static long reduceDays(int days)
    {

        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(new Date());
        todayCal = toMidnight(todayCal);
        long time = todayCal.getTimeInMillis();
        time -= days * 24 * 3600 * 1000;

        return time;
    }

    /**
     * Make the date go forward of the specified amount of minutes The internal
     * date is changed after this call.
     *
     * @return a reference to this DateUtil, for concatenation.
     */
    public CalendarUtil addMinutes(int minutes)
    {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long time = cal.getTimeInMillis();
        time += minutes * 60 * 1000;
        cal.setTimeInMillis(time);

        return this;
    }

    /**
     * Convert the date to GMT. The internal date is changed
     *
     * @return a reference to this DateUtil, for concatenation.
     */
    public CalendarUtil toGMT()
    {

        return toTZ("GMT");
    }

    /**
     * Convert the date to the given timezone. The internal date is changed.
     *
     * @param tz The name of the timezone to set
     * @return a reference to this DateUtil, for concatenation.
     */
    public CalendarUtil toTZ(String tz)
    {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.setTimeZone(TimeZone.getTimeZone(tz));

        return this;
    }

    /**
     * Get the days passed from the specified date up to the date provided in
     * the constructor
     *
     * @param date The starting date
     * @return number of days within date used in constructor and the provided
     * date
     */
    public int getDaysSince(Date date)
    {

        long millisecs = date.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date d = cal.getTime();
        long time = d.getTime();
        long daysMillisecs = time - millisecs;
        int days = (int) ((((daysMillisecs / 1000) / 60) / 60) / 24);
        return days;
    }

    /**
     * Utility method wrapping Calendar.after method Compares the date field
     * parameter with the date provided with the constructor answering the
     * question: date from constructor is after the given param date ?
     *
     * @param date The date to be used for comparison
     * @return true if date from constructor is after given param date
     */
    public boolean isAfter(Date date)
    {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        return cal1.after(cal2);
    }

    // 15:00 (今天)
    // 昨天9:05
    // 最近7天则显示 星期二16:03
    // 7天之外 08-14 17:41
    // 去年 2013-12-12 12:00
    public static String getDateStr(Context context, long timeStamp)
    {

        if (timeStamp == 0)
            return "";
        long oneDay = 24 * 3600 * 1000; // 毫秒

        long am = getAM();
        long dateTime = timeStamp;
        if (dateTime > am)
        {
            return "今天  " + getDateString(dateTime, "HH:mm");
        } else
        {
            if ((am - dateTime) < oneDay)
            {
                return "昨天  " + getDateString(dateTime, "HH:mm");
                // } else if (isInWeek(dateTime, am)) {
                // return getDateString(dateTime, "EEEEHH:mm");
            } else
            {
                String str = isInYear(dateTime, am) ? "MM-dd  HH:mm" : "yyyy-MM-dd  HH:mm";
                return getDateString(dateTime, str);
            }
        }
    }

    // 15:00 (今天)
    // 昨天9:05
    // 最近7天则显示 星期二16:03
    // 7天之外 08-14 17:41
    // 去年 2013-12-12 12:00
    public static String getArchivesDateStr(Context context, long timeStamp)
    {

        if (timeStamp == 0)
            return "";
        long oneDay = 24 * 3600 * 1000; // 毫秒

        long am = getAM();
        long dateTime = timeStamp;
        if (dateTime > am)
        {
            return getDateString(dateTime, "HH:mm");
        } else
        {
            if ((am - dateTime) < oneDay)
            {
                return "昨天\n" + getDateString(dateTime, "HH:mm");
                // } else if (isInWeek(dateTime, am)) {
                // return getDateString(dateTime, "EEEE\nHH:mm");
            } else
            {
                // String str = isInYear(dateTime, am) ? "MM/dd\nHH:mm" :
                // "HH:mm\nyy/MM/dd";
                String str = isInYear(dateTime, am) ? "MM/dd  HH:mm" : "HH:mm  yy/MM/dd";
                return getDateString(dateTime, str);
            }
        }
    }

    public static boolean isInWeek(long time, long today)
    {

        Calendar todayCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();

        todayCal.setFirstDayOfWeek(Calendar.MONDAY);
        dateCal.setFirstDayOfWeek(Calendar.MONDAY);
        todayCal.setTimeInMillis(today);
        dateCal.setTimeInMillis(time);

        return todayCal.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR) && todayCal.get(Calendar.WEEK_OF_YEAR) == dateCal.get(Calendar.WEEK_OF_YEAR);
    }

    public static boolean isInYear(long time, long today)
    {

        Calendar todayCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();

        todayCal.setTimeInMillis(today);
        dateCal.setTimeInMillis(time);

        return todayCal.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR);
    }

    /**
     * @return 今天凌晨时间
     */
    public static long getAM()
    {
        // 用户登录时间为今天时间
        String updatedAt = HomeMsgApplication.getInstance().getCurrentUser().getUpdatedAt();
        long relative_time = Long.parseLong(updatedAt);
        long milliseconds = System.currentTimeMillis() - relative_time;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateString(long timeStamp, String... params)
    {

        if (timeStamp == 0)
            return "";
        SimpleDateFormat format = null;
        if (params != null && params.length > 0)
        {
            format = new SimpleDateFormat(params[0]);
        } else
        {
            format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        }
        return format.format(new Date(timeStamp));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurDateString()
    {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public static String getCurDateString2()
    {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurDateStr()
    {

        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date());
    }

    /**
     * <pre>
     * 判断date和当前日期是否在同一周内
     * 注:
     * Calendar类提供了一个获取日期在所属年份中是第几周的方法，对于上一年末的某一天
     * 和新年初的某一天在同一周内也一样可以处理，例如2012-12-31和2013-01-01虽然在
     * 不同的年份中，但是使用此方法依然判断二者属于同一周内
     * </pre>
     *
     * @param date
     * @return
     */
    public static boolean isSameWeekWithToday(Date date)
    {

        if (date == null)
        {
            return false;
        }

        // 0.先把Date类型的对象转换Calendar类型的对象
        Calendar todayCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();

        todayCal.setTime(new Date());
        dateCal.setTime(date);

        // 1.比较当前日期在年份中的周数是否相同
        if (todayCal.get(Calendar.WEEK_OF_YEAR) == dateCal.get(Calendar.WEEK_OF_YEAR))
        {
            return true;
        } else
        {
            return false;
        }
    }

    public static String getFileModifyTime(long time)
    {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        return sdf.format(time);
    }

    /**
     * 解析日期时间 如果时间是今年则返回 MM-dd HH:mm 如果时间不是今年则返回 yy-MM-dd HH:mm
     *
     * @param dateString yyyy-MM-dd HH:mm:ss
     * @return
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public static String parserWorkDate(String dateString)
    {

        Date curTime = new Date();

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date date = formatter.parse(dateString);

            int curYear = curTime.getYear();
            int dateYear = date.getYear();
            if (curYear == dateYear)
            {
                DateFormat fmt = new SimpleDateFormat("MM-dd HH:mm");
                return fmt.format(date);
            } else
            {
                DateFormat fmt = new SimpleDateFormat("yy-MM-dd HH:mm");
                return fmt.format(date);
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return dateString;
    }
}
