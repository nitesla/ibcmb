package longbridge.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Longbridge on 10/26/2017.
 */
public class DateUtil {
    static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static long convertDateToLong(String date){
        long milliseconds = 0;
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            logger.info("te");
            if(!StringUtils.isEmpty(date)) {
                Date d = f.parse(date);
                logger.info("the date  is {}",d);
                milliseconds = d.getTime();
            }
            logger.info("the date in long is {}",milliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }
    public static String nextDate(Long date){
        logger.info("the long day {}",date);
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 =new Date(date);
        logger.info("the day {}",date1);
            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            c.add(Calendar.DATE, 1);
            date1 = c.getTime();
            logger.info("the next day {}",f.format(date1));
            return f.format(date1);


    }

    public static boolean isSameDayOfTheYear(Date date) {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        String dateString=null;
        dateString = formatter.format(date);
        LocalDate actualDate=LocalDate.parse(dateString);
        MonthDay monthDay=MonthDay.of(actualDate.getMonth(),actualDate.getDayOfMonth());
        MonthDay currentMonthDay=MonthDay.from(LocalDate.now());
        // logger.info("monthDay is {} and today is {}", monthDay,currentMonthDay);
        return (currentMonthDay.equals(monthDay));
    }

    public static boolean isWithinCurrentDateRange(Date date1, Date date2){
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        String dateString1 = formatter.format(date1);
        String dateString2 = formatter.format(date2);
        LocalDate actualDate1=LocalDate.parse(dateString1);
        LocalDate actualDate2=LocalDate.parse(dateString2);
        MonthDay monthDay1=MonthDay.of(actualDate1.getMonth(),actualDate1.getDayOfMonth());
        MonthDay monthDay2=MonthDay.of(actualDate2.getMonth(),actualDate2.getDayOfMonth());
        MonthDay currentMonthDay= MonthDay.from(LocalDate.now());
        return((monthDay1.isBefore(currentMonthDay)||monthDay1.equals(currentMonthDay))&&(
                (monthDay2.isAfter(currentMonthDay)||monthDay2.equals(currentMonthDay))));


    }

    public static Date getToday(){
        Date today = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat newformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String newDate = format.format(today);

        try {
            newDate = newformat.format(format.parse(newDate));
            today = newformat.parse(newDate);
            return today;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return today;
    }

    public static  Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    public static Date convertStringToDate(String date) throws  ParseException{
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        return f.parse(date);
    }

    public static Date addDaysToDate(Date date, int days){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(date); // Now use today date.
        c.add(Calendar.DATE, days);
        return c.getTime() ;
    }

    public static boolean isBeforeDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isBeforeDay(cal1, cal2);
    }

    public static boolean isBeforeDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return true;
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return false;
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return true;
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return false;
        return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
    }
}
