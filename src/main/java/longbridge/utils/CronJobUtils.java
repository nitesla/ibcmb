package longbridge.utils;


//import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by Longbridge on 7/1/2017.
 */
public class CronJobUtils {
    private static Logger logger = LoggerFactory.getLogger(new CronJobUtils().getClass());
    public static String getSecondExpression(String second){
        if(second!=null && !second.equalsIgnoreCase("")) {
            String exrInit = "*/";
            StringBuilder stringBuilder = new StringBuilder(exrInit);
            stringBuilder.append(second);
            stringBuilder.append(" * * * * *");
            logger.info("seconds expression {}", stringBuilder);
            return stringBuilder.toString();
        }
        return "";
    }
    public static String getMinuteExpression(String minute){
        String exrInit = "0 0/";
        StringBuilder stringBuilder = new StringBuilder(exrInit);
        stringBuilder.append(minute);
        stringBuilder.append(" * 1/1 * ? *");
        logger.info("minute expression {}",stringBuilder);

        return stringBuilder.toString();
    }
    public static String getHourExpression(String hourChecker, String exactHour, String hour, String minute){

        String exrInit = "0 ";
        StringBuilder stringBuilder = new StringBuilder(exrInit);
        if(hourChecker.equalsIgnoreCase("everyHour")){
            stringBuilder.append("0 0/"+minute);
        }else {
            stringBuilder.append(minute);
            stringBuilder.append(" "+hour);
        }
        stringBuilder.append(" 1/1 * ? *");
        logger.info("hour expression {}",stringBuilder);
//        0 0 0/20 1/1 * ? *
//        0 12 6 1/1 * ? *
        return stringBuilder.toString();
    }
    public static String getDailyExpression(String dailyChecker,String dayInterval, String dailyHour,String dailyMin){

        String exrInit = "0 ";
        StringBuilder stringBuilder = new StringBuilder(exrInit);
        if(dailyChecker.equalsIgnoreCase("everyDay")){
            stringBuilder.append(dailyMin);
            stringBuilder.append(" "+dailyHour);
            stringBuilder.append("1/"+dayInterval);
            stringBuilder.append(" * ? *");
        }else {
            stringBuilder.append(dailyMin);
            stringBuilder.append(" "+dailyHour);
            stringBuilder.append(" ? * MON-FRI *");
        }
        logger.info("minute expression {}",stringBuilder);
        //Sample Cron expression expected
//        	0 9 12 1/4 * ? *
//        	0 9 12 ? * MON-FRI *
        return stringBuilder.toString();
    }

    public static String getWeeklyExpression(String hour, String minute, String[] days){

        String exrInit = "0 ";
        StringBuilder stringBuilder = new StringBuilder(exrInit);
            stringBuilder.append(minute);
            stringBuilder.append(" "+hour);
            stringBuilder.append(" ? * ");
            stringBuilder.append(days[0]);
        if(days.length>1) {
            for (int i = 1;i<=days.length-1;i++ ) {
                stringBuilder.append(","+days[i]);
            }
        }
            stringBuilder.append(" *");

        logger.info("week expression {}",stringBuilder);
        /*
Sample Cron expression expected
0 0 12 ? * MON *
*/
        return stringBuilder.toString();
    }
    public static String getMonthlyExpression(String monthChecker, String monthDay, String monthNum, String monthCategory, String monthWeek, String monthNumDesp, String monthHour, String monthMinute){

        String exrInit = "0 ";
        StringBuilder stringBuilder = new StringBuilder(exrInit);
        stringBuilder.append(monthMinute);
        stringBuilder.append(" " + monthHour);
        if("eachMonthDay".equalsIgnoreCase(monthChecker)) {
            stringBuilder.append(" " + monthDay);
            stringBuilder.append(" 1/" + monthNum);
            stringBuilder.append(" * ?");
        }else {
            stringBuilder.append(" ? ");
            stringBuilder.append(" 1/" + monthNum);
            stringBuilder.append(" "+monthWeek+"#"+monthCategory);
            stringBuilder.append(" "+monthNumDesp);
            stringBuilder.append(" *");

        }
        logger.info("month expression {}",stringBuilder);
        /*
Sample Cron expression expected
0 0 12 ? * MON *
*/
        return stringBuilder.toString();
    }

    public static String getYearlyExpression(String yearChecker, String yearMonth1, String yearMonthNum, String yearCategory, String yearMonthWeek, String yearMonth2, String yearHour, String yearMinute){

        String exrInit = "0 ";
        StringBuilder stringBuilder = new StringBuilder(exrInit);
        stringBuilder.append(yearMinute);
        stringBuilder.append(" " + yearHour);
        if("perMonth".equalsIgnoreCase(yearChecker)) {
            stringBuilder.append(" " + yearMonthNum);
            stringBuilder.append(" " + yearMonth1);
            stringBuilder.append(" ? *");
        }else {
            stringBuilder.append(" ? ");
            stringBuilder.append(" " + yearMonth2);
            stringBuilder.append(" "+yearMonthWeek+"#"+yearCategory);
            stringBuilder.append(" *");

        }
        logger.info("year expression {}",stringBuilder);
        /*
Sample Cron expression expected
0 0 12 ? * MON *
*/
        return stringBuilder.toString();
    }

    public static String getCronExpression(String schedule,WebRequest webRequest){
        String expr = "";
        switch (schedule) {
            case "secondsDiv":
                return getSecondExpression(webRequest.getParameter("second"));
            case "minuteDiv":
                return getMinuteExpression(webRequest.getParameter("minute"));
            case "hourDiv":
                return getHourExpression(webRequest.getParameter("hourChecker"), webRequest.getParameter("exactHour"), webRequest.getParameter("hour"), webRequest.getParameter("hourMin"));
            case "DailyDiv":
                return getDailyExpression(webRequest.getParameter("dailyChecker"), webRequest.getParameter("dayInterval"), webRequest.getParameter("dailyHour"), webRequest.getParameter("dailyMin"));
            case "weekDiv":
                return getWeeklyExpression(webRequest.getParameter("weekHour"), webRequest.getParameter("weekSecond"), webRequest.getParameterValues("weekDay"));
            case "monthDiv":
                return getMonthlyExpression(webRequest.getParameter("monthChecker"), webRequest.getParameter("monthDay"), webRequest.getParameter("monthNum"), webRequest.getParameter("monthCategory"), webRequest.getParameter("monthWeek"), webRequest.getParameter("monthNumDesp"), webRequest.getParameter("monthHour"), webRequest.getParameter("monthMinute"));
            case "yearDiv":
            return getYearlyExpression(webRequest.getParameter("yearChecker"), webRequest.getParameter("yearMonth1"), webRequest.getParameter("yearMonthNum"), webRequest.getParameter("yearCategory"), webRequest.getParameter("yearMonthWeek"), webRequest.getParameter("yearMonth2"), webRequest.getParameter("yearHour"), webRequest.getParameter("yearMinute"));
        }
        return ""   ;
    }

}
